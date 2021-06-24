package ink.wsm.wtgm.utils;

import android.content.Context;

import org.json.JSONObject;

import ink.wsm.wtgm.bean.StageBaseInfo;

import static java.lang.Integer.parseInt;

public class StagesUtil {
    private StageBaseInfo stageBaseInfo = new StageBaseInfo();
    private Context context;
    private String fileStage;
    private String language;
    private String stageId;
    private boolean isCompulsory = false;//强制从文件中查询关卡信息

    public StagesUtil() {
        this.context = G.getContext();
        this.language = SmartUtils.getLanguage();
        this.fileStage = "stages_table/" + language + ".json";
    }

    /* 获取关卡 JSONObject 数据 */
    public JSONObject getStageData() {
        if(getStageInfoFromDatabase() && !isCompulsory) return null;

        String stageId = this.stageId;
        String stagesData = SmartUtils.getFromAssets(this.context, this.fileStage);
        JSONObject stagesList, stage;
        try {
            assert stagesData != null;
            stagesList = new JSONObject(stagesData).getJSONObject("stages");
            stage = stagesList.getJSONObject(stageId);
            stageBaseInfo.setLanguage(this.language);
            stageBaseInfo.setCode(stage.getString("code"));
            stageBaseInfo.setDescription(stage.getString("description"));
            stageBaseInfo.setName(stage.getString("name"));
            stageBaseInfo.setSpCost(stage.getInt("apCost"));
            stageBaseInfo.setApFailReturn(stage.getInt("apFailReturn"));
            stageBaseInfo.setStageId(stage.getString("stageId"));
            stageBaseInfo.setDangerLevel(stage.getString("dangerLevel"));
            stageBaseInfo.setExpGain(stage.getInt("expGain"));
            addStageInfoToDatabase();
        } catch (Exception e) {
            stage = null;
            e.printStackTrace();
        }
        return stage;
    }

    /* 设置欲查询的关卡ID */
    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    /* 设置强制性获取 Flag */
    public void setCompulsory(boolean compulsory) {
        this.isCompulsory = compulsory;
    }

    /* 获取语言 */
    public String getLanguage() {
        return stageBaseInfo.getLanguage();
    }

    /* 获取关卡ID */
    public String getStageId() {
        return stageBaseInfo.getStageId();
    }

    /* 获取关卡名字 */
    public String getStageName() {
        return stageBaseInfo.getName();
    }

    /* 获取关卡编号 */
    public String getStageCode() {
        return stageBaseInfo.getCode();
    }

    /* 获取关卡的简介 */
    public String getStageDescription() {
        return stageBaseInfo.getDescription();
    }

    /* 获取危险等级 */
    public String getDangerLevel() {
        return stageBaseInfo.getDangerLevel();
    }

    /* 获取进入关卡所需要的 Cost */
    public int getStageApCost() {
        return stageBaseInfo.getSpCost();
    }

    /* 获取失败返还理智 */
    public int getApFailReturn() {
        return stageBaseInfo.getApFailReturn();
    }

    /* 获取获得经验值 */
    public int getExpGain() {
        return stageBaseInfo.getExpGain();
    }

    /* 写入一个关卡的数据到数据库 */
    private void addStageInfoToDatabase() {
        if(getStageInfoFromDatabase()) return;

        SqLiteHelperUtil sqLiteHelperUtil = new SqLiteHelperUtil(G.getContext());

        StageBaseInfo stageBaseInfo = new StageBaseInfo();
        stageBaseInfo.setCode(getStageCode());
        stageBaseInfo.setDescription(getStageDescription());
        stageBaseInfo.setName(getStageName());
        stageBaseInfo.setSpCost(getStageApCost());
        stageBaseInfo.setLanguage(language);
        stageBaseInfo.setStageId(stageId);

        sqLiteHelperUtil.insertStageInfo(stageBaseInfo);
    }

    /* 从数据库中获取指定关卡的数据，如果存在则返回真并填写数据到成员变量，否则返回假 */
    private boolean getStageInfoFromDatabase() {
        SqLiteHelperUtil sqLiteHelperUtil = new SqLiteHelperUtil(G.getContext());
        StageBaseInfo stageBaseInfoInner = sqLiteHelperUtil.getStageBaseInfo(language, stageId);
        if(stageBaseInfoInner.getCode() == null) return false;
        stageBaseInfo.setCode(stageBaseInfoInner.getCode());
        stageBaseInfo.setName(stageBaseInfoInner.getName());
        stageBaseInfo.setDescription(stageBaseInfoInner.getDescription());
        stageBaseInfo.setSpCost(stageBaseInfoInner.getSpCost());
        return true;
    }
}
