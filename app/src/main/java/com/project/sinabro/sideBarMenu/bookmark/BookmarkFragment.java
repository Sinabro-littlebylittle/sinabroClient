package com.project.sinabro.sideBarMenu.bookmark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.makeramen.roundedimageview.RoundedImageView;
import com.project.sinabro.R;
import com.project.sinabro.bottomSheet.place.PlaceItem;
import com.project.sinabro.bottomSheet.place.PlaceListActivity;
import com.project.sinabro.sideBarMenu.settings.ModifyMyInfoActivity;
import com.project.sinabro.sideBarMenu.settings.SettingNotificationsActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;


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

    private TextView remove_list_tv;

    static private ListView listview;

    static private ListViewAdapter adapter;

    private Button addNewList_btn;

    int newListIconColor;

    String newListName;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        View view = inflater.inflate(R.layout.fragment_bookmark_activity, container, false);

        /** 뒤로가기 버튼 기능 */
        back_iBtn = (ImageButton) view.findViewById(R.id.back_iBtn);
        back_iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        /** 취소 TextView 클릭 시 */
        remove_list_tv = (TextView) view.findViewById(R.id.remove_list_tv);
        remove_list_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RemoveListActivity.class);
                startActivity(intent);
            }
        });

        /** "리스트 추가하기" 버튼 클릭 시 */
        addNewList_btn = (Button) view.findViewById(R.id.addNewList_btn);
        addNewList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewListActivity.class);
                startActivity(intent);
            }
        });

        listview = view.findViewById(R.id.list_listView);
        adapter = new ListViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new ListItem(R.color.num1, "청주 맛집"));
        adapter.addItem(new ListItem(R.color.num2, "충북대학교"));
        adapter.addItem(new ListItem(R.color.num3, "나중에 갈 곳"));
        adapter.addItem(new ListItem(R.color.num4, "기타"));

        listview.setAdapter(adapter);

        return view;
    }

    public void updateScreen() {
        if (getArguments() != null) {
            newListIconColor = getArguments().getInt("newListIconColor");
            newListName = getArguments().getString("newListName");
            adapter.addItem(new ListItem(newListIconColor, newListName));
        }

        // 리스트뷰에 Adapter 설정
        listview.setAdapter(adapter);
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
                convertView = inflater.inflate(R.layout.list_view_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            TextView listName_tv = convertView.findViewById(R.id.listName_tv);
            listName_tv.setText(listItem.getListName());

            RoundedImageView list_color_circle_roundedImageView = convertView.findViewById(R.id.list_color_circle_roundedImageView);
            list_color_circle_roundedImageView.setImageResource(listItem.getListIconColorValue());

            // 리스트 정보 수정 아이콘 버튼 클릭 시
            convertView.findViewById(R.id.modify_list_info_roundedImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AddNewListActivity.class);
                    intent.putExtra("forModify", true);
                    intent.putExtra("modify_clicked", true);
                    intent.putExtra("newListIconColor", listItem.getListIconColorValue());
                    intent.putExtra("newListName", listName_tv.getText().toString());
                    startActivity(intent);
                }
            });

            // 각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     // "리스트 세부" 액티비티로 이동
                    Intent intent = new Intent(getActivity(), PlaceInListActivity.class);
                    intent.putExtra("listName", listName_tv.getText().toString());
                    startActivity(intent);
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }
}