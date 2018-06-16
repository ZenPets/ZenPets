package biz.zenpets.users.creator.adoption;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import biz.zenpets.users.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AdoptionCreatorNew extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.groupSpecies) RadioGroup groupSpecies;
    @BindView(R.id.spnBreeds) Spinner spnBreeds;
    @BindView(R.id.inputAdoptionName) TextInputLayout inputAdoptionName;
    @BindView(R.id.edtAdoptionName) TextInputEditText edtAdoptionName;
    @BindView(R.id.groupGender) RadioGroup groupGender;
    @BindView(R.id.inputDescription) TextInputLayout inputDescription;
    @BindView(R.id.edtDescription) TextInputEditText edtDescription;
    @BindView(R.id.imgvwAdoptionCover) ImageView imgvwAdoptionCover;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** THE OBJECTS TO HOLD THE ADOPTION DETAILS **/
    Uri ADOPTION_COVER_URI = null;

    /** SELECT THE ADOPTION COVER PHOTO **/
    @OnClick(R.id.imgvwAdoptionCover) void selectCover()    {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_creator_new);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }
}