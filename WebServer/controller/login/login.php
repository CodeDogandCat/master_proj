<?php
session_start();
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/DBPDO.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';

class Login
{
    var $user;
    var $db;

    /**
     * Register constructor.
     * @param $user
     */
    public function __construct($user)
    {
        $this->user = $user;
        $this->db = new DBPDO();
    }


    /**
     * 保存token（登录用）
     * @return bool
     */
    public function updateToken($user_id, $token)
    {
        $sql = 'UPDATE  bd_user SET user_token = ? WHERE user_id = ?';
        $arr = array();
        $arr[0] = $token;
        $arr[1] = $user_id;
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        return true;//更新成功
    }


    /**
     * 核对用户登录信息（登录用）,返回给用户token(注册时候生成，或者更换密码时候生成，早已存在数据库中),avatar,姓名
     * @return bool
     */
    public function checkUser()
    {
        $sql = 'SELECT user_family_name,user_given_name,user_token,user_avatar FROM bd_user WHERE user_email =? AND user_password=?';
        $arr = array();
        $arr[0] = $this->user->getEmail();
        $arr[1] = $this->user->getPassword();
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
            $family_name = $rows[0]['user_family_name'];
            $given_name = $rows[0]['user_given_name'];
            $token = $rows[0]['user_token'];
            $avatar = $rows[0]['user_avatar'];
            $data = array("token" => $token, "familyName" => $family_name, "givenName" => $given_name, "avatar" => $avatar);
            return $data;//存在,返回token给客户端
        }


        return false;//不存在
    }

    /**
     * 核对用户登录信息（加好友,邀请时候用）
     * @return mixed
     */
    public function checkUserByEmail()
    {
        $sql = 'SELECT * FROM bd_user WHERE user_email =? ';
        $arr = array();
        $arr[0] = $this->user->getEmail();
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
            $family_name = $rows[0]['user_family_name'];
            $given_name = $rows[0]['user_given_name'];
            $id = $rows[0]['user_id'];
            $avatar = $rows[0]['user_avatar'];
            $data = array("familyName" => $family_name, "givenName" => $given_name, "id" => $id, "avatar" => $avatar);
            return $data;//存在,返回
        }


        return false;//不存在
    }

    /**
     * 在数据库中匹配 token（token拦截器 用）
     * @param $tmpToken
     * @return bool
     */
    public function findTokenInDB($tmpToken)
    {
        $sql = 'SELECT user_id FROM bd_user WHERE user_token =?';
        $arr = array();
        $arr[0] = $tmpToken;
        if ($this->db->countItem($sql, $arr) == 1) {
            return true;//存在
        }
        return false;//不存在
    }

    /**
     * 检查 token（token拦截器 用）
     * @param $tmpToken
     * @return bool
     */
    function checkToken($tmpToken)
    {
        if (($token1 = Session::get(SESSION_TOKEN)) == false) {//可能从来都不存在或者过期啦
            //如果验证码过期,可以在数据库中找到
            if ($this->findTokenInDB($tmpToken)) {
                return true;
            }


        } else {
            //比较token
            if ($token1 == $tmpToken) {
                return true;
            }

        }
        return false;
    }

}