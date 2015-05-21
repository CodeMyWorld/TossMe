<?php
define("TOPCOMMAND", "top -n 1 -b");

if($_GET["uid"] == ""){
	$users = array();
	$command = "top -n 1 -b";
	exec($command,$stdio_stream);
	$stdio_stream = array_slice($stdio_stream,8);
	for($i = 0; $i < cout($stdio_stream); $i++){
		$line = preg_replace("/^(\s)*\d + \s/", "", $stdio_stream[$i]);
		$pos = strpos($line," ");
		$user = substr($line,0,$pos);
		if(!in_array($user,$users)){
			array_push($users,$user);
		}
	}

	$echoline = "";
	foreach($users as $u){
		$echoline .= $u.",";
	}
	$echolines = rtrim($echoline,",");
	echo $echolines;
}
else{
	$uid = $_GET["uid"];
	$command = TOPCOMMAND." -u ".$uid;
	exec($command,$stdio_stream);
	$stdio_stream = array_slice($stdio_stream,7);
	$lines="";
	for($i=0; $i < count($stdio_stream); $i++){
		$lines .= $stdio_stream[$i]."\n";
	}
	echo $lines;
}
?>