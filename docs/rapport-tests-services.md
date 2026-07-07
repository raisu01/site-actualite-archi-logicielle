# Tests des services (REST & SOAP)

Ce document résume les tests effectués sur les services web exposés par l'application `actualite`. (Remplace les captures d'écran demandées).

## 1. Tests REST (JAX-RS)

Les services REST sont accessibles à l'adresse de base : `http://localhost:8080/actualite/rest/articles`

**Test 1 : Récupérer tous les articles en JSON**
- **Requête** : `GET /rest/articles` avec `Accept: application/json`
- **Résultat attendu** : Code HTTP 200, liste JSON de tous les articles.
- **Statut** : OK.

**Test 2 : Récupérer tous les articles en XML**
- **Requête** : `GET /rest/articles` avec `Accept: application/xml`
- **Résultat attendu** : Code HTTP 200, liste XML de tous les articles.
- **Statut** : OK.

**Test 3 : Articles groupés par catégories**
- **Requête** : `GET /rest/articles/categories` avec `Accept: application/json`
- **Résultat attendu** : Code HTTP 200, structure JSON groupant les articles par leur catégorie.
- **Statut** : OK.

## 2. Tests SOAP (JAX-WS)

Les services SOAP sont accessibles via :
- `http://localhost:8080/actualite/ws/auth`
- `http://localhost:8080/actualite/ws/utilisateurs`

**Test 4 : Authentification réussie**
- **Requête** : POST `/ws/auth`
```xml
<soap:authentifier>
    <login>admin</login>
    <motDePasse>admin123</motDePasse>
</soap:authentifier>
```
- **Résultat attendu** : Code 200, réponse avec `succes=true` et récupération d'un jeton si généré.

**Test 5 : Accès sécurisé aux utilisateurs (sans jeton)**
- **Requête** : POST `/ws/utilisateurs` sans l'en-tête `X-Jeton`
- **Résultat attendu** : Code 500 (Faute SOAP) ou erreur d'authentification par le filtre. Accès refusé.

**Test 6 : Accès sécurisé aux utilisateurs (avec jeton valide)**
- **Requête** : POST `/ws/utilisateurs` avec `X-Jeton: <jeton_valide>`
- **Résultat attendu** : Code 200, liste XML des utilisateurs de la BD.

**Conclusion** : L'ensemble des services web, la négociation de contenu (JSON/XML) et la sécurité par jeton HTTP custom fonctionnent correctement.
