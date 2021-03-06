package co.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.mikepenz.iconics.view.IconicsImageView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.details.adoptions.TestAdoptionDetails;
import co.zenpets.users.utils.models.adoptions.adoption.Adoption;

@SuppressWarnings("ConstantConditions")
public class TestAdoptionsAdapter extends RecyclerView.Adapter<TestAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Adoption> arrAdoptions;

    public TestAdoptionsAdapter(Activity activity, ArrayList<Adoption> arrAdoptions) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrAdoptions = arrAdoptions;
    }

    @Override
    public int getItemCount() {
        return arrAdoptions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Adoption data = arrAdoptions.get(position);

        /* SET THE ADOPTION COVER PHOTO */
        String strAdoptionCover = data.getAdoptionCoverPhoto();
        if (strAdoptionCover != null
                && !strAdoptionCover.equalsIgnoreCase("")
                && !strAdoptionCover.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(strAdoptionCover);
            holder.imgvwAdoptionCover.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwAdoptionCover.setImageURI(request.getSourceUri());
        }

        /* SET THE ADOPTION NAME */
        if (data.getAdoptionName() != null && !data.getAdoptionName().equalsIgnoreCase("")) {
            holder.txtAdoptionName.setText(data.getAdoptionName());
        } else {
            holder.txtAdoptionName.setText(activity.getString(R.string.adoption_details_unnamed));
        }

        /* SET THE PET'S GENDER */
        if (data.getAdoptionGender().equalsIgnoreCase("male"))  {
            holder.imgvwGender.setIcon("faw-mars");
            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
        } else if (data.getAdoptionGender().equalsIgnoreCase("female")) {
            holder.imgvwGender.setIcon("faw-venus");
            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
        }

        /* SET THE PET'S BREED */
        if (data.getBreedName() != null)    {
            holder.txtAdoptionBreed.setText(data.getBreedName());
        }
//
        /* SET THE TIMESTAMP (DATE OF CREATION )*/
        if (data.getAdoptionTimeStamp() != null)    {
            String adoptionTimeStamp = data.getAdoptionTimeStamp();
            long lngTimeStamp = Long.parseLong(adoptionTimeStamp) * 1000;

            /* GET THE PRETTY DATE */
            Calendar calPretty = Calendar.getInstance(Locale.getDefault());
            calPretty.setTimeInMillis(lngTimeStamp);
            Date date = calPretty.getTime();
            PrettyTime prettyTime = new PrettyTime();
            String strPrettyDate = prettyTime.format(date);
            holder.txtAdoptionTimeStamp.setText(activity.getString(R.string.adoption_details_posted_new, strPrettyDate));
        }

        /* SHOW THE ADOPTION DETAILS */
        holder.cardAdoptionContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TestAdoptionDetails.class);
                intent.putExtra("ADOPTION_ID", data.getAdoptionID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.adoptions_item_new, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{
        final CardView cardAdoptionContainer;
        final SimpleDraweeView imgvwAdoptionCover;
        final TextView txtAdoptionName;
        final TextView txtAdoptionBreed;
        final IconicsImageView imgvwGender;
        final TextView txtAdoptionTimeStamp;

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