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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.project.sinabro.R;

public class AddBookmarkPlaceActivity extends AppCompatActivity {

    private ImageButton back_iBtn;
    private Button colorPicker_button;
    private AppCompatButton addlistbtn;
    private Dialog addbookmarklist_dialog;
    private RadioButton radioButton_open, radioButton_close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bookmark_place_activity);

        // 취소 버튼
        back_iBtn = findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_place_remove();
            }
        });

        final Intent intent = getIntent();
        Boolean modify_clicked = intent.getBooleanExtra("modify_clicked", false);


        addbookmarklist_dialog = new Dialog(AddBookmarkPlaceActivity.this);     // dialog 초기화
        addbookmarklist_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);          // 타이틀 제거
        addbookmarklist_dialog.setContentView(R.layout.dialog_bookmarklist_remove);    // 커스텀 다이로그 레이아웃 연결
        addbookmarklist_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        /*컬러 버튼*/
        colorPicker_button = findViewById(R.id.colorPicker_button);
        colorPicker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        /*확인 버튼*/
        addlistbtn = findViewById(R.id.addlistbtn);
        addlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        /*공개 비공개*/

        /*세부 내용*/
    }

    /*다이어로그 내용 */
    public void showDialog_place_remove() {
        addbookmarklist_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        addbookmarklist_dialog.getWindow().setDimAmount(0.35f);

        // 아니오 버튼
        Button noBtn = addbookmarklist_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addbookmarklist_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 네 버튼
        addbookmarklist_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addbookmarklist_dialog.dismiss(); // 다이얼로그 닫기
                final Intent intent = new Intent(getApplicationContext(), AddBookmarkPlaceActivity.class);
                startActivity(intent);
            }
        });
    }
}
