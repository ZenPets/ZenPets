package biz.zenpets.users.details.kennels.reviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import biz.zenpets.users.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class KennelReviewsActivity extends AppCompatActivity {

    /** THE INCOMING KENNEL ID AND NAME **/
    String KENNEL_ID = null;
    String KENNEL_NAME = null;

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

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE TOOLBAR */
        configTB();
    }

    /** FETCH THE KENNEL'S REVIEWS **/
    private void fetchKennelReviews() {
    }

    /* GET THE INCOMING DATA */
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null
                && bundle.containsKey("DOCTOR_ID")
                && bundle.containsKey("DOCTOR_NAME")) {
            KENNEL_ID = bundle.getString("DOCTOR_ID");
            KENNEL_NAME = bundle.getString("DOCTOR_NAME");
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
}