package com.example.sinabro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;//
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    final String url = "http://34.64.77.135:3000/login/";
    private EditText Pw_Login, Id_Login;
    private Button Btn_Signup,Btn_Login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Pw_Login = findViewById(R.id.Pw_Login);
        Id_Login = findViewById(R.id.Id_Login);
        Btn_Signup = findViewById(R.id.Btn_Signup);
        Btn_Login = findViewById(R.id.Btn_Login);

        Btn_Signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = Id_Login.getText().toString();
                SavedUser savedUser = new SavedUser();
                savedUser.setUserID(userID);

                String userPw = Pw_Login.getText().toString();

                login(url, userID, userPw);

            }
        });

    }

    public void login(final String urls, final String id, final String pw){
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
                    //sendData.put("id","1234");
                    //sendData.put("passwd","5678");
                    sendData.put("id", id);
                    sendData.put("passwd", pw);
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
                    String name = obj.getString("name");

                    //TODO 로그인이 success 면 화면 전환, 로그인이 fail 이나 error 면 오류처리
                    if(selected_result.equals("success"))
                    {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("로그인","성공");


                                SavedUser savedUser = new SavedUser();
                                savedUser.setName(name);


                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });


                    }
                    else{
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                }catch (Exception e){
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        }.start();


    }
}

