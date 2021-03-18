package com.hog2020.ex40datastroageextenal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    EditText et;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et= findViewById(R.id.et);
        tv= findViewById(R.id.tv);

    }

    public void clicksave(View view) {

        //외부메모리(SD card)가 있는가?
        String state = Environment.getExternalStorageState();

        //외장메모리상태(state) 가 연결(mounted) 되어 있지 않은가 를 확인
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "SD card is not mounted", Toast.LENGTH_SHORT).show();
            return;
        }

        String data = et.getText().toString();
        et.setText("");

        //외부메모리 영역의 경로를 관리하는 객체 참조변수
        File path;// Data.txt파일이 저장될 디렉토리 경로

        //안드로이드 마시멜로우버전(api 23버전) 이상에서는
        //SD카드 의 아무위치에 직접 저장하는 것이 불가
        //오로지 각 앱에게 할당된 영역에만 저장가능
        //이 앱을 실행하는 디바이스가 몇버전인지 체크

        File[] dirs;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dirs= getExternalFilesDirs("Mydir");//폴더명
        }else{
            dirs= ContextCompat.getExternalFilesDirs(this,"Mydir");
        }
        path=dirs[0];
        tv.setText(path.getPath());//경로 확인해보기
        
        //위 경로에 Data.txt 라는 이름의 파일을 만들기 위해
        //file 객체 생성
        File file =new File(path,"Data.txt");
        try {
            FileWriter fw = new FileWriter(file,true);
            PrintWriter writer =new PrintWriter(fw);
            writer.println(data);
            writer.flush();
            writer.close();

            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clickload(View view) {

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){

            //읽을 수 있는 상태

            File path;
            File[] dirs;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                dirs=this.getExternalFilesDirs("Mydir");
            }else{
                dirs=ContextCompat.getExternalFilesDirs(this,"Mydir");
            }

            path= dirs[0];

            File file =new File(path,"Data.txt");

            try {
                FileReader fr =new FileReader(file);
                BufferedReader reader= new BufferedReader(fr);

                StringBuffer buffer =new StringBuffer();
                String line = reader.readLine();
                while(line!= null){
                    buffer.append(line+"\n");
                    line= reader.readLine();
                }
                tv.setText(buffer.toString());

                reader.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //동적퍼미션 필요한 외부저장소..(앱 이름 폴더 뺴고 나머지다)
    public void clickBtn(View view) {

        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "외부저장소 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        //동적퍼미션 체크
        //마시멜로우 버전 이상에서도입된 보안강화 기능
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int checkResult =checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //퍼미션결과가 거부되어 있는 상태인지 확인
            if(checkResult== PackageManager.PERMISSION_DENIED){

                //퍼미션을 요청하는 화면(Activity)를 실행 해주는
                //메소드가 액티비티에 존재함
                String[] permissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);

                return;
            }
        }
        //SDcard 의 특정위치에 저장하기
        File path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if(path!=null)tv.setText(path.getPath());

        File file = new File(path,"aaa.txt");
        FileWriter fw= null;
        try {
            fw = new FileWriter(file,true);

            PrintWriter writer = new PrintWriter(fw);
            writer.println(et.getText().toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
    }

    //requestPermissins()에 의해  보여진
    //다이얼로그의 Allow/Deny를 선택했을떼
    //자동으로 그결과를알려주기 위해 발동하는 메소드

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 10:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "외부저장소 쓰기 가능", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "외부저장소 사용 불가", Toast.LENGTH_SHORT).show();
                }break;
        }

    }
}