package co.zenpets.doctors.utils.helpers.location;

//public class GetAllStates extends AsyncTask<Object, Void, ArrayList<StateData>> {
//
//    /* THE INTERFACE INSTANCE TO RETURN THE RESULTS TO THE CALLING ACTIVITY */
//    final GetAllStatesInterface delegate;
//
//    /* THE COUNTRIES ARRAY LIST */
//    ArrayList<StateData> arrStates = new ArrayList<>();
//
//    public GetAllStates(GetAllStatesInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected ArrayList<StateData> doInBackground(Object... params) {
//        String STATES_URL = AppPrefs.context().getString(R.string.url_fetch_all_states);
//        HttpUrl.Builder builder = HttpUrl.parse(STATES_URL).newBuilder();
//        builder.addQueryParameter("countryID", String.valueOf(params[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("STATES URL", FINAL_URL);
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
//                JSONArray JAStates = JORoot.getJSONArray("states");
//
//                /* A STATES DATA INSTANCE */
//                StateData data;
//
//                for (int i = 0; i < JAStates.length(); i++) {
//                    JSONObject JOStates = JAStates.getJSONObject(i);
////                    Log.e("STATES", String.valueOf(JOStates));
//
//                    /* INSTANTIATE THE STATES DATA INSTANCE */
//                    data = new StateData();
//
//                    /* GET THE STATE ID */
//                    if (JOStates.has("stateID"))    {
//                        String stateID = JOStates.getString("stateID");
//                        data.setStateID(stateID);
//                    } else {
//                        data.setStateID(null);
//                    }
//
//                    /* GET THE STATE NAME */
//                    if (JOStates.has("stateName"))   {
//                        String stateName = JOStates.getString("stateName");
//                        data.setStateName(stateName);
//                    } else {
//                        data.setStateName(null);
//                    }
//
//                    /* GET THE COUNTRY ID */
//                    if (JOStates.has("countryID"))  {
//                        String countryID = JOStates.getString("countryID");
//                        data.setCountryID(countryID);
//                    } else {
//                        data.setCountryID(null);
//                    }
//
//                    /* ADD THE COLLECTED INFO TO THE ARRAY LIST */
//                    arrStates.add(data);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return arrStates;
//    }
//
//    @Override
//    protected void onPostExecute(ArrayList<StateData> statesData) {
//        super.onPostExecute(statesData);
//        delegate.onStatesResult(statesData);
//    }
//}