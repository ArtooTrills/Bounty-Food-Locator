<?php
    require_once 'Firebase.php';
    $ob = new Firebase();
    $id=$_POST['id'];
    $msg=$_POST['msg'];
    $user = $ob->sendPushNotification($id,$msg);
?>

