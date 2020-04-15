package com.example.attendclassspad01.aty;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.ActivityUtils;
import com.example.attendclassspad01.Util.ConstantsForPreferencesUtils;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.Util.PicFormatUtils;
import com.example.attendclassspad01.Util.PreferencesUtils;
import com.example.attendclassspad01.Util.ViewUtils;
import com.example.attendclassspad01.fg.ClassesFg;
import com.example.attendclassspad01.fg.ClassroomTestFg;
import com.example.attendclassspad01.fg.ErrorBookFg;
import com.example.attendclassspad01.model.Classes;

/**
 * 主界面
 *
 * @author chenhui_2020.02.25
 */
public class MainActivity extends FragmentActivity {
    private boolean hasLogined = false;//是否已登录，默认为未登录

    private Classes classes;//班级

    private FragmentManager manager;// Fragment工具
    private Resources res;
    private PicFormatUtils pUtils;// 图片工具
    private Handler uiHandler;// 主线程

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

        initView();
        initFg();

        rlClasses.performClick();

        checkIfHasLogined();
    }

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
     * 检查用户是否登录
     */
    private void checkIfHasLogined() {
        hasLogined = PreferencesUtils.acquireBooleanInfoFromPreferences(this, ConstantsForPreferencesUtils.HAS_LOGINED);
        if (!hasLogined) {//若未登录先去登录
            toLoginPage();
        } else {
            setLogined();
        }
    }

    /**
     * 跳转至登录界面
     */
    private void toLoginPage() {
        Intent intent = new Intent(MainActivity.this,
                LoginActivity.class);
//        intent.putExtra(ConstantsUtils.IS_SWITCH_LOGIN, true);
        startActivity(intent);
    }

    /**
     * 设置登录后的状态
     */
    private void setLogined() {
        String loginName = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.LOGIN_NAME);
        // 设置头像
        String headPicUrl = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.USER_HEAD_PIC_URL);
        setLogined(loginName, headPicUrl);

        String cName = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.CLASS_NAME);
        if (!TextUtils.isEmpty(cName)) {
            classes.setName(cName);
        }

        String cID = PreferencesUtils.acquireInfoFromPreferences(MainActivity.this, ConstantsForPreferencesUtils.CLASS_ID);
        if (!TextUtils.isEmpty(cID)) {
            classes.setID(cID);
        }
    }

    /**
     * 设置登录状态下的布局
     *
     * @param loginName
     */
    private void setLogined(String loginName, String headPicUrl) {
        if (!TextUtils.isEmpty(loginName)) {
            tvUserName.setText(loginName);
        }

        vUtils.showLoadingDialog("");

        pUtils.getBitmap(headPicUrl, uiHandler);
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

    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.rl_wrapper_classes_layout_aty_main://我的课堂
                    showFragment(cFg);

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

                    finish();

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
