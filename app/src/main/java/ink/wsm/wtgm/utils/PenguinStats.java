package ink.wsm.wtgm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ink.wsm.wtgm.bean.MaterialDropBaseInfo;

import static ink.wsm.wtgm.utils.HttpUtil.*;

public class PenguinStats {
    private static int handlerArg = 0;
    private static String host = "https://penguin-stats.io/";
    private static String hostApi = host + "PenguinStats/api/v2/";
    private static String hostApiMatrix = hostApi + "result/matrix";

    /* 理智概率算法：根据关卡所需理智和该关材料的掉落概率重新分析概率的一种算法，重新排序 */
    private static JSONArray algoSanityProb(JSONArray data, boolean useAlgo) throws JSONException {
        // 如果使用该算法，则使用算法结果进行排序 -> 得分越低越靠前
        // 否则按照掉落概率排序 -> 百分比越大越靠前

        JSONArray sortedJsonArray = new JSONArray();
        String key = "rate";
        if(useAlgo) key = "sanity";

        final String finalKey = key;
        List<JSONObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            jsonValues.add(data.getJSONObject(i));
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                double valA = 0f;
                double valB = 0f;

                try {
                    valA = a.getDouble(finalKey);
                    valB = b.getDouble(finalKey);
                }
                catch (JSONException ignored) {
                }

                int result = 0;
                if(finalKey.equals("rate")) result = valA - valB >= 0 ? -1 : 1;
                if(finalKey.equals("sanity")) result = valA - valB >= 0 ? 1 : -1;

                return result;
            }
        });

        for (int i = 0; i < data.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }

        return sortedJsonArray;
    }

    /* 通过 itemId 获取指定材料的关卡掉落数据，并进行关卡掉落概率分析，删除无效关卡 */
    private static JSONArray matrixData(int itemId) throws JSONException {
        String server = getServerFlag();
        String param = "itemFilter=" + itemId + "&server=" + server;
        String result = getRequest(hostApiMatrix, param);
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("matrix");
        StagesUtil stagesUtil = new StagesUtil();

        int bundleNow = 0, bundleAll = jsonArray.length();

        for(int i=0; i < jsonArray.length(); i++){
            if(handlerArg != 0){
                bundleNow++;
                Bundle bundle = new Bundle();
                bundle.putInt("now", bundleNow);
                bundle.putInt("all", bundleAll);
                sendMessageToMaterialActivity(bundle);
            }

            JSONObject oneRow = jsonArray.getJSONObject(i);
            String stageId = oneRow.getString("stageId");
            stageId = stageId.replace("_rep", "").replace("_perm", "");
            String stageName, stageCode;
            int stageApCost;
            int quantity = oneRow.getInt("quantity");
            int times = oneRow.getInt("times");

            double rate = ((quantity * 1.0 / times * 1.0) * 100);
            BigDecimal bigDecimal = new BigDecimal(rate);
            rate = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            oneRow.put("rate", rate);

            System.out.println("---->" + stageId + "--->" + rate);
            if(stageId.contains("randomMaterial") || rate == 0){
                jsonArray.remove(i);
                i--;
                continue;
            }

            stagesUtil.setStageId(stageId);
            stagesUtil.getStageData();
            stageName = stagesUtil.getStageName();
            stageCode = stagesUtil.getStageCode();
            stageApCost = stagesUtil.getStageApCost();

            if(stageName.equals(stageCode)){
                jsonArray.remove(i);
                i--;
                continue;
            }

            double sanity = (stageApCost * 1.0 * 100 / rate * 1.0);

            if(Double.isInfinite(sanity) || Double.isNaN(sanity))
                sanity = 0;

            bigDecimal = new BigDecimal(sanity);
            sanity = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            oneRow.put("sanity", sanity);
            oneRow.put("stageName", stageName);
            oneRow.put("stageCode", stageCode);
            oneRow.put("stageApCost", stageApCost);

            jsonArray.put(i, oneRow);
        }

        return jsonArray;
    }

    /* 通过 itemID 获取指定材料的最佳关卡的掉落数据 */
    public static MaterialDropBaseInfo getTheMaterialBestDropInfo(int itemId) throws JSONException{
        String server = getServerFlag();
        String param = "itemFilter=" + itemId + "&server=" + server;
        String result = getRequest(hostApiMatrix, param);
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("matrix");

        for(int i=0; i < jsonArray.length(); i++){
            JSONObject oneRow;
            String stageId;
            int quantity, times;

            oneRow = jsonArray.getJSONObject(i);

            stageId = oneRow.getString("stageId");
            stageId = stageId.replace("_rep", "").replace("_perm", "");
            oneRow.put("stageId", stageId);

            quantity = oneRow.getInt("quantity");
            times = oneRow.getInt("times");

            double rate = ((quantity * 1.0 / times * 1.0) * 100);
            BigDecimal bigDecimal = new BigDecimal(rate);
            rate = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            oneRow.put("rate", rate);

            if(stageId.contains("randomMaterial") || rate == 0){
                jsonArray.remove(i);
                i--;
                continue;
            }
            jsonArray.put(i, oneRow);
        }

        String stageName, stageCode, stageId, itemName;
        int stageApCost;
        double rate, sanity;

        jsonArray = algoSanityProb(jsonArray, false);
        JSONObject bestMaterial = jsonArray.getJSONObject(0);
        stageId = bestMaterial.getString("stageId");
        rate = bestMaterial.getDouble("rate");

        StagesUtil stagesUtil = new StagesUtil();
        stagesUtil.setStageId(stageId);
        stagesUtil.getStageData();
        stageName = stagesUtil.getStageName();
        stageCode = stagesUtil.getStageCode();
        stageApCost = stagesUtil.getStageApCost();
        itemName = getMaterialNameChinese(G.getContext(), itemId);
        if(SmartUtils.getLanguage().equals("en_US"))
            itemName = getMaterialNameEnglish(G.getContext(), itemId);

        sanity = (stageApCost * 1.0 * 100 / rate * 1.0);
        if(Double.isInfinite(sanity) || Double.isNaN(sanity))
            sanity = 0;

        BigDecimal bigDecimal = new BigDecimal(sanity);
        sanity = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        MaterialDropBaseInfo materialDropBaseInfo = new MaterialDropBaseInfo();
        materialDropBaseInfo.setId(itemId);
        materialDropBaseInfo.setName(itemName);
        materialDropBaseInfo.setRate(rate);
        materialDropBaseInfo.setSanity(sanity);
        materialDropBaseInfo.setStageCode(stageCode);
        materialDropBaseInfo.setStageId(stageId);
        materialDropBaseInfo.setStageName(stageName);

        return materialDropBaseInfo;
    }

    private static void sendMessageToMaterialActivity(Bundle bundle) {
        Message message = new Message();
        message.arg1 = handlerArg;

        message.setData(bundle);
        Handler handler = G.getHandlerMaterialActivity();
        handler.sendMessage(message);
    }

    /* 获取文字版的材料掉落矩阵数据 */
    public static String getMatrixTextData(int itemId, boolean useAlgo) throws JSONException {
        JSONArray data = matrixData(itemId);
        StringBuilder response = new StringBuilder();
        data = algoSanityProb(data, useAlgo);

        for(int i=0; i<data.length(); i++){
            JSONObject oneRow = data.getJSONObject(i);
            String stageId = oneRow.getString("stageId");
            double rate = oneRow.getDouble("rate");
            response.append("stageId: ").append(stageId).append(", rate: ").append(rate).append("\n");
        }
        return response.toString();
    }

    /* 获取数组版的材料掉落矩阵数据 */
    public static ArrayList getMatrixArrayListData(int itemId, boolean useAlgo) throws JSONException {
        JSONArray data = matrixData(itemId);
        ArrayList response = new ArrayList();
        data = algoSanityProb(data, useAlgo);

        for(int i=0; i < data.length(); i++){
            JSONObject oneRow = data.getJSONObject(i);
            if(oneRow.isNull("stageCode")) continue;

            String stageId = oneRow.getString("stageId");
            String stageName = oneRow.getString("stageName");
            String stageCode = oneRow.getString("stageCode");
            double rate = oneRow.getDouble("rate");
            double algo = oneRow.getDouble("sanity");
            ArrayList arrayList = new ArrayList();
            arrayList.add(stageId);
            arrayList.add(rate);
            arrayList.add(algo);
            arrayList.add(stageName);
            arrayList.add(stageCode);
            response.add(arrayList);
        }

        return response;
    }

    /* 获取标星了的材料矩阵掉落列表 */
    public static ArrayList getStarredMaterialsArrayListData() {
        ArrayList response = new ArrayList();
        ArrayList starList = PenguinStats.getAllStarredMaterials();

        for(Object oneRow : starList.toArray()){
            int id = (int) oneRow;
            MaterialDropBaseInfo materialDropBaseInfo = new MaterialDropBaseInfo();
            try {
                materialDropBaseInfo = PenguinStats.getTheMaterialBestDropInfo(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(materialDropBaseInfo.getStageCode() == null) continue;

            ArrayList arrayList = new ArrayList();
            arrayList.add(materialDropBaseInfo.getId());
            arrayList.add(materialDropBaseInfo.getName());
            arrayList.add(materialDropBaseInfo.getStageCode());
            arrayList.add(materialDropBaseInfo.getRate());
            response.add(arrayList);
        }
        return response;
    }

    /* 获取材料的中文名 */
    public static String getMaterialNameChinese(Context context, int id) {
        String idString = Integer.toString(id);
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(context);
        return sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_MATERIALS, "name_chinese", "id", idString);
    }

    /* 获取材料的英文名 */
    public static String getMaterialNameEnglish(Context context, int id) {
        String idString = Integer.toString(id);
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(context);
        return sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_MATERIALS, "name_english", "id", idString);
    }

    /* 获取材料是否被标星 */
    public static boolean getMaterialStarStatus(Context context, int id) {
        String idString = Integer.toString(id);
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(context);
        int star = sqlite.getTheIntValue(SqLiteHelperUtil.TABLE_NAME_MATERIALS, "star", "id", idString);
        return star != 0;
    }

    /* 设置材料的标星状态 */
    public static void setMaterialStarStatus(Context context, int id, int status) {
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(context);
        ContentValues values = new ContentValues();
        values.put("star", status);
        String[] args = new String[]{Integer.toString(id)};
        sqlite.updateData(SqLiteHelperUtil.TABLE_NAME_MATERIALS, values, "id = ?", args);
    }

    /* 获得所有标星的材料列表 */
    public static ArrayList getAllStarredMaterials() {
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(G.getContext());
        return sqlite.getMaterialsStarredList();
    }

    /* 设置 Activity 的 Handler Arg 标识符，以供传递消息 */
    public static void setActivityHandlerArg(int arg) {
        handlerArg = arg;
    }

    /* 获取数据库中所选择的服务器 Flag */
    public static String getServerFlag() {
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(G.getContext());
        return sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_SETTINGS,
                "v", "k", "'server'");
    }
}
