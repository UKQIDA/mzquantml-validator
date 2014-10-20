/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.liv.mzquantml.rulefilter.jaxb;

import javax.xml.bind.annotation.*;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 20-Sep-2012 11:24:10
 */
/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}userConditions"/>
 *         &lt;element ref="{}objectRuleConditions" minOccurs="0"/>
 *         &lt;element ref="{}cvMappingRuleConditions" minOccurs="0"/>
 *         &lt;element ref="{}references" minOccurs="0"/>
 *         &lt;element ref="{}mandatoryElements" minOccurs="0"/>
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
    "userConditions",
    "objectRuleConditions",
    "cvMappingRuleConditions",
    "references",
    "mandatoryElements"
})
@XmlRootElement(name = "ruleFilter")
public class RuleFilter {

    @XmlElement(required = true)
    protected UserConditions userConditions;
    protected ObjectRuleConditions objectRuleConditions;
    protected CvMappingRuleConditions cvMappingRuleConditions;
    protected References references;
    protected MandatoryElements mandatoryElements;

    /**
     * Gets the value of the userConditions property.
     *
     * @return
     * possible object is
     *     {@link UserConditions }
     *
     */
    public UserConditions getUserConditions() {
        return userConditions;
    }

    /**
     * Sets the value of the userConditions property.
     *
     * @param value
* allowed object is
     *     {@link UserConditions }
     *
     */
    public void setUserConditions(UserConditions value) {
        this.userConditions = value;
    }

    /**
     * Gets the value of the objectRuleConditions property.
     *
     * @return
     * possible object is
     *     {@link ObjectRuleConditions }
     *
     */
    public ObjectRuleConditions getObjectRuleConditions() {
        return objectRuleConditions;
    }

    /**
     * Sets the value of the objectRuleConditions property.
     *
     * @param value
* allowed object is
     *     {@link ObjectRuleConditions }
     *
     */
    public void setObjectRuleConditions(ObjectRuleConditions value) {
        this.objectRuleConditions = value;
    }

    /**
     * Gets the value of the cvMappingRuleConditions property.
     *
     * @return
     * possible object is
     *     {@link CvMappingRuleConditions }
     *
     */
    public CvMappingRuleConditions getCvMappingRuleConditions() {
        return cvMappingRuleConditions;
    }

    /**
     * Sets the value of the cvMappingRuleConditions property.
     *
     * @param value
* allowed object is
     *     {@link CvMappingRuleConditions }
     *
     */
    public void setCvMappingRuleConditions(CvMappingRuleConditions value) {
        this.cvMappingRuleConditions = value;
    }

    /**
     * Gets the value of the references property.
     *
     * @return
     * possible object is
     *     {@link References }
     *
     */
    public References getReferences() {
        return references;
    }

    /**
     * Sets the value of the references property.
     *
     * @param value
* allowed object is
     *     {@link References }
     *
     */
    public void setReferences(References value) {
        this.references = value;
    }

    /**
     * Gets the value of the mandatoryElements property.
     *
     * @return
     * possible object is
     *     {@link MandatoryElements }
     *
     */
    public MandatoryElements getMandatoryElements() {
        return mandatoryElements;
    }

    /**
     * Sets the value of the mandatoryElements property.
     *
     * @param value
* allowed object is
     *     {@link MandatoryElements }
     *
     */
    public void setMandatoryElements(MandatoryElements value) {
        this.mandatoryElements = value;
    }
}
