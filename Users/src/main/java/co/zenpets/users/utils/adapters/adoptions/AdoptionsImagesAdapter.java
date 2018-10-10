package co.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.content.Intent;
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
import co.zenpets.users.adoptions.images.AdoptionGalleryActivity;
import co.zenpets.users.utils.models.adoptions.images.AdoptionImage;

public class AdoptionsImagesAdapter extends RecyclerView.Adapter<AdoptionsImagesAdapter.AlbumsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<AdoptionImage> arrImages;

    public AdoptionsImagesAdapter(Activity activity, ArrayList<AdoptionImage> arrImages) {

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
    public void onBindViewHolder(@NonNull final AlbumsVH holder, int position) {
        AdoptionImage data = arrImages.get(position);

        /* SET THE CLINIC IMAGE **/
        if (data.getImageURL() != null) {
            Uri uri = Uri.parse(data.getImageURL());
            holder.imgvwImage.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_24dp)
                    .build();
            holder.imgvwImage.setImageURI(request.getSourceUri());
        }

        /* SHOW THE FULL SCREEN IMAGE */
        holder.imgvwImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings = new String[arrImages.size()];
                for (int i = 0; i < arrImages.size(); i++) {
                    strings[i] = arrImages.get(i).getImageURL();
                }
                Intent intent = new Intent(activity, AdoptionGalleryActivity.class);
                intent.putExtra("position", holder.getAdapterPosition());
                intent.putExtra("array", strings);
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public AlbumsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.adoptions_images_item, parent, false);

        return new AlbumsVH(itemView);
    }

    class AlbumsVH extends RecyclerView.ViewHolder   {
        
        final SimpleDraweeView imgvwImage;

        AlbumsVH(View v) {
            super(v);
            imgvwImage = v.findViewById(R.id.imgvwImage);
        }
    }
}
