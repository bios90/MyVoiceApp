package com.example.bios90.voicedemo10;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MyService extends Service implements SpeechDelegate, Speech.stopDueToDelay
{
    private static final String TAG = "MyService";
    final static String CONNECTION ="VoiceDemoConnection";

    public static SpeechDelegate delegate;
    String text="";
    Locale locale;

    GlobalHelper globalHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent.getAction().equals(GlobalHelper.START))
        {
            ((AudioManager) Objects.requireNonNull(getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);

            //Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
            Speech.init(this);
            delegate = this;
            Speech.getInstance().setListener(this);

            globalHelper = (GlobalHelper) getApplicationContext();

            locale = new Locale("en");
//        Locale.setDefault(locale);
//        Configuration config = getBaseContext().getResources().getConfiguration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());

            if (Speech.getInstance().isListening())
            {
                muteBeepSoundOfRecorder();
                Speech.getInstance().stopListening();
            } else
            {
                System.setProperty("rx.unsafe-disable", "True");
                try
                {
                    Speech.getInstance().stopTextToSpeech();
                    Speech.getInstance().setLocale(locale).startListening(null, this);
                    //Toast.makeText(this, "Listener Setted", Toast.LENGTH_SHORT).show();
                } catch (Exception e)
                {

                }
                muteBeepSoundOfRecorder();
            }
        }
        else if(intent.getAction().equals(GlobalHelper.STOP))
        {
            stopForeground(true);
            stopSelf();
        }
        //return Service.START_STICKY;
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onSpecifiedCommandPronounced(String event)
    {
        try
        {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().stopListening();
            Speech.getInstance().setLocale(locale).startListening(null, this);
        }
        catch (Exception e)
        {

        }
        //((AudioManager) Objects.requireNonNull(getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
//        if (Speech.getInstance().isListening())
//        {
//            muteBeepSoundOfRecorder();
//            Speech.getInstance().stopListening();
//        }
//        else
//        {
        try
        {
            Log.e(TAG, "onSpecifiedCommandPronounced: SPEFSD COMMANDDDD" );
//            Speech.getInstance().stopTextToSpeech();
//            Speech.getInstance().setLocale(locale).startListening(null, this);
        }
        catch (Exception e)
        {

        }
        muteBeepSoundOfRecorder();
        //}
    }

    @Override
    public void onStartOfSpeech()
    {
        Log.e(TAG, "onStartOfSpeech: ");

        if(globalHelper.work==false)
        {
            stopSelf();
        }
    }

    @Override
    public void onSpeechRmsChanged(float value)
    {

    }

    @Override
    public void onSpeechPartialResults(List<String> results)
    {

        text = results.get(0);//reBuildedText(results.get(0));//reBuildedText(result);
        if(!TextUtils.isEmpty(text))
        {
            Intent intent = new Intent();
            intent.setAction(CONNECTION);

            intent.putExtra("partialtext", text);
            sendBroadcast(intent);
        }
//        for(String partial :results)
//        {
//            if(!TextUtils.isEmpty(partial))
//            {
//                text = reBuildedText(partial);
//                Intent intent = new Intent();
//                intent.setAction(CONNECTION);
//
//                intent.putExtra("text", text);
//                sendBroadcast(intent);
//            }
//        }
    }

    @Override
    public void onSpeechResult(String result)
    {
//        if(!TextUtils.isEmpty(result))
//        {
//            text = reBuildedText(result);
//            Intent intent = new Intent();
//            intent.setAction(CONNECTION);
//
//            intent.putExtra("text", text);
//            sendBroadcast(intent);
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void muteBeepSoundOfRecorder()
    {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent)
//    {
//        PendingIntent service = PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
//                new Intent(getApplicationContext(), MyService.class), PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        assert alarmManager != null;
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
//        super.onTaskRemoved(rootIntent);
//        super.onTaskRemoved(rootIntent);
//    }


    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        onDestroy();
    }

    @Override
    public void onDestroy()
    {
        stopSelf();
        super.onDestroy();
        stopSelf();
    }

    private String reBuildedText(String str)
    {
        if(!TextUtils.isEmpty(str))
        {
            str=str.substring(0,1).toUpperCase()+str.substring(1);
            text +=str+" ";
        }
        if(text.length()>320)
        {
            int toDelete = str.length();
            text=text.substring(toDelete);
        }
        return text;
    }
}
