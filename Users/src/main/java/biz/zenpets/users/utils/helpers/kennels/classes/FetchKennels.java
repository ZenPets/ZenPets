package biz.zenpets.users.utils.helpers.kennels.classes;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.kennels.interfaces.FetchKennelsInterface;
import biz.zenpets.users.utils.models.kennels.Kennel;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchKennels extends AsyncTask<Object, Void, ArrayList<Kennel>> {

    /* THE INTERFACE INSTANCE */
    private final FetchKennelsInterface delegate;

    /* A KENNELS ARRAY LIST INSTANCE */
    ArrayList<Kennel> arrKennels = new ArrayList<>();

    public FetchKennels(FetchKennelsInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Kennel> doInBackground(Object... objects) {
        String strUrl = AppPrefs.context().getString(R.string.url_kennels_list);
        HttpUrl.Builder builder = HttpUrl.parse(strUrl).newBuilder();
        builder.addQueryParameter("cityID", String.valueOf(objects[0]));
        builder.addQueryParameter("pageNumber", String.valueOf(objects[1]));
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
                JSONArray JAKennels = JORoot.getJSONArray("kennels");
                if (JAKennels.length() > 0) {
                    Kennel data;
                    for (int i = 0; i < JAKennels.length(); i++) {
                        JSONObject JOKennels = JAKennels.getJSONObject(i);
                        data = new Kennel();

                        /* GET THE KENNEL ID */
                        if (JOKennels.has("kennelID"))  {
                            data.setKennelID(JOKennels.getString("kennelID"));
                        } else {
                            data.setKennelID(null);
                        }

                        /* GET THE KENNEL NAME */
                        if (JOKennels.has("kennelName"))    {
                            data.setKennelName(JOKennels.getString("kennelName"));
                        } else {
                            data.setKennelName(null);
                        }

                        /* GET THE KENNEL OWNER'S ID */
                        if (JOKennels.has("kennelOwnerID")) {
                            data.setKennelOwnerID(JOKennels.getString("kennelOwnerID"));
                        } else {
                            data.setKennelOwnerID(null);
                        }

                        /* GET THE KENNEL OWNER'S NAME */
                        if (JOKennels.has("kennelOwnerName"))   {
                            data.setKennelOwnerName(JOKennels.getString("kennelOwnerName"));
                        } else {
                            data.setKennelOwnerName(null);
                        }

                        /* GET THE KENNEL OWNER'S DISPLAY PROFILE */
                        if (JOKennels.has("kennelOwnerDisplayProfile")) {
                            data.setKennelOwnerDisplayProfile(JOKennels.getString("kennelOwnerDisplayProfile"));
                        } else {
                            data.setKennelOwnerDisplayProfile(null);
                        }

                        /* GET THE KENNEL ADDRESS */
                        if (JOKennels.has("kennelAddress")) {
                            data.setKennelAddress(JOKennels.getString("kennelAddress"));
                        } else {
                            data.setKennelAddress(null);
                        }

                        /* GET THE KENNEL PIN CODE */
                        if (JOKennels.has("kennelPinCode")) {
                            data.setKennelPinCode(JOKennels.getString("kennelPinCode"));
                        } else {
                            data.setKennelPinCode(null);
                        }

                        /* GET THE KENNEL COUNTY ID */
                        if (JOKennels.has("countryID")) {
                            data.setCountryID(JOKennels.getString("countryID"));
                        } else {
                            data.setCountryID(null);
                        }

                        /* GET THE KENNEL COUNTRY NAME */
                        if (JOKennels.has("countryName"))   {
                            data.setCountryName(JOKennels.getString("countryName"));
                        } else {
                            data.setCountryName(null);
                        }

                        /* GET THE KENNEL STATE ID */
                        if (JOKennels.has("stateID"))   {
                            data.setStateID(JOKennels.getString("stateID"));
                        } else {
                            data.setStateID(null);
                        }

                        /* GET THE KENNEL STATE NAME */
                        if (JOKennels.has("stateName")) {
                            data.setStateName(JOKennels.getString("stateName"));
                        } else {
                            data.setStateName(null);
                        }

                        /* GET THE KENNEL CITY ID */
                        if (JOKennels.has("cityID"))    {
                            data.setCityID(JOKennels.getString("cityID"));
                        } else {
                            data.setCityID(null);
                        }

                        /* GET THE KENNEL CITY NAME */
                        if (JOKennels.has("cityName"))  {
                            data.setCityName(JOKennels.getString("cityName"));
                        } else {
                            data.setCityName(null);
                        }

                        /* GET THE KENNEL LATITUDE */
                        if (JOKennels.has("kennelLatitude"))    {
                            data.setKennelLatitude(JOKennels.getString("kennelLatitude"));
                        } else {
                            data.setKennelLatitude(null);
                        }

                        /* GET THE KENNEL LONGITUDE */
                        if (JOKennels.has("kennelLongitude"))   {
                            data.setKennelLongitude(JOKennels.getString("kennelLongitude"));
                        } else {
                            data.setKennelLongitude(null);
                        }

                        /* GET THE KENNEL'S PHONE PREFIX #1*/
                        if (JOKennels.has("kennelPhonePrefix1"))    {
                            data.setKennelPhonePrefix1(JOKennels.getString("kennelPhonePrefix1"));
                        } else {
                            data.setKennelPhonePrefix1(null);
                        }

                        /* GET THE KENNEL'S PHONE NUMBER #1*/
                        if (JOKennels.has("kennelPhoneNumber1"))    {
                            data.setKennelPhoneNumber1(JOKennels.getString("kennelPhoneNumber1"));
                        } else {
                            data.setKennelPhoneNumber1(null);
                        }

                        /* GET THE KENNEL'S PHONE PREFIX #2*/
                        if (JOKennels.has("kennelPhonePrefix2"))    {
                            data.setKennelPhonePrefix2(JOKennels.getString("kennelPhonePrefix2"));
                        } else {
                            data.setKennelPhonePrefix2(null);
                        }

                        /* GET THE KENNEL'S PHONE NUMBER #2*/
                        if (JOKennels.has("kennelPhoneNumber2"))    {
                            data.setKennelPhoneNumber2(JOKennels.getString("kennelPhoneNumber2"));
                        } else {
                            data.setKennelPhoneNumber1(null);
                        }

                        /* GET THE KENNEL'S PET CAPACITY */
                        if (JOKennels.has("kennelPetCapacity"))    {
                            data.setKennelPetCapacity(JOKennels.getString("kennelPetCapacity"));
                        } else {
                            data.setKennelPetCapacity(null);
                        }

                        /* ADD THE GATHERED DATA TO THE ARRAY LIST */
                        arrKennels.add(data);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrKennels;
    }

    @Override
    protected void onPostExecute(ArrayList<Kennel> kennels) {
        super.onPostExecute(kennels);
        delegate.onKennels(kennels);
    }
}