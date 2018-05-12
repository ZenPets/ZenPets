package biz.zenpets.trainers.landing.modules;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.adapters.enquiries.TrainingEnquiriesAdapter;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.enquiries.Enquiries;
import biz.zenpets.trainers.utils.models.trainers.enquiries.EnquiriesAPI;
import biz.zenpets.trainers.utils.models.trainers.enquiries.Enquiry;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnquiriesFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN TRAINER'S ID **/
    String TRAINER_ID = null;

    /** THE TRAINING ENQUIRIES ARRAY LIST **/
    ArrayList<Enquiry> arrEnquiries = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listTrainingEnquiries) RecyclerView listTrainingEnquiries;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.enquiries_fragment_list, container, false);
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
    }

    /***** FETCH THE LIST OF TRAINING ENQUIRIES *****/
    private void fetchTrainingEnquiries() {
        EnquiriesAPI api = ZenApiClient.getClient().create(EnquiriesAPI.class);
        Call<Enquiries> call = api.fetchTrainingEnquiries(TRAINER_ID);
        call.enqueue(new Callback<Enquiries>() {
            @Override
            public void onResponse(Call<Enquiries> call, Response<Enquiries> response) {
                if (response.body() != null && response.body().getEnquiries() != null)  {
                    arrEnquiries = response.body().getEnquiries();
                    if (arrEnquiries.size() > 0)    {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listTrainingEnquiries.setAdapter(new TrainingEnquiriesAdapter(getActivity(), arrEnquiries));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listTrainingEnquiries.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listTrainingEnquiries.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listTrainingEnquiries.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Enquiries> call, Throwable t) {
                Log.e("ENQUIRIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* GET THE LOGGED IN TRAINER'S ID */
        TRAINER_ID = getApp().getTrainerID();
//        Log.e("TRAINER ID", TRAINER_ID);

        /* FETCH THE LIST OF TRAINING MODULES */
        if (TRAINER_ID != null) {
            /* SHOW THE PROGRESS AND FETCH THE LIST OF TRAINING ENQUIRIES */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchTrainingEnquiries();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listTrainingEnquiries.setLayoutManager(manager);
        listTrainingEnquiries.setHasFixedSize(true);

        /* SET THE ADAPTER TO THE RECYCLER VIEW */
        listTrainingEnquiries.setAdapter(new TrainingEnquiriesAdapter(getActivity(), arrEnquiries));
    }
}