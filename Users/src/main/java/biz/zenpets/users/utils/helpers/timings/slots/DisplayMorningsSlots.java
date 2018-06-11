package biz.zenpets.users.utils.helpers.timings.slots;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.appointment.slots.MorningTimeSlotsData;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class DisplayMorningsSlots extends AsyncTask<Object, Void, ArrayList<MorningTimeSlotsData>> {

    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
    private final MorningSlotsInterface delegate;

    /* THE AFTERNOON SLOTS ARRAY LIST */
    private final ArrayList<MorningTimeSlotsData> arrMorningSlots = new ArrayList<>();

    /* THE AFTERNOON TIME SLOTS DATA MODEL INSTANCE */
    private MorningTimeSlotsData morningData;

    /* THE STRINGS TO HOLD THE QUERY DATA */
    private String CURRENT_DATE = null;
    private String MORNING_START_TIME = null;
    private String MORNING_END_TIME = null;
    private String DOCTOR_ID = null;
    private String CLINIC_ID = null;

    public DisplayMorningsSlots(MorningSlotsInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<MorningTimeSlotsData> doInBackground(Object... objects) {
        /* GET THE PARAMETERS */
        CURRENT_DATE = String.valueOf(objects[0]);
        MORNING_START_TIME = (String) objects[1];
        MORNING_END_TIME = String.valueOf(objects[2]);
        DOCTOR_ID = String.valueOf(objects[3]);
        CLINIC_ID = String.valueOf(objects[4]);

        /* CALCULATE THE SLOTS */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            final Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(sdf.parse(CURRENT_DATE + " " + MORNING_START_TIME));
            if (startCalendar.get(Calendar.MINUTE) < 15) {
                startCalendar.set(Calendar.MINUTE, 0);
            } else {
                startCalendar.add(Calendar.MINUTE, 15);
                startCalendar.clear(Calendar.MINUTE);
            }

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(sdf.parse(CURRENT_DATE + " " + MORNING_END_TIME));
            endCalendar.add(Calendar.HOUR_OF_DAY, 0);

            endCalendar.clear(Calendar.MINUTE);
            endCalendar.clear(Calendar.SECOND);
            endCalendar.clear(Calendar.MILLISECOND);

            final SimpleDateFormat slotTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            while (endCalendar.after(startCalendar)) {
                morningData = new MorningTimeSlotsData();

                /* SET THE TIME SLOT */
                String slotStartTime = slotTime.format(startCalendar.getTime());
                morningData.setAppointmentTime(slotStartTime);
                startCalendar.add(Calendar.MINUTE, 15);

                /* SET THE DOCTOR ID */
                morningData.setDoctorID(DOCTOR_ID);

                /* SET THE CLINIC ID */
                morningData.setClinicID(CLINIC_ID);

                /* SET THE APPOINTMENT DATE */
                morningData.setAppointmentDate(CURRENT_DATE);

                String URL_APPOINTMENT_STATUS = AppPrefs.context().getString(R.string.url_timings_check_availability);
                HttpUrl.Builder builder = HttpUrl.parse(URL_APPOINTMENT_STATUS).newBuilder();
                builder.addQueryParameter("doctorID", DOCTOR_ID);
                builder.addQueryParameter("clinicID", CLINIC_ID);
                builder.addQueryParameter("appointmentDate", CURRENT_DATE);
                builder.addQueryParameter("appointmentTime", slotStartTime);
                String FINAL_URL = builder.build().toString();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(FINAL_URL)
                        .build();
                Call call = client.newCall(request);
                Response response = call.execute();
                String strResult = response.body().string();
                JSONObject JORoot = new JSONObject(strResult);
                if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                    JSONArray JAAppointments = JORoot.getJSONArray("appointments");
                    if (JAAppointments.length() > 0)    {
                        morningData.setAppointmentStatus("Unavailable");
                    } else {
                        morningData.setAppointmentStatus("Available");
                    }
                }

                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                arrMorningSlots.add(morningData);
            }
        } catch (ParseException | IOException | JSONException e) {
            e.printStackTrace();
        }

        return arrMorningSlots;
    }

    @Override
    protected void onPostExecute(ArrayList<MorningTimeSlotsData> morningTimeSlotsData) {
        super.onPostExecute(morningTimeSlotsData);
        delegate.onMorningSlotResult(morningTimeSlotsData);
    }
}