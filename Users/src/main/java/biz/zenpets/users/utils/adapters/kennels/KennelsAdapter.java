package biz.zenpets.users.utils.adapters.kennels;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.helpers.classes.ZenDistanceClient;
import biz.zenpets.users.utils.helpers.clinics.distance.DistanceAPI;
import biz.zenpets.users.utils.models.kennels.Kennel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Kennel> arrKennels;

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

        /* SET THE KENNEL'S DISTANCE */
        Double latitude = Double.valueOf(data.getKennelLatitude());
        Double longitude = Double.valueOf(data.getKennelLongitude());
        LatLng LATLNG_DESTINATION = new LatLng(latitude, longitude);
        String strOrigin = LATLNG_ORIGIN.latitude + "," + LATLNG_ORIGIN.longitude;
        String strDestination = LATLNG_DESTINATION.latitude + "," + LATLNG_DESTINATION.longitude;
        String strSensor = "false";
        String strKey = activity.getString(R.string.google_directions_api_key);
        DistanceAPI api = ZenDistanceClient.getClient().create(DistanceAPI.class);
        Call<String> call = api.json(strOrigin, strDestination, strSensor, strKey);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    String strDistance = response.body();
                    JSONObject JORootDistance = new JSONObject(strDistance);
                    JSONArray array = JORootDistance.getJSONArray("routes");
                    JSONObject JORoutes = array.getJSONObject(0);
                    JSONArray JOLegs= JORoutes.getJSONArray("legs");
                    JSONObject JOSteps = JOLegs.getJSONObject(0);
                    JSONObject JODistance = JOSteps.getJSONObject("distance");
                    if (JODistance.has("text")) {
                        String distance = JODistance.getString("text");
                        String strTilde = activity.getString(R.string.generic_tilde);
                        holder.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, distance));
                    } else {
                        String distance = "Unknown";
                        String strInfinity = activity.getString(R.string.generic_infinity);
                        holder.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Crashlytics.logException(t);
                String distance = "Unknown";
                String strInfinity = activity.getString(R.string.generic_infinity);
                holder.txtKennelDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
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

        SimpleDraweeView imgvwKennelCoverPhoto;
        TextView txtKennelName;
        TextView txtKennelAddress;
        TextView txtPetCapacity;
        TextView txtKennelLikes;
        TextView txtKennelDistance;

        KennelsVH(View v) {
            super(v);

            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
            txtKennelName = v.findViewById(R.id.txtKennelName);
            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
            txtKennelLikes = v.findViewById(R.id.txtKennelLikes);
            txtKennelDistance = v.findViewById(R.id.txtKennelDistance);
        }
    }
}