package promind.cleaner.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import promind.cleaner.app.R;
import promind.cleaner.app.core.data.model.DataModel;

/**
 * Created by sonu on 24/07/17.
 */

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> {


    class SectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView sectionLabel;
        private TextView showAllButton;
        private final RecyclerView itemRecyclerView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionLabel = (TextView) itemView.findViewById(R.id.section_label);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);

        }
    }

    private final Context context;
    private final RecyclerViewType recyclerViewType;
    private final ArrayList<DataModel> sectionModelArrayList;


    public SectionRecyclerViewAdapter(Context context, RecyclerViewType recyclerViewType, ArrayList<DataModel> sectionModelArrayList) {
        this.context = context;
        this.recyclerViewType = recyclerViewType;
        this.sectionModelArrayList = sectionModelArrayList;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_custom_row_dupicate, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        final DataModel mDataModel = sectionModelArrayList.get(position);
        holder.sectionLabel.setText(mDataModel.getTitleGroup());

        //recycler view for items
        holder.itemRecyclerView.setHasFixedSize(true);
        holder.itemRecyclerView.setNestedScrollingEnabled(false);

        /* set layout manager on basis of recyclerview enum type */
        switch (recyclerViewType) {
            case LINEAR_VERTICAL:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            case LINEAR_HORIZONTAL:
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager1);
                break;
            case GRID:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
                holder.itemRecyclerView.setLayoutManager(gridLayoutManager);
                break;
        }
        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(context, mDataModel.getListDuplicate());
        holder.itemRecyclerView.setAdapter(adapter);

        //show toast on click of show all button
//        holder.showAllButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "You clicked on Show All of : " + mDataModel.getTitleGroup() ,Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return sectionModelArrayList.size();
    }


}
