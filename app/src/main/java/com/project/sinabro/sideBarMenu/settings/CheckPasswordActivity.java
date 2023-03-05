package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityCheckPasswordBinding;
import com.project.sinabro.textWatcher.EmailWatcher;
import com.project.sinabro.textWatcher.PasswordWatcher;

public class CheckPasswordActivity extends AppCompatActivity {

    private ActivityCheckPasswordBinding binding;

    private Intent intent;

    private Boolean password_toggle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckPasswordBinding.inflate(getLayoutInflater());
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
                } else {
                    /* 이곳에 API 호출 코드가 추가되어야 합니다. */

                    // 비밀번호 확인 실패 case
                    // binding.signInFailedTv.setVisibility(View.VISIBLE);

                    intent = getIntent();
                    String destActivityName = intent.getStringExtra("destActivityName");
                    if (destActivityName.equals("MyPageActivity")) {
                        // "정보 수정" 액티비티로 이동
                        final Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
                        startActivity(intent);
                    } else if (destActivityName.equals("WithdrawalStep1Activity")) {
                        // "회원탈퇴 단계(1)" 액티비티로 이동
                        final Intent intent = new Intent(getApplicationContext(), WithdrawalStep1Activity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}