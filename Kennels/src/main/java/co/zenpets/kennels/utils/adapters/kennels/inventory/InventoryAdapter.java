package co.zenpets.kennels.utils.adapters.kennels.inventory;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.zenpets.kennels.R;
import co.zenpets.kennels.details.bookings.BookingHistory;
import co.zenpets.kennels.utils.models.bookings.Booking;
import co.zenpets.kennels.utils.models.bookings.BookingsAPI;
import co.zenpets.kennels.utils.models.helpers.ZenApiClient;
import co.zenpets.kennels.utils.models.inventory.Inventory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Inventory> arrInventory;

    public InventoryAdapter(Activity activity, ArrayList<Inventory> arrInventory) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrInventory = arrInventory;
    }

    @Override
    public int getItemCount() {
        return arrInventory.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryVH holder, final int position) {
        final Inventory data = arrInventory.get(position);

        /* SET THE INVENTORY ITEM NAME */
        if (data.getInventoryTypeName() != null)    {
            holder.txtInventoryName.setText(data.getKennelInventoryName());
        }

        /* SET THE INVENTORY STATUS */
        String kennelInventoryStatus = data.getKennelInventoryStatus();
        if (kennelInventoryStatus != null)    {
            if (kennelInventoryStatus.equalsIgnoreCase("Available"))    {
                holder.txtInventoryStatus.setText(activity.getString(R.string.inv_item_available));
                holder.txtInventoryStatus.setTextColor(activity.getResources().getColor(android.R.color.holo_green_dark));
                
                holder.switchAvailable.setText(activity.getString(R.string.inv_item_available_label));
                holder.switchAvailable.setChecked(true);
            } else if (kennelInventoryStatus.equalsIgnoreCase("Unavailable"))   {
                holder.txtInventoryStatus.setText(data.getKennelInventoryStatus());
                holder.txtInventoryStatus.setTextColor(activity.getResources().getColor(android.R.color.holo_orange_dark));

                holder.switchAvailable.setText(activity.getString(R.string.inv_item_unavailable_label));
                holder.switchAvailable.setChecked(false);
            } else if (kennelInventoryStatus.equalsIgnoreCase("Booked"))    {
                holder.txtInventoryStatus.setText(data.getKennelInventoryStatus());
                holder.txtInventoryStatus.setTextColor(activity.getResources().getColor(android.R.color.holo_red_dark));
            }
        }
        
        /* TOGGLE THE INVENTORY ITEM AVAILABILITY */
        holder.switchAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)  {
                    holder.switchAvailable.setText(activity.getString(R.string.inv_item_available_label));
                } else {
                    holder.switchAvailable.setText(activity.getString(R.string.inv_item_unavailable_label));
                }
            }
        });

        /* SHOW THE BOOKING HISTORY FOR THIS */
        holder.cardInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, BookingHistory.class);
                intent.putExtra("INVENTORY_ID", data.getKennelInventoryID());
                activity.startActivity(intent);
            }
        });

        /* GET THE CURRENTLY / PREVIOUSLY BOOKED PET'S DETAILS */
        BookingsAPI api = ZenApiClient.getClient().create(BookingsAPI.class);
        Call<Booking> call = api.fetchLastInventoryBooking(data.getKennelInventoryID());
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                Log.e("BOOKING RESPONSE", String.valueOf(response.raw()));
                Booking booking = response.body();
                if (!booking.getError())    {
                    /* GET THE USER DETAILS */
                    String userID = booking.getUserID();
                    String userName = booking.getUserName();
                    String userDisplayProfile = booking.getUserDisplayProfile();

                    /* GET THE PET DETAILS */
                    String petID = booking.getPetID();
                    String petName = booking.getPetName();
                    final String petDisplayProfile = booking.getPetDisplayProfile();

                    if (petID != null && petName != null && petDisplayProfile != null)  {
                        if (petDisplayProfile != null) {
                            if (petDisplayProfile != null) {
                                Picasso.with(activity)
                                        .load(petDisplayProfile)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .fit()
                                        .into(holder.imgvwPetDisplayProfile, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                Picasso.with(activity)
                                                        .load(petDisplayProfile)
                                                        .into(holder.imgvwPetDisplayProfile);
                                            }
                                        });
                            }
                        }
                    }

                    /* SHOW THE PET DETAILS AND HIDE THE NO BOOKING HISTORY TEXT VIEW */
                    holder.imgvwPetDisplayProfile.setVisibility(View.VISIBLE);
                    holder.txtPetName.setVisibility(View.VISIBLE);
                    holder.txtOccupationStatus.setVisibility(View.VISIBLE);
                    holder.txtNoBookingHistory.setVisibility(View.GONE);
                } else {
                    /* SHOW THE NO HISTORY TEXT VIEW AND HIDE THE PET DETAILS */
                    holder.txtNoBookingHistory.setVisibility(View.VISIBLE);
                    holder.imgvwPetDisplayProfile.setVisibility(View.GONE);
                    holder.txtPetName.setVisibility(View.GONE);
                    holder.txtOccupationStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Log.e("LAST BOOKING FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @NonNull
    @Override
    public InventoryVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.dash_inventory_item, parent, false);

        return new InventoryVH(itemView);
    }

    class InventoryVH extends RecyclerView.ViewHolder	{

        CardView cardInventory;
        TextView txtInventoryName;
        TextView txtInventoryStatus;
        ConstraintLayout layoutPetDetails;
        ImageView imgvwPetDisplayProfile;
        TextView txtPetName;
        TextView txtOccupationStatus;
        TextView txtNoBookingHistory;
        Switch switchAvailable;

        InventoryVH(View v) {
            super(v);

            cardInventory = v.findViewById(R.id.cardInventory);
            txtInventoryName = v.findViewById(R.id.txtInventoryName);
            txtInventoryStatus = v.findViewById(R.id.txtInventoryStatus);
            layoutPetDetails = v.findViewById(R.id.layoutPetDetails);
            imgvwPetDisplayProfile = v.findViewById(R.id.imgvwPetDisplayProfile);
            txtPetName = v.findViewById(R.id.txtPetName);
            txtOccupationStatus = v.findViewById(R.id.txtOccupationStatus);
            txtNoBookingHistory = v.findViewById(R.id.txtNoBookingHistory);
            switchAvailable = v.findViewById(R.id.switchAvailable);
        }
    }
}