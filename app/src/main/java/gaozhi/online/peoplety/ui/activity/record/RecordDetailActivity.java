package gaozhi.online.peoplety.ui.activity.record;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import gaozhi.online.peoplety.R;

public class RecordDetailActivity extends AppCompatActivity {

    public static void startActivity(Context context, long recordId){
        Intent intent  = new Intent(context,RecordDetailActivity.class);

        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);
    }
}