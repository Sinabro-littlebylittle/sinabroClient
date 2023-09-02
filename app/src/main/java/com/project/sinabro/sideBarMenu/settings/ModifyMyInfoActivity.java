package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityModifyMyInfoBinding;
import com.project.sinabro.models.UserInfo;
import com.project.sinabro.retrofit.interfaceAPIs.AuthAPI;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.UserAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.textWatcher.NicknameWatcher;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyMyInfoActivity extends AppCompatActivity {

    private ActivityModifyMyInfoBinding binding;

    public static Boolean emailConfirm;

    private String username, email;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private AuthAPI authAPI;
    UserAPI userAPI;

    private Intent intent;

    @Override
    public void onBackPressed() {
        // 프로그래머가 원하는 액티비티로 이동
        Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        retrofitService = new RetrofitService(tokenManager);
        authAPI = retrofitService.getRetrofit().create(AuthAPI.class);
        userAPI = retrofitService.getRetrofit().create(UserAPI.class);

        intent = getIntent();
        String departActivityName = intent.getStringExtra("departActivityName");

        emailConfirm = true;

        final Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        binding.usernameEditText.setText(username);
        binding.emailEditText.setText(email);

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                intent.putExtra("departActivityName", departActivityName);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.nicknameTextInputLayout.getEditText().addTextChangedListener(new NicknameWatcher(binding.nicknameTextInputLayout, getResources().getString(R.string.sign_up_nickname_failed)));
        binding.emailTextInputLayout.getEditText().addTextChangedListener(new EmailWatcher(binding.emailTextInputLayout, binding.emailConfirmResultTv, getResources().getString(R.string.sign_up_email), "ModifyMyInfoActivity"));

        /** 이메일 내용 지우기 X 버튼 클릭 시  */
        binding.emailTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something when the end icon is clicked
                emailConfirm = false;
                binding.emailEditText.setText("");
                binding.emailConfirmResultTv.setVisibility(View.GONE);
            }
        });

        /** 이메일 확인 버튼 클릭 시 이벤트 처리 코드 */
        binding.emailConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<ResponseBody> call = authAPI.checkExistEmail(binding.emailEditText.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // 입력된 이메일이 DB 내에 존재하지 않을 때 (사용 가능) 관련 문구 표기하기
                            binding.emailTextInputLayout.setErrorEnabled(false);
                            binding.emailConfirmResultTv.setTextColor(getResources().getColor(R.color.blue));
                            binding.emailConfirmResultTv.setText(getResources().getString(R.string.sign_up_email_confirm_success));
                            binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_selector);
                            binding.emailTextInputLayout.setPadding(-34, 20, 0, 20);
                            emailConfirm = true;
                        } else {
                            binding.emailConfirmResultTv.setTextColor(getResources().getColor(R.color.red));
                            switch (response.code()) {
                                case 400:
                                    // 입력된 이메일이 올바르지 않은 이메일 형식을 경우에 경고 관련 문구 표시하기
                                    binding.emailConfirmResultTv.setText(getResources().getString(R.string.sign_up_email_invalid_format));
                                    break;
                                case 409:
                                    // 입력된 이메일이 DB 내에 이미 존재할 때 (사용 불가) 관련 문구 표기하기
                                    binding.emailConfirmResultTv.setTextColor(getResources().getColor(R.color.red));
                                    binding.emailConfirmResultTv.setText(getResources().getString(R.string.sign_up_email_already_exist));
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), ModifyMyInfoActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), ModifyMyInfoActivity.this);
                    }
                });

                binding.emailConfirmResultTv.setVisibility(View.VISIBLE);
                binding.emailEditText.clearFocus();
            }
        });

        binding.modifyCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (String.valueOf(binding.usernameEditText.getText()).equals("")) {
                    binding.usernameEditText.requestFocus();
                    binding.nicknameTextInputLayout.setError(getResources().getString(R.string.sign_up_nickname_failed));
                    binding.nicknameTextInputLayout.setErrorEnabled(true);
                    binding.nicknameTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (!emailConfirm || String.valueOf(binding.emailEditText.getText()).equals("")) {
                    binding.emailEditText.requestFocus();
                    binding.emailTextInputLayout.setError(getResources().getString(R.string.sign_up_step1_email_validation_failed));
                    binding.emailTextInputLayout.setErrorEnabled(true);
                    binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsername(binding.usernameEditText.getText().toString());
                    userInfo.setEmail(binding.emailEditText.getText().toString());

                    Call<ResponseBody> call_changeUserSelfInfo = userAPI.changeUserSelfInfo(userInfo);
                    call_changeUserSelfInfo.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                new ToastSuccess(getResources().getString(R.string.toast_modify_my_info_success), ModifyMyInfoActivity.this);
                                Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                                intent.putExtra("departActivityName", departActivityName);
                                startActivity(intent);
                                finish(); // 현재 액티비티 종료
                            } else {
                                switch (response.code()) {
                                    case 400:
                                        new ToastWarning(getResources().getString(R.string.toast_bad_request), ModifyMyInfoActivity.this);
                                        break;
                                    case 401:
                                        new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), ModifyMyInfoActivity.this);
                                        // "로그인" 액티비티로 이동
                                        final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:
                                        new ToastWarning(getResources().getString(R.string.toast_none_status_code), ModifyMyInfoActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                            new ToastWarning(getResources().getString(R.string.toast_server_error), ModifyMyInfoActivity.this);
                        }
                    });
                }
            }
        });
    }
}