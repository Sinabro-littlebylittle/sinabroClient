package com.project.sinabro.sideBarMenu.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.project.sinabro.databinding.ActivityStoreUserInformationBinding;

public class StoreUserInformationActivity extends AppCompatActivity {

    private ActivityStoreUserInformationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoreUserInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });
    }
}