package co.zenpets.users.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import butterknife.ButterKnife;

public class NewLandingActivity extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_landing_activity);
        ButterKnife.bind(this);
    }
}