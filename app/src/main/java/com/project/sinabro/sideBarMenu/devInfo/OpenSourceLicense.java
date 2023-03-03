package com.project.sinabro.sideBarMenu.devInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityOpenSourceLicenseBinding;
import com.project.sinabro.databinding.ActivitySignInBinding;

public class OpenSourceLicense extends AppCompatActivity {

    private ActivityOpenSourceLicenseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenSourceLicenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        /** MIT License 레이블 하단의 url 클릭 시 sinabroClient repo 내
         *  LICENSE 파일이 존재하는 인터넷 페이지 로드 */
        binding.mitLicenseTvUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.mit_license_url)));
                startActivity(browserIntent);
            }
        });
    }
}