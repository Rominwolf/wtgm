package ink.wsm.wtgm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import ink.wsm.wtgm.R;
import ink.wsm.wtgm.utils.SmartUtils;
import ink.wsm.wtgm.utils.SqLiteHelperUtil;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.setTitle(R.string.stringTitleSetting);

        Spinner viewSpinnerLanguage = findViewById(R.id.viewSpinnerLanguage);
        Spinner viewSpinnerServer = findViewById(R.id.viewSpinnerServer);

        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);

        String language = sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_SETTINGS,
                "v", "k", "'language'");
        viewSpinnerLanguage.setSelection(getSpinnerArrayPosition(R.array.spinnerLanguage, language));

        String server = sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_SETTINGS,
                "v", "k", "'server'");
        viewSpinnerServer.setSelection(getSpinnerArrayPosition(R.array.spinnerServer, server));

        viewSpinnerLanguage.setOnItemSelectedListener(this);
        viewSpinnerServer.setOnItemSelectedListener(this);
    }

    public void onClickJumpToAbout(View v) {
        Intent intent = new Intent();
        intent.setClass(this, SupportActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if(parent.getId() == R.id.viewSpinnerLanguage){
            String result = this.getResources().getStringArray(R.array.spinnerLanguage)[position];
            result = SmartUtils.getSubStringBetween(result, "[", "]");
            ContentValues values = new ContentValues();
            values.put("v", result);
            String[] args = new String[]{"language"};
            SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
            sqlite.updateData(SqLiteHelperUtil.TABLE_NAME_SETTINGS, values, "k = ?", args);
            SmartUtils.setLanguage(this, result);
            //SmartUtils.restartApp();
        }

        if(parent.getId() == R.id.viewSpinnerServer){
            String result = this.getResources().getStringArray(R.array.spinnerServer)[position];
            result = SmartUtils.getSubStringBetween(result, "[", "]");
            ContentValues values = new ContentValues();
            values.put("v", result);
            String[] args = new String[]{"server"};
            SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
            sqlite.updateData(SqLiteHelperUtil.TABLE_NAME_SETTINGS, values, "k = ?", args);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /* 获取 arrays.xml 中指定 ID 的指定 key 的位置 */
    public int getSpinnerArrayPosition(int arrayId, String key) {
        int result = 0;
        String keyEncode = "[" + key + "]";
        String[] array = this.getResources().getStringArray(arrayId);
        for(int i = 0; i < array.length; i++){
            String oneRow = array[i];
            if(!oneRow.contains(keyEncode)) continue;;
            result = i;
            break;
        }
        return result;
    }
}