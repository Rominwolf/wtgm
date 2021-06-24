package ink.wsm.wtgm.utils;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    public static void fastHide(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
    public static void slowHide(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
