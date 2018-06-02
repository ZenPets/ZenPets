package biz.zenpets.users.utils.adapters.kennels.reviews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.kennels.reviews.KennelReview;

public class KennelReviewsAdapter extends RecyclerView.Adapter<KennelReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<KennelReview> arrReviews;

    public KennelReviewsAdapter(Activity activity, ArrayList<KennelReview> arrReviews) {

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
        KennelReview data = arrReviews.get(position);
    }

    @NonNull
    @Override
    public ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennel_review_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{
        ReviewsVH(View v) {
            super(v);
        }
    }
}