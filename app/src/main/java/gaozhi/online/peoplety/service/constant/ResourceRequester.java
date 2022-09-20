package gaozhi.online.peoplety.service.constant;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.function.BiConsumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.GetMessageService;
import io.realm.Realm;

/**
 * 资源请求
 */
public class ResourceRequester implements DataHelper.OnDataListener<Result> {
    //资源有效时间
    private static final long RESOURCE_VALIDATE_PERIOD = 1000 * 60 * 60 * 24;
    private int count;
    private final Gson gson = new Gson();
    private final Realm realm;
    private UserDTO loginUser;
    private final DataHelper.OnDataListener<UserDTO> resultHandler;
    //资源请求响应接口
    private final BiConsumer<Integer, Boolean> requestConsumer;
    private final GetUserStatusService getUserStatusService = new GetUserStatusService(this);
    private final GetRecordAreaService getRecordAreaService = new GetRecordAreaService(this);
    private final GetRecordTypeService getRecordTypeService = new GetRecordTypeService(this);


    public ResourceRequester(@NonNull Realm realm, DataHelper.OnDataListener<UserDTO> resultHandler, @NonNull BiConsumer<Integer, Boolean> requestConsumer) {
        this.realm = realm;
        this.resultHandler = resultHandler;
        this.requestConsumer = requestConsumer;
    }

    public int getResourceSize() {
        return 3;
    }

    @Override
    public void start(int id) {
        if (resultHandler != null) {
            resultHandler.start(id);
        }
    }

    @Override
    public void handle(int id, Result result) {
        if (id == getUserStatusService.getId()) {//请求身份资源成功
            realm.executeTransaction(realm -> {
                List<Status> statuses = gson.fromJson(result.getData(), new TypeToken<List<Status>>() {
                }.getType());
                for (Status status : statuses) {
                    realm.copyToRealmOrUpdate(status);
                }
            });
            requestConsumer.accept(count++, false);
            // 开始请求地区资源
            getRecordAreaService.request(loginUser.getToken());
            return;
        }

        if (id == getRecordAreaService.getId()) {
            realm.executeTransaction(realm -> {
                List<Area> areas = gson.fromJson(result.getData(), new TypeToken<List<Area>>() {
                }.getType());
                for (Area area : areas) {
                    realm.copyToRealmOrUpdate(area);
                }
            });
            requestConsumer.accept(count++, false);
            //开始请求类型资源
            getRecordTypeService.request(loginUser.getToken());
            return;
        }

        if (id == getRecordTypeService.getId()) {
            realm.executeTransaction(realm -> {
                List<RecordType> recordTypes = gson.fromJson(result.getData(), new TypeToken<List<RecordType>>() {
                }.getType());
                for (RecordType recordType : recordTypes) {
                    RecordType old = realm.where(RecordType.class).equalTo("id", recordType.getId()).findFirst();
                    if (old != null) {
                        recordType.setSelected(old.isSelected());
                    }
                    realm.copyToRealmOrUpdate(recordType);
                }
            });
            //修改资源有效时间，并进入主页
            realm.executeTransaction(realm -> {
                //刷新资源有效期
                loginUser.setResourceValidateTime(System.currentTimeMillis() + RESOURCE_VALIDATE_PERIOD);
                realm.copyToRealmOrUpdate(loginUser);
            });
            //资源更新完成
            requestConsumer.accept(count++, true);
            return;
        }
    }

    @Override
    public void error(int id, int code, String message, String data) {
        if (resultHandler != null) {
            resultHandler.error(id, code, message, data);
        }
    }

    public void refreshResource(@NonNull UserDTO loginUser) {
        this.loginUser = loginUser;
        //更新常量
        getUserStatusService.request(loginUser.getToken());
    }
}