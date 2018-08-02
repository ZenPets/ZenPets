package biz.zenpets.kennels.inventory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KennelInventory extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER'S ID **/
    private String KENNEL_OWNER_ID = null;

    /** AN ARRAY LIST TO STORE THE LIST OF KENNELS **/
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    /** A LINEAR LAYOUT MANAGER INSTANCE **/
    LinearLayoutManager manager;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listKennels) RecyclerView listKennels;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW KENNEL (FAB) **/
    @OnClick(R.id.fabNewKennel) void newFabKennel() {
        /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
        checkPublishedKennels();
    }

    /** ADD A NEW KENNEL (EMPTY LAYOUT) **/
    @OnClick(R.id.linlaEmpty) void newKennel()  {
        /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
        checkPublishedKennels();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_inventory_list);
        ButterKnife.bind(this);
    }
}