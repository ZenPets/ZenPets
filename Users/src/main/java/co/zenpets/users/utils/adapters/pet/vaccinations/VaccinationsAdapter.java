package co.zenpets.users.utils.adapters.pet.vaccinations;

//public class VaccinationsAdapter extends RecyclerView.Adapter<VaccinationsAdapter.RecordsVH> {
//
//    /** AN ACTIVITY INSTANCE **/
//    private final Activity activity;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private final ArrayList<Vaccination> arrVaccinations;
//
//    /** THE VACCINATION IMAGES ADAPTER AND ARRAY LIST **/
//    VaccinationImagesAdapter adapter;
//    ArrayList<VaccinationImage> arrImages = new ArrayList<>();
//
//    public VaccinationsAdapter(Activity activity, ArrayList<Vaccination> arrVaccinations) {
//
//        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrVaccinations = arrVaccinations;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrVaccinations.size();
//    }
//
//    @Override
//    public void onBindViewHolder(final RecordsVH holder, final int position) {
//        final Vaccination data = arrVaccinations.get(position);
//
//        /* SET THE VACCINE NAME */
//        if (data.getVaccineName() != null)  {
//            holder.txtVaccineName.setText(data.getVaccineName());
//        }
//
//        /* SET THE VACCINATION REMINDER */
//        if (data.getVaccinationReminder() != null)  {
//            if (data.getVaccinationReminder().equalsIgnoreCase("true")) {
//                holder.imgvwReminder.setIcon("faw_bell");
//                holder.imgvwReminder.setColor(ContextCompat.getColor(activity, android.R.color.holo_green_light));
//            } else if (data.getVaccinationReminder().equalsIgnoreCase("false")) {
//                holder.imgvwReminder.setIcon("faw_bell_slash");
//                holder.imgvwReminder.setColor(ContextCompat.getColor(activity, android.R.color.holo_red_light));
//            }
//        }
//
//        /* SET THE VACCINATION NOTES */
//        if (data.getVaccinationNotes() != null
//                && !data.getVaccinationNotes().equalsIgnoreCase("null")
//                && !data.getVaccinationNotes().equalsIgnoreCase(""))   {
//            holder.txtVaccinationNotes.setText(data.getVaccinationNotes());
//        } else {
//            holder.txtVaccinationNotes.setText("No notes added...");
//        }
//
//        /* SET THE VACCINATION DATE */
//        if (data.getVaccinationDate() != null)  {
//            SimpleDateFormat outFormat;
//            try {
//                SimpleDateFormat fmtDate = new SimpleDateFormat("dd", Locale.getDefault());
//                String strDate = fmtDate.format(new Date());
//                SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
//                Date date = inFormat.parse(data.getVaccinationDate());
//                if (strDate.endsWith("1") && !strDate.endsWith("11")) {
//                    outFormat = new SimpleDateFormat("dd'st' MMMM yyyy (EEEE)", Locale.getDefault());
//                } else if(strDate.endsWith("2") && !strDate.endsWith("12"))   {
//                    outFormat = new SimpleDateFormat("dd'nd' MMMM yyyy (EEEE)", Locale.getDefault());
//                } else if(strDate.endsWith("3") && !strDate.endsWith("13"))   {
//                    outFormat = new SimpleDateFormat("dd'rd' MMMM yyyy (EEEE)", Locale.getDefault());
//                } else {
//                    outFormat = new SimpleDateFormat("dd'th' MMMM yyyy (EEEE)", Locale.getDefault());
//                }
//                holder.txtVaccinationDate.setText(outFormat.format(date));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        /* SET THE NEXT VACCINATION DATE */
//        if (data.getVaccinationNextDate() != null)  {
//            SimpleDateFormat outFormat;
//            try {
//                SimpleDateFormat fmtDate = new SimpleDateFormat("dd", Locale.getDefault());
//                String strDate = fmtDate.format(new Date());
//                SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
//                Date date = inFormat.parse(data.getVaccinationNextDate());
//                if (strDate.endsWith("1") && !strDate.endsWith("11")) {
//                    outFormat = new SimpleDateFormat("dd'st' MMMM yyyy (EEEE)", Locale.getDefault());
//                } else if(strDate.endsWith("2") && !strDate.endsWith("12"))   {
//                    outFormat = new SimpleDateFormat("dd'nd' MMMM yyyy (EEEE)", Locale.getDefault());
//                } else if(strDate.endsWith("3") && !strDate.endsWith("13"))   {
//                    outFormat = new SimpleDateFormat("dd'rd' MMMM yyyy (EEEE)", Locale.getDefault());
//                } else {
//                    outFormat = new SimpleDateFormat("dd'th' MMMM yyyy (EEEE)", Locale.getDefault());
//                }
//                holder.txtVaccinationNextDate.setText(outFormat.format(date));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        /* SET THE VACCINATION IMAGES */
//        VaccinationsAPI api = ZenApiClient.getClient().create(VaccinationsAPI.class);
//        Call<VaccinationImages> call = api.fetchVaccinationImages(data.getVaccinationID());
//        call.enqueue(new Callback<VaccinationImages>() {
//            @Override
//            public void onResponse(Call<VaccinationImages> call, Response<VaccinationImages> response) {
//                if (response.body() != null && response.body().getImages() != null) {
//                    arrImages = response.body().getImages();
//                    if (arrImages.size() > 0)   {
//                        /* RECONFIGURE AND SET THE ADAPTER TO THE RECYCLER VIEW */
//                        adapter = new VaccinationImagesAdapter(activity, arrImages);
//                        holder.listVaccinationImages.setAdapter(adapter);
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
//            public void onFailure(Call<VaccinationImages> call, Throwable t) {
//                Log.e("EXCEPTION", t.getMessage());
//                Crashlytics.logException(t);
//            }
//        });
//
//            /* SHOW THE DROP DOWN MENU */
//        holder.imgvwVaccinationOptions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu pm = new PopupMenu(activity, holder.imgvwVaccinationOptions);
//                pm.getMenuInflater().inflate(R.menu.pm_pet_records, pm.getMenu());
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId())   {
//                            case R.id.menuEdit:
//                                Intent intent = new Intent(activity, VaccinationRecordModifier.class);
//                                intent.putExtra("VACCINATION_ID", data.getVaccinationID());
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
//    public RecordsVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.pet_vaccinations_fragment_item, parent, false);
//
//        return new RecordsVH(itemView);
//    }
//
//    class RecordsVH extends RecyclerView.ViewHolder	{
//        @BindView(R.id.txtVaccineName) AppCompatTextView txtVaccineName;
//        @BindView(R.id.imgvwReminder) IconicsImageView imgvwReminder;
//        @BindView(R.id.imgvwVaccinationOptions) IconicsImageView imgvwVaccinationOptions;
//        @BindView(R.id.txtVaccinationNotes) AppCompatTextView txtVaccinationNotes;
//        @BindView(R.id.linlaImagesContainer) LinearLayout linlaImagesContainer;
//        @BindView(R.id.listVaccinationImages) RecyclerView listVaccinationImages;
//        @BindView(R.id.txtVaccinationDate) AppCompatTextView txtVaccinationDate;
//        @BindView(R.id.txtVaccinationNextDate) AppCompatTextView txtVaccinationNextDate;
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
//            listVaccinationImages.setLayoutManager(llmAppointments);
//            listVaccinationImages.setHasFixedSize(true);
//            listVaccinationImages.setNestedScrollingEnabled(true);
//
//            /* CONFIGURE THE ADAPTER */
//            adapter = new VaccinationImagesAdapter(activity, arrImages);
//            listVaccinationImages.setAdapter(adapter);
//        }
//    }
//}