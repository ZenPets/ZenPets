package co.zenpets.doctors.utils.adapters.doctors.claim;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import co.zenpets.doctors.R;
import co.zenpets.doctors.credentials.ClaimProfileActivity;
import co.zenpets.doctors.utils.models.doctors.Doctor;

public class ClaimProfileAdapter extends RecyclerView.Adapter<ClaimProfileAdapter.ClinicsVH> {

    /** AN ACTIVITY INSTANCE **/
    private final Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private final ArrayList<Doctor> arrDoctors;

    public ClaimProfileAdapter(Activity activity, ArrayList<Doctor> arrDoctors) {

        /* CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE */
        this.activity = activity;

        /* CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE */
        this.arrDoctors = arrDoctors;
    }

    @Override
    public int getItemCount() {
        return arrDoctors.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ClinicsVH holder, final int position) {
        final Doctor data = arrDoctors.get(position);

        /* SET THE DOCTOR'S NAME */
        if (data.getDoctorPrefix() != null && data.getDoctorName() != null)   {
            String prefix = data.getDoctorPrefix();
            String name = data.getDoctorName();
            holder.txtDoctorName.setText(activity.getString(R.string.claim_profile_doc_name_placeholder, prefix, name));
        }

        /* SET THE ADDRESS */
        if (data.getDoctorAddress() != null)    {
            holder.txtDoctorAddress.setText(data.getDoctorAddress());
        }

        /* SET THE CLAIMED STATUS */
        if (data.getDoctorClaimed() != null
                && !data.getDoctorClaimed().equalsIgnoreCase("")
                && !data.getDoctorClaimed().equalsIgnoreCase("null"))   {
            if (data.getDoctorClaimed().equalsIgnoreCase("true"))   {
                holder.txtProfileClaimed.setText("Profile is claimed");
            } else {
                holder.txtProfileClaimed.setText("Claim this profile");
                configureLinks(holder.txtProfileClaimed, data.getDoctorID());
            }
        } else {
            holder.txtProfileClaimed.setText("Claim this profile");
            configureLinks(holder.txtProfileClaimed, data.getDoctorID());
        }

        /* SET THE DOCTOR'S EXPERIENCE */
        if (data.getDoctorExperience() != null
                && !data.getDoctorExperience().equalsIgnoreCase("null")
                && !data.getDoctorExperience().equalsIgnoreCase("")) {
            String exp = data.getDoctorExperience();
            String expText = activity.getString(R.string.claim_profile_experience_text);
            holder.txtDoctorExp.setText(activity.getString(R.string.claim_profile_experience_placeholder, exp, expText));
        } else {
            holder.txtDoctorExp.setText("Unknown Experience");
        }

        /* SET THE DOCTOR'S CHARGES */
        String currency = data.getCurrencySymbol();
        if (data.getDoctorCharges() != null
                && !data.getDoctorCharges().equalsIgnoreCase("")
                && !data.getDoctorCharges().equalsIgnoreCase("null"))    {
            String charges = data.getDoctorCharges();
            holder.txtDoctorCharges.setText(currency + " " + charges);
        } else {
            holder.txtDoctorCharges.setText(currency + " Unknown");
        }

        /* SET THE LIKES PERCENTAGE AND TOTAL NUMBER OF LIKES */
        if (data.getDoctorLikes() != null && data.getDoctorLikesPercent() != null)  {
            String percent = data.getDoctorLikesPercent();
            String open = "(";
            String close = ")";
            String votes = data.getDoctorVotes();
            holder.txtDoctorLikes.setText(percent + " " + open + votes + close);
        } else {
            String percent = "0 %";
            String open = "(";
            String close = ")";
            String votes = "0 Votes";
            holder.txtDoctorLikes.setText(percent + " " + open + votes + close);
        }

//        /* SHOW THE DOCTOR DETAILS */
//        holder.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, DoctorDetails.class);
//                intent.putExtra("DOCTOR_ID", data.getDoctorID());
//                intent.putExtra("CLINIC_ID", data.getClinicID());
//                activity.startActivity(intent);
//            }
//        });
    }

    private void configureLinks(AppCompatTextView txtProfileClaimed, final String doctorID) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(activity.getString(R.string.claim_profile_profile_unclaimed));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(activity, ClaimProfileActivity.class);
                intent.putExtra("DOCTOR_ID", doctorID);
                activity.startActivity(intent);
            }
        }, spanTxt.length() - activity.getString(R.string.claim_profile_profile_unclaimed).length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), 18, spanTxt.length(), 0);
        txtProfileClaimed.setMovementMethod(LinkMovementMethod.getInstance());
        txtProfileClaimed.setText(spanTxt, AppCompatTextView.BufferType.SPANNABLE);
    }

    @NonNull
    @Override
    public ClinicsVH onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.claim_profile_item, parent, false);

        return new ClinicsVH(itemView);
    }

    class ClinicsVH extends RecyclerView.ViewHolder	{
        final LinearLayout linlaDoctorContainer;
        final AppCompatTextView txtDoctorName;
        final AppCompatTextView txtDoctorAddress;
        final AppCompatTextView txtProfileClaimed;
        final AppCompatTextView txtDoctorLikes;
        final AppCompatTextView txtDoctorExp;
        final AppCompatTextView txtDoctorCharges;

        ClinicsVH (View v) {
            super(v);
            linlaDoctorContainer = v.findViewById(R.id.linlaDoctorContainer);
            txtDoctorName = v.findViewById(R.id.txtDoctorName);
            txtDoctorAddress = v.findViewById(R.id.txtDoctorAddress);
            txtProfileClaimed = v.findViewById(R.id.txtProfileClaimed);
            txtDoctorLikes = v.findViewById(R.id.txtDoctorLikes);
            txtDoctorExp = v.findViewById(R.id.txtDoctorExp);
            txtDoctorCharges = v.findViewById(R.id.txtDoctorCharges);
        }
    }
}