package co.zenpets.doctors.utils.helpers.location;

//public class FetchCityID extends AsyncTask<Object, Void, String> {
//
//    /* THE INTERFACE INSTANCE */
//    FetchCityIDInterface delegate;
//
//    /* THE CITY ID */
//    String CITY_ID = null;
//
//    public FetchCityID(FetchCityIDInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String doInBackground(Object... objects) {
//        String URL_CITY_ID = AppPrefs.context().getString(R.string.url_fetch_city_id);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_CITY_ID).newBuilder();
//        builder.addQueryParameter("cityName", String.valueOf(objects[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("CITY", FINAL_URL);
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
//                if (JORoot.has("cityID")) {
//                    CITY_ID = JORoot.getString("cityID");
//                } else {
//                    CITY_ID = null;
//                }
//            } else {
//                /* ERROR FETCHING RESULT */
//                CITY_ID = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return CITY_ID;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        delegate.onCityID(s);
//    }
//}