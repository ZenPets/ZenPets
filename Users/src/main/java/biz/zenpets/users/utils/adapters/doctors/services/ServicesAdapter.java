package biz.zenpets.users.utils.adapters.doctors.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.doctors.modules.Service;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesVH> {

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Service> arrReviews;

    public ServicesAdapter(ArrayList<Service> arrReviews) {

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrReviews = arrReviews;
    }

    @Override
    public int getItemCount() {
        return arrReviews.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesVH holder, final int position) {
        Service data = arrReviews.get(position);

        /* SET THE SERVICE NAME */
        if (data.getDoctorServiceName() != null)
            holder.txtServiceName.setText(data.getDoctorServiceName());
    }

    @Override
    public ServicesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctors_details_services_item, parent, false);

        return new ServicesVH(itemView);
    }

    class ServicesVH extends RecyclerView.ViewHolder	{
        AppCompatTextView txtServiceName;

        ServicesVH(View v) {
            super(v);
            txtServiceName = v.findViewById(R.id.txtServiceName);
        }
    }
}