package co.zenpets.users.creator.kennel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.users.R;
import co.zenpets.users.creator.pet.NewPetCreator;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.adapters.kennels.inventory.KennelInventoryAdapter;
import co.zenpets.users.utils.adapters.pet.PetSpinnerAdapter;
import co.zenpets.users.utils.helpers.classes.RangeDayDecorator;
import co.zenpets.users.utils.helpers.classes.ZenApiClient;
import co.zenpets.users.utils.models.kennels.bookings.Unit;
import co.zenpets.users.utils.models.pets.pets.Pet;
import co.zenpets.users.utils.models.pets.pets.Pets;
import co.zenpets.users.utils.models.pets.pets.PetsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelSlotSelector extends AppCompatActivity
        implements OnDateSelectedListener, OnRangeSelectedListener {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE INCOMING KENNEL ID **/
    String KENNEL_ID = null;

    /** THE SELECTED PET SIZE **/
    String PET_SIZE = "3";

    /** THE SELECTED PET'S DETAILS **/
    String PET_ID = null;
    String PET_TYPE_ID = null;
    String BREED_ID = null;

    /** THE CALENDAR MODE **/
    String CALENDAR_MORE = "Single";

    /** THE TOTAL DAYS AND THE SELECTED FROM AND TO DATES **/
    String TOTAL_DAYS = null;
    String DATE_FROM = null;
    String DATE_TO = null;

    /** PROGRESS DIALOG TO CHECK AVAILABLE UNITS FOR BOOKINGS **/
    ProgressDialog dialog;

    /** THE USER PETS ARRAY LIST **/
    private ArrayList<Pet> arrPets = new ArrayList<>();

    /** THE UNITS ARRAY LIST **/
    ArrayList<Unit> arrUnits = new ArrayList<>();

    /** A RANGE DECORATOR INSTANCE **/
    private RangeDayDecorator decorator;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnPets) Spinner spnPets;
    @BindView(R.id.groupSize) RadioGroup groupSize;
    @BindView(R.id.groupMode) RadioGroup groupMode;
    @BindView(R.id.kennelCalendar) MaterialCalendarView kennelCalendar;
    @BindView(R.id.linlaInventoryProgress) LinearLayout linlaInventoryProgress;
    @BindView(R.id.listInventory) RecyclerView listInventory;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_slot_selector_list);
        ButterKnife.bind(this);

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();

        if (USER_ID != null)    {
            /* FETCH THE USER'S PETS */
            fetchPetsList();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to get required data",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        /* INSTANTIATE THE DECORATOR INSTANCE */
        decorator = new RangeDayDecorator(this);

        /* INSTANTIATE AND CONFIGURE THE CALENDAR INSTANCE */
        LocalDate localDate = LocalDate.now();
        kennelCalendar.state().edit().setMinimumDate(localDate.plusDays(1)).commit();
        kennelCalendar.setOnDateChangedListener(this);
        kennelCalendar.setOnRangeSelectedListener(this);
        kennelCalendar.addDecorator(decorator);

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

        /* SELECT THE CALENDAR MODE */
        groupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btnSingle:
                        kennelCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                        CALENDAR_MORE = "Single";
                        break;
                    case R.id.btnMultiple:
                        kennelCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                        CALENDAR_MORE = "Multi";
                        break;
                    default:
                        break;
                }
            }
        });

        /* SELECT A PET FOR BOARDING */
        spnPets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /* GET THE SELECTED PET TYPE ID */
                PET_ID = arrPets.get(position).getPetID();
                PET_TYPE_ID = arrPets.get(position).getPetTypeID();
                BREED_ID = arrPets.get(position).getBreedID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** FETCH THE USER'S LIST OF PETS **/
    private void fetchPetsList() {
        PetsAPI api = ZenApiClient.getClient().create(PetsAPI.class);
        Call<Pets> call = api.fetchUserPets(USER_ID);
        call.enqueue(new Callback<Pets>() {
            @Override
            public void onResponse(Call<Pets> call, Response<Pets> response) {
                if (response.body() != null && response.body().getPets() != null)   {
                    arrPets = response.body().getPets();

                    /* CHECK FOR RESULTS */
                    if (arrPets.size() > 0) {
                        /* INSTANTIATE THE PETS SPINNER ADAPTER */
                        PetSpinnerAdapter petsAdapter = new PetSpinnerAdapter(KennelSlotSelector.this, arrPets);

                        /* SET THE ADAPTER TO THE PETS SPINNER */
                        spnPets.setAdapter(petsAdapter);
                    } else {
                        /* SHOW THE NO PETS FOUND DIALOG */
                        noPetsFound();
                    }
                }

                /* HIDE THE TOP LEVEL PROGRESS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Pets> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("KENNEL_ID"))  {
            KENNEL_ID = bundle.getString("KENNEL_ID");
            if (KENNEL_ID != null)  {
                /* SHOW THE TOP LEVEL PROGRESS AND FETCH THE KENNEL DETAILS */
                linlaProgress.setVisibility(View.VISIBLE);
//                fetchKennelDetails();
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
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (CALENDAR_MORE.equalsIgnoreCase("Single"))   {

            /* SET THE TOTAL DAYS TO "1" */
            TOTAL_DAYS = "1";

            /* SET THE FROM AND TO DATES */
            DATE_FROM = String.valueOf(date.getDate());
            DATE_TO = String.valueOf(date.getDate());
            Log.e("DATE FROM", DATE_FROM);
            Log.e("DATE TO", DATE_TO);

//            /* SHOW THE KENNEL INVENTORY PROGRESS AND CHECK FOR AVAILABLE UNITS */
//            dialog = new ProgressDialog(this);
//            dialog.setMessage("Checking for availability on your selected date...");
//            dialog.setIndeterminate(false);
//            dialog.setCancelable(false);
//            dialog.show();
//            checkAvailableUnits();
        }
    }


    @Override
    public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
        if (CALENDAR_MORE.equalsIgnoreCase("Multi"))    {
            if (dates.size() > 0) {
                /* SET THE TOTAL NUMBER OF DAYS */
                TOTAL_DAYS = String.valueOf(dates.size());
                decorator.addFirstAndLast(dates.get(0), dates.get(dates.size() - 1));
                for (int i = 0; i < dates.size(); i++) {
                    if (i == 0)  {
                        /* SET THE FROM DATE */
                        DATE_FROM = String.valueOf(dates.get(i).getDate());
                        Log.e("DATE FROM", DATE_FROM);
                    }

                    if (i == dates.size() - 1)  {
                        /* SET THE TO DATE */
                        DATE_TO = String.valueOf(dates.get(i).getDate());
                        Log.e("DATE TO", DATE_TO);
                    }
                }

                /* SHOW THE INVENTORY PROGRESS AND FETCH AVAILABLE UNITS */
                linlaInventoryProgress.setVisibility(View.VISIBLE);
//                fetchAvailableUnits();

//                /* SHOW THE KENNEL INVENTORY PROGRESS AND FETCH ALL AVAILABLE UNITS */
//                dialog = new ProgressDialog(this);
//                dialog.setMessage("Checking for availability on your selected dates...");
//                dialog.setIndeterminate(false);
//                dialog.setCancelable(false);
//                dialog.show();
//                checkAvailableUnits();
            }
        }
    }

    /** SHOW THE INVENTORY PROGRESS AND FETCH AVAILABLE UNITS **/
//    private void fetchAvailableUnits() {
//        BookingsAPI api = ZenApiClient.getClient().create(BookingsAPI.class);
//        Call<Units> call = api.checkInventoryAvailability(DATE_FROM, DATE_TO, KENNEL_ID, PET_SIZE);
//        call.enqueue(new Callback<Units>() {
//            @Override
//            public void onResponse(Call<Units> call, Response<Units> response) {
//                if (response.body() != null && response.body().getUnits() != null) {
//                    arrUnits = response.body().getUnits();
//                    if (arrUnits.size() > 0) {
//                        /* SET THE INVENTORY UNITS ADAPTER TO THE RECYCLER VIEW */
//                        listInventory.setAdapter(new KennelInventoryAdapter(KennelSlotSelector.this, arrUnits));
//
//                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY IMAGES VIEW */
//                        linlaEmpty.setVisibility(View.GONE);
//                        listInventory.setVisibility(View.VISIBLE);
//                    } else {
//                        /* SHOW THE NO IMAGES LAYOUT */
//                        linlaEmpty.setVisibility(View.VISIBLE);
//                        listInventory.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* SHOW THE NO IMAGES LAYOUT */
//                    linlaEmpty.setVisibility(View.VISIBLE);
//                    listInventory.setVisibility(View.GONE);
//                }
//
//                /* HIDE THE INVENTORY PROGRESS AFTER FETCHING THE DATA */
//                linlaInventoryProgress.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<Units> call, Throwable t) {
//                Crashlytics.logException(t);
//                Log.e("UNITS FAILURE", t.getMessage());
//            }
//        });
//    }

//    /** CHECK FOR ALL AVAILABLE UNITS **/
//    private void checkAvailableUnits() {
//        BookingsAPI api = ZenApiClient.getClient().create(BookingsAPI.class);
//        Call<KennelAvailability> call = api.checkInventoryAvailability(DATE_FROM, DATE_TO, KENNEL_ID, PET_SIZE);
//        call.enqueue(new Callback<KennelAvailability>() {
//            @Override
//            public void onResponse(Call<KennelAvailability> call, Response<KennelAvailability> response) {
//                Log.e("AVAILABILITY RESPONSE", String.valueOf(response.raw()));
//                KennelAvailability availability = response.body();
//                if (!availability.getError())   {
//                    /* GET THE FIRST AVAILABLE UNIT ID */
//                    String kennelInventoryID = availability.getKennelInventoryID();
//                    if (kennelInventoryID != null)  {
//                        Log.e("AVAILABLE ID", kennelInventoryID);
//
//                        String totalAvailable = availability.getTotalAvailable();
//                        String contentPlaceholder = getResources().getString(R.string.kbs_available_content, totalAvailable);
//
//                        /* SHOW THE AVAILABLE UNITS DIALOG */
//                        new MaterialDialog.Builder(KennelSlotSelector.this)
//                                .icon(ContextCompat.getDrawable(KennelSlotSelector.this, R.drawable.ic_info_outline_black_24dp))
//                                .title(getString(R.string.kbs_available_title))
//                                .cancelable(true)
//                                .content(contentPlaceholder)
//                                .positiveText(getString(R.string.kbs_available_positive))
//                                .negativeText(getString(R.string.kbs_available_negative))
//                                .theme(Theme.LIGHT)
//                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    }
//                                })
//                                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        dialog.dismiss();
//                                    }
//                                }).show();
//
//                        /* DISMISS THE DIALOG */
//                        dialog.dismiss();
//                    }
//                } else {
//
//                    /* SHOW THE NO UNITS AVAILABLE DIALOG */
//                    new MaterialDialog.Builder(KennelSlotSelector.this)
//                            .icon(ContextCompat.getDrawable(KennelSlotSelector.this, R.drawable.ic_info_outline_black_24dp))
//                            .title(getString(R.string.kbs_unavailable_title))
//                            .cancelable(true)
//                            .content(getString(R.string.kbs_unavailable_content))
//                            .positiveText(getString(R.string.kbs_unavailable_positive))
//                            .theme(Theme.LIGHT)
//                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                            .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                @Override
//                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    dialog.dismiss();
//
//                                    /* CLEAR THE CURRENT SELECTION */
//                                    kennelCalendar.clearSelection();
//                                }
//                            }).show();
//
//                    /* DISMISS THE DIALOG */
//                    dialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<KennelAvailability> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
//    }

    /***** SHOW THE NO PETS FOUND DIALOG *****/
    private void noPetsFound() {
        new MaterialDialog.Builder(KennelSlotSelector.this)
                .title("No pets found...")
                .content("You haven't added any Pets to your account yet. Before you can book a stay at a Kennel, you must have the Pet you are boarding at the Kennel added to your account.\n\nTo add a Pet now, click the \"ADD PET\" button. Or, select the \"CANCEL\" button to exit this page...")
                .positiveText("Add Pet")
                .negativeText("Cancel")
                .theme(Theme.LIGHT)
                .icon(ContextCompat.getDrawable(KennelSlotSelector.this, R.drawable.ic_info_outline_black_24dp))
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent addNewPet = new Intent(KennelSlotSelector.this, NewPetCreator.class);
                        startActivityForResult(addNewPet, 101);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager llmClinicImages = new LinearLayoutManager(this);
        llmClinicImages.setOrientation(LinearLayoutManager.HORIZONTAL);
        llmClinicImages.isAutoMeasureEnabled();
        listInventory.setLayoutManager(llmClinicImages);
        listInventory.setHasFixedSize(true);
        listInventory.setNestedScrollingEnabled(false);

        /* SET THE ADAPTER */
        listInventory.setAdapter(new KennelInventoryAdapter(KennelSlotSelector.this, arrUnits));
    }
}