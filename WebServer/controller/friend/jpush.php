<?php
require_once "autoload.php";
use \JPush\Client;
use \JPush\Exceptions\APIConnectionException;
use \JPush\Exceptions\APIRequestException;

$app_key="6335d4b2625c42a6d7638709";
$master_secret="f7351d64804d90e7616ca8a9";

$client=new Client($app_key,$master_secret);
try {
    $result= $client->push()
        ->setPlatform('all')
        ->setAudience('all')
//        ->message('233333', [
//            'title' => 'Hello',
//            'content_type' => 'text',
//            'extras' => [
//                'jjjj' => 'llllllllll'
//            ]
//        ])
        ->androidNotification('请及时查看', array(
            'title' => '加会邀请',
            'extras' =>  array(
                'host'=>'gaven',
                'topic'=>'about git',
                'meetingurl' => 'http://###/./',
                'size'=>'5'
            )

        ))
        ->send();
} catch (APIConnectionException $e) {
    // try something here
    print $e;
} catch (APIRequestException $e) {
    // try something here
    print $e;
}



