package com.example.wordmemorization;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuFragment extends ListFragment {
    ArrayList<String> list = new ArrayList<String>();


    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        list.add("단어검색");
        list.add("단어장");
        list.add("내 단어장 퀴즈");
        list.add("퀴즈 기록");

        setListAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list));
    }

    public void onListItemClick(ListView l, View v, int position, long id){
        Intent  intent = null;
        switch (position){
            case 0:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
            case 1:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
            case 2:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
            case 3:
                intent = new Intent(getActivity(), SearchActivity.class);
                break;
        }
        startActivity(intent);
    }
}
