package promind.cleaner.app.ui.activities.smartCharger;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import promind.cleaner.app.R;
import promind.cleaner.app.core.data.data.TaskChargeInfo;
import promind.cleaner.app.core.data.data.TaskBoostApps;
import promind.cleaner.app.core.data.model.TaskInfo;
import promind.cleaner.app.ui.activities.BaseActivity;
import promind.cleaner.app.ui.activities.main.MainActivity;
import promind.cleaner.app.core.utils.Config;
import promind.cleaner.app.core.service.widgets.PinChargerView;

public class SmartChargerBoostActivity extends BaseActivity {

    @BindView(R.id.im_back_toolbar)
    ImageView imBackToolbar;
    @BindView(R.id.tv_toolbar)
    TextView tvTitleToolbar;
    @BindView(R.id.pinChargerView)
    PinChargerView mPinChargerView;
    @BindView(R.id.id_menu_toolbar)
    ImageView imMenuToolbar;
    private boolean finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_charger_boost);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        imBackToolbar.setVisibility(View.VISIBLE);
        imBackToolbar.setOnClickListener(v -> finish());

        imBackToolbar.setColorFilter(getResources().getColor(R.color.color_222222), PorterDuff.Mode.SRC_IN);
        tvTitleToolbar.setTextColor(getResources().getColor(R.color.color_222222));
        tvTitleToolbar.setText(getString(R.string.smart_charge));

        imMenuToolbar.setVisibility(View.VISIBLE);
        imMenuToolbar.setImageResource(R.drawable.ic_settings);
        imMenuToolbar.setColorFilter(getResources().getColor(R.color.color_222222), PorterDuff.Mode.SRC_IN);
    }

    private void initData() {
        new TaskBoostApps(new TaskBoostApps.OnTaskListListener() {
            @Override
            public void OnResult(List<TaskInfo> arrList) {
                new TaskChargeInfo(SmartChargerBoostActivity.this, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                if (!finish) {
                    openScreenResult(Config.FUNCTION.SMART_CHARGE, true);
                    finish();
                }
            }

            @Override
            public void onProgress(String appName) {
                mPinChargerView.setContent("<b>" + getString(R.string.analyzing_battery_usage) + ": " + "</b>" + "  " + appName);
            }

            @Override
            public void size(int size) {

            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick(R.id.id_menu_toolbar)
    void click(View mView) {
        switch (mView.getId()) {
            case R.id.id_menu_toolbar:
                finish = true;
                Intent intentAct = new Intent(this, MainActivity.class);
                intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentAct.putExtra(Config.DATA_OPEN_FUNCTION, Config.FUNCTION.SMART_CHARGE.id);
                startActivity(intentAct);
                finish();
                break;
        }
    }
}
