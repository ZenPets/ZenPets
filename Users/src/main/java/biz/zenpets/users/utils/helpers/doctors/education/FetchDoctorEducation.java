package biz.zenpets.users.utils.helpers.doctors.education;

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

public class FetchDoctorEducation extends AsyncTask<Object, Void, StringBuilder> {

    /* THE INTERFACE INSTANCE */
    private final FetchDoctorEducationInterface delegate;

    /* THE STRING BUILDER INSTANCE */
    private StringBuilder sb;

    public FetchDoctorEducation(FetchDoctorEducationInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected StringBuilder doInBackground(Object... objects) {
        String URL_DOCTOR_EDUCATION = AppPrefs.context().getString(R.string.url_doctor_education_fetch);
        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTOR_EDUCATION).newBuilder();
        builder.addQueryParameter("doctorID", String.valueOf(objects[0]));
        String FINAL_URL = builder.build().toString();
//        Log.e("EDUCATION", FINAL_URL);
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
                JSONArray JAQualifications = JORoot.getJSONArray("qualifications");
                sb = new StringBuilder();
                for (int i = 0; i < JAQualifications.length(); i++) {
                    JSONObject JOQualifications = JAQualifications.getJSONObject(i);

                    if (JOQualifications.has("doctorEducationName"))    {
                        String doctorEducationName = JOQualifications.getString("doctorEducationName");
                        sb.append(doctorEducationName).append(", ");
                    }
                }
            } else {
                sb = null;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return sb;
    }

    @Override
    protected void onPostExecute(StringBuilder builder) {
        super.onPostExecute(builder);
        delegate.onEducationResult(builder);
    }
}