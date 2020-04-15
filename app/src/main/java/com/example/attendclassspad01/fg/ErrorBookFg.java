package com.example.attendclassspad01.fg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.Constants;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.adapter.ErrorBookAdapter;
import com.example.attendclassspad01.callback.InterfacesCallback;
import com.example.attendclassspad01.model.Test;
import com.example.attendclassspad01.model.TestPaper;

import java.util.ArrayList;
import java.util.List;

/**
 * 错题本
 *
 * @author chenhui_2020.02.27
 */
public class ErrorBookFg extends BaseNotPreLoadFg implements InterfacesCallback.ICanKnowSth12 {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了

    private List<TestPaper> testPaperList;//试卷

    private InterfacesCallback.ICanKnowSth12 callback12;//回调

    private ErrorBookAdapter ebAdapter;//错题本适配器
    private ErrorBookTypeFg typeFg;//错题本类型
    private ErrorBookContentFg contentFg;//错题本内容

    private View allFgView;// 总布局
    //    private GridView gdvErrorBook;//错题本
    private RelativeLayout rlContent;
    private TextView tvCount;//题目个数
    private TextView tvMsg03;//提示

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_error_book, null);

            testPaperList = new ArrayList<TestPaper>();
            callback12 = (InterfacesCallback.ICanKnowSth12) this;

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

    private void initView(View allFgView) {
        tvCount = (TextView) allFgView.findViewById(R.id.tv_paper_count_layout_fg_error_book);
        tvCount.setOnClickListener(new Listeners());
        tvMsg03 = (TextView) allFgView.findViewById(R.id.tv_msg03_layout_fg_error_book);

        testPaperList = getTestPaperList();
        typeFg = new ErrorBookTypeFg(testPaperList, callback12);
        showTypePage();
    }

    /**
     * 显示错题本类型页面
     */
    private void showTypePage() {
        showContent(typeFg);

        tvCount.setText("共" + String.valueOf(testPaperList.size() - 1) + "个");
        tvMsg03.setVisibility(View.GONE);
    }

    /**
     * 显示错题本内容页面
     */
    private void showContentPage(TestPaper paper) {
        showContent(contentFg);

        tvMsg03.setVisibility(View.VISIBLE);
        tvMsg03.setText(" > " + paper.getName());
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
            transaction.add(R.id.ll_wrapper_content_layout_fg_error_book, fg);
        } else {
            transaction.replace(R.id.ll_wrapper_content_layout_fg_error_book, fg);
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /**
     * 设置错题本适配器
     */
//    private void setBookAdapter() {
//        if (ebAdapter == null) {
//            ebAdapter = new ErrorBookAdapter(getActivity(), bookList);
//            gdvErrorBook.setAdapter(ebAdapter);
//        } else {
//            ebAdapter.notifyDataSetChanged();
//        }
//    }

    /**
     * 获取错题本假数据
     *
     * @return
     */
    private List<TestPaper> getTestPaperList() {
        List<TestPaper> list = new ArrayList<TestPaper>();
        String name[] = getActivity().getResources().getStringArray(R.array.arrays_high_school_subject);
        if (name.length == Constants.subjectPicResID.length - 1) {
            for (int i = 0; i < Constants.subjectPicResID.length; i++) {
                TestPaper paper = new TestPaper();
                paper.setID("pid" + String.valueOf(i));
                if (i == Constants.subjectPicResID.length - 1) {
                    paper.setID(ConstantsUtils.ADD);
                    paper.setName("新增错题本");
                } else {
                    paper.setName(name[i]);
                    paper.setQuestionList(getTestQuestionList(paper));
                }

                paper.setResID(Constants.subjectPicResID[i]);

                list.add(paper);
            }
        }

        return list;
    }

    /**
     * 获取试题列表
     *
     * @param paper
     * @return
     */
    private List<Test> getTestQuestionList(TestPaper paper) {
        List<Test> tests = new ArrayList<Test>();
        for (int j = 0; j < 10; j++) {
            Test q = new Test();
            q.setId("qid" + j);
            q.setContent("1.以下历史事件中，与关羽无关的是（）：\nＡ.单刀赴会　 Ｂ.水淹七军　 Ｃ.大意失荆州　 Ｄ.七擒七纵 \n“东风不与周郎便，铜雀春深锁二乔”。这首诗的作者生活的年代与诗中所描述的历史事件发生的年代大约相隔了（）： \nＡ.400年 　Ｂ.500年　 Ｃ.600年   D.800年 \n中秋节吃月饼最初的兴起是为了：\nA.纪念屈原   B.推翻元朝统治   C.南宋人民纪念抗金将士   D.由长娥奔月的传说而来");
            q.setUserAnswer("D C B");
            q.setRightAnswer("D C B");
            q.setAnalysis("七擒七纵，七擒孟获，又称南中平定战，是建兴三年蜀汉丞相诸葛亮对南中发动平定南中的战争。当时朱褒、雍闿、高定等人叛变，南中豪强孟获亦有参与，最后诸葛亮亲率大军南下，平定南中。");
            if (paper != null) {
                q.setTitle("错题本:" + paper.getName());
            }
            q.setChoiced(false);

            tests.add(q);
            paper.setQuestionList(tests);
        }

        return tests;
    }

    @Override
    public void doSth(TestPaper paper) {
        if (paper != null) {
            if (ConstantsUtils.ADD.equals(paper.getID())) {
                Toast.makeText(getActivity(), "请新增错题本", Toast.LENGTH_SHORT).show();
            } else {
//            Toast.makeText(getActivity(), "" + paper.getName(), Toast.LENGTH_SHORT).show();
                contentFg = new ErrorBookContentFg(paper);
                showContentPage(paper);

            }
        }
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
                case R.id.tv_paper_count_layout_fg_error_book://（错题本）个数
                    showTypePage();

                    break;
            }
        }
    }
}
