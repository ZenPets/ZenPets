package biz.zenpets.users.creator.pet;

import android.Manifest;
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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.adapters.adoptions.AdoptionsAlbumAdapter;
import biz.zenpets.users.utils.adapters.pet.vaccinations.VaccinesAdapter;
import biz.zenpets.users.utils.helpers.pets.vaccinations.AllVaccines;
import biz.zenpets.users.utils.helpers.pets.vaccinations.AllVaccinesInterface;
import biz.zenpets.users.utils.helpers.pets.vaccinations.NewVaccination;
import biz.zenpets.users.utils.helpers.pets.vaccinations.NewVaccinationInterface;
import biz.zenpets.users.utils.helpers.pets.vaccinations.PostVaccinationImage;
import biz.zenpets.users.utils.helpers.pets.vaccinations.PostVaccinationImageInterface;
import biz.zenpets.users.utils.models.adoptions.AdoptionAlbumData;
import biz.zenpets.users.utils.models.pets.vaccines.Vaccine;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("ConstantConditions")
public class VaccinationCreator extends AppCompatActivity
        implements AllVaccinesInterface,
        NewVaccinationInterface, PostVaccinationImageInterface {

    /** THE INCOMING PET ID **/
    private String PET_ID = null;

    /** THE NEW VACCINATION ID **/
    String VACCINATION_ID = null;

    /** DATA TYPES FOR HOLDING THE COLLECTED DATA **/
    private String VACCINE_ID = null;
    private String VACCINATION_NAME = null;
    private String VACCINATION_DATE = null;
    private String VACCINATION_NEXT_DATE = null;
    private String VACCINATION_REMINDER = "False";

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** THE UPLOAD INCREMENT COUNTER **/
    private int IMAGE_UPLOAD_COUNTER = 0;

    /** THE VACCINES SPINNER ADAPTER AND ARRAY LIST **/
    private ArrayList<Vaccine> arrVaccines = new ArrayList<>();

    /** THE ARRAY LISTS FOR THE VACCINATION IMAGES **/
    private final ArrayList<AdoptionAlbumData> arrAlbums = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnVaccineTypes) AppCompatSpinner spnVaccineTypes;
    @BindView(R.id.txtVaccinationDate) AppCompatTextView txtVaccinationDate;
    @BindView(R.id.txtVaccinationNextDate) AppCompatTextView txtVaccinationNextDate;
    @BindView(R.id.chkbxRemind) AppCompatCheckBox chkbxRemind;
    @BindView(R.id.inpVaccineNotes) TextInputLayout inpVaccineNotes;
    @BindView(R.id.edtVaccineNotes) TextInputEditText edtVaccineNotes;
    @BindView(R.id.gridVaccinationImages) RecyclerView gridVaccinationImages;

    /** SELECT THE PET'S DATE OF VACCINATION **/
    @OnClick(R.id.btnVaccinationDate) void selectVaccinationDate()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                /* FOR THE DATABASE ONLY !!!! */
                VACCINATION_DATE = sdf.format(cal.getTime());

                /* FOR DISPLAY ONLY !!!! */
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                String selectedDate = dateFormat.format(cal.getTime());
                txtVaccinationDate.setText(selectedDate);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    /** SELECT THE PET'S NEXT DATE OF VACCINATION **/
    @OnClick(R.id.btnVaccinationNextDate) void nextDate()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                /* FOR THE DATABASE ONLY !!!! */
                VACCINATION_NEXT_DATE = sdf.format(cal.getTime());

                /* FOR DISPLAY ONLY !!!! */
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                String selectedDate = dateFormat.format(cal.getTime());
                txtVaccinationNextDate.setText(selectedDate);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    /** ADD VACCINATION IMAGES **/
    @OnClick(R.id.txtAddImages) void addImages()    {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaccination_creator);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* SET THE CURRENT DATE */
        setCurrentDate();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SELECT A VACCINE */
        spnVaccineTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED VACCINE ID AND NAME */
                VACCINE_ID = arrVaccines.get(position).getVaccineID();
                VACCINATION_NAME = arrVaccines.get(position).getVaccineName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* TOGGLE THE REMINDER CHECK BOX */
        chkbxRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)  {
                    VACCINATION_REMINDER = "True";
                } else {
                    VACCINATION_REMINDER = "False";
                }
            }
        });
    }

    /***** CHECK VACCINATION DETAILS *****/
    private void checkVaccineDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtVaccineNotes.getWindowToken(), 0);
        
        /* GET THE VACCINE NOTES */
        String VACCINATION_NOTES;
        if (TextUtils.isEmpty(edtVaccineNotes.getText().toString()))    {
            VACCINATION_NOTES = "Null";
        } else {
            VACCINATION_NOTES = edtVaccineNotes.getText().toString();
        }

        if (arrAlbums.size() > 0)  {
            /* SHOW THE PROGRESS DIALOG AND POST THE NEW VACCINATION RECORD */
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait while we publish the Vaccination record...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            new NewVaccination(this).execute(PET_ID, VACCINE_ID, VACCINATION_DATE, VACCINATION_NEXT_DATE,
                    VACCINATION_REMINDER, VACCINATION_NOTES);
        } else {
            new MaterialDialog.Builder(VaccinationCreator.this)
                    .icon(ContextCompat.getDrawable(VaccinationCreator.this, R.drawable.ic_info_outline_black_24dp))
                    .title(R.string.vaccination_creator_no_title)
                    .cancelable(false)
                    .content(getString(R.string.vaccination_creator_no_images))
                    .positiveText(R.string.generic_mb_yes)
                    .negativeText(R.string.generic_mb_no)
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
                            Toast.makeText(getApplicationContext(), "Successfully added medical record", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }).show();

        }
    }

    @Override
    public void onVaccination(String result) {
        if (result != null) {
            VACCINATION_ID = result;
            if (arrAlbums.size() > 0)   {
                /* PUBLISH THE VACCINATION IMAGES */
                publishVaccinationImages(VACCINATION_ID);
            } else {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully added Vaccination record", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "There was an error adding the Vaccination record. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    /****** PUBLISH THE VACCINATION IMAGES *****/
    private void publishVaccinationImages(final String vaccinationID) {
        Bitmap bitmap = arrAlbums.get(IMAGE_UPLOAD_COUNTER).getBmpAdoptionImage();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ZenPets/Vaccinations");
        myDir.mkdirs();
        final String imageNumber = String.valueOf(IMAGE_UPLOAD_COUNTER + 1);
        String fName = "photo" + imageNumber + ".jpg";
        File file = new File(myDir, fName);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            /* GET THE FINAL ADOPTION IMAGE URI */
            Uri uri = Uri.fromFile(file);
            String FILE_NAME;
            if (VACCINATION_NAME != null) {
                FILE_NAME = VACCINATION_NAME.replaceAll(
                        " ", "_").toLowerCase().trim() + "_" + VACCINE_ID + "_"
                        + VACCINATION_DATE + "_" + fName;
            } else {
                FILE_NAME = "VACCINATION_" + VACCINE_ID + "_" + VACCINATION_DATE + "_" + fName;
            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference refStorage = storageReference.child("Vaccination Images").child(FILE_NAME);
            UploadTask uploadTask = refStorage.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadURL = taskSnapshot.getDownloadUrl();
                    if (downloadURL != null) {
                        /* INCREMENT THE UPLOAD COUNTER AND UPLOAD THE IMAGE */
                        IMAGE_UPLOAD_COUNTER++;
                        new PostVaccinationImage(VaccinationCreator.this)
                                .execute(vaccinationID, String.valueOf(downloadURL));
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageResult(String result) {
        if (result != null) {
            if (IMAGE_UPLOAD_COUNTER == arrAlbums.size()) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully added Vaccination record", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
               /* PUBLISH THE NEXT IMAGE IN THE ARRAY */
                publishVaccinationImages(VACCINATION_ID);
            }
        }
    }

    @Override
    public void allVaccines(ArrayList<Vaccine> data) {
        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
        arrVaccines = data;

        /* SET THE ADAPTER TO THE VISIT REASONS SPINNER */
        spnVaccineTypes.setAdapter(new VaccinesAdapter(VaccinationCreator.this, arrVaccines));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Vaccination";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(VaccinationCreator.this);
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
                /* CHECK VACCINATION DETAILS */
                checkVaccineDetails();
                break;
            case R.id.menuCancel:
                /* CANCEL CATEGORY CREATION */
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("PET_ID"))   {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {
                /* FETCH THE LIST OF VACCINES */
                new AllVaccines(this).execute();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** SET THE CURRENT DATE *****/
    private void setCurrentDate() {
        /* SET THE CURRENT DATE (DISPLAY ONLY !!!!) */
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        txtVaccinationDate.setText(formattedDate);
        txtVaccinationNextDate.setText(formattedDate);

        /* SET THE CURRENT DATE (DATABASE ONLY !!!!) */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        VACCINATION_DATE = sdf.format(cal.getTime());
        VACCINATION_NEXT_DATE = sdf.format(cal.getTime());
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        int intOrientation = getResources().getConfiguration().orientation;
        gridVaccinationImages.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(this, 2);
                glm.setAutoMeasureEnabled(true);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(this, 4);
                glm.setAutoMeasureEnabled(true);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(this, 2);
                glm.setAutoMeasureEnabled(true);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(this, 4);
                glm.setAutoMeasureEnabled(true);
            }
        }
        gridVaccinationImages.setLayoutManager(glm);
        gridVaccinationImages.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        gridVaccinationImages.setAdapter(new AdoptionsAlbumAdapter(VaccinationCreator.this, arrAlbums));
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(VaccinationCreator.this,
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
                                        VaccinationCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            Matisse.from(VaccinationCreator.this)
                    .choose(MimeType.allOf())
                    .theme(R.style.Matisse_Zhihu)
                    .countable(true)
                    .maxSelectable(4)
                    .imageEngine(new PicassoEngine())
                    .forResult(101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            /* CLEAR THE ARRAY LIST */
            arrAlbums.clear();

            AdoptionAlbumData albums;
            for (int i = 0; i < Matisse.obtainResult(data).size(); i++) {
                albums = new AdoptionAlbumData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Matisse.obtainResult(data).get(i));
                    Bitmap bmpImage = resizeBitmap(bitmap);
                    albums.setBmpAdoptionImage(bmpImage);

                    /* SET THE IMAGE NUMBER */
                    String strNumber = String.valueOf(i + 1);
                    albums.setTxtImageNumber(strNumber);

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrAlbums.add(albums);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /* SET THE ADAPTER TO THE RECYCLER VIEW */
            gridVaccinationImages.setAdapter(new AdoptionsAlbumAdapter(VaccinationCreator.this, arrAlbums));
            gridVaccinationImages.setVisibility(View.VISIBLE);
        }
    }

    /** RESIZE THE BITMAP **/
    private Bitmap resizeBitmap(Bitmap image)   {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = 800;
            height = (int) (width / bitmapRatio);
        } else {
            height = 800;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                Matisse.from(VaccinationCreator.this)
                        .choose(MimeType.allOf())
                        .theme(R.style.Matisse_Zhihu)
                        .countable(true)
                        .maxSelectable(6)
                        .imageEngine(new PicassoEngine())
                        .forResult(101);
            } else {
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title("Permission Denied...")
                        .cancelable(false)
                        .content("To add images to Vaccination Record, Zen Pets requires this permission." +
                                "\n\nTo continue without adding images, click the \"Now Now\" button. To grant the permission, click the \"Grant Permission\" button")
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                /* CHECK FOR THE PERMISSION AGAIN */
                                checkStoragePermission();
                            }
                        }).show();
            }
        }
    }
}