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
                $req = $connexion->prepare("SELECT * FROM camera WHERE id = $id");
                $req->execute();
                
                //si camera trouvée, on envoie le résultat
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
                $req = $connexion->prepare("SELECT * FROM camera ORDER BY id");
                $req->execute();
                
                $rows = array();
                
                //Tant qu'on trouve des caméras, on les ajoute a la liste des résultats
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
				$ip = $data->{'ip'};
				$id_local = $data->{'id_local'};

				print($OP_ADD . "%");
				$connexion = connexionPDO();
                $connexion->beginTransaction();
                
				$requete = "INSERT INTO camera(name, ip)";
				$requete .= "VALUES ('$name', '$ip')";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				$id_camera = $connexion->lastInsertId();
				$requete = "INSERT INTO local_camera(id_local,id_camera)";
				$requete .= "VALUES ('$id_local','$id_camera');";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				if($connexion->commit()){
                    print("INSERT_SUCCESSFUL");
                }else{
                    $connexion->rollback();
                    print("INSERT_ERROR");
                }
            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } else if($_REQUEST["operation"] == $OP_REMOVE){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id = $data->{'id'};
                print($OP_REMOVE."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("DELETE FROM camera WHERE id = $id");
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
        } else if($_REQUEST["operation"] == $OP_GETBYLOCAL){
            try{
                $data = json_decode($_REQUEST["donnees"]);
				$id_local = $data->{'id'};
                print($OP_GETBYLOCAL."%");
                $connexion = connexionPDO();
                $req = $connexion->prepare("SELECT * FROM camera WHERE id IN (SELECT id_camera FROM local_camera WHERE id_local = $id_local)");
                $req->execute();
                
                $rows = array();
                
                //Tant qu'on trouve des caméras, on les ajoute a la liste des résultats
                while($row = $req->fetch(PDO::FETCH_ASSOC)){
                    $rows[] = $row;
                }
				print json_encode($rows);

            }catch(PDOException $e){
                print("QUERY_ERROR");
                die();
            }
        } 
        else {
            print("OPERATION_UNKNOWN");
        }
    } else {
            print("NO_OPERATION_SPECIFIED");
        }
?>
