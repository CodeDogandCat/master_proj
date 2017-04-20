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
        $client = new Client($app_key, $master_secret, $_SERVER['DOCUMENT_ROOT'] . '/controller/friend/jpush.log');

        try {
            $result = $client->push()
                ->setPlatform('android')
                ->addAlias($alias)
                ->message($content, $data)
                ->send();
            if ($result != null) {
                if ($result['http_code'] == 200) {
                    return true;
                }
            }
            return false;
        } catch (APIConnectionException $e) {
            // try something here
            print $e;
            return false;
        } catch (APIRequestException $e) {
            // try something here
            print $e;
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
}
























