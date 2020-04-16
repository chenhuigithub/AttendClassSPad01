package com.example.attendclassspad01.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.ValidateFormatUtils;
import com.example.attendclassspad01.model.Classes;

import java.util.List;

/**
 * 班级列表适配器
 */
public class Classes01Adapter extends BaseListAdapter<Classes> {
    public Classes01Adapter(Context context, List<Classes> dataList) {
        super(context, dataList);
    }


    @Override
    protected int getLayoutResID() {
        return R.layout.layout_v_classes01;
    }

    @Override
    protected void doAssignValueForView(int position, View resultView, Classes dataObj) {
        RelativeLayout rlWrapperChoiced = (RelativeLayout) resultView.findViewById(R.id.iv_layout_v_classes01);
        rlWrapperChoiced.setSelected(false);
        ImageView ivStart = resultView.findViewById(R.id.iv_start_layout_v_classes01);
        ivStart.setVisibility(View.INVISIBLE);
        TextView tvClassName = resultView.findViewById(R.id.tv_class_name_layout_v_classes01);
        TextView tvMsg01 = resultView.findViewById(R.id.tv_msg01_layout_v_classes01);
        tvMsg01.setVisibility(View.INVISIBLE);

//        if (position == 0) {//假数据，第一条数据显示直播状态
//            ivStart.setVisibility(View.VISIBLE);
//            tvMsg01.setVisibility(View.VISIBLE);
//        }

        if (!ValidateFormatUtils.isEmpty(dataObj.getName())) {
            tvClassName.setText(dataObj.getName());
        }

        if (dataObj.isHasChoiced()) {
            rlWrapperChoiced.setSelected(true);
        } else {
            rlWrapperChoiced.setSelected(false);
        }
    }
}
