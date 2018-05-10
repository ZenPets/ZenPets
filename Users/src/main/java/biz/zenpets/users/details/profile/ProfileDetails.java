package biz.zenpets.users.details.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import biz.zenpets.users.R;
import biz.zenpets.users.modifier.profile.ProfileModifier;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetails extends AppCompatActivity {

    /** THE USER'S EMAIL ADDRESS **/
    private String USER_AUTH_ID = null;

    /** STRINGS TO HOLD THE USER DATA **/
    private String USER_NAME = null;
    private String USER_DISPLAY_PROFILE = null;
    private String USER_EMAIL = null;
    private String USER_PHONE_NUMBER = null;
    private String USER_CITY = null;
    private String USER_STATE = null;
    private String USER_COUNTRY = null;
    private String USER_GENDER = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwProfilePicture) SimpleDraweeView imgvwProfilePicture;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
    @BindView(R.id.txtPhoneNumber) AppCompatTextView txtPhoneNumber;
    @BindView(R.id.txtCity) AppCompatTextView txtCity;
    @BindView(R.id.txtGender) AppCompatTextView txtGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE USER LOGGED IN USER EMAIL ADDRESS */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_AUTH_ID = user.getUid();
            /* FETCH THE USER PROFILE */
            fetchUserProfile();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE USER'S PROFILE *****/
    private void fetchUserProfile() {
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

                    /* SET THE USER'S EMAIL ADDRESS */
                    USER_EMAIL = data.getUserEmail();
                    txtEmailAddress.setText(USER_EMAIL);

                    /* SET THE USER'S PHONE NUMBER */
                    USER_PHONE_NUMBER = data.getUserPhoneNumber();
                    txtPhoneNumber.setText(USER_PHONE_NUMBER);

                    /* SET THE USER'S GENDER */
                    USER_GENDER = data.getUserGender();
                    txtGender.setText(USER_GENDER);

                    /* SET THE USER'S LOCATION */
                    USER_CITY = data.getCityName();
                    USER_STATE = data.getStateName();
                    USER_COUNTRY = data.getCountryName();
                    txtCity.setText(getString(R.string.profile_details_location_placeholder, USER_CITY, USER_STATE, USER_COUNTRY));
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Profile";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(ProfileDetails.this);
        inflater.inflate(R.menu.activity_common_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuEdit:
                /* MODIFY THE PROFILE */
                Intent intent = new Intent(getApplicationContext(), ProfileModifier.class);
                startActivityForResult(intent, 101);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            /* FETCH THE USER PROFILE */
            fetchUserProfile();
        }
    }
}