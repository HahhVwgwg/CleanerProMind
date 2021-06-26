package promind.cleaner.app.ui.activities.deepClean;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import promind.cleaner.app.R;
import promind.cleaner.app.core.utils.utilts.Utils;


public class DeepScanResultActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setLocale(this);
        setContentView(R.layout.activity_dupicate_row);
        intView();
        intData();

    }
    public void intView(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title_tool_bar"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    public void intData(){
        int int_position = getIntent().getIntExtra("value", 0);

        TextView tvStatus = (TextView)findViewById(R.id.tvStatus);
        tvStatus.setText(int_position +" "+getString(R.string.file_removed));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
