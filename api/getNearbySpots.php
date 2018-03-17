<?php 
 
 date_default_timezone_set("Asia/Kolkata");
 //Getting the requested

 $lat = $_GET['lat'];
 $lng = $_GET['lng'];
 $radius = 10;
 
 //Importing database
 require_once('dbConnect.php');
 
 //Creating sql query with where clause to get an specific employee
 $sql="SELECT *, (3959 * acos(cos(radians('".$lat."')) * cos(radians(lat)) * cos( radians(lng) - radians('".$lng."')) + sin(radians('".$lat."')) * sin(radians(lat)))) AS distance FROM `spots` HAVING distance < 10 ORDER BY distance";

 //getting result 
 $r = mysqli_query($con,$sql);

 //pushing result to an array 
 $result = array();

 while($row = mysqli_fetch_array($r)){ 
 	// Fetch Working Hours
 	$spotId = $row['spotId'];
 	$dayInText = date("D");
 	$hoursQuery = "SELECT `$dayInText` FROM `working_hours` WHERE spotId = '$spotId'";
 	$hoursR = mysqli_query($con,$hoursQuery);
 	$hours = '';
 	while($hoursRow = mysqli_fetch_array($hoursR)){
 		$hours = $hoursRow[0];
 		$hours = trim($hours);
 		$hoursList = explode("-", $hours);
 	}

	array_push($result,array(
		"city"=>$row['city'],
		"spotId"=>$row['spotId'],
		"trending"=>$row['trending'],
		"name"=>$row['name'],
		"image"=>$row['image'],
		"rating"=>$row['rating'],
	 	"lat"=>$row['lat'],
		"lng"=>$row['lng'],
		"location"=>$row['location'],
		"cuisines"=>$row['cuisines'],
		"priceLevel"=>$row['priceLevel'],
		"cost"=>$row['cost'],
		"openStatus"=>$row['openStatus'],
		"openingTime"=>$hoursList[0],
		"closingTime"=>$hoursList[1],
		"phone"=>$row['phone'],
		"address"=>$row['address'],
		"imageList"=>explode(",", $row['imageList']),
		"amenities"=>explode(",", $row['amenities']),
		"verified"=>$row['verified'],
		"distance"=>$row['distance']
	 ));
 }
 
 //displaying in json format 
 echo json_encode(array('time'=>(date("H").(":").date("i").(" ").date("d").("-").date("m").("-").date("Y")),'spots'=>$result));
 
 mysqli_close($con);