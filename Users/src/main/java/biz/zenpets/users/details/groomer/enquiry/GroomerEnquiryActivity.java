package biz.zenpets.users.details.groomer.enquiry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import biz.zenpets.users.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GroomerEnquiryActivity extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.imgvwGroomerLogo) SimpleDraweeView imgvwGroomerLogo;
    @BindView(R.id.txtGroomerName) TextView txtGroomerName;
    @BindView(R.id.linlaMessagesProgress) LinearLayout linlaMessagesProgress;
    @BindView(R.id.listMessages) RecyclerView listMessages;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;
    @BindView(R.id.edtMessage) EditText edtMessage;
    @BindView(R.id.imgbtnPostMessage) ImageButton imgbtnPostMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groomer_enquiry_list);
        ButterKnife.bind(this);
    }
}