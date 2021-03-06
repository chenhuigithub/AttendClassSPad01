package com.example.attendclassspad01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.model.Test;

import java.util.List;

/**
 * 试题列表适配器
 */
public class TestQuestionAdapter01 extends BaseAdapter {
    private Context context;
    private List<Test> testList;//题目
    private int type = 0;//默认是答题状态（1为查看答案解析状态,2为答题解析状态，显示柱状图）
    private boolean showCbox;//是否选中

    public TestQuestionAdapter01(Context context, List<Test> dataList, int type) {
        this.context = context;
        this.testList = dataList;
        this.type = type;
    }


    public TestQuestionAdapter01(Context context, List<Test> dataList, int type, FragmentActivity activity) {
        this.context = context;
        this.testList = dataList;
        this.type = type;

//        manager = activity.getSupportFragmentManager();//FragmentManager调用v4包内的
    }

    /**
     * 是否显示复选框
     *
     * @param showCbox
     */
    public void setIfShowCbox(boolean showCbox) {
        this.showCbox = showCbox;
    }


    /**
     * 点击选项后的处理
     *
     * @param str 显示内容
     */
    private void doAfterClickOption(TextView tv, String str) {
        tv.setVisibility(View.VISIBLE);
        tv.setText(str);
    }


    protected void doAssignValueForView(int position, View resultView, final Test dataObj) {
        //题目
        TextView tvQuestion = (TextView) resultView.findViewById(R.id.tv_content_layout_fg_test);
        tvQuestion.setText(dataObj.getQuestionHtml());

        final LinearLayout llOptions = resultView.findViewById(R.id.ll_options_layout_fg_test01);
//        final LinearLayout llOptionA = resultView.findViewById(R.id.ll_option_a_layout_fg_test01);
//        final CheckBox cboxA = resultView.findViewById(R.id.cbox_option_a_layout_fg_test01);
//        final LinearLayout llOptionB = resultView.findViewById(R.id.ll_option_b_layout_fg_test01);
//        final LinearLayout llOptionC = resultView.findViewById(R.id.ll_option_c_layout_fg_test01);
//        final LinearLayout llOptionD = resultView.findViewById(R.id.ll_option_d_layout_fg_test01);

//        llOptionA.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!cboxA.isChecked()) {
//                    cboxA.setChecked(true);
//                } else {
//                    cboxA.setChecked(false);
//                }
//
//                Toast.makeText(context, "点击了" + dataObj.getContent(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        llOptionB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CheckBox cbox = view.findViewById(R.id.cbox_option_b_layout_fg_test01);
//                if (!cbox.isChecked()) {
//                    cbox.setChecked(true);
//                } else {
//                    cbox.setChecked(false);
//                }
//            }
//        });

        for (int i = 0; i < llOptions.getChildCount(); i++) {
            View vChild = llOptions.getChildAt(i);
            if (vChild instanceof LinearLayout) {
                final LinearLayout llChild = (LinearLayout) vChild;
                llChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cbox = view.findViewWithTag("cbox");
                        if (!cbox.isChecked()) {
                            cbox.setChecked(true);
                        } else {
                            cbox.setChecked(false);
                        }
                    }
                });
            }
        }

        //柱状图
//        HistogramView vGram = (HistogramView) resultView.findViewById(R.id.gramv_layout_fg_test_choice);
//        List<HistogramView.Histogram> gramList = new ArrayList<HistogramView.Histogram>();
//        HistogramView.Histogram gram = new HistogramView.Histogram();
//        gram.setValueName("20");
//        gram.setValue(60);
//        gramList.add(gram);
//        vGram.setData(gramList);
//        vGram.setTextSize(context.getResources().getDimension(R.dimen.dimen_text_008));

        //对错结果
//        ImageView ivResult = (ImageView) resultView.findViewById(R.id.iv_result_layout_fg_test_choice);

        //查看答案解析
//        LinearLayout llSeeAnswerAnalysis = (LinearLayout) resultView.findViewById(R.id.ll_wrapper_see_answer_analysis_layout_fg_test);

        //柱状图
//        View inHistogram = (View) resultView.findViewById(R.id.in_histogram_layout_fg_test);
        //学员名称
//        TextView tvStudentName = (TextView) inHistogram.findViewById(R.id.tv_student_name_layout_v_histogram_answer_analysis);
//        tvStudentName.setVisibility(View.GONE);

        //选项
//        final LinearLayout llOptionA = (LinearLayout) inHistogram.findViewById(R.id.ll_wrapper_a_layout_v_histogram_answer_analysis);
//        llOptionA.setTag(tvStudentName);
//        llOptionA.setOnClickListener(new Listeners());

//        LinearLayout llOptionB = (LinearLayout) inHistogram.findViewById(R.id.ll_wrapper_b_layout_v_histogram_answer_analysis);
//        llOptionB.setTag(tvStudentName);
//        llOptionB.setOnClickListener(new Listeners());

//        LinearLayout llOptionC = (LinearLayout) inHistogram.findViewById(R.id.ll_wrapper_c_layout_v_histogram_answer_analysis);
//        llOptionC.setTag(tvStudentName);
//        llOptionC.setOnClickListener(new Listeners());

//        LinearLayout llOptionD = (LinearLayout) inHistogram.findViewById(R.id.ll_wrapper_d_layout_v_histogram_answer_analysis);
//        llOptionD.setTag(tvStudentName);
//        llOptionD.setOnClickListener(new Listeners());

//        LinearLayout llOptionUnselected = (LinearLayout) inHistogram.findViewById(R.id.ll_wrapper_unselected_layout_v_histogram_answer_analysis);
//        llOptionUnselected.setOnClickListener(new Listeners());
//        llOptionUnselected.setTag(tvStudentName);

        //答案解析图
        // final ImageView iv01Analysis = (ImageView) resultView.findViewById(R.id.iv_01_layout_fg_test_choice);

        //答案解析
//        final TextView tvAnalysis = (TextView) resultView.findViewById(R.id.tv_analysis_layout_fg_test);
//        tvAnalysis.setText(dataObj.getAnalysis());

//        if (type == 0) {//答题状态
//            llSeeAnswerAnalysis.setVisibility(View.GONE);
//            tvAnalysis.setVisibility(View.GONE);
//       //     ivResult.setVisibility(View.GONE);
//            inHistogram.setVisibility(View.GONE);
//        } else if (type == 1) {//答案解析
//            llSeeAnswerAnalysis.setVisibility(View.GONE);
//            tvAnalysis.setVisibility(View.VISIBLE);
//         //   ivResult.setVisibility(View.GONE);
//            inHistogram.setVisibility(View.VISIBLE);
//        } else {//答题情况解析
//            llSeeAnswerAnalysis.setVisibility(View.GONE);
//            tvAnalysis.setVisibility(View.VISIBLE);
//          //  ivResult.setVisibility(View.VISIBLE);
//            inHistogram.setVisibility(View.GONE);


       /* if (manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();//FragmentTransaction调用v4包内的（FragmentTransaction transaction声明成局部的）
            AnswerAnalysisHistogramFg aahFg = new AnswerAnalysisHistogramFg();
            transaction.replace(R.id.rl_wrapper_histogram_layout_fg_test_choice, aahFg).commit();//替换为名称为A的fragment并显示它
        }*/


        //查看解析/收回解析
//    final TextView tvSeeAnalysis = (TextView) resultView.findViewById(R.id.tv_see_analysis_layout_fg_test);
//        tvSeeAnalysis.setOnClickListener(new View.OnClickListener()
//    {
//        @Override
//        public void onClick (View v){
//        if (tvAnalysis.getVisibility() == View.GONE) {
//            tvAnalysis.setVisibility(View.VISIBLE);
//            tvSeeAnalysis.setText("收起解析");
//        } else {
//            tvAnalysis.setVisibility(View.GONE);
//            tvSeeAnalysis.setText("查看解析");
//        }
//    }
//    });

    }

    @Override
    public int getCount() {
        return testList.size();
    }

    @Override
    public Object getItem(int i) {
        return testList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View resultView = LayoutInflater.from(context).inflate(R.layout.layout_fg_test01, null);

        final Test test = testList.get(i);
        if (test != null) {
            //题目
            TextView tvQuestion = (TextView) resultView.findViewById(R.id.tv_content_layout_fg_test);
            tvQuestion.setText(test.getQuestionHtml());

            final LinearLayout llOptions = resultView.findViewById(R.id.ll_options_layout_fg_test01);

            for (int j = 0; j < llOptions.getChildCount(); j++) {
                View vChild = llOptions.getChildAt(j);
                if (vChild instanceof LinearLayout) {
                    LinearLayout llChild = (LinearLayout) vChild;
                    final CheckBox cbox = llChild.findViewWithTag("cbox");
                    cbox.setTag(R.id.tag01, String.valueOf(j));
                    if (test.getUserAnswer().equals(String.valueOf(j))) {
                        cbox.setChecked(true);
                    } else {
                        cbox.setChecked(false);
                    }

                    llChild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!cbox.isChecked()) {
//                                cbox.setChecked(true);
                                test.setUserAnswer(cbox.getTag(R.id.tag01).toString());
                            } else {
//                                cbox.setChecked(false);
                                test.setChoiced(false);
                            }
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }


        return resultView;
    }

    /**
     * 控件监听
     */
    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_wrapper_a_layout_v_histogram_answer_analysis://A选项
                    doAfterClickOption((TextView) v.getTag(), "学员：燕小乙、林冲、陆逊、鲁肃、金角大王、吕小布、周公瑾、李师师");

                    break;
                case R.id.ll_wrapper_b_layout_v_histogram_answer_analysis://B选项
                    doAfterClickOption((TextView) v.getTag(), "学员：武松、关胜、扈三娘、王朗、典韦、荀文若");

                    break;
                case R.id.ll_wrapper_c_layout_v_histogram_answer_analysis://C选项
                    doAfterClickOption((TextView) v.getTag(), "学员：宋公明、关胜");

                    break;
                case R.id.ll_wrapper_d_layout_v_histogram_answer_analysis://D选项
                    doAfterClickOption((TextView) v.getTag(), "学员：曹孟德、袁本初、陈公台、郭奉孝");

                    break;
                case R.id.ll_wrapper_unselected_layout_v_histogram_answer_analysis://未选
                    doAfterClickOption((TextView) v.getTag(), "学员：无");

                    break;
            }

        }
    }
}
