package biz.zenpets.users.details.adoptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdoptionDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING ADOPTION ID **/
    private String ADOPTION_ID = null;

    /** THE LOGGED IN / POSTER'S USER ID **/
    private String POSTER_ID = null;

    /** THE ADOPTION DETAILS DATA **/
    private String PET_TYPE_ID = null;
    private String PET_TYPE_NAME = null;
    private String BREED_ID = null;
    private String BREED_NAME = null;
    private String USER_ID = null;
    private String USER_NAME = null;
    private String CITY_ID = null;
    private String CITY_NAME = null;
//    private String ADOPTION_NAME = null;
    private String ADOPTION_DESCRIPTION = null;
    private String ADOPTION_GENDER = null;
    private String ADOPTION_VACCINATION = null;
    private String ADOPTION_DEWORMED = null;
    private String ADOPTION_NEUTERED = null;
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
//    @BindView(R.id.txtAdoptionName) AppCompatTextView txtAdoptionName;
    @BindView(R.id.imgvwGender) IconicsImageView imgvwGender;
    @BindView(R.id.txtAdoptionDescription) AppCompatTextView txtAdoptionDescription;
    @BindView(R.id.txtNoImages) AppCompatTextView txtNoImages;
    @BindView(R.id.listAdoptionImages) RecyclerView listAdoptionImages;
    @BindView(R.id.txtPetDetails) AppCompatTextView txtPetDetails;
    @BindView(R.id.txtTimeStamp) AppCompatTextView txtTimeStamp;
    @BindView(R.id.txtVaccinated) AppCompatTextView txtVaccinated;
    @BindView(R.id.txtDewormed) AppCompatTextView txtDewormed;
    @BindView(R.id.txtNeutered) AppCompatTextView txtNeutered;
//    @BindView(R.id.txtAdopted) AppCompatTextView txtAdopted;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listMessages) RecyclerView listMessages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.edtComment) AppCompatEditText edtComment;

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
        setContentView(R.layout.adoption_details);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN / POSTER'S USER ID */
        POSTER_ID = getApp().getUserID();

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
        dialog = new ProgressDialog(AdoptionDetails.this);
        dialog.setMessage("Fetching the adoption details....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        AdoptionsAPI api = ZenApiClient.getClient().create(AdoptionsAPI.class);
        Call<Adoption> call = api.fetchAdoptionDetails(ADOPTION_ID);
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

                    /* SET THE PET DETAILS */
                    if (BREED_NAME != null  && PET_TYPE_NAME != null)   {
                        String breed = data.getBreedName();
                        String petType = data.getPetTypeName();
                        String combinedDetails = "Species: \"" + petType + "\" | Breed: \"" + breed + "\"";
                        txtPetDetails.setText(combinedDetails);
                    }

                    /* SET THE USER ID */
                    USER_ID = data.getUserID();

                    /* SET THE USER NAME */
                    USER_NAME = data.getUserName();

                    /* GET THE CITY ID */
                    CITY_ID = data.getCityID();

                    /* GET THE CITY NAME */
                    CITY_NAME = data.getCityName();

//                    /* GET THE ADOPTION NAME */
//                    ADOPTION_NAME = data.getAdoptionName();
//                    if (!ADOPTION_NAME.equalsIgnoreCase("null") && !ADOPTION_NAME.equalsIgnoreCase("")) {
//                        txtAdoptionName.setText(ADOPTION_NAME);
//                    } else {
//                        txtAdoptionName.setText(getString(R.string.adoption_details_unnamed));
//                    }

                    /* GET THE ADOPTION DESCRIPTION */
                    ADOPTION_DESCRIPTION = data.getAdoptionDescription();
                    txtAdoptionDescription.setText(ADOPTION_DESCRIPTION);

                    /* GET THE GENDER */
                    ADOPTION_GENDER = data.getAdoptionGender();
                    if (ADOPTION_GENDER.equalsIgnoreCase("male"))  {
                        imgvwGender.setIcon("faw-mars");
                        imgvwGender.setColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_blue_dark));
                    } else if (ADOPTION_GENDER.equalsIgnoreCase("female")) {
                        imgvwGender.setIcon("faw-venus");
                        imgvwGender.setColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_red_dark));
                    }

                    /* GET THE TIME STAMP */
                    String adoptionTimeStamp = data.getAdoptionTimeStamp();
//                    Log.e("TS", adoptionTimeStamp);
                    long lngTimeStamp = Long.parseLong(adoptionTimeStamp) * 1000;

                    /* GET THE PRETTY DATE */
                    Calendar calPretty = Calendar.getInstance(Locale.getDefault());
                    calPretty.setTimeInMillis(lngTimeStamp);
                    Date date = calPretty.getTime();
                    PrettyTime prettyTime = new PrettyTime();
                    String strPrettyDate = prettyTime.format(date);

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Date currentTimeZone = calendar.getTime();
                    String strDate = sdf.format(currentTimeZone);
                    txtTimeStamp.setText(getString(R.string.adoption_details_posted, strDate, strPrettyDate));

//                    /* GET THE VACCINATION STATUS */
//                    ADOPTION_VACCINATION = data.getAdoptionVaccinated();
//                    txtVaccinated.setText(ADOPTION_VACCINATION);
//                    if (ADOPTION_VACCINATION.equalsIgnoreCase("yes"))  {
//                        txtVaccinated.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_green_dark));
//                    } else if (ADOPTION_VACCINATION.equalsIgnoreCase("no"))    {
//                        txtVaccinated.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_red_dark));
//                    }

//                    /* GET THE DEWORMED STATUS */
//                    ADOPTION_DEWORMED = data.getAdoptionDewormed();
//                    txtDewormed.setText(ADOPTION_DEWORMED);
//                    if (ADOPTION_DEWORMED.equalsIgnoreCase("yes")) {
//                        txtDewormed.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_green_dark));
//                    } else if (ADOPTION_DEWORMED.equalsIgnoreCase("no"))   {
//                        txtDewormed.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_red_dark));
//                    }

//                    /* GET THE NEUTERED STATUS */
//                    ADOPTION_NEUTERED = data.getAdoptionNeutered();
//                    txtNeutered.setText(ADOPTION_NEUTERED);
//                    if (ADOPTION_NEUTERED.equalsIgnoreCase("yes")) {
//                        txtNeutered.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_green_dark));
//                    } else if (ADOPTION_NEUTERED.equalsIgnoreCase("no"))   {
//                        txtNeutered.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_red_dark));
//                    }

//                    /* GET THE ADOPTION STATUS */
//                    ADOPTION_STATUS = data.getAdoptionStatus();
//                    txtAdopted.setText(ADOPTION_STATUS);
//                    if (ADOPTION_STATUS.equalsIgnoreCase("Open"))   {
//                        txtAdopted.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_green_dark));
//                    } else if (ADOPTION_STATUS.equalsIgnoreCase("Adopted")) {
//                        txtAdopted.setTextColor(ContextCompat.getColor(AdoptionDetails.this, android.R.color.holo_red_dark));
//                    }
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
                        listAdoptionImages.setAdapter(new AdoptionsImagesAdapter(AdoptionDetails.this, arrImages));

                        /* SHOW THE RECYCLER VIEW */
                        listAdoptionImages.setVisibility(View.VISIBLE);
                        txtNoImages.setVisibility(View.GONE);
                    } else {
                        /* HIDE THE RECYCLER VIEW */
                        listAdoptionImages.setVisibility(View.GONE);
                        txtNoImages.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE RECYCLER VIEW */
                    listAdoptionImages.setVisibility(View.GONE);
                    txtNoImages.setVisibility(View.GONE);
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
        listAdoptionImages.setAdapter(new AdoptionsImagesAdapter(AdoptionDetails.this, arrImages));

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
        public void onBindViewHolder(MessagesVH holder, final int position) {
            AdoptionMessage data = arrMessages.get(position);

            /* CHECK IF THE USER IS ALSO THE MESSAGE POSTER */
            String userID = data.getUserID();

            if (userID.equals(USER_ID))    {
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

        @Override
        public MessagesVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.adoption_message_item_new, parent, false);

            return new MessagesVH(itemView);
        }

        class MessagesVH extends RecyclerView.ViewHolder	{

            final LinearLayout linlaIncoming;
            SimpleDraweeView imgvwIncomingProfile;
            final AppCompatTextView txtIncomingMessage;
            final AppCompatTextView txtIncomingUserName;
            final AppCompatTextView txtIncomingTimeStamp;
            final LinearLayout linlaOutgoing;
            SimpleDraweeView imgvwOutgoingProfile;
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