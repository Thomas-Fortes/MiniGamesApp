# MiniGames App

Application Android de mini-jeux développée en **Kotlin** avec **Jetpack Compose**.

## Auteur(s)

- **Thomas FORTES**

## Branche à tester

**`main`** — contient la version finale complète avec tous les TPs implémentés.

---

## Ce qui fonctionne ✅

### Général
- ✅ Navigation multi-écrans avec NavHost (type-safe)
- ✅ Saisie du pseudo avec validation (pré-remplit le champ au retour de chaque partie)
- ✅ Architecture MVVM propre (ViewModels + Repository + StateFlow)
- ✅ Persistance des données avec Room (SQLite)
- ✅ Code entièrement en Kotlin

### TP1 – Jeu de Réaction
- ✅ Écran d'accueil avec titre et boutons
- ✅ Timer qui se remplit à la bonne vitesse (variable aléatoire entre ×0.5 et ×3)
- ✅ Sens du timer aléatoire (croissant ↑ ou décroissant ↓)
- ✅ **Cible toujours atteignable** selon le sens choisi (bug corrigé)
- ✅ Affichage du timer avec format ms formaté (ex : `3.254 s`)
- ✅ Affichage de la cible et de l'écart
- ✅ Feedback personnalisé selon la précision ("Parfait !", "Excellent !", etc.)
- ✅ **Bonus – Timer aveugle** : masque le timer quand il est à moins de 1.5 s de la cible
- ✅ **Bonus – Vitesse variable** : la vitesse du timer change aléatoirement tous les ~5 s

### TP2 – Jeu de Mots Cachés
- ✅ Grille 3×3 avec lettres aléatoires (mot de 6 lettres + 3 leurres)
- ✅ Sélection lettre par lettre avec désactivation des cellules choisies
- ✅ Zone de saisie affichant les lettres sélectionnées
- ✅ Bouton effacer pour retirer la dernière lettre
- ✅ Validation du mot et incrémentation du score
- ✅ Bouton "Passer" pour générer une nouvelle grille sans points
- ✅ Timer décompte 60 secondes
- ✅ Score visible en temps réel
- ✅ Phase GAME_OVER avec score final et meilleur score de session
- ✅ **Bonus – Indice** : bouton "Indice" qui révèle la première lettre (une seule fois par grille, −1 pt)
- ✅ **Bonus – Meilleur score session** : affiché en bas après chaque partie

### TP3 – Persistance et Leaderboard
- ✅ Base de données Room avec entité Score
- ✅ Sauvegarde automatique des scores après chaque partie
- ✅ Scores stockés avec : pseudo, jeu, score, date/timestamp
- ✅ Écran Leaderboard affichant les 10 meilleurs scores (tous jeux)
- ✅ Affichage : rang, pseudo, nom du jeu, score, date formatée
- ✅ **Bonus – Filtre par jeu** : boutons "Tous / Réaction / Mot Caché" pour filtrer le leaderboard
- ✅ **Bonus – Statistiques personnelles** : affichage du nombre de parties et score moyen du joueur
- ✅ **Bonus – Réinitialisation** : bouton pour supprimer tous les scores de la base

---

## Ce qui ne fonctionne pas ❌

**Rien de critique.** L'application est entièrement fonctionnelle pour les trois séances.

### Notes minimes
- Le score du jeu de Réaction utilise une formule (`maxOf(0, 10_000 - gap)`) pour que plus c'est précis = plus haut score. C'est intentionnel pour faire sense avec un leaderboard.
- Le nom du jeu "Mot Caché" est stocké ainsi en base (avec accent). Si tu préfères "Mot Cache" (sans accent) pour cohérence, une migration Room serait nécessaire.
- La date du score utilise `System.currentTimeMillis()` (UTC), formatée en heure locale à l'affichage. Pas de problème en pratique.

---

## Architecture

```
com.example.minigamesapp/
├── data/                              # Couche persistance Room
│   ├── Score.kt                       # Entité
│   ├── ScoreDao.kt                    # Interface d'accès
│   ├── AppDatabase.kt                 # Singleton Room
│   └── ScoreRepository.kt             # Façade pour ViewModels
│
├── ui/
│   ├── home/
│   │   └── HomeScreen.kt              # Écran d'accueil + saisie pseudo
│   │
│   ├── reaction/
│   │   ├── ReactionScreen.kt          # UI du jeu de réaction
│   │   └── ReactionViewModel.kt       # Logique timer + sauvegarde score
│   │
│   ├── wordgame/
│   │   ├── WordGameScreen.kt          # UI du jeu de mots
│   │   └── WordGameViewModel.kt       # Logique grille + sauvegarde score
│   │
│   ├── leaderboard/
│   │   ├── LeaderboardScreen.kt       # UI leaderboard (avec filtres + stats)
│   │   └── LeaderboardViewModel.kt    # Logique chargement scores
│   │
│   └── theme/                         # Thème Material 3
│
├── Navigation.kt                      # Routes type-safe (@Serializable)
└── MainActivity.kt                    # Entrée + NavHost
```

---

## Commandes pour lancer

### Prérequis
- **Android SDK 26+**
- **Kotlin 2.2.10+**
- **Gradle 9.3.1+**

### Build debug
```bash
./gradlew assembleDebug
```

### Lancer sur émulateur/téléphone
1. Android Studio → **Edit Configuration** → Module = `app`, Deploy = `Default APK`, Launch = `Default Activity` → OK
2. Sélectionne un appareil (émulateur ou téléphone USB)
3. Clique **▶ Run** ou raccourci `Shift+F10`

### Tests
Le projet compile et fonctionne sans erreurs.

---

## Historique Git

```
4f9430e Amélioration : pseudo conservé entre les parties
0f59506 Séance 3 : Room, sauvegarde des scores et leaderboard
afec5ad Fix : la cible du timer est désormais toujours atteignable selon le sens
cdf1087 Séance 2 : ViewModel, NavHost et jeu Mot Caché
398f8ab Séance 1 : HomeScreen, jeu de réaction et navigation simple
7b7673f Setup : initialisation du projet Android
```

---

## Dépendances principales

- **AndroidX Activity Compose** 1.8.0
- **AndroidX Navigation Compose** 2.8.9
- **AndroidX Lifecycle ViewModel Compose** 2.8.7
- **Jetpack Compose** 2024.09.00
- **Room** 2.7.1
- **Kotlin Coroutines** 1.7.3
- **Kotlin Serialization** 2.2.10
- **KSP** 2.2.10-2.0.2

---

## Barème (TP3 : /20)

**Fonctionnel (12 pts)**
- Jeu de réaction complet ✅
- Navigation multi-écrans ✅
- Jeu de mots complet ✅
- Saisie pseudo + validation ✅
- Sauvegarde des scores via Room ✅
- Écran leaderboard ✅

**Technique (6 pts)**
- Architecture MVVM ✅
- Qualité du code ✅
- Git + README ✅

**Bonus (jusqu'à +2 pts)**
- TP1 – Timer aveugle ✅
- TP1 – Vitesse variable ✅
- TP2 – Indice ✅
- TP3 – Filtre du leaderboard par jeu ✅
- TP3 – Statistiques personnelles ✅
- TP3 – Réinitialisation des scores ✅

---

## Notes

- Tous les ViewModels étendant `AndroidViewModel` pour accéder au contexte application
- Repository pattern utilisé pour séparer la logique métier de l'accès aux données
- Navigation type-safe avec `@Serializable` et `toRoute<T>()`
- Pseudo stocké en `MiniGamesApp` pour persistence entre les écrans
- Scores sauvegardés automatiquement en fin de chaque partie via `viewModelScope.launch`

---

**Dernière mise à jour** : Juin 2026  
**Version** : 1.0 (complet TP1 + TP2 + TP3)
