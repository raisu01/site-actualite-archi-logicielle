# Lancer l'application

Guide pas à pas pour démarrer le site d'actualité en local.

## Prérequis

- **Java 17+** (`java -version`)
- **Maven** (`mvn -version`)
- **Docker** (pour la base) — ou un MySQL 8 installé localement
- **Apache Tomcat 10+** (obligatoirement 10+, car le projet utilise `jakarta.*`)

## 1. Démarrer la base de données

Avec Docker (recommandé), depuis la racine du projet :

```bash
docker compose up -d
```

Cela lance MySQL sur le port `3306`, crée la base `actualite` et charge les tables
+ données d'exemple (voir `docs/docker-bdd.md` pour le détail).

Vérifier qu'elle tourne :

```bash
docker compose ps
```

> Sans Docker : installer MySQL 8 puis exécuter `src/main/resources/schema.sql`.
> Vérifier que les identifiants dans `src/main/java/sn/actualite/dao/ConnexionBD.java`
> (hôte `localhost:3306`, utilisateur `root`, mot de passe vide) correspondent à votre base.

## 2. Construire l'application

```bash
mvn clean package
```

Cela produit le fichier **`target/actualite.war`**.

## 3. Déployer sur Tomcat

Copier le `.war` dans le dossier `webapps/` de Tomcat :

```bash
cp target/actualite.war $CATALINA_HOME/webapps/
```

Puis démarrer Tomcat :

```bash
# Linux / macOS
$CATALINA_HOME/bin/startup.sh

# Windows
%CATALINA_HOME%\bin\startup.bat
```

Tomcat déploie automatiquement le `.war` (le contexte prend le nom `actualite`).

## 4. Ouvrir le site

Dans le navigateur :

```
http://localhost:8080/actualite/
```

## Comptes de test

| Login   | Mot de passe | Rôle           |
|---------|--------------|----------------|
| admin   | admin123     | Administrateur |
| editeur | editeur123   | Éditeur        |

## Arrêter

```bash
# Tomcat
$CATALINA_HOME/bin/shutdown.sh        # (shutdown.bat sous Windows)

# Base de données (les données sont conservées)
docker compose down
```

## Application client (partie 3)

Le module `client/` n'est pas encore configuré (`client/pom.xml` est vide). Le guide de
lancement du client sera ajouté une fois la partie 3 développée.
