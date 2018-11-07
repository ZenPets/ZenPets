package co.zenpets.doctors.modifier.doctor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.zenpets.doctors.R;
import butterknife.ButterKnife;

public class DisplayProfileModifier extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_display_profile_modifier);
        ButterKnife.bind(this);
    }
}