package biz.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import biz.zenpets.users.R;
import biz.zenpets.users.details.doctors.DoctorDetailsNew;
import biz.zenpets.users.utils.models.clinics.images.ClinicImage;
import biz.zenpets.users.utils.models.doctors.list.Doctor;

@SuppressWarnings("ConstantConditions")
class NewDoctorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private final ArrayList<Doctor> arrDoctors;
    private final Activity activity;

    private boolean isLoadingAdded = false;

    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
    private final ArrayList<ClinicImage> arrImages = new ArrayList<>();
    private DoctorClinicImagesAdapter adapter;

    public NewDoctorsAdapter(Activity activity) {
        this.activity = activity;
        this.arrDoctors = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.endless_item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.doctors_item, parent, false);
        viewHolder = new DoctorsVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Doctor data = arrDoctors.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final DoctorsVH doctorsVH = (DoctorsVH) holder;

                /* SET THE CLINIC NAME */
                doctorsVH.txtClinicName.setText(data.getClinicName());

                /* SET THE CLINIC ADDRESS */
                String clinicAddress = data.getClinicAddress();
                String cityName = data.getCityName();
                String clinicPinCode = data.getClinicPinCode();
                doctorsVH.txtClinicAddress.setText(activity.getString(R.string.doctor_list_address_placeholder, clinicAddress, cityName, clinicPinCode));

                /* SET THE CLINIC'S DISTANCE FROM THE USER'S LOCATION */
                if (data.getClinicDistance() != null)   {
                    String distance = data.getClinicDistance();
                    String strTilde = activity.getString(R.string.generic_tilde);
                    doctorsVH.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strTilde, distance));
                } else {
                    String distance = "Unknown";
                    String strInfinity = activity.getString(R.string.generic_infinity);
                    doctorsVH.txtClinicDistance.setText(activity.getString(R.string.doctor_list_clinic_distance_placeholder, strInfinity, distance));
                }

                /* SET THE DOCTOR'S NAME */
                String doctorPrefix = data.getDoctorPrefix();
                String doctorName = data.getDoctorName();
                doctorsVH.txtDoctorName.setText(activity.getString(R.string.appointment_creator_details_doctor, doctorPrefix, doctorName));

                /* SET THE DOCTOR'S EXPERIENCE */
                String strExp = activity.getString(R.string.doctor_list_doctor_experience_text);
                String docExperience = data.getDoctorExperience();
                doctorsVH.txtDoctorExp.setText(activity.getString(R.string.doctor_list_doctor_experience_placeholder, docExperience, strExp));

                /* SET THE DOCTOR'S CHARGES */
                String currency = data.getCurrencySymbol();
                String charges = data.getDoctorCharges();
                doctorsVH.txtDoctorCharges.setText(activity.getString(R.string.doctor_details_doc_charges_placeholder, currency, charges));

                /* SET THE LIKES PERCENTAGE AND TOTAL NUMBER OF LIKES */
                if (data.getDoctorLikes() != null && data.getDoctorLikesPercent() != null)  {
                    String percent = data.getDoctorLikesPercent();
                    String open = activity.getString(R.string.doctor_list_votes_open);
                    String close = activity.getString(R.string.doctor_list_votes_close);
                    String votes = data.getDoctorVotes();
                    doctorsVH.txtDoctorLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder,percent, open, votes, close));
                } else {
                    String percent = activity.getString(R.string.doctor_list_votes_percent_text);
                    String open = activity.getString(R.string.doctor_list_votes_open);
                    String close = activity.getString(R.string.doctor_list_votes_close);
                    String votes = activity.getString(R.string.doctor_list_votes_text);
                    doctorsVH.txtDoctorLikes.setText(activity.getString(R.string.doctor_list_votes_placeholder,percent, open, votes, close));
                }

                /* SET THE IMAGES */
                ArrayList<ClinicImage> arrImages = data.getImages();
                if (arrImages != null && arrImages.size() > 0)  {
                    /* CONFIGURE THE RECYCLER VIEW */
                    LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
                    llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
                    llmAppointments.setAutoMeasureEnabled(true);
                    doctorsVH.listClinicImages.setLayoutManager(llmAppointments);
                    doctorsVH.listClinicImages.setHasFixedSize(true);
                    doctorsVH.listClinicImages.setNestedScrollingEnabled(false);

                    /* CONFIGURE THE ADAPTER */
                    DoctorClinicImagesAdapter adapter = new DoctorClinicImagesAdapter(activity, arrImages);

                    /* SET THE ADAPTER TO THE RECYCLER VIEW */
                    doctorsVH.listClinicImages.setAdapter(adapter);

                    /* SHOW THE IMAGES CONTAINER */
                    doctorsVH.linlaImagesContainer.setVisibility(View.VISIBLE);
                } else {
                    /* HIDE THE IMAGES CONTAINER */
                    doctorsVH.linlaImagesContainer.setVisibility(View.GONE);
                }

                /* SHOW THE DOCTOR DETAILS */
                doctorsVH.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, DoctorDetailsNew.class);
                        intent.putExtra("DOCTOR_ID", data.getDoctorID());
                        intent.putExtra("CLINIC_ID", data.getClinicID());
                        activity.startActivity(intent);
                    }
                });
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrDoctors == null ? 0 : arrDoctors.size();
    }

    public void addAll(ArrayList<Doctor> arrDoctors) {
        for (Doctor doctor : arrDoctors) {
            add(doctor);
        }
    }

    private void add(Doctor doctor) {
        arrDoctors.add(doctor);
        notifyItemInserted(arrDoctors.size() - 1);
    }

    private void remove(Doctor doctor) {
        int position = arrDoctors.indexOf(doctor);
        if (position > -1) {
            arrDoctors.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Doctor());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = arrDoctors.size() - 1;
        Doctor item = getItem(position);

        if (item != null) {
            arrDoctors.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Doctor getItem(int position) {
        return arrDoctors.get(position);
    }

    /** THE DOCTORS VIEW HOLDER **/
    class DoctorsVH extends RecyclerView.ViewHolder {

        final LinearLayout linlaDoctorContainer;
        final AppCompatTextView txtDoctorName;
        final AppCompatTextView txtDoctorLikes;
        final AppCompatTextView txtClinicName;
        final AppCompatTextView txtClinicAddress;
        final LinearLayout linlaImagesContainer;
        final RecyclerView listClinicImages;
        final LinearLayout linlaDoctorExp;
        final AppCompatTextView txtDoctorExp;
        final LinearLayout linlaDoctorCharges;
        final AppCompatTextView txtDoctorCharges;
        final LinearLayout linlaClinicDistance;
        final AppCompatTextView txtClinicDistance;

        DoctorsVH(View v) {
            super(v);

            linlaDoctorContainer = v.findViewById(R.id.linlaDoctorContainer);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtDoctorLikes = v.findViewById(R.id.txtDoctorLikes);
            txtClinicName = v.findViewById(R.id.txtClinicName);
            txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
            linlaImagesContainer = v.findViewById(R.id.linlaImagesContainer);
            listClinicImages = v.findViewById(R.id.listClinicImages);
            linlaDoctorExp = v.findViewById(R.id.linlaDoctorExp);
            txtDoctorExp = v.findViewById(R.id.txtDoctorExp);
            linlaDoctorCharges = v.findViewById(R.id.linlaDoctorCharges);
            txtDoctorCharges = v.findViewById(R.id.txtDoctorCharges);
            linlaClinicDistance = v.findViewById(R.id.linlaClinicDistance);
            txtClinicDistance = v.findViewById(R.id.txtClinicDistance);

            /* CONFIGURE THE RECYCLER VIEW */
            LinearLayoutManager manager = new LinearLayoutManager(activity);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            manager.setAutoMeasureEnabled(true);
            listClinicImages.setLayoutManager(manager);
            listClinicImages.setHasFixedSize(true);
            listClinicImages.setNestedScrollingEnabled(false);

            /* CONFIGURE THE ADAPTER */
            adapter = new DoctorClinicImagesAdapter(activity, arrImages);
            listClinicImages.setAdapter(adapter);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {
        LoadingVH(View itemView) {
            super(itemView);
        }
    }
}