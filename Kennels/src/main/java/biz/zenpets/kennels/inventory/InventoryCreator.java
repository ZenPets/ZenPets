package biz.zenpets.kennels.inventory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import biz.zenpets.kennels.R;
import butterknife.ButterKnife;

public class InventoryCreator extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_creator);
        ButterKnife.bind(this);
    }
}