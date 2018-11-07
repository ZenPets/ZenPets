package co.zenpets.doctors.utils.adapters.doctors.modules;

//public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ClinicsVH> {
//
//    /** AN ACTIVITY INSTANCE **/
//    private Activity activity;
//
//    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
//    private ArrayList<Qualification> arrEducation;
//
//    public EducationAdapter(Activity activity, ArrayList<Qualification> arrEducation) {
//
//        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
//        this.activity = activity;
//
//        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
//        this.arrEducation = arrEducation;
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrEducation.size();
//    }
//
//    @Override
//    public void onBindViewHolder(ClinicsVH holder, final int position) {
//        Qualification data = arrEducation.get(position);
//
//        /* SET THE EDUCATION NAME */
//        if (data.getDoctorEducationName() != null)   {
//            holder.txtDoctorEducation.setText(data.getDoctorEducationName());
//        }
//
//        /* SET THE COLLEGE AND YEAR OF COMPLETION */
//        if (data.getDoctorCollegeName() != null)    {
//            holder.txtDoctorCollegeYear.setText(Html.fromHtml(data.getDoctorCollegeName()));
//        }
//    }
//
//    @Override
//    public ClinicsVH onCreateViewHolder(ViewGroup parent, int i) {
//
//        View itemView = LayoutInflater.
//                from(parent.getContext()).
//                inflate(R.layout.doctor_details_education_item, parent, false);
//
//        return new ClinicsVH(itemView);
//    }
//
//    class ClinicsVH extends RecyclerView.ViewHolder	{
//        AppCompatTextView txtDoctorEducation;
//        AppCompatTextView txtDoctorCollegeYear;
//        IconicsImageView imgvwEducationOptions;
//
//        ClinicsVH(View v) {
//            super(v);
//            txtDoctorEducation = v.findViewById(R.id.txtDoctorEducation);
//            txtDoctorCollegeYear = v.findViewById(R.id.txtDoctorCollegeYear);
//            imgvwEducationOptions = v.findViewById(R.id.imgvwEducationOptions);
//        }
//
//    }
//}