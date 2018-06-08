package biz.zenpets.users.kennels;

//public class KennelsList extends AppCompatActivity implements SearchView.OnQueryTextListener {
//
//    private AppPrefs getApp()	{
//        return (AppPrefs) getApplication();
//    }
//
//    /** STRING TO HOLD THE DETECTED CITY NAME FOR QUERYING THE ADOPTIONS **/
//    private String DETECTED_CITY = null;
//    private String FINAL_CITY_ID = null;
//
//    /** A KENNELS API INSTANCE **/
//    KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
//
//    /** A KENNEL MODEL INSTANCE **/
//    Kennel data;
//
//    /** THE KENNELS ADAPTER AND AN ARRAY LIST INSTANCE **/
//    TestKennelsAdapter adapter;
//    ArrayList<Kennel> arrKennels = new ArrayList<>();
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
//    /** THE SEARCH VIEW INSTANCE **/
//    private SearchView searchView;
//
//    /** CAST THE LAYOUT ELEMENTS **/
////    @BindView(R.id.txtLocation) TextView txtLocation;
//    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
//    @BindView(R.id.listKennels) RecyclerView listKennels;
//    @BindView(R.id.progressLoading) ProgressBar progressLoading;
//    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.kennels_list);
//        ButterKnife.bind(this);
//
//        /* GET THE CITY ID, CITY NAME AND THE ORIGIN LATITUDE LONGITUDE */
//        String[] arrCity = getApp().getCityDetails();
//        FINAL_CITY_ID = arrCity[0];
//        DETECTED_CITY = arrCity[1];
////        txtLocation.setText(DETECTED_CITY);
//
//        /* CONFIGURE THE ACTIONBAR */
//        configAB();
//
//        /* FETCH THE TOTAL NUMBER OF PAGES */
//        fetchTotalPages();
//
//        /* CONFIGURE THE RECYCLER VIEW **/
//        configRecycler();
//
//        /* SHOW THE PROGRESS AND FETCH THE FIRST SET OF KENNELS (THE FIRST PAGE) */
//        linlaProgress.setVisibility(View.VISIBLE);
//        fetchFirstKennelPage();
//    }
//
//    /** FETCH THE FIRST SET OF KENNELS (THE FIRST PAGE) **/
//    private void fetchFirstKennelPage() {
////        new FetchKennels(this).execute(FINAL_CITY_ID, currentPage, LATITUDE, LONGITUDE);
//        Call<Kennels> call = api.fetchKennelsListByCityTest(FINAL_CITY_ID, String.valueOf(currentPage));
//        call.enqueue(new Callback<Kennels>() {
//            @Override
//            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                /* PROCESS THE RESPONSE */
//                arrKennels = processResult(response);
//
//                ArrayList<Kennel> kennels = arrKennels;
//                linlaProgress.setVisibility(View.GONE);
//                progressLoading.setVisibility(View.GONE);
//                adapter.addAll(kennels);
//
//                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
//                else isLastPage = true;
//            }
//
//            @Override
//            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//    }
//
//    /** FETCH THE NEXT SET OF KENNELS **/
//    private void fetchNextKennelPage() {
//        progressLoading.setVisibility(View.VISIBLE);
////        new FetchKennels(this).execute(FINAL_CITY_ID, currentPage, LATITUDE, LONGITUDE);
//        Call<Kennels> call = api.fetchKennelsListByCityTest(FINAL_CITY_ID, String.valueOf(currentPage));
//        call.enqueue(new Callback<Kennels>() {
//            @Override
//            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                /* PROCESS THE RESPONSE */
//                arrKennels = processResult(response);
//
//                adapter.removeLoadingFooter();
//                isLoading = false;
//
//                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
//                ArrayList<Kennel> kennels = arrKennels;
//                adapter.addAll(kennels);
//
//                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
//                else isLastPage = true;
//
//                progressLoading.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<Kennels> call, Throwable t) {
//                Log.e("KENNELS FAILURE", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//    }
//
//    /** PROCESS THE RESPONSE **/
//    private ArrayList<Kennel> processResult(Response<Kennels> response) {
//        ArrayList<Kennel> kennels = new ArrayList<>();
//        try {
//            String strResult = new Gson().toJson(response.body());
//            JSONObject JORoot = new JSONObject(strResult);
//            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                JSONArray JAKennels = JORoot.getJSONArray("kennels");
//                for (int i = 0; i < JAKennels.length(); i++) {
//                    JSONObject JOKennels = JAKennels.getJSONObject(i);
////                    Log.e("KENNEL", String.valueOf(JOKennels));
//                    data = new Kennel();
//
//                    /* GET THE KENNEL'S ID */
//                    if (JOKennels.has("kennelID"))  {
//                        data.setKennelID(JOKennels.getString("kennelID"));
//                    } else {
//                        data.setKennelID(null);
//                    }
//
//                    /* GET THE KENNEL'S NAME */
//                    if (JOKennels.has("kennelName"))    {
//                        data.setKennelName(JOKennels.getString("kennelName"));
//                    } else {
//                        data.setKennelName(null);
//                    }
//
//                    /* GET THE KENNEL'S COVER PHOTO */
//                    if (JOKennels.has("kennelCoverPhoto")
//                            && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase("")
//                            && !JOKennels.getString("kennelCoverPhoto").equalsIgnoreCase(null))  {
//                        data.setKennelCoverPhoto(JOKennels.getString("kennelCoverPhoto"));
//                    } else {
//                        data.setKennelCoverPhoto(null);
//                    }
//
//                    /* GET THE KENNEL OWNER'S ID */
//                    if (JOKennels.has("kennelOwnerID")) {
//                        data.setKennelOwnerID(JOKennels.getString("kennelOwnerID"));
//                    } else {
//                        data.setKennelOwnerID(null);
//                    }
//
//                    /* GET THE KENNEL OWNER'S NAME */
//                    if (JOKennels.has("kennelOwnerName"))  {
//                        data.setKennelOwnerName(JOKennels.getString("kennelOwnerName"));
//                    } else {
//                        data.setKennelOwnerName(null);
//                    }
//
//                    /* GET THE KENNEL OWNER'S DISPLAY PROFILE */
//                    if (JOKennels.has("kennelOwnerDisplayProfile")) {
//                        data.setKennelOwnerDisplayProfile(JOKennels.getString("kennelOwnerDisplayProfile"));
//                    } else {
//                        data.setKennelOwnerDisplayProfile(null);
//                    }
//
//                    /* GET THE KENNEL ADDRESS */
//                    if (JOKennels.has("kennelAddress")) {
//                        data.setKennelAddress(JOKennels.getString("kennelAddress"));
//                    } else {
//                        data.setKennelAddress(null);
//                    }
//
//                    /* GET THE KENNEL PIN CODE */
//                    if (JOKennels.has("kennelPinCode")) {
//                        data.setKennelPinCode(JOKennels.getString("kennelPinCode"));
//                    } else {
//                        data.setKennelPinCode(null);
//                    }
//
//                    /* GET THE COUNTRY ID AND NAME */
//                    if (JOKennels.has("countryID") && JOKennels.has("countryName")) {
//                        data.setCountryID(JOKennels.getString("countryID"));
//                        data.setCountryName(JOKennels.getString("countryName"));
//                    } else {
//                        data.setCountryID(null);
//                        data.setCountryName(null);
//                    }
//
//                    /* GET THE STATE ID AND NAME */
//                    if (JOKennels.has("stateID") && JOKennels.has("stateName"))   {
//                        data.setStateID(JOKennels.getString("stateID"));
//                        data.setStateName(JOKennels.getString("stateName"));
//                    } else {
//                        data.setStateID(null);
//                        data.setStateName(null);
//                    }
//
//                    /* GET THE CITY ID AND NAME */
//                    if (JOKennels.has("cityID") && JOKennels.has("cityName")) {
//                        data.setCityID(JOKennels.getString("cityID"));
//                        data.setCityName(JOKennels.getString("cityName"));
//                    } else {
//                        data.setCityID(null);
//                        data.setCityName(null);
//                    }
//
//                    /* GET KENNEL PHONE PREFIX AND NUMBER (#1) */
//                    if (JOKennels.has("kennelPhonePrefix1") && JOKennels.has("kennelPhoneNumber1")) {
//                        data.setKennelPhonePrefix1(JOKennels.getString("kennelPhonePrefix1"));
//                        data.setKennelPhoneNumber1(JOKennels.getString("kennelPhoneNumber1"));
//                    } else {
//                        data.setKennelPhonePrefix1(null);
//                        data.setKennelPhoneNumber1(null);
//                    }
//
//                    /* GET KENNEL PHONE PREFIX AND NUMBER (#2) */
//                    if (JOKennels.has("kennelPhonePrefix2") && JOKennels.has("kennelPhoneNumber2")) {
//                        data.setKennelPhonePrefix2(JOKennels.getString("kennelPhonePrefix2"));
//                        data.setKennelPhoneNumber2(JOKennels.getString("kennelPhoneNumber2"));
//                    } else {
//                        data.setKennelPhonePrefix2(null);
//                        data.setKennelPhoneNumber2(null);
//                    }
//
//                    /* GET THE KENNEL'S PET CAPACITY */
//                    if (JOKennels.has("kennelPetCapacity")) {
//                        data.setKennelPetCapacity(JOKennels.getString("kennelPetCapacity"));
//                    } else {
//                        data.setKennelPetCapacity(null);
//                    }
//
//                    /* GET THE KENNEL'S LATITUDE AND LONGITUDE */
//                    if (JOKennels.has("kennelLatitude") && JOKennels.has("kennelLongitude")) {
//                        data.setKennelLatitude(JOKennels.getString("kennelLatitude"));
//                        data.setKennelLongitude(JOKennels.getString("kennelLongitude"));
//                    } else {
//                        data.setKennelLatitude(null);
//                        data.setKennelLongitude(null);
//                    }
//
//                    /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
//                    kennels.add(data);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return kennels;
//    }
//
////    @Override
////    public void onKennels(ArrayList<Kennel> data) {
////        if (currentPage == 1)   {
////            ArrayList<Kennel> kennels = data;
////            linlaProgress.setVisibility(View.GONE);
////            progressLoading.setVisibility(View.GONE);
////            adapter.addAll(kennels);
////
////            if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
////            else isLastPage = true;
////        } else {
////            adapter.removeLoadingFooter();
////            isLoading = false;
////
////            /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
////            ArrayList<Kennel> kennels = data;
////            adapter.addAll(kennels);
////
////            if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
////            else isLastPage = true;
////
////            progressLoading.setVisibility(View.GONE);
////        }
////    }
//
////    /** FETCH THE FIRST SET OF KENNELS (THE FIRST PAGE) **/
////    private void fetchFirstKennelPage() {
////        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
////        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID, String.valueOf(currentPage));
////        call.enqueue(new Callback<Kennels>() {
////            @Override
////            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
////                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
////                ArrayList<Kennel> kennels = fetchResults(response);
////                progressLoading.setVisibility(View.GONE);
////                adapter.addAll(kennels);
////
////                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
////                else isLastPage = true;
////            }
////
////            @Override
////            public void onFailure(Call<Kennels> call, Throwable t) {
////                Log.e("FIRST FAILURE", t.getMessage());
////                Crashlytics.logException(t);
////            }
////        });
////    }
//
////    /** FETCH THE NEXT SET OF KENNELS **/
////    private void fetchNextKennelPage() {
////        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
////        Call<Kennels> call = api.fetchKennelsListByCity(FINAL_CITY_ID, String.valueOf(currentPage));
////        call.enqueue(new Callback<Kennels>() {
////            @Override
////            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
////                adapter.removeLoadingFooter();
////                isLoading = false;
////
////                /* PROCESS THE RESULT AND CAST IN THE ARRAY LIST */
////                ArrayList<Kennel> kennels = fetchResults(response);
////                adapter.addAll(kennels);
////
////                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
////                else isLastPage = true;
////            }
////
////            @Override
////            public void onFailure(Call<Kennels> call, Throwable t) {
////                Log.e("NEXT FAILURE", t.getMessage());
////                Crashlytics.logException(t);
////            }
////        });
////    }
//
////    /** PROCESS THE RESULT AND CAST IN THE ARRAY LIST **/
////    private ArrayList<Kennel> fetchResults(Response<Kennels> response) {
////        Kennels kennels = response.body();
////        return kennels.getKennels();
////    }
//
//    /** FETCH THE TOTAL NUMBER OF PAGES **/
//    private void fetchTotalPages() {
//        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
//        Call<KennelPages> call = api.fetchKennelPages(FINAL_CITY_ID);
//        call.enqueue(new Callback<KennelPages>() {
//            @Override
//            public void onResponse(Call<KennelPages> call, Response<KennelPages> response) {
//                if (response.body() != null && response.body().getTotalPages() != null) {
//                    TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
////                    Log.e("TOTAL PAGES", String.valueOf(TOTAL_PAGES));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<KennelPages> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
//    }
//
//    /***** CONFIGURE THE RECYCLER VIEW *****/
//    private void configRecycler() {
//        adapter = new TestKennelsAdapter(KennelsList.this);
//        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        listKennels.setLayoutManager(manager);
//        listKennels.setItemAnimator(new DefaultItemAnimator());
//        listKennels.setAdapter(adapter);
//
//        listKennels.addOnScrollListener(new PaginationScrollListener(manager) {
//            @Override
//            protected void loadMoreItems() {
//                isLoading = true;
//                currentPage += 1;
//
//                /* FETCH THE NEXT SET OF KENNELS */
//                fetchNextKennelPage();
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
//    /***** CONFIGURE THE ACTIONBAR *****/
//    private void configAB() {
//        Toolbar myToolbar = findViewById(R.id.myToolbar);
//        setSupportActionBar(myToolbar);
//        String strTitle = "Find Kennels";
//        SpannableString s = new SpannableString(strTitle);
//        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(s);
//        getSupportActionBar().setSubtitle("In " + DETECTED_CITY);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_search_kennels, menu);
//        MenuItem search = menu.findItem(R.id.menuSearchKennels);
//        searchView = (SearchView) search.getActionView();
//        searchView.setQueryHint(getResources().getString(R.string.search_hint_kennels));
//        searchView.setIconified(true);
//        searchView.setOnQueryTextListener(this);
//        return true;
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
//    public boolean onQueryTextSubmit(String query) {
//        if (query != null)
//            /* SHOW THE PROGRESS AND FETCH THE KENNEL SEARCH RESULTS */
//            linlaProgress.setVisibility(View.VISIBLE);
//            fetchKennelResults(query);
//            searchView.clearFocus();
//        return true;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        return false;
//    }
//
//    /** FETCH THE KENNEL SEARCH RESULTS **/
//    private void fetchKennelResults(String query) {
//        Call<Kennels> call = api.searchKennels(FINAL_CITY_ID, query);
//        call.enqueue(new Callback<Kennels>() {
//            @Override
//            public void onResponse(Call<Kennels> call, Response<Kennels> response) {
//                /* CLEAR THE ARRAY LIST */
//                arrKennels.clear();
//
//                /* CLEAR THE ADAPTER */
//                adapter.clear();
//
//                /* PROCESS THE RESPONSE */
//                arrKennels = processResult(response);
//
//                ArrayList<Kennel> kennels = arrKennels;
//                Log.e("SIZE", String.valueOf(kennels.size()));
//                linlaProgress.setVisibility(View.GONE);
//                progressLoading.setVisibility(View.GONE);
//                adapter.addAll(kennels);
//            }
//
//            @Override
//            public void onFailure(Call<Kennels> call, Throwable t) {
//            }
//        });
//    }
//}