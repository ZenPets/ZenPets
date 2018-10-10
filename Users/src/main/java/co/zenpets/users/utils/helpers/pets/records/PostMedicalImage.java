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

public class PostMedicalImage extends AsyncTask<Object, Void, String> {

    /** THE INTERFACE INSTANCE **/
    private final PostMedicalImageInterface delegate;

    /** THE MEDICAL RECORD IMAGE ID **/
    private String MEDICAL_RECORD_IMAGE_ID = null;

    public PostMedicalImage(PostMedicalImageInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Object... objects) {
        String URL_MEDICAL_IMAGE = AppPrefs.context().getString(R.string.url_new_medical_image);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("medicalRecordID", String.valueOf(objects[0]))
                .add("recordImageURL", String.valueOf(objects[1]))
                .build();
        Request request = new Request.Builder()
                .url(URL_MEDICAL_IMAGE)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String strResult = response.body().string();
            JSONObject JORoot = new JSONObject(strResult);
            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                if (JORoot.has("recordImageID"))  {
                    MEDICAL_RECORD_IMAGE_ID = JORoot.getString("recordImageID");
                } else {
                    MEDICAL_RECORD_IMAGE_ID = null;
                }
            } else {
                MEDICAL_RECORD_IMAGE_ID = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MEDICAL_RECORD_IMAGE_ID;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.onImageResult(s);
    }
}