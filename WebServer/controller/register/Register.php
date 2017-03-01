<?php
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/SmtpUtil.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/DBPDO.php';

class Register
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
     * 检查用户是否存在
     * @return bool
     */
    public function checkIfExists()
    {
        $sql = 'SELECT user_id FROM bd_user WHERE user_email =?';
        $arr = array();
        $arr[0] = $this->user->getEmail();
        if ($this->db->countItem($sql, $arr) >= 1) {
            return true;//存在
        }
        return false;//不存在
    }

    /**
     * 保存用户信息
     * @return bool
     */
    public function saveUser()
    {
        $sql = 'INSERT INTO bd_user (user_email,user_family_name,user_given_name,user_password,user_register_time,user_login_recent_time,user_token) 
                VALUES (?,?,?,?,?,?,?)';
        $arr = array();
        $arr[0] = $this->user->getEmail();
        $arr[1] = $this->user->getFamilyName();
        $arr[2] = $this->user->getGivenName();
        $arr[3] = $this->user->getPassword();
        $arr[4] = $this->user->getRegisterTime();
        $arr[5] = $this->user->getLoginRecentTime();
        $arr[6] = $this->user->getToken();
        if ($this->db->insert($sql, $arr)) {
            return true;//插入成功
        }
        return false;//插入失败
    }

    /**
     * 发送验证码
     * @return bool
     */
    public function sendVerifyCode()
    {
        $code = $this->generate_code(6);
        $subject = '验证码--小喵白板';
        $body = '<h2>请用下面的验证码完成邮箱的验证(2分钟内有效)，之后才能继续完成用户注册。</h2><br><br><h3>' . $code . '</h3>';
        if (SmtpUtil::sendMail($this->user->getEmail(), $subject, $body)) {
            return $code;
        }
        return false;
    }

    /**
     * 生成验证码
     * @param int $length
     * @return int
     */
    function generate_code($length = 6)
    {
        $min = pow(10, ($length - 1));
        $max = pow(10, $length) - 1;
        return rand($min, $max);
    }


}