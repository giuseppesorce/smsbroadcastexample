package it.warpmobile.smsbroadcast;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private String[] cellNumber = {"12345678", "1234567", "1234567"};
     private EditText etMessage;
    private int counterMessage;
    private String SENT = "SMS_SENT";
    private SmsBroadCast broadCast;
    private TextView tvReport;
    private int counterError;
    private Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViews();
        setupViews();
        changeStatusBar(getColor(R.color.colorPrimaryDark));
    }

    private void setupViews() {

       btSend.setOnClickListener(this);

    }

    private void findViews() {

        etMessage = (EditText) findViewById(R.id.etMessage);
        tvReport = (TextView) findViewById(R.id.tvReport);
        btSend = (Button) findViewById(R.id.btSend);
    }


    /**
     * <p>Init and reset sms sending </p>
     */

    private void startSendMessage() {

        String message = etMessage.getText().toString();
        if (message != null && message.length() > 5) {
            btSend.setEnabled(false);
            counterError=0;
            counterMessage = 0;
            sendSms();
        } else {
            Toast.makeText(MainActivity.this, "Inserisci almeno 5 caratteri", Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * <p>this method sends sms. </p>
     * <p>1. get numner 2.get message to edittext 3. create broadcast to receice report</p>
     */
    private void sendSms() {
        String toSend = cellNumber[counterMessage];
        String messageTosend = etMessage.getText().toString();
        Intent intentSms = new Intent(SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, intentSms, 0);
        broadCast = new SmsBroadCast();
        registerReceiver(broadCast
                , new IntentFilter(SENT));
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(toSend, null, messageTosend, sentPI, null);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.btSend){
            startSendMessage();
        }
    }


    /**
     * <p>BroadcastReciver to receiver report </p>
     */
    public class SmsBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            int resultCode = getResultCode();
            switch (resultCode) {
                case Activity.RESULT_OK:

                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    counterError++;
                    Toast.makeText(getApplicationContext(), "RESULT_ERROR_GENERIC_FAILURE", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    counterError++;
                    Toast.makeText(getApplicationContext(), "RESULT_ERROR_NO_SERVICE", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    counterError++;
                    Toast.makeText(getApplicationContext(), "RESULT_ERROR_NULL_PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    counterError++;
                    Toast.makeText(getApplicationContext(), "RESULT_ERROR_RADIO_OFF", Toast.LENGTH_SHORT).show();
                    break;
                default:

                    break;
            }
            nextMessage();
        }


    }

    /**
     * <p>after sms is sended this method increment counter and send next sms or stop</p>
     */
    private void nextMessage() {

        counterMessage++;
        tvReport.setText("Sms sended: "+counterMessage+"/"+cellNumber.length+"\nError: "+counterError);
        if (counterMessage >= cellNumber.length) {
            Toast.makeText(getApplicationContext(), "Tutti gli sms sono stati inviati", Toast.LENGTH_SHORT).show();
            btSend.setEnabled(true);
        } else {
            sendSms();
        }
    }

    /**
     * <p>change status bar color</p>
     *
     * @param color
     */
    protected void changeStatusBar(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

}
