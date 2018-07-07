package biz.zenpets.users.utils.adapters.trainers;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.details.trainers.enquiry.TrainerEnquiryActivity;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.trainers.modules.Module;
import biz.zenpets.users.utils.models.trainers.modules.ModuleImage;
import biz.zenpets.users.utils.models.trainers.modules.ModuleImages;
import biz.zenpets.users.utils.models.trainers.modules.ModuleImagesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingModulesAdapter extends RecyclerView.Adapter<TrainingModulesAdapter.ModulesVH> {
    
    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Module> arrAdapterModules;

    /** THE TRAINING MODULE IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<ModuleImage> arrImages = new ArrayList<>();
    private TrainingModuleImagesAdapter adapter;

    public TrainingModulesAdapter(Activity activity, ArrayList<Module> arrAdapterModules) {
        /* CAST THE ACTIVITY TO THE GLOBAL INSTANCE */
        this.activity = activity;
        
        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrAdapterModules = arrAdapterModules;
    }

    @Override
    public int getItemCount() {
        return arrAdapterModules.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ModulesVH holder, final int position) {
        final Module data = arrAdapterModules.get(position);

        /* SET THE TRAINING MODULE NAME */
        if (data.getTrainerModuleName() != null) {
            holder.txtModuleName.setText(data.getTrainerModuleName());
        }

        /* SET THE TRAINING DURATION AND SESSIONS */
        if (data.getTrainerModuleDuration() != null && data.getTrainerModuleSessions() != null) {
            String strNumber = data.getTrainerModuleDuration();
            String strUnits = data.getTrainerModuleDurationUnit();
            String strFinalUnits = null;
            if (strUnits.equalsIgnoreCase("day")) {
                int totalDays = Integer.parseInt(strNumber);
                Resources resDays = activity.getResources();
                if (totalDays == 1)    {
                    strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                } else if (totalDays > 1) {
                    strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                }
            } else if (strUnits.equalsIgnoreCase("Month")){
                int totalDays = Integer.parseInt(strNumber);
                Resources resDays = activity.getResources();
                if (totalDays == 1)    {
                    strFinalUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
                } else if (totalDays > 1) {
                    strFinalUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
                }
            }

            String strSessions = data.getTrainerModuleSessions();
            String strFinalSessions = null;
            int totalSessions = Integer.parseInt(strSessions);
            Resources resSessions = activity.getResources();
            if (totalSessions == 1)    {
                strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
            } else if (totalSessions > 1) {
                strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
            }
            holder.txtModuleDuration.setText(activity.getString(R.string.module_duration_sessions_placeholder, strFinalUnits, strFinalSessions));
        }

        /* SET THE TRAINING DETAILS */
        if (data.getTrainerModuleDetails() != null) {
            holder.txtModuleDetails.setText(data.getTrainerModuleDetails());
        }

        /* SET THE MODULE FORMAT */
        if (data.getTrainerModuleFormat().equalsIgnoreCase("Individual"))   {
            holder.imgvwModuleFormat.setImageResource(R.drawable.ic_training_individual_light);
            holder.txtModuleFormat.setText(activity.getString(R.string.module_format_individual_placeholder));
        } else {
            String strGroupSize = data.getTrainerModuleSize();
            holder.imgvwModuleFormat.setImageResource(R.drawable.ic_training_group_light);
            holder.txtModuleFormat.setText(activity.getString(R.string.module_format_group_placeholder, strGroupSize));
        }

        /* SET THE TRAINING FEES */
        if (data.getTrainerModuleFees() != null)    {
            holder.txtModuleFees.setText(activity.getString(R.string.module_fees_placeholder, data.getTrainerModuleFees()));
        }

        /* SET THE TRAINING MODULE IMAGES */
        ModuleImagesAPI apiImages = ZenApiClient.getClient().create(ModuleImagesAPI.class);
        Call<ModuleImages> callImages = apiImages.fetchTrainingModuleImages(data.getTrainerModuleID());
        callImages.enqueue(new Callback<ModuleImages>() {
            @Override
            public void onResponse(Call<ModuleImages> call, Response<ModuleImages> response) {
                if (response.body() != null && response.body().getImages() != null) {
                    arrImages = response.body().getImages();
                    if (arrImages.size() > 0)   {
                        /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                        adapter = new TrainingModuleImagesAdapter(activity, arrImages);
                        holder.listModuleImages.setAdapter(adapter);

                        /* SHOW THE IMAGES CONTAINER */
                        holder.linlaImagesContainer.setVisibility(View.VISIBLE);
                    } else {
                        /* HIDE THE IMAGES CONTAINER */
                        holder.linlaImagesContainer.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE IMAGES CONTAINER */
                    holder.linlaImagesContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ModuleImages> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });

        /** SHOW THE ENQUIRY ACTIVITY TO ALLOW THE PET PARENT TO CHAT WITH THE TRAINER **/
        holder.txtEnquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TrainerEnquiryActivity.class);
                intent.putExtra("TRAINER_ID", data.getTrainerID());
                intent.putExtra("MODULE_ID", data.getTrainerModuleID());
                activity.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ModulesVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.trainer_details_modules_item, parent, false);

        return new TrainingModulesAdapter.ModulesVH(itemView);
    }

    class ModulesVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaModuleContainer;
        final AppCompatTextView txtModuleName;
        final AppCompatTextView txtModuleDuration;
        final AppCompatTextView txtModuleDetails;
        final ImageView imgvwModuleFormat;
        final TextView txtModuleFormat;
        final AppCompatTextView txtModuleFees;
        final LinearLayout linlaImagesContainer;
        final RecyclerView listModuleImages;
        final AppCompatTextView txtEnquire;

        ModulesVH(View v) {
            super(v);
            linlaModuleContainer = v.findViewById(R.id.linlaModuleContainer);
            txtModuleName = v.findViewById(R.id.txtModuleName);
            txtModuleDuration = v.findViewById(R.id.txtModuleDuration);
            txtModuleDetails = v.findViewById(R.id.txtModuleDetails);
            imgvwModuleFormat = v.findViewById(R.id.imgvwModuleFormat);
            txtModuleFormat = v.findViewById(R.id.txtModuleFormat);
            txtModuleFees = v.findViewById(R.id.txtModuleFees);
            linlaImagesContainer = v.findViewById(R.id.linlaImagesContainer);
            listModuleImages = v.findViewById(R.id.listModuleImages);
            txtEnquire = v.findViewById(R.id.txtEnquire);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
            llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
            llmAppointments.setAutoMeasureEnabled(true);
            listModuleImages.setLayoutManager(llmAppointments);
            listModuleImages.setHasFixedSize(true);
            listModuleImages.setNestedScrollingEnabled(false);

            /* CONFIGURE THE ADAPTER */
            adapter = new TrainingModuleImagesAdapter(activity, arrImages);
            listModuleImages.setAdapter(adapter);
        }
    }
}