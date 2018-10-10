package co.zenpets.users.utils.helpers.timings.thursday;

//public class ThursdayAfternoonTimings extends AsyncTask<Object, Void, String[]> {
//
//    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
//    private final ThursdayAfternoonTimingsInterface delegate;
//
//    /* THE AFTERNOON START TIMINGS AND END TIMINGS STRINGS */
//    private String AFTERNOON_START_TIME = null;
//    private String AFTERNOON_END_TIME = null;
//
//    public ThursdayAfternoonTimings(ThursdayAfternoonTimingsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String[] doInBackground(Object... params) {
//        String TIMINGS_URL = AppPrefs.context().getString(R.string.url_timings_thu_aft);
//        HttpUrl.Builder builder = HttpUrl.parse(TIMINGS_URL).newBuilder();
//        builder.addQueryParameter("doctorID", String.valueOf(params[0]));
//        builder.addQueryParameter("clinicID", (String) params[1]);
//        String FINAL_URL = builder.build().toString();
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(FINAL_URL)
//                .build();
//        Call call = client.newCall(request);
//        try {
//            Response response = call.execute();
//            String strResult = response.body().string();
//            JSONObject JORoot = new JSONObject(strResult);
//            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//
//                /* GET THE AFTERNOON START TIME (FROM) */
//                if (JORoot.has("thuAftFrom"))    {
//                    AFTERNOON_START_TIME = JORoot.getString("thuAftFrom");
//                } else {
//                    AFTERNOON_START_TIME = null;
//                }
//
//                /* GET THE AFTERNOON END TIME (TO) */
//                if (JORoot.has("thuAftTo"))  {
//                    AFTERNOON_END_TIME = JORoot.getString("thuAftTo");
//                } else {
//                    AFTERNOON_END_TIME = null;
//                }
//            } else {
//                AFTERNOON_START_TIME = null;
//                AFTERNOON_END_TIME = null;
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return new String[] {AFTERNOON_START_TIME, AFTERNOON_END_TIME};
//    }
//
//    @Override
//    protected void onPostExecute(String[] strings) {
//        super.onPostExecute(strings);
//        delegate.onThursdayAfternoonResult(strings);
//    }
//}