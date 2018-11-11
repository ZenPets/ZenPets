package co.zenpets.doctors.creator.appointment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.adapters.visit.VisitReasonsAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.appointments.AppointmentData;
import co.zenpets.doctors.utils.models.appointments.AppointmentsAPI;
import co.zenpets.doctors.utils.models.doctors.DoctorsAPI;
import co.zenpets.doctors.utils.models.doctors.clients.Client;
import co.zenpets.doctors.utils.models.doctors.clients.ClientDetailsAPI;
import co.zenpets.doctors.utils.models.doctors.details.DoctorDetail;
import co.zenpets.doctors.utils.models.visit.Reason;
import co.zenpets.doctors.utils.models.visit.Reasons;
import co.zenpets.doctors.utils.models.visit.ReasonsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class AppointmentDetailsCreator extends AppCompatActivity {

    /** THE INCOMING DETAILS **/
    private String CLIENT_ID = null;
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;
    private String APPOINTMENT_TIME = null;
    private String APPOINTMENT_DATE = null;

    /** THE VISIT REASONS ARRAY LIST **/
    private ArrayList<Reason> arrReasons = new ArrayList<>();

    /** DATA TYPES FOR THE APPOINTMENT **/
    private String USER_ID = null;
    private final String PET_ID = null;
    private String VISIT_REASON_ID = null;

    /** THE PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtClinicDetails) AppCompatTextView txtClinicDetails;
    @BindView(R.id.txtDate) AppCompatTextView txtDate;
    @BindView(R.id.txtTime) AppCompatTextView txtTime;
    @BindView(R.id.imgvwUserProfile) SimpleDraweeView imgvwUserProfile;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.txtUserPhone) AppCompatTextView txtUserPhone;
    @BindView(R.id.spnVisitReason) AppCompatSpinner spnVisitReason;

    /** PUBLISH THE NEW APPOINTMENT **/
    @OnClick(R.id.btnBook) void newAppointment()    {
        publishNewAppointment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_details_creator);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* SELECT A VISIT REASON */
        spnVisitReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VISIT_REASON_ID = arrReasons.get(position).getVisitReasonID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** FETCH THE DOCTOR AND CLINIC DETAILS *****/
    private void fetchDoctorDetails()   {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<DoctorDetail> call = api.fetchDoctorDetails(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<DoctorDetail>() {
            @Override
            public void onResponse(@NonNull Call<DoctorDetail> call, @NonNull Response<DoctorDetail> response) {
                DoctorDetail data = response.body();
                if (data != null) {

                    /* GET AND SET THE DOCTOR'S PREFIX AND NAME */
                    String doctorPrefix = data.getDoctorPrefix();
                    String doctorName = data.getDoctorName();
                    txtDoctorName.setText(getString(R.string.appointment_creator_details_doctor, doctorPrefix, doctorName));

                    /* GET AND SET THE DOCTOR'S DISPLAY PROFILE */
                    String doctorDisplayProfile = data.getDoctorDisplayProfile();
                    if (doctorDisplayProfile != null) {
                        Uri uri = Uri.parse(doctorDisplayProfile);
                        imgvwDoctorProfile.setImageURI(uri);
                    }

                    /* GET THE CLINIC NAME */
                    String clinicName = data.getClinicName();
                    txtClinicDetails.setText(clinicName);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorDetail> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE CLIENT DETAILS *****/
    private void fetchClientDetails() {
        ClientDetailsAPI api = ZenApiClient.getClient().create(ClientDetailsAPI.class);
        Call<Client> call = api.fetchClientDetails(CLIENT_ID);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(@NonNull Call<Client> call, @NonNull Response<Client> response) {
                Client data = response.body();
                if (data != null)   {
                    USER_ID = data.getUserID();
                    if (USER_ID != null) {
                        /* GET THE USER DETAILS */
                        String userName = data.getUserName();
                        String userDisplayProfile = data.getUserDisplayProfile();
                        String userPhoneNumber = data.getUserPhoneNumber();

                        /* SET THE USER'S NAME */
                        txtUserName.setText(userName);

                        /* SET THE USER'S DISPLAY PROFILE */
                        Uri uriProfile = Uri.parse(userDisplayProfile);
                        imgvwUserProfile.setImageURI(uriProfile);

                        /* SET THE USER'S PHONE NUMBER */
                        txtUserPhone.setText(userPhoneNumber);
                    } else {
                        /* GET THE CLIENT DETAILS */
                        String clientName = data.getClientName();
                        String clientPhoneNumber = data.getClientPhoneNumber();

                        /* SET THE CLIENT'S NAME */
                        txtUserName.setText(clientName);

                        /* SET THE PLACEHOLDER IMAGE */
                        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ic_person_black_48dp).build();
                        imgvwUserProfile.setImageURI(request.getSourceUri());

                        /* SET THE CLIENT'S PHONE NUMBER */
                        txtUserPhone.setText(clientPhoneNumber);
                    }
                } else {
                }
            }

            @Override
            public void onFailure(@NonNull Call<Client> call, @NonNull Throwable t) {
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("CLIENT_ID")
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("CLINIC_ID")
                && bundle.containsKey("APPOINTMENT_TIME")
                && bundle.containsKey("APPOINTMENT_DATE")) {

            CLIENT_ID = bundle.getString("CLIENT_ID");
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            APPOINTMENT_TIME = bundle.getString("APPOINTMENT_TIME");
            APPOINTMENT_DATE = bundle.getString("APPOINTMENT_DATE");

            if (CLIENT_ID != null
                    && DOCTOR_ID != null && CLINIC_ID != null
                    && APPOINTMENT_TIME != null && APPOINTMENT_DATE != null) {

                /* FETCH THE DOCTOR DETAILS */
                fetchDoctorDetails();

                /* FETCH THE CLIENT DETAILS */
                fetchClientDetails();

                /* FETCH THE VISIT REASONS  */
                fetchVisitReasons();

                /* FORMAT THE INCOMING DATE */
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sdf.parse(APPOINTMENT_DATE + " " + APPOINTMENT_TIME));
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
                    String strFormattedDate = format.format(calendar.getTime());

                    /* SET THE SELECTED DATE */
                    txtDate.setText(strFormattedDate);

                    /* SET THE SELECTED TIME */
                    txtTime.setText(APPOINTMENT_TIME);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get the required data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get the required data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** FETCH THE VISIT REASONS  *****/
    private void fetchVisitReasons() {
        ReasonsAPI api = ZenApiClient.getClient().create(ReasonsAPI.class);
        Call<Reasons> call = api.visitReasons();
        call.enqueue(new Callback<Reasons>() {
            @Override
            public void onResponse(@NonNull Call<Reasons> call, @NonNull Response<Reasons> response) {
                /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
                arrReasons = response.body().getReasons();

                /* SET THE ADAPTER TO THE VISIT REASONS SPINNER */
                spnVisitReason.setAdapter(new VisitReasonsAdapter(AppointmentDetailsCreator.this, arrReasons));
            }

            @Override
            public void onFailure(@NonNull Call<Reasons> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
//                Log.e("REASONS FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Enter Client Details";
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
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** PUBLISH THE NEW APPOINTMENT *****/
    private void publishNewAppointment() {
        /* SHOW THE PROGRESS DIALOG PUBLISHING THE NEW APPOINTMENT **/
        dialog = new ProgressDialog(this);
        dialog.setMessage("Publishing your appointment....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /* PUBLISH THE NEW APPOINTMENT */
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentData> call = api.newDocAppointment(
                DOCTOR_ID, CLINIC_ID, VISIT_REASON_ID, USER_ID, PET_ID, CLIENT_ID,
                APPOINTMENT_DATE, APPOINTMENT_TIME,
                "Confirmed", "",
                String.valueOf(System.currentTimeMillis() / 1000)
        );
        call.enqueue(new Callback<AppointmentData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentData> call, @NonNull Response<AppointmentData> response) {
                if (response.isSuccessful())    {
                    /* DISMISS THE DIALOG */
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully published your new appointment", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error publishing your appointment. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentData> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }
}