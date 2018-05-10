package biz.zenpets.users.utils.helpers.pets.vaccinations;

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

public class NewVaccination extends AsyncTask<Object, Void, String> {

    /** AN INTERFACE INSTANCE **/
    NewVaccinationInterface delegate;

    /** THE VACCINATION ID **/
    String VACCINATION_ID = null;

    public NewVaccination(NewVaccinationInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String URL_NEW_CLINIC = AppPrefs.context().getString(R.string.url_new_vaccination);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("petID", String.valueOf(objects[0]))
                .add("vaccineID", String.valueOf(objects[1]))
                .add("vaccinationDate", String.valueOf(objects[2]))
                .add("vaccinationNextDate", String.valueOf(objects[3]))
                .add("vaccinationReminder", String.valueOf(objects[4]))
                .add("vaccinationNotes", String.valueOf(objects[5]))
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
                if (JORoot.has("vaccinationID"))  {
                    VACCINATION_ID = JORoot.getString("vaccinationID");
                } else {
                    VACCINATION_ID = null;
                }
            } else {
                VACCINATION_ID = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return VACCINATION_ID;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onVaccination(s);
    }
}