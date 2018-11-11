package co.zenpets.doctors.landing.modules;

//public class ProfileFragment extends Fragment
//        implements FetchDoctorProfileInterface,
//        FetchDoctorClinicsInterface {
//
//    private AppPrefs getApp()	{
//        return (AppPrefs) getActivity().getApplication();
//    }
//
//    /** THE DOCTOR'S ID **/
//    String DOCTOR_ID = null;
//
//    /** STRINGS TO HOLD THE DOCTOR'S DATA **/
//    String DOCTOR_PREFIX = null;
//    String DOCTOR_NAME = null;
//    String DOCTOR_EMAIL = null;
//    String DOCTOR_PHONE_PREFIX = null;
//    String DOCTOR_PHONE_NUMBER = null;
//    String DOCTOR_ADDRESS = null;
//    String COUNTRY_ID = null;
//    String COUNTRY_NAME = null;
//    String STATE_ID = null;
//    String STATE_NAME = null;
//    String CITY_ID = null;
//    String CITY_NAME = null;
//    String DOCTOR_GENDER = null;
//    String DOCTOR_SUMMARY = null;
//    String DOCTOR_EXPERIENCE = null;
//    String DOCTOR_CHARGES = null;
//    String CURRENCY_SYMBOL = null;
//    String DOCTOR_DISPLAY_PROFILE = null;
//
//    /** THE CLINICS ARRAY LIST **/
//    ArrayList<ClinicsData> arrClinics = new ArrayList<>();
//
//    /** THE EDUCATION ARRAY LIST **/
//    ArrayList<Qualification> arrEducation = new ArrayList<>();
//
//    /** THE SERVICES ARRAY LIST **/
//    ArrayList<ServiceData> arrServices = new ArrayList<>();
//
//    /** THE SPECIALIZATION ARRAY LIST **/
//    ArrayList<Specialization> arrSpecialization = new ArrayList<>();
//
//    /** THE NEW REQUEST CODES **/
//    private static int NEW_CLINIC_REQUEST = 101;
//    private static int NEW_EDUCATION_REQUEST = 102;
//
//    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
//    @BindView(R.id.imgvwUserProfile) AppCompatImageView imgvwUserProfile;
//    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
//    @BindView(R.id.txtInternalID) AppCompatTextView txtInternalID;
//    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
//    @BindView(R.id.txtPhoneNumber) AppCompatTextView txtPhoneNumber;
//    @BindView(R.id.txtAddress) AppCompatTextView txtAddress;
//    @BindView(R.id.txtGender) AppCompatTextView txtGender;
//    @BindView(R.id.txtSummary) AppCompatTextView txtSummary;
//    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
//    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;
//    @BindView(R.id.linlaClinics) LinearLayout linlaClinics;
//    @BindView(R.id.listClinics) RecyclerView listClinics;
//    @BindView(R.id.linlaNoClinics) LinearLayout linlaNoClinics;
//    @BindView(R.id.linlaEducation) LinearLayout linlaEducation;
//    @BindView(R.id.listEducation) RecyclerView listEducation;
//    @BindView(R.id.linlaNoEducation) LinearLayout linlaNoEducation;
//    @BindView(R.id.linlaServices) LinearLayout linlaServices;
//    @BindView(R.id.listServices) RecyclerView listServices;
//    @BindView(R.id.linlaNoServices) LinearLayout linlaNoServices;
//    @BindView(R.id.linlaSpecialization) LinearLayout linlaSpecialization;
//    @BindView(R.id.listSpecialization) RecyclerView listSpecialization;
//    @BindView(R.id.linlaNoSpecializations) LinearLayout linlaNoSpecializations;
//
//    /** SHOW THE INTERNAL ID INFO **/
//    @OnClick(R.id.imgvwIDInfo) protected void showIDInfo()  {
//        showIDDialog();
//    }
//
//    /** ADD A NEW CLINIC TO THE DOCTOR'S ACCOUNT **/
//    @OnClick(R.id.txtAddClinic) protected void newClinic()  {
//        Intent intent = new Intent(getActivity(), ClinicCreator.class);
//        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
//        startActivityForResult(intent, NEW_CLINIC_REQUEST);
//    }
//
//    /** ADD A NEW EDUCATIONAL QUALIFICATION TO THE DOCTOR'S ACCOUNT **/
//    @OnClick(R.id.txtAddEducation) protected void newEducation()    {
//        Intent intent = new Intent(getActivity(), EducationCreator.class);
//        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
//        startActivityForResult(intent, NEW_EDUCATION_REQUEST);
//    }
//
//    /** ADD A NEW SERVICE TO THE DOCTOR'S ACCOUNT **/
//    @OnClick(R.id.txtAddService) protected void newService()    {
//        /* SHOW THE NEW SERVICE DIALOG */
//        showNewServiceDialog();
//    }
//
//    /** ADD A NEW SPECIALIZATION TO THE DOCTOR'S ACCOUNT **/
//    @OnClick(R.id.txtAddSpecialization) protected void newSpecialisation()  {
//        /* SHOW THE NEW SPECIALIZATION DIALOG */
//        showNewSpecializationDialog();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
//        View view = inflater.inflate(R.layout.home_profile_fragment, container, false);
//        ButterKnife.bind(this, view);
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE */
//        setRetainInstance(true);
//
//        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU */
//        setHasOptionsMenu(true);
//
//        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES */
//        getActivity().invalidateOptionsMenu();
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        /* CONFIGURE THE ACTIONBAR */
//        configAB();
//
//        /* GET THE DOCTOR'S ID */
//        DOCTOR_ID = getApp().getDoctorID();
//
//        /* SHOW THE PROGRESS AND START FETCHING THE DOCTOR'S PROFILE */
//        if (DOCTOR_ID != null)  {
//            linlaProgress.setVisibility(View.VISIBLE);
//            fetchDoctorProfile();
//            new FetchDoctorProfile(this).execute(DOCTOR_ID);
//        }
//
//        /* CONFIGURE THE RECYCLER VIEW **/
//        configRecycler();
//    }
//
//    /***** FETCH THE DOCTOR'S PROFILE *****/
//    private void fetchDoctorProfile() {
//        DoctorProfileAPI apiInterface = ZenApiClient.getClient().create(DoctorProfileAPI.class);
//        retrofit2.Call<DoctorProfileData> call = apiInterface.fetchDoctorProfile(DOCTOR_ID);
//        call.enqueue(new retrofit2.Callback<DoctorProfileData>() {
//            @Override
//            public void onResponse(retrofit2.Call<DoctorProfileData> call, retrofit2.Response<DoctorProfileData> response) {
//                Log.e("RESPONSE", String.valueOf(response));
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<DoctorProfileData> call, Throwable t) {
//            }
//        });
//    }
//
//    @Override
//    public void fetchProfile(String[] strings) {
//        /* CHECK IF RESULTS WERE RETURNED */
//        if (strings.length > 0) {
//            /* GET THE RESULTS */
//            DOCTOR_PREFIX = strings[0];
//            DOCTOR_NAME = strings[1];
//            DOCTOR_EMAIL = strings[2];
//            DOCTOR_PHONE_PREFIX = strings[3];
//            DOCTOR_PHONE_NUMBER = strings[4];
//            DOCTOR_ADDRESS = strings[5];
//            COUNTRY_ID = strings[6];
//            COUNTRY_NAME = strings[7];
//            STATE_ID = strings[8];
//            STATE_NAME = strings[9];
//            CITY_ID = strings[10];
//            CITY_NAME = strings[11];
//            DOCTOR_GENDER = strings[12];
//            DOCTOR_SUMMARY = strings[13];
//            DOCTOR_EXPERIENCE = strings[14];
//            DOCTOR_CHARGES = strings[15];
//            CURRENCY_SYMBOL = strings[16];
//            DOCTOR_DISPLAY_PROFILE = strings[17];
//
//            /* SET THE INTERNAL ID */
//            txtInternalID.setText(getString(R.string.doctor_profile_id_placeholder, DOCTOR_ID));
//
//            /* SET THE DOCTOR'S NAME */
//            txtUserName.setText(getString(R.string.doctor_profile_name_placeholder, DOCTOR_PREFIX, DOCTOR_NAME));
//
//            /* SET THE DOCTOR'S EMAIL ADDRESS  */
//            txtEmailAddress.setText(DOCTOR_EMAIL);
//
//            /* SET THE DOCTOR'S PHONE NUMBER */
//            txtPhoneNumber.setText(getString(R.string.doctor_profile_phone_placeholder, DOCTOR_PHONE_PREFIX, DOCTOR_PHONE_NUMBER));
//
//            /* SET THE DOCTOR'S ADDRESS */
//            txtAddress.setText(getString(R.string.doctor_profile_address_placeholder, DOCTOR_ADDRESS, CITY_NAME, STATE_NAME, COUNTRY_NAME));
//
//            /* SET THE DOCTOR'S GENDER */
//            txtGender.setText(DOCTOR_GENDER);
//
//            /* SET THE DOCTOR'S SUMMARY */
//            txtSummary.setText(DOCTOR_SUMMARY);
//
//            /* SET THE DOCTOR'S EXPERIENCE */
//            txtExperience.setText(getString(R.string.doctor_profile_experience_placeholder, DOCTOR_EXPERIENCE));
//
//            /* SET THE DOCTOR'S CHARGES */
//            txtDoctorCharges.setText(getString(R.string.doctor_profile_charges_placeholder, CURRENCY_SYMBOL, DOCTOR_CHARGES));
//
//            /* SET THE DOCTOR'S DISPLAY PROFILE */
//            Glide.with(getActivity())
//                    .load(DOCTOR_DISPLAY_PROFILE)
//                    .apply(new RequestOptions()
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .centerCrop())
//                    .into(imgvwUserProfile);
//
//            /* FETCH THE DOCTOR'S CLINICS */
//            new FetchDoctorClinics(this).execute(DOCTOR_ID);
//        }
//
//        /* HIDE THE PROGRESS AFTER FETCHING THE DOCTOR'S PROFILE */
//        linlaProgress.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onDoctorClinics(ArrayList<ClinicsData> arrClinics) {
//        /* CAST THE CONTENTS TO THE GLOBAL ARRAY LIST INSTANCE */
//        this.arrClinics = arrClinics;
//
//        if (this.arrClinics.size() > 0) {
//            /* SET THE ADAPTER TO THE RECYCLER VIEW */
//            listClinics.setAdapter(new ClinicsAdapter(getActivity(), arrClinics));
//
//            /* SHOW THE RECYCLER AND HIDE THE EMPTY LAYOUT */
//            linlaClinics.setVisibility(View.VISIBLE);
//            linlaNoClinics.setVisibility(View.GONE);
//        } else {
//            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
//            linlaNoClinics.setVisibility(View.VISIBLE);
//            linlaClinics.setVisibility(View.GONE);
//        }
//
//        /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
//        linlaProgress.setVisibility(View.GONE);
//
//        /* FETCH THE LIST OF EDUCATIONAL QUALIFICATIONS */
//        fetchDoctorEducation();
//    }
//
//    /***** FETCH THE DOCTOR'S EDUCATION *****/
//    private void fetchDoctorEducation() {
//        QualificationsAPI apiInterface = ZenApiClient.getClient().create(QualificationsAPI.class);
//        retrofit2.Call<Qualifications> call = apiInterface.fetchDoctorEducation(DOCTOR_ID);
//        call.enqueue(new retrofit2.Callback<Qualifications>() {
//            @Override
//            public void onResponse(retrofit2.Call<Qualifications> call, retrofit2.Response<Qualifications> response) {
//                arrEducation = response.body().getQualifications();
//                if (arrEducation != null && arrEducation.size() > 0)    {
//                    /* SET THE ADAPTER */
//                    listEducation.setAdapter(new EducationAdapter(getActivity(), arrEducation));
//
//                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
//                    linlaEducation.setVisibility(View.VISIBLE);
//                    linlaNoEducation.setVisibility(View.GONE);
//                } else {
//                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
//                    linlaNoEducation.setVisibility(View.VISIBLE);
//                    linlaEducation.setVisibility(View.GONE);
//                }
//
//                /* FETCH THE LIST OF SERVICES */
//                fetchDoctorServices();
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<Qualifications> call, Throwable t) {
//            }
//        });
//    }
//
//    /***** FETCH THE LIST OF SERVICES *****/
//    private void fetchDoctorServices() {
//        /* CLEAR THE ARRAY LIST */
//        arrServices.clear();
//
//        String URL_DOCTOR_SERVICES = getString(R.string.url_fetch_doctor_services);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTOR_SERVICES).newBuilder();
//        builder.addQueryParameter("doctorID", DOCTOR_ID);
//        String FINAL_URL = builder.build().toString();
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(FINAL_URL)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("FAILURE", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String strResult = response.body().string();
//                    JSONObject JORoot = new JSONObject(strResult);
//                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                        JSONArray JAServices = JORoot.getJSONArray("services");
//                        if (JAServices.length() > 0)    {
//                            ServiceData data;
//                            for (int i = 0; i < JAServices.length(); i++) {
//                                JSONObject JOServices = JAServices.getJSONObject(i);
//                                data = new ServiceData();
//
//                                /* GET THE SERVICE ID */
//                                if (JOServices.has("doctorServiceID"))  {
//                                    data.setDoctorServiceID(JOServices.getString("doctorServiceID"));
//                                } else {
//                                    data.setDoctorServiceID(null);
//                                }
//
//                                /* GET THE SERVICE NAME */
//                                if (JOServices.has("doctorServiceName"))  {
//                                    data.setDoctorServiceName(JOServices.getString("doctorServiceName"));
//                                } else {
//                                    data.setDoctorServiceName(null);
//                                }
//
//                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                                arrServices.add(data);
//                            }
//
//                            /* SHOW THE RECYCLER VIEW, HIDE THE EMPTY LAYOUT AND SET THE ADAPTER */
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    linlaServices.setVisibility(View.VISIBLE);
//                                    linlaNoServices.setVisibility(View.GONE);
//                                    listServices.setAdapter(new ServicesAdapter(arrServices));
//                                }
//                            });
//                        } else {
//                            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    linlaNoServices.setVisibility(View.VISIBLE);
//                                    linlaServices.setVisibility(View.GONE);
//                                }
//                            });
//                        }
//                    } else {
//                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                linlaNoServices.setVisibility(View.VISIBLE);
//                                linlaServices.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        /* FETCH THE LIST OF SPECIALIZATIONS */
//        fetchDoctorSpecializations();
//    }
//
//    /***** FETCH THE LIST OF SPECIALIZATIONS *****/
//    private void fetchDoctorSpecializations() {
//        /* CLEAR THE ARRAY LIST */
//        arrSpecialization.clear();
//
//        String URL_DOCTOR_SPECIALIZATION = getString(R.string.url_fetch_doctor_specializations);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_DOCTOR_SPECIALIZATION).newBuilder();
//        builder.addQueryParameter("doctorID", DOCTOR_ID);
//        String FINAL_URL = builder.build().toString();
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(FINAL_URL)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("FAILURE", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String strResult = response.body().string();
//                    JSONObject JORoot = new JSONObject(strResult);
//                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                        JSONArray JASpecializations = JORoot.getJSONArray("specializations");
//                        if (JASpecializations.length() > 0)    {
//                            Specialization data;
//                            for (int i = 0; i < JASpecializations.length(); i++) {
//                                JSONObject JOSpecializations = JASpecializations.getJSONObject(i);
//                                data = new Specialization();
//
//                                /* GET THE SPECIALIZATION ID */
//                                if (JOSpecializations.has("doctorSpecializationID"))  {
//                                    data.setDoctorSpecializationID(JOSpecializations.getString("doctorSpecializationID"));
//                                } else {
//                                    data.setDoctorSpecializationID(null);
//                                }
//
//                                /* GET THE SPECIALIZATION NAME */
//                                if (JOSpecializations.has("doctorSpecializationName"))  {
//                                    data.setDoctorSpecializationName(JOSpecializations.getString("doctorSpecializationName"));
//                                } else {
//                                    data.setDoctorSpecializationName(null);
//                                }
//
//                                /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                                arrSpecialization.add(data);
//                            }
//
//                            /* SHOW THE RECYCLER VIEW, HIDE THE EMPTY LAYOUT AND SET THE ADAPTER */
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    linlaSpecialization.setVisibility(View.VISIBLE);
//                                    linlaNoSpecializations.setVisibility(View.GONE);
//                                    listSpecialization.setAdapter(new SpecializationAdapter(arrSpecialization));
//                                }
//                            });
//                        } else {
//                            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    linlaNoSpecializations.setVisibility(View.VISIBLE);
//                                    linlaSpecialization.setVisibility(View.GONE);
//                                }
//                            });
//                        }
//                    } else {
//                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                linlaNoSpecializations.setVisibility(View.VISIBLE);
//                                linlaSpecialization.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    /***** CONFIGURE THE ACTIONBAR *****/
//    private void configAB() {
////        String strTitle = getString(R.string.add_a_new_pet);
//        String strTitle = "Profile";
//        SpannableString s = new SpannableString(strTitle);
//        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//        assert actionBar != null;
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(s);
//    }
//
//    /***** SHOW THE INTERNAL ID INFO *****/
//    private void showIDDialog() {
//        new MaterialDialog.Builder(getActivity())
//                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
//                .title("Internal ID")
//                .cancelable(true)
//                .content("The Internal ID is a system generated unique identifier for your account. \n\nIf you need to contact us for assistance, you will quote either this unique ID or your email address.")
//                .positiveText("Dismiss")
//                .theme(Theme.LIGHT)
//                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK)   {
//            if (requestCode == NEW_CLINIC_REQUEST)  {
//                /* CLEAR THE CLINICS ARRAY LIST */
//                arrClinics.clear();
//
//                /* FETCH THE DOCTOR'S CLINICS */
//                new FetchDoctorClinics(this).execute(DOCTOR_ID);
//            } else if (requestCode == NEW_EDUCATION_REQUEST)    {
//                /* CLEAR THE EDUCATION ARRAY LIST */
//                arrEducation.clear();
//
//                /* FETCH THE LIST OF EDUCATIONAL QUALIFICATIONS */
//                fetchDoctorEducation();
//            }
//        }
//    }
//
//    /***** SHOW THE NEW SERVICE DIALOG *****/
//    private void showNewServiceDialog() {
//        new MaterialDialog.Builder(getActivity())
//                .title("New ServiceData")
//                .content("Add a new service offered by this Doctor. \n\nExample 1: Vaccination / Immunization\nExample 2: Pet Counselling\nExample 3: Pet Grooming")
//                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
//                .inputRange(5, 200)
//                .theme(Theme.LIGHT)
//                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                .positiveText("ADD")
//                .negativeText("Cancel")
//                .input("Add a service....", null, false, new MaterialDialog.InputCallback() {
//                    @Override
//                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
//                        String URL_NEW_SERVICE = getString(R.string.url_doctor_new_service);
//                        OkHttpClient client = new OkHttpClient();
//                        RequestBody body = new FormBody.Builder()
//                                .add("doctorID", DOCTOR_ID)
//                                .add("doctorServiceName", input.toString())
//                                .build();
//                        Request request = new Request.Builder()
//                                .url(URL_NEW_SERVICE)
//                                .post(body)
//                                .build();
//                        client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                Log.e("FAILURE", e.toString());
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                try {
//                                    String strResult = response.body().string();
//                                    JSONObject JORoot = new JSONObject(strResult);
//                                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                dialog.dismiss();
//                                                Toast.makeText(getActivity(), "Successfully added a new service", Toast.LENGTH_SHORT).show();
//
//                                                /* CLEAR THE ARRAY LIST */
//                                                arrServices.clear();
//
//                                                /* FETCH THE LIST OF SERVICES AGAIN */
//                                                fetchDoctorServices();
//                                            }
//                                        });
//                                    } else {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(getActivity(), "There was an error adding the new service. Please try again", Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }
//
//    /** SHOW THE NEW SPECIALIZATION DIALOG **/
//    private void showNewSpecializationDialog() {
//        new MaterialDialog.Builder(getActivity())
//                .title("New Specialization")
//                .content("Add a new specialization for this Doctor. \n\nExample 1: Veterinary Physician\n\nExample 2: Veterinary Surgeon")
//                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
//                .inputRange(5, 200)
//                .theme(Theme.LIGHT)
//                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                .positiveText("ADD")
//                .negativeText("Cancel")
//                .input("Add a specialization....", null, false, new MaterialDialog.InputCallback() {
//                    @Override
//                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
//                        /* THE URL TO CREATE A NEW SPECIALIZATION RECORD */
//                        String URL_NEW_SPECIALIZATION = getString(R.string.url_doctor_new_specialization);
//                        OkHttpClient client = new OkHttpClient();
//                        RequestBody body = new FormBody.Builder()
//                                .add("doctorID", DOCTOR_ID)
//                                .add("doctorSpecializationName", input.toString())
//                                .build();
//                        Request request = new Request.Builder()
//                                .url(URL_NEW_SPECIALIZATION)
//                                .post(body)
//                                .build();
//                        client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                Log.e("FAILURE", e.toString());
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                try {
//                                    String strResult = response.body().string();
//                                    JSONObject JORoot = new JSONObject(strResult);
//                                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                dialog.dismiss();
//                                                Toast.makeText(getActivity(), "Successfully added a new specialization", Toast.LENGTH_SHORT).show();
//
//                                                /* CLEAR THE ARRAY LIST */
//                                                arrSpecialization.clear();
//
//                                                /* FETCH THE LIST OF SPECIALIZATIONS AGAIN */
//                                                fetchDoctorSpecializations();
//                                            }
//                                        });
//                                    } else {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(getActivity(), "There was an error adding the new specialization. Please try again", Toast.LENGTH_LONG).show();
//                                            }
//                                        });
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }
//
//    /***** CONFIGURE THE RECYCLER VIEW *****/
//    private void configRecycler() {
//        /* SET THE CLINICS CONFIGURATION */
//        LinearLayoutManager clinics = new LinearLayoutManager(getActivity());
//        clinics.setOrientation(LinearLayoutManager.VERTICAL);
//        clinics.setAutoMeasureEnabled(true);
//        listClinics.setLayoutManager(clinics);
//        listClinics.setHasFixedSize(true);
//        listClinics.setNestedScrollingEnabled(true);
//
//        /* SET THE CLINICS ADAPTER */
//        listClinics.setAdapter(new ClinicsAdapter(getActivity(), arrClinics));
//
//        /* SET THE EDUCATION CONFIGURATION */
//        LinearLayoutManager education = new LinearLayoutManager(getActivity());
//        education.setOrientation(LinearLayoutManager.VERTICAL);
//        education.setAutoMeasureEnabled(true);
//        listEducation.setLayoutManager(education);
//        listEducation.setHasFixedSize(true);
//        listEducation.setNestedScrollingEnabled(true);
//
//        /* SET THE EDUCATIONS ADAPTER */
//        listEducation.setAdapter(new EducationAdapter(getActivity(), arrEducation));
//
//        /* SET THE SERVICES CONFIGURATION */
//        LinearLayoutManager services = new LinearLayoutManager(getActivity());
//        services.setOrientation(LinearLayoutManager.VERTICAL);
//        services.setAutoMeasureEnabled(true);
//        listServices.setLayoutManager(services);
//        listServices.setHasFixedSize(true);
//        listServices.setNestedScrollingEnabled(true);
//
//        /* SET THE SERVICES ADAPTER */
//        listServices.setAdapter(new ServicesAdapter(arrServices));
//
//        /* SET THE SPECIALIZATION CONFIGURATION */
//        LinearLayoutManager specialization = new LinearLayoutManager(getActivity());
//        specialization.setOrientation(LinearLayoutManager.VERTICAL);
//        specialization.setAutoMeasureEnabled(true);
//        listSpecialization.setLayoutManager(specialization);
//        listSpecialization.setHasFixedSize(true);
//        listSpecialization.setNestedScrollingEnabled(true);
//
//        /* SET THE SPECIALIZATION ADAPTER */
//        listSpecialization.setAdapter(new SpecializationAdapter(arrSpecialization));
//    }
//}