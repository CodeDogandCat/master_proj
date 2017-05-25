<?php
header("Content-type: text/html; charset=utf-8");
require_once $_SERVER['DOCUMENT_ROOT'] . '/controller/conn/settings.php';
require_once $_SERVER['DOCUMENT_ROOT'] . '/vendor/phpmailer/phpmailer/PHPMailerAutoload.php';

class SmtpUtil
{


    public static function sendMail($to, $subject, $body)
    {
        $mail = new PHPMailer;

//$mail->SMTPDebug = 3;                                     // Enable verbose debug output

        $mail->isSMTP();                                        // Set mailer to use SMTP
        $mail->Host = SMTP_HOST;                            // Specify main and backup SMTP servers
        $mail->SMTPAuth = true;                               // Enable SMTP authentication
        $mail->Username = SMTP_USERNAME;                    // SMTP username
        $mail->Password = SMTP_PASSWORD;                           // SMTP password
        $mail->SMTPSecure = 'tls';                            // Enable TLS encryption, `ssl` also accepted
        $mail->Port = SMTP_PORT;                                    // TCP port to connect to

        $mail->setFrom(SMTP_USERNAME, SYS_NAME);
//        $mail->addAddress('joe@example.net', 'Joe User');     // Add a recipient
        $mail->addAddress($to);                                 // Name is optional

        $mail->addAttachment($_SERVER['DOCUMENT_ROOT'] . '/assets/ic_logo.png', 'logo.png');    // Optional name
        $mail->isHTML(true);                                  // Set email format to HTML
        $mail->CharSet = "UTF-8";                             //chaset support zh-cn
        $mail->Subject = $subject;
        $mail->Body = $body;
        $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';

        if (!$mail->send()) {
            return false;
        } else {
            return true;
        }
    }

    public static function sendFileToEmail($to, $subject, $body, $path)
    {
        $mail = new PHPMailer;

//$mail->SMTPDebug = 3;                                     // Enable verbose debug output

        $mail->isSMTP();                                        // Set mailer to use SMTP
        $mail->Host = SMTP_HOST;                            // Specify main and backup SMTP servers
        $mail->SMTPAuth = true;                               // Enable SMTP authentication
        $mail->Username = SMTP_USERNAME;                    // SMTP username
        $mail->Password = SMTP_PASSWORD;                           // SMTP password
        $mail->SMTPSecure = 'tls';                            // Enable TLS encryption, `ssl` also accepted
        $mail->Port = SMTP_PORT;                                    // TCP port to connect to

        $mail->setFrom(SMTP_USERNAME, SYS_NAME);
//        $mail->addAddress('joe@example.net', 'Joe User');     // Add a recipient
        $mail->addAddress($to);                                 // Name is optional

        $mail->addAttachment($_SERVER['DOCUMENT_ROOT'] . '/assets/ic_logo.png', 'logo.png');    // Optional name
        $mail->addAttachment($path, 'error.log');    // Optional name
        $mail->isHTML(true);                                  // Set email format to HTML
        $mail->CharSet = "UTF-8";                             //chaset support zh-cn
        $mail->Subject = $subject;
        $mail->Body = $body;
        $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';

        if (!$mail->send()) {
            return false;
        } else {
            return true;
        }
    }
}