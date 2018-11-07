package co.zenpets.doctors.utils.adapters.doctors.modules;

//public class SpecializationAdapter extends RecyclerView.Adapter<SpecializationAdapter.ClinicsVH> {
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private ArrayList<Specialization> arrSpecialization;
//
//    public SpecializationAdapter(ArrayList<Specialization> arrSpecialization) {
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrSpecialization = arrSpecialization;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrSpecialization.size();
//    }
//
//    @Override
//    public void onBindViewHolder(ClinicsVH holder, final int position) {
//        Specialization data = arrSpecialization.get(position);
//
//        /* SET THE SPECIALIZATION NAME */
//        if (data.getDoctorSpecializationName() != null)   {
//            holder.txtDoctorSpecialization.setText(data.getDoctorSpecializationName());
//        }
//    }
//
//    @Override
//    public ClinicsVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.doctor_details_specialization_item, parent, false);
//
//        return new ClinicsVH(itemView);
//    }
//
//    class ClinicsVH extends RecyclerView.ViewHolder	{
//        AppCompatTextView txtDoctorSpecialization;
//
//        ClinicsVH(View v) {
//            super(v);
//            txtDoctorSpecialization = v.findViewById(R.id.txtDoctorSpecialization);
//        }
//
//    }
//}