# ServiceChronometreJava

Une application Android simple en Java illustrant l'utilisation des services (**Foreground Service** et **Bound Service**) pour implémenter un chronomètre persistant.

<img src="img/demo.gif" width="500" height="500">

## Fonctionnalités

*   **Chronomètre en arrière-plan** : Le temps continue de s'écouler même si l'application est fermée ou mise en arrière-plan grâce à un `Foreground Service`.
*   **Notification persistante** : Affiche le temps écoulé en temps réel dans la barre de notifications.
*   **Liaison dynamique (Binding)** : L'activité se connecte au service lorsqu'elle est visible pour mettre à jour l'interface utilisateur (UI).
*   **Gestion des permissions** : Demande la permission `POST_NOTIFICATIONS` sur Android 13+.

## Structure du projet

*   `MainActivity.java` : Gère l'interface utilisateur, démarre/arrête le service et s'y connecte via `ServiceConnection`.
*   `ChronometreService.java` : Contient la logique du chronomètre, gère la notification et fournit un `Binder` pour permettre à l'activité de récupérer les données.
*   `activity_main.xml` : Mise en page simple avec un `TextView` pour le temps et deux boutons de contrôle.

## Comment ça marche ?

1.  **Démarrage** : L'utilisateur clique sur "Démarrer". L'activité lance le service en mode `startForegroundService`.
2.  **Service** : Le service crée une notification et utilise un `ScheduledExecutorService` pour incrémenter le compteur chaque seconde.
3.  **UI Sync** : L'activité se lie (`bindService`) au service. Une fois liée, elle utilise un `Handler` pour interroger régulièrement le service et afficher le temps formaté (`00:00`).
4.  **Arrêt** : L'utilisateur clique sur "Arrêter". L'activité envoie une action "STOP" au service, ce qui arrête le chronomètre, supprime la notification et libère les ressources.

## Prérequis

*   Android Studio
*   JDK 8+
*   Android 8.0 (API 26) ou version ultérieure (pour le support des Notification Channels)

## Installation

1.  Clonez le dépôt.
2.  Ouvrez le projet dans Android Studio.
3.  Compilez et lancez sur un émulateur ou un appareil physique.
