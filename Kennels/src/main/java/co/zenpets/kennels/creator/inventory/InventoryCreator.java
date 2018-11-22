package co.zenpets.kennels.creator.inventory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.kennels.R;
import co.zenpets.kennels.credentials.SignUpActivity;
import co.zenpets.kennels.utils.AppPrefs;
import co.zenpets.kennels.utils.TypefaceSpan;
import co.zenpets.kennels.utils.adapters.inventory.InventoryTypesAdapter;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.inventory.InventoriesAPI;
import co.zenpets.kennels.utils.models.inventory.Inventory;
import co.zenpets.kennels.utils.models.inventory.Type;
import co.zenpets.kennels.utils.models.inventory.Types;
import co.zenpets.kennels.utils.models.inventory.TypesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCreator extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN KENNEL'S ID **/
    private String KENNEL_ID = null;

    /** THE INVENTORY ITEM DETAILS **/
    String INVENTORY_TYPE_ID = null;
    String KENNEL_INVENTORY_NAME = null;
    String KENNEL_INVENTORY_COST = null;
    String KENNEL_INVENTORY_TAX = null;
    String KENNEL_INVENTORY_STATUS = "Available";

    /** AN INVENTORY TYPES ARRAY LIST **/
    ArrayList<Type> arrTypes = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.spnInventoryType) Spinner spnInventoryType;
    @BindView(R.id.inputInventoryName) TextInputLayout inputInventoryName;
    @BindView(R.id.edtInventoryName) TextInputEditText edtInventoryName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_creator_new);
        ButterKnife.bind(this);

        /* GET THE KENNEL ID */
        KENNEL_ID = getApp().getKennelID();
//        Log.e("KENNEL ID", KENNEL_ID);
        if (KENNEL_ID != null)  {
            /* FETCH THE LIST OF INVENTORY TYPES */
            fetchInventoryTypes();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to get required info...",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* SELECT THE INVENTORY TYPE */
        spnInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                INVENTORY_TYPE_ID = arrTypes.get(position).getInventoryTypeID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /** CHECK INVENTORY DETAILS **/
    private void checkInventoryDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtInventoryName.getWindowToken(), 0);
        }

        /* COLLECT THE INVENTORY ITEM DETAILS */
        KENNEL_INVENTORY_NAME = edtInventoryName.getText().toString().trim();

        if (TextUtils.isEmpty(KENNEL_INVENTORY_NAME)) {
            inputInventoryName.setError("Provide a unique name for the inventory item");
        } else {
            /* SHOW THE PROGRESS DIALOG **/
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Checking for unique inventory name...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();

            /* CHECK FOR UNIQUE NAME */
            checkUniqueName();
        }
    }

    /** CHECK FOR UNIQUE NAME **/
    private void checkUniqueName() {
        InventoriesAPI api = ZenApiClient.getClient().create(InventoriesAPI.class);
        Call<Inventory> call = api.checkUniqueKennelInventory(KENNEL_ID, KENNEL_INVENTORY_NAME, INVENTORY_TYPE_ID);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(Call<Inventory> call, Response<Inventory> response) {
//                Log.e("UNIQUE RESPONSE", String.valueOf(response.raw()));

                /* DISMISS THE PROGRESS DIALOG */
                progressDialog.dismiss();

                Inventory inventory = response.body();
                if (!inventory.getError())  {
                    /* SHOW THE PROGRESS DIALOG **/
                    progressDialog = new ProgressDialog(InventoryCreator.this);
                    progressDialog.setMessage("Adding the new inventory item...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    /* ADD THE NEW INVENTORY ITEM TO THE KENNEL'S ACCOUNT */
                    addInventoryItem();
                } else {
                    new MaterialDialog.Builder(InventoryCreator.this)
                            .icon(ContextCompat.getDrawable(InventoryCreator.this, R.drawable.ic_info_black_24dp))
                            .title("Name Exists!")
                            .cancelable(true)
                            .content("An Inventory Item of the same name and type already exists in your inventory. Please choose a unique name for your new Inventory item. Unique names help managing your Kennel's inventory better.")
                            .positiveText("Got It")
                            .theme(Theme.LIGHT)
                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent signUp = new Intent(InventoryCreator.this, SignUpActivity.class);
                                    startActivity(signUp);
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<Inventory> call, Throwable t) {
//                Log.e("UNIQUE FAILURE", t.getMessage());
            }
        });
    }

    /** ADD THE NEW INVENTORY ITEM TO THE KENNEL'S ACCOUNT **/
    private void addInventoryItem() {
        InventoriesAPI api = ZenApiClient.getClient().create(InventoriesAPI.class);
        Call<Inventory> call = api.createKennelInventoryRecord(
                INVENTORY_TYPE_ID, KENNEL_ID, KENNEL_INVENTORY_NAME,
                KENNEL_INVENTORY_COST, KENNEL_INVENTORY_TAX, KENNEL_INVENTORY_STATUS);
        call.enqueue(new Callback<Inventory>() {
            @Override
            public void onResponse(Call<Inventory> call, Response<Inventory> response) {
                Inventory inventory = response.body();
                if (!inventory.getError())  {
                    String kennelInventoryID = response.body().getKennelInventoryID();
                    Log.e("INVENTORY ID", kennelInventoryID);

                    progressDialog.dismiss();
                    Intent success = new Intent();
                    setResult(RESULT_OK, success);
                    Toast.makeText(
                            getApplicationContext(),
                            "New inventory item published successfully...",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to publish new inventory item...",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Inventory> call, Throwable t) {

            }
        });
    }

    /** FETCH THE LIST OF INVENTORY TYPES **/
    private void fetchInventoryTypes() {
        TypesAPI api = ZenApiClient.getClient().create(TypesAPI.class);
        Call<Types> call = api.fetchInventoryTypes();
        call.enqueue(new Callback<Types>() {
            @Override
            public void onResponse(Call<Types> call, Response<Types> response) {
                if (response.body() != null && response.body().getTypes() != null) {
                    arrTypes = response.body().getTypes();
                    if (arrTypes.size() > 0) {
                        spnInventoryType.setAdapter(new InventoryTypesAdapter(
                                InventoryCreator.this,
                                R.layout.inventory_types_row,
                                arrTypes));
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to get required info...",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Types> call, Throwable t) {
//                Log.e("TYPES FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Inventory Item";
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
        MenuInflater inflater = new MenuInflater(InventoryCreator.this);
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
                /* CHECK INVENTORY DETAILS */
                checkInventoryDetails();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }
}