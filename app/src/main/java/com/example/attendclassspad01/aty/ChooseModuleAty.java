package com.example.attendclassspad01.aty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.adapter.Classes01Adapter;
import com.example.attendclassspad01.model.Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择模块界面
 */
public class ChooseModuleAty extends Activity {
    private List<Classes> classesList;//班级列表

    private Classes01Adapter cAdapter;//班级列表适配器

    private LinearLayout rlWrapper01;//进入班级外框
    private RelativeLayout rlIntoClass;//进入班级
    private LinearLayout rlWrapper02;//进入错题本外框

    private ListView lvClassesList;//班级列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_aty_choose_module);

        classesList = new ArrayList<Classes>();

        initView();
    }

    private void initView() {
        //进入班级
        rlWrapper01 = (LinearLayout) findViewById(R.id.ll_wrapper_01_layout_aty_choose_module);
        rlIntoClass = (RelativeLayout) findViewById(R.id.rl_wrapper_into_class_layout_aty_choose_module);
        rlIntoClass.setOnClickListener(new Listeners());

        //进入错题本
        rlWrapper02 = (LinearLayout) findViewById(R.id.ll_wrapper_02_layout_aty_choose_module);
        RelativeLayout rlIntoErrorBook = (RelativeLayout) findViewById(R.id.rl_wrapper_into_error_book_layout_aty_choose_module);
        rlIntoErrorBook.setOnClickListener(new Listeners());

        lvClassesList = (ListView) findViewById(R.id.lv_classes_list_layout_aty_choose_module);
        classesList = getClassesList();
        setLvClassesListAdapter();
        setLvClassesListListeners();
    }

    private List<Classes> getClassesList() {
        List<Classes> list = new ArrayList<Classes>();
        for (int i = 0; i < 3; i++) {
            Classes classes = new Classes();
            classes.setID("id" + String.valueOf(i));
            classes.setName("九年级(" + String.valueOf(i + 1) + ")班");

            list.add(classes);
        }
        return list;
    }

    /**
     * 设置错题本适配器
     */
    private void setLvClassesListAdapter() {
        if (cAdapter == null) {
            cAdapter = new Classes01Adapter(this, classesList);
            lvClassesList.setAdapter(cAdapter);
        } else {
            cAdapter.notifyDataSetChanged();
        }
    }

    private void setLvClassesListListeners() {
        lvClassesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView ivStart = view.findViewById(R.id.iv_start_layout_v_classes01);
                ivStart.setVisibility(View.VISIBLE);
                TextView tvMsg01 = view.findViewById(R.id.tv_msg01_layout_v_classes01);
                tvMsg01.setVisibility(View.VISIBLE);

                rlIntoClass.performClick();
                lvClassesList.setSelected(false);

                RelativeLayout rlWrapperChoiced = (RelativeLayout) view.findViewById(R.id.iv_layout_v_classes01);
                rlWrapperChoiced.setSelected(true);

                Intent intent = new Intent(ChooseModuleAty.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }


    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.rl_wrapper_into_class_layout_aty_choose_module://进入班级
                    rlWrapper01.setSelected(true);
                    rlWrapper02.setSelected(false);

                    break;
                case R.id.rl_wrapper_into_error_book_layout_aty_choose_module://进入错题本
                    rlWrapper02.setSelected(true);
                    rlWrapper01.setSelected(false);

                    Intent intent = new Intent(ChooseModuleAty.this, MainActivity.class);
                    startActivity(intent);

                    finish();

                    break;
            }
        }
    }
}
