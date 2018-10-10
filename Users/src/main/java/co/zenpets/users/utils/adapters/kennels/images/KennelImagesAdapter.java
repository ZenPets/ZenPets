package co.zenpets.users.utils.adapters.kennels.images;

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

import co.zenpets.users.R;
import co.zenpets.users.utils.models.kennels.images.KennelImage;

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
        final String kennelImageURL = data.getKennelImageURL();
        if (kennelImageURL != null) {
            Picasso.with(activity)
                    .load(kennelImageURL)
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
                                    .load(kennelImageURL)
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