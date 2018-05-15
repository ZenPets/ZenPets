package biz.zenpets.users.details.trainers.enquiry;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.trainers.TrainingModuleImagesAdapter;
import biz.zenpets.users.utils.adapters.trainers.enquiry.TrainingEnquiryAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.notifications.Notification;
import biz.zenpets.users.utils.models.notifications.NotificationsAPI;
import biz.zenpets.users.utils.models.trainers.Trainer;
import biz.zenpets.users.utils.models.trainers.TrainersAPI;
import biz.zenpets.users.utils.models.trainers.enquiry.EnquiryMessage;
import biz.zenpets.users.utils.models.trainers.enquiry.EnquiryMessages;
import biz.zenpets.users.utils.models.trainers.enquiry.EnquiryMessagesAPI;
import biz.zenpets.users.utils.models.trainers.enquiry.TrainingEnquiry;
import biz.zenpets.users.utils.models.trainers.enquiry.TrainingEnquiryAPI;
import biz.zenpets.users.utils.models.trainers.modules.Module;
import biz.zenpets.users.utils.models.trainers.modules.ModuleImage;
import biz.zenpets.users.utils.models.trainers.modules.ModuleImages;
import biz.zenpets.users.utils.models.trainers.modules.ModuleImagesAPI;
import biz.zenpets.users.utils.models.trainers.modules.ModulesAPI;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainerEnquiryActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** A TRAINING ENQUIRY API AND ENQUIRY MESSAGE INSTANCE **/
    TrainingEnquiryAPI api = ZenApiClient.getClient().create(TrainingEnquiryAPI.class);
    EnquiryMessagesAPI apiEnquiry = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);

    /** THE LOGGED IN USER'S ID AND NAME **/
    String USER_ID = null;
    String USER_NAME = null;

    /** THE INCOMING TRAINER AND MODULE ID **/
    String TRAINER_ID = null;
    String MODULE_ID = null;

    /** THE TRAINING ENQUIRY MASTER ID **/
    String TRAINING_MASTER_ID = null;

    /** THE TRAINER'S TOKEN **/
    String TRAINER_TOKEN = null;

    /** THE MESSAGES ARRAY LIST **/
    ArrayList<EnquiryMessage> arrMessages = new ArrayList<>();

    /** THE TRAINING MODULE IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<ModuleImage> arrImages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtModuleName) AppCompatTextView txtModuleName;
    @BindView(R.id.txtModuleDuration) AppCompatTextView txtModuleDuration;
    @BindView(R.id.txtModuleDetails) AppCompatTextView txtModuleDetails;
    @BindView(R.id.imgvwModuleFormat) AppCompatImageView imgvwModuleFormat;
    @BindView(R.id.txtModuleFormat) AppCompatTextView txtModuleFormat;
    @BindView(R.id.txtModuleFees) AppCompatTextView txtModuleFees;
    @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
    @BindView(R.id.listModuleImages) RecyclerView listModuleImages;
    @BindView(R.id.linlaMessagesProgress) LinearLayout linlaMessagesProgress;
    @BindView(R.id.listMessages) RecyclerView listMessages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.edtMessage) EditText edtMessage;

    /** POST A NEW MESSAGE **/
    @OnClick(R.id.imgbtnPostMessage) void postMessage() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtMessage.getWindowToken(), 0);
        }

        /* GET THE MESSAGE FROM THE MESSAGE EDIT TEXT */
        String ENQUIRY_MESSAGE = edtMessage.getText().toString().trim();

        /* GENERATE THE TIME STAMP */
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        /* VALIDATE THE MESSAGE IS NOT NULL */
        if (TextUtils.isEmpty(ENQUIRY_MESSAGE)) {
            edtMessage.setError("Please type a message....");
            edtMessage.requestFocus();
        } else  {
            /* PUBLISH THE TRAINING ENQUIRY MESSAGE */
            publishMessage(ENQUIRY_MESSAGE, timeStamp);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_enquiry_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* GET THE USER'S DETAILS */
        fetchUserDetails();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /***** FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER *****/
    private void fetchEnquiryMessages() {
        Call<EnquiryMessages> call = apiEnquiry.fetchEnquiryMessages(TRAINING_MASTER_ID);
        call.enqueue(new Callback<EnquiryMessages>() {
            @Override
            public void onResponse(Call<EnquiryMessages> call, Response<EnquiryMessages> response) {
                if (response.body() != null && response.body().getMessages() != null)   {
                    arrMessages = response.body().getMessages();
                    if (arrMessages.size() > 0) {

                        /* SET THE ADAPTER */
                        listMessages.setAdapter(new TrainingEnquiryAdapter(TrainerEnquiryActivity.this, arrMessages));
//                        listMessages.scrollToPosition(listMessages.getAdapter().getItemCount());
//                        listMessages.scrollToPosition(arrMessages.size() - 1);

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listMessages.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listMessages.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listMessages.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<EnquiryMessages> call, Throwable t) {
                Log.e("MESSAGES FAILURE", t.getMessage());
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /***** CHECK IF A MASTER ENQUIRY RECORD EXISTS *****/
    private void checkMasterEnquiry() {
        Call<TrainingEnquiry> call = api.checkMasterTrainingEnquiry(MODULE_ID, USER_ID);
        call.enqueue(new Callback<TrainingEnquiry>() {
            @Override
            public void onResponse(Call<TrainingEnquiry> call, Response<TrainingEnquiry> response) {
//                Log.e("RESPONSE RAW", String.valueOf(response.raw()));
                TrainingEnquiry enquiry = response.body();
                Boolean blnError = enquiry.getError();
                if (blnError)   {
                    /* CREATE A NEW ENQUIRY MASTER RECORD */
                    createMasterRecord();
                } else {
                    /* GET THE TRAINING ENQUIRY MASTER ID */
                    TRAINING_MASTER_ID = enquiry.getTrainingMasterID();

                    /* FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER */
                    fetchEnquiryMessages();
                }
            }

            @Override
            public void onFailure(Call<TrainingEnquiry> call, Throwable t) {
                Log.e("CHECK FAILURE", t.getMessage());
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /***** CREATE A NEW ENQUIRY MASTER RECORD *****/
    private void createMasterRecord() {
        Call<TrainingEnquiry> call = api.createMasterTrainingEnquiryRecord(MODULE_ID, USER_ID);
        call.enqueue(new Callback<TrainingEnquiry>() {
            @Override
            public void onResponse(Call<TrainingEnquiry> call, Response<TrainingEnquiry> response) {
                if (response.isSuccessful())    {
                    TrainingEnquiry enquiry = response.body();

                    /* GET THE TRAINING ENQUIRY MASTER ID */
                    TRAINING_MASTER_ID = enquiry.getTrainingMasterID();

                    /* FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER */
                    fetchEnquiryMessages();
                } else {
                    Toast.makeText(getApplicationContext(), "Network error. Please try again...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<TrainingEnquiry> call, Throwable t) {
                Log.e("CREATE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("TRAINER_ID") && bundle.containsKey("MODULE_ID")) {
            TRAINER_ID = bundle.getString("TRAINER_ID");
            MODULE_ID = bundle.getString("MODULE_ID");
            if (TRAINER_ID != null && MODULE_ID != null)    {
                /* SHOW THE PROGRESS AND FETCH THE TRAINING MODULE DETAILS */
                linlaMessagesProgress.setVisibility(View.VISIBLE);
                fetchModuleDetails();
                Log.e("TRAINER ID", TRAINER_ID);
                Log.e("MODULE ID", MODULE_ID);

                /* FETCH THE TRAINER'S TOKEN */
                fetchTrainerToken();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE TRAINING MODULE DETAILS *****/
    private void fetchModuleDetails() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Module> call = api.fetchModuleDetails(MODULE_ID);
        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
//                Log.e("RAW RESPONSE", String.valueOf(response.raw()));
                Module module = response.body();
                if (module != null) {

                    /* SET THE TRAINING MODULE NAME */
                    String MODULE_NAME = module.getTrainerModuleName();
                    if (MODULE_NAME != null) {
                        txtModuleName.setText(MODULE_NAME);
                    }

                    /* SET THE TRAINING DURATION AND SESSIONS */
                    String MODULE_DURATION = module.getTrainerModuleDuration();
                    String MODULE_DURATION_UNIT = module.getTrainerModuleDurationUnit();
                    String MODULE_SESSIONS = module.getTrainerModuleSessions();
                    if (MODULE_DURATION != null && MODULE_SESSIONS != null) {
                        String strNumber = MODULE_DURATION;
                        String strUnits = MODULE_DURATION_UNIT;
                        String strFinalUnits = null;
                        if (strUnits.equalsIgnoreCase("day")) {
                            int totalDays = Integer.parseInt(strNumber);
                            Resources resDays = getResources();
                            if (totalDays == 1)    {
                                strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                            } else if (totalDays > 1) {
                                strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                            }
                        } else if (strUnits.equalsIgnoreCase("Month")){
                            int totalDays = Integer.parseInt(strNumber);
                            Resources resDays = getResources();
                            if (totalDays == 1)    {
                                strFinalUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
                            } else if (totalDays > 1) {
                                strFinalUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
                            }
                        }

                        String strSessions = module.getTrainerModuleSessions();
                        String strFinalSessions = null;
                        int totalSessions = Integer.parseInt(strSessions);
                        Resources resSessions = getResources();
                        if (totalSessions == 1)    {
                            strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
                        } else if (totalSessions > 1) {
                            strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
                        }
                        txtModuleDuration.setText(getString(R.string.module_duration_sessions_placeholder, strFinalUnits, strFinalSessions));
                    }

                    /* SET THE TRAINING DETAILS */
                    String MODULE_DETAILS = module.getTrainerModuleDetails();
                    if (MODULE_DETAILS != null) {
                        txtModuleDetails.setText(MODULE_DETAILS);
                    }

                    /* SET THE MODULE FORMAT */
                    String MODULE_FORMAT = module.getTrainerModuleFormat();
                    if (MODULE_FORMAT.equalsIgnoreCase("Individual"))   {
                        imgvwModuleFormat.setImageResource(R.drawable.ic_training_individual_light);
                        txtModuleFormat.setText(getString(R.string.module_format_individual_placeholder));
                    } else {
                        String strGroupSize = module.getTrainerModuleSize();
                        imgvwModuleFormat.setImageResource(R.drawable.ic_training_group_light);
                        txtModuleFormat.setText(getString(R.string.module_format_group_placeholder, strGroupSize));
                    }

                    /* SET THE TRAINING FEES */
                    String MODULE_FEES = module.getTrainerModuleFees();
                    if (MODULE_DETAILS != null)    {
                        txtModuleFees.setText(getString(R.string.module_fees_placeholder, MODULE_FEES));
                    }

                    /* FETCH THE MODULE IMAGES */
                    fetchModuleImages();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /***** FETCH THE MODULE IMAGES *****/
    private void fetchModuleImages() {
        ModuleImagesAPI apiImages = ZenApiClient.getClient().create(ModuleImagesAPI.class);
        Call<ModuleImages> callImages = apiImages.fetchTrainingModuleImages(MODULE_ID);
        callImages.enqueue(new Callback<ModuleImages>() {
            @Override
            public void onResponse(Call<ModuleImages> call, Response<ModuleImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0)   {
                        /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                        listModuleImages.setAdapter(new TrainingModuleImagesAdapter(TrainerEnquiryActivity.this, arrImages));

                        /* SHOW THE IMAGES CONTAINER */
                        linlaImagesContainer.setVisibility(View.VISIBLE);
                    } else {
                        /* HIDE THE IMAGES CONTAINER */
                        linlaImagesContainer.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE IMAGES CONTAINER */
                    linlaImagesContainer.setVisibility(View.GONE);
                }

                /* CHECK IF A MASTER ENQUIRY RECORD EXISTS */
                checkMasterEnquiry();
            }

            @Override
            public void onFailure(Call<ModuleImages> call, Throwable t) {
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        listMessages.setLayoutManager(manager);
        listMessages.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(listMessages, false);

//        /* SET THE ADAPTER */
//        listMessages.setAdapter(new TrainingEnquiryAdapter(TrainerEnquiryActivity.this, arrMessages));

        /* SET THE CONFIGURATION FOR THE MODULE IMAGES */
        LinearLayoutManager llmAppointments = new LinearLayoutManager(this);
        llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmAppointments.setAutoMeasureEnabled(true);
        listModuleImages.setLayoutManager(llmAppointments);
        listModuleImages.setHasFixedSize(true);
        listModuleImages.setNestedScrollingEnabled(false);

        /* CONFIGURE THE ADAPTER */
        listModuleImages.setAdapter(new TrainingModuleImagesAdapter(TrainerEnquiryActivity.this, arrImages));
    }

    /***** PUBLISH THE TRAINING ENQUIRY MESSAGE *****/
    private void publishMessage(final String strMessage, String strTimestamp) {
        Call<EnquiryMessage> call = apiEnquiry.newEnquiryUserMessage(
                TRAINING_MASTER_ID, null, USER_ID, strMessage, strTimestamp);
        call.enqueue(new Callback<EnquiryMessage>() {
            @Override
            public void onResponse(Call<EnquiryMessage> call, Response<EnquiryMessage> response) {
                if (response.isSuccessful())    {
                    Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_SHORT).show();
                    edtMessage.setText("");
                    arrMessages.clear();

                    /* SEND A NOTIFICATION TO THE TRAINER */
                    sendNotificationToTrainer(strMessage);

                    /* FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER */
                    fetchEnquiryMessages();
                } else {
                    Toast.makeText(getApplicationContext(), "Publish failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EnquiryMessage> call, Throwable t) {
                Log.e("PUBLISH FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** SEND A NOTIFICATION TO THE TRAINER *****/
    private void sendNotificationToTrainer(String strMessage) {
        NotificationsAPI api = ZenApiClient.getClient().create(NotificationsAPI.class);
        Call<Notification> call = api.sendEnquiryReplyNotification(
                TRAINER_TOKEN, "New enquiry from " + USER_NAME,
                strMessage, "Enquiry", TRAINER_ID, MODULE_ID, TRAINING_MASTER_ID);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Log.e("PUSH FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TRAINER'S TOKEN *****/
    private void fetchTrainerToken() {
        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainer> call = api.fetchTrainerDetails(TRAINER_ID);
        call.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
                Trainer trainer = response.body();
                if (trainer != null)    {
                    /* GET THE TRAINER'S TOKEN */
                    TRAINER_TOKEN = trainer.getTrainerToken();
                    if (TRAINER_TOKEN != null)  {
                        Log.e("TOKEN", TRAINER_TOKEN);
                    } else {
                        Log.e("TOKEN", "No Token Fetched...");
                    }
                }
            }

            @Override
            public void onFailure(Call<Trainer> call, Throwable t) {
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE USER'S DETAILS *****/
    private void fetchUserDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchUserDetails(USER_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    USER_NAME = data.getUserName();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Log.e("PROFILE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Enquiry Messages";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }
}