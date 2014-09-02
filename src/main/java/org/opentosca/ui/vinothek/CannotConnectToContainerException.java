package org.opentosca.ui.vinothek;

public class CannotConnectToContainerException extends RuntimeException {

	private static final long serialVersionUID = -1378138872821013216L;
	
	private final String containerURL;
	
	public CannotConnectToContainerException(String containerURL) {
		super();
		this.containerURL = containerURL;
	}
	
	public CannotConnectToContainerException(String containerURL, Throwable cause) {
		super(cause);
		this.containerURL = containerURL;
	}
	
	public String getContainerURL() {
		return containerURL;
	}
	
}
