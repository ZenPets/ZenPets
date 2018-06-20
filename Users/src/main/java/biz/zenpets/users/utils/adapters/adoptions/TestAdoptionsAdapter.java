package biz.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.details.adoptions.TestAdoptionDetails;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImage;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImages;
import biz.zenpets.users.utils.models.adoptions.images.AdoptionImagesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
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

        /* SET THE DESCRIPTION */
        if (data.getAdoptionDescription() != null)  {
            holder.txtAdoptionDescription.setText(data.getAdoptionDescription());
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

        /* SET THE PET'S DETAILS (BREED NAME, PET TYPE NAME AND PET AGE) */
        if (data.getBreedName() != null  && data.getAdoptionGender() != null)   {
            String strPetDetails = "The pet is a " + data.getAdoptionGender() + ", " + data.getBreedName();
            holder.txtPetDetails.setText(strPetDetails);
        }

        /* SET THE ADOPTION IMAGES */
        AdoptionImagesAPI apiImages = ZenApiClient.getClient().create(AdoptionImagesAPI.class);
        Call<AdoptionImages> callImages = apiImages.fetchTestAdoptionImages(data.getAdoptionID());
        callImages.enqueue(new Callback<AdoptionImages>() {
            @Override
            public void onResponse(Call<AdoptionImages> call, Response<AdoptionImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0)   {
                        /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                        adapter = new AdoptionsImagesAdapter(activity, arrImages);
                        holder.listAdoptionImages.setAdapter(adapter);

                        /* SHOW THE IMAGES CONTAINER */
                        holder.linlaAdoptionImages.setVisibility(View.VISIBLE);
                    } else {
                        /* HIDE THE IMAGES CONTAINER */
                        holder.linlaAdoptionImages.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE IMAGES CONTAINER */
                    holder.linlaAdoptionImages.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AdoptionImages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });

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
                inflate(R.layout.adoptions_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    @SuppressWarnings("deprecation")
    class AdoptionsVH extends RecyclerView.ViewHolder	{
        CardView cardAdoptionContainer;
        SimpleDraweeView imgvwAdoptionCover;
        TextView txtAdoptionName;
        TextView txtAdoptionDescription;
        TextView txtPetDetails;
        TextView txtTimeStamp;
        LinearLayout linlaAdoptionImages;
        RecyclerView listAdoptionImages;

        AdoptionsVH(View v) {
            super(v);
            cardAdoptionContainer = v.findViewById(R.id.cardAdoptionContainer);
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