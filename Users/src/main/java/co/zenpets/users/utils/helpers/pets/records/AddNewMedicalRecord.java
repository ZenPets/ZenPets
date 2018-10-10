package co.zenpets.users.utils.helpers.pets.records;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class AddNewMedicalRecord extends AsyncTask<Object, Void, String> {

    /** THE INTERFACE INSTANCE **/
    private final AddNewMedicalRecordInterface delegate;

    /** A STRING INSTANCE TO HOLD THE NEW RECORD ID **/
    private String MEDICAL_RECORD_ID = null;

    public AddNewMedicalRecord(AddNewMedicalRecordInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String URL_NEW_RECORD = AppPrefs.context().getString(R.string.url_new_medical_record);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("recordTypeID", String.valueOf(objects[0]))
                .add("userID", String.valueOf(objects[1]))
                .add("petID", String.valueOf(objects[2]))
                .add("medicalRecordNotes", String.valueOf(objects[3]))
                .add("medicalRecordDate", String.valueOf(objects[4]))
                .build();
        Request request = new Request.Builder()
                .url(URL_NEW_RECORD)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                if (JORoot.has("medicalRecordID"))  {
                    MEDICAL_RECORD_ID = JORoot.getString("medicalRecordID");
                } else {
                    MEDICAL_RECORD_ID = null;
                }
            } else {
                MEDICAL_RECORD_ID = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MEDICAL_RECORD_ID;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onNewRecord(s);
    }
}