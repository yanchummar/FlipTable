<?php 
 if($_SERVER['REQUEST_METHOD']=='POST'){

	 //Importing our db connection script
	 require_once('dbConnect.php');

 	do{
 		$length = 30;
	    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
	    $charactersLength = strlen($characters);
	    $hashCode = '';
	    for ($i = 0; $i < $length; $i++) {
	        $hashCode .= $characters[rand(0, $charactersLength - 1)];
	    }
	    $data = mysqli_query($con,"SELECT * FROM users WHERE `hash` = '$hashCode' ");
 	}
 	while (mysqli_num_rows($data) > 0);

	 //Creating an sql query
	 $sql = "INSERT INTO users (`userId`,`hash`) VALUES(0,'$hashCode')";
	 
	 
	 //Executing query to database
	 if(mysqli_query($con,$sql)){
	 	echo json_encode($hashCode);
	 }else{
	 	echo json_encode('Error : '.mysqli_error($con));
	 }
	 

	 //Closing the database 
	 mysqli_close($con);
 }