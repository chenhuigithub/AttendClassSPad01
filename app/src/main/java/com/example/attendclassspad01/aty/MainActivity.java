package com.example.attendclassspad01.aty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.ActivityUtils;
import com.example.attendclassspad01.Util.ConstantsForPreferencesUtils;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.Util.PicFormatUtils;
import com.example.attendclassspad01.Util.PreferencesUtils;
import com.example.attendclassspad01.Util.ViewUtils;
import com.example.attendclassspad01.callback.InterfacesCallback;
import com.example.attendclassspad01.callback.OnListenerForPlayVideoSendOutInfo;
import com.example.attendclassspad01.fg.ClassesFg;
import com.example.attendclassspad01.fg.ClassroomTestFg;
import com.example.attendclassspad01.fg.ErrorBookFg;
import com.example.attendclassspad01.model.Classes;
import com.example.attendclassspad01.model.VideoAndAudioInfoModel;
import com.example.attendclassspad01.model.VideoAudio;

import java.util.List;

/**
 * 主界面
 *
 * @author chenhui_2020.02.25
 */
public class MainActivity extends FragmentActivity implements InterfacesCallback.ICanKnowSth2, OnListenerForPlayVideoSendOutInfo {
    private long exitTime = 0;
    private boolean hasLogined = false;//是否已登录，默认为未登录

    private Boolean needRefreshCatalog = false;//是否需要更新目录

    private Classes classes;//班级

    private FragmentManager manager;// Fragment工具
    private Resources res;
    private PicFormatUtils pUtils;// 图片工具
    private Handler uiHandler;// 主线程
    private LocalBroadcastManager broadcastManager;// 广播接收
    private BroadcastReceiver receiver;// 广播

    private ImageView ivUserLogo;//头像
    private TextView tvUserName;//用户名
    private TextView tvClassName;//班级名

    private ClassesFg cFg; // 课堂
    private ClassroomTestFg ctFg; // 随堂测试
    private ErrorBookFg eFg; // 错题本
    private ViewUtils vUtils;//布局助手

    //我的课堂
    private RelativeLayout rlClasses;
    private ImageView ivClasses;
    private TextView tvClasses;
    private View vCursor01;

    //随堂测试
    private RelativeLayout rlTest;
    private ImageView ivTest;
    private TextView tvTest;
    private View vCursorTest;

    //错题本
    private RelativeLayout rlErrorBook;
    private ImageView ivErrorBook;
    private TextView tvErrorBook;
    private View vCursor02;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && this.clearFragmentsTag()) {
            //重建时清除 fragment的状态
            savedInstanceState.remove(ConstantsUtils.BUNDLE_FRAGMENTS_KEY);
        }

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_main);

        // 加入栈
        ActivityUtils utils = new ActivityUtils();
        utils.addActivity(this);

        classes = new Classes();
        uiHandler = new Handler(getMainLooper());

        res = getResources();
        pUtils = new PicFormatUtils();
        vUtils = new ViewUtils(this);

        cFg = new ClassesFg();
        ctFg = new ClassroomTestFg();
        eFg = new ErrorBookFg();

//        dealWithExtras();

        initHandler();

        initView();
        initFg();

        initBroadcastReceiver();

        hasLogined = PreferencesUtils.acquireBooleanInfoFromPreferences(this, ConstantsForPreferencesUtils.HAS_LOGINED);
        if (!hasLogined) {//若未登录先去登录
            toLoginPage();

        } else {
            //若已登录展示信息
            setLogined();

//            rlClasses.performClick();
        }

        rlClasses.performClick();

    }

//    private void dealWithExtras() {
//        Bundle bundle = getIntent().getExtras();
//        if (bundle == null) {
//            return;
//        }
//
//        needRefreshCatalog =bundle.getBoolean(ConstantsUtils.NEED_REFRESH_CATALOG, false);
//    }

    private void initView() {
        ivUserLogo = (ImageView) findViewById(R.id.iv_user_logo_layout_aty_main);
        ivUserLogo.setOnClickListener(new Listeners());

        tvUserName = (TextView) findViewById(R.id.tv_user_name_layout_aty_main);
        tvClassName = (TextView) findViewById(R.id.tv_class_name_layout_aty_main);

        rlClasses = (RelativeLayout) findViewById(R.id.rl_wrapper_classes_layout_aty_main);
        rlClasses.setOnClickListener(new Listeners());
        ivClasses = (ImageView) findViewById(R.id.iv_classes_layout_aty_main);
        tvClasses = (TextView) findViewById(R.id.tv_classes_layout_aty_main);
        vCursor01 = (View) findViewById(R.id.v_cursor01_layout_aty_main);

        rlTest = (RelativeLayout) findViewById(R.id.rl_wrapper_test_layout_aty_main);
        rlTest.setOnClickListener(new Listeners());
        ivTest = (ImageView) findViewById(R.id.iv_test_layout_aty_main);
        tvTest = (TextView) findViewById(R.id.tv_test_layout_aty_main);
        vCursorTest = (View) findViewById(R.id.v_cursor_test_layout_aty_main);

        rlErrorBook = (RelativeLayout) findViewById(R.id.rl_wrapper_error_book_layout_aty_main);
        rlErrorBook.setOnClickListener(new Listeners());
        ivErrorBook = (ImageView) findViewById(R.id.iv_error_book_layout_aty_main);
        tvErrorBook = (TextView) findViewById(R.id.tv_error_book_layout_aty_main);
        vCursor02 = (View) findViewById(R.id.v_cursor02_layout_aty_main);
    }

    private void initFg() {
        manager = getSupportFragmentManager();// FragmentManager调用v4包内的

        showFragment(cFg);
    }

    /**
     * 展示布局
     *
     * @param fg
     */
    private void showFragment(Fragment fg) {
        FragmentTransaction transaction = manager.beginTransaction();// FragmentTransaction调用v4包内的
        if (fg.isAdded()) {// 如果该fragment已添加过，直接显示
            transaction.show(fg).commit();
        } else {
            transaction.replace(R.id.fl_right_content_layout_activity_main, fg)
                    .commit();// 替换为名称为A的fragment并显示它
        }
    }

    /**
     * 监听
     */
    private void initBroadcastReceiver() {
        // 注册广播接收
        broadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter();
//        filter.addAction(ConstantsUtils.ACQUIRE_MATERIAL_INFO);// 获取教材信息
        filter.addAction(ConstantsUtils.REFRESH_USER_INFO);//刷新用户信息
        filter.addAction(ConstantsUtils.CLOSE_APP);//关闭应用

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    switch (bundle.getInt(ConstantsUtils.INTENT)) {
                        case ConstantsUtils.INTENT01: {//跳转至班级分页
//                            cFg = new ClassesFg();
//                            callbackForClass = (InterfacesCallback.ICanKnowSth11) cFg;
//                            showFragment(cFg);
                        }

                        case ConstantsUtils.INTENT02: {//跳转至上课分页
//                            showFragment(aFg);
                        }

                        case ConstantsUtils.INTENT03: {//跳转至测试分页
//                            showFragment(tFg);
                        }
                    }
                }

//                if (ConstantsUtils.ACQUIRE_MATERIAL_INFO.equals(action)) {// 获取教材信息
//                    if (bundle == null) {
//                        return;
//                    }
//                    // 学段ID
//                    String periodID = bundle
//                            .getString(ConstantsUtils.PERIOD_ID);
//                    if (!ValidateFormatUtils.isEmpty(periodID)) {
//                        MainActivity.this.periodIDCurr = periodID;
//                    }
//                    // 学科ID
//                    String subjectID = bundle
//                            .getString(ConstantsUtils.SUBJECT_ID);
//                    if (!ValidateFormatUtils.isEmpty(subjectID)) {
//                        MainActivity.this.subjectIDCurr = subjectID;
//                    }
//                }else
                if (ConstantsUtils.REFRESH_USER_INFO.equals(action)) {//刷新用户信息
                    if (bundle == null) {
                        return;
                    }

                    Boolean hasLogined1 = bundle.getBoolean(ConstantsUtils.HAS_LOGINED);//是否登录的标志
                    if (hasLogined1 != null) {
                        hasLogined = hasLogined1;

                        if (hasLogined) {//已登录
                            setLogined();
                        } else {//未登录或退出登录
                            setLogout();

                            PreferencesUtils.saveInfoToPreferences(MainActivity.this, ConstantsUtils.HAS_LOGINED, false);
                        }
                    }
                } else if (ConstantsUtils.CLOSE_APP.equals(action)) {//关闭应用
                    finish();
                }
            }
        };
        broadcastManager.registerReceiver(receiver, filter);
    }

    /**
     * 初始化线程
     */
    private void initHandler() {
        uiHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PicFormatUtils.SIGN_FOR_BITMAP:
                        vUtils.dismissDialog();

                        // 接收老师头像并显示
                        Object obj = msg.obj;
                        if (obj != null && obj instanceof Bitmap) {
                            Bitmap picBm = (Bitmap) obj;
                            Drawable draw = pUtils.getDrawable(picBm);
                            if (draw != null) {
                                ivUserLogo.setImageDrawable(draw);
                            } else {
                                ivUserLogo.setImageDrawable(getResources()
                                        .getDrawable(R.drawable.ic_launcher_background));
                            }
                        }
                        ivUserLogo.setClickable(true);


                        break;
                }
            }
        };
    }


    /**
     * 设置未登录状态
     */
    private void setLogout() {
        tvUserName.setText("xx学生");
//        btnLogin.setText("点击登录");
        ivUserLogo.setImageDrawable(res.getDrawable(R.drawable.student_logo));

        classes.setID("");
        classes.setName("");
    }


    /**
     * 跳转至登录界面
     */
    private void toLoginPage() {
        Intent intent = new Intent(MainActivity.this,
                LoginActivity.class);
//      intent.putExtra(ConstantsUtils.IS_SWITCH_LOGIN, true);
        startActivity(intent);
    }

    /**
     * 设置登录后的状态
     */
    private void setLogined() {
        //用户名
        String loginName = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.LOGIN_NAME);
        if (!TextUtils.isEmpty(loginName)) {
            tvUserName.setText(loginName);
        }

        // 设置头像
        String headPicUrl = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.USER_HEAD_PIC_URL);
        vUtils.showLoadingDialog("");
        pUtils.getBitmap(headPicUrl, uiHandler);

        //班级名
        String cName = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.CLASS_NAME);
        if (!TextUtils.isEmpty(cName)) {
            classes.setName(cName);
            tvClassName.setText(cName);
        }

        //班级ID
        String cID = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.CLASS_ID);
        if (!TextUtils.isEmpty(cID)) {
            classes.setID(cID);
        }
    }

    /**
     * 设置被选中时页卡标题栏的显示
     *
     * @param tv
     * @param iv
     * @param v
     */
    private void showPagerTitleSelected(TextView tv, ImageView iv, View v) {
        iv.setSelected(true);
        tv.setTextColor(res.getColor(R.color.blue3));
        v.setVisibility(View.VISIBLE);
    }

    /**
     * 设置正常状态下页卡标题栏的显示
     *
     * @param tv
     * @param iv
     * @param v
     */
    private void showPagerTitleNormal(TextView tv, ImageView iv, View v) {
        iv.setSelected(false);
        tv.setTextColor(res.getColor(R.color.white));
        v.setVisibility(View.INVISIBLE);
    }


    protected boolean clearFragmentsTag() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (vUtils != null) {
            vUtils.setCanShowDialog(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (vUtils != null) {
            vUtils.setCanShowDialog(true);
        }

        if (hasLogined) {
//            //刷新课堂界面展示信息
//            Intent intentAction = new Intent();
//            intentAction.setAction(ConstantsUtils.REFRESH_INFO);
//            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intentAction);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void ICanGetVideoInfoCurrentPlay(VideoAndAudioInfoModel info) {

    }

    @Override
    public void doAfterClickBack() {

    }

    @Override
    public void doSwitchFullScreen(List<VideoAudio> list, VideoAudio info) {

    }

    @Override
    public void doSwitchHalfScreen() {

    }

    @Override
    public void getInfo(String str) {

    }

    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.rl_wrapper_classes_layout_aty_main://我的课堂
                    showFragment(cFg);

                    //刷新课堂界面展示信息
                    Intent intentAction = new Intent();
                    intentAction.setAction(ConstantsUtils.REFRESH_INFO);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intentAction);

//                    ivClasses.setSelected(true);
//                    tvClasses.setTextColor(res.getColor(R.color.blue3));
////                    tvClasses.getBackground().mutate().setAlpha(12);
//                    ivErrorBook.setSelected(false);
//                    tvErrorBook.setTextColor(res.getColor(R.color.white));
//                    vCursor01.setVisibility(View.VISIBLE);
//                    vCursor02.setVisibility(View.INVISIBLE);

                    showPagerTitleSelected(tvClasses, ivClasses, vCursor01);
                    showPagerTitleNormal(tvErrorBook, ivErrorBook, vCursor02);
                    showPagerTitleNormal(tvTest, ivTest, vCursorTest);

                    break;
                case R.id.rl_wrapper_error_book_layout_aty_main://错题本
                    showFragment(eFg);

//                    ivErrorBook.setSelected(true);
//                    tvErrorBook.setTextColor(res.getColor(R.color.blue3));
//                    ivClasses.setSelected(false);
//                    tvClasses.setTextColor(res.getColor(R.color.white));
//                    vCursor02.setVisibility(View.VISIBLE);
//                    vCursor01.setVisibility(View.INVISIBLE);

                    showPagerTitleSelected(tvErrorBook, ivErrorBook, vCursor02);
                    showPagerTitleNormal(tvClasses, ivClasses, vCursor01);
                    showPagerTitleNormal(tvTest, ivTest, vCursorTest);

                    break;
                case R.id.iv_user_logo_layout_aty_main://登录
                    Intent intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    startActivity(intent);

                    break;
                case R.id.rl_wrapper_test_layout_aty_main://随堂测试
                    showFragment(ctFg);

                    showPagerTitleSelected(tvTest, ivTest, vCursorTest);
                    showPagerTitleNormal(tvClasses, ivClasses, vCursor01);
                    showPagerTitleNormal(tvErrorBook, ivErrorBook, vCursor02);


                    break;
            }
        }
    }
}
