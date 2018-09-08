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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.adapters.pet.vaccinations.FilterVaccinesAdapter;
import biz.zenpets.users.utils.models.pets.vaccines.Vaccine;
import biz.zenpets.users.utils.models.pets.vaccines.Vaccines;
import biz.zenpets.users.utils.models.pets.vaccines.VaccinesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterVaccinesTypes extends AppCompatActivity {

    /** THE INCOMING VACCINE ID **/
    private String INCOMING_VACCINE_ID = null;

    /** THE SELECTED VACCINES ID AND NAME **/
    private String SELECTED_VACCINE_ID = null;
    private String SELECTED_VACCINE_NAME = null;

    /** THE VACCINES ADAPTER AND ARRAY LIST **/
    private FilterVaccinesAdapter vaccinesAdapter;
    private ArrayList<Vaccine> arrVaccines = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtClear) AppCompatTextView txtClear;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listProblems) ListView listProblems;
    @BindView(R.id.btnApplyFilter) AppCompatButton btnApplyFilter;

    /** APPLY THE FILTER AND PASS BACK THE SELECTED PROBLEM ID **/
    @OnClick(R.id.btnApplyFilter) void applyFilter()    {
        Intent intent = new Intent();
        intent.putExtra("VACCINE_ID", SELECTED_VACCINE_ID);
        intent.putExtra("VACCINE_NAME", SELECTED_VACCINE_NAME);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** CLEAR THE SELECTION **/
    @OnClick(R.id.txtClear) void clearSelection()   {
        if (INCOMING_VACCINE_ID != null)    {

            /* CLEAR THE SELECTION */
            SELECTED_VACCINE_ID = null;
            SELECTED_VACCINE_NAME = null;

            /* REMOVE THE SELECTION */
            vaccinesAdapter.setSelectedIndex(-1);
            vaccinesAdapter.notifyDataSetChanged();
            txtClear.setVisibility(View.GONE);
            btnApplyFilter.setVisibility(View.VISIBLE);

        } else {
            /* REMOVE THE SELECTION */
            vaccinesAdapter.setSelectedIndex(-1);
            vaccinesAdapter.notifyDataSetChanged();
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

        /* SHOW THE PROGRESS AND FETCH THE DATA */
        linlaProgress.setVisibility(View.VISIBLE);

        /* FETCH THE LIST OF VACCINES */
        fetchVaccines();

        listProblems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
                vaccinesAdapter.setSelectedIndex(position);
                vaccinesAdapter.notifyDataSetChanged();

                /* GET THE SELECTED VACCINE ID AND NAME */
                SELECTED_VACCINE_ID = arrVaccines.get(position).getVaccineID();
                SELECTED_VACCINE_NAME = arrVaccines.get(position).getVaccineName();

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

    /** FETCH THE LIST OF VACCINES **/
    private void fetchVaccines() {
        VaccinesAPI api = ZenApiClient.getClient().create(VaccinesAPI.class);
        Call<Vaccines> call = api.allVaccines();
        call.enqueue(new Callback<Vaccines>() {
            @Override
            public void onResponse(Call<Vaccines> call, Response<Vaccines> response) {
                if (response.body() != null && response.body().getVaccines() != null)   {
                    arrVaccines = response.body().getVaccines();
                    if (arrVaccines.size() > 0) {

                        /* INSTANTIATE THE VACCINES LIST ADAPTER */
                        vaccinesAdapter = new FilterVaccinesAdapter(FilterVaccinesTypes.this, arrVaccines);

                        /* SET THE ADAPTER TO THE PROBLEMS RECYCLER VIEW */
                        listProblems.setAdapter(vaccinesAdapter);

                        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                        linlaProgress.setVisibility(View.GONE);

                        /* GET THE INCOMING DATA (IF AVAILABLE) */
                        Bundle bundle = getIntent().getExtras();
                        if (bundle != null && bundle.containsKey("VACCINE_ID")) {
                            INCOMING_VACCINE_ID = bundle.getString("VACCINE_ID");
                            int intProblemPosition = getProblemIndex(arrVaccines, INCOMING_VACCINE_ID);
                            listProblems.setSelection(intProblemPosition);

                            /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
                            vaccinesAdapter.setSelectedIndex(intProblemPosition);
                            vaccinesAdapter.notifyDataSetChanged();

                            /* SHOW THE CLEAR BUTTON */
                            txtClear.setVisibility(View.VISIBLE);
                        } else {
                            INCOMING_VACCINE_ID = null;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Problem fetching list of Vaccines...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Problem fetching list of Vaccines...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Vaccines> call, Throwable t) {
//                Log.e("VACCINES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

//    @Override
//    public void allVaccines(ArrayList<Vaccine> data) {
//        /* CAST THE RESULTS IN THE GLOBAL INSTANCE */
//        arrVaccines = data;
//
//        /* INSTANTIATE THE VACCINES LIST ADAPTER */
//        vaccinesAdapter = new FilterVaccinesAdapter(FilterVaccinesTypes.this, arrVaccines);
//
//        /* SET THE ADAPTER TO THE PROBLEMS RECYCLER VIEW */
//        listProblems.setAdapter(vaccinesAdapter);
//
//        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//        linlaProgress.setVisibility(View.GONE);
//
//        /* GET THE INCOMING DATA (IF AVAILABLE) */
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null && bundle.containsKey("VACCINE_ID")) {
//            INCOMING_VACCINE_ID = bundle.getString("VACCINE_ID");
//            int intProblemPosition = getProblemIndex(arrVaccines, INCOMING_VACCINE_ID);
//            listProblems.setSelection(intProblemPosition);
//
//            /* MODIFY THE ADAPTER TO REFLECT THE SELECTION*/
//            vaccinesAdapter.setSelectedIndex(intProblemPosition);
//            vaccinesAdapter.notifyDataSetChanged();
//
//            /* SHOW THE CLEAR BUTTON */
//            txtClear.setVisibility(View.VISIBLE);
//        } else {
//            INCOMING_VACCINE_ID = null;
//        }
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
    private int getProblemIndex(ArrayList<Vaccine> array, String problemID) {
        int index = 0;
        for (int i =0; i < array.size(); i++) {
            if (array.get(i).getVaccineID().equalsIgnoreCase(problemID))   {
                index = i;
                break;
            }
        }
        return index;
    }
}