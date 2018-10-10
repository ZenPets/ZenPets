package co.zenpets.users.utils.helpers.pets.prescriptions;

//public class FetchPrescriptions extends AsyncTask<Object, Void, ArrayList<Prescription>> {
//
//    /** THE INTERFACE INSTANCE **/
//    FetchPrescriptionsInterface delegate;
//
//    /** VACCINATION AND VACCINATION IMAGES ARRAY LIST INSTANCES **/
//    private ArrayList<Prescription> arrPrescriptions = new ArrayList<>();
//    private ArrayList<PrescriptionImagesData> arrImages = new ArrayList<>();
//
//    public FetchPrescriptions(FetchPrescriptionsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<Prescription> doInBackground(Object... objects) {
//        String URL_PRESCRIPTIONS = AppPrefs.context().getString(R.string.url_all_prescriptions);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_PRESCRIPTIONS).newBuilder();
//        builder.addQueryParameter("petID", String.valueOf(objects[0]));
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
//                JSONArray JAPrescriptions = JORoot.getJSONArray("prescriptions");
//                Prescription data;
//                for (int i = 0; i < JAPrescriptions.length(); i++) {
//                    JSONObject JOPrescriptions = JAPrescriptions.getJSONObject(i);
//                    data = new Prescription();
//
//                    /* GET THE MEDICAL RECORD ID */
//                    if (JOPrescriptions.has("medicalRecordID"))    {
//                        data.setMedicalRecordID(JOPrescriptions.getString("medicalRecordID"));
//                    } else {
//                        data.setMedicalRecordID(null);
//                    }
//
//                    /* GET THE RECORD TYPE ID */
//                    if (JOPrescriptions.has("recordTypeID"))    {
//                        data.setRecordTypeID(JOPrescriptions.getString("recordTypeID"));
//                    } else {
//                        data.setRecordTypeID(null);
//                    }
//
//                    /* GET THE USER ID */
//                    if (JOPrescriptions.has("userID"))  {
//                        data.setUserID(JOPrescriptions.getString("userID"));
//                    } else {
//                        data.setUserID(null);
//                    }
//
//                    /* GET THE PET ID */
//                    if (JOPrescriptions.has("petID"))    {
//                        data.setPetID(JOPrescriptions.getString("petID"));
//                    } else {
//                        data.setPetID(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD NOTES */
//                    if (JOPrescriptions.has("medicalRecordNotes")) {
//                        data.setMedicalRecordNotes(JOPrescriptions.getString("medicalRecordNotes"));
//                    } else {
//                        data.setMedicalRecordNotes(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD DATE */
//                    if (JOPrescriptions.has("medicalRecordDate"))  {
//                        data.setMedicalRecordDate(JOPrescriptions.getString("medicalRecordDate"));
//                    } else {
//                        data.setMedicalRecordDate(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD IMAGES */
//                    arrImages = new ArrayList<>();
//                    String URL_RECORD_IMAGES = AppPrefs.context().getString(R.string.url_medical_record_images);
//                    HttpUrl.Builder builderImages = HttpUrl.parse(URL_RECORD_IMAGES).newBuilder();
//                    builderImages.addQueryParameter("medicalRecordID", JOPrescriptions.getString("medicalRecordID"));
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
//                        PrescriptionImagesData images;
//                        for (int j = 0; j < JAImages.length(); j++) {
//                            JSONObject JOImages = JAImages.getJSONObject(j);
//                            images = new PrescriptionImagesData();
//
//                            /* GET THE RECORD IMAGE ID */
//                            if (JOImages.has("recordImageID"))    {
//                                images.setRecordImageID(JOImages.getString("recordImageID"));
//                            } else {
//                                images.setRecordImageID(null);
//                            }
//
//                            /* GET THE MEDICAL RECORD ID */
//                            if (JOImages.has("medicalRecordID"))   {
//                                images.setMedicalRecordID(JOImages.getString("medicalRecordID"));
//                            } else {
//                                images.setMedicalRecordID(null);
//                            }
//
//                            /* GET THE RECORD IMAGE URL */
//                            if (JOImages.has("recordImageURL"))   {
//                                images.setRecordImageURL(JOImages.getString("recordImageURL"));
//                            } else {
//                                images.setRecordImageURL(null);
//                            }
//
//                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                            arrImages.add(images);
//                        }
////                        data.setImages(arrImages);
//                    }
//
//                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                    arrPrescriptions.add(data);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return arrPrescriptions;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<Prescription> prescriptionsData) {
//        super.onPostExecute(prescriptionsData);
//        delegate.onPrescriptions(prescriptionsData);
//    }
//}