package com.example.attendclassspad01.aty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.ConstantsForPreferencesUtils;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.Util.PreferencesUtils;
import com.example.attendclassspad01.Util.ServerRequestUtils;
import com.example.attendclassspad01.Util.ViewUtils;
import com.example.attendclassspad01.adapter.Classes01Adapter;
import com.example.attendclassspad01.model.Classes;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择模块界面
 */
public class ChooseModuleAty extends Activity {
    private List<Classes> classList;// 班级列表
    private String classesIDCurr = "";//当前选中的班级ID

    private Classes01Adapter cAdapter;//班级列表适配器
    private ServerRequestUtils requestUtils;// 网络请求
    private Handler uiHandler;// 主线程handler
    private ViewUtils vUtils;// 布局工具

    private LinearLayout rlWrapper01;//进入班级外框
    private RelativeLayout rlIntoClass;//进入班级
    private LinearLayout rlWrapper02;//进入错题本外框
    private ImageView ivNoClassData;//没有班级数据

    private ListView lvClassList;//班级列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_aty_choose_module);

        classList = new ArrayList<Classes>();
        // 初始化服务器请求操作
        requestUtils = new ServerRequestUtils(this);
        vUtils = new ViewUtils(this);
        uiHandler = new Handler(getMainLooper());

        initView();

        requestClassFromServer();
    }

    private void initView() {
        //进入班级
        rlWrapper01 = (LinearLayout) findViewById(R.id.ll_wrapper_01_layout_aty_choose_module);
        rlIntoClass = (RelativeLayout) findViewById(R.id.rl_wrapper_into_class_layout_aty_choose_module);
        rlIntoClass.setOnClickListener(new Listeners());

        TextView tvMsg05 = (TextView) findViewById(R.id.tv_msg05_layout_aty_choose_module);
        //str代表要显示的全部字符串
        SpannableStringBuilder style = new SpannableStringBuilder("进入课堂 开始上课");
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#027DD3")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMsg05.setText(style);

        //进入错题本
        rlWrapper02 = (LinearLayout) findViewById(R.id.ll_wrapper_02_layout_aty_choose_module);
        RelativeLayout rlIntoErrorBook = (RelativeLayout) findViewById(R.id.rl_wrapper_into_error_book_layout_aty_choose_module);
        rlIntoErrorBook.setOnClickListener(new Listeners());

        TextView tvMsg06 = (TextView) findViewById(R.id.tv_msg06_layout_aty_choose_module);
        //str代表要显示的全部字符串
        SpannableStringBuilder style1 = new SpannableStringBuilder("错题本 为解决问题提供方向");
        style1.setSpan(new ForegroundColorSpan(Color.parseColor("#FE710A")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvMsg06.setText(style1);

        ivNoClassData = (ImageView) findViewById(R.id.iv_no_data_layout_aty_module);
        ivNoClassData.setVisibility(View.GONE);
        ivNoClassData.setOnClickListener(new Listeners());

        //班级列表
        lvClassList = (ListView) findViewById(R.id.lv_class_list_layout_aty_choose_module);
//        classList = getClassList();
//        setLvClassListAdapter();
        setLvClassListListeners();
    }

    private List<Classes> getClassList() {
        List<Classes> list = new ArrayList<Classes>();
        for (int i = 0; i < 3; i++) {
            Classes classes = new Classes();
            classes.setID("id" + String.valueOf(i));
            classes.setName("九年级(" + String.valueOf(i + 1) + ")班");

            list.add(classes);
        }
        return list;
    }


    private void requestClassFromServer() {
        requestUtils.request("getClassList", "", "加载班级数据中", ServerRequestUtils.REQUEST_SHORT_TIME, new ServerRequestUtils.OnServerRequestListener2() {
            @Override
            public void onFailure(final String msg) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(msg)) {
                            Toast.makeText(ChooseModuleAty.this, msg + "加载失败，请点击图片重试",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChooseModuleAty.this, "获取班级列表失败，请点击图片重试",
                                    Toast.LENGTH_SHORT).show();
                        }

                        ivNoClassData.setVisibility(View.VISIBLE);
                        lvClassList.setVisibility(View.GONE);

                        vUtils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(String msg, JSONArray data, String count) {
                //暂无数据，用假数据代替，2019.11.20
//                data = new JSONArray();
//                try {
//                    JSONObject obj1 = new JSONObject();
//                    obj1.put("DataID", 1);
//                    obj1.put("DataName", "高一(1)班");
//
//                    JSONObject obj2 = new JSONObject();
//                    obj2.put("DataID", 2);
//                    obj2.put("DataName", "高一(2)班");
//
//                    data.put(obj1);
//                    data.put(obj2);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                if (data != null) {
                    List<Classes> list = com.alibaba.fastjson.JSON.parseArray(data.toString(), Classes.class);
                    if (list != null) {
                        if (list.size() == 0) {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ivNoClassData.setVisibility(View.VISIBLE);
                                    lvClassList.setVisibility(View.GONE);

                                    Toast.makeText(ChooseModuleAty.this, "暂无可选择的班级，可返回切换账号试试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            if (classList.size() > 0) {
                                classList.clear();
                            }
                            classList.addAll(list);
                        }

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ivNoClassData.setVisibility(View.GONE);
                                lvClassList.setVisibility(View.VISIBLE);

                                vUtils.dismissDialog();

                                setLvClassListAdapter();
                            }
                        });
                    }


                    //把班级jsonArray存入首选项文件
                    PreferencesUtils.saveInfoToPreferences(ChooseModuleAty.this, ConstantsForPreferencesUtils.CLASS_LIST_JSONARR, data.toString());
                } else {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ivNoClassData.setVisibility(View.VISIBLE);
                            lvClassList.setVisibility(View.GONE);

                            vUtils.dismissDialog();
                        }
                    });
                }

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        vUtils.dismissDialog();
                    }
                });
            }
        });
    }

    /**
     * 设置错题本适配器
     */
    private void setLvClassListAdapter() {
        if (cAdapter == null) {
            cAdapter = new Classes01Adapter(this, classList);
            lvClassList.setAdapter(cAdapter);
        } else {
            cAdapter.notifyDataSetChanged();
        }
    }

    private void setLvClassListListeners() {
        lvClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Classes classes = classList.get(position);
                if (classes != null) {
                    classesIDCurr = classes.getID();
                    cAdapter.setChoicedID(classesIDCurr);
                    classes.setHasChoiced(true);
                    PreferencesUtils.saveInfoToPreferences(ChooseModuleAty.this, ConstantsForPreferencesUtils.CLASS_ID, classes.getID());
                    cAdapter.notifyDataSetChanged();
                }

                rlIntoClass.performClick();
                //不抢焦点
                lvClassList.setSelected(false);

                Intent intentAction = new Intent();
//                intentAction.setAction(ConstantsUtils.REFRESH_INFO);
                intentAction.setAction(ConstantsUtils.REFRESH_CLASS_INFO);


                if (classList.size() > 0 && classList.get(position) != null) {
                    intentAction.putExtra(ConstantsUtils.CLASS_NAME, classList.get(position).getName());
                    PreferencesUtils.saveInfoToPreferences(ChooseModuleAty.this, ConstantsForPreferencesUtils.CLASS_NAME_CHOICED, classList.get(position).getName());
                }

                if (classList.size() > 0 && classList.get(position) != null) {
                    intentAction.putExtra(ConstantsUtils.CLASS_ID, classList.get(position).getID());
                    PreferencesUtils.saveInfoToPreferences(ChooseModuleAty.this, ConstantsForPreferencesUtils.CLASS_ID_CHOICED, classList.get(position).getID());
                }


                intentAction.putExtra(ConstantsUtils.HAS_LOGINED, true);
//                intentAction.putExtra(ConstantsUtils.INTENT, ConstantsUtils.INTENT01);

                // 发送广播
                LocalBroadcastManager.getInstance(ChooseModuleAty.this)
                        .sendBroadcast(intentAction);

                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intentAction = new Intent();
            intentAction.setAction(ConstantsUtils.REFRESH_USER_INFO);// 刷新用户信息
            intentAction.putExtra(ConstantsUtils.HAS_LOGINED, false);
            LocalBroadcastManager.getInstance(ChooseModuleAty.this).sendBroadcast(intentAction);


            Intent intent = new Intent(ChooseModuleAty.this, LoginActivity.class);
            startActivity(intent);

            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    intent.putExtra(ConstantsUtils.NEED_REFRESH_CATALOG, true);
                    startActivity(intent);

                    finish();

                    break;

                case R.id.iv_no_data_layout_aty_module://刷新班级列表
                    if (classList != null && classList.size() > 0) {
                        classList.clear();
                    }

                    vUtils.showLoadingDialog("");
                    requestClassFromServer();

                    break;
            }
        }
    }
}
