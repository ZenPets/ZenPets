package co.zenpets.kennels.addons;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.kennels.R;
import co.zenpets.kennels.creator.addon.AddonCreator;

public class KennelAddons extends AppCompatActivity {

    /** ADD A NEW ADD-ON (FAB) **/
    @OnClick(R.id.fabNewAddon) void fabNewAddon() {
        Intent intent = new Intent(KennelAddons.this, AddonCreator.class);
        startActivityForResult(intent, 101);
    }

    /** ADD A NEW ADD-ON (EMPTY LAYOUT) **/
    @OnClick(R.id.linlaEmpty) void newAddon()  {
        Intent intent = new Intent(KennelAddons.this, AddonCreator.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_addons_list);
        ButterKnife.bind(this);
    }
}