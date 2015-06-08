package com.shaddock.batterycontrol;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BCActivity extends Activity {
    
    private TextView mTV = null;
    private Button mButton = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);

        mTV = (TextView)findViewById(R.id.tv_power);
        mButton = (Button)findViewById(R.id.btn_gotosetting);
        
        mButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean result = gotoBatterySettings();
                if(!result) {
                    gotoFail();
                }
            }});
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGovernor();
    }

    private void updateGovernor() {
        StringBuilder sb = new StringBuilder(10);
        sb.append(this.getString(R.string.cur_governor) + " - " + Governor.getCurrent() + "\n\n");
        sb.append(this.getString(R.string.all_governor) + "\n\n");
        String all[] = Governor.getAll();
        if(all != null) {
            for(String x : all) {
                sb.append(x + "\n");
            }
        }

        sb.append(getString(R.string.governor_desp0));
        sb.append(getString(R.string.governor_desp1));
        sb.append(getString(R.string.governor_desp2));
        sb.append(getString(R.string.governor_desp3));
        sb.append(getString(R.string.governor_desp4));
        sb.append(getString(R.string.governor_desp5));
        sb.append(getString(R.string.governor_desp6));
        
        mTV.setText(sb.toString());
    }

    private boolean gotoBatterySettings() {
        return Governor.gotoBatterySettings(this);
    }

    private void gotoFail(){
        Toast.makeText(this, R.string.goto_fail, Toast.LENGTH_LONG).show();
    }

}
