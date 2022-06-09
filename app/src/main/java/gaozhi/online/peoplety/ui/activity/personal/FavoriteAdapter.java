package gaozhi.online.peoplety.ui.activity.personal;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;

/**
 * 收藏夹
 */
public class FavoriteAdapter extends NoAnimatorRecyclerView.BaseAdapter<FavoriteAdapter.FavoriteViewHolder, Favorite> {
    public FavoriteAdapter() {
        super(Favorite.class);
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(layoutInflate(parent, R.layout.item_recycler_favorite));
    }

    public static class FavoriteViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Favorite> {
        private final TextView textName;
        private final TextView textDescription;
        private final TextView textVisible;
        private final ImageView imageBin;
        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.item_recycler_favorite_name);
            textDescription = itemView.findViewById(R.id.item_recycler_favorite_description);
            textVisible = itemView.findViewById(R.id.item_recycler_favorite_visible);
            imageBin = itemView.findViewById(R.id.item_recycler_favorite_bin);
        }

        @Override
        public void bindView(Favorite item) {
            textName.setText(item.getName());
            textDescription.setText(item.getDescription());
            textVisible.setText(item.isVisible()?R.string.favorite_public:R.string.favorite_private);

        }
    }
}
