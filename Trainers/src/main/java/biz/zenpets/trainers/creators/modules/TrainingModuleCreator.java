package biz.zenpets.trainers.creators.modules;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
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
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Arrays;
import java.util.List;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.TypefaceSpan;
import biz.zenpets.trainers.utils.adapters.modules.TrainingDurationNumberAdapter;
import biz.zenpets.trainers.utils.adapters.modules.TrainingDurationUnitAdapter;
import biz.zenpets.trainers.utils.adapters.modules.TrainingModuleAlbumAdapter;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.modules.Module;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleAlbumData;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImage;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImagesAPI;
import biz.zenpets.trainers.utils.models.trainers.modules.ModulesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingModuleCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN TRAINER'S ID **/
    String TRAINER_ID = null;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** STRINGS TO HOLD THE TRAINING MODULE INFORMATION **/
    String MODULE_NAME = null;
    String MODULE_DURATION_NUMBER = null;
    String MODULE_DURATION_UNIT = null;
    String MODULE_DURATION = null;
    String MODULE_SESSIONS = null;
    String MODULE_DETAILS = null;
    String MODULE_FORMAT = "Individual";
    String MODULE_GROUP_SIZE = null;
    String MODULE_FEES = null;

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE ARRAY LISTS FOR THE TRAINING IMAGES **/
    private final ArrayList<ModuleAlbumData> arrImages = new ArrayList<>();

    /** THE UPLOAD INCREMENT COUNTER **/
    private int IMAGE_UPLOAD_COUNTER = 0;
    
    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputModuleName) TextInputLayout inputModuleName;
    @BindView(R.id.edtModuleName) TextInputEditText edtModuleName;
    @BindView(R.id.spnDurationNumber) AppCompatSpinner spnDurationNumber;
    @BindView(R.id.spnDurationUnit) AppCompatSpinner spnDurationUnit;
    @BindView(R.id.inputSessions) TextInputLayout inputSessions;
    @BindView(R.id.edtSessions) TextInputEditText edtSessions;
    @BindView(R.id.inputDetails) TextInputLayout inputDetails;
    @BindView(R.id.edtDetails) TextInputEditText edtDetails;
    @BindView(R.id.rgFormat) RadioGroup rgFormat;
    @BindView(R.id.inputGroup) TextInputLayout inputGroup;
    @BindView(R.id.edtGroup) TextInputEditText edtGroup;
    @BindView(R.id.inputFees) TextInputLayout inputFees;
    @BindView(R.id.edtFees) TextInputEditText edtFees;
    @BindView(R.id.gridModuleImages) RecyclerView gridModuleImages;

    /** ADD ADOPTION IMAGES **/
    @OnClick(R.id.txtAddImages) void imageSelectImages()  {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_modules_creator);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN TRAINER'S ID */
        TRAINER_ID = getApp().getTrainerID();
//        Log.e("TRAINER ID", TRAINER_ID);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* POPULATE THE DURATION NUMBER SPINNER */
        String[] strDurationNumber = getResources().getStringArray(R.array.duration_number);
        final List<String> arrDurationNumber;
        arrDurationNumber = Arrays.asList(strDurationNumber);
        spnDurationNumber.setAdapter(new TrainingDurationNumberAdapter(
                TrainingModuleCreator.this,
                R.layout.training_module_sessions_row,
                arrDurationNumber));

        /* SELECT THE DURATION NUMBER */
        spnDurationNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MODULE_DURATION_NUMBER = arrDurationNumber.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE DURATION UNITS SPINNER */
        String[] strDurationUnits = getResources().getStringArray(R.array.duration_units);
        final List<String> arrDurationUnits;
        arrDurationUnits = Arrays.asList(strDurationUnits);
        spnDurationUnit.setAdapter(new TrainingDurationUnitAdapter(
                TrainingModuleCreator.this,
                R.layout.training_module_sessions_row,
                arrDurationUnits));

        /* SELECT THE DURATION UNIT */
        spnDurationUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MODULE_DURATION_UNIT = arrDurationUnits.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* SELECT THE TRAINING MODULE FORMAT (INDIVIDUAL TRAINING / GROUP TRAINING) */
        rgFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtnIndividual:
                        /* SET THE FORMAT TO INDIVIDUAL */
                        MODULE_FORMAT = "Individual";

                        /* HIDE THE GROUP SIZE INPUT */
                        inputGroup.setVisibility(View.GONE);
                        break;
                    case R.id.rdbtnGroup:
                        /* SET THE FORMAT TO GROUP */
                        MODULE_FORMAT = "Group";

                        /* SHOW THE GROUP SIZE INPUT */
                        inputGroup.setVisibility(View.VISIBLE);
                        inputGroup.requestFocus();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /***** CHECK FOR ALL MODULE DETAILS *****/
    private void checkModuleDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtModuleName.getWindowToken(), 0);
        }

        /* GET THE REQUIRED INFO */
        MODULE_NAME = edtModuleName.getText().toString().trim();
        MODULE_DURATION = MODULE_DURATION_NUMBER + " " + MODULE_DURATION_UNIT;
        MODULE_SESSIONS = edtSessions.getText().toString().trim();
        MODULE_DETAILS = edtDetails.getText().toString().trim();
        if (MODULE_FORMAT.equalsIgnoreCase("Group"))    {
            MODULE_GROUP_SIZE = edtGroup.getText().toString().trim();
        }
        MODULE_FEES = edtFees.getText().toString().trim();

        /* VALIDATE THE DATA **/
        if (TextUtils.isEmpty(MODULE_NAME)) {
            inputModuleName.setError("Enter the Training Module's name");
            inputModuleName.requestFocus();
            inputModuleName.setErrorEnabled(true);
            inputSessions.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(MODULE_SESSIONS))  {
            inputSessions.setError("Enter the total number of sessions");
            inputSessions.requestFocus();
            inputSessions.setErrorEnabled(true);
            inputModuleName.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(MODULE_DETAILS))   {
            inputDetails.setError("Provide the Training Module's details");
            inputDetails.requestFocus();
            inputDetails.setErrorEnabled(true);
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (MODULE_FORMAT.equalsIgnoreCase("Group") && TextUtils.isEmpty(MODULE_GROUP_SIZE)) {
            inputGroup.setError("Provide size of the training group");
            inputGroup.requestFocus();
            inputGroup.setErrorEnabled(true);
            inputDetails.setErrorEnabled(false);
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(MODULE_FEES))  {
            inputFees.setError("Provide the training fee");
            inputFees.requestFocus();
            inputFees.setErrorEnabled(true);
            inputGroup.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
        } else {
            /* DISABLE ALL ERRORS */
            inputModuleName.setErrorEnabled(false);
            inputSessions.setErrorEnabled(false);
            inputDetails.setErrorEnabled(false);
            inputGroup.setErrorEnabled(false);
            inputFees.setErrorEnabled(false);

            /* SHOW THE "VERIFY DETAILS" DIALOG */
            verifyDetails();
        }
    }

    /***** SHOW THE "VERIFY DETAILS" DIALOG *****/
    private void verifyDetails() {
        /* GET THE NUMBER OF SESSIONS */
        String strSessions;
        String strFinalSessions = null;
        strSessions = MODULE_SESSIONS;
        int totalSessions = Integer.parseInt(strSessions);
        Resources resSessions = getResources();
        if (totalSessions == 1)    {
            strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
        } else if (totalSessions > 1) {
            strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
        }

        /* GET THE SINGULAR OR PLURAL FOR DAYS / MONTHS */
        String strUnits = null;
        if (MODULE_DURATION_UNIT.equalsIgnoreCase("day/s")) {
            int totalDays = Integer.parseInt(MODULE_DURATION_NUMBER);
            Resources resDays = getResources();
            if (totalDays == 1)    {
                strUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
            } else if (totalDays > 1) {
                strUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
            }
        } else {
            int totalDays = Integer.parseInt(MODULE_DURATION_NUMBER);
            Resources resDays = getResources();
            if (totalDays == 1)    {
                strUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
            } else if (totalDays > 1) {
                strUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
            }
        }

        /* CHECK IF THE MODULE FORMAT IS "INDIVIDUAL" OR "GROUP" */
        String strFinalContent = null;
        if (MODULE_FORMAT.equalsIgnoreCase("Individual"))   {
            strFinalContent = getString(R.string.tm_creator_verify_content_individually, MODULE_NAME, strUnits, strFinalSessions, MODULE_FEES);
        } else if (MODULE_FORMAT.equalsIgnoreCase("Group")){
            strFinalContent = getString(R.string.tm_creator_verify_content_group, MODULE_NAME, strUnits, strFinalSessions, MODULE_GROUP_SIZE, MODULE_FEES);
        }

        /* SHOW THE FINAL CONFIRMATION SCREEN DEPENDING ON THE CHOICES MADE */
        new MaterialDialog.Builder(TrainingModuleCreator.this)
                .icon(ContextCompat.getDrawable(TrainingModuleCreator.this, R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.tm_creator_verify_title))
                .cancelable(true)
                .content(strFinalContent)
                .positiveText(getString(R.string.tm_creator_verify_okay))
                .negativeText(getString(R.string.tm_creator_verify_cancel))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        /* PUBLISH THE NEW TRAINING MODULE */
                        publishTrainingModule();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /***** PUBLISH THE NEW TRAINING MODULE *****/
    private void publishTrainingModule() {
        /* INSTANTIATE THE DIALOG INSTANCE */
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we publish your training module..");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* GET THE SINGULAR OR PLURAL FOR DAYS / MONTHS */
        String strUnits;
        if (MODULE_DURATION_UNIT.equalsIgnoreCase("day/s")) {
            strUnits = "Day";
        } else {
            strUnits = "Month";
        }

        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Module> call = api.newTrainerModule(
                TRAINER_ID, MODULE_NAME, MODULE_DURATION_NUMBER, strUnits, MODULE_SESSIONS,
                MODULE_DETAILS, MODULE_FORMAT, MODULE_GROUP_SIZE, MODULE_FEES);
        call.enqueue(new Callback<Module>() {

            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                if (response.isSuccessful())    {
                    String trainerModuleID = response.body().getTrainerModuleID();

                    if (arrImages.size() > 0)   {
                        /* PUBLISH THE TRAINING MODULE IMAGES */
                        publishModuleImages(trainerModuleID);
                    }
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error publishing your Training Module. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** PUBLISH THE TRAINING MODULE IMAGES *****/
    private void publishModuleImages(final String trainerModuleID) {
        Bitmap bitmap = arrImages.get(IMAGE_UPLOAD_COUNTER).getBmpModuleImage();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ZenPets/Modules");
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
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String FILE_NAME = "TRAINER_" + TRAINER_ID + "_" + "MODULE_" + trainerModuleID + "_" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference refStorage = storageReference.child("Training Modules").child(FILE_NAME);
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
                            postImage(trainerModuleID, String.valueOf(downloadURL));
                        }
                    }
                }
            });
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadURL = taskSnapshot.getDownloadUrl();
//                    if (downloadURL != null) {
//                        /* INCREMENT THE UPLOAD COUNTER AND UPLOAD THE IMAGE */
//                        IMAGE_UPLOAD_COUNTER++;
//                        postImage(trainerModuleID, String.valueOf(downloadURL));
//                    }
//                }
//            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** POST / PUBLISH THE ADOPTION IMAGE *****/
    private void postImage(final String trainerModuleID, String imageURL)    {
        ModuleImagesAPI api = ZenApiClient.getClient().create(ModuleImagesAPI.class);
        Call<ModuleImage> call = api.newTrainingModuleImage(
                trainerModuleID, imageURL, null);
        call.enqueue(new Callback<ModuleImage>() {
            @Override
            public void onResponse(Call<ModuleImage> call, Response<ModuleImage> response) {
                if (response.isSuccessful())    {
                    if (IMAGE_UPLOAD_COUNTER == arrImages.size()) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "New Adoption listed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        /* PUBLISH THE NEXT IMAGE IN THE ARRAY */
                        publishModuleImages(trainerModuleID);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModuleImage> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Training Module";
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
        MenuInflater inflater = new MenuInflater(TrainingModuleCreator.this);
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
                /* CHECK FOR ALL MODULE DETAILS */
                checkModuleDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            /* CLEAR THE ARRAY LIST */
            arrImages.clear();

            ModuleAlbumData albums;
            for (int i = 0; i < Matisse.obtainResult(data).size(); i++) {
                albums = new ModuleAlbumData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Matisse.obtainResult(data).get(i));
                    Bitmap bmpImage = resizeBitmap(bitmap);
                    albums.setBmpModuleImage(bmpImage);

                    /* SET THE IMAGE NUMBER */
                    String strNumber = String.valueOf(i + 1);
                    albums.setTxtImageNumber(strNumber);

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrImages.add(albums);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /* SET THE ADAPTER TO THE RECYCLER VIEW */
            gridModuleImages.setAdapter(new TrainingModuleAlbumAdapter(arrImages));
            gridModuleImages.setVisibility(View.VISIBLE);
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
        if (ContextCompat.checkSelfPermission(TrainingModuleCreator.this,
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
                                        TrainingModuleCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            Matisse.from(TrainingModuleCreator.this)
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
                Matisse.from(TrainingModuleCreator.this)
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
                        .content("Since the permission was denied, you won't be able to upload images along with the new Training Module. Zen Pets needs this permission to optimize the images you select before uploading them to provide a uniform experience to the Pet Parents on  Zen Pets." +
                                "\n\nTo grant the permission, click the \"GRANT PERMISSION\" button. To post your new Training Module without adding images, click the \"NOT NOW\" button. You can always add images to your Module later.")
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

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        int intOrientation = getResources().getConfiguration().orientation;
        gridModuleImages.setHasFixedSize(true);
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
        gridModuleImages.setLayoutManager(glm);
        gridModuleImages.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        gridModuleImages.setAdapter(new TrainingModuleAlbumAdapter(arrImages));
    }
}