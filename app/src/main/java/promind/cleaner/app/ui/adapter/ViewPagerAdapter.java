package promind.cleaner.app.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import promind.cleaner.app.core.data.model.GroupItem;
import promind.cleaner.app.ui.deepcleanjunk.FragmentGallery;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<GroupItem> data = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return FragmentGallery.newInstance(position, data);
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void setData(List<GroupItem> mGroupItems) {
        ArrayList<GroupItem> dataCurrent = new ArrayList<>(mGroupItems.size());
        dataCurrent.addAll(mGroupItems);
        this.data = dataCurrent;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
