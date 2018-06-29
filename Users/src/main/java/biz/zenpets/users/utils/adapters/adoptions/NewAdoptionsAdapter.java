package biz.zenpets.users.utils.adapters.adoptions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import biz.zenpets.users.R;
import biz.zenpets.users.details.adoptions.TestAdoptionDetails;
import biz.zenpets.users.utils.adapters.adoptions.promoted.PromotedAdoptionsAdapter;
import biz.zenpets.users.utils.models.adoptions.adoption.Adoption;
import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;

@SuppressWarnings("ConstantConditions")
public class NewAdoptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean isLoadingAdded = false;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Adoption> arrAdoptions;
    private ArrayList<Promotion> arrPromotions = new ArrayList<>();

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private PromotedAdoptionsAdapter adapter;

    public NewAdoptionsAdapter(Activity activity, ArrayList<Adoption> arrAdoptions) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrAdoptions = arrAdoptions;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Adoption data = arrAdoptions.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final AdoptionsVH vh = (AdoptionsVH) holder;

                arrPromotions = data.getPromotions();
                if (arrPromotions.size() > 0)    {
                    Log.e("PROMOTIONS SIZE", String.valueOf(arrPromotions.size()));
                }

                /* SET THE ADOPTION COVER PHOTO */
                String strAdoptionCover = data.getAdoptionCoverPhoto();
                if (strAdoptionCover != null
                        && !strAdoptionCover.equalsIgnoreCase("")
                        && !strAdoptionCover.equalsIgnoreCase("null")) {
                    Uri uri = Uri.parse(strAdoptionCover);
                    vh.imgvwAdoptionCover.setImageURI(uri);
                } else {
                    ImageRequest request = ImageRequestBuilder
                            .newBuilderWithResourceId(R.drawable.empty_graphic)
                            .build();
                    vh.imgvwAdoptionCover.setImageURI(request.getSourceUri());
                }
        
                /* SET THE ADOPTION NAME */
                if (data.getAdoptionName() != null && !data.getAdoptionName().equalsIgnoreCase("")) {
                    vh.txtAdoptionName.setText(data.getAdoptionName());
                } else {
                    vh.txtAdoptionName.setText(activity.getString(R.string.adoption_details_unnamed));
                }
        
                /* SET THE PET'S GENDER */
                if (data.getAdoptionGender() != null)   {
                    if (data.getAdoptionGender().equalsIgnoreCase("male"))  {
                        vh.imgvwGender.setIcon("faw-mars");
                        vh.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_blue_dark));
                    } else if (data.getAdoptionGender().equalsIgnoreCase("female")) {
                        vh.imgvwGender.setIcon("faw-venus");
                        vh.imgvwGender.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark));
                    }
                }
        
                /* SET THE PET'S BREED */
                if (data.getBreedName() != null)    {
                    vh.txtAdoptionBreed.setText(data.getBreedName());
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
                    vh.txtAdoptionTimeStamp.setText(activity.getString(R.string.adoption_details_posted_new, strPrettyDate));
                }
        
                /* SHOW THE ADOPTION DETAILS */
                vh.cardAdoptionContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, TestAdoptionDetails.class);
                        intent.putExtra("ADOPTION_ID", data.getAdoptionID());
                        activity.startActivity(intent);
                    }
                });
                break;
            case LOADING:
                break;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.endless_item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.adoptions_item_new, parent, false);
        viewHolder = new AdoptionsVH(v1);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return arrAdoptions == null ? 0 : arrAdoptions.size();
    }

    public void addAll(ArrayList<Adoption> arrAdoptions) {
        for (Adoption adoption : arrAdoptions) {
            add(adoption);
        }
    }

    private void add(Adoption adoption) {
        arrAdoptions.add(adoption);
        notifyItemInserted(arrAdoptions.size() - 1);
    }

    private void remove(Adoption adoption) {
        int position = arrAdoptions.indexOf(adoption);
        if (position > -1) {
            arrAdoptions.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Adoption());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = arrAdoptions.size() - 1;
        Adoption item = getItem(position);

        if (item != null) {
            arrAdoptions.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Adoption getItem(int position) {
        return arrAdoptions.get(position);
    }

    protected class AdoptionsVH extends RecyclerView.ViewHolder {
        RecyclerView listPromoted;
        CardView cardAdoptionContainer;
        SimpleDraweeView imgvwAdoptionCover;
        TextView txtAdoptionName;
        TextView txtAdoptionBreed;
        IconicsImageView imgvwGender;
        TextView txtAdoptionTimeStamp;

        AdoptionsVH(View v) {
            super(v);
            listPromoted = v.findViewById(R.id.listPromoted);
            cardAdoptionContainer = v.findViewById(R.id.cardAdoptionContainer);
            imgvwAdoptionCover = v.findViewById(R.id.imgvwAdoptionCover);
            txtAdoptionName = v.findViewById(R.id.txtAdoptionName);
            txtAdoptionBreed = v.findViewById(R.id.txtAdoptionBreed);
            imgvwGender = v.findViewById(R.id.imgvwGender);
            txtAdoptionTimeStamp = v.findViewById(R.id.txtAdoptionTimeStamp);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
            llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
            llmAppointments.setAutoMeasureEnabled(true);
            listPromoted.setLayoutManager(llmAppointments);
            listPromoted.setHasFixedSize(true);
            listPromoted.setNestedScrollingEnabled(false);

            /* CONFIGURE THE ADAPTER */
            adapter = new PromotedAdoptionsAdapter(activity, arrPromotions);
            listPromoted.setAdapter(adapter);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}