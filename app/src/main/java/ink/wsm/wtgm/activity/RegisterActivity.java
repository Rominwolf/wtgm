package ink.wsm.wtgm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import ink.wsm.wtgm.R;
import ink.wsm.wtgm.utils.Bcrypt;
import ink.wsm.wtgm.utils.SqLiteHelperUtil;
import ink.wsm.wtgm.utils.Toaster;

public class RegisterActivity extends AppCompatActivity {
    private int countLicense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle(R.string.stringTitleRegister);
    }

    public void onClickLicense(View v) {
        boolean isChecked = ((CheckBox) v).isChecked();
        Button buttonRegister = findViewById(R.id.viewButtonRegister);
        if(isChecked) buttonRegister.setEnabled(true);
        else buttonRegister.setEnabled(false);
        countLicense++;
        if(countLicense > 6){
            TextView textLicenseSorry = findViewById(R.id.viewTextLicenseSorry);
            CheckBox viewCheckboxLicense = findViewById(R.id.viewCheckboxLicense);
            textLicenseSorry.setVisibility(View.VISIBLE);
            viewCheckboxLicense.setEnabled(false);
        }
    }

    public void onClickRegister(View v) {
        EditText viewEditUsername = findViewById(R.id.viewEditUsername);
        EditText viewEditPassword = findViewById(R.id.viewEditPassword);
        EditText viewEditConfirmPassword = findViewById(R.id.viewEditConfirmPassword);

        String username = viewEditUsername.getText().toString().trim();
        String password = viewEditPassword.getText().toString().trim();
        String confirmPassword = viewEditConfirmPassword.getText().toString().trim();

        if(username.isEmpty()){
            String toastInfo = getResources().getString(R.string.toast_username_cannot_empty);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        if(password.isEmpty() || confirmPassword.isEmpty()){
            String toastInfo = getResources().getString(R.string.toast_password_cannot_empty);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        if(!password.equals(confirmPassword)){
            String toastInfo = getResources().getString(R.string.toast_check_password_is_the_same);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        String quotaUsername = "'" + username + "'";
        SqLiteHelperUtil sqlite = new SqLiteHelperUtil(this);
        int existId = sqlite.getTheIntValue(sqlite.TABLE_NAME_USERS, "id", "username", quotaUsername);

        if(existId != 0){
            String toastInfo = getResources().getString(R.string.toast_user_was_registered);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        String hashSalt = Bcrypt.gensalt();
        String hashedPassword = Bcrypt.hashpw(password, hashSalt);

        long result = sqlite.insertNewUserInfo(username, hashedPassword);

        if(result < 1){
            String toastInfo = getResources().getString(R.string.toast_user_register_failed);
            Toaster.fastHide(this, toastInfo);
            return;
        }

        Intent intent = new Intent();
        intent.setClass(this, UserInfoActivity.class);
        intent.putExtra("id", String.valueOf(result));
        intent.putExtra("username", username);
        startActivity(intent);
        String toastInfo = getResources().getString(R.string.toast_register_succeed);
        Toaster.fastHide(this, toastInfo);
        finish();
    }
}