package biz.zenpets.trainers.details.modules;

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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.TypefaceSpan;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImage;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImagesAPI;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImages;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingImageManager extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** GET THE LOGGED IN TRAINER'S ID **/
    String TRAINER_ID = null;
    
    /** THE INCOMING TRAINING MODULE ID **/
    String MODULE_ID = null;

    /** THE MODULE IMAGES ADAPTER AND ARRAY LIST **/
    private TrainingImagesManagerAdapter imagesAdapter;
    private ArrayList<ModuleImage> arrImages = new ArrayList<>();

    /** THE MODULE IMAGE CONFIRMATION CUSTOM VIEW **/
    private View moduleConfirmation;

    /** THE IMAGE VIEW INSTANCE IN THE CUSTOM DIALOG **/
    private AppCompatImageView imgvwModuleImage;

    /* THE MODULE IMAGE URI AND URL (FOR UPLOADING THE SELECTED IMAGE) */
    private Uri MODULE_IMAGE_URI = null;
    private String MODULE_IMAGE_URL = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.gridModuleImages) RecyclerView gridModuleImages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD AN IMAGE TO THE CLINIC **/
    @OnClick(R.id.linlaEmpty) protected void addImage() {
        /* SHOW THE IMAGE SELECTOR SHEET */
        showImageSheet();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_images_manager_list);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN TRAINER'S ID */
        TRAINER_ID = getApp().getTrainerID();
//        Log.e("TRAINER ID", TRAINER_ID);

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

        /* INSTANTIATE THE MODULE IMAGES ADAPTER */
        imagesAdapter = new TrainingImagesManagerAdapter(arrImages);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* INFLATE THE CUSTOM VIEWS */
        moduleConfirmation = LayoutInflater.from(getApplicationContext()).inflate(R.layout.training_images_manager_confirmation, null);
    }

    /***** FETCH THE TRAINING MODULE IMAGES *****/
    private void fetchModuleImages() {
        ModuleImagesAPI apiImages = ZenApiClient.getClient().create(ModuleImagesAPI.class);
        Call<ModuleImages> callImages = apiImages.fetchTrainingModuleImages(MODULE_ID);
        callImages.enqueue(new Callback<ModuleImages>() {
            @Override
            public void onResponse(Call<ModuleImages> call, Response<ModuleImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0)   {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        gridModuleImages.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* INSTANTIATE THE ADAPTER */
                        imagesAdapter = new TrainingImagesManagerAdapter(arrImages);

                        /* SET THE SERVICES ADAPTER TO THE RECYCLER VIEW */
                        gridModuleImages.setAdapter(imagesAdapter);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        gridModuleImages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    gridModuleImages.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS BAR AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ModuleImages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("MODULE_ID"))    {
            MODULE_ID = bundle.getString("MODULE_ID");
            if (MODULE_ID != null)  {
                /* FETCH THE TRAINING MODULE IMAGES */
                fetchModuleImages();
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
        gridModuleImages.setHasFixedSize(true);
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
        gridModuleImages.setLayoutManager(glm);

        /* SET THE CLINIC IMAGES ADAPTER */
        gridModuleImages.setAdapter(imagesAdapter);
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
        MenuInflater inflater = new MenuInflater(TrainingImageManager.this);
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
        final BottomSheetDialog sheetDialog = new BottomSheetDialog(TrainingImageManager.this);
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
                EasyImage.openGallery(TrainingImageManager.this, 0);
            }
        });

        /* SELECT A CAMERA IMAGE */
        linlaCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                EasyImage.openCamera(TrainingImageManager.this, 0);
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
                MODULE_IMAGE_URI = Uri.fromFile(file);
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
        MaterialDialog dialogConfirmation = new MaterialDialog.Builder(TrainingImageManager.this)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Regular.ttf", "Roboto-Regular.ttf")
                .cancelable(false)
                .customView(moduleConfirmation, false)
                .positiveText("Upload")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)    {
                        /* SHOW THE DIALOG TO GET THE IMAGE CAPTION */
                        showCaptionDialog();
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
        imgvwModuleImage = dialogConfirmation.getCustomView().findViewById(R.id.imgvwModuleImage);

        /* SET THE BITMAP TO THE IMAGE VIEW AND SET THE SCALE TYPE */
        imgvwModuleImage.setImageBitmap(bitmap);
        imgvwModuleImage.setScaleType(AppCompatImageView.ScaleType.CENTER_CROP);

        /* SHOW THE DIALOG */
        dialogConfirmation.show();
    }

    /***** SHOW THE DIALOG TO GET THE IMAGE CAPTION *****/
    private void showCaptionDialog() {
        new MaterialDialog.Builder(TrainingImageManager.this)
                .title("Add A  Caption?")
                .content("Add a caption to the selected image for Pet Parents to better understand what the image describes. \n\nExample 1: Sit\nExample 2: Stay\nExample 3: Walk")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(0, 200)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .positiveText("Upload")
                .negativeText("Cancel")
                .input("Add a caption....", null, true, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, final CharSequence input) {
                        /* SHOW THE PROGRESS DIALOG */
                        progressDialog = new ProgressDialog(TrainingImageManager.this);
                        progressDialog.setMessage("Please wait while we publish the new Training Module image...");
                        progressDialog.setIndeterminate(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        /* GET THE TIME STAMP */
                        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                        String FILE_NAME = "TRAINER_" + TRAINER_ID + "_" + "MODULE_" + MODULE_ID + "_" + timestamp;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        final StorageReference refStorage = storageReference.child("Training Modules").child(FILE_NAME);
                        refStorage.putFile(MODULE_IMAGE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadURL = taskSnapshot.getDownloadUrl();
                                MODULE_IMAGE_URL = String.valueOf(downloadURL);
                                if (MODULE_IMAGE_URL != null)    {
                                    ModuleImagesAPI api = ZenApiClient.getClient().create(ModuleImagesAPI.class);
                                    Call<ModuleImage> call = api.newTrainingModuleImage(MODULE_ID, MODULE_IMAGE_URL, input.toString());
                                    call.enqueue(new Callback<ModuleImage>() {
                                        @Override
                                        public void onResponse(Call<ModuleImage> call, Response<ModuleImage> response) {
                                            if (response.isSuccessful())    {
                                                /* DISMISS THE DIALOG */
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "The new Clinic image was successfully uploaded.", Toast.LENGTH_LONG).show();

                                                /* CLEAR THE ARRAY */
                                                if (arrImages != null)
                                                    arrImages.clear();

                                                /* FETCH THE TRAINING MODULE IMAGES (AGAIN) */
                                                fetchModuleImages();
                                            } else {
                                                /* DISMISS THE DIALOG */
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "There was an error posting this image. Please try again...", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ModuleImage> call, Throwable t) {
//                                            Log.e("IMAGE FAILURE", t.getMessage());
                                            Crashlytics.logException(t);
                                        }
                                    });
//                                    new AddClinicImage(TrainingImageManager.this).execute(CLINIC_ID, CLINIC_IMAGE_URL);
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "There was an error posting this image. Please try again...",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                Log.e("UPLOAD EXCEPTION", e.toString());
                                Crashlytics.logException(e);
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
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
    
    /***** THE IMAGE MANAGER ADAPTER *****/
    private class TrainingImagesManagerAdapter extends RecyclerView.Adapter<TrainingImagesManagerAdapter.ImagesVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<ModuleImage> arrAdapterImages;

        TrainingImagesManagerAdapter(ArrayList<ModuleImage> arrAdapterImages) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrAdapterImages = arrAdapterImages;
        }

        @Override
        public int getItemCount() {
            return arrAdapterImages.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final TrainingImagesManagerAdapter.ImagesVH holder, int position) {
            final ModuleImage data = arrAdapterImages.get(position);

            /* SET THE TRAINING MODULE IMAGE **/
            if (data.getTrainerModuleImageURL() != null) {
                Uri uri = Uri.parse(data.getTrainerModuleImageURL());
                holder.imgvwModuleImage.setImageURI(uri);
            }

            /* SET THE IMAGE CAPTION */
            if (data.getTrainerModuleImageCaption() != null && !data.getTrainerModuleImageCaption().equalsIgnoreCase("null"))   {
                holder.txtModuleImageCaption.setText(data.getTrainerModuleImageCaption());
                holder.txtModuleImageCaption.setVisibility(View.VISIBLE);
            } else {
                holder.txtModuleImageCaption.setVisibility(View.GONE);
            }

            /* DELETE THE SELECTED IMAGE */
            holder.imgvwDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* SHOW THE DELETE DIALOG */
                    showDeleteDialog(data.getTrainerModuleImageID(), data.getTrainerModuleImageURL());
                }
            });
        }

        /***** SHOW THE DELETE DIALOG *****/
        private void showDeleteDialog(final String trainerModuleImageID, final String trainerModuleImageURL) {
            new MaterialDialog.Builder(TrainingImageManager.this)
                    .icon(ContextCompat.getDrawable(TrainingImageManager.this, R.drawable.ic_info_outline_black_24dp))
                    .title("Delete Image?")
                    .cancelable(false)
                    .content("Are you sure you want to delete this Image from your Training Module?")
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            /* SHOW THE PROGRESS DIALOG */
                            progressDialog = new ProgressDialog(TrainingImageManager.this);
                            progressDialog.setMessage("Please wait while we publish the new Training Module image...");
                            progressDialog.setIndeterminate(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(trainerModuleImageURL);
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ModuleImagesAPI api = ZenApiClient.getClient().create(ModuleImagesAPI.class);
                                    Call<ModuleImage> call = api.deleteTrainingModuleImage(trainerModuleImageID);
                                    call.enqueue(new Callback<ModuleImage>() {
                                        @Override
                                        public void onResponse(Call<ModuleImage> call, Response<ModuleImage> response) {
                                            if (response.isSuccessful())    {
                                                /* DISMISS THE PROGRESS DIALOG  */
                                                progressDialog.dismiss();

                                                /* SHOW THE SUCCESS MESSAGE */
                                                Toast.makeText(TrainingImageManager.this, "Module Image deleted...", Toast.LENGTH_SHORT).show();

                                                /* CLEAR THE ARRAY LIST */
                                                arrImages.clear();

                                                /* SHOW THE PROGRESS AND FETCH THE TRAINING MODULE IMAGES */
                                                linlaProgress.setVisibility(View.VISIBLE);
                                                fetchModuleImages();
                                            } else {
                                                /* DISMISS THE PROGRESS DIALOG  */
                                                progressDialog.dismiss();

                                                Toast.makeText(TrainingImageManager.this, "Failed to delete image...", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ModuleImage> call, Throwable t) {
                                            /* DISMISS THE PROGRESS DIALOG  */
                                            progressDialog.dismiss();

                                            Log.e("DELETE FAILURE", t.getMessage());
                                            Crashlytics.logException(t);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TrainingImageManager.this, "Failed to delete image...", Toast.LENGTH_SHORT).show();
                                    Crashlytics.logException(e);
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        @NonNull
        @Override
        public TrainingImagesManagerAdapter.ImagesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.training_images_manager_item, parent, false);

            return new TrainingImagesManagerAdapter.ImagesVH(itemView);
        }

        class ImagesVH extends RecyclerView.ViewHolder   {
            final SimpleDraweeView imgvwModuleImage;
            AppCompatTextView txtModuleImageCaption;
            IconicsImageView imgvwDeleteImage;

            ImagesVH(View v) {
                super(v);
                imgvwModuleImage = v.findViewById(R.id.imgvwModuleImage);
                txtModuleImageCaption = v.findViewById(R.id.txtModuleImageCaption);
                imgvwDeleteImage = v.findViewById(R.id.imgvwDeleteImage);
            }
        }
    }

}