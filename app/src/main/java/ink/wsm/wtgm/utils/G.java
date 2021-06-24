package ink.wsm.wtgm.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class G extends Application {
    private static String defPackage;
    private static Context context;
    private static Handler handlerMaterialActivity;
    private static String[] userInfo = {null, null};

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        defPackage = context.getPackageName();
    }

    public static Context getContext() {
        return context;
    }

    public static String getPackagerName() {
        return defPackage;
    }

    public static void setHandlerMaterialActivity(Handler handler) {
        handlerMaterialActivity = handler;
    }

    public static Handler getHandlerMaterialActivity() {
        return handlerMaterialActivity;
    }

    public static void setUserInfo(String userId, String userName) {
        userInfo = new String[]{userId, userName};
    }

    public static String[] getUserInfo() {
        return userInfo;
    }
}
