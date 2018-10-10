package co.zenpets.users.utils.helpers.doctors.subscription;

//public class FetchDoctorSubscription extends AsyncTask<Object, Void, Boolean> {
//
//    /** THE INTERFACE INSTANCE **/
//    private final FetchDoctorSubscriptionInterface delegate;
//
//    /** A BOOLEAN INSTANCE **/
//    private Boolean blnResult = false;
//
//    public FetchDoctorSubscription(FetchDoctorSubscriptionInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected Boolean doInBackground(Object... objects) {
//        String URL_BOOKMARK_STATUS = AppPrefs.context().getString(R.string.url_doctor_subscription_fetch);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_BOOKMARK_STATUS).newBuilder();
//        builder.addQueryParameter("doctorID", String.valueOf(objects[0]));
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
//                if (JORoot.has("subscriptionID"))  {
//                    String subscriptionID = JORoot.getString("subscriptionID");
//                    blnResult = !subscriptionID.equalsIgnoreCase("1");
//                } else {
//                    blnResult = false;
//                }
//            } else {
//                blnResult = false;
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//
//        return blnResult;
//    }
//
//    @Override
//    protected void onPostExecute(Boolean aBoolean) {
//        super.onPostExecute(aBoolean);
//        delegate.onDoctorSubscription(aBoolean);
//    }
//}