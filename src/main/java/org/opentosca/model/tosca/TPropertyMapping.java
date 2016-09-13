//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2013.07.10 at 12:45:26 PM CEST
//
// TOSCA version: TOSCA-v1.0-cs02.xsd
//

package org.opentosca.model.tosca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for tPropertyMapping complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tPropertyMapping">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="serviceTemplatePropertyRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetObjectRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="targetPropertyRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPropertyMapping")
public class TPropertyMapping {
	
	@XmlAttribute(name = "serviceTemplatePropertyRef", required = true)
	protected String serviceTemplatePropertyRef;
	@XmlAttribute(name = "targetObjectRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object targetObjectRef;
	@XmlAttribute(name = "targetPropertyRef", required = true)
	protected String targetPropertyRef;
	
	
	/**
	 * Gets the value of the serviceTemplatePropertyRef property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getServiceTemplatePropertyRef() {
		return this.serviceTemplatePropertyRef;
	}
	
	/**
	 * Sets the value of the serviceTemplatePropertyRef property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setServiceTemplatePropertyRef(String value) {
		this.serviceTemplatePropertyRef = value;
	}
	
	/**
	 * Gets the value of the targetObjectRef property.
	 * 
	 * @return possible object is {@link Object }
	 * 
	 */
	public Object getTargetObjectRef() {
		return this.targetObjectRef;
	}
	
	/**
	 * Sets the value of the targetObjectRef property.
	 * 
	 * @param value allowed object is {@link Object }
	 * 
	 */
	public void setTargetObjectRef(Object value) {
		this.targetObjectRef = value;
	}
	
	/**
	 * Gets the value of the targetPropertyRef property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTargetPropertyRef() {
		return this.targetPropertyRef;
	}
	
	/**
	 * Sets the value of the targetPropertyRef property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setTargetPropertyRef(String value) {
		this.targetPropertyRef = value;
	}
	
}
