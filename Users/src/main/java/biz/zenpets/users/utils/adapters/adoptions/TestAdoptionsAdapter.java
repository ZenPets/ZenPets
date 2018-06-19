package biz.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImage;

public class TestAdoptionsAdapter extends RecyclerView.Adapter<TestAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Adoption> arrAdoptions;

    /** THE ADOPTIONS IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<AdoptionImage> arrImages = new ArrayList<>();
    private AdoptionsImagesAdapter adapter;

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
    public void onBindViewHolder(@NonNull AdoptionsVH holder, final int position) {
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

        /* SET THE DESCRIPTION */
        if (data.getAdoptionDescription() != null)  {
            holder.txtAdoptionDescription.setText(data.getAdoptionDescription());
        }

        /* SET THE ADOPTION TIME STAMP */
        if (data.getAdoptionTimeStamp() != null)    {
            String strPrettyDate = data.getAdoptionPrettyDate();
            String strDate = data.getAdoptionTimeStamp();
            holder.txtTimeStamp.setText(activity.getString(R.string.adoption_details_posted, strDate, strPrettyDate));
        }

//        /* SET THE PET'S GENDER */
//        if (data.getAdoptionGender().equalsIgnoreCase("male"))  {
//            holder.imgvwGender.setIcon("faw-mars");
//            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
//        } else if (data.getAdoptionGender().equalsIgnoreCase("female")) {
//            holder.imgvwGender.setIcon("faw-venus");
//            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
//        }

//        /* SET THE TIMESTAMP (DATE OF CREATION )*/
//        if (data.getAdoptionTimeStamp() != null)    {
//            String adoptionTimeStamp = data.getAdoptionTimeStamp();
////            Log.e("TS", adoptionTimeStamp);
//            long lngTimeStamp = Long.parseLong(adoptionTimeStamp) * 1000;
//
//            /* GET THE PRETTY DATE */
//            Calendar calPretty = Calendar.getInstance(Locale.getDefault());
//            calPretty.setTimeInMillis(lngTimeStamp);
//            Date date = calPretty.getTime();
//            PrettyTime prettyTime = new PrettyTime();
//            String strPrettyDate = prettyTime.format(date);
//
//            Calendar calendar = Calendar.getInstance(Locale.getDefault());
//            calendar.setTimeInMillis(lngTimeStamp);
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//            Date currentTimeZone = calendar.getTime();
//            String strDate = sdf.format(currentTimeZone);
////            holder.txtTimeStamp.setText("Posted on: " + strDate + " (" + strPrettyDate + ")");
//            holder.txtTimeStamp.setText(activity.getString(R.string.adoption_details_posted, strDate, strPrettyDate));
//        }
//
//        /* SET THE PET'S DETAILS (BREED NAME, PET TYPE NAME AND PET AGE) */
//        if (data.getBreedName() != null  && data.getPetTypeName() != null)   {
//            String breed = data.getBreedName();
//            String petType = data.getPetTypeName();
////            String combinedDetails = "The Pet is a \"" + petType + "\" of the Breed \"" + breed + "\"";
//            String combinedDetails = "Species: \"" + petType + "\" | Breed: \"" + breed + "\"";
//            holder.txtPetDetails.setText(combinedDetails);
//        }
//
//        /* SET THE VACCINATED STATUS */
//        if (data.getAdoptionVaccinated() != null)  {
//            holder.txtVaccinated.setText(data.getAdoptionVaccinated());
//            if (data.getAdoptionVaccinated().equalsIgnoreCase("yes"))  {
//                holder.txtVaccinated.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
//            } else if (data.getAdoptionVaccinated().equalsIgnoreCase("no"))    {
//                holder.txtVaccinated.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
//            }
//        }
//
//        /* SET THE DEWORMED STATUS */
//        if (data.getAdoptionDewormed() != null) {
//            holder.txtDewormed.setText(data.getAdoptionDewormed());
//            if (data.getAdoptionDewormed().equalsIgnoreCase("yes")) {
//                holder.txtDewormed.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
//            } else if (data.getAdoptionDewormed().equalsIgnoreCase("no"))   {
//                holder.txtDewormed.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
//            }
//        }
//
//        /* SET THE NEUTERED STATUS */
//        if (data.getAdoptionNeutered() != null) {
//            holder.txtNeutered.setText(data.getAdoptionNeutered());
//            if (data.getAdoptionNeutered().equalsIgnoreCase("yes")) {
//                holder.txtNeutered.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
//            } else if (data.getAdoptionNeutered().equalsIgnoreCase("no"))   {
//                holder.txtNeutered.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
//            }
//        }
//
////        /* SET THE ADOPTION STATUS */
////        if (data.getAdoptionStatus() != null)   {
////            holder.txtAdopted.setText(data.getAdoptionStatus());
////            if (data.getAdoptionStatus().equalsIgnoreCase("Open"))  {
////                holder.txtAdopted.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_green_dark));
////            } else if (data.getAdoptionStatus().equalsIgnoreCase("Adopted"))    {
////                holder.txtAdopted.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
////            }
////        } else {
////            holder.txtAdopted.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
////        }
//
//        /* SHOW THE ADOPTION DETAILS */
//        holder.linlaAdoptionContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, AdoptionDetails.class);
//                intent.putExtra("ADOPTION_ID", data.getAdoptionID());
//                activity.startActivity(intent);
//            }
//        });
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.adoptions_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{

        SimpleDraweeView imgvwAdoptionCover;
        TextView txtAdoptionName;
        TextView txtAdoptionDescription;
        TextView txtPetDetails;
        TextView txtTimeStamp;
        LinearLayout linlaAdoptionImages;
        RecyclerView listAdoptionImages;

        AdoptionsVH(View v) {
            super(v);
            imgvwAdoptionCover = v.findViewById(R.id.imgvwAdoptionCover);
            txtAdoptionName = v.findViewById(R.id.txtAdoptionName);
            txtAdoptionDescription = v.findViewById(R.id.txtAdoptionDescription);
            txtPetDetails = v.findViewById(R.id.txtPetDetails);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
            linlaAdoptionImages = v.findViewById(R.id.linlaAdoptionImages);
            listAdoptionImages = v.findViewById(R.id.listAdoptionImages);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
            llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
            llmAppointments.setAutoMeasureEnabled(true);
            listAdoptionImages.setLayoutManager(llmAppointments);
            listAdoptionImages.setHasFixedSize(true);
            listAdoptionImages.setNestedScrollingEnabled(false);

            /* CONFIGURE THE ADAPTER */
            adapter = new AdoptionsImagesAdapter(activity, arrImages);
            listAdoptionImages.setAdapter(adapter);
        }
    }
}