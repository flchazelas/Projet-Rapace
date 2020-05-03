<?php
	include "fonctions.php";

	//contrôle réception paramètres
	if(isset($_REQUEST["operation"])){
		//demande récupératon du premier utilisateur
		if($_REQUEST["operation"] == "utilisateurs"){
			try{
				print("utilisateurs%");
				$connexion = connexionPDO();
				$req = $connexion->prepare("SELECT * FROM users ORDER BY id");
				$req->execute();

				//si utilisateur, récupération du premier
				if($ligne = $req->fetch(PDO::FETCH_ASSOC)){
					print(json_encode($ligne));
				}

			}catch(PDOException $e){
				print "Erreur !% ".$e->getMessage();
				die();
			}
		}

		//enregistrement d'utilisateur
		elseif($_REQUEST["operation"] == "ajout"){
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
					$requete = "INSERT INTO users(username, password)";
					$requete .= "VALUES ('$pseudo', '$mdp')";
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
	}
?>
