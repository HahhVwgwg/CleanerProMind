package promind.cleaner.app.core.app;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import promind.cleaner.app.core.utils.PreferenceUtils;
import promind.cleaner.app.core.utils.preferences.MyCache;
import promind.cleaner.app.core.utils.preferences.PreferenceHelper;
import promind.cleaner.app.core.utils.preferences.SharedManager;
import promind.cleaner.app.ui.activities.BaseActivity;
import promind.cleaner.app.ui.activities.main.MainActivity;

public class Application extends LitePalApplication {

    private static List<BaseActivity> activityList;

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    private synchronized static void setInstance(Application instance) {
        Application.instance = instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null)
            setInstance(Application.this);
        initPrefs();
        PreferenceUtils.init(this);
        if (PreferenceUtils.getTimeInstallApp() == 0)
            PreferenceUtils.setTimeInstallApp(System.currentTimeMillis());
        activityList = new ArrayList<>();

        initAnalytics();
    }

    public static SharedManager sharedManager;

    private void initPrefs() {
        MyCache.Companion.init(this);
        sharedManager = new SharedManager(PreferenceHelper.INSTANCE.customPrefs(this, "APP_CACHE"));
    }

    public void doForCreate(BaseActivity activity) {
        activityList.add(activity);
    }

    public void doForFinish(BaseActivity activity) {
        activityList.remove(activity);
    }

    public BaseActivity getTopActivity() {
        if (activityList.isEmpty())
            return null;
        return activityList.get(activityList.size() - 1);
    }

    public List<BaseActivity> getActivityList() {
        return activityList;
    }

    public void clearAllActivityUnlessMain() {
        for (BaseActivity activity : activityList) {
            if (activity != null && !(activity instanceof MainActivity))
                activity.clear();
        }
        activityList.clear();
    }

    private void initAnalytics() {
        initOneSignal();
        initFirebase();
        initAppsFlyer();
    }

    private void initOneSignal() {
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("7e92c456-9566-4168-91f4-39618740c310");
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
    }

    private void initAppsFlyer() {
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {

                for (String attrName : conversionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("LOG_TAG", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    Log.d("LOG_TAG", "attribute: " + attrName + " = " + attributionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("LOG_TAG", "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init("uwEhNBDniCv5RLLkaGoAt", conversionListener, this);
        AppsFlyerLib.getInstance().start(this);

    }
}
