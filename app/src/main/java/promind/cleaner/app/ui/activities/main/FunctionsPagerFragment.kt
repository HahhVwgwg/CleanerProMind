package promind.cleaner.app.ui.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_functions_pager.*
import promind.cleaner.app.R
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.ui.activities.BaseActivity

class FunctionsPagerFragment : Fragment() {

    companion object {
        fun newInstance(data: Config.FUNCTION): FunctionsPagerFragment {
            return FunctionsPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt("dataId", data.id)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_functions_pager, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            val dataId = requireArguments().getInt("dataId")
            val data: Config.FUNCTION = getFunctionById(dataId)
            data.apply {
                btn.text = resources.getString(btnText)
                funcTitle.text = resources.getString(title_slider)
                funcDesc.text = "${resources.getString(description)}. ${resources.getString(title)}"
                btn.setOnClickListener {
                    if (activity != null) (activity as BaseActivity?)!!.openScreenFunction(this)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun getFunctionById(dataId: Int): Config.FUNCTION {
        when (dataId) {
            1 -> {
                return Config.FUNCTION.JUNK_FILES
            }
            2 -> {
                return Config.FUNCTION.CPU_COOLER
            }
            3 -> {
                return Config.FUNCTION.PHONE_BOOST
            }
            4 -> {
                return Config.FUNCTION.ANTIVIRUS
            }
            5 -> {
                return Config.FUNCTION.POWER_SAVING
            }
            8 -> {
                return Config.FUNCTION.SMART_CHARGE
            }
            9 -> {
                return Config.FUNCTION.DEEP_CLEAN
            }
            10 -> {
                return Config.FUNCTION.APP_UNINSTALL
            }
            12 -> {
                return Config.FUNCTION.NOTIFICATION_MANAGER
            }
            13 -> {
                return Config.FUNCTION.PREMIUM_SECURITY
            }
            14 -> {
                return Config.FUNCTION.ADDITIONAL_DEEP_CLEAN
            }
            15 -> {
                return Config.FUNCTION.ALL_FUNCTIONS
            }
            16 -> {
                return Config.FUNCTION.ALL_FUNCTIONS
            }
            17 -> {
                return Config.FUNCTION.ALL_FUNCTIONS
            }
            19 -> {
                return Config.FUNCTION.DEEP_CLEAN_JUNK
            }
            else -> return Config.FUNCTION.PREMIUM_SECURITY
        }
    }

}


class PagerAdapter(val data: Array<Config.FUNCTION>, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return FunctionsPagerFragment.newInstance(data[position])
    }

    override fun getCount(): Int = data.size

    override fun saveState(): Parcelable? = null

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
    }
}

