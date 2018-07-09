package biz.zenpets.users.utils.helpers.doctors.doctors;

//public class FetchDoctors extends AsyncTask<Object, Void, ArrayList<Doctor>> {
//
//    /* THE INTERFACE INSTANCE */
//    private final FetchDoctorsInterface delegate;
//
//    /* THE DOCTOR ARRAY LIST AND CLINIC IMAGES ARRAY LIST INSTANCES */
//    private final ArrayList<Doctor> arrDoctors = new ArrayList<>();
//    private final ArrayList<ClinicImage> arrImages = new ArrayList<>();
//
//    public FetchDoctors(FetchDoctorsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<Doctor> doInBackground(Object... objects) {
////        String URL_DOCTORS_LIST = AppPrefs.context().getString(R.string.url_doctors_list);
//        String URL_DOCTORS_LIST = AppPrefs.context().getString(R.string.url_doctors_list_test);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTORS_LIST).newBuilder();
//        builder.addQueryParameter("cityID", String.valueOf(objects[0]));
////        builder.addQueryParameter("localityID", String.valueOf(objects[1]));
//        builder.addQueryParameter("pageNumber", String.valueOf(objects[2]));
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
//                JSONArray JAClinics = JORoot.getJSONArray("doctors");
//                if (JAClinics.length() > 0) {
//                    Doctor data;
//
//                    for (int i = 0; i < JAClinics.length(); i++) {
//                        JSONObject JOClinics = JAClinics.getJSONObject(i);
////                        Log.e("DOCTOR", String.valueOf(JOClinics));
//                        data = new Doctor();
//
//                        /* GET THE CLINIC ID */
//                        String clinicID = JOClinics.getString("clinicID");
//                        data.setClinicID(clinicID);
//
//                        /* GET THE CLINIC NAME */
//                        String clinicName = JOClinics.getString("clinicName");
//                        data.setClinicName(clinicName);
//
//                        /* GET THE CLINIC ADDRESS */
//                        String clinicAddress = JOClinics.getString("clinicAddress");
//                        data.setClinicAddress(clinicAddress);
//
//                        /* GET THE CLINIC PIN CODE */
//                        if (JOClinics.has("clinicPinCode")) {
//                            data.setClinicPinCode(JOClinics.getString("clinicPinCode"));
//                        }
//
//                        /* GET THE CITY NAME */
//                        if (JOClinics.has("cityName"))  {
//                            data.setCityName(JOClinics.getString("cityName"));
//                        }
//
//                        /* GET THE CLINIC LATITUDE AND LONGITUDE */
//                        String clinicLatitude = JOClinics.getString("clinicLatitude");
//                        String clinicLongitude = JOClinics.getString("clinicLongitude");
//                        if (clinicLatitude != null
//                                && !clinicLatitude.equalsIgnoreCase("0")
//                                && clinicLongitude != null
//                                && !clinicLongitude.equalsIgnoreCase("0"))    {
//
//                            /* GET THE ORIGIN (USER) */
//                            String originLat = String.valueOf(objects[3]);
//                            String originLng = String.valueOf(objects[4]);
//                            LatLng LATLNG_ORIGIN = new LatLng(Double.valueOf(originLat), Double.valueOf(originLng));
//
//                            /* GET THE DESTINATION (CLINIC) */
//                            Double latitude = Double.valueOf(clinicLatitude);
//                            Double longitude = Double.valueOf(clinicLongitude);
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
//                                data.setClinicDistance(JODistance.getString("text"));
//                            } else {
//                                data.setClinicDistance("Unknown");
//                            }
//                        } else {
//                            data.setClinicDistance("Unknown");
//                        }
//
//                        /* GET THE DOCTOR ID */
//                        String doctorID = JOClinics.getString("doctorID");
//                        data.setDoctorID(doctorID);
//
//                        /* GET THE DOCTOR'S PREFIX AND NAME */
//                        String doctorPrefix = JOClinics.getString("doctorPrefix");
//                        String doctorName = JOClinics.getString("doctorName");
//                        data.setDoctorPrefix(doctorPrefix);
//                        data.setDoctorName(doctorName);
////                        Log.e("PREFIX", doctorPrefix);
////                        Log.e("NAME", doctorName);
////                        data.setDoctorName(doctorPrefix + " " + doctorName);
//
//                        /* GET THE DOCTOR'S DISPLAY PROFILE */
//                        String doctorDisplayProfile = JOClinics.getString("doctorDisplayProfile");
//                        data.setDoctorDisplayProfile(doctorDisplayProfile);
//
//                        /* GET THE DOCTOR'S EXPERIENCE */
//                        String doctorExperience = JOClinics.getString("doctorExperience");
//                        data.setDoctorExperience(doctorExperience);
//
//                        /* GET THE CURRENCY CODE AND THE DOCTOR'S CHARGES */
//                        String currencySymbol = JOClinics.getString("currencySymbol");
//                        String doctorCharges = JOClinics.getString("doctorCharges");
//                        data.setCurrencySymbol(currencySymbol);
//                        data.setDoctorCharges(doctorCharges);
////                        data.setDoctorCharges(currencySymbol + " " + doctorCharges);
//
//                        /* THE TOTAL VOTES, TOTAL LIKES AND TOTAL DISLIKES */
//                        int TOTAL_VOTES = 0;
//                        int TOTAL_LIKES = 0;
//
//                        /* GET THE POSITIVE REVIEWS / FEEDBACK FOR THE DOCTORS */
//                        String URL_POSITIVE_REVIEWS = AppPrefs.context().getString(R.string.url_doctor_positive_reviews);
//                        HttpUrl.Builder builderPositive = HttpUrl.parse(URL_POSITIVE_REVIEWS).newBuilder();
//                        builderPositive.addQueryParameter("doctorID", doctorID);
//                        builderPositive.addQueryParameter("recommendStatus", "Yes");
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
//                        /* GET THE POSITIVE REVIEWS / FEEDBACK FOR THE DOCTORS */
//                        String URL_DOCTOR_REVIEWS = AppPrefs.context().getString(R.string.url_doctor_negative_reviews);
//                        HttpUrl.Builder builderNegative = HttpUrl.parse(URL_DOCTOR_REVIEWS).newBuilder();
//                        builderNegative.addQueryParameter("doctorID", doctorID);
//                        builderNegative.addQueryParameter("recommendStatus", "No");
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
//                        data.setDoctorLikes(String.valueOf(TOTAL_LIKES));
//
//                        /* CALCULATE THE PERCENTAGE OF LIKES */
//                        double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
//                        int finalPercentLikes = (int)percentLikes;
//                        data.setDoctorLikesPercent(String.valueOf(finalPercentLikes) + "%");
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
//                        data.setDoctorVotes(reviewQuantity);
//
//                        String URL_CLINIC_IMAGES = AppPrefs.context().getString(R.string.url_clinic_fetch_images);
//                        HttpUrl.Builder builderImages = HttpUrl.parse(URL_CLINIC_IMAGES).newBuilder();
//                        builderImages.addQueryParameter("clinicID", clinicID);
//                        String FINAL_URL_IMAGES = builderImages.build().toString();
////                        Log.e("IMAGES", FINAL_URL_IMAGES);
//                        OkHttpClient clientImages = new OkHttpClient();
//                        Request requestImages = new Request.Builder()
//                                .url(FINAL_URL_IMAGES)
//                                .build();
//                        Call callImages = clientImages.newCall(requestImages);
//                        Response responseImages = callImages.execute();
//                        String strResultImages = responseImages.body().string();
//                        JSONObject JORootImages = new JSONObject(strResultImages);
//                        if (JORootImages.has("error") && JORootImages.getString("error").equalsIgnoreCase("false")) {
//                            JSONArray JAImages = JORootImages.getJSONArray("images");
//                            ClinicImage images;
//                            for (int j = 0; j < JAImages.length(); j++) {
//                                JSONObject JOImages = JAImages.getJSONObject(j);
//                                images = new ClinicImage();
//
//                                /* GET THE IMAGE ID */
//                                if (JOImages.has("imageID"))    {
//                                    images.setImageID(JOImages.getString("imageID"));
//                                } else {
//                                    images.setImageID(null);
//                                }
//
//                                /* GET THE CLINIC ID */
//                                if (JOImages.has("clinicID"))   {
//                                    images.setClinicID(JOImages.getString("clinicID"));
//                                } else {
//                                    images.setClinicID(null);
//                                }
//
//                                /* GET THE CLINIC IMAGE URL */
//                                if (JOImages.has("imageURL"))   {
//                                    images.setImageURL(JOImages.getString("imageURL"));
//                                } else {
//                                    images.setImageURL(null);
//                                }
//
//                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                                arrImages.add(images);
//                            }
//                            data.setImages(arrImages);
////                            arrImages = new ArrayList<>();
//                        }
//
//                        /* ADD THE GATHERED DATA TO THE ARRAY LIST */
//                        arrDoctors.add(data);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return arrDoctors;
//    }
//
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
//
//    @Override
//    protected void onPostExecute(ArrayList<Doctor> doctorsData) {
//        super.onPostExecute(doctorsData);
//        delegate.onDoctorsList(doctorsData);
//    }
//}