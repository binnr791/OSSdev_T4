package com.example.wordmemorization;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    int maxQ    = 10;   //총 문제
    int curQ    = 0;    //현재 문제 번호
    int score   = 0;    //정답 횟수

    String correctkey = null;  //정답 키값(단어)

    Note correct    = new Note();   //정답 단어장
    Note wrong      = new Note();     //오답 단어장

    TextView desc;
    TextView quiznum;
    TextView progress;
    ListView answerList;
    ArrayList<String> answers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        desc        = (TextView)findViewById(R.id.quizdesc);
        quiznum     = (TextView)findViewById(R.id.quiznum);
        progress    = (TextView)findViewById(R.id.progress);
        answerList      = (ListView) findViewById(R.id.quizanswer);
        answers = new ArrayList<String>();

        newQ();
        quiznum.setText(curQ+"번 문제");
        progress.setText(curQ+"/"+maxQ);
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, answers);
        answerList.setAdapter(Adapter);


    }

    //새로운 문제
    private void newQ(){
        Random rand = new Random();

        answers.clear();
        ++curQ;                             //현재 문제 번호 증가
        Note note = MainActivity.note;      //개인노트 포인터 복사
        int Qnum = rand.nextInt(note.size());   //노트의 단어중 하나 선택
        int i = 0;
        //노트의 단어들을 순회하며 문제 선택
        for (Map.Entry<String, String> entry : note.getEntrySet()) {
            if(i++ == Qnum ) {
                correctkey = entry.getKey();        //정답 단어
                desc.setText(entry.getValue());     //문제에 표시(설명)
            }
        }
        int answer[] = new int[4];                  //선지 4개

        for( int j = 0 ; j < 4 ; ++ j) {

            answer[j] = rand.nextInt(note.size());  //단어들 중 선지에 들어갈것을 선택
            for( int k = 0 ; k < j ; ++ k) {    //그러나 해당 단어가 앞선 선지에 있으면
                if( answer[j] == answer[k]){            //선지 다시 골라야함
                    --j;
                    break;
                }
            }
        }

        int corrnum = rand.nextInt(4);

        boolean already = false;
        for( int j = 0 ; j < 4 ; ++ j) {

            i = 0;
            for (Map.Entry<String, String> entry : note.getEntrySet()) {
                if (i++ == answer[j]) {
                    answers.add(j+1+". "+entry.getKey());
                    if(entry.getKey().compareTo(correctkey) == 0) //    우연히 보기에 정답이 들어 갔으면
                        already = true;                            // 정답 보기를 추가 할 필요 없음
                }
            }
        }

        //아직 선지에 정답이 없으면 정답 추가
        if( already == false)
            answers.set(rand.nextInt(4), correctkey);


    }


}
