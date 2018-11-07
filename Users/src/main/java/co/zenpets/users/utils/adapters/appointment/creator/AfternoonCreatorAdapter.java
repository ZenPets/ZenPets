package co.zenpets.users.utils.adapters.appointment.creator;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.zenpets.users.R;
import co.zenpets.users.creator.appointment.AppointmentDetailsCreator;
import co.zenpets.users.utils.models.doctors.timings.TimeSlot;

public class AfternoonCreatorAdapter extends RecyclerView.Adapter<AfternoonCreatorAdapter.SlotsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<TimeSlot> arrSlots;

        public AfternoonCreatorAdapter(Activity activity, ArrayList<TimeSlot> arrSlots) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrSlots = arrSlots;
    }

    @Override
    public int getItemCount() {
        return arrSlots.size();
    }

    @Override
    public void onBindViewHolder(@NonNull SlotsVH holder, final int position) {
        final TimeSlot data = arrSlots.get(position);

//        Log.e("APPOINTMENT TIME", data.getAppointmentTime());
//        Log.e("APPOINTMENT DATE", data.getAppointmentDate());

        /* SET THE APPOINTMENT TIME */
        if (data.getAppointmentTime() != null)   {
            holder.txtTimeSlot.setText(data.getAppointmentTime());
        }

        /* SET THE APPOINTMENT STATUS */
        if (data.getAppointmentStatus() != null)    {
            if (data.getAppointmentStatus().equalsIgnoreCase("Available"))  {
                holder.txtTimeSlot.setTextColor(ContextCompat.getColor(activity, R.color.primary_text));
            } else if (data.getAppointmentStatus().equalsIgnoreCase("Unavailable"))  {
                holder.txtTimeSlot.setTextColor(ContextCompat.getColor(activity, R.color.tertiary_text));
            }
        } else {
            holder.txtTimeSlot.setTextColor(ContextCompat.getColor(activity, R.color.tertiary_text));
        }

        /* PASS THE DATA TO THE DETAILS ACTIVITY */
        holder.txtTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getAppointmentStatus() != null
                        && !data.getAppointmentStatus().equalsIgnoreCase("Unavailable"))    {
                    Intent intent = new Intent(activity, AppointmentDetailsCreator.class);
                    intent.putExtra("DOCTOR_ID", data.getDoctorID());
                    intent.putExtra("CLINIC_ID", data.getClinicID());
                    intent.putExtra("APPOINTMENT_TIME", data.getAppointmentTime());
                    intent.putExtra("APPOINTMENT_DATE", data.getAppointmentDate());
                    activity.startActivityForResult(intent, 101);
                }
            }
        });
    }

    @NonNull
    @Override
    public SlotsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.time_slot_item, parent, false);

        return new SlotsVH(itemView);
    }

    class SlotsVH extends RecyclerView.ViewHolder	{
        final AppCompatTextView txtTimeSlot;

        SlotsVH (View v) {
            super(v);
            txtTimeSlot = v.findViewById(R.id.txtTimeSlot);
        }

    }
}