package co.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.adoptions.AdoptionAlbumData;

public class AdoptionsAlbumAdapter extends RecyclerView.Adapter<AdoptionsAlbumAdapter.AlbumsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<AdoptionAlbumData> arrAlbums;

    public AdoptionsAlbumAdapter(Activity activity, ArrayList<AdoptionAlbumData> arrAlbums) {

        /* CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrAlbums = arrAlbums;
    }

    @Override
    public int getItemCount() {
        return arrAlbums.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsVH holder, int position) {
        final AdoptionAlbumData td = arrAlbums.get(position);

        /* SET THE CLINIC IMAGE **/
        Bitmap bmpImage = td.getBmpAdoptionImage();
        if (bmpImage!= null)	{
            holder.imgvwImage.setImageBitmap(bmpImage);
            holder.imgvwImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        /* SET THE IMAGE NUMBER **/
        String strNumber = td.getTxtImageNumber();
        holder.txtImageNumber.setText(strNumber);
    }

    @NonNull
    @Override
    public AlbumsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.adoptions_album_item, parent, false);

        return new AlbumsVH(itemView);
    }

    class AlbumsVH extends RecyclerView.ViewHolder   {
        
        final AppCompatImageView imgvwImage;
        final AppCompatTextView txtImageNumber;

        AlbumsVH(View v) {
            super(v);
            imgvwImage = v.findViewById(R.id.imgvwImage);
            txtImageNumber = v.findViewById(R.id.txtImageNumber);
        }
    }
}
