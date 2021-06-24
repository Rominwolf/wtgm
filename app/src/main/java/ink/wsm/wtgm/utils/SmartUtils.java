package ink.wsm.wtgm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class SmartUtils {
    /* 获取 Assets 文件夹内的文件资源 */
    public static String getFromAssets(Context context, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            StringBuilder Result = new StringBuilder();
            while((line = bufReader.readLine()) != null)
                Result.append(line);
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* 根据子列表数量动态设置 ListView 的高度 */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /* 获取字符串中两个标识符直接的字符 */
    public static String getSubStringBetween(String content, String start, String end) {
        return content.substring(content.indexOf(start) + 1, content.indexOf(end));
    }

    /* 设置语言 */
    public static void setLanguage(Activity activity, String language) {
        Locale locale = Locale.getDefault();
        if(language.equals("zh_CN")) locale = Locale.CHINESE;
        if(language.equals("en_US")) locale = Locale.ENGLISH;
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static void restartApp() {
        Context context = G.getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /* 获取语言 */
    public static String getLanguage() {
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(G.getContext());
        String language = sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_SETTINGS,
                "v", "k", "'language'");
        if(language.equals("auto")){
            String country = Locale.getDefault().getCountry().toUpperCase();
            language = Locale.getDefault().getLanguage();
            language = language + "_" + country;
            if(!language.equals("zh_CN") && !language.equals("en_US")) language = "zh_CN";
        }
        return language;
    }

    /* 通过浏览器打开链接 */
    public static void visitUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(uri);
        G.getContext().startActivity(intent);
    }
}
