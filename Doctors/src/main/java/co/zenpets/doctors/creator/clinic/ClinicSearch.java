package co.zenpets.doctors.creator.clinic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.clinics.ClinicsSearchAdapter;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.clinics.ClinicData;
import co.zenpets.doctors.utils.models.clinics.ClinicsData;
import co.zenpets.doctors.utils.models.clinics.ClinicsSearchAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicSearch extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /** THE CLINICS ARRAY LIST **/
    private ArrayList<ClinicData> arrClinics = new ArrayList<>();

    /** THE SEARCH VIEW INSTANCE **/
    private SearchView searchView;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaSearchHint) LinearLayout linlaSearchHint;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listClinics) RecyclerView listClinics;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW CLINIC **/
    @OnClick(R.id.linlaEmpty) protected void newClinic()    {
        Intent intent = new Intent(ClinicSearch.this, ClinicCreator.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clinic_search_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SHOW THE SEARCH HINT LAYOUT AND HIDE THE RECYCLER VIEW */
        linlaSearchHint.setVisibility(View.VISIBLE);
        listClinics.setVisibility(View.GONE);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Search Clinics";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_clinic_search, menu);
        MenuItem search = menu.findItem(R.id.search);
        searchView = (SearchView) search.getActionView();
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        /* CLEAR THE FOCUS */
        searchView.clearFocus();

        /* CLEAR THE ARRAY LIST */
        if (arrClinics != null)
            arrClinics.clear();

        /* HIDE THE SEARCH HINT LAYOUT AND SHOW THE PROGRESS BAR */
        linlaSearchHint.setVisibility(View.GONE);
        linlaProgress.setVisibility(View.VISIBLE);

        /* FETCH THE SEARCH RESULTS */
        clinicSearch(query);
        return false;
    }

    /***** FETCH THE SEARCH RESULTS *****/
    private void clinicSearch(String query) {
        ClinicsSearchAPI api = ZenApiClient.getClient().create(ClinicsSearchAPI.class);
        Call<ClinicsData> call = api.clinicSearch(query);
        call.enqueue(new Callback<ClinicsData>() {
            @Override
            public void onResponse(@NonNull Call<ClinicsData> call, @NonNull Response<ClinicsData> response) {
                arrClinics = response.body().getClinics();
                if (arrClinics != null && arrClinics.size() > 0)    {
                    /* SET THE ADAPTER TO THE RECYCLER VIEW */
                    listClinics.setAdapter(new ClinicsSearchAdapter(ClinicSearch.this, arrClinics));

                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listClinics.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW  */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listClinics.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE SEARCH RESULTS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ClinicsData> call, @NonNull Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listClinics.setLayoutManager(manager);
        listClinics.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listClinics.setAdapter(new ClinicsSearchAdapter(ClinicSearch.this, arrClinics));
    }
}