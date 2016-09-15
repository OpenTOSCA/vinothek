package org.opentosca.ui.vinothek;

/**
 * Defines the OpenTOSCA proprietary plan input message parameters used by
 * Vinothek and other tools
 */
public class WellknownPlanInputMessageParams {
	
	
	/**
	 * REST entrypoint for the container API of this CSAR<br/>
	 * Example: http://example.org:1337/containerapi/CSARs/Moodle.csar/
	 */
	public static final String IN_CSAR_ENTRYPOINT = "csarEntrypoint";
	
	/**
	 * REST entrypoint for the instance data API of this CSAR<br/>
	 * Will be replaced by TOSCA InterOp API in the future
	 */
	public static final String IN_INSTANCE_ENTRYPOINT = "instanceEntrypoint";
	
	/**
	 * Service instance this management or termination plans is executed on.
	 * <br/>
	 * Example: 56346347546754
	 */
	public static final String IN_INSTANCE_ID = "instanceId";
	
	/**
	 * The URL the plan should send its callback/result to<br/>
	 * Example: http://example.org/CallbackStatus?callbackId=x
	 */
	public static final String IN_CALLBACK_URL = "callbackUrl";
	
	/**
	 * Result status of the plan.<br/>
	 * Use Values of enum {@link OUT_SELFSERVICE_STATUS_CODES}.
	 */
	public static final String OUT_SELFSERVICE_STATUS = "selfserviceStatus";
	
	
	/**
	 * The possible codes for OUT_SELFSERVICE_STATUS_CODES
	 */
	public static enum OUT_SELFSERVICE_STATUS_CODES {
		/**
		 * Plan succeeded, intended action (build, management, termination) was
		 * executed.
		 */
		OK,
		
		/**
		 * Plan failed, intended action (build, management, termination) was NOT
		 * executed.<br/>
		 * Note: Plan is responsible to rollback partial changes!
		 */
		FAILED
	};
	
	
	/**
	 * Administrative/Log-Message for the Vinothek admin with the reason for
	 * this status. May be shown in advanced view.<br/>
	 * Example: "parameter x missing", "timeout calling localhost:1234"<br/>
	 */
	public static final String OUT_SELFSERVICE_ADMINMESSAGE = "selfserviceAdminMessage";
	
	/**
	 * The URL for the end user to use/view the instantiated application option,
	 * will be linked or opened automatically.<br/>
	 * But only if status is OK
	 */
	public static final String OUT_SELFSERVICE_APPLICATIONURL = "selfserviceApplicationUrl";
	
	/**
	 * User friendly result message to show in the UI,<br/>
	 * otherwise only OK or FAILED is displayed
	 */
	public static final String OUT_SELFSERVICE_MESSAGE = "selfserviceMessage";
	
	/**
	 * User friendly result message to show in the UI,<br/>
	 * contains information about the effects/results of policies
	 */
	public static final String OUT_SELFSERVICE_POLICY_MESSAGE = "selfservicePolicyMessage";
	
	/**
	 * This is the instance ID the container created for the instantiated
	 * service instance, which is used by the termination and management plans
	 * to retrieve information from the instance data API.<br/>
	 * Only included in the return message of the BUILD plan<br/>
	 * The format is up to the container.
	 */
	public static final String OUT_INSTANCE_ID = "instanceId";
	
}
