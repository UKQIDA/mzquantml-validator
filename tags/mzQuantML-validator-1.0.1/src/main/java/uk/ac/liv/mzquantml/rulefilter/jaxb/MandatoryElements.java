//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.02 at 05:09:26 PM CET 
//


package uk.ac.liv.mzquantml.rulefilter.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{}mandatoryMzIdentMLElement" maxOccurs="unbounded"/>
 *         &lt;element ref="{}mandatoryMzMLElement" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "mandatoryMzQuantMLElement"
})
@XmlRootElement(name = "mandatoryElements")
public class MandatoryElements {

    protected List<MandatoryMzQuantMLElement> mandatoryMzQuantMLElement;


    /**
     * Gets the value of the mandatoryMzIdentMLElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mandatoryMzIdentMLElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMandatoryMzIdentMLElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MandatoryMzQuantMLElement }
     * 
     * 
     */
    public List<MandatoryMzQuantMLElement> getMandatoryMzQuantMLElement() {
        if (mandatoryMzQuantMLElement == null) {
            mandatoryMzQuantMLElement = new ArrayList<MandatoryMzQuantMLElement>();
        }
        return this.mandatoryMzQuantMLElement;
    }



}
