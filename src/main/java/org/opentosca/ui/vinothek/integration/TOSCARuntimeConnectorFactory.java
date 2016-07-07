package org.opentosca.ui.vinothek.integration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kálmán Képes - kalman.kepes@iaas.uni-stuttgart.de
 *
 */
public class TOSCARuntimeConnectorFactory {

	private static List<TOSCARuntimeConnector> connectors = new ArrayList<TOSCARuntimeConnector>();

	// Private constructor. Prevents instantiation from other classes.
	private TOSCARuntimeConnectorFactory() {
		connectors.add(new OpenTOSCAConnector());
	}

	// taken from https://en.wikipedia.org/wiki/Singleton_pattern
	/**
	 * Initializes singleton.
	 *
	 * {@link SingletonHolder} is loaded on the first execution of
	 * {@link Singleton#getInstance()} or the first access to
	 * {@link SingletonHolder#INSTANCE}, not before.
	 */
	private static class SingletonHolder {
		private static final TOSCARuntimeConnectorFactory INSTANCE = new TOSCARuntimeConnectorFactory();
	}

	public static TOSCARuntimeConnectorFactory getInstance() {
		return SingletonHolder.INSTANCE;
	}

	protected static boolean addConnector(TOSCARuntimeConnector connector) {
		return TOSCARuntimeConnectorFactory.connectors.add(connector);
	}

	protected static boolean removeConnector(TOSCARuntimeConnector connector) {
		return TOSCARuntimeConnectorFactory.connectors.remove(connector);
	}

	public TOSCARuntimeConnector getConnector() {
		// TODO fancy check which connector to return should be implemented here
		return connectors.get(0);
	}

}
