<?php
// Logging in
require_once('../../dbConnect.php');

// Check connection
if (mysqli_connect_errno())
{
	die(json_encode(['error'=>1,'message'=>'Database connection error']));
}
if(!isset($_POST['mail'],$_POST['password']))
{
	die(json_encode(['error'=>2,'message'=>'Invalid Data']));
}
$mail = mysqli_real_escape_string($con, $_POST['mail']);
$password = mysqli_real_escape_string($con, $_POST['password']);

$data = mysqli_query($con,"SELECT `id` FROM `businesses` WHERE `mail` = '$mail' AND `password` = '$password' ");

if(mysqli_num_rows($data) == 0 ){
	die(json_encode(['error'=>3,'message'=>'Invalid Credentials']));
}

$row = mysqli_fetch_array($data,MYSQLI_ASSOC);

die(json_encode(['error'=>0,'message'=>'Login Success','id'=>$row['id']]));

?>