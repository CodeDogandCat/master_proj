<?php
require_once 'settings.php';
header("Content-type: text/html; charset=utf-8");
date_default_timezone_set('PRC'); //设置中国时区
//error_reporting(0);
$dbms = 'mysql';     //数据库类型
$host = 'localhost'; //数据库主机名
$dbName = 'board';    //使用的数据库
$user = 'root';      //数据库连接用户名
$pass = 'lilei123';          //对应的密码
$dsn = "$dbms:host=$host;dbname=$dbName";
$db = null;

try {
    $db = new PDO($dsn, $user, $pass);
    //需要数据库长连接，这样更快
    $db->setAttribute(PDO::ATTR_PERSISTENT, true);
    //关闭预处理模拟
    $db->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
    //设置PDO异常模式，抛出 PDOException 异常，可以捕获并抛出具体异常信息
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo 'ok';


} catch (PDOException $e) {
    printResult(DATABASE_CONN_FAILED);
    die ("Error!: " . $e->getMessage() . "<br/>");
}


?>
