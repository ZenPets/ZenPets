package co.zenpets.users.utils.helpers.clinics.images;

//public class FetchClinicImages extends AsyncTask<Object, Void, ArrayList<ClinicImagesData>> {
//
//    /* THE INTERFACE INSTANCE */
//    private final FetchClinicImagesInterface delegate;
//
//    /* THE ARRAY LIST INSTANCE */
//    private final ArrayList<ClinicImagesData> arrImages = new ArrayList<>();
//
//    public FetchClinicImages(FetchClinicImagesInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<ClinicImagesData> doInBackground(Object... objects) {
//        String URL_CLINIC_IMAGES = AppPrefs.context().getString(R.string.url_clinic_fetch_images);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_CLINIC_IMAGES).newBuilder();
//        builder.addQueryParameter("clinicID", String.valueOf(objects[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("IMAGES", FINAL_URL);
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
//                JSONArray JAImages = JORoot.getJSONArray("images");
//                ClinicImagesData data;
//                for (int i = 0; i < JAImages.length(); i++) {
//                    JSONObject JOImages = JAImages.getJSONObject(i);
//                    data = new ClinicImagesData();
//
//                    /* GET THE IMAGE ID */
//                    if (JOImages.has("imageID"))    {
//                        data.setImageID(JOImages.getString("imageID"));
//                    } else {
//                        data.setImageID(null);
//                    }
//
//                    /* GET THE CLINIC ID */
//                    if (JOImages.has("clinicID"))   {
//                        data.setClinicID(JOImages.getString("clinicID"));
//                    } else {
//                        data.setClinicID(null);
//                    }
//
//                    /* GET THE CLINIC IMAGE URL */
//                    if (JOImages.has("imageURL"))   {
//                        data.setImageURL(JOImages.getString("imageURL"));
//                    } else {
//                        data.setImageURL(null);
//                    }
//
//                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                    arrImages.add(data);
//                }
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return arrImages;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<ClinicImagesData> data) {
//        super.onPostExecute(data);
//        delegate.onClinicImages(data);
//    }
//}