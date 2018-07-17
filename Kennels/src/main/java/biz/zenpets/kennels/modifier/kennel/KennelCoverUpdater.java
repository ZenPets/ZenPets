package biz.zenpets.kennels.modifier.kennel;

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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.TypefaceSpan;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import biz.zenpets.kennels.utils.models.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelCoverUpdater extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER ID **/
    String KENNEL_OWNER_ID = null;

    /** THE INCOMING KENNEL ID AND NAME **/
    private String KENNEL_ID = null;
    private String KENNEL_NAME = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 200;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE STRINGS TO HOLD THE CLINIC LOGO INFORMATION **/
    private String FILE_NAME = null;
    private Uri LOGO_URI = null;
    private String LOGO_URL = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwKennelCoverPhoto) AppCompatImageView imgvwKennelCoverPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_cover_updater);
        ButterKnife.bind(this);

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(KennelCoverUpdater.this,
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
                                        KennelCoverUpdater.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelCoverUpdater.this);
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
                    EasyImage.openGallery(KennelCoverUpdater.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(KennelCoverUpdater.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelCoverUpdater.this);
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
                        EasyImage.openGallery(KennelCoverUpdater.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(KennelCoverUpdater.this, 0);
                    }
                });
            } else {
            }
        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("KENNEL_ID")
                && bundle.containsKey("KENNEL_NAME")) {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_NAME = bundle.getString("KENNEL_NAME");
            if (KENNEL_ID == null && KENNEL_NAME == null)   {
                Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
            finish();
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
            imgvwKennelCoverPhoto.setImageBitmap(bitmap);
            imgvwKennelCoverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                LOGO_URI = Uri.fromFile(file);
//                Log.e("URI", String.valueOf(LOGO_URI));

                /* GENERATE THE FILE NAME */
                if (LOGO_URI != null && KENNEL_NAME != null)    {
                    FILE_NAME = KENNEL_OWNER_ID + "_" + KENNEL_NAME.replaceAll(" ", "_").toLowerCase().trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Update Kennel Cover";
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
        MenuInflater inflater = new MenuInflater(KennelCoverUpdater.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuSave:
                /* UPLOAD THE KENNEL COVER PHOTO */
                uploadKennelCover();
                break;
            case R.id.menuCancel:
                Intent cancelled = new Intent();
                setResult(RESULT_CANCELED, cancelled);
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** UPLOAD THE KENNEL COVER PHOTO **/
    private void uploadKennelCover() {
        /* SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Kennel's Cover Photo...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference refStorage = storageReference.child("Kennel Covers").child(FILE_NAME);
        refStorage.putFile(LOGO_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
                LOGO_URL = String.valueOf(downloadURL);
                if (LOGO_URL != null)    {
                    /* UPDATE THE KENNEL'S COVER PHOTO*/
                    KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
                    Call<Kennel> call = api.updateKennelCoverPhoto(KENNEL_ID, LOGO_URL);
                    call.enqueue(new Callback<Kennel>() {
                        @Override
                        public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                            if (response.isSuccessful())    {
                                /* DISMISS THE DIALOG AND FINISH THE ACTIVITY */
                                dialog.dismiss();
                                Intent success = new Intent();
                                setResult(RESULT_OK, success);
                                Toast.makeText(getApplicationContext(), "Cover photo updated...", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Failed to update Cover photo...",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Kennel> call, Throwable t) {
//                            Log.e("LOGO FAILURE", t.getMessage());
                            Crashlytics.logException(t);
                        }
                    });
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to update Cover photo...",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.e("UPLOAD EXCEPTION", e.toString());
                Crashlytics.logException(e);
            }
        });
    }
}