package com.hong.myface;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gc.materialdesign.views.ButtonRectangle;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.hong.myface.Adapter.FacesAdapter;
import com.hong.myface.bean.FaceppBean;
import com.hong.myface.contract.Contract;
import com.hong.myface.degger.DaggerMainComponent;
import com.hong.myface.degger.MainModule;
import com.hong.myface.presenter.MainPresenter;
import com.hong.myface.util.BitmapUtil;
import com.hong.myface.util.UriUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.supercharge.shimmerlayout.ShimmerLayout;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class MainActivity extends AppCompatActivity implements Contract.IView {

    final String TAG = "MainActivity";
    final int TAKE_PHOTO = 1;
    final int CHO0SE_PHOTO = 2;
    Uri imgUri;
    File file;
    List<FaceppBean.FacesBean> facesList = new ArrayList<>();
    FacesAdapter facesAdapter;
    ShimmerLayout shimmerLayout;

    @BindView(R.id.btn_select_photo)
    ButtonRectangle btnSelectPhoto;
    @BindView(R.id.btn_take_photo)
    ButtonRectangle btnTakePhoto;
    @BindView(R.id.iv_faceImage)
    ImageView ivFaceImage;
    @BindView(R.id.pb_progressBar)
    ProgressBar pbProgressBar;
    @BindView(R.id.rv_recycleView)
    RecyclerView rvRecycleView;
    @BindView(R.id.bottom_text)
    TextView bottomText;
    @BindView(R.id.image_card)
    CardView imageCard;
    @BindView(R.id.iv_faceImage_bg)
    ImageView ivFaceImageBg;
    @BindView(R.id.shimmerCard)
    ShimmerLayout shimmerCard;
    @BindView(R.id.rv_card_view)
    CardView rvCardView;
    @BindView(R.id.empty_layout)
    CardView emptyCardView;

    @Inject
    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initProgress();

        requestPermission();

        DaggerMainComponent.builder()
                .mainModule(new MainModule(this))
                .build()
                .inject(this);

        mainPresenter.attachView(this);

        initAdapter();

        shimmerCard.startShimmerAnimation();
    }

    private void initAdapter() {
        facesAdapter = new FacesAdapter(R.layout.face_item, facesList, this);
        facesAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        facesAdapter.isFirstOnly(false);
        facesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DetailDialogFragment dialogFragment = new DetailDialogFragment();
                dialogFragment.show(getFragmentManager(), "detail");
                EventBus.getDefault().postSticky(new FaceEvent(facesList.get(position)));
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRecycleView.setLayoutManager(manager);
        rvRecycleView.setAdapter(facesAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvRecycleView);
    }

    private void initProgress() {
        Sprite wave = new Wave();
        wave.setColor(getResources().getColor(R.color.colorAccent));
        pbProgressBar.setIndeterminateDrawable(wave);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            if (decorView == null) {
                return;
            }
            int vis = decorView.getSystemUiVisibility();
            if (true) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        Toast.makeText(this, "需要授权才能使用", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "需要授权才能使用", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @OnClick({R.id.btn_select_photo})
    public void onViewClicked(View view) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHO0SE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imgUri));
                        int degree = BitmapUtil.getBitmapDegree(file.getAbsolutePath());
                        bitmap = BitmapUtil.rotateBitmapByDegree(bitmap, degree);
                        mainPresenter.handleRequest(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHO0SE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String path;
                    if (Build.VERSION.SDK_INT >= 19) {
                        path = UriUtil.handleImageOnKiKat(data, this);
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        mainPresenter.handleRequest(bitmap);
                    } else {
                        path = UriUtil.handleImageBeforeKiKat(data, this);
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        mainPresenter.handleRequest(bitmap);
                    }
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_take_photo)
    public void takePhoto() {

        file = new File(getExternalCacheDir(), "imageCache.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imgUri = FileProvider.getUriForFile(this, "com.hong.myface.fileprovider", file);
        } else {
            imgUri = Uri.fromFile(file);
        }
        Log.e("File", file.getAbsolutePath());
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void showError() {
        showEmpHideRv();
        clearFaces();
        Toast.makeText(this, "出错了，抱歉！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        btnSelectPhoto.setVisibility(View.GONE);
        btnTakePhoto.setVisibility(View.GONE);
        bottomText.setVisibility(View.INVISIBLE);
        pbProgressBar.setVisibility(View.VISIBLE);
        showEmpHideRv();
    }

    @Override
    public void hideProgress() {
        emptyCardView.setVisibility(View.GONE);
        pbProgressBar.setVisibility(View.GONE);
        shimmerCard.stopShimmerAnimation();
        btnTakePhoto.setVisibility(View.VISIBLE);
        btnSelectPhoto.setVisibility(View.VISIBLE);
        rvCardView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPhoto(Bitmap bitmap) {
        ivFaceImageBg.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(bitmap)
                .transition(new BitmapTransitionOptions().crossFade(1500))
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                .into(ivFaceImageBg);
    }

    @Override
    public void showMarkedPhoto(Bitmap bitmap) {
        ivFaceImage.setVisibility(View.VISIBLE);
        ivFaceImage.bringToFront();
        Glide.with(this)
                .asBitmap()
                .load(bitmap)
                .transition(new BitmapTransitionOptions().crossFade(700))
                .into(ivFaceImage);
    }

    @Override
    public void showFacesInfo(List<FaceppBean.FacesBean> faces) {
        if (facesList != null && facesList.size() > 0) {
            facesList.clear();
        }
        if (faces == null || faces.size() <= 0) {
            Snackbar.make(shimmerCard, "抱歉，没有检测到人脸", Snackbar.LENGTH_LONG).show();
            showEmpHideRv();
            return;
        } else {
            if (facesList != null) {
                facesList.addAll(faces);
            }
            if (faces.size() > 1) {
                showTips();
            }
        }
        facesAdapter.notifyDataSetChanged();
    }

    private void showTips() {
        final int HIDE_TIPS = 1;
        bottomText.setVisibility(View.VISIBLE);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HIDE_TIPS:
                        bottomText.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }
        };
        Message message = Message.obtain();
        message.what = HIDE_TIPS;
        handler.sendMessageDelayed(message, 5000);

    }

    @Override
    public void clearFaces() {
        ivFaceImage.setVisibility(View.GONE);
        ivFaceImageBg.setVisibility(View.GONE);
        if (facesList != null) {
            facesList.clear();
        }
        facesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    public void showEmpHideRv() {
        rvCardView.setVisibility(View.GONE);
        emptyCardView.setVisibility(View.VISIBLE);
        shimmerCard.startShimmerAnimation();
    }
}
