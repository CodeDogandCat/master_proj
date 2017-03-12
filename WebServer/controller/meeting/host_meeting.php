<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);

$data = array("token" => "", "avatar" => "");

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
if (isset($_REQUEST[post_need_feature])) {

    $feature = $_REQUEST[post_need_feature];

    switch ($feature) {

        case 'get':
            /**
             * 获取我的会议，分页加载， 每页5条
             */
            
            break;


        case 'add':
            /**
             * 检验参数
             */
            if (isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_meeting_theme]) &&
                isset($_REQUEST[post_meeting_is_drawable]) &&
                isset($_REQUEST[post_meeting_is_talkable]) &&
                isset($_REQUEST[post_meeting_is_add_to_calendar]) &&
                isset($_REQUEST[post_meeting_start_time]) &&
                isset($_REQUEST[post_meeting_end_time]) &&
                isset($_REQUEST[post_meeting_password])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(
                    $_REQUEST[post_meeting_theme],
                    null,//主持人id
                    $_REQUEST[post_meeting_is_drawable],
                    $_REQUEST[post_meeting_is_talkable],
                    $_REQUEST[post_meeting_is_add_to_calendar],
                    $_REQUEST[post_meeting_start_time],
                    $_REQUEST[post_meeting_end_time],
                    $_REQUEST[post_meeting_password],
                    1//1 ：未开始并且未到期
                );
                $meetingOp = new MeetingOp($user, $meeting);

                if (($result_arr = $meetingOp->addMeeting()) != false) {

                    if (isset($_REQUEST[post_is_enter_meeting])) {
                        if ($_REQUEST[post_is_enter_meeting] == true) {
                            /**
                             * 需要立即进入会议
                             */
                            $meeting_id = $result_arr[1];
                            $meeting->setId($meeting_id);
                            $meetingOp = new MeetingOp($user, $meeting);
                            if (($user_and_meeting_id = $meetingOp->enterMeeting(2)) == false)//主持会议 type=2
                            {
                                printResult(HOST_MEETING_ERROR, '召开会议失败', $data);
                            } else {
                                Session::set(SESSION_USER_AND_MEETING_ID, $user_and_meeting_id, 2592000);//30天过期
                                printResult(SUCCESS, '召开会议成功', $data);
                            }
                        }
                    } else {
                        printResult(SUCCESS, '安排会议成功', $data);
                    }


                } else {
                    printResult(ARRANGE_MEETING_ERROR, '安排会议失败', $data);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
            }
            break;


        case
        'update':
            /**
             * 检验参数，插入到meeting 表
             */
            if (isset($_REQUEST[post_meeting_id]) &&
                isset($_REQUEST[post_user_email]) &&
                isset($_REQUEST[post_meeting_theme]) &&
                isset($_REQUEST[post_meeting_is_drawable]) &&
                isset($_REQUEST[post_meeting_is_talkable]) &&
                isset($_REQUEST[post_meeting_is_add_to_calendar]) &&
                isset($_REQUEST[post_meeting_start_time]) &&
                isset($_REQUEST[post_meeting_end_time]) &&
                isset($_REQUEST[post_meeting_password])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(
                    $_REQUEST[post_meeting_theme],
                    null,//主持人ID
                    $_REQUEST[post_meeting_is_drawable],
                    $_REQUEST[post_meeting_is_talkable],
                    $_REQUEST[post_meeting_is_add_to_calendar],
                    $_REQUEST[post_meeting_start_time],
                    $_REQUEST[post_meeting_end_time],
                    $_REQUEST[post_meeting_password],
                    1//1 ：未开始并且未到期
                );
                $meeting->setId($_REQUEST[post_meeting_id]);
                $meetingOp = init($user, $meeting);

                if (($result_arr = $meetingOp->updateMeeting()) != false) {

                    printResult(SUCCESS, '更改会议安排成功', $data);

                } else {
                    printResult(ARRANGE_MEETING_ERROR, '更改会议安排失败', $data);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
            }
            break;


        case 'delete':
            if (isset($_REQUEST[post_meeting_id]) &&
                isset($_REQUEST[post_user_email])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meeting->setId($_REQUEST[post_meeting_id]);
                $meetingOp = new MeetingOp($user, $meeting);
                if (($result_arr = $meetingOp->deleteMeeting()) != false) {

                    printResult(SUCCESS, '删除会议成功', $data);

                } else {
                    printResult(DELETE_MEETING_ERROR, '删除失败', $data);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
            }
            break;


        default:
            printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
            break;


    }
} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
}

/**
 * 接受参数构造 user和 meeting对象
 * @param $user
 * @param $meeting
 * @return bool|MeetingOp
 */
function init($user, $meeting)
{
    /**
     * 检验参数，插入到meeting 表
     */
    if (isset($_REQUEST[post_user_email]) &&
        isset($_REQUEST[post_meeting_theme]) &&
        isset($_REQUEST[post_meeting_host_user_id]) &&
        isset($_REQUEST[post_meeting_is_drawable]) &&
        isset($_REQUEST[post_meeting_is_talkable]) &&
        isset($_REQUEST[post_meeting_is_add_to_calendar]) &&
        isset($_REQUEST[post_meeting_start_time]) &&
        isset($_REQUEST[post_meeting_end_time]) &&
        isset($_REQUEST[post_meeting_password])
    ) {
        $user = new User($_REQUEST[post_user_email]);
        $meeting = new Meeting(
            $_REQUEST[post_meeting_theme],
            $_REQUEST[post_meeting_host_user_id],
            $_REQUEST[post_meeting_is_drawable],
            $_REQUEST[post_meeting_is_talkable],
            $_REQUEST[post_meeting_is_add_to_calendar],
            $_REQUEST[post_meeting_start_time],
            $_REQUEST[post_meeting_end_time],
            $_REQUEST[post_meeting_password],
            1//1 ：未开始并且未到期
        );
        return $meetingOp = new MeetingOp($user, $meeting);

    } else {
        return false;
    }
}

