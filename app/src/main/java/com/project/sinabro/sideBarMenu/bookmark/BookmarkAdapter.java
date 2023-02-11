package com.project.sinabro.sideBarMenu.bookmark;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.project.sinabro.R;

import java.util.ArrayList;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private ArrayList<Dictionary> mList;
    private Context mContext ;
/*뷰 홀더 어뎁터*/
    public class BookmarkViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        /*변수 선언*/
        protected TextView name;

        private ContextMenu menu;
        private ContextMenu.ContextMenuInfo menuInfo;

        protected TextView bName;
        public BookmarkViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name_favorite_place);
            view.setOnCreateContextMenuListener(this::onCreateContextMenu);
        }
    @NonNull
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
        MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
        Edit.setOnMenuItemClickListener(onEditMenu);
        Delete.setOnMenuItemClickListener(onEditMenu);
    }

    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener(){
        @NonNull
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case 1001:  // 수정
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

                    View v = LayoutInflater.from(mContext)
                            .inflate(R.layout.favorite_edit_box, null, false);
                    builder.setView(v);
                    final Button ButtonSubmit = (Button) v.findViewById(R.id.button_bookmark_submit);
                    final EditText editTextName = (EditText) v.findViewById(R.id.edittext_bookmark_name);
                    //final EditText editTextEnglish = (EditText) view.findViewById(R.id.edittext_dialog_endlish);
                    //final EditText editTextKorean = (EditText) view.findViewById(R.id.edittext_dialog_korean);



                    // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                    editTextName.setText(mList.get(getAdapterPosition()).getName());
                   // editTextEnglish.setText(mList.get(getAdapterPosition()).getEnglish());
                   // editTextKorean.setText(mList.get(getAdapterPosition()).getKorean());



                    final AlertDialog dialog = builder.create();
                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                        // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로

                        public void onClick(View v) {
                            String strNAME = editTextName.getText().toString();
                            //String strEnglish = editTextEnglish.getText().toString();
                            //String strKorean = editTextKorean.getText().toString();

                            Dictionary dict = new Dictionary(strNAME);


                            // 8. ListArray에 있는 데이터를 변경하고
                            mList.set(getAdapterPosition(), dict);


                            // 9. 어댑터에서 RecyclerView에 반영하도록 합니다.

                            notifyItemChanged(getAdapterPosition());

                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    break;

                case 1002:              // 삭제

                    mList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), mList.size());

                    break;

            }
            return true;
        }
    };
}


    public BookmarkAdapter(Context context, ArrayList<Dictionary> list) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType ) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.favorite_place_list, viewGroup, false);

        BookmarkViewHolder viewHolder = new BookmarkViewHolder(view);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.BookmarkViewHolder viewholder, int position) {

        viewholder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        viewholder.name.setGravity(Gravity.CENTER);                             // 텍스트 설정

        viewholder.name.setText(mList.get(position).getName());                 // get 형식으로 텍스트를 받아옴

        /*추후 뷰홀더 클릭시 여기서 소스코드 구현 하면 됨 */
        viewholder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 뷰홀더 클릭 기능

                Log.d("ViewHolder", "onClick: 테스트 중입니다.");

            }
        });

    }
    @Override
    public int getItemCount() {
        return mList.size();                //(null != mList ? mList.size() : 0)
    }

}

