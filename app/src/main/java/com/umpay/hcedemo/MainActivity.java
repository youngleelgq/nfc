package com.umpay.hcedemo;

import android.content.ComponentName;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    @InjectView(R.id.card_no)
    TextView cardNo;
    @InjectView(R.id.card_balance)
    TextView cardBalance;
    @InjectView(R.id.card_city)
    TextView cardCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        StringBuilder sb = new StringBuilder();
        sb.append(cardNo.getText().toString() + ",").append(cardBalance.getText().toString() + ",")
                .append(cardCity.getText().toString());
        AccountStorage.SetAccount(this, sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 判断当前HCE应用是否是默认支付程序以及如何设置成默认支付程序的方法
     */
    private void checkIsDefaultApp() {
        CardEmulation cardEmulationManager = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(this));
        ComponentName paymentServiceComponent = new ComponentName(getApplicationContext(), MyHostApduService.class.getCanonicalName());
        if (!cardEmulationManager.isDefaultServiceForCategory(paymentServiceComponent, CardEmulation.CATEGORY_PAYMENT)) {
            Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
            intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
            intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, paymentServiceComponent);
            startActivityForResult(intent, 0);
            Log.d("TAG", "当前应用不是默认支付，需手动设置");
        } else {
            Log.d("TAG", "当前应用是系统默认支付程序");
        }
    }
}
