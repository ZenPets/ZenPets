package biz.zenpets.users.utils.helpers.timings.friday;

import android.os.AsyncTask;

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

public class FridayMorningTimings extends AsyncTask<Object, Void, String[]> {

    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
    private final FridayMorningTimingsInterface delegate;

    /* THE MORNING START TIMINGS AND END TIMINGS STRINGS */
    private String MORNING_START_TIME = null;
    private String MORNING_END_TIME = null;

    public FridayMorningTimings(FridayMorningTimingsInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String[] doInBackground(Object... params) {
        String TIMINGS_URL = AppPrefs.context().getString(R.string.url_timings_fri_mor);
        HttpUrl.Builder builder = HttpUrl.parse(TIMINGS_URL).newBuilder();
        builder.addQueryParameter("doctorID", String.valueOf(params[0]));
        builder.addQueryParameter("clinicID", (String) params[1]);
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

                /* GET THE MORNING START TIME (FROM) */
                if (JORoot.has("friMorFrom"))    {
                    MORNING_START_TIME = JORoot.getString("friMorFrom");
                } else {
                    MORNING_START_TIME = null;
                }

                /* GET THE MORNING END TIME (TO) */
                if (JORoot.has("friMorTo"))  {
                    MORNING_END_TIME = JORoot.getString("friMorTo");
                } else {
                    MORNING_END_TIME = null;
                }
            } else {
                MORNING_START_TIME = null;
                MORNING_END_TIME = null;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new String[] {MORNING_START_TIME, MORNING_END_TIME};
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        delegate.onFridayMorningResult(strings);
    }
}