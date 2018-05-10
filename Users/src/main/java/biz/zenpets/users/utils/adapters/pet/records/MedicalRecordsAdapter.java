package biz.zenpets.users.utils.adapters.pet.records;

//public class MedicalRecordsAdapter extends RecyclerView.Adapter<MedicalRecordsAdapter.RecordsVH> {
//
//    /** AN ACTIVITY INSTANCE **/
//    private final Activity activity;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private final ArrayList<MedicalRecordsData> arrRecords;
//
//    /** THE CLINIC IMAGES ADAPTER AND ARRAY LIST **/
//    MedicalRecordsImagesAdapter adapter;
//    ArrayList<RecordImagesData> arrImages = new ArrayList<>();
//
//    public MedicalRecordsAdapter(Activity activity, ArrayList<MedicalRecordsData> arrRecords) {
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
//        final MedicalRecordsData data = arrRecords.get(position);
//
//        /* GET THE RECORD TYPE ID */
//        String strRecordTypeID = data.getRecordTypeID();
//        if (strRecordTypeID.equalsIgnoreCase("1")) {
//            holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(activity, R.drawable.ic_prescription_light, R.color.primary));
//        } else if (strRecordTypeID.equalsIgnoreCase("2"))   {
//            holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(activity, R.drawable.ic_surgeon_light, R.color.primary));
//        } else if (strRecordTypeID.equalsIgnoreCase("3"))   {
//            holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(activity, R.drawable.ic_dental_light, R.color.primary));
//        } else if (strRecordTypeID.equalsIgnoreCase("4"))   {
//            holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(activity, R.drawable.ic_check_up_light, R.color.primary));
//        } else if (strRecordTypeID.equalsIgnoreCase("5"))   {
//            holder.timelineMarker.setMarker(VectorDrawableUtils.getDrawable(activity, R.drawable.ic_other_record_light, R.color.primary));
//        }
//
//        /* SET THE RECORD TYPE NAME */
//        if (data.getRecordTypeName() != null)   {
//            holder.txtRecordTypeName.setText(data.getRecordTypeName());
//        }
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
//        /* SET THE PRESCRIPTION IMAGES */
//        arrImages = data.getImages();
//        if (arrImages != null && arrImages.size() > 0)  {
//
//            /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
//            adapter = new MedicalRecordsImagesAdapter(activity, arrImages);
//            holder.listRecordImages.setAdapter(adapter);
//
//            /* SHOW THE IMAGES CONTAINER */
//            holder.linlaImagesContainer.setVisibility(View.VISIBLE);
//        } else {
//            /* HIDE THE IMAGES CONTAINER */
//            holder.linlaImagesContainer.setVisibility(View.GONE);
//        }
//
//        /* SHOW THE DROP DOWN MENU */
//        holder.imgvwRecordOptions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu pm = new PopupMenu(activity, holder.imgvwRecordOptions);
//                pm.getMenuInflater().inflate(R.menu.pm_pet_records, pm.getMenu());
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId())   {
//                            case R.id.menuEdit:
//                                Intent intent = new Intent(activity, MedicalRecordModifier.class);
//                                intent.putExtra("RECORD_ID", data.getMedicalRecordID());
//                                activity.startActivityForResult(intent, 103);
//                                break;
//                            case R.id.menuDelete:
//                                break;
//                            default:
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                pm.show();
//            }
//        });
//    }
//
//    @Override
//    public RecordsVH onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.pet_records_fragment_item, parent, false);
//
//        return new RecordsVH(itemView, viewType);
//    }
//
//    class RecordsVH extends RecyclerView.ViewHolder	{
//        @BindView(R.id.timelineMarker) TimelineView timelineMarker;
//        @BindView(R.id.txtRecordTypeName) AppCompatTextView txtRecordTypeName;
//        @BindView(R.id.imgvwRecordOptions) IconicsImageView imgvwRecordOptions;
//        @BindView(R.id.txtMedicalRecordNotes) AppCompatTextView txtMedicalRecordNotes;
//        @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
//        @BindView(R.id.listRecordImages) RecyclerView listRecordImages;
//        @BindView(R.id.txtMedicalRecordDate) AppCompatTextView txtMedicalRecordDate;
//
//        RecordsVH(View v, int viewType) {
//            super(v);
//            ButterKnife.bind(this, itemView);
//            timelineMarker.initLine(viewType);
//
//            /* CONFIGURE THE RECYCLER VIEW */
//            LinearLayoutManager llmAppointments = new LinearLayoutManager(activity);
//            llmAppointments.setOrientation(LinearLayoutManager.HORIZONTAL);
//            llmAppointments.setAutoMeasureEnabled(true);
//            listRecordImages.setLayoutManager(llmAppointments);
//            listRecordImages.setHasFixedSize(true);
//            listRecordImages.setNestedScrollingEnabled(false);
//
//            /* CONFIGURE THE ADAPTER */
//            adapter = new MedicalRecordsImagesAdapter(activity, arrImages);
//            listRecordImages.setAdapter(adapter);
//        }
//    }
//}