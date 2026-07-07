package sn.actualite.soap;

import jakarta.servlet.ServletConfig;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

/**
 * Point d'entrée JAX-WS (Apache CXF) des services SOAP : Tomcat ne fournit
 * aucun runtime JAX-WS (contrairement à GlassFish/Payara), donc CXF le fait à
 * sa place. Ce servlet remplace la configuration Spring habituelle de CXF
 * (inutile ici) : il publie lui-même les deux endpoints dès que son bus est
 * chargé, sur le préfixe "/ws/*" mappé dans web.xml :
 *
 * <pre>
 * GET/POST /actualite/ws/auth?wsdl           -&gt; AuthSoapService (non protégé)
 * GET/POST /actualite/ws/utilisateurs?wsdl   -&gt; UtilisateurSoapService (protégé par jeton, voir util/JetonFilter)
 * </pre>
 */
public class ServeurSoapServlet extends CXFNonSpringServlet {

    @Override
    protected void loadBus(ServletConfig servletConfig) {
        super.loadBus(servletConfig);
        Bus bus = getBus();
        publier(bus, AuthSoapService.class, new AuthSoapServiceImpl(), "/auth");
        publier(bus, UtilisateurSoapService.class, new UtilisateurSoapServiceImpl(), "/utilisateurs");
    }

    private void publier(Bus bus, Class<?> contrat, Object implementation, String adresse) {
        JaxWsServerFactoryBean fabrique = new JaxWsServerFactoryBean();
        fabrique.setBus(bus);
        fabrique.setServiceClass(contrat);
        fabrique.setServiceBean(implementation);
        fabrique.setAddress(adresse);
        fabrique.create();
    }
}
