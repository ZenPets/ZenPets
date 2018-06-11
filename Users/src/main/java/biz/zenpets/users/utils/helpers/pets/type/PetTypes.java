package biz.zenpets.users.utils.helpers.pets.type;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.pets.types.PetTypesData;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PetTypes extends AsyncTask<Void, Void, ArrayList<PetTypesData>> {

    /** THE INTERFACE INSTANCE **/
    private final PetTypesInterface delegate;

    /** THE ARRAY LIST INSTANCE **/
    private final ArrayList<PetTypesData> arrPetTypes = new ArrayList<>();

    public PetTypes(PetTypesInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<PetTypesData> doInBackground(Void... voids) {
        String URL_PET_TYPES = AppPrefs.context().getString(R.string.url_pet_types);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL_PET_TYPES)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                JSONArray JAVaccines = JORoot.getJSONArray("types");
                PetTypesData data;
                for (int i = 0; i < JAVaccines.length(); i++) {
                    JSONObject JOVaccines = JAVaccines.getJSONObject(i);
                    data = new PetTypesData();

                    /* GET THE PET TYPE ID **/
                    if (JOVaccines.has("petTypeID")) {
                        data.setPetTypeID(JOVaccines.getString("petTypeID"));
                    } else {
                        data.setPetTypeID(null);
                    }

                    /* GET THE PET TYPE NAME **/
                    if (JOVaccines.has("petTypeName")) {
                        data.setPetTypeName(JOVaccines.getString("petTypeName"));
                    } else {
                        data.setPetTypeName(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrPetTypes.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrPetTypes;
    }

    @Override
    protected void onPostExecute(ArrayList<PetTypesData> data) {
        super.onPostExecute(data);
        delegate.petTypes(data);
    }
}