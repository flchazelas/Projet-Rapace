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
					$requete = "INSERT INTO users(username, password, dataAccess, isLogged)";
					$requete .= "VALUES ('$pseudo', '$mdp', NOW(), 1)";
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
						$requete = $connexion->prepare("UPDATE users SET isLogged = 1 WHERE username='$pseudo'");
						$requete->execute();
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

				//recherche de l'utilisateur en BDD
				print ("changeDate%");
				$connexion = connexionPDO();
				$requete = "UPDATE users SET dataAccess = NOW() WHERE username='$pseudo'";
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
	}
?>
