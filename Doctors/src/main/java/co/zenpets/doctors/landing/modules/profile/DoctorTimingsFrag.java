package co.zenpets.doctors.landing.modules.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;


import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.modifier.profile.TimingsModifier;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.adapters.clinics.ClinicSelectorAdapter;
import co.zenpets.doctors.utils.helpers.classes.TimingsPickerActivity;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinic;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinics;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinicsAPI;
import co.zenpets.doctors.utils.models.doctors.modules.TimingsAPI;
import co.zenpets.doctors.utils.models.doctors.modules.Timings;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorTimingsFrag extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE SELECTED CLINIC'S ID **/
    private String CLINIC_ID = null;

    /** BOOLEAN TO CHECK IF THE TIMINGS HAVE BEEN SET **/
    private Boolean blnTimings = false;

    /** THE CLINICS ARRAY LIST **/
    private ArrayList<DoctorClinic> arrClinics = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnClinics) AppCompatSpinner spnClinics;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.scrollTimings) ScrollView scrollTimings;
    @BindView(R.id.txtSunMorning) AppCompatTextView txtSunMorning;
    @BindView(R.id.txtSunAfternoon) AppCompatTextView txtSunAfternoon;
    @BindView(R.id.txtMonMorning) AppCompatTextView txtMonMorning;
    @BindView(R.id.txtMonAfternoon) AppCompatTextView txtMonAfternoon;
    @BindView(R.id.txtTueMorning) AppCompatTextView txtTueMorning;
    @BindView(R.id.txtTueAfternoon) AppCompatTextView txtTueAfternoon;
    @BindView(R.id.txtWedMorning) AppCompatTextView txtWedMorning;
    @BindView(R.id.txtWedAfternoon) AppCompatTextView txtWedAfternoon;
    @BindView(R.id.txtThuMorning) AppCompatTextView txtThuMorning;
    @BindView(R.id.txtThuAfternoon) AppCompatTextView txtThuAfternoon;
    @BindView(R.id.txtFriMorning) AppCompatTextView txtFriMorning;
    @BindView(R.id.txtFriAfternoon) AppCompatTextView txtFriAfternoon;
    @BindView(R.id.txtSatMorning) AppCompatTextView txtSatMorning;
    @BindView(R.id.txtSatAfternoon) AppCompatTextView txtSatAfternoon;

    /** ADD DOCTOR TIMINGS **/
    @OnClick(R.id.linlaEmpty) void configureTimings()   {
        Intent intent = new Intent(getActivity(), TimingsPickerActivity.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        intent.putExtra("CLINIC_ID", CLINIC_ID);
        startActivityForResult(intent, 101);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.doctor_details_timings, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* GET THE DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* SHOW THE PROGRESS AND START FETCHING THE DOCTOR'S PROFILE */
        if (DOCTOR_ID != null)  {
            /* FETCH THE DOCTOR'S CLINICS */
            fetchDoctorClinics();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        /* SELECT A CLINIC TO SHOW THE DOCTOR'S TIMINGS AT IT */
        spnClinics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CLINIC_ID = arrClinics.get(position).getClinicID();
                if (CLINIC_ID != null)  {
                    /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S TIMINGS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchDoctorTimings();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /***** FETCH THE DOCTOR'S CLINICS *****/
    private void fetchDoctorClinics() {
        DoctorClinicsAPI api = ZenApiClient.getClient().create(DoctorClinicsAPI.class);
        Call<DoctorClinics> call = api.fetchDoctorClinics(DOCTOR_ID);
        call.enqueue(new Callback<DoctorClinics>() {
            @Override
            public void onResponse(Call<DoctorClinics> call, Response<DoctorClinics> response) {
                arrClinics = response.body().getClinics();
                if (arrClinics != null && arrClinics.size() > 0)    {
                    spnClinics.setAdapter(new ClinicSelectorAdapter(getActivity(),
                            R.layout.clinic_selector_row,
                            arrClinics));
                }
            }

            @Override
            public void onFailure(Call<DoctorClinics> call, Throwable t) {
            }
        });
    }

    /***** FETCH THE DOCTOR'S TIMINGS *****/
    private void fetchDoctorTimings() {
        TimingsAPI apiInterface = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = apiInterface.fetchDoctorTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(Call<Timings> call, Response<Timings> response) {
                Timings data = response.body();
                assert data != null;
                Boolean blnError = data.getError();
                if (!blnError)   {
                    /* GET THE SUNDAY MORNING TIMINGS */
                    String SUN_MOR_FROM = data.getSunMorFrom();
                    String SUN_MOR_TO = data.getSunMorTo();
                    if (!SUN_MOR_FROM.equalsIgnoreCase("null") && !SUN_MOR_TO.equalsIgnoreCase("null"))  {
                        txtSunMorning.setText(getString(R.string.doctor_timings_placeholder, SUN_MOR_FROM, SUN_MOR_TO));
                    } else {
                        txtSunMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE SUNDAY AFTERNOON TIMINGS */
                    String SUN_AFT_FROM = data.getSunAftFrom();
                    String SUN_AFT_TO = data.getSunAftTo();
                    if (!SUN_AFT_FROM.equalsIgnoreCase("null") && !SUN_AFT_TO.equalsIgnoreCase("null"))  {
                        txtSunAfternoon.setText(getString(R.string.doctor_timings_placeholder, SUN_AFT_FROM, SUN_AFT_TO));
                    } else {
                        txtSunAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE MONDAY MORNING TIMINGS */
                    String MON_MOR_FROM = data.getMonMorFrom();
                    String MON_MOR_TO = data.getMonMorTo();
                    if (!MON_MOR_FROM.equalsIgnoreCase("null") && !MON_MOR_TO.equalsIgnoreCase("null"))  {
                        txtMonMorning.setText(getString(R.string.doctor_timings_placeholder, MON_MOR_FROM, MON_MOR_TO));
                    } else {
                        txtMonMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE MONDAY AFTERNOON TIMINGS */
                    String MON_AFT_FROM = data.getMonAftFrom();
                    String MON_AFT_TO = data.getMonAftTo();
                    if (!MON_AFT_FROM.equalsIgnoreCase("null") && !MON_AFT_TO.equalsIgnoreCase("null"))  {
                        txtMonAfternoon.setText(getString(R.string.doctor_timings_placeholder, MON_AFT_FROM, MON_AFT_TO));
                    } else {
                        txtMonAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE TUESDAY MORNING TIMINGS */
                    String TUE_MOR_FROM = data.getTueMorFrom();
                    String TUE_MOR_TO = data.getTueMorTo();
                    if (!TUE_MOR_FROM.equalsIgnoreCase("null") && !TUE_MOR_TO.equalsIgnoreCase("null"))  {
                        txtTueMorning.setText(getString(R.string.doctor_timings_placeholder, TUE_MOR_FROM, TUE_MOR_TO));
                    } else {
                        txtTueMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE TUESDAY AFTERNOON TIMINGS */
                    String TUE_AFT_FROM = data.getTueAftFrom();
                    String TUE_AFT_TO = data.getTueAftTo();
                    if (!TUE_AFT_FROM.equalsIgnoreCase("null") && !TUE_AFT_TO.equalsIgnoreCase("null")) {
                        txtTueAfternoon.setText(getString(R.string.doctor_timings_placeholder, TUE_AFT_FROM, TUE_AFT_TO));
                    } else {
                        txtTueAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE WEDNESDAY MORNING TIMINGS */
                    String WED_MOR_FROM = data.getWedMorFrom();
                    String WED_MOR_TO = data.getWedMorTo();
                    if (!WED_MOR_FROM.equalsIgnoreCase("null") && !WED_MOR_TO.equalsIgnoreCase("null"))  {
                        txtWedMorning.setText(getString(R.string.doctor_timings_placeholder, WED_MOR_FROM, WED_MOR_TO));
                    } else {
                        txtWedMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE WEDNESDAY AFTERNOON TIMINGS */
                    String WED_AFT_FROM = data.getWedAftFrom();
                    String WED_AFT_TO = data.getWedAftTo();
                    if (!WED_AFT_FROM.equalsIgnoreCase("null") && !WED_AFT_TO.equalsIgnoreCase("null"))  {
                        txtWedAfternoon.setText(getString(R.string.doctor_timings_placeholder, WED_AFT_FROM, WED_AFT_TO));
                    } else {
                        txtWedAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE THURSDAY MORNING TIMINGS */
                    String THU_MOR_FROM = data.getThuMorFrom();
                    String THU_MOR_TO = data.getThuMorTo();
                    if (!THU_MOR_FROM.equalsIgnoreCase("null") && !THU_MOR_TO.equalsIgnoreCase("null"))  {
                        txtThuMorning.setText(getString(R.string.doctor_timings_placeholder, THU_MOR_FROM, THU_MOR_TO));
                    } else {
                        txtThuMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE THURSDAY AFTERNOON TIMINGS */
                    String THU_AFT_FROM = data.getThuAftFrom();
                    String THU_AFT_TO = data.getThuAftTo();
                    if (!THU_AFT_FROM.equalsIgnoreCase("null") && !THU_AFT_TO.equalsIgnoreCase("null"))  {
                        txtThuAfternoon.setText(getString(R.string.doctor_timings_placeholder, THU_AFT_FROM, THU_AFT_TO));
                    } else {
                        txtThuAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE FRIDAY MORNING TIMINGS */
                    String FRI_MOR_FROM = data.getFriMorFrom();
                    String FRI_MOR_TO = data.getFriMorTo();
                    if (!FRI_MOR_FROM.equalsIgnoreCase("null") && !FRI_MOR_TO.equalsIgnoreCase("null"))  {
                        txtFriMorning.setText(getString(R.string.doctor_timings_placeholder, FRI_MOR_FROM, FRI_MOR_TO));
                    } else {
                        txtFriMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE FRIDAY AFTERNOON TIMINGS */
                    String FRI_AFT_FROM = data.getFriAftFrom();
                    String FRI_AFT_TO = data.getFriAftTo();
                    if (!FRI_AFT_FROM.equalsIgnoreCase("null") && !FRI_AFT_TO.equalsIgnoreCase("null"))  {
                        txtFriAfternoon.setText(getString(R.string.doctor_timings_placeholder, FRI_AFT_FROM, FRI_AFT_TO));
                    } else {
                        txtFriAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE SATURDAY MORNING TIMINGS */
                    String SAT_MOR_FROM = data.getSatMorFrom();
                    String SAT_MOR_TO = data.getSatMorTo();
                    if (!SAT_MOR_FROM.equalsIgnoreCase("null") && !SAT_MOR_TO.equalsIgnoreCase("null"))  {
                        txtSatMorning.setText(getString(R.string.doctor_timings_placeholder, SAT_MOR_FROM, SAT_MOR_TO));
                    } else {
                        txtSatMorning.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* GET THE SATURDAY AFTERNOON TIMINGS */
                    String SAT_AFT_FROM = data.getSatAftFrom();
                    String SAT_AFT_TO = data.getSatAftTo();
                    if (!SAT_AFT_FROM.equalsIgnoreCase("null") && !SAT_AFT_TO.equalsIgnoreCase("null"))  {
                        txtSatAfternoon.setText(getString(R.string.doctor_timings_placeholder, SAT_AFT_FROM, SAT_AFT_TO));
                    } else {
                        txtSatAfternoon.setText(getString(R.string.doctor_timings_closed));
                    }

                    /* SHOW THE TIMINGS SCROLL AND HIDE THE EMPTY LAYOUT */
                    scrollTimings.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);

                    /* SET THE BOOLEAN TO TRUE AND INVALIDATE THE OPTIONS MENU */
                    blnTimings = true;
                    getActivity().invalidateOptionsMenu();
                } else {
                    /*  SHOW THE EMPTY LAYOUT AND HIDE THE TIMINGS SCROLL*/
                    linlaEmpty.setVisibility(View.VISIBLE);
                    scrollTimings.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);

                    /* SET THE BOOLEAN TO FALSE AND INVALIDATE THE OPTIONS MENU */
                    blnTimings = false;
                    getActivity().invalidateOptionsMenu();
                }
            }

            @Override
            public void onFailure(Call<Timings> call, Throwable t) {
//                Log.e("TIMINGS FAILURE", t.getMessage());

                /*  SHOW THE EMPTY LAYOUT AND HIDE THE TIMINGS SCROLL*/
                linlaEmpty.setVisibility(View.VISIBLE);
                scrollTimings.setVisibility(View.GONE);

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);

                /* SET THE BOOLEAN TO FALSE AND INVALIDATE THE OPTIONS MENU */
                blnTimings = false;
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_doctor_timings, menu);
        MenuItem menuItem = menu.findItem(R.id.menuEdit);
        if (blnTimings) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.menuEdit:
                Intent intent = new Intent(getActivity(), TimingsModifier.class);
                intent.putExtra("DOCTOR_ID", DOCTOR_ID);
                intent.putExtra("CLINIC_ID", CLINIC_ID);
                startActivityForResult(intent, 102);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)   {
            if (requestCode == 101 || requestCode == 102) {
                /* SHOW THE PROGRESS AND FETCH THE DOCTOR'S TIMINGS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchDoctorTimings();
            }
        }
    }
}