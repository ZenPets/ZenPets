package co.zenpets.doctors.utils.adapters.clinics;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.models.clinics.images.ImageData;

public class ClinicImagesManagerAdapter extends RecyclerView.Adapter<ClinicImagesManagerAdapter.ImagesVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<ImageData> arrImages;

    public ClinicImagesManagerAdapter(Activity activity, ArrayList<ImageData> arrImages) {

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
        final ImageData data = arrImages.get(position);

        /* SET THE CLINIC IMAGE **/
        if (data.getImageURL() != null) {
            Uri uri = Uri.parse(data.getImageURL());
            holder.imgvwClinicImage.setImageURI(uri);
        }
    }

    @Override
    public ImagesVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.clinic_images_manager_item, parent, false);

        return new ImagesVH(itemView);
    }

    class ImagesVH extends RecyclerView.ViewHolder   {
        final SimpleDraweeView imgvwClinicImage;

        ImagesVH(View v) {
            super(v);
            imgvwClinicImage = v.findViewById(R.id.imgvwClinicImage);
        }
    }
}
