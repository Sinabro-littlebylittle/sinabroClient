package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.project.sinabro.databinding.ActivityWithdrawalStep1Binding;

public class WithdrawalStep1Activity extends AppCompatActivity {

    private ActivityWithdrawalStep1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawalStep1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        binding.dontUseAppRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원탈퇴 단계(2) 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), WithdrawalStep2Activity.class);
                intent.putExtra("withdrawalReason", binding.dontUseAppTv.getText().toString());
                startActivity(intent);
            }
        });

        binding.securityConcernRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원탈퇴 단계(2) 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), WithdrawalStep2Activity.class);
                intent.putExtra("withdrawalReason", binding.securityConcernTv.getText().toString());
                startActivity(intent);
            }
        });

        binding.etcRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원탈퇴 단계(2) 액티비티로 이동
                final Intent intent = new Intent(getApplicationContext(), WithdrawalStep2Activity.class);
                intent.putExtra("withdrawalReason", binding.etcTv.getText().toString());
                startActivity(intent);
            }
        });
    }
}