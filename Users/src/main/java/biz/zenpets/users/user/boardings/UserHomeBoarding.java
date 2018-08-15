package biz.zenpets.users.user.boardings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import biz.zenpets.users.R;
import biz.zenpets.users.utils.AppPrefs;
import biz.zenpets.users.utils.helpers.classes.ZenApiClient;
import biz.zenpets.users.utils.models.boarding.BoardingsAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserHomeBoarding extends AppCompatActivity {

    private AppPrefs getApp()	{
        return (AppPrefs) getApplication();
    }

    /** THE LOGGED IN USER'S ID **/
    String USER_ID = null;

    /** AN INSTANCE OF THE BOARDING API INTERFACE **/
    BoardingsAPI api = ZenApiClient.getClient().create(BoardingsAPI.class);

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaProgress) LinearLayout linlaProgress;
    @BindView(R.id.layoutHomeDetails) ConstraintLayout layoutHomeDetails;
    @BindView(R.id.txtHomeType) TextView txtHomeType;
    @BindView(R.id.txtHomeDogs) TextView txtHomeDogs;
    @BindView(R.id.txtHomeCats) TextView txtHomeCats;
    @BindView(R.id.txtHomeSmoking) TextView txtHomeSmoking;
    @BindView(R.id.txtHomeVaping) TextView txtHomeVaping;
    @BindView(R.id.layoutEmptyHomeDetails) ConstraintLayout layoutEmptyHomeDetails;
    @BindView(R.id.layoutAccessDetails) ConstraintLayout layoutAccessDetails;
    @BindView(R.id.txtAccessCouch) TextView txtAccessCouch;
    @BindView(R.id.txtAccessBed) TextView txtAccessBed;
    @BindView(R.id.txtAccessFans) TextView txtAccessFans;
    @BindView(R.id.layoutEmptyAccessDetails) ConstraintLayout layoutEmptyAccessDetails;

    /** EDIT THE HOME DETAILS **/
    @OnClick(R.id.imgvwHomeEdit) void editHome()    {
    }

    /** EDIT THE ACCESS DETAILS **/
    @OnClick(R.id.imgvwAccessEdit) void editAccess()    {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_boarding);
        ButterKnife.bind(this);

        /* GET THE USER ID**/
        USER_ID = getApp().getUserID();

    }
}