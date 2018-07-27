package biz.zenpets.users.details.kennels.enquiry;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.kennels.enquiry.KennelEnquiryAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.enquiry.EnquiriesAPI;
import biz.zenpets.users.utils.models.kennels.enquiry.Enquiry;
import biz.zenpets.users.utils.models.kennels.enquiry.EnquiryMessage;
import biz.zenpets.users.utils.models.kennels.enquiry.EnquiryMessages;
import biz.zenpets.users.utils.models.kennels.enquiry.EnquiryMessagesAPI;
import biz.zenpets.users.utils.models.kennels.kennels.Account;
import biz.zenpets.users.utils.models.kennels.kennels.KennelsAPI;
import biz.zenpets.users.utils.models.kennels.notifications.Notification;
import biz.zenpets.users.utils.models.kennels.notifications.NotificationsAPI;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelEnquiryActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING KENNEL ID, NAME, COVER PHOTO AND THE MASTER ENQUIRY ID **/
    String KENNEL_ID = null;
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;
    String ENQUIRY_ID = null;

    /** THE LOGGED IN USER'S ID, NAME AND DISPLAY PROFILE **/
    private String USER_ID = null;
    private String USER_NAME = null;
    private String USER_DISPLAY_PROFILE = null;

    /** THE KENNEL OWNER'S ID, NAME, DISPLAY PROFILE AND DEVICE TOKEN **/
    String KENNEL_OWNER_ID = null;
    String KENNEL_OWNER_NAME = null;
    String KENNEL_OWNER_DISPLAY_PROFILE = null;
    String KENNEL_OWNER_TOKEN = null;

    /** AN ENQUIRY MESSAGES API INSTANCE **/
    EnquiryMessagesAPI api = ZenApiClient.getClient().create(EnquiryMessagesAPI.class);

    /** THE MESSAGES ARRAY LIST **/
    private ArrayList<EnquiryMessage> arrMessages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwKennelCoverPhoto) ImageView imgvwKennelCoverPhoto;
    @BindView(R.id.imgvwKennelOwnerDisplayProfile) CircleImageView imgvwKennelOwnerDisplayProfile;
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.txtKennelOwnerName) TextView txtKennelOwnerName;
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
            /* PUBLISH THE KENNEL ENQUIRY MESSAGE */
            publishMessage(ENQUIRY_MESSAGE, timeStamp);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_enquiry_list);
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

    /** FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE KENNEL **/
    private void fetchEnquiryMessages() {
        Call<EnquiryMessages> call = api.fetchKennelEnquiryMessages(ENQUIRY_ID);
        call.enqueue(new Callback<EnquiryMessages>() {
            @Override
            public void onResponse(Call<EnquiryMessages> call, Response<EnquiryMessages> response) {
//                Log.e("ENQUIRY RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getMessages() != null)   {
                    arrMessages = response.body().getMessages();
                    if (arrMessages.size() > 0) {

                        /* SET THE ADAPTER */
                        listMessages.setAdapter(new KennelEnquiryAdapter(KennelEnquiryActivity.this, arrMessages));

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
                Crashlytics.logException(t);

                /* HIDE THE PROGRESS */
                linlaMessagesProgress.setVisibility(View.GONE);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("KENNEL_ID")
                && bundle.containsKey("KENNEL_NAME")
                && bundle.containsKey("KENNEL_COVER_PHOTO")
                && bundle.containsKey("ENQUIRY_ID")
                && bundle.containsKey("KENNEL_OWNER_ID")) {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_NAME = bundle.getString("KENNEL_NAME");
            KENNEL_COVER_PHOTO = bundle.getString("KENNEL_COVER_PHOTO");
            ENQUIRY_ID = bundle.getString("ENQUIRY_ID");
            KENNEL_OWNER_ID = bundle.getString("KENNEL_OWNER_ID");
            if (KENNEL_ID != null && KENNEL_NAME != null && KENNEL_COVER_PHOTO != null
                    && ENQUIRY_ID != null && KENNEL_OWNER_ID != null)    {
                /* SET THE KENNEL DETAILS */
                setKennelDetails();

                /* SHOW THE PROGRESS AND FETCH THE KENNEL DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelDetails();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE KENNEL */
                linlaMessagesProgress.setVisibility(View.VISIBLE);
                fetchEnquiryMessages();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (bundle != null
                && bundle.containsKey("KENNEL_ID")
                && bundle.containsKey("KENNEL_NAME")
                && bundle.containsKey("KENNEL_COVER_PHOTO")
                && bundle.containsKey("KENNEL_OWNER_ID"))    {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_NAME = bundle.getString("KENNEL_NAME");
            KENNEL_COVER_PHOTO = bundle.getString("KENNEL_COVER_PHOTO");
            KENNEL_OWNER_ID = bundle.getString("KENNEL_OWNER_ID");
            if (KENNEL_ID != null && KENNEL_NAME != null && KENNEL_COVER_PHOTO != null
                    && KENNEL_OWNER_ID != null) {
                /* SET THE KENNEL DETAILS */
                setKennelDetails();

                /* SHOW THE PROGRESS AND FETCH THE KENNEL DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelDetails();

                /* CHECK THE MASTER KENNEL ENQUIRY */
                checkMasterEnquiry();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** CHECK THE MASTER KENNEL ENQUIRY **/
    private void checkMasterEnquiry() {
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiry> call = api.checkMasterKennelEnquiry(KENNEL_ID, USER_ID);
        call.enqueue(new Callback<Enquiry>() {
            @Override
            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
//                Log.e("RESPONSE RAW", String.valueOf(response.raw()));
                Enquiry enquiry = response.body();
                if (enquiry != null)    {
                    Boolean blnError = enquiry.getError();
                    if (!blnError)   {
                        /* GET THE ENQUIRY ID */
                        ENQUIRY_ID = enquiry.getKennelEnquiryID();

                        /* SHOW THE PROGRESS AND FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE KENNEL */
                        linlaMessagesProgress.setVisibility(View.VISIBLE);
                        fetchEnquiryMessages();
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Enquiry> call, Throwable t) {
            }
        });
    }

    /** FETCH THE KENNEL DETAILS **/
    private void fetchKennelDetails() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Account> call = api.fetchKennelOwnerProfile(KENNEL_OWNER_ID);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Account account = response.body();
                if (account != null)    {
                    /* GET AND SET THE KENNEL OWNER'S NAME */
                    KENNEL_OWNER_NAME = account.getKennelOwnerName();
                    if (KENNEL_OWNER_NAME != null)  {
                        txtKennelOwnerName.setText(KENNEL_OWNER_NAME);
                    }

                    /* GET AND SET THE KENNEL OWNER'S DISPLAY PROFILE */
                    KENNEL_OWNER_DISPLAY_PROFILE = account.getKennelOwnerDisplayProfile();
                    if (KENNEL_OWNER_DISPLAY_PROFILE != null) {
                        Picasso.with(KennelEnquiryActivity.this)
                                .load(KENNEL_OWNER_DISPLAY_PROFILE)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .fit()
                                .centerCrop()
                                .into(imgvwKennelOwnerDisplayProfile, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(KennelEnquiryActivity.this)
                                                .load(KENNEL_OWNER_DISPLAY_PROFILE)
                                                .fit()
                                                .centerCrop()
                                                .error(R.drawable.ic_person_black_24dp)
                                                .into(imgvwKennelOwnerDisplayProfile, new com.squareup.picasso.Callback() {
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
                    }

                    /* GET THE KENNEL OWNER'S DEVICE TOKEN */
                    KENNEL_OWNER_TOKEN = account.getKennelOwnerToken();

                    /* HIDE THE PROGRESS AFTER FETCHING THE KENNEL DATA */
                    linlaProgress.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

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
        listMessages.setAdapter(new KennelEnquiryAdapter(KennelEnquiryActivity.this, arrMessages));
    }

    /***** PUBLISH THE KENNEL ENQUIRY MESSAGE *****/
    private void publishMessage(final String strMessage, String strTimestamp) {
        Call<EnquiryMessage> call = api.newKennelEnquiryUserMessage(
                ENQUIRY_ID, null, USER_ID, strMessage, strTimestamp);
        call.enqueue(new Callback<EnquiryMessage>() {
            @Override
            public void onResponse(Call<EnquiryMessage> call, Response<EnquiryMessage> response) {
                if (response.isSuccessful())    {
                    Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_SHORT).show();
                    edtMessage.setText("");
                    arrMessages.clear();

                    /* SEND A NOTIFICATION TO THE KENNEL */
                    sendNotificationToKennel(strMessage);

                    /* FETCH THE LIST OF MESSAGE BETWEEN THE USER AND THE TRAINER */
                    fetchEnquiryMessages();
                } else {
                    Toast.makeText(getApplicationContext(), "Publish failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EnquiryMessage> call, Throwable t) {
//                Log.e("PUBLISH FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** SEND A NOTIFICATION TO THE KENNEL **/
    private void sendNotificationToKennel(String strMessage) {
        if (KENNEL_OWNER_TOKEN != null)  {
//            Log.e("TOKEN", KENNEL_OWNER_TOKEN);
            NotificationsAPI api = ZenApiClient.getClient().create(NotificationsAPI.class);
            Call<Notification> call = api.sendKennelReplyNotification(
                    KENNEL_OWNER_TOKEN, "New enquiry from " + USER_NAME,
                    strMessage, "Kennel Enquiry", ENQUIRY_ID, KENNEL_ID, USER_NAME, USER_DISPLAY_PROFILE);
            call.enqueue(new Callback<Notification>() {
                @Override
                public void onResponse(Call<Notification> call, Response<Notification> response) {
//                    Log.e("RESPONSE", String.valueOf(response.raw()));
                }

                @Override
                public void onFailure(Call<Notification> call, Throwable t) {
//                    Log.e("PUSH FAILURE", t.getMessage());
                    Crashlytics.logException(t);
                }
            });
        }
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
                    USER_DISPLAY_PROFILE = data.getUserDisplayProfile();
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
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

    /** SET THE KENNEL DETAILS **/
    private void setKennelDetails() {
        /* SET THE KENNEL NAME */
        txtKennelName.setText(KENNEL_NAME);

        /* SET THE KENNEL'S COVER PHOTO */
        if (KENNEL_COVER_PHOTO != null) {
            Picasso.with(KennelEnquiryActivity.this)
                    .load(KENNEL_COVER_PHOTO)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerCrop()
                    .into(imgvwKennelCoverPhoto, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(KennelEnquiryActivity.this)
                                    .load(KENNEL_COVER_PHOTO)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_business_black_24dp)
                                    .into(imgvwKennelCoverPhoto, new com.squareup.picasso.Callback() {
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
        }
    }
}