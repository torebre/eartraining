//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.15 at 06:13:08 PM CEST 
//


package musicxml30;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for on-off.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="on-off">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="on"/>
 *     &lt;enumeration value="off"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "on-off")
@XmlEnum
public enum OnOff {

    @XmlEnumValue("on")
    ON("on"),
    @XmlEnumValue("off")
    OFF("off");
    private final java.lang.String value;

    OnOff(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static OnOff fromValue(java.lang.String v) {
        for (OnOff c: OnOff.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
