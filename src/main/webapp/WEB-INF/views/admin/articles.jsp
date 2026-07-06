<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des articles - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="../fragments/entete.jsp"/>
<jsp:include page="../fragments/admin-nav.jsp"/>

<main class="conteneur">

    <div class="entete-section">
        <h2>Gestion des articles</h2>
        <c:if test="${empty action}">
            <a class="bouton-pilule" href="${pageContext.request.contextPath}/admin/articles?action=creer">+ Nouvel article</a>
        </c:if>
    </div>

    <c:if test="${not empty erreur}">
        <p class="message-erreur"><c:out value="${erreur}"/></p>
    </c:if>

    <%-- ===== Formulaire (création ou modification) ===== --%>
    <c:if test="${action == 'creer' or action == 'modifier'}">
        <div class="carte-formulaire">
            <h3>${action == 'creer' ? "Nouvel article" : "Modifier l'article"}</h3>

            <form method="post" action="${pageContext.request.contextPath}/admin/articles" class="formulaire-admin">
                <input type="hidden" name="action" value="${action}">
                <c:if test="${action == 'modifier'}">
                    <input type="hidden" name="id" value="${article.id}">
                </c:if>

                <label for="titre">Titre</label>
                <input type="text" id="titre" name="titre" value="<c:out value="${article.titre}"/>" required>

                <label for="categorieId">Catégorie</label>
                <select id="categorieId" name="categorieId" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${article.categorie.id == cat.id ? 'selected' : ''}>
                            <c:out value="${cat.nom}"/>
                        </option>
                    </c:forEach>
                </select>

                <label for="contenu">Contenu</label>
                <textarea id="contenu" name="contenu" rows="10" required><c:out value="${article.contenu}"/></textarea>

                <div class="actions-formulaire">
                    <button type="submit" class="bouton-principal">Enregistrer</button>
                    <a class="lien-annuler" href="${pageContext.request.contextPath}/admin/articles">Annuler</a>
                </div>
            </form>
        </div>
    </c:if>

    <%-- ===== Liste des articles ===== --%>
    <c:if test="${empty action}">
        <c:if test="${empty articles}">
            <p class="message-vide">Aucun article pour le moment.</p>
        </c:if>

        <c:if test="${not empty articles}">
            <table class="table-admin">
                <thead>
                    <tr>
                        <th>Titre</th>
                        <th>Catégorie</th>
                        <th>Auteur</th>
                        <th>Date</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="article" items="${articles}">
                        <tr>
                            <td><c:out value="${article.titre}"/></td>
                            <td><span class="badge badge-cat-${article.categorie.id % 4}"><c:out value="${article.categorie.nom}"/></span></td>
                            <td><c:out value="${article.auteur.login}"/></td>
                            <td>${article.datePublicationFormatee}</td>
                            <td class="colonne-actions">
                                <a href="${pageContext.request.contextPath}/admin/articles?action=modifier&id=${article.id}">Modifier</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/articles"
                                      class="formulaire-suppression"
                                      onsubmit="return confirm('Supprimer cet article ?');">
                                    <input type="hidden" name="action" value="supprimer">
                                    <input type="hidden" name="id" value="${article.id}">
                                    <button type="submit" class="lien-supprimer">Supprimer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </c:if>

</main>

<footer class="pied-page">
    <p>&copy; Projet Architecture Logicielle - Site d'actualité</p>
</footer>

</body>
</html>
