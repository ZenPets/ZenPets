package co.zenpets.doctors.landing.modules.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.crashlytics.android.Crashlytics;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.zenpets.doctors.R;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.modules.Service;
import co.zenpets.doctors.utils.models.doctors.modules.Services;
import co.zenpets.doctors.utils.models.doctors.modules.ServicesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorServicesFrag extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE SERVICES ARRAY LIST **/
    private ArrayList<Service> arrServices = new ArrayList<>();
    
    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listServices) RecyclerView listServices;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW SERVICE **/
    @OnClick(R.id.fabNewService) protected void newFabService() {
        /* SHOW THE NEW SERVICE DIALOG */
        showNewServiceDialog();
    }

    /** ADD A NEW SERVICE **/
    @OnClick(R.id.linlaEmpty) protected void newService()   {
        /* SHOW THE NEW SERVICE DIALOG */
        showNewServiceDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.doctor_details_service_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE */
        setRetainInstance(true);

        /* INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU */
        setHasOptionsMenu(true);

        /* INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES */
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /* GET THE DOCTOR'S ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* SHOW THE PROGRESS AND START FETCHING THE DOCTOR'S PROFILE */
        if (DOCTOR_ID != null)  {
            /* SHOW THE PROGRESS AND FETCH THE LIST OF SERVICES */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorServices();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /***** FETCH THE LIST OF SERVICES *****/
    private void fetchDoctorServices() {
        /* CLEAR THE ARRAY LIST */
        if (arrServices != null)
            arrServices.clear();

        ServicesAPI apiInterface = ZenApiClient.getClient().create(ServicesAPI.class);
        Call<Services> call = apiInterface.fetchDoctorServices(DOCTOR_ID);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(@NonNull Call<Services> call, @NonNull Response<Services> response) {
                arrServices = response.body().getServices();
                if (arrServices != null && arrServices.size() > 0)    {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listServices.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE ADAPTER */
                    listServices.setAdapter(new ServicesAdapter(arrServices));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listServices.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Services> call, @NonNull Throwable t) {
            }
        });
    }

    /***** SHOW THE NEW SERVICE DIALOG *****/
    private void showNewServiceDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("New Service")
                .content("Add a new service offered by this Doctor. \n\nExample 1: Vaccination / Immunization\nExample 2: Pet Counselling\nExample 3: Pet Grooming")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(5, 200)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .positiveText("ADD")
                .negativeText("Cancel")
                .input("Add a service....", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                        ServicesAPI apiInterface = ZenApiClient.getClient().create(ServicesAPI.class);
                        Call<Service> call = apiInterface.newDoctorService(DOCTOR_ID, input.toString());
                        call.enqueue(new Callback<Service>() {
                            @Override
                            public void onResponse(@NonNull Call<Service> call, @NonNull Response<Service> response) {
                                if (response.isSuccessful())    {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Successfully added a new service", Toast.LENGTH_SHORT).show();

                                    /* CLEAR THE ARRAY LIST */
                                    arrServices.clear();

                                    /* FETCH THE LIST OF SERVICES AGAIN */
                                    fetchDoctorServices();
                                } else {
                                    //TODO: SHOW AN ERROR FOR POSTING NEW SERVICE
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Service> call, @NonNull Throwable t) {
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

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager education = new LinearLayoutManager(getActivity());
        education.setOrientation(LinearLayoutManager.VERTICAL);
        education.setAutoMeasureEnabled(true);
        listServices.setLayoutManager(education);
        listServices.setHasFixedSize(true);
        listServices.setNestedScrollingEnabled(true);

        /* SET THE EDUCATIONS ADAPTER */
        listServices.setAdapter(new ServicesAdapter(arrServices));
    }

    /***** THE SERVICES ADAPTER *****/
    class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ClinicsVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<Service> arrServices;

        ServicesAdapter(ArrayList<Service> arrServices) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrServices = arrServices;
        }

        @Override
        public int getItemCount() {
            return arrServices.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ClinicsVH holder, final int position) {
            final Service data = arrServices.get(position);

            /* SET THE SERVICE NAME */
            if (data.getDoctorServiceName() != null)   {
                holder.txtDoctorService.setText(data.getDoctorServiceName());
            }

            /* SHOW THE SPECIALIZATION OPTIONS */
            holder.imgvwServiceOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu pm = new PopupMenu(getActivity(), view);
                    pm.getMenuInflater().inflate(R.menu.popup_profile_modules, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuEdit:
                                    new MaterialDialog.Builder(getActivity())
                                            .title("New Service")
                                            .content("Add a new service offered by this Doctor. \n\nExample 1: Vaccination / Immunization\nExample 2: Pet Counselling\nExample 3: Pet Grooming")
                                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                            .inputRange(5, 200)
                                            .theme(Theme.LIGHT)
                                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                            .positiveText("Update")
                                            .negativeText("Cancel")
                                            .input("Update the service....", data.getDoctorServiceName(), false, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                                                    ServicesAPI apiInterface = ZenApiClient.getClient().create(ServicesAPI.class);
                                                    Call<Service> call = apiInterface.updateServiceDetails(data.getDoctorServiceID(), DOCTOR_ID, input.toString());
                                                    call.enqueue(new Callback<Service>() {
                                                        @Override
                                                        public void onResponse(@NonNull Call<Service> call, @NonNull Response<Service> response) {
                                                            if (response.isSuccessful())    {
                                                                dialog.dismiss();
                                                                Toast.makeText(getActivity(), "Successfully updated record...", Toast.LENGTH_SHORT).show();

                                                                /* CLEAR THE ARRAY LIST */
                                                                arrServices.clear();

                                                                /* FETCH THE LIST OF SERVICES AGAIN */
                                                                fetchDoctorServices();
                                                            } else {
                                                                Toast.makeText(getActivity(), "Update failed...", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(@NonNull Call<Service> call, @NonNull Throwable t) {
//                                                            Log.e("FAILURE", t.getMessage());
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
                                    break;
                                case R.id.menuDelete:
                                    new MaterialDialog.Builder(getActivity())
                                            .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                                            .title("Delete Specialization?")
                                            .cancelable(true)
                                            .content("Are you sure you want to delete this Specialization? Deleting will stop showing this Specialization on the Zen Pets application for Pet Parents. \n\nTo delete, click the \"Delete\" button. To cancel, press the \"Cancel\" button.")
                                            .positiveText("Delete")
                                            .negativeText("Cancel")
                                            .theme(Theme.LIGHT)
                                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    /* DELETE THE SERVICE */
                                                    deleteService(data.getDoctorServiceID());
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
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

        /***** DELETE THE SERVICE *****/
        private void deleteService(String doctorServiceID) {
            ServicesAPI api = ZenApiClient.getClient().create(ServicesAPI.class);
            Call<Service> call = api.deleteService(doctorServiceID);
            call.enqueue(new Callback<Service>() {
                @Override
                public void onResponse(@NonNull Call<Service> call, @NonNull Response<Service> response) {
                    if (response.isSuccessful())    {
                        /* SHOW THE SUCCESS MESSAGE */
                        Toast.makeText(getActivity(), "Successfully deleted...", Toast.LENGTH_SHORT).show();

                        /* CLEAR THE SERVICES ARRAY LIST */
                        if (arrServices != null)
                            arrServices.clear();

                        /* SHOW THE PROGRESS AND FETCH THE LIST OF SERVICES */
                        linlaProgress.setVisibility(View.VISIBLE);
                        fetchDoctorServices();
                    } else {
                        /* SHOW THE FAILED MESSAGE */
                        Toast.makeText(getActivity(), "Delete failed...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Service> call, @NonNull Throwable t) {
//                    Log.e("DELETE FAILURE", t.getMessage());
                    Crashlytics.logException(t);
                }
            });
        }

        @NonNull
        @Override
        public ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.doctor_details_service_item, parent, false);

            return new ClinicsVH(itemView);
        }

        class ClinicsVH extends RecyclerView.ViewHolder	{
            final AppCompatTextView txtDoctorService;
            final IconicsImageView imgvwServiceOptions;

            ClinicsVH(View v) {
                super(v);
                txtDoctorService = v.findViewById(R.id.txtDoctorService);
                imgvwServiceOptions = v.findViewById(R.id.imgvwServiceOptions);
            }

        }
    }
}