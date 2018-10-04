package biz.zenpets.kennels.kennels;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.mikepenz.iconics.view.IconicsImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.creator.kennel.NewKennelCreator;
import biz.zenpets.kennels.details.kennel.KennelDetails;
import biz.zenpets.kennels.modifier.images.KennelImageManager;
import biz.zenpets.kennels.modifier.kennel.KennelModifier;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.TypefaceSpan;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import biz.zenpets.kennels.utils.models.kennels.KennelPages;
import biz.zenpets.kennels.utils.models.kennels.KennelsAPI;
import biz.zenpets.kennels.utils.models.test.TestKennel;
import biz.zenpets.kennels.utils.models.test.TestKennels;
import biz.zenpets.kennels.utils.models.test.TestKennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestKennelsList extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER'S ID **/
    private String KENNEL_OWNER_ID = null;

    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    ArrayList<TestKennel> arrKennels = new ArrayList<>();

    /** A LINEAR LAYOUT MANAGER INSTANCE **/
    LinearLayoutManager manager;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW KENNEL (FAB) **/
    @OnClick(R.id.fabNewKennel) void newFabKennel() {
        /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
        checkPublishedKennels();
    }

    /** ADD A NEW KENNEL (EMPTY LAYOUT) **/
    @OnClick(R.id.linlaEmpty) void newKennel()  {
        /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
        checkPublishedKennels();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_kennels_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchKennels();
    }

    /***** FETCH THE LIST OF KENNELS *****/
    private void fetchKennels() {
        TestKennelsAPI api = ZenApiClient.getClient().create(TestKennelsAPI.class);
        Call<TestKennels> call = api.fetchTestKennelsListByOwner(KENNEL_OWNER_ID);
        call.enqueue(new Callback<TestKennels>() {
            @Override
            public void onResponse(Call<TestKennels> call, Response<TestKennels> response) {
                Log.e("RAW", String.valueOf(response.raw()));
                if (response.body() != null && response.body().getKennels() != null)    {
                    arrKennels = processResult(response);
//                    arrKennels = response.body().getKennels();
                    if (arrKennels.size() > 0)  {

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listKennels.setAdapter(new KennelsAdapter(TestKennelsList.this, arrKennels));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listKennels.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennels.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennels.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TestKennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** PROCESS THE KENNEL RESULTS **/
    private ArrayList<TestKennel> processResult(Response<TestKennels> response) {
        ArrayList<TestKennel> kennels = new ArrayList<>();
        try {
            String strResult = new Gson().toJson(response.body());
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAKennels = JORoot.getJSONArray("kennels");
                if (JAKennels.length() > 0) {
                    TestKennel data;
                    for (int i = 0; i < JAKennels.length(); i++) {
                        final JSONObject JOKennels = JAKennels.getJSONObject(i);
                        data = new TestKennel();

                        /* GET THE KENNEL ID */
                        if (JOKennels.has("kennelID"))  {
                            data.setKennelID(JOKennels.getString("kennelID"));
                        } else {
                            data.setKennelID(null);
                        }

                        /* GET THE KENNEL COVER PHOTO */
                        if (JOKennels.has("kennelCoverPhoto")
                                && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("")
                                && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("null"))  {
                            data.setKennelCoverPhoto(JOKennels.getString("kennelCoverPhoto"));
                        } else {
                            data.setKennelCoverPhoto(null);
                        }

                        /* GET THE KENNEL NAME */
                        if (JOKennels.has("kennelName"))    {
                            data.setKennelName(JOKennels.getString("kennelName"));
                        } else {
                            data.setKennelName(null);
                        }

                        /* GET THE KENNEL OWNER'S ID */
                        if (JOKennels.has("kennelOwnerID")) {
                            data.setKennelOwnerID(JOKennels.getString("kennelOwnerID"));
                        } else {
                            data.setKennelOwnerID(null);
                        }

                        /* GET THE KENNEL OWNER'S NAME */
                        if (JOKennels.has("kennelOwnerName"))   {
                            data.setKennelOwnerName(JOKennels.getString("kennelOwnerName"));
                        } else {
                            data.setKennelOwnerName(null);
                        }

                        /* GET THE KENNEL OWNER'S DISPLAY PROFILE */
                        if (JOKennels.has("kennelOwnerDisplayProfile")) {
                            data.setKennelOwnerDisplayProfile(JOKennels.getString("kennelOwnerDisplayProfile"));
                        } else {
                            data.setKennelOwnerDisplayProfile(null);
                        }

                        /* GET THE KENNEL ADDRESS */
                        if (JOKennels.has("kennelAddress")) {
                            data.setKennelAddress(JOKennels.getString("kennelAddress"));
                        } else {
                            data.setKennelAddress(null);
                        }

                        /* GET THE KENNEL PIN CODE */
                        if (JOKennels.has("kennelPinCode")) {
                            data.setKennelPinCode(JOKennels.getString("kennelPinCode"));
                        } else {
                            data.setKennelPinCode(null);
                        }

                        /* GET THE KENNEL COUNTY ID */
                        if (JOKennels.has("countryID")) {
                            data.setCountryID(JOKennels.getString("countryID"));
                        } else {
                            data.setCountryID(null);
                        }

                        /* GET THE KENNEL COUNTRY NAME */
                        if (JOKennels.has("countryName"))   {
                            data.setCountryName(JOKennels.getString("countryName"));
                        } else {
                            data.setCountryName(null);
                        }

                        /* GET THE KENNEL STATE ID */
                        if (JOKennels.has("stateID"))   {
                            data.setStateID(JOKennels.getString("stateID"));
                        } else {
                            data.setStateID(null);
                        }

                        /* GET THE KENNEL STATE NAME */
                        if (JOKennels.has("stateName")) {
                            data.setStateName(JOKennels.getString("stateName"));
                        } else {
                            data.setStateName(null);
                        }

                        /* GET THE KENNEL CITY ID */
                        if (JOKennels.has("cityID"))    {
                            data.setCityID(JOKennels.getString("cityID"));
                        } else {
                            data.setCityID(null);
                        }

                        /* GET THE KENNEL CITY NAME */
                        if (JOKennels.has("cityName"))  {
                            data.setCityName(JOKennels.getString("cityName"));
                        } else {
                            data.setCityName(null);
                        }

                        /* GET THE KENNEL LATITUDE AND LONGITUDE */
                        if (JOKennels.has("kennelLatitude") && JOKennels.has("kennelLongitude"))   {
                            data.setKennelLatitude(JOKennels.getString("kennelLatitude"));
                            data.setKennelLongitude(JOKennels.getString("kennelLongitude"));
                        } else {
                            data.setKennelLatitude(null);
                            data.setKennelLongitude(null);
                        }

                        /* GET THE KENNEL'S PHONE PREFIX #1*/
                        if (JOKennels.has("kennelPhonePrefix1"))    {
                            data.setKennelPhonePrefix1(JOKennels.getString("kennelPhonePrefix1"));
                        } else {
                            data.setKennelPhonePrefix1(null);
                        }

                        /* GET THE KENNEL'S PHONE NUMBER #1*/
                        if (JOKennels.has("kennelPhoneNumber1"))    {
                            data.setKennelPhoneNumber1(JOKennels.getString("kennelPhoneNumber1"));
                        } else {
                            data.setKennelPhoneNumber1(null);
                        }

                        /* GET THE KENNEL'S PHONE PREFIX #2*/
                        if (JOKennels.has("kennelPhonePrefix2"))    {
                            data.setKennelPhonePrefix2(JOKennels.getString("kennelPhonePrefix2"));
                        } else {
                            data.setKennelPhonePrefix2(null);
                        }

                        /* GET THE KENNEL'S PHONE NUMBER #2*/
                        if (JOKennels.has("kennelPhoneNumber2"))    {
                            data.setKennelPhoneNumber2(JOKennels.getString("kennelPhoneNumber2"));
                        } else {
                            data.setKennelPhoneNumber1(null);
                        }

                        /* GET THE KENNEL'S SMALL PET CAPACITY */
                        if (JOKennels.has("kennelSmallCapacity"))    {
                            data.setKennelSmallCapacity(JOKennels.getString("kennelSmallCapacity"));
                        } else {
                            data.setKennelSmallCapacity(null);
                        }

                        /* GET THE KENNEL'S MEDIUM PET CAPACITY */
                        if (JOKennels.has("kennelMediumCapacity"))    {
                            data.setKennelMediumCapacity(JOKennels.getString("kennelMediumCapacity"));
                        } else {
                            data.setKennelMediumCapacity(null);
                        }

                        /* GET THE KENNEL'S LARGE PET CAPACITY */
                        if (JOKennels.has("kennelLargeCapacity"))    {
                            data.setKennelLargeCapacity(JOKennels.getString("kennelLargeCapacity"));
                        } else {
                            data.setKennelLargeCapacity(null);
                        }

                        /* GET THE KENNEL'S EXTRA LARGE PET CAPACITY */
                        if (JOKennels.has("kennelXLargeCapacity"))    {
                            data.setKennelXLargeCapacity(JOKennels.getString("kennelXLargeCapacity"));
                        } else {
                            data.setKennelXLargeCapacity(null);
                        }

                        /* GET THE KENNEL'S VERIFIED STATUS */
                        if (JOKennels.has("kennelVerified"))    {
                            data.setKennelVerified(JOKennels.getString("kennelVerified"));
                        } else {
                            data.setKennelVerified(null);
                        }

                        /* GET THE TOTAL REVIEWS, POSITIVE, AND FINALLY, CALCULATE THE PERCENTAGES */
                        if (JOKennels.has("kennelReviews")
                                && JOKennels.has("kennelPositives"))  {
                            String kennelReviews = JOKennels.getString("kennelReviews");
                            String kennelPositives = JOKennels.getString("kennelPositives");

                            int TOTAL_VOTES = Integer.parseInt(kennelReviews);
                            int TOTAL_LIKES = Integer.parseInt(kennelPositives);

                            /* CALCULATE THE PERCENTAGE OF LIKES */
                            double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                            int finalPercentLikes = (int)percentLikes;
                            String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";

                            /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                            Resources resReviews = getResources();
                            String reviewQuantity = null;
                            if (TOTAL_VOTES == 0)   {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            } else if (TOTAL_VOTES == 1)    {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            } else if (TOTAL_VOTES > 1) {
                                reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                            }
                            String strVotes = reviewQuantity;
                            String open = getString(R.string.kennel_list_votes_open);
                            String close = getString(R.string.kennel_list_votes_close);
                            data.setKennelVoteStats(getString(R.string.kennel_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
                        } else {
                            data.setKennelReviews("0");
                        }

                        /* GET THE AVERAGE KENNEL RATING */
                        if (JOKennels.has("kennelRating"))  {
                            data.setKennelRating(JOKennels.getString("kennelRating"));
                        } else {
                            data.setKennelRating("0");
                        }

                        /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                        kennels.add(data);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.e("EXCEPTION", e.getMessage());
            Crashlytics.logException(e);
        }
        return kennels;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101)   {
            /* CLEAR THE ARRAY LIST */
            arrKennels.clear();

            /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS AGAIN */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennels();
        } else if (resultCode == Activity.RESULT_OK && requestCode == 102)   {
            /* CLEAR THE ARRAY LIST */
            arrKennels.clear();

            /* SHOW THE PROGRESS AND FETCH THE LIST OF KENNELS AGAIN */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchKennels();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listKennels.setLayoutManager(manager);
        listKennels.setHasFixedSize(true);

        /* INSTANTIATE AND SET THE ADAPTER */
        listKennels.setAdapter(new KennelsAdapter(this, arrKennels));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Your Kennels";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(this), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
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

    /** CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER **/
    private void checkPublishedKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<KennelPages> call = api.fetchOwnerKennels(KENNEL_OWNER_ID);
        call.enqueue(new Callback<KennelPages>() {
            @Override
            public void onResponse(Call<KennelPages> call, Response<KennelPages> response) {
                if (response.body() != null)    {
                    int publishedKennels = Integer.parseInt(response.body().getTotalKennels());
//                    Log.e("KENNELS", String.valueOf(publishedKennels));
                    if (publishedKennels < 2)   {
                        Intent intent = new Intent(TestKennelsList.this, NewKennelCreator.class);
                        startActivityForResult(intent, 101);
                    } else {
                        new MaterialDialog.Builder(TestKennelsList.this)
                                .icon(ContextCompat.getDrawable(TestKennelsList.this, R.drawable.ic_info_black_24dp))
                                .title(getString(R.string.kennel_limit_title))
                                .content(getString(R.string.kennel_limit_message))
                                .cancelable(false)
                                .positiveText(getString(R.string.kennel_limit_okay))
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        /* DISMISS THE DIALOG */
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<KennelPages> call, Throwable t) {
            }
        });
    }

    /** THE KENNELS ADAPTER **/
    private class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        private final Activity activity;

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<TestKennel> arrKennelsAdapter;

        KennelsAdapter(Activity activity, ArrayList<TestKennel> arrKennelsAdapter) {

            /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
            this.activity = activity;

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrKennelsAdapter = arrKennelsAdapter;
        }

        @Override
        public int getItemCount() {
            return arrKennelsAdapter.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final KennelsAdapter.KennelsVH holder, final int position) {
            final TestKennel data = arrKennelsAdapter.get(position);

//            /* CHECK FOR ALERTS */
//            String kennelChargesID = data.getKennelChargesID();
//            if (kennelChargesID.equalsIgnoreCase("2"))    {
//                if (data.getPaymentID() != null
//                        && !data.getPaymentID().equalsIgnoreCase("null")
//                        && !data.getPaymentID().equalsIgnoreCase(""))  {
//                    holder.imgvwKennelAlert.setVisibility(View.GONE);
//                } else {
//                    holder.imgvwKennelAlert.setVisibility(View.VISIBLE);
//                }
//            } else {
//                holder.imgvwKennelAlert.setVisibility(View.GONE);
//            }

            /* SET THE KENNEL COVER PHOTO */
            String strKennelCoverPhoto = data.getKennelCoverPhoto();
            if (strKennelCoverPhoto != null
                    && !strKennelCoverPhoto.equalsIgnoreCase("")
                    && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
                Uri uri = Uri.parse(strKennelCoverPhoto);
                holder.imgvwKennelCoverPhoto.setImageURI(uri);
            } else {
                ImageRequest request = ImageRequestBuilder
                        .newBuilderWithResourceId(R.drawable.empty_graphic)
                        .build();
                holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
            }

            /* SET THE KENNEL NAME */
            if (data.getKennelName() != null)   {
                holder.txtKennelName.setText(data.getKennelName());
            }

            /* SET THE KENNEL ADDRESS */
            String strKennelAddress = data.getKennelAddress();
            String cityName = data.getCityName();
            String kennelPinCode = data.getKennelPinCode();
            holder.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));

            /* SET THE CAPACITY OF LARGE SIZE PETS */
            if (data.getKennelSmallCapacity() != null
                    && !data.getKennelSmallCapacity().equalsIgnoreCase("")
                    && !data.getKennelSmallCapacity().equalsIgnoreCase("null"))   {
                holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelSmallCapacity()));
            } else {
                holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
            }

            /* SET THE REVIEW VOTE STATS */
            holder.txtKennelLikes.setText(data.getKennelVoteStats());

            /* SET THE AVERAGE KENNEL RATING */
            Double dblRating = Double.valueOf(data.getKennelRating());
            holder.kennelRating.setRating(Float.parseFloat(String.format("%.2f", dblRating, Locale.getDefault())));

//            /* SET THE KENNEL VALIDITY DATES */
//            if (data.getKennelValidFrom() != null && data.getKennelValidTo() != null)   {
//                String kennelValidFrom = data.getKennelValidFrom();
//                String kennelValidTo = data.getKennelValidTo();
//                holder.txtKennelValidity.setText(getString(R.string.kennel_list_kennel_validity_label_placeholder, kennelValidFrom, kennelValidTo));
//            }

            /* SHOW THE UNPAID KENNEL LISTING ALERT */
            holder.imgvwKennelAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(activity)
                            .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_black_24dp))
                            .title(getString(R.string.kennel_list_unpaid_alert_title))
                            .content(getString(R.string.kennel_list_unpaid_alert_message))
                            .cancelable(false)
                            .positiveText("Process Payment")
                            .negativeText("Cancel")
                            .theme(Theme.LIGHT)
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });

            /* SHOW THE KENNEL DETAILS */
            holder.cardKennelDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentDetails = new Intent(activity, KennelDetails.class);
                    intentDetails.putExtra("KENNEL_ID", data.getKennelID());
                    startActivity(intentDetails);
                }
            });

            /* SHOW THE KENNEL OPTIONS POPUP MENU */
            holder.imgvwKennelOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pm = new PopupMenu(activity, holder.imgvwKennelOptions);
                    pm.getMenuInflater().inflate(R.menu.pm_kennel_options, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuDetails:
                                    Intent intentDetails = new Intent(activity, KennelDetails.class);
                                    intentDetails.putExtra("KENNEL_ID", data.getKennelID());
                                    startActivity(intentDetails);
                                    break;
                                case R.id.menuImages:
                                    Intent intentImages = new Intent(activity, KennelImageManager.class);
                                    intentImages.putExtra("KENNEL_ID", data.getKennelID());
                                    intentImages.putExtra("KENNEL_NAME", data.getKennelName());
                                    startActivity(intentImages);
                                    break;
                                case R.id.menuEdit:
                                    Intent intentEdit = new Intent(activity, KennelModifier.class);
                                    intentEdit.putExtra("KENNEL_ID", data.getKennelID());
                                    startActivityForResult(intentEdit, 102);
                                    break;
                                case R.id.menuDelete:
                                    /* SHOW THE DELETE DIALOG */
                                    showDeleteDialog(data.getKennelID(), data.getKennelName(), position);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    pm.show();
                }
            });
        }

        /***** SHOW THE DELETE DIALOG *****/
        private void showDeleteDialog(final String kennelID, final String kennelName, final int position) {
            new MaterialDialog.Builder(activity)
                    .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_black_24dp))
                    .title("Delete Kennel?")
                    .content(activity.getString(R.string.delete_kennel_content, kennelName))
                    .cancelable(false)
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
                            Call<Kennel> call = api.deleteKennelRecord(kennelID);
                            call.enqueue(new Callback<Kennel>() {
                                @Override
                                public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                                    if (response.isSuccessful())    {
                                        /* SHOW THE SUCCESS MESSAGE */
                                        Toast.makeText(activity, "Record deleted...", Toast.LENGTH_SHORT).show();

                                        /* DELETE THE ITEM FROM THE ARRAY LIST (THIS IS TEMPORARY OF COURSE) */
                                        arrKennelsAdapter.remove(position);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(activity, "Failed to delete record...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Kennel> call, Throwable t) {
//                                    Log.e("DELETE FAILURE", t.getMessage());
                                    Crashlytics.logException(t);
                                }
                            });
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        @NonNull
        @Override
        public KennelsAdapter.KennelsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.dash_test_kennels_item, parent, false);

            return new KennelsAdapter.KennelsVH(itemView);
        }

        class KennelsVH extends RecyclerView.ViewHolder	{

            CardView cardKennelDetails;
            SimpleDraweeView imgvwKennelCoverPhoto;
            TextView txtKennelName;
            IconicsImageView imgvwKennelOptions;
            IconicsImageView imgvwKennelAlert;
            TextView txtKennelAddress;
            TextView txtPetCapacity;
            TextView txtKennelValidity;
            TextView txtKennelLikes;
            RatingBar kennelRating;

            KennelsVH(View v) {
                super(v);

                cardKennelDetails = v.findViewById(R.id.cardKennelDetails);
                imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
                txtKennelName = v.findViewById(R.id.txtKennelName);
                imgvwKennelOptions = v.findViewById(R.id.imgvwKennelOptions);
                imgvwKennelAlert = v.findViewById(R.id.imgvwKennelAlert);
                txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
                txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
                txtKennelValidity = v.findViewById(R.id.txtKennelValidity);
                txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
                kennelRating = v.findViewById(R.id.kennelRating);
            }
        }
    }
}