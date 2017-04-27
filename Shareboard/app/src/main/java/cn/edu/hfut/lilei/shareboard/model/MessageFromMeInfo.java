package cn.edu.hfut.lilei.shareboard.model;

import com.google.gson.Gson;

public class MessageFromMeInfo {
    public int type;
    public String content;
    public String filepath;
    public int sendState;
    public String time;
    public String header;
    public String imageUrl;
    public long voiceTime;
    public String msgId;
    public int indexOfAdapter;
    public int indexOfList;
    public String familyName;
    public String givenyName;
    public String client_email;//参与者邮箱

    public String getClient_email() {
        return client_email;
    }

    public void setClient_email(String client_email) {
        this.client_email = client_email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenyName() {
        return givenyName;
    }

    public void setGivenyName(String givenyName) {
        this.givenyName = givenyName;
    }

    public int getIndexOfAdapter() {
        return indexOfAdapter;
    }

    public void setIndexOfAdapter(int indexOfAdapter) {
        this.indexOfAdapter = indexOfAdapter;
    }

    public int getIndexOfList() {
        return indexOfList;
    }

    public void setIndexOfList(int indexOfList) {
        this.indexOfList = indexOfList;
    }

    public MessageFromMeInfo(String client_email, int type, String content, String filepath,
                             int sendState,
                             String time, String header, String imageUrl, long voiceTime,
                             String msgId, int indexOfAdapter, int indexOfList,
                             String familyName, String givenyName) {
        this.client_email = client_email;
        this.type = type;
        this.content = content;
        this.filepath = filepath;
        this.sendState = sendState;
        this.time = time;
        this.header = header;
        this.imageUrl = imageUrl;
        this.voiceTime = voiceTime;
        this.msgId = msgId;
        this.indexOfAdapter = indexOfAdapter;
        this.indexOfList = indexOfList;
        this.familyName = familyName;
        this.givenyName = givenyName;
    }

    public MessageFromMeInfo() {

    }

    public MessageInfo toMessageInfo() {
        return new MessageInfo(client_email,type, content, filepath, sendState, time, header, imageUrl,
                voiceTime, msgId, familyName, givenyName);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(long voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
//        return "MessageInfo{" +
//                "type=" + type +
//                ", content='" + content + '\'' +
//                ", filepath='" + filepath + '\'' +
//                ", sendState=" + sendState +
//                ", time='" + time + '\'' +
//                ", header='" + header + '\'' +
//                ", imageUrl='" + imageUrl + '\'' +
//                ", voiceTime=" + voiceTime +
//                ", msgId='" + msgId + '\'' +
//                '}';
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
