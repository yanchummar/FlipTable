<?php 
 if($_SERVER['REQUEST_METHOD']=='POST'){
 
	 //Getting values
 	 $hash = $_POST['hash'];
	 $spotId = $_POST['spotId'];
	 $spotName = $_POST['spotName'];
	 $spotLocation = $_POST['spotLocation'];
	 $name = $_POST['name'];
	 $email = $_POST['email'];
	 $phone = $_POST['phone'];
	 $foodieCount = $_POST['foodieCount'];
	 $timeSlot = $_POST['timeSlot'];
	 $bookingDate = $_POST['bookingDate'];
	 $freeBooking = $_POST['freeBooking'];
	 $cost = $_POST['cost'];
	 $tipAmount = $_POST['tipAmount'];
	 
	 //Creating an sql query
	 $status = 'Pending Confirmation';
	 if ($freeBooking == 'false') {
	 	$status = 'Reservation Confirmed';
	 }

	 $sql = "INSERT INTO reservations(`hash`,`spotId`,`status`,`spotName`,`spotLocation`,`name`,`email`,`phone`,`foodieCount`,`timeSlot`,`bookingDate`,`freeBooking`,`cost`,`tipAmount`) VALUES('$hash','$spotId','$status','$spotName','$spotLocation','$name','$email','$phone','$foodieCount','$timeSlot','$bookingDate','$freeBooking','$cost','$tipAmount')";
	 
	 //Importing our db connection script
	 require_once('dbConnect.php');
	 
	 //Executing query to database
	 if(mysqli_query($con,$sql)){
	 	echo json_encode(mysqli_insert_id($con));
	 }else{
	 	echo json_encode('Error : '.mysqli_error($con));
	 }
	 

	 //Closing the database 
	 mysqli_close($con);
 }