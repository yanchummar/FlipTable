<form action="md5.php" method="post"><input type="password" name="pwd"><input type="submit"></form><br/>
<?php echo md5($_POST['pwd']) ?>