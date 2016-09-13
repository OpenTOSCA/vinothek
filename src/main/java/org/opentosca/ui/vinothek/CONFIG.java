package org.opentosca.ui.vinothek;

public class CONFIG {

	// CONTAINER API CONFIG
	public static final String DEFAULT_CONTAINER_HOST = "localhost";
	public static final int DEFAULT_CONTAINER_PORT = 1337;
	public final static String CONTAINER_API = "http://" + DEFAULT_CONTAINER_HOST + ":" + DEFAULT_CONTAINER_PORT + "/containerapi";

	// CSAR CONFIG
	public static final String CSAR_LIST_REL_URL = "/containerapi/CSARs";
	public static final String METADATA_FOLDER = "/Content/SELFSERVICE-Metadata/";
	public static final String METADATA_FILE = "data.xml";
}
