package biz.zenpets.users.utils.adapters.kennels;

//public class TestKennelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private AppPrefs getApp()	{
//        return (AppPrefs) AppPrefs.context();
//    }
//
//    private static final int ITEM = 0;
//    private static final int LOADING = 1;
//
//    private ArrayList<Kennel> arrKennels;
//    private Context context;
//
//    private boolean isLoadingAdded = false;
//
//    /** THE LAT LNG INSTANCE **/
//    LatLng LATLNG_ORIGIN = null;
//
//    public TestKennelsAdapter(Context context, ArrayList<Kennel> arrKennels, LatLng LATLNG_ORIGIN) {
//        this.context = context;
//        this.arrKennels = arrKennels;
//        this.LATLNG_ORIGIN = LATLNG_ORIGIN;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        RecyclerView.ViewHolder viewHolder = null;
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//
//        switch (viewType) {
//            case ITEM:
//                viewHolder = getViewHolder(parent, inflater);
//                break;
//            case LOADING:
//                View v2 = inflater.inflate(R.layout.endless_item_progress, parent, false);
//                viewHolder = new LoadingVH(v2);
//                break;
//        }
//        return viewHolder;
//    }
//
//    @NonNull
//    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
//        RecyclerView.ViewHolder viewHolder;
//        View v1 = inflater.inflate(R.layout.kennels_item, parent, false);
//        viewHolder = new KennelsVH(v1);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
//        final Kennel data = arrKennels.get(position);
//
//        switch (getItemViewType(position)) {
//            case ITEM:
//                final KennelsVH kennelsVH = (KennelsVH) holder;
//
//                /* SET THE KENNEL COVER PHOTO */
//                String strKennelCoverPhoto = data.getKennelCoverPhoto();
//                if (strKennelCoverPhoto != null
//                        && !strKennelCoverPhoto.equalsIgnoreCase("")
//                        && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
//                    Uri uri = Uri.parse(strKennelCoverPhoto);
//                    kennelsVH.imgvwKennelCoverPhoto.setImageURI(uri);
//                } else {
//                    ImageRequest request = ImageRequestBuilder
//                            .newBuilderWithResourceId(R.drawable.empty_graphic)
//                            .build();
//                    kennelsVH.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
//                }
//
//                /* SET THE KENNEL NAME */
//                if (data.getKennelName() != null)   {
//                    kennelsVH.txtKennelName.setText(data.getKennelName());
//                }
//
//                /* SET THE KENNEL ADDRESS */
//                String strKennelAddress = data.getKennelAddress();
//                String cityName = data.getCityName();
//                String kennelPinCode = data.getKennelPinCode();
//                kennelsVH.txtKennelAddress.setText(context.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));
//
//                /* SET THE CAPACITY OF LARGE SIZE PETS */
//                if (data.getKennelPetCapacity() != null
//                        && !data.getKennelPetCapacity().equalsIgnoreCase("")
//                        && !data.getKennelPetCapacity().equalsIgnoreCase("null"))   {
//                    kennelsVH.txtPetCapacity.setText(context.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
//                } else {
//                    kennelsVH.txtPetCapacity.setText(context.getString(R.string.kennel_list_kennel_capacity_zero));
//                }
//                break;
//            case LOADING:
//                break;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrKennels == null ? 0 : arrKennels.size();
//    }
//
//    public void addAll(ArrayList<Kennel> arrKennels) {
//        for (Kennel kennel : arrKennels) {
//            add(kennel);
//        }
//    }
//
//    private void add(Kennel kennel) {
//        arrKennels.add(kennel);
//        notifyItemInserted(arrKennels.size() - 1);
//    }
//
//    private void remove(Kennel kennel) {
//        int position = arrKennels.indexOf(kennel);
//        if (position > -1) {
//            arrKennels.remove(position);
//            notifyItemRemoved(position);
//        }
//    }
//
//    public void clear() {
//        isLoadingAdded = false;
//        while (getItemCount() > 0) {
//            remove(getItem(0));
//        }
//    }
//
//    public boolean isEmpty() {
//        return getItemCount() == 0;
//    }
//
//    public void addLoadingFooter() {
//        isLoadingAdded = true;
//        add(new Kennel());
//    }
//
//    public void removeLoadingFooter() {
//        isLoadingAdded = false;
//
//        int position = arrKennels.size() - 1;
//        Kennel item = getItem(position);
//
//        if (item != null) {
//            arrKennels.remove(position);
//            notifyItemRemoved(position);
//        }
//    }
//
//    public Kennel getItem(int position) {
//        return arrKennels.get(position);
//    }
//
//    /** THE KENNELS VIEW HOLDER **/
//    protected class KennelsVH extends RecyclerView.ViewHolder {
//
//        SimpleDraweeView imgvwKennelCoverPhoto;
//        TextView txtKennelName;
//        TextView txtKennelAddress;
//        TextView txtPetCapacity;
//        TextView txtKennelLikes;
//        TextView txtKennelDistance;
//
//        KennelsVH(View v) {
//            super(v);
//
//            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
//            txtKennelName = v.findViewById(R.id.txtKennelName);
//            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
//            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
//            txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
//            txtKennelDistance = v.findViewById(R.id.txtKennelDistance);
//        }
//    }
//
//    protected class LoadingVH extends RecyclerView.ViewHolder {
//        LoadingVH(View itemView) {
//            super(itemView);
//        }
//    }
//}