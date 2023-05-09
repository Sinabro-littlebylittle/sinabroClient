package com.project.sinabro;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

import com.project.sinabro.bottomSheet.place.AddLocationInfoActivity;
import com.project.sinabro.bottomSheet.place.PlaceListActivity;
import com.project.sinabro.toast.ToastSuccess;

public abstract class AbstractCameraXActivity<R> extends BaseModuleActivity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA};

    private long mLastAnalysisResultTime;
    private Button scanbtn;

    private Dialog peopleCountDetectionResult_dialog;


    protected abstract int getContentViewLayoutId();

    protected abstract TextureView getCameraPreviewTextureView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());

        startBackgroundThread();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            setupCameraX();
        }

        peopleCountDetectionResult_dialog = new Dialog(AbstractCameraXActivity.this);  // Dialog 초기화
        peopleCountDetectionResult_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        peopleCountDetectionResult_dialog.setContentView(com.project.sinabro.R.layout.dialog_people_count_detection_result); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        peopleCountDetectionResult_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Animation animation = AnimationUtils.loadAnimation(this, com.project.sinabro.R.anim.ripple_animation);
        scanbtn = findViewById(com.project.sinabro.R.id.scanbutton);
        scanbtn.startAnimation(animation);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //카메라 화면에서 완료버튼 클릭 시
                showDialog_people_count_detection_result();
            }
        });
    }

    // (dialog_place_remove) 다이얼로그를 디자인하는 함수
    public void showDialog_people_count_detection_result() {
        int peopleCnt = ResultView.countP();
        ResultView.sum=0;
        TextView peopleCountDetectionResult_tv = peopleCountDetectionResult_dialog.findViewById(com.project.sinabro.R.id.peopleCountDetectionResult_tv);
        peopleCountDetectionResult_tv.setText((String.valueOf(peopleCnt) + "명의 인원이 확인되었습니다"));
        peopleCountDetectionResult_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        peopleCountDetectionResult_dialog.getWindow().setDimAmount(0.35f);

        // "취소" 버튼
        peopleCountDetectionResult_dialog.findViewById(com.project.sinabro.R.id.noBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleCountDetectionResult_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼
        peopleCountDetectionResult_dialog.findViewById(com.project.sinabro.R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleCountDetectionResult_dialog.dismiss(); // 다이얼로그 닫기
                finish();
                final Intent intent = new Intent(AbstractCameraXActivity.this, MainActivity.class);
                intent.putExtra("isDetected", true);
                intent.putExtra("peopleCountDetectionResult", peopleCnt);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                                this,
                                "You can't use object detection example without granting CAMERA permission",
                                Toast.LENGTH_LONG)
                        .show();
                finish();
            } else {
                setupCameraX();
            }
        }

    }

    private void setupCameraX() {
        final TextureView textureView = getCameraPreviewTextureView();
        final PreviewConfig previewConfig = new PreviewConfig.Builder().build();
        final Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> textureView.setSurfaceTexture(output.getSurfaceTexture()));

        final ImageAnalysisConfig imageAnalysisConfig =
                new ImageAnalysisConfig.Builder()
                        .setTargetResolution(new Size(480, 640))
                        .setCallbackHandler(mBackgroundHandler)
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                        .build();
        final ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);
        imageAnalysis.setAnalyzer((image, rotationDegrees) -> {
            if (SystemClock.elapsedRealtime() - mLastAnalysisResultTime < 500) {
                return;
            }

            final R result = analyzeImage(image, rotationDegrees);
            if (result != null) {
                mLastAnalysisResultTime = SystemClock.elapsedRealtime();
                runOnUiThread(() -> applyToUiAnalyzeImageResult(result));
            }
        });

        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    @WorkerThread
    @Nullable
    protected abstract R analyzeImage(ImageProxy image, int rotationDegrees);

    @UiThread
    protected abstract void applyToUiAnalyzeImageResult(R result);
}
