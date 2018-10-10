package co.zenpets.groomers.images;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
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

import co.zenpets.groomers.R;
import co.zenpets.groomers.utils.TypefaceSpan;
import co.zenpets.groomers.utils.adapters.images.ImagesManagerAdapter;
import co.zenpets.groomers.utils.helpers.ZenApiClient;
import co.zenpets.groomers.utils.models.images.GroomerImage;
import co.zenpets.groomers.utils.models.images.GroomerImages;
import co.zenpets.groomers.utils.models.images.GroomerImagesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomerImageManager extends AppCompatActivity {

    /** THE INCOMING GROOMER ID AND NAME **/
    private String GROOMER_ID = null;
    private String GROOMER_NAME = null;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /* THE IMAGE URI AND URL (FOR UPLOADING THE SELECTED IMAGE) */
    private Uri GROOMER_IMAGE_URI = null;
    private String GROOMER_IMAGE_URL = null;

    /** THE GROOMER IMAGE CONFIRMATION CUSTOM VIEW **/
    private View groomerConfirmation;

    /** THE IMAGE VIEW INSTANCE IN THE CUSTOM DIALOG **/
    private ImageView imgvwGroomerImage;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** THE GROOMER IMAGES ARRAY LIST **/
    private ArrayList<GroomerImage> arrImages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.gridGroomerImages) RecyclerView gridGroomerImages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD AN IMAGE TO THE CLINIC **/
    @OnClick(R.id.linlaEmpty) protected void addImage() {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomer_image_manager_list);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(false)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(false);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INFLATE THE CUSTOM VIEWS */
        groomerConfirmation = LayoutInflater.from(getApplicationContext()).inflate(R.layout.groomer_image_manager_confirmation, null);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /** FETCH THE GROOMER'S SERVICE IMAGES **/
    private void fetchGroomerImages() {
        GroomerImagesAPI api = ZenApiClient.getClient().create(GroomerImagesAPI.class);
        Call<GroomerImages> call = api.fetchGroomerImages(GROOMER_ID);
        call.enqueue(new Callback<GroomerImages>() {
            @Override
            public void onResponse(Call<GroomerImages> call, Response<GroomerImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages != null && arrImages.size() > 0)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        gridGroomerImages.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        gridGroomerImages.setAdapter(new ImagesManagerAdapter(GroomerImageManager.this, arrImages));
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        gridGroomerImages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    gridGroomerImages.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GroomerImages> call, Throwable t) {
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
        MenuInflater inflater = new MenuInflater(GroomerImageManager.this);
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
        final BottomSheetDialog sheetDialog = new BottomSheetDialog(GroomerImageManager.this);
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
                EasyImage.openGallery(GroomerImageManager.this, 0);
            }
        });

        /* SELECT A CAMERA IMAGE */
        linlaCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                EasyImage.openCamera(GroomerImageManager.this, 0);
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
                GROOMER_IMAGE_URI = Uri.fromFile(file);
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

    /** SHOW THE CONFIRMATION SCREEN **/
    private void showConfirmationScreen(Bitmap bitmap) {
        /* CONFIGURE THE DIALOG */
        MaterialDialog dialogConfirmation = new MaterialDialog.Builder(GroomerImageManager.this)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Regular.ttf", "Roboto-Regular.ttf")
                .cancelable(false)
                .customView(groomerConfirmation, false)
                .positiveText("Upload")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        /* SHOW THE PROGRESS DIALOG */
                        progressDialog = new ProgressDialog(GroomerImageManager.this);
                        progressDialog.setMessage("Adding the new Grooming service image...");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        /* GET THE TIME STAMP */
                        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                        String FILE_NAME = "GROOMER_" + GROOMER_ID + "_" + timestamp;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference refStorage = storageReference.child("Groomer Images").child(FILE_NAME);
                        UploadTask uploadTask = refStorage.putFile(GROOMER_IMAGE_URI);
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
                                    GROOMER_IMAGE_URL = String.valueOf(task.getResult());
                                    if (GROOMER_IMAGE_URL != null)    {
                                        GroomerImagesAPI api = ZenApiClient.getClient().create(GroomerImagesAPI.class);
                                        Call<GroomerImage> call = api.newGroomerImage(GROOMER_ID, GROOMER_IMAGE_URL);
                                        call.enqueue(new Callback<GroomerImage>() {
                                            @Override
                                            public void onResponse(Call<GroomerImage> call, Response<GroomerImage> response) {
                                                if (response.isSuccessful())    {
                                                    /* DISMISS THE DIALOG */
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Image successfully published....", Toast.LENGTH_LONG).show();

                                                    /* CLEAR THE ARRAY */
                                                    if (arrImages != null)
                                                        arrImages.clear();

                                                    /* FETCH THE GROOMER SERVICE IMAGES (AGAIN) */
                                                    fetchGroomerImages();
                                                } else {
                                                    /* DISMISS THE DIALOG */
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Error publishing image...", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<GroomerImage> call, Throwable t) {
//                                                Log.e("IMAGE FAILURE", t.getMessage());
//                                                Crashlytics.logException(t);
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
        imgvwGroomerImage = dialogConfirmation.getCustomView().findViewById(R.id.imgvwGroomerImage);

        /* SET THE BITMAP TO THE IMAGE VIEW AND SET THE SCALE TYPE */
        imgvwGroomerImage.setImageBitmap(bitmap);
        imgvwGroomerImage.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

        /* SHOW THE DIALOG */
        dialogConfirmation.show();
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(GroomerImageManager.this,
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
                                        GroomerImageManager.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            /* SHOW THE IMAGE SELECTOR SHEET */
            showImageSheet();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                /* SHOW THE IMAGE SELECTOR SHEET */
                showImageSheet();
            } else {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_denied))
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
                                        GroomerImageManager.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            }
        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("GROOMER_ID") && bundle.containsKey("GROOMER_NAME"))    {
            GROOMER_ID = bundle.getString("GROOMER_ID");
            GROOMER_NAME = bundle.getString("GROOMER_NAME");
            if (GROOMER_ID != null && GROOMER_NAME != null)  {
                /* SHOW THE PROGRESS AND FETCH THE GROOMER'S SERVICE IMAGES */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchGroomerImages();
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
        gridGroomerImages.setHasFixedSize(true);
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
        gridGroomerImages.setLayoutManager(glm);

        /* SET THE CLINIC IMAGES ADAPTER */
        gridGroomerImages.setAdapter(new ImagesManagerAdapter(GroomerImageManager.this, arrImages));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}