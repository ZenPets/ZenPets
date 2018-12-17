package co.zenpets.users.creator.kennel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.users.R;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.kennels.bookings.BookingsAPI;
import co.zenpets.users.utils.models.kennels.bookings.KennelAvailability;
import co.zenpets.users.utils.models.kennels.inventory.KennelInventory;
import co.zenpets.users.utils.models.kennels.inventory.KennelInventoryAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewKennelSlotSelector extends AppCompatActivity {

    /** THE INCOMING KENNEL ID AND NAME **/
    String KENNEL_ID = null;
    String KENNEL_NAME = null;

    /** THE SELECTED PET SIZE **/
    String PET_SIZE = "3";

    /** THE TOTAL NUMBER OF NIGHTS AND SELECTED DATES (FROM AND TO) **/
    int TOTAL_DAYS = 0;
    String DATE_FROM = null;
    String DATE_TO = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtKennelName) TextView txtKennelName;
    @BindView(R.id.txtSelectedDates) TextView txtSelectedDates;
    @BindView(R.id.groupSize) RadioGroup groupSize;
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.layoutUnitSelection) ConstraintLayout layoutUnitSelection;
    @BindView(R.id.txtPricePerNight) TextView txtPricePerNight;
    @BindView(R.id.txtTotalPrice) TextView txtTotalPrice;

    /** SELECT THE CHECK-IN AND CHECK-OUT DATES **/
    @OnClick(R.id.linlaDate) void selectDates() {
        Intent intent = new Intent(NewKennelSlotSelector.this, NewKennelDateSelector.class);
        startActivityForResult(intent, 101);
    }

    /** SEARCH FOR AVAILABLE KENNEL UNITS **/
    @OnClick(R.id.btnSearchUnits) void searchUnits()    {
        if (DATE_FROM != null && DATE_TO != null)   {
            /* HIDE THE UNIT SELECTION LAYOUT, SHOW THE PROGRESS AND FETCH AVAILABLE UNITS */
            layoutUnitSelection.setVisibility(View.GONE);
            linlaProgress.setVisibility(View.VISIBLE);
            fetchAvailableUnits();
        }
    }

    /** PAY THE CHARGES AND BOOK THE KENNEL **/
    @OnClick(R.id.btnBookNow) void bookKennel() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_booking_slot_selector_new);
        ButterKnife.bind(this);

        /* HIDE THE UNIT SELECTION LAYOUT */
        layoutUnitSelection.setVisibility(View.GONE);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* SELECT THE PET'S SIZE */
        groupSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btnSmall:
                        /* SET THE PET SIZE TO SMALL */
                        PET_SIZE = "3";
                        break;
                    case R.id.btnMedium:
                        /* SET THE PET SIZE TO MEDIUM */
                        PET_SIZE = "2";
                        break;
                    case R.id.btnLarge:
                        /* SET THE PET SIZE TO LARGE */
                        PET_SIZE = "1";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /** SHOW THE INVENTORY PROGRESS AND FETCH AVAILABLE UNITS **/
    private void fetchAvailableUnits()  {
        BookingsAPI api = ZenApiClient.getClient().create(BookingsAPI.class);
        Call<KennelAvailability> call = api.checkInventoryAvailability(DATE_FROM, DATE_TO, KENNEL_ID, PET_SIZE);
        call.enqueue(new Callback<KennelAvailability>() {
            @Override
            public void onResponse(Call<KennelAvailability> call, Response<KennelAvailability> response) {
//                Log.e("RAW RESPONSE", String.valueOf(response.raw()));
                KennelAvailability availability = response.body();
                if (!availability.getError())   {
                    /* GET THE FIRST AVAILABLE UNIT ID */
                    String kennelInventoryID = availability.getKennelInventoryID();
                    if (kennelInventoryID != null) {
                        Log.e("AVAILABLE ID", kennelInventoryID);

                        /* FETCH THE PER NIGHT INVENTORY COST */
                        fetchUnitPricing(kennelInventoryID);
                    }
                } else {
                    /* SHOW THE NO UNITS AVAILABLE DIALOG */
                    new MaterialDialog.Builder(NewKennelSlotSelector.this)
                            .icon(ContextCompat.getDrawable(NewKennelSlotSelector.this, R.drawable.ic_info_outline_black_24dp))
                            .title(getString(R.string.kbs_unavailable_title))
                            .cancelable(true)
                            .content(getString(R.string.kbs_unavailable_content))
                            .positiveText(getString(R.string.kbs_unavailable_positive))
                            .theme(Theme.LIGHT)
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();

                    /* HIDE THE UNITS SELECTION LAYOUT */
                    layoutUnitSelection.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<KennelAvailability> call, Throwable t) {
                Crashlytics.logException(t);
//                Log.e("FAILURE", t.getMessage());
            }
        });
    }

    /** FETCH THE PER NIGHT INVENTORY COST **/
    private void fetchUnitPricing(String kennelInventoryID) {
        KennelInventoryAPI api = ZenApiClient.getClient().create(KennelInventoryAPI.class);
        Call<KennelInventory> call = api.fetchInventoryDetails(kennelInventoryID);
        call.enqueue(new Callback<KennelInventory>() {
            @Override
            public void onResponse(Call<KennelInventory> call, Response<KennelInventory> response) {
//                Log.e("RAW RESPONSE", String.valueOf(response.raw()));
                KennelInventory inventory = response.body();
                if (inventory != null)  {
                    /* GET THE INVENTORY COST */
                    String kennelInventoryCost = inventory.getKennelInventoryCost();
                    int PER_NIGHT_COST = Integer.parseInt(kennelInventoryCost);
                    Double TOTAL_COST = (double) (PER_NIGHT_COST * TOTAL_DAYS);

                    /* SET THE PER NIGHT PRICE*/
                    txtPricePerNight.setText(kennelInventoryCost);

                    /* SET THE TOTAL PRICE */
                    txtTotalPrice.setText(String.valueOf(TOTAL_COST));

                    /* SHOW THE UNITS SELECTION LAYOUT */
                    layoutUnitSelection.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to get required data",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<KennelInventory> call, Throwable t) {
                Crashlytics.logException(t);
//                Log.e("FAILURE", t.getMessage());
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("KENNEL_ID") && bundle.containsKey("KENNEL_NAME")) {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            KENNEL_NAME = bundle.getString("KENNEL_NAME");
            if (KENNEL_ID != null && KENNEL_NAME != null)   {
                txtKennelName.setText(KENNEL_NAME);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Failed to get required data",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to get required data",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK)  {
            Bundle bundle = data.getExtras();
            if (bundle != null &&
                    bundle.containsKey("DATE_FROM") &&
                    bundle.containsKey("DATE_TO") &&
                    bundle.containsKey("TOTAL_DAYS")) {

                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat output = new SimpleDateFormat("dd MMM", Locale.getDefault());
                DATE_FROM = bundle.getString("DATE_FROM");
                DATE_TO = bundle.getString("DATE_TO");
                TOTAL_DAYS = bundle.getInt("TOTAL_DAYS");
                try {
                    Date FINAL_DATE_FROM = input.parse(DATE_FROM);
                    Date FINAL_DATE_TO = input.parse(DATE_TO);
                    String strFinalFrom = output.format(FINAL_DATE_FROM);
                    String strFinalTo = output.format(FINAL_DATE_TO);
                    txtSelectedDates.setText(strFinalFrom + " - " + strFinalTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}