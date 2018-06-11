package biz.zenpets.users.details.pets.details;

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
import com.github.vipulasri.timelineview.TimelineView;
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.modifier.pet.PrescriptionRecordModifier;
import biz.zenpets.users.utils.adapters.pet.records.MedicalRecordsImagesAdapter;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.pets.prescriptions.Prescription;
import biz.zenpets.users.utils.models.pets.prescriptions.Prescriptions;
import biz.zenpets.users.utils.models.pets.prescriptions.PrescriptionsAPI;
import biz.zenpets.users.utils.models.pets.records.MedicalImage;
import biz.zenpets.users.utils.models.pets.records.MedicalImages;
import biz.zenpets.users.utils.models.pets.records.MedicalRecordsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class PrescriptionsFragment extends Fragment {

    /** THE INCOMING PET ID **/
    private String PET_ID = null;

    /** THE PRESCRIPTIONS ARRAY LIST **/
    private ArrayList<Prescription> arrPrescriptions = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listPrescriptions) RecyclerView listPrescriptions;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pet_prescriptions_fragment_list, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /***** FETCH THE PET'S PRESCRIPTION RECORDS *****/
    private void fetchPrescriptionRecords() {
        PrescriptionsAPI api = ZenApiClient.getClient().create(PrescriptionsAPI.class);
        Call<Prescriptions> call = api.fetchPetPrescriptions(PET_ID);
        call.enqueue(new Callback<Prescriptions>() {
            @Override
            public void onResponse(Call<Prescriptions> call, Response<Prescriptions> response) {
                if (response.body() != null && response.body().getPrescriptions() != null)  {
                    arrPrescriptions = response.body().getPrescriptions();
                    if (arrPrescriptions.size() > 0)    {
                        /* SHOW THE VACCINATIONS RECYCLER AND HIDE THE EMPTY LAYOUT */
                        listPrescriptions.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE VACCINATIONS RECYCLER VIEW */
                        listPrescriptions.setAdapter(new PrescriptionsRecordsAdapter(arrPrescriptions));

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                        listPrescriptions.setVisibility(View.GONE);
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                    listPrescriptions.setVisibility(View.GONE);
                    linlaEmpty.setVisibility(View.VISIBLE);

                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Prescriptions> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID")) {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {

                /* CLEAR THE ARRAY LIST, SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
                arrPrescriptions.clear();
                linlaProgress.setVisibility(View.VISIBLE);
                fetchPrescriptionRecords();

            } else {
                Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        } else {
            Toast.makeText(getActivity(), "Failed to get required info...", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)   {
            if (requestCode == 101) {
            } else if (requestCode == 102)  {
            } else if (requestCode == 103)  {
                /* CLEAR THE ARRAY LIST */
                arrPrescriptions.clear();

                /* SHOW THE PROGRESS AND FETCH THE PET'S PRESCRIPTION RECORDS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchPrescriptionRecords();
            }
        }
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE VACCINATIONS CONFIGURATION */
        LinearLayoutManager managerVaccinations = new LinearLayoutManager(getActivity());
        managerVaccinations.setOrientation(LinearLayoutManager.VERTICAL);
        listPrescriptions.setLayoutManager(managerVaccinations);
        listPrescriptions.setHasFixedSize(true);

        /* SET THE VACCINATIONS ADAPTER */
        listPrescriptions.setAdapter(new PrescriptionsRecordsAdapter(arrPrescriptions));
    }

    /***** THE PRESCRIPTIONS ADAPTER *****/
    class PrescriptionsRecordsAdapter extends RecyclerView.Adapter<PrescriptionsRecordsAdapter.RecordsVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<Prescription> arrRecords;

        /** THE MEDICAL RECORDS IMAGES ADAPTER AND ARRAY LIST **/
        MedicalRecordsImagesAdapter adapter;
        ArrayList<MedicalImage> arrImages = new ArrayList<>();

        PrescriptionsRecordsAdapter(ArrayList<Prescription> arrRecords) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrRecords = arrRecords;
        }

        @Override
        public int getItemCount() {
            return arrRecords.size();
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position,getItemCount());
        }

        @Override
        public void onBindViewHolder(final RecordsVH holder, final int position) {
            final Prescription data = arrRecords.get(position);

            /* SET THE RECORD NOTES */
            if (data.getMedicalRecordNotes() != null
                    && !data.getMedicalRecordNotes().equalsIgnoreCase("null")
                    && !data.getMedicalRecordNotes().equalsIgnoreCase(""))   {
                holder.txtMedicalRecordNotes.setText(data.getMedicalRecordNotes());
            } else {
                holder.txtMedicalRecordNotes.setText(getString(R.string.vaccination_item_no_notes));
            }

            /* SET THE RECORD DATE */
            if (data.getMedicalRecordDate() != null)    {
                String medicalRecordDate = data.getMedicalRecordDate();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                try {
                    Date date = inputFormat.parse(medicalRecordDate);
                    String strDate = outputFormat.format(date);
                    holder.txtMedicalRecordDate.setText(strDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            /* SET THE MEDICAL RECORD IMAGES */
            MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
            Call<MedicalImages> call = api.fetchMedicalImages(data.getMedicalRecordID());
            call.enqueue(new Callback<MedicalImages>() {
                @Override
                public void onResponse(Call<MedicalImages> call, Response<MedicalImages> response) {
                    if (response.body() != null && response.body().getImages() != null) {
                        arrImages = response.body().getImages();
                        if (arrImages.size() > 0)   {
                            /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                            adapter = new MedicalRecordsImagesAdapter(getActivity(), arrImages);
                            holder.listRecordImages.setAdapter(adapter);

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
                public void onFailure(Call<MedicalImages> call, Throwable t) {
//                    Log.e("EXCEPTION", t.getMessage());
                    Crashlytics.logException(t);
                }
            });

            /* SHOW THE DROP DOWN MENU */
            holder.imgvwRecordOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu pm = new PopupMenu(getActivity(), holder.imgvwRecordOptions);
                    pm.getMenuInflater().inflate(R.menu.pm_pet_records, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuEdit:
                                    Intent intent = new Intent(getActivity(), PrescriptionRecordModifier.class);
                                    intent.putExtra("RECORD_ID", data.getMedicalRecordID());
                                    startActivityForResult(intent, 103);
                                    break;
                                case R.id.menuDelete:
                                    /* SHOW THE DELETE DIALOG */
                                    showDeleteDialog(data.getMedicalRecordID());
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
        private void showDeleteDialog(final String medicalRecordID) {
            new MaterialDialog.Builder(getActivity())
                    .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                    .title("Delete Prescription?")
                    .cancelable(false)
                    .content("Are you sure you want to delete this Prescription Record?")
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            PrescriptionsAPI api = ZenApiClient.getClient().create(PrescriptionsAPI.class);
                            Call<Prescription> call = api.deletePrescription(medicalRecordID);
                            call.enqueue(new Callback<Prescription>() {
                                @Override
                                public void onResponse(Call<Prescription> call, Response<Prescription> response) {
                                    if (response.isSuccessful())    {
                                        /* SHOW THE SUCCESS MESSAGE */
                                        Toast.makeText(getActivity(), "Prescription deleted...", Toast.LENGTH_SHORT).show();

                                        /* CLEAR THE ARRAY LIST */
                                        arrPrescriptions.clear();

                                        /* SHOW THE PROGRESS AND FETCH THE PET'S PRESCRIPTION RECORDS */
                                        linlaProgress.setVisibility(View.VISIBLE);
                                        fetchPrescriptionRecords();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete prescription...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Prescription> call, Throwable t) {
//                                    Log.e("DELETE FAILURE", t.getMessage());
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

        @Override
        public RecordsVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.pet_prescriptions_fragment_item, parent, false);

            return new RecordsVH(itemView);
        }

        class RecordsVH extends RecyclerView.ViewHolder	{
            @BindView(R.id.imgvwRecordOptions) IconicsImageView imgvwRecordOptions;
            @BindView(R.id.txtMedicalRecordNotes) AppCompatTextView txtMedicalRecordNotes;
            @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
            @BindView(R.id.listRecordImages) RecyclerView listRecordImages;
            @BindView(R.id.txtMedicalRecordDate) AppCompatTextView txtMedicalRecordDate;

            RecordsVH(View v) {
                super(v);
                ButterKnife.bind(this, itemView);

                /* HIDE THE IMAGE CONTAINER */
                linlaImagesContainer.setVisibility(View.GONE);

                /* CONFIGURE THE RECYCLER VIEW */
                LinearLayoutManager llmAppointments = new LinearLayoutManager(getActivity());
                llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
                llmAppointments.setAutoMeasureEnabled(true);
                listRecordImages.setLayoutManager(llmAppointments);
                listRecordImages.setHasFixedSize(true);
                listRecordImages.setNestedScrollingEnabled(true);

                /* CONFIGURE THE ADAPTER */
                adapter = new MedicalRecordsImagesAdapter(getActivity(), arrImages);
                listRecordImages.setAdapter(adapter);
            }
        }
    }
}