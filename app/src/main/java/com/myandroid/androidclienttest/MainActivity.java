package com.myandroid.androidclienttest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    Socket socket;
    //從手機裡的"我的檔案"選張照片，準備傳到電腦，
    //因不同的手機路徑可能不同，可自行修改。
    //public static final String file_name = "//storage/MicroSD/DCIM/Hello.jpg";
    Button imageChooser, connecter, refreshLog;
    OutputStream outputstream;
    String ImagePath="", timeString="";
    BufferedInputStream bis;
    FileInputStream fis;
    TextView statusText;
    static byte[] mybytearray;
    int serverPort;
    InetAddress serverAddr;
    MyDBHandler dbHandler;
    SimpleDateFormat formatter;
    Date curDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageChooser = (Button)findViewById(R.id.button);
        connecter = (Button) findViewById(R.id.button2);
        refreshLog = (Button) findViewById(R.id.refreshLog);
        statusText = (TextView) findViewById(R.id.textView);
        statusText.setMovementMethod(ScrollingMovementMethod.getInstance());
        formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String timeString = formatter.format(curDate);

        dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.addLog("[*]"+ timeString +": Choose a picture to send to PC\n");
        printDatabase();

        imageChooser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // 建立 "選擇檔案 Action" 的 Intent
                Intent intent = new Intent(Intent.ACTION_PICK);

                // 過濾檔案格式
                intent.setType("image/*");

                // 建立 "檔案選擇器" 的 Intent  (第二個參數: 選擇器的標題)
                Intent destIntent = Intent.createChooser(intent, "選擇檔案");

                // 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
                startActivityForResult(destIntent, 0);
            }
        });
        connecter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Thread test = new Thread(clientSocket);
                test.start();
                //statusText.setText(statusText.getText() + "[*] Connecting to server: " + serverAddr.toString() + "/" + serverPort + " ...\n");
                //statusText.setText(statusText.getText()+"[*] Done!");
            }
        });

        refreshLog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                printDatabase();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // 有選擇檔案
        if ( resultCode == RESULT_OK ) {
            // 取得檔案的 Uri
            if (data != null) {
                Log.e("Socket", "great, data is not null");
                Uri uri = data.getData();
                if (uri != null) {
                    String path = MagicFileChooser.getAbsolutePathFromUri(this,uri);
                    Log.d("path:",path);
                    ImagePath =path;
                    Log.e("Socket", ImagePath);
                    curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    timeString = formatter.format(curDate);
                    dbHandler.addLog("[*]"+timeString+": "+ "ImagePath: "+ ImagePath + " \n");
                    File myFile = new File(ImagePath);
                    Log.e("myFile",myFile.toString());
                    Log.e("myfile.exists",String.valueOf(myFile.exists()));
                    if (myFile.exists()){


                        try {
                            mybytearray = new byte[(int) myFile.length()];

                            fis = new FileInputStream(myFile);
                            bis = new BufferedInputStream(fis, 8 * 1024);
                            bis.read(mybytearray, 0, mybytearray.length);


                            curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                            timeString = formatter.format(curDate);
                            Log.e("Socket", "File read done!");
                            dbHandler.addLog("[*]"+timeString + ": Image has been chosen\n");

                            printDatabase();
                        } catch (IOException e) {
                            curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                            timeString = formatter.format(curDate);
                            Log.e("Socket", "File read error!");
                            dbHandler.addLog("[*]"+timeString + ": file read error!");
                            printDatabase();
                        }
                        //輸出到電腦


                    } else {
                        curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                        timeString = formatter.format(curDate);
                        Log.e("Socket", "file doesn't exist!");
                        dbHandler.addLog("[*]"+timeString + ": file doesn't exist!");
                        printDatabase();
                    }
                } else {
                    setTitle("無效的檔案路徑 !!");
                }
            } else {
                setTitle("取消選擇檔案 !!");
            }
        }
    }

    Runnable clientSocket = new Runnable() {
        @Override
        public void run() {
            try {

                serverAddr = InetAddress.getByName("192.168.1.185");
                serverPort = 5987;

                curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                timeString = formatter.format(curDate);
                Log.e("Socket", "Client: Connecting...");
                dbHandler.addLog("[*]"+ timeString + ": Client: Connecting...: " + serverAddr + "/" + serverPort + "\n");
                //printDatabase();

                try {
                    socket = new Socket(serverAddr, serverPort);
                    outputstream = socket.getOutputStream();
                    outputstream.write(mybytearray, 0, mybytearray.length);
                    outputstream.flush();
                } catch (Exception e) {
                    curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    timeString = formatter.format(curDate);
                    Log.e("Socket", "Client: Error", e);
                    dbHandler.addLog("[*]"+timeString+ ": Client: Error" + e + "\n");
                    //printDatabase();

                } finally {
                    curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    timeString = formatter.format(curDate);
                    Log.e("Socket", "Transfer done!");
                    dbHandler.addLog("[*]"+timeString+ ": Transfer done!\n");
                    socket.close();
                }
            } catch (Exception e) {
                curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                timeString = formatter.format(curDate);
                Log.e("Socket", "Client: Error", e);
                dbHandler.addLog("[*]"+timeString+ ": Client: Error" + e + "\n");
                //printDatabase();
            }
        }
    };

    public void printDatabase(){
        String DBString = dbHandler.databaseToString();
        statusText.setText(DBString);
    }

}

