//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.05.27 at 03:05:42 PM BRT
//

package io.quarkus.it.jaxb;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ExtensionOfBaseObj complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExtensionOfBaseObj"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://acme.org/beans}BaseObj"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="moreZeep" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtensionOfBaseObj", propOrder = {
        "moreZeep"
})
@XmlSeeAlso({
        Response.class
})
public class ExtensionOfBaseObj
        extends BaseObj {

    protected int moreZeep;

    /**
     * Gets the value of the moreZeep property.
     *
     */
    public int getMoreZeep() {
        return moreZeep;
    }

    /**
     * Sets the value of the moreZeep property.
     *
     */
    public void setMoreZeep(int value) {
        this.moreZeep = value;
    }

}
