 <?php
	include "fonctions.php";
	include "CRUD_OPERATIONS_NAMES.php";
	if(isset($_REQUEST["operation"])){
        if($_REQUEST["operation"] == $OP_GETBYID){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id = $data->{'id'};
                print($OP_GETBYID."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM local WHERE id = $id");
                $req->execute();
                
                //si local trouvée, on envoie le résultat
                if($row = $req->fetch(PDO::FETCH_ASSOC)){
                    print(json_encode($row));
                }else{
					print("NO_RESULT");
				}

            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_GETALL){
            try{
                $data = json_decode($_REQUEST["donnees"]);
                print($OP_GETALL . "%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM local ORDER BY id");
                $req->execute();
                
                $rows = array();
                
                //Tant qu'on trouve des locaux, on les ajoute a la liste des résultats
                while($row = $req->fetch(PDO::FETCH_ASSOC)){
                    $rows[] = $row;
                }
				print json_encode($rows);

            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_ADD){
            try{
				$data = json_decode($_REQUEST["donnees"]);
				$name = $data->{'name'};
				$address = $data->{'address'};
				$id_user = $data->{'id_user'};

				print($OP_ADD . "%");
				$connexion = connexionPDO();
                $connexion->beginTransaction();
				
				$requete = "INSERT INTO local(name,address)";
				$requete .= "VALUES ('$name','$address')";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				$id_local = $connexion->lastInsertId();
				$requete = "INSERT INTO local_user(id_local,id_user)";
				$requete .= "VALUES ('$id_local','$id_user');";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				if($connexion->commit()){
                    print("INSERT_SUCCESSFUL");
                }else{
                    $connexion->rollback();
                    print("INSERT_ERROR");
                }

            }catch(PDOException $e){
                $connexion->rollback();
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_REMOVE){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id = $data->{'id'};
                print($OP_REMOVE."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("DELETE FROM local WHERE id = $id");
                $req->execute();
                
                //Si 1 ligne afféctée == suppresion ok
                if($req->rowCount() == 1){
                    print("REMOVE_SUCCESSFUL");
                }else{
                    print("REMOVE_ERROR");
                }

            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
         } else if($_REQUEST["operation"] == $OP_GETBYCAMERA){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id_camera = $data->{'id_camera'};
                print($OP_GETBYCAMERA."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM local WHERE id IN (SELECT id_local FROM local_camera WHERE id_camera = $id_camera)");
                $req->execute();
                
                //si local trouvée, on envoie le résultat
                if($row = $req->fetch(PDO::FETCH_ASSOC)){
                    print(json_encode($row));
                }else{
					print("NO_RESULT");
				}

            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_ADDUSERPERMISSION){
            try{
                $data = json_decode($_REQUEST["donnees"]);
                $id_local = $data->{'id_local'};
				$id_user= $data->{'id_user'};

				print($OP_ADDUSERPERMISSION . "%");
				$connexion = connexionPDO();
                $connexion->beginTransaction();
				
				$id_video = $connexion->lastInsertId();
				$requete = "INSERT INTO local_user(id_local,id_user)";
				$requete .= "VALUES ('$id_local','$id_user');";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				$connexion->commit();
                print("INSERT_SUCCESSFUL");

            }catch(PDOException $e){
                $connexion->rollback();
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_GETBYUSER){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id_user = $data->{'id_user'};
                print($OP_GETBYUSER."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM local WHERE id IN (SELECT id_local FROM local_user WHERE id_user = $id_user) ORDER BY id");
                $req->execute();
                
                $rows = array();
                
                //Tant qu'on trouve des vidéos, on les ajoute a la liste des résultats
                while($row = $req->fetch(PDO::FETCH_ASSOC)){
                    $rows[] = $row;
                }
				print json_encode($rows);

            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_GETUSERSPERMISSION){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id_local = $data->{'id_local'};
                print($OP_GETUSERSPERMISSION."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM users WHERE id IN (SELECT id_user FROM local_user WHERE id_local = $id_local) ORDER BY id");
                $req->execute();
                
                $rows = array();
                
                
                print("QUERY_ERROR");
                
                while($row = $req->fetch(PDO::FETCH_ASSOC)){
                    $ligne = array();
                    $ligne["id"] = $row[0];
                    $ligne["name"] = $row[1];
                    $id = $ligne["id"];
                    $req = $connexion->prepare("SELECT * FROM local WHERE id = $id_local AND id IN (SELECT id_local FROM local_user WHERE id_user = $id) ORDER BY id");
                    $req->execute();
                    
                    if($row = $req->fetch(PDO::FETCH_ASSOC)){
                        $ligne["permission"] = 1;
                    }else{
                        $ligne["permission"] = 0;
                    }
                    
                    $rows[] = $row;
                }
                    
                //print json_encode($rows);
            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_CHANGEUSERSPERMISSION){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id_user = $data->{'id_user'};
				$id_local = $data->{'id_local'};
                print($OP_GETBYUSER."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM local WHERE id_local = $id_local AND id IN (SELECT id_local FROM local_user WHERE id_user = $id) ORDER BY id");
                $req->execute();
                
                $rows = array();
                
                if($row = $req->fetch(PDO::FETCH_ASSOC)){
                    $requete = "DELETE FROM local_user";
                    $requete .= "WHERE id_local = $id_local AND id_user = $id_user;";
                    $req = $connexion->prepare($requete);
                    $req->execute();
                }else{
                    $requete = "INSERT INTO local_user(id_local,id_user)";
                    $requete .= "VALUES ('$id_local','$id_user');";
                    $req = $connexion->prepare($requete);
                    $req->execute();
				}
				
				print("CHANGE_SUCCESSFUL");
            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else {
            print("OPERATION_UNKNOWN");
        }
    } else {
            print("NO_OPERATION_SPECIFIED");
        }
?>
