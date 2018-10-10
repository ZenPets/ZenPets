package co.zenpets.users.details.pets.vaccinations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.zenpets.users.R;
import butterknife.ButterKnife;

public class AllVaccinationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_vaccinations_list);
        ButterKnife.bind(this);
    }
}