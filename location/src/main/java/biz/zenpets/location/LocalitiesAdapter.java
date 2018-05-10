package biz.zenpets.location;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LocalitiesAdapter extends RecyclerView.Adapter<LocalitiesAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<String> arrReviews;

    public LocalitiesAdapter(Activity activity, ArrayList<String> arrReviews) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrReviews = arrReviews;
    }

    @Override
    public int getItemCount() {
        return arrReviews.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsVH holder, final int position) {

        if (arrReviews.get(position) != null)   {
            holder.txtLocalityName.setText(arrReviews.get(position));
        }
    }

    @Override
    public ReviewsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.localities_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{
        TextView txtLocalityName;

        ReviewsVH(View v) {
            super(v);
            txtLocalityName = v.findViewById(R.id.txtLocalityName);
        }
    }
}