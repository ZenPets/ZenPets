package co.zenpets.kennels.enquiry;

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
import android.widget.Toast;

import java.util.ArrayList;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.adapters.enquiries.KennelMessagesAdapter;
import co.zenpets.kennels.utils.models.enquiries.EnquiryMessage;
import co.zenpets.kennels.utils.models.enquiries.EnquiryMessages;
import co.zenpets.kennels.utils.models.enquiries.EnquiryMessagesAPI;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.kennels.Kennel;
import co.zenpets.kennels.utils.models.kennels.KennelsAPI;
import co.zenpets.kennels.utils.models.notifications.Notification;
import co.zenpets.kennels.utils.models.notifications.NotificationsAPI;
import co.zenpets.kennels.utils.models.users.UserData;
import co.zenpets.kennels.utils.models.users.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelEnquiryActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL OWNER'S ID **/
    String KENNEL_OWNER_ID = null;

    /** THE INCOMING KENNEL ID AND ENQUIRY ID **/
    String KENNEL_ID = null;
    String ENQUIRY_ID = null;

    /** THE KENNEL NAME AND COVER PHOTO **/
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;

    /** THE MESSAGES ARRAY LIST **/
    private ArrayList<EnquiryMessage> arrMessages = new ArrayList<>();

    /** THE USER'S ID AND TOKEN **/
    private String USER_ID = null;
    private String USER_TOKEN = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
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
            /* PUBLISH THE KENNEL OWNER'S REPLY */
            publishReply(ENQUIRY_MESSAGE, timeStamp);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_enquiry_list);
        ButterKnife.bind(this);

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();
//        Log.e("OWNER ID", KENNEL_OWNER_ID);

        /* GET THE KENNEL DETAILS */
        fetchKennelDetails();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /** FETCH THE KENNEL ENQUIRY MESSAGES **/
    private void fetchEnquiryMessages() {
        arrMessages.clear();
        EnquiryMessagesAPI apiEnquiry = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
        Call<EnquiryMessages> call = apiEnquiry.fetchKennelEnquiryMessages(KENNEL_ID);
        call.enqueue(new Callback<EnquiryMessages>() {
            @Override
            public void onResponse(Call<EnquiryMessages> call, Response<EnquiryMessages> response) {
//                Log.e("ENQUIRY RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getMessages() != null)   {
                    arrMessages = response.body().getMessages();
                    if (arrMessages.size() > 0) {

                        /* SET THE ADAPTER */
                        listMessages.setAdapter(new KennelMessagesAdapter(KennelEnquiryActivity.this, arrMessages));

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
                linlaProgress.setVisibility(View.GONE);

                /* FETCH THE USER'S TOKEN */
                fetchUserToken();
            }

            @Override
            public void onFailure(Call<EnquiryMessages> call, Throwable t) {
//                Log.e("MESSAGES FAILURE", t.getMessage());
            }
        });
    }

    /** FETCH THE USER'S TOKEN **/
    private void fetchUserToken() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchUserDetails(USER_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
//                Log.e("RAW", String.valueOf(response.raw()));
                UserData data = response.body();
                if (data != null)    {
                    /* GET THE USER'S TOKEN */
                    USER_TOKEN = data.getUserToken();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("KENNEL_ID")
                && bundle.containsKey("ENQUIRY_ID")) {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            ENQUIRY_ID = bundle.getString("ENQUIRY_ID");
            if (KENNEL_ID != null && ENQUIRY_ID != null)    {
                /* SHOW THE PROGRESS AND FETCH THE KENNEL DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** FETCH THE KENNEL DETAILS **/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.fetchKennelDetails(KENNEL_ID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                if (response.body() != null)    {
                    Kennel kennel = response.body();

                    KENNEL_NAME = kennel.getKennelName();
                    KENNEL_COVER_PHOTO = kennel.getKennelCoverPhoto();

                    /* FETCH THE KENNEL ENQUIRY MESSAGES */
                    fetchEnquiryMessages();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("KENNEL DETAILS FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        manager.setStackFromEnd(true);
        listMessages.setLayoutManager(manager);
        listMessages.setHasFixedSize(true);
        listMessages.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(listMessages, false);

        /* SET THE ADAPTER */
        listMessages.setAdapter(new KennelMessagesAdapter(KennelEnquiryActivity.this, arrMessages));
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

    /** PUBLISH THE KENNEL OWNER'S REPLY **/
    private void publishReply(final String strMessage, String strTimestamp) {
        EnquiryMessagesAPI apiEnquiry = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);
        Call<EnquiryMessage> call = apiEnquiry.newKennelEnquiryOwnerMessage(
                ENQUIRY_ID, KENNEL_ID, null, strMessage, strTimestamp);
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

    /** SEND A NOTIFICATION TO THE USER **/
    private void sendNotificationToUser(String strMessage) {
        if (USER_TOKEN != null) {
            NotificationsAPI api = ZenApiClient.getClient().create(NotificationsAPI.class);
            Call<Notification> call = api.sendKennelReplyNotification(
                    USER_TOKEN, "New reply from " + KENNEL_NAME,
                    strMessage, "Kennel Enquiry", ENQUIRY_ID,
                    KENNEL_ID, KENNEL_NAME, KENNEL_COVER_PHOTO, KENNEL_OWNER_ID);
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
}