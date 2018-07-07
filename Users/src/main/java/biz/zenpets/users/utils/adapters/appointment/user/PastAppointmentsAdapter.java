package biz.zenpets.users.utils.adapters.appointment.user;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.models.appointment.Appointment;

public class PastAppointmentsAdapter extends RecyclerView.Adapter<PastAppointmentsAdapter.AppointmentsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Appointment> arrAppointments;

    public PastAppointmentsAdapter(Activity activity, ArrayList<Appointment> arrAppointments) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrAppointments = arrAppointments;
    }

    @Override
    public int getItemCount() {
        return arrAppointments.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentsVH holder, final int position) {
        final Appointment data = arrAppointments.get(position);

        /* GET THE APPOINTMENT DATE AND TIME AND FORMAT IT */
        String appointmentDate = data.getAppointmentDate();
        String appointmentTime = data.getAppointmentTime();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(appointmentDate + " " + appointmentTime));
            SimpleDateFormat formatDate = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
            SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String strFormattedDate = formatDate.format(calendar.getTime());
            String strFormattedTime = formatTime.format(calendar.getTime());

            /* SET THE SELECTED DATE AND TIME */
            holder.txtAppointmentDateAndTime.setText("On \"" + strFormattedDate + "\" at \"" + strFormattedTime + "\"");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* SET THE DOCTOR'S NAME */
        if (data.getDoctorPrefix() != null && data.getDoctorName() != null) {
            String prefix = data.getDoctorPrefix();
            String name = data.getDoctorName();
            holder.txtDoctorName.setText(activity.getString(R.string.user_appointment_item_doc_name_placeholder, prefix, name));
        }

        /* SET THE CLINIC NAME */
        if (data.getClinicName() != null)   {
            holder.txtClinicName.setText(data.getClinicName());
        }

        /* SET THE VISIT REASON */
        if (data.getVisitReason() != null)  {
            String reason = data.getVisitReason();
            holder.txtVisitReason.setText(activity.getString(R.string.user_appointment_item_reason_placeholder, reason));
        }

        /* SET THE CLINIC CITY AND LOCALITY */
        if (data.getCityName() != null && data.getLocalityName() != null)   {
            String locality = data.getLocalityName();
            String city = data.getCityName();
            holder.txtAppointmentLocation.setText(activity.getString(R.string.user_appointment_item_location_placeholder, locality, city));
        }
        
        /* SET THE APPOINTMENT STATUS */
        if (data.getAppointmentStatus() != null)    {
            holder.txtAppointmentStatus.setText(data.getAppointmentStatus());
        }

        /* SHOW THE STATUS */
        holder.txtAppointmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                if (data.getAppointmentNote() != null)  {
                    message = data.getAppointmentNote();
                } else {
                    message = "No note has been published for this Appointment";
                }
                new MaterialDialog.Builder(activity)
                        .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_outline_black_24dp))
                        .title("Appointment Status")
                        .cancelable(true)
                        .content(message)
                        .positiveText("Okay")
                        .theme(Theme.LIGHT)
                        .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {dialog.dismiss();
                            }
                        }).show();
            }
        });

        /* SHOW THE APPOINTMENT DETAILS */
//        holder.linlaAppointment.setOnClickListener(new ConsultationView.OnClickListener() {
//            @Override
//            public void onClick(ConsultationView v) {
//                Intent intent = new Intent(activity, AppointmentDetails.class);
//                intent.putExtra("APPOINTMENT_ID", data.getAppointmentID());
//                activity.startActivity(intent);
//            }
//        });
    }

    @NonNull
    @Override
    public AppointmentsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.user_appointments_item, parent, false);

        return new AppointmentsVH(itemView);
    }

    class AppointmentsVH extends RecyclerView.ViewHolder	{
        final AppCompatTextView txtAppointmentDateAndTime;
        final AppCompatTextView txtDoctorName;
        final AppCompatTextView txtClinicName;
        final AppCompatTextView txtVisitReason;
        final AppCompatTextView txtAppointmentLocation;
        final AppCompatTextView txtAppointmentStatus;

        AppointmentsVH (View v) {
            super(v);
            txtAppointmentDateAndTime = v.findViewById(R.id.txtAppointmentDateAndTime);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtVisitReason = v.findViewById(R.id.txtVisitReason);
            txtAppointmentLocation = v.findViewById(R.id.txtAppointmentLocation);
            txtAppointmentStatus = v.findViewById(R.id.txtAppointmentStatus);
        }
    }
}