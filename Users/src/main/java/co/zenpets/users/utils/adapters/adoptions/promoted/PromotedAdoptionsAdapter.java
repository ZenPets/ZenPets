package co.zenpets.users.utils.adapters.adoptions.promoted;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.adoptions.promotion.Promotion;

@SuppressWarnings("ConstantConditions")
public class PromotedAdoptionsAdapter extends RecyclerView.Adapter<PromotedAdoptionsAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Promotion> arrPromoted;

//    /** THE ADOPTION IMAGES ADAPTER AND ARRAY LIST **/
//    private AdoptionsImagesAdapter adapter;
//    private ArrayList<AdoptionImage> arrImages = new ArrayList<>();

    public PromotedAdoptionsAdapter(Activity activity, ArrayList<Promotion> arrPromoted) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrPromoted = arrPromoted;
    }

    @Override
    public int getItemCount() {
        return arrPromoted == null ? 0 : arrPromoted.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Promotion data = arrPromoted.get(position);

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

        /* SET THE ADOPTION DESCRIPTION */
        if (data.getAdoptionDescription() != null)  {
            holder.txtAdoptionDescription.setText(data.getAdoptionDescription());
        }

        /* SET THE PET'S DETAILS (BREED NAME, PET TYPE NAME AND PET AGE) */
        if (data.getBreedName() != null  && data.getPetTypeName() != null)   {
            String breed = data.getBreedName();
            String petType = data.getPetTypeName();
            String combinedDetails = "Species: \"" + petType + "\" | Breed: \"" + breed + "\"";
            holder.txtPetDetails.setText(combinedDetails);
        }

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

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(lngTimeStamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date currentTimeZone = calendar.getTime();
            String strDate = sdf.format(currentTimeZone);
            holder.txtTimeStamp.setText(activity.getString(R.string.adoption_details_posted, strDate, strPrettyDate));
        }

//        /* SET THE ADOPTION IMAGES */
//        AdoptionImagesAPI api = ZenApiClient.getClient().create(AdoptionImagesAPI.class);
//        Call<AdoptionImages> call = api.fetchAdoptionImages(data.getAdoptionID());
//        call.enqueue(new Callback<AdoptionImages>() {
//            @Override
//            public void onResponse(Call<AdoptionImages> call, Response<AdoptionImages> response) {
//                if (response.isSuccessful() && response.body().getImages() != null)    {
//                    arrImages = response.body().getImages();
//
//                    /* CHECK FOR ARRAY SIZE */
//                    if (arrImages.size() > 0 && arrImages != null)  {
//                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
//                        holder.listAdoptionImages.setAdapter(new AdoptionsImagesAdapter(activity, arrImages));
//
//                        /* SHOW THE RECYCLER VIEW */
//                        holder.listAdoptionImages.setVisibility(View.VISIBLE);
//                    } else {
//                        /* HIDE THE RECYCLER VIEW */
//                        holder.listAdoptionImages.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* HIDE THE RECYCLER VIEW */
//                    holder.linlaAdoptionImages.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AdoptionImages> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });
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
        final SimpleDraweeView imgvwAdoptionCover;
        final TextView txtAdoptionName;
        final TextView txtAdoptionDescription;
        final TextView txtPetDetails;
        final TextView txtTimeStamp;
//        LinearLayout linlaAdoptionImages;
//        RecyclerView listAdoptionImages;

        AdoptionsVH(View v) {
            super(v);
            imgvwAdoptionCover = v.findViewById(R.id.imgvwAdoptionCover);
            txtAdoptionName = v.findViewById(R.id.txtAdoptionName);
            txtAdoptionDescription = v.findViewById(R.id.txtAdoptionDescription);
            txtPetDetails = v.findViewById(R.id.txtPetDetails);
            txtTimeStamp = v.findViewById(R.id.txtTimeStamp);
//            linlaAdoptionImages = v.findViewById(R.id.linlaAdoptionImages);
//            listAdoptionImages = v.findViewById(R.id.listAdoptionImages);
//
//            /* CONFIGURE THE RECYCLER VIEW */
//            LinearLayoutManager manager = new LinearLayoutManager(activity);
//            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            manager.setAutoMeasureEnabled(true);
//            listAdoptionImages.setLayoutManager(manager);
//            listAdoptionImages.setHasFixedSize(true);
//            listAdoptionImages.setNestedScrollingEnabled(false);
//
//            /* CONFIGURE THE ADAPTER */
//            adapter = new AdoptionsImagesAdapter(activity, arrImages);
//            listAdoptionImages.setAdapter(adapter);
        }
    }
}