package com.project.sinabro.sideBarMenu.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivitySignInBinding;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.textWatcher.PasswordWatcher;

public class SignIn extends AppCompatActivity {

    private ActivitySignInBinding binding;

    private Boolean password_toggle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** TextInputLayout helper 생성 관련 코드 */
        binding.emailTextInputLayout.getEditText().addTextChangedListener(new EmailWatcher(binding.emailTextInputLayout, getResources().getString(R.string.sign_in_email_failed), "SignIn"));
        binding.passwordTextInputLayout.getEditText().addTextChangedListener(new PasswordWatcher(binding.passwordTextInputLayout, getResources().getString(R.string.sign_in_password_failed)));

        /** "회원가입" textView 클릭 시 회원가입 액티비티로 이동하는 코드 */
        binding.signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), SignUpStep1.class);
                startActivity(intent);
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

        /** "비밀번호 찾기" textView 클릭 시 비밀번호 초기화 액티비티로 이동하는 코드 */
        binding.resetPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
            }
        });

        /** "로그인" 버튼 클릭 시 이벤트 처리 코드 */
        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 입력 란 검증 실패 및 공란 확인 조건식
                if (String.valueOf(binding.emailEditText.getText()).equals("")) {
                    binding.emailEditText.requestFocus();
                    binding.emailTextInputLayout.setError(getResources().getString(R.string.sign_in_email_failed));
                    binding.emailTextInputLayout.setErrorEnabled(true);
                    binding.emailTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else if (String.valueOf(binding.passwordEditText.getText()).equals("")) {
                    binding.passwordEditText.requestFocus();
                    binding.passwordTextInputLayout.setError(getResources().getString(R.string.sign_in_password_failed));
                    binding.passwordTextInputLayout.setErrorEnabled(true);
                    binding.passwordTextInputLayout.setBackgroundResource(R.drawable.edt_bg_only_helper_selected);
                } else {
                    /* 이곳에 API 호출 코드가 추가되어야 합니다. */
                    binding.signInFailedTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}