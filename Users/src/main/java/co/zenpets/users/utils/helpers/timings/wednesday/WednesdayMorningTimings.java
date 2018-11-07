package co.zenpets.users.utils.helpers.timings.wednesday;

//public class WednesdayMorningTimings extends AsyncTask<Object, Void, String[]> {
//
//    /** THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY **/
//    private final WednesdayMorningTimingsInterface delegate;
//
//    /** THE MORNING START TIMINGS AND END TIMINGS STRINGS **/
//    private String MORNING_START_TIME = null;
//    private String MORNING_END_TIME = null;
//
//    public WednesdayMorningTimings(WednesdayMorningTimingsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String[] doInBackground(Object... params) {
//        String TIMINGS_URL = AppPrefs.context().getString(R.string.url_timings_wed_mor);
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
//                /* GET THE MORNING START TIME (FROM) */
//                if (JORoot.has("wedMorFrom"))    {
//                    MORNING_START_TIME = JORoot.getString("wedMorFrom");
//                } else {
//                    MORNING_START_TIME = null;
//                }
//
//                /* GET THE MORNING END TIME (TO) */
//                if (JORoot.has("wedMorTo"))  {
//                    MORNING_END_TIME = JORoot.getString("wedMorTo");
//                } else {
//                    MORNING_END_TIME = null;
//                }
//            } else {
//                MORNING_START_TIME = null;
//                MORNING_END_TIME = null;
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return new String[] {MORNING_START_TIME, MORNING_END_TIME};
//    }
//
//    @Override
//    protected void onPostExecute(String[] strings) {
//        super.onPostExecute(strings);
//        delegate.onWednesdayMorningResult(strings);
//    }
//}