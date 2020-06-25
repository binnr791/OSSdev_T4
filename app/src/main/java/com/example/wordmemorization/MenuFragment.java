package com.example.wordmemorization;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//메뉴 리스트
public class MenuFragment extends ListFragment {
    ArrayList<String> list = new ArrayList<String>();


    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        list.add("단어검색");
        list.add("단어장");
        list.add("내 단어장 퀴즈");
        list.add("노트 불러오기");

  //      list.add("퀴즈 기록"); 기술력 부족으로 보류

        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list));
    }

  //  public void onBackPressed(){
  //  }
    public void onListItemClick(ListView l, View v, int position, long id){
        Intent  intent = null;
        switch (position){
            case 0:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
            case 1:
                intent = new Intent(getActivity(), NoteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                break;
            case 2:
                if( MainActivity.note.size() < 4) {
                    Toast.makeText(getActivity(), "단어가 너무 적습니다", Toast.LENGTH_SHORT).show();
                    return ;
                }
                intent = new Intent(getActivity(), QuizActivity.class);
                break;


    //        case 3:
    //            intent = new Intent(getActivity(), SearchActivity.class);
    //            break;
        }

        startActivity(intent);
    }
}
