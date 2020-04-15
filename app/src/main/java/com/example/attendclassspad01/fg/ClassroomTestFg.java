package com.example.attendclassspad01.fg;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.attendclassspad01.R;
import com.example.attendclassspad01.Util.Constants;
import com.example.attendclassspad01.Util.ConstantsUtils;
import com.example.attendclassspad01.Util.FileUtils;
import com.example.attendclassspad01.adapter.TestQuestionAdapter01;
import com.example.attendclassspad01.model.Test;
import com.example.attendclassspad01.model.TestPaper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.attendclassspad01.Util.ConstantsUtils.TAKE_PHOTO;

/**
 * 答题界面
 * author chenhui_2020.03.04
 */
public class ClassroomTestFg extends BaseNotPreLoadFg {
    private boolean isPrepared;// 标志位，标志已经初始化完成
    private boolean hasLoadOnce = false;// 是否已被加载过一次，第二次就不再去请求数据了

    private TestPaper paper;//试卷
    private List<Test> questionList;//题目
    private Uri imageUri;


    private TestQuestionAdapter01 qAdapter;//题目适配器

    private View allFgView;// 总布局
    private ListView lvQuestion;//题目
    private ImageView ivAnswerQuestionPic;//答题图片

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (allFgView == null) {
            allFgView = inflater.inflate(R.layout.layout_fg_classroom_test, null);

            paper = new TestPaper();

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
        lvQuestion = allFgView.findViewById(R.id.lv_question_layout_fg_classroom_test);
        questionList = getTestQuestionList(paper);
        setLvQuestionAdapter();
        setLvListener();

        //答题图片
//        ivAnswerQuestionPic = (ImageView) allFgView.findViewById(R.id.iv_answer_question_pic_layout_fg_classroom_test);
        //拍照
//        ImageView ivTakePhoto = (ImageView) allFgView.findViewById(R.id.iv_take_photo_layout_fg_classroom_test);
//        ivTakePhoto.setOnClickListener(new Listeners());
        //本地
//        ImageView ivLocalPic = (ImageView) allFgView.findViewById(R.id.iv_local_pic_layout_fg_classroom_test);
//        ivLocalPic.setOnClickListener(new Listeners());
        //删除已选照片
//        ImageView ivDeletePic = (ImageView) allFgView.findViewById(R.id.iv_delete_layout_fg_classroom_test);
//        ivTakePhoto.setOnClickListener(new Listeners());
    }

    private void setLvQuestionAdapter() {
        if (qAdapter == null) {
            qAdapter = new TestQuestionAdapter01(getActivity(), paper.getQuestionList(), 0);
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

    /**
     * 获取试题列表
     *
     * @param paper
     * @return
     */
    private List<Test> getTestQuestionList(TestPaper paper) {
        List<Test> tests = new ArrayList<Test>();
        for (int j = 0; j < 10; j++) {
            Test q = new Test();
            q.setId("qid" + j);
            q.setContent(String.valueOf(j + 1) + ".以下历史事件中，与关羽无关的是（）：");
            q.setUserAnswer("D");
            q.setRightAnswer("D");
            q.setAnalysis("七擒七纵，七擒孟获，又称南中平定战，是建兴三年蜀汉丞相诸葛亮对南中发动平定南中的战争。当时朱褒、雍闿、高定等人叛变，南中豪强孟获亦有参与，最后诸葛亮亲率大军南下，平定南中。");
            if (paper != null) {
                q.setTitle("错题本:" + paper.getName());
            }
            q.setChoiced(false);

            tests.add(q);
            paper.setQuestionList(tests);
        }

        return tests;
    }

    private void takePhoto() {
        // 创建一个File对象，用于保存摄像头拍下的图片，这里把图片命名为output_image.jpg
        // 并将它存放在手机SD卡的应用关联缓存目录下
        File outputImage = new File(FileUtils.ROOT_DIRECTORY + "com.example.attendclassspad01/files/Pictures/", "answer_question_pic.jpg");

        // 对照片更换设置
        try {
            // 如果上一次的照片存在，就删除
            if (outputImage.exists()) {
                outputImage.delete();
            }
            // 创建一个新的文件
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果Android版本大于等于7.0
        if (Build.VERSION.SDK_INT >= 24) {
            // 将File对象转换成一个封装过的Uri对象
            Log.i("包名", getActivity().getPackageName().toString());
            imageUri = FileProvider.getUriForFile(getActivity(), "com.example.attendclassspad01" + ".fileprovider", outputImage);
        } else {
            // 将File对象转换为Uri对象，这个Uri标识着output_image.jpg这张图片的本地真实路径
            imageUri = Uri.fromFile(outputImage);
        }

        // 动态申请权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            // 启动相机程序
            startCamera();
        }
    }

    private void startCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 指定图片的输出地址为imageUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
//                if (requestCode == RESULT_OK) {
                try {
                    // 将图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                    // 将图片显示出来
                    ivAnswerQuestionPic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                }
                break;
            default:
        }
    }

    @Override
    protected void lazyLoad() {
//        if (!isPrepared || !isVisible || hasLoadOnce) {
//            return;
//        }
    }

    private class Listeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.iv_take_photo_layout_fg_classroom_test://拍照
//                    takePhoto();
//                    break;
            }
        }
    }
}
