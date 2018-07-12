package biz.zenpets.users.utils.helpers.kennels.classes;

//public class FetchKennels extends AsyncTask<Object, Void, ArrayList<Kennel>> {
//
//    /* THE INTERFACE INSTANCE */
//    private final FetchKennelsInterface delegate;
//
//    /* A KENNELS ARRAY LIST INSTANCE */
//    private final ArrayList<Kennel> arrKennels = new ArrayList<>();
//
//    public FetchKennels(FetchKennelsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<Kennel> doInBackground(Object... objects) {
//        String strUrl = AppPrefs.context().getString(R.string.url_kennels_list);
//        HttpUrl.Builder builder = HttpUrl.parse(strUrl).newBuilder();
//        builder.addQueryParameter("cityID", String.valueOf(objects[0]));
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
//                JSONArray JAKennels = JORoot.getJSONArray("kennels");
//                if (JAKennels.length() > 0) {
//                    Kennel data;
//                    for (int i = 0; i < JAKennels.length(); i++) {
//                        JSONObject JOKennels = JAKennels.getJSONObject(i);
//                        data = new Kennel();
//
//                        /* GET THE KENNEL ID */
//                        if (JOKennels.has("kennelID"))  {
//                            data.setKennelID(JOKennels.getString("kennelID"));
//                        } else {
//                            data.setKennelID(null);
//                        }
//
//                        /* GET THE KENNEL COVER PHOTO */
//                        if (JOKennels.has("kennelCoverPhoto")
//                                && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("")
//                                && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("null"))  {
//                            data.setKennelCoverPhoto(JOKennels.getString("kennelCoverPhoto"));
//                        } else {
//                            data.setKennelCoverPhoto(null);
//                        }
//
//                        /* GET THE KENNEL NAME */
//                        if (JOKennels.has("kennelName"))    {
//                            data.setKennelName(JOKennels.getString("kennelName"));
//                        } else {
//                            data.setKennelName(null);
//                        }
//
//                        /* GET THE KENNEL OWNER'S ID */
//                        if (JOKennels.has("kennelOwnerID")) {
//                            data.setKennelOwnerID(JOKennels.getString("kennelOwnerID"));
//                        } else {
//                            data.setKennelOwnerID(null);
//                        }
//
//                        /* GET THE KENNEL OWNER'S NAME */
//                        if (JOKennels.has("kennelOwnerName"))   {
//                            data.setKennelOwnerName(JOKennels.getString("kennelOwnerName"));
//                        } else {
//                            data.setKennelOwnerName(null);
//                        }
//
//                        /* GET THE KENNEL OWNER'S DISPLAY PROFILE */
//                        if (JOKennels.has("kennelOwnerDisplayProfile")) {
//                            data.setKennelOwnerDisplayProfile(JOKennels.getString("kennelOwnerDisplayProfile"));
//                        } else {
//                            data.setKennelOwnerDisplayProfile(null);
//                        }
//
//                        /* GET THE KENNEL ADDRESS */
//                        if (JOKennels.has("kennelAddress")) {
//                            data.setKennelAddress(JOKennels.getString("kennelAddress"));
//                        } else {
//                            data.setKennelAddress(null);
//                        }
//
//                        /* GET THE KENNEL PIN CODE */
//                        if (JOKennels.has("kennelPinCode")) {
//                            data.setKennelPinCode(JOKennels.getString("kennelPinCode"));
//                        } else {
//                            data.setKennelPinCode(null);
//                        }
//
//                        /* GET THE KENNEL COUNTY ID */
//                        if (JOKennels.has("countryID")) {
//                            data.setCountryID(JOKennels.getString("countryID"));
//                        } else {
//                            data.setCountryID(null);
//                        }
//
//                        /* GET THE KENNEL COUNTRY NAME */
//                        if (JOKennels.has("countryName"))   {
//                            data.setCountryName(JOKennels.getString("countryName"));
//                        } else {
//                            data.setCountryName(null);
//                        }
//
//                        /* GET THE KENNEL STATE ID */
//                        if (JOKennels.has("stateID"))   {
//                            data.setStateID(JOKennels.getString("stateID"));
//                        } else {
//                            data.setStateID(null);
//                        }
//
//                        /* GET THE KENNEL STATE NAME */
//                        if (JOKennels.has("stateName")) {
//                            data.setStateName(JOKennels.getString("stateName"));
//                        } else {
//                            data.setStateName(null);
//                        }
//
//                        /* GET THE KENNEL CITY ID */
//                        if (JOKennels.has("cityID"))    {
//                            data.setCityID(JOKennels.getString("cityID"));
//                        } else {
//                            data.setCityID(null);
//                        }
//
//                        /* GET THE KENNEL CITY NAME */
//                        if (JOKennels.has("cityName"))  {
//                            data.setCityName(JOKennels.getString("cityName"));
//                        } else {
//                            data.setCityName(null);
//                        }
//
//                        /* GET THE KENNEL LATITUDE */
//                        if (JOKennels.has("kennelLatitude"))    {
//                            data.setKennelLatitude(JOKennels.getString("kennelLatitude"));
//                        } else {
//                            data.setKennelLatitude(null);
//                        }
//
//                        /* GET THE KENNEL LONGITUDE */
//                        if (JOKennels.has("kennelLongitude"))   {
//                            data.setKennelLongitude(JOKennels.getString("kennelLongitude"));
//                        } else {
//                            data.setKennelLongitude(null);
//                        }
//
//                        /* GET THE CLINIC LATITUDE AND LONGITUDE */
//                        String kennelLatitude = JOKennels.getString("kennelLatitude");
//                        String kennelLongitude = JOKennels.getString("kennelLongitude");
//                        if (kennelLatitude != null
//                                && !kennelLatitude.equalsIgnoreCase("0")
//                                && kennelLongitude != null
//                                && !kennelLongitude.equalsIgnoreCase("0"))    {
//
//                            /* GET THE ORIGIN (USER) */
//                            String originLat = String.valueOf(objects[1]);
//                            String originLng = String.valueOf(objects[2]);
//                            LatLng LATLNG_ORIGIN = new LatLng(Double.valueOf(originLat), Double.valueOf(originLng));
////                            Log.e("LAT LNG", String.valueOf(LATLNG_ORIGIN));
//
//                            /* GET THE DESTINATION (CLINIC) */
//                            Double latitude = Double.valueOf(kennelLatitude);
//                            Double longitude = Double.valueOf(kennelLongitude);
//                            LatLng LATLNG_DESTINATION = new LatLng(latitude, longitude);
//                            String URL_DISTANCE = getUrl(LATLNG_ORIGIN, LATLNG_DESTINATION);
//                            OkHttpClient clientDistance = new OkHttpClient();
//                            Request requestDistance = new Request.Builder()
//                                    .url(URL_DISTANCE)
//                                    .build();
//                            Call callDistance = clientDistance.newCall(requestDistance);
//                            Response respDistance = callDistance.execute();
//                            String strDistance = respDistance.body().string();
////                            Log.e("DISTANCE", strDistance);
//                            JSONObject JORootDistance = new JSONObject(strDistance);
//                            JSONArray array = JORootDistance.getJSONArray("routes");
//                            JSONObject JORoutes = array.getJSONObject(0);
//                            JSONArray JOLegs= JORoutes.getJSONArray("legs");
//                            JSONObject JOSteps = JOLegs.getJSONObject(0);
//                            JSONObject JODistance = JOSteps.getJSONObject("distance");
//                            if (JODistance.has("text")) {
//                                data.setKennelDistance(JODistance.getString("text"));
//                            } else {
//                                data.setKennelDistance("Unknown");
//                            }
//                        } else {
//                            data.setKennelDistance("Unknown");
//                        }
//
//                        /* GET THE KENNEL'S PHONE PREFIX #1*/
//                        if (JOKennels.has("kennelPhonePrefix1"))    {
//                            data.setKennelPhonePrefix1(JOKennels.getString("kennelPhonePrefix1"));
//                        } else {
//                            data.setKennelPhonePrefix1(null);
//                        }
//
//                        /* GET THE KENNEL'S PHONE NUMBER #1*/
//                        if (JOKennels.has("kennelPhoneNumber1"))    {
//                            data.setKennelPhoneNumber1(JOKennels.getString("kennelPhoneNumber1"));
//                        } else {
//                            data.setKennelPhoneNumber1(null);
//                        }
//
//                        /* GET THE KENNEL'S PHONE PREFIX #2*/
//                        if (JOKennels.has("kennelPhonePrefix2"))    {
//                            data.setKennelPhonePrefix2(JOKennels.getString("kennelPhonePrefix2"));
//                        } else {
//                            data.setKennelPhonePrefix2(null);
//                        }
//
//                        /* GET THE KENNEL'S PHONE NUMBER #2*/
//                        if (JOKennels.has("kennelPhoneNumber2"))    {
//                            data.setKennelPhoneNumber2(JOKennels.getString("kennelPhoneNumber2"));
//                        } else {
//                            data.setKennelPhoneNumber1(null);
//                        }
//
//                        /* GET THE KENNEL'S PET CAPACITY */
//                        if (JOKennels.has("kennelPetCapacity"))    {
//                            data.setKennelPetCapacity(JOKennels.getString("kennelPetCapacity"));
//                        } else {
//                            data.setKennelPetCapacity(null);
//                        }
//
//                        /* THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES */
//                        int TOTAL_VOTES = 0;
//                        int TOTAL_LIKES = 0;
//
//                        /* GET THE POSITIVE REVIEWS / FEEDBACK */
//                        String URL_POSITIVE_REVIEWS = AppPrefs.context().getString(R.string.url_positive_kennel_reviews);
//                        HttpUrl.Builder builderPositive = HttpUrl.parse(URL_POSITIVE_REVIEWS).newBuilder();
//                        builderPositive.addQueryParameter("kennelID", data.getKennelID());
//                        builderPositive.addQueryParameter("kennelRecommendStatus", "Yes");
//                        String FINAL_URL_POSITIVE = builderPositive.build().toString();
//                        OkHttpClient clientPositive = new OkHttpClient();
//                        Request requestPositive = new Request.Builder()
//                                .url(FINAL_URL_POSITIVE)
//                                .build();
//                        Call callPositive = clientPositive.newCall(requestPositive);
//                        Response responsePositive = callPositive.execute();
//                        String strPositiveReview = responsePositive.body().string();
//                        JSONObject JORootPositive = new JSONObject(strPositiveReview);
//                        if (JORootPositive.has("error") && JORootPositive.getString("error").equalsIgnoreCase("false")) {
//                            JSONArray JAPositiveReviews = JORootPositive.getJSONArray("reviews");
//                            TOTAL_LIKES = JAPositiveReviews.length();
//                            TOTAL_VOTES = TOTAL_VOTES + JAPositiveReviews.length();
//                        }
//
//                        /* GET THE POSITIVE REVIEWS / FEEDBACK */
//                        String URL_DOCTOR_REVIEWS = AppPrefs.context().getString(R.string.url_doctor_negative_reviews);
//                        HttpUrl.Builder builderNegative = HttpUrl.parse(URL_DOCTOR_REVIEWS).newBuilder();
//                        builderNegative.addQueryParameter("kennelID", data.getKennelID());
//                        builderNegative.addQueryParameter("kennelRecommendStatus", "No");
//                        String FINAL_URL_Negative = builderNegative.build().toString();
//                        OkHttpClient clientNegative = new OkHttpClient();
//                        Request requestReviews = new Request.Builder()
//                                .url(FINAL_URL_Negative)
//                                .build();
//                        Call reviewCall = clientNegative.newCall(requestReviews);
//                        Response responseNegative = reviewCall.execute();
//                        String strNegativeReview = responseNegative.body().string();
//                        JSONObject JORootNegative = new JSONObject(strNegativeReview);
//                        if (JORootNegative.has("error") && JORootNegative.getString("error").equalsIgnoreCase("false")) {
//                            JSONArray JANegativeReviews = JORootNegative.getJSONArray("reviews");
//                            TOTAL_VOTES = TOTAL_VOTES + JANegativeReviews.length();
//                        }
//
//                        /* GET THE TOTAL LIKES */
//                        data.setKennelLikes(String.valueOf(TOTAL_LIKES));
//
//                        /* CALCULATE THE PERCENTAGE OF LIKES */
//                        double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
//                        int finalPercentLikes = (int)percentLikes;
//                        data.setKennelLikesPercent(String.valueOf(finalPercentLikes) + "%");
//
//                        /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
//                        Resources resReviews = AppPrefs.context().getResources();
//                        String reviewQuantity = null;
//                        if (TOTAL_VOTES == 0)   {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        } else if (TOTAL_VOTES == 1)    {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        } else if (TOTAL_VOTES > 1) {
//                            reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                        }
//                        data.setKennelVotes(reviewQuantity);
//
//                        /* ADD THE GATHERED DATA TO THE ARRAY LIST */
//                        arrKennels.add(data);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return arrKennels;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<Kennel> kennels) {
//        super.onPostExecute(kennels);
//        delegate.onKennels(kennels);
//    }
//
//    /** CONCATENATE THE URL TO THE GOOGLE MAPS API FOR GETTING THE DISTANCE **/
//    private String getUrl(LatLng origin, LatLng destination) {
//        /* ORIGIN OF THE ROUTE */
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//
//        /* DESTINATION OF THE ROUTE */
//        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
//
//        /* SENSOR ENABLED */
//        String sensor = "sensor=false&key=" + AppPrefs.context().getString(R.string.google_directions_api_key);
//
//        /* BUILDING THE PARAMETERS FOR THE WEB SERVICE */
//        String parameters = str_origin + "&" + str_dest + "&" + sensor;
//
//        /* THE OUTPUT FORMAT */
//        String output = "json";
//
//        /* BUILD THE FINAL URL FOR THE WEB SERVICE */
//        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//    }
//}