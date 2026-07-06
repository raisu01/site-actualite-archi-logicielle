<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des catégories - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="../fragments/entete.jsp"/>
<jsp:include page="../fragments/admin-nav.jsp"/>

<main class="conteneur">

    <div class="entete-section">
        <h2>Gestion des catégories</h2>
        <c:if test="${empty action}">
            <a class="bouton-pilule" href="${pageContext.request.contextPath}/admin/categories?action=creer">+ Nouvelle catégorie</a>
        </c:if>
    </div>

    <c:if test="${not empty erreur}">
        <p class="message-erreur"><c:out value="${erreur}"/></p>
    </c:if>

    <%-- ===== Formulaire (création ou modification) ===== --%>
    <c:if test="${action == 'creer' or action == 'modifier'}">
        <div class="carte-formulaire">
            <h3>${action == 'creer' ? "Nouvelle catégorie" : "Modifier la catégorie"}</h3>

            <form method="post" action="${pageContext.request.contextPath}/admin/categories" class="formulaire-admin">
                <input type="hidden" name="action" value="${action}">
                <c:if test="${action == 'modifier'}">
                    <input type="hidden" name="id" value="${categorieEnEdition.id}">
                </c:if>

                <label for="nom">Nom</label>
                <input type="text" id="nom" name="nom" value="<c:out value="${categorieEnEdition.nom}"/>" required>

                <div class="actions-formulaire">
                    <button type="submit" class="bouton-principal">Enregistrer</button>
                    <a class="lien-annuler" href="${pageContext.request.contextPath}/admin/categories">Annuler</a>
                </div>
            </form>
        </div>
    </c:if>

    <%-- ===== Liste des catégories ===== --%>
    <c:if test="${empty action}">
        <c:if test="${empty categories}">
            <p class="message-vide">Aucune catégorie pour le moment.</p>
        </c:if>

        <c:if test="${not empty categories}">
            <table class="table-admin">
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cat" items="${categories}">
                        <tr>
                            <td><c:out value="${cat.nom}"/></td>
                            <td class="colonne-actions">
                                <a href="${pageContext.request.contextPath}/admin/categories?action=modifier&id=${cat.id}">Modifier</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/categories"
                                      class="formulaire-suppression"
                                      onsubmit="return confirm('Supprimer cette catégorie ?');">
                                    <input type="hidden" name="action" value="supprimer">
                                    <input type="hidden" name="id" value="${cat.id}">
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
