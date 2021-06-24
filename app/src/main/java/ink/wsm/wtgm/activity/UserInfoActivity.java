package ink.wsm.wtgm.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ink.wsm.wtgm.utils.G;
import ink.wsm.wtgm.R;
import ink.wsm.wtgm.utils.Toaster;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    String username, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        this.setTitle(R.string.stringTitleUserInfo);

        Intent getIntent = getIntent();
        this.id = getIntent.getStringExtra("id");
        this.username = getIntent.getStringExtra("username");

        G.setUserInfo(this.id, this.username);

        if(id.isEmpty()){
            String toastInfo = getResources().getString(R.string.toast_not_login);
            Toaster.fastHide(this, toastInfo);
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        TextView viewTextUsername = findViewById(R.id.viewTextUsername);
        TextView viewTextId = findViewById(R.id.viewTextId);
        viewTextId.setText(this.id);
        viewTextUsername.setText(this.username);

        Button viewButtonChangeUsername = findViewById(R.id.viewButtonChangeUsername);
        Button viewButtonChangePassword = findViewById(R.id.viewButtonChangePassword);
        Button viewButtonDeleteAccount = findViewById(R.id.viewButtonDeleteAccount);

        viewButtonChangeUsername.setOnClickListener(this);
        viewButtonChangePassword.setOnClickListener(this);
        viewButtonDeleteAccount.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String[] userInfo = G.getUserInfo();
        if(userInfo[0] == null) finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, EditUserInfoActivity.class);
        intent.putExtra("id", this.id);
        intent.putExtra("username", this.username);
        if(v.getId() == R.id.viewButtonChangeUsername)
            intent.putExtra("type", "username");
        if(v.getId() == R.id.viewButtonChangePassword)
            intent.putExtra("type", "password");
        if(v.getId() == R.id.viewButtonDeleteAccount)
            intent.putExtra("type", "delete");
        startActivity(intent);
    }

    public void onClickLogout(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String titleTwiceConfirm = getResources().getString(R.string.title_twice_confirm);
        String titleSureToLogout = getResources().getString(R.string.title_confirm_to_logout);
        String titleConfirm = getResources().getString(R.string.title_confirm);
        String titleCancel = getResources().getString(R.string.title_cancel);

        builder.setTitle(titleTwiceConfirm)
                .setMessage(titleSureToLogout)
                .setPositiveButton(titleConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        G.setUserInfo(null, null);
                        Context context = G.getContext();
                        Intent intent = new Intent();
                        intent.setClass(context, LoginActivity.class);
                        startActivity(intent);
                        String toastInfo = getResources().getString(R.string.toast_already_logout);
                        Toaster.fastHide(context, toastInfo);
                        finish();
                    }
                })
                .setNegativeButton(titleCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}