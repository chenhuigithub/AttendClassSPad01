package com.example.attendclassspad01.adapter;

import android.content.Context;
import android.view.View;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.model.Classes;

import java.util.List;

/**
 * 班级列表适配器
 */
public class Classes01Adapter extends  BaseListAdapter<Classes>{
    public Classes01Adapter(Context context, List<Classes> dataList) {
        super(context, dataList);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.layout_v_classes01;
    }

    @Override
    protected void doAssignValueForView(int position, View resultView, Classes dataObj) {

    }
}
