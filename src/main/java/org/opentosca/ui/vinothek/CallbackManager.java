package org.opentosca.ui.vinothek;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.ui.vinothek.model.ApplicationInstance;

public class CallbackManager {
	
	
	private final static List<ApplicationInstance> instanceStore = new ArrayList<ApplicationInstance>();
	
	
	public static String generateCallbackId(String applicationId) {
		return System.currentTimeMillis() + "";
	}
	
	public static void addInstance(ApplicationInstance i) {
		List<Integer> toRemove = new ArrayList<Integer>();
		for (int index = 0; index < instanceStore.size(); index++) {
			if (instanceStore.get(index).getCallbackId().equals(i.getCallbackId())) {
				toRemove.add(index);
			}
		}
		
		for (int index : toRemove) {
			instanceStore.remove(index);
		}
		
		instanceStore.add(0, i);
	}
	
	public static void removeInstance(ApplicationInstance i) {
		instanceStore.remove(i);
	}
	
	public static List<ApplicationInstance> getAllInstances() {
		return instanceStore;
	}
	
	public static ApplicationInstance getInstance(String callbackId) {
		for (ApplicationInstance i : instanceStore) {
			if (i.getCallbackId().equals(callbackId)) {
				return i;
			}
		}
		throw new RuntimeException("No application instance found for callbackId " + callbackId);
	}
}
