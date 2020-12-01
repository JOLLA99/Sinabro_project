package com.example.sinabro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SignupActivity extends AppCompatActivity {
    final String url = "https://webhook.site/b2f72fe2-41ca-429a-a2e6-bdc3fd0d4c05";
    private EditText Name_Signup, Pw_Signup, Id_Signup;
    private Button Btn_Signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        Name_Signup = findViewById(R.id.Name_Signup);
        Pw_Signup = findViewById(R.id.editTextTextPassword);
        Id_Signup = findViewById(R.id.editTextTextPersonName);
        Btn_Signup = findViewById(R.id.signin_button);

        //회원가입 버튼 클릭 시 수행
        Btn_Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String userID = Id_Signup.getText().toString();
                String userPw = Pw_Signup.getText().toString();
                String userName = Name_Signup.getText().toString();

                signin(url, userID, userPw, userName);

            }
        });




    }

    public void signin(final String urls, final String id, final String pw, final String name){
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
                    sendData.put("id", id);
                    sendData.put("passwd", pw);
                    sendData.put("name", name);
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
                    Log.d("받아온 리절트",obj.getString("result"));
                    String selected_result = obj.getString("result");

                    //TODO 로그인이 success 면 화면 전환, 로그인이 fail 이나 error 면 오류처리
                    if(selected_result=="success")
                    {
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, CalendarActivity.class);
                        startActivity(intent);

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        return;

                    }

                }catch (Exception e){

                }

            }
        }.start();


    }

}