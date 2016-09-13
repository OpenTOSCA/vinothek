package org.opentosca.ui.vinothek.model;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class ApplicationUnmarshaller {

	/**
	 * Unmarshalls the given XML string into an Application model.
	 * 
	 * @param xmlContent
	 * @return
	 */
	public static ApplicationWrapper unmarshall(String xmlContent) {
		
		/**
		 * For backward compatibility reasons we fix the namespace here.
		 * The self-service model JAR from Eclipse Winery has a different
		 * namespace than the CSAR which have been developed in the OpenTOSCA
		 * project.
		 */
		xmlContent = xmlContent.replace("http://opentosca.org/self-service", "http://www.eclipse.org/winery/model/selfservice");
		
		Class<ApplicationWrapper> targetClass = ApplicationWrapper.class;

		try {
			JAXBContext jc = JAXBContext.newInstance(targetClass);
			Unmarshaller u = jc.createUnmarshaller();

			StreamSource xmlStream = new StreamSource(new ByteArrayInputStream(
					xmlContent.getBytes("UTF-8")));
			JAXBElement<ApplicationWrapper> doc = u.unmarshal(xmlStream, targetClass);

			return doc.getValue();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
