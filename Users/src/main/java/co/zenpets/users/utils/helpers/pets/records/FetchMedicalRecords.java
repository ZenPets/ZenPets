package co.zenpets.users.utils.helpers.pets.records;

//public class FetchMedicalRecords extends AsyncTask<Object, Void, ArrayList<MedicalRecordsData>> {
//
//    /** THE INTERFACE INSTANCE **/
//    FetchMedicalRecordsInterface recordsInterface;
//
//    /** THE MEDICAL RECORDS ARRAY LIST AND RECORD IMAGES ARRAY LIST **/
//    ArrayList<MedicalRecordsData> arrRecords = new ArrayList<>();
//    ArrayList<RecordImagesData> arrImages = new ArrayList<>();
//
//    public FetchMedicalRecords(FetchMedicalRecordsInterface recordsInterface) {
//        this.recordsInterface = recordsInterface;
//    }
//
//    @Override
//    protected ArrayList<MedicalRecordsData> doInBackground(Object... objects) {
//        /* GET THE RECORD TYPE ID */
//        String RECORD_TYPE_ID = String.valueOf(objects[1]);
//
//        String URL_RECORDS = AppPrefs.context().getString(R.string.url_all_medical_records);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_RECORDS).newBuilder();
//        builder.addQueryParameter("petID", String.valueOf(objects[0]));
//        if (RECORD_TYPE_ID != null && !RECORD_TYPE_ID.equalsIgnoreCase("null")) {
//            builder.addQueryParameter("recordTypeID", RECORD_TYPE_ID);
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
//                JSONArray JARecords = JORoot.getJSONArray("records");
//                MedicalRecordsData data;
//                for (int i = 0; i < JARecords.length(); i++) {
//                    JSONObject JORecords = JARecords.getJSONObject(i);
//                    data = new MedicalRecordsData();
//
//                    /* GET THE MEDICAL RECORD ID */
//                    if (JORecords.has("medicalRecordID"))    {
//                        data.setMedicalRecordID(JORecords.getString("medicalRecordID"));
//                    } else {
//                        data.setMedicalRecordID(null);
//                    }
//
//                    /* GET THE RECORD TYPE ID */
//                    if (JORecords.has("recordTypeID"))    {
//                        data.setRecordTypeID(JORecords.getString("recordTypeID"));
//                    } else {
//                        data.setRecordTypeID(null);
//                    }
//
//                    /* GET THE USER ID */
//                    if (JORecords.has("userID"))  {
//                        data.setUserID(JORecords.getString("userID"));
//                    } else {
//                        data.setUserID(null);
//                    }
//
//                    /* GET THE PET ID */
//                    if (JORecords.has("petID"))    {
//                        data.setPetID(JORecords.getString("petID"));
//                    } else {
//                        data.setPetID(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD NOTES */
//                    if (JORecords.has("medicalRecordNotes")) {
//                        data.setMedicalRecordNotes(JORecords.getString("medicalRecordNotes"));
//                    } else {
//                        data.setMedicalRecordNotes(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD DATE */
//                    if (JORecords.has("medicalRecordDate"))  {
//                        data.setMedicalRecordDate(JORecords.getString("medicalRecordDate"));
//                    } else {
//                        data.setMedicalRecordDate(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD DATE */
//                    if (JORecords.has("recordTypeName"))  {
//                        data.setRecordTypeName(JORecords.getString("recordTypeName"));
//                    } else {
//                        data.setRecordTypeName(null);
//                    }
//
//                    /* GET THE MEDICAL RECORD IMAGES */
//                    arrImages = new ArrayList<>();
//                    String URL_RECORD_IMAGES = AppPrefs.context().getString(R.string.url_medical_record_images);
//                    HttpUrl.Builder builderImages = HttpUrl.parse(URL_RECORD_IMAGES).newBuilder();
//                    builderImages.addQueryParameter("medicalRecordID", JORecords.getString("medicalRecordID"));
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
//                        RecordImagesData images;
//                        for (int j = 0; j < JAImages.length(); j++) {
//                            JSONObject JOImages = JAImages.getJSONObject(j);
//                            images = new RecordImagesData();
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
//                        data.setImages(arrImages);
//                    }
//
//                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                    arrRecords.add(data);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return arrRecords;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<MedicalRecordsData> medicalRecordsData) {
//        super.onPostExecute(medicalRecordsData);
//        recordsInterface.onMedicalRecords(medicalRecordsData);
//    }
//}