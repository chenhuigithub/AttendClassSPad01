package com.example.attendclassspad01.fg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.Constants;
import com.example.attendclassspad01.adapter.ErrorBookAdapter;
import com.example.attendclassspad01.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * 错题本
 * @author chenhui_2020.02.27
 */
public class ErrorBookFg extends BaseNotPreLoadFg {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了

    private List<Book> bookList;//错题本列表

    private ErrorBookAdapter ebAdapter;//错题本适配器

    private View allFgView;// 总布局
    private GridView gdvErrorBook;//错题本

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_error_book, null);

            bookList = new ArrayList<Book>();

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
        gdvErrorBook = (GridView) allFgView.findViewById(R.id.gdv_book_layout_fg_error_book);
        bookList =getBookList();
        setBookAdapter();
        gdvErrorBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {//错题本单项点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    /**
     * 设置错题本适配器
     */
    private void setBookAdapter() {
        if (ebAdapter == null) {
            ebAdapter = new ErrorBookAdapter(getActivity(), bookList);
            gdvErrorBook.setAdapter(ebAdapter);
        } else {
            ebAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取错题本假数据
     * @return
     */
    private List<Book> getBookList() {
        for (int i = 0; i < Constants.subjectPicResID.length; i++) {
            Book book = new Book();
            book.setID("id" + String.valueOf(i));
            book.setName("name" + String.valueOf(i));
            book.setSubjectName("");
            book.setResID(Constants.subjectPicResID[i]);

            bookList.add(book);
        }
        return bookList;
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

        }
    }
}
