package co.zenpets.doctors.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.R;
import co.zenpets.doctors.completer.TimingsCompleter;
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
import co.zenpets.doctors.utils.models.doctors.modules.Timings;
import co.zenpets.doctors.utils.models.doctors.modules.TimingsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity
        implements OnDateSelectedListener, OnMonthChangedListener {

    private AppPrefs getApp() {
        return (AppPrefs) getApplication();
    }

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** THE SELECTED CLINIC ID **/
    private String CLINIC_ID = null;

    /** THE APPOINTMENT DATE (CURRENT / TODAY'S) **/
    private String APPOINTMENT_DATE = null;

    /** THE APPOINTMENT YEAR AND MONTH **/
    private int APPOINTMENT_YEAR;
    private int APPOINTMENT_MONTH;
    private String MONTH = null;

    /** THE CLINICS AND DOCTORS ARRAY LISTS **/
    private ArrayList<DoctorClinic> arrClinics = new ArrayList<>();

    /** THE APPOINTMENTS ADAPTER AND ARRAY LIST **/
    private ArrayList<AppointmentData> arrAppointments = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnClinics) AppCompatSpinner spnClinics;
    @BindView(R.id.calendarView) MaterialCalendarView calendarView;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listAppointments) RecyclerView listAppointments;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        ButterKnife.bind(this);

        /* GET THE DOCTOR ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE CURRENT DATE */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        APPOINTMENT_DATE = sdf.format(cal.getTime());

        /* GET THE YEAR AND MONTH */
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(APPOINTMENT_DATE));
            SimpleDateFormat formatYear = new SimpleDateFormat("YYYY", Locale.getDefault());
            APPOINTMENT_YEAR = Integer.parseInt(formatYear.format(calendar.getTime()));
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM", Locale.getDefault());
            APPOINTMENT_MONTH = Integer.parseInt(formatMonth.format(calendar.getTime()));
            MONTH = formatMonth.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* HIGHLIGHT TODAY'S DATE */
        calendarView.setDateSelected(cal.getTime(), true);

        /* SET THE CURRENT DATE */
        calendarView.setSelectedDate(CalendarDay.today());

        /* SET THE DATE CHANGE LISTENER */
        calendarView.setOnDateChangedListener(this);

        /* SET THE MONTH LISTENER */
        calendarView.setOnMonthChangedListener(this);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* FETCH THE DOCTOR'S CLINICS */
        fetchDoctorClinics();

        /* SELECT A CLINIC */
        spnClinics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED CLINIC'S ID */
                CLINIC_ID = arrClinics.get(position).getClinicID();

                /* CHECK FOR THE DOCTOR - CLINIC TIMINGS STATUS */
                checkTimingsStatus();

                /* CLEAR THE APPOINTMENTS ARRAY */
                if (arrAppointments != null)
                    arrAppointments.clear();

                /* HIDE THE EMPTY LAYOUT, SHOW THE PROGRESS AND FETCH THE LIST OF TODAY'S APPOINTMENTS */
                linlaEmpty.setVisibility(View.GONE);
                listAppointments.setVisibility(View.GONE);
                linlaProgress.setVisibility(View.VISIBLE);
                fetchTodayAppointments();

                /* FETCH THE CURRENT MONTH'S APPOINTMENTS */
                fetchMonthlyAppointments();
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
            public void onResponse(@NonNull Call<DoctorClinics> call, @NonNull Response<DoctorClinics> response) {
//                String strResponse = new Gson().toJson(response);
//                try {
//                    JSONObject JORoot = new JSONObject(strResponse);
//                    JSONObject JOBody = JORoot.getJSONObject("body");
//                    JSONArray JAClinics = JOBody.getJSONArray("clinics");
//                    for (int i = 0; i < JAClinics.length(); i++) {
//                        JSONObject JOClinics = JAClinics.getJSONObject(i);
//                        Log.e("CLINIC", String.valueOf(JOClinics));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                arrClinics = response.body().getClinics();
                if (arrClinics != null && arrClinics.size() > 0)    {
                    spnClinics.setAdapter(new ClinicSelectorAdapter(CalendarActivity.this,
                            R.layout.clinic_selector_row,
                            arrClinics));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DoctorClinics> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE CURRENT MONTH'S APPOINTMENTS *****/
    private void fetchMonthlyAppointments() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentsData> call = api.fetchMonthlyAppointments(
                DOCTOR_ID, CLINIC_ID, String.valueOf(APPOINTMENT_YEAR), MONTH);
        call.enqueue(new Callback<AppointmentsData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentsData> call, @NonNull Response<AppointmentsData> response) {
                ArrayList<AppointmentData> arrAppointments = response.body().getAppointments();
                if (arrAppointments != null && arrAppointments.size() > 0)  {
                    for (int i = 0; i < arrAppointments.size(); i++) {
                        String appointmentDate = arrAppointments.get(i).getAppointmentDate();
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(sdf.parse(appointmentDate));
                            SimpleDateFormat format = new SimpleDateFormat("dd", Locale.getDefault());
                            final String strFormattedDate = format.format(calendar.getTime());

                            /* SET THE MARKER FOR THE DATA */
                            calendarView.addDecorator(new DayViewDecorator() {
                                @Override
                                public boolean shouldDecorate(CalendarDay day) {
                                    int calMonth = day.getMonth() + 1;
                                    if (calMonth == APPOINTMENT_MONTH
                                            && day.getDay() == Integer.parseInt(strFormattedDate))    {
                                        return true;
                                    }
                                    return false;
                                }

                                @Override
                                public void decorate(DayViewFacade view) {
                                    DotSpan span =
                                            new DotSpan(
                                                    6,
                                                    getResources().getColor(R.color.accent));
                                    view.addSpan(span);
                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentsData> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE LIST OF TODAY'S APPOINTMENTS *****/
    private void fetchTodayAppointments() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentsData> call = api.fetchDoctorTodayAppointments(DOCTOR_ID, CLINIC_ID, APPOINTMENT_DATE);
        call.enqueue(new Callback<AppointmentsData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentsData> call, @NonNull Response<AppointmentsData> response) {
                arrAppointments = response.body().getAppointments();
                if (arrAppointments != null && arrAppointments.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY APPOINTMENTS LAYOUT */
                    listAppointments.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE ADAPTER TO THE RECYCLER VIEW */
                    listAppointments.setAdapter(new AppointmentsAdapter(CalendarActivity.this, arrAppointments));
                } else {
                    /* SHOW THE EMPTY APPOINTMENTS LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listAppointments.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentsData> call, @NonNull Throwable t) {
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAppointments.setLayoutManager(manager);
        listAppointments.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listAppointments.setAdapter(new AppointmentsAdapter(this, arrAppointments));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Calendar";
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int year = widget.getSelectedDate().getYear();
        int month = widget.getSelectedDate().getMonth();
        month = month + 1;
        String day = String.valueOf(widget.getSelectedDate().getDay());
        if (day.length() == 1) {
            day = "0" + String.valueOf(widget.getSelectedDate().getDay());
        }

        /* CONCATENATE THE APPOINTMENT DATE */
        APPOINTMENT_DATE = year + "-" + month + "-" + day;
//        Log.e("APPOINTMENT_DATE", APPOINTMENT_DATE);

        /* CLEAR THE APPOINTMENTS ARRAY */
        if (arrAppointments != null)    {
            arrAppointments.clear();
        }

        /* HIDE THE EMPTY LAYOUT, SHOW THE PROGRESS AND FETCH THE LIST OF TODAY'S APPOINTMENTS */
        linlaEmpty.setVisibility(View.GONE);
        listAppointments.setVisibility(View.GONE);
        linlaProgress.setVisibility(View.VISIBLE);
        fetchTodayAppointments();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        calendarView.removeDecorators();
        calendarView.invalidateDecorators();
        APPOINTMENT_YEAR = date.getYear();
        APPOINTMENT_MONTH = date.getMonth();
        APPOINTMENT_MONTH = APPOINTMENT_MONTH + 1;
        MONTH = String.valueOf(APPOINTMENT_MONTH);
        if (MONTH.length() == 1)    {
            MONTH = "0" + MONTH;
        }

        /* FETCH THE CURRENT MONTH'S APPOINTMENTS */
        fetchMonthlyAppointments();
    }

    /***** CHECK FOR THE DOCTOR - CLINIC TIMINGS STATUS *****/
    private void checkTimingsStatus() {
        TimingsAPI apiInterface = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = apiInterface.fetchDoctorTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                String timingsID = response.body().getTimingsID();
                if (timingsID != null)  {
                } else {
                    new MaterialDialog.Builder(CalendarActivity.this)
                            .icon(ContextCompat.getDrawable(CalendarActivity.this, R.drawable.ic_info_outline_black_24dp))
                            .title("Timings Not Set")
                            .cancelable(true)
                            .content("You haven't set your timings at this Clinic. " +
                                    "The Zen Pets Pet Parents will not be able to see your daily " +
                                    "appointment schedule nor would you be able to set appointments " +
                                    "without setting up your Clinic timings." +
                                    "\n\nTo set your Clinic timings, press the \"Set Timings\" button." +
                                    "\n\nOtherwise, press the \"Cancel\" button")
                            .positiveText("Set Timings")
                            .negativeText(getString(R.string.login_auth_failed_cancel))
                            .theme(Theme.LIGHT)
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(CalendarActivity.this, TimingsCompleter.class);
                                    intent.putExtra("DOCTOR_ID", DOCTOR_ID);
                                    intent.putExtra("CLINIC_ID", CLINIC_ID);
                                    startActivityForResult(intent, 101);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)   {
            /* CLEAR THE APPOINTMENTS ARRAY */
            if (arrAppointments != null)
                arrAppointments.clear();

            /* HIDE THE EMPTY LAYOUT, SHOW THE PROGRESS AND FETCH THE LIST OF TODAY'S APPOINTMENTS */
            linlaEmpty.setVisibility(View.GONE);
            linlaProgress.setVisibility(View.VISIBLE);
            fetchTodayAppointments();

            /* FETCH THE CURRENT MONTH'S APPOINTMENTS */
            fetchMonthlyAppointments();
        }
    }
}