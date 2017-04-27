package cn.edu.hfut.lilei.shareboard.JsonEnity;


import java.util.List;

import cn.edu.hfut.lilei.shareboard.model.MessageInfo;

public class MessageListSendJson {
    public List<MessageInfo> data;

    public List<MessageInfo> getData() {
        return data;
    }

    public void setData(List<MessageInfo> data) {
        this.data = data;
    }
}
