package com.project.sinabro.sideBarMenu.settings;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityMyPageBinding;
import com.project.sinabro.toast.ToastSuccess;

import java.io.InputStream;

public class MyPageActivity extends AppCompatActivity {

    private ActivityMyPageBinding binding;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** "마이페이지(fragment)"에서 "정보 수정(activity)"로 화면 전환 코드 추가 */
        binding.modifyMyInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "정보 수정" 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), ModifyMyInfoActivity.class);
                startActivity(intent);
            }
        });

        binding.userImageRoundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //갤러리 호출
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_PICK);
                activityResultLauncher.launch(intent);
            }
        });

        /** "마이페이지(fragment)"에서 "비밀번호 변경(activity)"로 화면 전환r 코드 추가 */
        binding.passwordRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "비밀번호 변경" 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });

        binding.modifyCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.modifyCompleteBtn.setVisibility(View.INVISIBLE);
                new ToastSuccess(getResources().getString(R.string.toast_modify_user_image_success), MyPageActivity.this);
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        int calRatio = calculateInSampleSize(
                                result.getData().getData()
                                , getResources().getDimensionPixelSize(R.dimen.imgSize)
                                , getResources().getDimensionPixelSize(R.dimen.imgSize)
                        );

                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = calRatio;

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(result.getData().getData());
                            bitmap = BitmapFactory.decodeStream(inputStream, null, option);
                            inputStream.close();
                            if (bitmap != null) {
                                binding.userImageRoundedImageView.setImageBitmap(bitmap);
                                binding.modifyCompleteBtn.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeInUp)
                                        .duration(500)
                                        .repeat(0)
                                        .playOn(binding.modifyCompleteBtn);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private int calculateInSampleSize(Uri fileUri, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);

            // inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            // 로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 비율 계산
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}