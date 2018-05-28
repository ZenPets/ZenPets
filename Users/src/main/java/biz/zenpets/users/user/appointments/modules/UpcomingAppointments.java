package biz.zenpets.users.user.appointments.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.doctors.NewDoctorsList;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.appointment.user.UpcomingAppointmentsAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.appointment.Appointment;
import biz.zenpets.users.utils.models.appointment.Appointments;
import biz.zenpets.users.utils.models.appointment.AppointmentsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingAppointments extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE APPOINTMENT DATA **/
    private String APPOINTMENT_DATE = null;

    /** THE UPCOMING APPOINTMENTS ARRAY LIST **/
    private ArrayList<Appointment> arrAppointments = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listUpcomingAppointments) RecyclerView listUpcomingAppointments;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** MAKE A NEW APPOINTMENT (FAB) **/
    @OnClick(R.id.fabNewAppointment) void fabNewAppointment()   {
        Intent intent = new Intent(getActivity(), NewDoctorsList.class);
        startActivity(intent);
    }

    /** MAKE A NEW APPOINTMENT (EMPTY BUTTON) **/
    @OnClick(R.id.btnMakeAppointment) void btnNewAppointment()  {
        Intent intent = new Intent(getActivity(), NewDoctorsList.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.user_appointments_upcoming_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* GET THE CURRENT DATE */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        APPOINTMENT_DATE = sdf.format(cal.getTime());

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        
        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SHOW THE PROGRESS BAR AND FETCH THE USER'S UPCOMING APPOINTMENTS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchUpcomingAppointments();
    }

    /***** FETCH THE USER'S UPCOMING APPOINTMENTS *****/
    private void fetchUpcomingAppointments() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<Appointments> call = api.fetchUserUpcomingAppointments(USER_ID, APPOINTMENT_DATE);
        call.enqueue(new Callback<Appointments>() {
            @Override
            public void onResponse(Call<Appointments> call, Response<Appointments> response) {
                if (response.body() != null && response.body().getAppointments() != null)   {
                    /* GET THE APPOINTMENTS RESULT */
                    arrAppointments = response.body().getAppointments();

                    /* CHECK IF VALUES WERE RETURNED */
                    if (arrAppointments.size() > 0)  {
                        /* SHOW THE APPOINTMENTS RECYCLER AND HIDE THE EMPTY LAYOUT */
                        listUpcomingAppointments.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE DOCTORS RECYCLER VIEW */
                        listUpcomingAppointments.setAdapter(new UpcomingAppointmentsAdapter(getActivity(), arrAppointments));

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                        listUpcomingAppointments.setVisibility(View.GONE);
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                    listUpcomingAppointments.setVisibility(View.GONE);
                    linlaEmpty.setVisibility(View.VISIBLE);

                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Appointments> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listUpcomingAppointments.setLayoutManager(manager);
        listUpcomingAppointments.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listUpcomingAppointments.setAdapter(new UpcomingAppointmentsAdapter(getActivity(), arrAppointments));
    }
}