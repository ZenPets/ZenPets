package biz.zenpets.users.utils.helpers.pets.pet;

import android.content.res.Resources;
import android.os.AsyncTask;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.pets.pets.Pet;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("ConstantConditions")
public class FetchUserPets extends AsyncTask<Object, Void, ArrayList<Pet>> {

    /** THE INTERFACE INSTANCE **/
    private final FetchUserPetsInterface delegate;

    /** AN ARRAY LIST INSTANCE **/
    private final ArrayList<Pet> arrPets = new ArrayList<>();

    public FetchUserPets(FetchUserPetsInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Pet> doInBackground(Object... objects) {
        String URL_USER_PETS = AppPrefs.context().getString(R.string.url_user_pets);
        HttpUrl.Builder builder = HttpUrl.parse(URL_USER_PETS).newBuilder();
        builder.addQueryParameter("userID", String.valueOf(objects[0]));
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
                JSONArray JAPets = JORoot.getJSONArray("pets");
                Pet data;
                for (int i = 0; i < JAPets.length(); i++) {
                    JSONObject JOPets = JAPets.getJSONObject(i);
                    data = new Pet();

                    /* GET THE PET ID */
                    if (JOPets.has("petID"))    {
                        data.setPetID(JOPets.getString("petID"));
                    } else {
                        data.setPetID(null);
                    }

                    /* GET THE USER ID */
                    if (JOPets.has("userID"))    {
                        data.setUserID(JOPets.getString("userID"));
                    } else {
                        data.setUserID(null);
                    }

                    /* GET THE PET TYPE ID */
                    if (JOPets.has("petTypeID"))    {
                        data.setPetTypeID(JOPets.getString("petTypeID"));
                    } else {
                        data.setPetTypeID(null);
                    }

                    /* GET THE PET TYPE NAME */
                    if (JOPets.has("petTypeName"))  {
                        data.setPetTypeName(JOPets.getString("petTypeName"));
                    } else {
                        data.setPetTypeName(null);
                    }

                    /* GET THE BREED ID */
                    if (JOPets.has("breedID"))  {
                        data.setBreedID(JOPets.getString("breedID"));
                    } else {
                        data.setBreedID(null);
                    }

                    /* GET THE BREED NAME */
                    if (JOPets.has("breedName"))  {
                        data.setBreedName(JOPets.getString("breedName"));
                    } else {
                        data.setBreedName(null);
                    }

                    /* GET THE PET NAME */
                    if (JOPets.has("petName"))  {
                        data.setPetName(JOPets.getString("petName"));
                    } else {
                        data.setPetName(null);
                    }

                    /* GET THE PET GENDER */
                    if (JOPets.has("petGender")) {
                        data.setPetGender(JOPets.getString("petGender"));
                    } else {
                        data.setPetGender(null);
                    }

                    /* GET THE PET DATE OF BIRTH (CALCULATE THE PET'S AGE) */
                    if (JOPets.has("petDOB")) {
                        String strPetDOB = JOPets.getString("petDOB");
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String strPetAge = null;
                        try {
                            /* SET THE DATE OF BIRTH TO A CALENDAR DATE */
                            Date dtDOB = format.parse(strPetDOB);
                            Calendar calDOB = Calendar.getInstance();
                            calDOB.setTime(dtDOB);
                            int dobYear = calDOB.get(Calendar.YEAR);
                            int dobMonth = calDOB.get(Calendar.MONTH) + 1;
                            int dobDate = calDOB.get(Calendar.DATE);

                            /* SET THE CURRENT DATE TO A CALENDAR INSTANCE */
                            Calendar calNow = Calendar.getInstance();
                            int nowYear = calNow.get(Calendar.YEAR);
                            int nowMonth = calNow.get(Calendar.MONTH) + 1;
                            int nowDate = calNow.get(Calendar.DATE);

                            LocalDate dateDOB = new LocalDate(dobYear, dobMonth, dobDate);
                            LocalDate dateNOW = new LocalDate(nowYear, nowMonth, nowDate);
                            Period period = new Period(dateDOB, dateNOW, PeriodType.yearMonthDay());
                            Resources resources = AppPrefs.context().getResources();
                            if (period.getYears() == 0)   {
                                strPetAge = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
                            } else if (period.getYears() == 1)    {
                                strPetAge = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
                            } else if (period.getYears() > 1) {
                                strPetAge = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
                            }
                            data.setPetDOB(strPetAge);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        data.setPetDOB(null);
                    }

                    /* GET THE PET NEUTERED STATUS */
                    if (JOPets.has("petNeutered")) {
                        data.setPetNeutered(JOPets.getString("petNeutered"));
                    } else {
                        data.setPetNeutered(null);
                    }

                    /* GET THE PET'S DISPLAY PROFILE  */
                    if (JOPets.has("petDisplayProfile")) {
                        data.setPetDisplayProfile(JOPets.getString("petDisplayProfile"));
                    } else {
                        data.setPetDisplayProfile(null);
                    }

                    /* GET THE PET'S ACTIVE STATUS (1 == ALIVE || 0 == DEAD)*/
                    if (JOPets.has("petActive")) {
                        data.setPetActive(JOPets.getString("petActive"));
                    } else {
                        data.setPetActive(null);
                    }

                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                    arrPets.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrPets;
    }

    @Override
    protected void onPostExecute(ArrayList<Pet> data) {
        super.onPostExecute(data);
        delegate.userPets(data);
    }
}