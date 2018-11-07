package co.zenpets.doctors.utils.adapters.doctors.clients;

//public class ClientSpinnerAdapter extends ArrayAdapter<Client> {
//
//    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
//    private final Activity activity;
//
//    /** LAYOUT INFLATER TO USE A CUSTOM LAYOUT **/
//    private LayoutInflater inflater = null;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private final ArrayList<Client> arrClients;
//
//    public ClientSpinnerAdapter(@NonNull Activity activity, ArrayList<Client> arrClients) {
//        super(activity, R.layout.clients_spinner_row);
//
//        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE */
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrClients = arrClients;
//
//        /* INSTANTIATE THE LAYOUT INFLATER */
//        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return arrClients.size();
//    }
//
//    @Override
//    public Client getItem(int position) {
//        return arrClients.get(position);
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
//            vi = inflater.inflate(R.layout.clients_spinner_row_dropdown, parent, false);
//
//            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
//            holder = new ViewHolder();
//
//            /* CAST THE LAYOUT ELEMENTS */
//            holder.txtUserName = vi.findViewById(R.id.txtUserName);
//            holder.imgvwUserProfile = vi.findViewById(R.id.imgvwUserProfile);
//
//            /* SET THE TAG TO "vi" **/
//            vi.setTag(holder);
//        } else {
//            /* CAST THE VIEW HOLDER INSTANCE **/
//            holder = (ViewHolder) vi.getTag();
//        }
//
//        /* SET THE USER'D DISPLAY PROFILE */
//        if (arrClients.get(position).getUserDisplayProfile() != null)   {
//            Uri uri = Uri.parse(arrClients.get(position).getUserDisplayProfile());
//            holder.imgvwUserProfile.setImageURI(uri);
//        }
//
//        /* SET THE USER'S NAME **/
//        String strClinicName = arrClients.get(position).getUserName();
//        if (strClinicName != null)	{
//            holder.txtUserName.setText(strClinicName);
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
//            vi = inflater.inflate(R.layout.clients_spinner_row, parent, false);
//
//            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
//            holder = new ViewHolder();
//
//            /*	CAST THE LAYOUT ELEMENTS	*/
//            holder.txtUserName = vi.findViewById(R.id.txtUserName);
//
//            /* SET THE TAG TO "vi" **/
//            vi.setTag(holder);
//        } else {
//            /* CAST THE VIEW HOLDER INSTANCE **/
//            holder = (ViewHolder) vi.getTag();
//        }
//
//        /* SET THE CLIENT NAME (USER NAME) **/
//        String strBreedName = arrClients.get(position).getUserName();
//        if (strBreedName != null)	{
//            holder.txtUserName.setText(strBreedName);
//        }
//
//        return vi;
//    }
//
//    private static class ViewHolder	{
//        SimpleDraweeView imgvwUserProfile;
//        AppCompatTextView txtUserName;
//    }
//}