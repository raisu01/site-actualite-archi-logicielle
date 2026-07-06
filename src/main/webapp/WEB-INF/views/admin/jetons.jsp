<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des jetons - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="../fragments/entete.jsp"/>
<jsp:include page="../fragments/admin-nav.jsp"/>

<main class="conteneur">

    <div class="entete-section">
        <h2>Gestion des jetons</h2>
    </div>

    <c:if test="${not empty erreur}">
        <p class="message-erreur"><c:out value="${erreur}"/></p>
    </c:if>

    <%-- ===== Génération d'un nouveau jeton ===== --%>
    <div class="carte-formulaire">
        <h3>Générer un nouveau jeton</h3>

        <form method="post" action="${pageContext.request.contextPath}/admin/jetons" class="formulaire-admin">
            <input type="hidden" name="action" value="generer">

            <label for="utilisateurId">Utilisateur</label>
            <select id="utilisateurId" name="utilisateurId" required>
                <c:forEach var="u" items="${utilisateurs}">
                    <option value="${u.id}"><c:out value="${u.login}"/> (${u.role})</option>
                </c:forEach>
            </select>

            <div class="actions-formulaire">
                <button type="submit" class="bouton-principal">Générer le jeton</button>
            </div>
        </form>
    </div>

    <%-- ===== Liste des jetons ===== --%>
    <c:if test="${empty jetons}">
        <p class="message-vide">Aucun jeton pour le moment.</p>
    </c:if>

    <c:if test="${not empty jetons}">
        <table class="table-admin">
            <thead>
                <tr>
                    <th>Valeur</th>
                    <th>Utilisateur</th>
                    <th>Date de création</th>
                    <th>Statut</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="j" items="${jetons}">
                    <tr>
                        <td><code><c:out value="${j.valeur}"/></code></td>
                        <td><c:out value="${j.utilisateur.login}"/></td>
                        <td>${j.dateCreationFormatee}</td>
                        <td>${j.actif ? "Actif" : "Révoqué"}</td>
                        <td class="colonne-actions">
                            <c:choose>
                                <c:when test="${j.actif}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/jetons" class="formulaire-suppression">
                                        <input type="hidden" name="action" value="revoquer">
                                        <input type="hidden" name="id" value="${j.id}">
                                        <button type="submit" class="lien-supprimer">Révoquer</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/jetons" class="formulaire-suppression">
                                        <input type="hidden" name="action" value="reactiver">
                                        <input type="hidden" name="id" value="${j.id}">
                                        <button type="submit">Réactiver</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                            <form method="post" action="${pageContext.request.contextPath}/admin/jetons"
                                  class="formulaire-suppression"
                                  onsubmit="return confirm('Supprimer definitivement ce jeton ?');">
                                <input type="hidden" name="action" value="supprimer">
                                <input type="hidden" name="id" value="${j.id}">
                                <button type="submit" class="lien-supprimer">Supprimer</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

</main>

<footer class="pied-page">
    <p>&copy; Projet Architecture Logicielle - Site d'actualité</p>
</footer>

</body>
</html>
