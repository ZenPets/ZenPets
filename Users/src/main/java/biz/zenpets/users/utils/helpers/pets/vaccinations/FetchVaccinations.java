package biz.zenpets.users.utils.helpers.pets.vaccinations;

//public class FetchVaccinations extends AsyncTask<Object, Void, ArrayList<VaccinationsData>> {
//
//    /** AN INTERFACE INSTANCE **/
//    private FetchVaccinationsInterface delegate;
//
//    /** VACCINATION AND VACCINATION IMAGES ARRAY LIST INSTANCES **/
//    private ArrayList<VaccinationsData> arrVaccinations = new ArrayList<>();
//    private ArrayList<VaccinationImages> arrImages = new ArrayList<>();
//
//    public FetchVaccinations(FetchVaccinationsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<VaccinationsData> doInBackground(Object... objects) {
//        /* GET THE VACCINE ID */
//        String VACCINE_ID = String.valueOf(objects[1]);
//
//        String URL_DOCTORS_LIST = AppPrefs.context().getString(R.string.url_all_vaccinations);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTORS_LIST).newBuilder();
//        builder.addQueryParameter("petID", String.valueOf(objects[0]));
//        if (VACCINE_ID != null && !VACCINE_ID.equalsIgnoreCase("null")) {
//            builder.addQueryParameter("vaccineID", VACCINE_ID);
//        }
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
//                JSONArray JAVaccinations = JORoot.getJSONArray("vaccinations");
//                VaccinationsData data;
//                for (int i = 0; i < JAVaccinations.length(); i++) {
//                    JSONObject JOVaccinations = JAVaccinations.getJSONObject(i);
//                    data = new VaccinationsData();
//
//                    /* GET THE VACCINATION ID */
//                    if (JOVaccinations.has("vaccinationID"))    {
//                        data.setVaccinationID(JOVaccinations.getString("vaccinationID"));
//                    } else {
//                        data.setVaccinationID(null);
//                    }
//
//                    /* GET THE PET ID */
//                    if (JOVaccinations.has("petID"))    {
//                        data.setPetID(JOVaccinations.getString("petID"));
//                    } else {
//                        data.setPetID(null);
//                    }
//
//                    /* GET THE VACCINE ID */
//                    if (JOVaccinations.has("vaccineID"))    {
//                        data.setVaccineID(JOVaccinations.getString("vaccineID"));
//                    } else {
//                        data.setVaccineID(null);
//                    }
//
//                    /* GET THE VACCINE NAME */
//                    if (JOVaccinations.has("vaccineName"))  {
//                        data.setVaccineName(JOVaccinations.getString("vaccineName"));
//                    } else {
//                        data.setVaccineName(null);
//                    }
//
//                    /* GET THE VACCINATION DATE */
//                    if (JOVaccinations.has("vaccinationDate"))  {
//                        data.setVaccinationDate(JOVaccinations.getString("vaccinationDate"));
//                    } else {
//                        data.setVaccinationDate(null);
//                    }
//
//                    /* GET THE NEXT VACCINATION DATE */
//                    if (JOVaccinations.has("vaccinationNextDate"))  {
//                        data.setVaccinationNextDate(JOVaccinations.getString("vaccinationNextDate"));
//                    } else {
//                        data.setVaccinationNextDate(null);
//                    }
//
//                    /* GET THE VACCINATION REMINDER */
//                    if (JOVaccinations.has("vaccinationReminder"))  {
//                        data.setVaccinationReminder(JOVaccinations.getString("vaccinationReminder"));
//                    } else {
//                        data.setVaccinationReminder(null);
//                    }
//
//                    /* GET THE VACCINATION NOTES */
//                    if (JOVaccinations.has("vaccinationNotes")) {
//                        data.setVaccinationNotes(JOVaccinations.getString("vaccinationNotes"));
//                    } else {
//                        data.setVaccinationNotes(null);
//                    }
//
//                    /* GET THE VACCINATION IMAGES */
//                    arrImages = new ArrayList<>();
//                    String URL_CLINIC_IMAGES = AppPrefs.context().getString(R.string.url_vaccination_images);
//                    HttpUrl.Builder builderImages = HttpUrl.parse(URL_CLINIC_IMAGES).newBuilder();
//                    builderImages.addQueryParameter("vaccinationID", JOVaccinations.getString("vaccinationID"));
//                    String FINAL_URL_IMAGES = builderImages.build().toString();
//                    OkHttpClient clientImages = new OkHttpClient();
//                    Request requestImages = new Request.Builder()
//                            .url(FINAL_URL_IMAGES)
//                            .build();
//                    Call callImages = clientImages.newCall(requestImages);
//                    Response responseImages = callImages.execute();
//                    String strResultImages = responseImages.body().string();
//                    JSONObject JORootImages = new JSONObject(strResultImages);
//                    if (JORootImages.has("error") && JORootImages.getString("error").equalsIgnoreCase("false")) {
//                        JSONArray JAImages = JORootImages.getJSONArray("images");
//                        VaccinationImages images;
//                        for (int j = 0; j < JAImages.length(); j++) {
//                            JSONObject JOImages = JAImages.getJSONObject(j);
//                            images = new VaccinationImages();
//
//                            /* GET THE VACCINATION IMAGE ID */
//                            if (JOImages.has("vaccinationImageID"))    {
//                                images.setVaccinationImageID(JOImages.getString("vaccinationImageID"));
//                            } else {
//                                images.setVaccinationImageID(null);
//                            }
//
//                            /* GET THE VACCINATION ID */
//                            if (JOImages.has("vaccinationID"))   {
//                                images.setVaccinationID(JOImages.getString("vaccinationID"));
//                            } else {
//                                images.setVaccinationID(null);
//                            }
//
//                            /* GET THE VACCINATION IMAGE URL */
//                            if (JOImages.has("vaccinationImageURL"))   {
//                                images.setVaccinationImageURL(JOImages.getString("vaccinationImageURL"));
//                            } else {
//                                images.setVaccinationImageURL(null);
//                            }
//
//                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                            arrImages.add(images);
//                        }
//                        data.setImages(arrImages);
//                    }
//
//                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                    arrVaccinations.add(data);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return arrVaccinations;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<VaccinationsData> vaccinationsData) {
//        super.onPostExecute(vaccinationsData);
//        delegate.onVaccinations(vaccinationsData);
//    }
//}