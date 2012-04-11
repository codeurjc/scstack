/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Fichero: UsuarioResource.java
 * Autor: Arek Klauza
 * Fecha: Marzo 2011
 * Revisión: -
 * Versión: 1.0
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package RESTful.restlets.usuarios;

import RESTful.datos.Usuario;
import RESTful.restlets.BaseUsuariosResource;
import excepciones.api.ExcepcionLogin;
import excepciones.api.ExcepcionParametros;
import excepciones.configuradorApache.ExcepcionConsola;
import excepciones.configuradorApache.ExcepcionGeneradorFicherosApache;
import excepciones.gestorLdap.ExcepcionGestorLDAP;
import excepciones.gestorLdap.ExcepcionLDAPAdministradorUnico;
import excepciones.gestorLdap.ExcepcionLDAPNoExisteRegistro;
import excepciones.gestorRedmine.ExcepcionGestorRedmine;
import excepciones.modeloDatos.ExcepcionUsuario;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
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
 * <p>Recurso que representa una entidad usuario de la Forja.</p>
 * <p>Sobre este recurso está permitido realizar métodos GET, PUT y DELETE.</p>
 * @author Arek Klauza
 */
public class UsuarioResource extends BaseUsuariosResource {

    public UsuarioResource(Context context, Request request, Response response) throws ResourceException {
        super(context, request, response);
    }



    

    /**
     * <p>GET</p>
     * <p>Consulta los datos de un usuario determinado de la Forja.</p>
     * <p>Corresponde con el CUU.1</p>
     * @param variant Objeto que representa el tipo de petición recibida
     * @return Devuelve una representación del usuario.
     * @throws ResourceException
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException  {
        Representation rep = null;
        Usuario resource = null;
        String user = this.userReq;
        String pass = this.passReq;

       try {
            // Recupera el uid del usuario a consultar
            String uid = (String) getRequest().getAttributes().get("uid");
            resource = proxy.getUsuario(uid, userReq, passReq);
            // Comprobación del tipo de representación pedida y serialización
            if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
                rep = getHtmlRepresentation("/usuarios/usuario.html");
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
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, ex.getMessage());
        } catch (ExcepcionGestorLDAP ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        }
        return rep;
    }



    
    
    
    
    /**
     * <p>PUT</p>
     * <p>Edita un usuario cuando el UID suministrado ya existe. Si el UID no
     * existe devuelve un 404.</p>
     * <p>Corresponde con el CUU.2</p>
     * @param entity Representación del objeto que se recibe en la petición
     * @throws ResourceException
     */
    @Override
    public void storeRepresentation(Representation entity) throws ResourceException {
        Usuario usuario = null;
        String user = this.userReq;
        String pass = this.passReq;

        try {
            // Decodificamos la información en función de cómo venga la petición
            usuario = this.decodificaUsuario(entity);

            // Si el uid de la URI no coincide con el uid del XML se aborta
            if (!((String) getRequest().getAttributes().get("uid")).equals(usuario.getUid())) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
                        "El uid suministrado en la estructura de datos no coincide con el de la URI");
            }

            // Guarda el nuevo usuario a través del proxy que habla con la API
            proxy.putUsuario(usuario, user, pass);
            
            // Devuelve en el location header de respuesta la URI del nuevo usuario
            getResponse().setStatus(Status.SUCCESS_OK);
            getResponse().setLocationRef(usuario.getUri());

        } catch (JSONException ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionParametros ex) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (ExcepcionGestorRedmine ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
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
    public boolean allowPut() {
        return true;
    }



    


    /**
     * <p>DELETE</p>
     * <p>Elimina definitivamente a un usuario concreto de la Forja.</p>
     * <p>Corresponde con el CUS.4</p>
     * @throws ResourceException
     */
    @Override
    public void removeRepresentations()throws ResourceException {
        String user = this.userReq;
        String pass = this.passReq;

        try {
            String uid = (String)getRequest().getAttributes().get("uid");
            proxy.deleteUsuario(uid, user, pass);

            // Notifica que la solicitud ha sido realizada. No necesita enviarse ningún contenido.
            getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
            
        } catch (ExcepcionGestorRedmine ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionParametros ex) {
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
        } catch (ExcepcionLogin ex) {
            // this.pedirLogin(ex.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, ex.getMessage());
        } catch (ExcepcionLDAPNoExisteRegistro ex) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, ex.getMessage());
        } catch (ExcepcionLDAPAdministradorUnico ex) {
            throw new ResourceException(Status.CLIENT_ERROR_FAILED_DEPENDENCY, ex.getMessage());
        } catch (ExcepcionConsola ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionGeneradorFicherosApache ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        } catch (ExcepcionGestorLDAP ex) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage());
        }
    }
    @Override
    public boolean allowDelete() {
        return true;
    }
}
