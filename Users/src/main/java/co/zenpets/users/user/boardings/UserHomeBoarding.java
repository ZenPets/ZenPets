package co.zenpets.users.user.boardings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import co.zenpets.users.R;
import co.zenpets.users.boarding.BoardingHouseCompleter;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.boarding.Boarding;
import co.zenpets.users.utils.models.boarding.BoardingsAPI;
import co.zenpets.users.utils.models.boarding.access.AccessAPI;
import co.zenpets.users.utils.models.boarding.conditions.ConditionsAPI;
import co.zenpets.users.utils.models.boarding.house.House;
import co.zenpets.users.utils.models.boarding.house.HouseAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeBoarding extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    String USER_ID = null;

    /** THE BOARDING DETAILS **/
    String BOARDING_ID = null;

    /** AN INSTANCE OF THE BOARDING API INTERFACE'S **/
    BoardingsAPI boardingsAPI = ZenApiClient.getClient().create(BoardingsAPI.class);
    HouseAPI houseAPI = ZenApiClient.getClient().create(HouseAPI.class);
    AccessAPI accessAPI = ZenApiClient.getClient().create(AccessAPI.class);
    ConditionsAPI conditionsAPI = ZenApiClient.getClient().create(ConditionsAPI.class);

    /* A PROGRESS DIALOG INSTANCE */
    ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwBoardingCoverPhoto) SimpleDraweeView imgvwBoardingCoverPhoto;
    @BindView(R.id.imgvwUserDisplayProfile) SimpleDraweeView imgvwUserDisplayProfile;
    @BindView(R.id.txtUserName) TextView txtUserName;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtBoardingAddress) TextView txtBoardingAddress;
    @BindView(R.id.boardingMap) MapView boardingMap;
    @BindView(R.id.layoutHomeDetails) ConstraintLayout layoutHomeDetails;
    @BindView(R.id.txtHomeType) TextView txtHomeType;
    @BindView(R.id.txtHomeDogs) TextView txtHomeDogs;
    @BindView(R.id.txtHomeCats) TextView txtHomeCats;
    @BindView(R.id.txtHomeSmoking) TextView txtHomeSmoking;
    @BindView(R.id.txtHomeVaping) TextView txtHomeVaping;
    @BindView(R.id.layoutEmptyHomeDetails) ConstraintLayout layoutEmptyHomeDetails;
    @BindView(R.id.layoutAccessDetails) ConstraintLayout layoutAccessDetails;
    @BindView(R.id.txtAccessCouch) TextView txtAccessCouch;
    @BindView(R.id.txtAccessBed) TextView txtAccessBed;
    @BindView(R.id.txtAccessFans) TextView txtAccessFans;
    @BindView(R.id.layoutEmptyAccessDetails) ConstraintLayout layoutEmptyAccessDetails;
    @BindView(R.id.layoutPreferencesDetails) ConstraintLayout layoutPreferencesDetails;
    @BindView(R.id.imgvwSmall) ImageView imgvwSmall;
    @BindView(R.id.imgvwMedium) ImageView imgvwMedium;
    @BindView(R.id.imgvwLarge) ImageView imgvwLarge;
    @BindView(R.id.imgvwExtraLarge) ImageView imgvwExtraLarge;
    @BindView(R.id.txtSmallLabel) TextView txtSmallLabel;
    @BindView(R.id.txtMediumLabel) TextView txtMediumLabel;
    @BindView(R.id.txtLargeLabel) TextView txtLargeLabel;
    @BindView(R.id.txtExtraLargeLabel) TextView txtExtraLargeLabel;
    @BindView(R.id.layoutEmptyPreferencesDetails) ConstraintLayout layoutEmptyPreferencesDetails;
    @BindView(R.id.layoutConditionsAccepted) ConstraintLayout layoutConditionsAccepted;
    @BindView(R.id.listConditionsAccepted) RecyclerView listConditionsAccepted;
    @BindView(R.id.layoutEmptyConditionsAccepted) ConstraintLayout layoutEmptyConditionsAccepted;
    @BindView(R.id.layoutConditionsNotAccepted) ConstraintLayout layoutConditionsNotAccepted;
    @BindView(R.id.listConditionsNotAccepted) RecyclerView listConditionsNotAccepted;
    @BindView(R.id.layoutEmptyConditionsNotAccepted) ConstraintLayout layoutEmptyConditionsNotAccepted;

    /** EDIT THE HOME DETAILS **/
    @OnClick(R.id.imgvwHomeEdit) void editHome()    {
        Intent intent = new Intent(UserHomeBoarding.this, BoardingHouseCompleter.class);
        intent.putExtra("BOARDING_ID", BOARDING_ID);
        startActivityForResult(intent, 101);
    }

    /** EDIT THE ACCESS DETAILS **/
    @OnClick(R.id.imgvwAccessEdit) void editAccess()    {
    }

    /** EDIT THE PET SIZE PREFERENCES **/
    @OnClick(R.id.imgvwPreferencesEdit) void editPreferences()  {
    }

    /** EDIT THE CONDITIONS ACCEPTED **/
    @OnClick(R.id.imgvwAcceptedEdit) void edtAccepted() {
    }

    /** EIDT THE CONDITIONS NOT ACCEPTED **/
    @OnClick(R.id.imgvwNotAcceptedEdit) void editNotAccepted()  {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_boarding);
        ButterKnife.bind(this);
        boardingMap.onCreate(savedInstanceState != null ? savedInstanceState.getBundle("map_save_state") : null);
        boardingMap.onResume();
        boardingMap.setClickable(false);

        /* GET THE USER ID**/
        USER_ID = getApp().getUserID();

        if (USER_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE BOARDING DETAILS */
//            linlaProgress.setVisibility(View.VISIBLE);
            fetchBoardingDetails();

            /* FETCH THE HOUSE DETAILS */
            fetchHouseDetails();
        }
    }

    /** FETCH THE HOUSE DETAILS **/
    private void fetchHouseDetails() {
        Call<House> call = houseAPI.fetchBoardingHouseDetails(USER_ID);
        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
//                Log.e("HOUSE RESPONSE", String.valueOf(response.raw()));
                boolean blnError = response.body().getError();
                if (!blnError)  {
                    /* CAST THE RESPONSE IN THE HOUSE POJO INSTANCE */
                    House house = response.body();
                    if (house != null)  {
                        /* GET AND SET THE BOARDING UNIT ID AND TYPE */
                        String BOARDING_UNIT_ID = house.getBoardingUnitID();
                        String BOARDING_UNIT_TYPE = house.getBoardingUnitType();
                        txtHomeType.setText(getString(R.string.hbud_unit_type, BOARDING_UNIT_TYPE));

                        /* GET THE DOGS STATUS */
                        String HOUSE_DOGS = house.getDetailsDog();
                        if (HOUSE_DOGS.equalsIgnoreCase("0"))   {
                            txtHomeDogs.setText(getString(R.string.hbud_unit_dogs_no));
                        } else {
                            txtHomeDogs.setText(getString(R.string.hbud_unit_dogs_yes));
                        }

                        /* GET THE CATS STATUS */
                        String HOUSE_CATS = house.getDetailsCat();
                        if (HOUSE_CATS.equalsIgnoreCase("0"))   {
                            txtHomeCats.setText(getString(R.string.hbud_unit_cats_no));
                        } else {
                            txtHomeCats.setText(getString(R.string.hbud_unit_cats_yes));
                        }

                        /* GET THE SMOKING STATUS */
                        String HOUSE_SMOKING = house.getDetailsSmoking();
                        if (HOUSE_SMOKING.equalsIgnoreCase("0"))    {
                            txtHomeSmoking.setText(getString(R.string.hbud_unit_smoking_no));
                        } else {
                            txtHomeSmoking.setText(getString(R.string.hbud_unit_smoking_yes));
                        }

                        /* GET THE VAPING STATUS */
                        String HOUSE_VAPING = house.getDetailsVaping();
                        if (HOUSE_VAPING.equalsIgnoreCase("0")) {
                            txtHomeVaping.setText(getString(R.string.hbud_unit_vaping_no));
                        } else {
                            txtHomeVaping.setText(getString(R.string.hbud_unit_vaping_yes));
                        }

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
//                Log.e("HOUSE FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE BOARDING DETAILS **/
    private void fetchBoardingDetails() {
        Call<Boarding> call = boardingsAPI.fetchBoardingDetailsByUser(USER_ID);
        call.enqueue(new Callback<Boarding>() {
            @Override
            public void onResponse(Call<Boarding> call, Response<Boarding> response) {
//                Log.e("RAW RESPONSE", String.valueOf(response.raw()));

                /* CAST THE RESPONSE IN THE BOARDING POJO INSTANCE */
                Boarding boarding = response.body();
                if (boarding != null)   {
                    /* GET THE BOARDING ID */
                    BOARDING_ID = boarding.getBoardingID();

                    /* GET AND SET THE BOARDING COVER PHOTO */
                    String BOARDING_COVER_PHOTO = boarding.getBoardingCoverPhoto();
                    if (BOARDING_COVER_PHOTO != null
                            && !BOARDING_COVER_PHOTO.equalsIgnoreCase("")
                            && !BOARDING_COVER_PHOTO.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(BOARDING_COVER_PHOTO);
                        imgvwBoardingCoverPhoto.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        imgvwBoardingCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE USER'S DISPLAY PROFILE */
                    String USER_DISPLAY_PROFILE = boarding.getUserDisplayProfile();
                    if (USER_DISPLAY_PROFILE != null
                            && !USER_DISPLAY_PROFILE.equalsIgnoreCase("")
                            && !USER_DISPLAY_PROFILE.equalsIgnoreCase("null"))    {
                        Uri uri = Uri.parse(USER_DISPLAY_PROFILE);
                        imgvwUserDisplayProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwUserDisplayProfile.setImageURI(request.getSourceUri());
                    }

                    /* GET AND SET THE USER'S NAME */
                    String USER_NAME = boarding.getUserName();
                    txtUserName.setText(USER_NAME);

                    /* GET AND SET THE BOARDING ADDRESS */
                    String BOARDING_ADDRESS = boarding.getBoardingAddress();
                    String cityName = boarding.getCityName();
                    String boardingPincode = boarding.getBoardingPincode();
                    txtBoardingAddress.setText(getString(R.string.doctor_list_address_placeholder, BOARDING_ADDRESS, cityName, boardingPincode));

                    /* GET AND SET THE BOARDING LATITUDE AND LONGITUDE ON THE MAP */
                    Double BOARDING_LATITUDE = Double.valueOf(boarding.getBoardingLatitude());
                    Double BOARDING_LONGITUDE = Double.valueOf(boarding.getBoardingLongitude());
                    if (BOARDING_LATITUDE != null && BOARDING_LONGITUDE != null) {
                        final LatLng latLng = new LatLng(BOARDING_LATITUDE, BOARDING_LONGITUDE);
                        boardingMap.getMapAsync(new OnMapReadyCallback() {
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
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boarding> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)    {
            if (requestCode == 101) {
                /* SHOW THE PROGRESS AND REFRESH THE HOUSE DETAILS */
                /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
                dialog = new ProgressDialog(this);
                dialog.setMessage("Updating your House details...");
                dialog.setIndeterminate(false);
                dialog.setCancelable(false);
                dialog.show();

                /* FETCH THE HOUSE DETAILS */
                fetchHouseDetails();

            } else if (requestCode == 102)   {
            } else if (requestCode == 103)  {
            } else if (requestCode == 104)  {
            } else if (requestCode == 105)  {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boardingMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        boardingMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        boardingMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        boardingMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        boardingMap.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle(outState);
        boardingMap.onSaveInstanceState(bundle);
        outState.putBundle("map_save_state", bundle);
        super.onSaveInstanceState(outState);
    }
}