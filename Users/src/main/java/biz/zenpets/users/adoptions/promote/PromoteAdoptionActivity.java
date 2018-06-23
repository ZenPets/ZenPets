package biz.zenpets.users.adoptions.promote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import biz.zenpets.users.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PromoteAdoptionActivity extends AppCompatActivity implements PaymentResultListener {

    /** THE INCOMING ADOPTION ID **/
    String ADOPTION_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwAdoptionCover) SimpleDraweeView imgvwAdoptionCover;
    @BindView(R.id.txtAdoptionName) TextView txtAdoptionName;
    @BindView(R.id.txtAdoptionDetails) TextView txtAdoptionDetails;
    @BindView(R.id.groupOptions) RadioGroup groupOptions;

    /** THE STRINGS TO HOLD THE SELECTED OPTIONS **/
    String PROMOTION_OPTION_ID = null;

    /** CONFIRM THE ADOPTION'S PROMOTION **/
    @OnClick(R.id.btnConfirmPromotion) void confirmPromotion()  {
        startPayment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_promote_activity);
        ButterKnife.bind(this);

        /* PRELOAD A CHECK OUT */
        Checkout.preload(PromoteAdoptionActivity.this);

        /* GET THE INCOMING DATA */
        getIncomingData();

        /* SELECT THE ADOPTION PROMOTION OPTION */
        groupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbtn7Days:
                        PROMOTION_OPTION_ID = "1";
                        break;
                    case R.id.rdbtn15Days:
                        PROMOTION_OPTION_ID = "2";
                        break;
                    case R.id.rdbtn30Days:
                        PROMOTION_OPTION_ID = "3";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /** FETCH THE ADOPTION DETAILS **/
    private void fetchAdoptionDetails() {
    }

    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("ADOPTION_ID"))    {
            ADOPTION_ID = bundle.getString("ADOPTION_ID");
            if (ADOPTION_ID != null)    {
                /* FETCH THE ADOPTION DETAILS */
                fetchAdoptionDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** CONFIRM THE ADOPTION'S PROMOTION **/
    private void startPayment() {
        final Activity activity = this;
        final Checkout checkout = new Checkout();
        int icon = R.mipmap.ic_launcher;
        checkout.setImage(icon);
        checkout.setFullScreenDisable(true);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Zen Pets");
            options.put("description", "Adoption Promotion");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", "100");
            JSONObject preFill = new JSONObject();
            preFill.put("email", "siddharth.lele@gmail.com");
            preFill.put("contact", "8087471157");
            options.put("prefill", preFill);
            Log.e("JSON", String.valueOf(options));

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("PAYMENT FAILURE", e.getMessage());
            Toast.makeText(getApplicationContext(), "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onPaymentSuccess(String razorPaymentID) {
        try {
            Log.e("PAYMENT ID", "The Payment was successfully completed with the ID: " + razorPaymentID);
            Toast.makeText(this, "Payment Successful: " + razorPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PAYMENT SUCCESS EXCEPTION", e.getMessage());
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PAYMENT ERROR EXCEPTION", e.getMessage());
        }
    }
}