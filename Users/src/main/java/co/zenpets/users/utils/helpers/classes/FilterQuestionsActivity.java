package co.zenpets.users.utils.helpers.classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.problems.ProblemsListAdapter;
import co.zenpets.users.utils.models.problems.Problem;
import co.zenpets.users.utils.models.problems.Problems;
import co.zenpets.users.utils.models.problems.ProblemsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterQuestionsActivity extends AppCompatActivity {

    /** THE INCOMING PROBLEM ID AND TEXT **/
    private String INCOMING_PROBLEM_ID = null;

    /** THE SELECTED PROBLEM ID AND PROBLEM TEXT **/
    private String SELECTED_PROBLEM_ID = null;
    private String SELECTED_PROBLEM_TEXT = null;

    /** THE PROBLEMS LIST ADAPTER AND ARRAY LIST **/
    private ProblemsListAdapter problemsAdapter;
    private ArrayList<Problem> arrProblems = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtClear) AppCompatTextView txtClear;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listProblems) ListView listProblems;
    @BindView(R.id.btnApplyFilter) AppCompatButton btnApplyFilter;

    /** APPLY THE FILTER AND PASS BACK THE SELECTED PROBLEM ID **/
    @OnClick(R.id.btnApplyFilter) void applyFilter()    {
        Intent intent = new Intent();
        intent.putExtra("PROBLEM_ID", SELECTED_PROBLEM_ID);
        intent.putExtra("PROBLEM_TEXT", SELECTED_PROBLEM_TEXT);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** CLEAR THE SELECTION **/
    @OnClick(R.id.txtClear) void clearSelection()   {
        if (INCOMING_PROBLEM_ID != null)    {

            /* CLEAR THE SELECTION */
            SELECTED_PROBLEM_ID = null;
            SELECTED_PROBLEM_TEXT = null;

            /* REMOVE THE SELECTION */
            problemsAdapter.setSelectedIndex(-1);
            problemsAdapter.notifyDataSetChanged();
            txtClear.setVisibility(View.GONE);
            btnApplyFilter.setVisibility(View.VISIBLE);

        } else {
            /* REMOVE THE SELECTION */
            problemsAdapter.setSelectedIndex(-1);
            problemsAdapter.notifyDataSetChanged();
            txtClear.setVisibility(View.GONE);
            btnApplyFilter.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_questions_activity);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* FETCH THE LIST OF PROBLEMS */
        fetchListOfProblems();

        listProblems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
                problemsAdapter.setSelectedIndex(position);
                problemsAdapter.notifyDataSetChanged();

                /* GET THE SELECTED PROBLEM ID */
                SELECTED_PROBLEM_ID = arrProblems.get(position).getProblemID();
                SELECTED_PROBLEM_TEXT = arrProblems.get(position).getProblemText();

                /* SHOW OR HIDE THE APPLY BUTTON */
                if (position < 0)   {
                    btnApplyFilter.setVisibility(View.GONE);
                } else {
                    btnApplyFilter.setVisibility(View.VISIBLE);
                }

                /* SHOW OR HIDE THE CLEAR BUTTON */
                if (position < 0)   {
                    txtClear.setVisibility(View.GONE);
                } else {
                    txtClear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /***** FETCH THE LIST OF PROBLEMS *****/
    private void fetchListOfProblems() {
        /* SHOW THE PROGRESS AND FETCH THE DATA */
        linlaProgress.setVisibility(View.VISIBLE);

        ProblemsAPI api = ZenApiClient.getClient().create(ProblemsAPI.class);
        Call<Problems> call = api.allProblemTypes();
        call.enqueue(new Callback<Problems>() {
            @Override
            public void onResponse(Call<Problems> call, Response<Problems> response) {
                arrProblems = response.body().getProblems();

                /* INSTANTIATE THE PROBLEMS LIST ADAPTER */
                problemsAdapter = new ProblemsListAdapter(FilterQuestionsActivity.this, arrProblems);

                /* SET THE ADAPTER TO THE PROBLEMS RECYCLER VIEW */
                listProblems.setAdapter(problemsAdapter);

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);

                /* GET THE INCOMING DATA (IF AVAILABLE) */
                Bundle bundle = getIntent().getExtras();
                if (bundle != null && bundle.containsKey("PROBLEM_ID")) {
                    INCOMING_PROBLEM_ID = bundle.getString("PROBLEM_ID");
                    int intProblemPosition = getProblemIndex(arrProblems, INCOMING_PROBLEM_ID);
                    listProblems.setSelection(intProblemPosition);

                    /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
                    problemsAdapter.setSelectedIndex(intProblemPosition);
                    problemsAdapter.notifyDataSetChanged();

                    /* SHOW THE CLEAR BUTTON */
                    txtClear.setVisibility(View.VISIBLE);
                } else {
                    INCOMING_PROBLEM_ID = null;
                }
            }

            @Override
            public void onFailure(Call<Problems> call, Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
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

    /***** GET THE PROBLEM POSITION *****/
    private int getProblemIndex(ArrayList<Problem> array, String problemID) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getProblemID().equalsIgnoreCase(problemID))   {
                index = i;
                break;
            }
        }
        return index;
    }
}