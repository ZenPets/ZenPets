package co.zenpets.trainers.utils.adapters.modules;

import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import co.zenpets.trainers.R;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleAlbumData;

public class TrainingModuleAlbumAdapter extends RecyclerView.Adapter<TrainingModuleAlbumAdapter.AlbumsVH> {

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<ModuleAlbumData> arrAlbums;

    public TrainingModuleAlbumAdapter(ArrayList<ModuleAlbumData> arrAlbums) {

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrAlbums = arrAlbums;
    }

    @Override
    public int getItemCount() {
        return arrAlbums.size();
    }

    @Override
    public void onBindViewHolder(AlbumsVH holder, int position) {
        final ModuleAlbumData td = arrAlbums.get(position);

        /* SET THE MODULE IMAGE **/
        Bitmap bmpImage = td.getBmpModuleImage();
        if (bmpImage!= null)	{
            holder.imgvwImage.setImageBitmap(bmpImage);
            holder.imgvwImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        /* SET THE IMAGE NUMBER **/
        String strNumber = td.getTxtImageNumber();
        holder.txtImageNumber.setText(strNumber);
    }

    @Override
    public AlbumsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.training_module_album_item, parent, false);

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
