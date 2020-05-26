<?php
	include "fonctions.php";

	//contrôle réception paramètres
	if(isset($_REQUEST["operation"])){

		//enregistrement d'utilisateur
		if($_REQUEST["operation"] == "ajout"){
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];
				//hashage du mdp
				//$mdp = $donnee[1];
				$mdp = password_hash($donnee[1], PASSWORD_BCRYPT);
				$num = $donnee[5];
				$dateAccess = $donnee[6];
				list($annee, $mois, $jour, $heure, $minute, $seconde) = sscanf($dateAccess, "%d-%d-%d %d:%d:%d");

				//insertion BDD
				print ("enregistrement%");
				$connexion = connexionPDO();

				$requete = $connexion->prepare("SELECT * FROM users WHERE username='$pseudo'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					if($ligne["username"] == $pseudo){
						print("Erreur connexion !% ");
					}
				}
				else{
					$requete = "INSERT INTO users(username, password, dataAccess, isLogged, isAdmin, isActif, numTel, dataCreate)";
					$requete .= "VALUES ('$pseudo', '$mdp', '$dateAccess', 0, 0, 1, '$num', '$dateAccess')";
					print($requete);
					$req = $connexion->prepare($requete);
					$req->execute();
				}

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//vérification de la connexion d'un utilisateur
		elseif ($_REQUEST["operation"] == "connexion") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];
				$mdp = $donnee[1];

				//recherche de l'utilisateur en BDD
				print ("recherche%");
				$connexion = connexionPDO();
				$requete = $connexion->prepare("SELECT * FROM users WHERE username='$pseudo'");
				$requete->execute();
				//$result = $connexion->query($requete);
				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					if(password_verify($mdp, $ligne["password"])){
						print(json_encode($ligne));
						if ($ligne["isActif"] == 1) {
							$requete = $connexion->prepare("UPDATE users SET isLogged = 1 WHERE username='$pseudo'");
							$requete->execute();
						}
					}
					else{
						print("Erreur connexion !% ");
					}
				}
				else{
					print("Erreur connexion !% ");
				}
				/*
				if ($result->num_rows > 0) {
				    while($row = $result->fetch_assoc()) {
				        echo "id: " . $row["id"]. " - Pseudo: " . $row["pseudo"]. " " . $row["mdp"]. "<br>";
				        print($row["pseudo"]);
				    }
				} else {
				    echo "0 results";
				}
				/*$req = $connexion->prepare($requete);
				$req->execute();*/


			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}
/*
		//vérification du champ dataAccess d'un utilisateur
		elseif ($_REQUEST["operation"] == "recupDataAccess") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];

				//recherche de l'utilisateur en BDD
				print ("recupDataAccess%");
				$connexion = connexionPDO();
				$requete = $connexion->prepare("SELECT dataAccess FROM users WHERE username='$pseudo'");
				$requete->execute();
				//$result = $connexion->query($requete);
				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					print(json_encode($ligne));
				}
				else{
					print("Erreur connexion !% ");
				}

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}*/

		//vérification du champ dataAccess d'un utilisateur
		elseif ($_REQUEST["operation"] == "deconnexion") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];

				//recherche de l'utilisateur en BDD
				print ("deconnexion%");
				$connexion = connexionPDO();
				$requete = "UPDATE users SET isLogged = 0 WHERE username='$pseudo'";
				print($requete);
				$req = $connexion->prepare($requete);
				$req->execute();

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//vérification du champ dataAccess d'un utilisateur
		elseif ($_REQUEST["operation"] == "changeDate") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];
				$dateAccess = $donnee[6];
				list($annee, $mois, $jour, $heure, $minute, $seconde) = sscanf($dateAccess, "%d-%d-%d %d:%d:%d");

				//recherche de l'utilisateur en BDD
				print ("changeDate%");
				$connexion = connexionPDO();
				$requete = "UPDATE users SET dataAccess = '$dateAccess' WHERE username='$pseudo'";
				$req = $connexion->prepare($requete);
				$req->execute();

				$requete = $connexion->prepare("SELECT dataAccess FROM users WHERE username='$pseudo'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					print(json_encode($ligne));
				}
				else{
					print("Erreur connexion !% ");
				}


			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//récupération des utilisateurs en BDD
		elseif ($_REQUEST["operation"] == "recupUtilisateurs") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];

				//récup tous les utilisateurs en Base sauf celui appellant la requête
				print ("recupUtilisateurs%");
				$connexion = connexionPDO();
				$requete = $connexion->prepare("SELECT * FROM users WHERE username!='$pseudo'");
				$requete->execute();

				if($rows = $requete->fetchAll(PDO::FETCH_ASSOC)){
					foreach($rows as $row){
					    print(json_encode($row)."%");
					}
				}
				else{
					print("Erreur connexion !% ");
				}

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//récupération d'un utilisateur en BDD
		elseif ($_REQUEST["operation"] == "recupUtilisateur") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];

				//récup tous les utilisateurs en Base sauf celui appellant la requête
				print ("recupUtilisateur%");
				$connexion = connexionPDO();
				$requete = $connexion->prepare("SELECT * FROM users WHERE username='$pseudo'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					print(json_encode($ligne));
				}
				else{
					print("Erreur connexion !% ");
				}

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//Changement du champs isActif
		elseif ($_REQUEST["operation"] == "changeIsActif") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];
				$isActif = $donnee[3];
				$isAdmin = $donnee[4];

				//recherche de l'utilisateur en BDD
				print ("changeIsActif%");
				$connexion = connexionPDO();
				if($isAdmin == 0){
					if($isActif == 0){
						$requete = "UPDATE users SET isActif = 1 WHERE username='$pseudo'";
					}
					else{
						$requete = "UPDATE users SET isActif = 0 WHERE username='$pseudo'";
					}
				}
				print($requete);
				$req = $connexion->prepare($requete);
				$req->execute();

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//Changement du champs isAdmin
		elseif ($_REQUEST["operation"] == "changeIsAdmin") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];
				$isActif = $donnee[3];
				$isAdmin = $donnee[4];

				//recherche de l'utilisateur en BDD
				print ("changeIsAdmin%");
				$connexion = connexionPDO();
				if($isAdmin == 0 && $isActif == 1){
					$requete = "UPDATE users SET isAdmin = 1 WHERE username='$pseudo'";
				}
				print($requete);
				$req = $connexion->prepare($requete);
				$req->execute();

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//Update profil Utilisateur
		elseif ($_REQUEST["operation"] == "updateProfil") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$pseudo = $donnee[0];
				$mdp = $donnee[1];
				$id = $donnee[2];
				$num = $donnee[5];

				print ("updateProfil%");
				//recherche de l'utilisateur en BDD
				$connexion = connexionPDO();

				$requete = $connexion->prepare("SELECT * FROM users WHERE id='$id'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					if($ligne["password"] != $mdp){
						$mdp = password_hash($donnee[1], PASSWORD_BCRYPT);
						$requete = "UPDATE users SET password = '$mdp' WHERE id='$id'";
						//print($requete);
						$req = $connexion->prepare($requete);
						$req->execute();
					}
					if($ligne["numTel"] != $num){
						$requete = "UPDATE users SET numTel = '$num' WHERE id='$id'";
						//print($requete);
						$req = $connexion->prepare($requete);
						$req->execute();
					}
					if($ligne["username"] != $pseudo){
						$requete = "UPDATE users SET username = '$pseudo' WHERE id='$id'";
						//print($requete);
						$req = $connexion->prepare($requete);
						$req->execute();
					}

					$requete = $connexion->prepare("SELECT * FROM users WHERE id='$id'");
					$requete->execute();

					if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
						print(json_encode($ligne));
					}
				}
				else{
					print("Erreur update !% ");
				}
/*
				print ("updateProfil%");
				$requete = $connexion->prepare("SELECT * FROM users WHERE id='$id'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					print(json_encode($ligne));
				}*/

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//suppression Utilisateur
		elseif ($_REQUEST["operation"] == "supprimerUtilisateur") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$id = $donnee[2];
				$isAdmin = $donnee[4];

				//recherche de l'utilisateur en BDD
				print ("supprimerUtilisateur%");
				$connexion = connexionPDO();
				if($isAdmin == 0){
					$requete = "DELETE FROM users WHERE id='$id'";
				}
				print($requete);
				$req = $connexion->prepare($requete);
				$req->execute();

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//récupération des numéros des utilisateurs liés à l'id du local en Alerte
		elseif ($_REQUEST["operation"] == "recupNumUtilisateur") {
			try{
				//récupération données en post
				$donnees = $_REQUEST["donnees"];
				$donnee = json_decode($donnees);
				$id_alerte = $donnee[0];

				print ("recupNumUtilisateur%");
				$connexion = connexionPDO();

				//Récupération de l'id_camera
				$requete = $connexion->prepare("SELECT * FROM camera_alerte WHERE id_alerte ='$id_alerte'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					$id_camera = $ligne["id_camera"];
				}

				//Récupération de l'id_local
				$requete = $connexion->prepare("SELECT * FROM local_camera WHERE id_camera ='$id_camera'");
				$requete->execute();

				if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
					$id_local = $ligne["id_local"];
				}

				//Récupération de l'id_user
				$requete = $connexion->prepare("SELECT * FROM local_user WHERE id_local ='$id_local'");
				$requete->execute();

				if($rows = $requete->fetchAll(PDO::FETCH_ASSOC)){
					foreach($rows as $row){
						$user = $row["id_user"];
						//Récupération de l'utilisateur
						$requete = $connexion->prepare("SELECT * FROM users WHERE id ='$user'");
						$requete->execute();

						if($ligne = $requete->fetch(PDO::FETCH_ASSOC)){
							print(json_encode($ligne)."%");
						}
					}
				}
				else{
					print("Erreur connexion !% ");
				}

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}
	}
?>
