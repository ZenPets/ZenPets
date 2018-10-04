package biz.zenpets.groomers.utils.adapters.images;

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

import biz.zenpets.groomers.R;
import biz.zenpets.groomers.utils.models.images.GroomerImage;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<GroomerImage> arrImages;

    public ImagesAdapter(Activity activity, ArrayList<GroomerImage> arrImages) {

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
        final GroomerImage data = arrImages.get(position);

        /* SET THE KENNEL IMAGE **/
        String kennelImageURL = data.getImageURL();
        if (kennelImageURL != null) {
            Uri uri = Uri.parse(kennelImageURL);
            holder.imgvwGroomerImage.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.ic_business_black_48dp)
                    .build();
            holder.imgvwGroomerImage.setImageURI(request.getSourceUri());
        }
    }

    @Override
    public ImagesVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.groomer_images_details_item, parent, false);

        return new ImagesVH(itemView);
    }

    class ImagesVH extends RecyclerView.ViewHolder   {
        final SimpleDraweeView imgvwGroomerImage;

        ImagesVH(View v) {
            super(v);
            imgvwGroomerImage = v.findViewById(R.id.imgvwGroomerImage);
        }
    }
}