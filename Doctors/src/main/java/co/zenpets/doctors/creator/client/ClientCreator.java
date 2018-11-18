package co.zenpets.doctors.creator.client;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.clients.Client;
import co.zenpets.doctors.utils.models.doctors.clients.DoctorClientsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientCreator extends AppCompatActivity {

    /** THE INCOMING DOCTOR ID **/
    String DOCTOR_ID = null;

    /** THE CLIENT NAME AND PHONE NUMBER **/
    String CLIENT_NAME = null;
    String CLIENT_PHONE_PREFIX = "91";
    String CLIENT_PHONE_NUMBER = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputClientName) TextInputLayout inputClientName;
    @BindView(R.id.edtClientName) TextInputEditText edtClientName;
    @BindView(R.id.inputClientPhone) TextInputLayout inputClientPhone;
    @BindView(R.id.edtClientPhone) TextInputEditText edtClientPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_creator);
        ButterKnife.bind(this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* CONFIGURE THE ACTIONBAR */
        configAB();
    }

    /** CHECK FOR ALL CLIENT DETAILS **/
    private void checkClientDetails() {
        /* HIDE THE KEYBOARD */
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtClientName.getWindowToken(), 0);
        }

        /* COLLECT ALL THE DATA */
        CLIENT_NAME = edtClientName.getText().toString().trim();
        CLIENT_PHONE_NUMBER = edtClientPhone.getText().toString().trim();

        if (TextUtils.isEmpty(CLIENT_NAME)) {
            inputClientName.requestFocus();
            inputClientName.setError("");
            inputClientName.setErrorEnabled(true);
            inputClientPhone.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(CLIENT_PHONE_NUMBER))  {
            inputClientPhone.requestFocus();
            inputClientPhone.setError("");
            inputClientName.setErrorEnabled(false);
            inputClientPhone.setErrorEnabled(true);
        } else {
            /* SAVE THE NEW CLIENT RECORD */
            saveClientRecord();
        }
    }

    /** SAVE THE NEW CLIENT RECORD **/
    private void saveClientRecord() {
        DoctorClientsAPI api = ZenApiClient.getClient().create(DoctorClientsAPI.class);
        Call<Client> call = api.newClient(DOCTOR_ID, CLIENT_NAME, CLIENT_PHONE_PREFIX, CLIENT_PHONE_NUMBER);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful())    {
                    String clientID = response.body().getClientID();
                    Log.e("NEW CLIENT ID", clientID);
                } else {
                    Log.e("RESPONSE FAILURE", "NEW CLIENT CREATION FAILED");
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Log.e("CLIENT FAILURE", t.getMessage());
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "New Client";
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
        MenuInflater inflater = new MenuInflater(ClientCreator.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuSave:
                /* CHECK FOR ALL CLIENT DETAILS */
                checkClientDetails();
                break;
            case R.id.menuCancel:
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("DOCTOR_ID"))  {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID == null)  {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}