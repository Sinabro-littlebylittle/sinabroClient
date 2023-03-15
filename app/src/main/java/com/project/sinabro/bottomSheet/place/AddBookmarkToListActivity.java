package com.project.sinabro.bottomSheet.place;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityAddBookmarkToListBinding;
import com.project.sinabro.sideBarMenu.bookmark.AddNewListActivity;
import com.project.sinabro.sideBarMenu.bookmark.ListItem;
import com.project.sinabro.sideBarMenu.bookmark.PlaceInListActivity;
import com.project.sinabro.sideBarMenu.bookmark.RemoveListActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;

import java.util.ArrayList;

public class AddBookmarkToListActivity extends AppCompatActivity {

    private ActivityAddBookmarkToListBinding binding;

    private ListViewAdapter adapter;

    private Dialog ask_add_or_cancel_bookmark_dialog;

    int checked_toggle_cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookmarkToListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /** "즐겨찾기 추가/취소 확인" 다이얼로그 변수 초기화 및 설정 */
        ask_add_or_cancel_bookmark_dialog = new Dialog(AddBookmarkToListActivity.this);  // Dialog 초기화
        ask_add_or_cancel_bookmark_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        ask_add_or_cancel_bookmark_dialog.setContentView(R.layout.dialog_ask_accept_add_or_remove_list); // xml 레이아웃 파일과 연결
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

        /** "리스트 추가" TextView */
        binding.addNewListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent prevIntent = getIntent();
                Boolean calledView = prevIntent.getBooleanExtra("fromPlaceListActivity", false);
                final Intent intent = new Intent(getApplicationContext(), AddNewListActivity.class);
                intent.putExtra("fromBottomSheetDialog", true);
                if (calledView)
                    intent.putExtra("fromPlaceListActivity", true);
                startActivity(intent);
                finish();
            }
        });

        /** "추가하기" 버튼 기능 */
        binding.addBookmarkToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked_toggle_cnt == 0) {
                    new ToastWarning(getResources().getString(R.string.toast_remove_list_failed), AddBookmarkToListActivity.this);
                    return;
                }

                // dialog_ask_accept_remove_list 다이얼로그 창 띄우기
                showDialog_ask_accept_add_or_remove_list();
            }
        });

        adapter = new ListViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new ListItem(R.color.num1, "청주 맛집"));
        adapter.addItem(new ListItem(R.color.num2, "충북대학교"));
        adapter.addItem(new ListItem(R.color.num3, "나중에 갈 곳"));
        adapter.addItem(new ListItem(R.color.num4, "기타"));

        final Intent intent = getIntent();
        int newListIconColor = intent.getIntExtra("newListIconColor", -1);
        if (newListIconColor != -1) {
            String newListName = intent.getStringExtra("newListName");
            adapter.addItem(new ListItem(newListIconColor, newListName));
        }

        // 리스트뷰에 Adapter 설정
        binding.removeListListView.setAdapter(adapter);
    }

    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<ListItem> items = new ArrayList<ListItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(ListItem item) {
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
            final ListItem listItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checkbox_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView listName_tv = convertView.findViewById(R.id.listName_tv);
            listName_tv.setText(listItem.getListName());

            RoundedImageView list_color_circle_roundedImageView = convertView.findViewById(R.id.list_color_circle_roundedImageView);
            list_color_circle_roundedImageView.setImageResource(listItem.getListIconColorValue());

            final Boolean[] check_policies_toggle = {false};

            RoundedImageView check_circle_roundedImageView = convertView.findViewById(R.id.check_circle_roundedImageView);
            check_circle_roundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (check_policies_toggle[0]) {
                        check_circle_roundedImageView.setImageResource(R.drawable.check_circle_empty);
                        checked_toggle_cnt--;
                    } else {
                        check_circle_roundedImageView.setImageResource(R.drawable.check_circle_green);
                        checked_toggle_cnt++;
                    }
                    check_policies_toggle[0] = !check_policies_toggle[0];
                }
            });

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(getApplicationContext(), PlaceInListActivity.class);
                    intent.putExtra("listName", listName_tv.getText().toString());
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

    /**
     * (dialog_ask_accept_remove_list) 다이얼로그를 디자인하는 함수
     */
    public void showDialog_ask_accept_add_or_remove_list() {
        ask_add_or_cancel_bookmark_dialog.show(); // 다이얼로그 띄우기
        // 다이얼로그 창이 나타나면서 외부 액티비티가 어두워지는데, 그 정도를 조절함
        ask_add_or_cancel_bookmark_dialog.getWindow().setDimAmount(0.35f);

        // "취소" 버튼 클릭 시 이벤트 처리 코드
        ask_add_or_cancel_bookmark_dialog.findViewById(R.id.noBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기
            }
        });

        TextView dialog_tv = ask_add_or_cancel_bookmark_dialog.findViewById(R.id.dialog_tv);
        dialog_tv.setText(checked_toggle_cnt + "개의 리스트가 선택되었습니다.\n리스트에 추가하시겠습니까?");

        // "확인" 버튼 클릭 시 이벤트 처리 코드
        ask_add_or_cancel_bookmark_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기
                new ToastSuccess(getResources().getString(R.string.toast_add_bookmark_to_list_success), AddBookmarkToListActivity.this);
                // "뒤로가기" 기능을 통해, 즐겨찾기 리스트 fragment로 이동

                Boolean calledView = getIntent().getBooleanExtra("fromMainActivity", false);
                Boolean calledView2 = getIntent().getBooleanExtra("fromPlaceInList", false);

                if (calledView) {
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.updateBookmarkBtnState(true);
                } else {
                    if (calledView2) {
                        PlaceInListActivity placeInListActivity = new PlaceInListActivity();
                        placeInListActivity.updateBookmarkBtnState(true);
                    } else {
                        PlaceListActivity placeListActivity = new PlaceListActivity();
                        placeListActivity.updateBookmarkBtnState(true);
                    }
                }

                onBackPressed(); // 뒤로가기 기능 수행
                finish(); // 현재 액티비티 종료
            }
        });
    }
}