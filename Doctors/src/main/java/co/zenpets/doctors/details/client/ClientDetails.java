package co.zenpets.doctors.details.client;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.doctors.clients.ClientAppointmentsAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.clients.Client;
import co.zenpets.doctors.utils.models.doctors.clients.ClientAppointment;
import co.zenpets.doctors.utils.models.doctors.clients.ClientAppointments;
import co.zenpets.doctors.utils.models.doctors.clients.ClientAppointmentsAPI;
import co.zenpets.doctors.utils.models.doctors.clients.ClientDetailsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING CLIENT ID **/
    private String CLIENT_ID = null;

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** STRINGS TO HOLD THE USER'S INFORMATION **/
    private String USER_ID = null;
    private String USER_NAME = null;
    private String USER_EMAIL = null;
    private String USER_DISPLAY_PROFILE = null;
    private String USER_PHONE_NUMBER = null;
    private String USER_GENDER = null;

    /* THE CLIENT APPOINTMENTS ARRAY LIST */
    private ArrayList<ClientAppointment> arrClientAppointments = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwUserProfile) SimpleDraweeView imgvwUserProfile;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.txtUserPhone) AppCompatTextView txtUserPhone;
    @BindView(R.id.txtUserEmail) AppCompatTextView txtUserEmail;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listClientAppointments) RecyclerView listClientAppointments;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_details);
        ButterKnife.bind(this);

        /* GET THE LOGGED IN DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE ACTIONBAR */
        configAB();
    }

    /***** FETCH THE CLIENT DETAILS *****/
    private void fetchClientDetails() {
        ClientDetailsAPI api = ZenApiClient.getClient().create(ClientDetailsAPI.class);
        Call<Client> call = api.fetchClientDetails(CLIENT_ID);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull Response<Client> response) {
                USER_ID = response.body().getUserID();
                USER_NAME = response.body().getUserName();
                USER_EMAIL = response.body().getUserEmail();
                USER_DISPLAY_PROFILE = response.body().getUserDisplayProfile();
                USER_PHONE_NUMBER = response.body().getUserPhoneNumber();
                USER_GENDER = response.body().getUserGender();

                /* SET THE USER'S DISPLAY PROFILE */
                if (USER_DISPLAY_PROFILE != null)   {
                    Uri uriProfile = Uri.parse(USER_DISPLAY_PROFILE);
                    imgvwUserProfile.setImageURI(uriProfile);
                }

                /* SET THE USER'S NAME */
                if (USER_NAME != null)  {
                    txtUserName.setText(USER_NAME);
                }

                /* SET THE USER'S PHONE NUMBER */
                if (USER_PHONE_NUMBER != null)  {
                    txtUserPhone.setText(USER_PHONE_NUMBER);
                } else {
                    txtUserEmail.setText("No phone available");
                }

                /* SET THE USER'S EMAIL ADDRESS */
                if (USER_EMAIL != null) {
                    txtUserEmail.setText(USER_EMAIL);
                } else {
                    txtUserEmail.setText("No email available");
                }

                /* SHOW THE PROGRESS AND FETCH THE LIST OF APPOINTMENTS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchClientAppointments();
            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {
            }
        });
    }

    private void fetchClientAppointments() {
        ClientAppointmentsAPI apiInterface = ZenApiClient.getClient().create(ClientAppointmentsAPI.class);
        Call<ClientAppointments> call = apiInterface.getClientAppointments(DOCTOR_ID, USER_ID);
        call.enqueue(new Callback<ClientAppointments>() {
            @Override
            public void onResponse(@NonNull Call<ClientAppointments> call, @NonNull Response<ClientAppointments> response) {
                arrClientAppointments = response.body().getAppointments();

                /* CHECK IF RESULTS WERE RETURNED */
                if (arrClientAppointments.size() > 0)  {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
                    listClientAppointments.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE CLIENT APPOINTMENTS ADAPTER TO THE RECYCLER VIEW */
                    listClientAppointments.setAdapter(new ClientAppointmentsAdapter(ClientDetails.this, arrClientAppointments));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listClientAppointments.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ClientAppointments> call, @NonNull Throwable t) {
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("CLIENT_ID"))   {
            CLIENT_ID = bundle.getString("CLIENT_ID");
            if (CLIENT_ID != null)  {
                /* FETCH THE CLIENT DETAILS */
                fetchClientDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Client Details";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listClientAppointments.setLayoutManager(manager);
        listClientAppointments.setHasFixedSize(true);
        listClientAppointments.setNestedScrollingEnabled(true);

        /* SET THE CLIENT APPOINTMENTS ADAPTER */
        listClientAppointments.setAdapter(new ClientAppointmentsAdapter(this, arrClientAppointments));
    }
}