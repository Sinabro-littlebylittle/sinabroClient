package com.project.sinabro.bottomSheet.place;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.MainActivity;
import com.project.sinabro.R;
import com.project.sinabro.databinding.ActivityAddBookmarkToListBinding;
import com.project.sinabro.models.Bookmark;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.BookmarksAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.sideBarMenu.bookmark.AddNewBookmarkListActivity;
import com.project.sinabro.sideBarMenu.bookmark.BookmarkFragment;
import com.project.sinabro.sideBarMenu.bookmark.BookmarkListItem;
import com.project.sinabro.sideBarMenu.bookmark.BookmarkedPlacesInListActivity;
import com.project.sinabro.sideBarMenu.bookmark.RemoveBookmarkListActivity;
import com.project.sinabro.toast.ToastSuccess;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookmarkToListActivity extends AppCompatActivity {

    private ActivityAddBookmarkToListBinding binding;

    private ListViewAdapter adapter;

    private Dialog ask_add_or_cancel_bookmark_dialog;

    private List<String> bookmarkIds = new ArrayList<>();

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    private BookmarksAPI bookmarksAPI;

    private Intent intent;

    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBookmarkToListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(getApplicationContext());
        retrofitService = new RetrofitService(tokenManager);
        bookmarksAPI = retrofitService.getRetrofit().create(BookmarksAPI.class);

        intent = getIntent();
        placeId = intent.getStringExtra("placeId");

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
                finish(); // 현재 액티비티 종료
            }
        });

        /** "리스트 추가" TextView */
        binding.addNewListTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), AddNewBookmarkListActivity.class);
                startActivity(intent);
            }
        });

        /** "추가하기" 버튼 기능 */
        binding.addBookmarkToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookmarkIds.size() == 0) {
                    new ToastWarning(getResources().getString(R.string.toast_remove_list_failed), AddBookmarkToListActivity.this);
                    return;
                }

                // dialog_ask_accept_remove_list 다이얼로그 창 띄우기
                showDialog_ask_accept_add_or_remove_list();
            }
        });
    }

    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<BookmarkListItem> items = new ArrayList<BookmarkListItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(BookmarkListItem item) {
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
            final BookmarkListItem listItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checkbox_list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView listName_tv = convertView.findViewById(R.id.listName_tv);
            listName_tv.setText(listItem.getBookmarkName());

            RoundedImageView list_color_circle_roundedImageView = convertView.findViewById(R.id.list_color_circle_roundedImageView);
            list_color_circle_roundedImageView.setImageResource(listItem.getIconColor());

            final Boolean[] check_policies_toggle = {false};

            RoundedImageView check_circle_roundedImageView = convertView.findViewById(R.id.check_circle_roundedImageView);
            check_circle_roundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (check_policies_toggle[0]) {
                        check_circle_roundedImageView.setImageResource(R.drawable.check_circle_empty);

                        // 추가 대상 리스트 내에서 선택된 리스트의 markerId값 제거
                        if (bookmarkIds.contains(listItem.getId()))
                            bookmarkIds.remove(listItem.getId());
                    } else {
                        check_circle_roundedImageView.setImageResource(R.drawable.check_circle_green);

                        // 아이디 추가
                        if (!bookmarkIds.contains(listItem.getId()))
                            bookmarkIds.add(listItem.getId());
                    }
                    check_policies_toggle[0] = !check_policies_toggle[0];
                }
            });

            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // "리스트 세부" 액티비티로 이동
                    Intent intent = new Intent(getApplicationContext(), BookmarkedPlacesInListActivity.class);
                    intent.putExtra("listName", listName_tv.getText().toString());
                    intent.putExtra("bookmarkId", listItem.getId());
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
        dialog_tv.setText(bookmarkIds.size() + "개의 리스트가 선택되었습니다.\n리스트에 추가하시겠습니까?");

        // "확인" 버튼 클릭 시 이벤트 처리 코드
        ask_add_or_cancel_bookmark_dialog.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_add_or_cancel_bookmark_dialog.dismiss(); // 다이얼로그 닫기

                Call<ResponseBody> call_bookmarksAPI_addBookmarkedPlaceInBookmarkList = bookmarksAPI.addBookmarkedPlaceInBookmarkList(placeId, bookmarkIds);
                call_bookmarksAPI_addBookmarkedPlaceInBookmarkList.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            MainActivity mainActivity = new MainActivity();
                            mainActivity.updateBookmarkBtnState(true);
                            new ToastSuccess(getResources().getString(R.string.toast_add_bookmark_to_list_success), AddBookmarkToListActivity.this);
                            finish(); // 현재 액티비티 종료
                        } else {
                            switch (response.code()) {
                                case 401:
                                    new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddBookmarkToListActivity.this);
                                    final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddBookmarkToListActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                        new ToastWarning(getResources().getString(R.string.toast_server_error), AddBookmarkToListActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter = new ListViewAdapter();

        Call<List<Bookmark>> call_bookmarksAPI_getBookmarkList = bookmarksAPI.getBookmarkList();
        call_bookmarksAPI_getBookmarkList.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                if (response.isSuccessful()) {
                    List<Bookmark> bookmarkList = response.body();
                    if (!bookmarkList.isEmpty()) {
                        for (int k = 0; k < bookmarkList.size(); k++) {
                            //Adapter 안에 아이템의 정보 담기
                            adapter.addItem(new
                                    BookmarkListItem(bookmarkList.get(k).getId(), bookmarkList.get(k).getUserId(), bookmarkList.get(k).getBookmarkName(), bookmarkList.get(k).getIconColor(), bookmarkList.get(k).getBookmarkedPlaceId()));
                        }

                        // 리스트뷰에 Adapter 설정
                        binding.removeListListView.setAdapter(adapter);
                        binding.loadingTv.setVisibility(View.GONE);
                    }
                } else {
                    switch (response.code()) {
                        case 401:
                            new ToastWarning(getResources().getString(R.string.toast_login_time_exceed), AddBookmarkToListActivity.this);
                            final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            break;
                        case 404:
                            binding.loadingTv.setText("정보 없음");
                            break;
                        default:
                            new ToastWarning(getResources().getString(R.string.toast_none_status_code), AddBookmarkToListActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), AddBookmarkToListActivity.this);
            }
        });

        // 리스트뷰에 Adapter 설정
        binding.removeListListView.setAdapter(adapter);
    }
}