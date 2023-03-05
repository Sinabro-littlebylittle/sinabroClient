package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityWithdrawalStep2Binding;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastSuccess;

public class WithdrawalStep2Activity extends AppCompatActivity {

    private ActivityWithdrawalStep2Binding binding;

    private Dialog askAcceptWithdrawal_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawalStep2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** "회원 탈퇴 질문 안내" 다이얼로그 변수 초기화 및 설정 */
        askAcceptWithdrawal_dialog = new Dialog(WithdrawalStep2Activity.this);  // Dialog 초기화
        askAcceptWithdrawal_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        askAcceptWithdrawal_dialog.setContentView(R.layout.dialog_ask_accept_withdrawal); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        askAcceptWithdrawal_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        binding.checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_accept_withdrawal();
            }
        });
    }

    /**
     * (dialog_ask_accept_withdrawal) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_accept_withdrawal() {
        askAcceptWithdrawal_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        askAcceptWithdrawal_dialog.getWindow().setDimAmount(0.35f);

        // "아니오" 버튼
        Button noBtn = askAcceptWithdrawal_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askAcceptWithdrawal_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼
        askAcceptWithdrawal_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askAcceptWithdrawal_dialog.dismiss(); // 다이얼로그 닫기
                new ToastSuccess(getResources().getString(R.string.toast_withdrawal_success), WithdrawalStep2Activity.this);
                // 로그인 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}