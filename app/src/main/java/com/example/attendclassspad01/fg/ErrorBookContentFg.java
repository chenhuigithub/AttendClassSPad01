package com.example.attendclassspad01.fg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.adapter.TestQuestionAdapter;
import com.example.attendclassspad01.model.Test;
import com.example.attendclassspad01.model.TestPaper;

import java.util.List;

/**
 * 错题本内容
 */
public class ErrorBookContentFg extends BaseNotPreLoadFg {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了

    private TestPaper paper;//试卷
    private List<Test> questionList;//题目

    private TestQuestionAdapter qAdapter;//题目适配器

    private View allFgView;// 总布局
    private ListView lvQuestion;//题目列表

    public ErrorBookContentFg() {
    }


    public ErrorBookContentFg(TestPaper paper) {
        if (paper != null) {
            this.paper = paper;

            questionList = paper.getQuestionList();
        } else {
            this.paper = new TestPaper();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_error_book_content, null);

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
        lvQuestion = allFgView.findViewById(R.id.lv_question_layout_fg_error_book_content);
        setLvQuestionAdapter();
        setLvListener();
    }

    private void setLvQuestionAdapter() {
        if (qAdapter == null) {
            qAdapter = new TestQuestionAdapter(getActivity(), paper.getQuestionList(), 0);
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

    @Override
    protected void lazyLoad() {
//        if (!isPrepared || !isVisible || hasLoadOnce) {
//            return;
//        }
    }
}
