package com.project.sinabro.bottomSheet.place;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.project.sinabro.R;

public class AddBookmarkPlaceActivity extends AppCompatActivity {
    private Button addPlace_btn;
    private ImageButton back_iBtn;
    private TextView placeRemove_tv;
    private TextInputEditText placeName_editText, detailAddress_editText;
    private Dialog placeRemove_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bookmark_place_activity);

        // 뒤로가기 버튼 기능
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });
    }
}
