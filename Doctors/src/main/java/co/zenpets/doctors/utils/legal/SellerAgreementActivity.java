package co.zenpets.doctors.utils.legal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.zenpets.doctors.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SellerAgreementActivity extends AppCompatActivity {

    /** GO BACK **/
    @OnClick(R.id.imgvwBack) void goBack()  {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legal_seller_agreement);
        ButterKnife.bind(this);
    }
}