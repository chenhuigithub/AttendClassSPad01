package com.example.attendclassspad01.fg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendclassspad01.R;

/**
 * 课堂
 *
 * @author chenhui_2020.02.27
 */
public class ClassesFg extends BaseNotPreLoadFg {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了

    private View allFgView;// 总布局
    private ImageView iv;//测试图

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_classes, null);

            initView(allFgView);
        }

        // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) allFgView.getParent();
        if (parent != null) {
            parent.removeView(allFgView);
        }
        // 标志当前页面可见
        isPrepared = true;
        lazyLoad();

        return allFgView;
    }

    private void initView(View v) {
        //切换班级
        RelativeLayout rlSwitchClass = (RelativeLayout) v.findViewById(R.id.rl_wrapper_switch_class_layout_fg_classes);
        rlSwitchClass.setOnClickListener(new Listeners());
        iv = (ImageView) v.findViewById(R.id.iv_layout_fg_classes);
        iv.setOnClickListener(new Listeners());


    }

    /**
     * 设置内容显示
     *
     * @param fg
     */
    private void showContent(Fragment fg) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (fg == null) {
            transaction.add(R.id.rl_content_layout_fg_classes, fg);
        } else {
            transaction.replace(R.id.rl_content_layout_fg_classes, fg);
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    protected void lazyLoad() {
//        if (!isPrepared || !isVisible || hasLoadOnce) {
//            return;
//        }
    }

    /**
     * 控件监听
     *
     * @author chenhui
     */
    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_wrapper_switch_class_layout_fg_classes://切换班级

                    break;
                case R.id.iv_layout_fg_classes://图片
                    iv.setVisibility(View.GONE);
                    ClassesAnswerQuestionFg aqFg = new ClassesAnswerQuestionFg();
                    showContent(aqFg);

                    break;
            }
        }
    }
}
