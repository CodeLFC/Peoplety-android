package gaozhi.online.peoplety.entity.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    private long from;
    private long to;
    private String headUrl;
    private String name;
    private String remark;
    private int unread;
    private long time;
}
