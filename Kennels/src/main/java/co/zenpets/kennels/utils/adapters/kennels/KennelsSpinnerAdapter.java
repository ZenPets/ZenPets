package co.zenpets.kennels.utils.adapters.kennels;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

import co.zenpets.kennels.R;
import co.zenpets.kennels.utils.models.kennels.Kennel;

public class KennelsSpinnerAdapter extends ArrayAdapter<Kennel> {

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final List<Kennel> arrKennels;

    public KennelsSpinnerAdapter(Activity activity, int resource, List<Kennel> arrKennels) {
        super(activity, resource);

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrKennels = arrKennels;

        /* INSTANTIATE THE LAYOUT INFLATER INSTANCE */
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrKennels.size();
    }

    @Override
    public Kennel getItem(int position) {
        return arrKennels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.kennel_selector_dropdown, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtKennelName = vi.findViewById(R.id.txtKennelName);
            holder.imgvwKennelCoverPhoto = vi.findViewById(R.id.imgvwKennelCoverPhoto);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE KENNEL COVER PHOTO */
        if (arrKennels.get(position).getKennelCoverPhoto() != null)   {
            Uri uri = Uri.parse(arrKennels.get(position).getKennelCoverPhoto());
            holder.imgvwKennelCoverPhoto.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
        }

        /* SET THE KENNEL NAME **/
        String strKennelName = arrKennels.get(position).getKennelName();
        if (strKennelName != null)	{
            holder.txtKennelName.setText(strKennelName);
        }

        return vi;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        /* A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /* CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /* CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /* CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.kennel_selector_row, parent, false);

            /* INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /* CAST THE LAYOUT ELEMENTS */
            holder.txtKennelName = vi.findViewById(R.id.txtKennelName);

            /* SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /* CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /* SET THE KENNEL NAME **/
        String strKennelName = arrKennels.get(position).getKennelName();
        if (strKennelName != null)	{
            holder.txtKennelName.setText(strKennelName);
        }

        return vi;
    }

    private static class ViewHolder	{
        SimpleDraweeView imgvwKennelCoverPhoto;
        AppCompatTextView txtKennelName;
    }
}