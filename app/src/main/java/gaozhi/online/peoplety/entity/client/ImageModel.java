package gaozhi.online.peoplety.entity.client;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageModel implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    public static final int UPLOAD_SUCCESS_PROCESS =100;
    public static final int UPLOAD_FAIL_PROCESS =-1;
    private int id;
    private int process;
    private String url;
    private String fileName;

    @Override
    public long getItemId() {
        return id;
    }

}
