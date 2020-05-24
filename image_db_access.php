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
                $req = $connexion->prepare("SELECT * FROM image WHERE id = $id");
                $req->execute();
                
                //si image trouvée, on envoie le résultat
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
                $req = $connexion->prepare("SELECT * FROM image ORDER BY id");
                $req->execute();
                
                $rows = array();
                
                //Tant qu'on trouve des images, on les ajoute a la liste des résultats
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
				$chemin = $data->{'chemin'};

				print($OP_ADD . "%");
				$connexion = connexionPDO();
				$requete = "INSERT INTO image(chemin)";
				$requete .= "VALUES ('$chemin')";
				$req = $connexion->prepare($requete);
				$req->execute();
				if($req)
                    print("INSERT_SUCCESSFUL");
                else 
                    print("INSERT_ERROR");

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
                $req = $connexion->prepare("DELETE FROM image WHERE id = $id");
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
                $req = $connexion->prepare("SELECT * FROM image WHERE id IN (SELECT id_image FROM camera_image WHERE id_camera = $id_camera) ORDER BY id");
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
        } else if($_REQUEST["operation"] == $OP_ADDFORCAMERA){
            try{
                $data = json_decode($_REQUEST["donnees"]);
                $id_camera = $data->{'id_camera'};
				$chemin = $data->{'chemin'};

				print($OP_ADDFORCAMERA . "%");
				$connexion = connexionPDO();
                $connexion->beginTransaction();
                
				$requete = "INSERT INTO image(chemin)";
				$requete .= "VALUES ('$chemin');";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				$id_image = $connexion->lastInsertId();
				$requete = "INSERT INTO camera_image(id_camera,id_image)";
				$requete .= "VALUES ('$id_camera','$id_image');";
				$req = $connexion->prepare($requete);
				$req->execute();
				
				$connexion->commit();
                print("INSERT_SUCCESSFUL");

            }catch(PDOException $e){
                $connexion->rollback();
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
