package co.zenpets.doctors.credentials;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.adapters.doctors.claim.ClaimProfileAdapter;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.Doctor;
import co.zenpets.doctors.utils.models.doctors.Doctors;
import co.zenpets.doctors.utils.models.doctors.DoctorsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimProfileList extends AppCompatActivity {

    /** AN ARRAY LIST OF DOCTORS **/
    private ArrayList<Doctor> arrDoctors = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.searchDoctors) SearchView searchDoctors;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_profile_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* SEARCH DOCTORS */
        searchDoctors.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /* CLEAR THE FOCUS */
                searchDoctors.clearFocus();

                /* CLEAR THE ARRAY LIST */
                if (arrDoctors != null)
                    arrDoctors.clear();

                /* SHOW THE PROGRESS BAR */
                linlaProgress.setVisibility(View.VISIBLE);

                /* FETCH THE SEARCH RESULTS */
                searchDoctors(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /***** SEARCH DOCTORS *****/
    private void searchDoctors(String query) {
        DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
        Call<Doctors> call = api.doctorSearch(query);
        call.enqueue(new Callback<Doctors>() {
            @Override
            public void onResponse(@NonNull Call<Doctors> call, @NonNull Response<Doctors> response) {
                if (response.body() != null && response.body().getDoctors() != null)    {
                    arrDoctors = response.body().getDoctors();
                    if (arrDoctors.size() > 0)  {
                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listDoctors.setAdapter(new ClaimProfileAdapter(ClaimProfileList.this, arrDoctors));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listDoctors.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS LAYOUT */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listDoctors.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS LAYOUT */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listDoctors.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS LAYOUT */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Doctors> call, @NonNull Throwable t) {
//                Log.e("SEARCH FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctors.setLayoutManager(manager);
        listDoctors.setHasFixedSize(true);

        /* SET THE ADAPTER */
        listDoctors.setAdapter(new ClaimProfileAdapter(ClaimProfileList.this, arrDoctors));
    }
}