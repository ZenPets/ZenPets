package co.zenpets.users.user.appointments.modules;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.appointment.user.PastAppointmentsAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.appointment.Appointment;
import co.zenpets.users.utils.models.appointment.Appointments;
import co.zenpets.users.utils.models.appointment.AppointmentsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastAppointments extends Fragment {

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
    @BindView(R.id.listPastAppointments) RecyclerView listPastAppointments;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.user_appointments_past_list, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* GET THE CURRENT DATE */
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        APPOINTMENT_DATE = sdf.format(cal.getTime());
//        Log.e("APPOINTMENT DATE", APPOINTMENT_DATE);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        
        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SHOW THE PROGRESS AND START FETCHING THE USER'S PAST APPOINTMENTS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchPastAppointments();
    }

    /***** FETCH THE USER'S UPCOMING APPOINTMENTS *****/
    private void fetchPastAppointments() {
        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<Appointments> call = api.fetchUserPastAppointments(USER_ID, APPOINTMENT_DATE);
        call.enqueue(new Callback<Appointments>() {
            @Override
            public void onResponse(Call<Appointments> call, Response<Appointments> response) {
                /* GET THE APPOINTMENTS RESULT */
                arrAppointments = response.body().getAppointments();

                /* CHECK IF VALUES WERE RETURNED */
                if (arrAppointments.size() > 0)  {
                    /* SHOW THE APPOINTMENTS RECYCLER AND HIDE THE EMPTY LAYOUT */
                    listPastAppointments.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE DOCTORS RECYCLER VIEW */
                    listPastAppointments.setAdapter(new PastAppointmentsAdapter(getActivity(), arrAppointments));
                } else {
                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                    listPastAppointments.setVisibility(View.GONE);
                    linlaEmpty.setVisibility(View.VISIBLE);
                }

                /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Appointments> call, Throwable t) {
//                Log.e("APPOINTMENTS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listPastAppointments.setLayoutManager(manager);
        listPastAppointments.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listPastAppointments.setAdapter(new PastAppointmentsAdapter(getActivity(), arrAppointments));
    }
}