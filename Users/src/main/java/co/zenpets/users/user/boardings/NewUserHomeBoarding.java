package co.zenpets.users.user.boardings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.zenpets.users.R;
import butterknife.ButterKnife;

public class NewUserHomeBoarding extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_boarding_new);
        ButterKnife.bind(this);
    }
}