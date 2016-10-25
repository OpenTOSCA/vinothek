package org.opentosca.model.tosca.extension.transportextension;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.opentosca.model.tosca.TParameter;
import org.opentosca.model.tosca.TPlan;
import org.w3c.dom.Element;

@XmlRootElement(name = "Plan")
@XmlAccessorType(XmlAccessType.FIELD)
public class TPlanDTO {
<<<<<<< Updated upstream
	
	
	@XmlElement(name = "InputParameters")
	protected TPlanDTO.InputParameters inputParameters;
	@XmlElement(name = "OutputParameters")
	protected TPlanDTO.OutputParameters outputParameters;
	@XmlAttribute(name = "id", required = true)
	// @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	// @XmlID
	@XmlSchemaType(name = "ID")
	protected QName id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "planType", required = true)
	@XmlSchemaType(name = "anyURI")
	protected String planType;
	@XmlAttribute(name = "planLanguage", required = true)
	@XmlSchemaType(name = "anyURI")
	protected String planLanguage;
	
	
	public TPlanDTO(TPlan plan, String namespace) {
		id = new QName(namespace, plan.getId());
		name = plan.getName();
		planType = plan.getPlanType();
		planLanguage = plan.getPlanLanguage();
		
		if (null != plan.getInputParameters()) {
			inputParameters = new InputParameters();
			for (TParameter param : plan.getInputParameters().getInputParameter()) {
				TParameterDTO dto = new TParameterDTO(param);
				inputParameters.getInputParameter().add(dto);
			}
		}
		
		if (null != plan.getOutputParameters()) {
			outputParameters = new OutputParameters();
			for (TParameter param : plan.getOutputParameters().getOutputParameter()) {
				outputParameters.getOutputParameter().add(new TParameterDTO(param));
			}
		}
	}
	
	public TPlanDTO() {
	}
	
	public QName getId() {
		return id;
=======

    @XmlElement(name = "InputParameters")
    protected TPlanDTO.InputParameters inputParameters;
    @XmlElement(name = "OutputParameters")
    protected TPlanDTO.OutputParameters outputParameters;
    @XmlAttribute(name = "id", required = true)
    // @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    // @XmlID
    @XmlSchemaType(name = "ID")
    protected QName id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "planType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planType;
    @XmlAttribute(name = "planLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planLanguage;

    public TPlanDTO(TPlan plan, String namespace) {
	id = new QName(namespace, plan.getId());
	name = plan.getName();
	planType = plan.getPlanType();
	planLanguage = plan.getPlanLanguage();

	if (null != plan.getInputParameters()) {
	    inputParameters = new InputParameters();
	    for (TParameter param : plan.getInputParameters().getInputParameter()) {
		inputParameters.inputParameter.add(new TParameterDTO(param));
	    }
>>>>>>> Stashed changes
	}

	if (null != plan.getOutputParameters()) {
	    outputParameters = new OutputParameters();
	    for (TParameter param : plan.getOutputParameters().getOutputParameter()) {
		outputParameters.outputParameter.add(new TParameterDTO(param));
	    }
	}
    }

    public TPlanDTO() {
    }

    public QName getId() {
	return id;
    }

    public void setId(QName id) {
	this.id = id;
    }

    /**
     * Gets the value of the inputParameters property.
     * 
     * @return possible object is {@link TPlanDTO.InputParameters }
     * 
     */
    public TPlanDTO.InputParameters getInputParameters() {
	if (null == inputParameters) {
	    inputParameters = new TPlanDTO.InputParameters();
	}
	return inputParameters;
    }

    /**
     * Sets the value of the inputParameters property.
     * 
     * @param value
     *            allowed object is {@link TPlanDTO.InputParameters }
     * 
     */
    public void setInputParameters(TPlanDTO.InputParameters value) {
	inputParameters = value;
    }

    /**
     * Sets the value of the outputParameters property with the origin
     * OutputParameters element.
     * 
     * @param value
     *            allowed object is {@link TPlanDTO.OutputParameters }
     * 
     */
    public void setInputParameters(TPlan.InputParameters value) {
	if (null != value) {
	    inputParameters = new InputParameters();
	    for (TParameter param : value.getInputParameter()) {
		inputParameters.getInputParameter().add(new TParameterDTO(param));
	    }
	}
    }

    /**
     * Gets the value of the outputParameters property.
     * 
     * @return possible object is {@link TPlanDTO.OutputParameters }
     * 
     */
    public TPlanDTO.OutputParameters getOutputParameters() {
	return outputParameters;
    }

    /**
     * Sets the value of the outputParameters property.
     * 
     * @param value
     *            allowed object is {@link TPlanDTO.OutputParameters }
     * 
     */
    public void setOutputParameters(TPlanDTO.OutputParameters value) {
	outputParameters = value;
    }

    /**
     * Sets the value of the outputParameters property with the origin
     * OutputParameters element.
     * 
     * @param value
     *            allowed object is {@link TPlanDTO.OutputParameters }
     * 
     */
    public void setOutputParameters(TPlan.OutputParameters value) {
	if (null != value) {
	    outputParameters = new OutputParameters();
	    for (TParameter param : value.getOutputParameter()) {
		outputParameters.getOutputParameter().add(new TParameterDTO(param));
	    }
	}
    }

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
	return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setName(String value) {
	name = value;
    }

    /**
     * Gets the value of the planType property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPlanType() {
	return planType;
    }

    /**
     * Sets the value of the planType property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPlanType(String value) {
	planType = value;
    }

    /**
     * Gets the value of the planLanguage property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPlanLanguage() {
	return planLanguage;
    }

    /**
     * Sets the value of the planLanguage property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPlanLanguage(String value) {
	planLanguage = value;
    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="InputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
	"inputParameter"
    })
    public static class InputParameters {

	@XmlElement(name = "InputParameter", required = true)
	protected List<TParameterDTO> inputParameter;

	/**
	 * Gets the value of the inputParameter property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the inputParameter property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getInputParameter().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TParameter }
	 * 
	 * 
	 */
	public List<TParameterDTO> getInputParameter() {
	    if (inputParameter == null) {
		inputParameter = new ArrayList<TParameterDTO>();
	    }
	    return inputParameter;
	}

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="OutputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
	"outputParameter"
    })
    public static class OutputParameters {

	@XmlElement(name = "OutputParameter", required = true)
	protected List<TParameterDTO> outputParameter;

	/**
	 * Gets the value of the outputParameter property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the outputParameter property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getOutputParameter().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TParameter }
	 * 
	 * 
	 */
	public List<TParameterDTO> getOutputParameter() {
	    if (outputParameter == null) {
		outputParameter = new ArrayList<TParameterDTO>();
	    }
	    return outputParameter;
	}

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any processContents='lax' namespace='##other'/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
	"any"
    })
    public static class PlanModel {

	@XmlAnyElement(lax = true)
	protected Object any;

	/**
	 * Gets the value of the any property.
	 * 
	 * @return possible object is {@link Object } {@link Element }
	 * 
	 */
	public Object getAny() {
	    return any;
	}

	/**
	 * Sets the value of the any property.
	 * 
	 * @param value
	 *            allowed object is {@link Object } {@link Element }
	 * 
	 */
	public void setAny(Object value) {
	    any = value;
	}

    }

    /**
     * <p>
     * Java class for anonymous complex type.
     * 
     * <p>
     * The following schema fragment specifies the expected content contained
     * within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="reference" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PlanModelReference {

	@XmlAttribute(name = "reference", required = true)
	@XmlSchemaType(name = "anyURI")
	protected String reference;

	/**
	 * Gets the value of the reference property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReference() {
	    return reference;
	}

	/**
	 * Sets the value of the reference property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReference(String value) {
	    reference = value;
	}

    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Plan with ID \"" + id + "\", name \"" + name + "\", of type \"" + planType + "\", language \""
	    + planLanguage + "\" and input [");
	for (TParameterDTO param : inputParameters.getInputParameter()) {
	    builder.append("\"" + param.getName() + "\", ");
	}
	builder.append("]");
	for (TParameterDTO param : outputParameters.getOutputParameter()) {
	    builder.append("\"" + param.getName() + "\", ");
	}
	builder.append("]");
	return builder.toString();
    }
}
