package gaozhi.online.peoplety.ui.activity.personal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import gaozhi.online.peoplety.entity.Item;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.ui.activity.record.RecordAdapter;
import io.realm.Realm;

/**
 * 收藏条目适配器
 */
public class FavoriteItemAdapter extends RecordAdapter implements Function<Item, Record> {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private final Realm realm;

    public FavoriteItemAdapter(Token token, Realm realm) {
        super(token);
        this.realm = realm;
        init(new BaseSortedListAdapterCallback<>() {
            @Override
            public int compare(Record o1, Record o2) {
                return (int) (itemMap.get(o2.getId()).getId() - itemMap.get(o1.getId()).getId());
            }
        });
    }

    public void addItem(List<Item> items) {
        for (Item item : items) {
            itemMap.put(item.getRecordId(), item);
        }
        add(items, this);
    }

    @Override
    public void clear() {
        super.clear();
        itemMap.clear();
    }

    @Override
    public Record apply(Item item) {
        Record record = realm.where(Record.class).equalTo("id", item.getRecordId()).findFirst();
        if (record == null) {
            record = new Record();
            record.setId(item.getRecordId());
            record.setImgs("[]");
        }
        return record;
    }
}
