<?php 
 if($_SERVER['REQUEST_METHOD']=='POST'){
 
	 //Getting values
 	 $id = $_POST['id'];
 	 $status = $_POST['status'];
	 
	 //Creating an sql query
	 $sql = "UPDATE `reservations` SET status = '$status' WHERE reservationId = '$id'";
	 
	 //Importing our db connection script
	 require_once('../dbConnect.php');
	 
	 //Executing query to database
	 if(mysqli_query($con,$sql)){
	 	echo json_encode(mysqli_insert_id($con));
	 }else{
	 	echo json_encode('Error : '.mysqli_error($con));
	 }
	 

	 //Closing the database 
	 mysqli_close($con);
 }