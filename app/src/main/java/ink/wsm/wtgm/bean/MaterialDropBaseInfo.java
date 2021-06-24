package ink.wsm.wtgm.bean;

public class MaterialDropBaseInfo {
    private int id; //材料ID
    private double rate; // 材料掉落概率
    private double sanity; //材料理智指数
    private String name; //材料名称
    private String stageName; //关卡名字
    private String stageId; //关卡ID
    private String stageCode; // 关卡编号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getSanity() {
        return sanity;
    }

    public void setSanity(double sanity) {
        this.sanity = sanity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }
}
