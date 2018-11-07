package co.zenpets.users.doctors;

//public class NewDoctorsList extends AppCompatActivity
//        implements FetchCityIDInterface, FetchLocalityIDInterface, FetchDoctorsInterface {
//
//    private AppPrefs getApp()	{
//        return (AppPrefs) getApplication();
//    }
//
//    /** AN INSTANCE OF THE DOCTOR'S API **/
//    DoctorsAPI api = ZenApiClient.getClient().create(DoctorsAPI.class);
//
//    /** A FUSED LOCATION PROVIDER CLIENT INSTANCE**/
//    private FusedLocationProviderClient locationProviderClient;
//
//    /** A LOCATION INSTANCE **/
//    private Location location;
//
//    /** STRING TO HOLD THE DETECTED CITY NAME AND LOCALITY FOR QUERYING THE DOCTORS INFORMATION **/
//    private String DETECTED_CITY = null;
//    private String FINAL_CITY_ID = null;
//    private String DETECTED_LOCALITY = null;
//    private String FINAL_LOCALITY_ID = null;
//    private String LATITUDE = null;
//    private String LONGITUDE = null;
//
//    /** PERMISSION REQUEST CONSTANTS **/
//    private static final int ACCESS_FINE_LOCATION_CONSTANT = 200;
//
//    /** THE LATLNG INSTANCE FOR GETTING THE USER'S CURRENT COORDINATES **/
//    private LatLng LATLNG_ORIGIN;
//
//    /** THE KENNELS ADAPTER **/
//    NewDoctorsAdapter adapter;
//
//    /** A LINEAR LAYOUT MANAGER INSTANCE **/
//    LinearLayoutManager manager;
//
//    private static final int PAGE_START = 1;
//    private boolean isLoading = false;
//    private boolean isLastPage = false;
//    private int TOTAL_PAGES = 0;
//    private int currentPage = PAGE_START;
//
//    /** CAST THE LAYOUT ELEMENTS **/
//    @BindView(R.id.txtLocation) AppCompatTextView txtLocation;
//    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
//    @BindView(R.id.listDoctors) RecyclerView listDoctors;
//    @BindView(R.id.progressLoading) ProgressBar progressLoading;
//    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
//    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.new_doctors_list);
//        ButterKnife.bind(this);
//
//        /* CONFIGURE THE ACTIONBAR */
//        configAB();
//
//        /* GET THE LATITUDE AND LONGITUDE FROM THE SHARED PREFERENCES */
//        LATITUDE = getApp().getOriginLatitude();
//        LONGITUDE = getApp().getOriginLongitude();
//
//        /* FETCH THE TOTAL NUMBER OF PAGES */
//        fetchTotalPages();
//
//        /* INSTANTIATE THE LOCATION CLIENT */
//        locationProviderClient = LocationServices.getFusedLocationProviderClient(NewDoctorsList.this);
//
//        /* SHOW THE PROGRESS AND FETCH THE USER'S LOCATION */
//        linlaProgress.setVisibility(View.VISIBLE);
//        fetchUsersLocation();
//
//        /* CONFIGURE THE RECYCLER VIEW **/
//        configRecycler();
//    }
//
//    /** FETCH THE FIRST LIST OF DOCTORS **/
//    private void fetchFirstDoctorsPage() {
//        new FetchDoctors(this).execute(FINAL_CITY_ID, FINAL_LOCALITY_ID, currentPage, LATITUDE, LONGITUDE);
//    }
//
//    /** FETCH THE NEXT SET OF DOCTORS **/
//    private void fetchNextDoctorsPage() {
//        progressLoading.setVisibility(View.VISIBLE);
//        new FetchDoctors(this).execute(FINAL_CITY_ID, FINAL_LOCALITY_ID, currentPage, LATITUDE, LONGITUDE);
//    }
//
//    @Override
//    public void onDoctorsList(ArrayList<Doctor> data) {
//        if (currentPage == 1)   {
//            ArrayList<Doctor> doctors = data;
//            linlaProgress.setVisibility(View.GONE);
//            progressLoading.setVisibility(View.GONE);
//            adapter.addAll(doctors);
//
//            if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
//            else isLastPage = true;
//        } else {
//            adapter.removeLoadingFooter();
//            isLoading = false;
//
//            /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
//            ArrayList<Doctor> doctors = data;
//            adapter.addAll(doctors);
//
//            if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
//            else isLastPage = true;
//
//            progressLoading.setVisibility(View.GONE);
//        }
//    }
//
////    /** FETCH THE FIRST LIST OF DOCTORS **/
////    private void fetchFirstDoctorsPage() {
////        Call<Doctors> call = api.fetchDoctorsListTest(FINAL_CITY_ID, String.valueOf(currentPage));
////        call.enqueue(new Callback<Doctors>() {
////            @Override
////            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
////                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
////                ArrayList<Doctor> doctors = fetchResults(response);
////                progressLoading.setVisibility(View.GONE);
////                adapter.addAll(doctors);
////
////                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
////                else isLastPage = true;
////            }
////
////            @Override
////            public void onFailure(Call<Doctors> call, Throwable t) {
////                Log.e("FIRST FAILURE", t.getMessage());
////            }
////        });
////    }
//
////    /** FETCH THE NEXT SET OF DOCTORS **/
////    private void fetchNextDoctorsPage() {
////        Call<Doctors> call = api.fetchDoctorsListTest(FINAL_CITY_ID, String.valueOf(currentPage));
////        call.enqueue(new Callback<Doctors>() {
////            @Override
////            public void onResponse(Call<Doctors> call, Response<Doctors> response) {
////                adapter.removeLoadingFooter();
////                isLoading = false;
////
////                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
////                ArrayList<Doctor> doctors = fetchResults(response);
////                adapter.addAll(doctors);
////
////                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
////                else isLastPage = true;
////            }
////
////            @Override
////            public void onFailure(Call<Doctors> call, Throwable t) {
////                Log.e("NEXT FAILURE", t.getMessage());
////            }
////        });
////    }
//
////    /** PROCESS THE RESULT AND CAST IN THE ARRAY LIST **/
////    private ArrayList<Doctor> fetchResults(Response<Doctors> response) {
////        Doctors doctors = response.body();
////        return doctors.getDoctors();
////    }
//
//    /** FETCH THE USER'S LOCATION **/
//    private void fetchUsersLocation() {
//        /* CHECK FOR PERMISSION STATUS */
//        if (ContextCompat.checkSelfPermission(NewDoctorsList.this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED)   {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))    {
//                /* SHOW THE DIALOG */
//                new MaterialDialog.Builder(this)
//                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
//                        .title(getString(R.string.location_permission_title))
//                        .cancelable(true)
//                        .content(getString(R.string.location_permission_message))
//                        .positiveText(getString(R.string.permission_grant))
//                        .negativeText(getString(R.string.permission_deny))
//                        .theme(Theme.LIGHT)
//                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.cancel();
//                                new MaterialDialog.Builder(NewDoctorsList.this)
//                                        .icon(ContextCompat.getDrawable(NewDoctorsList.this, R.drawable.ic_info_outline_black_24dp))
//                                        .title(getString(R.string.doctor_location_denied_title))
//                                        .cancelable(true)
//                                        .content(getString(R.string.doctor_location_denied_message))
//                                        .positiveText(getString(R.string.permission_grant))
//                                        .negativeText(getString(R.string.permission_nevermind))
//                                        .theme(Theme.LIGHT)
//                                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                            @Override
//                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                dialog.cancel();
//                                            }
//                                        })
//                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                            @Override
//                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                dialog.cancel();
//                                                ActivityCompat.requestPermissions(
//                                                        NewDoctorsList.this,
//                                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
//                                            }
//                                        }).show();
//                            }
//                        })
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.cancel();
//                                ActivityCompat.requestPermissions(
//                                        NewDoctorsList.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
//                            }
//                        }).show();
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        ACCESS_FINE_LOCATION_CONSTANT);
//            }
//        } else {
//            locationProviderClient.getLastLocation()
//                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Location> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                location = task.getResult();
//
//                                /* GET THE ORIGIN LATLNG */
//                                LATLNG_ORIGIN = new LatLng(location.getLatitude(), location.getLongitude());
//
//                                /* FETCH THE LOCATION USING GEOCODER */
//                                fetchTheLocation();
//                            } else {
////                                Log.e("EXCEPTION", String.valueOf(task.getException()));
//                            }
//                        }
//                    });
//        }
//    }
//
//    /***** FETCH THE LOCATION USING GEOCODER *****/
//    private void fetchTheLocation() {
//        Geocoder geocoder = new Geocoder(NewDoctorsList.this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            if (addresses.size() > 0)   {
//                DETECTED_CITY = addresses.get(0).getLocality();
//                DETECTED_LOCALITY = addresses.get(0).getSubLocality();
//
//                if (DETECTED_CITY != null)  {
//                    if (!DETECTED_CITY.equalsIgnoreCase("null")) {
//                        if (DETECTED_LOCALITY != null)  {
//                            if (!DETECTED_LOCALITY.equalsIgnoreCase("null"))   {
//                                /* GET THE CITY ID */
//                                new FetchCityID(this).execute(DETECTED_CITY);
//
//                                /* SET THE LOCATION */
//                                txtLocation.setText(getString(R.string.doctor_list_tb_location_placeholder, DETECTED_LOCALITY, DETECTED_CITY));
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
////            Log.e("GEOCODER", e.getMessage());
//        }
//    }
//
//    @Override
//    public void onCityID(String result) {
//        /* GET THE RESULT */
//        FINAL_CITY_ID = result;
//
//        /* CHECK FOR A VALID RESULT */
//        if (FINAL_CITY_ID != null)   {
//            /* GET THE LOCALITY ID */
//            new FetchLocalityID(this).execute(DETECTED_LOCALITY);
//        } else {
//            /* SET THE ERROR MESSAGE */
//            txtEmpty.setText(getString(R.string.doctor_list_location_not_served));
//
//            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//            linlaEmpty.setVisibility(View.VISIBLE);
//            listDoctors.setVisibility(View.GONE);
//
//            /* HIDE THE PROGRESS */
//            progressLoading.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onLocalityID(String result) {
//        /* GET THE RESULT */
//        FINAL_LOCALITY_ID = result;
//
//        /* CHECK FOR A VALID, NON NULL RESULT */
//        if (FINAL_LOCALITY_ID != null)  {
//            /* FETCH THE FIRST LIST OF DOCTORS */
//            fetchFirstDoctorsPage();
//        } else {
//            /* SET THE ERROR MESSAGE */
//            txtEmpty.setText(getString(R.string.doctor_list_location_not_served));
//
//            /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
//            linlaEmpty.setVisibility(View.VISIBLE);
//            listDoctors.setVisibility(View.GONE);
//
//            /* HIDE THE PROGRESS */
//            progressLoading.setVisibility(View.GONE);
//        }
//    }
//
//    /** FETCH THE TOTAL NUMBER OF PAGES **/
//    private void fetchTotalPages() {
//        Call<DoctorPages> call = api.fetchDoctorPages("172", "23");
//        call.enqueue(new Callback<DoctorPages>() {
//            @Override
//            public void onResponse(Call<DoctorPages> call, Response<DoctorPages> response) {
//                if (response.body() != null && response.body().getTotalPages() != null) {
//                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
////                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DoctorPages> call, Throwable t) {
////                Log.e("PAGES FAILURE", t.getMessage());
//            }
//        });
//    }
//
//    /***** CONFIGURE THE RECYCLER VIEW *****/
//    private void configRecycler() {
//        adapter = new NewDoctorsAdapter(NewDoctorsList.this);
//        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        listDoctors.setLayoutManager(manager);
//        listDoctors.setItemAnimator(new DefaultItemAnimator());
//        listDoctors.setAdapter(adapter);
//
//        listDoctors.addOnScrollListener(new PaginationScrollListener(manager) {
//            @Override
//            protected void loadMoreItems() {
//                isLoading = true;
//                currentPage += 1;
//
//                /* FETCH THE NEXT SET OF DOCTORS */
//                fetchNextDoctorsPage();
//            }
//
//            @Override
//            public int getTotalPageCount() {
//                return TOTAL_PAGES;
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return isLastPage;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return isLoading;
//            }
//        });
//    }
//
//    /** CONFIGURE THE ACTIONBAR **/
//    private void configAB() {
//        Toolbar myToolbar = findViewById(R.id.myToolbar);
//        setSupportActionBar(myToolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(null);
//        getSupportActionBar().setSubtitle(null);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == ACCESS_FINE_LOCATION_CONSTANT)   {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)    {
//                /* FETCH THE USER'S LOCATION */
//                fetchUsersLocation();
//            } else {
//                new MaterialDialog.Builder(this)
//                        .icon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp))
//                        .title(getString(R.string.doctor_location_denied_title))
//                        .cancelable(true)
//                        .content(getString(R.string.doctor_location_denied_message))
//                        .positiveText(getString(R.string.permission_grant))
//                        .negativeText(getString(R.string.permission_nevermind))
//                        .theme(Theme.LIGHT)
//                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.cancel();
//                            }
//                        })
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                dialog.cancel();
//                                ActivityCompat.requestPermissions(
//                                        NewDoctorsList.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CONSTANT);
//                            }
//                        }).show();
//            }
//        }
//    }
//}