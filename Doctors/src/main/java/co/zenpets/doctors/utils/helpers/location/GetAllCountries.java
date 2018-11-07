package co.zenpets.doctors.utils.helpers.location;

//public class GetAllCountries extends AsyncTask<Void, Void, ArrayList<CountryData>> {
//
//    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
//    final GetAllCountriesInterface delegate;
//
//    /* THE COUNTRIES ARRAY LIST */
//    ArrayList<CountryData> arrCountries = new ArrayList<>();
//
//    public GetAllCountries(GetAllCountriesInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<CountryData> doInBackground(Void... voids) {
//        String COUNTRY_URL = AppPrefs.context().getString(R.string.url_fetch_all_countries);
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(COUNTRY_URL)
//                .build();
//        Call call = client.newCall(request);
//        try {
//            Response response = call.execute();
//            String strResult = response.body().string();
//            JSONObject JORoot = new JSONObject(strResult);
//            if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
//                JSONArray JACountries = JORoot.getJSONArray("countries");
//
//                /* A COUNTRY DATA INSTANCE */
//                CountryData data;
//
//                for (int i = 0; i < JACountries.length(); i++) {
//                    JSONObject JOCountries = JACountries.getJSONObject(i);
////                    Log.e("COUNTRIES", String.valueOf(JOCountries));
//
//                    /* INSTANTIATE THE COUNTRY DATA INSTANCE */
//                    data = new CountryData();
//
//                    /* GET THE COUNTRY ID */
//                    if (JOCountries.has("countryID"))    {
//                        String countryID = JOCountries.getString("countryID");
//                        data.setCountryID(countryID);
//                    } else {
//                        data.setCountryID(null);
//                    }
//
//                    /* GET THE COUNTRY NAME */
//                    if (JOCountries.has("countryName")) {
//                        String countryName = JOCountries.getString("countryName");
//                        data.setCountryName(countryName);
//                    } else {
//                        data.setCountryName(null);
//                    }
//
//                    /* GET THE CURRENCY NAME */
//                    if (JOCountries.has("currencyName"))    {
//                        String currencyName = JOCountries.getString("currencyName");
//                        data.setCurrencyName(currencyName);
//                    } else {
//                        data.setCurrencyName(null);
//                    }
//
//                    /* GET THE CURRENCY CODE */
//                    if (JOCountries.has("currencyCode"))    {
//                        String currencyCode = JOCountries.getString("currencyCode");
//                        data.setCurrencyCode(currencyCode);
//                    } else {
//                        data.setCurrencyCode(null);
//                    }
//
//                    /* GET THE CURRENCY SYMBOL */
//                    if (JOCountries.has("currencySymbol"))  {
//                        String currencySymbol = JOCountries.getString("currencySymbol");
//                        data.setCurrencySymbol(currencySymbol);
//                    } else {
//                        data.setCurrencySymbol(null);
//                    }
//
//                    /* ADD THE COLLECTED INFO TO THE ARRAY LIST */
//                    arrCountries.add(data);
//                }
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//        return arrCountries;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<CountryData> countriesData) {
//        super.onPostExecute(countriesData);
//        delegate.onCountriesResult(countriesData);
//    }
//}