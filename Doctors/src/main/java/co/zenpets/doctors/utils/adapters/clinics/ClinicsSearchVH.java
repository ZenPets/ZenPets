package co.zenpets.doctors.utils.adapters.clinics;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import co.zenpets.doctors.R;

class ClinicsSearchVH extends RecyclerView.ViewHolder {
    final LinearLayout linlaClinicContainer;
    final SimpleDraweeView imgvwClinicLogo;
    final AppCompatTextView txtClinicName;
    final AppCompatTextView txtClinicAddress;

    ClinicsSearchVH(View v) {
        super(v);

        /* CAST THE LAYOUT ELEMENTS */
        linlaClinicContainer = v.findViewById(R.id.linlaClinicContainer);
        imgvwClinicLogo = v.findViewById(R.id.imgvwClinicLogo);
        txtClinicName = v.findViewById(R.id.txtClinicName);
        txtClinicAddress = v.findViewById(R.id.txtClinicAddress);
    }
}