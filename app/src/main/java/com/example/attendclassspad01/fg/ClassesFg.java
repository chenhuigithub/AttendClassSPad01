package com.example.attendclassspad01.fg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.ConstantsForPreferencesUtils;
import com.example.attendclassspad01.Util.ConstantsForServerUtils;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.Util.PreferencesUtils;
import com.example.attendclassspad01.Util.ServerDataAnalyzeUtils;
import com.example.attendclassspad01.Util.ServerRequestUtils;
import com.example.attendclassspad01.Util.UrlUtils;
import com.example.attendclassspad01.Util.ValidateFormatUtils;
import com.example.attendclassspad01.Util.ViewUtils;
import com.example.attendclassspad01.adapter.CustomPagerAdapter03;
import com.example.attendclassspad01.adapter.CustomPagerAdapter04;
import com.example.attendclassspad01.adapter.FilesListAdapter;
import com.example.attendclassspad01.adapter.GalleryAdapter;
import com.example.attendclassspad01.aty.ChoiceCatalogAty;
import com.example.attendclassspad01.aty.ChooseModuleAty;
import com.example.attendclassspad01.callback.InterfacesCallback;
import com.example.attendclassspad01.callback.OnListenerForPlayVideoCallback;
import com.example.attendclassspad01.model.Bean;
import com.example.attendclassspad01.model.Courseware;
import com.example.attendclassspad01.model.DataID01;
import com.example.attendclassspad01.model.DataInfo;
import com.example.attendclassspad01.model.File01;
import com.example.attendclassspad01.model.Picture;
import com.example.attendclassspad01.model.TestData;
import com.example.attendclassspad01.model.VideoAudio;
import com.example.pullrefreshlistviewlibrary.util.PullDownView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 我的课堂
 *
 * @author chenhui_2020.02.27
 */
@SuppressWarnings("deprecation")
public class ClassesFg extends BaseNotPreLoadFg implements InterfacesCallback.ICanDoSth {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了
    private String catalogIDCurr = "";// 目录ID
    private String catalogNameCurr = "";// 目录名称
    private boolean isPageSelected = false;// 是否允许走viewPager滑动监听中的onPageSelected方法
    private boolean isScroll = false;// 是否正在滑动，默认未滑动
    private int prePosition;// 幻灯片展示的上一个位置
    private int currentPageNumForFile = 1;// 授课列表当前页页码，默认是首页
    private int currentPageNumForPreview = 1;// 预览授课当前页页码，默认是首页

    private String classID = "";//班级ID
    private List<File01> fileList;//授课文件
    private ArrayList<String> idFileChoicedList;//被选中的资源文件ID集合
    private ArrayList<String> idList;//ID集合
    private List<DataInfo> planList;// 教案学案列表（来源：配套资源）
    private List<Courseware> coursewareList;// 右侧课件列表（来源：组课件）
    private List<TestData> testList;// 试题列表（来源：组试题）
    private List<TestData> omicsList;// 学案列表（来源：组学案）
    private ArrayList<String> txtList;//txt文档类数据
    private List<VideoAudio> videoList;// 音视频列表
    private String rightInfoCount;//  预览授课资源的总个数
    private boolean needMultiPageRequestPreview = true;//授课预览是否需要多页请求,默认需要
    private InterfacesCallback.ICanKnowSth6 picCallback;//图片回调
    private String className = "";//班级名称
    private String catalogName = "";//目录名称

    private InterfacesCallback.ICanDoSth callback;//回调
    private OnListenerForPlayVideoCallback callbackForVideo;// 音视频播放器回调
    private CustomPagerAdapter04 wordVPagerAdapter;// 学案教案滑动布局适配器
    private CustomPagerAdapter03 picVpagerAdapter;// 课件大图滑动布局适配器
    private PlayVideoFragment videoFg;// 音视频
    //    PullDown vPullDown;
    private PullDownView vPullDown;//授课列表下拉刷新、上拉加载更多
    private GalleryAdapter glAdapter;
    private ServerRequestUtils sUtils;// 服务器请求工具
    private ViewUtils vUtils;// 布局工具
    private Handler uiHandler;// ui主线程
    private FilesListAdapter filesAdapter;// 文件目录适配器
    private File01 fileFocus;//正在预览的授课资源
    private VideoAudio videoCurr;//正在观看的视频
    private int newPosition;// viewPager当前位置
    private LocalBroadcastManager broadcastManager;// 广播接收
    private BroadcastReceiver receiver;// 广播

    private ViewPager vpager;// 滑动布局
    //    private PullRefreshListView prlstvFiles;//授课列表(刷新、加载的另一种实现形式)
    private ListView lstvFiles;//授课列表
    private LinearLayout llNoFile;//没有授课文件
    private LinearLayout llWrapper01;//ppt、viewpager区域
    private LinearLayout llPreviewContent;//预览Html区域
    private RelativeLayout rlVideo;// 音视频
    private ImageView ivNoPreviewContent;// 无预览授课内容情况下的展示图片
    private View allFgView;// 总布局
    private ImageView iv;//测试图
    private RelativeLayout rlFile;// 画廊布局：文件（PPT）缩略图展示布局
    private Gallery glFile;// 画廊效果：文件（PPT）缩略图展示布局


    // 上一个（小图）
    private TextView tvPrevious;
    // 下一个（小图）
    private TextView tvNext;
    // 班级名称
    private TextView tvClassName;
    //目录名称
    private TextView tvCatalogName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_classes, null);

            vUtils = new ViewUtils(getActivity());
            sUtils = new ServerRequestUtils(getActivity());
            uiHandler = new Handler(getActivity().getMainLooper());
            fileList = new ArrayList<File01>();
            idFileChoicedList = new ArrayList<String>();
            idList = new ArrayList<String>();
            planList = new ArrayList<DataInfo>();
            coursewareList = new ArrayList<Courseware>();
            testList = new ArrayList<TestData>();
            omicsList = new ArrayList<TestData>();
            txtList = new ArrayList<String>();
            videoList = new ArrayList<VideoAudio>();
            fileFocus = new File01();

            initView(allFgView);

            initBroadcastReceiver();

            String catalogIDCurr = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CATALOG_ID_CHOICED);
            if (!ValidateFormatUtils.isEmpty(catalogIDCurr)) {
                this.catalogIDCurr = catalogIDCurr;
            }

            String catalogNameCurr = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CATALOG_NAME_CHOICED);
            if (!ValidateFormatUtils.isEmpty(catalogNameCurr)) {
                this.catalogNameCurr = catalogNameCurr;
            }

            String clName = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CLASS_NAME_CHOICED);
            if (!ValidateFormatUtils.isEmpty(clName)) {
                className = clName;
                tvClassName.setText(className);
            }

            String caName = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CATALOG_NAME_CHOICED);
            if (!ValidateFormatUtils.isEmpty(caName)) {
                catalogName = caName;
                tvCatalogName.setText(catalogName);
            }

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

    private void initView(View v) {
        //切换班级
        RelativeLayout rlSwitchClass = (RelativeLayout) v.findViewById(R.id.rl_wrapper_switch_class_layout_fg_classes);
        rlSwitchClass.setOnClickListener(new Listeners());

        //切换目录
        RelativeLayout rlSwitchCatalog = (RelativeLayout) v.findViewById(R.id.rl_wrapper_switch_catalog_layout_fg_classes);
        rlSwitchCatalog.setOnClickListener(new Listeners());

        // 班级名称
        tvClassName = (TextView) allFgView
                .findViewById(R.id.tv_class_name_layout_fg_classes);
//        tvClassName.setOnClickListener(new Listeners());

        // 目录名称
        tvCatalogName = (TextView) allFgView
                .findViewById(R.id.tv_catalog_name_layout_fg_classes);

        llWrapper01 = (LinearLayout) allFgView
                .findViewById(R.id.ll_wrapper01_layout_fg_attend_class_detail);

        llPreviewContent = (LinearLayout) allFgView
                .findViewById(R.id.ll_preview_content_layout_fg_attend_class_detail);

        //没有授课文件
        llNoFile = (LinearLayout) allFgView
                .findViewById(R.id.ll_no_file_layout_fg_attend_class_detail);
        //没有授课预览内容
        ivNoPreviewContent = (ImageView) allFgView
                .findViewById(R.id.iv_no_preview_content_layout_fg_attend_class_detail);
        ivNoPreviewContent.setVisibility(View.GONE);

        //本地上传(授课文件)
        TextView tvFileLoad = (TextView) allFgView
                .findViewById(R.id.tv_file_upload_layout_fg_attend_class_detail);
        tvFileLoad.setVisibility(View.GONE);
        tvFileLoad.setOnClickListener(new Listeners());

        initGallery(allFgView);

        vPullDown = (PullDownView) allFgView
                .findViewById(R.id.v_files_catalog_layout_fg_attend_class_detail);
        initPullDownLstv(vPullDown);
        lstvFiles = vPullDown.getListView();

        initPagerViews();

        // 音视频
        rlVideo = (RelativeLayout) allFgView.findViewById(R.id.rl_video_content_layout_fg_classes);
        rlVideo.setVisibility(View.GONE);
    }


    /**
     * 初始化viewPager相关布局
     */
    private void initPagerViews() {
        // 教案内容，滑动布局
        vpager = (ViewPager) allFgView
                .findViewById(R.id.vpager_content_layout_fg_classes);
        // 限制加载数量
        vpager.setOffscreenPageLimit(2);
        // 监听
        vpager.setOnPageChangeListener(new VPagerChangeListener());

//        setVPagerAdapter(0);
    }

    /**
     * 监听
     */
    private void initBroadcastReceiver() {
        // 注册广播接收
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter filter = new IntentFilter();
//        filter.addAction(ConstantsUtils.ACQUIRE_MATERIAL_INFO);// 获取教材信息
        filter.addAction(ConstantsUtils.REFRESH_INFO);//刷新信息
        filter.addAction(ConstantsUtils.REFRESH_CLASS_INFO);//刷新班级信息

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Bundle bundle = intent.getExtras();

                if (ConstantsUtils.REFRESH_CLASS_INFO.equals(action)) {//刷新信息
                    if (bundle == null) {
                        return;
                    }


                    String clName = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CLASS_NAME_CHOICED);
                    if (!ValidateFormatUtils.isEmpty(clName)) {
                        className = clName;
                        tvClassName.setText(className);
                    }

                } else if (ConstantsUtils.REFRESH_CATALOG_INFO.equals(action)) {//刷新班级
                    if (bundle == null) {
                        return;
                    }


                    String caName = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CATALOG_NAME_CHOICED);
                    if (!ValidateFormatUtils.isEmpty(caName)) {
                        catalogName = caName;
                        tvCatalogName.setText(catalogName);
                    }
                }
            }
        };
        broadcastManager.registerReceiver(receiver, filter);
    }

    /**
     * 设置音视频播放器
     */
    private void initVideo(VideoAudio videoCurr) {
        // 将视频fragment填充到本activity中
        FragmentManager fgManager = getActivity().getSupportFragmentManager();// v4包内提供的方法
        FragmentTransaction fgTransaction = fgManager.beginTransaction();


        if (videoFg != null) {
            fgTransaction.remove(videoFg);
        }

        if (videoCurr != null) {
            videoFg = new PlayVideoFragment(videoCurr.getPath(),
                    videoCurr.getTitle(), videoList, false, "0");
            callbackForVideo = (OnListenerForPlayVideoCallback) videoFg;
        }

        fgTransaction.add(R.id.rl_video_content_layout_fg_classes, videoFg);
        fgTransaction.commit();
    }

    /**
     * 以画廊效果展示文件内容（PPT）
     *
     * @param v
     */
    private void initGallery(View v) {
        rlFile = (RelativeLayout) v
                .findViewById(R.id.rl_wrapper01_layout_fg_attend_class_detail);

        // 实例化控件
        glFile = (Gallery) v
                .findViewById(R.id.gl_file_layout_fg_attend_class_detail);

//        fileContents = new ArrayList<FileContent>();
//        FileContent fileContent01 = new FileContent();
//        fileContent01.setPageNumber("1");
//        fileContent01.setIvRes(ivsRes[0]);
//
//        FileContent fileContent02 = new FileContent();
//        fileContent02.setPageNumber("2");
//        fileContent02.setIvRes(ivsRes[1]);
//
//        FileContent fileContent03 = new FileContent();
//        fileContent03.setPageNumber("3");
//        fileContent03.setIvRes(ivsRes[2]);
//
//        FileContent fileContent04 = new FileContent();
//        fileContent04.setPageNumber("4");
//        fileContent04.setIvRes(ivsRes[3]);
//
//        FileContent fileContent05 = new FileContent();
//        fileContent05.setPageNumber("5");
//        fileContent05.setIvRes(ivsRes[4]);
//
//        FileContent fileContent06 = new FileContent();
//        fileContent06.setPageNumber("6");
//        fileContent06.setIvRes(ivsRes[5]);
//
//        fileContents.add(fileContent01);
//        fileContents.add(fileContent02);
//        fileContents.add(fileContent03);
//        fileContents.add(fileContent04);
//        fileContents.add(fileContent05);
//        fileContents.add(fileContent06);

        setGridViewAdapter(0);

        // 设置左边距为负数，达到默认居左的显示效果
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) glFile
                .getLayoutParams();
        WindowManager wm = (WindowManager) getActivity().getSystemService(
                Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        layoutParams.setMargins(-width * 6 / 13, 0, 0, 0);

        // 给Gallery添加监听
        glFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long arg3) {

                // 重新赋值
                prePosition = position;

                setVPagerAdapter(position);
            }
        });

        // 上一个
        tvPrevious = (TextView) allFgView
                .findViewById(R.id.ll_previous_layout_fg_attend_class_detail);
        tvPrevious.setOnClickListener(new Listeners());

        // 下一个
        tvNext = (TextView) allFgView
                .findViewById(R.id.ll_next_layout_fg_attend_class_detail);
        tvNext.setOnClickListener(new Listeners());
    }


    /**
     * 初始化上拉刷新下拉加载
     *
     * @param vPullDown
     */
    private void initPullDownLstv(PullDownView vPullDown) {
        vPullDown.setOnPullDownListener(new PullDownView.OnPullDownListener() {
            @Override
            public void onRefresh() {
                // 重置页码
                currentPageNumForFile = 1;
                // 请求数据
                requestFileListFromServer(catalogIDCurr);
            }

            @Override
            public void onMore() {
                currentPageNumForFile = currentPageNumForFile + 1;
                requestFileListFromServer(catalogIDCurr);
            }
        });
    }

    /**
     * 从服务器获取授课文件列表
     */
    private void requestFileListFromServer(String catalogIDCurr) {
        DataID01 dataID01 = new DataID01();


        dataID01.setChid(catalogIDCurr);//章节ID

        classID = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CLASS_ID_CHOICED);
        dataID01.setCid(classID);//班级ID

        Bean bean = new Bean();
        bean.setData(dataID01);
        bean.setIndex(String.valueOf(currentPageNumForFile));
        bean.setSize(ConstantsForServerUtils.PAGE_SIZE_DEFAULT_VALUE);

        Gson gson = new Gson();
        String json = gson.toJson(bean);

        sUtils.request("getLectureList", json, "", ServerRequestUtils.REQUEST_LONG_TIME, new ServerRequestUtils.OnServerRequestListener2() {
            @Override
            public void onFailure(final String msg) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg1 = "文件获取失败，请重试";
                        if (!TextUtils.isEmpty(msg)) {
                            msg1 = msg;
                        }
                        showNoFileData(msg1);

                        vUtils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(String msg, JSONArray data, final String count) {
                // 重置数据
                if (currentPageNumForFile == 1 && fileList.size() > 0) {
                    fileList.clear();
                }

                List<File01> list = com.alibaba.fastjson.JSON.parseArray(data.toString(), File01.class);

                //假数据
                //List<File01> list = getFileData();

                if (list != null) {
                    if (list.size() == 0) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String msg = "没有更多数据啦";
                                showNoFileData(msg);
                            }
                        });
                    } else {
                        fileList.addAll(list);

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                llNoFile.setVisibility(View.GONE);
                                lstvFiles.setVisibility(View.VISIBLE);
                                vPullDown.setVisibility(View.VISIBLE);

                                setLstvFileAdapter(false);
//                                vPullDown.enableAutoFetchMore(true, 1);

                                vPullDown.notifyDidLoad();

                                if (currentPageNumForFile == 1) {
                                    lstvFiles.setSelection(0);

                                    // 隐藏刷新模块
                                    vPullDown.notifyDidRefresh();

//                                prlstvFiles.refreshComplete();
                                } else {// 非首页数据，焦点定到文末
                                    final int lastVisiblePostion = lstvFiles
                                            .getLastVisiblePosition();
                                    lstvFiles.setSelection(lastVisiblePostion);

                                    // 隐藏加载更多模块
                                    vPullDown.notifyDidMore();
//                                prlstvFiles.loadMoreComplete();
                                }
                            }
                        });

                        if (currentPageNumForFile == 1 && fileList.size() > 0) {
                            fileFocus = fileList.get(0);
                            if (fileFocus != null) {
                                requestFileDetailFromServer(fileFocus.getFileType(), fileFocus.getDataID());
                            }
                        }
                    }

                    hasLoadOnce = true;
                } else {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String msg = "没有更多数据啦";
                            showNoFileData(msg);
                        }
                    });
                }
            }
        });
    }

    /**
     * 从服务器获取文件
     */
    private void requestFileDetailFromServer(String fileType, String ID) {
        DataID01 data = new DataID01();
        data.setDataid(ID);
        data.setIndex(String.valueOf(currentPageNumForPreview));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(data);

        if (ConstantsForServerUtils.WORD.equals(fileType)) {// 文字格式（学案、教案）
            requestFileDetailJsonArrFromServer("getLectureView", jsonStr, ID);
        } else if (ConstantsForServerUtils.HTML.equals(fileType)) {//Html
            requestFileDetailJsonArrFromServer("getLectureInfo", jsonStr, ID);

        } else if (ConstantsForServerUtils.PPT.equals(fileType)) {//组课件ppt格式
            requestFileDetailJsonArrFromServer("getLectureInfo", jsonStr, ID);
        } else if (ConstantsForServerUtils.TXT.equals(fileType) || ConstantsForServerUtils.IMAGES.equals(fileType) || ConstantsForServerUtils.VIDEO.equals(fileType)) {//图片
            requestFileDetailJsonObjFromServer("getLectureInfo", jsonStr, ID);
        } else if (ConstantsForServerUtils.SWF.equals(fileType)) {// flash动画类,只有swf格式
        } else {//其它未知格式
            requestFileDetailJsonArrFromServer("getLectureView", jsonStr, ID);
        }
    }

    private void requestFileDetailJsonArrFromServer(String methodName, String jsonStr, String id) {
        sUtils.request(methodName, jsonStr, "", ServerRequestUtils.REQUEST_SHORT_TIME, new ServerRequestUtils.OnServerRequestListener2() {
            @Override
            public void onFailure(final String msg) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg1 = "预览失败，请重试";
                        if (!TextUtils.isEmpty(msg)) {
                            msg1 = msg;
                        }

                        showNoPreviewContent(msg1);
                    }
                });
            }


            @Override
            public void onResponse(String msg, JSONArray data, String count) {
                rightInfoCount = count;

                dealWithPreviewData(data);
            }
        });
    }

    private void requestFileDetailJsonObjFromServer(String methodName, String jsonStr, String id) {
        sUtils.request(methodName, jsonStr, "", ServerRequestUtils.REQUEST_SHORT_TIME, new ServerRequestUtils.OnServerRequestListener() {
            @Override
            public void onFailure(final String msg) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg1 = "";
                        if (!TextUtils.isEmpty(msg)) {
                            msg1 = msg;
                        }
                        showNoPreviewContent(msg1);

                        vUtils.dismissDialog();
                    }
                });
            }

            @Override
            public void onResponse(String msg, JSONObject data, String count) {
                dealWithPreviewDataInfoData(data);
            }
        });
    }

    private void dealWithPreviewDataInfoData(JSONObject data) {
        JSONArray dataInfoArr = ServerDataAnalyzeUtils.getDataAsJSONArray(data, ConstantsForServerUtils.DATAINFO);
        dealWithPreviewData(dataInfoArr);
    }

    private void dealWithPreviewData(JSONArray dataArr) {
        if (dataArr != null) {
//        if (true) {
//            fileType = ConstantsForServerUtils.VIDEO;

            if (dataArr.length() == 0) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "暂无预览，切换别的文件试试";
                        showNoPreviewContent(msg);
                    }
                });
            } else {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ivNoPreviewContent.setVisibility(View.GONE);
                    }
                });

                if (ConstantsForServerUtils.PPT.equals(fileFocus.getFileType())) {// 课件ppt格式
                    coursewareList = com.alibaba.fastjson.JSON.parseArray(dataArr.toString(), Courseware.class);
                    rightInfoCount = String.valueOf(coursewareList.size());
                    needMultiPageRequestPreview = false;

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resetAllPreviewContentWidgets();

                            previewPPT();
                            setGridViewAdapter(newPosition);

                            setVPagerAdapter(0);

                            vUtils.dismissDialog();
                        }
                    });
                } else if (ConstantsForServerUtils.HTML.equals(fileFocus.getFileType())) {// Html格式
                    if (getActivity().getResources().getString(R.string.group_test).equals(fileFocus.getSource())) {//试题
                        resetTestList();
                        testList = com.alibaba.fastjson.JSON.parseArray(dataArr.toString(), TestData.class);
                        rightInfoCount = String.valueOf(testList.size());
                        needMultiPageRequestPreview = false;

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                previewHtml();
                                PreviewTestFg fg = new PreviewTestFg(testList, true);
                                setFgContent(fg);

                                vUtils.dismissDialog();
                            }
                        });
                    } else if (getActivity().getResources().getString(R.string.group_omics).equals(fileFocus.getSource())) {//学案
                        resetOmicsList();
                        omicsList = com.alibaba.fastjson.JSON.parseArray(dataArr.toString(), TestData.class);
                        rightInfoCount = String.valueOf(omicsList.size());
                        needMultiPageRequestPreview = false;

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                previewHtml();
                                PreviewOmicsFg fg = new PreviewOmicsFg(omicsList, true);
                                setFgContent(fg);

                                vUtils.dismissDialog();
                            }
                        });
                    }
                } else if (ConstantsForServerUtils.TXT.equals(fileFocus.getFileType())) {//txt文档类
                    resetTxtList();
                    txtList = getTxtList(dataArr);

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            PreviewTxtFg wordFg = new PreviewTxtFg(txtList);
//                            txtCallback = (InterfacesCallback.ICanKnowSth8) wordFg;
                            setFgContent(wordFg);

                            vUtils.dismissDialog();
                        }
                    });
                } else if (ConstantsForServerUtils.IMAGES.equals(fileFocus.getFileType())) {//图片
                    final List<Picture> picList = com.alibaba.fastjson.JSON.parseArray(dataArr.toString(), Picture.class);
                    if (picList != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                previewPic();

                                PreviewPicFg fg = new PreviewPicFg(picList);
                                picCallback = (InterfacesCallback.ICanKnowSth6) fg;
                                setFgContent(fg);

                                vUtils.dismissDialog();
                            }
                        });
                    }
                } else if (ConstantsForServerUtils.AUDIO.equals(fileFocus.getFileType()) || ConstantsForServerUtils.VIDEO.equals(fileFocus.getFileType())) {//音频、视频
                    String url = "";
                    try {
                        url = dataArr.getString(0);
                    } catch (JSONException e) {
                        e.printStackTrace();

//                        ivNoData.setVisibility(View.VISIBLE);
//                        tvNum.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "数据返回不合规范！", Toast.LENGTH_SHORT).show();
                    }

                    resetVideoList();
                    url = "/upload/extend/video/201807/06/201807061457305988.mp4";

                    if (videoList != null && !TextUtils.isEmpty(url)) {
                        VideoAudio va = new VideoAudio();
                        va.setTitle("视频" + String.valueOf(1));
                        va.setPath(UrlUtils.PREFIX + url);

                        // 添加
                        videoList.add(va);

                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                previewAudioVideo(videoList);

                                vUtils.dismissDialog();
                            }
                        });
                    }
                }

                if (ConstantsForServerUtils.WORD.equals(fileFocus.getFileType())) {// 文字格式（学案、教案）
                    for (int i = 0; i < dataArr.length(); i++) {
                        if (dataArr.isNull(i)) {
                            continue;
                        }

                        JSONObject jsonObj = null;
                        try {
                            jsonObj = dataArr.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        DataInfo info = com.alibaba.fastjson.JSON.parseObject(
                                jsonObj.toString(), DataInfo.class);

                        needMultiPageRequestPreview = true;

                        planList.add(info);
                        if (planList.size() < Integer.parseInt(rightInfoCount)) {//循环请求单页数据
                            currentPageNumForPreview = currentPageNumForPreview + 1;

                            DataID01 data = new DataID01();
                            data.setDataid(fileFocus.getDataID());
                            data.setIndex(String.valueOf(currentPageNumForPreview));
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(data);

                            requestFileDetailJsonArrFromServer("getLectureView", jsonStr, fileFocus.getDataID());
                        } else {
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    previewWord();

                                    llWrapper01.setVisibility(View.VISIBLE);
                                    setVPagerAdapter(0);
                                }
                            });
                        }

//                        if (planList.size() == 0) {
//                            planList.add(info);
//
//                            // 造一个假数据放到尾端，便于viewPager滑动监听
//                            planList.add(new DataInfo());
//                        } else if (planList.size() > 1) {
//                            planList.remove(planList.size() - 1);// 先去掉尾端的假数据
//                            planList.add(planList.size(), info);// 在尾端加上新数据
//
//                            if (String.valueOf(planList.size()).equals(rightInfoCount)) {//最后一条数据的情况
//                            } else {
//                                // 造一个假数据放到尾端，便于viewPager滑动监听
//                                planList.add(new DataInfo());
//                            }
//                        }
//
//                    }
                    }

                    hasLoadOnce = true;
                }
            }
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    String msg = "暂无预览，切换文件试试？";
                    showNoPreviewContent(msg);
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

    /**
     * 添加题目Fragment
     */
    private void setFgContent(Fragment fg) {
        FragmentManager manager = getActivity().getSupportFragmentManager();//FragmentManager调用v4包内的
        FragmentTransaction transaction = manager.beginTransaction();//FragmentTransaction调用v4包内的（FragmentTransaction transaction声明成局部的）

        if (fg.isAdded()) {
            transaction.show(fg).commit();
        } else {
            transaction.replace(R.id.ll_preview_content_layout_fg_attend_class_detail, fg).commit();//替换为名称为A的fragment并显示它
        }
    }

    private void showNoFileData(String msg) {
        if (currentPageNumForFile == 1) {// 首页数据
            llNoFile.setVisibility(View.VISIBLE);
            lstvFiles.setVisibility(View.GONE);

            // 刷新显示没有更多数据
            vPullDown.notifyDidNoMore();
            vPullDown.setVisibility(View.GONE);

            showNoPreviewContent("暂无预览，请切换授课文件");
        } else {
            // 刷新显示没有更多数据
            vPullDown.notifyDidNoMore();
            // prlstvFiles.loadMoreComplete();

            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 授课列表适配器
     */
    private void setLstvFileAdapter(boolean isShowChoiceMenu) {
        if (filesAdapter == null) {
            callback = (InterfacesCallback.ICanDoSth) this;
            filesAdapter = new FilesListAdapter(getActivity(), fileList, callback);
            lstvFiles.setAdapter(filesAdapter);
        } else {
            filesAdapter.notifyDataSetChanged();
        }
    }


    private void setGridViewAdapter(int pos) {
//        if (glAdapter == null) {
        // 实例化ImageAdapter适配器
        glAdapter = new GalleryAdapter(getActivity(), coursewareList);
        // 将适配器的数据存储到Gallery组件中（设置Gallery组件的Adapter对象）
        glFile.setAdapter(glAdapter);
        glFile.setUnselectedAlpha(1f);
//        } else {
//            glAdapter.notifyDataSetChanged();
//        }

        if (pos > 0) {
            glFile.setSelection(pos - 1);
        }
//        if (coursewareList.size() > 2 && pos < 4) {
//            // 设置第二个数据在中央位置
//            glFile.setSelection(2);
//        } else {
//            glFile.setSelection(pos);
//        }

        glAdapter.setCurrentPos(pos);
    }

    /**
     * 设置授课viewPager适配器
     *
     * @param position 某个位置
     */
    private void setVPagerAdapter(int position) {
        if (vpager == null) {
            return;
        }

        vpager.removeAllViews();

        resetAllPreviewContentWidgets();

        if (ConstantsForServerUtils.PPT.equals(fileFocus.getFileType()) || ConstantsForServerUtils.IMAGES.equals(fileFocus.getFileType())) {// 图片类
            setPicVPagerAdapter(position);
        } else if (ConstantsForServerUtils.HTML.equals(fileFocus.getFileType())) {//试题、学案Html类型
        } else {// 文字格式（学案、教案、试题、文档）等
            setWordVPagerAdapter(position);
        }

//        // 设置适配器
//        if (picVpagerAdapter == null) {
//            picVpagerAdapter = new CustomPagerAdapter03(getActivity(),
//                    fileContents);
//            vpager.setAdapter(picVpagerAdapter);
//
//            // 默认设置到中间的某个位置
//            // if (coursewareList.size() > 0) {
//            // int pos = Integer.MAX_VALUE / 2
//            // - (Integer.MAX_VALUE / 2 % rightInfoList.size());
//            // 2147483647 / 2 = 1073741823 - (1073741823 % 5)
//            // }
//        } else {
//            // 刷新布局
//            picVpagerAdapter.notifyDataSetChanged();
//        }

        //  设置到某个位置
        vpager.setCurrentItem(position);
    }


    /**
     * 设置图片展示的viewPager适配器
     *
     * @param position 某个位置
     */
    private void setPicVPagerAdapter(int position) {
        // 设置适配器
        if (picVpagerAdapter == null) {
            picVpagerAdapter = new CustomPagerAdapter03(getActivity(),
                    coursewareList);
            vpager.setAdapter(picVpagerAdapter);

            // 默认设置到中间的某个位置
            // if (coursewareList.size() > 0) {
            // int pos = Integer.MAX_VALUE / 2
            // - (Integer.MAX_VALUE / 2 % rightInfoList.size());
            // 2147483647 / 2 = 1073741823 - (1073741823 % 5)
            // }
        } else {
            // 刷新布局
            picVpagerAdapter.notifyDataSetChanged();
        }

        // 重置文字适配器
//        wordVPagerAdapter = null;
    }


    /**
     * 设置右侧文字展示的viewPager适配器
     *
     * @param position 某个位置
     */
    private void setWordVPagerAdapter(int position) {
        // 设置适配器
        if (wordVPagerAdapter == null) {
            wordVPagerAdapter = new CustomPagerAdapter04(getActivity(),
                    planList);
            vpager.setAdapter(wordVPagerAdapter);

            // 默认设置到中间的某个位置
            // if (rightInfoList.size() > 0) {
            // int pos = Integer.MAX_VALUE / 2
            // - (Integer.MAX_VALUE / 2 % rightInfoList.size());
            // 2147483647 / 2 = 1073741823 - (1073741823 % 5)
            // }
        } else {
            // 刷新布局
            wordVPagerAdapter.notifyDataSetChanged();
        }

        // 重置图片适配器
//        picVpagerAdapter = null;
    }

    /**
     * 设置试题Html类型展示的viewPager适配器
     *
     * @param position 某个位置
     */
    private void setHtmlVPagerAdapter(int position) {
        // 设置适配器
        if (wordVPagerAdapter == null) {
            wordVPagerAdapter = new CustomPagerAdapter04(getActivity(),
                    planList);
            vpager.setAdapter(wordVPagerAdapter);

            // 默认设置到中间的某个位置
            // if (rightInfoList.size() > 0) {
            // int pos = Integer.MAX_VALUE / 2
            // - (Integer.MAX_VALUE / 2 % rightInfoList.size());
            // 2147483647 / 2 = 1073741823 - (1073741823 % 5)
            // }
        } else {
            // 刷新布局
            wordVPagerAdapter.notifyDataSetChanged();
        }

        // 重置图片适配器
//        picVpagerAdapter = null;
    }

    /**
     * 当前无预览内容的展示
     *
     * @param msg
     */
    private void showNoPreviewContent(String msg) {
        resetAllPreviewContentData();
        resetAllPreviewContentWidgets();

//        setVPagerAdapter(0);
//        llWrapper01.setVisibility(View.GONE);
        ivNoPreviewContent.setVisibility(View.VISIBLE);


        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        vUtils.dismissDialog();
    }

    /**
     * 重置预览布局
     */
    private void resetAllPreviewContentWidgets() {
        setPicVPagerAdapter(0);
        setWordVPagerAdapter(0);
        setHtmlVPagerAdapter(0);
//        showNoPreviewPPT();
//        showNoPreviewWord();
//        showNoPreviewPic();
//        showNoPreviewHtml();
//        showNoPreviewAudioVideo();
    }

    /**
     * 展示PPT课件，以ViewPager滑动+缩略图方式展示
     */
    private void previewPPT() {
        llWrapper01.setVisibility(View.VISIBLE);

        //画廊
        rlFile.setVisibility(View.VISIBLE);

//        llPreviewContent.setVisibility(View.GONE);
    }

    /**
     * 展示PPT课件，以ViewPager滑动+缩略图方式展示
     */
    private void showNoPreviewPPT() {
        llWrapper01.setVisibility(View.GONE);

        //画廊
        rlFile.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.GONE);
    }

    /**
     * 展示word格式文件，以ViewPager滑动方式展示
     */
    private void previewWord() {
        llWrapper01.setVisibility(View.VISIBLE);

        //画廊
        rlFile.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.GONE);
    }

    /**
     * 展示word格式文件，以ViewPager滑动方式展示
     */
    private void showNoPreviewWord() {
        llWrapper01.setVisibility(View.VISIBLE);

        //画廊
        rlFile.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.GONE);
    }

    /**
     * 展示Html格式文件（如组学案、组试题），以普通上下滑动方式展示
     */
    private void previewHtml() {
        llWrapper01.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.VISIBLE);
    }

    /**
     * 展示Html格式文件（如组学案、组试题），以普通上下滑动方式展示
     */
    private void showNoPreviewHtml() {
        llWrapper01.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.GONE);
    }

    /**
     * 展示图片文件，以gridView方式展示
     */
    private void previewPic() {
        llWrapper01.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.VISIBLE);
    }

    /**
     * 展示图片文件，以gridView方式展示
     */
    private void showNoPreviewPic() {
        llWrapper01.setVisibility(View.GONE);

        llPreviewContent.setVisibility(View.GONE);
    }

    /**
     * 展示音视频
     */
    private void previewAudioVideo(List<VideoAudio> videoList) {
        llWrapper01.setVisibility(View.GONE);

        //画廊
//        rlFile.setVisibility(View.GONE);
//        llPreviewContent.setVisibility(View.GONE);

        rlVideo.setVisibility(View.VISIBLE);

        // 初始化视频
        videoCurr = videoList.get(0);
        initVideo(videoCurr);
    }

    /**
     * 隐藏音视频
     */
    private void showNoPreviewAudioVideo() {
        llWrapper01.setVisibility(View.GONE);

        //画廊
//        rlFile.setVisibility(View.GONE);
//        llPreviewContent.setVisibility(View.GONE);

        rlVideo.setVisibility(View.GONE);

        // 初始化视频
//        videoCurr = new VideoAudio();
//        initVideo(videoCurr);
    }

    private ArrayList<String> getTxtList(JSONArray dataArr) {
        String str = "";

//        str = "<p style=\"text-align: justify;\"><span>（</span><span>1</span><span>）</span><span>走进作者</span><span>&nbsp;&nbsp;</span><span>毛泽东，字润之。</span><span>1893</span><span>年</span><span>12</span><span>月</span><span>26</span><span>日生于湖南湘潭韶山冲一个农民家庭。</span><span>1976</span><span>年</span><span>9</span><span>月</span><span>9</span><span>日在北京逝世。中国人民的领袖，马克思主义者，伟大的无产阶级革命家、战略家和理论家，中国共产党、中国人民解放军和中华人民共和国的主要缔造者和领导人，诗人，书法家。</span></p><p style=\"text-align: justify;\"><span>（</span><span>2</span><span>）了解背景</span><span>&nbsp;&nbsp;&nbsp;</span><span>这首词写于</span><span>1925</span><span>年。</span><span>1925</span><span>年是北伐战争开始的前一年。当时中国阶级斗争异常激烈。震惊中外的</span><span>“</span><span>五卅运动</span><span>”</span><span>和省港大罢工已经爆发农民运动也势如破竹，迅猛异常，在 全国十几个省蔓延开来。毛泽东同志当时直接领导了湖南的农村运动先后在韶山等地建了二十多个农民协会，成立雪耻会，并创立了湖南农村第一个党支部</span><span>——</span><span>中共韶山支部。随着革命高潮的到来，各党派对革命领导权进行激烈的争夺。国民党右派势力了想篡夺立功难道权利，而党内，陈独秀又提出了</span><span>“</span><span>一切权力归国民党</span><span>”</span><span>的错误主张。毛泽东等同志同国民党右派势力和党内右倾机会注义进行了针锋相对的斗争。军阀要逮捕毛泽东同志，在韶山人民的掩护下，毛泽东秘密离开了韶山，前往广东创办全国农民运动讲习所。他在途经长沙时重游了橘子洲。面对绚丽的秋景，毛泽东回忆往昔的峥嵘岁月，写下了这首气势磅礴的词。</span></p>";
        if (dataArr != null && dataArr.length() > 0) {
            if (!dataArr.isNull(0)) {
                try {
                    str = dataArr.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONEx_PRAty", e.toString());
                }

                txtList.add(str);
            }
        }

        return txtList;
    }

    private void resetAllPreviewContentData() {
        resetPlanList();
        resetCoursewareList();
        resetOmicsList();
        resetTestList();
        resetTxtList();
        resetVideoList();

        rightInfoCount = "0";
        currentPageNumForPreview = 1;
        fileFocus = new File01();
    }

    /**
     * 重置教案学案等文字类数据
     */
    private void resetPlanList() {
        // 右侧课件
        if (planList.size() > 0) {
            planList.clear();
        }
    }

    /**
     * 重置试题类数据
     */
    private void resetTestList() {
        // 右侧课件
        if (testList.size() > 0) {
            testList.clear();
        }
    }

    /**
     * 重置学案类数据(来源：组学案)
     */
    private void resetOmicsList() {
        // 右侧课件
        if (omicsList.size() > 0) {
            omicsList.clear();
        }
    }

    /**
     * 重置文档txt类数据(来源：扩展资源)
     */
    private void resetTxtList() {
        // 右侧课件
        if (txtList.size() > 0) {
            txtList.clear();
        }
    }


    /**
     * 重置课件ppt数据
     */
    private void resetCoursewareList() {
        // 右侧课件
        if (coursewareList.size() > 0) {
            coursewareList.clear();
        }
    }

    /**
     * 重置音视频数据
     */
    private void resetVideoList() {
        // 右侧课件
        if (videoList.size() > 0) {
            videoList.clear();
        }
    }

    private void switchCatalog() {
        // 跳转至选择教材目录界面
        Intent intent = new Intent(getActivity(),
                ChoiceCatalogAty.class);
        intent.putExtra(ChoiceCatalogAty.CATALOG_POS, 0);
        startActivityForResult(intent, ConstantsUtils.REQUEST_CODE01);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsUtils.REQUEST_CODE01 && resultCode == RESULT_OK) {// 从选择教材界面回传数据
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }

            // 目录ID
            String catalogIDCurr = bundle.getString(ConstantsUtils.CATALOG_ID);
            // 目录名称
            String catalogNameCurr = bundle.getString(ConstantsUtils.CATALOG_NAME);
            if (!ValidateFormatUtils.isEmpty(catalogNameCurr)) {
                this.catalogNameCurr = catalogNameCurr;
                tvCatalogName.setText(catalogNameCurr);
            }

            if (!ValidateFormatUtils.isEmpty(catalogIDCurr)) {
                this.catalogIDCurr = catalogIDCurr;
                requestFileListFromServer(catalogIDCurr);
            } else {
                llNoFile.setVisibility(View.VISIBLE);
                ivNoPreviewContent.setVisibility(View.VISIBLE);

                resetAllPreviewContentData();
                setVPagerAdapter(0);

                Toast.makeText(getActivity(), "目录ID为空，请重新选择目录", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    protected void lazyLoad() {
//        if (!isPrepared || !isVisible || hasLoadOnce) {
//            return;
//        }


        boolean hasChoicedCatalog = PreferencesUtils.acquireBooleanInfoFromPreferences(getActivity(), ConstantsUtils.HAS_CHOICED_MATERIAL);
        if (!hasChoicedCatalog) {
            switchCatalog();
        }

        boolean hasChoiced = PreferencesUtils.acquireBooleanInfoFromPreferences(getActivity(), ConstantsUtils.HAS_CHOICED_MATERIAL);
        if (hasChoiced) {

        }
        catalogNameCurr = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CLASS_NAME_CHOICED);
        if (tvClassName != null && !TextUtils.isEmpty(catalogNameCurr)) {
            tvClassName.setText(catalogNameCurr);
        }

        String classID = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CLASS_ID_CHOICED);
        if (!TextUtils.isEmpty(classID)) {
            this.classID = classID;
        }

        String catalogIDCurr = PreferencesUtils.acquireInfoFromPreferences(getActivity(), ConstantsForPreferencesUtils.CATALOG_ID_CHOICED);
        if (!TextUtils.isEmpty(catalogIDCurr)) {
            requestFileListFromServer(catalogIDCurr);
        }
    }

    @Override
    public void doSth(int i, int pos, String ID) {
        switch (i) {
            case ConstantsUtils.AFTER_CLICK_ALL://点击文件列表单项
//                positionMax = -1;
                resetAllPreviewContentData();

                if (fileList != null && fileList.size() > 0) {
                    fileFocus = fileList.get(pos);
                    if (fileFocus != null) {
                        vUtils.showLoadingDialog("");

                        requestFileDetailFromServer(fileFocus.getFileType(), ID);
                    }
                }

                break;
        }
    }

    /**
     * 底部页卡偏移事件监听
     */
    private class VPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case 0:// 滑动结束
                    isPageSelected = false;

                    break;
                case 1:// 正在滑动（初次加载是不走此路径的）
                    isPageSelected = true;
                    isScroll = true;

                    break;
                case 2:// 滑动完毕(onPageScrolled()方法前)
                    if (isScroll) {
                        isScroll = false;
                    } else {
                        isPageSelected = false;
                    }

                    break;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // 滚动时调用
            // int s = position;
            // System.out.print(s);
        }

        @Override
        public void onPageSelected(int arriveIndex) { // 新的条目被选中时调用
            // System.out.println("onPageSelected: " + arriveIndex);

            newPosition = arriveIndex % Integer.valueOf(rightInfoCount);
//            if (isPageSelected && newPosition > positionMax) {// 正向向右滑动的情况
//                if (newPosition == 1) {
//                    currentPageNumForPreview = newPosition + 1;
//                }
//                positionMax = newPosition;
//                if (fileFocus != null && needMultiPageRequestPreview) {
//                    vUtils.showLoadingDialog("");
//                    String ID = fileFocus.getDataID();
//                    requestFileDetailFromServer(fileType, ID);
//                }
//            }

            if (ConstantsForServerUtils.PPT.equals(fileFocus.getFileType())) {
                setGridViewAdapter(newPosition);
            }
        }
    }

    /**
     * 控件监听
     *
     * @author chenhui
     */
    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_wrapper_switch_class_layout_fg_classes://切换班级
                    Intent intent = new Intent(getActivity(), ChooseModuleAty.class);
//                    startActivityForResult(intent, ConstantsUtils.REQUEST_CODE01);
                    startActivity(intent);

                    break;

                case R.id.rl_wrapper_switch_catalog_layout_fg_classes://切换目录
                    switchCatalog();

                    break;
//                case R.id.iv_layout_fg_classes://图片
//                    iv.setVisibility(View.GONE);
//                    ClassroomTestFg aqFg = new ClassroomTestFg();
//                    showContent(aqFg);

//                    break;
            }
        }
    }
}
