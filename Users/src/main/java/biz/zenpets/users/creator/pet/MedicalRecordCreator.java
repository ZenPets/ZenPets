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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
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
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.adoptions.AdoptionsAlbumAdapter;
import biz.zenpets.users.utils.adapters.pet.records.RecordTypesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.pets.records.FetchRecordTypes;
import biz.zenpets.users.utils.helpers.pets.records.FetchRecordTypesInterface;
import biz.zenpets.users.utils.helpers.pets.records.PostMedicalImageInterface;
import biz.zenpets.users.utils.models.adoptions.AdoptionAlbumData;
import biz.zenpets.users.utils.models.pets.records.MedicalRecord;
import biz.zenpets.users.utils.models.pets.records.MedicalRecordsAPI;
import biz.zenpets.users.utils.models.pets.records.RecordType;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalRecordCreator extends AppCompatActivity
        implements FetchRecordTypesInterface, PostMedicalImageInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING PET ID **/
    private String PET_ID = null;

    /** THE LOGGED IN USER'S ID **/
    private String USER_ID = null;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** DATA TYPES TO HOLD THE RECORD DATA **/
    private String RECORD_TYPE_ID = null;
    private String RECORD_TYPE_NAME = null;
    private String RECORD_NOTES = null;
    private String RECORD_DATE = null;

    /** THE NEWLY CREATED MEDICAL RECORD ID **/
    private String MEDICAL_RECORD_ID = null;

    /** THE ARRAY LIST FOR THE RECORD IMAGES **/
    private final ArrayList<AdoptionAlbumData> arrAlbums = new ArrayList<>();

    /** THE ARRAY LIST FOR THE RECORD TYPES **/
    private ArrayList<RecordType> arrRecordTypes = new ArrayList<>();

    /** A PROGRESS DIALOG **/
    private ProgressDialog dialog;

    /** THE UPLOAD INCREMENT COUNTER **/
    private int IMAGE_UPLOAD_COUNTER = 0;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnRecordType) AppCompatSpinner spnRecordType;
    @BindView(R.id.inputRecordNotes) TextInputLayout inputRecordNotes;
    @BindView(R.id.edtRecordNotes) TextInputEditText edtRecordNotes;
    @BindView(R.id.txtRecordDate) AppCompatTextView txtRecordDate;
    @BindView(R.id.gridRecordImages) RecyclerView gridRecordImages;

    /** SELECT THE RECORD DATE **/
    @OnClick(R.id.btnRecordDate) void selectDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int date) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                /* FOR THE DATABASE ONLY !!!! */
                RECORD_DATE = sdf.format(cal.getTime());

                /* FOR DISPLAY ONLY !!!! */
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                String selectedDate = dateFormat.format(cal.getTime());
                txtRecordDate.setText(selectedDate);
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    /** ADD MEDICAL RECORD IMAGES **/
    @OnClick(R.id.txtAddImages) void addImages()    {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_record_creator);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN USER'S ID */
        USER_ID = getApp().getUserID();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* SET THE CURRENT DATE */
        setCurrentDate();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SELECT A RECORD TYPE */
        spnRecordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED RECORD TYPE ID AND NAME */
                RECORD_TYPE_ID = arrRecordTypes.get(position).getRecordTypeID();
                RECORD_TYPE_NAME = arrRecordTypes.get(position).getRecordTypeName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID"))   {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {
                /* FETCH THE LIST OF RECORD TYPES */
                new FetchRecordTypes(this).execute();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** VALIDATE THE RECORD DETAILS *****/
    private void validateRecordDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(edtRecordNotes.getWindowToken(), 0);

        /* GET THE RECORD NOTES */
        if (TextUtils.isEmpty(edtRecordNotes.getText().toString()))    {
            RECORD_NOTES = "Null";
        } else {
            RECORD_NOTES = edtRecordNotes.getText().toString();
        }

        if (arrAlbums.size() > 0)  {
            /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait while we publish the Medical Record...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
            Call<MedicalRecord> call = api.newMedicalRecord(
                    RECORD_TYPE_ID, USER_ID, PET_ID, RECORD_NOTES, RECORD_DATE
            );
            call.enqueue(new Callback<MedicalRecord>() {
                @Override
                public void onResponse(Call<MedicalRecord> call, Response<MedicalRecord> response) {
                    if (response.isSuccessful()) {
                        MEDICAL_RECORD_ID = response.body().getMedicalRecordID();
                        if (arrAlbums.size() > 0)   {
                            /* PUBLISH THE MEDICAL RECORD IMAGES */
                            publishRecordImages(MEDICAL_RECORD_ID);
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

                @Override
                public void onFailure(Call<MedicalRecord> call, Throwable t) {
//                    Log.e("ADD RECORD FAILURE", t.getMessage());
                    Crashlytics.logException(t);
                }
            });
        } else {
            new MaterialDialog.Builder(MedicalRecordCreator.this)
                    .icon(ContextCompat.getDrawable(MedicalRecordCreator.this, R.drawable.ic_info_outline_black_24dp))
                    .title(R.string.vaccination_creator_no_title)
                    .cancelable(false)
                    .content(getString(R.string.medical_creator_no_images))
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

    /***** PUBLISH THE MEDICAL RECORD IMAGES *****/
    private void publishRecordImages(final String medicalRecordID) {
        Bitmap bitmap = arrAlbums.get(IMAGE_UPLOAD_COUNTER).getBmpAdoptionImage();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ZenPets/Records");
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
            if (RECORD_TYPE_NAME != null) {
                FILE_NAME = RECORD_TYPE_NAME.replaceAll(
                        " ", "_").toLowerCase().trim() + "_" + PET_ID + "_"
                        + RECORD_DATE + "_" + fName;
            } else {
                FILE_NAME = "MEDICAL_" + PET_ID + "_" + RECORD_DATE + "_" + fName;
            }

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference refStorage = storageReference.child("Medical Records").child(FILE_NAME);
            UploadTask uploadTask = refStorage.putFile(uri);
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadURL = taskSnapshot.getDownloadUrl();
//                    if (downloadURL != null) {
//                        /* INCREMENT THE UPLOAD COUNTER AND UPLOAD THE IMAGE */
//                        IMAGE_UPLOAD_COUNTER++;
//                        new PostMedicalImage(MedicalRecordCreator.this)
//                                .execute(medicalRecordID, String.valueOf(downloadURL));
//                    }
//                }
//            });
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
                Toast.makeText(getApplicationContext(), "Successfully added medical record", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                /* PUBLISH THE NEXT IMAGE IN THE ARRAY */
                publishRecordImages(MEDICAL_RECORD_ID);
            }
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Medical Record";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MedicalRecordCreator.this);
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
                /* VALIDATE THE RECORD DETAILS */
                validateRecordDetails();
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

    /***** SET THE CURRENT DATE *****/
    private void setCurrentDate() {
        /* SET THE CURRENT DATE (DISPLAY ONLY !!!!) */
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        txtRecordDate.setText(formattedDate);

        /* SET THE CURRENT DATE (DATABASE ONLY !!!!) */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        RECORD_DATE = sdf.format(cal.getTime());
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        int intOrientation = getResources().getConfiguration().orientation;
        gridRecordImages.setHasFixedSize(true);
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
        gridRecordImages.setLayoutManager(glm);
        gridRecordImages.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        gridRecordImages.setAdapter(new AdoptionsAlbumAdapter(MedicalRecordCreator.this, arrAlbums));
    }

    @Override
    public void allRecordTypes(ArrayList<RecordType> data) {
        /* CAST THE CONTENTS IN THE GLOBAL INSTANCE */
        arrRecordTypes = data;

        /* SET THE ADAPTER TO THE RECORD TYPES SPINNER */
        spnRecordType.setAdapter(new RecordTypesAdapter(MedicalRecordCreator.this, arrRecordTypes));
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
            gridRecordImages.setAdapter(new AdoptionsAlbumAdapter(MedicalRecordCreator.this, arrAlbums));
            gridRecordImages.setVisibility(View.VISIBLE);
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
        if (ContextCompat.checkSelfPermission(MedicalRecordCreator.this,
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
                                        MedicalRecordCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            Matisse.from(MedicalRecordCreator.this)
                    .choose(MimeType.allOf())
                    .theme(R.style.Matisse_Zhihu)
                    .countable(true)
                    .maxSelectable(10)
                    .imageEngine(new PicassoEngine())
                    .forResult(101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                Matisse.from(MedicalRecordCreator.this)
                        .choose(MimeType.allOf())
                        .theme(R.style.Matisse_Zhihu)
                        .countable(true)
                        .maxSelectable(10)
                        .imageEngine(new PicassoEngine())
                        .forResult(101);
            } else {
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title("Permission Denied...")
                        .cancelable(false)
                        .content("To add images to a Medical Record, Zen Pets requires this permission." +
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