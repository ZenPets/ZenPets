package biz.zenpets.users.utils.helpers.pets.breed;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.pets.breeds.BreedsData;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchBreedTypes extends AsyncTask<Object, Void, ArrayList<BreedsData>> {

    /** THE INTERFACE INSTANCE **/
    FetchBreedTypesInterface delegate;

    /** AN ARRAY LIST INSTANCE **/
    ArrayList<BreedsData> arrBreeds = new ArrayList<>();

    public FetchBreedTypes(FetchBreedTypesInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<BreedsData> doInBackground(Object... objects) {
        String URL_BREEDS = AppPrefs.context().getString(R.string.url_pet_breeds);
        HttpUrl.Builder builder = HttpUrl.parse(URL_BREEDS).newBuilder();
        builder.addQueryParameter("petTypeID", String.valueOf(objects[0]));
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
                JSONArray JABreeds = JORoot.getJSONArray("breeds");
                BreedsData data;
                for (int i = 0; i < JABreeds.length(); i++) {
                    JSONObject JOBreeds = JABreeds.getJSONObject(i);
                    data = new BreedsData();

                    /* GET THE BREED ID */
                    if (JOBreeds.has("breedID"))    {
                        data.setBreedID(JOBreeds.getString("breedID"));
                    } else {
                        data.setBreedID(null);
                    }

                    /* GET THE PET TYPE ID */
                    if (JOBreeds.has("petTypeID"))    {
                        data.setPetTypeID(JOBreeds.getString("petTypeID"));
                    } else {
                        data.setPetTypeID(null);
                    }

                    /* GET THE BREED NAME */
                    if (JOBreeds.has("breedName"))    {
                        data.setBreedName(JOBreeds.getString("breedName"));
                    } else {
                        data.setBreedName(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrBreeds.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrBreeds;
    }

    @Override
    protected void onPostExecute(ArrayList<BreedsData> data) {
        super.onPostExecute(data);
        delegate.breedTypes(data);
    }
}