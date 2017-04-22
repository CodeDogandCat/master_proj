<?php
require_once $_SERVER['DOCUMENT_ROOT'] . '/vendor/load_jpush.php';
use \JPush\Client;
use \JPush\Exceptions\APIConnectionException;
use \JPush\Exceptions\APIRequestException;

class Jpush
{
    public static function pushMsg($alias, $content, $data)
    {
        $app_key = "249af21de66f79d249c2d315";
        $master_secret = "183bdd10b5f970e8b23633b3";
        $client = new Client($app_key, $master_secret);
        try {
            $result = $client->push()
                ->setPlatform('android')
                ->addAlias($alias)
                ->message($content, $data)
                ->options(array(
                    // sendno: 表示推送序号，纯粹用来作为 API 调用标识，
                    // API 返回时被原样返回，以方便 API 调用方匹配请求与返回
                    // 这里设置为 100 仅作为示例

                    // 'sendno' => 100,

                    // time_to_live: 表示离线消息保留时长(秒)，
                    // 推送当前用户不在线时，为该用户保留多长时间的离线消息，以便其上线时再次推送。
                    // 默认 86400 （1 天），最长 10 天。设置为 0 表示不保留离线消息，只有推送当前在线的用户可以收到
                    // 这里设置为 1 仅作为示例

                    'time_to_live' => 10,

                    // apns_production: 表示APNs是否生产环境，
                    // True 表示推送生产环境，False 表示要推送开发环境；如果不指定则默认为推送生产环境

                    'apns_production' => True,

                    // big_push_duration: 表示定速推送时长(分钟)，又名缓慢推送，把原本尽可能快的推送速度，降低下来，
                    // 给定的 n 分钟内，均匀地向这次推送的目标用户推送。最大值为1400.未设置则不是定速推送
                    // 这里设置为 1 仅作为示例

                    // 'big_push_duration' => 1
                ))
                ->send();
            if ($result != null) {
                if ($result['http_code'] == 200) {
                    return true;
                }
            }
            return false;
        } catch (APIConnectionException $e) {
            // try something here
            return false;
        } catch (APIRequestException $e) {
            // try something here
            return false;
        }
        //            ->message($content, [
//                'title' => 'Hello',
//                'content_type' => 'text',
//                'extras' => [
//                    'jjjj' => 'llllllllll'
//                ]
//            ])
//            ->androidNotification('请及时查看', array(
//                'title' => '加会邀请',
//                'extras' => array(
//                    'host' => 'gaven',
//                    'topic' => 'about git',
//                    'meetingurl' => 'http://###/./',
//                    'size' => '5'
//                )
//
//            ))
    }

    public static function pushSingleScheduleMsg($alias, $content, $data, $time)
    {
        $app_key = "249af21de66f79d249c2d315";
        $master_secret = "183bdd10b5f970e8b23633b3";
        $client = new Client($app_key, $master_secret);
        try {
            $result = $client->push()
                ->setPlatform('android')
                ->addAlias($alias)
                ->message($content, $data)
                ->options(array(

                    // time_to_live: 表示离线消息保留时长(秒)，
                    // 推送当前用户不在线时，为该用户保留多长时间的离线消息，以便其上线时再次推送。
                    // 默认 86400 （1 天），最长 10 天。设置为 0 表示不保留离线消息，只有推送当前在线的用户可以收到
                    // 这里设置为 1 仅作为示例

                    'time_to_live' => 0,

                    // apns_production: 表示APNs是否生产环境，
                    // True 表示推送生产环境，False 表示要推送开发环境；如果不指定则默认为推送生产环境

                    'apns_production' => True,

                ))
                ->build();
            $response = $client->schedule()->createSingleSchedule("会议前5分钟提醒用户", $result, array("time" => $time));
            if ($response != null) {
                if ($response['http_code'] == 200) {
                    return true;
                }
            }
            return false;
        } catch (APIConnectionException $e) {
            // try something here
            return false;
        } catch (APIRequestException $e) {
            // try something here
            return false;
        }
    }


}
























