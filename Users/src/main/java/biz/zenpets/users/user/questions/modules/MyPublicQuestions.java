package biz.zenpets.users.user.questions.modules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.question.QuestionCreator;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.questions.UserQuestionsAdapter;
import biz.zenpets.users.utils.helpers.classes.FilterQuestionsActivity;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.consultations.consultations.Consultation;
import biz.zenpets.users.utils.models.consultations.consultations.Consultations;
import biz.zenpets.users.utils.models.consultations.consultations.ConsultationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPublicQuestions extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE SELECTED PROBLEM ID (FOR FILTERING THE LIST OF QUESTIONS) **/
    private String PROBLEM_ID = null;

    /** THE USER CONSULTATIONS ARRAY LIST **/
    private ArrayList<Consultation> arrConsultations = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.txtFilter) AppCompatTextView txtFilter;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listMyConsultations) RecyclerView listMyConsultations;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    /** FILTER THE LIST OF CONSULTATION QUESTION **/
//    @OnClick(R.id.linlaFilter) void filterQuestions()   {
//        Intent intent = new Intent(getActivity(), FilterQuestionsActivity.class);
//        if (PROBLEM_ID != null) {
//            intent.putExtra("PROBLEM_ID", PROBLEM_ID);
//            startActivityForResult(intent, 102);
//        } else {
//            startActivityForResult(intent, 102);
//        }
//    }

    /** ASK A NEW QUESTION **/
    @OnClick(R.id.btnAskQuestion) void askQuestion()    {
        Intent intent = new Intent(getActivity(), QuestionCreator.class);
        startActivityForResult(intent, 101);
    }

    /** ASK A NEW QUESTION **/
    @OnClick(R.id.fabNewQuestion) void newQuestion()    {
        Intent intent = new Intent(getActivity(), QuestionCreator.class);
        startActivityForResult(intent, 101);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.my_public_questions, container, false);
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

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
        if (USER_ID != null) {
            /* SHOW THE PROGRESS AND FETCH THE USER'S CONSULTATION QUESTIONS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchUserPublicConsultations();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info....", Toast.LENGTH_SHORT).show();
        }
    }

    /***** FETCH THE USER'S CONSULTATION QUESTIONS *****/
    private void fetchUserPublicConsultations() {
        ConsultationsAPI api = ZenApiClient.getClient().create(ConsultationsAPI.class);
        Call<Consultations> call = api.listUserConsultations(USER_ID, PROBLEM_ID);
        call.enqueue(new Callback<Consultations>() {
            @Override
            public void onResponse(Call<Consultations> call, Response<Consultations> response) {
                arrConsultations = response.body().getConsultations();

                /* CHECK IF THERE ARE RESULTS TO DISPLAY */
                if (arrConsultations.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listMyConsultations.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE ADAPTER TO THE RECYCLER VIEW */
                    listMyConsultations.setAdapter(new UserQuestionsAdapter(getActivity(), arrConsultations));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listMyConsultations.setVisibility(View.GONE);

                    /* SET THE APPROPRIATE ERROR MESSAGE */
                    String errorMessage;
                    if (PROBLEM_ID == null) {
                        errorMessage = getString(R.string.user_public_question_empty);
                        txtEmpty.setText(errorMessage);
                    } else {
                        errorMessage = getString(R.string.user_public_question_empty_filtered);
                        txtEmpty.setText(errorMessage);
                    }
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Consultations> call, Throwable t) {
//                Log.e("CONSULTATIONS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listMyConsultations.setLayoutManager(manager);
        listMyConsultations.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listMyConsultations.setAdapter(new UserQuestionsAdapter(getActivity(), arrConsultations));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            /* CLEAR THE ARRAY LIST */
            arrConsultations.clear();

            /* SHOW THE PROGRESS AND FETCH THE USER'S CONSULTATION QUESTIONS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchUserPublicConsultations();

        } else if (resultCode == Activity.RESULT_OK && requestCode == 102)  {
            Bundle bundle = data.getExtras();
            if (bundle != null && bundle.containsKey("PROBLEM_ID")) {
                PROBLEM_ID = bundle.getString("PROBLEM_ID");
                String PROBLEM_TEXT = bundle.getString("PROBLEM_TEXT");
                if (PROBLEM_ID != null && PROBLEM_TEXT != null) {
//                    /* SET THE PROBLEM TYPE */
//                    txtFilter.setText(PROBLEM_TEXT);

                    /* CLEAR THE ARRAY LIST */
                    arrConsultations.clear();

                    /* SHOW THE PROGRESS AND FETCH THE USER'S CONSULTATION QUESTIONS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchUserPublicConsultations();
                } else {
                    /* EXPLICITLY MARK THE PROBLEM ID "NULL" */
                    PROBLEM_ID = null;

//                    /* SET THE PROBLEM TYPE TO THE DEFAULT "FILTER" */
//                    txtFilter.setText(getString(R.string.user_public_question_filter));

                    /* CLEAR THE ARRAY LIST */
                    arrConsultations.clear();

                    /* SHOW THE PROGRESS AND FETCH THE USER'S CONSULTATION QUESTIONS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchUserPublicConsultations();
                }
            }
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