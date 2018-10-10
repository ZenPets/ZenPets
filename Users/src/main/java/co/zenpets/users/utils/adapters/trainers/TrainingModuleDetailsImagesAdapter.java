package co.zenpets.users.utils.adapters.trainers;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.trainers.modules.ModuleImage;

public class TrainingModuleDetailsImagesAdapter extends RecyclerView.Adapter<TrainingModuleDetailsImagesAdapter.ImagesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<ModuleImage> arrImages;

    public TrainingModuleDetailsImagesAdapter(Activity activity, ArrayList<ModuleImage> arrImages) {

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrImages = arrImages;
    }

    @Override
    public int getItemCount() {
        return arrImages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagesVH holder, int position) {
        final ModuleImage data = arrImages.get(position);

        /* SET THE TRAINING MODULE IMAGE **/
        if (data.getTrainerModuleImageURL() != null) {
            Uri uri = Uri.parse(data.getTrainerModuleImageURL());
            holder.imgvwModuleImage.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                    .build();
            holder.imgvwModuleImage.setImageURI(request.getSourceUri());
        }

        /* SHOW FULL SCREEN IMAGE **/
        holder.imgvwModuleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String[] strings = new String[arrImages.size()];
//                for (int i = 0; i < arrImages.size(); i++) {
//                    strings[i] = arrImages.get(i).getTrainerModuleImageURL();
//                }
//                Intent intent = new Intent(activity, TrainingGalleryActivity.class);
//                intent.putExtra("position", holder.getAdapterPosition());
//                intent.putExtra("array", strings);
//                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ImagesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.training_module_details_image_item, parent, false);

        return new ImagesVH(itemView);
    }

    class ImagesVH extends RecyclerView.ViewHolder   {
        final SimpleDraweeView imgvwModuleImage;

        ImagesVH(View v) {
            super(v);
            imgvwModuleImage = v.findViewById(R.id.imgvwModuleImage);
        }
    }
}
