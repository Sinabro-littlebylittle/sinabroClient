package com.project.sinabro.sideBarMenu.bookmark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.R;
import com.project.sinabro.models.Bookmark;
import com.project.sinabro.retrofit.RetrofitService;
import com.project.sinabro.retrofit.interfaceAPIs.BookmarksAPI;
import com.project.sinabro.sideBarMenu.authentication.SignInActivity;
import com.project.sinabro.toast.ToastWarning;
import com.project.sinabro.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton back_iBtn;

    private TextView remove_list_tv, loading_tv;

    static private ListView listview;

    static private ListViewAdapter adapter;

    private Button addNewList_btn;

    private List<Bookmark> bookmarkList = new ArrayList<>();

    int newListIconColor;

    String newListName;

    private TokenManager tokenManager;
    private RetrofitService retrofitService;
    static BookmarksAPI bookmarksAPI;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmarkActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarkFragment newInstance(String param1, String param2) {
        BookmarkFragment fragment = new BookmarkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bookmark_activity, container, false);

        tokenManager = TokenManager.getInstance(getContext());
        retrofitService = new RetrofitService(tokenManager);
        bookmarksAPI = retrofitService.getRetrofit().create(BookmarksAPI.class);

        /** 뒤로가기 버튼 기능 */
        back_iBtn = (ImageButton) view.findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        /** 삭제 TextView 클릭 시 */
        remove_list_tv = (TextView) view.findViewById(R.id.remove_list_tv);
        remove_list_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookmarkList.isEmpty()) {
                    new ToastWarning(getResources().getString(R.string.toast_cannot_remove_list), getActivity());
                    return;
                }

                Intent intent = new Intent(getActivity(), RemoveBookmarkListActivity.class);
                startActivity(intent);
            }
        });

        loading_tv = view.findViewById(R.id.loading_tv);

        /** "리스트 추가하기" 버튼 클릭 시 */
        addNewList_btn = (Button) view.findViewById(R.id.addNewList_btn);
        addNewList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewBookmarkListActivity.class);
                startActivity(intent);
            }
        });

        listview = view.findViewById(R.id.bookmarkList_listView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loading_tv.setVisibility(View.VISIBLE);

        adapter = new ListViewAdapter();

        Call<List<Bookmark>> call_bookmarksAPI_getBookmarkList = bookmarksAPI.getBookmarkList();
        call_bookmarksAPI_getBookmarkList.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                if (response.isSuccessful()) {
                    bookmarkList = response.body();
                    if (!bookmarkList.isEmpty()) {
                        for (int k = 0; k < bookmarkList.size(); k++) {
                            //Adapter 안에 아이템의 정보 담기
                            adapter.addItem(new

                                    BookmarkListItem(bookmarkList.get(k).getId(), bookmarkList.get(k).getUserId(), bookmarkList.get(k).getBookmarkName(), bookmarkList.get(k).getIconColor(), bookmarkList.get(k).getBookmarkedPlaceId()));
                        }

                        listview.setAdapter(adapter);
                        loading_tv.setVisibility(View.GONE);
                    }
                } else {
                    switch (response.code()) {
                        case 401:
                            final Intent intent = new Intent(getContext(), SignInActivity.class);
                            startActivity(intent);
                            getActivity().onBackPressed();
                            break;
                        case 404:
                            loading_tv.setText("정보 없음");
                            Animation animation = AnimationUtils.loadAnimation(getContext(), com.project.sinabro.R.anim.ripple_animation);
                            addNewList_btn.startAnimation(animation);
                            break;
                        default:
                            new ToastWarning(getResources().getString(R.string.toast_none_status_code), getActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                // 서버 코드 및 네트워크 오류 등의 이유로 요청 실패
                new ToastWarning(getResources().getString(R.string.toast_server_error), getActivity());
            }
        });

        // 리스트뷰에 Adapter 설정
        listview.setAdapter(adapter);
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
                convertView = inflater.inflate(R.layout.list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView listName_tv = convertView.findViewById(R.id.listName_tv);
            listName_tv.setText(listItem.getBookmarkName());

            RoundedImageView list_color_circle_roundedImageView = convertView.findViewById(R.id.list_color_circle_roundedImageView);
            list_color_circle_roundedImageView.setImageResource(listItem.getIconColor());

            // 리스트 정보 수정 아이콘 버튼 클릭 시
            convertView.findViewById(R.id.modify_list_info_roundedImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AddNewBookmarkListActivity.class);
                    intent.putExtra("forModify", true);
                    intent.putExtra("modify_clicked", true);
                    intent.putExtra("listIconColor", listItem.getIconColor());
                    intent.putExtra("listName", listName_tv.getText().toString());
                    intent.putExtra("bookmarkId", listItem.getId());
                    startActivity(intent);
                }
            });

            // 각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // "리스트 세부" 액티비티로 이동
                    Intent intent = new Intent(getActivity(), BookmarkedPlacesInListActivity.class);
                    intent.putExtra("listName", listName_tv.getText().toString());
                    intent.putExtra("bookmarkId", listItem.getId());
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }
}