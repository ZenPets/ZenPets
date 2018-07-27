package biz.zenpets.users.creator.question;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.pet.NewPetCreator;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.pet.PetSpinnerAdapter;
import biz.zenpets.users.utils.adapters.problems.ProblemSpinnerAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.pets.pet.FetchUserPets;
import biz.zenpets.users.utils.helpers.pets.pet.FetchUserPetsInterface;
import biz.zenpets.users.utils.models.consultations.consultations.Consultation;
import biz.zenpets.users.utils.models.consultations.consultations.ConsultationsAPI;
import biz.zenpets.users.utils.models.pets.pets.Pet;
import biz.zenpets.users.utils.models.problems.Problem;
import biz.zenpets.users.utils.models.problems.Problems;
import biz.zenpets.users.utils.models.problems.ProblemsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionCreator extends AppCompatActivity implements FetchUserPetsInterface {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** DATA TYPES TO HOLD THE CONSULTATION DATA **/
    private String USER_ID = null;
    private String PET_ID = null;
    private String PROBLEM_ID = null;
    private String CONSULTATION_TITLE = null;
    private String CONSULTATION_DESCRIPTION = null;
    private String CONSULTATION_DATE = null;
    private String CONSULTATION_PICTURE = null;
    private String FILE_NAME = null;
    private Uri CONSULTATION_URI = null;

    /** THE PETS ARRAY LIST **/
    private ArrayList<Pet> arrPets = new ArrayList<>();

    /** THE PROBLEMS ARRAY LIST **/
    private ArrayList<Problem> arrProblems = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnMyPets) AppCompatSpinner spnMyPets;
    @BindView(R.id.spnProblem) AppCompatSpinner spnProblem;
    @BindView(R.id.inputTitle) TextInputLayout inputTitle;
    @BindView(R.id.edtTitle) AppCompatEditText edtTitle;
    @BindView(R.id.inputDescription) TextInputLayout inputDescription;
    @BindView(R.id.edtDescription) AppCompatEditText edtDescription;
    @BindView(R.id.cardConsultThumb) CardView cardConsultThumb;
    @BindView(R.id.imgvwConsultThumb) AppCompatImageView imgvwConsultThumb;
    @BindView(R.id.chkbxAcceptTerms) AppCompatCheckBox chkbxAcceptTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_creator);
        ButterKnife.bind(this);

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* SET THE CURRENT DATE */
        setCurrentDate();

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        if (USER_ID != null)    {
            /* FETCH THE USER'S PETS */
            new FetchUserPets(this).execute(USER_ID);
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }

        /* SELECT A PET */
        spnMyPets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PET_ID = arrPets.get(position).getPetID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* SELECT A PROBLEM */
        spnProblem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PROBLEM_ID = arrProblems.get(position).getProblemID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** CHECK FOR ALL CONSULTATION DETAILS *****/
    private void checkConsultationDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtTitle.getWindowToken(), 0);
        }

        /* GET THE REQUIRED TEXTS */
        CONSULTATION_TITLE = edtTitle.getText().toString();
        CONSULTATION_DESCRIPTION = edtDescription.getText().toString();
        if (CONSULTATION_URI != null && CONSULTATION_TITLE != null)   {
            FILE_NAME = CONSULTATION_TITLE.replaceAll(" ", "_").toLowerCase().trim() + "_" + USER_ID + "_" + CONSULTATION_DATE.replaceAll(" ", "_").toLowerCase().trim();
        } else {
            FILE_NAME = null;
            CONSULTATION_PICTURE = "Null";
        }

        /* CHECK THE REQUIRED DETAILS */
        if (chkbxAcceptTerms.isChecked()) {
            if (TextUtils.isEmpty(CONSULTATION_TITLE))  {
                inputTitle.setErrorEnabled(false);
                inputDescription.setErrorEnabled(false);
                inputTitle.setError("Enter the title");
            } else if (CONSULTATION_TITLE.length() < 10)    {
                inputTitle.setErrorEnabled(false);
                inputDescription.setErrorEnabled(false);
                inputTitle.setError("Title should be more than 10 chars");
            } else if (CONSULTATION_TITLE.length() > 50)    {
                inputTitle.setErrorEnabled(false);
                inputDescription.setErrorEnabled(false);
                inputTitle.setError("Title cannot be more than 50 chars");
            } else if (TextUtils.isEmpty(CONSULTATION_DESCRIPTION)) {
                inputTitle.setErrorEnabled(false);
                inputDescription.setErrorEnabled(false);
                inputDescription.setError("Provide some description....");
            } else if (CONSULTATION_DESCRIPTION.length() < 100) {
                inputTitle.setErrorEnabled(false);
                inputDescription.setErrorEnabled(false);
                inputDescription.setError("Description should be minimum 100 chars");
            } else {
                /* CHECK IF AN IMAGES IS ATTACHED */
                if (CONSULTATION_URI != null)   {
                    /* PUBLISH THE CONSULTATION IMAGE */
                    publishConsultationImage();
                } else {
                    /* SHOW THE PROGRESS DIALOG AND PUBLISH CONSULTATION */
                    dialog = new ProgressDialog(this);
                    dialog.setMessage("Please wait while we publish your question..");
                    dialog.setIndeterminate(false);
                    dialog.setCancelable(false);
                    dialog.show();
                    publishPublicConsultation();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "You must accept the terms before posting", Toast.LENGTH_LONG).show();
        }
    }

    /***** PUBLISH THE CONSULTATION IMAGE *****/
    private void publishConsultationImage() {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we publish your question..");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* PUBLISH THE PET PROFILE TO FIREBASE */
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference refStorage = storageReference.child("Consultations").child(FILE_NAME);
//        refStorage.putFile(CONSULTATION_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                if (downloadURL != null)    {
//                    CONSULTATION_PICTURE = String.valueOf(downloadURL);
//                    if (CONSULTATION_PICTURE != null)    {
//                        /* DISMISS THE DIALOG AND PUBLISH THE QUESTION */
//                        dialog.dismiss();
//                        publishPublicConsultation();
//                    } else {
//                        dialog.dismiss();
//                        Toast.makeText(
//                                getApplicationContext(),
//                                "There was a problem publishing your new Consultation. Please try again by clicking the Save button.",
//                                Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    dialog.dismiss();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "There was a problem publishing your new Consultation. Please try again by clicking the Save button.",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    /***** PUBLISH THE NEW PUBLIC CONSULTATION *****/
    private void publishPublicConsultation() {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
        Call<Consultation> call = api.newConsultation(
                USER_ID, PET_ID, PROBLEM_ID,
                CONSULTATION_TITLE, CONSULTATION_DESCRIPTION, CONSULTATION_PICTURE, timeStamp
        );
        call.enqueue(new Callback<Consultation>() {
            @Override
            public void onResponse(Call<Consultation> call, Response<Consultation> response) {
                if (response.isSuccessful())    {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully published your question", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error publishing your question. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Consultation> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void userPets(ArrayList<Pet> data) {
        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
        arrPets = data;

        /* CHECK FOR THE SIZE OF THE RESULT */
        if (arrPets.size() > 0) {
            /* SET THE ADAPTER TO THE PETS SPINNER */
            spnMyPets.setAdapter(new PetSpinnerAdapter(QuestionCreator.this, arrPets));
        } else {
            /* SHOW THE NO PETS FOUND DIALOG */
            noPetsFound();
        }

        /* FETCH THE LIST OF PROBLEMS */
        fetchListOfProblems();
    }

    /***** FETCH THE LIST OF PROBLEMS *****/
    private void fetchListOfProblems() {
        ProblemsAPI api = ZenApiClient.getClient().create(ProblemsAPI.class);
        Call<Problems> call = api.allProblemTypes();
        call.enqueue(new Callback<Problems>() {
            @Override
            public void onResponse(Call<Problems> call, Response<Problems> response) {
                arrProblems = response.body().getProblems();

                /* SET THE ADAPTER TO THE PROBLEMS SPINNER */
                spnProblem.setAdapter(new ProblemSpinnerAdapter(QuestionCreator.this, arrProblems));
            }

            @Override
            public void onFailure(Call<Problems> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Ask free question";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(QuestionCreator.this);
        inflater.inflate(R.menu.activity_question_creator, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuAttach:
                /* SHOW THE IMAGE DIALOG */
                showImageDialog();
                break;
            case R.id.menuSubmit:
                /* CHECK FOR ALL CONSULTATION DETAILS */
                checkConsultationDetails();
                break;
            default:
                break;
        }
        return false;
    }

    /***** SHOW THE IMAGE DIALOG *****/
    private void showImageDialog() {
        final BottomSheetDialog sheetDialog = new BottomSheetDialog(QuestionCreator.this);
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
                EasyImage.openGallery(QuestionCreator.this, 0);
            }
        });

        /* SELECT A CAMERA IMAGE */
        linlaCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                EasyImage.openCamera(QuestionCreator.this, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            /* CLEAR THE PETS ARRAY LIST */
            arrPets.clear();

            /* FETCH THE LIST OF PETS AGAIN */
            new FetchUserPets(this).execute(USER_ID);
        }

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
            imgvwConsultThumb.setImageBitmap(bitmap);
            imgvwConsultThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /* SHOW THE IMAGE CONTAINER */
            cardConsultThumb.setVisibility(View.VISIBLE);

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
                CONSULTATION_URI = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***** SET THE CURRENT DATE *****/
    private void setCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        CONSULTATION_DATE = sdf.format(cal.getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EasyImage.clearConfiguration(this);
    }

    /***** SHOW THE NO PETS FOUND DIALOG *****/
    private void noPetsFound() {
        new MaterialDialog.Builder(QuestionCreator.this)
                .title("No pets found...")
                .content("You haven't added any Pets to your account yet. Before you can post a consultation question, you must have the Pet you are asking the question for added to your account.\\n\\nThis helps the Doctors that answer your question know the type of Pet as well as it's breed.\\n\\nTo add a Pet now, click the \\\"ADD PET\\\" button. Or, select the \\\"CANCEL\\\" button to exit this page...")
                .positiveText("Add Pet")
                .negativeText("Cancel")
                .theme(Theme.LIGHT)
                .icon(ContextCompat.getDrawable(QuestionCreator.this, R.drawable.ic_info_outline_black_24dp))
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent addNewPet = new Intent(QuestionCreator.this, NewPetCreator.class);
                        startActivityForResult(addNewPet, 101);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}