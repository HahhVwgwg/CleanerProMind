package promind.cleaner.app.core.data.data;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;

import promind.cleaner.app.core.data.model.TaskInfo;
import promind.cleaner.app.core.utils.Toolbox;

import java.util.List;

public class TaskKillProgress extends AsyncTask<Void, Void, Void> {

    private ActivityManager mActivityManager;
    private List<TaskInfo> lstAppKill;

    public TaskKillProgress(Context mContext, List<TaskInfo> lstAppKill) {
        this.lstAppKill = lstAppKill;
        mActivityManager = (ActivityManager) mContext.getSystemService(
                Context.ACTIVITY_SERVICE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<TaskInfo> lstKill = Toolbox.getLstTaskFillterIgnore(lstAppKill);
        for (TaskInfo mTaskInfo : lstKill) {
            if (mTaskInfo.isChceked())
                mActivityManager.killBackgroundProcesses(mTaskInfo.getAppinfo().packageName);
        }
        return null;
    }
}
