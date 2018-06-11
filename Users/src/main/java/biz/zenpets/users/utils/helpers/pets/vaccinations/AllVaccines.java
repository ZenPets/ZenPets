package biz.zenpets.users.utils.helpers.pets.vaccinations;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.pets.vaccines.Vaccine;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllVaccines extends AsyncTask<Void, Void, ArrayList<Vaccine>> {

    /** AN INTERFACE INSTANCE **/
    private final AllVaccinesInterface delegate;

    /** AN ARRAY LIST INSTANCE **/
    private final ArrayList<Vaccine> arrVaccines = new ArrayList<>();

    public AllVaccines(AllVaccinesInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Vaccine> doInBackground(Void... voids) {
        String URL_VACCINES = AppPrefs.context().getString(R.string.url_all_vaccines);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL_VACCINES)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAVaccines = JORoot.getJSONArray("vaccines");
                Vaccine data;
                for (int i = 0; i < JAVaccines.length(); i++) {
                    JSONObject JOVaccines = JAVaccines.getJSONObject(i);
                    data = new Vaccine();

                    /* GET THE VACCINE ID **/
                    if (JOVaccines.has("vaccineID")) {
                        data.setVaccineID(JOVaccines.getString("vaccineID"));
                    } else {
                        data.setVaccineID(null);
                    }

                    /* GET THE VACCINE NAME **/
                    if (JOVaccines.has("vaccineName")) {
                        data.setVaccineName(JOVaccines.getString("vaccineName"));
                    } else {
                        data.setVaccineName(null);
                    }

                    /* GET THE VACCINE DESCRIPTION **/
                    if (JOVaccines.has("vaccineDescription")) {
                        data.setVaccineDescription(JOVaccines.getString("vaccineDescription"));
                    } else {
                        data.setVaccineDescription(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrVaccines.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrVaccines;
    }

    @Override
    protected void onPostExecute(ArrayList<Vaccine> vaccinesData) {
        super.onPostExecute(vaccinesData);
        delegate.allVaccines(vaccinesData);
    }
}