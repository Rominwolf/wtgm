package ink.wsm.wtgm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ink.wsm.wtgm.utils.G;
import ink.wsm.wtgm.R;
import ink.wsm.wtgm.utils.Bcrypt;
import ink.wsm.wtgm.utils.SqLiteHelperUtil;
import ink.wsm.wtgm.utils.Toaster;

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String[] userInfo = G.getUserInfo();
        if(userInfo[0] != null){
            jumpToUserInfo(userInfo[0], userInfo[1]);
            return;
        }

        this.setTitle(R.string.stringTitleLogin);
    }

    public void onClickJumpToRegister(View v) {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onClickLogin(View v) {
        EditText viewEditUsername = findViewById(R.id.viewEditUsername);
        EditText viewEditPassword = findViewById(R.id.viewEditPassword);
        String username = viewEditUsername.getText().toString().trim();
        String password = viewEditPassword.getText().toString().trim();

        if(username.isEmpty()){
            String toastInfo = getResources().getString(R.string.toast_username_cannot_empty);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        if(password.isEmpty()){
            String toastInfo = getResources().getString(R.string.toast_password_cannot_empty);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        String quotaUsername = "'" + username + "'";
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(G.getContext());
        String hashPassword = sqlite.getTheStringValue(sqlite.TABLE_NAME_USERS,
                "password", "username", quotaUsername);

        if(hashPassword == null){
            String toastInfo = getResources().getString(R.string.toast_user_is_unregistered);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        if(!Bcrypt.checkpw(password, hashPassword)){
            String toastInfo = getResources().getString(R.string.toast_wrong_password);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        int id = sqlite.getTheIntValue(sqlite.TABLE_NAME_USERS,
                "id", "username", quotaUsername);

        jumpToUserInfo(String.valueOf(id), username);
    }

    public void jumpToUserInfo(String userId, String userName) {
        Intent intent = new Intent();
        intent.setClass(this, UserInfoActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("username", userName);
        startActivity(intent);
        finish();
    }
}