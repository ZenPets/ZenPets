package co.zenpets.doctors.creator.appointment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.adapters.appointments.creator.AfternoonCreatorAdapter;
import co.zenpets.doctors.utils.adapters.appointments.creator.MorningCreatorAdapter;
import co.zenpets.doctors.utils.adapters.clinics.ClinicSelectorAdapter;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.calendar.ZenCalendarData;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinic;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinics;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinicsAPI;
import co.zenpets.doctors.utils.models.doctors.modules.TimeSlot;
import co.zenpets.doctors.utils.models.doctors.modules.Timings;
import co.zenpets.doctors.utils.models.doctors.modules.TimingsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class AppointmentSlotCreator extends AppCompatActivity
        /*implements AfternoonSlotsInterface, MorningSlotsInterface*/ {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE SELECTED CLINIC ID **/
    private String CLINIC_ID = null;

    /** THE CURRENT DATE **/
    private String CURRENT_DATE = null;

    /** THE START TIME AND END TIME **/
    private String MORNING_START_TIME = null;
    private String MORNING_END_TIME = null;
    private String AFTERNOON_START_TIME = null;
    private String AFTERNOON_END_TIME = null;

    /** THE CLINICS AND DOCTORS ARRAY LISTS **/
    private ArrayList<DoctorClinic> arrClinics = new ArrayList<>();

    /** THE ZEN CALENDAR ADAPTER AND ARRAY LIST **/
    private ZenCalendarAdapter calendarAdapter;
    private final ArrayList<ZenCalendarData> arrDates = new ArrayList<>();

    /** A MORNING TIME SLOTS INSTANCE **/
    private TimeSlot morningData;

    /** THE MORNING TIME SLOTS ADAPTER AND ARRAY LIST **/
    private MorningCreatorAdapter morningCreatorAdapter;
    //    private final ArrayList<MorningTimeSlotsData> arrMorningSlots = new ArrayList<>();
    private final ArrayList<TimeSlot> arrMorningSlots = new ArrayList<>();

    /** AN AFTERNOON TIME SLOTS INSTANCE **/
    private TimeSlot afternoonData;

    /** THE AFTERNOON TIME SLOTS ADAPTER AND ARRAY LIST **/
    private AfternoonCreatorAdapter afternoonCreatorAdapter;
    //    private final ArrayList<AfternoonTimeSlotsData> arrAfternoonSlots = new ArrayList<>();
    private final ArrayList<TimeSlot> arrAfternoonSlots = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnClinics) AppCompatSpinner spnClinics;
    @BindView(R.id.listDates) RecyclerView listDates;
    @BindView(R.id.linlaMorningProgress) LinearLayout linlaMorningProgress;
    @BindView(R.id.listMorningTimes) RecyclerView listMorningTimes;
    @BindView(R.id.linlaMorningEmpty) LinearLayout linlaMorningEmpty;
    @BindView(R.id.linlaAfternoonProgress) LinearLayout linlaAfternoonProgress;
    @BindView(R.id.listAfternoonTimes) RecyclerView listAfternoonTimes;
    @BindView(R.id.linlaAfternoonEmpty) LinearLayout linlaAfternoonEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_slot_creator);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* FETCH THE DOCTOR'S CLINICS */
        fetchDoctorClinics();

        /* INSTANTIATE THE ZEN CALENDAR ADAPTER */
        calendarAdapter = new ZenCalendarAdapter(AppointmentSlotCreator.this, arrDates);

        /* INSTANTIATE THE MORNING TIME SLOTS ADAPTER */
        morningCreatorAdapter = new MorningCreatorAdapter(AppointmentSlotCreator.this, arrMorningSlots);

        /* INSTANTIATE THE AFTERNOON TIME SLOTS ADAPTER */
        afternoonCreatorAdapter = new AfternoonCreatorAdapter(AppointmentSlotCreator.this, arrAfternoonSlots);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SELECT A CLINIC */
        spnClinics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED CLINIC'S ID */
                CLINIC_ID = arrClinics.get(position).getClinicID();

                /* GET THE START DATE */
                getStartDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* GET THE START DATE */
        getStartDate();
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
                        spnClinics.setAdapter(new ClinicSelectorAdapter(AppointmentSlotCreator.this,
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

    /***** GET THE START DATE *****/
    private void getStartDate() {
        DateTime today = new DateTime().withTimeAtStartOfDay();
        Date dt = today.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        SimpleDateFormat currentDay = new SimpleDateFormat("EE", Locale.getDefault());
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String SELECTED_DAY = currentDay.format(cal.getTime());
        CURRENT_DATE = currentDate.format(cal.getTime());
//        Log.e("DATE", CURRENT_DATE);

        /* FETCH THE TIME SLOTS  */
        if (SELECTED_DAY.equalsIgnoreCase("sun"))   {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            sundayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            sundayAfternoonTimings();

        } else if (SELECTED_DAY.equalsIgnoreCase("mon"))    {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            mondayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            mondayAfternoonTimings();

        } else if (SELECTED_DAY.equalsIgnoreCase("tue"))    {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            tuesdayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            tuesdayAfternoonTimings();

        } else if (SELECTED_DAY.equalsIgnoreCase("wed"))    {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            wednesdayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            wednesdayAfternoonTimings();

        } else if (SELECTED_DAY.equalsIgnoreCase("thu"))    {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            thursdayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            thursdayAfternoonTimings();

        } else if (SELECTED_DAY.equalsIgnoreCase("fri"))    {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            fridayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            fridayAfternoonTimings();

        } else if (SELECTED_DAY.equalsIgnoreCase("sat"))    {
            /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
            arrMorningSlots.clear();
            listMorningTimes.setVisibility(View.GONE);
            linlaMorningEmpty.setVisibility(View.GONE);
            linlaMorningProgress.setVisibility(View.VISIBLE);
            saturdayMorningTimings();

            /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
            arrAfternoonSlots.clear();
            listAfternoonTimes.setVisibility(View.GONE);
            linlaAfternoonEmpty.setVisibility(View.GONE);
            linlaAfternoonProgress.setVisibility(View.VISIBLE);
            saturdayAfternoonTimings();
        }

        /* POPULATE THE ZEN CALENDAR DATES */
        ZenCalendarData data;
        for (int i = 0; i < 14; i++) {
            data = new ZenCalendarData();
            Date date = today.plusDays(i).withTimeAtStartOfDay().toDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            /* SET THE LONG DATE */
            SimpleDateFormat longDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String LONG_DATE = longDate.format(calendar.getTime());
            data.setLongDate(LONG_DATE);

            /* SET THE SHORT DATE */
            SimpleDateFormat shortDate = new SimpleDateFormat("dd", Locale.getDefault());
            String SHORT_DATE = shortDate.format(calendar.getTime());
            data.setShortDate(SHORT_DATE);

            /* SET THE LONG DAY */
            SimpleDateFormat longDay = new SimpleDateFormat("EE", Locale.getDefault());
            String LONG_DAY = longDay.format(calendar.getTime());
            data.setLongDay(LONG_DAY);

            /* SET THE SHORT DATE (FOR DISPLAY PURPOSES ONLY!!) */
            String SHORT_DAY = LONG_DAY.substring(0, 1);
            data.setShortDay(SHORT_DAY);

            arrDates.add(data);

            /* CHECK WHEN THE FOR LOOP ENDS */
            if (i + 1 == 15) {
                /* INSTANTIATE THE ZEN CALENDAR ADAPTER */
                calendarAdapter = new ZenCalendarAdapter(AppointmentSlotCreator.this, arrDates);
            }
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Select a time slot";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 101)  {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* CONFIGURE THE TIME SLOTS RECYCLER VIEW */
        LinearLayoutManager dates = new LinearLayoutManager(this);
        dates.setOrientation(LinearLayoutManager.HORIZONTAL);
        dates.setAutoMeasureEnabled(true);
        listDates.setLayoutManager(dates);
        listDates.setHasFixedSize(true);
        listDates.setAdapter(calendarAdapter);

        /* CHECK IF DEVICE IS A TABLET */
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        /* GET THE DEVICE ORIENTATION */
        int intOrientation = getResources().getConfiguration().orientation;

        /* CONFIGURE THE MORNING TIME SLOTS RECYCLER VIEW */
        listMorningTimes.setHasFixedSize(true);
        GridLayoutManager managerMorning = null;
        if (isTablet)   {
            if (intOrientation == 1)	{
                managerMorning = new GridLayoutManager(AppointmentSlotCreator.this, 2);
            } else if (intOrientation == 2) {
                managerMorning = new GridLayoutManager(AppointmentSlotCreator.this, 3);
            }
        } else {
            if (intOrientation == 1)    {
                managerMorning = new GridLayoutManager(AppointmentSlotCreator.this, 4);
            } else if (intOrientation == 2) {
                managerMorning = new GridLayoutManager(AppointmentSlotCreator.this, 8);
            }
        }
        listMorningTimes.setLayoutManager(managerMorning);
        listMorningTimes.setNestedScrollingEnabled(false);
        listMorningTimes.setAdapter(morningCreatorAdapter);

        /* CONFIGURE THE AFTERNOON TIME SLOTS RECYCLER VIEW */
        listAfternoonTimes.setHasFixedSize(true);
        GridLayoutManager managerAfternoon = null;
        if (isTablet)   {
            if (intOrientation == 1)	{
                managerAfternoon = new GridLayoutManager(AppointmentSlotCreator.this, 2);
            } else if (intOrientation == 2) {
                managerAfternoon = new GridLayoutManager(AppointmentSlotCreator.this, 3);
            }
        } else {
            if (intOrientation == 1)    {
                managerAfternoon = new GridLayoutManager(AppointmentSlotCreator.this, 4);
            } else if (intOrientation == 2) {
                managerAfternoon = new GridLayoutManager(AppointmentSlotCreator.this, 8);
            }
        }
        listAfternoonTimes.setLayoutManager(managerAfternoon);
        listAfternoonTimes.setNestedScrollingEnabled(false);
        listAfternoonTimes.setAdapter(afternoonCreatorAdapter);
    }

    /***** THE PRIVATE ZEN CALENDAR ADAPTER *****/
    private class ZenCalendarAdapter extends RecyclerView.Adapter<ZenCalendarAdapter.CalendarVH> {

        /** AN ACTIVITY INSTANCE **/
        private final Activity activity;

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<ZenCalendarData> arrDates;

        /** SELECTED ITEM **/
        private int selectedPosition = 0;

        ZenCalendarAdapter(Activity activity, ArrayList<ZenCalendarData> arrDates) {

            /* CAST THE ACTIVITY IN THE GLOBAL INSTANCE */
            this.activity = activity;

            /* CAST THE CONTENTS OF THE LOCAL ARRAY LIST IN THE METHOD TO THE GLOBAL INSTANCE */
            this.arrDates = arrDates;
        }

        @Override
        public int getItemCount() {
            return arrDates.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final ZenCalendarAdapter.CalendarVH holder, int position) {
            final ZenCalendarData data = arrDates.get(position);

            /* SET THE SELECTION AND MARK WITH DRAWABLE */
            if (selectedPosition == position)   {
                holder.txtShortDate.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.circular_btn_bg));
            } else {
                holder.txtShortDate.setBackgroundDrawable(null);
            }

            /* SET THE SHORT DAY */
            if (data.getShortDay() != null) {
                holder.txtShortDay.setText(data.getShortDay());
            }

            /* SET THE SHORT DATE */
            if (data.getShortDate() != null)    {
                holder.txtShortDate.setText(data.getShortDate());
            }

            /* SHOW THE AVAILABILITY ON THE SELECTED DATE */
            holder.linlaDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = holder.getAdapterPosition();
                    notifyDataSetChanged();

                    String strLongDay = data.getLongDay();
//                    Log.e("LONG DAY", strLongDay);

                    /* FETCH THE TIME SLOTS  */
                    if (strLongDay.equalsIgnoreCase("sun"))   {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        sundayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        sundayAfternoonTimings();

                    } else if (strLongDay.equalsIgnoreCase("mon"))    {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        mondayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        mondayAfternoonTimings();

                    } else if (strLongDay.equalsIgnoreCase("tue"))    {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        tuesdayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        tuesdayAfternoonTimings();

                    } else if (strLongDay.equalsIgnoreCase("wed"))    {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        wednesdayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        wednesdayAfternoonTimings();

                    } else if (strLongDay.equalsIgnoreCase("thu"))    {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        thursdayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        thursdayAfternoonTimings();

                    } else if (strLongDay.equalsIgnoreCase("fri"))    {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        fridayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        fridayAfternoonTimings();

                    } else if (strLongDay.equalsIgnoreCase("sat"))    {
                        /* SHOW THE PROGRESS WHILE FETCHING THE MORNING DATA */
                        arrMorningSlots.clear();
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.GONE);
                        linlaMorningProgress.setVisibility(View.VISIBLE);
                        saturdayMorningTimings();

                        /* SHOW THE PROGRESS WHILE FETCHING THE AFTERNOON DATA */
                        arrAfternoonSlots.clear();
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.GONE);
                        linlaAfternoonProgress.setVisibility(View.VISIBLE);
                        saturdayAfternoonTimings();
                    }
                }
            });
        }

        @NonNull
        @Override
        public ZenCalendarAdapter.CalendarVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.zen_calendar_item, parent, false);

            return new ZenCalendarAdapter.CalendarVH(itemView);
        }

        class CalendarVH extends RecyclerView.ViewHolder	{

            final LinearLayout linlaDate;
            final AppCompatTextView txtShortDay;
            final AppCompatTextView txtShortDate;

            CalendarVH(View v) {
                super(v);
                linlaDate = v.findViewById(R.id.linlaDate);
                txtShortDay = v.findViewById(R.id.txtShortDay);
                txtShortDate = v.findViewById(R.id.txtShortDate);

                /* GET THE DISPLAY SIZE */
                DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
                int width = (int) ((float) metrics.widthPixels);
                linlaDate.getLayoutParams().width = width / 7;
            }
        }
    }

    /***** FETCH THE SUNDAY MORNING TIMINGS *****/
    private void sundayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchSundayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getSunMorFrom();
                    MORNING_END_TIME = timing.getSunMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }

                    /* HIDE THE MORNING PROGRESS */
                    linlaMorningProgress.setVisibility(View.GONE);
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SUNDAY AFTERNOON TIMINGS *****/
    private void sundayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchSundayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getSunAftFrom();
                    AFTERNOON_END_TIME = timing.getSunAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE MONDAY MORNING TIMINGS *****/
    private void mondayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchMondayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getMonMorFrom();
                    MORNING_END_TIME = timing.getMonMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }

                    /* HIDE THE MORNING PROGRESS */
                    linlaMorningProgress.setVisibility(View.GONE);
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE MONDAY AFTERNOON TIMINGS *****/
    private void mondayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchMondayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getMonAftFrom();
                    AFTERNOON_END_TIME = timing.getMonAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TUESDAY MORNING TIMINGS *****/
    private void tuesdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchTuesdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getTueMorFrom();
                    MORNING_END_TIME = timing.getTueMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listMorningTimes.setVisibility(View.VISIBLE);
                linlaMorningProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TUESDAY AFTERNOON TIMINGS *****/
    private void tuesdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchTuesdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getTueAftFrom();
                    AFTERNOON_END_TIME = timing.getTueAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE WEDNESDAY MORNING TIMINGS *****/
    private void wednesdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchWednesdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getWedMorFrom();
                    MORNING_END_TIME = timing.getWedMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listMorningTimes.setVisibility(View.VISIBLE);
                linlaMorningProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE WEDNESDAY AFTERNOON TIMINGS *****/
    private void wednesdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchWednesdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getWedAftFrom();
                    AFTERNOON_END_TIME = timing.getWedAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE THURSDAY MORNING TIMINGS *****/
    private void thursdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchThursdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getThuMorFrom();
                    MORNING_END_TIME = timing.getThuMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listMorningTimes.setVisibility(View.VISIBLE);
                linlaMorningProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE THURSDAY AFTERNOON TIMINGS *****/
    private void thursdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchThursdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getThuAftFrom();
                    AFTERNOON_END_TIME = timing.getThuAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FRIDAY MORNING TIMINGS *****/
    private void fridayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchFridayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getFriMorFrom();
                    MORNING_END_TIME = timing.getFriMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listMorningTimes.setVisibility(View.VISIBLE);
                linlaMorningProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FRIDAY AFTERNOON TIMINGS *****/
    private void fridayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchFridayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getFriAftFrom();
                    AFTERNOON_END_TIME = timing.getFriAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SATURDAY MORNING TIMINGS *****/
    private void saturdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchSaturdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    MORNING_START_TIME = timing.getSatMorFrom();
                    MORNING_END_TIME = timing.getSatMorTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (MORNING_START_TIME != null && !MORNING_START_TIME.equalsIgnoreCase("null")
                            && MORNING_END_TIME != null && !MORNING_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE MORNING SLOTS */
                        theMorningSlots();
                    } else {
                        /* HIDE THE LIST */
                        listMorningTimes.setVisibility(View.GONE);
                        linlaMorningEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listMorningTimes.setVisibility(View.GONE);
                    linlaMorningEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listMorningTimes.setVisibility(View.VISIBLE);
                linlaMorningProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SATURDAY AFTERNOON TIMINGS *****/
    private void saturdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timings> call = api.fetchSaturdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timings>() {
            @Override
            public void onResponse(@NonNull Call<Timings> call, @NonNull Response<Timings> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timings timing = response.body();
                if (timing != null) {
                    AFTERNOON_START_TIME = timing.getSatAftFrom();
                    AFTERNOON_END_TIME = timing.getSatAftTo();

                    /* CALCULATE THE TIME SLOTS */
                    if (AFTERNOON_START_TIME != null && !AFTERNOON_START_TIME.equalsIgnoreCase("null")
                            && AFTERNOON_END_TIME != null && !AFTERNOON_END_TIME.equalsIgnoreCase("null")) {
                        /* DISPLAY THE AFTERNOON SLOTS */
                        theAfternoonSlots();
                    } else {
                        /* HIDE THE LIST */
                        listAfternoonTimes.setVisibility(View.GONE);
                        linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    /* HIDE THE LIST */
                    listAfternoonTimes.setVisibility(View.GONE);
                    linlaAfternoonEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.VISIBLE);
                linlaAfternoonProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Timings> call, @NonNull Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** DISPLAY THE MORNING SLOTS *****/
    private void theMorningSlots() {
        /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
        listMorningTimes.setVisibility(View.GONE);
        linlaMorningEmpty.setVisibility(View.GONE);
        linlaMorningProgress.setVisibility(View.VISIBLE);

        /* CLEAR THE ARRAY LIST */
        arrMorningSlots.clear();

        /* INSTANTIATE THE TIME SLOTS ADAPTER */
        morningCreatorAdapter = new MorningCreatorAdapter(AppointmentSlotCreator.this, arrMorningSlots);

        /* SET THE TIME SLOTS ADAPTER TO THE AFTERNOON RECYCLER VIEW */
        listMorningTimes.setAdapter(morningCreatorAdapter);

        /* SET THE LIST VISIBILITY */
        listMorningTimes.setVisibility(View.VISIBLE);
        linlaMorningEmpty.setVisibility(View.GONE);

        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
        linlaMorningProgress.setVisibility(View.GONE);

        /* DISPLAY THE MORNING SLOTS */
        ArrayList<String> arrMorSlots = fetchTimeSlots(CURRENT_DATE, MORNING_START_TIME, MORNING_END_TIME);
        displayMorningSlots(arrMorSlots);
    }

    /***** DISPLAY THE AFTERNOON SLOTS *****/
    private void theAfternoonSlots() {
        /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
        listAfternoonTimes.setVisibility(View.GONE);
        linlaAfternoonEmpty.setVisibility(View.GONE);
        linlaAfternoonProgress.setVisibility(View.VISIBLE);

        /* CLEAR THE ARRAY LIST */
        arrAfternoonSlots.clear();

        /* INSTANTIATE THE AFTERNOON TIME SLOTS ADAPTER */
        afternoonCreatorAdapter = new AfternoonCreatorAdapter(AppointmentSlotCreator.this, arrAfternoonSlots);

        /* SET THE TIME SLOTS ADAPTER TO THE AFTERNOON RECYCLER VIEW */
        listAfternoonTimes.setAdapter(afternoonCreatorAdapter);

        /* SHOW THE AFTERNOON SLOTS AND HIDE THE EMPTY VIEW */
        listAfternoonTimes.setVisibility(View.VISIBLE);
        linlaAfternoonEmpty.setVisibility(View.GONE);

        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
        linlaAfternoonProgress.setVisibility(View.GONE);

        /* DISPLAY THE AFTERNOON SLOTS */
        ArrayList<String> arrAftSlots = fetchTimeSlots(CURRENT_DATE, AFTERNOON_START_TIME, AFTERNOON_END_TIME);
        displayAfternoonSlots(arrAftSlots);
    }

    /** DISPLAY THE MORNING SLOTS **/
    private void displayMorningSlots(ArrayList<String> arrMorSlots) {
        for (int i = 0; i < arrMorSlots.size(); i++) {

            /* GET THE SLOT TIME */
            final String slotTime = arrMorSlots.get(i);
//            Log.e("SLOT", arrMorSlots.get(i));

            TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
            Call<TimeSlot> call = api.checkAvailability(DOCTOR_ID, CLINIC_ID, CURRENT_DATE, arrMorSlots.get(i));
            call.enqueue(new Callback<TimeSlot>() {
                @Override
                public void onResponse(Call<TimeSlot> call, Response<TimeSlot> response) {
//                    Log.e("AVAILABILITY RESPONSE", String.valueOf(response.raw()));
                    TimeSlot slot = response.body();
                    morningData = new TimeSlot();

                    /* SET THE SLOT TIME */
                    morningData.setAppointmentTime(slotTime);

                    /* SET THE DOCTOR ID */
                    morningData.setDoctorID(DOCTOR_ID);

                    /* SET THE CLINIC ID */
                    morningData.setClinicID(CLINIC_ID);

                    /* SET THE APPOINTMENT DATE */
                    morningData.setAppointmentDate(CURRENT_DATE);

                    if (slot != null)   {
                        if (slot.getError())    {
                            morningData.setAppointmentStatus("Available");
                        } else {
                            morningData.setAppointmentStatus("Unavailable");
                        }
                    } else {
                        morningData.setAppointmentStatus("Unavailable");
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrMorningSlots.add(morningData);
                    morningCreatorAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TimeSlot> call, Throwable t) {
//                    Log.e("TIME SLOT FAILURE", t.getMessage());
                }
            });
        }
    }

    /** DISPLAY THE AFTERNOON SLOTS **/
    private void displayAfternoonSlots(final ArrayList<String> arrAftSlots) {
        for (int i = 0; i < arrAftSlots.size(); i++) {

            /* GET THE SLOT TIME */
            final String slotTime = arrAftSlots.get(i);
//            Log.e("SLOT", arrAftSlots.get(i));

            TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
            Call<TimeSlot> call = api.checkAvailability(DOCTOR_ID, CLINIC_ID, CURRENT_DATE, arrAftSlots.get(i));
            call.enqueue(new Callback<TimeSlot>() {
                @Override
                public void onResponse(Call<TimeSlot> call, Response<TimeSlot> response) {
//                    Log.e("AVAILABILITY RESPONSE", String.valueOf(response.raw()));
                    TimeSlot slot = response.body();
                    afternoonData = new TimeSlot();

                    /* SET THE SLOT TIME */
                    afternoonData.setAppointmentTime(slotTime);

                    /* SET THE DOCTOR ID */
                    afternoonData.setDoctorID(DOCTOR_ID);

                    /* SET THE CLINIC ID */
                    afternoonData.setClinicID(CLINIC_ID);

                    /* SET THE APPOINTMENT DATE */
                    afternoonData.setAppointmentDate(CURRENT_DATE);

                    if (slot != null)   {
                        if (slot.getError())    {
                            afternoonData.setAppointmentStatus("Available");
                        } else {
                            afternoonData.setAppointmentStatus("Unavailable");
                        }
                    } else {
                        afternoonData.setAppointmentStatus("Unavailable");
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrAfternoonSlots.add(afternoonData);
                    afternoonCreatorAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TimeSlot> call, Throwable t) {
//                    Log.e("TIME SLOT FAILURE", t.getMessage());
                }
            });
        }
    }

    /** FETCH THE LIST OF TIME SLOTS **/
    private ArrayList<String> fetchTimeSlots(String currentDate, String afternoonStartTime, String afternoonEndTime) {
        ArrayList<String> list = new ArrayList<>();

        /* CALCULATE THE SLOTS */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            final Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(sdf.parse(currentDate + " " + afternoonStartTime));
            if (startCalendar.get(Calendar.MINUTE) < 15) {
                startCalendar.set(Calendar.MINUTE, 0);
            } else {
                startCalendar.add(Calendar.MINUTE, 15); // overstep hour and clear minutes
                startCalendar.clear(Calendar.MINUTE);
            }

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(sdf.parse(CURRENT_DATE + " " + afternoonEndTime));
            endCalendar.add(Calendar.HOUR_OF_DAY, 0);

            endCalendar.clear(Calendar.MINUTE);
            endCalendar.clear(Calendar.SECOND);
            endCalendar.clear(Calendar.MILLISECOND);

            final SimpleDateFormat slotTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            while (endCalendar.after(startCalendar)) {

                /* SET THE TIME SLOT */
                String slotStartTime = slotTime.format(startCalendar.getTime());
                startCalendar.add(Calendar.MINUTE, 15);
//                Log.e("QUERY TIME", slotStartTime);

                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String currentTime = format.format(new Date());
//                Log.e("CURRENT TIME", currentTime);

                int spacePOSSlot = slotStartTime.indexOf(" ");
                String finalSlot = slotStartTime.substring(0, spacePOSSlot - 1);
                int spacePOSCurrent = currentTime.indexOf(" ");
                String finalCurrent = currentTime.substring(0, spacePOSCurrent - 1);

                LocalTime timeSlot = new LocalTime(finalSlot);
                LocalTime timeCurrent = new LocalTime(finalCurrent);
                int slotStatus = timeCurrent.compareTo(timeSlot);
//                Log.e("STATUS", String.valueOf(slotStatus));

                list.add(slotStartTime);
            }

            return list;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

//    @Override
//    public void onMorningSlotResult(ArrayList<AppointmentSlotsData> arrMorningSlots) {
//        /* INSTANTIATE THE TIME SLOTS ADAPTER */
//        morningCreatorAdapter = new MorningCreatorAdapter(AppointmentSlotCreator.this, arrMorningSlots);
//
//        /* SET THE TIME SLOTS ADAPTER TO THE AFTERNOON RECYCLER VIEW */
//        listMorningTimes.setAdapter(morningCreatorAdapter);
//
//        /* SET THE LIST VISIBILITY */
//        listMorningTimes.setVisibility(View.VISIBLE);
//        linlaMorningEmpty.setVisibility(View.GONE);
//
//        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//        listMorningTimes.setVisibility(View.VISIBLE);
//        linlaMorningProgress.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onAfternoonSlotResult(ArrayList<AppointmentSlotsData> arrAfternoonSlots) {
//        /* INSTANTIATE THE AFTERNOON TIME SLOTS ADAPTER */
//        afternoonCreatorAdapter = new AfternoonCreatorAdapter(AppointmentSlotCreator.this, arrAfternoonSlots);
//
//        /* SET THE TIME SLOTS ADAPTER TO THE AFTERNOON RECYCLER VIEW */
//        listAfternoonTimes.setAdapter(afternoonCreatorAdapter);
//
//        /* SET THE LIST VISIBILITY */
//        listAfternoonTimes.setVisibility(View.VISIBLE);
//        linlaAfternoonEmpty.setVisibility(View.GONE);
//
//        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//        listAfternoonTimes.setVisibility(View.VISIBLE);
//        linlaAfternoonProgress.setVisibility(View.GONE);
//    }
}