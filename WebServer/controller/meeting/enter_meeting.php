<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);

$data = array("token" => "", "avatar" => "");
try {
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/User.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/model/Meeting.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/Session.php';
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/meeting/MeetingOp.php';

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
                isset($_REQUEST[post_meeting_id])
            ) {
                $user = new User($_REQUEST[post_user_email]);

                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meeting->setId($_REQUEST[post_meeting_id]);
                $meetingOp = new MeetingOp($user, $meeting);

                if (($user_and_meeting_id = $meetingOp->enterMeeting($type)) != false) {

                    Session::set(SESSION_USER_AND_MEETING_ID, $user_and_meeting_id, 2592000);//30天过期
                    printResult(SUCCESS, '主持人进会成功', $data);

                } else {
                    printResult(HOST_MEETING_ERROR, '主持人进会失败', $data);
                }
            }
        } elseif ($type == 1) {//普通加会

            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_meeting_url]) &&
                isset($_REQUEST[post_meeting_password])
            ) {
                $user = new User($_REQUEST[post_user_email]);

                $meeting = new Meeting(null, null, null, null, null, null, null, $_REQUEST[post_meeting_password], null);
                $meeting->setUrl($_REQUEST[post_meeting_url]);
                $meetingOp = new MeetingOp($user, $meeting);

                if (($user_and_meeting_id = $meetingOp->enterMeeting($type)) != false) {

                    Session::set(SESSION_USER_AND_MEETING_ID, $user_and_meeting_id, 2592000);//30天过期
                    printResult(SUCCESS, '与会者加会成功', $data);

                } else {
                    printResult(ADD_MEETING_ERROR, '与会者加会失败', $data);
                }
            }
        }

    }
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);


} catch
(Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), $data);

}