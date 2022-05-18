package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.client.ImageModel;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.StringUtil;

/**
 * 显示图片的适配器
 */
public class ImageAdapter extends NoAnimatorRecyclerView.BaseAdapter<ImageAdapter.ImageViewHolder, ImageModel> {
    private final AtomicInteger uploadedSize = new AtomicInteger(0);

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(layoutInflate(parent, R.layout.item_recycler_selected_image));
    }

    @Override
    public ImageModel getItem(int index) {
        return super.getItem(index);
    }

    public void updateItem(ImageModel model) {
        for (int i = 0; i < getItemCount(); i++) {
            if (StringUtil.equals(model.getFileName(), getItem(i).getFileName())) {
                updateItem(i, model);
                break;
            }
        }
    }

    public void addUploaded() {
        uploadedSize.addAndGet(1);
    }

    public boolean allUploaded() {
        Log.i(getClass().getName(), uploadedSize.get() + ":" + getItemCount());
        return uploadedSize.get() >= getItemCount();
    }

    public void remove(ImageModel model) {
        for (int i = 0; i < getItemCount(); i++) {
            if (StringUtil.equals(model.getFileName(), getItem(i).getFileName())) {
                remove(i);
                break;
            }
        }
    }

    public static class ImageViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<ImageModel> {
        private final ImageView imageView;
        private final TextView textView;
        private final Context context;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.item_recycler_selected_img_img);
            textView = itemView.findViewById(R.id.item_recycler_selected_img_text);
        }

        @Override
        public void bindView(ImageModel item) {
            GlideUtil.loadImage(context, item.getUrl(), imageView);
            if (item.getProcess() == 0) {
                textView.setText("");
            } else if (item.getProcess() == ImageModel.UPLOAD_FAIL_PROCESS) {
                textView.setText(R.string.tip_upload_fail);
            } else if (item.getProcess() < ImageModel.UPLOAD_SUCCESS_PROCESS) {
                textView.setText(context.getString(R.string.tip_upload_ing) + item.getProcess() + "%");
            } else if (item.getProcess() == ImageModel.UPLOAD_SUCCESS_PROCESS) {
                textView.setText(R.string.tip_upload_success);
            }
        }
    }
}
