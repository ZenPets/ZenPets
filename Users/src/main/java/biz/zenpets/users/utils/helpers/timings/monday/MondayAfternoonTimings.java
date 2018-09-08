package biz.zenpets.users.utils.helpers.timings.monday;

//public class MondayAfternoonTimings extends AsyncTask<Object, Void, String[]> {
//
//    /** THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY **/
//    private final MondayAfternoonTimingsInterface delegate;
//
//    /** THE AFTERNOON START TIMINGS AND END TIMINGS STRINGS **/
//    private String AFTERNOON_START_TIME = null;
//    private String AFTERNOON_END_TIME = null;
//
//    public MondayAfternoonTimings(MondayAfternoonTimingsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String[] doInBackground(Object... params) {
//        String TIMINGS_URL = AppPrefs.context().getString(R.string.url_timings_mon_aft);
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
//                if (JORoot.has("monAftFrom"))    {
//                    AFTERNOON_START_TIME = JORoot.getString("monAftFrom");
//                } else {
//                    AFTERNOON_START_TIME = null;
//                }
//
//                /* GET THE AFTERNOON END TIME (TO) */
//                if (JORoot.has("monAftTo"))  {
//                    AFTERNOON_END_TIME = JORoot.getString("monAftTo");
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
//        delegate.onMondayAfternoonResult(strings);
//    }
//}