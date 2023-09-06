package com.project.sinabro.sideBarMenu.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityResetPasswordBinding;
import com.project.sinabro.models.UserInfo;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.UserAPI;
import com.project.sinabro.sideBarMenu.settings.MyPageActivity;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    private Dialog resetPasswordSuccess_dialog;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        /** "비밀번호 초기화 완료 안내" 다이얼로그 변수 초기화 및 설정 */
        resetPasswordSuccess_dialog = new Dialog(ResetPasswordActivity.this);  // Dialog 초기화
        resetPasswordSuccess_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        resetPasswordSuccess_dialog.setContentView(R.layout.dialog_reset_password_success); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        resetPasswordSuccess_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.emailTextInputLayout.getEditText().addTextChangedListener(new EmailWatcher(binding.emailTextInputLayout, binding.emailEditText, getResources().getString(R.string.sign_in_email_failed), "ResetPassword"));

        /** "초기화" 버튼 클릭 시 이벤트 처리 코드 */
        binding.passwordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (binding.emailTextInputLayout.getError() != null || String.valueOf(binding.emailEditText.getText()).equals("")) {
                    binding.emailEditText.requestFocus();
                    binding.emailTextInputLayout.setError(getResources().getString(R.string.sign_in_email_failed));
                    binding.emailTextInputLayout.setErrorEnabled(true);
                    binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                    return;
                }

                binding.resetPasswordFailedTv.setVisibility(View.GONE);

                UserInfo userInfo = new UserInfo();
                userInfo.setEmail(binding.emailEditText.getText().toString());

                Call<UserInfo> call_userAPI_generateTempPassword = userAPI.generateTempPassword(userInfo);
                call_userAPI_generateTempPassword.enqueue(new Callback<UserInfo>() {
                    @Override
                    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                        if (response.isSuccessful()) {
                            // 입력된 이메일이 정상적으로 DB에서 조회되었을 때 다이얼로그 띄우기
                            showDialog_reset_password_success();
                        } else {
                            switch (response.code()) {
                                case 400:
                                case 404:
                                    binding.resetPasswordFailedTv.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), ResetPasswordActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfo> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), ResetPasswordActivity.this);
                    }
                });
            }
        });
    }

    /**
     * (dialog_reset_password_success) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_reset_password_success() {
        resetPasswordSuccess_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        resetPasswordSuccess_dialog.getWindow().setDimAmount(0.35f);

        // "확인" 버튼 클릭 시 이벤트 처리 코드
        resetPasswordSuccess_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordSuccess_dialog.dismiss(); // 다이얼로그 닫기

                // 로그인 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}