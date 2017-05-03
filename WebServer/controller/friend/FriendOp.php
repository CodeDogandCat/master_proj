<?php
session_start();
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/DBPDO.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/Jpush.php';

class FriendOp
{
    var $user1;
    var $user2;
    var $msg_data;
    var $db;

    public function __construct($user1, $user2, $msg_data)
    {
        $this->user1 = $user1;
        $this->user2 = $user2;
        $this->msg_data = $msg_data;
        $this->db = new DBPDO();
    }


    /**
     * 根据 email获取 用户id
     * @return mixed
     */
    public function getUserIdFromEmail($email)
    {
        $sql = 'SELECT user_id FROM bd_user WHERE user_email = ?';
        $arr = array();
        $arr[0] = $email;
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
//            var_dump($rows);
            return $rows[0]['user_id'];

        }
        return false;//不存在
    }

    /**
     * 根据 用户id 获取 email
     * @return mixed
     */
    public function getEmailFromUserId($id)
    {
        $sql = 'SELECT user_email FROM bd_user WHERE  user_id= ?';
        $arr = array();
        $arr[0] = $id;
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
//            var_dump($rows);
            return $rows[0]['user_email'];

        }
        return false;//不存在
    }

    /**
     * 给 user_id排序, 小的在前面
     * @param $user_id1
     * @param $user_id2
     * @return array
     */
    public function compare($user_id1, $user_id2)
    {
        $tmp1 = $user_id1;
        $tmp2 = $user_id2;
        if ($user_id1 > $user_id2) {
            $tmp = $user_id1;
            $tmp1 = $user_id2;
            $tmp2 = $tmp;

        }
        return [$tmp1, $tmp2];
    }

    /**
     * 存在互加好友的记录(可能只是申请,没同意,各种情况都算)
     */
    public function hasHistory()
    {
        $sql = 'SELECT friend_id FROM bd_friend WHERE bd_user_user_id =? AND bd_user_user_id1=? ';

        $arr = $this->compare($this->user1->getId(), $this->user2->getId());

        $rows = $this->db->select($sql, $arr);

        if (count($rows) >= 1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 当前是否是好友关系
     */
    public function isFriendNow()
    {
        if ($this->hasHistory() == false) {
            return false;
        } else {
            $sql = 'SELECT friend_id,response_status FROM bd_friend WHERE bd_user_user_id =? AND bd_user_user_id1=? AND response_status = 3 ';
            $arr = $this->compare($this->user1->getId(), $this->user2->getId());
            $rows = $this->db->select($sql, $arr);
            if (count($rows) >= 1) {
                //存在记录,检查response_status
                return true;
            }
        }

        return false;
    }

    /**
     * 请求加好友
     * @return bool
     */
    public function requestAddFriend($tag)
    {


        $content = $this->user1->getFamilyName() . $this->user1->getGivenName() . "请求加你为好友";
        $data = array();
        $data['title'] = "好友申请";
        $data['content_type'] = 'text';
        $extras = array('familyName' => $this->user1->getFamilyName(),
            'givenName' => $this->user1->getGivenName(),
            'email' => $this->user1->getEmail(),
            'avatar' => $this->user1->getAvatar(),
            'tag' => $tag,
            "feature" => "requestAddFriend");
        $data['extras'] = $extras;

        //1.判断是否有历史
        if ($this->hasHistory() == false) {
            //插入
            $sql = 'INSERT INTO bd_friend (response_status,message_time,bd_user_user_id,bd_user_user_id1) 
                VALUES (?,?,?,?)';

            $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
            $dt = new DateTime();
            $arr = array();
            $arr[0] = 1;//申请未回复
            $arr[1] = $dt->format('Y-m-d H:i:s');
            $arr[2] = $tmp[0];
            $arr[3] = $tmp[1];

            if ($this->db->insert($sql, $arr) != false) {
                //推送
                if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                    return true;
                }
            }

        } else {
            //更新
            $sql = 'UPDATE  bd_friend SET   response_status = ?,message_time= ? WHERE bd_user_user_id =? AND bd_user_user_id1=?';

            $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
            $dt = new DateTime();
            $arr = array();
            $arr[0] = 1;//申请未回复
            $arr[1] = $dt->format('Y-m-d H:i:s');
            $arr[2] = $tmp[0];
            $arr[3] = $tmp[1];

            if ($this->db->update($sql, $arr) != false) {
                //推送
                if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                    return true;
                }

            }

        }


        return false;//插入失败
    }

    /**
     * 同意加好友
     * @return bool
     */
    public function acceptAddFriend($tag)
    {


        $content = $this->user1->getFamilyName() . $this->user1->getGivenName() . "同意加你为好友";
        $data = array();
        $data['title'] = "好友回复";
        $data['content_type'] = 'text';
        $extras = array('familyName' => $this->user1->getFamilyName(),
            'givenName' => $this->user1->getGivenName(),
            'email' => $this->user1->getEmail(),
            'avatar' => $this->user1->getAvatar(),
            'tag' => $tag,
            "feature" => "acceptFriend");
        $data['extras'] = $extras;

        //1.判断是否有历史
        if ($this->hasHistory() == false) {
            //插入
            $sql = 'INSERT INTO bd_friend (response_status,message_time,bd_user_user_id,bd_user_user_id1) 
                VALUES (?,?,?,?)';

            $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
            $dt = new DateTime();
            $arr = array();
            $arr[0] = 3;//同意
            $arr[1] = $dt->format('Y-m-d H:i:s');
            $arr[2] = $tmp[0];
            $arr[3] = $tmp[1];

            if ($this->db->insert($sql, $arr) != false) {
                //推送
                if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                    return true;
                }
            }

        } else {
            //更新
            $sql = 'UPDATE  bd_friend SET   response_status = ?,message_time= ? WHERE bd_user_user_id =? AND bd_user_user_id1=?';

            $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
            $dt = new DateTime();
            $arr = array();
            $arr[0] = 3;//同意
            $arr[1] = $dt->format('Y-m-d H:i:s');
            $arr[2] = $tmp[0];
            $arr[3] = $tmp[1];

            if ($this->db->update($sql, $arr) != false) {
                //推送
                if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                    return true;
                }

            }

        }


        return false;//插入失败
    }

    /**
     * 拒绝加好友
     * @return bool
     */
    public function rejectAddFriend($tag)
    {


        $content = $this->user1->getFamilyName() . $this->user1->getGivenName() . "拒绝加你为好友";
        $data = array();
        $data['title'] = "好友回复";
        $data['content_type'] = 'text';
        $extras = array('familyName' => $this->user1->getFamilyName(),
            'givenName' => $this->user1->getGivenName(),
            'email' => $this->user1->getEmail(),
            'avatar' => $this->user1->getAvatar(),
            'tag' => $tag,
            "feature" => "rejectFriend");
        $data['extras'] = $extras;

        //1.判断是否有历史
        if ($this->hasHistory() == false) {
            //插入
            $sql = 'INSERT INTO bd_friend (response_status,message_time,bd_user_user_id,bd_user_user_id1) 
                VALUES (?,?,?,?)';

            $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
            $dt = new DateTime();
            $arr = array();
            $arr[0] = 2;//拒绝
            $arr[1] = $dt->format('Y-m-d H:i:s');
            $arr[2] = $tmp[0];
            $arr[3] = $tmp[1];

            if ($this->db->insert($sql, $arr) != false) {
                //推送
                if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                    return true;
                }
            }

        } else {
            //更新
            $sql = 'UPDATE  bd_friend SET   response_status = ?,message_time= ? WHERE bd_user_user_id =? AND bd_user_user_id1=?';

            $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
            $dt = new DateTime();
            $arr = array();
            $arr[0] = 2;//拒绝
            $arr[1] = $dt->format('Y-m-d H:i:s');
            $arr[2] = $tmp[0];
            $arr[3] = $tmp[1];

            if ($this->db->update($sql, $arr) != false) {
                //推送
                if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                    return true;
                }

            }

        }


        return false;//插入失败
    }


    /**
     * 请求删除好友
     * @return bool
     */
    public function requestDelFriend()
    {


        $content = $this->user1->getFamilyName() . $this->user1->getGivenName() . "把你从好友列表中移除";
        $data = array();
        $data['title'] = "删除好友";
        $data['content_type'] = 'text';
        $extras = array('familyName' => $this->user1->getFamilyName(),
            'givenName' => $this->user1->getGivenName(),
            'email' => $this->user1->getEmail(),
            'avatar' => $this->user1->getAvatar(),
            "feature" => "deleteFriend");
        $data['extras'] = $extras;

        //更新
        $sql = 'UPDATE  bd_friend SET   response_status = ?,message_time= ? WHERE bd_user_user_id =? AND bd_user_user_id1=?';

        $tmp = $this->compare($this->user1->getId(), $this->user2->getId());
        $dt = new DateTime();
        $arr = array();
        $arr[0] = 4;//好友关系已经删除
        $arr[1] = $dt->format('Y-m-d H:i:s');
        $arr[2] = $tmp[0];
        $arr[3] = $tmp[1];

        if ($this->db->update($sql, $arr) != false) {
            //推送
            if (Jpush::pushMsg($this->user2->getEmail(), $content, $data) == true) {
                return true;
            }

        }


        return false;//失败
    }

    public function getAllFriend()
    {
//        echo "#1" . $this->user1->getId();
        $datas = array();
        $sql = 'SELECT * FROM bd_friend WHERE response_status = 3 AND ( bd_user_user_id =? OR  bd_user_user_id1 =? ) ';
//        $sql = 'SELECT * FROM bd_friend WHERE response_status = 3 AND bd_user_user_id =? ';
        $arr = array();
        $arr[0] = $this->user1->getId();
        $arr[1] = $this->user1->getId();
        $rows = $this->db->select($sql, $arr);


//        echo "$rows size" . count($rows);
        $datas = $this->convertData($rows);
//        echo "size" . count($datas);

//        $sql = 'SELECT * FROM bd_friend WHERE response_status = 3 AND bd_user_user_id1 =? ';
//        $arr = array();
//        $arr[0] = $this->user->getId();
//        $rows = $this->db->select($sql, $arr);
//        $datas = array_merge($datas, $this->convertData($rows));

//        echo "#3";
        return $datas;

    }

    /**
     * @param $rows
     * @return mixed
     */
    public function convertData($rows)
    {
        $datas = array();
        foreach ($rows as $value) {
            $friend_id = -1;
            if ($value['bd_user_user_id'] != $this->user1->getId()) {
                $friend_id = $value['bd_user_user_id'];
            } else {
                $friend_id = $value['bd_user_user_id1'];
            }
//            echo "friend_id" . $friend_id;

            //查找个人信息
            $sql = 'SELECT * FROM bd_user WHERE user_id =? ';
            $arr = array();
            $arr[0] = $friend_id;
            $tmp = $this->db->select($sql, $arr);

            if (count($tmp) == 1) {//存在且只存在一个这样的用户
                $family_name = $tmp[0]['user_family_name'];
                $given_name = $tmp[0]['user_given_name'];
                $email = $tmp[0]['user_email'];
                $avatar = $tmp[0]['user_avatar'];


                $data = array("familyName" => $family_name, "givenName" => $given_name, "email" => $email, "avatar" => $avatar);
                //添加
                array_push($datas, $data);
            }


        }
        return $datas;
    }

}