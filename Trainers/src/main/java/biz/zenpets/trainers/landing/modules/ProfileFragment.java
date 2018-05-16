package biz.zenpets.trainers.landing.modules;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.TypefaceSpan;
import biz.zenpets.trainers.utils.adapters.modules.TrainingModulesAdapter;
import biz.zenpets.trainers.utils.adapters.modules.reviews.TrainerReviewsAdapter;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.Trainer;
import biz.zenpets.trainers.utils.models.trainers.TrainersAPI;
import biz.zenpets.trainers.utils.models.trainers.modules.Module;
import biz.zenpets.trainers.utils.models.trainers.modules.Modules;
import biz.zenpets.trainers.utils.models.trainers.modules.ModulesAPI;
import biz.zenpets.trainers.utils.models.trainers.reviews.Review;
import biz.zenpets.trainers.utils.models.trainers.reviews.Reviews;
import biz.zenpets.trainers.utils.models.trainers.reviews.ReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN TRAINER'S ID **/
    String TRAINER_ID = null;

    /** INSTANCES TO HOLD THE TRAINER'S PROFILE DETAILS **/
    String TRAINER_AUTH_ID = null;
    String TRAINER_NAME = null;
    String TRAINER_EMAIL = null;
    String TRAINER_PHONE_PREFIX = null;
    String TRAINER_PHONE_NUMBER = null;
    String TRAINER_ADDRESS = null;
    String TRAINER_PINCODE = null;
    String TRAINER_LANDMARK = null;
    Double TRAINER_LATITUDE = null;
    Double TRAINER_LONGITUDE = null;
    String COUNTRY_ID = null;
    String COUNTRY_NAME = null;
    String STATE_ID = null;
    String STATE_NAME = null;
    String CITY_ID = null;
    String CITY_NAME = null;
    String TRAINER_GENDER = null;
    String TRAINER_PROFILE = null;
    String TRAINER_DISPLAY_PROFILE = null;

    /** THE TRAINER REVIEWS ARRAY LIST **/
    ArrayList<Review> arrReviews = new ArrayList<>();

    /** THE TRAINING MODULES ARRAY LIST **/
    ArrayList<Module> arrModules = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS  **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwTrainerDisplayProfile) SimpleDraweeView imgvwTrainerDisplayProfile;
    @BindView(R.id.txtTrainerName) AppCompatTextView txtTrainerName;
    @BindView(R.id.txtTrainerID) AppCompatTextView txtTrainerID;
    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
    @BindView(R.id.txtVotes) AppCompatTextView txtVotes;
    @BindView(R.id.txtTrainerAddress) AppCompatTextView txtTrainerAddress;
    @BindView(R.id.trainerMap) MapView trainerMap;
    @BindView(R.id.linlaReviewsProgress) LinearLayout linlaReviewsProgress;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.txtAllReviews) AppCompatTextView txtAllReviews;
    @BindView(R.id.linlaModulesProgress) LinearLayout linlaModulesProgress;
    @BindView(R.id.listModules) RecyclerView listModules;
    @BindView(R.id.linlaNoModules) LinearLayout linlaNoModules;
    @BindView(R.id.txtAllModules) AppCompatTextView txtAllModules;

    /** SHOW ALL REVIEWS **/
    @OnClick(R.id.txtAllReviews) void showReviews() {
    }

    /** SHOW ALL TRAINING MODULES **/
    @OnClick(R.id.txtAllModules) void showModules() {
    }

    /** SHOW THE INTERNAL ID INFO **/
    @OnClick(R.id.imgvwIDInfo) protected void showIDInfo()  {
        showIDDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.home_profile_frag, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        trainerMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("trainer_map_save_state") : null);
        trainerMap.onResume();
        trainerMap.setClickable(false);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* GET THE LOGGED IN TRAINER'S ID */
        TRAINER_ID = getApp().getTrainerID();
//        Log.e("TRAINER ID", TRAINER_ID);

        /* FETCH THE LIST OF TRAINING MODULES */
        if (TRAINER_ID != null) {
            /* SET THE TRAINER'S ID */
            txtTrainerID.setText(getString(R.string.tp_internal_id, TRAINER_ID));

            /* SHOW THE PROGRESS AND FETCH THE TRAINER'S PROFILE */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchTrainerProfile();
        }
    }

    /***** FETCH THE TRAINER'S PROFILE *****/
    private void fetchTrainerProfile() {
        TrainersAPI api = ZenApiClient.getClient().create(TrainersAPI.class);
        Call<Trainer> call = api.fetchTrainerProfile(TRAINER_ID);
        call.enqueue(new Callback<Trainer>() {
            @Override
            public void onResponse(Call<Trainer> call, Response<Trainer> response) {
                Trainer data = response.body();
                if (data != null)   {
                    /* GET AND SET THE TRAINER'S NAME */
                    TRAINER_NAME = data.getTrainerName();
                    if (TRAINER_NAME != null)
                        txtTrainerName.setText(TRAINER_NAME);

                    /* GET AND SET THE TRAINER'S DISPLAY PROFILE */
                    TRAINER_DISPLAY_PROFILE = data.getTrainerDisplayProfile();
                    if (TRAINER_DISPLAY_PROFILE != null) {
                        Uri uri = Uri.parse(TRAINER_DISPLAY_PROFILE);
                        imgvwTrainerDisplayProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwTrainerDisplayProfile.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE TRAINER'S ADDRESS */
                    TRAINER_ADDRESS = data.getTrainerAddress();
                    CITY_NAME = data.getCityName();
                    TRAINER_PINCODE = data.getTrainerPincode();
                    STATE_NAME = data.getStateName();
                    txtTrainerAddress.setText(getString(R.string.tp_address_placeholder, TRAINER_ADDRESS, CITY_NAME, TRAINER_PINCODE, STATE_NAME));

                    /* GET AND SET THE TRAINER'S LOCATION ON THE MAP */
                    TRAINER_LATITUDE = data.getTrainerLatitude();
                    TRAINER_LONGITUDE = data.getTrainerLongitude();
                    if (TRAINER_LATITUDE != null && TRAINER_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(TRAINER_LATITUDE, TRAINER_LONGITUDE);
                        trainerMap.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                googleMap.getUiSettings().setMapToolbarEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.setBuildingsEnabled(true);
                                googleMap.setTrafficEnabled(false);
                                googleMap.setIndoorEnabled(false);
                                MarkerOptions options = new MarkerOptions();
                                options.position(latLng);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                Marker mMarker = googleMap.addMarker(options);
                                googleMap.addMarker(options);

                                /* MOVE THE MAP CAMERA */
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 10));
                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

                                /* SHOW THE MAP DETAILS AND DIRECTIONS */
//                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                                    @Override
//                                    public void onMapClick(LatLng latLng) {
//                                        Intent intent = new Intent(getApplicationContext(), MapDetails.class);
//                                        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
//                                        intent.putExtra("DOCTOR_NAME", DOCTOR_PREFIX + " " + DOCTOR_NAME);
//                                        intent.putExtra("DOCTOR_PHONE_NUMBER", DOCTOR_PHONE_NUMBER);
//                                        intent.putExtra("CLINIC_ID", CLINIC_ID);
//                                        intent.putExtra("CLINIC_NAME", CLINIC_NAME);
//                                        intent.putExtra("CLINIC_LATITUDE", CLINIC_LATITUDE);
//                                        intent.putExtra("CLINIC_LONGITUDE", CLINIC_LONGITUDE);
//                                        intent.putExtra("CLINIC_ADDRESS", CLINIC_ADDRESS);
//                                        startActivity(intent);
//                                    }
//                                });
                            }
                        });
                    }

                    /* SHOW THE PROGRESS AND FETCH A SUBSET OF THE TRAINER'S REVIEWS */
                    linlaReviewsProgress.setVisibility(View.VISIBLE);
                    fetchTrainerReviews();

                    /* SHOW THE PROGRESS AND FETCH A SUBSET OF THE TRAINER'S TRAINING MODULES */
                    linlaModulesProgress.setVisibility(View.VISIBLE);
                    fetchTrainingModules();

                } else {
                    Toast.makeText(getActivity(), "Problem fetching data...", Toast.LENGTH_SHORT).show();
                }
                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Trainer> call, Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH A SUBSET OF THE TRAINER'S REVIEWS *****/
    private void fetchTrainerReviews() {
        ReviewsAPI api = ZenApiClient.getClient().create(ReviewsAPI.class);
        Call<Reviews> call = api.fetchTrainerReviewsSubset(TRAINER_ID);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                try {
                    String strResult = new Gson().toJson(response.body());
                    JSONObject JORoot = new JSONObject(strResult);
                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                        JSONArray JAReviews = JORoot.getJSONArray("reviews");
                        Review data;
                        for (int i = 0; i < JAReviews.length(); i++) {
                            JSONObject JOReviews = JAReviews.getJSONObject(i);
                            data = new Review();

                            /* GET THE REVIEW ID */
                            if (JOReviews.has("trainerReviewID"))  {
                                data.setTrainerReviewID(JOReviews.getString("trainerReviewID"));
                            } else {
                                data.setTrainerReviewID(null);
                            }

                            /* GET THE TRAINER ID */
                            if (JOReviews.has("trainerID"))  {
                                data.setTrainerID(JOReviews.getString("trainerID"));
                            } else {
                                data.setTrainerID(null);
                            }

                            /* GET THE USER ID */
                            if (JOReviews.has("userID")) {
                                data.setUserID(JOReviews.getString("userID"));
                            } else {
                                data.setUserID(null);
                            }

                            /* GET THE USER'S NAME */
                            if (JOReviews.has("userName")) {
                                data.setUserName(JOReviews.getString("userName"));
                            } else {
                                data.setUserName(null);
                            }

                            /* GET THE USER'S DISPLAY PROFILE */
                            if (JOReviews.has("userDisplayProfile")) {
                                data.setUserDisplayProfile(JOReviews.getString("userDisplayProfile"));
                            } else {
                                data.setUserDisplayProfile(null);
                            }

                            /* GET THE RECOMMEND STATUS */
                            if (JOReviews.has("recommendStatus")) {
                                data.setRecommendStatus(JOReviews.getString("recommendStatus"));
                            } else {
                                data.setRecommendStatus(null);
                            }

                            /* GET THE TRAINER'S EXPERIENCE */
                            if (JOReviews.has("trainerExperience"))  {
                                data.setTrainerExperience(JOReviews.getString("trainerExperience"));
                            } else {
                                data.setTrainerExperience(null);
                            }

                            /* GET THE REVIEW TIMESTAMP */
                            if (JOReviews.has("reviewTimestamp"))   {
                                data.setReviewTimestamp(JOReviews.getString("reviewTimestamp"));
                            } else {
                                data.setReviewTimestamp(null);
                            }

                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                            arrReviews.add(data);
                        }

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listReviews.setAdapter(new TrainerReviewsAdapter(getActivity(), arrReviews));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listReviews.setVisibility(View.VISIBLE);
                        txtAllReviews.setVisibility(View.VISIBLE);
                        linlaNoReviews.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaNoReviews.setVisibility(View.VISIBLE);
                        listReviews.setVisibility(View.GONE);
                        txtAllReviews.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* HIDE THE REVIEWS PROGRESS */
                linlaReviewsProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {

            }
        });
    }

    /***** FETCH A SUBSET OF THE TRAINER'S TRAINING MODULES *****/
    private void fetchTrainingModules() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Modules> call = api.fetchTrainerModulesSubset(TRAINER_ID);
        call.enqueue(new Callback<Modules>() {
            @Override
            public void onResponse(Call<Modules> call, Response<Modules> response) {
                try {
                    String strResult = new Gson().toJson(response.body());
                    JSONObject JORoot = new JSONObject(strResult);
                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                        JSONArray JAModules = JORoot.getJSONArray("modules");
                        Module data;
                        for (int i = 0; i < JAModules.length(); i++) {
                            JSONObject JOModules = JAModules.getJSONObject(i);
                            data = new Module();

                            /* GET THE MODULE ID */
                            if (JOModules.has("trainerModuleID"))  {
                                data.setTrainerModuleID(JOModules.getString("trainerModuleID"));
                            } else {
                                data.setTrainerModuleID(null);
                            }

                            /* GET THE TRAINER ID */
                            if (JOModules.has("trainerID"))  {
                                data.setTrainerID(JOModules.getString("trainerID"));
                            } else {
                                data.setTrainerID(null);
                            }

                            /* GET THE MODULE NAME */
                            if (JOModules.has("trainerModuleName")) {
                                data.setTrainerModuleName(JOModules.getString("trainerModuleName"));
                            } else {
                                data.setTrainerModuleName(null);
                            }

                            /* GET THE MODULE DURATION */
                            if (JOModules.has("trainerModuleDuration")) {
                                data.setTrainerModuleDuration(JOModules.getString("trainerModuleDuration"));
                            } else {
                                data.setTrainerModuleDuration(null);
                            }

                            /* GET THE MODULE DURATION UNIT */
                            if (JOModules.has("trainerModuleDurationUnit")) {
                                data.setTrainerModuleDurationUnit(JOModules.getString("trainerModuleDurationUnit"));
                            } else {
                                data.setTrainerModuleDurationUnit(null);
                            }

                            /* GET THE MODULE SESSIONS */
                            if (JOModules.has("trainerModuleSessions")) {
                                data.setTrainerModuleSessions(JOModules.getString("trainerModuleSessions"));
                            } else {
                                data.setTrainerModuleSessions(null);
                            }

                            /* GET THE MODULE DETAILS */
                            if (JOModules.has("trainerModuleDetails"))  {
                                data.setTrainerModuleDetails(JOModules.getString("trainerModuleDetails"));
                            } else {
                                data.setTrainerModuleDetails(null);
                            }

                            /* GET THE MODULE FORMAT */
                            if (JOModules.has("trainerModuleFormat"))   {
                                data.setTrainerModuleFormat(JOModules.getString("trainerModuleFormat"));
                            } else {
                                data.setTrainerModuleFormat(null);
                            }

                            /* GET THE MODULE GROUP SIZE */
                            if (JOModules.has("trainerModuleSize")) {
                                data.setTrainerModuleSize(JOModules.getString("trainerModuleSize"));
                            } else {
                                data.setTrainerModuleSize(null);
                            }

                            /* GET THE MODULE FEES */
                            if (JOModules.has("trainerModuleFees")) {
                                data.setTrainerModuleFees(JOModules.getString("trainerModuleFees"));
                            } else {
                                data.setTrainerModuleFees(null);
                            }

                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                            arrModules.add(data);
                        }

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listModules.setAdapter(new TrainingModulesAdapter(getActivity(), arrModules));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listModules.setVisibility(View.VISIBLE);
                        txtAllModules.setVisibility(View.VISIBLE);
                        linlaNoModules.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaNoModules.setVisibility(View.VISIBLE);
                        listModules.setVisibility(View.GONE);
                        txtAllModules.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* HIDE THE TRAINING MODULES PROGRESS */
                linlaModulesProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Modules> call, Throwable t) {

            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        String strTitle = "Your Profile";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    @Override
    public void onResume() {
        super.onResume();
        trainerMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        trainerMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        trainerMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trainerMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        trainerMap.onLowMemory();
    }

    /***** SHOW THE INTERNAL ID INFO *****/
    private void showIDDialog() {
        new MaterialDialog.Builder(getActivity())
                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                .title("Internal ID")
                .cancelable(true)
                .content("The Internal ID is a system generated unique identifier for your account. \n\nWhen you get in touch with us, for assistance or information, providing us this unique ID will speed things up. You could always also provide your email address.")
                .positiveText("Dismiss")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE REVIEWS CONFIGURATION */
        LinearLayoutManager managerReviews = new LinearLayoutManager(getActivity());
        managerReviews.setOrientation(LinearLayoutManager.VERTICAL);
        listReviews.setLayoutManager(managerReviews);
        listReviews.setHasFixedSize(true);

        /* SET THE ADAPTER TO THE REVIEWS RECYCLER VIEW */
        listReviews.setAdapter(new TrainerReviewsAdapter(getActivity(), arrReviews));

        /* SET THE TRAINING MODULES CONFIGURATION */
        LinearLayoutManager managerModules = new LinearLayoutManager(getActivity());
        managerModules.setOrientation(LinearLayoutManager.VERTICAL);
        listModules.setLayoutManager(managerModules);
        listModules.setHasFixedSize(true);

        /* SET THE ADAPTER TO THE TRAINING MODULES RECYCLER VIEW */
        listModules.setAdapter(new TrainingModulesAdapter(getActivity(), arrModules));
    }
}