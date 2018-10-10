package co.zenpets.users.utils.adapters.boardings;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.zenpets.users.R;
import co.zenpets.users.utils.AppPrefs;
import co.zenpets.users.utils.models.boarding.Boarding;

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
//                    Log.e("BOARDING ID", data.getBoardingID());

                    /* SET THE BOARDING'S COVER PHOTO */
                    String boardingCoverPhoto = data.getBoardingCoverPhoto();
                    if (boardingCoverPhoto != null
                            && !boardingCoverPhoto.equalsIgnoreCase("")
                            && !boardingCoverPhoto.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(boardingCoverPhoto);
                        vh.imgvwBoardingCoverPhoto.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        vh.imgvwBoardingCoverPhoto.setImageURI(request.getSourceUri());
                    }

                    /* SET THE USER'S NAME */
                    if (data.getUserName() != null) {
                        vh.txtUserName.setText(data.getUserName());
                    }

                    /* SET THE USER'S DISPLAY PROFILE */
                    String userDisplayProfile = data.getUserDisplayProfile();
                    if (userDisplayProfile != null
                            && !userDisplayProfile.equalsIgnoreCase("")
                            && !userDisplayProfile.equalsIgnoreCase("null"))    {
                        Uri uri = Uri.parse(userDisplayProfile);
                        vh.imgvwUserDisplayProfile.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.ic_person_black_24dp)
                                .build();
                        vh.imgvwUserDisplayProfile.setImageURI(request.getSourceUri());
                    }

                    /* SET THE BOARDING ADDRESS */
                    String boardingAddress = data.getBoardingAddress();
                    String cityName = data.getCityName();
                    String boardingPincode = data.getBoardingPincode();
                    ((BoardingsVH) holder).txtBoardingAddress.setText(activity.getString(R.string.doctor_list_address_placeholder, boardingAddress, cityName, boardingPincode));

                    /* SET THE BOARDING DISTANCE */
                    String boardingDistance = data.getBoardingDistance();
                    String strTilde = activity.getString(R.string.generic_tilde);
                    vh.txtBoardingDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, boardingDistance));

                    /* SHOW THE BOARDINGS CARD */
                    vh.cardBoarding.setVisibility(View.VISIBLE);
                } else {
                    /* HIDE THE BOARDINGS CARD */
                    vh.cardBoarding.setVisibility(View.GONE);
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
        View v1 = inflater.inflate(R.layout.home_boarding_item, parent, false);
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
        SimpleDraweeView imgvwBoardingCoverPhoto;
        SimpleDraweeView imgvwUserDisplayProfile;
        TextView txtUserName;
        TextView txtBoardingAddress;
        TextView txtBoardingLikes;
        RatingBar boardingRating;
        TextView txtBoardingDistance;

        BoardingsVH(View v) {
            super(v);

            cardBoarding = v.findViewById(R.id.cardBoarding);
            imgvwBoardingCoverPhoto = v.findViewById(R.id.imgvwBoardingCoverPhoto);
            imgvwUserDisplayProfile = v.findViewById(R.id.imgvwUserDisplayProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtBoardingAddress = v.findViewById(R.id.txtBoardingAddress);
            txtBoardingLikes = v.findViewById(R.id.txtBoardingLikes);
            boardingRating = v.findViewById(R.id.boardingRating);
            txtBoardingDistance = v.findViewById(R.id.txtBoardingDistance);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}