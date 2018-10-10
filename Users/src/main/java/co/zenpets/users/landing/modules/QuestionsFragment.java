package co.zenpets.users.landing.modules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.creator.question.QuestionCreator;
import co.zenpets.users.utils.adapters.questions.QuestionsAdapter;
import co.zenpets.users.utils.helpers.classes.FilterQuestionsActivity;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.consultations.consultations.Consultation;
import co.zenpets.users.utils.models.consultations.consultations.Consultations;
import co.zenpets.users.utils.models.consultations.consultations.ConsultationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class QuestionsFragment extends Fragment {

    /** THE SELECTED PROBLEM ID AND TEXT (FOR FILTERING THE LIST OF QUESTIONS) **/
    private String PROBLEM_ID = null;
    private String PROBLEM_TEXT = null;

    /** THE CONSULTATIONS ARRAY LIST **/
    private ArrayList<Consultation> arrConsultations = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listConsult) RecyclerView listConsult;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW CONSULTATION QUESTION **/
    @OnClick(R.id.btnAskQuestion) void newConsultation()    {
        Intent intent = new Intent(getActivity(), QuestionCreator.class);
        startActivityForResult(intent, 101);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.fragment_question_list, container, false);
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

        /* CONFIGURE THE TOOLBAR */
        configToolbar();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF CONSULTATIONS */
        linlaProgress.setVisibility(View.VISIBLE);
        fetchConsultations();
    }

    /* FETCH THE LIST OF CONSULTATIONS */
    private void fetchConsultations() {
        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
        Call<Consultations> call = api.fetchConsultations(PROBLEM_ID);
        call.enqueue(new Callback<Consultations>() {
            @Override
            public void onResponse(Call<Consultations> call, Response<Consultations> response) {
                if (response.body() != null)    {
                    arrConsultations = response.body().getConsultations();
                    if (arrConsultations != null && arrConsultations.size() > 0)    {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listConsult.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listConsult.setAdapter(new QuestionsAdapter(getActivity(), arrConsultations));
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
            public void onFailure(Call<Consultations> call, Throwable t) {
//                Log.e("CONSULTATION FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            /* CLEAR THE ARRAY LIST */
            arrConsultations.clear();

            /* SHOW THE PROGRESS AND FETCH THE LIST OF CONSULTATIONS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchConsultations();

        } else if (resultCode == Activity.RESULT_OK && requestCode == 102)  {
            Bundle bundle = data.getExtras();
            if (bundle != null && bundle.containsKey("PROBLEM_ID")) {
                PROBLEM_ID = bundle.getString("PROBLEM_ID");
                PROBLEM_TEXT = bundle.getString("PROBLEM_TEXT");
                if (PROBLEM_ID != null && PROBLEM_TEXT != null) {

                    /* (RE) CONFIGURE THE TOOLBAR */
                    configToolbar();

                    /* CLEAR THE ARRAY LIST */
                    arrConsultations.clear();

                    /* SHOW THE PROGRESS AND FETCH THE LIST OF CONSULTATIONS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchConsultations();
                } else {
                    /* EXPLICITLY MARK THE PROBLEM ID "NULL" */
                    PROBLEM_ID = null;

                    /* (RE) CONFIGURE THE TOOLBAR */
                    configToolbar();

                    /* CLEAR THE ARRAY LIST */
                    if (arrConsultations != null)
                        arrConsultations.clear();

                    /* SHOW THE PROGRESS AND FETCH THE LIST OF CONSULTATIONS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchConsultations();
                }
            }
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listConsult.setLayoutManager(manager);
        listConsult.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listConsult.setAdapter(new QuestionsAdapter(getActivity(), arrConsultations));
    }

    /** CONFIGURE THE TOOLBAR **/
    private void configToolbar() {
        Toolbar myToolbar = getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);
        String strTitle = "Ask A Doctor";
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (PROBLEM_TEXT == null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(strTitle);
        } else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(strTitle + " (" + PROBLEM_TEXT + ")");
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
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
                Intent intent = new Intent(getActivity(), FilterQuestionsActivity.class);
                if (PROBLEM_ID != null) {
                    intent.putExtra("PROBLEM_ID", PROBLEM_ID);
                    startActivityForResult(intent, 102);
                } else {
                    startActivityForResult(intent, 102);
                }
                break;
            default:
                break;
        }
        return false;
    }
}