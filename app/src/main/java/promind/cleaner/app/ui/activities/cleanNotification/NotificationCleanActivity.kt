package promind.cleaner.app.ui.activities.cleanNotification

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_clean_notification.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import promind.cleaner.app.R
import promind.cleaner.app.core.data.model.NotifiModel
import promind.cleaner.app.core.service.listener.observerPartener.ObserverInterface
import promind.cleaner.app.core.service.listener.observerPartener.ObserverUtils
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbAddListNoti
import promind.cleaner.app.core.service.service.NotificationListener
import promind.cleaner.app.core.service.service.NotificationUtil
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.SystemUtil
import promind.cleaner.app.core.utils.listener
import promind.cleaner.app.core.utils.preferences.MyCache.Companion.getCache
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.adapter.NotificationCleanAdapter
import promind.cleaner.app.ui.dialog.DialogConfirmCancel
import java.util.*

class NotificationCleanActivity : BaseActivity(), ObserverInterface<Any> {

    private var mNotificationCleanAdapter: NotificationCleanAdapter? = null
    private val lstNotifi: MutableList<NotifiModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clean_notification)
        ObserverUtils.getInstance().registerObserver(this)
        initView()
        initData()
        click()
    }

    private fun initView() {
        im_back_toolbar!!.visibility = View.VISIBLE
        im_back_toolbar!!.setOnClickListener { v: View? ->
//            val dialogConfirmCancel = DialogConfirmCancel(this, Config.FUNCTION.NOTIFICATION_MANAGER.id)
//            dialogConfirmCancel.listener = object : DialogConfirmCancel.OnClickListener {
//                override fun onDo() {}
//                override fun onCancel() {
//                    finish()
//                }
//            }
            finish()
//            dialogConfirmCancel.show()
        }
        tv_toolbar!!.text = getString(R.string.notification_manager)
        id_menu_toolbar!!.visibility = View.VISIBLE
        id_menu_toolbar!!.setImageResource(R.drawable.ic_settings)
        id_menu_toolbar!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
    }

    private fun initData() {
        mNotificationCleanAdapter = NotificationCleanAdapter(lstNotifi)
        rcv_notification!!.adapter = mNotificationCleanAdapter
        mNotificationCleanAdapter!!.setmItemClickListener { mNotifiModel: NotifiModel? ->
            if (mNotifiModel != null) {
                val mPendingIntent = mNotifiModel.barNotification.notification.contentIntent
                val intent = Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    mPendingIntent?.send(this, 0, intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                // Remove item from backing list here
                val position = viewHolder.adapterPosition
                lstNotifi.removeAt(position)
                mNotificationCleanAdapter!!.notifyItemRemoved(position)
                if (NotificationListener.getInstance() != null) NotificationListener.getInstance().removeItemLst(position)
                if (lstNotifi.size == 0) ll_empty!!.visibility = View.VISIBLE
            }
        })
        itemTouchHelper.attachToRecyclerView(rcv_notification)
        if (NotificationListener.getInstance() != null) {
            lstNotifi.addAll(NotificationListener.getInstance().lstSave)
            mNotificationCleanAdapter!!.notifyDataSetChanged()
            Handler().postDelayed({ if (SystemUtil.checkDNDDoing()) NotificationUtil.getInstance().postNotificationSpam(lstNotifi[0].barNotification, lstNotifi.size) }, 500)
        }
    }

    protected fun click() {
        id_menu_toolbar.setOnClickListener { click(it) }
        tv_clean.setOnClickListener { click(it) }
    }

    fun click(mView: View) {
        when (mView.id) {
            R.id.id_menu_toolbar -> startActivity(Intent(this, NotificationCleanSettingActivity::class.java))
            R.id.tv_clean -> {
                cleanAll()
                getCache().save(Config.FUNCTION.NOTIFICATION_MANAGER.id, System.currentTimeMillis())
                Objects.requireNonNull(listener)!!.invoke()
            }
        }
    }

    fun cleanAll() {
        lstNotifi.clear()
        mNotificationCleanAdapter!!.notifyDataSetChanged()
        ll_empty!!.visibility = View.VISIBLE
        if (NotificationListener.getInstance() != null) NotificationListener.getInstance().removeAllItem()
        openScreenResult(Config.FUNCTION.NOTIFICATION_MANAGER)
        finish()
    }

    override fun notifyAction(action: Any) {
        if (action is EvbAddListNoti) {
            val notification = action.lst
            //            if (notification.size() == 1) {
//                lstNotifi.add(0, notification.get(0));
//            } else {
//                lstNotifi.clear();
//                lstNotifi.addAll(notification);
//            }
            lstNotifi.clear()
            lstNotifi.addAll(notification)
            mNotificationCleanAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverUtils.getInstance().removeObserver(object :ObserverInterface<Any>{
            override fun notifyAction(action: Any) {
                this.notifyAction(action)
            }
        })
    }
}