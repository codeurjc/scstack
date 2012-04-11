/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Fichero: ExcepcionGeneradorFicherosApache.java
 * Autor: Arek Klauza
 * Fecha: Enero 2011
 * Revisión: -
 * Versión: 1.0
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package excepciones.configuradorApache;

import excepciones.ExcepcionForja;

/**
 * <p>Este tipo de excepción nos indicará que ha sucedido algún tipo de problema
 * Mientras se estaba ejecutando la clase de apoyo para la generación de ficheros
 * de configuración de Apache.</p>
 * <p>Consiste básicamente en un wrapper sobre la clase <i>Exception</i> nativa
 * de Java para tener mejor clasificado el origen de la Excepción.</p>
 * @author Arek Klauza
 */
public class ExcepcionGeneradorFicherosApache extends ExcepcionForja {
    public ExcepcionGeneradorFicherosApache(String msg) {
        super(msg);
    }

}
