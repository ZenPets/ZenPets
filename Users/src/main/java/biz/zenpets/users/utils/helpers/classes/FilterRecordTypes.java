package biz.zenpets.users.utils.helpers.classes;

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

import biz.zenpets.users.R;
import biz.zenpets.users.utils.adapters.pet.records.FilterRecordTypesAdapter;
import biz.zenpets.users.utils.helpers.pets.records.FetchRecordTypes;
import biz.zenpets.users.utils.helpers.pets.records.FetchRecordTypesInterface;
import biz.zenpets.users.utils.models.pets.records.RecordType;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterRecordTypes extends AppCompatActivity implements FetchRecordTypesInterface {

    /** THE INCOMING RECORD TYPE ID **/
    private String INCOMING_RECORD_TYPE_ID = null;

    /** THE SELECTED RECORD TYPE ID AND NAME **/
    private String SELECTED_RECORD_TYPE_ID = null;
    private String SELECTED_RECORD_TYPE_NAME = null;

    /** THE RECORD TYPES ADAPTER AND ARRAY LIST **/
    private FilterRecordTypesAdapter problemsAdapter;
    private ArrayList<RecordType> arrRecords = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtClear) AppCompatTextView txtClear;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listProblems) ListView listProblems;
    @BindView(R.id.btnApplyFilter) AppCompatButton btnApplyFilter;

    /** APPLY THE FILTER AND PASS BACK THE SELECTED PROBLEM ID **/
    @OnClick(R.id.btnApplyFilter) void applyFilter()    {
        Intent intent = new Intent();
        intent.putExtra("RECORD_TYPE_ID", SELECTED_RECORD_TYPE_ID);
        intent.putExtra("RECORD_TYPE_NAME", SELECTED_RECORD_TYPE_NAME);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** CLEAR THE SELECTION **/
    @OnClick(R.id.txtClear) void clearSelection()   {
        if (INCOMING_RECORD_TYPE_ID != null)    {

            /* CLEAR THE SELECTION */
            SELECTED_RECORD_TYPE_ID = null;
            SELECTED_RECORD_TYPE_NAME = null;

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

        /* FETCH THE LIST OF RECORD TYPES */
        new FetchRecordTypes(this).execute();

        listProblems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
                problemsAdapter.setSelectedIndex(position);
                problemsAdapter.notifyDataSetChanged();

                /* GET THE SELECTED RECORD TYPE ID AND NAME */
                SELECTED_RECORD_TYPE_ID = arrRecords.get(position).getRecordTypeID();
                SELECTED_RECORD_TYPE_NAME = arrRecords.get(position).getRecordTypeName();

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

    @Override
    public void allRecordTypes(ArrayList<RecordType> data) {
        /* CAST THE CONTENTS IN THE GLOBAL INSTANCE */
        arrRecords = data;

        /* INSTANTIATE THE PROBLEMS LIST ADAPTER */
        problemsAdapter = new FilterRecordTypesAdapter(FilterRecordTypes.this, arrRecords);

        /* SET THE ADAPTER TO THE PROBLEMS RECYCLER VIEW */
        listProblems.setAdapter(problemsAdapter);

        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
        linlaProgress.setVisibility(View.GONE);

        /* GET THE INCOMING DATA (IF AVAILABLE) */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("RECORD_TYPE_ID")) {
            INCOMING_RECORD_TYPE_ID = bundle.getString("RECORD_TYPE_ID");
            int intProblemPosition = getProblemIndex(arrRecords, INCOMING_RECORD_TYPE_ID);
            listProblems.setSelection(intProblemPosition);

            /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
            problemsAdapter.setSelectedIndex(intProblemPosition);
            problemsAdapter.notifyDataSetChanged();

            /* SHOW THE CLEAR BUTTON */
            txtClear.setVisibility(View.VISIBLE);
        } else {
            INCOMING_RECORD_TYPE_ID = null;
        }

    }

    /***** FETCH THE LIST OF PROBLEMS *****/
//    private void fetchListOfProblems() {
//        /* SHOW THE PROGRESS AND FETCH THE DATA */
//        linlaProgress.setVisibility(View.VISIBLE);
//
//        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
//        Call<RecordTypes> call = api.allRecordTypes();
//        call.enqueue(new Callback<RecordTypes>() {
//            @Override
//            public void onResponse(Call<RecordTypes> call, Response<RecordTypes> response) {
//                arrRecords = response.body().getRecords();
//
//                /* INSTANTIATE THE PROBLEMS LIST ADAPTER */
//                problemsAdapter = new FilterRecordTypesAdapter(FilterRecordTypes.this, arrRecords);
//
//                /* SET THE ADAPTER TO THE PROBLEMS RECYCLER VIEW */
//                listProblems.setAdapter(problemsAdapter);
//
//                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//                linlaProgress.setVisibility(View.GONE);
//
//                /* GET THE INCOMING DATA (IF AVAILABLE) */
//                Bundle bundle = getIntent().getExtras();
//                if (bundle != null && bundle.containsKey("RECORD_TYPE_ID")) {
//                    INCOMING_RECORD_TYPE_ID = bundle.getString("RECORD_TYPE_ID");
//                    int intProblemPosition = getProblemIndex(arrRecords, INCOMING_RECORD_TYPE_ID);
//                    listProblems.setSelection(intProblemPosition);
//
//                    /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
//                    problemsAdapter.setSelectedIndex(intProblemPosition);
//                    problemsAdapter.notifyDataSetChanged();
//
//                    /* SHOW THE CLEAR BUTTON */
//                    txtClear.setVisibility(View.VISIBLE);
//                } else {
//                    INCOMING_RECORD_TYPE_ID = null;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RecordTypes> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
//    }

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
    private int getProblemIndex(ArrayList<RecordType> array, String problemID) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getRecordTypeID().equalsIgnoreCase(problemID))   {
                index = i;
                break;
            }
        }
        return index;
    }
}