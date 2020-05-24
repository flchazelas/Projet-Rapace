<?php
    $file_path = "Data";
    if(!file_exists($file_path)){ mkdir($file_path,0744); }
    $file_path .= "/";
    
    $path = $_FILES['uploaded_file']['name'];
    $ext = pathinfo($path, PATHINFO_EXTENSION);
    
    if($ext == "jpeg"){
        $file_path = $file_path . "images";if(!file_exists($file_path)){ mkdir($file_path,0744); }
        $file_path .= "/";
    }else if($ext == "mjpeg"){
        $file_path = $file_path . "videos";if(!file_exists($file_path)){ mkdir($file_path,0744); }
        $file_path .= "/";
    }
     
    $file_path = $file_path . basename( $_FILES['uploaded_file']['name']);
    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
        echo "success";
    } else{
        echo "fail";
    }
 ?>
