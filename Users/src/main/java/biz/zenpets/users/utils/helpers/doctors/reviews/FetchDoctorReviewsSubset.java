package biz.zenpets.users.utils.helpers.doctors.reviews;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.reviews.Review;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class FetchDoctorReviewsSubset extends AsyncTask<Object, Void, ArrayList<Review>> {

    /* THE INTERFACE INSTANCE */
    private final FetchDoctorReviewsSubsetInterface delegate;

    /* THE ARRAY LIST INSTANCE */
    private final ArrayList<Review> arrReviews = new ArrayList<>();

    public FetchDoctorReviewsSubset(FetchDoctorReviewsSubsetInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Review> doInBackground(Object... objects) {
        String URL_DOCTOR_SERVICES = AppPrefs.context().getString(R.string.url_doctor_review_subset_fetch);
        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTOR_SERVICES).newBuilder();
        builder.addQueryParameter("doctorID", String.valueOf(objects[0]));
        String FINAL_URL = builder.build().toString();
//        Log.e("REVIEWS URL", FINAL_URL);
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
                JSONArray JAReviews = JORoot.getJSONArray("reviews");
                if (JAReviews.length() > 0) {
                    Review data;
                    for (int i = 0; i < JAReviews.length(); i++) {
                        JSONObject JOReviews = JAReviews.getJSONObject(i);
                        data = new Review();

                        /* GET THE REVIEW ID */
                        if (JOReviews.has("reviewID"))  {
                            data.setReviewID(JOReviews.getString("reviewID"));
                        } else {
                            data.setReviewID(null);
                        }

                        /* GET THE DOCTOR ID */
                        if (JOReviews.has("doctorID"))  {
                            data.setDoctorID(JOReviews.getString("doctorID"));
                        } else {
                            data.setDoctorID(null);
                        }

                        /* GET THE USER ID */
                        if (JOReviews.has("userID"))    {
                            data.setUserID(JOReviews.getString("userID"));
                        } else {
                            data.setUserID(null);
                        }

                        /* GET THE USER NAME */
                        if (JOReviews.has("userName"))  {
                            data.setUserName(JOReviews.getString("userName"));
                        } else {
                            data.setUserName(null);
                        }

                        /* GET THE VISIT REASON ID */
                        if (JOReviews.has("visitReasonID"))    {
                            data.setVisitReasonID(JOReviews.getString("visitReasonID"));
                        } else {
                            data.setVisitReasonID(null);
                        }

                        /* GET THE VISIT REASON */
                        if (JOReviews.has("visitReason"))   {
                            data.setVisitReason("Visited for " + JOReviews.getString("visitReason"));
                        } else {
                            data.setVisitReason(null);
                        }

                        /* GET THE RECOMMEND STATUS */
                        if (JOReviews.has("recommendStatus"))    {
                            data.setRecommendStatus(JOReviews.getString("recommendStatus"));
                        } else {
                            data.setRecommendStatus(null);
                        }

                        /* GET THE APPOINTMENT STATUS */
                        if (JOReviews.has("appointmentStatus"))    {
                            data.setAppointmentStatus(JOReviews.getString("appointmentStatus"));
                        } else {
                            data.setAppointmentStatus(null);
                        }

//                        /* GET THE DOCTOR RATING */
//                        if (JOReviews.has("doctorRating"))    {
//                            data.setDoctorRating(JOReviews.getString("doctorRating"));
//                        } else {
//                            data.setDoctorRating(null);
//                        }

                        /* GET THE DOCTOR EXPERIENCE */
                        if (JOReviews.has("doctorExperience"))    {
                            data.setDoctorExperience(JOReviews.getString("doctorExperience"));
                        } else {
                            data.setDoctorExperience(null);
                        }

                        /* GET THE REVIEW TIMESTAMP */
                        if (JOReviews.has("reviewTimestamp"))    {
                            String reviewTimestamp = JOReviews.getString("reviewTimestamp");
                            long lngTimeStamp = Long.parseLong(reviewTimestamp) * 1000;
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(lngTimeStamp);
                            Date date = calendar.getTime();
                            PrettyTime prettyTime = new PrettyTime();
                            String strDate = prettyTime.format(date);
                            data.setReviewTimestamp(strDate);
                        } else {
                            data.setReviewTimestamp(null);
                        }

                        /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                        arrReviews.add(data);
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return arrReviews;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviewsData) {
        super.onPostExecute(reviewsData);
        delegate.onReviewSubset(reviewsData);
    }
}