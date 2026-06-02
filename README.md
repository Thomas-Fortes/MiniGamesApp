# MiniGames App

## Auteur

Thomas FORTES

## Branche à utiliser pour tester le projet

main

## Ce qui fonctionne

- Écran d'accueil avec saisie du pseudo (pré-remplissage conservé)
- Jeu de Réaction : timer avec sens aléatoire (croissant/décroissant), vitesse variable (0.5x à 3x), cible atteignable, affichage écart et feedback
- Jeu de Mots Cachés : grille 3x3, sélection lettres, validation, timer 60s, score, meilleur score session
- Navigation multi-écrans NavHost avec routes type-safe
- Sauvegarde des scores en base de données Room
- Écran Leaderboard affichant les 10 meilleurs scores
- Indice dans jeu de mots (première lettre révélée, -1 point)
- Filtre leaderboard par jeu (Tous/Réaction/Mot Caché)
- Statistiques personnelles (nombre de parties, score moyen)
- Bouton réinitialisation des scores
- Timer aveugle (masquage quand à moins de 1.5s de la cible)
- Code entièrement en Kotlin
- Architecture MVVM propre
