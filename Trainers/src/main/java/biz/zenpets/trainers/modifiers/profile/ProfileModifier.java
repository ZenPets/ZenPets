package biz.zenpets.trainers.modifiers.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import biz.zenpets.trainers.R;
import butterknife.ButterKnife;

public class ProfileModifier extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modifier);
        ButterKnife.bind(this);
    }
}