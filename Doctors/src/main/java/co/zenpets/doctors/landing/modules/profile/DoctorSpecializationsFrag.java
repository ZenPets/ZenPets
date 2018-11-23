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
import co.zenpets.doctors.utils.models.doctors.modules.Specialization;
import co.zenpets.doctors.utils.models.doctors.modules.Specializations;
import co.zenpets.doctors.utils.models.doctors.modules.SpecializationsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorSpecializationsFrag extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE SPECIALIZATION ARRAY LIST **/
    private ArrayList<Specialization> arrSpecialization = new ArrayList<>();
    
    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listSpecializations) RecyclerView listSpecializations;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW SPECIALIZATION **/
    @OnClick(R.id.fabNewSpecialization) protected void newFabSpecialization()   {
        /* SHOW THE NEW SPECIALIZATION DIALOG */
        showNewSpecializationDialog();
    }

    /** ADD A NEW SPECIALIZATION **/
    @OnClick(R.id.linlaEmpty) protected void newSpecialization()    {
        /* SHOW THE NEW SPECIALIZATION DIALOG */
        showNewSpecializationDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.doctor_details_specialization_list, container, false);
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
            /* SHOW THE PROGRESS AND FETCH THE LIST OF SPECIALIZATIONS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorSpecializations();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /***** FETCH THE LIST OF SPECIALIZATIONS *****/
    private void fetchDoctorSpecializations() {
        /* CLEAR THE ARRAY LIST */
        if (arrSpecialization != null)
            arrSpecialization.clear();

        SpecializationsAPI apiInterface = ZenApiClient.getClient().create(SpecializationsAPI.class);
        Call<Specializations> call = apiInterface.fetchDoctorSpecializations(DOCTOR_ID);
        call.enqueue(new Callback<Specializations>() {
            @Override
            public void onResponse(@NonNull Call<Specializations> call, @NonNull Response<Specializations> response) {
                arrSpecialization = response.body().getSpecializations();
                if (arrSpecialization != null && arrSpecialization.size() > 0)  {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listSpecializations.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE ADAPTER */
                    listSpecializations.setAdapter(new SpecializationAdapter(arrSpecialization));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listSpecializations.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Specializations> call, @NonNull Throwable t) {
            }
        });
    }

    /** SHOW THE NEW SPECIALIZATION DIALOG **/
    private void showNewSpecializationDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("New Specialization")
                .content("Add a new specialization for this Doctor. \n\nExample 1: Veterinary Physician\n\nExample 2: Veterinary Surgeon")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(5, 200)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .positiveText("ADD")
                .negativeText("Cancel")
                .input("Add a specialization....", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                        SpecializationsAPI apiInterface = ZenApiClient.getClient().create(SpecializationsAPI.class);
                        Call<Specialization> call = apiInterface.newDoctorSpecialization(DOCTOR_ID, input.toString());
                        call.enqueue(new Callback<Specialization>() {
                            @Override
                            public void onResponse(@NonNull Call<Specialization> call, @NonNull Response<Specialization> response) {
                                if (response.isSuccessful())    {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Successfully added a new specialization", Toast.LENGTH_SHORT).show();

                                    /* CLEAR THE ARRAY LIST */
                                    arrSpecialization.clear();

                                    /* FETCH THE LIST OF SPECIALIZATIONS AGAIN */
                                    fetchDoctorSpecializations();
                                } else {
                                    //TODO: SHOW AN ERROR FOR POSTING NEW SERVICE
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Specialization> call, @NonNull Throwable t) {
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
        listSpecializations.setLayoutManager(education);
        listSpecializations.setHasFixedSize(true);
        listSpecializations.setNestedScrollingEnabled(true);

        /* SET THE EDUCATIONS ADAPTER */
        listSpecializations.setAdapter(new SpecializationAdapter(arrSpecialization));
    }

    /***** THE SPECIALIZATION ADAPTER *****/
    class SpecializationAdapter extends RecyclerView.Adapter<SpecializationAdapter.ClinicsVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<Specialization> arrSpecialization;

        SpecializationAdapter(ArrayList<Specialization> arrSpecialization) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrSpecialization = arrSpecialization;
        }

        @Override
        public int getItemCount() {
            return arrSpecialization.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ClinicsVH holder, final int position) {
            final Specialization data = arrSpecialization.get(position);

            /* SET THE SPECIALIZATION NAME */
            if (data.getDoctorSpecializationName() != null)   {
                holder.txtDoctorSpecialization.setText(data.getDoctorSpecializationName());
            }

            /* SHOW THE SPECIALIZATION OPTIONS */
            holder.imgvwSpecializationOptions.setOnClickListener(new View.OnClickListener() {
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
                                            .title("New Specialization")
                                            .content("Add a new specialization for this Doctor. \n\nExample 1: Veterinary Physician\n\nExample 2: Veterinary Surgeon")
                                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                            .inputRange(5, 200)
                                            .theme(Theme.LIGHT)
                                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                            .positiveText("Update")
                                            .negativeText("Cancel")
                                            .input("Update specialization....", data.getDoctorSpecializationName(), false, new MaterialDialog.InputCallback() {
                                                @Override
                                                public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                                                    SpecializationsAPI apiInterface = ZenApiClient.getClient().create(SpecializationsAPI.class);
                                                    Call<Specialization> call = apiInterface.updateSpecializationDetails(data.getDoctorSpecializationID(), DOCTOR_ID, input.toString());
                                                    call.enqueue(new Callback<Specialization>() {
                                                        @Override
                                                        public void onResponse(@NonNull Call<Specialization> call, @NonNull Response<Specialization> response) {
                                                            if (response.isSuccessful())    {
                                                                dialog.dismiss();
                                                                Toast.makeText(getActivity(), "Successfully updated record...", Toast.LENGTH_SHORT).show();

                                                                /* CLEAR THE ARRAY LIST */
                                                                arrSpecialization.clear();

                                                                /* FETCH THE LIST OF SPECIALIZATIONS AGAIN */
                                                                fetchDoctorSpecializations();
                                                            } else {
                                                                Toast.makeText(getActivity(), "Update failed...", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(@NonNull Call<Specialization> call, @NonNull Throwable t) {
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
                                                    /* DELETE THE SPECIALIZATION */
                                                    deleteSpecialization(data.getDoctorSpecializationID());
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

        /***** DELETE THE SPECIALIZATION *****/
        private void deleteSpecialization(String doctorSpecializationID) {
            SpecializationsAPI api = ZenApiClient.getClient().create(SpecializationsAPI.class);
            Call<Specialization> call = api.deleteSpecialization(doctorSpecializationID);
            call.enqueue(new Callback<Specialization>() {
                @Override
                public void onResponse(@NonNull Call<Specialization> call, @NonNull Response<Specialization> response) {
                    if (response.isSuccessful())    {
                        /* SHOW THE SUCCESS MESSAGE */
                        Toast.makeText(getActivity(), "Successfully deleted...", Toast.LENGTH_SHORT).show();

                        /* CLEAR THE SPECIALIZATIONS ARRAY LIST */
                        if (arrSpecialization != null)
                            arrSpecialization.clear();

                        /* SHOW THE PROGRESS AND FETCH THE LIST OF SPECIALIZATIONS */
                        linlaProgress.setVisibility(View.VISIBLE);
                        fetchDoctorSpecializations();
                    } else {
                        /* SHOW THE FAILED MESSAGE */
                        Toast.makeText(getActivity(), "Delete failed...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Specialization> call, @NonNull Throwable t) {
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
                    inflate(R.layout.doctor_details_specialization_item, parent, false);

            return new ClinicsVH(itemView);
        }

        class ClinicsVH extends RecyclerView.ViewHolder	{
            final AppCompatTextView txtDoctorSpecialization;
            final IconicsImageView imgvwSpecializationOptions;

            ClinicsVH(View v) {
                super(v);
                txtDoctorSpecialization = v.findViewById(R.id.txtDoctorSpecialization);
                imgvwSpecializationOptions = v.findViewById(R.id.imgvwSpecializationOptions);
            }

        }
    }
}