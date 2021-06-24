package ink.wsm.wtgm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ink.wsm.wtgm.R;
import ink.wsm.wtgm.bean.StageBaseInfo;
import ink.wsm.wtgm.utils.StagesUtil;

public class StageInfoActivity extends AppCompatActivity {
    StageBaseInfo stageBaseInfo = new StageBaseInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_info);
        Intent getIntent = getIntent();
        String stageId = getIntent.getStringExtra("stageId");
        insertStageInfoToBase(stageId);

        this.setTitle(stageBaseInfo.getName());

        TextView viewTextCode = findViewById(R.id.viewTextCode);
        TextView viewTextId = findViewById(R.id.viewTextId);
        TextView viewTextDangerLevel = findViewById(R.id.viewTextDangerLevel);
        TextView viewTextApCost = findViewById(R.id.viewTextApCost);
        TextView viewTextApFailReturn = findViewById(R.id.viewTextApFailReturn);
        TextView viewTextExpGain = findViewById(R.id.viewTextExpGain);
        TextView viewTextDescription = findViewById(R.id.viewTextDescription);

        viewTextCode.setText(stageBaseInfo.getCode());
        viewTextId.setText(stageBaseInfo.getStageId());
        viewTextDangerLevel.setText(stageBaseInfo.getDangerLevel());
        viewTextDescription.setText(stageBaseInfo.getDescription());
        viewTextApCost.setText(String.valueOf(stageBaseInfo.getSpCost()));
        viewTextApFailReturn.setText(String.valueOf(stageBaseInfo.getApFailReturn()));
        viewTextExpGain.setText(String.valueOf(stageBaseInfo.getExpGain()));
    }

    private void insertStageInfoToBase(String stageId) {
        StagesUtil stagesUtil = new StagesUtil();
        stagesUtil.setStageId(stageId);
        stagesUtil.setCompulsory(true);
        stagesUtil.getStageData();

        stageBaseInfo.setSpCost(stagesUtil.getStageApCost());
        stageBaseInfo.setDescription(stagesUtil.getStageDescription());
        stageBaseInfo.setName(stagesUtil.getStageName());
        stageBaseInfo.setCode(stagesUtil.getStageCode());
        stageBaseInfo.setExpGain(stagesUtil.getExpGain());
        stageBaseInfo.setDangerLevel(stagesUtil.getDangerLevel());
        stageBaseInfo.setStageId(stagesUtil.getStageId());
        stageBaseInfo.setApFailReturn(stagesUtil.getApFailReturn());
        stageBaseInfo.setLanguage(stagesUtil.getLanguage());
    }
}