package promind.cleaner.app.ui.deepcleanjunk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_gallery.*
import promind.cleaner.app.R
import promind.cleaner.app.core.data.model.GroupItem
import promind.cleaner.app.ui.adapter.BigFilesAdapter
import java.io.File
import java.util.*

class FragmentGallery : Fragment() {

    private var mGroupItems = ArrayList<GroupItem>()
    private var mAdapter: BigFilesAdapter? = null
    private var mTotalJunk: Long = 0
    private val mFileListLarge = ArrayList<File>()

    companion object {
        @JvmStatic
        fun newInstance(type: Int, data: ArrayList<GroupItem> ): FragmentGallery {
            return FragmentGallery().apply {
                arguments = Bundle().apply {
                    putInt("type", type)
                    putSerializable("groupItems", data)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val type = requireArguments().getInt("type")
        mGroupItems = requireArguments().getSerializable("groupItems") as ArrayList<GroupItem>
        mAdapter = BigFilesAdapter(context, mGroupItems, object : BigFilesAdapter.OnGroupClickListener {
            override fun onGroupClick(groupPosition: Int) {
                if (recyclerView.isGroupExpanded(groupPosition)) {
                    recyclerView.collapseGroupWithAnimation(groupPosition)
                } else {
                    recyclerView.expandGroupWithAnimation(groupPosition)
                }
            }

            override fun onSelectItemHeader(position: Int, isCheck: Boolean) {
//                changeCleanFileHeader(position, isCheck)
            }

            override fun onSelectItem(groupPosition: Int, childPosition: Int, isCheck: Boolean) {
//                changeCleanFileItem(groupPosition, childPosition, isCheck)
            }
        })
        recyclerView.setAdapter(mAdapter)
        mAdapter?.notifyDataSetChanged()
//        if (mGroupItems.size == 0) {
//            Toolbox.setTextFromSize(0, mTvTotalCache, mTvType, btnCleanUp)
//            mViewLoading!!.visibility = View.VISIBLE
//            startImageLoading()
//            largeFile
//        } else {
//            mAdapter!!.notifyDataSetChanged()
//        }
    }
}