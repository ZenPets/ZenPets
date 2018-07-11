package biz.zenpets.kennels.creator.additional;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import biz.zenpets.kennels.R;
import butterknife.ButterKnife;

public class AdditionalKennelPayment extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_kennel_payment);
        ButterKnife.bind(this);
    }
}