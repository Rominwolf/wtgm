package ink.wsm.wtgm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ink.wsm.wtgm.R;

public class ChooseMaterialsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_materials);
        this.setTitle(R.string.stringTitleChooseMaterial);
    }

    public void onClickMaterial(View v) {
        int id = v.getId();
        String flagId = v.getResources().getResourceName(id);
        String materialId = flagId.substring(flagId.indexOf("viewMaterial") + 12);
        Intent intent = new Intent(this, MaterialActivity.class);
        intent.putExtra("materialId", materialId);
        startActivity(intent);
    }
}