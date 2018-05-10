package biz.zenpets.users.utils.helpers.visit;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.visit.VisitReasonsData;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchVisitReasons extends AsyncTask<Void, Void, ArrayList<VisitReasonsData>> {

    /** AN INTERFACE INSTANCE **/
    FetchVisitReasonsInterface delegate;

    /** AN ARRAY LIST INSTANCE **/
    ArrayList<VisitReasonsData> arrReasons = new ArrayList<>();

    public FetchVisitReasons(FetchVisitReasonsInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<VisitReasonsData> doInBackground(Void... voids) {
        String URL_VISIT_REASONS = AppPrefs.context().getString(R.string.url_visit_reasons);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL_VISIT_REASONS)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAReasons = JORoot.getJSONArray("reasons");
                VisitReasonsData data;
                for (int i = 0; i < JAReasons.length(); i++) {
                    JSONObject JOReasons = JAReasons.getJSONObject(i);
                    data = new VisitReasonsData();

                    /* GET THE VISIT REASON ID **/
                    if (JOReasons.has("visitReasonID")) {
                        data.setVisitReasonID(JOReasons.getString("visitReasonID"));
                    } else {
                        data.setVisitReasonID(null);
                    }

                    /* GET THE VISIT REASON **/
                    if (JOReasons.has("visitReason")) {
                        data.setVisitReason(JOReasons.getString("visitReason"));
                    } else {
                        data.setVisitReason(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrReasons.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrReasons;
    }

    @Override
    protected void onPostExecute(ArrayList<VisitReasonsData> data) {
        super.onPostExecute(data);
        delegate.visitReasons(data);
    }
}