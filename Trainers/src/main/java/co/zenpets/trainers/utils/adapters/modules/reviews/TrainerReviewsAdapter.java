package co.zenpets.trainers.utils.adapters.modules.reviews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import co.zenpets.trainers.R;
import co.zenpets.trainers.utils.models.trainers.reviews.Review;

public class TrainerReviewsAdapter extends RecyclerView.Adapter<TrainerReviewsAdapter.ReviewsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Review> arrReviews;

    public TrainerReviewsAdapter(Activity activity, ArrayList<Review> arrReviews) {

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
    public void onBindViewHolder(@NonNull final ReviewsVH holder, final int position) {
        final Review data = arrReviews.get(position);
    }

    @NonNull
    @Override
    public ReviewsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.training_modules_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder	{

        final AppCompatTextView txtModuleName;
        IconicsImageView imgvwModuleOptions;
        final AppCompatTextView txtModuleDuration;
        final AppCompatTextView txtModuleDetails;
        ImageView imgvwModuleFormat;
        TextView txtModuleFormat;
        AppCompatTextView txtModuleFees;
        LinearLayout linlaImagesContainer;
        RecyclerView listModuleImages;
        AppCompatTextView txtManageImages;

        ReviewsVH(View v) {
            super(v);
            txtModuleName = v.findViewById(R.id.txtModuleName);
            imgvwModuleOptions = v.findViewById(R.id.imgvwModuleOptions);
            txtModuleDuration = v.findViewById(R.id.txtModuleDuration);
            txtModuleDetails = v.findViewById(R.id.txtModuleDetails);
            imgvwModuleFormat = v.findViewById(R.id.imgvwModuleFormat);
            txtModuleFormat = v.findViewById(R.id.txtModuleFormat);
            txtModuleFees = v.findViewById(R.id.txtModuleFees);
            linlaImagesContainer = v.findViewById(R.id.linlaImagesContainer);
            listModuleImages = v.findViewById(R.id.listModuleImages);
            txtManageImages = v.findViewById(R.id.txtManageImages);
        }
    }
}