<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${categorie.nom}"/> - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="fragments/entete.jsp"/>

<main class="conteneur">

    <div class="entete-section">
        <h2>Catégorie : <c:out value="${categorie.nom}"/></h2>
    </div>

    <c:if test="${empty articles}">
        <p class="message-vide">Aucun article dans cette catégorie pour le moment.</p>
    </c:if>

    <c:if test="${not empty articles}">
        <div class="grille-articles">
            <c:forEach var="article" items="${articles}">
                <article class="carte-article">
                    <span class="badge badge-cat-${article.categorie.id % 4}"><c:out value="${article.categorie.nom}"/></span>
                    <h3><a href="${pageContext.request.contextPath}/article?id=${article.id}"><c:out value="${article.titre}"/></a></h3>
                    <p class="resume-article"><c:out value="${article.resume}"/></p>
                    <p class="meta-article"><c:out value="${article.auteur.login}"/> &middot; ${article.datePublicationFormatee}</p>
                </article>
            </c:forEach>
        </div>
    </c:if>

</main>

<footer class="pied-page">
    <p>&copy; Projet Architecture Logicielle - Site d'actualité</p>
</footer>

</body>
</html>
