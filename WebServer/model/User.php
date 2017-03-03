<?php


class User
{
    private $id, $email, $family_name, $given_name, $register_time,
        $login_recent_time, $status, $class, $password, $pmi, $token,$avatar;

    /**
     * @return mixed
     */
    public function getToken()
    {
        return $this->token;
    }

    /**
     * @param mixed $token
     */
    public function setToken($token)
    {
        $this->token = $token;
    }


    /**
     * User constructor.
     * @param $email
     */
    public function __construct($email)
    {
        $this->email = $email;
    }


    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param mixed $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @return mixed
     */
    public function getEmail()
    {
        return $this->email;
    }

    /**
     * @param mixed $email
     */
    public function setEmail($email)
    {
        $this->email = $email;
    }

    /**
     * @return mixed
     */
    public function getFamilyName()
    {
        return $this->family_name;
    }

    /**
     * @param mixed $family_name
     */
    public function setFamilyName($family_name)
    {
        $this->family_name = $family_name;
    }

    /**
     * @return mixed
     */
    public function getGivenName()
    {
        return $this->given_name;
    }

    /**
     * @param mixed $given_name
     */
    public function setGivenName($given_name)
    {
        $this->given_name = $given_name;
    }

    /**
     * @return mixed
     */
    public function getRegisterTime()
    {
        return $this->register_time;
    }

    /**
     * @param mixed $register_time
     */
    public function setRegisterTime($register_time)
    {
        $this->register_time = $register_time;
    }

    /**
     * @return mixed
     */
    public function getLoginRecentTime()
    {
        return $this->login_recent_time;
    }

    /**
     * @param mixed $login_recent_time
     */
    public function setLoginRecentTime($login_recent_time)
    {
        $this->login_recent_time = $login_recent_time;
    }

    /**
     * @return mixed
     */
    public function getStatus()
    {
        return $this->status;
    }

    /**
     * @param mixed $status
     */
    public function setStatus($status)
    {
        $this->status = $status;
    }

    /**
     * @return mixed
     */
    public function getClass()
    {
        return $this->class;
    }

    /**
     * @param mixed $class
     */
    public function setClass($class)
    {
        $this->class = $class;
    }

    /**
     * @return mixed
     */
    public function getPassword()
    {
        return $this->password;
    }

    /**
     * @param mixed $password
     */
    public function setPassword($password)
    {
        $this->password = $password;
    }

    /**
     * @return mixed
     */
    public function getPmi()
    {
        return $this->pmi;
    }

    /**
     * @param mixed $pmi
     */
    public function setPmi($pmi)
    {
        $this->pmi = $pmi;
    }

    /**
     * @return mixed
     */
    public function getAvatar()
    {
        return $this->avatar;
    }

    /**
     * @param mixed $avatar
     */
    public function setAvatar($avatar)
    {
        $this->avatar = $avatar;
    }


}