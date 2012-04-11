/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Fichero: UsuariosResource.java
 * Autor: Arek Klauza
 * Fecha: Marzo 2011
 * Revisión: -
 * Versión: 1.0
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package RESTful.restlets.usuarios;

import RESTful.datos.Usuario;
import RESTful.datos.Usuarios;
import RESTful.restlets.BaseUsuariosResource;
import excepciones.api.ExcepcionLogin;
import excepciones.api.ExcepcionParametros;
import excepciones.gestorLdap.ExcepcionGestorLDAP;
import excepciones.gestorLdap.ExcepcionLDAPNoExisteRegistro;
import excepciones.gestorRedmine.ExcepcionGestorRedmine;
import excepciones.modeloDatos.ExcepcionUsuario;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * <p>Recurso que representa las listas de usuarios de la Forja, pudiendo ser la
 * lista de UIDs o la lista de Nombres completos.</p>
 * <p>Sobre este recurso está permitido realizar métodos GET y POST.</p>
 * @author Arek Klauza
 */
public class UsuariosResource extends BaseUsuariosResource {

    public UsuariosResource(Context context, Request request, Response response) throws ResourceException {
        super(context, request, response);
    }



    /**
     * <p>GET</p>
     * <p>Devuelve la lista de usuarios que hay en la Forja, pudiendo ser lista
     * de UIDs (sin parámetros) o lista de Nombres (con parámetro "?porNombres").</p>
     * <p>Corresponde con el CUA.1 y CUA.2</p>
     * @param variant Objeto que representa el tipo de petición recibida
     * @return Devuelve una representación del usuario.
     * @throws ResourceException
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Representation rep = null;
        Usuarios resource = null;
        String user = this.userReq;
        String pass = this.passReq;

        try {
            // Decodifica qué tipo de lista se ha pedido
            Parameter paramNombre = getRequest().getResourceRef().getQueryAsForm().getFirst("porNombre", true);
            Parameter paramEmail = getRequest().getResourceRef().getQueryAsForm().getFirst("emails", true);
            if (paramNombre != null) {
                resource = this.proxy.getUsuariosXNombre(user, pass);
            } else if (paramEmail != null) {
                resource = this.proxy.getEmailsUsuarios(user, pass);
            } else {
                resource = this.proxy.getUsuariosXUid(user, pass);
            }

            // Comprobación del tipo de representación pedida y serialización
            if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
                rep = getHtmlRepresentation(getRequest().getResourceRef().getPath() + "/index.html");
            } else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
                String stringRep = resource.serializarXml();
                rep = new StringRepresentation(stringRep, MediaType.TEXT_XML);
            } else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
                rep = resource.serializarJson();
            } else
                // No es una de las representaciones que puedo ofrecer,
                throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);

        } catch (ExcepcionParametros ex) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (ExcepcionLogin ex) {
            // this.pedirLogin(ex.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, ex.getMessage());
        } catch (ExcepcionLDAPNoExisteRegistro ex) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND , ex.getMessage());
        } catch (ExcepcionGestorLDAP ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL , ex.getMessage());
        }
        return rep;
    }





    /**
     * <p>POST</p>
     * <p>Crea un nuevo usuario en la Forja. Teniendo como restricción que el UID
     * de usuario facilitado en la estructura no esté ya en uso, en cuyo caso
     * lanzará una excepción</p>
     * <p>Corresponde con el CUS.1</p>
     * @param entity Representación del objeto que se recibe en la petición
     * @throws ResourceException
     */
    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException {
        Usuario usuario = null;
        String user = this.userReq;
        String pass = this.passReq;

        try {
            // Decodificamos la información en función de cómo venga la petición
            usuario = this.decodificaUsuario(entity);


            // Guarda el nuevo usuario a través del proxy que habla con la API
            proxy.postUsuario(usuario, user, pass);

            // Devuelve en el location header de respuesta la URI del nuevo usuario
            getResponse().setStatus(Status.SUCCESS_CREATED);            
            getResponse().setLocationRef(usuario.getUri());

        } catch (JSONException ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionGestorRedmine ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionParametros ex) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (ExcepcionUsuario ex) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (ExcepcionLogin ex) {
            // this.pedirLogin(ex.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, ex.getMessage());
        } catch (ExcepcionLDAPNoExisteRegistro ex) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionGestorLDAP ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (IOException ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        }
    }
    @Override
    public boolean allowPost() {
        return true;
    }
}
