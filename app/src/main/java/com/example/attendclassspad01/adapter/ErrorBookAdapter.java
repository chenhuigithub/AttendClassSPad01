package com.example.attendclassspad01.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.model.Book;

import java.util.List;

/**
 * 错题本适配器
 *
 * @author chenhui_2020.02.27
 */
public class ErrorBookAdapter extends BaseListAdapter<Book> {
    Context context;

    public ErrorBookAdapter(Context context, List<Book> dataList) {
        super(context, dataList);
        this.context = context;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.layout_v_book;
    }

    @Override
    protected void doAssignValueForView(int position, View resultView, Book dataObj) {
        ImageView iv = resultView.findViewById(R.id.iv_layout_v_book);
        iv.setBackgroundResource(dataObj.getResID());

    }
}
