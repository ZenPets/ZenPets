package biz.zenpets.trainers.details.trainers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import biz.zenpets.trainers.R;
import butterknife.ButterKnife;

public class TrainerEnquiryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_enquiry_list);
        ButterKnife.bind(this);
    }
}