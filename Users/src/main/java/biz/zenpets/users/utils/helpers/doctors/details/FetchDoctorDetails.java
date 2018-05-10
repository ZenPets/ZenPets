package biz.zenpets.users.utils.helpers.doctors.details;

import android.content.res.Resources;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchDoctorDetails extends AsyncTask<Object, Void, String[]> {

    /* THE INTERFACE INSTANCE */
    private final FetchDoctorDetailsInterface delegate;

    /* DATA INSTANCES TO HOLD THE DOCTOR DETAILS */
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_PROFILE = null;
    private String CLINIC_LOGO = null;
    private String DOCTOR_EXPERIENCE = null;
    private String CLINIC_CURRENCY = null;
    private String DOCTOR_CHARGES = null;
    private String CLINIC_NAME = null;
    private String CLINIC_ADDRESS = null;
    private String CLINIC_CITY = null;
    private String CLINIC_STATE = null;
    private String CLINIC_PIN_CODE = null;
    private String CLINIC_LANDMARK = null;
    private String DOCTOR_PHONE_NUMBER = null;
    private Double CLINIC_LATITUDE = null;
    private Double CLINIC_LONGITUDE = null;
    private String DOCTOR_LIKES_PERCENT = null;
    private String DOCTOR_VOTES = null;

    public FetchDoctorDetails(FetchDoctorDetailsInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String[] doInBackground(Object... objects) {
        String URL_DOCTOR_PROFILE = AppPrefs.context().getString(R.string.url_doctor_details);
        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTOR_PROFILE).newBuilder();
        builder.addQueryParameter("doctorID", String.valueOf(objects[0]));
        builder.addQueryParameter("clinicID", String.valueOf(objects[1]));
        String FINAL_URL = builder.build().toString();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(FINAL_URL)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {

                /* GET THE DOCTOR'S PREFIX AND NAME */
                DOCTOR_PREFIX = JORoot.getString("doctorPrefix");
                DOCTOR_NAME = JORoot.getString("doctorName");

                /* GET THE DOCTOR'S PROFILE PICTURE */
                DOCTOR_PROFILE = JORoot.getString("doctorDisplayProfile");

                /* GET THE CLINIC'S LOGO */
                CLINIC_LOGO = JORoot.getString("clinicLogo");

                /* GET THE DOCTOR'S EXPERIENCE */
                DOCTOR_EXPERIENCE = JORoot.getString("doctorExperience");

                /* GET THE DOCTOR'S SELECTED CURRENCY AND CHARGES */
                CLINIC_CURRENCY = JORoot.getString("currencySymbol");
                DOCTOR_CHARGES = JORoot.getString("doctorCharges");

                /* GET THE CLINIC NAME */
                CLINIC_NAME = JORoot.getString("clinicName");

                /* GET THE CLINIC ADDRESS */
                CLINIC_ADDRESS = JORoot.getString("clinicAddress");
                CLINIC_CITY = JORoot.getString("cityName");
                CLINIC_STATE = JORoot.getString("stateName");
                CLINIC_PIN_CODE = JORoot.getString("clinicPinCode");
                CLINIC_LANDMARK = JORoot.getString("clinicLandmark");

                /* GET THE DOCTOR'S PHONE NUMBER */
                if (JORoot.has("doctorPhonePrefix") && JORoot.has("doctorPhoneNumber")) {
                    String doctorPhonePrefix = JORoot.getString("doctorPhonePrefix");
                    String doctorPhoneNumber = JORoot.getString("doctorPhoneNumber");
                    DOCTOR_PHONE_NUMBER = doctorPhonePrefix + doctorPhoneNumber;
                }

                /* GET THE CLINIC LATITUDE AND LONGITUDE */
                CLINIC_LATITUDE = Double.valueOf(JORoot.getString("clinicLatitude"));
                CLINIC_LONGITUDE = Double.valueOf(JORoot.getString("clinicLongitude"));

                /* THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES */
                int TOTAL_VOTES = 0;
                int TOTAL_LIKES = 0;

                /* GET THE POSITIVE REVIEWS / FEEDBACK FOR THE DOCTORS */
                String URL_POSITIVE_REVIEWS = AppPrefs.context().getString(R.string.url_doctor_positive_reviews);
                HttpUrl.Builder builderPositive = HttpUrl.parse(URL_POSITIVE_REVIEWS).newBuilder();
                builderPositive.addQueryParameter("doctorID", String.valueOf(objects[0]));
                builderPositive.addQueryParameter("recommendStatus", "Yes");
                String FINAL_URL_POSITIVE = builderPositive.build().toString();
                OkHttpClient clientPositive = new OkHttpClient();
                Request requestPositive = new Request.Builder()
                        .url(FINAL_URL_POSITIVE)
                        .build();
                Call callPositive = clientPositive.newCall(requestPositive);
                Response responsePositive = callPositive.execute();
                String strPositiveReview = responsePositive.body().string();
                JSONObject JORootPositive = new JSONObject(strPositiveReview);
                if (JORootPositive.has("error") && JORootPositive.getString("error").equalsIgnoreCase("false")) {
                    JSONArray JAPositiveReviews = JORootPositive.getJSONArray("reviews");
                    TOTAL_LIKES = JAPositiveReviews.length();
                    TOTAL_VOTES = TOTAL_VOTES + JAPositiveReviews.length();
                }

                /* GET THE POSITIVE REVIEWS / FEEDBACK FOR THE DOCTORS */
                String URL_DOCTOR_REVIEWS = AppPrefs.context().getString(R.string.url_doctor_negative_reviews);
                HttpUrl.Builder builderNegative = HttpUrl.parse(URL_DOCTOR_REVIEWS).newBuilder();
                builderNegative.addQueryParameter("doctorID", String.valueOf(objects[0]));
                builderNegative.addQueryParameter("recommendStatus", "No");
                String FINAL_URL_Negative = builderNegative.build().toString();
                OkHttpClient clientNegative = new OkHttpClient();
                Request requestReviews = new Request.Builder()
                        .url(FINAL_URL_Negative)
                        .build();
                Call reviewCall = clientNegative.newCall(requestReviews);
                Response responseNegative = reviewCall.execute();
                String strNegativeReview = responseNegative.body().string();
                JSONObject JORootNegative = new JSONObject(strNegativeReview);
                if (JORootNegative.has("error") && JORootNegative.getString("error").equalsIgnoreCase("false")) {
                    JSONArray JANegativeReviews = JORootNegative.getJSONArray("reviews");
                    TOTAL_VOTES = TOTAL_VOTES + JANegativeReviews.length();
                }

                /* CALCULATE THE PERCENTAGE OF LIKES */
                double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
                int finalPercentLikes = (int)percentLikes;
                DOCTOR_LIKES_PERCENT = String.valueOf(finalPercentLikes) + "%";

                /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
                Resources resReviews = AppPrefs.context().getResources();
                String reviewQuantity = null;
                if (TOTAL_VOTES == 0)   {
                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                } else if (TOTAL_VOTES == 1)    {
                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                } else if (TOTAL_VOTES > 1) {
                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
                }
                DOCTOR_VOTES = reviewQuantity;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new String[] {DOCTOR_PREFIX, DOCTOR_NAME, DOCTOR_PROFILE, CLINIC_LOGO,
                DOCTOR_EXPERIENCE, CLINIC_CURRENCY, DOCTOR_CHARGES, CLINIC_NAME,
                CLINIC_ADDRESS, CLINIC_CITY, CLINIC_STATE, CLINIC_PIN_CODE, CLINIC_LANDMARK, DOCTOR_PHONE_NUMBER,
                String.valueOf(CLINIC_LATITUDE), String.valueOf(CLINIC_LONGITUDE),
                DOCTOR_LIKES_PERCENT, DOCTOR_VOTES};
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        delegate.onDoctorDetails(strings);
    }
}