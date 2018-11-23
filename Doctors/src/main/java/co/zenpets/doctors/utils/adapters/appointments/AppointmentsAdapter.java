package co.zenpets.doctors.utils.adapters.appointments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.appointments.AppointmentData;
import co.zenpets.doctors.utils.models.appointments.AppointmentsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<AppointmentData> arrAppointments;

    /* BOOLEAN INSTANCE TO CHECK UPDATE STATUS */
    private boolean blnSuccess = false;

    public AppointmentsAdapter(Activity activity, ArrayList<AppointmentData> arrAppointments) {

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
        final AppointmentData data = arrAppointments.get(position);

        /* SET THE APPOINTMENT TIME */
        if (data.getAppointmentTime() != null)   {
            holder.txtTime.setText(data.getAppointmentTime());
        }

        /* SET THE USER NAME */
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }

        /* SET THE VISIT REASON */
        if (data.getVisitReason() != null)  {
            holder.txtVisitReason.setText(data.getVisitReason());
        }

        /* SET THE PET'S DETAILS (BREED NAME, PET TYPE NAME AND PET AGE) */
        if (data.getBreedName() != null  && data.getPetTypeName() != null)   {
            String breed = data.getBreedName();
            String petType = data.getPetTypeName();
            String combinedDetails = "The Pet is a \"" + petType + "\" of the Breed \"" + breed + "\"";
            holder.txtPetDetails.setText(combinedDetails);
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

        /* SHOW THE LIST OF APPOINTMENT OPTIONS  */
        holder.imgvwAppointmentOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(activity, v);
                pm.getMenuInflater().inflate(R.menu.popup_appointments, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())   {
                            case R.id.menuConfirm:
                                /* CONFIRM THE APPOINTMENT */
                                confirmAppointment(data.getAppointmentID());
                                break;
                            case R.id.menuComplete:
                                break;
                            case R.id.menuNoShow:
                                break;
                            case R.id.menuCancel:
                                /* CANCEL THE APPOINTMENT */
                                cancelAppointment(data.getAppointmentID());
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                pm.show();
            }
        });
    }

    @NonNull
    @Override
    public AppointmentsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.appointment_summary_item, parent, false);

        return new AppointmentsVH(itemView);
    }

    class AppointmentsVH extends RecyclerView.ViewHolder	{
        final AppCompatTextView txtTime;
        final AppCompatTextView txtUserName;
        final AppCompatTextView txtVisitReason;
        final AppCompatTextView txtPetDetails;
        final AppCompatTextView txtAppointmentStatus;
        final IconicsImageView imgvwAppointmentOptions;

        AppointmentsVH (View v) {
            super(v);
            txtTime = v.findViewById(R.id.txtTime);
            txtUserName = v.findViewById(R.id.txtUserName);
            txtVisitReason = v.findViewById(R.id.txtVisitReason);
            txtPetDetails = v.findViewById(R.id.txtPetDetails);
            txtAppointmentStatus = v.findViewById(R.id.txtAppointmentStatus);
            imgvwAppointmentOptions = v.findViewById(R.id.imgvwAppointmentOptions);
        }
    }

    /***** CANCEL THE APPOINTMENT *****/
    private void cancelAppointment(final String appointmentID) {
        new MaterialDialog.Builder(activity)
                .title("Add a note")
                .content("Add a note to inform the User why this Appointment has been canceled. Write as much information as required.")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(20, 400)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .positiveText("Post Note")
                .negativeText("Cancel")
                .input("Add a note stating the reason for cancelling the appointment....", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
                        Call<AppointmentData> call = api.cancelAppointment(
                                appointmentID,
                                input.toString(),
                                "Cancelled");
                        call.enqueue(new Callback<AppointmentData>() {
                            @Override
                            public void onResponse(@NonNull Call<AppointmentData> call, @NonNull Response<AppointmentData> response) {
                                if (response.isSuccessful())    {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "Appointment has been cancelled", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "There was an error cancelling the appointment", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppointmentData> call, @NonNull Throwable t) {
//                                Log.e("FAILURE", t.getMessage());
                                Crashlytics.logException(t);
                            }
                        });
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /***** CONFIRM THE APPOINTMENT *****/
    private void confirmAppointment(String appointmentID) {
        /* INSTANTIATE THE PROGRESS DIALOG INSTANCE */
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Updating appointment...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AppointmentsAPI api = ZenApiClient.getClient().create(AppointmentsAPI.class);
        Call<AppointmentData> call = api.updateAppointmentStatus(
                appointmentID,
                "Confirmed");
        call.enqueue(new Callback<AppointmentData>() {
            @Override
            public void onResponse(@NonNull Call<AppointmentData> call, @NonNull Response<AppointmentData> response) {
                if (response.isSuccessful())    {
                    Toast.makeText(activity, "The Appointment was confirmed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "There was a problem confirming the Appointment...", Toast.LENGTH_SHORT).show();
                }

                /* DISMISS THE DIALOG */
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AppointmentData> call, @NonNull Throwable t) {
//                Log.e("CONFIRM FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }
}