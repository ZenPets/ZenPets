package biz.zenpets.kennels.creator.kennel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import biz.zenpets.kennels.R;
import biz.zenpets.kennels.utils.AppPrefs;
import biz.zenpets.kennels.utils.TypefaceSpan;
import biz.zenpets.kennels.utils.adapters.capacity.PetCapacityAdapter;
import biz.zenpets.kennels.utils.adapters.location.CitiesAdapter;
import biz.zenpets.kennels.utils.adapters.location.StatesAdapter;
import biz.zenpets.kennels.utils.helpers.LocationPickerActivity;
import biz.zenpets.kennels.utils.models.helpers.ZenApiClient;
import biz.zenpets.kennels.utils.models.kennels.Kennel;
import biz.zenpets.kennels.utils.models.kennels.KennelPages;
import biz.zenpets.kennels.utils.models.kennels.KennelsAPI;
import biz.zenpets.kennels.utils.models.location.CitiesData;
import biz.zenpets.kennels.utils.models.location.CityData;
import biz.zenpets.kennels.utils.models.location.LocationAPI;
import biz.zenpets.kennels.utils.models.location.StateData;
import biz.zenpets.kennels.utils.models.location.StatesData;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelCreator extends AppCompatActivity implements PaymentResultListener {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE KENNEL OWNER ID **/
    String KENNEL_OWNER_ID = null;

    /** THE INCOMING LISTING TYPE **/
    String LISTING_TYPE = null;

    /** PERMISSION REQUEST CONSTANT **/
    private static final int ACCESS_STORAGE_CONSTANT = 201;

    /** THE LOCATION REQUEST CODE **/
    private final int REQUEST_LOCATION = 1;

    /** DATA TYPES TO HOLD THE KENNEL DETAILS **/
    String KENNEL_ID = null;
    String KENNEL_CHARGES_ID = "1";
    String KENNEL_NAME = null;
    String KENNEL_COVER_PHOTO = null;
    Uri KENNEL_COVER_PHOTO_URI = null;
    String KENNEL_COVER_PHOTO_FILE_NAME = null;
    String KENNEL_ADDRESS = null;
    String KENNEL_PIN_CODE = null;
    String COUNTRY_ID = "51";
    String STATE_ID = null;
    String CITY_ID = null;
    Double KENNEL_LATITUDE = null;
    Double KENNEL_LONGITUDE = null;
    String KENNEL_PHONE_PREFIX_1 = "91";
    String KENNEL_PHONE_NUMBER_1 = null;
    String KENNEL_PHONE_PREFIX_2 = "91";
    String KENNEL_PHONE_NUMBER_2 = null;
    String KENNEL_PET_CAPACITY = null;
    private String KENNEL_VALID_FROM = null;
    private String KENNEL_VALID_TO = null;

    /** ADDITIONAL KENNEL CHARGES **/
    String ADDITIONAL_KENNEL_COST = "1000";

    /** THE STATES ADAPTER AND ARRAY LIST **/
    private ArrayList<StateData> arrStates = new ArrayList<>();

    /** CITIES ADAPTER AND ARRAY LIST **/
    private ArrayList<CityData> arrCities = new ArrayList<>();

    /** A PET CAPACITY LIST INSTANCE **/
    private List<String> arrCapacity = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.inputKennelName) TextInputLayout inputKennelName;
    @BindView(R.id.edtKennelName) TextInputEditText edtKennelName;
    @BindView(R.id.inputAddress) TextInputLayout inputAddress;
    @BindView(R.id.edtAddress) TextInputEditText edtAddress;
    @BindView(R.id.inputPinCode) TextInputLayout inputPinCode;
    @BindView(R.id.edtPinCode) TextInputEditText edtPinCode;
    @BindView(R.id.spnState) Spinner spnState;
    @BindView(R.id.spnCity) Spinner spnCity;
    @BindView(R.id.txtLocation) TextView txtLocation;
    @BindView(R.id.inputPhoneNumber1) TextInputLayout inputPhoneNumber1;
    @BindView(R.id.edtPhoneNumber1) TextInputEditText edtPhoneNumber1;
    @BindView(R.id.inputPhoneNumber2) TextInputLayout inputPhoneNumber2;
    @BindView(R.id.edtPhoneNumber2) TextInputEditText edtPhoneNumber2;
    @BindView(R.id.spnPetCapacity) Spinner spnPetCapacity;
    @BindView(R.id.imgvwKennelCoverPhoto) SimpleDraweeView imgvwKennelCoverPhoto;

    /** SELECT THE KENNEL'S LOCATION ON THE MAP **/
    @OnClick(R.id.btnLocationPicker) void selectLocation()  {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    /** PICK THE KENNEL'S COVER PHOTO **/
    @OnClick(R.id.imgvwKennelCoverPhoto) void selectCoverPhoto()    {
        /* CHECK STORAGE PERMISSION */
        checkStoragePermission();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kennel_creator);
        ButterKnife.bind(this);

        /* GET THE INCOMING LISTING TYPE */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("LISTING_TYPE"))   {
            LISTING_TYPE = bundle.getString("LISTING_TYPE");
        }

        /* THE EASY IMAGE CONFIGURATION */
        EasyImage.configuration(this)
                .setImagesFolderName("Zen Pets")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);

        /* PRELOAD A CHECK OUT */
        Checkout.preload(KennelCreator.this);

        /* GET THE KENNEL OWNER'S ID */
        KENNEL_OWNER_ID = getApp().getKennelOwnerID();

        /* CONFIGURE THE TOOLBAR */
        configTB();

        /* SHOW THE PROGRESS AND FETCH THE LIST OF STATES */
        fetchStates();

        /* SELECT A STATE */
        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                STATE_ID = arrStates.get(position).getStateID();

                /* CLEAR THE CITIES ARRAY LIST */
                arrCities.clear();

                /* FETCH THE LIST OF CITIES */
                if (STATE_ID != null)   {
                    fetchCities();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* SELECT A CITY */
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CITY_ID = arrCities.get(position).getCityID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* POPULATE THE PET CAPACITY SPINNER */
        String[] strServes = getResources().getStringArray(R.array.pet_capacity);
        arrCapacity = Arrays.asList(strServes);
        spnPetCapacity.setAdapter(new PetCapacityAdapter(
                KennelCreator.this,
                R.layout.pet_capacity_row,
                arrCapacity));

        /* SELECT THE PET CAPACITY */
        spnPetCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KENNEL_PET_CAPACITY = arrCapacity.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* GET THE VALID FROM AND TO DATES */
        try {
            /* CALCULATE THE KENNEL VALID FROM DATE */
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            KENNEL_VALID_FROM = format.format(date);
//            Log.e("VALID FROM", KENNEL_VALID_FROM);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(KENNEL_VALID_FROM));

            /* CALCULATE THE END DATE */
            calendar.add(Calendar.YEAR, 1);
            Date dateEnd = new Date(calendar.getTimeInMillis());
            KENNEL_VALID_TO = format.format(dateEnd);
//            Log.e("VALID TO", KENNEL_VALID_TO);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /** CHECK KENNEL DETAILS **/
    private void checkKennelDetails() {
        /* HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtKennelName.getWindowToken(), 0);
        }

        /* COLLECT THE KENNEL DETAILS */
        KENNEL_NAME = edtKennelName.getText().toString().trim();
        KENNEL_ADDRESS = edtAddress.getText().toString().trim();
        KENNEL_PIN_CODE = edtPinCode.getText().toString().trim();
        KENNEL_PHONE_NUMBER_1 = edtPhoneNumber1.getText().toString().trim();
        KENNEL_PHONE_NUMBER_2 = edtPhoneNumber2.getText().toString().trim();

        /* GENERATE THE FILE NAME */
        if (!(KENNEL_COVER_PHOTO_URI == null) && !TextUtils.isEmpty(KENNEL_NAME))    {
            KENNEL_COVER_PHOTO_FILE_NAME = KENNEL_OWNER_ID + "_" + KENNEL_NAME.replaceAll(" ", "_").toLowerCase().trim();
        } else {
            KENNEL_COVER_PHOTO_FILE_NAME = null;
        }

        /* VERIFY THE KENNEL DETAILS */
        if (TextUtils.isEmpty(KENNEL_NAME)) {
            inputKennelName.setError("Provide the Kennel's name");
            inputKennelName.setErrorEnabled(true);
            inputKennelName.requestFocus();
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_ADDRESS))   {
            inputAddress.setError("Provide the Kennel's address");
            inputAddress.setErrorEnabled(true);
            inputAddress.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PIN_CODE))  {
            inputPinCode.setError("Provide the Pin Code");
            inputPinCode.setErrorEnabled(true);
            inputPinCode.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
        } else if (TextUtils.isEmpty(KENNEL_PHONE_NUMBER_1))    {
            inputPhoneNumber1.setError("Provide the Phone Number");
            inputPhoneNumber1.setErrorEnabled(true);
            inputPhoneNumber1.requestFocus();
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
        } else if (KENNEL_LATITUDE == null || KENNEL_LONGITUDE == null) {
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please mark the Kennel's location on the Map", Toast.LENGTH_LONG).show();
        } else if (KENNEL_COVER_PHOTO_FILE_NAME == null)    {
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);
            Toast.makeText(getApplicationContext(), "Please select the Kennel's Cover Photo.", Toast.LENGTH_LONG).show();
        } else {
            inputKennelName.setErrorEnabled(false);
            inputAddress.setErrorEnabled(false);
            inputPinCode.setErrorEnabled(false);
            inputPhoneNumber1.setErrorEnabled(false);

            /* UPLOAD THE KENNEL'S COVER PHOTO  */
            uploadKennelCover();
        }
    }

    /** UPLOAD THE KENNEL'S COVER PHOTO  **/
    private void uploadKennelCover() {
        /* SHOW THE PROGRESS DIALOG **/
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Publishing the new Kennel...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference refStorage = storageReference.child("Kennel Covers").child(KENNEL_COVER_PHOTO_FILE_NAME);
        refStorage.putFile(KENNEL_COVER_PHOTO_URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getDownloadUrl();
//                Log.e("URL", String.valueOf(downloadURL));
                KENNEL_COVER_PHOTO = String.valueOf(downloadURL);
                if (KENNEL_COVER_PHOTO != null)    {
                    /* UPLOAD THE NEW KENNEL LISTING */
                    uploadKennelListing();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(
                            getApplicationContext(),
                            "Problem publishing your new Kennel...",
                            Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
//                Log.e("UPLOAD EXCEPTION", e.toString());
                Crashlytics.logException(e);
            }
        });
    }

    /** UPLOAD THE NEW KENNEL LISTING **/
    private void uploadKennelListing() {
        if (LISTING_TYPE.equalsIgnoreCase("Free"))  {
            KENNEL_CHARGES_ID = "1";
        } else {
            KENNEL_CHARGES_ID = "2";
        }
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.registerNewKennel(
                KENNEL_OWNER_ID, KENNEL_CHARGES_ID, KENNEL_NAME, KENNEL_COVER_PHOTO, KENNEL_ADDRESS, KENNEL_PIN_CODE,
                COUNTRY_ID, STATE_ID, CITY_ID, String.valueOf(KENNEL_LATITUDE), String.valueOf(KENNEL_LONGITUDE),
                KENNEL_PHONE_PREFIX_1, KENNEL_PHONE_NUMBER_1, KENNEL_PHONE_PREFIX_2, KENNEL_PHONE_NUMBER_2,
                KENNEL_PET_CAPACITY, KENNEL_VALID_FROM, KENNEL_VALID_TO
        );
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                if (response.isSuccessful())    {
                    /* GET THE KENNEL ID */
                    KENNEL_ID = response.body().getKennelID();

                    /* CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER */
                    checkPublishedKennels();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update failed...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
                progressDialog.dismiss();
                Crashlytics.logException(t);
//                Log.e("PUBLISH FAILURE", t.getMessage());
            }
        });
    }

    /** CHECK TOTAL KENNELS CREATED BY CURRENT KENNEL OWNER **/
    private void checkPublishedKennels() {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<KennelPages> call = api.fetchOwnerKennels(KENNEL_OWNER_ID);
        call.enqueue(new Callback<KennelPages>() {
            @Override
            public void onResponse(Call<KennelPages> call, Response<KennelPages> response) {
                if (response.body() != null)    {
                    int publishedKennels = Integer.parseInt(response.body().getTotalKennels());
                    if (publishedKennels < 2)   {
                        progressDialog.dismiss();
                        Intent success = new Intent();
                        setResult(RESULT_OK, success);
                        Toast.makeText(getApplicationContext(), "Kennel published successfully...", Toast.LENGTH_LONG).show();
                        finish();
                    } else if (publishedKennels > 2){
                        new MaterialDialog.Builder(KennelCreator.this)
                                .icon(ContextCompat.getDrawable(KennelCreator.this, R.drawable.ic_info_black_24dp))
                                .title("Exceeding Kennel Limit")
                                .content(getString(R.string.kennel_creator_limit_exceed_payment_message))
                                .cancelable(false)
                                .positiveText("Pay Now")
                                .negativeText("Cancel")
                                .theme(Theme.LIGHT)
                                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        /* START THE PAYMENT PROCESS */
                                        startPaymentProcess();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<KennelPages> call, Throwable t) {
//                Log.e("CHECK KENNELS FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** START THE PAYMENT PROCESS **/
    private void startPaymentProcess() {
        final Activity activity = this;
        final Checkout checkout = new Checkout();
        int icon = R.mipmap.ic_launcher;
        checkout.setImage(icon);
        checkout.setFullScreenDisable(true);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Zen Pets");
            options.put("description", "Additional Kennel Listing");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(ADDITIONAL_KENNEL_COST) * 100);
            JSONObject preFill = new JSONObject();
            preFill.put("email", "siddharth.lele@gmail.com");
            preFill.put("contact", "8087471157");
            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorPaymentID, Toast.LENGTH_SHORT).show();

            /* CAPTURE THE KENNEL PAYMENT */
            captureKennelPayment(razorPaymentID);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    /** CAPTURE THE KENNEL PAYMENT **/
    private void captureKennelPayment(final String razorPaymentID) {
        String apiKey = getString(R.string.razor_pay_api_key_id);
        String apiSecret = getString(R.string.razor_pay_api_key_secret);
        String strCredentials = Credentials.basic(apiKey, apiSecret);
        String strUrl = "https://" + apiKey + ":" + apiSecret + "@api.razorpay.com/v1/payments/" + razorPaymentID + "/capture";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("amount", String.valueOf(Integer.parseInt(ADDITIONAL_KENNEL_COST) * 100))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", strCredentials)
                .url(strUrl)
                .post(body)
                .build();
//        Log.e("REQUEST", String.valueOf(request));
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.e("FAILURE", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
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
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    String strResult = response.body().string();
                    JSONObject JORoot = new JSONObject(strResult);
                    if (JORoot.has("error_code") && JORoot.getString("error_code").equalsIgnoreCase("null")) {
                        /* CHECK THE CAPTURED STATUS */
                        if (JORoot.has("captured")) {
                            String CAPTURED_STATUS = JORoot.getString("captured");
                            if (CAPTURED_STATUS.equalsIgnoreCase("true"))   {
                                /* UPDATE THE KENNEL PAYMENT */
                                updateKennelPayment(razorPaymentID);
                            } else {
//                                Log.e("CAPTURE FAILED", "The payment was not captured successfully...");
                                Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            Log.e("CAPTURE FAILED", "The payment was not captured successfully...");
                            Toast.makeText(getApplicationContext(), "An error occurred...", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /** UPDATE THE KENNEL PAYMENT **/
    private void updateKennelPayment(String razorPaymentID) {
        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
        Call<Kennel> call = api.updateKennelPayment(KENNEL_ID, razorPaymentID);
        call.enqueue(new Callback<Kennel>() {
            @Override
            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
                progressDialog.dismiss();
                Intent success = new Intent();
                setResult(RESULT_OK, success);
                Toast.makeText(getApplicationContext(), "Kennel published successfully...", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<Kennel> call, Throwable t) {
//                Log.e("PAYMENT UPDATE FAILURE",t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        String strTitle = "Modify Kennel Listing";
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
        MenuInflater inflater = new MenuInflater(KennelCreator.this);
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
                /* CHECK KENNEL DETAILS */
                checkKennelDetails();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    KENNEL_LATITUDE = bundle.getDouble("LATITUDE");
                    KENNEL_LONGITUDE = bundle.getDouble("LONGITUDE");
                }

                /* GET THE APPROXIMATE ADDRESS FOR DISPLAY */
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(KENNEL_LATITUDE, KENNEL_LONGITUDE, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    if (!TextUtils.isEmpty(address)) {
                        txtLocation.setText(address);
                    } else {
                        // TODO: DISPLAY THE COORDINATES INSTEAD
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotoReturned(imageFiles);
            }
        });
    }

    /***** PROCESS THE SELECTED IMAGE AND GRAB THE URI *****/
    private void onPhotoReturned(List<File> imageFiles) {
        try {
            File compressedFile = new Compressor(this)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .compressToFile(imageFiles.get(0));
            Uri uri = Uri.fromFile(compressedFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imgvwKennelCoverPhoto.setImageURI(uri);
//            imgvwKennelCoverPhoto.setImageBitmap(bitmap);
//            imgvwKennelCoverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

            /* STORE THE BITMAP AS A FILE AND USE THE FILE'S URI */
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/Zen Pets");
            myDir.mkdirs();
            String fName = "photo.jpg";
            File file = new File(myDir, fName);
            if (file.exists()) file.delete();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                /* GET THE FINAL URI */
                KENNEL_COVER_PHOTO_URI = Uri.fromFile(file);
//                Log.e("URI", String.valueOf(KENNEL_COVER_PHOTO_URI));
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
//                Log.e("EXCEPTION", e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
//            Log.e("EXCEPTION", e.getMessage());
        }
    }

    /***** CHECK STORAGE PERMISSION *****/
    private void checkStoragePermission() {
        /* CHECK FOR PERMISSION STATUS */
        if (ContextCompat.checkSelfPermission(KennelCreator.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)   {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))    {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_message))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        KennelCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ACCESS_STORAGE_CONSTANT);
            }
        } else {
            final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelCreator.this);
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
            sheetDialog.setContentView(view);
            sheetDialog.show();

            /* CAST THE CHOOSER ELEMENTS */
            LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
            LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

            /* SELECT A GALLERY IMAGE */
            linlaGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openGallery(KennelCreator.this, 0);
                }
            });

            /* SELECT A CAMERA IMAGE */
            linlaCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sheetDialog.dismiss();
                    EasyImage.openCamera(KennelCreator.this, 0);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_STORAGE_CONSTANT)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
                final BottomSheetDialog sheetDialog = new BottomSheetDialog(KennelCreator.this);
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.image_picker_sheet, null);
                sheetDialog.setContentView(view);
                sheetDialog.show();

                /* CAST THE CHOOSER ELEMENTS */
                LinearLayout linlaGallery = view.findViewById(R.id.linlaGallery);
                LinearLayout linlaCamera = view.findViewById(R.id.linlaCamera);

                /* SELECT A GALLERY IMAGE */
                linlaGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openGallery(KennelCreator.this, 0);
                    }
                });

                /* SELECT A CAMERA IMAGE */
                linlaCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheetDialog.dismiss();
                        EasyImage.openCamera(KennelCreator.this, 0);
                    }
                });
            } else {
                /* SHOW THE DIALOG */
                new MaterialDialog.Builder(this)
                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
                        .title(getString(R.string.storage_permission_title))
                        .cancelable(false)
                        .content(getString(R.string.storage_permission_denied))
                        .positiveText(getString(R.string.permission_grant))
                        .negativeText(getString(R.string.permission_deny))
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(
                                        KennelCreator.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCESS_STORAGE_CONSTANT);
                            }
                        }).show();
            }
        }
    }

    /** FETCH THE LIST OF STATES **/
    private void fetchStates() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<StatesData> call = api.allStates(COUNTRY_ID);
        call.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(Call<StatesData> call, Response<StatesData> response) {
                arrStates = response.body().getStates();

                /* SET THE ADAPTER TO THE STATES SPINNER */
                spnState.setAdapter(new StatesAdapter(KennelCreator.this, arrStates));
            }

            @Override
            public void onFailure(Call<StatesData> call, Throwable t) {
//                Log.e("STATES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /** FETCH THE LIST OF CITIES **/
    private void fetchCities() {
        LocationAPI api = ZenApiClient.getClient().create(LocationAPI.class);
        Call<CitiesData> call = api.allCities(STATE_ID);
        call.enqueue(new Callback<CitiesData>() {
            @Override
            public void onResponse(Call<CitiesData> call, Response<CitiesData> response) {
                arrCities = response.body().getCities();

                /* SET THE ADAPTER TO THE CITIES SPINNER */
                spnCity.setAdapter(new CitiesAdapter(KennelCreator.this, arrCities));
            }

            @Override
            public void onFailure(Call<CitiesData> call, Throwable t) {
//                Log.e("CITIES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}