/*--

 Copyright (C) 2000-2007 Jason Hunter & Brett McLaughlin.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.

 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.

 In addition, we request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many
 individuals on behalf of the JDOM Project and was originally
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.

 */

package org.jdom2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * An XML attribute. Methods allow the user to obtain the value of the attribute
 * as well as namespace and type information.
 *
 * @author  Brett McLaughlin
 * @author  Jason Hunter
 * @author  Elliotte Rusty Harold
 * @author  Wesley Biggs
 * @author  Victor Toni
 */
public class Attribute extends Content implements Serializable, Cloneable {

	// Keep the old constant names for one beta cycle to help migration
	
	/**
	 * @see AttributeType#UNDECLARED
	 */
	public final static AttributeType UNDECLARED_TYPE = AttributeType.UNDECLARED;

	/**
	 * @see AttributeType#CDATA
	 */
	public final static AttributeType CDATA_TYPE = AttributeType.CDATA;

	/**
	 * @see AttributeType#ID
	 */
	public final static AttributeType ID_TYPE = AttributeType.ID;

	/**
	 * @see AttributeType#IDREF
	 */
	public final static AttributeType IDREF_TYPE = AttributeType.IDREF;

	/**
	 * @see AttributeType#IDREFS
	 */
	public final static AttributeType IDREFS_TYPE = AttributeType.IDREFS;

	/**
	 * @see AttributeType#ENTITY
	 */
	public final static AttributeType ENTITY_TYPE = AttributeType.ENTITY;

	/**
	 * @see AttributeType#ENTITIES
	 */
	public final static AttributeType ENTITIES_TYPE = AttributeType.ENTITIES;

	/**
	 * @see AttributeType#NMTOKEN
	 */
	public final static AttributeType NMTOKEN_TYPE = AttributeType.NMTOKEN;

	/**
	 * @see AttributeType#NMTOKENS
	 */
	public final static AttributeType NMTOKENS_TYPE = AttributeType.NMTOKENS;

	/**
	 * @see AttributeType#NOTATION
	 */
	public final static AttributeType NOTATION_TYPE = AttributeType.NOTATION;

	/**
	 * @see AttributeType#ENUMERATION
	 */
	public final static AttributeType ENUMERATED_TYPE = AttributeType.ENUMERATION;




	/** The local name of the <code>Attribute</code> */
	protected String name;

	/** The <code>{@link Namespace}</code> of the <code>Attribute</code> */
	protected transient Namespace namespace;

	/** The value of the <code>Attribute</code> */
	protected String value;

	/** The type of the <code>Attribute</code> */
	protected AttributeType type = AttributeType.UNDECLARED;

	/**
	 * Default, no-args constructor for implementations to use if needed.
	 */
	protected Attribute() {
		super(CType.Attribute);
	}

	/**
	 * This will create a new <code>Attribute</code> with the
	 * specified (local) name and value, and in the provided
	 * <code>{@link Namespace}</code>.
	 *
	 * @param name <code>String</code> name of <code>Attribute</code>.
	 * @param value <code>String</code> value for new attribute.
	 * @param namespace <code>Namespace</code> namespace for new attribute.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name or if if the new namespace is the default
	 *         namespace. Attributes cannot be in a default namespace.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}).
	 */
	public Attribute(final String name, final String value, final Namespace namespace) {
		this(name, value, AttributeType.UNDECLARED, namespace);
	}

	/**
	 * This will create a new <code>Attribute</code> with the
	 * specified (local) name, value, and type, and in the provided
	 * <code>{@link Namespace}</code>.
	 *
	 * @param name <code>String</code> name of <code>Attribute</code>.
	 * @param value <code>String</code> value for new attribute.
	 * @param type <code>int</code> type for new attribute.
	 * @param namespace <code>Namespace</code> namespace for new attribute.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name or if if the new namespace is the default
	 *         namespace. Attributes cannot be in a default namespace.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}) or
	 *         if the given attribute type is not one of the
	 *         supported types.
	 * @deprecated Use Constructor with AttributeType type, not int type.
	 */
	@Deprecated
	public Attribute(final String name, final String value, final int type, final Namespace namespace) {
		this(name, value, AttributeType.byIndex(type), namespace);
	}
	
	/**
	 * This will create a new <code>Attribute</code> with the
	 * specified (local) name, value, and type, and in the provided
	 * <code>{@link Namespace}</code>.
	 *
	 * @param name <code>String</code> name of <code>Attribute</code>.
	 * @param value <code>String</code> value for new attribute.
	 * @param type <code>int</code> type for new attribute.
	 * @param namespace <code>Namespace</code> namespace for new attribute.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name or if if the new namespace is the default
	 *         namespace. Attributes cannot be in a default namespace.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}) or
	 *         if the given attribute type is not one of the
	 *         supported types.
	 */
	public Attribute(final String name, final String value, final AttributeType type, final Namespace namespace) {
		super(CType.Attribute);
		setName(name);
		setValue(value);
		setAttributeType(type);
		setNamespace(namespace);
	}

	/**
	 * This will create a new <code>Attribute</code> with the
	 * specified (local) name and value, and does not place
	 * the attribute in a <code>{@link Namespace}</code>.
	 * <p>
	 * <b>Note</b>: This actually explicitly puts the
	 * <code>Attribute</code> in the "empty" <code>Namespace</code>
	 * (<code>{@link Namespace#NO_NAMESPACE}</code>).
	 *
	 * @param name <code>String</code> name of <code>Attribute</code>.
	 * @param value <code>String</code> value for new attribute.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}).
	 */
	public Attribute(final String name, final String value) {
		this(name, value, AttributeType.UNDECLARED, Namespace.NO_NAMESPACE);
	}

	/**
	 * This will create a new <code>Attribute</code> with the
	 * specified (local) name, value and type, and does not place
	 * the attribute in a <code>{@link Namespace}</code>.
	 * <p>
	 * <b>Note</b>: This actually explicitly puts the
	 * <code>Attribute</code> in the "empty" <code>Namespace</code>
	 * (<code>{@link Namespace#NO_NAMESPACE}</code>).
	 *
	 * @param name <code>String</code> name of <code>Attribute</code>.
	 * @param value <code>String</code> value for new attribute.
	 * @param type <code>int</code> type for new attribute.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}) or
	 *         if the given attribute type is not one of the
	 *         supported types.
	 */
	public Attribute(final String name, final String value, final AttributeType type) {
		this(name, value, type, Namespace.NO_NAMESPACE);
	}

	/**
	 * This will create a new <code>Attribute</code> with the
	 * specified (local) name, value and type, and does not place
	 * the attribute in a <code>{@link Namespace}</code>.
	 * <p>
	 * <b>Note</b>: This actually explicitly puts the
	 * <code>Attribute</code> in the "empty" <code>Namespace</code>
	 * (<code>{@link Namespace#NO_NAMESPACE}</code>).
	 *
	 * @param name <code>String</code> name of <code>Attribute</code>.
	 * @param value <code>String</code> value for new attribute.
	 * @param type <code>int</code> type for new attribute.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}) or
	 *         if the given attribute type is not one of the
	 *         supported types.
	 * @deprecated Use {@link #Attribute(String, String, AttributeType)}
	 */
	@Deprecated
	public Attribute(final String name, final String value, final int type) {
		this(name, value, type, Namespace.NO_NAMESPACE);
	}

	/**
	 * This will return the parent of this <code>Attribute</code>.
	 * If there is no parent, then this returns <code>null</code>.
	 * Use return-type covariance to override Content's getParent() method
	 * to return an Element, not just a Parent
	 *
	 * @return parent of this <code>Attribute</code>
	 */
	@Override
	public Element getParent() {
		return (Element)super.getParent();
	}
	
	/**
	 * This will retrieve the local name of the
	 * <code>Attribute</code>. For any XML attribute
	 * which appears as
	 * <code>[namespacePrefix]:[attributeName]</code>,
	 * the local name of the attribute would be
	 * <code>[attributeName]</code>. When the attribute
	 * has no namespace, the local name is simply the attribute
	 * name.
	 * <p>
	 * To obtain the namespace prefix for this
	 * attribute, the
	 * <code>{@link #getNamespacePrefix()}</code>
	 * method should be used.
	 *
	 * @return <code>String</code> - name of this attribute,
	 *                               without any namespace prefix.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This sets the local name of the <code>Attribute</code>.
	 *
	 * @param name the new local name to set
	 * @return <code>Attribute</code> - the attribute modified.
	 * @throws IllegalNameException if the given name is illegal as an
	 *         attribute name.
	 */
	public Attribute setName(final String name) {
		if (name == null) {
			throw new NullPointerException(
					"Can not set a null name for an Attribute.");
		}
		final String reason  = Verifier.checkAttributeName(name);
		if (reason != null) {
			throw new IllegalNameException(name, "attribute", reason);
		}
		this.name = name;
		return this;
	}

	/**
	 * This will retrieve the qualified name of the <code>Attribute</code>.
	 * For any XML attribute whose name is
	 * <code>[namespacePrefix]:[elementName]</code>,
	 * the qualified name of the attribute would be
	 * everything (both namespace prefix and
	 * element name). When the attribute has no
	 * namespace, the qualified name is simply the attribute's
	 * local name.
	 * <p>
	 * To obtain the local name of the attribute, the
	 * <code>{@link #getName()}</code> method should be used.
	 * <p>
	 * To obtain the namespace prefix for this attribute,
	 * the <code>{@link #getNamespacePrefix()}</code>
	 * method should be used.
	 *
	 * @return <code>String</code> - full name for this element.
	 */
	public String getQualifiedName() {
		// Note: Any changes here should be reflected in
		// XMLOutputter.printQualifiedName()
		final String prefix = namespace.getPrefix();

		// no prefix found
		if ((prefix == null) || ("".equals(prefix))) {
			return getName();
		}
		return new StringBuilder(prefix)
		.append(':')
		.append(getName())
		.toString();
	}

	/**
	 * This will retrieve the namespace prefix of the
	 * <code>Attribute</code>. For any XML attribute
	 * which appears as
	 * <code>[namespacePrefix]:[attributeName]</code>,
	 * the namespace prefix of the attribute would be
	 * <code>[namespacePrefix]</code>. When the attribute
	 * has no namespace, an empty <code>String</code> is returned.
	 *
	 * @return <code>String</code> - namespace prefix of this
	 *                               attribute.
	 */
	public String getNamespacePrefix() {
		return namespace.getPrefix();
	}

	/**
	 * This returns the URI mapped to this <code>Attribute</code>'s
	 * prefix. If no mapping is found, an empty <code>String</code> is
	 * returned.
	 *
	 * @return <code>String</code> - namespace URI for this <code>Attribute</code>.
	 */
	public String getNamespaceURI() {
		return namespace.getURI();
	}

	/**
	 * This will return this <code>Attribute</code>'s
	 * <code>{@link Namespace}</code>.
	 *
	 * @return <code>Namespace</code> - Namespace object for this <code>Attribute</code>
	 */
	public Namespace getNamespace() {
		return namespace;
	}

	/**
	 * This sets this <code>Attribute</code>'s <code>{@link Namespace}</code>.
	 * If the provided namespace is null, the attribute will have no namespace.
	 * The namespace must have a prefix.
	 *
	 * @param namespace the new namespace
	 * @return <code>Element</code> - the element modified.
	 * @throws IllegalNameException if the new namespace is the default
	 *         namespace. Attributes cannot be in a default namespace.
	 */
	public Attribute setNamespace(Namespace namespace) {
		if (namespace == null) {
			namespace = Namespace.NO_NAMESPACE;
		}

		// Verify the attribute isn't trying to be in a default namespace
		// Attributes can't be in a default namespace
		if (namespace != Namespace.NO_NAMESPACE &&
				"".equals(namespace.getPrefix())) {
			throw new IllegalNameException("", "attribute namespace",
					"An attribute namespace without a prefix can only be the " +
					"NO_NAMESPACE namespace");
		}
		this.namespace = namespace;
		return this;
	}

	/**
	 * This will return the actual textual value of this
	 * <code>Attribute</code>.  This will include all text
	 * within the quotation marks.
	 *
	 * @return <code>String</code> - value for this attribute.
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * This will set the value of the <code>Attribute</code>.
	 *
	 * @param value <code>String</code> value for the attribute.
	 * @return <code>Attribute</code> - this Attribute modified.
	 * @throws IllegalDataException if the given attribute value is
	 *         illegal character data (as determined by
	 *         {@link org.jdom2.Verifier#checkCharacterData}).
	 */
	public Attribute setValue(final String value) {
		if (value == null) {
			throw new NullPointerException(
					"Can not set a null value for an Attribute");
		}
		final String reason = Verifier.checkCharacterData(value);
		if (reason != null) {
			throw new IllegalDataException(value, "attribute", reason);
		}
		this.value = value;
		return this;
	}

	/**
	 * This will return the actual declared type of this
	 * <code>Attribute</code>.
	 *
	 * @return <code>int</code> - type for this attribute.
	 */
	public AttributeType getAttributeType() {
		return type;
	}

	/**
	 * This will set the type of the <code>Attribute</code>.
	 *
	 * @param type <code>int</code> type for the attribute.
	 * @return <code>Attribute</code> - this Attribute modified.
	 * @throws IllegalDataException if the given attribute type is
	 *         not one of the supported types.
	 */
	public Attribute setAttributeType(final AttributeType type) {
		this.type = type == null ? AttributeType.UNDECLARED : type;
		return this;
	}

	/**
	 * This will set the type of the <code>Attribute</code>.
	 *
	 * @param type <code>int</code> type for the attribute.
	 * @return <code>Attribute</code> - this Attribute modified.
	 * @throws IllegalDataException if the given attribute type is
	 *         not one of the supported types.
	 * @deprecated use {@link #setAttributeType(AttributeType)}
	 */
	@Deprecated
	public Attribute setAttributeType(final int type) {
		setAttributeType(AttributeType.byIndex(type));
		return this;
	}
	
	/**
	 * This returns a <code>String</code> representation of the
	 * <code>Attribute</code>, suitable for debugging.
	 *
	 * @return <code>String</code> - information about the
	 *         <code>Attribute</code>
	 */
	@Override
	public String toString() {
		return new StringBuilder()
		.append("[Attribute: ")
		.append(getQualifiedName())
		.append("=\"")
		.append(value)
		.append("\"")
		.append("]")
		.toString();
	}

	@Override
	public Attribute clone() {
		return (Attribute) super.clone();
	}

	@Override
	public Attribute detach() {
		return (Attribute)super.detach();
	}

	@Override
	protected Attribute setParent(Parent parent) {
		return (Attribute)super.setParent(parent);
	}

	
	/////////////////////////////////////////////////////////////////
	// Convenience Methods below here
	/////////////////////////////////////////////////////////////////

	/**
	 * This gets the value of the attribute, in
	 * <code>int</code> form, and if no conversion
	 * can occur, throws a
	 * <code>{@link DataConversionException}</code>
	 *
	 * @return <code>int</code> value of attribute.
	 * @throws DataConversionException when conversion fails.
	 */
	public int getIntValue() throws DataConversionException {
		try {
			return Integer.parseInt(value.trim());
		} catch (final NumberFormatException e) {
			throw new DataConversionException(name, "int");
		}
	}

	/**
	 * This gets the value of the attribute, in
	 * <code>long</code> form, and if no conversion
	 * can occur, throws a
	 * <code>{@link DataConversionException}</code>
	 *
	 * @return <code>long</code> value of attribute.
	 * @throws DataConversionException when conversion fails.
	 */
	public long getLongValue() throws DataConversionException {
		try {
			return Long.parseLong(value.trim());
		} catch (final NumberFormatException e) {
			throw new DataConversionException(name, "long");
		}
	}

	/**
	 * This gets the value of the attribute, in
	 * <code>float</code> form, and if no conversion
	 * can occur, throws a
	 * <code>{@link DataConversionException}</code>
	 *
	 * @return <code>float</code> value of attribute.
	 * @throws DataConversionException when conversion fails.
	 */
	public float getFloatValue() throws DataConversionException {
		try {
			// Avoid Float.parseFloat() to support JDK 1.1
			return Float.valueOf(value.trim()).floatValue();
		} catch (final NumberFormatException e) {
			throw new DataConversionException(name, "float");
		}
	}

	/**
	 * This gets the value of the attribute, in
	 * <code>double</code> form, and if no conversion
	 * can occur, throws a
	 * <code>{@link DataConversionException}</code>
	 *
	 * @return <code>double</code> value of attribute.
	 * @throws DataConversionException when conversion fails.
	 */
	public double getDoubleValue() throws DataConversionException {
		try {
			// Avoid Double.parseDouble() to support JDK 1.1
			return Double.valueOf(value.trim()).doubleValue();
		} catch (final NumberFormatException e) {
			// Specially handle INF and -INF that Double.valueOf doesn't do
			String v = value.trim();
			if ("INF".equals(v)) {
				return Double.POSITIVE_INFINITY;
			}
			if ("-INF".equals(v)) {
				return Double.NEGATIVE_INFINITY;
			}
			throw new DataConversionException(name, "double");
		}
	}

	/**
	 * This gets the effective boolean value of the attribute, or throws a
	 * <code>{@link DataConversionException}</code> if a conversion can't be
	 * performed.  True values are: "true", "on", "1", and "yes".  False
	 * values are: "false", "off", "0", and "no".  Values are trimmed before
	 * comparison.  Values other than those listed here throw the exception.
	 *
	 * @return <code>boolean</code> value of attribute.
	 * @throws DataConversionException when conversion fails.
	 */
	public boolean getBooleanValue() throws DataConversionException {
		final String valueTrim = value.trim();
		if (
				(valueTrim.equalsIgnoreCase("true")) ||
				(valueTrim.equalsIgnoreCase("on")) ||
				(valueTrim.equalsIgnoreCase("1")) ||
				(valueTrim.equalsIgnoreCase("yes"))) {
			return true;
		} else if (
				(valueTrim.equalsIgnoreCase("false")) ||
				(valueTrim.equalsIgnoreCase("off")) ||
				(valueTrim.equalsIgnoreCase("0")) ||
				(valueTrim.equalsIgnoreCase("no"))
				) {
			return false;
		} else {
			throw new DataConversionException(name, "boolean");
		}
	}

	// Support a custom Namespace serialization so no two namespace
	// object instances may exist for the same prefix/uri pair
	private void writeObject(final ObjectOutputStream out) throws IOException {

		out.defaultWriteObject();

		// We use writeObject() and not writeUTF() to minimize space
		// This allows for writing pointers to already written strings
		out.writeObject(namespace.getPrefix());
		out.writeObject(namespace.getURI());
	}

	private void readObject(final ObjectInputStream in)
			throws IOException, ClassNotFoundException {

		in.defaultReadObject();

		namespace = Namespace.getNamespace(
				(String) in.readObject(), (String) in.readObject());
	}

	@Override
	public List<Namespace> getNamespacesInScope() {
		if (getParent() == null) {
			ArrayList<Namespace> ret = new ArrayList<Namespace>(3);
			ret.add(getNamespace());
			ret.add(Namespace.XML_NAMESPACE);
			return Collections.unmodifiableList(ret);
		}
		return orderFirst(getNamespace(), getParent().getNamespacesInScope());
	}

	@Override
	public List<Namespace> getNamespacesIntroduced() {
		if (getParent() == null) {
			return Collections.singletonList(getNamespace());
		}
		return Collections.emptyList();
	}

	@Override
	public List<Namespace> getNamespacesInherited() {
		if (getParent() == null) {
			return Collections.singletonList(Namespace.XML_NAMESPACE);
		}
		return orderFirst(getNamespace(), getParent().getNamespacesInScope());
	}


	private static final List<Namespace> orderFirst(final Namespace nsa, List<Namespace> nsl) {
		if (nsl.get(0) == nsa) {
			return nsl;
		}
		// OK, we have our namespace list, but our's is not the first.
		// we need the Attribute's Namespace to be up front.
		TreeMap<String,Namespace> tm = new TreeMap<String, Namespace>();
		for (Namespace ns : nsl) {
			if (ns != nsa) {
				tm.put(ns.getPrefix(), ns);
			}
		}
		ArrayList<Namespace> ret = new ArrayList<Namespace>(tm.size() + 1);
		ret.add(nsa);
		ret.addAll(tm.values());
		return Collections.unmodifiableList(ret);
	}

}
