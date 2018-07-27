package biz.zenpets.users.creator.pet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.pet.BreedsAdapter;
import biz.zenpets.users.utils.adapters.pet.PetTypesAdapter;
import biz.zenpets.users.utils.helpers.pets.breed.FetchBreedTypes;
import biz.zenpets.users.utils.helpers.pets.breed.FetchBreedTypesInterface;
import biz.zenpets.users.utils.helpers.pets.pet.AddNewPetInterface;
import biz.zenpets.users.utils.helpers.pets.type.PetTypes;
import biz.zenpets.users.utils.helpers.pets.type.PetTypesInterface;
import biz.zenpets.users.utils.models.pets.breeds.Breed;
import biz.zenpets.users.utils.models.pets.types.PetTypesData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class NewPetCreator extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        PetTypesInterface, FetchBreedTypesInterface,
        AddNewPetInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /****** DATA TYPES FOR THE PET DETAILS *****/
    private String USER_ID = null;
    private String PET_TYPE_ID = null;
    private String PET_BREED_ID = null;
    private String PET_NAME = null;
    private String PET_GENDER = "Male";
    private String PET_DOB = null;
    private String PET_NEUTERED = "1";
    private String PET_PROFILE = null;
    private String PET_ACTIVE = "1";
    private String FILE_NAME = null;
    private Uri PET_URI = null;

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** THE PET TYPES ARRAY LIST **/
    private ArrayList<PetTypesData> arrPetTypes = new ArrayList<>();

    /** THE BREEDS ARRAY LIST **/
    private ArrayList<Breed> arrBreeds = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnPetTypes) AppCompatSpinner spnPetTypes;
    @BindView(R.id.spnBreeds) AppCompatSpinner spnBreeds;
    @BindView(R.id.inputPetName) TextInputLayout inputPetName;
    @BindView(R.id.edtPetName) TextInputEditText edtPetName;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.txtPetDOB) AppCompatTextView txtPetDOB;
    @BindView(R.id.rdgNeutered) RadioGroup rdgNeutered;
    @BindView(R.id.imgvwPetThumb) AppCompatImageView imgvwPetThumb;

    /** SELECT AN IMAGE OF THE MEDICINE **/
    @OnClick(R.id.imgvwPetThumb) void selectImage()    {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    /** SELECT THE PET'S DATE OF BIRTH **/
    @OnClick(R.id.btnDOBSelector) void selectDOB()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                NewPetCreator.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_creator);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* SET THE CURRENT DATE */
        setCurrentDate();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* FETCH ALL PET TYPES */
        new PetTypes(this).execute();

        /* SELECT THE PET'S GENDER */
        rdgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnMale:
                        /* SET THE GENDER TO MALE */
                        PET_GENDER = "Male";
                        break;
                    case R.id.rdbtnFemale:
                        /* SET THE GENDER TO FEMALE */
                        PET_GENDER = "Female";
                        break;
                    default:
                        break;
                }
            }
        });

        /* SET THE PET'S NEUTERED STATUS */
        rdgNeutered.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnNeuteredYes:
                        /* SET THE STATUS TO "1" (YES) */
                        PET_NEUTERED = "1";
                        break;
                    case R.id.rdbtnNeuteredNo:
                        /* SET THE STATUS TO "0" (NO) */
                        PET_NEUTERED = "0";
                        break;
                    default:
                        break;
                }
            }
        });

        /* SELECT A PET TYPE */
        spnPetTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED PET TYPE ID */
                PET_TYPE_ID = arrPetTypes.get(position).getPetTypeID();

                /* CLEAR THE BREEDS ARRAY LIST */
                arrBreeds.clear();

                /* FETCH THE LIST OF BREEDS IN THE SELECTED PET TYPE */
                new FetchBreedTypes(NewPetCreator.this).execute(PET_TYPE_ID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* SELECT A BREED */
        spnBreeds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PET_BREED_ID = arrBreeds.get(position).getBreedID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** CHECK FOR ALL PET DETAILS  *****/
    private void checkPetDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtPetName.getWindowToken(), 0);
        }

        /* GET THE REQUIRED TEXTS */
        PET_NAME = edtPetName.getText().toString().trim();
        if (!TextUtils.isEmpty(PET_NAME))   {
            FILE_NAME = PET_NAME.replaceAll(
                    " ", "_").toLowerCase().trim() + "_" + USER_ID + "_" + PET_TYPE_ID + "_" + PET_BREED_ID;
        }

        if (TextUtils.isEmpty(PET_NAME))   {
            inputPetName.setError("Enter the Pet's name");
        } else if (PET_URI == null)   {
            Toast.makeText(getApplicationContext(), "Please select your Pet's image", Toast.LENGTH_LONG).show();
        } else {
            /* POST THE PETS DISPLAY PICTURE */
            postPetPicture();
        }
    }

    /***** POST THE PETS DISPLAY PICTURE *****/
    private void postPetPicture() {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we add the new Pet to your account..");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* PUBLISH THE PET PROFILE TO FIREBASE */
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference refStorage = storageReference.child("Pets").child(FILE_NAME);
//        refStorage.putFile(PET_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                if (downloadURL != null)    {
//                    PET_PROFILE = String.valueOf(downloadURL);
//                    if (PET_PROFILE != null)    {
//                        /* PUBLISH THE NEW PET */
//                        new AddNewPet(NewPetCreator.this).execute(
//                                USER_ID, PET_TYPE_ID, PET_BREED_ID, PET_NAME,
//                                PET_GENDER, PET_DOB, PET_NEUTERED, PET_PROFILE);
//                    } else {
//                        dialog.dismiss();
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "There was a problem adding your new Pet. Please try again by clicking the Save button.",
//                                Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    dialog.dismiss();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "There was a problem adding your new Pet. Please try again by clicking the Save button.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    @Override
    public void onNewPet(String result) {
        if (result != null) {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Successfully added your Pet", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "There was an error adding your Pet. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Add a new Pet";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(NewPetCreator.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* CHECK FOR ALL PET DETAILS  */
                checkPetDetails();
                break;
            case R.id.menuCancel:
                /* CANCEL NEW PET CREATION */
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** SET THE CURRENT DATE *****/
    private void setCurrentDate() {
        /* SET THE CURRENT DATE (DISPLAY ONLY !!!!) */
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        txtPetDOB.setText(formattedDate);

        /* SET THE CURRENT DATE (DATABASE ONLY !!!!) */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        PET_DOB = sdf.format(cal.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        /* FOR THE DATABASE ONLY !!!! */
        PET_DOB = sdf.format(cal.getTime());

        /* FOR DISPLAY ONLY !!!! */
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String selectedDate = dateFormat.format(cal.getTime());
        txtPetDOB.setText(selectedDate);
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(NewPetCreator.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        NewPetCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(NewPetCreator.this);
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
            sheetDialog.setContentView(view);
            sheetDialog.show();

            /* CAST THE CHOOSER ELEMENTS */
            LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
            LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

            /* SELECT A GALLERY IMAGE */
            linlaGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openGallery(NewPetCreator.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(NewPetCreator.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(NewPetCreator.this);
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
                sheetDialog.setContentView(view);
                sheetDialog.show();

                /* CAST THE CHOOSER ELEMENTS */
                LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
                LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

                /* SELECT A GALLERY IMAGE */
                linlaGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openGallery(NewPetCreator.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(NewPetCreator.this, 0);
                    }
                });
            } else {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotoReturned(imageFiles);
            }
        });
    }

    /***** PROCESS THE SELECTED IMAGE AND GRAB THE URI *****/
    private void onPhotoReturned(List<File> imageFiles) {
        try {
            File compressedFile = new Compressor(this)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(imageFiles.get(0));
            Uri uri = Uri.fromFile(compressedFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imgvwPetThumb.setImageBitmap(bitmap);
            imgvwPetThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /* STORE THE BITMAP AS A FILE AND USE THE FILE'S URI */
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/ZenPets");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /* GET THE FINAL URI */
                PET_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void petTypes(ArrayList<PetTypesData> data) {
        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
        arrPetTypes = data;

        /* INSTANTIATE THE PET TYPES ADAPTER */
        PetTypesAdapter petTypesAdapter = new PetTypesAdapter(NewPetCreator.this, arrPetTypes);

        /* SET THE ADAPTER TO THE PET TYPES SPINNER */
        spnPetTypes.setAdapter(petTypesAdapter);
    }

    @Override
    public void breedTypes(ArrayList<Breed> data) {
        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
        arrBreeds = data;

        /* INSTANTIATE THE BREEDS ADAPTER */
        BreedsAdapter breedsAdapter = new BreedsAdapter(NewPetCreator.this, arrBreeds);

        /* SET THE ADAPTER TO THE BREEDS SPINNER */
        spnBreeds.setAdapter(breedsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}