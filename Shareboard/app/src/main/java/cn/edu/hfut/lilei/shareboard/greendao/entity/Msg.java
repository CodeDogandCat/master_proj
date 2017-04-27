package cn.edu.hfut.lilei.shareboard.greendao.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;


/**
 * 存入本地数据库 定义本地数据表
 * 表名 msg
 * 字段
 * id   long
 * title string
 * familyName  string
 * givenName string
 * feature string
 * status  int  0 请求加好友[同意,拒绝] 1 点击同意:你们已经是好友了 2 已拒绝
 * msgTime long
 */


@Entity
public class Msg {
    @Id(autoincrement = true)
    private Long id;
    private String email;
    private String title;
    private String content;
    private String familyName;
    private String givenName;
    private String feature;
    private String avatar;
    private int status;
    private Long msgTime;
    private String tag;
    @Transient
    private int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Generated(hash = 1927517163)
    public Msg(Long id, String email, String title, String content,
            String familyName, String givenName, String feature, String avatar,
            int status, Long msgTime, String tag) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.content = content;
        this.familyName = familyName;
        this.givenName = givenName;
        this.feature = feature;
        this.avatar = avatar;
        this.status = status;
        this.msgTime = msgTime;
        this.tag = tag;
    }
    @Generated(hash = 23037457)
    public Msg() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getFamilyName() {
        return this.familyName;
    }
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    public String getGivenName() {
        return this.givenName;
    }
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    public String getFeature() {
        return this.feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public Long getMsgTime() {
        return this.msgTime;
    }
    public void setMsgTime(Long msgTime) {
        this.msgTime = msgTime;
    }
    public String getTag() {
        return this.tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }


}
