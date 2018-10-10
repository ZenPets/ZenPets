package co.zenpets.users.details.kennels.reviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.adapters.kennels.reviews.KennelReviewsAdapter;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.kennels.reviews.KennelReview;
import co.zenpets.users.utils.models.kennels.reviews.KennelReviews;
import co.zenpets.users.utils.models.kennels.reviews.KennelReviewsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelReviewsActivity extends AppCompatActivity {

    /** THE INCOMING KENNEL ID AND NAME **/
    private String KENNEL_ID = null;
    private String KENNEL_NAME = null;

    /** A KENNEL REVIEWS ARRAY LIST INSTANCE **/
    private ArrayList<KennelReview> arrReviews = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennelReviews) RecyclerView listKennelReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) TextView txtEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_reviews_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /** FETCH THE KENNEL'S REVIEWS **/
    private void fetchKennelReviews() {
        KennelReviewsAPI api = ZenApiClient.getClient().create(KennelReviewsAPI.class);
        Call<KennelReviews> call = api.fetchKennelReviews(KENNEL_ID);
        call.enqueue(new Callback<KennelReviews>() {
            @Override
            public void onResponse(Call<KennelReviews> call, Response<KennelReviews> response) {
                if (!response.body().getError() && response.body() != null && response.body().getReviews() != null)    {
                    arrReviews = response.body().getReviews();
                    if (arrReviews.size() > 0)  {
                        /* SET THE KENNEL REVIEWS ADAPTER */
                        listKennelReviews.setAdapter(new KennelReviewsAdapter(KennelReviewsActivity.this, arrReviews));

                        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                        linlaProgress.setVisibility(View.GONE);

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listKennelReviews.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listKennelReviews.setVisibility(View.GONE);

                        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listKennelReviews.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<KennelReviews> call, Throwable t) {
//                Log.e("REVIEWS FAILURE", t.getMessage());
            }
        });
    }

    /* GET THE INCOMING DATA */
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("KENNEL_ID")
                && bundle.containsKey("KENNEL_NAME")) {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_NAME = bundle.getString("KENNEL_NAME");
            if (KENNEL_ID != null && KENNEL_NAME != null)  {
                /* SET THE KENNEL'S NAME */
                txtKennelName.setText(KENNEL_NAME);

                /* SHOW THE PROGRESS AND FETCH THE KENNEL'S REVIEWS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchKennelReviews();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
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
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(KennelReviewsActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setAutoMeasureEnabled(true);
        listKennelReviews.setLayoutManager(manager);
        listKennelReviews.setHasFixedSize(true);
        listKennelReviews.setNestedScrollingEnabled(true);

        /* SET THE KENNEL REVIEWS ADAPTER */
        listKennelReviews.setAdapter(new KennelReviewsAdapter(KennelReviewsActivity.this, arrReviews));
    }
}