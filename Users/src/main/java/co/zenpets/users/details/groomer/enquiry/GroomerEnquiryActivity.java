package co.zenpets.users.details.groomer.enquiry;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.groomers.enquiry.GroomerEnquiryAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.groomers.enquiry.EnquiriesAPI;
import co.zenpets.users.utils.models.groomers.enquiry.Enquiry;
import co.zenpets.users.utils.models.groomers.enquiry.EnquiryMessage;
import co.zenpets.users.utils.models.groomers.enquiry.EnquiryMessages;
import co.zenpets.users.utils.models.groomers.enquiry.EnquiryMessagesAPI;
import co.zenpets.users.utils.models.groomers.groomers.Groomer;
import co.zenpets.users.utils.models.groomers.groomers.GroomersAPI;
import co.zenpets.users.utils.models.groomers.notifications.Notification;
import co.zenpets.users.utils.models.groomers.notifications.NotificationsAPI;
import co.zenpets.users.utils.models.user.UserData;
import co.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class GroomerEnquiryActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID, NAME AND DISPLAY PROFILE **/
    private String USER_ID = null;
    private String USER_NAME = null;
    private String USER_DISPLAY_PROFILE = null;

    /** THE GROOMER DETAILS AND THE ENQUIRY ID **/
    private  String GROOMER_ID = null;
    private String GROOMER_NAME = null;
    private String GROOMER_LOGO = null;
    private String GROOMER_TOKEN = null;
    private String ENQUIRY_ID = null;

    /** THE MESSAGES ARRAY LIST **/
    private ArrayList<EnquiryMessage> arrMessages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwGroomerLogo) SimpleDraweeView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.linlaMessagesProgress) LinearLayout linlaMessagesProgress;
    @BindView(R.id.listMessages) RecyclerView listMessages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.edtMessage) EditText edtMessage;
    @BindView(R.id.imgbtnPostMessage) ImageButton imgbtnPostMessage;

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
            /* PUBLISH THE GROOMER ENQUIRY MESSAGE */
            publishMessage(ENQUIRY_MESSAGE, timeStamp);
        }
    }

    /** PUBLISH THE GROOMER ENQUIRY MESSAGE **/
    private void publishMessage(final String enquiryMessage, final String timeStamp) {
        if (ENQUIRY_ID != null) {
            EnquiryMessagesAPI api = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
            Call<EnquiryMessage> call = api.newGroomerEnquiryUserMessage(
                    ENQUIRY_ID, null, USER_ID, enquiryMessage, timeStamp);
            call.enqueue(new retrofit2.Callback<EnquiryMessage>() {
                @Override
                public void onResponse(Call<EnquiryMessage> call, Response<EnquiryMessage> response) {
                    if (response.isSuccessful())    {
                        Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_SHORT).show();
                        edtMessage.setText("");
                        arrMessages.clear();

                        /* SEND A NOTIFICATION TO THE GROOMER */
                        sendNotificationToGroomer(enquiryMessage);

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
        } else {
            final EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
            Call<Enquiry> call = api.createMasterGroomerEnquiryRecord(GROOMER_ID, USER_ID);
            call.enqueue(new retrofit2.Callback<Enquiry>() {
                @Override
                public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
                    if (response.isSuccessful())    {
                        Enquiry enquiry = response.body();

                        /* GET THE ENQUIRY ID */
                        ENQUIRY_ID = enquiry.getEnquiryID();
//                        Log.e("ENQUIRY ID", ENQUIRY_ID);

                        EnquiryMessagesAPI messagesAPI = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
                        Call<EnquiryMessage> messageCall = messagesAPI.newGroomerEnquiryUserMessage(
                                ENQUIRY_ID, GROOMER_ID, USER_ID, enquiryMessage, timeStamp);
                        messageCall.enqueue(new retrofit2.Callback<EnquiryMessage>() {
                            @Override
                            public void onResponse(Call<EnquiryMessage> call, Response<EnquiryMessage> response) {
                                if (response.isSuccessful())    {
                                    Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_SHORT).show();
                                    edtMessage.setText("");
                                    arrMessages.clear();

                                    /* SEND A NOTIFICATION TO THE GROOMER */
                                    sendNotificationToGroomer(enquiryMessage);

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
                }

                @Override
                public void onFailure(Call<Enquiry> call, Throwable t) {
                }
            });
        }
    }

    /** SEND A NOTIFICATION TO THE GROOMER **/
    private void sendNotificationToGroomer(String enquiryMessage) {
        if (GROOMER_TOKEN != null)  {
//            Log.e("TOKEN", KENNEL_OWNER_TOKEN);
            NotificationsAPI api = ZenApiClient.getClient().create(NotificationsAPI.class);
            Call<Notification> call = api.sendGroomerReplyNotification(
                    GROOMER_TOKEN, "New enquiry from " + USER_NAME,
                    enquiryMessage, "Groomer Enquiry", ENQUIRY_ID, GROOMER_ID, USER_ID, USER_NAME, USER_DISPLAY_PROFILE);
            call.enqueue(new retrofit2.Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
//                    Log.e("RESPONSE", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
//                    Log.e("PUSH FAILURE", t.getMessage());
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomer_enquiry_list);
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

    /** CHECK THE MASTER KENNEL ENQUIRY **/
    private void checkMasterEnquiry() {
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiry> call = api.checkMasterGroomerEnquiry(GROOMER_ID, USER_ID);
        call.enqueue(new retrofit2.Callback<Enquiry>() {
            @Override
            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
//                Log.e("MASTER RESPONSE", String.valueOf(response.raw()));
                Enquiry enquiry = response.body();
                if (enquiry != null)    {
                    Boolean blnError = enquiry.getError();
                    if (!blnError)   {
                        /* GET THE ENQUIRY ID */
                        ENQUIRY_ID = enquiry.getEnquiryID();

                        /* SHOW THE PROGRESS AND FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE KENNEL */
                        linlaMessagesProgress.setVisibility(View.VISIBLE);
                        fetchEnquiryMessages();
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
            }

            @Override
            public void onFailure(Call<Enquiry> call, Throwable t) {
            }
        });
    }

    /** FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE GROOMER **/
    private void fetchEnquiryMessages() {
        EnquiryMessagesAPI api = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
        Call<EnquiryMessages> call = api.fetchGroomerEnquiryMessages(ENQUIRY_ID);
        call.enqueue(new retrofit2.Callback<EnquiryMessages>() {
            @Override
            public void onResponse(Call<EnquiryMessages> call, Response<EnquiryMessages> response) {
//                Log.e("MESSAGES RESPONSE", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getMessages() != null)   {
                    arrMessages = response.body().getMessages();
                    if (arrMessages.size() > 0) {

                        /* SET THE ADAPTER */
                        listMessages.setAdapter(new GroomerEnquiryAdapter(GroomerEnquiryActivity.this, arrMessages));

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
//                Log.e("MESSAGES FAILURE", t.getMessage());

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /** FETCH THE GROOMER DETAILS **/
    private void fetchGroomerDetails() {
        GroomersAPI api = ZenApiClient.getClient().create(GroomersAPI.class);
        Call<Groomer> call = api.fetchGroomerDetails(GROOMER_ID, null, null);
        call.enqueue(new retrofit2.Callback<Groomer>() {
            @Override
            public void onResponse(Call<Groomer> call, Response<Groomer> response) {
//                Log.e("GROOMER DETAILS", String.valueOf(response.raw()));
                Groomer data = response.body();
                if (data != null) {

                    /* GET THE GROOMER'S ID */
                    GROOMER_ID = data.getGroomerID();

                    /* GET THE GROOMER'S DEVICE TOKEN */
                    GROOMER_TOKEN = data.getGroomerToken();
//                    Log.e("TOKEN", GROOMER_TOKEN);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE GROOMER'S TOKEN */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Groomer> call, Throwable t) {

            }
        });
    }

    /** SET THE GROOMER DETAILS **/
    private void setGroomerDetails() {
        /* SET THE KENNEL NAME */
        txtGroomerName.setText(GROOMER_NAME);

        /* SET THE GROOMER'S LOGO PHOTO */
        if (GROOMER_LOGO != null) {
            Picasso.with(GroomerEnquiryActivity.this)
                    .load(GROOMER_LOGO)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerCrop()
                    .into(imgvwGroomerLogo, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(GroomerEnquiryActivity.this)
                                    .load(GROOMER_LOGO)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_business_black_24dp)
                                    .into(imgvwGroomerLogo, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
//                                                        Log.e("Picasso","Could not fetch image");
                                        }
                                    });
                        }
                    });
        } else {
        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("GROOMER_ID")
                && bundle.containsKey("GROOMER_NAME")
                && bundle.containsKey("GROOMER_LOGO")
                && bundle.containsKey("ENQUIRY_ID")) {
            GROOMER_ID = bundle.getString("GROOMER_ID");
            GROOMER_NAME = bundle.getString("GROOMER_NAME");
            GROOMER_LOGO = bundle.getString("GROOMER_LOGO");
            ENQUIRY_ID = bundle.getString("ENQUIRY_ID");
            if (GROOMER_ID != null && GROOMER_NAME != null && GROOMER_LOGO != null && ENQUIRY_ID != null)    {
                /* SET THE GROOMER DETAILS */
                setGroomerDetails();

                /* SHOW THE PROGRESS AND FETCH THE GROOMER DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchGroomerDetails();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE GROOMER */
                linlaMessagesProgress.setVisibility(View.VISIBLE);
                fetchEnquiryMessages();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (bundle != null
                && bundle.containsKey("GROOMER_ID")
                && bundle.containsKey("GROOMER_NAME")
                && bundle.containsKey("GROOMER_LOGO"))    {
            GROOMER_ID = bundle.getString("GROOMER_ID");
            GROOMER_NAME = bundle.getString("GROOMER_NAME");
            GROOMER_LOGO = bundle.getString("GROOMER_LOGO");
            if (GROOMER_ID != null && GROOMER_NAME != null && GROOMER_LOGO != null) {
                /* SET THE GROOMER DETAILS */
                setGroomerDetails();

                /* SHOW THE PROGRESS AND FETCH THE GROOMER DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchGroomerDetails();

                /* CHECK THE MASTER KENNEL ENQUIRY */
                checkMasterEnquiry();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
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

    /***** GET THE USER'S DETAILS *****/
    private void fetchUserDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchUserDetails(USER_ID);
        call.enqueue(new retrofit2.Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    USER_NAME = data.getUserName();
                    USER_DISPLAY_PROFILE = data.getUserDisplayProfile();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
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

        /* SET THE ADAPTER */
        listMessages.setAdapter(new GroomerEnquiryAdapter(GroomerEnquiryActivity.this, arrMessages));
    }
}