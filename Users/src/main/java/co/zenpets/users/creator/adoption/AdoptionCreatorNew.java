package co.zenpets.users.creator.adoption;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.adoptions.AdoptionsAlbumAdapter;
import co.zenpets.users.utils.adapters.pet.BreedsAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.adoptions.AdoptionAlbumData;
import co.zenpets.users.utils.models.adoptions.adoption.Adoption;
import co.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import co.zenpets.users.utils.models.adoptions.images.AdoptionImage;
import co.zenpets.users.utils.models.adoptions.images.AdoptionImagesAPI;
import co.zenpets.users.utils.models.pets.breeds.Breed;
import co.zenpets.users.utils.models.pets.breeds.Breeds;
import co.zenpets.users.utils.models.pets.breeds.BreedsAPI;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdoptionCreatorNew extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID, USER_AUTH_ID, AND CITY ID **/
    private String USER_ID = null;
    private String USER_AUTH_ID = null;
    private String CITY_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.groupSpecies) RadioGroup groupSpecies;
    @BindView(R.id.spnBreeds) Spinner spnBreeds;
    @BindView(R.id.inputAdoptionName) TextInputLayout inputAdoptionName;
    @BindView(R.id.edtAdoptionName) TextInputEditText edtAdoptionName;
    @BindView(R.id.groupGender) RadioGroup groupGender;
    @BindView(R.id.inputDescription) TextInputLayout inputDescription;
    @BindView(R.id.edtDescription) TextInputEditText edtDescription;
    @BindView(R.id.imgvwAdoptionCover) ImageView imgvwAdoptionCover;
    @BindView(R.id.gridAdoptionImages) RecyclerView gridAdoptionImages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** BOOLEAN TO CHECK IF USER IS SELECTING A COVER PHOTO (FALSE) OR ADOPTION IMAGES (TRUE) **/
    private boolean blnSource = false;

    /** THE BREEDS ARRAY LIST **/
    private ArrayList<Breed> arrBreeds = new ArrayList<>();

    /** THE ARRAY LISTS FOR THE ADOPTION ALBUMS **/
    private final ArrayList<AdoptionAlbumData> arrAlbums = new ArrayList<>();

    /** THE UPLOAD INCREMENT COUNTER **/
    private int IMAGE_UPLOAD_COUNTER = 0;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE OBJECTS TO HOLD THE ADOPTION DETAILS **/
    private String ADOPTION_ID = null;
    private String ADOPTION_PET_TYPE_ID = "1";
    private String ADOPTION_BREED_ID = null;
    private String ADOPTION_GENDER = "Male";
    private String ADOPTION_NAME = null;
    private String ADOPTION_DESCRIPTION = null;
    private Uri ADOPTION_COVER_URI = null;
    private String FILE_NAME = null;
    private String ADOPTION_COVER_URL = null;

    /** SELECT THE ADOPTION COVER PHOTO **/
    @OnClick(R.id.imgvwAdoptionCover) void selectCover()    {
        /* TOGGLE THE FLAG TO FALSE */
        blnSource = false;

        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    /** SELECT THE ADOPTION IMAGES **/
    @OnClick(R.id.linlaEmpty) void selectImages()   {
        /* TOGGLE THE FLAG TO TRUE */
        blnSource = true;

        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_creator_new);
        ButterKnife.bind(this);

        /* FETCH THE USER'S NAME AND DISPLAY PROFILE */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_AUTH_ID = user.getUid();
            /* FETCH THE USER'S PROFILE DETAILS */
            fetchProfileDetails();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
        }

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SELECT THE ADOPTION PET'S SPECIES */
        groupSpecies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnDog:
                        /* SET THE PET SPECIES TO "1" (PET TYPE ID)*/
                        ADOPTION_PET_TYPE_ID = "1";

                        /* CLEAR THE BREEDS ARRAY LIST */
                        arrBreeds.clear();

                        /* FETCH THE LIST OF BREEDS */
                        fetchBreedsList();

                        break;
                    case R.id.rdbtnCat:
                        /* SET THE PET SPECIES TO "2" (PET TYPE ID)*/
                        ADOPTION_PET_TYPE_ID = "2";

                        /* CLEAR THE BREEDS ARRAY LIST */
                        arrBreeds.clear();

                        /* FETCH THE LIST OF BREEDS */
                        fetchBreedsList();

                        break;
                    default:
                        break;
                }
            }
        });

        /* FETCH THE LIST OF BREEDS */
        fetchBreedsList();

        /* SELECT THE ADOPTION PET'S GENDER */
        groupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnMale:
                        /* SET THE GENDER TO MALE */
                        ADOPTION_GENDER = "Male";
                        break;
                    case R.id.rdbtnFemale:
                        /* SET THE GENDER TO FEMALE */
                        ADOPTION_GENDER = "Female";
                        break;
                    default:
                        break;
                }
            }
        });

        /* SELECT A BREED */
        spnBreeds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ADOPTION_BREED_ID = arrBreeds.get(position).getBreedID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** FETCH THE LIST OF BREEDS **/
    private void fetchBreedsList() {
        BreedsAPI api = ZenApiClient.getClient().create(BreedsAPI.class);
        Call<Breeds> call = api.allPetBreeds(ADOPTION_PET_TYPE_ID);
        call.enqueue(new Callback<Breeds>() {
            @Override
            public void onResponse(Call<Breeds> call, Response<Breeds> response) {
                arrBreeds = response.body().getBreeds();

                /* INSTANTIATE THE BREEDS ADAPTER */
                BreedsAdapter breedsAdapter = new BreedsAdapter(AdoptionCreatorNew.this, arrBreeds);

                /* SET THE ADAPTER TO THE BREEDS SPINNER */
                spnBreeds.setAdapter(breedsAdapter);
            }

            @Override
            public void onFailure(Call<Breeds> call, Throwable t) {
//                Log.e("BREEDS FAILURE", t.getMessage());
            }
        });
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(AdoptionCreatorNew.this,
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
                                        AdoptionCreatorNew.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            if (blnSource)  {
                Matisse.from(AdoptionCreatorNew.this)
                        .choose(MimeType.allOf())
                        .theme(R.style.Matisse_Zhihu)
                        .countable(true)
                        .maxSelectable(10)
                        .imageEngine(new PicassoEngine())
                        .forResult(101);
            } else {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(AdoptionCreatorNew.this);
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
                        EasyImage.openGallery(AdoptionCreatorNew.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(AdoptionCreatorNew.this, 0);
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                if (blnSource)  {
                    Matisse.from(AdoptionCreatorNew.this)
                            .choose(MimeType.allOf())
                            .theme(R.style.Matisse_Zhihu)
                            .countable(true)
                            .maxSelectable(10)
                            .imageEngine(new PicassoEngine())
                            .forResult(101);
                } else {
                    final BottomSheetDialog sheetDialog = new BottomSheetDialog(AdoptionCreatorNew.this);
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
                            EasyImage.openGallery(AdoptionCreatorNew.this, 0);
                        }
                    });

                    /* SELECT A CAMERA IMAGE */
                    linlaCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sheetDialog.dismiss();
                            EasyImage.openCamera(AdoptionCreatorNew.this, 0);
                        }
                    });
                }
            } else {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
            gridAdoptionImages.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);

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
            gridAdoptionImages.setAdapter(new AdoptionsAlbumAdapter(AdoptionCreatorNew.this, arrAlbums));
            gridAdoptionImages.setVisibility(View.VISIBLE);
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                    onPhotoReturned(imageFiles);
                }
            });
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
            imgvwAdoptionCover.setImageBitmap(bitmap);
            imgvwAdoptionCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                ADOPTION_COVER_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Adoption Listing";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(AdoptionCreatorNew.this);
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
                /* CHECK FOR ALL ADOPTION DETAILS  */
                checkDetails();
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

    /** CHECK FOR ALL ADOPTION DETAILS  **/
    private void checkDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtAdoptionName.getWindowToken(), 0);
        }

        /* COLLECT THE REQUIRED DATA */
        if (!TextUtils.isEmpty(edtAdoptionName.getText().toString()))    {
            ADOPTION_NAME = edtAdoptionName.getText().toString();
        } else {
            ADOPTION_NAME = "Null";
        }
        ADOPTION_DESCRIPTION = edtDescription.getText().toString().trim();

        if (ADOPTION_COVER_URI != null && ADOPTION_NAME != null)   {
            FILE_NAME = ADOPTION_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + USER_ID;
        } else {
            FILE_NAME = null;
            ADOPTION_COVER_URL = "Null";
        }

        if (TextUtils.isEmpty(ADOPTION_DESCRIPTION)) {
            edtDescription.setError("Please provide the Pet's Description");
            edtDescription.requestFocus();
        } else if (ADOPTION_COVER_URI == null)  {
            Toast.makeText(getApplicationContext(), "Please choose a Cover Photo", Toast.LENGTH_SHORT).show();
        } else if (arrAlbums.size() <= 0) {
            Toast.makeText(getApplicationContext(), "Add at least one picture of the Pet (excluding the Cover Photo)", Toast.LENGTH_LONG).show();
        } else  {
            /* PUBLISH THE ADOPTION COVER PHOTO */
            publishAdoptionCover();
        }
    }

    /** PUBLISH THE ADOPTION COVER PHOTO **/
    private void publishAdoptionCover() {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we publish your question..");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* PUBLISH THE ADOPTION COVER PHOTO TO FIREBASE */
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Adoption Covers").child(FILE_NAME);
        UploadTask uploadTask = refStorage.putFile(ADOPTION_COVER_URI);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                /* CONTINUE WITH THE TASK TO GET THE IMAGE URL */
                return refStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadURL = task.getResult();
                    ADOPTION_COVER_URL = String.valueOf(downloadURL);
                    if (ADOPTION_COVER_URL != null)    {
                        /* PUBLISH THE ADOPTION LISTING */
                        publishAdoptionListing();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                "Error publishing adoption...",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "Error publishing adoption...",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /** PUBLISH THE ADOPTION LISTING **/
    private void publishAdoptionListing() {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoption> call = api.newTestAdoption(
                ADOPTION_PET_TYPE_ID, ADOPTION_BREED_ID, USER_ID, CITY_ID,
                ADOPTION_NAME, ADOPTION_COVER_URL, ADOPTION_DESCRIPTION, ADOPTION_GENDER,
                timeStamp, "Open"
        );
        call.enqueue(new Callback<Adoption>() {
            @Override
            public void onResponse(Call<Adoption> call, Response<Adoption> response) {
                if (response.isSuccessful())    {
                    /* GET THE ADOPTION ID */
                    ADOPTION_ID = response.body().getAdoptionID();

                    /* PUBLISH THE ADOPTION IMAGES */
                    publishAdoptionImages();
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error posting the new adoption. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Adoption> call, Throwable t) {
//                Log.e("ADOPTION FAILURE", t.getMessage());
            }
        });
    }

    /***** PUBLISH THE ADOPTION IMAGES *****/
    private void publishAdoptionImages()  {
        Bitmap bitmap = arrAlbums.get(IMAGE_UPLOAD_COUNTER).getBmpAdoptionImage();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ZenPets/Adoptions");
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
            if (ADOPTION_NAME != null) {
                FILE_NAME = ADOPTION_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + USER_ID + "_" + fName;
            } else {
                FILE_NAME = "ADOPTION_" + USER_ID + "_" + fName;
            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference refStorage = storageReference.child("Adoptions").child(FILE_NAME);
            UploadTask uploadTask = refStorage.putFile(uri);
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    /* CONTINUE WITH THE TASK TO GET THE IMAGE URL */
                    return refStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadURL = task.getResult();
                        if (downloadURL != null) {
                            /* INCREMENT THE UPLOAD COUNTER AND UPLOAD THE IMAGE */
                            IMAGE_UPLOAD_COUNTER++;
                            postImage(String.valueOf(downloadURL));
                        }
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** POST / PUBLISH THE ADOPTION IMAGE *****/
    private void postImage(String imageURL)    {
        AdoptionImagesAPI api = ZenApiClient.getClient().create(AdoptionImagesAPI.class);
        Call<AdoptionImage> call = api.postAdoptionImages(
                ADOPTION_ID, imageURL);
        call.enqueue(new Callback<AdoptionImage>() {
            @Override
            public void onResponse(Call<AdoptionImage> call, Response<AdoptionImage> response) {
                if (response.isSuccessful())    {
                    if (IMAGE_UPLOAD_COUNTER == arrAlbums.size()) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "New Adoption listed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        /* PUBLISH THE NEXT IMAGE IN THE ARRAY */
                        publishAdoptionImages();
                    }
                }
            }

            @Override
            public void onFailure(Call<AdoptionImage> call, Throwable t) {
            }
        });
    }

    /***** FETCH THE USER'S PROFILE DETAILS *****/
    private void fetchProfileDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(USER_AUTH_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    /* GET THE USER'S ID */
                    USER_ID = data.getUserID();

                    /* GET THE USER'S CITY ID */
                    CITY_ID = data.getCityID();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        int intOrientation = getResources().getConfiguration().orientation;
        gridAdoptionImages.setHasFixedSize(true);
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
        gridAdoptionImages.setLayoutManager(glm);
        gridAdoptionImages.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        gridAdoptionImages.setAdapter(new AdoptionsAlbumAdapter(AdoptionCreatorNew.this, arrAlbums));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}