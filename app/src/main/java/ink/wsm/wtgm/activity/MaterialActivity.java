package ink.wsm.wtgm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ink.wsm.wtgm.utils.G;
import ink.wsm.wtgm.R;
import ink.wsm.wtgm.utils.PenguinStats;
import ink.wsm.wtgm.utils.SmartUtils;
import ink.wsm.wtgm.utils.SqLiteHelperUtil;
import ink.wsm.wtgm.utils.Toaster;

import static java.lang.Integer.parseInt;

public class MaterialActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView viewListStages;
    private StagesList stagesList;
    public Handler handler;
    private ArrayList stagesData = new ArrayList<>();
    private int materialId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        init();
    }

    private void init() {
        final Bundle bundle = getIntent().getExtras();
        materialId = parseInt(bundle.getString("materialId"));

        new Thread(stagesProbThread).start();

        String materialNameChinese = PenguinStats.getMaterialNameChinese(this, materialId);
        String materialNameEnglish = PenguinStats.getMaterialNameEnglish(this, materialId);
        boolean materialStarred = PenguinStats.getMaterialStarStatus(this, materialId);
        int starId = materialStarred ? R.drawable.starred : R.drawable.star;
        String materialMainName = materialNameChinese;
        if(SmartUtils.getLanguage().equals("en_US")) materialMainName = materialNameEnglish;

        TextView viewTextMaterialName = findViewById(R.id.viewTextMaterialName);
        TextView viewTextMaterialCode = findViewById(R.id.viewTextMaterialCode);
        ImageView viewImageMaterialIcon = findViewById(R.id.viewImageMaterialIcon);
        ImageButton viewButtonStar = findViewById(R.id.viewButtonStar);
        viewListStages = findViewById(R.id.viewListStages);

        String iconFlagId = "material_" + materialId;
        int iconId = getResources().getIdentifier(iconFlagId, "drawable", this.getPackageName());
        viewImageMaterialIcon.setBackgroundResource(iconId);

        viewTextMaterialName.setText(materialMainName);
        viewTextMaterialCode.setText(getString(R.string.title_material_id) + materialId);
        viewButtonStar.setBackgroundResource(starId);
        this.setTitle(getString(R.string.title_material_prob_start) + materialMainName + getString(R.string.title_material_prob_end));
        stagesList = new StagesList();
        viewListStages.setAdapter(stagesList);
        viewListStages.setOnItemClickListener(this);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                // 获取关卡列表 Handler
                if(msg.arg1 == 1){
                    TextView viewTextLoading = findViewById(R.id.viewTextLoading);
                    // 获取成功
                    if(msg.arg2 == 1){
                        stagesList.notifyDataSetChanged();
                        viewTextLoading.setVisibility(View.INVISIBLE);
                        viewListStages.setVisibility(View.VISIBLE);
                    }else{
                        viewTextLoading.setText(R.string.title_get_stages_failed);
                    }
                }

                // 更新页面底部进度文本
                if(msg.arg1 == 256){
                    TextView viewTextProgress = findViewById(R.id.viewTextProgress);
                    Bundle argBundle = msg.getData();
                    int now = argBundle.getInt("now");
                    int all = argBundle.getInt("all");
                    if(now != all){
                        String mixed = getString(R.string.title_now_is_get_1) + now
                                + getString(R.string.title_now_is_get_2) + all
                                + getString(R.string.title_now_is_get_3);
                        viewTextProgress.setText(mixed);
                    }else{
                        viewTextProgress.setVisibility(View.INVISIBLE);
                    }
                }

                return true;
            }
        });

        G.setHandlerMaterialActivity(handler);
    }

    public void onClickStar(View v) {
        ImageButton viewButtonStar = findViewById(R.id.viewButtonStar);
        boolean materialStarred = !PenguinStats.getMaterialStarStatus(this, materialId);

        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
        int count = sqlite.getTheIntValue(SqLiteHelperUtil.TABLE_NAME_MATERIALS, "count(id)", "star", "1");
        if(count > 6 && materialStarred){
            Toaster.fastHide(this, getString(R.string.title_max_can_mark_7_materials));
            return;
        }
        int starStatus = materialStarred ? 1 : 0;
        int starId = materialStarred ? R.drawable.starred : R.drawable.star;
        PenguinStats.setMaterialStarStatus(this, materialId, starStatus);
        viewButtonStar.setBackgroundResource(starId);
        Toaster.fastHide(this, materialStarred ?
                getString(R.string.toast_is_marked) : getString(R.string.toast_is_cancel_marked));
    }

    public void onClickSort(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView viewTextStageId = view.findViewById(R.id.viewTextStageId);
        String stageId = viewTextStageId.getText().toString();
        Intent intent = new Intent();
        intent.setClass(this, StageInfoActivity.class);
        intent.putExtra("stageId", stageId);
        startActivity(intent);
    }

    class StagesList extends BaseAdapter {
        @Override
        public int getCount() {
            return stagesData.size();
        }

        @Override
        public Object getItem(int position) {
            return stagesData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MaterialActivity.this, R.layout.stages_list_layout, null);

            TextView viewTextStageTitle = view.findViewById(R.id.viewTextStageTitle);
            TextView viewTextStageCode = view.findViewById(R.id.viewTextStageCode);
            TextView viewTextStageId = view.findViewById(R.id.viewTextStageId);
            TextView viewTextProb = view.findViewById(R.id.viewTextProb);
            TextView viewTextAlgo = view.findViewById(R.id.viewTextAlgo);
            TextView viewTextRank = view.findViewById(R.id.viewTextRank);

            ArrayList stageData = (ArrayList) stagesData.get(position);
            String stageId = stageData.get(0).toString();
            stageId = stageId.replace("_rep", "");
            stageId = stageId.replace("_perm", "");
            float rateProb = Float.parseFloat(stageData.get(1).toString());
            float sanityIndex = Float.parseFloat(stageData.get(2).toString());
            String stageName = stageData.get(3).toString();
            String stageCode = stageData.get(4).toString();

            String rank = Integer.toString(position + 1);

            if(stageCode != stageName) viewTextStageTitle.setText("（" + stageName + "）");
            viewTextStageCode.setText(stageCode);
            viewTextStageId.setText(stageId);
            viewTextProb.setText(rateProb + "%");
            viewTextAlgo.setText(String.valueOf(sanityIndex));

            if(position  < 9) viewTextRank.setText(rank);
            if(position == 0) viewTextRank.setTextColor(getResources().getColor(R.color.rankNo01));
            if(position == 1) viewTextRank.setTextColor(getResources().getColor(R.color.rankNo02));
            if(position == 2) viewTextRank.setTextColor(getResources().getColor(R.color.rankNo03));

            return view;
        }
    }

    Runnable stagesProbThread = new Runnable() {
        @Override
        public void run() {
            Message message = new Message();
            message.arg1 = 1;
            try {
                message.arg2 = 1;
                PenguinStats.setActivityHandlerArg(256);
                stagesData = PenguinStats.getMatrixArrayListData(materialId, false);
            } catch (Exception e) {
                e.printStackTrace();
                message.arg2 = 0;
            }
            handler.sendMessage(message);
        }
    };
}