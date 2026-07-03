<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${article.titre}"/> - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="fragments/entete.jsp"/>

<main class="conteneur conteneur-article">

    <a class="lien-retour" href="${pageContext.request.contextPath}/accueil">&larr; Retour à l'accueil</a>

    <article class="carte-detail">
        <span class="badge badge-cat-${article.categorie.id % 4}"><c:out value="${article.categorie.nom}"/></span>

        <h1 class="titre-detail"><c:out value="${article.titre}"/></h1>

        <p class="meta-article meta-detail">
            Par <c:out value="${article.auteur.login}"/> &middot; ${article.datePublicationFormatee}
            &middot; <a href="${pageContext.request.contextPath}/categorie?id=${article.categorie.id}"><c:out value="${article.categorie.nom}"/></a>
        </p>

        <div class="corps-article"><c:out value="${article.contenu}"/></div>
    </article>

</main>

<footer class="pied-page">
    <p>&copy; Projet Architecture Logicielle - Site d'actualité</p>
</footer>

</body>
</html>
