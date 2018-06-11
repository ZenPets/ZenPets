package biz.zenpets.users.utils.helpers.doctors.reviews;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostClinicRating extends AsyncTask<Object, Void, String> {

    /** THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY **/
    private final PostClinicRatingInterface delegate;

    /** THE NEW CLINIC RATING ID **/
    private String RATING_ID = null;

    public PostClinicRating(PostClinicRatingInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String URL_NEW_CLINIC = AppPrefs.context().getString(R.string.url_clinic_rating_new);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("clinicID", String.valueOf(objects[0]))
                .add("userID", String.valueOf(objects[1]))
                .add("clinicRating", String.valueOf(objects[2]))
                .build();
        Request request = new Request.Builder()
                .url(URL_NEW_CLINIC)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                if (JORoot.has("clinicRatingID"))  {
                    RATING_ID = JORoot.getString("clinicRatingID");
                } else {
                    RATING_ID = null;
                }
            } else {
                RATING_ID = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return RATING_ID;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onRatingResult(s);
    }
}