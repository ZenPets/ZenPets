package biz.zenpets.users.utils.adapters.kennels;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.adapters.kennels.promoted.PromotedAdoptionsAdapter;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;
import biz.zenpets.users.utils.models.kennels.promotion.Promotion;

public class NewTestKennelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    private static final int ITEM = 0;
    private static final int LOADING = 2;

    private boolean isLoadingAdded = false;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private ArrayList<Kennel> arrKennels = new ArrayList<>();
    private ArrayList<Promotion> arrPromotions = new ArrayList<>();

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private PromotedAdoptionsAdapter adapter;

    /** A LATLNG INSTANCE **/
    LatLng LATLNG_ORIGIN;

    public NewTestKennelsAdapter(Activity activity, ArrayList<Kennel> arrKennels, LatLng LATLNG_ORIGIN) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrKennels = arrKennels;

        /* CAST THE LATLNG VALUES TO THE GLOBAL INSTANCE */
        this.LATLNG_ORIGIN = LATLNG_ORIGIN;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Kennel data = arrKennels.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final KennelsVH vh = (KennelsVH) holder;

                arrPromotions = data.getPromotions();
                if (arrPromotions != null && arrPromotions.size() > 0)    {
                    /* SHOW THE LIST OF PROMOTED ADOPTIONS AND HIDE THE ADOPTION ITEM */
                    adapter = new PromotedAdoptionsAdapter(activity, arrPromotions);
                    vh.listPromoted.setAdapter(adapter);
                    vh.listPromoted.setVisibility(View.VISIBLE);
                    vh.cardKennel.setVisibility(View.GONE);
                } else if (data.getKennelID() != null) {
                    /* SET THE KENNEL COVER PHOTO */
                    String strKennelCoverPhoto = data.getKennelCoverPhoto();
                    if (strKennelCoverPhoto != null
                            && !strKennelCoverPhoto.equalsIgnoreCase("")
                            && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(strKennelCoverPhoto);
                        vh.imgvwKennelCoverPhoto.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        vh.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* SET THE KENNEL NAME */
                    if (data.getKennelName() != null) {
                        vh.txtKennelName.setText(data.getKennelName());
                    }

                    /* SET THE KENNEL ADDRESS */
                    String strKennelAddress = data.getKennelAddress();
                    String cityName = data.getCityName();
                    String kennelPinCode = data.getKennelPinCode();
                    vh.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));

                    /* SET THE CAPACITY OF LARGE SIZE PETS */
                    if (data.getKennelPetCapacity() != null
                            && !data.getKennelPetCapacity().equalsIgnoreCase("")
                            && !data.getKennelPetCapacity().equalsIgnoreCase("null")) {
                        vh.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
                    } else {
                        vh.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
                    }

                    /* SHOW THE ADOPTION ITEM AND HIDE THE LIST OF PROMOTED ADOPTION */
                    vh.cardKennel.setVisibility(View.VISIBLE);
                    vh.listPromoted.setVisibility(View.GONE);
                } else {
                    vh.cardKennel.setVisibility(View.GONE);
                    vh.listPromoted.setVisibility(View.GONE);
                }
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
        View v1 = inflater.inflate(R.layout.kennels_item, parent, false);
        viewHolder = new KennelsVH(v1);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return arrKennels == null ? 0 : arrKennels.size();
    }

    public void addAll(ArrayList<Kennel> arrKennels) {
        for (Kennel adoption : arrKennels) {
            add(adoption);
        }
    }

    private void add(Kennel adoption) {
        arrKennels.add(adoption);
        notifyItemInserted(arrKennels.size() - 1);
    }

    private void remove(Kennel adoption) {
        int position = arrKennels.indexOf(adoption);
        if (position > -1) {
            arrKennels.remove(position);
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
        add(new Kennel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = arrKennels.size() - 1;
        Kennel item = getItem(position);

        if (item != null) {
            arrKennels.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Kennel getItem(int position) {
        return arrKennels.get(position);
    }

    class KennelsVH extends RecyclerView.ViewHolder {

        RecyclerView listPromoted;
        CardView cardKennel;
        SimpleDraweeView imgvwKennelCoverPhoto;
        TextView txtKennelName;
        TextView txtKennelAddress;
        TextView txtPetCapacity;
        TextView txtKennelLikes;
        TextView txtKennelDistance;

        KennelsVH(View v) {
            super(v);

            listPromoted = v.findViewById(R.id.listPromoted);
            cardKennel = v.findViewById(R.id.cardKennel);
            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
            txtKennelName = v.findViewById(R.id.txtKennelName);
            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
            txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
            txtKennelDistance = v.findViewById(R.id.txtKennelDistance);

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

    class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}