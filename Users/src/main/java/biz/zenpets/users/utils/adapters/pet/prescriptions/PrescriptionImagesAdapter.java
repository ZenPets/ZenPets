package biz.zenpets.users.utils.adapters.pet.prescriptions;

//public class PrescriptionImagesAdapter extends RecyclerView.Adapter<PrescriptionImagesAdapter.ImagesVH> {
//
//    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
//    private final Activity activity;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private final ArrayList<PrescriptionImagesData> arrImages;
//
//    public PrescriptionImagesAdapter(Activity activity, ArrayList<PrescriptionImagesData> arrImages) {
//
//        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
//        this.arrImages = arrImages;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrImages.size();
//    }
//
//    @Override
//    public void onBindViewHolder(final ImagesVH holder, int position) {
//        PrescriptionImagesData data = arrImages.get(position);
//
//        /* SET THE PRESCRIPTION IMAGE URL **/
//        String url = data.getRecordImageURL();
//        Uri uri = Uri.parse(url);
//        holder.imgvwImage.setImageURI(uri);
//
//        /* SHOW THE FULL SCREEN IMAGE */
//        holder.imgvwImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] strings = new String[arrImages.size()];
//                for (int i = 0; i < arrImages.size(); i++) {
//                    strings[i] = arrImages.get(i).getRecordImageURL();
//                }
//                Intent intent = new Intent(activity, AdoptionGalleryActivity.class);
//                intent.putExtra("position", holder.getAdapterPosition());
//                intent.putExtra("array", strings);
//                activity.startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public ImagesVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.adoptions_images_item, parent, false);
//
//        return new ImagesVH(itemView);
//    }
//
//    class ImagesVH extends RecyclerView.ViewHolder   {
//
//        final SimpleDraweeView imgvwImage;
//
//        ImagesVH(View v) {
//            super(v);
//            imgvwImage = v.findViewById(R.id.imgvwImage);
//        }
//    }
//}
