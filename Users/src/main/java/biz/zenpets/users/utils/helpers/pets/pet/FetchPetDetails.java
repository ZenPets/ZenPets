package biz.zenpets.users.utils.helpers.pets.pet;

//public class FetchPetDetails extends AsyncTask<Object, Void, String[]> {
//
//    /** THE INTERFACE INSTANCE **/
//    private final FetchPetDetailsInterface delegate;
//
//    /* THE PET DETAIL STRINGS */
//    private String PET_TYPE_ID = null;
//    private String PET_TYP_NAME = null;
//    private String BREED_ID = null;
//    private String BREED_NAME = null;
//    private String PET_NAME = null;
//    private String PET_GENDER = null;
//    private String PET_AGE = null;
//    private String PET_NEUTERED = null;
//    private String PET_DISPLAY_PROFILE = null;
//    private String PET_ACTIVE = null;
//
//    public FetchPetDetails(FetchPetDetailsInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String[] doInBackground(Object... objects) {
//        String URL_PET_DETAILS = AppPrefs.context().getString(R.string.url_pet_details);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_PET_DETAILS).newBuilder();
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
//
//                /* GET THE PET'S PET TYPE ID AND NAME */
//                if (JORoot.has("petTypeID") && JORoot.has("petTypeName")) {
//                    PET_TYPE_ID = JORoot.getString("petTypeID");
//                    PET_TYP_NAME = JORoot.getString("petTypeName");
//                } else {
//                    PET_TYPE_ID = null;
//                    PET_TYP_NAME = null;
//                }
//
//                /* GET THE PET'S BREED ID AND NAME */
//                if (JORoot.has("breedID") && JORoot.has("breedName"))  {
//                    BREED_ID = JORoot.getString("breedID");
//                    BREED_NAME = JORoot.getString("breedName");
//                }
//
//                /* GET THE PET'S NAME  */
//                if (JORoot.has("petName"))    {
//                    PET_NAME = JORoot.getString("petName");
//                } else {
//                    PET_NAME = null;
//                }
//
//                /* GET THE PET'S GENDER */
//                if (JORoot.has("petGender"))  {
//                    PET_GENDER = JORoot.getString("petGender");
//                } else {
//                    PET_GENDER = null;
//                }
//
//                /* GET THE PET'S DOB AND CALCULATE IT'S AGE */
//                if (JORoot.has("petDOB"))   {
//                    String strPetDOB = JORoot.getString("petDOB");
//                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                    try {
//                        /* SET THE DATE OF BIRTH TO A CALENDAR DATE */
//                        Date dtDOB = format.parse(strPetDOB);
//                        Calendar calDOB = Calendar.getInstance();
//                        calDOB.setTime(dtDOB);
//                        int dobYear = calDOB.get(Calendar.YEAR);
//                        int dobMonth = calDOB.get(Calendar.MONTH) + 1;
//                        int dobDate = calDOB.get(Calendar.DATE);
//
//                        /* SET THE CURRENT DATE TO A CALENDAR INSTANCE */
//                        Calendar calNow = Calendar.getInstance();
//                        int nowYear = calNow.get(Calendar.YEAR);
//                        int nowMonth = calNow.get(Calendar.MONTH) + 1;
//                        int nowDate = calNow.get(Calendar.DATE);
//
//                        LocalDate dateDOB = new LocalDate(dobYear, dobMonth, dobDate);
//                        LocalDate dateNOW = new LocalDate(nowYear, nowMonth, nowDate);
//                        Period period = new Period(dateDOB, dateNOW, PeriodType.yearMonthDay());
//                        Resources resources = AppPrefs.context().getResources();
//                        if (period.getYears() == 0)   {
//                            PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
//                        } else if (period.getYears() == 1)    {
//                            PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
//                        } else if (period.getYears() > 1) {
//                            PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    PET_AGE = null;
//                }
//
//                /* GET THE PET'S NEUTERED STATUS */
//                if (JORoot.has("petNeutered")) {
//                    PET_NEUTERED = JORoot.getString("petNeutered");
//                } else {
//                    PET_NEUTERED = null;
//                }
//
//                /* GET THE PET'S DISPLAY PROFILE  */
//                if (JORoot.has("petDisplayProfile")) {
//                    PET_DISPLAY_PROFILE = JORoot.getString("petDisplayProfile");
//                } else {
//                    PET_DISPLAY_PROFILE = null;
//                }
//
//                /* GET THE PET'S ACTIVE STATUS (1 == ALIVE || 0 == DEAD)*/
//                if (JORoot.has("petActive")) {
//                    PET_ACTIVE = JORoot.getString("petActive");
//                } else {
//                    PET_ACTIVE = null;
//                }
//
//            } else {
//                PET_TYPE_ID = null;
//                PET_TYP_NAME = null;
//                BREED_ID = null;
//                BREED_NAME = null;
//                PET_NAME = null;
//                PET_GENDER = null;
//                PET_AGE = null;
//                PET_NEUTERED = null;
//                PET_DISPLAY_PROFILE = null;
//                PET_ACTIVE = null;
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return new String[] {PET_TYPE_ID, PET_TYP_NAME, BREED_ID, BREED_NAME,
//                PET_NAME, PET_GENDER, PET_AGE, PET_NEUTERED, PET_DISPLAY_PROFILE, PET_ACTIVE};
//    }
//
//    @Override
//    protected void onPostExecute(String[] strings) {
//        super.onPostExecute(strings);
//        delegate.petDetails(strings);
//    }
//}