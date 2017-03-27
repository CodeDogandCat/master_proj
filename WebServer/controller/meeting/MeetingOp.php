<?php
session_start();
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/model/Meeting.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/DBPDO.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/util/Particle.php';

class MeetingOp
{
    var $user;
    var $meeting;
    var $db;

    /**
     * Register constructor.
     * @param $user
     * @param $meeting
     */
    public function __construct($user, $meeting)
    {
        $this->user = $user;
        $this->meeting = $meeting;
        $this->db = new DBPDO();
    }


    /**
     * 主持会议,插入会议表
     * @return bool
     */
    public function addMeeting()
    {
        if (($user_id = $this->getUserIdFromEmail()) != false) {

            $sql = 'INSERT INTO bd_meeting (meeting_url,meeting_theme,meeting_host_user_id,meeting_is_drawable,
                meeting_is_talkable,meeting_is_add_to_calendar,meeting_password,
                meeting_start_time,meeting_end_time,meeting_status,event_id,meeting_desc) 
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?)';

            $arr = array();
            $arr[0] = Particle::generateParticle();
            $arr[1] = $this->meeting->getTheme();
            $arr[2] = $user_id;
            $arr[3] = $this->meeting->getIsDrawable();
            $arr[4] = $this->meeting->getIsTalkable();
            $arr[5] = $this->meeting->getIsAddToCalendar();
            $arr[6] = $this->meeting->getPassword();
            $arr[7] = $this->meeting->getStartTime();
            $arr[8] = $this->meeting->getEndTime();
            $arr[9] = $this->meeting->getStatus();
            $arr[10] = $this->meeting->getEventId();
            $arr[11] = $this->meeting->getMeetingDesc();

            if (($meeting_id = $this->db->insert($sql, $arr)) != false) {
                $arr2 = array();//返回 会议ID 会议URL
                $arr2[0] = (int)($meeting_id);
                $arr2[1] = $arr[0];

                return $arr2;//插入成功
            }
        }

        return false;//插入失败
    }

    /**
     * 更改会议,插入会议表
     * @return bool
     */
    public function updateMeeting()
    {
        $sql = 'UPDATE  bd_meeting SET   meeting_theme = ?,meeting_is_drawable = ?,
                                         meeting_is_talkable = ?,meeting_is_add_to_calendar = ?,
                                         meeting_password = ?,meeting_start_time = ?,
                                         meeting_end_time = ?,meeting_status = ?,event_id = ?,meeting_desc = ?
 
                                   WHERE meeting_id = ?';
        $arr = array();
        $arr[0] = $this->meeting->getTheme();
        $arr[1] = $this->meeting->getIsDrawable();
        $arr[2] = $this->meeting->getIsTalkable();
        $arr[3] = $this->meeting->getIsAddToCalendar();
        $arr[4] = $this->meeting->getPassword();
        $arr[5] = $this->meeting->getStartTime();
        $arr[6] = $this->meeting->getEndTime();
        $arr[7] = $this->meeting->getStatus();
        $arr[8] = $this->meeting->getEventId();
        $arr[9] = $this->meeting->getMeetingDesc();
        $arr[10] = $this->meeting->getId();
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        return true;//更新成功

    }

    /**
     * 删除会议
     * @return bool
     */
    public function deleteMeeting()
    {
        $sql = 'DELETE FROM bd_meeting  WHERE meeting_id = ?';


        $arr = array();
        $arr[0] = $this->meeting->getId();
        if ($this->db->delete($sql, $arr) == false) {

            return false;//删除失败
        }
        return true;//删除成功

    }

    /**
     * 根据 email获取 用户id
     * @return mixed
     */
    public function getUserIdFromEmail()
    {
        $sql = 'SELECT user_id FROM bd_user WHERE user_email = ?';
        $arr = array();
        $arr[0] = $this->user->getEmail();
//        echo "email" . $arr[0];
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
//            var_dump($rows);
//            echo $rows[0]["user_id"];
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
//        echo "email" . $arr[0];
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
//            var_dump($rows);
//            echo $rows[0]["user_id"];
            return $rows[0]['user_email'];

        }
        return false;//不存在
    }

    /**
     * 根据会议ID 获取会议装状态
     * @return mixed
     */
    public function getMeetingStatusById()
    {
        if (($meeting_id = $this->meeting->getId()) != null) {
            $sql = 'SELECT meeting_status FROM bd_meeting WHERE meeting_id =?';
            $arr = array();
            $arr[0] = $meeting_id;
            $rows = $this->db->select($sql, $arr);

            return $rows[0]['meeting_status'];

        }

        return false;//不存在
    }

    /**
     * 根据会议url获取 会议号,会议状态和密码
     * @return mixed
     */
    public function getMeetingInfoByUrl()
    {
        if (($meeting_url = $this->meeting->getUrl()) != null) {
            $sql = 'SELECT meeting_status,meeting_password,meeting_id,meeting_host_user_id FROM bd_meeting WHERE meeting_url =?';
            $arr = array();
            $arr[0] = $meeting_url;
            $rows = $this->db->select($sql, $arr);
            if (count($rows) == 1) {//存在且只存在一个这样的用户
                return $rows[0];
            }

        }

        return false;//不存在
    }

    /**
     * 检查user_and_meeting 表中 是否存在同样的（主持会议或）加会记录
     * @param $type
     * @return mixed
     */
    public function checkIfExistSameUserAndMeeting($type)
    {
        $sql = 'SELECT user_and_meeting_id FROM bd_user_and_meeting WHERE bd_user_user_id = ? AND
                bd_meeting_meeting_id = ? And check_in_type= ?';
        $arr = array();
        $arr[0] = $this->user->getId();
        $arr[1] = $this->meeting->getId();
        $arr[2] = $type;
        $rows = $this->db->select($sql, $arr);

        if (count($rows) >= 1) {//存在
            return $rows[0]['user_and_meeting_id'];

        }
        return false;//不存在
    }

    /**
     * 增加入会记录
     * @return mixed
     */
    public function addCheckIn($type)
    {

        $sql = 'INSERT INTO bd_user_and_meeting (check_in_type,check_in_time,bd_user_user_id,bd_meeting_meeting_id) 
                VALUES (?,?,?,?)';
        $arr = array();
        $arr[0] = $type;
        $dt = new DateTime();
        $arr[1] = $dt->format('Y-m-d H:i:s');
        $arr[2] = $this->user->getId();
        $arr[3] = $this->meeting->getId();
//        var_dump($arr);
        if (($user_and_meeting_id = $this->db->insert($sql, $arr)) == false) {

            return false;//插入失败
        }
        return $user_and_meeting_id;//插入成功
    }

    /**
     * 更新入会记录
     * @param $_id
     * @return mixed
     */
    public function updateCheckIn($_id)
    {
        $sql = 'UPDATE  bd_user_and_meeting SET check_in_time = ? WHERE user_and_meeting_id = ?';
        $arr = array();
        $dt = new DateTime();
        $arr[0] = $dt->format('Y-m-d H:i:s');
        $arr[1] = $_id;
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        return $_id;//更新成功
    }

    /**
     * 更新离会记录
     * @param $_id
     * @return bool
     */
    public function updateCheckOut($_id)
    {
        $sql = 'UPDATE  bd_user_and_meeting SET check_out_time = ? WHERE user_and_meeting_id = ?';
        $arr = array();
        $dt = new DateTime();
        $arr[0] = $dt->format('Y-m-d H:i:s');
        $arr[1] = $_id;
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        return true;//更新成功
    }

    /**
     * 更新会议状态
     * @param $toStatus 1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束
     * @return bool
     */
    public function updateMeetingStatus($toStatus)
    {
        $sql = 'UPDATE  bd_meeting SET meeting_status = ? WHERE meeting_id = ?';
        $arr = array();
        $arr[0] = $toStatus;
        $arr[1] = $this->meeting->getId();
        if ($this->db->update($sql, $arr) == false) {

            return false;//更新失败
        }
        return true;//更新成功
    }


    /**
     * 根据 user_and_meeting_id 获取 进会信息
     * @param $_id
     * @return mixed
     */
    public function getCheckInInfoById($_id)
    {
        $sql = 'SELECT check_in_type,bd_user_user_id,bd_meeting_meeting_id FROM bd_user_and_meeting
                WHERE user_and_meeting_id =?';
        $arr = array();
        $arr[0] = $_id;
        $rows = $this->db->select($sql, $arr);

        if (count($rows) == 1) {//存在且只存在一个这样的用户
            return $rows[0];

        }
        return false;//不存在
    }

    /**
     * 离会
     * @param $_id
     * @return bool
     */
    public function leaveMeeting($_id)
    {
        if (($result_arr = $this->getCheckInInfoById($_id)) != false) {
            $checkInType = $result_arr[0]['check_in_type'];
            $user_id = $result_arr[0]['bd_user_user_id'];
            $meeting_id = $result_arr[0]['bd_meeting_meeting_id'];
            $this->user->setId($user_id);
            $this->meeting->setId($meeting_id);
            /**
             * 判断用户是不是会议的主持人
             */
            if ($checkInType == 2) {//是主持人
                /**
                 * 更新 check_out_time,还要更改会议的状态
                 */
                if ($this->updateCheckOut($_id) && $this->updateMeetingStatus(4)) {
                    return true;
                }


            } elseif ($checkInType == 1) {//不是主持人
                /**
                 * 只需要更新 check_out_time
                 */
                if ($this->updateCheckOut($_id)) {
                    return true;
                }
            }
        }


        return false;
    }

    /**
     * 进入会议
     * @param $type 1:与会 2:主持
     * @return bool
     */
    public function enterMeeting($type)
    {
        if ($type == 2) {
            /**
             * 获取user_email对应的user_id
             */
            if (($user_id = $this->getUserIdFromEmail()) != false) {
//                echo 'getUserIdFromEmail';
                $this->user->setId($user_id);
                /**
                 * 根据meeting_id,判断会议的状态（1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束）
                 * 状态必须为1，判断user_id和host_user_id是否一致,主持人会议期间不能退出，否则会议结束
                 */
                if ($this->getMeetingStatusById() == 1) {
//                    echo 'getMeetingStatusById';
                    if (($_id = $this->checkIfExistSameUserAndMeeting(2)) == false) {//不存在
//                        echo 'checkIfExistSameUserAndMeeting';
                        /**
                         * 插入到 user_and_meeting表
                         */
                        if (($user_and_meeting_id = $this->addCheckIn(2)) != false) {
//                            echo 'addCheckIn';
                            /**
                             * 更新 meeting 表的会议状态 为 3
                             */
                            if ($this->updateMeetingStatus(3)) {
//                                echo 'updateMeetingStatus';
                                /**
                                 * 返回
                                 */
                                return $user_and_meeting_id;
                            }


                        }

                    }

                }

            }

        } elseif ($type == 1) {
            /**
             * 获取user_email对应的user_id
             */
            if (($user_id = $this->getUserIdFromEmail()) != false) {
                $this->user->setId($user_id);
                /**
                 * 根据meeting_url 获取 meeting_id，meeting_password,判断会议的状态（1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束）
                 * 状态必须为3，判断user_id和host_user_id是否一致,主持人会议期间不能退出，否则会议结束
                 */
                if (($result_arr = $this->getMeetingInfoByUrl()) != false) {
                    $status = $result_arr['meeting_status'];
                    $password = $result_arr['meeting_password'];
                    $meeting_id = $result_arr['meeting_id'];
                    $host_id = $result_arr['meeting_host_user_id'];
                    /**
                     * 获取主持人的email
                     */
                    if (($host_user_email = $this->getEmailFromUserId($host_id)) != false) {

                        Session::set(SESSION_HOST_EMAIL, $host_user_email, 2592000);//30天过期
                    } else {
                        return false;
                    }


                    /**
                     * 比较 会议状态和密码
                     */
                    $this->meeting->setId($meeting_id);

                    if ($status == 3 && $password == $this->meeting->getPassword()) {
                        if (($_id = $this->checkIfExistSameUserAndMeeting(1)) == false) {//不存在
                            /**
                             * 插入到 user_and_meeting表
                             */
                            if (($user_and_meeting_id = $this->addCheckIn(1)) != false) {
                                /**
                                 * 返回
                                 */
                                return $user_and_meeting_id;
                            }

                        } else {

                            /**
                             * 更新到 user_and_meeting表
                             */
                            if (($user_and_meeting_id = $this->updateCheckIn($_id)) != false) {
                                /**
                                 * 返回
                                 */
                                return $user_and_meeting_id;
                            }
                        }
                    }


                }

            }
        }

        return false;
    }

    /**
     * 获取指定状态的会议的行数
     * @param $status
     * @return int
     */
    public function getMeetingPagesByStatus($status)
    {
        $sql = 'SELECT * FROM bd_meeting WHERE meeting_host_user_id = ? AND
                meeting_status = ? ';
        $arr = array();
        $arr[0] = $this->user->getId();
        $arr[1] = $status;
        $rows = $this->db->select($sql, $arr);

        return count($rows);

    }

    /**
     * 获取我的会议的页数
     * @return bool|float|int
     */
    public function getPages()
    {
        /**
         * 获取email对应的 user_id
         */
        if (($user_id = $this->getUserIdFromEmail()) != false) {
            $this->user->setId($user_id);
            /**
             * 获取指定 user_id 主持的会议, 且状态为 1 （1 ：未开始并且未到期 2：未开始并且过期了 3：正在进行 4：开会结束）
             */
            $numberPage = $this->getMeetingPagesByStatus(1);

            $pageSize = 8;
            $page = ceil($numberPage / $pageSize);
            return $page;


        }
        return false;
    }

    /**
     * 获取指定状态的会议,从 $fromPage 开始的 $size 条数据
     * @param $status
     * @param $fromPage
     * @param $size
     * @return bool
     */
    public function getMeetingByStatusAndPage($status, $fromPage, $size)
    {
//        $sql = 'SELECT meeting_id,meeting_url,meeting_theme,meeting_is_drawable,meeting_is_talkable,
//                meeting_is_add_to_calendar,meeting_password,meeting_start_time,meeting_end_time,event_id,meeting_desc
//                FROM bd_meeting
//                WHERE meeting_host_user_id = ? AND meeting_status = ?
//                ORDER BY meeting_id DESC LIMIT ?,?';
        $sql = 'SELECT meeting_id,meeting_url,meeting_theme,meeting_is_drawable,meeting_is_talkable,
                meeting_is_add_to_calendar,meeting_password,meeting_start_time,meeting_end_time,event_id,meeting_desc
                FROM bd_meeting  
                WHERE meeting_host_user_id = ' . $this->user->getId() . ' AND meeting_status = ' . $status . '
                ORDER BY meeting_id DESC LIMIT ' . $fromPage . ',' . $size;

//        $arr = array();
//        $arr[0] = $this->user->getId();
//        $arr[1] = $status;
//        $arr[2] = $fromPage;
//        $arr[3] = $size;

        $rows = $this->db->select2($sql);
        return $rows;


    }

    /**
     * 获取会议信息
     * @param $post_meeting_page
     * @return bool|int
     */
    public function getMeetingInfo($post_meeting_page)
    {
        /**
         * 获取email对应的 user_id
         */
        if (($user_id = $this->getUserIdFromEmail()) != false) {
            $this->user->setId($user_id);
            //获取分页参数
            $page = $post_meeting_page;
            $pageSize = 8;
            $pageFrom = $page * $pageSize;
            /**
             * 获取指定 user_id 主持的会议, 且状态为 1 ,从 $pageFrom 开始的 $pageSize 条
             */

            $rows = $this->getMeetingByStatusAndPage(1, $pageFrom, $pageSize);

            return $rows;


        }
        return false;
    }


}