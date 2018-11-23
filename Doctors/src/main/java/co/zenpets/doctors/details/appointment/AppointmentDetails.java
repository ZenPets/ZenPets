package co.zenpets.doctors.details.appointment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.appointments.AppointmentData;
import co.zenpets.doctors.utils.models.appointments.AppointmentsAPI;
import co.zenpets.doctors.utils.models.appointments.notifications.AppointmentNotification;
import co.zenpets.doctors.utils.models.appointments.notifications.AppointmentNotificationAPI;
import co.zenpets.doctors.utils.models.doctors.Doctor;
import co.zenpets.doctors.utils.models.doctors.DoctorsAPI;
import co.zenpets.doctors.utils.models.users.UserData;
import co.zenpets.doctors.utils.models.users.UsersAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetails extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE INCOMING APPOINTMENT ID **/
    private String APPOINTMENT_ID = null;

    /** THE APPOINTMENT DATE AND TIME **/
    private String APPOINTMENT_DATE = null;
    private String APPOINTMENT_TIME = null;

    /** THE USER'S ID AND DEVICE TOKEN **/
    private String USER_ID = null;
    private String USER_DEVICE_TOKEN = null;

    /** THE DOCTOR'S DETAILS **/
    private String DOCTOR_ID = null;
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_DISPLAY_PROFILE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.txtTime) TextView txtTime;
    @BindView(R.id.txtUserName) TextView txtUserName;
    @BindView(R.id.txtVisitReason) TextView txtVisitReason;
    @BindView(R.id.txtPetDetails) TextView txtPetDetails;
    @BindView(R.id.txtAppointmentStatus) TextView txtAppointmentStatus;
    @BindView(R.id.btnConfirm) Button btnConfirm;
    @BindView(R.id.btnCancel) Button btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_details);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* GET THE DOCTOR'S ID AND FETCH THE DOCTOR'S DETAILS */
        DOCTOR_ID = getApp().getDoctorID();
        fetchDoctorDetails();
    }

    /** FETCH THE APPOINTMENT DETAILS **/
    private void fetchAppointmentDetails() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentData> call = api.fetchAppointmentDetails(APPOINTMENT_ID);
        call.enqueue(new Callback<AppointmentData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentData> call, @NonNull Response<AppointmentData> response) {
//                Log.e("DETAILS RAW", String.valueOf(response.raw()));
                AppointmentData data = response.body();

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
                    if (appointmentStatus.equalsIgnoreCase("Confirmed"))    {
                        btnConfirm.setEnabled(false);
                        btnCancel.setEnabled(false);
                    } else {
                        btnConfirm.setEnabled(true);
                        btnCancel.setEnabled(true);
                    }
                } else {
                    txtAppointmentStatus.setText(getString(R.string.app_det_app_status_pending));
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);

                /* GET THE USER'S DEVICE TOKEN */
                fetchUserDeviceToken();
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentData> call, @NonNull Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** GET THE USER'S DEVICE TOKEN **/
    private void fetchUserDeviceToken() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchUserToken(USER_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull Response<UserData> response) {
                /* GET THE USER'S DEVICE TOKEN */
                USER_DEVICE_TOKEN = response.body().getUserToken();
//                Log.e("DEVICE TOKEN", USER_DEVICE_TOKEN);
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
//                Log.e("TOKEN FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE DOCTOR'S DETAILS **/
    private void fetchDoctorDetails() {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctor> call = api.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(@NonNull Call<Doctor> call, @NonNull Response<Doctor> response) {
                Doctor doctor = response.body();

                /* GET THE DOCTOR'S PREFIX AND NAME */
                DOCTOR_PREFIX = doctor.getDoctorPrefix();
                DOCTOR_NAME = doctor.getDoctorName();

                /* GET THE DOCTOR'S DISPLAY PROFILE */
                DOCTOR_DISPLAY_PROFILE = doctor.getDoctorDisplayProfile();
            }

            @Override
            public void onFailure(@NonNull Call<Doctor> call, @NonNull Throwable t) {
//                Log.e("PROFILE FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("APPOINTMENT_ID"))   {
            APPOINTMENT_ID = bundle.getString("APPOINTMENT_ID");
            if (APPOINTMENT_ID != null) {
                /* SHOW THE PROGRESS AND FETCH THE APPOINTMENT DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
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

    /** CONFIRM THE APPOINTMENT **/
    @OnClick(R.id.btnConfirm) void confirmAppointment() {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        final ProgressDialog progressDialog = new ProgressDialog(AppointmentDetails.this);
        progressDialog.setMessage("Updating appointment...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentData> call = api.updateAppointmentStatus(
                APPOINTMENT_ID,
                "Confirmed");
        call.enqueue(new Callback<AppointmentData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentData> call, @NonNull Response<AppointmentData> response) {
                if (response.isSuccessful())    {
                    /* SEND CONFIRMATION NOTIFICATION TO USER */
                    sendConfirmationNotification();
                    Toast.makeText(getApplicationContext(), "The Appointment was confirmed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "There was a problem confirming the Appointment...", Toast.LENGTH_SHORT).show();
                }

                /* DISMISS THE DIALOG */
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentData> call, @NonNull Throwable t) {
//                Log.e("CONFIRM FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** CANCEL THE APPOINTMENT **/
    @OnClick(R.id.btnCancel) void cancelAppointment()   {
        new MaterialDialog.Builder(AppointmentDetails.this)
                .title("Add a note")
                .content("Add a note to inform the User why this Appointment has been canceled. Write as much information as required.")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(20, 500)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .positiveText("Post Note")
                .negativeText("Cancel")
                .input("Add a note stating the reason for cancelling the appointment....", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
                        Call<AppointmentData> call = api.cancelAppointment(
                                APPOINTMENT_ID,
                                input.toString(),
                                "Cancelled");
                        call.enqueue(new Callback<AppointmentData>() {
                            @Override
                            public void onResponse(@NonNull Call<AppointmentData> call, @NonNull Response<AppointmentData> response) {
                                if (response.isSuccessful())    {
                                    /* SEND CANCELLATION NOTIFICATION TO USER */
                                    sendCancellationNotification();
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Appointment has been cancelled", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "There was an error cancelling the appointment", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppointmentData> call, @NonNull Throwable t) {
//                                Log.e("FAILURE", t.getMessage());
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
                })
                .show();
    }

    /** SEND CONFIRMATION NOTIFICATION TO USER **/
    private void sendConfirmationNotification() {
        AppointmentNotificationAPI api = ZenApiClient.getClient().create(AppointmentNotificationAPI.class);
        Call<AppointmentNotification> call = api.sendUserAppointmentNotification(
                USER_DEVICE_TOKEN, "Appointment updated by " + DOCTOR_PREFIX + " " + DOCTOR_NAME,
                "Update on your appointment",
                "Appointment Update", APPOINTMENT_ID, APPOINTMENT_DATE, APPOINTMENT_TIME,
                DOCTOR_ID, DOCTOR_PREFIX, DOCTOR_NAME, DOCTOR_DISPLAY_PROFILE
        );
        call.enqueue(new Callback<AppointmentNotification>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentNotification> call, @NonNull Response<AppointmentNotification> response) {
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentNotification> call, @NonNull Throwable t) {
//                Log.e("CONFIRM FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** SEND CANCELLATION NOTIFICATION TO USER **/
    private void sendCancellationNotification() {
    }
}