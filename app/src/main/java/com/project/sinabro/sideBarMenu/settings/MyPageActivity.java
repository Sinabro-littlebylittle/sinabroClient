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
import android.util.Log;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityMyPageBinding;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.UserAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends AppCompatActivity {

    private ActivityMyPageBinding binding;

    private Bitmap bitmap;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    UserAPI userAPI;

    private Intent intent;

    @Override
    public void onBackPressed() {
        // 프로그래머가 원하는 액티비티로 이동
        intent = getIntent();
        String departActivityName = intent.getStringExtra("departActivityName");
        if (departActivityName.equals("MainActivity")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        } else if (departActivityName.equals("SettingsFragment")) {
            Intent intent = new Intent(getApplicationContext(), CheckPasswordActivity.class);
            intent.putExtra("exitCheckPasswordActivity", true);
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        intent = getIntent();
        String departActivityName = intent.getStringExtra("departActivityName");

        Call<ResponseBody> call_userAPI_getUserSelfInfo = userAPI.getUserSelfInfo();
        call_userAPI_getUserSelfInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        tokenManager.saveUserInfo(jsonObject);
                        String username = tokenManager.getUsername();
                        String email = tokenManager.getEmail();
                        binding.myPageFragmentTitleTv.setText(username + "님의 정보");
                        binding.usernameTv.setText(username);
                        binding.emailTv.setText(email);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    switch (response.code()) {
                        case 401:
                        case 404:
                        case 415:
                            new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), MyPageActivity.this);
                            // "로그인" 액티비티로 이동
                            final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            finish(); // 현재 액티비티 종료
                            break;
                        default:
                            new ToastWarning(getResources().getString(R.string.toast_none_status_code), MyPageActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), MyPageActivity.this);
            }
        });

        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 액티비티로 이동
                if (departActivityName.equals("MainActivity")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                } else if (departActivityName.equals("SettingsFragment")) {
                    Intent intent = new Intent(getApplicationContext(), CheckPasswordActivity.class);
                    intent.putExtra("exitCheckPasswordActivity", true);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                }
            }
        });

        /** "마이페이지(fragment)"에서 "정보 수정(activity)"로 화면 전환 코드 추가 */
        binding.modifyMyInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "정보 수정" 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), ModifyMyInfoActivity.class);
                intent.putExtra("username", binding.usernameTv.getText().toString());
                intent.putExtra("email", binding.emailTv.getText().toString());
                intent.putExtra("departActivityName", departActivityName);
                startActivity(intent);
                finish();
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