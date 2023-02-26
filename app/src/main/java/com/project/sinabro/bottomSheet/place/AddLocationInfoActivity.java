package com.project.sinabro.bottomSheet.place;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.project.sinabro.R;

public class AddLocationInfoActivity extends AppCompatActivity {

    private Button addPlace_btn;
    private ImageButton back_iBtn;
    private TextView placeRemove_tv;
    private TextInputEditText placeName_editText, detailAddress_editText;
    private Dialog placeRemove_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_info);

        // 뒤로가기 버튼 기능
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        final Intent intent = getIntent();
        Boolean modify_clicked = intent.getBooleanExtra("modify_clicked", false);

        placeRemove_dialog = new Dialog(AddLocationInfoActivity.this);  // Dialog 초기화
        placeRemove_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        placeRemove_dialog.setContentView(R.layout.dialog_place_remove); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        placeRemove_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        placeRemove_tv = findViewById(R.id.placeRemove_tv);
        if (modify_clicked) placeRemove_tv.setVisibility(View.VISIBLE);
        placeRemove_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_place_remove();
            }
        });

        placeName_editText = findViewById(R.id.placeName_editText);
        detailAddress_editText = findViewById(R.id.detailAddress_editText);

        if (modify_clicked) {
            String placeName_value = intent.getStringExtra("placeName_value");
            String detailAddress_value = intent.getStringExtra("detailAddress_value");

            placeName_editText.setText(placeName_value);
            detailAddress_editText.setText(detailAddress_value);
        }

        addPlace_btn = findViewById(R.id.addPlace_btn);
        addPlace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), PlaceListActivity.class);
                if (placeName_editText.getText().toString().equals("") || detailAddress_editText.getText().toString().equals("")) {
                    intent.putExtra("input_value", false);
                    startActivity(intent);
                } else {
                    intent.putExtra("input_value", true);
                    intent.putExtra("placeName_value", placeName_editText.getText().toString());
                    intent.putExtra("detailAddress_value", detailAddress_editText.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

    // (dialog_place_remove) 다이얼로그를 디자인하는 함수
    public void showDialog_place_remove() {
        placeRemove_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        placeRemove_dialog.getWindow().setDimAmount(0.35f);

        // 아니오 버튼
        Button noBtn = placeRemove_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeRemove_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 네 버튼
        placeRemove_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeRemove_dialog.dismiss(); // 다이얼로그 닫기
                final Intent intent = new Intent(getApplicationContext(), PlaceListActivity.class);
                startActivity(intent);
            }
        });
    }
}