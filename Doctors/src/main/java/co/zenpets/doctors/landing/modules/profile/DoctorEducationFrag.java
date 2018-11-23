package co.zenpets.doctors.landing.modules.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import co.zenpets.doctors.creator.doctor.EducationCreator;
import co.zenpets.doctors.modifier.profile.EducationModifier;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.helpers.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.modules.Qualification;
import co.zenpets.doctors.utils.models.doctors.modules.Qualifications;
import co.zenpets.doctors.utils.models.doctors.modules.QualificationsAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class DoctorEducationFrag extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE LOGGED IN DOCTOR'S ID **/
    private String DOCTOR_ID = null;

    /** THE EDUCATION ARRAY LIST **/
    private ArrayList<Qualification> arrEducation = new ArrayList<>();

    /** THE NEW REQUEST CODE **/
    private static final int NEW_EDUCATION_REQUEST = 101;
    private static final int EDIT_EDUCATION_REQUEST = 102;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listEducation) RecyclerView listEducation;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW EDUCATIONAL QUALIFICATION **/
    @OnClick(R.id.fabNewEducation) protected void newFabEducation() {
        Intent intent = new Intent(getActivity(), EducationCreator.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        startActivityForResult(intent, NEW_EDUCATION_REQUEST);
    }

    /** ADD A NEW EDUCATIONAL QUALIFICATION **/
    @OnClick(R.id.linlaEmpty) protected void newEducation() {
        Intent intent = new Intent(getActivity(), EducationCreator.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        startActivityForResult(intent, NEW_EDUCATION_REQUEST);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.doctor_details_education_list, container, false);
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
            /* SHOW THE PROGRESS AND FETCH THE LIST OF EDUCATIONAL QUALIFICATIONS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchDoctorEducation();
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /***** FETCH THE DOCTOR'S EDUCATION *****/
    private void fetchDoctorEducation() {
        QualificationsAPI apiInterface = ZenApiClient.getClient().create(QualificationsAPI.class);
        retrofit2.Call<Qualifications> call = apiInterface.fetchDoctorEducation(DOCTOR_ID);
        call.enqueue(new retrofit2.Callback<Qualifications>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Qualifications> call, @NonNull retrofit2.Response<Qualifications> response) {
                arrEducation = response.body().getQualifications();
                if (arrEducation != null && arrEducation.size() > 0)    {
                    /* SET THE ADAPTER */
                    listEducation.setAdapter(new EducationAdapter(arrEducation));

                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listEducation.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listEducation.setVisibility(View.GONE);

                    /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Qualifications> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)   {
            if (requestCode == NEW_EDUCATION_REQUEST || requestCode == EDIT_EDUCATION_REQUEST)   {
                /* CLEAR THE EDUCATION ARRAY LIST */
                if (arrEducation != null)
                    arrEducation.clear();

                /* SHOW THE PROGRESS AND FETCH THE LIST OF EDUCATIONAL QUALIFICATIONS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchDoctorEducation();
            }
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager education = new LinearLayoutManager(getActivity());
        education.setOrientation(LinearLayoutManager.VERTICAL);
        education.setAutoMeasureEnabled(true);
        listEducation.setLayoutManager(education);
        listEducation.setHasFixedSize(true);
        listEducation.setNestedScrollingEnabled(true);

        /* SET THE EDUCATIONS ADAPTER */
        listEducation.setAdapter(new EducationAdapter(arrEducation));
    }

    /***** THE EDUCATION ADAPTER *****/
    class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ClinicsVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<Qualification> arrEducation;

        EducationAdapter(ArrayList<Qualification> arrEducation) {
            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrEducation = arrEducation;
        }

        @Override
        public int getItemCount() {
            return arrEducation.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ClinicsVH holder, final int position) {
            final Qualification data = arrEducation.get(position);

            /* SET THE EDUCATION NAME */
            if (data.getDoctorEducationName() != null)   {
                holder.txtDoctorEducation.setText(data.getDoctorEducationName());
            }

            /* SET THE COLLEGE AND YEAR OF COMPLETION */
            if (data.getDoctorCollegeName() != null)    {
                holder.txtDoctorCollegeYear.setText(Html.fromHtml(data.getDoctorCollegeName()));
            }

            /* SHOW THE SPECIALIZATION OPTIONS */
            holder.imgvwEducationOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu pm = new PopupMenu(getActivity(), view);
                    pm.getMenuInflater().inflate(R.menu.popup_profile_modules, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuEdit:
                                    Intent intent = new Intent(getActivity(), EducationModifier.class);
                                    intent.putExtra("EDUCATION_ID", data.getDoctorEducationID());
                                    startActivityForResult(intent, EDIT_EDUCATION_REQUEST);
                                    break;
                                case R.id.menuDelete:
                                    new MaterialDialog.Builder(getActivity())
                                            .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                                            .title("Delete Education?")
                                            .cancelable(true)
                                            .content("Are you sure you want to delete this Educational Qualification? Deleting will stop showing this Qualification on the Zen Pets application for Pet Parents. \n\nTo delete, click the \"Delete\" button. To cancel, press the \"Cancel\" button.")
                                            .positiveText("Delete")
                                            .negativeText("Cancel")
                                            .theme(Theme.LIGHT)
                                            .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    /* DELETE THE EDUCATION */
                                                    deleteEducation(data.getDoctorEducationID());
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

        /***** DELETE THE EDUCATION *****/
        private void deleteEducation(String doctorEducationID) {
            QualificationsAPI api = ZenApiClient.getClient().create(QualificationsAPI.class);
            Call<Qualification> call = api.deleteEducation(doctorEducationID);
            call.enqueue(new Callback<Qualification>() {
                @Override
                public void onResponse(@NonNull Call<Qualification> call, @NonNull Response<Qualification> response) {
                    if (response.isSuccessful())    {
                        /* SHOW THE SUCCESS MESSAGE */
                        Toast.makeText(getActivity(), "Successfully deleted...", Toast.LENGTH_SHORT).show();

                        /* CLEAR THE EDUCATION ARRAY LIST */
                        if (arrEducation != null)
                            arrEducation.clear();

                        /* SHOW THE PROGRESS AND FETCH THE LIST OF EDUCATIONAL QUALIFICATIONS */
                        linlaProgress.setVisibility(View.VISIBLE);
                        fetchDoctorEducation();
                    } else {
                        /* SHOW THE FAILED MESSAGE */
                        Toast.makeText(getActivity(), "Delete failed...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Qualification> call, @NonNull Throwable t) {
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
                    inflate(R.layout.doctor_details_education_item, parent, false);

            return new ClinicsVH(itemView);
        }

        class ClinicsVH extends RecyclerView.ViewHolder	{
            final AppCompatTextView txtDoctorEducation;
            final AppCompatTextView txtDoctorCollegeYear;
            final IconicsImageView imgvwEducationOptions;

            ClinicsVH(View v) {
                super(v);
                txtDoctorEducation = v.findViewById(R.id.txtDoctorEducation);
                txtDoctorCollegeYear = v.findViewById(R.id.txtDoctorCollegeYear);
                imgvwEducationOptions = v.findViewById(R.id.imgvwEducationOptions);
            }

        }
    }
}