package co.zenpets.trainers.details.trainers;

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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.zenpets.trainers.R;
import co.zenpets.trainers.utils.adapters.enquiries.TrainingMessagesAdapter;
import co.zenpets.trainers.utils.adapters.modules.TrainingModuleImagesAdapter;
import co.zenpets.trainers.utils.helpers.ZenApiClient;
import co.zenpets.trainers.utils.models.notifications.Notification;
import co.zenpets.trainers.utils.models.notifications.NotificationsAPI;
import co.zenpets.trainers.utils.models.trainers.enquiries.EnquiryMessage;
import co.zenpets.trainers.utils.models.trainers.enquiries.EnquiryMessages;
import co.zenpets.trainers.utils.models.trainers.enquiries.EnquiryMessagesAPI;
import co.zenpets.trainers.utils.models.trainers.modules.Module;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleImage;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleImages;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleImagesAPI;
import co.zenpets.trainers.utils.models.trainers.modules.ModulesAPI;
import co.zenpets.trainers.utils.models.users.UserData;
import co.zenpets.trainers.utils.models.users.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class TrainerEnquiryActivity extends AppCompatActivity {

    /** THE INCOMING TRAINER, MODULE ID AND TRAINING ENQUIRY MASTER ID **/
    private String TRAINER_ID = null;
    private String MODULE_ID = null;
    private String TRAINING_MASTER_ID = null;

    /** THE TRAINER'S NAME **/
    String TRAINER_NAME = null;

    /** THE MESSAGES ARRAY LIST **/
    private ArrayList<EnquiryMessage> arrMessages = new ArrayList<>();

    /** THE TRAINING MODULE IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<ModuleImage> arrImages = new ArrayList<>();

    /** THE USER'S ID AND TOKEN **/
    private String USER_ID = null;
    private String USER_TOKEN = null;

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

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /***** FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER *****/
    private void fetchEnquiryMessages() {
        EnquiryMessagesAPI apiEnquiry = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
        Call<EnquiryMessages> call = apiEnquiry.fetchEnquiryMessages(TRAINING_MASTER_ID);
        call.enqueue(new Callback<EnquiryMessages>() {
            @Override
            public void onResponse(Call<EnquiryMessages> call, Response<EnquiryMessages> response) {
                try {
                    String strResult = new Gson().toJson(response.body());
                    JSONObject JORoot = new JSONObject(strResult);
                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                        JSONArray JAMessages = JORoot.getJSONArray("messages");
//                        Log.e("MESSAGES", String.valueOf(JAMessages));

                        /* THE DATA MODEL INSTANCE */
                        EnquiryMessage data;

                        for (int i = 0; i < JAMessages.length(); i++) {
                            JSONObject JOMessages = JAMessages.getJSONObject(i);

                            /* INSTANTIATE THE DATA OBJECT INSTANCE */
                            data = new EnquiryMessage();

                            /* SET THE TRAINING SLAVE ID */
                            if (JOMessages.has("trainingSlaveID"))  {
                                data.setTrainingSlaveID(JOMessages.getString("trainingSlaveID"));
                            } else {
                                data.setTrainingSlaveID(null);
                            }

                            /* SET THE TRAINING MASTER ID */
                            if (JOMessages.has("trainingMasterID")) {
                                data.setTrainingMasterID(JOMessages.getString("trainingMasterID"));
                            } else {
                                data.setTrainingMasterID(null);
                            }

                            /* SET THE TRAINER'S ID */
                            if (JOMessages.has("trainerID"))    {
                                data.setTrainerID(JOMessages.getString("trainerID"));
                            } else {
                                data.setTrainerID(null);
                            }

                            /* SET THE TRAINER'S NAME */
                            if (JOMessages.has("trainerName"))  {
                                data.setTrainerName(JOMessages.getString("trainerName"));
                                TRAINER_NAME = JOMessages.getString("trainerName");
                            } else {
                                data.setTrainerName(null);
                            }

                            /* SET THE TRAINER'S DISPLAY PROFILE */
                            if (JOMessages.has("trainerDisplayProfile"))    {
                                data.setTrainerDisplayProfile(JOMessages.getString("trainerDisplayProfile"));
                            } else {
                                data.setTrainerDisplayProfile(null);
                            }

                            /* SET THE TRAINER'S TOKEN */
                            if (JOMessages.has("trainerToken")) {
                                data.setTrainerToken(JOMessages.getString("trainerToken"));
                            } else {
                                data.setTrainerToken(null);
                            }

                            /* SET THE USER'S ID */
                            if (JOMessages.has("userID"))   {
                                data.setUserID(JOMessages.getString("userID"));
                                USER_ID = JOMessages.getString("userID");
//                                Log.e("USER ID", USER_ID);
                            } else {
                                data.setUserID(null);
                            }

                            /* SET THE USER'S NAME */
                            if (JOMessages.has("userName")) {
                                data.setUserName(JOMessages.getString("userName"));
                            } else {
                                data.setUserName(null);
                            }

                            /* SET THE USER'S DISPLAY PROFILE */
                            if (JOMessages.has("userDisplayProfile"))   {
                                data.setUserDisplayProfile(JOMessages.getString("userDisplayProfile"));
                            } else {
                                data.setUserDisplayProfile(null);
                            }

                            /* SET THE USER'S TOKEN */
                            if (JOMessages.has("userToken"))    {
                                data.setUserToken(JOMessages.getString("userToken"));
                            } else {
                                data.setUserToken(null);
                            }

                            /* SET THE ENQUIRY MESSAGE */
                            if (JOMessages.has("trainingSlaveMessage")) {
                                data.setTrainingSlaveMessage(JOMessages.getString("trainingSlaveMessage"));
                            } else {
                                data.setTrainingSlaveMessage(null);
                            }

                            /* SET THE ENQUIRY TIME STAMP */
                            if (JOMessages.has("trainerSlaveTimestamp"))    {
                                data.setTrainerSlaveTimestamp(JOMessages.getString("trainerSlaveTimestamp"));
                            } else {
                                data.setTrainerSlaveTimestamp(null);
                            }

                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                            arrMessages.add(data);
                        }

                        /* SET THE ADAPTER */
                        listMessages.setAdapter(new TrainingMessagesAdapter(TrainerEnquiryActivity.this, arrMessages));
                        listMessages.scrollToPosition(arrMessages.size() - 1);

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listMessages.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* ENABLE THE REPLY EDIT TEXT */
                        edtMessage.setEnabled(true);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listMessages.setVisibility(View.GONE);

                        /* DISABLE THE REPLY EDIT TEXT */
                        edtMessage.setEnabled(false);
                    }

                    /* HIDE THE PROGRESS */
                    linlaMessagesProgress.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();

                    /* HIDE THE PROGRESS */
                    linlaMessagesProgress.setVisibility(View.GONE);
                }

                /* FETCH THE USER'S TOKEN */
                fetchUserToken();
            }

            @Override
            public void onFailure(Call<EnquiryMessages> call, Throwable t) {
//                Log.e("MESSAGES FAILURE", t.getMessage());
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("TRAINER_ID") && bundle.containsKey("MODULE_ID")) {
            TRAINER_ID = bundle.getString("TRAINER_ID");
            MODULE_ID = bundle.getString("MODULE_ID");
            TRAINING_MASTER_ID = bundle.getString("TRAINING_MASTER_ID");
            if (TRAINER_ID != null && MODULE_ID != null && TRAINING_MASTER_ID != null)    {
                /* SHOW THE PROGRESS AND FETCH THE TRAINING MODULE DETAILS */
                linlaMessagesProgress.setVisibility(View.VISIBLE);
                fetchModuleDetails();
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
                        String strFinalUnits = null;
                        if (MODULE_DURATION_UNIT.equalsIgnoreCase("day")) {
                            int totalDays = Integer.parseInt(MODULE_DURATION);
                            Resources resDays = getResources();
                            if (totalDays == 1)    {
                                strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                            } else if (totalDays > 1) {
                                strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                            }
                        } else if (MODULE_DURATION_UNIT.equalsIgnoreCase("Month")){
                            int totalDays = Integer.parseInt(MODULE_DURATION);
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
//                Log.e("DETAILS FAILURE", t.getMessage());

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

                /* FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER */
                fetchEnquiryMessages();
            }

            @Override
            public void onFailure(Call<ModuleImages> call, Throwable t) {

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
        listMessages.setHasFixedSize(true);
        listMessages.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(listMessages, false);

        /* SET THE ADAPTER */
        listMessages.setAdapter(new TrainingMessagesAdapter(TrainerEnquiryActivity.this, arrMessages));

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
        EnquiryMessagesAPI apiEnquiry = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
        Call<EnquiryMessage> call = apiEnquiry.newEnquiryTrainerMessage(
                TRAINING_MASTER_ID, TRAINER_ID, null, strMessage, strTimestamp);
        call.enqueue(new Callback<EnquiryMessage>() {
            @Override
            public void onResponse(Call<EnquiryMessage> call, Response<EnquiryMessage> response) {
                if (response.isSuccessful())    {
                    Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_SHORT).show();
                    edtMessage.setText("");
                    arrMessages.clear();

                    /* SEND A NOTIFICATION TO THE USER */
                    sendNotificationToUser(strMessage);

                    /* FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER */
                    fetchEnquiryMessages();
                } else {
                    Toast.makeText(getApplicationContext(), "Publish failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EnquiryMessage> call, Throwable t) {
//                Log.e("PUBLISH FAILURE", t.getMessage());
            }
        });
    }

    /***** SEND A NOTIFICATION TO THE USER *****/
    private void sendNotificationToUser(String strMessage) {
        if (USER_TOKEN != null) {
            NotificationsAPI api = ZenApiClient.getClient().create(NotificationsAPI.class);
            Call<Notification> call = api.sendEnquiryReplyNotification(
                    USER_TOKEN, "New reply from " + TRAINER_NAME,
                    strMessage, "Enquiry", TRAINER_ID, MODULE_ID, TRAINING_MASTER_ID);
            call.enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
//                    Log.e("PUSH FAILURE", t.getMessage());
                }
            });
        }
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

    /***** FETCH THE USER'S TOKEN *****/
    private void fetchUserToken() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchUserDetails(USER_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
//                Log.e("RAW", String.valueOf(response.raw()));
                UserData trainer = response.body();
                if (trainer != null)    {
                    /* GET THE USER'S TOKEN */
                    USER_TOKEN = trainer.getUserToken();
                    if (USER_TOKEN != null)  {
//                        Log.e("TOKEN", USER_TOKEN);
                    } else {
//                        Log.e("TOKEN", "No Token Fetched...");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
    }
}