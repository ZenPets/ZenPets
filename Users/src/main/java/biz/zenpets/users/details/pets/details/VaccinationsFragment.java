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
import com.mikepenz.iconics.view.IconicsImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import biz.zenpets.users.R;
import biz.zenpets.users.creator.pet.VaccinationCreator;
import biz.zenpets.users.modifier.pet.VaccinationRecordModifier;
import biz.zenpets.users.utils.adapters.pet.vaccinations.VaccinationImagesAdapter;
import biz.zenpets.users.utils.helpers.classes.FilterVaccinesTypes;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.pets.vaccination.Vaccination;
import biz.zenpets.users.utils.models.pets.vaccination.VaccinationImage;
import biz.zenpets.users.utils.models.pets.vaccination.VaccinationImages;
import biz.zenpets.users.utils.models.pets.vaccination.Vaccinations;
import biz.zenpets.users.utils.models.pets.vaccination.VaccinationsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ConstantConditions")
public class VaccinationsFragment extends Fragment {

    /** THE INCOMING PET ID **/
    private String PET_ID = null;

    /** THE SELECTED VACCINE ID (FOR FILTERING THE LIST OF VACCINATIONS) **/
    private String VACCINE_ID = null;

    /** THE VACCINATIONS ARRAY LIST **/
    private ArrayList<Vaccination> arrVaccinations = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.listVaccinations) RecyclerView listVaccinations;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.txtEmpty) AppCompatTextView txtEmpty;

    @OnClick(R.id.fabNewVaccination) void newVaccination()  {
        Intent intent = new Intent(getActivity(), VaccinationCreator.class);
        intent.putExtra("PET_ID", PET_ID);
        startActivityForResult(intent, 101);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pet_vaccinations_fragment_list, container, false);
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

        /* CONFIGURE THE RECYCLER VIEW */
        configRecycler();

        /* GET THE INCOMING DATA */
        getIncomingData();
    }

    /***** FETCH THE PET'S VACCINATIONS *****/
    private void fetchVaccinations() {
        VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
        Call<Vaccinations> call = api.fetchPetVaccinations(PET_ID, VACCINE_ID);
        call.enqueue(new Callback<Vaccinations>() {
            @Override
            public void onResponse(Call<Vaccinations> call, Response<Vaccinations> response) {
                if (response.body() != null && response.body().getVaccinations() != null)   {
                    arrVaccinations = response.body().getVaccinations();
                    if (arrVaccinations.size() > 0) {
                        /* SHOW THE VACCINATIONS RECYCLER AND HIDE THE EMPTY LAYOUT */
                        listVaccinations.setVisibility(View.VISIBLE);
                        linlaEmpty.setVisibility(View.GONE);

                        /* SET THE VACCINATIONS RECYCLER VIEW */
                        listVaccinations.setAdapter(new VaccinationsAdapter(arrVaccinations));

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    } else {
                        /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                        listVaccinations.setVisibility(View.GONE);
                        linlaEmpty.setVisibility(View.VISIBLE);

                        /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                        linlaProgress.setVisibility(View.GONE);
                    }
                } else {
                    /* HIDE THE RECYCLER AND SHOW THE EMPTY LAYOUT */
                    listVaccinations.setVisibility(View.GONE);
                    linlaEmpty.setVisibility(View.VISIBLE);

                    /* HIDE THE PROGRESS AFTER LOADING THE DATA */
                    linlaProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Vaccinations> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)   {
            if (requestCode == 101) {
                /* CLEAR THE ARRAY LIST */
                arrVaccinations.clear();

                /* SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchVaccinations();

            } else if (requestCode == 102)   {
                Bundle bundle = data.getExtras();
                if (bundle != null && bundle.containsKey("VACCINE_ID")) {
                    VACCINE_ID = bundle.getString("VACCINE_ID");
                    String VACCINE_NAME = bundle.getString("VACCINE_NAME");
                    if (VACCINE_ID != null && VACCINE_NAME != null) {

                        /* CLEAR THE ARRAY LIST */
                        arrVaccinations.clear();

                        /* SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
                        linlaProgress.setVisibility(View.VISIBLE);
                        fetchVaccinations();
                    } else {
                        /* EXPLICITLY MARK THE PROBLEM ID "NULL" */
                        VACCINE_ID = null;

                        /* CLEAR THE ARRAY LIST */
                        arrVaccinations.clear();
                        /* SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
                        linlaProgress.setVisibility(View.VISIBLE);
                        fetchVaccinations();
                    }
                }
            } else if (requestCode == 103)   {
                /* CLEAR THE ARRAY LIST */
                arrVaccinations.clear();

                /* SHOW THE PROGRESS AND FETCH THE PET'S MEDICAL RECORDS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchVaccinations();
            }
        }
    }

    /***** GET THE INCOMING DATA *****/
    private void getIncomingData() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID")) {
            PET_ID = bundle.getString("PET_ID");
            if (PET_ID != null) {

                /* SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
                linlaProgress.setVisibility(View.VISIBLE);
                fetchVaccinations();
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
                Intent intent = new Intent(getActivity(), FilterVaccinesTypes.class);
                if (VACCINE_ID != null) {
                    intent.putExtra("VACCINE_ID", VACCINE_ID);
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
        /* SET THE VACCINATIONS CONFIGURATION */
        LinearLayoutManager managerVaccinations = new LinearLayoutManager(getActivity());
        managerVaccinations.setOrientation(LinearLayoutManager.VERTICAL);
        listVaccinations.setLayoutManager(managerVaccinations);
        listVaccinations.setHasFixedSize(true);

        /* SET THE VACCINATIONS ADAPTER */
        listVaccinations.setAdapter(new VaccinationsAdapter(arrVaccinations));
    }

    /***** THE VACCINATIONS ADAPTER *****/
    @SuppressWarnings("deprecation")
    class VaccinationsAdapter extends RecyclerView.Adapter<VaccinationsAdapter.RecordsVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private final ArrayList<Vaccination> arrVaccinations;

        /** THE VACCINATION IMAGES ADAPTER AND ARRAY LIST **/
        VaccinationImagesAdapter adapter;
        ArrayList<VaccinationImage> arrImages = new ArrayList<>();

        VaccinationsAdapter(ArrayList<Vaccination> arrVaccinations) {

            /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
            this.arrVaccinations = arrVaccinations;
        }

        @Override
        public int getItemCount() {
            return arrVaccinations.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final VaccinationsAdapter.RecordsVH holder, final int position) {
            final Vaccination data = arrVaccinations.get(position);

            /* SET THE VACCINE NAME */
            if (data.getVaccineName() != null)  {
                holder.txtVaccineName.setText(data.getVaccineName());
            }

            /* SET THE VACCINATION REMINDER */
            if (data.getVaccinationReminder() != null)  {
                if (data.getVaccinationReminder().equalsIgnoreCase("true")) {
                    holder.imgvwReminder.setIcon("faw_bell");
                    holder.imgvwReminder.setColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_light));
                } else if (data.getVaccinationReminder().equalsIgnoreCase("false")) {
                    holder.imgvwReminder.setIcon("faw_bell_slash");
                    holder.imgvwReminder.setColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light));
                }
            }

            /* SET THE VACCINATION NOTES */
            if (data.getVaccinationNotes() != null
                    && !data.getVaccinationNotes().equalsIgnoreCase("null")
                    && !data.getVaccinationNotes().equalsIgnoreCase(""))   {
                holder.txtVaccinationNotes.setText(data.getVaccinationNotes());
            } else {
                holder.txtVaccinationNotes.setText(R.string.vaccination_item_no_notes);
            }

            /* SET THE VACCINATION DATE */
            if (data.getVaccinationDate() != null)  {
                String strOriginalDate = data.getVaccinationDate();
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
                SimpleDateFormat outFormat;
                try {
                    Date dtOriginal = inputFormat.parse(strOriginalDate);
                    String strIsolatedDate = dateFormat.format(dtOriginal);
                    if (strIsolatedDate.endsWith("1") && !strIsolatedDate.endsWith("11")) {
                        outFormat = new SimpleDateFormat("dd'st' MMMM yyyy (EEEE)", Locale.getDefault());
                    } else if(strIsolatedDate.endsWith("2") && !strIsolatedDate.endsWith("12"))   {
                        outFormat = new SimpleDateFormat("dd'nd' MMMM yyyy (EEEE)", Locale.getDefault());
                    } else if(strIsolatedDate.endsWith("3") && !strIsolatedDate.endsWith("13"))   {
                        outFormat = new SimpleDateFormat("dd'rd' MMMM yyyy (EEEE)", Locale.getDefault());
                    } else {
                        outFormat = new SimpleDateFormat("dd'th' MMMM yyyy (EEEE)", Locale.getDefault());
                    }
                    holder.txtVaccinationDate.setText(outFormat.format(dtOriginal));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            /* SET THE NEXT VACCINATION DATE */
            if (data.getVaccinationReminder() != null
                    && data.getVaccinationReminder().equalsIgnoreCase("true"))  {
                holder.linlaNextDate.setVisibility(View.VISIBLE);
                if (data.getVaccinationNextDate() != null)  {
                    String strOriginalDate = data.getVaccinationNextDate();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat outFormat;
                    try {
                        Date dtOriginal = inputFormat.parse(strOriginalDate);
                        String strIsolatedDate = dateFormat.format(dtOriginal);
                        if (strIsolatedDate.endsWith("1") && !strIsolatedDate.endsWith("11")) {
                            outFormat = new SimpleDateFormat("dd'st' MMMM yyyy (EEEE)", Locale.getDefault());
                        } else if(strIsolatedDate.endsWith("2") && !strIsolatedDate.endsWith("12"))   {
                            outFormat = new SimpleDateFormat("dd'nd' MMMM yyyy (EEEE)", Locale.getDefault());
                        } else if(strIsolatedDate.endsWith("3") && !strIsolatedDate.endsWith("13"))   {
                            outFormat = new SimpleDateFormat("dd'rd' MMMM yyyy (EEEE)", Locale.getDefault());
                        } else {
                            outFormat = new SimpleDateFormat("dd'th' MMMM yyyy (EEEE)", Locale.getDefault());
                        }
                        holder.txtVaccinationNextDate.setText(outFormat.format(dtOriginal));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                holder.linlaNextDate.setVisibility(View.GONE);
            }

            /* SET THE VACCINATION IMAGES */
            VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
            Call<VaccinationImages> call = api.fetchVaccinationImages(data.getVaccinationID());
            call.enqueue(new Callback<VaccinationImages>() {
                @Override
                public void onResponse(Call<VaccinationImages> call, Response<VaccinationImages> response) {
                    if (response.body() != null && response.body().getImages() != null) {
                        arrImages = response.body().getImages();
                        if (arrImages.size() > 0)   {
                            /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
                            adapter = new VaccinationImagesAdapter(getActivity(), arrImages);
                            holder.listVaccinationImages.setAdapter(adapter);

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
                public void onFailure(Call<VaccinationImages> call, Throwable t) {
//                    Log.e("EXCEPTION", t.getMessage());
                    Crashlytics.logException(t);
                }
            });

            /* SHOW THE DROP DOWN MENU */
            holder.imgvwVaccinationOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu pm = new PopupMenu(getActivity(), holder.imgvwVaccinationOptions);
                    pm.getMenuInflater().inflate(R.menu.pm_pet_records, pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())   {
                                case R.id.menuEdit:
                                    Intent intent = new Intent(getActivity(), VaccinationRecordModifier.class);
                                    intent.putExtra("VACCINATION_ID", data.getVaccinationID());
                                    startActivityForResult(intent, 103);
                                    break;
                                case R.id.menuDelete:
                                    /* SHOW THE DELETE DIALOG */
                                    showDeleteDialog(data.getVaccinationID());
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
        private void showDeleteDialog(final String vaccinationID) {
            new MaterialDialog.Builder(getActivity())
                    .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                    .title("Delete Vaccination?")
                    .cancelable(false)
                    .content("Are you sure you want to delete this Vaccination Record?")
                    .positiveText("Delete")
                    .negativeText("No")
                    .theme(Theme.LIGHT)
                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
                            Call<Vaccination> call = api.deleteVaccination(vaccinationID);
                            call.enqueue(new Callback<Vaccination>() {
                                @Override
                                public void onResponse(Call<Vaccination> call, Response<Vaccination> response) {
                                    if (response.isSuccessful())    {
                                        /* SHOW THE SUCCESS MESSAGE */
                                        Toast.makeText(getActivity(), "Vaccination deleted...", Toast.LENGTH_SHORT).show();

                                        /* CLEAR THE ARRAY LIST */
                                        arrVaccinations.clear();

                                        /* SHOW THE PROGRESS AND FETCH THE PET'S VACCINATION RECORDS */
                                        linlaProgress.setVisibility(View.VISIBLE);
                                        fetchVaccinations();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete vaccination...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Vaccination> call, Throwable t) {
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

        @NonNull
        @Override
        public VaccinationsAdapter.RecordsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.pet_vaccinations_fragment_item, parent, false);

            return new VaccinationsAdapter.RecordsVH(itemView);
        }

        class RecordsVH extends RecyclerView.ViewHolder	{
            @BindView(R.id.txtVaccineName) AppCompatTextView txtVaccineName;
            @BindView(R.id.imgvwReminder) IconicsImageView imgvwReminder;
            @BindView(R.id.imgvwVaccinationOptions) IconicsImageView imgvwVaccinationOptions;
            @BindView(R.id.txtVaccinationNotes) AppCompatTextView txtVaccinationNotes;
            @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
            @BindView(R.id.listVaccinationImages) RecyclerView listVaccinationImages;
            @BindView(R.id.txtVaccinationDate) AppCompatTextView txtVaccinationDate;
            @BindView(R.id.linlaNextDate) LinearLayout linlaNextDate;
            @BindView(R.id.txtVaccinationNextDate) AppCompatTextView txtVaccinationNextDate;

            RecordsVH(View v) {
                super(v);
                ButterKnife.bind(this, itemView);

                /* HIDE THE IMAGE CONTAINER */
                linlaImagesContainer.setVisibility(View.GONE);

                /* CONFIGURE THE RECYCLER VIEW */
                LinearLayoutManager llmAppointments = new LinearLayoutManager(getActivity());
                llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
                llmAppointments.setAutoMeasureEnabled(true);
                listVaccinationImages.setLayoutManager(llmAppointments);
                listVaccinationImages.setHasFixedSize(true);
                listVaccinationImages.setNestedScrollingEnabled(true);

                /* CONFIGURE THE ADAPTER */
                adapter = new VaccinationImagesAdapter(getActivity(), arrImages);
                listVaccinationImages.setAdapter(adapter);
            }
        }
    }
}