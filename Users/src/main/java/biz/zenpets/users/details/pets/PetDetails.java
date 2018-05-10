package biz.zenpets.users.details.pets;

//public class PetDetails extends AppCompatActivity {
//
//    /** THE INCOMING PET ID **/
//    private String PET_ID = null;
//
//    /** A MENU INSTANCE **/
//    private Menu menu;
//
//    /** THE PET DETAILS **/
//    private String PET_TYPE_ID = null;
//    private String PET_TYPE_NAME = null;
//    private String BREED_ID = null;
//    private String BREED_NAME = null;
//    private String PET_NAME = null;
//    private String PET_GENDER = null;
//    private String PET_DOB = null;
//    private String PET_AGE = null;
//    private String PET_NEUTERED = null;
//    private String PET_DISPLAY_PROFILE = null;
//
//    /** THE VACCINATIONS AND MEDICAL RECORDS ARRAY LISTS **/
//    ArrayList<VaccinationsData> arrVaccinations = new ArrayList<>();
//    ArrayList<MedicalRecord> arrRecords = new ArrayList<>();
//
//    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
//    @BindView(R.id.appBar) AppBarLayout appBar;
//    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout toolbarLayout;
//    @BindView(R.id.imgvwPetProfile) SimpleDraweeView imgvwPetProfile;
//    @BindView(R.id.txtPetBreed) AppCompatTextView txtPetBreed;
//    @BindView(R.id.txtPetAge) AppCompatTextView txtPetAge;
//    @BindView(R.id.txtPetGender) AppCompatTextView txtPetGender;
//    @BindView(R.id.txtNeutered) AppCompatTextView txtNeutered;
//    @BindView(R.id.linlaVaccinationsProgress) LinearLayout linlaVaccinationsProgress;
//    @BindView(R.id.listVaccinations) RecyclerView listVaccinations;
//    @BindView(R.id.linlaEmptyVaccinations) LinearLayout linlaEmptyVaccinations;
//    @BindView(R.id.linlaRecordsProgress) LinearLayout linlaRecordsProgress;
//    @BindView(R.id.listMedicalRecords) RecyclerView listMedicalRecords;
//    @BindView(R.id.linlaEmptyMedicalRecords) LinearLayout linlaEmptyMedicalRecords;
//
//    /** SHOW ALL VACCINATION RECORDS **/
//    @OnClick(R.id.txtViewVaccination) void allVaccinations()    {
//        Intent intent = new Intent(PetDetails.this, AllVaccinationsActivity.class);
//        intent.putExtra("PET_ID", PET_ID);
//        startActivity(intent);
//    }
//
//    /** ADD A NEW VACCINATION RECORD **/
//    @OnClick(R.id.txtNewVaccination) void newVaccination()  {
//        Intent intent = new Intent(PetDetails.this, VaccinationCreator.class);
//        intent.putExtra("PET_ID", PET_ID);
//        startActivityForResult(intent, 101);
//    }
//
//    /** SHOW ALL MEDICAL RECORDS **/
//    @OnClick(R.id.txtViewRecords) void allRecords() {
//    }
//
//    /** ADD A NEW MEDICAL RECORD **/
//    @OnClick(R.id.txtNewRecord) void newRecord()    {
//        Intent intent = new Intent(PetDetails.this, MedicalRecordCreator.class);
//        intent.putExtra("PET_ID", PET_ID);
//        startActivityForResult(intent, 102);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pet_details);
//        ButterKnife.bind(this);
//
//        /* CONFIGURE THE RECYCLER VIEW */
//        configRecycler();
//
//        /* GET THE INCOMING DATA */
//        getIncomingData();
//
//        /* CONFIGURE THE APP BAR LAYOUT */
//        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//
//            /* BOOLEAN TO TRACK IF TOOLBAR IS SHOWING */
//            boolean isShow = false;
//
//            /* SCROLL RANGE */
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1)  {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    isShow = true;
//                    showOption(R.id.menuEdit);
//                } else if (isShow) {
//                    isShow = false;
//                    hideOption(R.id.menuEdit);
//                }
//            }
//        });
//
//        /* CONFIGURE THE TOOLBAR */
//        configTB();
//    }
//
//    /***** FETCH THE PET'S DETAILS *****/
//    private void fetchPetDetails() {
//        PetsAPI api = ZenApiClient.getClient().create(PetsAPI.class);
//        Call<Pet> call = api.fetchPetDetails(PET_ID);
//        call.enqueue(new Callback<Pet>() {
//            @Override
//            public void onResponse(Call<Pet> call, Response<Pet> response) {
//                if (response.isSuccessful())    {
//                    Pet pet = response.body();
//                    if (pet != null)    {
//                        /* GET THE PET TYPE ID AND NAME */
//                        PET_TYPE_ID = pet.getPetTypeID();
//                        PET_TYPE_NAME = pet.getPetTypeName();
//
//                        /* GET AND SET THE BREED ID AND NAME */
//                        BREED_ID = pet.getBreedID();
//                        BREED_NAME = pet.getBreedName();
//                        txtPetBreed.setText(BREED_NAME);
//
//                        /* GET AND SET THE PET'S NAME */
//                        PET_NAME = pet.getPetName();
//                        toolbarLayout.setTitleEnabled(true);
//                        toolbarLayout.setTitle(PET_NAME);
//
//                        /* GET AND SET THE PET'S GENDER */
//                        PET_GENDER = pet.getPetGender();
//                        txtPetGender.setText(PET_GENDER);
//
//                        /* GET THE PET'S DOB AND CALCULATE IT'S AGE */
//                        PET_DOB = pet.getPetDOB();
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                        try {
//                            /* SET THE DATE OF BIRTH TO A CALENDAR DATE */
//                            Date dtDOB = format.parse(PET_DOB);
//                            Calendar calDOB = Calendar.getInstance();
//                            calDOB.setTime(dtDOB);
//                            int dobYear = calDOB.get(Calendar.YEAR);
//                            int dobMonth = calDOB.get(Calendar.MONTH) + 1;
//                            int dobDate = calDOB.get(Calendar.DATE);
//
//                            /* SET THE CURRENT DATE TO A CALENDAR INSTANCE */
//                            Calendar calNow = Calendar.getInstance();
//                            int nowYear = calNow.get(Calendar.YEAR);
//                            int nowMonth = calNow.get(Calendar.MONTH) + 1;
//                            int nowDate = calNow.get(Calendar.DATE);
//
//                            LocalDate dateDOB = new LocalDate(dobYear, dobMonth, dobDate);
//                            LocalDate dateNOW = new LocalDate(nowYear, nowMonth, nowDate);
//                            Period period = new Period(dateDOB, dateNOW, PeriodType.yearMonthDay());
//                            Resources resources = getResources();
//                            if (period.getYears() == 0)   {
//                                PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
//                            } else if (period.getYears() == 1)    {
//                                PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
//                            } else if (period.getYears() > 1) {
//                                PET_AGE = resources.getQuantityString(R.plurals.age, period.getYears(), period.getYears());
//                            }
//                            txtPetAge.setText(PET_AGE);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//                        /* GET THE PET'S NEUTERED STATUS */
//                        PET_NEUTERED = pet.getPetNeutered();
//                        if (PET_NEUTERED != null)   {
//                            if (PET_NEUTERED.equalsIgnoreCase("1"))   {
//                                txtNeutered.setText(R.string.pet_details_status_neutered);
//                            } else {
//                                txtNeutered.setText(R.string.pet_details_status_not_neutered);
//                            }
//                        } else {
//                            txtNeutered.setText(R.string.pet_details_status_not_neutered);
//                        }
//
//                        /* GET AND SET THE PET'S DISPLAY PROFILE */
//                        PET_DISPLAY_PROFILE = pet.getPetDisplayProfile();
//                        Uri uri = Uri.parse(PET_DISPLAY_PROFILE);
//                        imgvwPetProfile.setImageURI(uri);
//                    } else {
//                    }
//                } else {
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Pet> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
//    }
//
//    /***** CONFIGURE THE TOOLBAR *****/
//    private void configTB() {
//        Toolbar myToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);
//        String strTitle = "Pet Details";
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(strTitle);
//        getSupportActionBar().setSubtitle(null);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menu = menu;
//        MenuInflater inflater = new MenuInflater(PetDetails.this);
//        inflater.inflate(R.menu.activity_pet_details, menu);
//        hideOption(R.id.menuEdit);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish();
//                break;
//            case R.id.menuEdit:
//                Snackbar.make(coordinatorLayout, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            default:
//                break;
//        }
//        return false;
//    }
//
//    private void hideOption(int id) {
//        MenuItem item = menu.findItem(id);
//        item.setVisible(false);
//    }
//
//    private void showOption(int id) {
//        MenuItem item = menu.findItem(id);
//        item.setVisible(true);
//    }
//
//    /***** GET THE INCOMING DATA *****/
//    private void getIncomingData() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null && bundle.containsKey("PET_ID")) {
//            PET_ID = bundle.getString("PET_ID");
//            if (PET_ID != null) {
//                /* FETCH THE PET'S DETAILS */
//                fetchPetDetails();
//
//                /* SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
//                linlaVaccinationsProgress.setVisibility(View.VISIBLE);
////                fetchVaccinationRecords();
//
//                /* SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
//                linlaRecordsProgress.setVisibility(View.VISIBLE);
//                fetchMedicalRecords();
//            } else {
//                Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        } else {
//            Toast.makeText(getApplicationContext(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
//
//    /***** FETCH THE PET'S VACCINATION RECORDS *****/
////    private void fetchVaccinationRecords() {
////        /* CLEAR THE VACCINATIONS ARRAY LIST */
////        if (arrVaccinations != null)
////            arrVaccinations.clear();
////
////        VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
////        Call<Vaccinations> call = api.fetchPetVaccinationsSubset(PET_ID);
////        call.enqueue(new Callback<Vaccinations>() {
////            @Override
////            public void onResponse(Call<Vaccinations> call, Response<Vaccinations> response) {
////                if (response.body() != null && response.body().getVaccinations() != null)    {
////                    arrVaccinations = response.body().getVaccinations();
////                    if (arrVaccinations.size() > 0) {
////                        /* SHOW THE VACCINATIONS RECYCLER AND HIDE THE EMPTY LAYOUT */
////                        listVaccinations.setVisibility(View.VISIBLE);
////                        linlaEmptyVaccinations.setVisibility(View.GONE);
////
////                        /* SET THE VACCINATIONS RECYCLER VIEW */
////                        listVaccinations.setAdapter(new VaccinationsAdapter(PetDetails.this, arrVaccinations));
////
////                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
////                        linlaVaccinationsProgress.setVisibility(View.GONE);
////                    } else {
////                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
////                        listVaccinations.setVisibility(View.GONE);
////                        linlaEmptyVaccinations.setVisibility(View.VISIBLE);
////
////                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
////                        linlaVaccinationsProgress.setVisibility(View.GONE);
////                    }
////                } else {
////                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
////                    listVaccinations.setVisibility(View.GONE);
////                    linlaEmptyVaccinations.setVisibility(View.VISIBLE);
////
////                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
////                    linlaVaccinationsProgress.setVisibility(View.GONE);
////                }
////            }
////
////            @Override
////            public void onFailure(Call<Vaccinations> call, Throwable t) {
////                Crashlytics.logException(t);
////            }
////        });
////    }
//
//    /***** FETCH THE PET'S MEDICAL RECORDS *****/
//    private void fetchMedicalRecords() {
//        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
//        Call<MedicalRecords> call = api.fetchPetMedicalRecordsSubset(PET_ID);
//        call.enqueue(new Callback<MedicalRecords>() {
//            @Override
//            public void onResponse(Call<MedicalRecords> call, Response<MedicalRecords> response) {
//                if (response.body() != null && response.body().getRecords() != null)    {
//                    arrRecords = response.body().getRecords();
//
//                    /* CHECK IF VALUES WERE RETURNED */
//                    if (arrRecords.size() > 0)  {
//                        /* SHOW THE APPOINTMENTS RECYCLER AND HIDE THE EMPTY LAYOUT */
//                        listMedicalRecords.setVisibility(View.VISIBLE);
//                        linlaEmptyMedicalRecords.setVisibility(View.GONE);
//
//                        /* SET THE DOCTORS RECYCLER VIEW */
//                        listMedicalRecords.setAdapter(new MedicalRecordsAdapter(PetDetails.this, arrRecords));
//
//                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
//                        linlaRecordsProgress.setVisibility(View.GONE);
//                    } else {
//                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
//                        listMedicalRecords.setVisibility(View.GONE);
//                        linlaEmptyMedicalRecords.setVisibility(View.VISIBLE);
//
//                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
//                        linlaRecordsProgress.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
//                    listMedicalRecords.setVisibility(View.GONE);
//                    linlaEmptyMedicalRecords.setVisibility(View.VISIBLE);
//
//                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
//                    linlaRecordsProgress.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MedicalRecords> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
//    }
//
//    /***** CONFIGURE THE RECYCLER VIEW *****/
//    private void configRecycler() {
//        /* SET THE VACCINATIONS CONFIGURATION */
//        LinearLayoutManager managerVaccinations = new LinearLayoutManager(this);
//        managerVaccinations.setOrientation(LinearLayoutManager.VERTICAL);
//        listVaccinations.setLayoutManager(managerVaccinations);
//        listVaccinations.setHasFixedSize(true);
//
//        /* SET THE VACCINATIONS ADAPTER */
//        listVaccinations.setAdapter(new VaccinationsAdapter(PetDetails.this, arrVaccinations));
//
//        /* SET THE MEDICAL RECORDS CONFIGURATION */
//        LinearLayoutManager managerMedical = new LinearLayoutManager(this);
//        managerMedical.setOrientation(LinearLayoutManager.VERTICAL);
//        listMedicalRecords.setLayoutManager(managerMedical);
//        listMedicalRecords.setHasFixedSize(true);
//
//        /* SET THE MEDICAL RECORDS ADAPTER */
//        listMedicalRecords.setAdapter(new MedicalRecordsAdapter(PetDetails.this, arrRecords));
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK && requestCode == 101)  {
//            /* CLEAR THE VACCINATIONS ARRAY LIST */
//            if (arrVaccinations != null)
//                arrVaccinations.clear();
//
//            /* FETCH THE LIST OF VACCINATIONS */
////            fetchVaccinationRecords();
//        }
//    }
//}