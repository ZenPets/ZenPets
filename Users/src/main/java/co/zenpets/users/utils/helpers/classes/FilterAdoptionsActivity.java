package co.zenpets.users.utils.helpers.classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import co.zenpets.users.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterAdoptionsActivity extends AppCompatActivity {

    /** DATA TYPES TO HOLD THE USER'S SELECTIONS **/
    private String PET_GENDER = null;
    private String PET_SPECIES = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtClear) AppCompatTextView txtClear;
    @BindView(R.id.chkbxMale) AppCompatCheckBox chkbxMale;
    @BindView(R.id.chkbxFemale) AppCompatCheckBox chkbxFemale;
    @BindView(R.id.groupSpecies) RadioGroup groupSpecies;
    @BindView(R.id.rbtnDog) AppCompatRadioButton rbtnDog;
    @BindView(R.id.rbtnCat) AppCompatRadioButton rbtnCat;
    @BindView(R.id.rbtnBoth) AppCompatRadioButton rbtnBoth;
    @BindView(R.id.btnApplyFilter)AppCompatButton btnApplyFilter;

    /** APPLY THE FILTER AND PASS BACK THE SELECTED PROBLEM ID **/
    @OnClick(R.id.btnApplyFilter) void applyFilter()    {
        Intent intent = new Intent();
        intent.putExtra("PET_GENDER", PET_GENDER);
        intent.putExtra("PET_SPECIES", PET_SPECIES);
        setResult(RESULT_OK, intent);
        finish();
    }

    /** CLEAR THE SELECTION **/
    @OnClick(R.id.txtClear) void clearSelection()   {
            /* REMOVE THE SELECTIONS */
            chkbxMale.setChecked(false);
            chkbxFemale.setChecked(false);
            groupSpecies.clearCheck();

            txtClear.setVisibility(View.GONE);
            btnApplyFilter.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_adoptions_activity);
        ButterKnife.bind(this);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* GET INCOMING DATA */
        getIncomingData();

        /* SELECT THE MALE GENDER */
        chkbxMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txtClear.setVisibility(View.VISIBLE);
                if (b && !chkbxFemale.isChecked())  {
                    PET_GENDER = "Male";
                } else if (b && chkbxFemale.isChecked())  {
                    PET_GENDER = "Both";
                } else if (!b && chkbxFemale.isChecked()) {
                    PET_GENDER = "Female";
                } else if (!b && !chkbxFemale.isChecked())    {
                    PET_GENDER = null;
                }
            }
        });

        /* SELECT THE FEMALE GENDER */
        chkbxFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                txtClear.setVisibility(View.VISIBLE);
                if (b && !chkbxMale.isChecked())  {
                    PET_GENDER = "Female";
                } else if (b && chkbxMale.isChecked())  {
                    PET_GENDER = "Both";
                } else if (!b && chkbxMale.isChecked()) {
                    PET_GENDER = "Male";
                } else if (!b && !chkbxMale.isChecked())    {
                    PET_GENDER = null;
                }
            }
        });

        /* SELECT THE PET'S SPECIES */
        groupSpecies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                /* SHOW THE CLEAR BUTTON */
                txtClear.setVisibility(View.VISIBLE);
                switch (checkedId) {
                    case R.id.rbtnDog:
                        /* SET THE SPECIES TO "DOG" */
                        PET_SPECIES = "Dog";
                        break;
                    case R.id.rbtnCat:
                        /* SET THE SPECIES TO "CAT" */
                        PET_SPECIES = "Cat";
                        break;
                        /* SET THE SPECIES TO "BOTH" */
                    case R.id.rbtnBoth:
                        PET_SPECIES = "Both";
                    default:
                        break;
                }
            }
        });
    }

    /***** GET INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            /* CHECK FOR PET GENDER */
            if (bundle.containsKey("PET_GENDER"))   {
                PET_GENDER = bundle.getString("PET_GENDER");
                if (PET_GENDER != null) {
                    if (PET_GENDER.equalsIgnoreCase("Male"))    {
                        chkbxMale.setChecked(true);
                        chkbxFemale.setChecked(false);
                    } else if (PET_GENDER.equalsIgnoreCase("Female"))   {
                        chkbxFemale.setChecked(true);
                        chkbxMale.setChecked(false);
                    } else if (PET_GENDER.equalsIgnoreCase("Both")) {
                        chkbxFemale.setChecked(true);
                        chkbxMale.setChecked(true);
                    }
                } else {
                    chkbxMale.setChecked(false);
                    chkbxFemale.setChecked(false);
                }
            } else {
                chkbxMale.setChecked(false);
                chkbxFemale.setChecked(false);
            }

            /* CHECK FOR PET SPECIES */
            if (bundle.containsKey("PET_SPECIES"))  {
                PET_SPECIES = bundle.getString("PET_SPECIES");
                if (PET_SPECIES != null)    {
                    if (PET_SPECIES.equalsIgnoreCase("Dog"))    {
                        rbtnDog.setChecked(true);
                        rbtnCat.setChecked(false);
                        rbtnBoth.setChecked(false);
                    } else if (PET_SPECIES.equalsIgnoreCase("Cat")) {
                        rbtnDog.setChecked(false);
                        rbtnCat.setChecked(true);
                        rbtnBoth.setChecked(false);
                    } else if (PET_SPECIES.equalsIgnoreCase("Both"))    {
                        rbtnDog.setChecked(false);
                        rbtnCat.setChecked(false);
                        rbtnBoth.setChecked(true);
                    }
                } else {
                    groupSpecies.clearCheck();
                }
            }
        } else {
        }
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }
}