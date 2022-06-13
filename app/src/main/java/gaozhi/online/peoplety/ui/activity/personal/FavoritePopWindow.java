package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.github.pagehelper.PageInfo;

import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Item;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetFavoritesByUseridService;
import gaozhi.online.peoplety.service.record.PublishFavoriteItemService;
import gaozhi.online.peoplety.ui.base.DBBasePopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;

/**
 * 收藏夹
 */
public class FavoritePopWindow extends DBBasePopWindow implements DataHelper.OnDataListener<PageInfo<Favorite>>, Consumer<Favorite>, NoAnimatorRecyclerView.OnLoadListener, View.OnClickListener {
    public static final int PAGE_SIZE = 10;
    //service
    private final GetFavoritesByUseridService getFavoritesByUseridService = new GetFavoritesByUseridService(this);
    //db
    private UserDTO loginUser;
    //ui
    private FavoriteAdapter favoriteAdapter;
    private Consumer<Item> itemConsumer;
    private Button btnManager;
    //data
    private PageInfo<Favorite> currentPageInfo;
    private long recordId;

    //service
    //收藏
    private final PublishFavoriteItemService publishFavoriteItemService = new PublishFavoriteItemService(new DataHelper.OnDataListener<>() {
        @Override
        public void handle(int id, Item data) {
            //收藏成功
            dismiss();
            if (itemConsumer != null) {
                itemConsumer.accept(data);
            }
        }
    });

    public void setItemConsumer(Consumer<Item> itemConsumer) {
        this.itemConsumer = itemConsumer;
    }

    public FavoritePopWindow(Context context) {
        super(context, R.layout.pop_window_favorite_item, true);
    }

    @Override
    protected void initView(View rootView) {
        loginUser = getRealm().where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = getRealm().copyFromRealm(loginUser);

        NoAnimatorRecyclerView recyclerViewFavorite = rootView.findViewById(R.id.pop_window_favorite_item_recycler_items);
        recyclerViewFavorite.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(rootView.getContext()));
        recyclerViewFavorite.setOnLoadListener(this);
        favoriteAdapter = new FavoriteAdapter();
        recyclerViewFavorite.setAdapter(favoriteAdapter);
        favoriteAdapter.setOnItemClickedListener(this);

        btnManager = rootView.findViewById(R.id.pop_window_favorite_item_btn_manage);
        btnManager.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context context) {

    }

    public void showPopupWindow(View parent, long recordId) {
        this.recordId = recordId;
        super.showPopupWindow(parent);
        getFavoritesByUseridService.request(loginUser.getToken(), loginUser.getUserInfo().getId(), 1, PAGE_SIZE);
    }

    @Override
    public void handle(int id, PageInfo<Favorite> data, boolean local) {
        if (data == null) return;
        currentPageInfo = data;
        if (currentPageInfo.getPageNum() <= 1) {
            favoriteAdapter.clear();
        }
        favoriteAdapter.add(data.getList());
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
    }

    @Override
    public void accept(Favorite favorite) {
        Item item = new Item();
        item.setFavoriteId(favorite.getId());
        item.setRecordId(recordId);
        publishFavoriteItemService.request(loginUser.getToken(), item);
    }

    @Override
    public void onLoad() {
        if (currentPageInfo != null && currentPageInfo.isHasNextPage()) {
            getFavoritesByUseridService.request(loginUser.getToken(), loginUser.getUserInfo().getId(), currentPageInfo.getNextPage(), PAGE_SIZE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnManager.getId()) {
            dismiss();
            FavoriteActivity.startActivity(getContext(), loginUser.getUserInfo().getId());
        }
    }
}