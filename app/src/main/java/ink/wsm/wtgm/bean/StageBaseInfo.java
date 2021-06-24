package ink.wsm.wtgm.bean;

public class StageBaseInfo {
//    private String id; //加上语言的关卡ID -> zh_CN/main_00-01
    private String stageId; //不加语言的关卡ID -> main_00-01
    private String language; //语言 -> zh_CN
    private String code; //关卡编号 -> 0-1
    private String name; //关卡名 -> 少见多怪
    private String description; //关卡简介 -> 三点方向出现了敌人的先锋部队，请部署近战干员进行拦截。
    private String dangerLevel; //危险等级 -> 精英2 LV.1
    private int spCost; //进入关卡所需理智 -> 18
    private int apFailReturn; //失败返还理智 -> 17
    private int expGain; //获得经验 -> 180

    public String getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(String dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public int getApFailReturn() {
        return apFailReturn;
    }

    public void setApFailReturn(int apFailReturn) {
        this.apFailReturn = apFailReturn;
    }

    public int getExpGain() {
        return expGain;
    }

    public void setExpGain(int expGain) {
        this.expGain = expGain;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getId() {
        return language + "/" + stageId;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSpCost() {
        return spCost;
    }

    public void setSpCost(int spCost) {
        this.spCost = spCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
