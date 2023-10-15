package com.project.sinabro.sideBarMenu.authentication;

import static kotlin.io.ByteStreamsKt.readBytes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivitySignUpStep2Binding;
import com.project.sinabro.models.UserInfo;
import com.project.sinabro.retrofit.interfaceAPIs.AuthAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.UserAPI;
import com.project.sinabro.textWatcher.NicknameWatcher;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpStep2Activity extends AppCompatActivity {

    private ActivitySignUpStep2Binding binding;

    private Dialog askUserUseDefaultProfileImage_dialog,
            signUpSuccess_dialog;

    private Bitmap bitmap;

    private Intent intent;

    private String email, password;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private AuthAPI authAPI;

    private UserAPI userAPI;
    private String profilePath;
    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOrient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpStep2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        authAPI = retrofitService.getRetrofit().create(AuthAPI.class);

        intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        /** "기본 프로필 이미지 이용 안내" 다이얼로그 변수 초기화 및 설정 */
        askUserUseDefaultProfileImage_dialog = new Dialog(SignUpStep2Activity.this);  // Dialog 초기화
        askUserUseDefaultProfileImage_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        askUserUseDefaultProfileImage_dialog.setContentView(R.layout.dialog_ask_user_use_default_profile_image); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        askUserUseDefaultProfileImage_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** "회원가입 완료 안내" 다이얼로그 변수 초기화 및 설정 */
        signUpSuccess_dialog = new Dialog(SignUpStep2Activity.this);  // Dialog 초기화
        signUpSuccess_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        signUpSuccess_dialog.setContentView(R.layout.dialog_sign_up_success); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        signUpSuccess_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.nicknameTextInputLayout.getEditText().addTextChangedListener(new NicknameWatcher(binding.nicknameTextInputLayout, getResources().getString(R.string.sign_up_nickname_failed)));

        /** "유저 이미지 추가" 이미지뷰 클릭 시 이벤트 처리 코드 */
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

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (binding.nicknameTextInputLayout.getError() != null || String.valueOf(binding.nicknameEditText.getText()).equals("")) {
                    binding.nicknameEditText.requestFocus();
                    binding.nicknameTextInputLayout.setError(getResources().getString(R.string.sign_up_nickname_failed));
                    binding.nicknameTextInputLayout.setErrorEnabled(true);
                    binding.nicknameTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                    return;
                }

                if (bitmap == null) {
                    showDialog_ask_user_use_default_profile_image();
                    return;
                }
                else{
                    signup();
                }

                // 모든 입력이 정상적으로 완료되었을 때 "회원가입 완료 안내" 다이얼로그 띄우기
                showDialog_sign_up_success();
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
                                // 이미지 파일을 MultipartBody.Part로 변환
                                /*File file = new File(result.getData().getDataString());
                                Log.d("Profile", "onActivityResult: file"+file.toString() +" "+file.getName());
//                                Uri uri = Uri.parse(file.getPath());
//                                Log.d("Profile", "onActivityResult: uri"+uri.toString());
//                                getImageNameToUri(uri);*/

                                // content URI 가져오기
                                Uri contentUri = result.getData().getData();
                                // ContentResolver를 사용하여 파일 경로 가져오기
                                String[] projection = {MediaStore.Images.Media.DATA};
                                Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);

                                if (cursor != null && cursor.moveToFirst()) {
                                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                                    String filePath = cursor.getString(columnIndex);
                                    cursor.close();

                                    File file = new File(filePath);

                                    Log.d("Profile", "1: fail"+"1"+result.getData().toString() +"2"+ result.getData().getData().toString() +"path" +result.getData().getData().getPath() +"file "+mImgPath + " t " + mImgTitle + "o "+ mImgOrient);
                                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                                    Log.d("Profile  ", "23: fail");
                                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("profile", file.getName(), requestFile);
                                    userAPI = retrofitService.getRetrofit().create(UserAPI.class);
                                    Log.d("Profile  ", "234: fail"+imagePart);
                                    Log.d("Profile  ", "2343: fail"+userAPI+" n "+file);
                                    Call<ResponseBody> call = userAPI.uploadProfile(imagePart);
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Log.d("Profile", "3: fail");
                                            if (response.isSuccessful()) {
                                                // 이미지 업로드 성공
                                                // 서버로부터의 응답 처리
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(response.body().string());
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                try {
                                                    profilePath = jsonObject.getString("success");
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                Log.d("Profile", "onResponse: success"+profilePath);

                                            } else {
                                                // 이미지 업로드 실패
                                                Log.d("Profile", "onResponse: fail");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            // 네트워크 오류 또는 서버 오류
                                            Log.e("Profile", "Network error:"+t.getMessage());
                                            Log.d("Profile", "onResponse: fail"+"1"+result.getData().toString() +"2"+ result.getData().getData().toString() +"path" +result.getData().getData().getPath());
                                        }
                                    });

                                    // filePath를 이용하여 파일 업로드 등의 작업 수행
                                } else {
                                    Log.e("Profile", "Failed to retrieve file path from Content URI");
                                }
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

    public void signup(){
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setPassword(password);
        userInfo.setProfile(profilePath);
        userInfo.setUsername(binding.nicknameEditText.getText().toString());
        userInfo.setRole("member");

        Call<UserInfo> call = authAPI.signUp(userInfo);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    // "회원가입 완료 안내" 다이얼로그 띄우기
                    showDialog_sign_up_success();
                } else {
                    switch (response.code()) {
                        case 400:
                            // 가입하려는 계정의 이메일이 이미 존재하는 경우 서버 측에서 에러 반환
                            new ToastWarning(getResources().getString(R.string.toast_exist_email), SignUpStep2Activity.this);
                            break;
                        default:
                            new ToastWarning(getResources().getString(R.string.toast_none_status_code), SignUpStep2Activity.this);
                    }

                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), SignUpStep2Activity.this);
            }
        });
    }

    /**
     * (dialog_ask_user_use_default_profile_image) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_user_use_default_profile_image() {
        askUserUseDefaultProfileImage_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        askUserUseDefaultProfileImage_dialog.getWindow().setDimAmount(0.35f);

        // "아니오" 버튼
        Button noBtn = askUserUseDefaultProfileImage_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askUserUseDefaultProfileImage_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼
        askUserUseDefaultProfileImage_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askUserUseDefaultProfileImage_dialog.dismiss(); // 다이얼로그 닫기

                UserInfo userInfo = new UserInfo();
                userInfo.setEmail(email);
                userInfo.setPassword(password);
                userInfo.setUsername(binding.nicknameEditText.getText().toString());
                userInfo.setRole("member");

                Call<UserInfo> call = authAPI.signUp(userInfo);
                call.enqueue(new Callback<UserInfo>() {
                    @Override
                    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                        if (response.isSuccessful()) {
                            // "회원가입 완료 안내" 다이얼로그 띄우기
                            showDialog_sign_up_success();
                        } else {
                            switch (response.code()) {
                                case 400:
                                    // 가입하려는 계정의 이메일이 이미 존재하는 경우 서버 측에서 에러 반환
                                    new ToastWarning(getResources().getString(R.string.toast_exist_email), SignUpStep2Activity.this);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), SignUpStep2Activity.this);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfo> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), SignUpStep2Activity.this);
                    }
                });
            }
        });
    }

    /**
     * (dialog_sign_up_success) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_sign_up_success() {
        signUpSuccess_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        signUpSuccess_dialog.getWindow().setDimAmount(0.35f);

        // "확인" 버튼 클릭 시 이벤트 처리 코드
        signUpSuccess_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpSuccess_dialog.dismiss(); // 다이얼로그 닫기

                // 로그인 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}