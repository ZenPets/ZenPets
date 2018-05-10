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

public class PostVaccinationImage extends AsyncTask<Object, Void, String> {

    /** THE INTERFACE INSTANCE **/
    PostVaccinationImageInterface delegate;

    /** THE VACCINATION IMAGE ID **/
    String VACCINATION_IMAGE_ID = null;

    public PostVaccinationImage(PostVaccinationImageInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String URL_VACCINATION_IMAGE = AppPrefs.context().getString(R.string.url_new_vaccination_image);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("vaccinationID", String.valueOf(objects[0]))
                .add("vaccinationImageURL", String.valueOf(objects[1]))
                .build();
        Request request = new Request.Builder()
                .url(URL_VACCINATION_IMAGE)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                if (JORoot.has("vaccinationImageID"))  {
                    VACCINATION_IMAGE_ID = JORoot.getString("vaccinationImageID");
                } else {
                    VACCINATION_IMAGE_ID = null;
                }
            } else {
                VACCINATION_IMAGE_ID = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return VACCINATION_IMAGE_ID;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onImageResult(s);
    }
}
