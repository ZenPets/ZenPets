package co.zenpets.doctors.utils.helpers.location;

//public class GetAllLocalities extends AsyncTask<Object, Void, ArrayList<LocalityData>> {
//
//    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
//    final GetAllLocalitiesInterface delegate;
//
//    /* THE COUNTRIES ARRAY LIST */
//    ArrayList<LocalityData> arrLocalities = new ArrayList<>();
//
//    public GetAllLocalities(GetAllLocalitiesInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<LocalityData> doInBackground(Object... params) {
//        String LOCALITIES_URL = AppPrefs.context().getString(R.string.url_fetch_all_localities);
//        HttpUrl.Builder builder = HttpUrl.parse(LOCALITIES_URL).newBuilder();
//        builder.addQueryParameter("cityID", String.valueOf(params[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("STATES URL", FINAL_URL);
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
//                JSONArray JALocalities = JORoot.getJSONArray("localities");
//
//                /* A LOCALITIES DATA INSTANCE */
//                LocalityData data;
//
//                for (int i = 0; i < JALocalities.length(); i++) {
//                    JSONObject JOLocalities = JALocalities.getJSONObject(i);
//
//                    /* INSTANTIATE THE LOCALITIES DATA POJO INSTANCE */
//                    data = new LocalityData();
//
//                    /* GET THE LOCALITY ID */
//                    if (JOLocalities.has("localityID"))  {
//                        String localityID = JOLocalities.getString("localityID");
//                        data.setLocalityID(localityID);
//                    } else {
//                        data.setLocalityID(null);
//                    }
//
//                    /* GET THE LOCALITY NAME */
//                    if (JOLocalities.has("localityName"))   {
//                        String localityName = JOLocalities.getString("localityName");
//                        data.setLocalityName(localityName);
//                    } else {
//                        data.setLocalityName(null);
//                    }
//
//                    /* GET THE CITY ID */
//                    if (JOLocalities.has("cityID"))    {
//                        String cityID = JOLocalities.getString("cityID");
//                        data.setCityID(cityID);
//                    } else {
//                        data.setCityID(null);
//                    }
//
//                    /* ADD THE COLLECTED INFO TO THE ARRAY LIST */
//                    arrLocalities.add(data);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return arrLocalities;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<LocalityData> localitiesData) {
//        super.onPostExecute(localitiesData);
//        delegate.onLocalitiesResult(localitiesData);
//    }
//}