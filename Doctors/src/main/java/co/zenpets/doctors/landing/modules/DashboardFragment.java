package co.zenpets.doctors.landing.modules;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.calendar.CalendarActivity;
import co.zenpets.doctors.completer.EducationCompleter;
import co.zenpets.doctors.completer.ServiceCompleter;
import co.zenpets.doctors.completer.SpecializationCompleter;
import co.zenpets.doctors.completer.TimingsCompleter;
import co.zenpets.doctors.creator.appointment.AppointmentSlotCreator;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.appointments.AppointmentsAdapter;
import co.zenpets.doctors.utils.adapters.clinics.ClinicSelectorAdapter;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.appointments.AppointmentData;
import co.zenpets.doctors.utils.models.appointments.AppointmentsAPI;
import co.zenpets.doctors.utils.models.appointments.AppointmentsData;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinic;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinics;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinicsAPI;
import co.zenpets.doctors.utils.models.doctors.modules.Qualification;
import co.zenpets.doctors.utils.models.doctors.modules.Qualifications;
import co.zenpets.doctors.utils.models.doctors.modules.QualificationsAPI;
import co.zenpets.doctors.utils.models.doctors.modules.Service;
import co.zenpets.doctors.utils.models.doctors.modules.Services;
import co.zenpets.doctors.utils.models.doctors.modules.ServicesAPI;
import co.zenpets.doctors.utils.models.doctors.modules.Specialization;
import co.zenpets.doctors.utils.models.doctors.modules.Specializations;
import co.zenpets.doctors.utils.models.doctors.modules.SpecializationsAPI;
import co.zenpets.doctors.utils.models.doctors.modules.Timings;
import co.zenpets.doctors.utils.models.doctors.modules.TimingsAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileAPI;
import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** THE SELECTED CLINIC ID **/
    private String CLINIC_ID = null;

    /** THE APPOINTMENT DATE (CURRENT / TODAY'S) **/
    private String APPOINTMENT_DATE = null;

    /** THE CLINICS AND DOCTORS ARRAY LISTS **/
    private ArrayList<DoctorClinic> arrClinics = new ArrayList<>();

    /** THE APPOINTMENTS ADAPTER AND ARRAY LIST **/
    private ArrayList<AppointmentData> arrAppointments = new ArrayList<>();

    /** THE PROFILE COMPLETION PROGRESS AND STATUS TRACKERS **/
    private Integer PROFILE_COMPLETE = 0;
    private boolean blnEducation = false;
    private boolean blnSpecializations = false;
    private boolean blnServices = false;
    private boolean blnTimings = false;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProfileStatus) LinearLayout linlaProfileStatus;
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.pbProfileCompletion) ProgressBar pbProfileCompletion;
    @BindView(R.id.txtProgress) AppCompatTextView txtProgress;
    @BindView(R.id.linlaHomeContent) LinearLayout linlaHomeContent;
    @BindView(R.id.spnClinics) AppCompatSpinner spnClinics;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listAppointments) RecyclerView listAppointments;
    @BindView(R.id.linlaEmptyAppointments) LinearLayout linlaEmptyAppointments;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** VIEW ALL APPOINTMENTS **/
    @OnClick(R.id.txtViewAllAppointments) protected void allAppointments()  {
        Intent intent = new Intent(getActivity(), CalendarActivity.class);
        startActivity(intent);
    }

    /** ADD NEW APPOINTMENT **/
    @OnClick(R.id.txtAddAppointment) void newAppointment()  {
        Intent intent = new Intent(getActivity(), AppointmentSlotCreator.class);
        intent.putExtra("CLINIC_ID", CLINIC_ID);
        startActivityForResult(intent, 101);
    }

    /** ADD A NEW CLINIC / HOSPITAL **/
    @OnClick(R.id.txtEmpty) protected void newClinic()  {
//        Intent intent = new Intent(getActivity(), ClinicCreator.class);
//        startActivityForResult(intent, 101);
    }

    /** COMPLETE THE PROFILE **/
    @OnClick(R.id.btnCompleteProfile) void completeProfile()  {
        if (!blnServices)   {
            Intent intent = new Intent(getActivity(), EducationCompleter.class);
            intent.putExtra("DOCTOR_ID", DOCTOR_ID);
            startActivityForResult(intent, 102);
        } else if (!blnSpecializations) {
            Intent intent = new Intent(getActivity(), SpecializationCompleter.class);
            intent.putExtra("DOCTOR_ID", DOCTOR_ID);
            startActivityForResult(intent, 103);
        } else if (!blnEducation)   {
            Intent intent = new Intent(getActivity(), ServiceCompleter.class);
            intent.putExtra("DOCTOR_ID", DOCTOR_ID);
            startActivityForResult(intent, 104);
        } else if (!blnTimings) {
            Intent intent = new Intent(getActivity(), TimingsCompleter.class);
            intent.putExtra("DOCTOR_ID", DOCTOR_ID);
            intent.putExtra("CLINIC_ID", CLINIC_ID);
            startActivityForResult(intent, 105);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.home_dashboard_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE */
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU */
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES */
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE CURRENT DATE */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        APPOINTMENT_DATE = sdf.format(cal.getTime());

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* GET THE DOCTOR ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* FETCH THE DOCTOR'S CLINICS */
        fetchDoctorClinics();

        /* SELECT A CLINIC */
        spnClinics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED CLINIC'S ID */
                CLINIC_ID = arrClinics.get(position).getClinicID();

                /* CLEAR THE APPOINTMENTS ARRAY */
                if (arrAppointments != null)
                    arrAppointments.clear();

                /* SET THE PROFILE COMPLETE TO DEFAULT */
                PROFILE_COMPLETE = 0;

                /* SHOW THE PROGRESS AND FETCH THE LIST OF UPCOMING APPOINTMENTS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchUpcomingAppointments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* FETCH THE DOCTOR'S PROFILE */
        fetchDoctorProfile();
    }

    /***** FETCH THE DOCTOR'S CLINICS *****/
    private void fetchDoctorClinics() {
        DoctorClinicsAPI api = ZenApiClient.getClient().create(DoctorClinicsAPI.class);
        Call<DoctorClinics> call = api.fetchDoctorClinics(DOCTOR_ID);
        call.enqueue(new Callback<DoctorClinics>() {
            @Override
            public void onResponse(@NonNull Call<DoctorClinics> call, @NonNull Response<DoctorClinics> response) {
                if (response.body() != null && response.body().getClinics() != null)    {
                    arrClinics = response.body().getClinics();
                    if (arrClinics != null && arrClinics.size() > 0)    {
                        spnClinics.setAdapter(new ClinicSelectorAdapter(getActivity(),
                                R.layout.clinic_selector_row,
                                arrClinics));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorClinics> call, @NonNull Throwable t) {
            }
        });
    }

    /** FETCH THE LIST OF UPCOMING APPOINTMENTS **/
    private void fetchUpcomingAppointments() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentsData> call = api.fetchDoctorTodayAppointments(DOCTOR_ID, CLINIC_ID, APPOINTMENT_DATE);
        call.enqueue(new Callback<AppointmentsData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentsData> call, @NonNull Response<AppointmentsData> response) {
//                Log.e("RESPONSE", String.valueOf(response.raw()));
                arrAppointments = response.body().getAppointments();
                if (arrAppointments != null && arrAppointments.size() > 0)    {

                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT  */
                    listAppointments.setVisibility(View.VISIBLE);
                    linlaEmptyAppointments.setVisibility(View.GONE);

                    /* SET THE CLIENT APPOINTMENTS ADAPTER TO THE RECYCLER VIEW */
                    listAppointments.setAdapter(new AppointmentsAdapter(getActivity(), arrAppointments));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmptyAppointments.setVisibility(View.VISIBLE);
                    listAppointments.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentsData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
            }
        });

        /* GET THE PROFILE COMPLETION STATUS */
        pbProfileCompletion.setMax(100);
        pbProfileCompletion.setProgress(PROFILE_COMPLETE);

        /* CHECK IF THE DOCTOR HAS ADDED THEIR EDUCATION */
        fetchDoctorEducation();

//        /* CHECK IF THE DOCTOR HAS ADDED THEIR SPECIALIZATIONS */
//        fetchDoctorSpecializations();
//
//        /* CHECK IF THE DOCTOR HAS ADDED THEIR SERVICES */
//        fetchDoctorServices();
//
//        /* CHECK IF THE DOCTOR HAS ADDED THEIR TIMINGS */
//        fetchDoctorTimings();
    }

    /***** FETCH THE DOCTOR'S EDUCATION *****/
    private void fetchDoctorEducation() {
        QualificationsAPI apiInterface = ZenApiClient.getClient().create(QualificationsAPI.class);
        Call<Qualifications> call = apiInterface.fetchDoctorEducation(DOCTOR_ID);
        call.enqueue(new Callback<Qualifications>() {
            @Override
            public void onResponse(@NonNull Call<Qualifications> call, @NonNull Response<Qualifications> response) {
                ArrayList<Qualification> arrEducation = response.body().getQualifications();
                if (arrEducation != null && arrEducation.size() > 0)    {
                    PROFILE_COMPLETE = PROFILE_COMPLETE + 25;
                    pbProfileCompletion.setMax(100);
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnEducation = true;
                } else {
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnEducation = false;
                }

                /* CHECK IF THE DOCTOR HAS ADDED THEIR SPECIALIZATIONS */
                fetchDoctorSpecializations();
            }

            @Override
            public void onFailure(@NonNull Call<Qualifications> call, @NonNull Throwable t) {
            }
        });
    }

    /***** FETCH THE DOCTOR'S SPECIALIZATIONS *****/
    private void fetchDoctorSpecializations() {
        SpecializationsAPI apiInterface = ZenApiClient.getClient().create(SpecializationsAPI.class);
        Call<Specializations> call = apiInterface.fetchDoctorSpecializations(DOCTOR_ID);
        call.enqueue(new Callback<Specializations>() {
            @Override
            public void onResponse(@NonNull Call<Specializations> call, @NonNull Response<Specializations> response) {
                ArrayList<Specialization> arrSpecialization = response.body().getSpecializations();
                if (arrSpecialization != null && arrSpecialization.size() > 0)  {
                    PROFILE_COMPLETE = PROFILE_COMPLETE + 25;
                    pbProfileCompletion.setMax(100);
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnSpecializations = true;
                } else {
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnSpecializations = false;
                }

                /* CHECK IF THE DOCTOR HAS ADDED THEIR SERVICES */
                fetchDoctorServices();
            }

            @Override
            public void onFailure(@NonNull Call<Specializations> call, @NonNull Throwable t) {
            }
        });
    }

    /***** FETCH THE DOCTOR'S SERVICES *****/
    private void fetchDoctorServices() {
        ServicesAPI apiInterface = ZenApiClient.getClient().create(ServicesAPI.class);
        Call<Services> call = apiInterface.fetchDoctorServices(DOCTOR_ID);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(@NonNull Call<Services> call, @NonNull Response<Services> response) {
                ArrayList<Service> arrService = response.body().getServices();
                if (arrService != null && arrService.size() > 0)    {
                    PROFILE_COMPLETE = PROFILE_COMPLETE + 25;
                    pbProfileCompletion.setMax(100);
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnServices = true;
                } else {
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnServices = false;
                }

                /* CHECK IF THE DOCTOR HAS ADDED THEIR TIMINGS */
                fetchDoctorTimings();
            }

            @Override
            public void onFailure(@NonNull Call<Services> call, @NonNull Throwable t) {
            }
        });
    }

    /***** FETCH THE DOCTOR'S TIMINGS *****/
    private void fetchDoctorTimings() {
        TimingsAPI apiInterface = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = apiInterface.fetchDoctorTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                String timingsID = response.body().getTimingsID();
                if (timingsID != null)  {
                    PROFILE_COMPLETE = PROFILE_COMPLETE + 25;
                    pbProfileCompletion.setMax(100);
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnTimings = true;
                } else {
                    pbProfileCompletion.setProgress(PROFILE_COMPLETE);
                    txtProgress.setText(PROFILE_COMPLETE + "%");

                    /* TOGGLE THE STATUS */
                    blnTimings = false;
                }

                /* SHOW OR HIDE THE PROFILE PROGRESS LAYOUT */
                if (PROFILE_COMPLETE >= 100)    {
                    linlaProfileStatus.setVisibility(View.GONE);
                } else if (PROFILE_COMPLETE < 100){
                    linlaProfileStatus.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAppointments.setLayoutManager(manager);
        listAppointments.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listAppointments.setAdapter(new AppointmentsAdapter(getActivity(), arrAppointments));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Home";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    /***** FETCH THE DOCTOR'S PROFILE *****/
    private void fetchDoctorProfile() {
        DoctorProfileAPI apiInterface = ZenApiClient.getClient().create(DoctorProfileAPI.class);
        Call<DoctorProfileData> call = apiInterface.fetchDoctorProfile(DOCTOR_ID);
        call.enqueue(new Callback<DoctorProfileData>() {
            @Override
            public void onResponse(@NonNull Call<DoctorProfileData> call, @NonNull Response<DoctorProfileData> response) {
                /* GET THE DOCTOR'S PREFIX AND NAME */
                String DOCTOR_PREFIX = response.body().getDoctorPrefix();
                String DOCTOR_NAME = response.body().getDoctorName();
                if (DOCTOR_NAME != null)    {
                    txtDoctorName.setText(DOCTOR_PREFIX + " " + DOCTOR_NAME);
                }

                /* GET THE DOCTOR'S DISPLAY PROFILE */
                String DOCTOR_DISPLAY_PROFILE = response.body().getDoctorDisplayProfile();
                if (DOCTOR_DISPLAY_PROFILE != null) {
                    Uri uri = Uri.parse(DOCTOR_DISPLAY_PROFILE);
                    imgvwDoctorProfile.setImageURI(uri);
//                    Glide.with(getActivity())
//                            .load(DOCTOR_DISPLAY_PROFILE)
//                            .apply(new RequestOptions()
//                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                    .centerCrop())
//                            .into(imgvwDoctorProfile);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorProfileData> call, @NonNull Throwable t) {
//                Log.e("FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)   {
            if (requestCode == 101) {
                /* CLEAR THE APPOINTMENTS ARRAY */
                if (arrAppointments != null)
                    arrAppointments.clear();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF UPCOMING APPOINTMENTS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchUpcomingAppointments();
            } else if (requestCode == 102)  {
                /* CHECK IF THE DOCTOR HAS ADDED THEIR EDUCATION */
                fetchDoctorEducation();
            } else if (requestCode == 103)  {
                /* CHECK IF THE DOCTOR HAS ADDED THEIR SPECIALIZATIONS */
                fetchDoctorSpecializations();
            } else if (requestCode == 104)  {
                /* CHECK IF THE DOCTOR HAS ADDED THEIR SERVICES */
                fetchDoctorServices();
            } else if (requestCode == 105)  {
                /* CHECK IF THE DOCTOR HAS ADDED THEIR TIMINGS */
                fetchDoctorTimings();
            }
        }
    }
}