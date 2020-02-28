package com.example.attendclassspad01.fg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.Constants;
import com.example.attendclassspad01.adapter.ErrorBookAdapter;
import com.example.attendclassspad01.callback.InterfacesCallback;
import com.example.attendclassspad01.model.Book;

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

    private List<Book> bookList;//错题本列表

    private InterfacesCallback.ICanKnowSth12 callback12;//回调

    private ErrorBookAdapter ebAdapter;//错题本适配器
    ErrorBookTypeFg typeFg;
    ErrorBookContentFg contentFg;

    private View allFgView;// 总布局
    //    private GridView gdvErrorBook;//错题本
    private RelativeLayout rlContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_error_book, null);

            bookList = new ArrayList<Book>();
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
        bookList = getBookList();
        typeFg = new ErrorBookTypeFg(bookList, callback12);
        showContent(typeFg);
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
    private List<Book> getBookList() {
        List<Book> list = new ArrayList<Book>();
        String name[] = getActivity().getResources().getStringArray(R.array.arrays_high_school_subject);
        if (name.length == Constants.subjectPicResID.length - 1) {
            for (int i = 0; i < Constants.subjectPicResID.length; i++) {
                Book book = new Book();
                book.setID("id" + String.valueOf(i));
                if (i == Constants.subjectPicResID.length - 1) {
                    book.setName("新增错题本");
                } else {
                    book.setName(name[i]);
                }

                book.setSubjectName("");
                book.setResID(Constants.subjectPicResID[i]);

                list.add(book);
            }
        }

        return list;
    }

    @Override
    protected void lazyLoad() {
//        if (!isPrepared || !isVisible || hasLoadOnce) {
//            return;
//        }
    }

    @Override
    public void doSth(Book book) {
        Toast.makeText(getActivity(), "" + book.getName(), Toast.LENGTH_SHORT).show();
        contentFg = new ErrorBookContentFg();
        showContent(contentFg);
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
