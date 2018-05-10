package biz.zenpets.users.details.pets.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import biz.zenpets.users.creator.pet.MedicalRecordCreator;
import biz.zenpets.users.modifier.pet.MedicalRecordModifier;
import biz.zenpets.users.utils.adapters.pet.records.MedicalRecordsImagesAdapter;
import biz.zenpets.users.utils.helpers.classes.FilterRecordTypes;
import biz.zenpets.users.utils.helpers.classes.VectorDrawableUtils;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.pets.records.MedicalImage;
import biz.zenpets.users.utils.models.pets.records.MedicalImages;
import biz.zenpets.users.utils.models.pets.records.MedicalRecord;
import biz.zenpets.users.utils.models.pets.records.MedicalRecords;
import biz.zenpets.users.utils.models.pets.records.MedicalRecordsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalRecordsFragment extends Fragment {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /** THE INCOMING PET ID **/
    private String PET_ID = null;

    /** THE SELECTED RECORD TYPE ID AND NAME (FOR FILTERING THE LIST OF RECORDS) **/
    private String RECORD_TYPE_ID = null;

    /** THE MEDICAL RECORDS ARRAY LIST **/
    ArrayList<MedicalRecord> arrRecords = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listMedicalRecords) RecyclerView listMedicalRecords;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    /** ADD A NEW MEDICAL RECORD **/
    @OnClick(R.id.fabNewMedicalRecord) void newRecord() {
        Intent intent = new Intent(getActivity(), MedicalRecordCreator.class);
        intent.putExtra("PET_ID", PET_ID);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pet_records_fragment_list, container, false);
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

    /***** FETCH THE PET'S MEDICAL RECORDS *****/
    private void fetchMedicalRecords() {
        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
        Call<MedicalRecords> call = api.fetchPetMedicalRecords(PET_ID, RECORD_TYPE_ID);
        call.enqueue(new Callback<MedicalRecords>() {
            @Override
            public void onResponse(Call<MedicalRecords> call, Response<MedicalRecords> response) {
                if (response.body() != null && response.body().getRecords() != null)    {
                    arrRecords = response.body().getRecords();

                    /* CHECK OF RECORDS ARE AVAILABLE */
                    if (arrRecords.size() > 0)  {
                        /* SHOW THE APPOINTMENTS RECYCLER AND HIDE THE EMPTY LAYOUT */
                        listMedicalRecords.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE DOCTORS RECYCLER VIEW */
                        listMedicalRecords.setAdapter(new MedicalRecordsAdapter(arrRecords));

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                        listMedicalRecords.setVisibility(View.GONE);
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                    listMedicalRecords.setVisibility(View.GONE);
                    linlaEmpty.setVisibility(View.VISIBLE);

                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MedicalRecords> call, Throwable t) {
                Log.e("EXCEPTION", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            /* CLEAR THE ARRAY LIST */
            arrRecords.clear();

            /* CLEAR THE ARRAY LIST, SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
            arrRecords.clear();
            linlaProgress.setVisibility(View.VISIBLE);
            fetchMedicalRecords();

        } else if (resultCode == Activity.RESULT_OK && requestCode == 102)  {
            Bundle bundle = data.getExtras();
            if (bundle != null && bundle.containsKey("RECORD_TYPE_ID")) {
                RECORD_TYPE_ID = bundle.getString("RECORD_TYPE_ID");
                String RECORD_TYPE_NAME = bundle.getString("RECORD_TYPE_NAME");
                if (RECORD_TYPE_ID != null && RECORD_TYPE_NAME != null) {

                    /* CLEAR THE ARRAY LIST */
                    arrRecords.clear();

                    /* SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchMedicalRecords();
                } else {
                    /* EXPLICITLY MARK THE PROBLEM ID "NULL" */
                    RECORD_TYPE_ID = null;

                    /* CLEAR THE ARRAY LIST */
                    arrRecords.clear();
                    /* SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
                    linlaProgress.setVisibility(View.VISIBLE);
                    fetchMedicalRecords();
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 103)  {
            /* CLEAR THE ARRAY LIST */
            arrRecords.clear();

            /* SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
            linlaProgress.setVisibility(View.VISIBLE);
            fetchMedicalRecords();
        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID")) {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {

                /* CLEAR THE ARRAY LIST, SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
                arrRecords.clear();
                linlaProgress.setVisibility(View.VISIBLE);
                fetchMedicalRecords();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.menuFilter:
                Intent intent = new Intent(getActivity(), FilterRecordTypes.class);
                if (RECORD_TYPE_ID != null) {
                    intent.putExtra("RECORD_TYPE_ID", RECORD_TYPE_ID);
                    startActivityForResult(intent, 102);
                } else {
                    startActivityForResult(intent, 102);
                }
                break;
            default:
                break;
        }
        return false;
    }

    /***** CONFIGURE THE RECYCLER VIEW *****/
    private void configRecycler() {
        /* SET THE MEDICAL RECORDS CONFIGURATION */
        LinearLayoutManager managerMedical = new LinearLayoutManager(getActivity());
        managerMedical.setOrientation(LinearLayoutManager.VERTICAL);
        listMedicalRecords.setLayoutManager(managerMedical);
        listMedicalRecords.setHasFixedSize(true);

        /* SET THE MEDICAL RECORDS ADAPTER */
        listMedicalRecords.setAdapter(new MedicalRecordsAdapter(arrRecords));
    }

    /***** THE MEDICAL RECORDS ADAPTER *****/
    class MedicalRecordsAdapter extends RecyclerView.Adapter<MedicalRecordsAdapter.RecordsVH> {

        /** A CONTEXT INSTANCE **/
        private Context mContext;

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<MedicalRecord> arrRecords;

        /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
        MedicalRecordsImagesAdapter adapter;
        ArrayList<MedicalImage> arrImages = new ArrayList<>();

        MedicalRecordsAdapter(ArrayList<MedicalRecord> arrRecords) {

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
        public void onBindViewHolder(@NonNull final RecordsVH holder, final int position) {
            final MedicalRecord data = arrRecords.get(position);

            /* GET THE RECORD TYPE ID */
            String strRecordTypeID = data.getRecordTypeID();
            if (strRecordTypeID.equalsIgnoreCase("1")) {
                holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_prescription_dark, R.color.primary));
            } else if (strRecordTypeID.equalsIgnoreCase("2"))   {
                holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_surgeon_light, R.color.primary));
            } else if (strRecordTypeID.equalsIgnoreCase("3"))   {
                holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_dental_light, R.color.primary));
            } else if (strRecordTypeID.equalsIgnoreCase("4"))   {
                holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_check_up_light, R.color.primary));
            } else if (strRecordTypeID.equalsIgnoreCase("5"))   {
                holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_other_record_light, R.color.primary));
            }

            /* SET THE RECORD TYPE NAME */
            if (data.getRecordTypeName() != null)   {
                holder.txtRecordTypeName.setText(data.getRecordTypeName());
            }

            /* SET THE RECORD NOTES */
            if (data.getMedicalRecordNotes() != null
                    && !data.getMedicalRecordNotes().equalsIgnoreCase("null")
                    && !data.getMedicalRecordNotes().equalsIgnoreCase(""))   {
                holder.txtMedicalRecordNotes.setText(data.getMedicalRecordNotes());
            } else {
                holder.txtMedicalRecordNotes.setText("No notes added...");
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
                    Log.e("EXCEPTION", t.getMessage());
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
                                    Intent intent = new Intent(getActivity(), MedicalRecordModifier.class);
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
                    .title("Delete Record?")
                    .cancelable(false)
                    .content("Are you sure you want to delete this Medical Record?")
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
                            Call<MedicalRecord> call = api.deleteRecord(medicalRecordID);
                            call.enqueue(new Callback<MedicalRecord>() {
                                @Override
                                public void onResponse(Call<MedicalRecord> call, Response<MedicalRecord> response) {
                                    if (response.isSuccessful())    {
                                        /* SHOW THE SUCCESS MESSAGE */
                                        Toast.makeText(getActivity(), "Record deleted...", Toast.LENGTH_SHORT).show();

                                        /* CLEAR THE ARRAY LIST */
                                        arrRecords.clear();

                                        /* SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
                                        linlaProgress.setVisibility(View.VISIBLE);
                                        fetchMedicalRecords();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete record...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MedicalRecord> call, Throwable t) {
                                    Log.e("DELETE FAILURE", t.getMessage());
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
        public RecordsVH onCreateViewHolder(ViewGroup parent, int viewType) {

            /* INSTANTIATE THE CONTEXT INSTANCE */
            mContext = parent.getContext();

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.pet_records_fragment_item, parent, false);

            return new RecordsVH(itemView, viewType);
        }

        class RecordsVH extends RecyclerView.ViewHolder	{
            @BindView(R.id.timelineMarker) TimelineView timelineMarker;
            @BindView(R.id.txtRecordTypeName) AppCompatTextView txtRecordTypeName;
            @BindView(R.id.imgvwRecordOptions) IconicsImageView imgvwRecordOptions;
            @BindView(R.id.txtMedicalRecordNotes) AppCompatTextView txtMedicalRecordNotes;
            @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
            @BindView(R.id.listRecordImages) RecyclerView listRecordImages;
            @BindView(R.id.txtMedicalRecordDate) AppCompatTextView txtMedicalRecordDate;

            RecordsVH(View v, int viewType) {
                super(v);
                ButterKnife.bind(this, itemView);
                timelineMarker.initLine(viewType);

                /* HIDE THE IMAGE CONTAINER */
                linlaImagesContainer.setVisibility(View.GONE);

                /* CONFIGURE THE RECYCLER VIEW */
                LinearLayoutManager llmAppointments = new LinearLayoutManager(getActivity());
                llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
                llmAppointments.setAutoMeasureEnabled(true);
                listRecordImages.setLayoutManager(llmAppointments);
                listRecordImages.setHasFixedSize(true);
                listRecordImages.setNestedScrollingEnabled(false);

                /* CONFIGURE THE ADAPTER */
                adapter = new MedicalRecordsImagesAdapter(getActivity(), arrImages);
                listRecordImages.setAdapter(adapter);
            }
        }
    }
}