package com.project.sinabro.sideBarMenu.bookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.AddBookmarkToListActivity;
import com.project.sinabro.bottomSheet.place.AddLocationInfoActivity;
import com.project.sinabro.bottomSheet.place.PlaceItem;
import com.project.sinabro.bottomSheet.place.RemoveBookmarkFromListActivity;
import com.project.sinabro.databinding.ActivityPlaceInListBinding;

import java.util.ArrayList;

public class PlaceInListActivity extends AppCompatActivity {

    private ActivityPlaceInListBinding binding;

    private ListViewAdapter adapter;

    private Dialog placeInfo_dialog, ask_add_or_cancel_bookmark_dialog;

    private PlaceItem clickedPlaceItem;

    private Button bookmarkFilled_btn;

    Boolean bookmarked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaceInListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** "장소정보 확인" 다이얼로그 변수 초기화 및 설정 */
        placeInfo_dialog = new Dialog(PlaceInListActivity.this);  // Dialog 초기화
        placeInfo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        placeInfo_dialog.setContentView(R.layout.dialog_place_info); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        placeInfo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** "즐겨찾기 추가/취소 확인" 다이얼로그 변수 초기화 및 설정 */
        ask_add_or_cancel_bookmark_dialog = new Dialog(PlaceInListActivity.this);  // Dialog 초기화
        ask_add_or_cancel_bookmark_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        ask_add_or_cancel_bookmark_dialog.setContentView(R.layout.dialog_ask_add_or_cancel_bookmark); // xml 레이아웃 파일과 연결
        // dialog 창의 root 레이아웃을 투명하게 조절 모서리(코너)를 둥글게 보이게 하기 위해
        ask_add_or_cancel_bookmark_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /** 뒤로가기 버튼 기능 */
        binding.backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });

        adapter = new ListViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new PlaceItem("충북대학교 중앙도서관", "3층", 5));
        adapter.addItem(new PlaceItem("양성재 학생생활관", "식당", 1));
        adapter.addItem(new PlaceItem("충북대학교가나다라마바사아자", "205호", 10));
        adapter.addItem(new PlaceItem("가나다라마바사 아자차카타하가", "205호", 100));
        adapter.addItem(new PlaceItem("가나다라마바사아자차카 타하가", "205호", -1));
        adapter.addItem(new PlaceItem("가나다라마바  사아자차카타하", "205호", -1));
        adapter.addItem(new PlaceItem("샘마루(SAMMaru)", "113호", -1));
        adapter.addItem(new PlaceItem("충북대학교 소프트웨어학부", "205호", -1));
        adapter.addItem(new PlaceItem("충북대학교 소프트웨어학부", "203호", -1));

//        final Intent intent = getIntent();
//        Boolean input_value = intent.getBooleanExtra("input_value", false);
//
//        if (input_value) {
//            String placeName_value = intent.getStringExtra("placeName_value");
//            String detailAddress_value = intent.getStringExtra("detailAddress_value");
//
//            adapter.addItem(new PlaceItem(placeName_value, detailAddress_value, -1));
//        }

        // 리스트뷰에 Adapter 설정
        binding.placeListView.setAdapter(adapter);

        final Intent intent = getIntent();
        binding.listNameTv.setText(intent.getStringExtra("listName"));
    }

    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<PlaceItem> items = new ArrayList<PlaceItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(PlaceItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final PlaceItem placeItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.place_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView placeName_tv = convertView.findViewById(R.id.placeName_tv);
            TextView placeInfo_tv = convertView.findViewById(R.id.placeInfo_tv);
            TextView peopleCount_tv = convertView.findViewById(R.id.peopleCount_tv);

            placeName_tv.setText(placeItem.getPlaceName());
            placeInfo_tv.setText(placeItem.getDetailAddress());
            if (placeItem.getPeopleCnt() > 0) {
                peopleCount_tv.setText("" + placeItem.getPeopleCnt());
            } else {
                peopleCount_tv.setText("?");
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog_placeInfo(placeItem);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

    /**
     * (dialog_ask_add_or_cancel_bookmark) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_add_or_delete_bookmark() {
        ask_add_or_cancel_bookmark_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        ask_add_or_cancel_bookmark_dialog.getWindow().setDimAmount(0.35f);

        Button bookmarkFilled_btn = findViewById(R.id.bookmarkFilled_btn);
        TextView dialog_tv = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.dialog_tv);
        if (bookmarkFilled_btn.getVisibility() == View.VISIBLE) {
            dialog_tv.setText(getResources().getString(R.string.dialog_cancel_bookmark));
            bookmarked = true;
        } else {
            dialog_tv.setText(getResources().getString(R.string.dialog_add_bookmark));
            bookmarked = false;
        }

        // "아니오" 버튼
        Button noBtn = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // "확인" 버튼
        Button yesBtn = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기
                if (bookmarked) {
                    final Intent intent = new Intent(getApplicationContext(), RemoveBookmarkFromListActivity.class);
                    startActivity(intent);
                } else {
                    final Intent intent = new Intent(getApplicationContext(), AddBookmarkToListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // (dialog_place_info) 다이얼로그를 디자인하는 함수
    public void showDialog_placeInfo(PlaceItem placeItem) {
        clickedPlaceItem = placeItem;
        placeInfo_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        placeInfo_dialog.getWindow().setDimAmount(0.35f);

        TextView placeName_tv = placeInfo_dialog.findViewById(R.id.placeName_tv);
        TextView peopleCount_tv = placeInfo_dialog.findViewById(R.id.peopleCount_tv);
        TextView roadNameAddress_tv = placeInfo_dialog.findViewById(R.id.roadNameAddress_tv);
        TextView detailAddress_tv = placeInfo_dialog.findViewById(R.id.detailAddress_tv);

        placeName_tv.setText(placeItem.getPlaceName());
        if (placeItem.getPeopleCnt() > 0) {
            peopleCount_tv.setText("" + placeItem.getPeopleCnt());
        } else {
            peopleCount_tv.setText("?");
        }
        roadNameAddress_tv.setText(roadNameAddress_tv.getText().toString());
        detailAddress_tv.setText(placeItem.getDetailAddress());

        // Dialog 닫기 버튼
        ImageButton dialogClose_iBtn = placeInfo_dialog.findViewById(R.id.dialogClose_iBtn);
        dialogClose_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 카메라 스캔 버튼
        Button peopleScan_btn = placeInfo_dialog.findViewById(R.id.peopleScan_btn);
        peopleScan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        // 장소 수정 버튼
        Button editLocation_btn = placeInfo_dialog.findViewById(R.id.editLocation_btn);
        editLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfo_dialog.dismiss(); // 다이얼로그 닫기
                final Intent intent = new Intent(getApplicationContext(), AddLocationInfoActivity.class);

                intent.putExtra("modify_clicked", true);
                intent.putExtra("placeName_value", placeName_tv.getText().toString());
                intent.putExtra("detailAddress_value", detailAddress_tv.getText().toString());

                startActivity(intent);
            }
        });

        // 즐겨찾기 버튼
        Button bookmarkEmpty_btn = placeInfo_dialog.findViewById(R.id.bookmarkEmpty_btn);
        bookmarkFilled_btn = placeInfo_dialog.findViewById(R.id.bookmarkFilled_btn);

        if (bookmarked) {
            bookmarkEmpty_btn.setVisibility(View.GONE);
            bookmarkFilled_btn.setVisibility(View.VISIBLE);
        } else {
            bookmarkEmpty_btn.setVisibility(View.VISIBLE);
            bookmarkFilled_btn.setVisibility(View.GONE);
        }

        bookmarkEmpty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_add_or_delete_bookmark();
            }
        });

        bookmarkFilled_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_ask_add_or_delete_bookmark();
            }
        });
    }
}