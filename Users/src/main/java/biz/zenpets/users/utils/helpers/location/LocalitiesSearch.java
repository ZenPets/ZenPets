package biz.zenpets.users.utils.helpers.location;

//public class LocalitiesSearch extends AsyncTask<Object, Void, ArrayList<LocalitiesData>> {
//
//    /* THE INTERFACE INSTANCE */
//    private final LocalitiesSearchInterface delegate;
//
//    /* THE LOCALITIES ARRAY LIST */
//    private final ArrayList<LocalitiesData> arrLocalities = new ArrayList<>();
//
//    public LocalitiesSearch(LocalitiesSearchInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<LocalitiesData> doInBackground(Object... objects) {
//        String URL_LOCALITIES_SEARCH = AppPrefs.context().getString(R.string.url_location_localities_search);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_LOCALITIES_SEARCH).newBuilder();
//        builder.addQueryParameter("cityID", String.valueOf(objects[0]));
//        builder.addQueryParameter("localityName", String.valueOf(objects[1]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("LOCALITY", FINAL_URL);
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
//                LocalitiesData data;
//                for (int i = 0; i < JALocalities.length(); i++) {
//                    JSONObject JOLocalities = JALocalities.getJSONObject(i);
////                    Log.e("LOCALITIES", String.valueOf(JOLocalities));
//                    data = new LocalitiesData();
//
//                    /* GET THE LOCALITY ID */
//                    if (JOLocalities.has("localityID")) {
//                        data.setLocalityID(JOLocalities.getString("localityID"));
//                    } else {
//                        data.setLocalityID(null);
//                    }
//
//                    /* GET THE LOCALITY NAME */
//                    if (JOLocalities.has("localityName"))   {
//                        data.setLocalityName(JOLocalities.getString("localityName"));
//                    } else {
//                        data.setLocalityName(null);
//                    }
//
//                    /* GET THE CITY ID */
//                    if (JOLocalities.has("cityID")) {
//                        data.setCityID(JOLocalities.getString("cityID"));
//                    } else {
//                        data.setCityID(null);
//                    }
//
//                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
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
//    protected void onPostExecute(ArrayList<LocalitiesData> data) {
//        super.onPostExecute(data);
//        delegate.localitiesSearch(data);
//    }
//}