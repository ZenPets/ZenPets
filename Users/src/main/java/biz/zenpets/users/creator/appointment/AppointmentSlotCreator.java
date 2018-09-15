package biz.zenpets.users.creator.appointment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.adapters.appointment.creator.AfternoonCreatorAdapter;
import biz.zenpets.users.utils.adapters.appointment.creator.MorningCreatorAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.helpers.timings.AfternoonSlotsInterface;
import biz.zenpets.users.utils.helpers.timings.DisplayAfternoonSlots;
import biz.zenpets.users.utils.helpers.timings.DisplayMorningsSlots;
import biz.zenpets.users.utils.helpers.timings.MorningSlotsInterface;
import biz.zenpets.users.utils.models.appointment.slots.AfternoonTimeSlotsData;
import biz.zenpets.users.utils.models.appointment.slots.MorningTimeSlotsData;
import biz.zenpets.users.utils.models.calendar.ZenCalendarData;
import biz.zenpets.users.utils.models.doctors.DoctorsAPI;
import biz.zenpets.users.utils.models.doctors.list.Doctor;
import biz.zenpets.users.utils.models.doctors.timings.Timing;
import biz.zenpets.users.utils.models.doctors.timings.TimingsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class AppointmentSlotCreator extends AppCompatActivity
        implements AfternoonSlotsInterface, MorningSlotsInterface {

    /** THE INCOMING DOCTOR ID AND CLINIC ID **/
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;

    /** THE DOCTOR AND CLINIC DETAILS **/
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_DISPLAY_PROFILE = null;
    private String CLINIC_NAME = null;
    private String CLINIC_ADDRESS = null;

    /** THE CURRENT DATE **/
    private String CURRENT_DATE = null;

    /** THE START TIME AND END TIME **/
    private String MORNING_START_TIME = null;
    private String MORNING_END_TIME = null;
    private String AFTERNOON_START_TIME = null;
    private String AFTERNOON_END_TIME = null;

    /** THE ZEN CALENDAR ADAPTER AND ARRAY LIST **/
    private ZenCalendarAdapter calendarAdapter;
    private final ArrayList<ZenCalendarData> arrDates = new ArrayList<>();

    /** THE MORNING TIME SLOTS ADAPTER AND ARRAY LIST **/
    private MorningCreatorAdapter morningCreatorAdapter;
    private final ArrayList<MorningTimeSlotsData> arrMorningSlots = new ArrayList<>();

    /** THE AFTERNOON TIME SLOTS ADAPTER AND ARRAY LIST **/
    private AfternoonCreatorAdapter afternoonCreatorAdapter;
    private final ArrayList<AfternoonTimeSlotsData> arrAfternoonSlots = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) SimpleDraweeView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtClinicDetails) AppCompatTextView txtClinicDetails;
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

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* INSTANTIATE THE ZEN CALENDAR ADAPTER */
        calendarAdapter = new ZenCalendarAdapter(AppointmentSlotCreator.this, arrDates);

        /* INSTANTIATE THE MORNING TIME SLOTS ADAPTER */
        morningCreatorAdapter = new MorningCreatorAdapter(AppointmentSlotCreator.this, arrMorningSlots);

        /* INSTANTIATE THE AFTERNOON TIME SLOTS ADAPTER */
        afternoonCreatorAdapter = new AfternoonCreatorAdapter(AppointmentSlotCreator.this, arrAfternoonSlots);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE START DATE */
        getStartDate();
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

    /* GET THE INCOMING DATA */
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("CLINIC_ID")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            CLINIC_ID = bundle.getString("CLINIC_ID");
            if (DOCTOR_ID != null && CLINIC_ID != null) {
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

                    /* CHANGE THE CURRENT DATE */
                    CURRENT_DATE = data.getLongDate();

                    String strLongDay = data.getLongDay();

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
        Call<Timing> call = api.fetchSundayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SUNDAY AFTERNOON TIMINGS *****/
    private void sundayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSundayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE MONDAY MORNING TIMINGS *****/
    private void mondayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchMondayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE MONDAY AFTERNOON TIMINGS *****/
    private void mondayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchMondayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TUESDAY MORNING TIMINGS *****/
    private void tuesdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchTuesdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE TUESDAY AFTERNOON TIMINGS *****/
    private void tuesdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchTuesdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE WEDNESDAY MORNING TIMINGS *****/
    private void wednesdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchWednesdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE WEDNESDAY AFTERNOON TIMINGS *****/
    private void wednesdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchWednesdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE THURSDAY MORNING TIMINGS *****/
    private void thursdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchThursdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE THURSDAY AFTERNOON TIMINGS *****/
    private void thursdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchThursdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FRIDAY MORNING TIMINGS *****/
    private void fridayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchFridayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE FRIDAY AFTERNOON TIMINGS *****/
    private void fridayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchFridayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SATURDAY MORNING TIMINGS *****/
    private void saturdayMorningTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSaturdayMorningTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listMorningTimes.setVisibility(View.GONE);
                linlaMorningProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
//                Log.e("TUESDAY FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** FETCH THE SATURDAY AFTERNOON TIMINGS *****/
    private void saturdayAfternoonTimings() {
        TimingsAPI api = ZenApiClient.getClient().create(TimingsAPI.class);
        Call<Timing> call = api.fetchSaturdayAfternoonTimings(DOCTOR_ID, CLINIC_ID);
        call.enqueue(new Callback<Timing>() {
            @Override
            public void onResponse(Call<Timing> call, Response<Timing> response) {
                /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
                listAfternoonTimes.setVisibility(View.GONE);
                linlaAfternoonProgress.setVisibility(View.VISIBLE);

                Timing timing = response.body();
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
            public void onFailure(Call<Timing> call, Throwable t) {
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
        new DisplayMorningsSlots(this).execute(CURRENT_DATE, MORNING_START_TIME, MORNING_END_TIME, DOCTOR_ID, CLINIC_ID);
    }

    /***** DISPLAY THE AFTERNOON SLOTS *****/
    private void theAfternoonSlots() {
        /* SHOW THE PROGRESS WHILE FETCHING THE DATA */
        listAfternoonTimes.setVisibility(View.GONE);
        linlaAfternoonEmpty.setVisibility(View.GONE);
        linlaAfternoonProgress.setVisibility(View.VISIBLE);

        /* CLEAR THE ARRAY LIST */
        arrAfternoonSlots.clear();

        /* DISPLAY THE AFTERNOON SLOTS */
        new DisplayAfternoonSlots(this).execute(CURRENT_DATE, AFTERNOON_START_TIME, AFTERNOON_END_TIME, DOCTOR_ID, CLINIC_ID);
    }

    @Override
    public void onMorningSlotResult(ArrayList<MorningTimeSlotsData> arrMorningSlots) {
        /* INSTANTIATE THE TIME SLOTS ADAPTER */
        morningCreatorAdapter = new MorningCreatorAdapter(AppointmentSlotCreator.this, arrMorningSlots);

        /* SET THE TIME SLOTS ADAPTER TO THE AFTERNOON RECYCLER VIEW */
        listMorningTimes.setAdapter(morningCreatorAdapter);

        /* SET THE LIST VISIBILITY */
        listMorningTimes.setVisibility(View.VISIBLE);
        linlaMorningEmpty.setVisibility(View.GONE);

        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
        listMorningTimes.setVisibility(View.VISIBLE);
        linlaMorningProgress.setVisibility(View.GONE);
    }

    @Override
    public void onAfternoonSlotResult(ArrayList<AfternoonTimeSlotsData> arrAfternoonSlots) {
        /* INSTANTIATE THE AFTERNOON TIME SLOTS ADAPTER */
        afternoonCreatorAdapter = new AfternoonCreatorAdapter(AppointmentSlotCreator.this, arrAfternoonSlots);

        /* SET THE TIME SLOTS ADAPTER TO THE AFTERNOON RECYCLER VIEW */
        listAfternoonTimes.setAdapter(afternoonCreatorAdapter);

        /* SET THE LIST VISIBILITY */
        listAfternoonTimes.setVisibility(View.VISIBLE);
        linlaAfternoonEmpty.setVisibility(View.GONE);

        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
        listAfternoonTimes.setVisibility(View.VISIBLE);
        linlaAfternoonProgress.setVisibility(View.GONE);
    }
}