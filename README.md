# Demo Chess Game WebSocket

Ce projet est une application de jeu d'échecs en temps réel utilisant **Spring Boot** et **WebSocket**.  
Deux joueurs peuvent se connecter à une même partie, jouer des coups et voir le plateau synchronisé instantanément.

---

## **1️⃣ Pré-requis**

Avant de commencer le test :

1. Créer deux utilisateurs via l’API :  
   - **POST** `http://localhost:8080/demo/api/auth/register`  
   - Exemple : `user1` et `test`

2. Se connecter avec chaque utilisateur :  
   - **POST** `http://localhost:8080/demo/api/auth/login`  
   - Le login peut être enregistré dans le **localStorage** pour la session front.

3. Vérifier les joueurs en ligne :  
   - **GET** `http://localhost:8080/demo/api/players/online`

---

## **2️⃣ Lancer le serveur**

1. Assurez-vous que la base de données est configurée et démarrée.  
2. Lancer Spring Boot :  

Endpoints importants :
                  WebSocket : ws://localhost:8080/demo/ws
                  Auth Register : POST http://localhost:8080/demo/api/auth/register
                  Auth Login : POST http://localhost:8080/demo/api/auth/login
                  Joueurs en ligne : GET http://localhost:8080/demo/api/players/online

3️⃣ Invitation et démarrage de la partie 
Depuis le joueur test, envoyer une invitation à user1 via l’interface front.
Vérifier dans la console serveur si l’invitation est envoyée.
Depuis l’autre onglet (user1), accepter ou rejeter l’invitation.

Si acceptée, la console affiche :
Message reçu via WebSocket dans le console angular : 
{type: 'gameStart', player2: 'user1', player1: 'test', gameId: 1}
Cela indique que la partie a commencé et que le plateau est synchronisé pour les deux joueurs.


4️⃣Connexion des deux joueurs
Ouvrir WebSocket King ou tout client WebSocket : https://websocketking.com
Ouvrir deux onglets : un pour chaque joueur.
URL à utiliser :
Joueur 1 (test) :ws://localhost:8080/demo/ws?gameId=1&username=test
Joueur 2 (user1) : ws://localhost:8080/demo/ws?gameId=1&username=user1

Cliquer sur Connect dans chaque onglet websocket : 
Connected to ws://localhost:8080/demo/ws?gameId=3&username=test
Connected to ws://localhost:8080/demo/ws?gameId=3&username=user1


5️⃣ Jouer des coups
Chaque fois qu’un joueur fait un mouvement sur le plateau, envoyer un message de type move via WebSocket.
Exemple de séquence :
test joue E2 → E4 :
{
  "type": "move",
  "gameId": 3,
  "fromCell": "E2",
  "toCell": "E4",
  "player": "test"
}

user1 joue E7 → E5 :
{
  "type": "move",
  "gameId": 3,
  "fromCell": "E7",
  "toCell": "E5",
  "player": "user1"
}

test joue G1 → F3 :
{
  "type": "move",
  "gameId": 3,
  "fromCell": "G1",
  "toCell": "F3",
  "player": "test"
}

Chaque coup est sauvegardé en base de données via MoveRepository.
Le serveur transmet automatiquement chaque coup à l’autre joueur pour synchronisation temps réel.


6️⃣ Reconnexion et reprise de partie
Si un joueur se déconnecte et se reconnecte avec le même gameId,
le serveur renvoie tous les coups précédents pour reconstruire le plateau.


