package biz.zenpets.users.utils.adapters.boardings;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.models.boarding.Boarding;

public class BoardingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppPrefs getApp()	{
        return (AppPrefs) AppPrefs.context().getApplicationContext();
    }

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    private static final int ITEM = 0;
    private static final int LOADING = 2;

    private boolean isLoadingAdded = false;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private ArrayList<Boarding> arrBoardings = new ArrayList<>();

    /** A LATLNG INSTANCE TO HOLD THE CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** GET THE USER ID AND THE CURRENT DATE **/
    String USER_ID = null;
    String CURRENT_DATE = null;

    public BoardingsAdapter(Activity activity, ArrayList<Boarding> arrBoardings, LatLng LATLNG_ORIGIN) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrBoardings = arrBoardings;

        /* CAST THE CONTENTS OF THE LATLNG INSTANCE TO THE LOCAL INSTANCE */
        this.LATLNG_ORIGIN = LATLNG_ORIGIN;

        /* GET THE USER ID */
        USER_ID = getApp().getUserID();
//        Log.e("USER ID", USER_ID);

        /* GET THE CURRENT DATE IN THE REQUIRED FORMAT */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        CURRENT_DATE = format.format(date);
//        Log.e("CURRENT DATE", CURRENT_DATE);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Boarding data = arrBoardings.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final BoardingsVH vh = (BoardingsVH) holder;

                if (data.getBoardingID() != null)   {
                    Log.e("BOARDING ID", data.getBoardingID());

                    /* SHOW THE BOARDINGS CARD */
                    vh.cardBoarding.setVisibility(View.VISIBLE);
                } else {
                    /* HIDE THE BOARDINGS CARD */
                    vh.cardBoarding.setVisibility(View.GONE);
                }

//                /* SET THE KENNEL COVER PHOTO */
//                String strKennelCoverPhoto = data.getKennelCoverPhoto();
//                if (strKennelCoverPhoto != null
//                        && !strKennelCoverPhoto.equalsIgnoreCase("")
//                        && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
//                    Uri uri = Uri.parse(strKennelCoverPhoto);
//                    vh.imgvwKennelCoverPhoto.setImageURI(uri);
//                } else {
//                    ImageRequest request = ImageRequestBuilder
//                            .newBuilderWithResourceId(R.drawable.empty_graphic)
//                            .build();
//                    vh.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
//                }
//
//                /* SET THE KENNEL NAME */
//                if (data.getKennelName() != null) {
//                    vh.txtKennelName.setText(data.getKennelName());
//                }
//
//                /* SET THE KENNEL ADDRESS */
//                String strKennelAddress = data.getKennelAddress();
//                String cityName = data.getCityName();
//                String kennelPinCode = data.getKennelPinCode();
//                vh.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));
//
//                /* SET THE KENNEL DISTANCE */
//                String kennelDistance = data.getKennelDistance();
//                String strTilde = activity.getString(R.string.generic_tilde);
//                vh.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, kennelDistance));
//
//                /* SET THE PET CAPACITY */
//                if (data.getKennelPetCapacity() != null
//                        && !data.getKennelPetCapacity().equalsIgnoreCase("")
//                        && !data.getKennelPetCapacity().equalsIgnoreCase("null")) {
//                    vh.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
//                } else {
//                    vh.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
//                }
//
//                /* SET THE REVIEW VOTE STATS */
//                vh.txtKennelLikes.setText(data.getKennelVoteStats());
//
//                /* SET THE AVERAGE KENNEL RATING */
//                Float dblRating = Float.valueOf(data.getKennelRating());
//                vh.kennelRating.setRating(dblRating);
//
//                /* SHOW THE KENNEL DETAILS */
//                vh.cardKennel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(activity, KennelDetails.class);
//                        intent.putExtra("KENNEL_ID", data.getKennelID());
//                        intent.putExtra("ORIGIN_LATITUDE", String.valueOf(LATLNG_ORIGIN.latitude));
//                        intent.putExtra("ORIGIN_LONGITUDE", String.valueOf(LATLNG_ORIGIN.longitude));
//                        activity.startActivity(intent);
//                    }
//                });
//
//                /* PUBLISH A NEW KENNEL VIEWED STATUS */
//                StatisticsAPI api = ZenApiClient.getClient().create(StatisticsAPI.class);
//                Call<Stat> call = api.publishKennelViewStatus(data.getKennelID(), USER_ID, CURRENT_DATE);
//                call.enqueue(new Callback<Stat>() {
//                    @Override
//                    public void onResponse(Call<Stat> call, Response<Stat> response) {
////                            if (response.body().getMessage() != null)
////                                Log.e("MESSAGE", response.body().getMessage());
//                    }
//
//                    @Override
//                    public void onFailure(Call<Stat> call, Throwable t) {
////                            Log.e("VIEWED FAILED", t.getMessage());
//                        Crashlytics.logException(t);
//                    }
//                });

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
        viewHolder = new BoardingsVH(v1);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return arrBoardings == null ? 0 : arrBoardings.size();
    }

    public void addAll(ArrayList<Boarding> arrBoardings) {
        for (Boarding boarding : arrBoardings) {
            add(boarding);
        }
    }

    private void add(Boarding boarding) {
        arrBoardings.add(boarding);
        notifyItemInserted(arrBoardings.size() - 1);
    }

    private void remove(Boarding boarding) {
        int position = arrBoardings.indexOf(boarding);
        if (position > -1) {
            arrBoardings.remove(position);
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
        add(new Boarding());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = arrBoardings.size() - 1;
        Boarding item = getItem(position);

        if (item != null) {
            arrBoardings.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Boarding getItem(int position) {
        return arrBoardings.get(position);
    }

    class BoardingsVH extends RecyclerView.ViewHolder {

        CardView cardBoarding;
        SimpleDraweeView imgvwKennelCoverPhoto;
        TextView txtKennelName;
        TextView txtKennelAddress;
        TextView txtPetCapacity;
        TextView txtKennelLikes;
        RatingBar kennelRating;
        TextView txtKennelDistance;

        BoardingsVH(View v) {
            super(v);

            cardBoarding = v.findViewById(R.id.cardKennel);
            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
            txtKennelName = v.findViewById(R.id.txtKennelName);
            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
            txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
            kennelRating = v.findViewById(R.id.kennelRating);
            txtKennelDistance = v.findViewById(R.id.txtKennelDistance);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}