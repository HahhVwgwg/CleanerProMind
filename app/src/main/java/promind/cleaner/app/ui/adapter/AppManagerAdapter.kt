package promind.cleaner.app.ui.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_app_manager.view.*
import promind.cleaner.app.R
import promind.cleaner.app.core.data.data.FieldProperty
import promind.cleaner.app.core.utils.Toolbox
import java.io.File

class MapWrapper(val map: MutableMap<Int, Boolean>, private val onChange: () -> Unit) {

    val size = map.size

    fun get(index: Int): Boolean {
        return map[index] ?: false
    }

    fun set(index: Int, value: Boolean) {
        map[index] = value
        onChange()
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class AppManagerAdapter(context: Context, private val onClickItemListener: OnClickItemListener) : RecyclerView.Adapter<ViewHolder>() {

    private lateinit var itemsChecked: MapWrapper

    private var items = listOf<ApplicationInfo>()
    fun setData(items: List<ApplicationInfo>) {
        this.items = items
        itemsChecked = MapWrapper(mutableMapOf()) {
//            updateCheckedItems()
            onClickItemListener.onChecked()
        }
        notifyDataSetChanged()
    }

    fun setChecked(checked: Boolean) {
        items.forEachIndexed { index, _ ->
            itemsChecked.set(index, checked)
        }
        notifyDataSetChanged()
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mPackageManager: PackageManager = context.packageManager
    private val mOnClickItemListener: OnClickItemListener = onClickItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_app_manager, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            items[holder.adapterPosition].apply {
                tvName!!.text = loadLabel(mPackageManager)
                val file = File(publicSourceDir)
                tvSize!!.text = Toolbox.formatSize(file.length())
                imgIconApp!!.setImageDrawable(loadIcon(mPackageManager))

                btnUninstall.isChecked = itemsChecked.get(holder.adapterPosition)

                btnUninstall.setOnCheckedChangeListener { _, isChecked ->
                    itemsChecked.set(holder.adapterPosition, isChecked)
                }
                setOnClickListener { mOnClickItemListener.onClickItem(this) }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateCheckedItems() {
        itemsChecked.map.filter { it.value }.forEach { (key, isChecked) ->
            items[key].isChecked = isChecked
        }
    }

    fun checkedCount(): Int = itemsChecked.map.filter { it.value }.size

    private var checked = arrayListOf<ApplicationInfo>()
    fun getCheckedItems(): List<ApplicationInfo> {
        checked = arrayListOf()
        itemsChecked.map.filter { it.value }.keys.forEach {
            checked.add(items[it])
        }
        return checked
    }

    interface OnClickItemListener {
        fun onUninstallApp(data: ApplicationInfo)
        fun onClickItem(data: ApplicationInfo)
        fun onChecked()
    }
}

var ApplicationInfo.isChecked: Boolean by FieldProperty { false }
