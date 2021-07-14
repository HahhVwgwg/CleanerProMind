package promind.cleaner.app.core.data.data;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import promind.cleaner.app.core.app.Application;
import promind.cleaner.app.core.data.model.TaskInfo;
import promind.cleaner.app.core.utils.Toolbox;

public class TaskBoostApps extends AsyncTask<Void, TaskInfo, List<TaskInfo>> {
    private static final long TIME_DELAY = 100;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final OnTaskListListener mOnTaskListListener;

    public interface OnTaskListListener {
        void OnResult(List<TaskInfo> arrList);

        void onProgress(String appName);

        void size(int size);
    }

    public TaskBoostApps(OnTaskListListener onTaskListListener) {
        mContext = Application.getInstance();
        mOnTaskListListener = onTaskListListener;
        mPackageManager = mContext.getPackageManager();
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected List<TaskInfo> doInBackground(Void... arg0) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list != null)
            mOnTaskListListener.size(list.size());
        ArrayList<TaskInfo> arrList = new ArrayList<>();
        int mem = 0;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : am.getRunningServices(Integer.MAX_VALUE)) {
                try {
                    if (mPackageManager == null) break;
                    PackageInfo packageInfo = mPackageManager.getPackageInfo(runningServiceInfo.service.getPackageName(), PackageManager.GET_ACTIVITIES);
                    if (packageInfo == null) continue;
                    ApplicationInfo applicationInfo;
                    applicationInfo = mPackageManager.getApplicationInfo(packageInfo.packageName, 0);
                    if (applicationInfo == null) continue;
                    if (!packageInfo.packageName.contains(mContext.getPackageName()) && applicationInfo != null && Toolbox.isUserApp(applicationInfo)) {
                        TaskInfo info = new TaskInfo(mContext, applicationInfo);
                        if (Toolbox.checkLockedItem(mContext, mContext.getPackageName())) {
                            info.setChceked(false);
                        } else {
                            info.setChceked(true);
                        }

                        if (info.isGoodProcess()) {
                            int j = runningServiceInfo.pid;
                            int i[] = new int[1];
                            i[0] = j;
                            Debug.MemoryInfo memInfo[] = am
                                    .getProcessMemoryInfo(i);
                            for (Debug.MemoryInfo mInfo : memInfo) {
                                int m = mInfo.getTotalPss() * 1024;
                                info.setMem(m);
                                int jl = mInfo.getTotalPss() * 1024;
                                int kl = mem;
                                if (jl > kl)
                                    mem = mInfo.getTotalPss() * 1024;
                            }
                            if (mem > 0) {
                                Thread.sleep(TIME_DELAY);
                                arrList.add(info);
                                publishProgress(info);
                            }
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } else {
//                UsageStatsManager usage = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
//                List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - (60 * 1000), time);
            List<ApplicationInfo> applicationInfos = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            int total = 0;
            for (ApplicationInfo applicationInfo : applicationInfos) {
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || applicationInfo.packageName.equals(mContext.getPackageName())) {
                } else {
                    total++;
                }
            }
            mOnTaskListListener.size(total);
            for (ApplicationInfo applicationInfo : applicationInfos) {
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || applicationInfo.packageName.equals(mContext.getPackageName())) {
                    continue;
                }
                try {
                    if (mPackageManager == null) break;
                    PackageInfo packageInfo = mPackageManager.getPackageInfo(applicationInfo.packageName, PackageManager.GET_ACTIVITIES);
                    if (packageInfo == null) continue;
                    if (!packageInfo.packageName.contains(mContext.getPackageName()) && applicationInfo != null && Toolbox.isUserApp(applicationInfo)) {
                        TaskInfo info = new TaskInfo(mContext, applicationInfo);
                        if (Toolbox.checkLockedItem(mContext, mContext.getPackageName())) {
                            info.setChceked(false);
                        } else {
                            info.setChceked(true);
                        }
                        Thread.sleep(TIME_DELAY);
                        arrList.add(info);
                        publishProgress(info);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            HashMap<String, TaskInfo> hashMap = new HashMap<>();
            for (TaskInfo taskInfo : arrList) {
                if (taskInfo.getMem() != 0) {
                    if (hashMap.containsKey(taskInfo.getPackageName())) {
                        TaskInfo old = hashMap.get(taskInfo.getPackageName());
                        if (old.getMem() < taskInfo.getMem()) {
                            hashMap.put(taskInfo.getPackageName(), taskInfo);
                        }
                    } else {
                        hashMap.put(taskInfo.getPackageName(), taskInfo);
                    }
                }
                hashMap.put(taskInfo.getPackageName(), taskInfo);
            }
            Collection<TaskInfo> collection = hashMap.values();
            return new ArrayList(collection);
        }

        return arrList;
    }

    @Override
    protected void onProgressUpdate(TaskInfo... values) {
        super.onProgressUpdate(values);
        if (null != mOnTaskListListener) {
            mOnTaskListListener.onProgress(values[0].getTitle());
        }
    }

    @Override
    protected void onPostExecute(List<TaskInfo> taskInfos) {
        super.onPostExecute(taskInfos);
        if (null != mOnTaskListListener) {
            mOnTaskListListener.OnResult(taskInfos);
        }
    }

    private long getAvaiableRam(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(mi);
        }
        return mi.availMem;
    }


}

