package biz.zenpets.users.details.adoptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.adoptions.promote.PromoteAdoptionActivity;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.adoptions.AdoptionsImagesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.adoptions.adoption.AdoptionsAPI;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImage;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImages;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImagesAPI;
import biz.zenpets.users.utils.models.adoptions.messages.AdoptionMessage;
import biz.zenpets.users.utils.models.adoptions.messages.AdoptionMessages;
import biz.zenpets.users.utils.models.adoptions.messages.AdoptionMessagesAPI;
import biz.zenpets.users.utils.models.adoptions.notifications.AdoptionNotification;
import biz.zenpets.users.utils.models.adoptions.notifications.AdoptionNotificationAPI;
import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;
import biz.zenpets.users.utils.models.adoptions.promotion.PromotionAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestUserAdoptionDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING ADOPTION ID **/
    private String ADOPTION_ID = null;

    /** THE LOGGED IN / POSTER'S USER ID **/
    private String POSTER_ID = null;

    /** AN ARRAY LIST INSTANCE TO HOLD THE LIST OF USERS PARTICIPATING IN THE ADOPTION LISTING **/
    private ArrayList<AdoptionMessage> arrUsers = new ArrayList<>();

    /** THE ADOPTION DETAILS DATA **/
    private String PET_TYPE_ID = null;
    private String PET_TYPE_NAME = null;
    private String BREED_ID = null;
    private String BREED_NAME = null;
    private String USER_ID = null;
    private String USER_NAME = null;
    private String USER_DISPLAY_PROFILE = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
    private String ADOPTION_NAME = null;
    private String ADOPTION_COVER_PHOTO = null;
    private String ADOPTION_DESCRIPTION = null;
    private String ADOPTION_GENDER = null;
    private String ADOPTION_TIMESTAMP = null;
    private String ADOPTION_STATUS = null;

    /** THE ADOPTION MESSAGE **/
    private String ADOPTION_MESSAGE = null;

    /** THE PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** THE ADOPTION IMAGES ARRAY LIST **/
    private ArrayList<AdoptionImage> arrImages = new ArrayList<>();

    /** THE ADOPTION MESSAGES ARRAY LIST **/
    private ArrayList<AdoptionMessage> arrMessages = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwAdoptionCover) SimpleDraweeView imgvwAdoptionCover;
    @BindView(R.id.txtAdoptionName) TextView txtAdoptionName;
    @BindView(R.id.imgvwGender) IconicsImageView imgvwGender;
    @BindView(R.id.imgvwOptions) IconicsImageView imgvwOptions;
    @BindView(R.id.txtAdoptionTimeStamp) TextView txtAdoptionTimeStamp;
    @BindView(R.id.txtAdoptionBreed) TextView txtAdoptionBreed;
    @BindView(R.id.txtAdoptionDescription) TextView txtAdoptionDescription;
    @BindView(R.id.linlaAdoptionImages) LinearLayout linlaAdoptionImages;
    @BindView(R.id.listAdoptionImages) RecyclerView listAdoptionImages;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listMessages) RecyclerView listMessages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.edtComment) AppCompatEditText edtComment;

    /** SHOW THE ADOPTION OPTIONS **/
    @OnClick(R.id.imgvwOptions) void showOptions()  {
        PopupMenu pm = new PopupMenu(TestUserAdoptionDetails.this, imgvwOptions);
        pm.getMenuInflater().inflate(R.menu.pm_adoption_details_options, pm.getMenu());
        if (ADOPTION_STATUS.equalsIgnoreCase("Adopted"))   {
            pm.getMenu().findItem(R.id.menuStatus).setEnabled(false);
            pm.getMenu().findItem(R.id.menuPromote).setEnabled(false);
        } else {
            pm.getMenu().findItem(R.id.menuStatus).setEnabled(true);
            pm.getMenu().findItem(R.id.menuPromote).setEnabled(true);
        }
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())   {
                    case R.id.menuEdit:
                        break;
                    case R.id.menuStatus:
                        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
                        final ProgressDialog dialogStatus = new ProgressDialog(TestUserAdoptionDetails.this);
                        dialogStatus.setMessage("Updating the Adoption status....");
                        dialogStatus.setIndeterminate(false);
                        dialogStatus.setCancelable(false);
                        dialogStatus.show();

                        AdoptionsAPI apiStatus = ZenApiClient.getClient().create(AdoptionsAPI.class);
                        Call<Adoption> callStatus = apiStatus.changeAdoptionStatus(ADOPTION_ID, "Adopted");
                        callStatus.enqueue(new Callback<Adoption>() {
                            @Override
                            public void onResponse(Call<Adoption> call, Response<Adoption> response) {
                                if (response.isSuccessful())    {
                                    ADOPTION_STATUS = "Adopted";
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "The Adoption was updated successfully...",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Status update error...",
                                            Toast.LENGTH_SHORT).show();
                                }

                                /* DISMISS THE DIALOG */
                                dialogStatus.dismiss();
                            }

                            @Override
                            public void onFailure(Call<Adoption> call, Throwable t) {
                                Log.e("STATUS FAILURE", t.getMessage());
                                Crashlytics.logException(t);
                            }
                        });
                        break;
                    case R.id.menuPromote:
                        /* CHECK IF THE ADOPTION IS BEING PROMOTED */
                        PromotionAPI api = ZenApiClient.getClient().create(PromotionAPI.class);
                        Call<Promotion> call = api.promotionExists(ADOPTION_ID);
                        call.enqueue(new Callback<Promotion>() {
                            @Override
                            public void onResponse(Call<Promotion> call, Response<Promotion> response) {
                                Promotion body = response.body();
                                if (body != null)   {
                                    String message = body.getMessage();
                                    if (message != null)    {
                                        if (message.equalsIgnoreCase("Promotion record exists..."))   {
                                            Toast.makeText(getApplicationContext(), "This Adoption listing is already being promoted...", Toast.LENGTH_LONG).show();
                                        } else if (message.equalsIgnoreCase("Promotion record doesn't exist..."))    {
                                            Intent intentPromote = new Intent(TestUserAdoptionDetails.this, PromoteAdoptionActivity.class);
                                            intentPromote.putExtra("ADOPTION_ID", ADOPTION_ID);
                                            intentPromote.putExtra("ADOPTION_NAME", ADOPTION_NAME);
                                            intentPromote.putExtra("ADOPTION_COVER_PHOTO", ADOPTION_COVER_PHOTO);
                                            startActivity(intentPromote);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Promotion> call, Throwable t) {
//                                        Log.e("CHECK FAILURE", t.getMessage());
                                Crashlytics.logException(t);
                            }
                        });
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        pm.show();
    }

    /** PUBLISH A NEW MESSAGE **/
    @OnClick(R.id.imgbtnComment) void newMessage()  {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
        }

        /* COLLECT THE ADOPTION MESSAGE */
        ADOPTION_MESSAGE = edtComment.getText().toString().trim();

        /* VALIDATE THE MESSAGE IS NOT NULL */
        if (TextUtils.isEmpty(ADOPTION_MESSAGE)) {
            edtComment.setError("Please type a message....");
            edtComment.requestFocus();
        } else  {
            /* PUBLISH THE ADOPTION MESSAGE */
            postMessage();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_adoption_details_new);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN / POSTER'S USER ID */
        POSTER_ID = getApp().getUserID();
//        Log.e("USER (POSTER) ID", POSTER_ID);

        /* GE THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /***** FETCH THE ADOPTION DETAILS *****/
    private void fetchAdoptionDetails() {
        /* SHOW THE PROGRESS DIALOG WHILE FETCHING THE ADOPTION DETAILS **/
        dialog = new ProgressDialog(TestUserAdoptionDetails.this);
        dialog.setMessage("Fetching the adoption details....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoption> call = api.fetchTestAdoptionDetails(ADOPTION_ID);
        call.enqueue(new Callback<Adoption>() {
            @Override
            public void onResponse(Call<Adoption> call, Response<Adoption> response) {
                Adoption data = response.body();
                if (data != null)   {
                    /* GET THE PET TYPE ID */
                    PET_TYPE_ID = data.getPetTypeID();

                    /* GET THE PET TYPE NAME */
                    PET_TYPE_NAME = data.getPetTypeName();

                    /* GET THE BREED ID */
                    BREED_ID = data.getBreedID();

                    /* SET THE BREED NAME */
                    BREED_NAME = data.getBreedName();

                    /* SET THE USER ID */
                    USER_ID = data.getUserID();

                    /* SET THE USER NAME */
                    USER_NAME = data.getUserName();

                    /* GET THE USER'S DISPLAY PROFILE */
                    USER_DISPLAY_PROFILE = data.getUserDisplayProfile();

                    /* GET THE CITY ID */
                    CITY_ID = data.getCityID();

                    /* GET THE CITY NAME */
                    CITY_NAME = data.getCityName();

                    /* SET THE ADOPTION COVER PHOTO */
                    ADOPTION_COVER_PHOTO = data.getAdoptionCoverPhoto();
                    if (ADOPTION_COVER_PHOTO != null
                            && !ADOPTION_COVER_PHOTO.equalsIgnoreCase("")
                            && !ADOPTION_COVER_PHOTO.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(ADOPTION_COVER_PHOTO);
                        imgvwAdoptionCover.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        imgvwAdoptionCover.setImageURI(request.getSourceUri());
                    }

                    /* GET THE ADOPTION NAME */
                    ADOPTION_NAME = data.getAdoptionName();
                    if (!ADOPTION_NAME.equalsIgnoreCase("null") && !ADOPTION_NAME.equalsIgnoreCase("")) {
                        txtAdoptionName.setText(ADOPTION_NAME);
                    } else {
                        txtAdoptionName.setText(getString(R.string.adoption_details_unnamed));
                    }

                    /* GET THE ADOPTION DESCRIPTION */
                    ADOPTION_DESCRIPTION = data.getAdoptionDescription();
                    txtAdoptionDescription.setText(ADOPTION_DESCRIPTION);

                    /* SET THE PET'S GENDER */
                    if (data.getAdoptionGender().equalsIgnoreCase("male"))  {
                        imgvwGender.setIcon("faw-mars");
                        imgvwGender.setColor(ContextCompat.getColor(TestUserAdoptionDetails.this, android.R.color.holo_blue_dark));
                    } else if (data.getAdoptionGender().equalsIgnoreCase("female")) {
                        imgvwGender.setIcon("faw-venus");
                        imgvwGender.setColor(ContextCompat.getColor(TestUserAdoptionDetails.this, android.R.color.holo_red_dark));
                    }

                    /* SET THE PET'S BREED */
                    if (data.getBreedName() != null)    {
                        txtAdoptionBreed.setText(data.getBreedName());
                    }

                    /* GET THE TIME STAMP */
                    String adoptionTimeStamp = data.getAdoptionTimeStamp();
                    long lngTimeStamp = Long.parseLong(adoptionTimeStamp) * 1000;

                    /* GET THE PRETTY DATE */
                    Calendar calPretty = Calendar.getInstance(Locale.getDefault());
                    calPretty.setTimeInMillis(lngTimeStamp);
                    Date date = calPretty.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    String strPrettyDate = prettyTime.format(date);
                    txtAdoptionTimeStamp.setText(getString(R.string.adoption_details_posted_new, strPrettyDate));

                    /* GET THE ADOPTION STATUS */
                    ADOPTION_STATUS = data.getAdoptionStatus();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Adoption details error",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

                /* FETCH THE ADOPTION IMAGES */
                fetchAdoptionImages();
            }

            @Override
            public void onFailure(Call<Adoption> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE ADOPTION IMAGES *****/
    private void fetchAdoptionImages() {
        AdoptionImagesAPI api = ZenApiClient.getClient().create(AdoptionImagesAPI.class);
        Call<AdoptionImages> call = api.fetchAdoptionImages(ADOPTION_ID);
        call.enqueue(new Callback<AdoptionImages>() {
            @Override
            public void onResponse(Call<AdoptionImages> call, Response<AdoptionImages> response) {
                if (response.isSuccessful() && response.body().getImages() != null)    {
                    arrImages = response.body().getImages();

                    /* CHECK FOR ARRAY SIZE */
                    if (arrImages.size() > 0 && arrImages != null)  {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listAdoptionImages.setAdapter(new AdoptionsImagesAdapter(TestUserAdoptionDetails.this, arrImages));

                        /* SHOW THE RECYCLER VIEW */
                        listAdoptionImages.setVisibility(View.VISIBLE);
                        linlaAdoptionImages.setVisibility(View.VISIBLE);
                    } else {
                        /* HIDE THE RECYCLER VIEW */
                        listAdoptionImages.setVisibility(View.GONE);
                        linlaAdoptionImages.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE RECYCLER VIEW */
                    listAdoptionImages.setVisibility(View.GONE);
                    linlaAdoptionImages.setVisibility(View.GONE);
                }

                /* FETCH THE ADOPTION MESSAGES */
                fetchAdoptionMessages();
            }

            @Override
            public void onFailure(Call<AdoptionImages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE ADOPTION MESSAGES *****/
    private void fetchAdoptionMessages() {
        AdoptionMessagesAPI api = ZenApiClient.getClient().create(AdoptionMessagesAPI.class);
        Call<AdoptionMessages> call = api.fetchAdoptionMessages(ADOPTION_ID);
        call.enqueue(new Callback<AdoptionMessages>() {
            @Override
            public void onResponse(Call<AdoptionMessages> call, Response<AdoptionMessages> response) {
//                Log.e("ADOPTION MESSAGES", String.valueOf(response.raw()));
                if (response.isSuccessful() && response.body().getMessages() != null)    {
                    arrMessages = response.body().getMessages();

                    /* CHECK FOR ARRAY SIZE */
                    if (arrMessages.size() > 0 && arrMessages != null)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY MESSAGE VIEW */
                        listMessages.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listMessages.setAdapter(new AdoptionMessagesAdapter(arrMessages));
                    } else {
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listMessages.setVisibility(View.GONE);
                    }
                } else {
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listMessages.setVisibility(View.GONE);
                }

                /* DISMISS THE DIALOG */
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<AdoptionMessages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("ADOPTION_ID")) {
            ADOPTION_ID = bundle.getString("ADOPTION_ID");
            if (ADOPTION_ID != null) {
                /* FETCH THE ADOPTION DETAILS */
                fetchAdoptionDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void configRecycler() {
        /* SET THE ADOPTION IMAGES CONFIGURATION */
        LinearLayoutManager images = new LinearLayoutManager(this);
        images.setOrientation(LinearLayoutManager.HORIZONTAL);
        images.setAutoMeasureEnabled(true);
        listAdoptionImages.setLayoutManager(images);
        listAdoptionImages.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listAdoptionImages.setAdapter(new AdoptionsImagesAdapter(TestUserAdoptionDetails.this, arrImages));

        /* SET THE ADOPTION MESSAGE CONFIGURATION */
        LinearLayoutManager messages = new LinearLayoutManager(this);
        messages.setOrientation(LinearLayoutManager.VERTICAL);
        messages.setAutoMeasureEnabled(true);
        listMessages.setLayoutManager(messages);
        listMessages.setHasFixedSize(true);
        listMessages.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        listMessages.setAdapter(new AdoptionMessagesAdapter(arrMessages));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Adoption Details";
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

    /***** PUBLISH THE ADOPTION MESSAGE *****/
    private void postMessage() {
        final ProgressDialog dialogMessage = new ProgressDialog(this);
        dialog.setMessage("Posting your message....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        AdoptionMessagesAPI api = ZenApiClient.getClient().create(AdoptionMessagesAPI.class);
        Call<AdoptionMessage> call = api.newAdoptionMessage(
                ADOPTION_ID, POSTER_ID, ADOPTION_MESSAGE, timeStamp
        );
        call.enqueue(new Callback<AdoptionMessage>() {
            @Override
            public void onResponse(Call<AdoptionMessage> call, Response<AdoptionMessage> response) {
                if (response.isSuccessful())    {
                    /* FETCH A LIST OF PARTICIPATING USERS */
                    fetchUsersList();
                    Toast.makeText(getApplicationContext(), "Message successfully posted", Toast.LENGTH_SHORT).show();
                    edtComment.setText("");
                    arrMessages.clear();

                    /* FETCH THE ADOPTION MESSAGES AGAIN */
                    fetchAdoptionMessages();
                } else {
                    Toast.makeText(getApplicationContext(), "There was a problem posting your message....", Toast.LENGTH_SHORT).show();
                }

                /* DISMISS THE DIALOG */
                dialogMessage.dismiss();
            }

            @Override
            public void onFailure(Call<AdoptionMessage> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH A LIST OF PARTICIPATING USERS **/
    private void fetchUsersList() {
        final AdoptionNotificationAPI api = ZenApiClient.getClient().create(AdoptionNotificationAPI.class);
        Call<AdoptionMessages> call = api.fetchAdoptionParticipants(ADOPTION_ID, POSTER_ID);
        call.enqueue(new Callback<AdoptionMessages>() {
            @Override
            public void onResponse(Call<AdoptionMessages> call, Response<AdoptionMessages> response) {
                arrUsers = response.body().getMessages();
                if (arrUsers.size() > 0)    {
                    /* SEND A NOTIFICATION */
                    for (int i = 0; i < arrUsers.size(); i++) {
                        Call<AdoptionNotification> notificationCall = api.sendAdoptionReplyNotification(
                                arrUsers.get(i).getUserToken(), "New reply from " + USER_NAME, ADOPTION_MESSAGE,
                                "Adoption", ADOPTION_ID, USER_ID, USER_NAME, USER_DISPLAY_PROFILE
                        );
                        notificationCall.enqueue(new Callback<AdoptionNotification>() {
                            @Override
                            public void onResponse(Call<AdoptionNotification> call, Response<AdoptionNotification> response) {
                            }

                            @Override
                            public void onFailure(Call<AdoptionNotification> call, Throwable t) {
//                                Log.e("ADOPTION PUSH FAILURE", t.getMessage());
                                Crashlytics.logException(t);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<AdoptionMessages> call, Throwable t) {
//                Log.e("USERS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** THE ADOPTION MESSAGES ADAPTER *****/
    private class AdoptionMessagesAdapter extends RecyclerView.Adapter<AdoptionMessagesAdapter.MessagesVH> {

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<AdoptionMessage> arrMessages;

        AdoptionMessagesAdapter(ArrayList<AdoptionMessage> arrMessages) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrMessages = arrMessages;
        }

        @Override
        public int getItemCount() {
            return arrMessages.size();
        }

        @Override
        public void onBindViewHolder(@NonNull AdoptionMessagesAdapter.MessagesVH holder, final int position) {
            AdoptionMessage data = arrMessages.get(position);

            /* CHECK IF THE USER IS ALSO THE MESSAGE POSTER */
            String userID = data.getUserID();

            if (userID.equals(POSTER_ID))    {
                /* SHOW THE OUTGOING MESSAGE CONTAINER AND HIDE THE INCOMING MESSAGE CONTAINER */
                holder.linlaOutgoing.setVisibility(View.VISIBLE);
                holder.linlaIncoming.setVisibility(View.GONE);

                /* SET THE INCOMING USER'S DISPLAY PROFILE */
                String strUserDisplayProfile = data.getUserDisplayProfile();
                if (strUserDisplayProfile != null)    {
                    Uri uri = Uri.parse(strUserDisplayProfile);
                    holder.imgvwOutgoingProfile.setImageURI(uri);
                } else {
                    ImageRequest request = ImageRequestBuilder
                            .newBuilderWithResourceId(R.drawable.ic_action_user_light)
                            .build();
                    holder.imgvwOutgoingProfile.setImageURI(request.getSourceUri());
                }

                /* SET THE USER'S NAME */
                String strUserName = data.getUserName();
                if (strUserName != null)    {
                    holder.txtOutgoingUserName.setText(strUserName);
                }

                /* SET THE MESSAGE TEXT */
                if (data.getMessageText() != null)  {
                    holder.txtOutgoingMessage.setText(data.getMessageText());
                }

                /* SET THE TIME STAMP */
                if (data.getMessageTimeStamp() != null) {
                    String messageTimeStamp = data.getMessageTimeStamp();
                    long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    holder.txtOutgoingTimeStamp.setText(prettyTime.format(date));
                }
            } else {
                /* SHOW THE INCOMING MESSAGE CONTAINER AND HIDE THE OUTGOING MESSAGE CONTAINER */
                holder.linlaIncoming.setVisibility(View.VISIBLE);
                holder.linlaOutgoing.setVisibility(View.GONE);

                /* SET THE MESSAGE TEXT */
                if (data.getMessageText() != null)  {
                    holder.txtIncomingMessage.setText(data.getMessageText());
                }

                /* SET THE INCOMING USER'S DISPLAY PROFILE */
                String strUserDisplayProfile = data.getUserDisplayProfile();
                if (strUserDisplayProfile != null)    {
                    Uri uri = Uri.parse(strUserDisplayProfile);
                    holder.imgvwIncomingProfile.setImageURI(uri);
                } else {
                    ImageRequest request = ImageRequestBuilder
                            .newBuilderWithResourceId(R.drawable.ic_action_user_light)
                            .build();
                    holder.imgvwIncomingProfile.setImageURI(request.getSourceUri());
                }

                /* SET THE USER'S NAME */
                String strUserName = data.getUserName();
                if (strUserName != null)    {
                    holder.txtIncomingUserName.setText(strUserName);
                }

                /* SET THE TIME STAMP */
                if (data.getMessageTimeStamp() != null) {
                    String messageTimeStamp = data.getMessageTimeStamp();
                    long lngTimeStamp = Long.parseLong(messageTimeStamp) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    holder.txtIncomingTimeStamp.setText(prettyTime.format(date));
                }
            }
        }

        @NonNull
        @Override
        public AdoptionMessagesAdapter.MessagesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.adoption_message_item_new, parent, false);

            return new AdoptionMessagesAdapter.MessagesVH(itemView);
        }

        class MessagesVH extends RecyclerView.ViewHolder	{

            final LinearLayout linlaIncoming;
            final SimpleDraweeView imgvwIncomingProfile;
            final AppCompatTextView txtIncomingMessage;
            final AppCompatTextView txtIncomingUserName;
            final AppCompatTextView txtIncomingTimeStamp;
            final LinearLayout linlaOutgoing;
            final SimpleDraweeView imgvwOutgoingProfile;
            final AppCompatTextView txtOutgoingMessage;
            final AppCompatTextView txtOutgoingUserName;
            final AppCompatTextView txtOutgoingTimeStamp;

            MessagesVH(View v) {
                super(v);

                linlaIncoming = v.findViewById(R.id.linlaIncoming);
                imgvwIncomingProfile = v.findViewById(R.id.imgvwIncomingProfile);
                txtIncomingMessage = v.findViewById(R.id.txtIncomingMessage);
                txtIncomingUserName = v.findViewById(R.id.txtIncomingUserName);
                txtIncomingTimeStamp = v.findViewById(R.id.txtIncomingTimeStamp);
                linlaOutgoing = v.findViewById(R.id.linlaOutgoing);
                imgvwOutgoingProfile = v.findViewById(R.id.imgvwOutgoingProfile);
                txtOutgoingMessage = v.findViewById(R.id.txtOutgoingMessage);
                txtOutgoingUserName = v.findViewById(R.id.txtOutgoingUserName);
                txtOutgoingTimeStamp = v.findViewById(R.id.txtOutgoingTimeStamp);
            }
        }
    }
}