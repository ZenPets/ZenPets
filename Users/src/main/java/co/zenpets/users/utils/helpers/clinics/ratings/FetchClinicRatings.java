package co.zenpets.users.utils.helpers.clinics.ratings;

//public class FetchClinicRatings extends AsyncTask<Object, Void, String> {
//
//    /** THE INTERFACE INSTANCE **/
//    private final FetchClinicRatingsInterface delegate;
//
//    /** A STRING INSTANCE TO HOLD THE CLINIC RATING **/
//    private String CLINIC_RATING = null;
//
//    public FetchClinicRatings(FetchClinicRatingsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String doInBackground(Object... objects) {
//        String URL_DOCTOR_SERVICES = "http://192.168.11.2/zenpets/public/fetchClinicRatings";
//        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTOR_SERVICES).newBuilder();
//        builder.addQueryParameter("clinicID", String.valueOf(objects[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("REVIEWS URL", FINAL_URL);
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
//                if (JORoot.has("clinicRating") && !JORoot.getString("clinicRating").equalsIgnoreCase("null")) {
//                    CLINIC_RATING = JORoot.getString("clinicRating");
//                } else {
//                    CLINIC_RATING = "0.00";
//                }
//            } else {
//                CLINIC_RATING = "0.00";
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return CLINIC_RATING;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        delegate.clinicRatings(s);
//    }
//}