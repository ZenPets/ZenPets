package biz.zenpets.trainers.utils.adapters.modules.images;

//public class TrainingImagesManagerAdapter extends RecyclerView.Adapter<TrainingImagesManagerAdapter.ImagesVH> {
//
//    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
//    private final Activity activity;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private final ArrayList<ModuleImage> arrImages;
//
//    public TrainingImagesManagerAdapter(Activity activity, ArrayList<ModuleImage> arrImages) {
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
//    public void onBindViewHolder(@NonNull final ImagesVH holder, int position) {
//        final ModuleImage data = arrImages.get(position);
//
//        /* SET THE TRAINING MODULE IMAGE **/
//        if (data.getTrainerModuleImageURL() != null) {
//            Uri uri = Uri.parse(data.getTrainerModuleImageURL());
//            holder.imgvwModuleImage.setImageURI(uri);
//        }
//
//        /* SET THE IMAGE CAPTION */
//        if (data.getTrainerModuleImageCaption() != null && !data.getTrainerModuleImageCaption().equalsIgnoreCase("null"))   {
//            holder.txtModuleImageCaption.setText(data.getTrainerModuleImageCaption());
//            holder.txtModuleImageCaption.setVisibility(View.VISIBLE);
//        } else {
//            holder.txtModuleImageCaption.setVisibility(View.GONE);
//        }
//
//        /* DELETE THE SELECTED IMAGE */
//        holder.imgvwDeleteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }
//
//    @NonNull
//    @Override
//    public ImagesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.training_images_manager_item, parent, false);
//
//        return new ImagesVH(itemView);
//    }
//
//    class ImagesVH extends RecyclerView.ViewHolder   {
//        final SimpleDraweeView imgvwModuleImage;
//        AppCompatTextView txtModuleImageCaption;
//        IconicsImageView imgvwDeleteImage;
//
//        ImagesVH(View v) {
//            super(v);
//            imgvwModuleImage = v.findViewById(R.id.imgvwModuleImage);
//            txtModuleImageCaption = v.findViewById(R.id.txtModuleImageCaption);
//            imgvwDeleteImage = v.findViewById(R.id.imgvwDeleteImage);
//        }
//    }
//}
