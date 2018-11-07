package co.zenpets.doctors.utils.helpers.location;

//public class GetAllCities extends AsyncTask<Object, Void, ArrayList<CityData>> {
//
//    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
//    final GetAllCitiesInterface delegate;
//
//    /* THE COUNTRIES ARRAY LIST */
//    ArrayList<CityData> arrCities = new ArrayList<>();
//
//    public GetAllCities(GetAllCitiesInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<CityData> doInBackground(Object... params) {
//        String CITIES_URL = AppPrefs.context().getString(R.string.url_fetch_all_cities);
//        HttpUrl.Builder builder = HttpUrl.parse(CITIES_URL).newBuilder();
//        builder.addQueryParameter("stateID", String.valueOf(params[0]));
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
//                JSONArray JACities = JORoot.getJSONArray("cities");
//
//                /* A CITIES DATA INSTANCE */
//                CityData data;
//
//                for (int i = 0; i < JACities.length(); i++) {
//                    JSONObject JOCities = JACities.getJSONObject(i);
//
//                    /* INSTANTIATE THE CITIES DATA INSTANCE */
//                    data = new CityData();
//
//                    /* GET THE CITY ID */
//                    if (JOCities.has("cityID"))    {
//                        String cityID = JOCities.getString("cityID");
//                        data.setCityID(cityID);
//                    } else {
//                        data.setCityID(null);
//                    }
//
//                    /* GET THE CITY NAME */
//                    if (JOCities.has("cityName"))   {
//                        String cityName = JOCities.getString("cityName");
//                        data.setCityName(cityName);
//                    } else {
//                        data.setCityName(null);
//                    }
//
//                    /* GET THE STATE ID */
//                    if (JOCities.has("stateID"))  {
//                        String stateID = JOCities.getString("stateID");
//                        data.setStateID(stateID);
//                    } else {
//                        data.setStateID(null);
//                    }
//
//                    /* ADD THE COLLECTED INFO TO THE ARRAYLIST */
//                    arrCities.add(data);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return arrCities;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<CityData> citiesData) {
//        super.onPostExecute(citiesData);
//        delegate.onCitiesResult(citiesData);
//    }
//}