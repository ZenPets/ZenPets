package biz.zenpets.users.kennels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.kennels.TestKennelsAdapter;
import biz.zenpets.users.utils.helpers.classes.PaginationScrollListener;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.KennelPages;
import biz.zenpets.users.utils.models.kennels.Kennels;
import biz.zenpets.users.utils.models.kennels.KennelsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelsList extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** STRING TO HOLD THE DETECTED CITY NAME FOR QUERYING THE ADOPTIONS **/
    private String DETECTED_CITY = null;
    private String FINAL_CITY_ID = null;

    /** THE KENNELS ADAPTER **/
    TestKennelsAdapter adapter;

    /** A LINEAR LAYOUT MANAGER INSTANCE **/
    LinearLayoutManager manager;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.progressLoading) ProgressBar progressLoading;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennels_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET THE CITY ID, CITY NAME AND THE ORIGIN LATITUDE LONGITUDE */
        String[] arrCity = getApp().getCityDetails();
        FINAL_CITY_ID = arrCity[0];
        DETECTED_CITY = arrCity[1];
        txtLocation.setText(DETECTED_CITY);
//        Log.e("CITY ID", FINAL_CITY_ID);

        /* FETCH THE TOTAL NUMBER OF PAGES */
        fetchTotalPages();

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* FETCH THE FIRST SET OF KENNELS (THE FIRST PAGE) */
        fetchFirstKennelPage();
    }

    /** FETCH THE FIRST SET OF KENNELS (THE FIRST PAGE) **/
    private void fetchFirstKennelPage() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID, String.valueOf(currentPage));
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
                ArrayList<Kennel> kennels = fetchResults(response);
                progressLoading.setVisibility(View.GONE);
                adapter.addAll(kennels);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
                Log.e("FIRST FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE NEXT SET OF KENNELS **/
    private void fetchNextKennelPage() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID, String.valueOf(currentPage));
        call.enqueue(new Callback<Kennels>() {
            @Override
            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
                ArrayList<Kennel> kennels = fetchResults(response);
                adapter.addAll(kennels);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Kennels> call, Throwable t) {
                Log.e("NEXT FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** PROCESS THE RESULT AND CAST IN THE ARRAY LIST **/
    private ArrayList<Kennel> fetchResults(Response<Kennels> response) {
        Kennels kennels = response.body();
        return kennels.getKennels();
    }

    /** FETCH THE TOTAL NUMBER OF PAGES **/
    private void fetchTotalPages() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<KennelPages> call = api.fetchKennelPages(FINAL_CITY_ID);
        call.enqueue(new Callback<KennelPages>() {
            @Override
            public void onResponse(Call<KennelPages> call, Response<KennelPages> response) {
                if (response.body() != null && response.body().getTotalPages() != null) {
                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));
                }
            }

            @Override
            public void onFailure(Call<KennelPages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        adapter = new TestKennelsAdapter(KennelsList.this);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listKennels.setLayoutManager(manager);
        listKennels.setItemAnimator(new DefaultItemAnimator());
        listKennels.setAdapter(adapter);

        listKennels.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                /* FETCH THE NEXT SET OF KENNELS */
                fetchNextKennelPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
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
}