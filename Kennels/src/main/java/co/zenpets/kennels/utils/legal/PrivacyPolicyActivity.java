package co.zenpets.kennels.utils.legal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import co.zenpets.kennels.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrivacyPolicyActivity extends AppCompatActivity {

    /** CAST THE WEB VIEW **/
    @BindView(R.id.webPrivacyPolicy) WebView webPrivacyPolicy;

    /** GO BACK **/
    @OnClick(R.id.imgvwBack) void goBack()  {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legal_privacy_policy);
        ButterKnife.bind(this);

        /* SHOW THE PRIVACY POLICY */
        webPrivacyPolicy.loadUrl(getString(R.string.privacy_policy_url));
    }
}