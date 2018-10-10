package co.zenpets.kennels.details.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import co.zenpets.kennels.R;
import butterknife.ButterKnife;

public class ReviewDetails extends AppCompatActivity {

    /** THE INCOMING REVIEW ID **/
    String REVIEW_ID = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_details);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /** FETCH THE REVIEW DETAILS **/
    private void fetchReviewDetails() {
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("REVIEW_ID"))  {
            REVIEW_ID = bundle.getString("REVIEW_ID");
            if (REVIEW_ID != null)  {
                /* SHOW THE PROGRESS AND FETCH THE REVIEW DETAILS */
                fetchReviewDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}