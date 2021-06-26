package promind.cleaner.app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import promind.cleaner.app.R;
import promind.cleaner.app.core.utils.Config;

import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    private Config.FUNCTION[] lstFunction;
    private Config.TYPE_DISPLAY_ADAPTER typeDisplay;
    private Context mContext;
    private Boolean bindClick = true;
    private ClickItemListener mClickItemListener;

    public void resetDot() {
        for (Config.FUNCTION function : lstFunction) {
            function.needDot = false;
        }
        notifyDataSetChanged();
    }

    public void setDot(int position) {
        for (Config.FUNCTION function : lstFunction) {
            function.needDot = false;
        }
        lstFunction[position].needDot = true;
        notifyDataSetChanged();
    }


    public interface ClickItemListener {
        void itemSelected(Config.FUNCTION mFunction);
    }

    public FunctionAdapter(Config.FUNCTION[] lstFunction, Config.TYPE_DISPLAY_ADAPTER typeDisplay) {
        this.lstFunction = lstFunction;
        this.typeDisplay = typeDisplay;
    }

    public FunctionAdapter(Config.FUNCTION[] lstFunction, Config.TYPE_DISPLAY_ADAPTER typeDisplay, Boolean bindClick) {
        this.lstFunction = lstFunction;
        this.typeDisplay = typeDisplay;
        this.bindClick = bindClick;
    }

    public void setmClickItemListener(ClickItemListener mClickItemListener) {
        this.mClickItemListener = mClickItemListener;
    }

    @NonNull
    @Override
    public FunctionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View mView = null;
        if (typeDisplay == Config.TYPE_DISPLAY_ADAPTER.HORIZOLTAL) {
            mView = mLayoutInflater.inflate(R.layout.item_function_horizontal, parent, false);
        } else if (typeDisplay == Config.TYPE_DISPLAY_ADAPTER.VERTICAL) {
            mView = mLayoutInflater.inflate(R.layout.item_function_vertical, parent, false);
        } else if (typeDisplay == Config.TYPE_DISPLAY_ADAPTER.SUGGEST) {
            mView = mLayoutInflater.inflate(R.layout.item_function_suggest, parent, false);
        }
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull FunctionAdapter.ViewHolder holder, int position) {
        holder.binData(lstFunction[position]);
    }

    @Override
    public int getItemCount() {
        return lstFunction.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.im_icon)
        ImageView imIcon;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @Nullable
        @BindView(R.id.tv_description)
        TextView tvDescrtion;
        @Nullable
        @BindView(R.id.tv_action)
        TextView tvAction;
        @Nullable
        @BindView(R.id.view_suggest_left)
        RoundedImageView imSguuestLeft;
        @Nullable
        @BindView(R.id.iv_dot)
        ImageView ivDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void binData(Config.FUNCTION mFunction) {
            if (mFunction != null) {
                imIcon.setImageResource(mFunction.icon);
                tvTitle.setText(mContext.getString(mFunction.title));
                if (tvDescrtion != null)
                    tvDescrtion.setText(mContext.getString(mFunction.description));
                if (tvAction != null) { /*TH item list suggest*/
                    tvAction.setTextColor(mContext.getResources().getColor(mFunction.color));
                    tvAction.setText(mContext.getString(mFunction.action));
                    imSguuestLeft.setImageResource(mFunction.background);
                    imIcon.setVisibility(View.GONE);
                }
                if (ivDot != null) {
                    if (mFunction.needDot) {
                        imIcon.setBackgroundResource(R.drawable.bg_blue);
                        imIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                        ivDot.setVisibility(View.VISIBLE);
                    } else {
                        imIcon.setBackgroundResource(R.drawable.bg_white);
                        imIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorViolet));
                        ivDot.setVisibility(View.GONE);
                    }
                }
            }

            itemView.setOnClickListener(v -> {
                if (mClickItemListener != null && bindClick)
                    mClickItemListener.itemSelected(mFunction);
            });
        }
    }
}
