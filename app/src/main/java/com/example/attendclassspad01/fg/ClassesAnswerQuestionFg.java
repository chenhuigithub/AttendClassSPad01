package com.example.attendclassspad01.fg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.Constants;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.adapter.TestQuestionAdapter01;
import com.example.attendclassspad01.model.Test;
import com.example.attendclassspad01.model.TestPaper;

import java.util.ArrayList;
import java.util.List;

/**
 * 答题界面
 * author chenhui_2020.03.04
 */
public class ClassesAnswerQuestionFg extends BaseNotPreLoadFg {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了

    private TestPaper paper;//试卷
    private List<Test> questionList;//题目

    private TestQuestionAdapter01 qAdapter;//题目适配器

    private View allFgView;// 总布局
    private ListView lvQuestion;//题目

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_classes_answer_question, null);

            paper = new TestPaper();

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
        lvQuestion = allFgView.findViewById(R.id.lv_question_layout_fg_caq);
        questionList = getTestQuestionList(paper);
        setLvQuestionAdapter();
        setLvListener();

        //拍照
        ImageView ivTakePhoto = (ImageView) allFgView.findViewById(R.id.iv_take_photo_layout_fg_caq);
        ivTakePhoto.setOnClickListener(new Listeners());
        //本地
        ImageView ivLocalPic = (ImageView) allFgView.findViewById(R.id.iv_local_pic_layout_fg_caq);
        ivLocalPic.setOnClickListener(new Listeners());
        //删除已选照片
        ImageView ivDeletePic = (ImageView) allFgView.findViewById(R.id.iv_delete_layout_fg_caq);
        ivTakePhoto.setOnClickListener(new Listeners());
    }

    private void setLvQuestionAdapter() {
        if (qAdapter == null) {
            qAdapter = new TestQuestionAdapter01(getActivity(), paper.getQuestionList(), 0);
            lvQuestion.setAdapter(qAdapter);
        } else {
            qAdapter.notifyDataSetChanged();
        }
    }


    private void setLvListener() {
        lvQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lvQuestion.setSelected(false);

                if (paper != null) {
                    List<Test> qList = paper.getQuestionList();
                    Test q = qList.get(position);
                    if (q != null) {
                        q.setChoiced(true);
                    }
                }

                setLvQuestionAdapter();
            }
        });
    }

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
            q.setAnswer("D C B");
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
    protected void lazyLoad() {
//        if (!isPrepared || !isVisible || hasLoadOnce) {
//            return;
//        }
    }

    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_take_photo_layout_fg_caq://拍照

                    break;
            }
        }
    }
}
