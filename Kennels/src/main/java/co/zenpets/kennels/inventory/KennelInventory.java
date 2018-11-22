package co.zenpets.kennels.inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.kennels.R;
import co.zenpets.kennels.creator.inventory.InventoryCreator;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.kennels.inventory.InventoryAdapter;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.inventory.Inventories;
import co.zenpets.kennels.utils.models.inventory.InventoriesAPI;
import co.zenpets.kennels.utils.models.inventory.Inventory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelInventory extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL'S ID **/
    private String KENNEL_ID = null;

    /** AN INVENTORY ARRAY LIST INSTANCE **/
    ArrayList<Inventory> arrInventory = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listInventory) RecyclerView listInventory;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW KENNEL (FAB) **/
    @OnClick(R.id.fabNewInventory) void fabNewInventory() {
        Intent intent = new Intent(KennelInventory.this, InventoryCreator.class);
        startActivityForResult(intent, 101);
    }

    /** ADD A NEW KENNEL (EMPTY LAYOUT) **/
    @OnClick(R.id.linlaEmpty) void newKennel()  {
        Intent intent = new Intent(KennelInventory.this, InventoryCreator.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_inventory_list);
        ButterKnife.bind(this);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* GET THE KENNEL ID */
        KENNEL_ID = getApp().getKennelID();
//        Log.e("KENNEL ID", KENNEL_ID);
        if (KENNEL_ID != null)  {
            /* FETCH THE KENNEL'S INVENTORY */
            fetchInventory();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to get required info...",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* CALCULATE THE NUMBER OF DAYS (TESTING) */
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            Date date = new Date();
//            String DATE_FROM = format.format(date);
//            Log.e("DATE FROM", DATE_FROM);
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(format.parse(DATE_FROM));
//
//            /* CALCULATE THE END DATE */
//            calendar.add(Calendar.MONTH, 3);
//            Date dateEnd = new Date(calendar.getTimeInMillis());
//            String DATE_TO = format.format(dateEnd);
//            Log.e("DATE TO", DATE_TO);
//
//            DateTime startDate = new DateTime(date);
//            DateTime endDate = new DateTime(dateEnd);
//
//            int days = Days.daysBetween(startDate, endDate).getDays();
//            Log.e("DAYS", String.valueOf(days));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    /** FETCH THE KENNEL'S INVENTORY **/
    private void fetchInventory() {
        InventoriesAPI api = ZenApiClient.getClient().create(InventoriesAPI.class);
        Call<Inventories> call = api.fetchKennelInventory(KENNEL_ID);
        call.enqueue(new Callback<Inventories>() {
            @Override
            public void onResponse(Call<Inventories> call, Response<Inventories> response) {
                if (response.body() != null && response.body().getInventories() != null) {
                    arrInventory = response.body().getInventories();
                    if (arrInventory.size() > 0) {
                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY VIEW */
                        listInventory.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE REVIEWS ADAPTER TO THE RECYCLER VIEW */
                        listInventory.setAdapter(new InventoryAdapter(KennelInventory.this, arrInventory));
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listInventory.setVisibility(View.GONE);
                    }
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listInventory.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Inventories> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101)   {
            /* CLEAR THE ARRAY LIST */
            arrInventory.clear();

            /* SHOW THE PROGRESS AND FETCH THE LIST OF INVENTORIES AGAIN */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchInventory();
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE  CONFIGURATION */
        LinearLayoutManager reviews = new LinearLayoutManager(KennelInventory.this);
        reviews.setOrientation(LinearLayoutManager.VERTICAL);
        listInventory.setLayoutManager(reviews);
        listInventory.setHasFixedSize(true);
        listInventory.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER TO THE RECYCLER VIEW */
        listInventory.setAdapter(new InventoryAdapter(KennelInventory.this, arrInventory));

    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Kennel Inventory Items";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }
}