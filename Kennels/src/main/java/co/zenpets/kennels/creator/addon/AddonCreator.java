package co.zenpets.kennels.creator.addon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.zenpets.kennels.R;

public class AddonCreator extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.inputAddonName) TextInputLayout inputAddonName;
    @BindView(R.id.edtAddonName) TextInputEditText edtAddonName;
    @BindView(R.id.inputCost) TextInputLayout inputCost;
    @BindView(R.id.edtCost) TextInputEditText edtCost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addon_creator);
        ButterKnife.bind(this);
    }
}