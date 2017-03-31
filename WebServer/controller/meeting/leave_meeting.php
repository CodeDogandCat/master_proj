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

    /**
     * 1.拦截token
     */
    require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/tokenInterceptor.php';


    /**
     * 2.离会
     */
    if (isset($_REQUEST[post_user_email])) {
        if (($user_and_meeting_id = Session::get(SESSION_USER_AND_MEETING_ID)) != false) {
//            echo "入会id" . $user_and_meeting_id;
            $user = new User($_REQUEST[post_user_email]);
            $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
            $meetingOp = new MeetingOp($user, $meeting);

            if ($meetingOp->leaveMeeting($user_and_meeting_id)) {

                printResult(SUCCESS, '成功离开会议', -1);
            } else {
                printResult(FAILURE, '离开会议时出错', -1);
            }


        }
        printResult(FAILURE, '内部错误', -1);
    }
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);


} catch
(Exception $exception) {
    printResult(FAILURE, $exception->getMessage(), -1);

}