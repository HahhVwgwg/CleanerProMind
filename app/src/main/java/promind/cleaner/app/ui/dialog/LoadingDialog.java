package promind.cleaner.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import promind.cleaner.app.R;


public class LoadingDialog extends Dialog {
    private final Context mContext;

    public LoadingDialog(Context activity) {
        super(activity);
        this.mContext = activity;
        requestWindowFeature(1);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_loading_dialog_dupicate);

    }
}