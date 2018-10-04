package biz.zenpets.groomers.utils.adapters.location;

//public class CountriesAdapter extends ArrayAdapter<CountryData> {
//
//    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
//    private final Activity activity;
//
//    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
//    private LayoutInflater inflater = null;
//
//    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
//    private final ArrayList<CountryData> arrCountries;
//
//    public CountriesAdapter(Activity activity, ArrayList<CountryData> arrCountries) {
//        super(activity, R.layout.country_row);
//
//        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
//        this.arrCountries = arrCountries;
//
//        /* INSTANTIATE THE LAYOUT INFLATER **/
//        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return arrCountries.size();
//    }
//
//    @Override
//    public CountryData getItem(int position) {
//        return arrCountries.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
//
//        /* A VIEW HOLDER INSTANCE **/
//        ViewHolder holder;
//
//        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
//        View vi = convertView;
//
//        /* CHECK CONVERT VIEW STATUS **/
//        if (convertView == null)	{
//            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
//            vi = inflater.inflate(R.layout.country_row, parent, false);
//
//            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
//            holder = new ViewHolder();
//
//            /* CAST THE LAYOUT ELEMENTS */
//            holder.txtCountryName = vi.findViewById(R.id.txtCountryName);
//            holder.txtCurrencySymbol = vi.findViewById(R.id.txtCurrencySymbol);
//
//            /* SET THE TAG TO "vi" **/
//            vi.setTag(holder);
//        } else {
//            /* CAST THE VIEW HOLDER INSTANCE **/
//            holder = (ViewHolder) vi.getTag();
//        }
//
//        /* SET THE COUNTRY NAME **/
//        String strCountryName = arrCountries.get(position).getCountryName();
//        if (strCountryName != null)	{
//            holder.txtCountryName.setText(strCountryName);
//        }
//
//        /* SET THE CURRENCY SYMBOL **/
//        String strCurrencySymbol = arrCountries.get(position).getCurrencySymbol();
//        if (strCurrencySymbol != null)  {
//            holder.txtCurrencySymbol.setText(strCurrencySymbol);
//        }
//
//        return vi;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//
//        /* A VIEW HOLDER INSTANCE **/
//        ViewHolder holder;
//
//        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
//        View vi = convertView;
//
//        /* CHECK CONVERT VIEW STATUS **/
//        if (convertView == null)	{
//            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
//            vi = inflater.inflate(R.layout.country_row, parent, false);
//
//            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
//            holder = new ViewHolder();
//
//            /* CAST THE LAYOUT ELEMENTS */
//            holder.txtCountryName = vi.findViewById(R.id.txtCountryName);
//            holder.txtCurrencySymbol = vi.findViewById(R.id.txtCurrencySymbol);
//
//            /* SET THE TAG TO "vi" **/
//            vi.setTag(holder);
//        } else {
//            /* CAST THE VIEW HOLDER INSTANCE **/
//            holder = (ViewHolder) vi.getTag();
//        }
//
//        /* SET THE COUNTRY NAME **/
//        String strCountryName = arrCountries.get(position).getCountryName();
//        if (strCountryName != null)	{
//            holder.txtCountryName.setText(strCountryName);
//        }
//
//        /* SET THE CURRENCY SYMBOL **/
//        String strCurrencySymbol = arrCountries.get(position).getCurrencySymbol();
//        if (strCurrencySymbol != null)  {
//            holder.txtCurrencySymbol.setText(strCurrencySymbol);
//        }
//
//        return vi;
//    }
//
//    private static class ViewHolder	{
//        AppCompatTextView txtCountryName;
//        AppCompatTextView txtCurrencySymbol;
//    }
//}