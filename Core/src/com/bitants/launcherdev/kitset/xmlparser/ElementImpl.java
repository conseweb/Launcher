package com.bitants.launcherdev.kitset.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Element
 */
public class ElementImpl implements Element {

    protected String name;

    protected String value;

    protected Map<String, String> attributes;

    protected Element parent;

    protected List<Element> children;

    public ElementImpl() {
        attributes = new HashMap<String, String>();
        children = new ArrayList<Element>();
    }

    @Override
    public Element getParent() {
        return parent;
    }

    @Override
    public Element getFirstChild(String childName) {
        for (Element e : children) {
            if (e.getName().equals(childName)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String getAttribute(String attrName) throws NullPointerException {
        String attributeValue = attributes.get(attrName);
        if (attributeValue == null) {
            throw new NullPointerException();
        }
        return attributeValue;
    }

    @Override
    public String getAttribute(String attrName, String defValue) {
        String attributeValue = attributes.get(attrName);
        if (attributeValue == null) {
            return defValue;
        }
        return attributeValue;
    }

    @Override
    public List<Element> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }

    /**
     * 是否有子节点
     * @return boolean
     */
    public boolean haveChildren() {
        return null == getChildren() || getChildren().isEmpty() ? false : true;
    }

}
