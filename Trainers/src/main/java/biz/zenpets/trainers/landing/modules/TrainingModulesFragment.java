package biz.zenpets.trainers.landing.modules;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mikepenz.iconics.view.IconicsImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import biz.zenpets.trainers.R;
import biz.zenpets.trainers.creators.modules.TrainingModuleCreator;
import biz.zenpets.trainers.details.modules.TrainingImageManager;
import biz.zenpets.trainers.modifiers.modules.TrainingModuleEditor;
import biz.zenpets.trainers.utils.AppPrefs;
import biz.zenpets.trainers.utils.adapters.modules.TrainingModuleImagesAdapter;
import biz.zenpets.trainers.utils.helpers.ZenApiClient;
import biz.zenpets.trainers.utils.models.trainers.modules.Module;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImage;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImagesAPI;
import biz.zenpets.trainers.utils.models.trainers.modules.ModuleImages;
import biz.zenpets.trainers.utils.models.trainers.modules.Modules;
import biz.zenpets.trainers.utils.models.trainers.modules.ModulesAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingModulesFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN TRAINER'S ID **/
    String TRAINER_ID = null;

    /** THE TRAINING MODULES ARRAY LIST **/
    ArrayList<Module> arrModules = new ArrayList<>();

    /** A PROGRESS DIALOG INSTANCE **/
    private ProgressDialog progressDialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listTrainingModules) RecyclerView listTrainingModules;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    /** ADD A NEW TRAINING MODULE (EMPTY TEXT) **/
    @OnClick(R.id.txtEmpty) void emptyNewModule()  {
        Intent intent = new Intent(getActivity(), TrainingModuleCreator.class);
        startActivityForResult(intent, 101);
    }

    /** ADD A NEW TRAINING MODULE (FAB) **/
    @OnClick(R.id.fabNewModule) void fabNewModule() {
        Intent intent = new Intent(getActivity(), TrainingModuleCreator.class);
        startActivityForResult(intent, 101);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.training_modules_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* GET THE LOGGED IN TRAINER'S ID */
        TRAINER_ID = getApp().getTrainerID();
//        Log.e("TRAINER ID", TRAINER_ID);

        /* FETCH THE LIST OF TRAINING MODULES */
        if (TRAINER_ID != null) {
            /* SHOW THE PROGRESS AND FETCH THE LIST OF TRAINING MODULES */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchTrainingModules();
        }
    }

    /***** FETCH THE LIST OF TRAINING MODULES *****/
    private void fetchTrainingModules() {
        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
        Call<Modules> call = api.fetchTrainerModules(TRAINER_ID);
        call.enqueue(new Callback<Modules>() {
            @Override
            public void onResponse(Call<Modules> call, Response<Modules> response) {
                String strResult = new Gson().toJson(response.body());
                try {
                    JSONObject JORoot = new JSONObject(strResult);
                    if (JORoot.has("error") && JORoot.getString("error").equalsIgnoreCase("false")) {
                        JSONArray JAModules = JORoot.getJSONArray("modules");
                        Module data;
                        for (int i = 0; i < JAModules.length(); i++) {
                            JSONObject JOModules = JAModules.getJSONObject(i);
                            data = new Module();

                            /* GET THE MODULE ID */
                            if (JOModules.has("trainerModuleID"))  {
                                data.setTrainerModuleID(JOModules.getString("trainerModuleID"));
                            } else {
                                data.setTrainerModuleID(null);
                            }

                            /* GET THE TRAINER ID */
                            if (JOModules.has("trainerID"))  {
                                data.setTrainerID(JOModules.getString("trainerID"));
                            } else {
                                data.setTrainerID(null);
                            }

                            /* GET THE MODULE NAME */
                            if (JOModules.has("trainerModuleName")) {
                                data.setTrainerModuleName(JOModules.getString("trainerModuleName"));
                            } else {
                                data.setTrainerModuleName(null);
                            }

                            /* GET THE MODULE DURATION */
                            if (JOModules.has("trainerModuleDuration")) {
                                data.setTrainerModuleDuration(JOModules.getString("trainerModuleDuration"));
                            } else {
                                data.setTrainerModuleDuration(null);
                            }

                            /* GET THE MODULE DURATION UNIT */
                            if (JOModules.has("trainerModuleDurationUnit")) {
                                data.setTrainerModuleDurationUnit(JOModules.getString("trainerModuleDurationUnit"));
                            } else {
                                data.setTrainerModuleDurationUnit(null);
                            }

                            /* GET THE MODULE SESSIONS */
                            if (JOModules.has("trainerModuleSessions")) {
                                data.setTrainerModuleSessions(JOModules.getString("trainerModuleSessions"));
                            } else {
                                data.setTrainerModuleSessions(null);
                            }

                            /* GET THE MODULE DETAILS */
                            if (JOModules.has("trainerModuleDetails"))  {
                                data.setTrainerModuleDetails(JOModules.getString("trainerModuleDetails"));
                            } else {
                                data.setTrainerModuleDetails(null);
                            }

                            /* GET THE MODULE FORMAT */
                            if (JOModules.has("trainerModuleFormat"))   {
                                data.setTrainerModuleFormat(JOModules.getString("trainerModuleFormat"));
                            } else {
                                data.setTrainerModuleFormat(null);
                            }

                            /* GET THE MODULE GROUP SIZE */
                            if (JOModules.has("trainerModuleSize")) {
                                data.setTrainerModuleSize(JOModules.getString("trainerModuleSize"));
                            } else {
                                data.setTrainerModuleSize(null);
                            }

                            /* GET THE MODULE FEES */
                            if (JOModules.has("trainerModuleFees")) {
                                data.setTrainerModuleFees(JOModules.getString("trainerModuleFees"));
                            } else {
                                data.setTrainerModuleFees(null);
                            }

                            /* ADD THE COLLECTED DATA TO THE ARRAY LIST */
                            arrModules.add(data);
                        }

                        /* SET THE ADAPTER TO THE RECYCLER VIEW */
                        listTrainingModules.setAdapter(new TrainingModulesAdapter(arrModules));

                        /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                        listTrainingModules.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);
                    } else {
                        /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                        linlaEmpty.setVisibility(View.VISIBLE);
                        listTrainingModules.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /* HIDE THE PROGRESS */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Modules> call, Throwable t) {
                Log.e("MODULES FAILURE", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)   {
            if (requestCode == 101 || requestCode == 102)   {
                /* CLEAR THE ARRAY */
                arrModules.clear();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF TRAINING MODULES */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchTrainingModules();
            }
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listTrainingModules.setLayoutManager(manager);
        listTrainingModules.setHasFixedSize(true);

        /* SET THE ADAPTER TO THE RECYCLER VIEW */
        listTrainingModules.setAdapter(new TrainingModulesAdapter(arrModules));
    }
    
    /***** THE TRAINING MODULE ADAPTER *****/
    private class TrainingModulesAdapter extends RecyclerView.Adapter<TrainingModulesAdapter.AdoptionsVH> {

        /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
        private final ArrayList<Module> arrAdapterModules;

        /** THE TRAINING MODULE IMAGES ADAPTER AND ARRAY LIST **/
        private ArrayList<ModuleImage> arrImages = new ArrayList<>();
        private TrainingModuleImagesAdapter adapter;

        TrainingModulesAdapter(ArrayList<Module> arrAdapterModules) {
            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrAdapterModules = arrAdapterModules;
        }

        @Override
        public int getItemCount() {
            return arrAdapterModules.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final TrainingModulesAdapter.AdoptionsVH holder, final int position) {
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
                    Resources resDays = getActivity().getResources();
                    if (totalDays == 1)    {
                        strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                    } else if (totalDays > 1) {
                        strFinalUnits = resDays.getQuantityString(R.plurals.days, totalDays, totalDays);
                    }
                } else if (strUnits.equalsIgnoreCase("Month")){
                    int totalDays = Integer.parseInt(strNumber);
                    Resources resDays = getActivity().getResources();
                    if (totalDays == 1)    {
                        strFinalUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
                    } else if (totalDays > 1) {
                        strFinalUnits = resDays.getQuantityString(R.plurals.months, totalDays, totalDays);
                    }
                }

                String strSessions = data.getTrainerModuleSessions();
                String strFinalSessions = null;
                int totalSessions = Integer.parseInt(strSessions);
                Resources resSessions = getActivity().getResources();
                if (totalSessions == 1)    {
                    strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
                } else if (totalSessions > 1) {
                    strFinalSessions = resSessions.getQuantityString(R.plurals.sessions, totalSessions, totalSessions);
                }
                holder.txtModuleDuration.setText(getActivity().getString(R.string.module_duration_sessions_placeholder, strFinalUnits, strFinalSessions));
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
                holder.txtModuleFees.setText(getActivity().getString(R.string.module_fees_placeholder, data.getTrainerModuleFees()));
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
                            adapter = new TrainingModuleImagesAdapter(getActivity(), arrImages);
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

            /* MANAGE THE TRAINING MODULE IMAGES */
            holder.txtManageImages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TrainingImageManager.class);
                    intent.putExtra("MODULE_ID", data.getTrainerModuleID());
                    getActivity().startActivityForResult(intent, 103);
                }
            });

            /* SHOW THE DROPDOWN MENU */
            holder.imgvwModuleOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu pm = new PopupMenu(getActivity(), holder.imgvwModuleOptions);
                    pm.getMenuInflater().inflate(R.menu.pm_training_module, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuEdit:
                                    Intent intentEdit = new Intent(getActivity(), TrainingModuleEditor.class);
                                    intentEdit.putExtra("MODULE_ID", data.getTrainerModuleID());
                                    getActivity().startActivityForResult(intentEdit, 102);
                                    break;
                                case R.id.menuImages:
                                    Intent intent = new Intent(getActivity(), TrainingImageManager.class);
                                    intent.putExtra("MODULE_ID", data.getTrainerModuleID());
                                    getActivity().startActivityForResult(intent, 103);
                                    break;
                                case R.id.menuDelete:
                                    /* SHOW THE DELETE DIALOG */
                                    showDeleteDialog(data.getTrainerModuleID());
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

        /***** SHOW THE DELETE DIALOG *****/
        private void showDeleteDialog(final String moduleID) {
            new MaterialDialog.Builder(getActivity())
                    .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                    .title("Delete Image?")
                    .cancelable(false)
                    .content("Are you sure you want to delete this Training Module? Clicking \"Delete\" will delete this Training Module permanently and will not be shown on your Profile Page to the Pet Parents. Click \"Cancel\" if you do not wish to delete this. You could always update any Training details by selecting the \"Edit\" option from the dropdown button.")
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            /* SHOW THE PROGRESS DIALOG */
                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Deleting the training module. This may take a bit depending on the number of images added...");
                            progressDialog.setIndeterminate(false);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            ModuleImagesAPI apiImages = ZenApiClient.getClient().create(ModuleImagesAPI.class);
                            Call<ModuleImages> callImages = apiImages.fetchTrainingModuleImages(moduleID);
                            callImages.enqueue(new Callback<ModuleImages>() {
                                @Override
                                public void onResponse(Call<ModuleImages> call, Response<ModuleImages> response) {
                                    if (response.body() != null && response.body().getImages() != null) {
                                        final ArrayList<ModuleImage> images = response.body().getImages();
                                        if (images.size() > 0) {
                                            /* LOOP THROUGH THE IMAGES IF AVAILABLE, DELETE EACH IMAGE FROM THE FIREBASE AND THEN THE PRIMARY DATABASE */
                                            for (int i = 0; i < images.size(); i++) {
                                                final String imageID = images.get(i).getTrainerModuleImageID();
                                                String imageURL = images.get(i).getTrainerModuleImageURL();
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        ModuleImagesAPI api = ZenApiClient.getClient().create(ModuleImagesAPI.class);
                                                        Call<ModuleImage> call = api.deleteTrainingModuleImage(imageID);
                                                        call.enqueue(new Callback<ModuleImage>() {
                                                            @Override
                                                            public void onResponse(Call<ModuleImage> call, Response<ModuleImage> response) {
                                                            }

                                                            @Override
                                                            public void onFailure(Call<ModuleImage> call, Throwable t) {
                                                                /* DISMISS THE PROGRESS DIALOG  */
                                                                progressDialog.dismiss();

                                                                Log.e("DELETE FAILURE", t.getMessage());
                                                                Crashlytics.logException(t);
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        /* DISMISS THE PROGRESS DIALOG  */
                                                        progressDialog.dismiss();

                                                        Log.e("DELETE FAILURE", e.getMessage());
                                                        Crashlytics.logException(e);
                                                    }
                                                });
                                            }

                                            /* NOW DELETE THE TRAINING MODULE RECORD */
                                            ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
                                            Call<Module> callModule = api.deleteTrainingModule(moduleID);
                                            callModule.enqueue(new Callback<Module>() {
                                                @Override
                                                public void onResponse(Call<Module> call, Response<Module> response) {
                                                    if (response.isSuccessful())    {
                                                        /* SHOW THE CONFIRMATION */
                                                        Toast.makeText(getActivity(), "Deleted successfully...", Toast.LENGTH_SHORT).show();

                                                        /* CLEAR THE ARRAY */
                                                        arrModules.clear();

                                                        /* SHOW THE PROGRESS AND FETCH THE LIST OF TRAINING MODULES */
                                                        linlaProgress.setVisibility(View.VISIBLE);
                                                        fetchTrainingModules();

                                                        /* DISMISS THE PROGRESS DIALOG */
                                                        progressDialog.dismiss();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Failed to delete...", Toast.LENGTH_SHORT).show();

                                                        /* DISMISS THE PROGRESS DIALOG */
                                                        progressDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Module> call, Throwable t) {
                                                    Log.e("DELETE FAILURE", t.getMessage());
                                                    Crashlytics.logException(t);
                                                }
                                            });
                                        } else {
                                            ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
                                            Call<Module> callModule = api.deleteTrainingModule(moduleID);
                                            callModule.enqueue(new Callback<Module>() {
                                                @Override
                                                public void onResponse(Call<Module> call, Response<Module> response) {
                                                    if (response.isSuccessful())    {
                                                        /* SHOW THE CONFIRMATION */
                                                        Toast.makeText(getActivity(), "Deleted successfully...", Toast.LENGTH_SHORT).show();

                                                        /* CLEAR THE ARRAY */
                                                        arrModules.clear();

                                                        /* SHOW THE PROGRESS AND FETCH THE LIST OF TRAINING MODULES */
                                                        linlaProgress.setVisibility(View.VISIBLE);
                                                        fetchTrainingModules();

                                                        /* DISMISS THE PROGRESS DIALOG */
                                                        progressDialog.dismiss();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Failed to delete...", Toast.LENGTH_SHORT).show();

                                                        /* DISMISS THE PROGRESS DIALOG */
                                                        progressDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Module> call, Throwable t) {
                                                    Log.e("DELETE FAILURE", t.getMessage());
                                                    Crashlytics.logException(t);
                                                }
                                            });
                                        }
                                    } else {
                                        ModulesAPI api = ZenApiClient.getClient().create(ModulesAPI.class);
                                        Call<Module> callModule = api.deleteTrainingModule(moduleID);
                                        callModule.enqueue(new Callback<Module>() {
                                            @Override
                                            public void onResponse(Call<Module> call, Response<Module> response) {
                                                if (response.isSuccessful())    {
                                                    /* SHOW THE CONFIRMATION */
                                                    Toast.makeText(getActivity(), "Deleted successfully...", Toast.LENGTH_SHORT).show();

                                                    /* CLEAR THE ARRAY */
                                                    arrModules.clear();

                                                    /* SHOW THE PROGRESS AND FETCH THE LIST OF TRAINING MODULES */
                                                    linlaProgress.setVisibility(View.VISIBLE);
                                                    fetchTrainingModules();

                                                    /* DISMISS THE PROGRESS DIALOG */
                                                    progressDialog.dismiss();
                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to delete...", Toast.LENGTH_SHORT).show();

                                                    /* DISMISS THE PROGRESS DIALOG */
                                                    progressDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Module> call, Throwable t) {
                                                /* DISMISS THE PROGRESS DIALOG */
                                                progressDialog.dismiss();

                                                Log.e("DELETE FAILURE", t.getMessage());
                                                Crashlytics.logException(t);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<ModuleImages> call, Throwable t) {
                                    Log.e("IMAGES FAILURE", t.getMessage());
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
                    }).show();
        }

        @NonNull
        @Override
        public TrainingModulesAdapter.AdoptionsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.training_modules_item, parent, false);

            return new TrainingModulesAdapter.AdoptionsVH(itemView);
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
                LinearLayoutManager llmAppointments = new LinearLayoutManager(getActivity());
                llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
                llmAppointments.setAutoMeasureEnabled(true);
                listModuleImages.setLayoutManager(llmAppointments);
                listModuleImages.setHasFixedSize(true);
                listModuleImages.setNestedScrollingEnabled(false);

                /* CONFIGURE THE ADAPTER */
                adapter = new TrainingModuleImagesAdapter(getActivity(), arrImages);
                listModuleImages.setAdapter(adapter);
            }
        }
    }
}