package co.zenpets.users.utils.adapters.doctors.services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.utils.models.doctors.modules.Service;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesVH> {

    /** AN ACTIVITY INSTANCE **/
    Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Service> arrServices;

    public ServicesAdapter(Activity activity, ArrayList<Service> arrServices) {

        /* CAST THE ACTIVITY IN THE GLOBAL INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrServices = arrServices;
    }

    @Override
    public int getItemCount() {
        return arrServices.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesVH holder, final int position) {
        Service data = arrServices.get(position);

        /* SET THE SERVICE NAME */
        if (data.getDoctorServiceName() != null)
            holder.txtServiceName.setText(data.getDoctorServiceName());
    }

    @NonNull
    @Override
    public ServicesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctors_details_services_item, parent, false);

        return new ServicesVH(itemView);
    }

    class ServicesVH extends RecyclerView.ViewHolder	{
        final AppCompatTextView txtServiceName;

        ServicesVH(View v) {
            super(v);
            txtServiceName = v.findViewById(R.id.txtServiceName);
        }
    }
}