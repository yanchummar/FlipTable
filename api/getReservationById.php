<?php 
 
 date_default_timezone_set("Asia/Kolkata");
 //Getting the requested

 $resId = $_GET['resId'];
 
 //Importing database
 require_once('dbConnect.php');
 
 //Creating sql query with where clause to get an specific employee
 $sql = "SELECT * FROM `reservations` WHERE reservationId = '$resId'";
 
 //getting result 
 $r = mysqli_query($con,$sql);
 
 //pushing result to an array 
 $result = array();

 while($row = mysqli_fetch_array($r)){
 array_push($result,array(
 "id"=>$row['reservationId'],
 "spotId"=>$row['spotId'],
 "status"=>$row['status'],
 "spotName"=>$row['spotName'],
 "spotLocation"=>$row['spotLocation'],
 "name"=>$row['name'],
 "email"=>$row['email'],
 "phone"=>$row['phone'],
 "foodieCount"=>$row['foodieCount'],
 "timeSlot"=>$row['timeSlot'],
 "bookingDate"=>$row['bookingDate'],
 "freeBooking"=>$row['freeBooking'],
 "cost"=>$row['cost'],
 "tipAmount"=>$row['tipAmount']
 ));
 }
 
 //displaying in json format 
 echo json_encode(array('time'=>(date("H").(":").date("i").(" ").date("d").("-").date("m").("-").date("Y")),'reservation'=>$result));
 
 mysqli_close($con);