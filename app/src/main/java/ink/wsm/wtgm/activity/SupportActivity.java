package ink.wsm.wtgm.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ink.wsm.wtgm.R;
import ink.wsm.wtgm.bean.SupportBaseInfo;
import ink.wsm.wtgm.utils.SmartUtils;
import ink.wsm.wtgm.utils.Toaster;

public class SupportActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<SupportBaseInfo> supportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        this.setTitle(R.string.stringTitleSupport);
        getSupportData();

        ListView viewList = findViewById(R.id.viewListSupport);
        SupportAdapter supportAdapter = new SupportAdapter();
        viewList.setAdapter(supportAdapter);
        viewList.setOnItemClickListener(this);
    }

    public void getSupportData() {
        String supportFilePath = "supports/" + SmartUtils.getLanguage() + ".json";
        String supportData = SmartUtils.getFromAssets(this, supportFilePath);
        List<SupportBaseInfo> supportBaseInfos = new ArrayList<>();
        try {
            JSONArray supportList = new JSONArray(supportData);
            for(int i = 0; i < supportList.length(); i++){
                JSONObject support = supportList.getJSONObject(i);
                SupportBaseInfo supportBaseInfo = new SupportBaseInfo();
                supportBaseInfo.setType(support.getString("type"));

                if(!support.isNull("content"))
                    supportBaseInfo.setContent(support.getString("content"));
                if(!support.isNull("avatar_link"))
                    supportBaseInfo.setAvatarLink(support.getString("avatar_link"));
                if(!support.isNull("url"))
                    supportBaseInfo.setUrl(support.getString("url"));
                if(!support.isNull("toast"))
                    supportBaseInfo.setToast(support.getString("toast"));

                supportBaseInfos.add(supportBaseInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.supportList = supportBaseInfos;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView viewTextToast = view.findViewById(R.id.viewTextToast);
        TextView viewTextUrl = view.findViewById(R.id.viewTextUrl);
        String toast = viewTextToast.getText().toString();
        String url = viewTextUrl.getText().toString();

        if(!toast.isEmpty()) Toaster.slowHide(this, toast);
        if(!url.isEmpty()) SmartUtils.visitUrl(url);
    }

    class SupportAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return supportList.size();
        }

        @Override
        public Object getItem(int position) {
            return supportList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(SupportActivity.this, R.layout.support_item_layout, null);

            ImageView viewImage = view.findViewById(R.id.viewImage);
            TextView viewText = view.findViewById(R.id.viewText);
            TextView viewTextToast = view.findViewById(R.id.viewTextToast);
            TextView viewTextUrl = view.findViewById(R.id.viewTextUrl);
            ImageView viewIsLink = view.findViewById(R.id.viewIsLink);

            String type = supportList.get(position).getType();
            String content = supportList.get(position).getContent();
            String avatarLink = supportList.get(position).getAvatarLink();
            String toast = supportList.get(position).getToast();
            String url = supportList.get(position).getUrl();

            viewTextUrl.setText(url);
            viewTextToast.setText(toast);
            viewText.setText(content);

            Typeface sourceBold = Typeface.create("source_bold", Typeface.BOLD);
            int colorMainTitle = getResources().getColor(R.color.mainTitle);

            if (type.equals("title")){
                viewText.setTextSize(18);
                viewText.setTypeface(sourceBold);
                viewText.setTextColor(colorMainTitle);
            }

            if (url != null) viewIsLink.setVisibility(View.VISIBLE);

            return view;
        }
    }
}