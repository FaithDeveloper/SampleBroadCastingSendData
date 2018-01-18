package kr.co.kcs.packagetestserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = ((TextView)findViewById(R.id.txt_data));
        ((Button)findViewById(R.id.btn_get_data)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format("[SendData]\n[Last Time] %s" ,Utils.getDatetime(MainActivity.this)));

                        sendDateTime(MainActivity.this, Utils.getCurrentTime());
                    }
                });
            }
        });
    }

    //Broadcast 전송
    public void sendDateTime(Context ctx, String input) {
        sendBroadcast(new Intent(TestBroadcastReceiver.BROADCAST_CLIENT0_ONE).putExtra(TestBroadcastReceiver.KEY_DATE, input));
        sendBroadcast(new Intent(TestBroadcastReceiver.BROADCAST_CLIENT0_TWO).putExtra(TestBroadcastReceiver.KEY_DATE, input));
    }
}
