package sn.actualite.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Set;

/**
 * Point d'entrée JAX-RS. Les services REST sont exposés sous
 * {@code /actualite/rest/...} (ex : {@code /actualite/rest/articles}).
 */
@ApplicationPath("/rest")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(ArticleRestService.class);
    }
}
