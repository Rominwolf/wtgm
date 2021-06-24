package ink.wsm.wtgm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ink.wsm.wtgm.utils.G;
import ink.wsm.wtgm.R;
import ink.wsm.wtgm.bean.MaterialNameConvert;
import ink.wsm.wtgm.utils.PenguinStats;
import ink.wsm.wtgm.utils.SmartUtils;
import ink.wsm.wtgm.utils.SqLiteHelperUtil;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private String languageOld = null;
    public Handler handler;
    SqLiteHelperUtil sqlite;
    private ArrayList starData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread(threadShowStarList).start();

        Button buttonLogin = findViewById(R.id.viewLogin);
        String[] userInfo = G.getUserInfo();
        if(userInfo[0] != null){buttonLogin.setText(R.string.title_back_to_my_office);}
        else{buttonLogin.setText(R.string.stringButtonLoginOrRegister);}

        if(!SmartUtils.getLanguage().equals(this.languageOld)) refresh();
    }

    private void init() {
        initInsertSettings();

        sqlite = new SqLiteHelperUtil(this);

        String language = SmartUtils.getLanguage();
        SmartUtils.setLanguage(this, language);

        if(this.languageOld == null){
            this.languageOld = language;
            refresh();
        }

        this.languageOld = language;
        this.setTitle(R.string.app_name);

        Button buttonChooseMaterial = findViewById(R.id.viewMaterials);
        Button buttonLogin = findViewById(R.id.viewLogin);
        ImageButton buttonSetting = findViewById(R.id.viewButtonSetting);
        final ListView viewListStar = findViewById(R.id.viewListStarMaterials);
        final TextView viewTextLoading = findViewById(R.id.viewTextLoading);

        buttonChooseMaterial.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        buttonSetting.setOnClickListener(this);

        addMaterialsInfoToDatabase();
        new Thread(threadShowStarList).start();
        final StarredMaterialsAdapter starAdapter = new StarredMaterialsAdapter();
        viewListStar.setAdapter(starAdapter);
        viewListStar.setOnItemClickListener(this);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                // 更新界面底部标星的材料列表
                if(msg.arg1 == 1)
                    starAdapter.notifyDataSetChanged();
                    if(starData.size() != 0) {
                        SmartUtils.setListViewHeightBasedOnChildren(viewListStar);
                        viewTextLoading.setVisibility(View.INVISIBLE);
                    }else{
                        viewTextLoading.setText(R.string.empty_starred_list);
                    }
                return true;
            }
        });
    }

    public void initInsertSettings() {
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
        sqlite.insertSettingData("language", "auto");
        sqlite.insertSettingData("server", "CN");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if(v.getId() == R.id.viewMaterials)
            intent.setClass(MainActivity.this, ChooseMaterialsActivity.class);
        if(v.getId() == R.id.viewLogin)
            intent.setClass(MainActivity.this, LoginActivity.class);
        if(v.getId() == R.id.viewButtonSetting)
            intent.setClass(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    /* 初始化：写入所有材料的名称信息到数据库 */
    private void addMaterialsInfoToDatabase() {
        int tableCount = sqlite.getTableRows(SqLiteHelperUtil.TABLE_NAME_MATERIALS, "id");
        String materialsRaw = SmartUtils.getFromAssets(this, "materials_name_convert.txt");
        String[] materials = materialsRaw.split("　");

        if(tableCount == materials.length) {
            Log.i("Materials", "已写入所有材料数据: " + tableCount);
            return;
        }

        for(String oneRow : materials){
            String[] contents = oneRow.split(",");
            MaterialNameConvert materialNameConvert = new MaterialNameConvert();
            materialNameConvert.setId(parseInt(contents[0]));
            materialNameConvert.setNameChinese(contents[1]);
            materialNameConvert.setNameEnglish(contents[2]);
            sqlite.insertMaterialInfo(materialNameConvert);
        }
    }

    Runnable threadShowStarList = new Runnable() {
        @Override
        public void run() {
            Message message = new Message();
            message.arg1 = 1;
            starData = PenguinStats.getStarredMaterialsArrayListData();
            handler.sendMessage(message);
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView viewTextMaterialId = view.findViewById(R.id.viewTextMaterialId);
        String materialId = viewTextMaterialId.getText().toString();
        Intent intent = new Intent(this, MaterialActivity.class);
        intent.putExtra("materialId", materialId);
        startActivity(intent);
    }

    class StarredMaterialsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return starData.size();
        }

        @Override
        public Object getItem(int position) {
            return starData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.main_star_materials_list_layout, null);

            ImageView viewImageIcon = view.findViewById(R.id.viewImageMaterialIcon);
            TextView viewTextMaterialName = view.findViewById(R.id.viewTextMaterialName);
            TextView viewTextStageCode = view.findViewById(R.id.viewTextStageCode);
            TextView viewTextRate = view.findViewById(R.id.viewTextProb);
            TextView viewTextMaterialId = view.findViewById(R.id.viewTextMaterialId);

            ArrayList oneStarData = (ArrayList) starData.get(position);
            String materialId = oneStarData.get(0).toString();
            String materialName = oneStarData.get(1).toString();
            String stageCode = oneStarData.get(2).toString();
            String rate = oneStarData.get(3).toString();

            String iconFlagId = "material_" + materialId;
            int iconId = getResources().getIdentifier(iconFlagId, "drawable", G.getPackagerName());

            viewImageIcon.setBackgroundResource(iconId);
            viewTextMaterialId.setText(materialId);
            viewTextMaterialName.setText(materialName);
            viewTextRate.setText(rate);
            viewTextStageCode.setText(stageCode);
            return view;
        }
    }

    private void refresh() {
        ViewGroup vg = findViewById(R.id.viewLayoutMain);
        vg.invalidate();
        setContentView(R.layout.activity_main);
        init();
    }
}