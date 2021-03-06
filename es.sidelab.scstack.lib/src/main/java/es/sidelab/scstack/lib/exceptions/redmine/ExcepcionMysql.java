/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Fichero: ExcepcionMysql.java
 * Autor: Arek Klauza
 * Fecha: Febrero 2011
 * Revisión: -
 * Versión: 1.0
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package es.sidelab.scstack.lib.exceptions.redmine;

import es.sidelab.scstack.lib.exceptions.SCStackException;

/**
 * <p>Este tipo de excepción nos indicará que ha sucedido algún tipo de problema
 * durante la conexión con la base de datos Mysql de Redmine.</p>
 * <p>Consiste básicamente en un wrapper sobre la clase <i>Exception</i> nativa
 * de Java para tener mejor clasificado el origen de la Excepción.</p>
 * @author Arek Klauza
 */
public class ExcepcionMysql extends SCStackException {

	public ExcepcionMysql(Exception e) {
        super(e);
    }

    public ExcepcionMysql(String msg) {
        super(msg);
    }

}
