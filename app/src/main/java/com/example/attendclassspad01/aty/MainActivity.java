package com.example.attendclassspad01.aty;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.ActivityUtils;
import com.example.attendclassspad01.fg.ClassesFg;
import com.example.attendclassspad01.fg.ErrorBookFg;

/**
 * 主界面
 *
 * @author chenhui_2020.02.25
 */
public class MainActivity extends FragmentActivity {
    private FragmentManager manager;// Fragment工具

    private Resources res;

    private ClassesFg cFg; // 课堂
    private ErrorBookFg eFg; // 错题本

    //我的课堂
    private RelativeLayout rlClasses;
    private ImageView ivClasses;
    private TextView tvClasses;
    private View vCursor01;
    //错题本
    private RelativeLayout rlErrorBook;
    private ImageView ivErrorBook;
    private TextView tvErrorBook;
    private View vCursor02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_main);

        // 加入栈
        ActivityUtils utils = new ActivityUtils();
        utils.addActivity(this);

        cFg = new ClassesFg();
        eFg = new ErrorBookFg();
        res = getResources();

        initView();
        initFg();

        rlClasses.performClick();
    }

    private void initView() {
        rlClasses = (RelativeLayout) findViewById(R.id.rl_wrapper_classes_layout_aty_main);
        rlClasses.setOnClickListener(new Listeners());
        ivClasses = (ImageView) findViewById(R.id.iv_classes_layout_aty_main);
        tvClasses = (TextView) findViewById(R.id.tv_classes_layout_aty_main);
        vCursor01 = (View) findViewById(R.id.v_cursor01_layout_aty_main);

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

    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.rl_wrapper_classes_layout_aty_main://我的课堂
                    showFragment(cFg);

                    ivClasses.setSelected(true);
                    tvClasses.setTextColor(res.getColor(R.color.blue3));
//                    tvClasses.getBackground().mutate().setAlpha(12);
                    ivErrorBook.setSelected(false);
                    tvErrorBook.setTextColor(res.getColor(R.color.white));
                    vCursor01.setVisibility(View.VISIBLE);
                    vCursor02.setVisibility(View.INVISIBLE);

                    break;
                case R.id.rl_wrapper_error_book_layout_aty_main://错题本
                    showFragment(eFg);

                    ivErrorBook.setSelected(true);
                    tvErrorBook.setTextColor(res.getColor(R.color.blue3));
                    ivClasses.setSelected(false);
                    tvClasses.setTextColor(res.getColor(R.color.white));
                    vCursor02.setVisibility(View.VISIBLE);
                    vCursor01.setVisibility(View.INVISIBLE);

                    break;
            }
        }
    }
}
