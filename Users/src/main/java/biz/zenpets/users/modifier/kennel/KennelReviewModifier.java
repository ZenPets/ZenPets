package biz.zenpets.users.modifier.kennel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import biz.zenpets.users.R;
import butterknife.ButterKnife;

public class KennelReviewModifier extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_review_modifier);
        ButterKnife.bind(this);
    }
}