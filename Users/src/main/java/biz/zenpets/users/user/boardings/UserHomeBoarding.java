package biz.zenpets.users.user.boardings;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.boarding.Boarding;
import biz.zenpets.users.utils.models.boarding.BoardingsAPI;
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

    /** AN INSTANCE OF THE BOARDING API INTERFACE **/
    BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);

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
            linlaProgress.setVisibility(View.VISIBLE);
            fetchBoardingDetails();

            /* FETCH THE HOME DETAILS */
            fetchHomeDetails();
        }

    }

    /** FETCH THE HOME DETAILS **/
    private void fetchHomeDetails() {
    }

    /** FETCH THE BOARDING DETAILS **/
    private void fetchBoardingDetails() {
        Call<Boarding> call = api.fetchBoardingDetailsByUser(USER_ID);
        call.enqueue(new Callback<Boarding>() {
            @Override
            public void onResponse(Call<Boarding> call, Response<Boarding> response) {
                Log.e("RAW RESPONSE", String.valueOf(response.raw()));

                /* CAST THE RESPONSE IN THE BOARDING POJO INSTANCE */
                Boarding boarding = response.body();
                if (boarding != null)   {
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
                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
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