package es.sidelab.scstack.installer;

import es.sidelab.scstack.crawler.CrawlerException;
import es.sidelab.scstack.crawler.CrawlerInfo;
import es.sidelab.scstack.crawler.RedmineCrawler;

/**
 * Provides a method to install the REST service and web console of 
 * the SidelabCode's Stack as a system daemon.
 * @author <a href="mailto:radutom.vlad@gmail.com">Radu Tom Vlad</a>
 */
public class SCStackService {
	/**
	 * Configures the Redmine installation using a RedmineCrawler and 
	 * retrieves the REST API key.
	 * Then it installs the REST service and web console as a daemon. 
	 * @throws Exception
	 * @throws CrawlerException
	 */
	public void install() throws Exception, CrawlerException {
		System.out.println("\n*** CONFIGURING REDMINE PAGE ***\n");
		RedmineCrawler rcJSEnabled;
		rcJSEnabled = new RedmineCrawler(true, new CrawlerInfo("redmine", "http://localhost/login"));
		LDAPConnection conn = new LDAPConnection.Builder("ldapconn")
					.host(Instalacion.config.getProperty("hostLDAP"))
					.port(Instalacion.config.getProperty("puertoLDAP"))
					.account(Instalacion.config.getProperty("bindDN"))
					.accountPassword(Instalacion.config.getProperty("passBindDN"))
					.baseDN(Instalacion.config.getProperty("baseDN"))
					.build();
		String apiKey = rcJSEnabled.configureRedmine("admin", "admin", conn);
		if (apiKey == null || apiKey.isEmpty())
			throw new CrawlerException("Redmine's API key could not be obtained." +
					" REST Service will not be installed.");
		Instalacion.overwriteConfigValue("keyRedmineAPI", apiKey);
		System.out.println("**************************************************\n");
		
		System.out.println("\n*** INSTALLING REST SERVICE AS DAEMON***\n");
		Instalacion.ejecutar("unzip scstack-service-bin.zip");
		//copy the configuration file (overwriting it if necessary)
		Instalacion.ejecutar("cp scstack.conf scstack-service/");
		Instalacion.ejecutar("chown root:root scstack-service/daemon/scstack-service.conf");
		Instalacion.ejecutar("chmod 644 scstack-service/daemon/scstack-service.conf");
		Instalacion.ejecutar("cp scstack-service/daemon/scstack-service.conf /etc/init/");

		Instalacion.ejecutar("chown root:root scstack-service/daemon/scstack-service");
		Instalacion.ejecutar("chmod 755 scstack-service/daemon/scstack-service");
		Instalacion.ejecutar("cp scstack-service/daemon/scstack-service /etc/init.d/");

		Instalacion.ejecutar("start scstack-service");
		Instalacion.ejecutar("status scstack-service");
		System.out.println("**************************************************\n");
	}

}