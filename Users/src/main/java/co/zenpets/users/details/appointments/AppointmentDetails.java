package co.zenpets.users.details.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.users.R;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.appointment.Appointment;
import co.zenpets.users.utils.models.appointment.AppointmentsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetails extends AppCompatActivity {

    /** THE INCOMING APPOINTMENT ID **/
    String APPOINTMENT_ID = null;

    /** THE APPOINTMENT DATE AND TIME **/
    String APPOINTMENT_DATE = null;
    String APPOINTMENT_TIME = null;

    /** THE USER'S ID AND DEVICE TOKEN **/
    String USER_ID = null;
    String USER_DEVICE_TOKEN = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtTime) TextView txtTime;
    @BindView(R.id.txtUserName) TextView txtUserName;
    @BindView(R.id.txtVisitReason) TextView txtVisitReason;
    @BindView(R.id.txtPetDetails) TextView txtPetDetails;
    @BindView(R.id.txtAppointmentStatus) TextView txtAppointmentStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_details);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /** FETCH APPOINTMENT DETAILS **/
    private void fetchAppointmentDetails() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<Appointment> call = api.fetchAppointmentDetails(APPOINTMENT_ID);
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
//                Log.e("DETAILS RAW", String.valueOf(response.raw()));
                Appointment data = response.body();

                /* GET AND SET THE APPOINTMENT TIME */
                APPOINTMENT_TIME = data.getAppointmentTime();
                if (APPOINTMENT_TIME != null)   {
                    txtTime.setText(APPOINTMENT_TIME);
                }

                /* GET THE APPOINTMENT DATE */
                APPOINTMENT_DATE = data.getAppointmentDate();

                /* GET AND SET THE USER NAME */
                if (data.getUserName() != null) {
                    txtUserName.setText(data.getUserName());
                }

                /* GET THE USER'S ID */
                USER_ID = data.getUserID();

                /* SET THE VISIT REASON */
                if (data.getVisitReason() != null)  {
                    txtVisitReason.setText(data.getVisitReason());
                }

                /* SET THE PET'S DETAILS (BREED NAME, PET TYPE NAME AND PET AGE) */
                if (data.getBreedName() != null  && data.getPetTypeName() != null)   {
                    String breed = data.getBreedName();
                    String petType = data.getPetTypeName();
                    String combinedDetails = "The Pet is a \"" + petType + "\" of the Breed \"" + breed + "\"";
                    txtPetDetails.setText(combinedDetails);
                }

                /* SET THE APPOINTMENT STATUS */
                String appointmentStatus = data.getAppointmentStatus();

                if (appointmentStatus != null)    {
                    txtAppointmentStatus.setText(appointmentStatus);
                } else {
                    txtAppointmentStatus.setText("Pending");
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("APPOINTMENT_ID")) {
            APPOINTMENT_ID = bundle.getString("APPOINTMENT_ID");
            if (APPOINTMENT_ID != null) {
                /* FETCH APPOINTMENT DETAILS */
                fetchAppointmentDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}