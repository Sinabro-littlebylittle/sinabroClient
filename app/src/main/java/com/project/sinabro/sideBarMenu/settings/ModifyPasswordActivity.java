package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityModifyPasswordBinding;
import com.project.sinabro.models.requests.ChangePasswordRequest;
import com.project.sinabro.models.requests.LoginRequest;
import com.project.sinabro.retrofit.interfaceAPIs.AuthAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.UserAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.textWatcher.PasswordConfirmWatcher;
import com.project.sinabro.textWatcher.PasswordWatcher;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyPasswordActivity extends AppCompatActivity {

    private ActivityModifyPasswordBinding binding;

    private Boolean current_password_toggle = true, password_toggle = true, passwordConfirm_toggle = true;

    private Dialog modify_password_failed_dialog;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    AuthAPI authAPI;
    UserAPI userAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        authAPI = retrofitService.getRetrofit().create(AuthAPI.class);
        userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        /** "비밀번호 변경 실패 안내" 다이얼로그 변수 초기화 및 설정 */
        modify_password_failed_dialog = new Dialog(ModifyPasswordActivity.this);  // Dialog 초기화
        modify_password_failed_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        modify_password_failed_dialog.setContentView(R.layout.dialog_modify_password_failed); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        modify_password_failed_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.currentPasswordTextInputLayout.getEditText().addTextChangedListener(new PasswordWatcher(binding.currentPasswordTextInputLayout, getResources().getString(R.string.sign_in_password_failed)));
        binding.passwordTextInputLayout.getEditText().addTextChangedListener(new PasswordWatcher(binding.passwordTextInputLayout, getResources().getString(R.string.sign_up_password)));
        binding.passwordConfirmTextInputLayout.getEditText().addTextChangedListener(new PasswordConfirmWatcher(binding.passwordTextInputLayout, binding.passwordConfirmTextInputLayout, getResources().getString(R.string.sign_up_step1_password_confirm_validation_failed)));

        /** "현재 비밀번호 입력 란" 비밀번호 show/hidden 아이콘 클릭 시 */
        binding.currentPasswordTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_password_toggle) {
                    binding.currentPasswordEditText.setTransformationMethod(null);
                    binding.currentPasswordEditText.setPadding(34, 50, 0, 25);
                } else
                    binding.currentPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
                current_password_toggle = !current_password_toggle;
            }
        });

        /** "비밀번호 입력 란" 비밀번호 show/hidden 아이콘 클릭 시 */
        binding.passwordTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password_toggle) {
                    binding.passwordEditText.setTransformationMethod(null);
                    binding.passwordEditText.setPadding(34, 50, 0, 25);
                } else
                    binding.passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                password_toggle = !password_toggle;
            }
        });

        /** "비밀번호 재확인 입력 란" 비밀번호 show/hidden 아이콘 클릭 시 */
        binding.passwordConfirmTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordConfirm_toggle) {
                    binding.passwordConfirmEditText.setTransformationMethod(null);
                    binding.passwordConfirmEditText.setPadding(34, 50, 0, 25);
                } else
                    binding.passwordConfirmEditText.setTransformationMethod(new PasswordTransformationMethod());
                passwordConfirm_toggle = !passwordConfirm_toggle;
            }
        });

        binding.modifyCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (binding.currentPasswordTextInputLayout.getError() != null || String.valueOf(binding.currentPasswordEditText.getText()).equals("")) {
                    binding.currentPasswordEditText.requestFocus();
                    binding.currentPasswordTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_password_validation_failed));
                    binding.currentPasswordTextInputLayout.setErrorEnabled(true);
                    binding.currentPasswordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (String.valueOf(binding.currentPasswordEditText.getText()).equals(String.valueOf(binding.passwordEditText.getText()))) {
                    binding.passwordEditText.requestFocus();
                    binding.passwordTextInputLayout.setError(getResources().getString(R.string.current_and_new_password_same));
                    binding.passwordTextInputLayout.setErrorEnabled(true);
                    binding.passwordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (binding.passwordTextInputLayout.getError() != null || String.valueOf(binding.passwordEditText.getText()).equals("")) {
                    binding.passwordEditText.requestFocus();
                    binding.passwordTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_password_validation_failed));
                    binding.passwordTextInputLayout.setErrorEnabled(true);
                    binding.passwordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (!String.valueOf(binding.passwordEditText.getText()).equals(String.valueOf(binding.passwordConfirmEditText.getText()))) {
                    binding.passwordConfirmEditText.requestFocus();
                    binding.passwordConfirmTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_current_password_validation_failed));
                    binding.passwordConfirmTextInputLayout.setErrorEnabled(true);
                    binding.passwordConfirmTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if ((binding.passwordConfirmTextInputLayout.getError() != null || String.valueOf(binding.passwordConfirmEditText.getText()).equals("")) && !String.valueOf(binding.passwordEditText.getText()).equals(String.valueOf(binding.passwordConfirmEditText.getText()))) {
                    binding.passwordConfirmEditText.requestFocus();
                    binding.passwordConfirmTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_password_confirm_validation_failed));
                    binding.passwordConfirmTextInputLayout.setErrorEnabled(true);
                    binding.passwordConfirmTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else {
                    LoginRequest loginRequest = new LoginRequest(tokenManager.getEmail(), binding.currentPasswordEditText.getText().toString());
                    Call<ResponseBody> call = authAPI.login(loginRequest);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                // 로그인 성공, 서버로부터 받은 JWT 토큰을 TokenManager에 저장
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String accessToken = jsonObject.optString("accessToken");
                                    TokenManager.getInstance(getApplicationContext()).saveToken(accessToken);

                                    Call<ResponseBody> call_change_password = userAPI.changePassword(new ChangePasswordRequest(binding.passwordEditText.getText().toString()));
                                    call_change_password.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful()) {
                                                new ToastSuccess(getResources().getString(R.string.toast_modify_password_success), ModifyPasswordActivity.this);
                                                onBackPressed(); // 뒤로가기 기능 수행
                                                finish(); // 현재 액티비티 종료
                                            } else {
                                                switch (response.code()) {
                                                    case 400:
                                                        new ToastWarning(getResources().getString(R.string.toast_bad_request), ModifyPasswordActivity.this);
                                                        break;
                                                    case 401:
                                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), ModifyPasswordActivity.this);
                                                        // "로그인" 액티비티로 이동
                                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                        startActivity(intent);
                                                        break;
                                                    default:
                                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), ModifyPasswordActivity.this);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                                            new ToastWarning(getResources().getString(R.string.toast_server_error), ModifyPasswordActivity.this);
                                        }
                                    });
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // 로그인 실패
                                switch (response.code()) {
                                    case 400:
                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), ModifyPasswordActivity.this);
                                        // "로그인" 액티비티로 이동
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 401:
                                        showDialog_modify_password_failed();
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), ModifyPasswordActivity.this);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), ModifyPasswordActivity.this);
                        }
                    });
                }
            }
        });
    }

    /**
     * (dialog_modify_password_failed) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_modify_password_failed() {
        modify_password_failed_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        modify_password_failed_dialog.getWindow().setDimAmount(0.35f);

        // "확인" 버튼
        modify_password_failed_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify_password_failed_dialog.dismiss(); // 다이얼로그 닫기
                binding.currentPasswordEditText.requestFocus();
                binding.currentPasswordTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_current_password_validation_failed));
                binding.currentPasswordTextInputLayout.setErrorEnabled(true);
                binding.currentPasswordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
            }
        });
    }
}