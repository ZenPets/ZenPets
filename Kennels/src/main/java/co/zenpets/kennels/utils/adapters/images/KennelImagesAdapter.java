package co.zenpets.kennels.utils.adapters.images;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.models.images.KennelImage;

public class KennelImagesAdapter extends RecyclerView.Adapter<KennelImagesAdapter.ImagesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<KennelImage> arrImages;

    public KennelImagesAdapter(Activity activity, ArrayList<KennelImage> arrImages) {

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
    public void onBindViewHolder(final ImagesVH holder, int position) {
        final KennelImage data = arrImages.get(position);

        /* SET THE KENNEL IMAGE **/
        String kennelImageURL = data.getKennelImageURL();
        if (kennelImageURL != null) {
            Uri uri = Uri.parse(kennelImageURL);
            holder.imgvwKennelImage.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_48dp)
                    .build();
            holder.imgvwKennelImage.setImageURI(request.getSourceUri());
        }
    }

    @Override
    public ImagesVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennel_images_details_item, parent, false);

        return new ImagesVH(itemView);
    }

    class ImagesVH extends RecyclerView.ViewHolder   {
        final SimpleDraweeView imgvwKennelImage;

        ImagesVH(View v) {
            super(v);
            imgvwKennelImage = v.findViewById(R.id.imgvwKennelImage);
        }
    }
}