<?php
session_start();
header("Content-type: text/html; charset=utf-8");
//error_reporting(0);


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
if (isset($_REQUEST[post_need_feature])) {

    $feature = $_REQUEST[post_need_feature];

    switch ($feature) {
        case 'getPages':
            /**
             * 获取页数
             */
            if (isset($_REQUEST[post_user_email])) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meetingOp = new MeetingOp($user, $meeting);
                if (($result = $meetingOp->getPages()) != false) {

                    printResult(SUCCESS, '获取页数成功', $result);

                } else {
                    printResult(GET_PAGES_ERROR, '获取页数失败', -1);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }


            break;

        case 'get':
            $data = array(
                array("meeting_id" => -1, "meeting_url" => -1,
                    "meeting_theme" => "", "meeting_is_drawable" => -1,
                    "meeting_is_talkable" => -1, "meeting_is_add_to_calendar" => -1,
                    "meeting_password" => "", "meeting_start_time" => -1,
                    "meeting_end_time" => -1, "event_id" => -1, "meeting_desc" => "")

            );
            /**
             * 获取我的会议，分页加载， 每页8条
             */
            if (isset($_REQUEST[post_user_email]) && isset($_REQUEST[post_meeting_page])) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meetingOp = new MeetingOp($user, $meeting);
                if (($result_arr = $meetingOp->getMeetingInfo($_REQUEST[post_meeting_page])) != false) {

                    $data = $result_arr;
                    printResult(SUCCESS, '获取会议列表成功', $data);

                } else {
                    printResult(GET_MEETING_LIST_ERROR, '获取会议列表失败', $data);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', $data);
            }
            break;


        case 'add':
            $data = array(
                "meeting_id" => -1, "meeting_url" => -1,
                "meeting_theme" => "", "meeting_is_drawable" => -1,
                "meeting_is_talkable" => -1, "meeting_is_add_to_calendar" => -1,
                "meeting_password" => "", "meeting_start_time" => -1,
                "meeting_end_time" => -1, "event_id" => -1, "meeting_desc" => ""

            );
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
                isset($_REQUEST[post_meeting_password]) &&
                isset($_REQUEST[post_meeting_event_id]) &&
                isset($_REQUEST[post_meeting_desc])
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
                    EncryptUtil::hash($_REQUEST[post_meeting_password], "lilimiao"),
                    1//1 ：未开始并且未到期
                );
                $meeting->setEventId($_REQUEST[post_meeting_event_id]);
                $meeting->setMeetingDesc($_REQUEST[post_meeting_desc]);
                $meetingOp = new MeetingOp($user, $meeting);

                if (($result_arr = $meetingOp->addMeeting()) != false) {
                    $data['meeting_id'] = $result_arr[0];
                    $data['meeting_url'] = $result_arr[1];

                    if (isset($_REQUEST[post_is_enter_meeting])) {
                        if ($_REQUEST[post_is_enter_meeting] == true) {
                            /**
                             * 需要立即进入会议
                             */
                            $meeting_id = $result_arr[0];
                            $meeting->setId($meeting_id);
                            $meetingOp = new MeetingOp($user, $meeting);
                            if (($result_arr2 = $meetingOp->enterMeeting(2)) == false)//主持会议 type=2
                            {
                                printResult(HOST_MEETING_ERROR, '召开会议失败', $data);
                            } else {
                                $user_and_meeting_id = $result_arr2['user_and_meeting_id'];
//                                echo "放入session";
                                //放入session  (进会id ,会议 id ,会议url ,用户 email)
                                Session::set(SESSION_USER_AND_MEETING_ID, $user_and_meeting_id, 2592000);//30天过期
                                Session::set(SESSION_MEETING_ID, $data['meeting_id'], 2592000);//30天过期
                                Session::set(SESSION_MEETING_URL, $data['meeting_url'], 2592000);//30天过期
                                Session::set(SESSION_EMAIL, $_REQUEST[post_user_email], 2592000);//30天过期

//                                echo "放完session";
//                                echo Session::get(SESSION_USER_AND_MEETING_ID) . ' ' .
//                                    Session::get(SESSION_MEETING_ID) . ' ' .
//                                    Session::get(SESSION_MEETING_URL) . ' ' .
//                                    Session::get(SESSION_EMAIL) . ' ';
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


        case  'edit':
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
                isset($_REQUEST[post_meeting_password]) &&
                isset($_REQUEST[post_meeting_event_id]) &&
                isset($_REQUEST[post_meeting_desc])
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
                $meeting->setEventId($_REQUEST[post_meeting_event_id]);
                $meeting->setMeetingDesc($_REQUEST[post_meeting_desc]);
                $meetingOp = new MeetingOp($user, $meeting);

                if (($result_arr = $meetingOp->updateMeeting()) != false) {
                    printResult(SUCCESS, '更改会议安排成功', -1);

                } else {
                    printResult(ARRANGE_MEETING_ERROR, '更改会议安排失败', -1);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
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

                    printResult(SUCCESS, '删除会议成功', -1);

                } else {
                    printResult(DELETE_MEETING_ERROR, '删除失败', -1);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }
            break;

        case 'lock':
            if (isset($_REQUEST[post_meeting_id]) &&
                isset($_REQUEST[post_user_email])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meeting->setId($_REQUEST[post_meeting_id]);
                $meetingOp = new MeetingOp($user, $meeting);
                if (($result_arr = $meetingOp->lockMeeting()) != false) {

                    printResult(SUCCESS, '锁定会议成功', -1);

                } else {
                    printResult(LOCK_MEETING_ERROR, '锁定会议失败', -1);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }
            break;

        case 'unlock':
            if (isset($_REQUEST[post_meeting_id]) &&
                isset($_REQUEST[post_user_email])
            ) {
                $user = new User($_REQUEST[post_user_email]);
                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
                $meeting->setId($_REQUEST[post_meeting_id]);
                $meetingOp = new MeetingOp($user, $meeting);
                if (($result_arr = $meetingOp->unlockMeeting()) != false) {

                    printResult(SUCCESS, '解锁会议成功', -1);

                } else {
                    printResult(UNLOCK_MEETING_ERROR, '解锁会议失败', -1);
                }
            } else {
                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            }
            break;
//        case 'get_members':
//            if (isset($_REQUEST[post_meeting_id]) &&
//                isset($_REQUEST[post_user_email])
//            ) {
//                $user = new User($_REQUEST[post_user_email]);
//                $meeting = new Meeting(null, null, null, null, null, null, null, null, null);
//                $meeting->setId($_REQUEST[post_meeting_id]);
//                $meetingOp = new MeetingOp($user, $meeting);
//                if (($members = $meetingOp->unlockMeeting()) != false) {
//
//                    printResult(SUCCESS, '获取参与者成功', $result_arr);
//
//                } else {
//                    printResult(GET_MEETING_MEMBERS_ERROR, '获取参与者失败', -1);
//                }
//            } else {
//                printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
//            }
//            break;


        default:
            printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
            break;


    }
} else {
    printResult(NO_PARAMS_RECEIVE, '服务器未收到参数', -1);
}

