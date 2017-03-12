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
     * 2.离会
     */
    if (isset($_REQUEST[post_user_email])) {
        if (($user_and_meeting_id = Session::get(SESSION_USER_AND_MEETING_ID)) != false) {
            $user = new User($_REQUEST[post_user_email]);
            $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
            $meetingOp = new MeetingOp($user, $meeting);

            if ($meetingOp->leaveMeeting($user_and_meeting_id)) {

                printResult(SUCCESS, '成功离开会议', $data);
            } else {
                printResult(SUCCESS, '离开会议时出错', $data);
            }


        }
        printResult(FAILURE, '内部错误', $data);
    }
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);


} catch
(Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), $data);

}