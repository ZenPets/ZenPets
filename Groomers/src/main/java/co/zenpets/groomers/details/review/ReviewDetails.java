package co.zenpets.groomers.details.review;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.zenpets.groomers.R;
import butterknife.ButterKnife;

public class ReviewDetails extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_details);
        ButterKnife.bind(this);
    }
}