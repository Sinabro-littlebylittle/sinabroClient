package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityCheckPasswordBinding;
import com.project.sinabro.models.requests.LoginRequest;
import com.project.sinabro.retrofit.interfaceAPIs.AuthAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.textWatcher.PasswordWatcher;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckPasswordActivity extends AppCompatActivity {

    private ActivityCheckPasswordBinding binding;

    private Intent intent;

    private Boolean password_toggle = true;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private AuthAPI authAPI;

    private static String departActivityName = "", destActivityName;
    private static Boolean exitActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        authAPI = retrofitService.getRetrofit().create(AuthAPI.class);

        intent = getIntent();
        exitActivity = intent.getBooleanExtra("exitCheckPasswordActivity", false);

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.passwordTextInputLayout.getEditText().addTextChangedListener(new PasswordWatcher(binding.passwordTextInputLayout, getResources().getString(R.string.sign_in_password_failed)));

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

        /** "확인" 버튼 클릭 시 특정 액티비티로 이동하는 코드  */
        binding.checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (String.valueOf(binding.passwordEditText.getText()).equals("")) {
                    binding.passwordEditText.requestFocus();
                    binding.passwordTextInputLayout.setError(getResources().getString(R.string.sign_in_password_failed));
                    binding.passwordTextInputLayout.setErrorEnabled(true);
                    binding.passwordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(tokenManager.getEmail(), binding.passwordEditText.getText().toString());
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
                                destActivityName = intent.getStringExtra("destActivityName");
                                departActivityName = intent.getStringExtra("departActivityName");
                                if (destActivityName.equals("MyPageActivity")) {
                                    // "정보 수정" 액티비티로 이동
                                    final Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                                    intent.putExtra("departActivityName", departActivityName);
                                    startActivity(intent);
                                } else if (destActivityName.equals("WithdrawalStep1Activity")) {
                                    // "회원탈퇴 단계(1)" 액티비티로 이동
                                    final Intent intent = new Intent(getApplicationContext(), WithdrawalStep1Activity.class);
                                    startActivity(intent);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // 로그인 실패
                            switch (response.code()) {
                                case 400:
                                    new ToastWarning(getResources().getString(R.string.toast_bad_request), CheckPasswordActivity.this);
                                    // "로그인" 액티비티로 이동
                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    break;
                                case 401:
                                    binding.signInFailedTv.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), CheckPasswordActivity.this);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), CheckPasswordActivity.this);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exitActivity) {
            finish(); // 현재 액티비티 종료
        }
    }
}