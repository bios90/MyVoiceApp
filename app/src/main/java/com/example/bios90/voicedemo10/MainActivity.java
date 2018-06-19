package com.example.bios90.voicedemo10;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import net.gotev.speech.Speech;
//import net.gotev.speech.SpeechDelegate;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    WebView webView;
    TextView textView;
    String lastEdded="";
    String lastPartial="";
    String lastFull="";
    MyReciever myReciever;
    IntentFilter intentFilter;

    public static final String serviceName = "com.example.bios90.voicedemo10.MyService";

    Intent intent = new Intent();
    Button btn,btn2;
    Vibrator vibrator;

    ImageView imgLoc;
    RelativeLayout laLock;
    Boolean locked=false;

    GlobalHelper globalHelper;

    //SpeechDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


        webView = findViewById(R.id.webView);
        textView = findViewById(R.id.tvForText);

        laLock = findViewById(R.id.laLock);
        laLock.setVisibility(View.GONE);
        laLock.setOnClickListener(null);
        imgLoc = findViewById(R.id.imgLock);

        globalHelper = (GlobalHelper)getApplicationContext();
        globalHelper.work=false;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://bestbanksapp.ru/index.html");

        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},123);
        }

//        Speech.init(this, getPackageName());
//        //region SpeechDelegate
//        delegate = new SpeechDelegate()
//        {
//            @Override
//            public void onStartOfSpeech()
//            {
//
//            }
//
//            @Override
//            public void onSpeechRmsChanged(float value)
//            {
//
//            }
//
//            @Override
//            public void onSpeechPartialResults(List<String> results)
//            {
//
//            }
//
//            @Override
//            public void onSpeechResult(String result)
//            {
//                Log.e(TAG, "onSpeechResult: "+result );
//                textView.setText(result);
//            }
//        };
//        //endregion


        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!checkServiceRunning())
                {
                    registerReceiver(myReciever, intentFilter);
                    textView.setGravity(Gravity.LEFT | Gravity.TOP);
                    textView.setText("");
                    startService(intent);

                    globalHelper.work=true;
                }
                else
                    {
                        Toast.makeText(globalHelper, "Service  Running", Toast.LENGTH_SHORT).show();
//                        stopService(new Intent(MainActivity.this, MyService.class));
//                        unregisterReceiver(myReciever);
//                        btn.setEnabled(true);
//
//                        Intent stopIntent = new Intent(MainActivity.this, MyService.class);
//                        stopIntent.setAction(GlobalHelper.STOP);
//                        startService(stopIntent);
//
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                myReciever = new MyReciever();
//                                registerReceiver(myReciever, intentFilter);
//                                globalHelper.work=true;
//
//                                startService(intent);
//                            }
//                        }, 2000);

                    }

                btn.setEnabled(false);
            }

        });

        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    stopService(new Intent(MainActivity.this, MyService.class));
                    unregisterReceiver(myReciever);
                    btn.setEnabled(true);

                    Intent stopIntent = new Intent(MainActivity.this, MyService.class);
                    stopIntent.setAction(GlobalHelper.STOP);
                    startService(stopIntent);
                }
                catch (Exception e)
                {

                }
            }
        });

        imgLoc.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                if(locked)
                {
                    laLock.setVisibility(View.GONE);
                    locked=false;
                    imgLoc.setImageDrawable(getResources().getDrawable(R.drawable.lock));
                }
                else
                    {
                        laLock.setVisibility(View.VISIBLE);
                        locked=true;
                        imgLoc.setImageDrawable(getResources().getDrawable(R.drawable.unlock));
                    }
                    vibrator.vibrate(400);
                return false;
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(!checkServiceRunning())
        {
            myReciever = new MyReciever();
            intentFilter = new IntentFilter();
            intentFilter.addAction(MyService.CONNECTION);

            intent = new Intent(MainActivity.this, MyService.class);
            intent.setAction(GlobalHelper.START);

        }
        else
            {
                myReciever = new MyReciever();
                intentFilter = new IntentFilter();
                intentFilter.addAction(MyService.CONNECTION);
                stopService(intent);
            }
    }

    @Override
    protected void onDestroy()
    {
        globalHelper.work=false;
        stopService(intent);
        unregisterReceiver(myReciever);
        super.onDestroy();
        globalHelper.work=false;
        stopService(intent);
        unregisterReceiver(myReciever);
    }

    @Override
    protected void onStop()
    {
        try
        {
            stopService(intent);
            unregisterReceiver(myReciever);
            globalHelper.work=false;
        }
        catch (Exception e)
        {

        }
        super.onStop();
        try
        {
            stopService(intent);
            unregisterReceiver(myReciever);
            globalHelper.work=false;
        }
        catch (Exception e)
        {

        }
    }

    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null)
        {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE))
            {
                if (serviceName.equals(service.service.getClassName()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void killService()
    {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName()))
                {

                }
            }
        }

    }
    //region Custom JavaScript Interface
    private class WebAppInterface
    {
        Context mContext;

        WebAppInterface(Context c)
        {
            mContext = c;
        }

        @android.webkit.JavascriptInterface
        public void showToast1(String str)
        {
            try
            {
                textView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        btn2.performClick();
                    }
                });
            }
            catch (Exception e)
            {

            }
        }

        @android.webkit.JavascriptInterface
        public void showToast(String toast)
        {

            Toast.makeText(mContext, "Распознование Активированно", Toast.LENGTH_SHORT).show();

            try
            {
                textView.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textView.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    btn.performClick();
//                                    registerReceiver(myReciever,intentFilter);
//                                    textView.setGravity(Gravity.LEFT|Gravity.TOP);
//                                    textView.setText("");
//                                    startService(intent);
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, "run: ____-----___----"+e.getMessage() );
                                }
                            }
                        });
                    }
                });
            }
            catch (Exception e)
            {
                Log.e(TAG, "showToast: Exxception  ---- "+e.getMessage() );
            }
        }
    }
    //endregion



    private class MyReciever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String text = intent.getStringExtra("text");
            String partial = intent.getStringExtra("partialtext");

            if(!TextUtils.isEmpty(text))
            {
                textView.setText(text);
                lastFull=text;
            }
            else if(!TextUtils.isEmpty(partial))
            {
                String newStr = lastFull+partial;

                if(newStr.length()>320)
                {
                    int partLengt = partial.length();
                    newStr=newStr.substring(partLengt);
                    try
                    {
                        lastFull = lastFull.substring(partLengt);
                    }
                    catch (Exception e)
                    {
                        lastFull = "";
                    }
                }
                textView.setText(newStr);

            }
//            else if(!TextUtils.isEmpty(partial))
//                {
//                    if(!TextUtils.isEmpty(lastPartial))
//                    {
//                        for (int a =0;a<lastPartial.length();a++)
//                        {
//                            String char1 = partial.substring(a,a+1);
//                            String char2 = lastPartial.substring(a,a+1);
//
//                            if(char1.equals(char2))
//                            {
//                                partial="Abrakadabra";
//                                //partial=partial.substring(a);
//                            }
//                        }
//                    }
//
//                    lastPartial=partial;
//                    textView.setText(textView.getText()+" "+partial);
//                }
//            if(!TextUtils.isEmpty(lastEdded))
//            {
//                for(int a = 0;a<lastEdded.length();a++)
//                {
//                    char c1 = text.charAt(a);
//                    char c2 = lastEdded.charAt(a);
//
//                    if(c1==c2)
//                    {
//                        int charInt = text.indexOf(c1);
//                        text.substring(charInt);
//                    }
//                }
//            }
//
//            lastEdded=text;
//
//            String currStr = textView.getText().toString();
//            String newStr = currStr+" "+ text;
//            if(newStr.length()>220)
//            {
//                int toDelete = text.length();
//                text=text.substring(toDelete);
//            }
//            textView.setText(newStr);
            //textView.setText(text);


        }
    }

}
