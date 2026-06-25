# Base de données avec Docker

Ce document explique comment lancer la base de données MySQL du projet via Docker,
sans avoir à installer MySQL sur sa machine.

## Pourquoi

Chaque membre de l'équipe peut démarrer **la même base, identique pour tout le monde**,
avec une seule commande. Le schéma et les données d'exemple sont chargés automatiquement
au premier démarrage.

## Ce que fait `docker-compose.yml`

- Lance un conteneur **MySQL 8.3** nommé `actualite-db`.
- Expose le port **3306** sur la machine (le même que celui attendu par `dao/ConnexionBD`).
- Autorise la connexion **root sans mot de passe** (`MYSQL_ALLOW_EMPTY_PASSWORD`), ce qui
  correspond exactement aux identifiants utilisés dans `dao/ConnexionBD`
  (`root` / mot de passe vide).
- Monte `src/main/resources/schema.sql` dans `/docker-entrypoint-initdb.d/`. MySQL exécute
  automatiquement ce fichier **au tout premier démarrage** : il crée la base `actualite`,
  les tables et les données d'exemple.
- Stocke les données dans un volume nommé `actualite-db-data` : elles **survivent** à un
  arrêt/redémarrage du conteneur.

## Commandes

Démarrer la base (en arrière-plan) :

```bash
docker compose up -d
```

Vérifier qu'elle tourne :

```bash
docker compose ps
```

Voir les logs :

```bash
docker compose logs -f db
```

Se connecter en ligne de commande dans le conteneur :

```bash
docker exec -it actualite-db mysql -u root actualite
```

Arrêter la base (les données sont conservées) :

```bash
docker compose down
```

Tout supprimer, **y compris les données** (repart de zéro, le schéma sera rechargé au
prochain démarrage) :

```bash
docker compose down -v
```

## Point important

Le fichier `schema.sql` n'est rejoué **qu'au premier démarrage**, quand le volume est vide.
Si tu modifies `schema.sql` et que tu veux le recharger, il faut repartir d'un volume propre :

```bash
docker compose down -v
docker compose up -d
```
