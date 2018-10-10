package co.zenpets.users.utils.adapters.adoptions.user;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.adoptions.adoption.Adoption;

public class UserAdoptionsAdapter extends RecyclerView.Adapter<UserAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Adoption> arrAdoptions;

    public UserAdoptionsAdapter(Activity activity, ArrayList<Adoption> arrAdoptions) {

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

//        /* SET THE PET'S GENDER */
//        if (data.getAdoptionGender().equalsIgnoreCase("male"))  {
//            holder.imgvwGender.setIcon("faw-mars");
//            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
//        } else if (data.getAdoptionGender().equalsIgnoreCase("female")) {
//            holder.imgvwGender.setIcon("faw-venus");
//            holder.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
//        }
//
//        /* SET THE DESCRIPTION */
//        if (data.getAdoptionDescription() != null)  {
//            holder.txtAdoptionDescription.setText(data.getAdoptionDescription());
//        }
//
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
//        /* SHOW THE ADOPTION DETAILS */
//        holder.linlaAdoptionContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, UserAdoptionDetails.class);
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
                inflate(R.layout.user_adoptions_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{

//        final LinearLayout linlaAdoptionContainer;
////        final AppCompatTextView txtAdoptionName;
//        final IconicsImageView imgvwGender;
//        final AppCompatTextView txtAdoptionDescription;
//        final AppCompatTextView txtNoImages;
//        final RecyclerView listAdoptionImages;
//        final AppCompatTextView txtTimeStamp;
//        final AppCompatTextView txtPetDetails;
//        final AppCompatTextView txtVaccinated;
//        final AppCompatTextView txtDewormed;
//        final AppCompatTextView txtNeutered;
//        final AppCompatTextView txtAdopted;

        AdoptionsVH(View v) {
            super(v);

//            linlaAdoptionContainer = v.findViewById(R.id.linlaAdoptionContainer);
////            txtAdoptionName = v.findViewById(R.id.txtAdoptionName);
//            imgvwGender = v.findViewById(R.id.imgvwGender);
//            txtAdoptionDescription = v.findViewById(R.id.txtAdoptionDescription);
//            txtNoImages = v.findViewById(R.id.txtNoImages);
//            listAdoptionImages = v.findViewById(R.id.listAdoptionImages);
//            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
//            txtPetDetails = v.findViewById(R.id.txtPetDetails);
//            txtVaccinated = v.findViewById(R.id.txtVaccinated);
//            txtDewormed = v.findViewById(R.id.txtDewormed);
//            txtNeutered = v.findViewById(R.id.txtNeutered);
//            txtAdopted = v.findViewById(R.id.txtAdopted);
        }
    }
}