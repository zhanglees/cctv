package com.cctv.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import com.nativec.tools.ModuleManager;
import com.util.PreferenceUtil;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private static final boolean DEBUG = true;

    protected boolean mSwitchFlag = false;
    private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtil.init(this);
        switchLanguage(PreferenceUtil.getString("language", "zh"));
    }

    /**
     * Changing the language of application through setting the local configuration.
     *
     * @param language
     */
    protected void switchLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);

        PreferenceUtil.commitString("language", language);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVirtualKeyListenerBroadcastReceiver = new VirtualKeyListenerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        this.registerReceiver(mVirtualKeyListenerBroadcastReceiver, intentFilter);
        if (mSwitchFlag) {
            ModuleManager.newInstance().setUHFStatus(true);
        }
    }



    private class VirtualKeyListenerBroadcastReceiver extends BroadcastReceiver {
        private final String SYSTEM_REASON = "reason";
        private final String SYSTEM_HOME_KEY = "homekey";
        private final String SYSTEM_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String systemReason = intent.getStringExtra(SYSTEM_REASON);
                if (systemReason != null) {
                    if (systemReason.equals(SYSTEM_HOME_KEY)) {
                        System.out.println("Press HOME key");
                    } else if (systemReason.equals(SYSTEM_RECENT_APPS)) {
                        System.out.println("Press RECENT_APPS key");
                        ModuleManager.newInstance().setUHFStatus(false);
                        mSwitchFlag = true;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(mVirtualKeyListenerBroadcastReceiver);
    }
}