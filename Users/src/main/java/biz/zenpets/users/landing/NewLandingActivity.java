package biz.zenpets.users.landing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import biz.zenpets.users.R;
import butterknife.BindView;

public class NewLandingActivity extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing_activity);
    }
}