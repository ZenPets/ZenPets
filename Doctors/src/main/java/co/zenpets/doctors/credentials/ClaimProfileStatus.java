package co.zenpets.doctors.credentials;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.claims.ClaimStatus;
import co.zenpets.doctors.utils.models.doctors.claims.DoctorClaimsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimProfileStatus extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE CLAIM DATA **/
    private String CLAIM_ID = null;
    private String CLAIM_STATUS = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtDoctorAddress) AppCompatTextView txtDoctorAddress;
    @BindView(R.id.txtClaimID) AppCompatTextView txtClaimID;
    @BindView(R.id.txtClaimStatus) AppCompatTextView txtClaimStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_profile_status);
        ButterKnife.bind(this);

        /* GET THE CLAIM DATA */
        CLAIM_ID = getApp().getClaimID();
        CLAIM_STATUS = getApp().getClaimStatus();

        if (CLAIM_ID != null)   {
            txtClaimID.setText("Your Claim ID is: \"" + CLAIM_ID + "\"");
        }

        if (CLAIM_STATUS != null)   {
            txtClaimStatus.setText("The status of your claim is: \"" + CLAIM_STATUS + "\"");
        }

        /* GET THE CURRENT CLAIM STATUS */
        getClaimStatus();
    }

    /***** GET THE CURRENT CLAIM STATUS *****/
    private void getClaimStatus() {
        DoctorClaimsAPI api = ZenApiClient.getClient().create(DoctorClaimsAPI.class);
        Call<ClaimStatus> call = api.fetchClaimDetails(CLAIM_ID);
        call.enqueue(new Callback<ClaimStatus>() {
            @Override
            public void onResponse(Call<ClaimStatus> call, Response<ClaimStatus> response) {
                final ClaimStatus status = response.body();
                if (status != null) {
                    /* GET THE CLAIM STATUS */
                    String doctorClaimStatus = status.getDoctorClaimStatus();

                    /* CHECK IF THE CLAIM HAS BEEN APPROVED */
                    if (doctorClaimStatus.equalsIgnoreCase("approved"))  {
                        new MaterialDialog.Builder(ClaimProfileStatus.this)
                                .icon(ContextCompat.getDrawable(ClaimProfileStatus.this, R.drawable.ic_info_outline_black_24dp))
                                .title("One More Thing")
                                .cancelable(true)
                                .content("Your Claim has been approved. To start using you Zen Pets account, we (and the Pet Parents) need to know a little more about you. Click the \"Complete Profile\" button to, complete your Doctor Profile")
                                .positiveText("Complete Profile")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        getApp().setClaimStatus(null);
                                        Intent showLogin = new Intent(ClaimProfileStatus.this, ClaimSignUpActivity.class);
                                        showLogin.putExtra("DOCTOR_ID", status.getDoctorID());
                                        showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(showLogin);
                                        finish();
                                    }
                                }).show();
                    } else {
                        txtClaimStatus.setText("The status of your claim is: \"" + CLAIM_STATUS + "\"");
                    }


                    /* GET THE DOCTOR'S PREFIX AND NAME */
                    String doctorPrefix = status.getDoctorPrefix();
                    String doctorName = status.getDoctorName();
                    txtDoctorName.setText(getString(R.string.doctor_profile_name_placeholder, doctorPrefix, doctorName));

                    /* GET AND SET THE DOCTOR'S ADDRESS */
                    String doctorAddress = status.getDoctorAddress();
                    if (doctorAddress != null)  {
                        txtDoctorAddress.setText(doctorAddress);
                    }
                }
            }

            @Override
            public void onFailure(Call<ClaimStatus> call, Throwable t) {
//                Log.e("CLAIM FAILURE", t.getMessage());
//                Crashlytics.logException(t);
            }
        });
    }
}