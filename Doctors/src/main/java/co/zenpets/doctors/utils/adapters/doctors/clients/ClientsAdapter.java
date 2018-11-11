package co.zenpets.doctors.utils.adapters.doctors.clients;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.details.client.ClientDetails;
import co.zenpets.doctors.utils.models.doctors.clients.Client;

public class ClientsAdapter 
        extends RecyclerView.Adapter<ClientsAdapter.ClientsVH>
        implements Filterable {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Client> arrClients;
    private ArrayList<Client> mFilteredList;

    public ClientsAdapter(Activity activity, ArrayList<Client> arrClients) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrClients = arrClients;
        this.mFilteredList = arrClients;
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ClientsVH holder, final int position) {
        final Client data = mFilteredList.get(position);

        /* CHECK IF A USER ID IS AVAILABLE */
        String userID = data.getUserID();
        if (userID == null) {
            /* SET THE CLIENTS'S NAME */
            if (data.getClientName() != null) {
                holder.txtUserName.setText(data.getClientName());
            }

            /* SET THE CLIENTS'S PHONE */
            if (data.getClientPhoneNumber() != null && !data.getClientPhoneNumber().equalsIgnoreCase(""))   {
                holder.txtUserPhone.setText(data.getClientPhoneNumber());
            }

        } else {
            /* SET THE USER'S DISPLAY PROFILE */
            if (data.getUserDisplayProfile() != null) {
                Uri uri = Uri.parse(data.getUserDisplayProfile());
                holder.imgvwUserProfile.setImageURI(uri);
            }

            /* SET THE USER'S NAME */
            if (data.getUserName() != null) {
                holder.txtUserName.setText(data.getUserName());
            }

            /* SET THE USER'S PHONE */
            if (data.getUserPhoneNumber() != null && !data.getUserPhoneNumber().equalsIgnoreCase(""))   {
                holder.txtUserPhone.setText(data.getUserPhoneNumber());
            }
        }

        /* SHOW THE CLIENT DETAILS */
        holder.cardClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ClientDetails.class);
                intent.putExtra("CLIENT_ID", data.getClientID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ClientsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.home_clients_frag_item, parent, false);

        return new ClientsVH(itemView);
    }

    class ClientsVH extends RecyclerView.ViewHolder	{
        final CardView cardClient;
        final SimpleDraweeView imgvwUserProfile;
        final AppCompatTextView txtUserName;
        final AppCompatTextView txtUserPhone;

        ClientsVH(View v) {
            super(v);
            cardClient = v.findViewById(R.id.cardClient);
            imgvwUserProfile = v.findViewById(R.id.imgvwUserProfile);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtUserPhone = v.findViewById(R.id.txtUserPhone);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                if (charString.isEmpty()) {
                    mFilteredList = arrClients;
                } else {
                    ArrayList<Client> filteredList = new ArrayList<>();
                    for (Client data : arrClients) {
                        if (data.getUserName().toLowerCase().contains(charString)) {
                            filteredList.add(data);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (ArrayList<Client>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}