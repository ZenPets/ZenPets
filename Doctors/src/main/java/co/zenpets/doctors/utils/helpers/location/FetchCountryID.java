package biz.zenpets.doctors.utils.helpers.location;

//public class FetchCountryID extends AsyncTask<Object, Void, String> {
//
//    /* THE INTERFACE INSTANCE */
//    FetchCountryIDInterface delegate;
//
//    /* THE COUNTRY ID */
//    String COUNTRY_ID = null;
//
//    public FetchCountryID(FetchCountryIDInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String doInBackground(Object... objects) {
//        String URL_COUNTRY_ID = AppPrefs.context().getString(R.string.url_fetch_country_id);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_COUNTRY_ID).newBuilder();
//        builder.addQueryParameter("countryName", String.valueOf(objects[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("COUNTRY", FINAL_URL);
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
//                String city = JORoot.getString("country");
//                if (city.equalsIgnoreCase("") || city.equalsIgnoreCase("null") || city.equalsIgnoreCase("[]")) {
//                    /* ERROR FETCHING RESULT */
//                    COUNTRY_ID = null;
//                } else {
//                    JSONArray JACity = JORoot.getJSONArray("country");
//                    for (int i = 0; i < JACity.length(); i++) {
//                        JSONObject JOCountry = JACity.getJSONObject(i);
//
//                        /* GET THE COUNTRY ID */
//                        if (JOCountry.has("countryID"))   {
//                            COUNTRY_ID = JOCountry.getString("countryID");
//                        }  else {
//                            /* ERROR FETCHING RESULT */
//                            COUNTRY_ID = null;
//                        }
//                    }
//                }
//            } else {
//                /* ERROR FETCHING RESULT */
//                COUNTRY_ID = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return COUNTRY_ID;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        delegate.onCountryID(s);
//    }
//}