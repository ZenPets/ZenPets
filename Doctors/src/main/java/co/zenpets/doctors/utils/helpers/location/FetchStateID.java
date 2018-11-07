package co.zenpets.doctors.utils.helpers.location;

//public class FetchStateID extends AsyncTask<Object, Void, String> {
//
//    /* THE INTERFACE INSTANCE */
//    FetchStateIDInterface delegate;
//
//    /* THE STATE ID */
//    String STATE_ID = null;
//
//    public FetchStateID(FetchStateIDInterface delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    protected String doInBackground(Object... objects) {
//        String URL_STATE_ID = AppPrefs.context().getString(R.string.url_fetch_state_id);
//        HttpUrl.Builder builder = HttpUrl.parse(URL_STATE_ID).newBuilder();
//        builder.addQueryParameter("stateName", String.valueOf(objects[0]));
//        String FINAL_URL = builder.build().toString();
////        Log.e("STATE", FINAL_URL);
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
//                String state = JORoot.getString("state");
//                if (state.equalsIgnoreCase("") || state.equalsIgnoreCase("null") || state.equalsIgnoreCase("[]")) {
//                    /* ERROR FETCHING RESULT */
//                    STATE_ID = null;
//                } else {
//                    JSONArray JAState = JORoot.getJSONArray("state");
//                    for (int i = 0; i < JAState.length(); i++) {
//                        JSONObject JOState = JAState.getJSONObject(i);
//
//                        /* GET THE STATE ID */
//                        if (JOState.has("stateID"))   {
//                            STATE_ID = JOState.getString("stateID");
//                        }  else {
//                            /* ERROR FETCHING RESULT */
//                            STATE_ID = null;
//                        }
//                    }
//                }
//            } else {
//                /* ERROR FETCHING RESULT */
//                STATE_ID = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return STATE_ID;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        delegate.onStateID(s);
//    }
//}