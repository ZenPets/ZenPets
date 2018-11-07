package co.zenpets.doctors.utils.adapters.doctors.modules;

//public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ClinicsVH> {
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private ArrayList<ServiceData> arrServices;
//
//    public ServicesAdapter(ArrayList<ServiceData> arrServices) {
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrServices = arrServices;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrServices.size();
//    }
//
//    @Override
//    public void onBindViewHolder(ClinicsVH holder, final int position) {
//        ServiceData data = arrServices.get(position);
//
//        /* SET THE SERVICE NAME */
//        if (data.getDoctorServiceName() != null)   {
//            holder.txtDoctorService.setText(data.getDoctorServiceName());
//        }
//    }
//
//    @Override
//    public ClinicsVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.doctor_details_service_item, parent, false);
//
//        return new ClinicsVH(itemView);
//    }
//
//    class ClinicsVH extends RecyclerView.ViewHolder	{
//        AppCompatTextView txtDoctorService;
//
//        ClinicsVH(View v) {
//            super(v);
//            txtDoctorService = v.findViewById(R.id.txtDoctorService);
//        }
//
//    }
//}