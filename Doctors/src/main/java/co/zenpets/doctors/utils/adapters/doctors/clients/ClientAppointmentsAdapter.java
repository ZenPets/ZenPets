package co.zenpets.doctors.utils.adapters.doctors.clients;

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
import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.helpers.VectorDrawableUtils;
import co.zenpets.doctors.utils.models.doctors.clients.ClientAppointment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ClientAppointmentsAdapter extends RecyclerView.Adapter<ClientAppointmentsAdapter.PatientsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<ClientAppointment> arrClientAppointments;

    public ClientAppointmentsAdapter(Activity activity, ArrayList<ClientAppointment> arrClientAppointments) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrClientAppointments = arrClientAppointments;
    }

    @Override
    public int getItemCount() {
        return arrClientAppointments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsVH holder, final int position) {
        final ClientAppointment data = arrClientAppointments.get(position);

        /* SET THE TIME LINE VIEW GRAPHIC */
        holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(activity, R.drawable.ic_marker_active, R.color.primary));

        /* GET THE VISIT REASON */
        if (data.getVisitReason() != null)  {
            holder.txtVisitReason.setText(data.getVisitReason());
        } else {
            data.setVisitReason(null);
        }

        /* SET THE PET'S DETAILS (BREED NAME, PET TYPE NAME AND PET AGE) */
        if (data.getBreedName() != null  && data.getPetTypeName() != null)   {
            String breed = data.getBreedName();
            String petType = data.getPetTypeName();
            String combinedDetails = "The Pet is a \"" + petType + "\" of the Breed \"" + breed + "\"";
            holder.txtPetDetails.setText(combinedDetails);
        }

        /* SET THE APPOINTMENT DATE AND TIME */
        if (data.getAppointmentDate() != null && data.getAppointmentTime() != null)  {
            String date = data.getAppointmentDate();
            String time = data.getAppointmentTime();
            holder.txtAppointmentDateAndTime.setText("On " + date + " at " + time);
        }

        /* SET THE APPOINTMENT STATUS */
        if (data.getAppointmentStatus() != null)    {
            holder.txtAppointmentStatus.setText(data.getAppointmentStatus());
        } else {
            holder.txtAppointmentStatus.setText("Pending");
        }

        /* SHOW THE STATUS */
        holder.txtAppointmentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message;
                if (data.getAppointmentNote() != null
                        && !data.getAppointmentNote().equalsIgnoreCase("null")
                        && !data.getAppointmentNote().equalsIgnoreCase(""))  {
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
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @NonNull
    @Override
    public PatientsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.client_detail_appointment_items, parent, false);
        return new PatientsVH(itemView, viewType);
    }

    class PatientsVH extends RecyclerView.ViewHolder	{

        @BindView(R.id.timelineMarker) TimelineView timelineMarker;
        @BindView(R.id.txtVisitReason) AppCompatTextView txtVisitReason;
        @BindView(R.id.txtPetDetails) AppCompatTextView txtPetDetails;
        @BindView(R.id.txtAppointmentDateAndTime) AppCompatTextView txtAppointmentDateAndTime;
        @BindView(R.id.txtAppointmentStatus) AppCompatTextView txtAppointmentStatus;

        PatientsVH(View v, int viewType) {
            super(v);

            ButterKnife.bind(this, itemView);
            timelineMarker.initLine(viewType);
        }
    }
}