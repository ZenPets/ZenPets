package biz.zenpets.users.utils.helpers.timings;

//public class FetchDoctorTimings extends AsyncTask<Object, Void, String[]> {
//
//    /** THE INTERFACE INSTANCE **/
//    private final FetchDoctorTimingsInterface delegate;
//
//    /** THE TIMING STRINGS **/
//    private String SUN_MOR_FROM = null;
//    private String SUN_MOR_TO = null;
//    private String SUN_AFT_FROM = null;
//    private String SUN_AFT_TO = null;
//    private String MON_MOR_FROM = null;
//    private String MON_MOR_TO = null;
//    private String MON_AFT_FROM = null;
//    private String MON_AFT_TO = null;
//    private String TUE_MOR_FROM = null;
//    private String TUE_MOR_TO = null;
//    private String TUE_AFT_FROM = null;
//    private String TUE_AFT_TO = null;
//    private String WED_MOR_FROM = null;
//    private String WED_MOR_TO = null;
//    private String WED_AFT_FROM = null;
//    private String WED_AFT_TO = null;
//    private String THU_MOR_FROM = null;
//    private String THU_MOR_TO = null;
//    private String THU_AFT_FROM = null;
//    private String THU_AFT_TO = null;
//    private String FRI_MOR_FROM = null;
//    private String FRI_MOR_TO = null;
//    private String FRI_AFT_FROM = null;
//    private String FRI_AFT_TO = null;
//    private String SAT_MOR_FROM = null;
//    private String SAT_MOR_TO = null;
//    private String SAT_AFT_FROM = null;
//    private String SAT_AFT_TO = null;
//
//    public FetchDoctorTimings(FetchDoctorTimingsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String[] doInBackground(Object... objects) {
//        String TIMINGS_URL = AppPrefs.context().getString(R.string.url_doctor_timings);
//        HttpUrl.Builder builder = HttpUrl.parse(TIMINGS_URL).newBuilder();
//        builder.addQueryParameter("doctorID", String.valueOf(objects[0]));
//        builder.addQueryParameter("clinicID", String.valueOf(objects[1]));
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
//                /* GET THE SUNDAY MORNING TIMINGS */
//                if (JORoot.has("sunMorFrom") && JORoot.has("sunMorTo")) {
//                    if (!JORoot.getString("sunMorFrom").equalsIgnoreCase("null") && !JORoot.getString("sunMorTo").equalsIgnoreCase("null"))  {
//                        SUN_MOR_FROM = JORoot.getString("sunMorFrom");
//                        SUN_MOR_TO = JORoot.getString("sunMorTo");
//                    } else {
//                        SUN_MOR_FROM = null;
//                        SUN_MOR_TO = null;
//                    }
//                } else {
//                    SUN_MOR_FROM = null;
//                    SUN_MOR_TO = null;
//                }
//
//                /* GET THE SUNDAY AFTERNOON TIMINGS */
//                if (JORoot.has("sunAftFrom") && JORoot.has("sunAftTo")) {
//                    if (!JORoot.getString("sunAftFrom").equalsIgnoreCase("null") && !JORoot.getString("sunAftTo").equalsIgnoreCase("null"))  {
//                        SUN_AFT_FROM = JORoot.getString("sunAftFrom");
//                        SUN_AFT_TO = JORoot.getString("sunAftTo");
//                    } else {
//                        SUN_AFT_FROM = null;
//                        SUN_AFT_TO = null;
//                    }
//                } else {
//                    SUN_AFT_FROM = null;
//                    SUN_AFT_TO = null;
//                }
//
//                /* GET THE MONDAY MORNING TIMINGS */
//                if (JORoot.has("monMorFrom") && JORoot.has("monMorTo")) {
//                    if (!JORoot.getString("monMorFrom").equalsIgnoreCase("null") && !JORoot.getString("monMorTo").equalsIgnoreCase("null"))  {
//                        MON_MOR_FROM = JORoot.getString("monMorFrom");
//                        MON_MOR_TO = JORoot.getString("monMorTo");
//                    } else {
//                        MON_MOR_FROM = null;
//                        MON_MOR_TO = null;
//                    }
//                } else {
//                    MON_MOR_FROM = null;
//                    MON_MOR_TO = null;
//                }
//
//                /* GET THE MONDAY AFTERNOON TIMINGS */
//                if (JORoot.has("monAftFrom") && JORoot.has("monAftTo")) {
//                    if (!JORoot.getString("monAftFrom").equalsIgnoreCase("null") && !JORoot.getString("monAftTo").equalsIgnoreCase("null"))  {
//                        MON_AFT_FROM = JORoot.getString("monAftFrom");
//                        MON_AFT_TO = JORoot.getString("monAftTo");
//                    } else {
//                        MON_AFT_FROM = null;
//                        MON_AFT_TO = null;
//                    }
//                } else {
//                    MON_AFT_FROM = null;
//                    MON_AFT_TO = null;
//                }
//
//                /* GET THE TUESDAY MORNING TIMINGS */
//                if (JORoot.has("tueMorFrom") && JORoot.has("tueMorTo")) {
//                    if (!JORoot.getString("tueMorFrom").equalsIgnoreCase("null") && !JORoot.getString("tueMorTo").equalsIgnoreCase("null"))  {
//                        TUE_MOR_FROM = JORoot.getString("tueMorFrom");
//                        TUE_MOR_TO = JORoot.getString("tueMorTo");
//                    } else {
//                        TUE_MOR_FROM = null;
//                        TUE_MOR_TO = null;
//                    }
//                } else {
//                    TUE_MOR_FROM = null;
//                    TUE_MOR_TO = null;
//                }
//
//                /* GET THE TUESDAY AFTERNOON TIMINGS */
//                if (JORoot.has("tueAftFrom") && JORoot.has("tueAftTo")) {
//                    if (!JORoot.getString("tueAftFrom").equalsIgnoreCase("null") && !JORoot.getString("tueAftTo").equalsIgnoreCase("null"))  {
//                        TUE_AFT_FROM = JORoot.getString("tueAftFrom");
//                        TUE_AFT_TO = JORoot.getString("tueAftTo");
//                    } else {
//                        TUE_AFT_FROM = null;
//                        TUE_AFT_TO = null;
//                    }
//                } else {
//                    TUE_AFT_FROM = null;
//                    TUE_AFT_TO = null;
//                }
//
//                /* GET THE WEDNESDAY MORNING TIMINGS */
//                if (JORoot.has("wedMorFrom") && JORoot.has("wedMorTo")) {
//                    if (!JORoot.getString("wedMorFrom").equalsIgnoreCase("null") && !JORoot.getString("wedMorTo").equalsIgnoreCase("null"))  {
//                        WED_MOR_FROM = JORoot.getString("wedMorFrom");
//                        WED_MOR_TO = JORoot.getString("wedMorTo");
//                    } else {
//                        WED_MOR_FROM = null;
//                        WED_MOR_TO = null;
//                    }
//                } else {
//                    WED_MOR_FROM = null;
//                    WED_MOR_TO = null;
//                }
//
//                /* GET THE WEDNESDAY AFTERNOON TIMINGS */
//                if (JORoot.has("wedAftFrom") && JORoot.has("wedAftTo")) {
//                    if (!JORoot.getString("wedAftFrom").equalsIgnoreCase("null") && !JORoot.getString("wedAftTo").equalsIgnoreCase("null"))  {
//                        WED_AFT_FROM = JORoot.getString("wedAftFrom");
//                        WED_AFT_TO = JORoot.getString("wedAftTo");
//                    } else {
//                        WED_AFT_FROM = null;
//                        WED_AFT_TO = null;
//                    }
//                } else {
//                    WED_AFT_FROM = null;
//                    WED_AFT_TO = null;
//                }
//
//                /* GET THE THURSDAY MORNING TIMINGS */
//                if (JORoot.has("thuMorFrom") && JORoot.has("thuMorTo")) {
//                    if (!JORoot.getString("thuMorFrom").equalsIgnoreCase("null") && !JORoot.getString("thuMorTo").equalsIgnoreCase("null"))  {
//                        THU_MOR_FROM = JORoot.getString("thuMorFrom");
//                        THU_MOR_TO = JORoot.getString("thuMorTo");
//                    } else {
//                        THU_MOR_FROM = null;
//                        THU_MOR_TO = null;
//                    }
//                } else {
//                    THU_MOR_FROM = null;
//                    THU_MOR_TO = null;
//                }
//
//                /* GET THE THURSDAY AFTERNOON TIMINGS */
//                if (JORoot.has("thuAftFrom") && JORoot.has("thuAftTo")) {
//                    if (!JORoot.getString("thuAftFrom").equalsIgnoreCase("null") && !JORoot.getString("thuAftTo").equalsIgnoreCase("null"))  {
//                        THU_AFT_FROM = JORoot.getString("thuAftFrom");
//                        THU_AFT_TO = JORoot.getString("thuAftTo");
//                    } else {
//                        THU_AFT_FROM = null;
//                        THU_AFT_TO = null;
//                    }
//                } else {
//                    THU_AFT_FROM = null;
//                    THU_AFT_TO = null;
//                }
//
//                /* GET THE FRIDAY MORNING TIMINGS */
//                if (JORoot.has("friMorFrom") && JORoot.has("friMorTo")) {
//                    if (!JORoot.getString("friMorFrom").equalsIgnoreCase("null") && !JORoot.getString("friMorTo").equalsIgnoreCase("null"))  {
//                        FRI_MOR_FROM = JORoot.getString("friMorFrom");
//                        FRI_MOR_TO = JORoot.getString("friMorTo");
//                    } else {
//                        FRI_MOR_FROM = null;
//                        FRI_MOR_TO = null;
//                    }
//                } else {
//                    FRI_MOR_FROM = null;
//                    FRI_MOR_TO = null;
//                }
//
//                /* GET THE FRIDAY AFTERNOON TIMINGS */
//                if (JORoot.has("friAftFrom") && JORoot.has("friAftTo")) {
//                    if (!JORoot.getString("friAftFrom").equalsIgnoreCase("null") && !JORoot.getString("friAftTo").equalsIgnoreCase("null"))  {
//                        FRI_AFT_FROM = JORoot.getString("friAftFrom");
//                        FRI_AFT_TO = JORoot.getString("friAftTo");
//                    } else {
//                        FRI_AFT_FROM = null;
//                        FRI_AFT_TO = null;
//                    }
//                } else {
//                    FRI_AFT_FROM = null;
//                    FRI_AFT_TO = null;
//                }
//
//                /* GET THE SATURDAY MORNING TIMINGS */
//                if (JORoot.has("satMorFrom") && JORoot.has("satMorTo")) {
//                    if (!JORoot.getString("satMorFrom").equalsIgnoreCase("null") && !JORoot.getString("satMorTo").equalsIgnoreCase("null"))  {
//                        SAT_MOR_FROM = JORoot.getString("satMorFrom");
//                        SAT_MOR_TO = JORoot.getString("satMorTo");
//                    } else {
//                        SAT_MOR_FROM = null;
//                        SAT_MOR_TO = null;
//                    }
//                } else {
//                    SAT_MOR_FROM = null;
//                    SAT_MOR_TO = null;
//                }
//
//                /* GET THE SATURDAY AFTERNOON TIMINGS */
//                if (JORoot.has("satAftFrom") && JORoot.has("satAftTo")) {
//                    if (!JORoot.getString("satAftFrom").equalsIgnoreCase("null") && !JORoot.getString("satAftTo").equalsIgnoreCase("null"))  {
//                        SAT_AFT_FROM = JORoot.getString("satAftFrom");
//                        SAT_AFT_TO = JORoot.getString("satAftTo");
//                    } else {
//                        SAT_AFT_FROM = null;
//                        SAT_AFT_TO = null;
//                    }
//                } else {
//                    SAT_AFT_FROM = null;
//                    SAT_AFT_TO = null;
//                }
//
////                JSONArray JATimings = JORoot.getJSONArray("timings");
////                if (JATimings.length() > 0) {
////                    for (int i = 0; i < JATimings.length(); i++) {
////                        JSONObject JOTimings = JATimings.getJSONObject(i);
////                    }
////                } else {
////                    SUN_MOR_FROM = null;
////                    SUN_MOR_TO = null;
////                    SUN_AFT_FROM = null;
////                    SUN_AFT_TO = null;
////                    MON_MOR_FROM = null;
////                    MON_MOR_TO = null;
////                    MON_AFT_FROM = null;
////                    MON_AFT_TO = null;
////                    TUE_MOR_FROM = null;
////                    TUE_MOR_TO = null;
////                    TUE_AFT_FROM = null;
////                    TUE_AFT_TO = null;
////                    WED_MOR_FROM = null;
////                    WED_MOR_TO = null;
////                    WED_AFT_FROM = null;
////                    WED_AFT_TO = null;
////                    THU_MOR_FROM = null;
////                    THU_MOR_TO = null;
////                    THU_AFT_FROM = null;
////                    THU_AFT_TO = null;
////                    FRI_MOR_FROM = null;
////                    FRI_MOR_TO = null;
////                    FRI_AFT_FROM = null;
////                    FRI_AFT_TO = null;
////                    SAT_MOR_FROM = null;
////                    SAT_MOR_TO = null;
////                    SAT_AFT_FROM = null;
////                    SAT_AFT_TO = null;
////                }
//            } else {
//                SUN_MOR_FROM = null;
//                SUN_MOR_TO = null;
//                SUN_AFT_FROM = null;
//                SUN_AFT_TO = null;
//                MON_MOR_FROM = null;
//                MON_MOR_TO = null;
//                MON_AFT_FROM = null;
//                MON_AFT_TO = null;
//                TUE_MOR_FROM = null;
//                TUE_MOR_TO = null;
//                TUE_AFT_FROM = null;
//                TUE_AFT_TO = null;
//                WED_MOR_FROM = null;
//                WED_MOR_TO = null;
//                WED_AFT_FROM = null;
//                WED_AFT_TO = null;
//                THU_MOR_FROM = null;
//                THU_MOR_TO = null;
//                THU_AFT_FROM = null;
//                THU_AFT_TO = null;
//                FRI_MOR_FROM = null;
//                FRI_MOR_TO = null;
//                FRI_AFT_FROM = null;
//                FRI_AFT_TO = null;
//                SAT_MOR_FROM = null;
//                SAT_MOR_TO = null;
//                SAT_AFT_FROM = null;
//                SAT_AFT_TO = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return new String[] {
//                SUN_MOR_FROM, SUN_MOR_TO, SUN_AFT_FROM, SUN_AFT_TO,
//                MON_MOR_FROM, MON_MOR_TO, MON_AFT_FROM, MON_AFT_TO,
//                TUE_MOR_FROM, TUE_MOR_TO, TUE_AFT_FROM, TUE_AFT_TO,
//                WED_MOR_FROM, WED_MOR_TO, WED_AFT_FROM, WED_AFT_TO,
//                THU_MOR_FROM, THU_MOR_TO, THU_AFT_FROM, THU_AFT_TO,
//                FRI_MOR_FROM, FRI_MOR_TO, FRI_AFT_FROM, FRI_AFT_TO,
//                SAT_MOR_FROM, SAT_MOR_TO, SAT_AFT_FROM, SAT_AFT_TO
//        };
//    }
//
//    @Override
//    protected void onPostExecute(String[] strings) {
//        super.onPostExecute(strings);
//        delegate.onDoctorTimings(strings);
//    }
//}