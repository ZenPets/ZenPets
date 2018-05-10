package biz.zenpets.users.creator.appointment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.clinics.ratings.ClinicRating;
import biz.zenpets.users.utils.models.clinics.ratings.ClinicRatingsAPI;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.clinics.Clinic;
import biz.zenpets.users.utils.models.doctors.clinics.Clinics;
import biz.zenpets.users.utils.models.doctors.clinics.ClinicsAPI;
import biz.zenpets.users.utils.models.doctors.profile.DoctorProfile;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentClinicSelector extends AppCompatActivity {

    /** THE INCOMING DOCTOR ID **/
    private String DOCTOR_ID = null;

    /* THE DOCTOR CLINICS ARRAY LIST INSTANCE */
    ArrayList<Clinic> arrClinics = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listClinics) RecyclerView listClinics;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_clinic_selector);
        ButterKnife.bind(this);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /***** FETCH THE DOCTOR AND CLINIC DETAILS *****/
    private void fetchDoctorDetails()   {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<DoctorProfile> call = api.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfile>() {
            @Override
            public void onResponse(Call<DoctorProfile> call, Response<DoctorProfile> response) {
                DoctorProfile data = response.body();
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

                    /* SHOW THE PROGRESS AND FETCH THE LIST OF CLINICS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    listClinics.setVisibility(View.GONE);
                    fetchDoctorClinics();
                }
            }

            @Override
            public void onFailure(Call<DoctorProfile> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    private void fetchDoctorClinics() {
        ClinicsAPI api = ZenApiClient.getClient().create(ClinicsAPI.class);
        Call<Clinics> call = api.fetchDoctorClinics(DOCTOR_ID);
        call.enqueue(new Callback<Clinics>() {
            @Override
            public void onResponse(Call<Clinics> call, Response<Clinics> response) {
                if (response.body() != null && response.body().getClinics() != null)    {
                    arrClinics = response.body().getClinics();
                    if (arrClinics.size() > 0)  {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY REVIEWS VIEW */
                        listClinics.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listClinics.setAdapter(new ClinicSelectorAdapter(AppointmentClinicSelector.this, arrClinics));
                    } else {
                        /* SHOW THE NO CLINICS LAYOUT */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listClinics.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE NO CLINICS LAYOUT */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listClinics.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DOCTOR'S CLINICS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Clinics> call, Throwable t) {
                Log.e("CLINICS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /* GET THE INCOMING DATA */
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null) {
                /* FETCH THE DOCTOR DETAILS */
                fetchDoctorDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Select A Clinic";
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

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* CONFIGURE THE CLINICS RECYCLER VIEW */
        LinearLayoutManager clinics = new LinearLayoutManager(this);
        clinics.setOrientation(LinearLayoutManager.VERTICAL);
        clinics.setAutoMeasureEnabled(true);
        listClinics.setLayoutManager(clinics);
        listClinics.setHasFixedSize(true);
        listClinics.setNestedScrollingEnabled(false);
        listClinics.setAdapter(new ClinicSelectorAdapter(AppointmentClinicSelector.this, arrClinics));
    }

    /***** THE CLINIC SELECTOR ADAPTER ******/
    private class ClinicSelectorAdapter extends RecyclerView.Adapter<ClinicSelectorAdapter.ClinicsVH> {

        /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
        private final Activity activity;

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<Clinic> arrClinics;

        ClinicSelectorAdapter(Activity activity, ArrayList<Clinic> arrClinics) {

            /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
            this.activity = activity;

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrClinics = arrClinics;
        }

        @Override
        public int getItemCount() {
            return arrClinics.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final ClinicSelectorAdapter.ClinicsVH holder, final int position) {
            final Clinic data = arrClinics.get(position);

            /* SET THE CLINIC NAME */
            if (data.getClinicName() != null)   {
                holder.txtClinicName.setText(data.getClinicName());
            }

            /* SET THE CLINIC ADDRESS */
            if (data.getClinicAddress() != null) {
                String CLINIC_ADDRESS = data.getClinicAddress();
                String CLINIC_CITY = data.getCityName();
                String CLINIC_STATE = data.getStateName();
                String CLINIC_PIN_CODE = data.getClinicPinCode();
                holder.txtClinicAddress.setText(activity.getString(R.string.doctor_details_address_placeholder, CLINIC_ADDRESS, CLINIC_CITY, CLINIC_STATE, CLINIC_PIN_CODE));
            }

            /* FETCH THE CLINIC RATINGS */
            ClinicRatingsAPI api = ZenApiClient.getClient().create(ClinicRatingsAPI.class);
            Call<ClinicRating> call = api.fetchClinicRatings(data.getClinicID());
            call.enqueue(new Callback<ClinicRating>() {
                @Override
                public void onResponse(Call<ClinicRating> call, Response<ClinicRating> response) {
                    ClinicRating rating = response.body();
                    if (rating != null) {
                        String clinicRating = rating.getClinicRating();
                        if (clinicRating != null
                                && !clinicRating.equalsIgnoreCase("")
                                && !clinicRating.equalsIgnoreCase("null"))   {
                            holder.clinicRating.setVisibility(View.VISIBLE);
                            holder.clinicRating.setRating(Float.parseFloat(clinicRating));
                        } else {
                            holder.clinicRating.setVisibility(View.GONE);
                        }
                    } else {
                        holder.clinicRating.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ClinicRating> call, Throwable t) {
                    Log.e("RATINGS FAILURE", t.getMessage());
                    Crashlytics.logException(t);
                }
            });

            /* SEND THE DOCTOR ID AND CLINIC ID TO THE APPOINTMENT SLOT CREATOR */
            holder.linlaClinicContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentAppointment = new Intent(getApplicationContext(), AppointmentSlotCreator.class);
                    intentAppointment.putExtra("DOCTOR_ID", DOCTOR_ID);
                    intentAppointment.putExtra("CLINIC_ID", data.getClinicID());
                    startActivity(intentAppointment);
                }
            });
        }

        @NonNull
        @Override
        public ClinicSelectorAdapter.ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.doctor_clinics_item, parent, false);

            return new ClinicSelectorAdapter.ClinicsVH(itemView);
        }

        class ClinicsVH extends RecyclerView.ViewHolder	{
            LinearLayout linlaClinicContainer;
            AppCompatTextView txtClinicName;
            AppCompatRatingBar clinicRating;
            AppCompatTextView txtClinicAddress;
            ClinicsVH(View v) {
                super(v);
                linlaClinicContainer = v.findViewById(R.id.linlaClinicContainer);
                txtClinicName = v.findViewById(R.id.txtClinicName);
                clinicRating = v.findViewById(R.id.clinicRating);
                txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
            }
        }
    }
}