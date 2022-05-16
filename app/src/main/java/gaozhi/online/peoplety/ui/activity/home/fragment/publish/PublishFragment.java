package gaozhi.online.peoplety.ui.activity.home.fragment.publish;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.function.Consumer;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublishFragment extends DBBaseFragment implements Consumer<RecordType> {
    //ui
    private TextView title;
    private RecyclerView recordTypeRecyclerView;
    private RecordTypeAdapter recordTypeAdapter;
    private ImageView imagePreStep;

    //service
    //类型
    @Override
    protected void doBusiness(Realm realm) {

    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_publish;
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_publish);
        recordTypeRecyclerView = view.findViewById(R.id.fragment_publish_recycler_record_type);
        recordTypeAdapter = new RecordTypeAdapter();
        recordTypeRecyclerView.setAdapter(recordTypeAdapter);
        List<RecordType> recordTypes = getRealm().where(RecordType.class).equalTo("parentId", 0).findAll();
        recordTypeAdapter.add(recordTypes);

        imagePreStep = view.findViewById(R.id.fragment_publish_image_pre_step);
        imagePreStep.setOnClickListener(this);
        recordTypeAdapter.setOnItemClickedListener(this);
    }

    @Override
    public void initParams(Bundle bundle) {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == imagePreStep.getId()) {
            int parentId = recordTypeAdapter.getParentId();
            RecordType parentRecord = getRealm().where(RecordType.class).equalTo("id", parentId).findFirst();
            if (parentRecord != null) {
                title.setText(parentRecord.getName());
                List<RecordType> parents = getRealm().where(RecordType.class).equalTo("parentId", parentRecord.getParentId()).findAll();
                if (parents.size() > 0) {
                    recordTypeAdapter.clear();
                    recordTypeAdapter.add(parents);
                }
            }else{
                title.setText(R.string.bottom_publish);
            }
        }
    }


    @Override
    public void accept(RecordType recordType) {
        List<RecordType> recordTypes = getRealm().where(RecordType.class).equalTo("parentId", recordType.getId()).findAll();
        if (recordTypes.size() > 0) {
            title.setText(recordType.getName());
            recordTypeAdapter.clear();
            recordTypeAdapter.add(recordTypes);
            return;
        }
        //是最终节点
        ToastUtil.showToastShort("打开发布页面");
    }
}