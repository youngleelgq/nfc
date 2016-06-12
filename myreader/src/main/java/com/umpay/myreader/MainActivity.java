package com.umpay.myreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Resources res = getResources();
        this.res = res;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        onNewIntent(getIntent());
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    CardReader.FILTERS, CardReader.TECHLISTS);

        Log.e("NFC----", IsoDep.class.getName());
        refreshStatus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "onNewIntent:" + intent.getAction());
        showData((p != null) ? CardReader.load(p, res) : null);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshStatus();
    }

    private void refreshStatus() {
        final Resources r = this.res;

        final String tip;
        if (nfcAdapter == null)
            tip = r.getString(R.string.tip_nfc_notfound);
        else if (nfcAdapter.isEnabled())
            tip = r.getString(R.string.tip_nfc_enabled);
        else
            tip = r.getString(R.string.tip_nfc_disabled);

        final StringBuilder s = new StringBuilder(
                r.getString(R.string.app_name));

        s.append("  --  ").append(tip);
        setTitle(s);
        Log.d(TAG,s.toString());
    }

    private void showData(String data) {
        if (data == null || data.length() == 0) {

            return;
        }

        Log.d(TAG, "得到的数据为:" + data);
        String[] info = data.split(",");
        ((TextView) findViewById(R.id.rc_tv_number)).setText(info[0]);
        ((TextView) findViewById(R.id.rc_tv_city)).setText(info[2]);
        ((TextView) findViewById(R.id.rc_tv_money)).setText(info[1]);
    }

}
