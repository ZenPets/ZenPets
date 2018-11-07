package co.zenpets.doctors.landing.modules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.creator.clinic.ClinicCreator;
import co.zenpets.doctors.creator.clinic.ClinicSearch;
import co.zenpets.doctors.utils.AppPrefs;
import co.zenpets.doctors.utils.TypefaceSpan;
import co.zenpets.doctors.utils.adapters.clinics.UserClinicsAdapter;
import co.zenpets.doctors.utils.helpers.classes.ZenApiClient;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinic;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinics;
import co.zenpets.doctors.utils.models.doctors.clinic.DoctorClinicsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicsFragment extends Fragment {

    private AppPrefs getApp()	{
        return (AppPrefs) getActivity().getApplication();
    }

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** THE CLINICS ARRAY LIST **/
    private ArrayList<DoctorClinic> arrClinics = new ArrayList<>();

    /** THE REQUEST CODES **/
    private static final int REQUEST_NEW_CLINIC = 101;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listClinics) RecyclerView listClinics;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW CLINIC **/
    @OnClick(R.id.txtNewClinic) protected void newClinic()  {
        new MaterialDialog.Builder(getActivity())
                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.clinic_creator_prompter_title))
                .cancelable(true)
                .content(getString(R.string.clinic_creator_prompter_message))
                .positiveText(getString(R.string.clinic_creator_prompter_new))
                .negativeText(getString(R.string.clinic_creator_prompter_search))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getActivity(), ClinicCreator.class);
                        startActivityForResult(intent, REQUEST_NEW_CLINIC);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getActivity(), ClinicSearch.class);
                        startActivityForResult(intent, REQUEST_NEW_CLINIC);
                    }
                }).show();
    }

    /** ADD A NEW CLINIC **/
    @OnClick(R.id.linlaEmpty) protected void newEmptyClinic()    {
        new MaterialDialog.Builder(getActivity())
                .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                .title(getString(R.string.clinic_creator_prompter_title))
                .cancelable(true)
                .content(getString(R.string.clinic_creator_prompter_message))
                .positiveText(getString(R.string.clinic_creator_prompter_new))
                .negativeText(getString(R.string.clinic_creator_prompter_search))
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getActivity(), ClinicCreator.class);
                        startActivityForResult(intent, REQUEST_NEW_CLINIC);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(getActivity(), ClinicSearch.class);
                        startActivityForResult(intent, REQUEST_NEW_CLINIC);
                    }
                }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE */
        View view = inflater.inflate(R.layout.home_clinics_fragment_list, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE ACTIONBAR */
        configAB();

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE DOCTOR ID */
        DOCTOR_ID = getApp().getDoctorID();

        /* SHOW THE PRIMARY PROGRESS AND FETCH THE LIST OF PRIMARY CLINICS */
        linlaProgress.setVisibility(View.VISIBLE);
        listClinics.setVisibility(View.GONE);
        fetchPrimaryClinics();

//        /* SHOW THE SECONDARY PROGRESS AND FETCH THE LIST OF SECONDARY CLINICS */
//        linlaSecondaryProgress.setVisibility(View.VISIBLE);
//        listSecondaryClinics.setVisibility(View.GONE);
//        fetchSecondaryClinics();
    }

    /***** FETCH THE LIST OF PRIMARY CLINICS *****/
    private void fetchPrimaryClinics() {
        /* CLEAR THE ARRAY */
        if (arrClinics != null)
            arrClinics.clear();

        DoctorClinicsAPI api = ZenApiClient.getClient().create(DoctorClinicsAPI.class);
        Call<DoctorClinics> call = api.fetchDoctorClinics(DOCTOR_ID);
        call.enqueue(new Callback<DoctorClinics>() {
            @Override
            public void onResponse(Call<DoctorClinics> call, Response<DoctorClinics> response) {
                arrClinics = response.body().getClinics();
                if (arrClinics != null && arrClinics.size() > 0)  {
                    /* SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT */
                    listClinics.setVisibility(View.VISIBLE);
                    linlaEmpty.setVisibility(View.GONE);

                    /* SET THE ADAPTER */
                    listClinics.setAdapter(new UserClinicsAdapter(getActivity(), arrClinics));
                } else {
                    /* SHOW THE EMPTY LAYOUT AND HIDE THE RECYCLER VIEW */
                    linlaEmpty.setVisibility(View.VISIBLE);
                    listClinics.setVisibility(View.GONE);
                }

                /* HIDE THE PROGRESS AFTER FETCHING THE DATA */
                linlaProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DoctorClinics> call, Throwable t) {
            }
        });
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE CONFIGURATION */
        LinearLayoutManager education = new LinearLayoutManager(getActivity());
        education.setOrientation(LinearLayoutManager.VERTICAL);
        education.setAutoMeasureEnabled(true);
        listClinics.setLayoutManager(education);
        listClinics.setHasFixedSize(true);
        listClinics.setNestedScrollingEnabled(true);

        /* SET THE USER CLINICS ADAPTER */
        listClinics.setAdapter(new UserClinicsAdapter(getActivity(), arrClinics));
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
//        String strTitle = getString(R.string.add_a_new_pet);
        String strTitle = "Clinics";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_NEW_CLINIC)  {
            /* CLEAR THE ARRAY LIST */
            if (arrClinics != null)
                arrClinics.clear();

            /* SHOW THE PROGRESS AND FETCH THE PRIMARY CLINICS AGAIN */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchPrimaryClinics();
        }
    }
}