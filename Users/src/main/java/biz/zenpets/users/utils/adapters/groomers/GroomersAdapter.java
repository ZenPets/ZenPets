package biz.zenpets.users.utils.adapters.groomers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.details.groomer.GroomerDetails;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.adapters.groomers.promoted.PromotedGroomersAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.groomers.groomers.Groomer;
import biz.zenpets.users.utils.models.groomers.promotion.Promotion;
import biz.zenpets.users.utils.models.groomers.statistics.GroomerStats;
import biz.zenpets.users.utils.models.groomers.statistics.GroomerStatsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroomersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppPrefs getApp()	{
        return (AppPrefs) AppPrefs.context().getApplicationContext();
    }

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    private static final int ITEM = 0;
    private static final int LOADING = 2;

    private boolean isLoadingAdded = false;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private ArrayList<Groomer> arrGroomers;
    private ArrayList<Promotion> arrPromotions = new ArrayList<>();

    /** THE PROMOTED GROOMERS ADAPTER **/
    private PromotedGroomersAdapter adapter;

    /** A LATLNG INSTANCE TO HOLD THE CURRENT COORDINATES **/
    private LatLng LATLNG_ORIGIN;

    /** GET THE USER ID AND THE CURRENT DATE **/
    String USER_ID = null;
    String CURRENT_DATE = null;

    public GroomersAdapter(Activity activity, ArrayList<Groomer> arrGroomers, LatLng LATLNG_ORIGIN) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrGroomers = arrGroomers;

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
        final Groomer data = arrGroomers.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final KennelsVH vh = (KennelsVH) holder;

                arrPromotions = data.getPromotions();
                if (arrPromotions != null && arrPromotions.size() > 0)    {
//                    Log.e("PROMOTIONS SIZE", String.valueOf(arrPromotions.size()));
                    /* SHOW THE LIST OF PROMOTED ADOPTIONS AND HIDE THE ADOPTION ITEM */
                    adapter = new PromotedGroomersAdapter(activity, arrPromotions);
                    vh.listPromoted.setAdapter(adapter);
                    vh.listPromoted.setVisibility(View.VISIBLE);
                    vh.cardGroomer.setVisibility(View.GONE);
                } else if (data.getGroomerID() != null) {
                    /* SET THE GROOMER'S LOGO */
                    String groomerLogo = data.getGroomerLogo();
                    if (groomerLogo != null
                            && !groomerLogo.equalsIgnoreCase("")
                            && !groomerLogo.equalsIgnoreCase("null")) {
                        Uri uri = Uri.parse(groomerLogo);
                        vh.imgvwGroomerLogo.setImageURI(uri);
                    } else {
                        ImageRequest request = ImageRequestBuilder
                                .newBuilderWithResourceId(R.drawable.empty_graphic)
                                .build();
                        vh.imgvwGroomerLogo.setImageURI(request.getSourceUri());
                    }

                    /* SET THE GROOMER'S NAME */
                    if (data.getGroomerName() != null) {
                        vh.txtGroomerName.setText(data.getGroomerName());
                    }

                    /* SET THE GROOMER'S ADDRESS */
                    String groomerAddress = data.getGroomerAddress();
                    String cityName = data.getCityName();
                    String groomerPincode = data.getGroomerPincode();
                    vh.txtGroomerAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, groomerAddress, cityName, groomerPincode));

                    /* SET THE GROOMER'S DISTANCE */
                    String groomerDistance = data.getGroomerDistance();
                    String strTilde = activity.getString(R.string.generic_tilde);
                    vh.txtGroomerDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, groomerDistance));

                    /* SET THE GROOMER'S REVIEW VOTE STATS */
                    vh.txtGroomerLikes.setText(data.getGroomerVoteStats());

                    /* SET THE AVERAGE GROOMER'S AVERAGE RATING */
                    Float dblRating = Float.valueOf(data.getGroomerRating());
                    vh.groomerRating.setRating(dblRating);

                    /* SHOW THE GROOMER DETAILS */
                    vh.cardGroomer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, GroomerDetails.class);
                            intent.putExtra("GROOMER_ID", data.getGroomerID());
                            intent.putExtra("ORIGIN_LATITUDE", String.valueOf(LATLNG_ORIGIN.latitude));
                            intent.putExtra("ORIGIN_LONGITUDE", String.valueOf(LATLNG_ORIGIN.longitude));
                            activity.startActivity(intent);
                        }
                    });

                    /* PUBLISH A NEW GROOMER VIEW STATUS */
                    GroomerStatsAPI api = ZenApiClient.getClient().create(GroomerStatsAPI.class);
                    Call<GroomerStats> call = api.publishGroomerViewStatus(data.getGroomerID(), USER_ID, CURRENT_DATE);
                    call.enqueue(new Callback<GroomerStats>() {
                        @Override
                        public void onResponse(Call<GroomerStats> call, Response<GroomerStats> response) {
//                            if (response.body().getMessage() != null)
//                                Log.e("MESSAGE", response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<GroomerStats> call, Throwable t) {
                            Log.e("VIEWED FAILED", t.getMessage());
                            Crashlytics.logException(t);
                        }
                    });

                    /* SHOW THE KENNEL ITEM AND HIDE THE LIST OF PROMOTED ADOPTION */
                    vh.cardGroomer.setVisibility(View.VISIBLE);
                    vh.listPromoted.setVisibility(View.GONE);
                } else {
                    vh.cardGroomer.setVisibility(View.GONE);
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
        View v1 = inflater.inflate(R.layout.groomers_item, parent, false);
        viewHolder = new KennelsVH(v1);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return arrGroomers == null ? 0 : arrGroomers.size();
    }

    public void addAll(ArrayList<Groomer> arrGroomers) {
        for (Groomer adoption : arrGroomers) {
            add(adoption);
        }
    }

    private void add(Groomer adoption) {
        arrGroomers.add(adoption);
        notifyItemInserted(arrGroomers.size() - 1);
    }

    private void remove(Groomer adoption) {
        int position = arrGroomers.indexOf(adoption);
        if (position > -1) {
            arrGroomers.remove(position);
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
        add(new Groomer());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = arrGroomers.size() - 1;
        Groomer item = getItem(position);

        if (item != null) {
            arrGroomers.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Groomer getItem(int position) {
        return arrGroomers.get(position);
    }

    class KennelsVH extends RecyclerView.ViewHolder {

        RecyclerView listPromoted;
        CardView cardGroomer;
        SimpleDraweeView imgvwGroomerLogo;
        TextView txtGroomerName;
        TextView txtGroomerAddress;
        TextView txtGroomerLikes;
        RatingBar groomerRating;
        TextView txtGroomerDistance;

        KennelsVH(View v) {
            super(v);

            listPromoted = v.findViewById(R.id.listPromoted);
            cardGroomer = v.findViewById(R.id.cardGroomer);
            imgvwGroomerLogo = v.findViewById(R.id.imgvwGroomerLogo);
            txtGroomerName = v.findViewById(R.id.txtGroomerName);
            txtGroomerAddress = v.findViewById(R.id.txtGroomerAddress);
            txtGroomerLikes = v.findViewById(R.id.txtGroomerLikes);
            groomerRating = v.findViewById(R.id.groomerRating);
            txtGroomerDistance = v.findViewById(R.id.txtGroomerDistance);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            manager.setAutoMeasureEnabled(true);
            listPromoted.setLayoutManager(manager);
            listPromoted.setHasFixedSize(true);
            listPromoted.setNestedScrollingEnabled(false);

            /* CONFIGURE THE ADAPTER */
            adapter = new PromotedGroomersAdapter(activity, arrPromotions);
            listPromoted.setAdapter(adapter);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}