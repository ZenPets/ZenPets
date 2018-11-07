package co.zenpets.doctors.utils.adapters.doctors.clients;

//public class ClientsSelectorAdapter
//        extends RecyclerView.Adapter<ClientsSelectorAdapter.ClientsVH>
//        implements Filterable {
//
//    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
//    private final Activity activity;
//
//    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
//    private final ArrayList<ClientData> mClients;
//    private ArrayList<ClientData> mFilteredList;
//
//    public ClientsSelectorAdapter(Activity activity, ArrayList<ClientData> mClients) {
//
//        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.mClients = mClients;
//        this.mFilteredList = mClients;
//    }
//
//    @Override
//    public int getItemCount() {
//        return mFilteredList.size();
//    }
//
//    @Override
//    public void onBindViewHolder(ClientsVH holder, final int position) {
//        final ClientData data = mFilteredList.get(position);
//
//        /* SET THE USER'S DISPLAY PROFILE */
//        if (data.getUserDisplayProfile() != null)   {
//            Uri uri = Uri.parse(data.getUserDisplayProfile());
//            holder.imgvwUserProfile.setImageURI(uri);
//        }
//
//        /* SET THE USER'S NAME */
//        if (data.getUserName() != null) {
//            holder.txtUserName.setText(data.getUserName());
//        }
//
//        /* SHOW THE CLIENT DETAILS */
//        holder.cardClient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, ClientDetails.class);
//                intent.putExtra("CLIENT_ID", data.getClientID());
//                activity.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public ClientsVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.home_clients_frag_item, parent, false);
//
//        return new ClientsVH(itemView);
//    }
//
//    class ClientsVH extends RecyclerView.ViewHolder	{
//        CardView cardClient;
//        SimpleDraweeView imgvwUserProfile;
//        final AppCompatTextView txtUserName;
//
//        ClientsVH(View v) {
//            super(v);
//            cardClient = v.findViewById(R.id.cardClient);
//            imgvwUserProfile = v.findViewById(R.id.imgvwUserProfile);
//            txtUserName = v.findViewById(R.id.txtUserName);
//        }
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String charString = constraint.toString();
//
//                if (charString.isEmpty()) {
//                    mFilteredList = mClients;
//                } else {
//                    ArrayList<ClientData> filteredList = new ArrayList<>();
//                    for (ClientData data : mClients) {
//                        if (data.getUserName().toLowerCase().contains(charString)) {
//                            filteredList.add(data);
//                        }
//                    }
//                    mFilteredList = filteredList;
//                }
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mFilteredList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                mFilteredList = (ArrayList<ClientData>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
//}