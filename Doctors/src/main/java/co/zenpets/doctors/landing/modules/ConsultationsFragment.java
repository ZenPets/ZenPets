package co.zenpets.doctors.landing.modules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.consultations.ConsultationsAdapter;
import co.zenpets.doctors.utils.helpers.classes.FilterConsultationsActivity;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.consultations.consultations.Consultation;
import co.zenpets.doctors.utils.models.consultations.consultations.Consultations;
import co.zenpets.doctors.utils.models.consultations.consultations.ConsultationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationsFragment extends Fragment {

    /** THE SELECTED PROBLEM ID (FOR FILTERING THE LIST OF QUESTIONS) **/
    private String PROBLEM_TEXT = null;
    private String PROBLEM_ID = null;

    /** THE CONSULTATIONS ADAPTER AND ARRAY LIST **/
    private ConsultationsAdapter adapter;
    private ArrayList<Consultation> arrConsultations = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.txtFilter) AppCompatTextView txtFilter;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listConsult) RecyclerView listConsult;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.home_consultation_fragment_list, container, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* INSTANTIATE THE DOCTORS ADAPTER */
        adapter = new ConsultationsAdapter(getActivity(), arrConsultations);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND START FETCHING THE LIST OF CONSULTATIONS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchConsultations();
//        new FetchConsultations(this).execute(PROBLEM_ID);
    }

    /* FETCH THE LIST OF CONSULTATIONS */
    private void fetchConsultations() {
        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
        Call<Consultations> call = api.fetchConsultations(PROBLEM_ID);
        call.enqueue(new Callback<Consultations>() {
            @Override
            public void onResponse(@NonNull Call<Consultations> call, @NonNull Response<Consultations> response) {
                if (response.body() != null)    {
                    arrConsultations = response.body().getConsultations();
                    if (arrConsultations != null && arrConsultations.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listConsult.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listConsult.setAdapter(new ConsultationsAdapter(getActivity(), arrConsultations));
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listConsult.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listConsult.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Consultations> call, @NonNull Throwable t) {
//                Log.e("CONSULTATION FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101)  {
            Bundle bundle = data.getExtras();
            if (bundle != null && bundle.containsKey("PROBLEM_ID")) {
                PROBLEM_ID = bundle.getString("PROBLEM_ID");
                PROBLEM_TEXT = bundle.getString("PROBLEM_TEXT");
                if (PROBLEM_ID != null && PROBLEM_TEXT != null) {

                    /* (RE) CONFIGURE THE TOOLBAR */
                    configAB();
                    getActivity().invalidateOptionsMenu();

                    /* CLEAR THE ARRAY LIST */
                    arrConsultations.clear();

                    /* SHOW THE PROGRESS AND FETCH THE LIST OF CONSULTATIONS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchConsultations();
//                    new FetchConsultations(this).execute(PROBLEM_ID);
                } else {
                    /* EXPLICITLY MARK THE PROBLEM ID "NULL" */
                    PROBLEM_ID = null;

                    /* (RE) CONFIGURE THE TOOLBAR */
                    configAB();
                    getActivity().invalidateOptionsMenu();

                    /* CLEAR THE ARRAY LIST */
                    arrConsultations.clear();

                    /* SHOW THE PROGRESS AND FETCH THE LIST OF CONSULTATIONS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchConsultations();
//                    new FetchConsultations(this).execute(PROBLEM_ID);
                }
            }
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = "Consultations";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (PROBLEM_TEXT == null) {
            actionBar.setTitle(s);
        } else {
            actionBar.setTitle(s + " (" + PROBLEM_TEXT + ")");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.menuFilter:
                Intent intent = new Intent(getActivity(), FilterConsultationsActivity.class);
                if (PROBLEM_ID != null) {
                    intent.putExtra("PROBLEM_ID", PROBLEM_ID);
                    startActivityForResult(intent, 101);
                } else {
                    startActivityForResult(intent, 101);
                }
                break;
            default:
                break;
        }
        return false;
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listConsult.setLayoutManager(manager);
        listConsult.setHasFixedSize(true);
        listConsult.setNestedScrollingEnabled(true);

        /* SET THE PATIENTS ADAPTER */
        listConsult.setAdapter(adapter);
    }
}