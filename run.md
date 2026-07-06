# Lancer l'application

Guide pas à pas pour démarrer le site d'actualité en local (commandes PowerShell / Windows).

## Prérequis

- **Java 17+** (`java -version`)
- **Maven** (`mvn -version`)
- **Docker** (pour la base) — ou un MySQL 8 installé localement
- **Apache Tomcat 10+** installé dans `C:\Users\Raisu\tools\apache-tomcat-10.1.56`
  (obligatoirement 10+, car le projet utilise `jakarta.*`)

## 1. Démarrer la base de données

Avec Docker (recommandé), depuis la racine du projet :

```powershell
docker compose up -d
```

Cela lance MySQL sur le port `3306`, crée la base `actualite` et charge les tables
+ données d'exemple (voir `docs/docker-bdd.md` pour le détail).

Vérifier qu'elle tourne :

```powershell
docker compose ps
```

> Sans Docker : installer MySQL 8 puis exécuter `src/main/resources/schema.sql`.
> Vérifier que les identifiants dans `src/main/java/sn/actualite/dao/ConnexionBD.java`
> (hôte `localhost:3306`, utilisateur `root`, mot de passe vide) correspondent à votre base.

## 2. Construire l'application

```powershell
mvn clean package
```

Cela produit le fichier **`target\actualite.war`**.

## 3. Déployer sur Tomcat

Si un ancien déploiement traîne, le supprimer d'abord (sinon Tomcat peut garder d'anciennes
JSP compilées en cache) :

```powershell
Remove-Item -Recurse -Force "C:\Users\Raisu\tools\apache-tomcat-10.1.56\webapps\actualite" -ErrorAction SilentlyContinue
```

Copier le `.war` dans le dossier `webapps` de Tomcat :

```powershell
Copy-Item target\actualite.war "C:\Users\Raisu\tools\apache-tomcat-10.1.56\webapps\"
```

Puis démarrer Tomcat :

```powershell
$env:CATALINA_HOME = "C:\Users\Raisu\tools\apache-tomcat-10.1.56"
& "$env:CATALINA_HOME\bin\startup.bat"
```

> `CATALINA_HOME` est aussi défini de façon persistante au niveau utilisateur (variable
> d'environnement Windows), donc un **nouveau** terminal PowerShell ouvert après
> aujourd'hui peut directement faire `& "$env:CATALINA_HOME\bin\startup.bat"` sans
> le réassigner. Dans un terminal déjà ouvert avant que la variable soit créée, il
> faut la réassigner comme ci-dessus (ou rouvrir le terminal).

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

## Services web (SOAP et REST)

Exposés par le même Tomcat, sous le même contexte `actualite` :

```
http://localhost:8080/actualite/ws/auth?wsdl            (SOAP - authentification)
http://localhost:8080/actualite/ws/utilisateurs?wsdl     (SOAP - CRUD utilisateurs, protégé par jeton)
http://localhost:8080/actualite/rest/articles            (REST - tous les articles, XML ou JSON selon "Accept")
http://localhost:8080/actualite/rest/articles/categories (REST - articles groupés par catégorie)
http://localhost:8080/actualite/rest/articles/categorie/{id} (REST - articles d'une catégorie)
```

Le CRUD utilisateurs SOAP (`/ws/utilisateurs`) exige un en-tête HTTP `X-Jeton` valide.
Un jeton se génère depuis le site web : connecté en tant qu'administrateur,
aller sur **Articles > Jetons** (sous-menu admin), choisir un utilisateur, cliquer "Générer".

## Arrêter

```powershell
# Tomcat
$env:CATALINA_HOME = "C:\Users\Raisu\tools\apache-tomcat-10.1.56"
& "$env:CATALINA_HOME\bin\shutdown.bat"

# Base de données (les données sont conservées)
docker compose down
```

## Application client (partie 3)

Application Swing indépendante, dans le module Maven `client/`. Elle appelle uniquement
le service SOAP (jamais la base ni le site web directement), donc **le serveur Tomcat
doit déjà tourner** (étapes 1 à 4 ci-dessus) avant de la lancer.

### Construire

```powershell
cd client
mvn clean package
```

Produit un jar exécutable autonome : **`client\target\actualite-client.jar`**.

### Lancer

```powershell
java -jar target\actualite-client.jar
```

Une fenêtre de connexion s'ouvre, demandant uniquement **login** et **mot de passe**.

### Workflow

1. Saisie login/mot de passe → appel SOAP à `AuthSoapService.authentifier()`.
2. Si le compte n'est pas administrateur → accès refusé.
3. Si administrateur → le serveur renvoie automatiquement le jeton actif de ce compte
   (pas besoin de le ressaisir) et la fenêtre de gestion des utilisateurs s'ouvre.
4. Chaque action (lister/créer/modifier/supprimer) appelle `UtilisateurSoapService`
   avec ce jeton dans l'en-tête `X-Jeton`.

> Si le message "Aucun jeton actif pour ce compte" apparaît : générer un jeton pour ce
> compte depuis le site web (voir section "Services web" ci-dessus), puis relancer le client.
