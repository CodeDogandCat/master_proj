<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);

try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/Meeting.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/meeting/MeetingOp.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/util/EncryptUtil.php';

    /**
     * 1.拦截token
     */
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';
    /**
     * 2.判断功能参数
     */
    if (isset($_REQUEST[post_meeting_check_in_type])) {
        $type = $_REQUEST[post_meeting_check_in_type];
        if ($type == 2) {//主持

            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_meeting_id]) &&
                isset($_REQUEST[post_meeting_url])
            ) {
                $user = new User($_REQUEST[post_user_email]);

                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meeting->setId($_REQUEST[post_meeting_id]);
                $meeting->setUrl($_REQUEST[post_meeting_url]);
                $meetingOp = new MeetingOp($user, $meeting);

                if (($result_arr = $meetingOp->enterMeeting($type)) != false) {
                    if ($result_arr['user_and_meeting_id'] == -1) {
                        printResult(HOST_MEETING_ERROR, $result_arr['result_desc'], -1);
                    } else {
                        $user_and_meeting_id = $result_arr['user_and_meeting_id'];
                        //放入session  (进会id ,会议 id ,会议url ,用户 email)
                        Session::set(SESSION_USER_AND_MEETING_ID, $user_and_meeting_id, 2592000);//30天过期
                        Session::set(SESSION_MEETING_ID, $_REQUEST[post_meeting_id], 2592000);//30天过期
                        Session::set(SESSION_MEETING_URL, $_REQUEST[post_meeting_url], 2592000);//30天过期
                        Session::set(SESSION_EMAIL, $_REQUEST[post_user_email], 2592000);//30天过期

                        printResult(SUCCESS, '主持人进会成功', -1);
                    }


                } else {
                    printResult(HOST_MEETING_ERROR, '主持人进会失败', -1);
                }
            }
        } elseif ($type == 1) {//普通加会

            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_meeting_url]) &&
                isset($_REQUEST[post_meeting_password])
            ) {
                $user = new User($_REQUEST[post_user_email]);

                $meeting = new Meeting(null, null, null, null, null, null, null, EncryptUtil::hash($_REQUEST[post_meeting_password], "lilimiao"), null);
                $meeting->setUrl($_REQUEST[post_meeting_url]);
                $meetingOp = new MeetingOp($user, $meeting);

                if (($result_arr = $meetingOp->enterMeeting($type)) != false) {
                    if ($result_arr['user_and_meeting_id'] == -1) {

                        printResult(ADD_MEETING_ERROR, $result_arr['result_desc'], -1);
                    } else {
                        $user_and_meeting_id = $result_arr['user_and_meeting_id'];
                        //放入session  (进会id  ,会议url ,用户 email)
                        Session::set(SESSION_USER_AND_MEETING_ID, $user_and_meeting_id, 2592000);//30天过期
                        Session::set(SESSION_MEETING_URL, $_REQUEST[post_meeting_url], 2592000);//30天过期
                        Session::set(SESSION_EMAIL, $_REQUEST[post_user_email], 2592000);//30天过期
                        if (($host_email = Session::get(SESSION_HOST_EMAIL)) != false) {
                            //返回 主持人邮箱
                            printResult(SUCCESS, $host_email, $result_arr["meeting_is_drawable"] * 10 + $result_arr["meeting_is_talkable"]);
                        } else {
                            printResult(ADD_MEETING_ERROR, '加会失败', -1);
                        }
                    }


                } else {
                    printResult(ADD_MEETING_ERROR, '加会失败', -1);
                }
            }
        }

    }
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);


} catch
(Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}