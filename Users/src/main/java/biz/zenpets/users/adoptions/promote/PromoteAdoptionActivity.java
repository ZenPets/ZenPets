package biz.zenpets.users.adoptions.promote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;
import biz.zenpets.users.utils.models.adoptions.promotion.PromotionAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PromoteAdoptionActivity extends AppCompatActivity implements PaymentResultListener {

    /** THE INCOMING ADOPTION ID **/
    private String ADOPTION_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwAdoptionCover) SimpleDraweeView imgvwAdoptionCover;
    @BindView(R.id.txtAdoptionName) TextView txtAdoptionName;
    @BindView(R.id.txtAdoptionDetails) TextView txtAdoptionDetails;
    @BindView(R.id.groupOptions) RadioGroup groupOptions;

    /** THE STRINGS TO HOLD THE SELECTED OPTIONS **/
    private String PROMOTION_OPTION_ID = "1";
    private String PROMOTION_DAYS = "7";
    private String PROMOTION_CHARGES = "70";
    private String PROMOTION_FROM = null;
    private String PROMOTION_TO = null;

    /** CONFIRM THE ADOPTION'S PROMOTION **/
    @OnClick(R.id.btnConfirmPromotion) void confirmPromotion()  {
        startPayment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_promote_activity);
        ButterKnife.bind(this);

        /* CONFIGURE THE TOOLBAR */
        configTB();

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
                        PROMOTION_DAYS = "7";
                        PROMOTION_CHARGES = "70";
                        break;
                    case R.id.rdbtn15Days:
                        PROMOTION_OPTION_ID = "2";
                        PROMOTION_DAYS = "15";
                        PROMOTION_CHARGES = "140";
                        break;
                    case R.id.rdbtn30Days:
                        PROMOTION_OPTION_ID = "3";
                        PROMOTION_DAYS = "30";
                        PROMOTION_CHARGES = "250";
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
            options.put("amount", Integer.parseInt(PROMOTION_CHARGES) * 100);
            JSONObject preFill = new JSONObject();
            preFill.put("email", "siddharth.lele@gmail.com");
            preFill.put("contact", "8087471157");
            options.put("prefill", preFill);
//            Log.e("JSON", String.valueOf(options));

            checkout.open(activity, options);
        } catch (Exception e) {
//            Log.e("PAYMENT FAILURE", e.getMessage());
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
//            Log.e("PAYMENT ID", "The Payment was successfully completed with the ID: " + razorPaymentID);
            Toast.makeText(this, "Payment Successful: " + razorPaymentID, Toast.LENGTH_SHORT).show();

            /* TESTING THE PAYMENT CAPTURE API */
            testCapture(razorPaymentID);
        } catch (Exception e) {
//            Log.e("PAYMENT SUCCESS EXCEPTION", e.getMessage());
        }
    }

    /** TESTING THE PAYMENT CAPTURE API **/
    private void testCapture(final String razorPaymentID) {
        String apiKey = getString(R.string.razor_pay_api_key_id);
        String apiSecret = getString(R.string.razor_pay_api_key_secret);
        String strCredentials = Credentials.basic(apiKey, apiSecret);
        String strUrl = "https://" + apiKey + ":" + apiSecret + "@api.razorpay.com/v1/payments/" + razorPaymentID + "/capture";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("amount", String.valueOf(Integer.parseInt(PROMOTION_CHARGES) * 100))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", strCredentials)
                .url(strUrl)
                .post(body)
                .build();
//        Log.e("REQUEST", String.valueOf(request));
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.e("FAILURE", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                /* CHECK IF THE PAYMENT WAS CAPTURED SUCCESSFULLY */
                checkCaptureStatus(razorPaymentID);
//                Log.e("RESPONSE", String.valueOf(response));
            }
        });
    }

    /** CHECK IF THE PAYMENT WAS CAPTURED SUCCESSFULLY **/
    private void checkCaptureStatus(final String razorPaymentID) {
        String apiKey = getString(R.string.razor_pay_api_key_id);
        String apiSecret = getString(R.string.razor_pay_api_key_secret);
        String strCredentials = Credentials.basic(apiKey, apiSecret);
        String URL_CHECK_CAPTURE = "https://" + apiKey + ":" + apiSecret + "@api.razorpay.com/v1/payments/" + razorPaymentID;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", strCredentials)
                .url(URL_CHECK_CAPTURE)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String strResult = response.body().string();
                    JSONObject JORoot = new JSONObject(strResult);
//                    Log.e("ROOT", String.valueOf(JORoot));
                    if (JORoot.has("error_code") && JORoot.getString("error_code").equalsIgnoreCase("null")) {
                        /* CHECK THE CAPTURED STATUS */
                        if (JORoot.has("captured")) {
                            String CAPTURED_STATUS = JORoot.getString("captured");
                            if (CAPTURED_STATUS.equalsIgnoreCase("true"))   {

                                /* CALCULATE THE START DATE */
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date date = new Date();
                                PROMOTION_FROM = format.format(date);
//                                Log.e("START DATE", PROMOTION_FROM);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(format.parse(PROMOTION_FROM));

                                /* CALCULATE THE END DATE */
                                calendar.add(Calendar.DATE, Integer.parseInt(PROMOTION_DAYS));
                                Date dateEnd = new Date(calendar.getTimeInMillis());
                                PROMOTION_TO = format.format(dateEnd);
//                                Log.e("END DATE", PROMOTION_TO);

                                /* CREATE THE ADOPTION PROMOTION RECORD */
                                createPromotionRecord(razorPaymentID, PROMOTION_FROM, PROMOTION_TO);
                            } else {
//                                Log.e("CAPTURE FAILED", "The payment was not captured successfully...");
//                                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            Log.e("CAPTURE FAILED", "The payment was not captured successfully...");
//                                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /** CREATE THE ADOPTION PROMOTION RECORD **/
    private void createPromotionRecord(String razorPaymentID, String promotionFrom, String promotionTo) {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        PromotionAPI api = ZenApiClient.getClient().create(PromotionAPI.class);
        retrofit2.Call<Promotion> call = api.publishAdoptionPromotion(
                ADOPTION_ID, PROMOTION_OPTION_ID, razorPaymentID,
                promotionFrom, promotionTo, timeStamp);
        call.enqueue(new retrofit2.Callback<Promotion>() {
            @Override
            public void onResponse(retrofit2.Call<Promotion> call, retrofit2.Response<Promotion> response) {
                Promotion promotion = response.body();
                if (promotion != null)  {
//                    Log.e("PROMOTION ID", promotion.getPromotedID());
                } else {
//                    Log.e("FAILED", "Failed to create the Promotion record...");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Promotion> call, Throwable t) {
//                Log.e("PUBLISH FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            Log.e("PAYMENT ERROR EXCEPTION", e.getMessage());
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Promote Adoption";
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(strTitle);
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