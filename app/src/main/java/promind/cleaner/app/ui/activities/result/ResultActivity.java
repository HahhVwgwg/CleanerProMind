package promind.cleaner.app.ui.activities.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.BindView;
import butterknife.ButterKnife;
import promind.cleaner.app.core.adsControl.AdmobHelp;
import promind.cleaner.app.core.app.Application;
import promind.cleaner.app.core.utils.Constants;
import promind.cleaner.app.R;
import promind.cleaner.app.ui.adapter.FunctionAdapter;
import promind.cleaner.app.core.service.listener.observerPartener.ObserverUtils;
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbCheckLoadAds;
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbOnResumeAct;
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbOpenFunc;
import promind.cleaner.app.ui.activities.BaseActivity;
import promind.cleaner.app.ui.activities.ExitActivity;
import promind.cleaner.app.ui.activities.main.MainActivity;
import promind.cleaner.app.core.utils.Config;
import promind.cleaner.app.core.utils.PreferenceUtils;

public class ResultActivity extends BaseActivity {

    @BindView(R.id.rcv_funtion_suggest)
    RecyclerView rcvFunctionSuggest;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;
    @BindView(R.id.im_back_toolbar)
    ImageView imBack;
    @BindView(R.id.ll_infor)
    NestedScrollView scvInfor;
    @BindView(R.id.ll_main_result)
    View llMainResut;
    @BindView(R.id.ll_background)
    View llBackground;
    @BindView(R.id.layout_padding)
    View llToolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private Config.FUNCTION mFunction;
    private Boolean mNeedInterstitial;
    private FunctionAdapter mFunctionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_result);
        ButterKnife.bind(this);
        initView();
        initData();
        if (Constants.INSTANCE.getShowAds())
            AdmobHelp.getInstance().loadNative(this);
    }

    private void initView() {
        llToolbar.setAlpha(0.0f);
        scvInfor.setAlpha(0.0f);
        if (getIntent() != null) {
            mFunction = Config.getFunctionById(getIntent().getIntExtra(Config.DATA_OPEN_RESULT, 0));
            mNeedInterstitial = getIntent().getBooleanExtra(Config.DATA_OPEN_RESULT_WITHOUT_INTERSTITIAL, false);
        }
        if (mFunction != null) {
            tvTitle.setText(getString(mFunction.titleResult));
            PreferenceUtils.setLastTimeUseFunction(mFunction);
        }
        imBack.setVisibility(View.VISIBLE);

        imBack.setOnClickListener(v -> {
            if (mFunction == Config.FUNCTION.DEEP_CLEAN)
                finish();
            else super.onBackPressed();
        });
        if (mNeedInterstitial && Constants.INSTANCE.getShowAds())
            AdmobHelp.getInstance().showInterstitialAd(() -> {

                YoYo.with(Techniques.SlideInUp).duration(1000).playOn(scvInfor);
                YoYo.with(Techniques.FadeIn).duration(1000).playOn(llBackground);
                YoYo.with(Techniques.FadeIn).duration(1000).playOn(llToolbar);
            });

    }
    private void initData() {
        mFunctionAdapter = new FunctionAdapter(Config.LST_HOME_VERTICAL, Config.TYPE_DISPLAY_ADAPTER.VERTICAL);
        mFunctionAdapter.setmClickItemListener(mFunction -> {
            if (Application.getInstance().getActivityList().size() == 1) {
                Intent intentAct = new Intent(this, MainActivity.class);
                intentAct.putExtra(Config.DATA_OPEN_FUNCTION, mFunction.id);
                startActivity(intentAct);
            } else {
                ObserverUtils.getInstance().notifyObservers(new EvbOpenFunc(mFunction));
            }
            finish();
        });
        rcvFunctionSuggest.setAdapter(mFunctionAdapter);
        ObserverUtils.getInstance().notifyObservers(new EvbOnResumeAct());
    }

    @Override
    public void onBackPressed() {
        if (mFunction == Config.FUNCTION.DEEP_CLEAN) {
            ObserverUtils.getInstance().notifyObservers(new EvbOpenFunc(mFunction));
            finish();
        } else if (mFunction == null) {
            ExitActivity.exitApplicationAndRemoveFromRecent(this);
        } else {
            ObserverUtils.getInstance().notifyObservers(new EvbCheckLoadAds());
            super.onBackPressed();
        }
    }
}
