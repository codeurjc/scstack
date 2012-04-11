/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RESTful.restlets;

import excepciones.api.ExcepcionLogin;
import excepciones.api.ExcepcionParametros;
import excepciones.gestorLdap.ExcepcionGestorLDAP;
import excepciones.gestorLdap.ExcepcionLDAPNoExisteRegistro;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * <p>
 * @author Arek
 */
public class LoginResource extends BaseResource {

    public LoginResource(Context context, Request request, Response response) throws ResourceException {
        super(context, request, response);
    }


    /**
     * <p>GET</p>
     * <p>Devuelve un String con el rol del usuario que hace el Login.</p>
     * @param variant Objeto que representa el tipo de petición recibida
     * @return Devuelve una representación String del rol de usuario.
     * @throws ResourceException
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Representation rep = null;
        String user = this.userReq;
        String pass = this.passReq;

        try {
            String login = proxy.doLogin(user, pass);

            // Comprobación del tipo de representación pedida y serialización
            if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
                rep = getHtmlRepresentation("/index.html");
            } else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
                rep = new StringRepresentation(login, MediaType.TEXT_XML);
            } else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
                rep = new StringRepresentation(login, MediaType.TEXT_PLAIN);
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
}
