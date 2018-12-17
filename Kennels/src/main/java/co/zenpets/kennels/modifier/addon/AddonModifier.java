package co.zenpets.kennels.modifier.addon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import co.zenpets.kennels.R;

public class AddonModifier extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addon_modifier);
        ButterKnife.bind(this);
    }
}