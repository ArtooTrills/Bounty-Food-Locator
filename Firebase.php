<?php
class Firebase {
 
    public function sendPushNotification($to,$msg) {
          
	
		$fields = array(
				'to' => $to,
				'data' => array( "message" => $msg ),
        );
        // Set POST variables
		define("GOOGLE_API_KEY", "AAAAlFNrAlw:APA91bH6GpcEf4fwkqTVjT2uWA4-wx7ZhV42-9dNqa1S9bC3lQCQFBsV4ikqF7IKBalw9YUeKJOYP7yu8F_aRcZJQPOCSAHCuMEyxJbWlPwbxg3mizYrn5k5VANAG-aNlD2t4XHOguMy");   
        $url = 'https://fcm.googleapis.com/fcm/send';
        $headers = array(
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        // Open connection
        $ch = curl_init();
		
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        // Execute post
        $result = curl_exec($ch);
		echo $result;
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
 
        // Close connection
        curl_close($ch);
 
        return $result;
    }
}
?>