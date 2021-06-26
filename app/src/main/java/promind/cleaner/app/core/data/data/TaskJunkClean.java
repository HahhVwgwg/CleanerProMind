package promind.cleaner.app.core.data.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import promind.cleaner.app.core.utils.ExtensionsKt;

public class TaskJunkClean extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "MMM";
    private Method mFreeStorageAndNotifyMethod;
    private final String packageName;
    private final Context mContext;
    private final OnTaskCleanListener mOnTaskCleanListener;

    public TaskJunkClean(Context context, String packageN, OnTaskCleanListener onTaskCleanListener) {
        mContext = context;
        packageName = packageN;
        mOnTaskCleanListener = onTaskCleanListener;
        try {
            mFreeStorageAndNotifyMethod = context.getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        try {
            if (canCleanInternalCache(mContext)) {
                mFreeStorageAndNotifyMethod.invoke(mContext.getPackageManager(),
                        (long) stat.getBlockCount() * (long) stat.getBlockSize(),
                        new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded)
                                    throws RemoteException {
                                countDownLatch.countDown();
                            }
                        }
                );
            } else {
                countDownLatch.countDown();
            }

            if (isExternalStorageWritable()) {
                final File externalDataDirectory = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath() + "/Android/data");

                final String externalCachePath = externalDataDirectory.getAbsolutePath() + "/" + packageName + "/cache";

                final String rootDataPath = Environment.getDataDirectory()+"/data";
                final String rootCachePath = Environment.getDataDirectory()+"/data/" + packageName + "/cache";
                final String rootCodeCachePath = Environment.getDataDirectory()+"/data/" + packageName + "/code_cache";

                Log.i("checkandfix", "doInBackground: " + new File(Environment.getDataDirectory() +"/data").exists());

                ExtensionsKt.deleteRecursively(new File(rootCachePath));
                ExtensionsKt.deleteRecursively(new File(rootCodeCachePath));
                ExtensionsKt.deleteRecursively(new File(rootDataPath));
                ExtensionsKt.deleteRecursively(new File(externalCachePath));

            } else {
                Log.e(TAG, "doInBackground: NOT WRITABLE");
            }

            countDownLatch.await();
        } catch (InterruptedException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mOnTaskCleanListener != null) {
            mOnTaskCleanListener.onCleanCompleted(result);
        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private static boolean canCleanInternalCache(Context context) {
        return hasPermission(context, Manifest.permission.CLEAR_APP_CACHE);
    }

    public interface OnTaskCleanListener {
        void onCleanCompleted(boolean result);
    }
}
