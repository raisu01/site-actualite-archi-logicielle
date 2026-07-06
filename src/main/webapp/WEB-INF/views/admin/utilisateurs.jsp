<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des utilisateurs - ActuSN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="../fragments/entete.jsp"/>
<jsp:include page="../fragments/admin-nav.jsp"/>

<main class="conteneur">

    <div class="entete-section">
        <h2>Gestion des utilisateurs</h2>
        <c:if test="${empty action}">
            <a class="bouton-pilule" href="${pageContext.request.contextPath}/admin/utilisateurs?action=creer">+ Nouvel utilisateur</a>
        </c:if>
    </div>

    <c:if test="${not empty erreur}">
        <p class="message-erreur"><c:out value="${erreur}"/></p>
    </c:if>

    <%-- ===== Formulaire (création ou modification) ===== --%>
    <c:if test="${action == 'creer' or action == 'modifier'}">
        <div class="carte-formulaire">
            <h3>${action == 'creer' ? "Nouvel utilisateur" : "Modifier l'utilisateur"}</h3>

            <form method="post" action="${pageContext.request.contextPath}/admin/utilisateurs" class="formulaire-admin">
                <input type="hidden" name="action" value="${action}">
                <c:if test="${action == 'modifier'}">
                    <input type="hidden" name="id" value="${utilisateurEnEdition.id}">
                </c:if>

                <label for="login">Login</label>
                <input type="text" id="login" name="login" value="<c:out value="${utilisateurEnEdition.login}"/>" required>

                <label for="motDePasse">
                    Mot de passe
                    <c:if test="${action == 'modifier'}">(laisser vide pour ne pas changer)</c:if>
                </label>
                <input type="password" id="motDePasse" name="motDePasse" ${action == 'creer' ? 'required' : ''}>

                <label for="role">Rôle</label>
                <select id="role" name="role" required>
                    <option value="EDITEUR" ${utilisateurEnEdition.role == 'EDITEUR' ? 'selected' : ''}>Éditeur</option>
                    <option value="ADMINISTRATEUR" ${utilisateurEnEdition.role == 'ADMINISTRATEUR' ? 'selected' : ''}>Administrateur</option>
                </select>

                <div class="actions-formulaire">
                    <button type="submit" class="bouton-principal">Enregistrer</button>
                    <a class="lien-annuler" href="${pageContext.request.contextPath}/admin/utilisateurs">Annuler</a>
                </div>
            </form>
        </div>
    </c:if>

    <%-- ===== Liste des utilisateurs ===== --%>
    <c:if test="${empty action}">
        <c:if test="${empty utilisateurs}">
            <p class="message-vide">Aucun utilisateur pour le moment.</p>
        </c:if>

        <c:if test="${not empty utilisateurs}">
            <table class="table-admin">
                <thead>
                    <tr>
                        <th>Login</th>
                        <th>Rôle</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${utilisateurs}">
                        <tr>
                            <td><c:out value="${u.login}"/></td>
                            <td>${u.role}</td>
                            <td class="colonne-actions">
                                <a href="${pageContext.request.contextPath}/admin/utilisateurs?action=modifier&id=${u.id}">Modifier</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/utilisateurs"
                                      class="formulaire-suppression"
                                      onsubmit="return confirm('Supprimer cet utilisateur ?');">
                                    <input type="hidden" name="action" value="supprimer">
                                    <input type="hidden" name="id" value="${u.id}">
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
