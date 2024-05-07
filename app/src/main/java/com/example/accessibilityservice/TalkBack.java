package com.example.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.Locale;

public class TalkBack extends AccessibilityService implements TextToSpeech.OnInitListener {
    private static final String TAG = "TalkBack";
    private TextToSpeech textToSpeech;
    public static final String EXTRA_SPEAK = "extra_speak";
    @Override
    public void onCreate() {
        super.onCreate();
        textToSpeech = new TextToSpeech(this, this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "onAccessibilityEvent: " );
        if (event.getEventType() == AccessibilityEvent.TYPE_ANNOUNCEMENT) {
            CharSequence text = event.getText().toString();
            Log.d(TAG, "Received text: " + text);
            // You can perform additional actions with the received text here
            speakText(text.toString());
        }
    }

    @Override
    public void onInterrupt() {
        Log.e(TAG,"onInterrupt: Something went wrong");
    }

    @Override
    protected void onServiceConnected() {
//        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_ANNOUNCEMENT;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(info);

    }
    private void speakText(String text) {
        if (textToSpeech != null && textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
            textToSpeech.setLanguage(Locale.US);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e(TAG, "TextToSpeech not initialized or language not available");
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language not supported");
            }
        } else {
            Log.e(TAG, "Initialization failed");
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(EXTRA_SPEAK)) {
            String textToSpeak = intent.getStringExtra(EXTRA_SPEAK);
            // Use the TextToSpeech engine to speak the text
            speakText(textToSpeak);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
