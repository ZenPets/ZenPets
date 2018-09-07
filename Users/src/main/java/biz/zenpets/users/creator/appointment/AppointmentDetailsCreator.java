package biz.zenpets.users.creator.appointment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.pet.NewPetCreator;
import biz.zenpets.users.utils.adapters.pet.PetSpinnerAdapter;
import biz.zenpets.users.utils.adapters.visit.VisitReasonsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.pets.pet.FetchUserPets;
import biz.zenpets.users.utils.helpers.pets.pet.FetchUserPetsInterface;
import biz.zenpets.users.utils.models.appointment.Appointment;
import biz.zenpets.users.utils.models.appointment.AppointmentsAPI;
import biz.zenpets.users.utils.models.appointment.client.Client;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.pets.pets.Pet;
import biz.zenpets.users.utils.models.user.UserData;
import biz.zenpets.users.utils.models.user.UsersAPI;
import biz.zenpets.users.utils.models.visit.Reason;
import biz.zenpets.users.utils.models.visit.Reasons;
import biz.zenpets.users.utils.models.visit.ReasonsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class AppointmentDetailsCreator extends AppCompatActivity implements FetchUserPetsInterface {

    /** THE INCOMING DETAILS **/
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;
    private String APPOINTMENT_TIME = null;
    private String APPOINTMENT_DATE = null;

    /** THE USER DETAILS **/
    private String USER_AUTH_ID = null;
    private String USER_ID = null;

    /** THE PET DETAILS **/
    private String PET_ID = null;

    /** THE VISIT REASON **/
    private String VISIT_REASON_ID = null;

    /** THE VISIT REASONS ADAPTER AND ARRAY LIST **/
    private ArrayList<Reason> arrReasons = new ArrayList<>();

    /** THE USER PETS ARRAY LIST **/
    private ArrayList<Pet> arrPets = new ArrayList<>();

    /** THE PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtClinicDetails) AppCompatTextView txtClinicDetails;
    @BindView(R.id.txtDateTime) AppCompatTextView txtDateTime;
    @BindView(R.id.inputUserName) TextInputLayout inputUserName;
    @BindView(R.id.edtUserName) AppCompatEditText edtUserName;
    @BindView(R.id.inputEmailAddress) TextInputLayout inputEmailAddress;
    @BindView(R.id.edtEmailAddress) AppCompatEditText edtEmailAddress;
    @BindView(R.id.inputPhoneNumber) TextInputLayout inputPhoneNumber;
    @BindView(R.id.edtPhoneNumber) AppCompatEditText edtPhoneNumber;
    @BindView(R.id.spnPet) AppCompatSpinner spnPet;
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

        /* SELECT A PET */
        spnPet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PET_ID = arrPets.get(position).getPetID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
        Call<Doctor> call = api.fetchDoctorDetails(DOCTOR_ID, CLINIC_ID, null, null);
        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {
                Doctor data = response.body();
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
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        imgvwDoctorProfile.setImageURI(request.getSourceUri());
                    }

                    /* GET THE CLINIC DETAILS */
                    String clinicName = data.getClinicName();
                    String cityName = data.getCityName();
                    String localityName = data.getLocalityName();
                    String clinicAddress = localityName + ", " + cityName;
                    txtClinicDetails.setText(getString(R.string.appointment_creator_clinic_details_placeholder, clinicName, clinicAddress));
                }
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("CLINIC_ID")
                && bundle.containsKey("APPOINTMENT_TIME")
                && bundle.containsKey("APPOINTMENT_DATE")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            APPOINTMENT_TIME = bundle.getString("APPOINTMENT_TIME");
            APPOINTMENT_DATE = bundle.getString("APPOINTMENT_DATE");

            if (CLINIC_ID != null && DOCTOR_ID != null && APPOINTMENT_TIME != null && APPOINTMENT_DATE != null) {
                /* FETCH THE DOCTOR DETAILS */
                fetchDoctorDetails();

                /* FETCH THE USER DETAILS */
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    /* GET THE USER AUTH ID */
                    USER_AUTH_ID = user.getUid();

                    /* FETCH THE USER DETAILS */
                    fetchUserDetails();

                    /* FETCH VISIT REASONS */
                    fetchVisitReasons();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
                    finish();
                }

                /* FORMAT THE INCOMING DATE */
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(sdf.parse(APPOINTMENT_DATE + " " + APPOINTMENT_TIME));
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
                    String strFormattedDate = format.format(calendar.getTime());

                    /* SET THE SELECTED DATE AND TIME */
                    txtDateTime.setText(getString(R.string.appointment_creator_date_time_placeholder, strFormattedDate, APPOINTMENT_TIME));
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

    @Override
    public void userPets(ArrayList<Pet> data) {
        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
        arrPets = data;

        /* CHECK FOR THE SIZE OF THE RESULT */
        if (arrPets.size() > 0) {
            /* INSTANTIATE THE PETS SPINNER ADAPTER */
            PetSpinnerAdapter petsAdapter = new PetSpinnerAdapter(AppointmentDetailsCreator.this, arrPets);

            /* SET THE ADAPTER TO THE PETS SPINNER */
            spnPet.setAdapter(petsAdapter);
        } else {
            /* SHOW THE NO PETS FOUND DIALOG */
            noPetsFound();
        }
    }

    /***** FETCH VISIT REASONS *****/
    private void fetchVisitReasons() {
        ReasonsAPI api = ZenApiClient.getClient().create(ReasonsAPI.class);
        Call<Reasons> call = api.visitReasons();
        call.enqueue(new Callback<Reasons>() {
            @Override
            public void onResponse(Call<Reasons> call, Response<Reasons> response) {
                arrReasons = response.body().getReasons();

                /* SET THE ADAPTER TO THE VISIT REASONS SPINNER */
                spnVisitReason.setAdapter(new VisitReasonsAdapter(AppointmentDetailsCreator.this, arrReasons));
            }

            @Override
            public void onFailure(@NonNull Call<Reasons> call, Throwable t) {
//                Log.e("REASONS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE USER DETAILS *****/
    private void fetchUserDetails() {
        UsersAPI api = ZenApiClient.getClient().create(UsersAPI.class);
        Call<UserData> call = api.fetchProfile(USER_AUTH_ID);
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                UserData data = response.body();
                if (data != null)   {
                    /* GET THE USER'S ID */
                    USER_ID = data.getUserID();

                    /* SET THE USER'S NAME */
                    edtUserName.setText(data.getUserName());

                    /* SET THE USER'S EMAIL ADDRESS */
                    edtEmailAddress.setText(data.getUserEmail());

                    /* SET THE USER'S PHONE NUMBER */
                    edtPhoneNumber.setText(data.getUserPhoneNumber());

                    /* FETCH THE USER'S PETS */
                    new FetchUserPets(AppointmentDetailsCreator.this).execute(USER_ID);
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Enter contact details";
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

        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtUserName.getWindowToken(), 0);
        }

        /* PUBLISH THE NEW APPOINTMENT */
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<Appointment> call = api.newDocAppointment(
                DOCTOR_ID, CLINIC_ID, VISIT_REASON_ID, USER_ID, PET_ID,
                APPOINTMENT_DATE, APPOINTMENT_TIME,
                "Pending", "",
                String.valueOf(System.currentTimeMillis() / 1000)
        );
        call.enqueue(new Callback<Appointment>() {
            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful())    {
                    /* CHECK IF THE CLIENT EXISTS ON THE DOCTOR'S LIST */
                    checkClientRecord();
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "There was an error publishing your appointment. Please try again",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CHECK IF THE CLIENT EXISTS ON THE DOCTOR'S LIST *****/
    private void checkClientRecord() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<Client> call = api.clientExists(USER_ID);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                Client client = response.body();
                if (response.isSuccessful() && client != null)    {
                    String message = client.getMessage();
                    if (message.equalsIgnoreCase("Client record doesn't exist..."))   {
                        /* CREATE THE CLIENTS RECORD */
                        createClientRecord();
                    } else if (message.equalsIgnoreCase("Client record exists..."))    {
                        /* DISMISS THE DIALOG */
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Successfully published your new appointment", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CREATE THE CLIENT'S RECORD *****/
    private void createClientRecord() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<Client> call = api.newClient(DOCTOR_ID, USER_ID);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
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
            public void onFailure(Call<Client> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            /* CLEAR THE PETS ARRAY LIST */
            arrPets.clear();

            /* FETCH THE LIST OF PETS AGAIN */
            new FetchUserPets(AppointmentDetailsCreator.this).execute(USER_ID);
        }
    }

    /***** SHOW THE NO PETS FOUND DIALOG *****/
    private void noPetsFound() {
        new MaterialDialog.Builder(AppointmentDetailsCreator.this)
                .title("No pets found...")
                .content("You haven't added any Pets to your account yet. Before you can book an online appointment, you must have the Pet you are seeking an appointment for added to your account.\n\nTo add a Pet now, click the \"ADD PET\" button. Or, select the \"CANCEL\" button to exit this page...")
                .positiveText("Add Pet")
                .negativeText("Cancel")
                .theme(Theme.LIGHT)
                .icon(ContextCompat.getDrawable(AppointmentDetailsCreator.this, R.drawable.ic_info_outline_black_24dp))
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent addNewPet = new Intent(AppointmentDetailsCreator.this, NewPetCreator.class);
                        startActivityForResult(addNewPet, 101);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}