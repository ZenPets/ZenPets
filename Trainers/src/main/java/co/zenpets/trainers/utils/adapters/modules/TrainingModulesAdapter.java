package co.zenpets.trainers.utils.adapters.modules;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import co.zenpets.trainers.R;
import co.zenpets.trainers.details.modules.TrainingImageManager;
import co.zenpets.trainers.utils.helpers.ZenApiClient;
import co.zenpets.trainers.utils.models.trainers.modules.Module;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleImage;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleImages;
import co.zenpets.trainers.utils.models.trainers.modules.ModuleImagesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingModulesAdapter extends RecyclerView.Adapter<TrainingModulesAdapter.AdoptionsVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<Module> arrModule;

    /** THE TRAINING MODULE IMAGES ADAPTER AND ARRAY LIST **/
    private ArrayList<ModuleImage> arrImages = new ArrayList<>();
    private TrainingModuleImagesAdapter adapter;

    public TrainingModulesAdapter(Activity activity, ArrayList<Module> arrModule) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrModule = arrModule;
    }

    @Override
    public int getItemCount() {
        return arrModule.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final AdoptionsVH holder, final int position) {
        final Module data = arrModule.get(position);

        /* SET THE TRAINING MODULE NAME */
        if (data.getTrainerModuleName() != null) {
            holder.txtModuleName.setText(data.getTrainerModuleName());
        }

        /* SET THE TRAINING DURATION AND SESSIONS */
        if (data.getTrainerModuleDuration() != null && data.getTrainerModuleSessions() != null) {
            String strNumber = data.getTrainerModuleDuration();
            String strUnits = data.getTrainerModuleDuration();
            String strFinalUnits = null;
            if (strUnits.equalsIgnoreCase("day")) {
                int totalDays = Integer.parseInt(strNumber);
                Resources resDays = activity.getResources();
                if (totalDays == 1)    {
                    strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                } else if (totalDays > 1) {
                    strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                }
            } else {
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
            holder.txtModuleFormat.setText("Pets are trained individually");
        } else {
            String strGroupSize = data.getTrainerModuleSize();
            holder.imgvwModuleFormat.setImageResource(R.drawable.ic_training_group_light);
            holder.txtModuleFormat.setText("Pets are trained in groups of " + strGroupSize);
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
            }
        });

        /* MANAGE THE TRAINING MODULE IMAGES */
        holder.txtManageImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TrainingImageManager.class);
                intent.putExtra("MODULE_ID", data.getTrainerModuleID());
                activity.startActivityForResult(intent, 103);
            }
        });

        /* SHOW THE DROPDOWN MENU */
        holder.imgvwModuleOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(activity, holder.imgvwModuleOptions);
                pm.getMenuInflater().inflate(R.menu.pm_training_module, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())   {
                            case R.id.menuEdit:
                                break;
                            case R.id.menuImages:
                                Intent intent = new Intent(activity, TrainingImageManager.class);
                                intent.putExtra("MODULE_ID", data.getTrainerModuleID());
                                activity.startActivityForResult(intent, 103);
                                break;
                            case R.id.menuDelete:
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                pm.show();
            }
        });
    }

    @NonNull
    @Override
    public AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.training_modules_item, parent, false);

        return new AdoptionsVH(itemView);
    }

    class AdoptionsVH extends RecyclerView.ViewHolder	{

        final AppCompatTextView txtModuleName;
        IconicsImageView imgvwModuleOptions;
        final AppCompatTextView txtModuleDuration;
        final AppCompatTextView txtModuleDetails;
        ImageView imgvwModuleFormat;
        TextView txtModuleFormat;
        AppCompatTextView txtModuleFees;
        LinearLayout linlaImagesContainer;
        RecyclerView listModuleImages;
        AppCompatTextView txtManageImages;

        AdoptionsVH(View v) {
            super(v);
            txtModuleName = v.findViewById(R.id.txtModuleName);
            imgvwModuleOptions = v.findViewById(R.id.imgvwModuleOptions);
            txtModuleDuration = v.findViewById(R.id.txtModuleDuration);
            txtModuleDetails = v.findViewById(R.id.txtModuleDetails);
            imgvwModuleFormat = v.findViewById(R.id.imgvwModuleFormat);
            txtModuleFormat = v.findViewById(R.id.txtModuleFormat);
            txtModuleFees = v.findViewById(R.id.txtModuleFees);
            linlaImagesContainer = v.findViewById(R.id.linlaImagesContainer);
            listModuleImages = v.findViewById(R.id.listModuleImages);
            txtManageImages = v.findViewById(R.id.txtManageImages);

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