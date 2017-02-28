<?php

require_once 'settings.php';
//定义数据库信息

header("Content-type:text/html; charset=utf-8");


class DBPDO
{

    public $dsn;
    public $dbuser;
    public $dbpwd;
    public $sth;
    public $dbh;

    //初始化
    function __construct()
    {
        $this->dsn = DBMS . ':host=' . DB_HOST . ';dbname=' . DB_NAME;
        $this->dbuser = DB_USER;
        $this->dbpwd = DB_PWD;
        $this->connect();
        //设置PDO异常模式，抛出 PDOException 异常，可以捕获并抛出具体异常信息
        $this->dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $this->dbh->query("SET NAMES 'UTF8'");
        $this->dbh->query("SET TIME_ZONE = '+8:00'");
    }

    //连接数据库
    public function connect()
    {
        try {
            $this->dbh = new PDO($this->dsn, $this->dbuser, $this->dbpwd);
        } catch (PDOException $e) {
            printResult(DATABASE_CONN_FAILED);
            exit('连接失败:' . $e->getMessage());
        }
    }

    //获取表字段
    public function getFields($table)
    {
        $this->sth = $this->dbh->query("DESCRIBE $table");
        $this->getPDOError();
        $this->sth->setFetchMode(PDO::FETCH_ASSOC);
        $result = $this->sth->fetchAll();
        $this->sth = null;
        return $result;
    }

    //插入数据
    public function insert($sql)
    {
        if ($this->dbh->exec($sql)) {
            $this->getPDOError();
            return $this->dbh->lastInsertId();
        }
        return false;
    }

    //删除数据
    public function delete($sql)
    {
        if (($rows = $this->dbh->exec($sql)) > 0) {
            $this->getPDOError();
            return $rows;
        } else {
            return false;
        }
    }

    //更改数据
    public function update($sql)
    {
        if (($rows = $this->dbh->exec($sql)) > 0) {
            $this->getPDOError();
            return $rows;
        }
        return false;
    }

    //获取数据
    public function select($sql)
    {
        $this->sth = $this->dbh->query($sql);
        $this->getPDOError();
        $this->sth->setFetchMode(PDO::FETCH_ASSOC);
        $result = $this->sth->fetchAll();
        $this->sth = null;
        return $result;
    }

    //获取数目
    public function countItem($sql, $arr)
    {
        $stmt = $this->dbh->prepare($sql);
        $stmt->execute($arr);

        $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
//        $count = $this->dbh->query($sql);

        $this->getPDOError();
        return count($rows);
    }

    //获取PDO错误信息
    private function getPDOError()
    {
        if ($this->dbh->errorCode() != '00000') {
            $error = $this->dbh->errorInfo();
            exit($error[2]);
        }
    }

    //关闭连接
    public function __destruct()
    {
        $this->dbh = null;
    }
}





