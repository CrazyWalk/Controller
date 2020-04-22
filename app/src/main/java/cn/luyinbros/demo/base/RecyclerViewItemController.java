package cn.luyinbros.demo.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.luyinbros.android.controller.ControllerDelegate;
import cn.luyinbros.android.controller.SimpleControllerDelegate;
import cn.luyinbros.logger.LoggerFactory;

public abstract class RecyclerViewItemController {
    private final SimpleControllerDelegate mDelegate = ControllerDelegate.create(this);
    public final RecyclerView.ViewHolder mHolder;

    {
        LoggerFactory.getLogger(RecyclerViewItemController.class).debug(" " + mDelegate.getClass());
    }

    public RecyclerViewItemController(ViewGroup parent) {
        mDelegate.onCreate(parent.getContext(), null, parent);
        mHolder = new ViewHolder(this, mDelegate.getView());
    }

    public int getLayoutPosition() {
        return mHolder.getLayoutPosition();
    }

    public int getAdapterPosition() {
        return mHolder.getAdapterPosition();
    }

    public int getOldPosition() {
        return mHolder.getAdapterPosition();
    }

    public final long getItemId() {
        return mHolder.getItemId();
    }

    public final long getItemViewType() {
        return mHolder.getItemViewType();
    }



    @SuppressWarnings("unchecked")
    public static <T extends RecyclerViewItemController> T as(RecyclerView.ViewHolder viewHolder) {
        return (T) ((ViewHolder) viewHolder).controller;
    }

    @NonNull
    @Override
    public String toString() {
        return mHolder.toString();
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {
        public final RecyclerViewItemController controller;

        private ViewHolder(@NonNull RecyclerViewItemController controller, View view) {
            super(view);
            this.controller = controller;
        }
    }

}
