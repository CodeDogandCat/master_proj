package cn.edu.hfut.lilei.shareboard.models;


import java.util.List;

import cn.edu.hfut.lilei.shareboard.enity.MessageInfo;

public class MessageListSendJson {
    public List<MessageInfo> data;

    public List<MessageInfo> getData() {
        return data;
    }

    public void setData(List<MessageInfo> data) {
        this.data = data;
    }
}
