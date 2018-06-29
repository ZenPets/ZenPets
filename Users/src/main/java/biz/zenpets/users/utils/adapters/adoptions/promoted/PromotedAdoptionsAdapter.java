package biz.zenpets.users.utils.adapters.adoptions.promoted;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;

@SuppressWarnings("ConstantConditions")
public class PromotedAdoptionsAdapter extends RecyclerView.Adapter<PromotedAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Promotion> arrPromoted;

    public PromotedAdoptionsAdapter(Activity activity, ArrayList<Promotion> arrPromoted) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrPromoted = arrPromoted;
    }

    @Override
    public int getItemCount() {
        return arrPromoted.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Promotion data = arrPromoted.get(position);
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.promoted_adoption_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{
        CardView cardAdoptionContainer;
        SimpleDraweeView imgvwAdoptionCover;
        TextView txtAdoptionName;
        TextView txtAdoptionBreed;
        IconicsImageView imgvwGender;
        TextView txtAdoptionTimeStamp;

        AdoptionsVH(View v) {
            super(v);
            cardAdoptionContainer = v.findViewById(R.id.cardAdoptionContainer);
            imgvwAdoptionCover = v.findViewById(R.id.imgvwAdoptionCover);
            txtAdoptionName = v.findViewById(R.id.txtAdoptionName);
            txtAdoptionBreed = v.findViewById(R.id.txtAdoptionBreed);
            imgvwGender = v.findViewById(R.id.imgvwGender);
            txtAdoptionTimeStamp = v.findViewById(R.id.txtAdoptionTimeStamp);
        }
    }
}