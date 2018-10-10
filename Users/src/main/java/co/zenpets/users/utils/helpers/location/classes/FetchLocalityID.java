package co.zenpets.users.utils.helpers.location.classes;

//public class FetchLocalityID extends AsyncTask<Object, Void, String> {
//
//    /* THE INTERFACE INSTANCE */
//    private final FetchLocalityIDInterface delegate;
//
//    /* THE LOCALITY ID */
//    private String LOCALITY_ID = null;
//
//    public FetchLocalityID(FetchLocalityIDInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String doInBackground(Object... objects) {
//        String URL_LOCALITY_ID = AppPrefs.context().getString(R.string.url_location_locality_id);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_LOCALITY_ID).newBuilder();
//        builder.addQueryParameter("localityName", String.valueOf(objects[0]));
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
//            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false"))  {
//                if (JORoot.has("localityID"))   {
//                    LOCALITY_ID = JORoot.getString("localityID");
//                } else {
//                    LOCALITY_ID = null;
//                }
//            } else  {
//                /* ERROR FETCHING RESULT */
//                LOCALITY_ID = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return LOCALITY_ID;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        delegate.onLocalityID(s);
//    }
//}