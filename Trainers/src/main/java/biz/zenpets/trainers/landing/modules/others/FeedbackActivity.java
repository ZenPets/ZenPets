package biz.zenpets.trainers.landing.modules.others;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import biz.zenpets.trainers.R;
import butterknife.ButterKnife;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        ButterKnife.bind(this);
    }
}