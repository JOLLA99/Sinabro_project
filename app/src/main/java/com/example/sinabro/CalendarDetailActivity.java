package com.example.sinabro;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class CalendarDetailActivity extends AppCompatActivity {
    private static final String TAG="CalendarDetailActivity";

    private TextView theDate, theTime;
    private Button btonGoCalendar, btnTime;
    TextClock textClock;
    String selected_date="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);
        theDate = (TextView)findViewById(R.id.date);
        btonGoCalendar = (Button)findViewById(R.id.gotocal);
        btnTime = (Button)findViewById(R.id.btn_time);
        theTime = (EditText)findViewById(R.id.timetxt);

        Intent incomingintent = getIntent();
        String date = incomingintent.getStringExtra("date");
        theDate.setText(date);


        btonGoCalendar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(CalendarDetailActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if(view==btnTime){
                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR);
                    int mMin = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            theTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            selected_date = String.format("%02d:%02d", hourOfDay, minute);
                        }
                    },mHour, mMin, false);
                    timePickerDialog.show();
                }
            }
        });


    }

    public static String sendPostJson(String selected_date) throws Exception{
        //JSON 데이터 받을 URL 객체 생성
        URL url = new URL("http://34.64.77.135:3000/");
        //객체를 생성해 url과 연결
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        //전송방식 POST
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        //Response Data를 JSON으로 받도록 설정
        con.setRequestProperty("Accept", "application/json");
        //Output Stream을 POST 데이터로 전송
        con.setDoOutput(true);
        //json data
        String jsonInputString = "{id:userID,password:userPw,name:userName}";

        //JSON을 보내는 Output stream
        try(
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
            StringBuilder response = new StringBuilder();
            String responseLine =null;
            while((responseLine=br.readLine())!=null){
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            return response.toString();
        }
    }



}
