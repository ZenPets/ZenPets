package co.zenpets.doctors.creator.clinic.images;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.clinics.ClinicImagesManagerAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.clinics.images.ClinicImagesAPI;
import co.zenpets.doctors.utils.models.clinics.images.ImageData;
import co.zenpets.doctors.utils.models.clinics.images.ImagesData;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class ClinicImagesManager extends AppCompatActivity {

    /** THE INCOMING CLINIC ID **/
    private String CLINIC_ID = null;

    /* THE CLINIC IMAGE URI AND URL (FOR UPLOADING THE SELECTED IMAGE) */
    private Uri CLINIC_IMAGE_URI = null;
    private String CLINIC_IMAGE_URL = null;

    /** THE CLINIC IMAGE CONFIRMATION CUSTOM VIEW **/
    private View clinicConfirmation;

    /** THE IMAGE VIEW INSTANCE IN THE CUSTOM DIALOG **/
    private AppCompatImageView imgvwClinicImage;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private ClinicImagesManagerAdapter imagesAdapter;
    private ArrayList<ImageData> arrImages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.gridClinicImages) RecyclerView gridClinicImages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD AN IMAGE TO THE CLINIC **/
    @OnClick(R.id.linlaEmpty) protected void addImage() {
        /* SHOW THE IMAGE SELECTOR SHEET */
        showImageSheet();
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_images_manager_list);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INSTANTIATE THE CLINIC IMAGES ADAPTER */
        imagesAdapter = new ClinicImagesManagerAdapter(ClinicImagesManager.this, arrImages);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* INFLATE THE CUSTOM VIEWS */
        clinicConfirmation = LayoutInflater.from(getApplicationContext()).inflate(R.layout.clinic_images_manager_confirmation, null);
    }

    /***** FETCH THE CLINIC IMAGES *****/
    private void fetchClinicImages() {
        ClinicImagesAPI apiInterface = ZenApiClient.getClient().create(ClinicImagesAPI.class);
        retrofit2.Call<ImagesData> call = apiInterface.fetchClinicImages(CLINIC_ID);
        call.enqueue(new retrofit2.Callback<ImagesData>() {
            @Override
            public void onResponse(@NonNull Call<ImagesData> call, @NonNull Response<ImagesData> response) {
                arrImages = response.body().getImages();
                if (arrImages != null && arrImages.size() > 0)  {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    gridClinicImages.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* INSTANTIATE THE ADAPTER */
                    imagesAdapter = new ClinicImagesManagerAdapter(ClinicImagesManager.this, arrImages);

                    /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                    gridClinicImages.setAdapter(imagesAdapter);
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    gridClinicImages.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ImagesData> call, @NonNull Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Image Manager";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(ClinicImagesManager.this);
        inflater.inflate(R.menu.activity_new_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuNewImage:
                /* SHOW THE IMAGE SELECTOR SHEET */
                showImageSheet();
                break;
            default:
                break;
        }
        return false;
    }

    private void showImageSheet() {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(ClinicImagesManager.this);
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
                    EasyImage.openGallery(ClinicImagesManager.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(ClinicImagesManager.this, 0);
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
//                Log.e("SIZE", String.valueOf(imageFiles.size()));
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
                CLINIC_IMAGE_URI = Uri.fromFile(file);
//                Log.e("URI", String.valueOf(CLINIC_IMAGE_URI));
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* SHOW THE CONFIRMATION SCREEN */
            showConfirmationScreen(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** SHOW THE CONFIRMATION SCREEN *****/
    private void showConfirmationScreen(Bitmap bitmap) {
        /* CONFIGURE THE DIALOG */
        MaterialDialog dialogConfirmation = new MaterialDialog.Builder(ClinicImagesManager.this)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Regular.ttf", "Roboto-Regular.ttf")
                .cancelable(false)
                .customView(clinicConfirmation, false)
                .positiveText("Upload")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        /* SHOW THE PROGRESS DIALOG */
                        progressDialog = new ProgressDialog(ClinicImagesManager.this);
                        progressDialog.setMessage("Please wait while we add the new Clinic image...");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        /* GET THE TIME STAMP */
                        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                        String FILE_NAME = "CLINIC_" + CLINIC_ID + "_" + timestamp;

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference refStorage = storageReference.child("Clinic Images").child(FILE_NAME);
                        UploadTask uploadTask = refStorage.putFile(CLINIC_IMAGE_URI);
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
                                    CLINIC_IMAGE_URL = String.valueOf(downloadURL);
                                    if (CLINIC_IMAGE_URL != null)    {
                                        ClinicImagesAPI api = ZenApiClient.getClient().create(ClinicImagesAPI.class);
                                        Call<ImageData> call = api.postClinicImages(CLINIC_ID, CLINIC_IMAGE_URL);
                                        call.enqueue(new Callback<ImageData>() {
                                            @Override
                                            public void onResponse(Call<ImageData> call, Response<ImageData> response) {
                                                if (response.isSuccessful())    {
                                                    /* DISMISS THE DIALOG */
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "The new Clinic image was successfully uploaded.", Toast.LENGTH_LONG).show();

                                                    /* CLEAR THE ARRAY */
                                                    if (arrImages != null)
                                                        arrImages.clear();

                                                    /* FETCH THE CLINIC IMAGES (AGAIN) */
                                                    fetchClinicImages();
                                                } else {
                                                    /* DISMISS THE DIALOG */
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "There was an error posting this image. Please try again...", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ImageData> call, Throwable t) {
                                            }
                                        });
                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "There was an error posting this image. Please try again...",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();

        /* CAST THE IMAGE VIEW */
        imgvwClinicImage = dialogConfirmation.getCustomView().findViewById(R.id.imgvwClinicImage);

        /* SET THE BITMAP TO THE IMAGE VIEW AND SET THE SCALE TYPE */
        imgvwClinicImage.setImageBitmap(bitmap);
        imgvwClinicImage.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

        /* SHOW THE DIALOG */
        dialogConfirmation.show();
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CLINIC_ID"))    {
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (CLINIC_ID != null)  {
                /* FETCH THE CLINIC IMAGES */
                fetchClinicImages();
//                new FetchClinicImages(this).execute(CLINIC_ID);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION FOR THE IMAGES GRID */
        int intOrientation = getResources().getConfiguration().orientation;
        gridClinicImages.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(this, 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(this, 4);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(this, 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(this, 4);
            }
        }
        gridClinicImages.setLayoutManager(glm);

        /* SET THE CLINIC IMAGES ADAPTER */
        gridClinicImages.setAdapter(imagesAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}