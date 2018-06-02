package biz.zenpets.users.utils.adapters.kennels;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
import biz.zenpets.users.details.kennels.KennelDetails;
import biz.zenpets.users.utils.models.kennels.kennels.Kennel;

public class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Kennel> arrKennels;

    /** THE DATA TYPES FOR CALCULATING THE LIKES PERCENTAGE  **/
    private int TOTAL_LIKES = 0;
    private int TOTAL_VOTES = 0;

    /** THE ORIGIN **/
    private LatLng LATLNG_ORIGIN;

    public KennelsAdapter(Activity activity, ArrayList<Kennel> arrKennels, LatLng LATLNG_ORIGIN) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrKennels = arrKennels;

        /* CAST THE ORIGIN TO THE GLOBAL INSTANCE */
        this.LATLNG_ORIGIN = LATLNG_ORIGIN;
    }

    @Override
    public int getItemCount() {
        return arrKennels.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final KennelsVH holder, final int position) {
        final Kennel data = arrKennels.get(position);

        /* SET THE KENNEL COVER PHOTO */
        String strKennelCoverPhoto = data.getKennelCoverPhoto();
        if (strKennelCoverPhoto != null
                && !strKennelCoverPhoto.equalsIgnoreCase("")
                && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
            Uri uri = Uri.parse(strKennelCoverPhoto);
            holder.imgvwKennelCoverPhoto.setImageURI(uri);
        } else {
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithResourceId(R.drawable.empty_graphic)
                    .build();
            holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
        }

        /* SET THE KENNEL NAME */
        if (data.getKennelName() != null)   {
            holder.txtKennelName.setText(data.getKennelName());
        }

        /* SET THE KENNEL ADDRESS */
        String strKennelAddress = data.getKennelAddress();
        String cityName = data.getCityName();
        String kennelPinCode = data.getKennelPinCode();
        holder.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));

        /* SET THE CAPACITY OF LARGE SIZE PETS */
        if (data.getKennelPetCapacity() != null
                && !data.getKennelPetCapacity().equalsIgnoreCase("")
                && !data.getKennelPetCapacity().equalsIgnoreCase("null"))   {
            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
        } else {
            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
        }

//        /* GET THE TOTAL NUMBER OF POSITIVE REVIEWS */
//        KennelReviewsAPI apiYes = ZenApiClient.getClient().create(KennelReviewsAPI.class);
//        Call<KennelReviews> callYes = apiYes.fetchPositiveKennelReviews(data.getKennelID(), "Yes");
//        callYes.enqueue(new Callback<KennelReviews>() {
//            @Override
//            public void onResponse(Call<KennelReviews> call, Response<KennelReviews> response) {
//                Log.e("POSITIVE RAW", String.valueOf(response.raw()));
//                if (response.body() != null && response.body().getReviews() != null)    {
//                    ArrayList<KennelReview> arrPositive = response.body().getReviews();
//                    TOTAL_LIKES = arrPositive.size();
//                    TOTAL_VOTES = TOTAL_VOTES + arrPositive.size();
//
//                    /* GET THE TOTAL NUMBER OF NEGATIVE REVIEWS */
//                    KennelReviewsAPI apiNo = ZenApiClient.getClient().create(KennelReviewsAPI.class);
//                    Call<KennelReviews> callNo = apiNo.fetchNegativeKennelReviews(data.getKennelID(), "No");
//                    callNo.enqueue(new Callback<KennelReviews>() {
//                        @Override
//                        public void onResponse(Call<KennelReviews> call, Response<KennelReviews> response) {
//                            Log.e("NEGATIVE RAW", String.valueOf(response.raw()));
////                            if (response.body() != null && response.body().getReviews() != null)    {
//                                ArrayList<KennelReview> arrNegative = response.body().getReviews();
//                                TOTAL_VOTES = TOTAL_VOTES + arrNegative.size();
//
//                                /* CALCULATE THE PERCENTAGE OF LIKES */
//                                double percentLikes = ((double)TOTAL_LIKES / TOTAL_VOTES) * 100;
//                                int finalPercentLikes = (int)percentLikes;
//                                String strLikesPercentage = String.valueOf(finalPercentLikes) + "%";
//
//                                /* GET THE TOTAL NUMBER OF REVIEWS / VOTES */
//                                Resources resReviews = AppPrefs.context().getResources();
//                                String reviewQuantity = null;
//                                if (TOTAL_VOTES == 0)   {
//                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                                } else if (TOTAL_VOTES == 1)    {
//                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                                } else if (TOTAL_VOTES > 1) {
//                                    reviewQuantity = resReviews.getQuantityString(R.plurals.votes, TOTAL_VOTES, TOTAL_VOTES);
//                                }
//                                String strVotes = reviewQuantity;
//                                String open = activity.getString(R.string.doctor_list_votes_open);
//                                String close = activity.getString(R.string.doctor_list_votes_close);
//                                holder.txtKennelLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder, strLikesPercentage, open, strVotes, close));
////                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<KennelReviews> call, Throwable t) {
//                            Crashlytics.logException(t);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<KennelReviews> call, Throwable t) {
//                Crashlytics.logException(t);
//            }
//        });

//        /* SET THE KENNEL'S DISTANCE */
//        Double latitude = Double.valueOf(data.getKennelLatitude());
//        Double longitude = Double.valueOf(data.getKennelLongitude());
//        LatLng LATLNG_DESTINATION = new LatLng(latitude, longitude);
//        String strOrigin = LATLNG_ORIGIN.latitude + "," + LATLNG_ORIGIN.longitude;
//        String strDestination = LATLNG_DESTINATION.latitude + "," + LATLNG_DESTINATION.longitude;
//        String strSensor = "false";
//        String strKey = activity.getString(R.string.google_directions_api_key);
//        DistanceAPI api = ZenDistanceClient.getClient().create(DistanceAPI.class);
//        Call<String> call = api.json(strOrigin, strDestination, strSensor, strKey);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                try {
//                    String strDistance = response.body();
//                    JSONObject JORootDistance = new JSONObject(strDistance);
//                    JSONArray array = JORootDistance.getJSONArray("routes");
//                    JSONObject JORoutes = array.getJSONObject(0);
//                    JSONArray JOLegs= JORoutes.getJSONArray("legs");
//                    JSONObject JOSteps = JOLegs.getJSONObject(0);
//                    JSONObject JODistance = JOSteps.getJSONObject("distance");
//                    if (JODistance.has("text")) {
//                        String distance = JODistance.getString("text");
//                        String strTilde = activity.getString(R.string.generic_tilde);
//                        holder.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, distance));
//                    } else {
//                        String distance = "Unknown";
//                        String strInfinity = activity.getString(R.string.generic_infinity);
//                        holder.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Crashlytics.logException(t);
//                String distance = "Unknown";
//                String strInfinity = activity.getString(R.string.generic_infinity);
//                holder.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
//            }
//        });

        /* SHOW THE KENNEL DETAILS */
        holder.cardKennel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, KennelDetails.class);
                intent.putExtra("KENNEL_ID", data.getKennelID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public KennelsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kennels_item, parent, false);

        return new KennelsVH(itemView);
    }

    class KennelsVH extends RecyclerView.ViewHolder	{
        CardView cardKennel;
        SimpleDraweeView imgvwKennelCoverPhoto;
        TextView txtKennelName;
        TextView txtKennelAddress;
        TextView txtPetCapacity;
        TextView txtKennelLikes;
        TextView txtKennelDistance;

        KennelsVH(View v) {
            super(v);
            cardKennel = v.findViewById(R.id.cardKennel);
            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
            txtKennelName = v.findViewById(R.id.txtKennelName);
            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
            txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
            txtKennelDistance = v.findViewById(R.id.txtKennelDistance);
        }
    }
}