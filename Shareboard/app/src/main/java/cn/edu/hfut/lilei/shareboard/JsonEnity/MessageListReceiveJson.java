package cn.edu.hfut.lilei.shareboard.JsonEnity;


import java.util.List;

import cn.edu.hfut.lilei.shareboard.model.MessageInfo;

public class MessageListReceiveJson {
    public String type;//信息类型
    public String from_client_email;//用户邮箱
    public String to_client_email;//用户邮箱
    public List<MessageInfo> chat_list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom_client_email() {
        return from_client_email;
    }

    public void setFrom_client_email(String from_client_email) {
        this.from_client_email = from_client_email;
    }

    public String getTo_client_email() {
        return to_client_email;
    }

    public void setTo_client_email(String to_client_email) {
        this.to_client_email = to_client_email;
    }

    public List<MessageInfo> getChat_list() {
        return chat_list;
    }

    public void setChat_list(List<MessageInfo> chat_list) {
        this.chat_list = chat_list;
    }
}
