package biz.zenpets.users.utils.helpers.pets.pet;

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

public class AddNewPet extends AsyncTask<Object, Void, String> {

    /** THE INTERFACE INSTANCE **/
    private final AddNewPetInterface delegate;

    /** STRING TO HOLD AND RETURN THE NEW PET ID **/
    private String PET_ID = null;

    public AddNewPet(AddNewPetInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String URL_NEW_PET = AppPrefs.context().getString(R.string.url_add_new_pet);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("userID", String.valueOf(objects[0]))
                .add("petTypeID", String.valueOf(objects[1]))
                .add("breedID", String.valueOf(objects[2]))
                .add("petName", String.valueOf(objects[3]))
                .add("petGender", String.valueOf(objects[4]))
                .add("petDOB", String.valueOf(objects[5]))
                .add("petNeutered", String.valueOf(objects[6]))
                .add("petDisplayProfile", String.valueOf(objects[7]))
                .add("petActive", "1")
                .build();
        Request request = new Request.Builder()
                .url(URL_NEW_PET)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                if (JORoot.has("petID"))  {
                    PET_ID = JORoot.getString("petID");
                } else {
                    PET_ID = null;
                }
            } else {
                PET_ID = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return PET_ID;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onNewPet(s);
    }
}