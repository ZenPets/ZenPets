package biz.zenpets.users.utils.adapters.groomers.images;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.groomers.images.GroomerImage;

public class GroomerImagesAdapter extends RecyclerView.Adapter<GroomerImagesAdapter.ImagesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<GroomerImage> arrImages;

    public GroomerImagesAdapter(Activity activity, ArrayList<GroomerImage> arrImages) {

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

        /* SET THE GROOMER IMAGE URL **/
        final String imageURL = data.getImageURL();
        if (imageURL != null) {
            Picasso.with(activity)
                    .load(imageURL)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .fit()
                    .centerCrop()
                    .into(holder.imgvwKennelImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(activity)
                                    .load(imageURL)
                                    .fit()
                                    .centerCrop()
                                    .error(R.drawable.ic_person_black_24dp)
                                    .into(holder.imgvwKennelImage, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError() {
//                                            Log.e("Picasso","Could not fetch image");
                                        }
                                    });
                        }
                    });
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
        final ImageView imgvwKennelImage;

        ImagesVH(View v) {
            super(v);
            imgvwKennelImage = v.findViewById(R.id.imgvwKennelImage);
        }
    }
}