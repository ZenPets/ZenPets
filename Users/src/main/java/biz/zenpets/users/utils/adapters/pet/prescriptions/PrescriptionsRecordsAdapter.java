package biz.zenpets.users.utils.adapters.pet.prescriptions;

//public class PrescriptionsRecordsAdapter extends RecyclerView.Adapter<PrescriptionsRecordsAdapter.RecordsVH> {
//
//    /** AN ACTIVITY INSTANCE **/
//    private final Activity activity;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private final ArrayList<Prescription> arrRecords;
//
//    /** THE MEDICAL RECORDS IMAGES ADAPTER AND ARRAY LIST **/
//    MedicalRecordsImagesAdapter adapter;
//    ArrayList<MedicalImage> arrImages = new ArrayList<>();
//
//    public PrescriptionsRecordsAdapter(Activity activity, ArrayList<Prescription> arrRecords) {
//
//        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrRecords = arrRecords;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrRecords.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return TimelineView.getTimeLineViewType(position,getItemCount());
//    }
//
//    @Override
//    public void onBindViewHolder(final RecordsVH holder, final int position) {
//        Prescription data = arrRecords.get(position);
//
//        /* SET THE RECORD NOTES */
//        if (data.getMedicalRecordNotes() != null
//                && !data.getMedicalRecordNotes().equalsIgnoreCase("null")
//                && !data.getMedicalRecordNotes().equalsIgnoreCase(""))   {
//            holder.txtMedicalRecordNotes.setText(data.getMedicalRecordNotes());
//        } else {
//            holder.txtMedicalRecordNotes.setText("No notes added...");
//        }
//
//        /* SET THE RECORD DATE */
//        if (data.getMedicalRecordDate() != null)    {
//            String medicalRecordDate = data.getMedicalRecordDate();
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
//            try {
//                Date date = inputFormat.parse(medicalRecordDate);
//                String strDate = outputFormat.format(date);
//                holder.txtMedicalRecordDate.setText(strDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        /* SET THE MEDICAL RECORD IMAGES */
//        MedicalRecordsAPI api = ZenApiClient.getClient().create(MedicalRecordsAPI.class);
//        Call<MedicalImages> call = api.fetchMedicalImages(data.getMedicalRecordID());
//        call.enqueue(new Callback<MedicalImages>() {
//            @Override
//            public void onResponse(Call<MedicalImages> call, Response<MedicalImages> response) {
//                if (response.body() != null && response.body().getImages() != null) {
//                    arrImages = response.body().getImages();
//                    if (arrImages.size() > 0)   {
//                        /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
//                        adapter = new MedicalRecordsImagesAdapter(activity, arrImages);
//                        holder.listRecordImages.setAdapter(adapter);
//
//                        /* SHOW THE IMAGES CONTAINER */
//                        holder.linlaImagesContainer.setVisibility(View.VISIBLE);
//                    } else {
//                        /* HIDE THE IMAGES CONTAINER */
//                        holder.linlaImagesContainer.setVisibility(View.GONE);
//                    }
//                } else {
//                    /* HIDE THE IMAGES CONTAINER */
//                    holder.linlaImagesContainer.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MedicalImages> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//    }
//
//    @Override
//    public RecordsVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.pet_prescriptions_fragment_item, parent, false);
//
//        return new RecordsVH(itemView);
//    }
//
//    class RecordsVH extends RecyclerView.ViewHolder	{
//        @BindView(R.id.txtMedicalRecordNotes) AppCompatTextView txtMedicalRecordNotes;
//        @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
//        @BindView(R.id.listRecordImages) RecyclerView listRecordImages;
//        @BindView(R.id.txtMedicalRecordDate) AppCompatTextView txtMedicalRecordDate;
//
//        RecordsVH(View v) {
//            super(v);
//            ButterKnife.bind(this, itemView);
//
//            /* HIDE THE IMAGE CONTAINER */
//            linlaImagesContainer.setVisibility(View.GONE);
//
//            /* CONFIGURE THE RECYCLER VIEW */
//            LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
//            llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
//            llmAppointments.setAutoMeasureEnabled(true);
//            listRecordImages.setLayoutManager(llmAppointments);
//            listRecordImages.setHasFixedSize(true);
//            listRecordImages.setNestedScrollingEnabled(true);
//
//            /* CONFIGURE THE ADAPTER */
//            adapter = new MedicalRecordsImagesAdapter(activity, arrImages);
//            listRecordImages.setAdapter(adapter);
//        }
//    }
//}