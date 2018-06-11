package biz.zenpets.users.utils.helpers.pets.records;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.pets.records.RecordType;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchRecordTypes extends AsyncTask<Void, Void, ArrayList<RecordType>> {

    /** THE INTERFACE INSTANCE **/
    private final FetchRecordTypesInterface typesInterface;

    /** AN ARRAY LIST INSTANCE **/
    private final ArrayList<RecordType> arrRecordTypes = new ArrayList<>();

    public FetchRecordTypes(FetchRecordTypesInterface typesInterface) {
        this.typesInterface = typesInterface;
    }

    @Override
    protected ArrayList<RecordType> doInBackground(Void... voids) {
        String URL_RECORD_TYPES = AppPrefs.context().getString(R.string.url_all_record_types);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL_RECORD_TYPES)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JARecords = JORoot.getJSONArray("records");
                RecordType data;
                for (int i = 0; i < JARecords.length(); i++) {
                    JSONObject JORecords = JARecords.getJSONObject(i);
                    data = new RecordType();

                    /* GET THE PET TYPE ID **/
                    if (JORecords.has("recordTypeID")) {
                        data.setRecordTypeID(JORecords.getString("recordTypeID"));
                    } else {
                        data.setRecordTypeID(null);
                    }

                    /* GET THE RECORD TYPE NAME **/
                    if (JORecords.has("recordTypeName")) {
                        data.setRecordTypeName(JORecords.getString("recordTypeName"));
                    } else {
                        data.setRecordTypeName(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrRecordTypes.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrRecordTypes;
    }

    @Override
    protected void onPostExecute(ArrayList<RecordType> data) {
        super.onPostExecute(data);
        typesInterface.allRecordTypes(data);
    }
}