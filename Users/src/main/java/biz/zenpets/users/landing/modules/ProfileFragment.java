package biz.zenpets.users.landing.modules;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.boarding.BoardingEnrollment;
import biz.zenpets.users.creator.pet.NewPetCreator;
import biz.zenpets.users.details.profile.ProfileDetails;
import biz.zenpets.users.user.adoptions.TestUserAdoptions;
import biz.zenpets.users.user.appointments.UserAppointments;
import biz.zenpets.users.user.boardings.UserHomeBoarding;
import biz.zenpets.users.user.questions.UserQuestions;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.pet.UserPetsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.settings.SettingsActivity;
import biz.zenpets.users.utils.models.boarding.Boarding;
import biz.zenpets.users.utils.models.boarding.BoardingsAPI;
import biz.zenpets.users.utils.models.pets.pets.Pet;
import biz.zenpets.users.utils.models.pets.pets.Pets;
import biz.zenpets.users.utils.models.pets.pets.PetsAPI;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
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

    /** A FIREBASE AUTH INSTANCE **/
    private FirebaseAuth auth;

    /** THE USER AUTH ID AND USER ID **/
    private String USER_AUTH_ID = null;
    private String USER_ID = null;

    /** THE USER NAME AND DISPLAY PROFILE **/
    private String USER_NAME = null;
    private String USER_DISPLAY_PROFILE = null;

    /** THE PETS ADAPTER AND ARRAY LIST **/
    private ArrayList<Pet> arrPets = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwProfilePicture) SimpleDraweeView imgvwProfilePicture;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.listUserPets) RecyclerView listUserPets;
    @BindView(R.id.linlaEmptyPets) LinearLayout linlaEmptyPets;

    /** SHOW THE PROFILE DETAILS **/
    @OnClick(R.id.txtViewProfile) void showProfile()    {
        Intent intent = new Intent(getActivity(), ProfileDetails.class);
        startActivity(intent);
    }

    /** ADD A NEW PET **/
    @OnClick(R.id.txtAddPet) void newPet()  {
        Intent intent = new Intent(getActivity(), NewPetCreator.class);
        startActivityForResult(intent, 101);
    }

    /** SHOW THE USER'S DOCTORS **/
    @OnClick(R.id.linlaMyAppointments) void showUserAppointments()    {
        Intent intent = new Intent(getActivity(), UserAppointments.class);
        startActivity(intent);
    }

    /** SHOW THE USER'S CONSULTATION QUERIES **/
    @OnClick(R.id.linlaMyConsultations) void showUserQueries() {
        Intent intent = new Intent(getActivity(), UserQuestions.class);
        startActivity(intent);
    }

    /** SHOW THE USER'S ADOPTION LISTINGS **/
    @OnClick(R.id.linlaMyAdoptions) void showUserAdoptions()    {
        Intent intent = new Intent(getActivity(), TestUserAdoptions.class);
        startActivity(intent);
    }

    /** SHOW THE HOME BOARDING ACTIVITY **/
    @OnClick(R.id.linlaHomeBoarding) void showHomeBoarding()    {

        /* SHOW THE PROGRESS DIALOG */
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* CHECK IS THE USER HAS ALREADY ENROLLED FOR HOME BOARDING */
        checkBoardingStatus();
    }

    /** SHOW THE SETTINGS ACTIVITY **/
    @OnClick(R.id.linlaSettings) void showSettings()    {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
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

        /* INSTANTIATE THE FIREBASE AUTH INSTANCE **/
        auth = FirebaseAuth.getInstance();

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* FETCH THE USER'S NAME AND DISPLAY PROFILE */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_AUTH_ID = user.getUid();
            /* FETCH THE USER'S PROFILE DETAILS */
            fetchProfileDetails();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
        }

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        if (USER_ID != null)    {
            /* SHOW THE PROGRESS AND FETCH THE USER'S LIST OF PETS */
            linlaProgress.setVisibility(View.VISIBLE);
            listUserPets.setVisibility(View.GONE);
            fetchPetsList();
        }
    }

    /***** FETCH THE PROFILE DETAILS *****/
    private void fetchProfileDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(USER_AUTH_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    /* SET THE USER'S NAME */
                    USER_NAME = data.getUserName();
                    txtUserName.setText(USER_NAME);

                    /* SET THE USER'S DISPLAY PROFILE */
                    USER_DISPLAY_PROFILE = data.getUserDisplayProfile();
                    if (USER_DISPLAY_PROFILE != null)   {
                        Uri uri = Uri.parse(USER_DISPLAY_PROFILE);
                        imgvwProfilePicture.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwProfilePicture.setImageURI(request.getSourceUri());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE USER'S LIST OF PETS *****/
    private void fetchPetsList() {
        PetsAPI api = ZenApiClient.getClient().create(PetsAPI.class);
        Call<Pets> call = api.fetchUserPets(USER_ID);
        call.enqueue(new Callback<Pets>() {
            @Override
            public void onResponse(Call<Pets> call, Response<Pets> response) {
                if (response.body() != null && response.body().getPets() != null)   {
                    arrPets = response.body().getPets();

                    /* CHECK FOR RESULTS */
                    if (arrPets.size() > 0) {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY VIEW */
                        listUserPets.setVisibility(View.VISIBLE);
                        linlaEmptyPets.setVisibility(View.GONE);

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listUserPets.setAdapter(new UserPetsAdapter(getActivity(), arrPets));
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmptyPets.setVisibility(View.VISIBLE);
                        listUserPets.setVisibility(View.GONE);
                    }
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Pets> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** CONFIGURE THE TOOLBAR **/
    private void configToolbar() {
        Toolbar myToolbar = getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        String strTitle = "Profile";
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(strTitle);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* CONFIGURE THE LAYOUT MANAGER */
        LinearLayoutManager llmClinicImages = new LinearLayoutManager(getActivity());
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.setAutoMeasureEnabled(true);
        listUserPets.setLayoutManager(llmClinicImages);
        listUserPets.setHasFixedSize(true);
        listUserPets.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        listUserPets.setAdapter(new UserPetsAdapter(getActivity(), arrPets));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            /* CLEAR THE ARRAY, SHOW THE PROGRESS AND FETCH THE USER'S LIST OF PETS */
            arrPets.clear();
            linlaProgress.setVisibility(View.VISIBLE);
            listUserPets.setVisibility(View.GONE);
            fetchPetsList();
        }
    }

    /** CHECK IS THE USER HAS ALREADY ENROLLED FOR HOME BOARDING **/
    private void checkBoardingStatus() {
        BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);
        Call<Boarding> call = api.checkBoardingStatus(USER_ID);
        call.enqueue(new Callback<Boarding>() {
            @Override
            public void onResponse(Call<Boarding> call, Response<Boarding> response) {
                Boarding boarding = response.body();
                if (boarding != null)   {
                    String message = boarding.getMessage();
                    if (message != null)    {
                        if (message.equalsIgnoreCase("Home Boarding is enabled for this User"))   {

                            /* DISMISS THE DIALOG */
                            dialog.dismiss();

                            Intent intent = new Intent(getActivity(), UserHomeBoarding.class);
                            startActivity(intent);

                        } else if (message.equalsIgnoreCase("User hasn't enabled Home Boarding"))    {

                            /* DISMISS THE DIALOG */
                            dialog.dismiss();

                            Intent intent = new Intent(getActivity(), BoardingEnrollment.class);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Boarding> call, Throwable t) {
            }
        });
    }
}