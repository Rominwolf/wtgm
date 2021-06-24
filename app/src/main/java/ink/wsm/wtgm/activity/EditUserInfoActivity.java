package ink.wsm.wtgm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ink.wsm.wtgm.utils.G;
import ink.wsm.wtgm.R;
import ink.wsm.wtgm.utils.Bcrypt;
import ink.wsm.wtgm.utils.SqLiteHelperUtil;
import ink.wsm.wtgm.utils.Toaster;

public class EditUserInfoActivity extends AppCompatActivity {
    String type, id, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

        TextView viewTextTitle = findViewById(R.id.viewTextTitle);
        EditText viewEditContent = findViewById(R.id.viewEditContent);

        Intent getIntent = getIntent();
        this.type = getIntent.getStringExtra("type");
        this.id = getIntent.getStringExtra("id");
        this.username = getIntent.getStringExtra("username");

        if(this.type.equals("username")){
            viewTextTitle.setText(R.string.title_change_username_edit);
            viewEditContent.setText(this.username);
        }

        if(this.type.equals("password"))
            viewTextTitle.setText(R.string.title_change_password_edit);

        if(this.type.equals("delete"))
            viewTextTitle.setText(R.string.title_delete_account_edit);
    }

    public void onClickSubmit(View v) {
        EditText viewEditContent = findViewById(R.id.viewEditContent);
        String content = viewEditContent.getText().toString().trim();

        if(content.isEmpty()){
            Toaster.fastHide(this, getString(R.string.toast_input_something));
            return;
        }

        if(this.type.equals("username")){
            String quotaUsername = "'" + content + "'";
            SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
            int existId = sqlite.getTheIntValue(SqLiteHelperUtil.TABLE_NAME_USERS, "id", "username", quotaUsername);

            if(existId != 0){
                String toastInfo = getResources().getString(R.string.toast_user_was_registered);
                Toaster.fastHide(this, toastInfo);
                return;
            }

            ContentValues values = new ContentValues();
            values.put("username", content);
            String[] args = new String[]{this.id};
            int result = sqlite.updateData(SqLiteHelperUtil.TABLE_NAME_USERS, values, "id = ?", args);
            if(result == 0){
                Toaster.fastHide(this, getString(R.string.toast_change_failed));
                return;
            }

            G.setUserInfo(null, null);
            Toaster.fastHide(this, getString(R.string.toast_change_succeed_as_restart));
            finish();
        }

        if(this.type.equals("password")){
            String hashSalt = Bcrypt.gensalt();
            String hashedPassword = Bcrypt.hashpw(content, hashSalt);
            SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);

            ContentValues values = new ContentValues();
            values.put("password", hashedPassword);
            String[] args = new String[]{this.id};
            int result = sqlite.updateData(SqLiteHelperUtil.TABLE_NAME_USERS, values, "id = ?", args);
            if(result == 0){
                Toaster.fastHide(this, getString(R.string.toast_change_failed));
                return;
            }

            G.setUserInfo(null, null);
            Toaster.fastHide(this, getString(R.string.toast_change_succeed_as_new_password));
            finish();
        }

        if(this.type.equals("delete")){
            SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
            String hashedPassword = sqlite.getTheStringValue(SqLiteHelperUtil.TABLE_NAME_USERS,
                    "password", "id", id);

            if(!Bcrypt.checkpw(content, hashedPassword)){
                Toaster.fastHide(this, getString(R.string.toast_wrong_password));
                return;
            }

            sqlite.delete(SqLiteHelperUtil.TABLE_NAME_USERS, "id = ?", new String[]{id});
            G.setUserInfo(null, null);
            Toaster.fastHide(this, getString(R.string.toast_account_deleted));
            finish();
        }
    }
}