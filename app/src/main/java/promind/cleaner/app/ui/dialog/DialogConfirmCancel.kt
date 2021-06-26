package promind.cleaner.app.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_confirm_cancel.view.*
import promind.cleaner.app.R
import promind.cleaner.app.core.adsControl.AdmobHelp
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Constants.showAds

@SuppressLint("InflateParams")
class DialogConfirmCancel(context: Context, which: Int) : AlertDialog(context) {

    interface OnClickListener {
        fun onDo()
        fun onCancel()
    }

    var listener: OnClickListener? = null

    init {


        val view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_cancel, null, false)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setView(view)

        if (showAds) AdmobHelp.getInstance().loadNativeFragment(context as Activity, view)

        Config.FUNCTION.values().toList().forEach {
            if (which == it.id) {
                view.image.setImageResource(it.icon)
                view.message.text = context.getString(it.dialogQuestion)
                view.doFunc.text = context.getString(it.btnText)
            }
        }

        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.doFunc.setOnClickListener {
            listener?.onDo()
            cancel()
        }

        view.cancelFunc.setOnClickListener {
            listener?.onCancel()
            cancel()
        }

    }

}