package com.example.sinabro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GrowthActivity extends AppCompatActivity {

    int getStage_result;
    int point =0;
    String pointData;
    Button goback;

    String UserID; // 로그인 액티비티에서 아이디 받아옴
    // 로그인 액티비티에서 id랑 패스워드 제가임시로 전역변수로 바꿔놧어염 !~!~!!! 수정부탁부탁

    final String url = "http://34.64.77.135:3000/tree/"; //서버 주소

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth);

        SavedUser savedUser = new SavedUser();
        UserID = savedUser.getID();


        getPoint(url,UserID); // 서버로부터 나무경험치 받아오는 함수 호출
        getStage(point); // 서버로 부터 받아온 포인트 값(getPoint()의 리턴 값) 넘겨줌 ! 암튼 얘도 호출


        final TextView textView1, textView2;
        final ImageView imageView;

        textView1 = findViewById(R.id.txt1);
        textView2 = findViewById(R.id.txt2);
        imageView = findViewById(R.id.imageView);

        //getStage()로 부터 반환된 결과에 따라 조건문 실행
        //단계에 따라 알맞게 텍스트/이미지뷰 바꿔줌
        if(getStage_result==4){ //4단계
            textView1.setText("마침내 나무가 다 자랐습니다.");
            textView2.setText("당신을 닮아 예쁜 꽃이 피어났네요 !");
            imageView.setImageResource(R.drawable.t3);
        }
        else if (getStage_result==3){ //3단계
            textView1.setText("나무에 꽃봉오리가 맺혔습니다.");
            textView2.setText("곧 꽃을 피울 것 같습니다.");
            imageView.setImageResource(R.drawable.t2);
        }
        else if (getStage_result==2){ //2단계
            textView1.setText("새싹은 자라나 나무가 되었습니다.");
            textView2.setText("나무가 무럭무럭 성장하고 있습니다.");
            imageView.setImageResource(R.drawable.t1);
        }
        else if (getStage_result==1){ //1단계
            textView1.setText("환경을 생각하는 당신의 새싹이");
            textView2.setText("무럭무럭 자라나고 있습니다.");
            imageView.setImageResource(R.drawable.s1);
        }

        goback = (Button)findViewById(R.id.button_back);

        goback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrowthActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    //서버로부터 포인트 받는함수
    public int getPoint(final String urls, final String id){
        new Thread(){
            @Override
            public void run() {
                try{

                    URL url = new URL(urls);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST"); //전송방식
                    connection.setDoOutput(true);       //데이터를 쓸 지 설정
                    connection.setDoInput(true);        //데이터를 읽어올지 설정

                    OutputStream os = connection.getOutputStream();
                    JSONObject sendData = new JSONObject();

                    //아이디 전송
                    sendData.put("id", id);
                    Log.d("제이슨 오브젝트",sendData.toString());
                    String body = sendData.toString();
                    // Request Body에 Data 셋팅.
                    os.write(body.getBytes("utf-8"));

                    // Request Body에 Data 입력.
                    os.flush();

                    // OutputStream 종료.
                    os.close();

                    InputStream is = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String result;
                    while((result = br.readLine())!=null){
                        sb.append(result+"\n");
                    }

                    connection.disconnect();

                    result = sb.toString();

                    Log.d("리절트",result);
                    JSONObject obj = new JSONObject(result);
                    Log.d("받아온 리절트",obj.getString("result")); //성공 유무

                    String selected_result = obj.getString("result");
                    pointData = obj.getString("treeCount"); // 트리경험치

                    if(selected_result.equals("success")) {
                        GrowthActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "서버 연결 성공", Toast.LENGTH_SHORT).show();

                                point = Integer.parseInt(pointData); //int로 형변환 해서 point에 저장
                            }
                        });

                    }




                }catch (Exception e){
                    GrowthActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        }.start();

        return point; //point 넘겨줌
    }

    //몇단계인지 판별하는 함수 (서버에서 넘어온 나무 경험치에따라)
    public int getStage(int point ) {

        if (point >= 6) {
            getStage_result = 4; //4단계
        } else if (point >= 4 && point < 6) {
            getStage_result = 3; //3단계
        } else if (point >= 2 && point < 4) {
            getStage_result =  2; //2단계
        }
        else if (point>2)
            getStage_result = 1; //1단계

        return getStage_result; // 결과 반환
    }

}








