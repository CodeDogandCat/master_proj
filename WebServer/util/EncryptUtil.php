<?php
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';

class EncryptUtil
{


    public static function hash($a, $salt)
    {
        $b = $salt . $a . $salt . 'lilei';  //把密码和salt连接
        $b = md5($b);  //执行MD5散列
        return $b;  //返回散列
    }
}