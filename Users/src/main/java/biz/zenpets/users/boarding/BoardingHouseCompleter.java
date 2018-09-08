package biz.zenpets.users.boarding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.TypefaceSpan;
import biz.zenpets.users.utils.adapters.boardings.UnitTypesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.boarding.house.House;
import biz.zenpets.users.utils.models.boarding.house.HouseAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardingHouseCompleter extends AppCompatActivity {

    /** THE INCOMING BOARDING ID **/
    String BOARDING_ID = null;

    /** UNIT TYPES ARRAY LIST INSTANCE **/
    private List<String> arrUnits = new ArrayList<>();

    /** THE USER HOUSE SELECTIONS **/
    String BOARDING_UNIT_ID = "1";
    String BOARDING_DOGS = "0";
    String BOARDING_CATS = "0";
    String BOARDING_SMOKING = "0";
    String BOARDING_VAPING = "0";

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnUnitType) Spinner spnUnitType;
    @BindView(R.id.chkbxDogs) CheckBox chkbxDogs;
    @BindView(R.id.chkbxCats) CheckBox chkbxCats;
    @BindView(R.id.chkbxSmoking) CheckBox chkbxSmoking;
    @BindView(R.id.chkbxVaping) CheckBox chkbxVaping;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_boarding_house_completer);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* POPULATE THE DURATION NUMBER SPINNER */
        String[] boardingUnitTypes = getResources().getStringArray(R.array.boarding_unit_types);
        arrUnits = Arrays.asList(boardingUnitTypes);
        spnUnitType.setAdapter(new UnitTypesAdapter(
                BoardingHouseCompleter.this,
                R.layout.boarding_units_row,
                arrUnits));

        /* SELECT THE DURATION NUMBER */
        spnUnitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)  {
                    BOARDING_UNIT_ID = "0";
                } else if (position == 1)   {
                    BOARDING_UNIT_ID = "1";
                } else if (position == 2)   {
                    BOARDING_UNIT_ID = "2";
                } else if (position == 3)   {
                    BOARDING_UNIT_ID = "3";
                } else if (position == 4)   {
                    BOARDING_UNIT_ID = "4";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* TOGGLE THE DOG STATUS */
        chkbxDogs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  {
                    BOARDING_DOGS = "1";
                } else {
                    BOARDING_DOGS = "0";
                }
            }
        });

        /* TOGGLE THE CAT STATUS */
        chkbxCats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  {
                    BOARDING_CATS = "1";
                } else {
                    BOARDING_CATS = "0";
                }
            }
        });

        /* TOGGLE THE SMOKING STATUS */
        chkbxSmoking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  {
                    BOARDING_SMOKING = "1";
                } else {
                    BOARDING_SMOKING = "0";
                }
            }
        });

        /* TOGGLE THE VAPING STATUS */
        chkbxVaping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  {
                    BOARDING_VAPING = "1";
                } else {
                    BOARDING_VAPING = "0";
                }
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("BOARDING_ID"))    {
            BOARDING_ID = bundle.getString("BOARDING_ID");
            if (BOARDING_ID == null)    {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Boarding House Options";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(BoardingHouseCompleter.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /* CHECK BOARDING HOUSE DETAILS */
                checkHouseDetails();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** CHECK BOARDING HOUSE DETAILS **/
    private void checkHouseDetails() {
        if (BOARDING_UNIT_ID.equalsIgnoreCase("0")) {
            Toast.makeText(getApplicationContext(), "Please select a valid House Type", Toast.LENGTH_LONG).show();
        } else {
            /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
            dialog = new ProgressDialog(this);
            dialog.setMessage("Adding your house details...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

            /* CREATE THE BOARDING HOUSE DETAILS RECORD */
            createRecord();
        }
    }

    /** CREATE THE BOARDING HOUSE DETAILS RECORD **/
    private void createRecord() {
        HouseAPI api = ZenApiClient.getClient().create(HouseAPI.class);
        Call<House> call = api.addBoardingHouseDetails(
                BOARDING_ID, BOARDING_UNIT_ID, BOARDING_DOGS, BOARDING_CATS, BOARDING_SMOKING, BOARDING_VAPING);
        call.enqueue(new Callback<House>() {
            @Override
            public void onResponse(Call<House> call, Response<House> response) {
                boolean blnError = response.body().getError();
                if (!blnError)  {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Details successfully added...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred. Please try again...", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<House> call, Throwable t) {
//                Log.e("DETAILS FAILURE", t.getMessage());
            }
        });
    }
}