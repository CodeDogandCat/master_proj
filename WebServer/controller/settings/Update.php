<?php
session_start();
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/DBPDO.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';

class Update
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
     * 获取密码和注册时间
     * @return bool
     */
    public function getPwdAndRegisterTime()
    {

        $sql = 'SELECT user_password,user_register_time FROM bd_user WHERE user_email =?';
        $arr = array();
        $arr[0] = $this->user->getEmail();
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
            return $rows[0];

        }
        return false;//不存在
    }

    /**
     * 更新密码和token(数据库中的session中的)
     * @param $newPwdEncrypted
     * @param $token
     * @return bool
     */
    public function updatePwdAndToken($newPwdEncrypted, $token)
    {

        $sql = 'UPDATE  bd_user SET user_password = ?,user_token = ? WHERE user_email = ?';
        $arr = array();
        $arr[0] = $newPwdEncrypted;
        $arr[1] = $token;
        $arr[2] = $this->user->getEmail();
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        //把token放到session中
        Session::set(SESSION_TOKEN, $token, 2592000);//30天过期
        return true;//更新成功
    }

    /**
     * 更新密码
     * @param $oldPwd
     * @param $newPwd
     * @return bool
     */
    public function updatePassword($oldPwd, $newPwd)
    {
        /**
         * 1.对收到的2个密码进行加密
         */
        $oldPwdEncrypted = EncryptUtil::hash($oldPwd, $this->user->getEmail());
        $newPwdEncrypted = EncryptUtil::hash($newPwd, $this->user->getEmail());

        /**
         *2.获取到对应邮箱的密码
         */
        $row = $this->getPwdAndRegisterTime();
        if ($row != false) {
            $pwd = $row['user_password'];
            $registerTime = $row['user_register_time'];

            /**
             * 3.比较oldPwd 和数据库中的密码是否相同
             */
            if ($oldPwdEncrypted == $pwd) {
                //同->更新
                $token = EncryptUtil::hash($this->user->getEmail() . $newPwdEncrypted . $registerTime, $registerTime);
                if ($this->updatePwdAndToken($newPwdEncrypted, $token)) {
                    //什么都不返回，让用户重新登陆，那个时候再返回TOKEN
                    return true;
                }

            }

        }

        return false;
    }


    /**
     * 更新用户的姓名
     * @return bool
     */
    public function updateName()
    {


        $sql = 'UPDATE  bd_user SET user_family_name = ?,user_given_name = ? WHERE user_email = ?';
        $arr = array();
        $arr[0] = $this->user->getFamilyName();
        $arr[1] = $this->user->getGivenName();
        $arr[2] = $this->user->getEmail();
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        return true;//更新成功
    }

}