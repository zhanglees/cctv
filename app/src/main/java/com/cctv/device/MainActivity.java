package com.cctv.device;

import android.content.Intent;
import android.os.Bundle;

import com.cctv.activity.CarLoadActivity;
import com.cctv.activity.PreSetTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.module.interaction.ReaderHelper;
import com.nativec.tools.ModuleManager;
import com.nativec.tools.SerialPort;
import com.nativec.tools.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import com.cctv.UHFApplication;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderDataPackageParser;
import com.rfid.ReaderDataPackageProcess;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "COONECTRS232";
    private static final String TTYS1 = "ttyS4 (rk_serial)";
    private static final boolean DEBUG = true;

    private ReaderHelper mReaderHelper;

    private List<String> mPortList = new ArrayList<String>();

    private TextView mPortTextView, mBaud115200View, mBaud38400View;
    private TableRow mDropPort;
   // private SpinerPopWindow mSpinerPort;

    private int mPosPort = -1;

    private SerialPortFinder mSerialPortFinder;

    String[] entries = null;
    String[] entryValues = null;

    public static SerialPort mSerialPort = null;
    private int baud = 115200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        Button btn1 = findViewById(R.id.btnloding);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this,"设备装载",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //intent.setClass(MainActivity.this, MainActivityList.class);
                //intent.putExtra("tasknum", "test");
                intent.putExtra("type", "load");

                intent.setClass(MainActivity.this, PreSetTask.class);
                //intent.setClass(MainActivity.this, CarLoadActivity.class);
                startActivity(intent);
            }
        });

        Button btn2 = findViewById(R.id.btnUnloding);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this,"卸载设备",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("type", "unload");
                intent.setClass(MainActivity.this, PreSetTask.class);
                startActivity(intent);
            }
        });

        Button btn3 = findViewById(R.id.btnReadWrite);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this,"写标签",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
               // intent.putExtra("type", "unload");
                intent.setClass(MainActivity.this, MainActivityList.class);
                startActivity(intent);
            }
        });

        Button btn4 = findViewById(R.id.btnDetail);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this,"装车详细",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                // intent.putExtra("type", "unload");
                intent.setClass(MainActivity.this, UnLoding.class);
                startActivity(intent);
            }
        });
        try {
            ((UHFApplication) getApplication()).addActivity(this);
            mSerialPortFinder = new SerialPortFinder();
            entries = mSerialPortFinder.getAllDevices();
            entryValues = mSerialPortFinder.getAllDevicesPath();


            ModuleManager.newInstance().setUHFStatus(true);
            String[] lists = entries;
            for (int i = 0; i < lists.length; i++) {
                mPortList.add(lists[i]);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ModuleManager.newInstance().setUHFStatus(false);
            mPosPort = mPortList.indexOf(TTYS1.toString());
            if (DEBUG)
                Log.e(TAG, "test the value of mPosPort::" + mPosPort);

            if (mPosPort < 0) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.rs232_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                mSerialPort = new SerialPort(new File(entryValues[mPosPort]), baud, 0);

                if (DEBUG)
                    Log.e(TAG, "ttys1 value :::" + entryValues[0]);

                try {
                    mReaderHelper = RFIDReaderHelper.getDefaultHelper();
                    mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream(), new ReaderDataPackageParser(), new ReaderDataPackageProcess());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (!ModuleManager.newInstance().setUHFStatus(true)) {
                    throw new RuntimeException("UHF RFID power on failure,may you open in other" +
                            " Process and do not exit it");
                }



                //finish();
            } catch (SecurityException e) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.error_security),
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            } catch (InvalidParameterException e) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.error_configuration),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception ex) {

        }


    }

}