package biz.zenpets.kennels.utils.adapters.kennels;

//public class KennelsAdapter extends RecyclerView.Adapter<KennelsAdapter.KennelsVH> {
//
//    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
//    private final Activity activity;
//
//    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
//    private final ArrayList<Kennel> arrKennels;
//
//    public KennelsAdapter(Activity activity, ArrayList<Kennel> arrKennels) {
//
//        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrKennels = arrKennels;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrKennels.size();
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final KennelsVH holder, final int position) {
//        final Kennel data = arrKennels.get(position);
//
//        /* SET THE KENNEL COVER PHOTO */
//        String strKennelCoverPhoto = data.getKennelCoverPhoto();
//        if (strKennelCoverPhoto != null
//                && !strKennelCoverPhoto.equalsIgnoreCase("")
//                && !strKennelCoverPhoto.equalsIgnoreCase("null")) {
//            Uri uri = Uri.parse(strKennelCoverPhoto);
//            holder.imgvwKennelCoverPhoto.setImageURI(uri);
//        } else {
//            ImageRequest request = ImageRequestBuilder
//                    .newBuilderWithResourceId(R.drawable.empty_graphic)
//                    .build();
//            holder.imgvwKennelCoverPhoto.setImageURI(request.getSourceUri());
//        }
//
//        /* SET THE KENNEL NAME */
//        if (data.getKennelName() != null)   {
//            holder.txtKennelName.setText(data.getKennelName());
//        }
//
//        /* SET THE KENNEL ADDRESS */
//        String strKennelAddress = data.getKennelAddress();
//        String cityName = data.getCityName();
//        String kennelPinCode = data.getKennelPinCode();
//        holder.txtKennelAddress.setText(activity.getString(R.string.kennel_list_kennel_address_placeholder, strKennelAddress, cityName, kennelPinCode));
//
//        /* SET THE CAPACITY OF LARGE SIZE PETS */
//        if (data.getKennelPetCapacity() != null
//                && !data.getKennelPetCapacity().equalsIgnoreCase("")
//                && !data.getKennelPetCapacity().equalsIgnoreCase("null"))   {
//            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_placeholder, data.getKennelPetCapacity()));
//        } else {
//            holder.txtPetCapacity.setText(activity.getString(R.string.kennel_list_kennel_capacity_zero));
//        }
//
//        /* SHOW THE KENNEL OPTIONS POPUP MENU */
//        holder.imgvwKennelOptions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu pm = new PopupMenu(activity, holder.imgvwKennelOptions);
//                pm.getMenuInflater().inflate(R.menu.pm_kennel_options, pm.getMenu());
//                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId())   {
//                            case R.id.menuDetails:
//                                Intent intentDetails = new Intent(activity, KennelDetails.class);
//                                intentDetails.putExtra("KENNEL_ID", data.getKennelID());
//                                activity.startActivity(intentDetails);
//                                break;
//                            case R.id.menuEdit:
//                                Intent intentEdit = new Intent(activity, KennelModifier.class);
//                                intentEdit.putExtra("KENNEL_ID", data.getKennelID());
//                                activity.startActivityForResult(intentEdit, 101);
//                                break;
//                            case R.id.menuDelete:
//                                /* SHOW THE DELETE DIALOG */
//                                showDeleteDialog(data.getKennelID(), data.getKennelName(), position);
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
//    /***** SHOW THE DELETE DIALOG *****/
//    private void showDeleteDialog(final String kennelID, final String kennelName, final int position) {
//        new MaterialDialog.Builder(activity)
//                .icon(ContextCompat.getDrawable(activity, R.drawable.ic_info_black_24dp))
//                .title("Delete Kennel?")
//                .content(activity.getString(R.string.delete_kennel_content, kennelName))
//                .cancelable(false)
//                .positiveText("Delete")
//                .negativeText("No")
//                .theme(Theme.LIGHT)
//                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        KennelsAPI api = ZenApiClient.getClient().create(KennelsAPI.class);
//                        Call<Kennel> call = api.deleteKennelRecord(kennelID);
//                        call.enqueue(new Callback<Kennel>() {
//                            @Override
//                            public void onResponse(Call<Kennel> call, Response<Kennel> response) {
//                                if (response.isSuccessful())    {
//                                    /* SHOW THE SUCCESS MESSAGE */
//                                    Toast.makeText(activity, "Record deleted...", Toast.LENGTH_SHORT).show();
//
//                                    /* DELETE THE ITEM FROM THE ARRAY LIST (THIS IS TEMPORARY OF COURSE) */
//                                    arrKennels.remove(position);
//                                    notifyDataSetChanged();
//                                } else {
//                                    Toast.makeText(activity, "Failed to delete record...", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Kennel> call, Throwable t) {
//                                Log.e("DELETE FAILURE", t.getMessage());
//                                Crashlytics.logException(t);
//                            }
//                        });
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//    }
//
//    @NonNull
//    @Override
//    public KennelsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.dash_kennels_item, parent, false);
//
//        return new KennelsVH(itemView);
//    }
//
//    class KennelsVH extends RecyclerView.ViewHolder	{
//
//        SimpleDraweeView imgvwKennelCoverPhoto;
//        TextView txtKennelName;
//        IconicsImageView imgvwKennelOptions;
//        TextView txtKennelAddress;
//        TextView txtPetCapacity;
//
//        KennelsVH(View v) {
//            super(v);
//
//            imgvwKennelCoverPhoto = v.findViewById(R.id.imgvwKennelCoverPhoto);
//            txtKennelName = v.findViewById(R.id.txtKennelName);
//            imgvwKennelOptions = v.findViewById(R.id.imgvwKennelOptions);
//            txtKennelAddress = v.findViewById(R.id.txtKennelAddress);
//            txtPetCapacity = v.findViewById(R.id.txtPetCapacity);
//        }
//    }
//}