package co.zenpets.users.creator.adoption;

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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
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
import android.widget.RadioGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.adoptions.AdoptionsAlbumAdapter;
import co.zenpets.users.utils.adapters.pet.BreedsAdapter;
import co.zenpets.users.utils.adapters.pet.PetTypesAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.adoptions.AdoptionAlbumData;
import co.zenpets.users.utils.models.adoptions.adoption.Adoption;
import co.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import co.zenpets.users.utils.models.adoptions.images.AdoptionImage;
import co.zenpets.users.utils.models.adoptions.images.AdoptionImagesAPI;
import co.zenpets.users.utils.models.pets.breeds.Breed;
import co.zenpets.users.utils.models.pets.breeds.Breeds;
import co.zenpets.users.utils.models.pets.breeds.BreedsAPI;
import co.zenpets.users.utils.models.pets.types.PetType;
import co.zenpets.users.utils.models.pets.types.PetTypes;
import co.zenpets.users.utils.models.pets.types.PetTypesAPI;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class AdoptionCreator extends AppCompatActivity
        /*implements PetTypesInterface, FetchBreedTypesInterface*/ {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER AUTH ID **/
    private String USER_AUTH_ID = null;

    /** DATA TYPES TO HOLD THE NEW ADOPTION LISTING DETAILS **/
    private String ADOPTION_ID = null;
    private String USER_ID = null;
    private String CITY_ID = null;
    private String PET_TYPE_ID = null;
    private String PET_BREED_ID = null;
    private String PET_NAME = null;
    private String PET_GENDER = "Male";
    private String PET_VACCINATED = "Yes";
    private String PET_DEWORMED = "Yes";
    private String PET_NEUTERED = "Yes";
    private String PET_DESCRIPTION = null;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /* THE PET TYPES ARRAY LIST */
    private ArrayList<PetType> arrPetTypes = new ArrayList<>();

    /** THE BREEDS ARRAY LIST **/
    private ArrayList<Breed> arrBreeds = new ArrayList<>();

    /** THE ARRAY LISTS FOR THE ADOPTION ALBUMS **/
    private final ArrayList<AdoptionAlbumData> arrAlbums = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE UPLOAD INCREMENT COUNTER **/
    private int IMAGE_UPLOAD_COUNTER = 0;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnPetTypes) AppCompatSpinner spnPetTypes;
    @BindView(R.id.spnBreeds) AppCompatSpinner spnBreeds;
    @BindView(R.id.inputPetName) TextInputLayout inputPetName;
    @BindView(R.id.edtPetName) AppCompatEditText edtPetName;
    @BindView(R.id.rdgGender) RadioGroup rdgGender;
    @BindView(R.id.rdgVaccinated) RadioGroup rdgVaccinated;
    @BindView(R.id.rdgDewormed) RadioGroup rdgDewormed;
    @BindView(R.id.rdgNeutered) RadioGroup rdgNeutered;
    @BindView(R.id.inputDescription) TextInputLayout inputDescription;
    @BindView(R.id.edtDescription) AppCompatEditText edtDescription;
    @BindView(R.id.gridAdoptionImages) RecyclerView gridAdoptionImages;

    /** ADD ADOPTION IMAGES **/
    @OnClick(R.id.txtAddImages) void imageSelectImages()  {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_creator);
        ButterKnife.bind(this);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* FETCH THE USER'S NAME AND DISPLAY PROFILE */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_AUTH_ID = user.getUid();
            /* FETCH THE USER'S PROFILE DETAILS */
            fetchProfileDetails();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
        }

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* FETCH A LIST OF ALL PET TYPES */
        fetchPetTypes();
//        new PetTypes(this).execute();

        /* SELECT A PET TYPE */
        spnPetTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED PET TYPE ID */
                PET_TYPE_ID = arrPetTypes.get(position).getPetTypeID();

                /* CLEAR THE BREEDS ARRAY LIST */
                arrBreeds.clear();

                /* FETCH THE LIST OF BREEDS IN THE SELECTED PET TYPE */
                fetchBreedTypes();
//                new FetchBreedTypes(AdoptionCreator.this).execute(PET_TYPE_ID);
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

        /* SELECT THE PET'S VACCINATION STATUS */
        rdgVaccinated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnVaccinatedYes:
                        /* SET THE VACCINATED STATUS TO "YES" */
                        PET_VACCINATED = "Yes";
                        break;
                    case R.id.rdbtnVaccinatedNo:
                        /* SET THE VACCINATED STATUS TO "NO" */
                        PET_VACCINATED = "No";
                        break;
                    default:
                        break;
                }
            }
        });

        /* SELECT THE PET'S DEWORMING STATUS */
        rdgVaccinated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnDewormedYes:
                        /* SET THE DEWORMED STATUS TO "YES" */
                        PET_DEWORMED = "Yes";
                        break;
                    case R.id.rdbtnDewormedNo:
                        /* SET THE DEWORMED STATUS TO "NO" */
                        PET_DEWORMED = "No";
                        break;
                    default:
                        break;
                }
            }
        });

        /* SELECT THE PET'S NEUTERED STATUS */
        rdgVaccinated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnNeuteredYes:
                        /* SET THE NEUTERED STATUS TO "YES" */
                        PET_NEUTERED = "Yes";
                        break;
                    case R.id.rdbtnNeuteredNo:
                        /* SET THE NEUTERED STATUS TO "NO" */
                        PET_NEUTERED = "No";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /***** CHECK FOR ALL PET ADOPTION DETAILS  *****/
    private void checkDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtPetName.getWindowToken(), 0);
        }

        /* COLLECT THE REQUIRED DATA */
        if (!TextUtils.isEmpty(edtPetName.getText().toString()))    {
            PET_NAME = edtPetName.getText().toString();
        } else {
            PET_NAME = "Null";
        }
        PET_DESCRIPTION = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(PET_DESCRIPTION)) {
            edtDescription.setError("Please provide the Pet's Description");
            edtDescription.requestFocus();
        } else if (arrAlbums.size() <= 0) {
            Toast.makeText(getApplicationContext(), "Upload at least one picture of the Pet", Toast.LENGTH_SHORT).show();
        } else  {
            /* POST THE ADOPTION LISTING */
            postListing();
        }
    }

    /***** POST THE ADOPTION LISTING *****/
    private void postListing() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we publish your new Adoption listing");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoption> call = api.newAdoption(
                PET_TYPE_ID, PET_BREED_ID, USER_ID, CITY_ID,
                PET_NAME, PET_DESCRIPTION, PET_GENDER,
                PET_VACCINATED, PET_DEWORMED, PET_NEUTERED, timeStamp, "Open"
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
            if (PET_NAME != null) {
                FILE_NAME = PET_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + USER_ID + "_" + fName;
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
        MenuInflater inflater = new MenuInflater(AdoptionCreator.this);
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
                /* CHECK FOR ALL PET ADOPTION DETAILS  */
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

    /***** FETCH THE USER'S PROFILE DETAILS *****/
    private void fetchProfileDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(USER_AUTH_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
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
        gridAdoptionImages.setAdapter(new AdoptionsAlbumAdapter(AdoptionCreator.this, arrAlbums));
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
            gridAdoptionImages.setAdapter(new AdoptionsAlbumAdapter(AdoptionCreator.this, arrAlbums));
            gridAdoptionImages.setVisibility(View.VISIBLE);
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

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(AdoptionCreator.this,
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
                                        AdoptionCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            Matisse.from(AdoptionCreator.this)
                    .choose(MimeType.allOf())
                    .theme(R.style.Matisse_Zhihu)
                    .countable(true)
                    .maxSelectable(6)
                    .imageEngine(new PicassoEngine())
                    .forResult(101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                Matisse.from(AdoptionCreator.this)
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
                        .content("Since the permission was denied, Zen Pets cannot select images for posting your new Adoption. And because Adoptions cannot be posted without at least one image, you cannot add one now." +
                                "\n\nTo grant the permission, click the \"GRANT PERMISSION\" button. To go back without adding a new Adoption, click the \"NOT NOW\" button.")
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

    /** FETCH A LIST OF ALL PET TYPES **/
    private void fetchPetTypes() {
        PetTypesAPI api = ZenApiClient.getClient().create(PetTypesAPI.class);
        Call<PetTypes> call = api.petTypes();
        call.enqueue(new Callback<PetTypes>() {
            @Override
            public void onResponse(Call<PetTypes> call, Response<PetTypes> response) {
                if (response.body() != null && response.body().getTypes() != null)  {
                    arrPetTypes = response.body().getTypes();
                    if (arrPetTypes.size() > 0)    {

                        /* INSTANTIATE THE PET TYPES ADAPTER */
                        PetTypesAdapter petTypesAdapter = new PetTypesAdapter(AdoptionCreator.this, arrPetTypes);

                        /* SET THE ADAPTER TO THE PET TYPES SPINNER */
                        spnPetTypes.setAdapter(petTypesAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Problem fetching list of Pet Types...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Problem fetching list of Pet Types...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PetTypes> call, Throwable t) {
//                Log.e("TYPE FAILURE", t.getMessage());
            }
        });
    }

    /** FETCH THE LIST OF BREEDS IN THE SELECTED PET TYPE **/
    private void fetchBreedTypes() {
        BreedsAPI api = ZenApiClient.getClient().create(BreedsAPI.class);
        Call<Breeds> call = api.allPetBreeds(PET_TYPE_ID);
        call.enqueue(new Callback<Breeds>() {
            @Override
            public void onResponse(Call<Breeds> call, Response<Breeds> response) {
                if (response.body() != null && response.body().getBreeds() != null) {
                    arrBreeds = response.body().getBreeds();
                    if (arrPetTypes.size() > 0)    {

                        /* INSTANTIATE THE BREEDS ADAPTER */
                        BreedsAdapter breedsAdapter = new BreedsAdapter(AdoptionCreator.this, arrBreeds);

                        /* SET THE ADAPTER TO THE BREEDS SPINNER */
                        spnBreeds.setAdapter(breedsAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Problem fetching list of Pet Types...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Problem fetching list of Pet Breeds...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Breeds> call, Throwable t) {
//                Log.e("BREEDS FAILURE", t.getMessage());
            }
        });
    }

//    @Override
//    public void petTypes(ArrayList<PetTypesData> data) {
//        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
//        arrPetTypes = data;
//
//        /* INSTANTIATE THE PET TYPES ADAPTER */
//        PetTypesAdapter petTypesAdapter = new PetTypesAdapter(AdoptionCreator.this, arrPetTypes);
//
//        /* SET THE ADAPTER TO THE PET TYPES SPINNER */
//        spnPetTypes.setAdapter(petTypesAdapter);
//    }

//    @Override
//    public void breedTypes(ArrayList<Breed> data) {
//        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
//        arrBreeds = data;
//
//        /* INSTANTIATE THE BREEDS ADAPTER */
//        BreedsAdapter breedsAdapter = new BreedsAdapter(AdoptionCreator.this, arrBreeds);
//
//        /* SET THE ADAPTER TO THE BREEDS SPINNER */
//        spnBreeds.setAdapter(breedsAdapter);
//    }
}