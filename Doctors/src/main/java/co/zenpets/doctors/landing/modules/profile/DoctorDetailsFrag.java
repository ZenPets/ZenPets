package co.zenpets.doctors.landing.modules.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.modifier.profile.ProfileModifier;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
public class DoctorDetailsFrag extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID AND AUTH ID **/
    private String DOCTOR_ID = null;
    private String AUTH_ID = null;

    /** STRINGS TO HOLD THE DOCTOR'S DATA **/
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_EMAIL = null;
    private String DOCTOR_PHONE_PREFIX = null;
    private String DOCTOR_PHONE_NUMBER = null;
    private String DOCTOR_ADDRESS = null;
    private String COUNTRY_NAME = null;
    private String STATE_NAME = null;
    private String CITY_NAME = null;
    private String DOCTOR_GENDER = null;
    private String DOCTOR_SUMMARY = null;
    private String DOCTOR_EXPERIENCE = null;
    private String DOCTOR_CHARGES = null;
    private String CURRENCY_SYMBOL = null;
    private String DOCTOR_DISPLAY_PROFILE = null;
    private Uri DOCTOR_DISPLAY_PROFILE_URI = null;
    private String DOCTOR_DISPLAY_PROFILE_FILE_NAME = null;

    /** PERMISSION REQUEST CONSTANTS **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwUserProfile) SimpleDraweeView imgvwUserProfile;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.txtInternalID) AppCompatTextView txtInternalID;
    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
    @BindView(R.id.txtPhoneNumber) AppCompatTextView txtPhoneNumber;
    @BindView(R.id.txtAddress) AppCompatTextView txtAddress;
    @BindView(R.id.txtGender) AppCompatTextView txtGender;
    @BindView(R.id.txtSummary) AppCompatTextView txtSummary;
    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;

    /** EDIT THE DOCTOR'S PROFILE **/
    @OnClick(R.id.fabEditProfile) void editProfile()    {
        Intent intent = new Intent(getActivity(), ProfileModifier.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        startActivityForResult(intent, 101);
    }

    /** EDIT THE DOCTOR'S DISPLAY PROFILE **/
    @OnClick(R.id.linlaProfileEdit) void editDisplayProfile()   {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    /** SHOW THE INTERNAL ID INFO **/
    @OnClick(R.id.imgvwIDInfo) protected void showIDInfo()  {
        showIDDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.doctor_details_frag, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE */
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU */
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES */
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(getActivity())
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* GET THE DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* GET DOCTOR'S AUTH ID */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AUTH_ID = user.getUid();
//            Log.e("AUTH ID", AUTH_ID);
        }

        /* SHOW THE PROGRESS AND START FETCHING THE DOCTOR'S PROFILE */
        if (DOCTOR_ID != null)  {
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorProfile();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /***** FETCH THE DOCTOR'S PROFILE *****/
    private void fetchDoctorProfile() {
        DoctorProfileAPI apiInterface = ZenApiClient.getClient().create(DoctorProfileAPI.class);
        Call<DoctorProfileData> call = apiInterface.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(Call<DoctorProfileData> call, Response<DoctorProfileData> response) {
                /* SET THE INTERNAL ID */
                txtInternalID.setText(getString(R.string.doctor_profile_id_placeholder, DOCTOR_ID));

                DoctorProfileData data = response.body();
                if (data != null)   {
                    /* SET THE DOCTOR'S PREFIX AND NAME */
                    DOCTOR_PREFIX = data.getDoctorPrefix();
                    DOCTOR_NAME = data.getDoctorName();
                    txtUserName.setText(getString(R.string.doctor_profile_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));

                    /* SET THE DOCTOR'S DISPLAY PROFILE */
                    DOCTOR_DISPLAY_PROFILE = data.getDoctorDisplayProfile();
                    Uri uri = Uri.parse(DOCTOR_DISPLAY_PROFILE);
                    imgvwUserProfile.setImageURI(uri);

                    /* SET THE DOCTOR'S EMAIL ADDRESS  */
                    DOCTOR_EMAIL = data.getDoctorEmail();
                    txtEmailAddress.setText(DOCTOR_EMAIL);

                    /* SET THE DOCTOR'S PHONE NUMBER */
                    DOCTOR_PHONE_PREFIX = data.getDoctorPhonePrefix();
                    DOCTOR_PHONE_NUMBER = data.getDoctorPhoneNumber();
                    txtPhoneNumber.setText(getString(R.string.doctor_profile_phone_placeholder, DOCTOR_PHONE_PREFIX, DOCTOR_PHONE_NUMBER));

                    /* SET THE DOCTOR'S ADDRESS */
                    DOCTOR_ADDRESS = data.getDoctorAddress();
                    CITY_NAME = data.getCityName();
                    STATE_NAME = data.getStateName();
                    COUNTRY_NAME = data.getCountryName();
                    txtAddress.setText(getString(R.string.doctor_profile_address_placeholder, DOCTOR_ADDRESS, CITY_NAME, STATE_NAME, COUNTRY_NAME));

                    /* SET THE DOCTOR'S GENDER */
                    DOCTOR_GENDER = data.getDoctorGender();
                    txtGender.setText(DOCTOR_GENDER);

                    /* SET THE DOCTOR'S SUMMARY */
                    DOCTOR_SUMMARY = data.getDoctorSummary();
                    txtSummary.setText(DOCTOR_SUMMARY);

                    /* SET THE DOCTOR'S EXPERIENCE */
                    DOCTOR_EXPERIENCE = data.getDoctorExperience();
                    txtExperience.setText(getString(R.string.doctor_profile_experience_placeholder, DOCTOR_EXPERIENCE));

                    /* SET THE DOCTOR'S CHARGES */
                    CURRENCY_SYMBOL = data.getCurrencySymbol();
                    DOCTOR_CHARGES = data.getDoctorCharges();
                    txtDoctorCharges.setText(getString(R.string.doctor_profile_charges_placeholder, CURRENCY_SYMBOL, DOCTOR_CHARGES));

                    /* HIDE THE PROGRESS AFTER FETCHING THE DOCTOR'S PROFILE */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<DoctorProfileData> call, Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                /* INSTANTIATE THE PROGRESS DIALOG */
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Updating display profile...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(false);
                dialog.show();

                onPhotoReturned(imageFiles);
            }
        });

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            /* SHOW THE PROGRESS AND START FETCHING THE DOCTOR'S PROFILE */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorProfile();
        }
    }

    /***** SHOW THE INTERNAL ID INFO *****/
    private void showIDDialog() {
        new MaterialDialog.Builder(getActivity())
                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                .title("Internal ID")
                .cancelable(true)
                .content("The Internal ID is a system generated unique identifier for your account. \n\nIf you need to contact us for assistance, you will quote either this unique ID or your email address.")
                .positiveText("Dismiss")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(getActivity())
                        .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
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
                                        getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
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
                    EasyImage.openGallery(DoctorDetailsFrag.this, 0);
//                    EasyImage.openGallery(getActivity(), 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(DoctorDetailsFrag.this, 0);
//                    EasyImage.openCamera(getActivity(), 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(getActivity());
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
                        EasyImage.openGallery(getActivity(), 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(getActivity(), 0);
                    }
                });
            }
        }
    }

    /***** PROCESS THE SELECTED IMAGE AND GRAB THE URI *****/
    private void onPhotoReturned(List<File> imageFiles) {
        try {
            File compressedFile = new Compressor(getActivity())
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(imageFiles.get(0));
            Uri uri = Uri.fromFile(compressedFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            imgvwUserProfile.setImageBitmap(bitmap);
            imgvwUserProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);

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
                DOCTOR_DISPLAY_PROFILE_URI = Uri.fromFile(file);
//                Log.e("URI", String.valueOf(DOCTOR_DISPLAY_PROFILE_URI));

                /* DELETE THE CURRENT FILE */
                deleteProfileFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** DELETE THE CURRENT FILE *****/
    private void deleteProfileFile() {
        FirebaseStorage reference = FirebaseStorage.getInstance();
        StorageReference refFile = reference.getReferenceFromUrl(DOCTOR_DISPLAY_PROFILE);
        refFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                /* UPLOAD THE NEW DISPLAY PROFILE */
                uploadNewProfile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Update failed...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /***** UPLOAD THE NEW DISPLAY PROFILE *****/
    private void uploadNewProfile() {
//        DOCTOR_DISPLAY_PROFILE_FILE_NAME =
//                DOCTOR_NAME.replaceAll(" ", "_").toLowerCase().trim() + "_" + AUTH_ID;
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
//        StorageReference refStorage = storageReference.child("Doctor Profiles").child(DOCTOR_DISPLAY_PROFILE_FILE_NAME);
//        refStorage.putFile(DOCTOR_DISPLAY_PROFILE_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                DoctorProfileAPI api = ZenApiClient.getClient().create(DoctorProfileAPI.class);
//                Call<AccountData> call = api.updateDoctorDisplayProfile(DOCTOR_ID, String.valueOf(downloadURL));
//                call.enqueue(new Callback<AccountData>() {
//                    @Override
//                    public void onResponse(Call<AccountData> call, Response<AccountData> response) {
//                        if (response.isSuccessful())    {
//                            dialog.dismiss();
//                            Toast.makeText(getActivity(), "Profile updated successfully...", Toast.LENGTH_SHORT).show();
//                        } else {
//                            dialog.dismiss();
//                            Toast.makeText(getActivity(), "Update failed...", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<AccountData> call, Throwable t) {
//                        dialog.dismiss();
////                        Crashlytics.logException(t);
////                        Log.e("DISPLAY FAILURE", t.getMessage());
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                dialog.dismiss();
////                Log.e("UPLOAD EXCEPTION", e.toString());
////                Crashlytics.logException(e);
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(getActivity());
    }
}