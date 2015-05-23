package com.bitants.common.kitset.xmlparser;

import java.util.List;
import java.util.Map;

/**
 * Element类
 */
public interface Element {

    /**
     * 获取标签名称
     * 
     * @return String 
     */
    public String getName();

    /**
     * 获取标签值
     * 
     * @return String 
     */
    public String getValue();

    /**
     * 获取标签属性值
     * 
     * @param attrName
     *            属性名称
     * @return String 
     * @throws NullPointerException
     */
    public String getAttribute(String attrName) throws NullPointerException;

    /**
     * 获取标签属性值
     * 
     * @param attrName
     *            属性名称
     * @param defValue
     *            默认值
     * @return String 
     */
    public String getAttribute(String attrName, String defValue);

    /**
     * 获取标签属性列表
     * 
     * @return Map 
     */
    public Map<String, String> getAttributes();

    /**
     * 获取标签父标签
     * 
     * @return Element 
     */
    public Element getParent();

    /**
     * 获取标签子标签列表
     * 
     * @return List 
     */
    public List<Element> getChildren();

    /**
     * 获取标签第一个子标签
     * 
     * @param eleName 标签名称
     * @return Element 
     */
    public Element getFirstChild(String eleName);

    /**
     * 是否有子标签
     * @return boolean
     */
    public boolean haveChildren();
}
