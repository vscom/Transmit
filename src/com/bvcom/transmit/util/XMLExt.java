package com.bvcom.transmit.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.InvalidXPathException;


/**
 * XML节点属"读写类， 封装对属"Attribute)或文本节点标"Text型Node)"br>
 * 读写代码，用户只""提供Document引用和xql语句"
 * 
 * @author liyh
 * @since 2007-3-26
 * 
 */
public class XMLExt {

    private static Logger logger = Logger.getLogger(XMLExt.class.getSimpleName());

    /**
     * 在指定Document中执行指定xql语句，读取某属"值"<br>
     * 该xql语句要求查询到属性名，例"/Msg/@MsgID"
     * 
     * @param xql
     * @param doc
     * @return String
     * @throws XmlPathExcption
     */
    public static String getAttributeValue(String xql, Document doc) throws InvalidXPathException {
        List resultList = doc.selectNodes(xql);
        if (!resultList.isEmpty() && resultList.size() != 0) {
            return ((Attribute) resultList.get(0)).getValue();
        } else {
            logger.info("info: 执行xql语句" + xql + "时无记录返回");
            throw new InvalidXPathException("info: 执行xql语句" + xql + "时无记录返回");
        }
    }

    /**
     * 在指定Document中执行指定xql语句，读取多个属性""br>
     * 该xql语句要求查询到属性名，例"/Msg/IDQueryReport/TsItem/@ICCardNo"
     * 
     * @param xql
     * @param doc
     * @return List
     * @throws XmlPathExcption
     */
    public static List getMultiAttributeValue(String xql, Document doc)
            throws InvalidXPathException {
        List returnList = new ArrayList();
        List resultList = doc.selectNodes(xql);
        String attrValue = "";
        if (!resultList.isEmpty() && resultList.size() != 0) {
            for (int i = 0; i < resultList.size(); i++) {
                attrValue = ((Attribute) resultList.get(0)).getValue();
                returnList.add(attrValue);
            }
            return returnList;
        } else {
            logger.info("info: 执行xql语句" + xql + "时无记录返回");
            throw new InvalidXPathException("info: 执行xql语句" + xql + "时无记录返回");
        }

    }

    /**
     * 在指定Document中执行指定xql语句，读取某属""br>
     * 该xql语句要求查询到属性名，例"/Msg/@MsgID"
     * 
     * @param xql
     * @param doc
     * @return Attribute
     * @throws XmlPathExcption
     */
    public static Attribute getAttribute(String xql, Document doc)
            throws InvalidXPathException {

        List resultList = doc.selectNodes(xql);
        if (!resultList.isEmpty() && resultList.size() != 0) {
            return (Attribute) resultList.get(0);
        } else {
            logger.info("info: 执行xql语句" + xql + "时无记录返回");
            throw new InvalidXPathException("info: 执行xql语句" + xql + "时无记录返回");
        }

    }

    /**
     * 根据xql语句，查询多个属"该xql语句要求查询到属性名，例"/Msg/IDQueryReport/TsItem/@ICCardNo"
     * 
     * @param xql
     * @param doc
     * @return List
     * @throws XmlPathExcption
     */
    public static List getMultiAttribute(String xql, Document doc)
            throws InvalidXPathException {

        List returnList = new ArrayList();
        List resultList = doc.selectNodes(xql);

        Attribute attr = null;
        if (!resultList.isEmpty() && resultList.size() != 0) {
            for (int i = 0; i < resultList.size(); i++) {
                attr = (Attribute) resultList.get(0);
                returnList.add(attr);
            }
            return returnList;
        } else {
            logger.info("info: 执行xql语句" + xql + "时无记录返回");
            throw new InvalidXPathException("info: 执行xql语句" + xql + "时无记录返回");
        }

    }

    /**
     * 在指定Document中执行指定xql语句，读取某元素"br>
     * 该xql语句要求查询到属性名，例"/Msg/Return"
     * 
     * @param xql
     * @param doc
     * @return Element
     * @exception Exception
     */
    public static Element getElement(String xql, Document doc) throws InvalidXPathException {
        List resultList = doc.selectNodes(xql);
        if(resultList.size()==0) return null;
        if (!resultList.isEmpty() && resultList.size() != 0) {
            return (Element) resultList.get(0);
        } else {
            logger.info("执行xql语句" + xql + "时无记录返回");
            throw new InvalidXPathException("执行xql语句" + xql + "时无记录返回");
        }
    }

    /**
     * 在指定Document中执行指定xql语句，读取多个元素"<br>
     * 该xql语句要求查询到属性名，例"/Msg/IDQueryReport/TsItem"
     * 
     * @param xql
     * @param doc
     * @return List
     * @throws XmlPathExcption
     */
    public static List getMultiElement(String xql, Document doc)
            throws InvalidXPathException {
        List list = new ArrayList();
        List resultList = doc.selectNodes(xql);

        Element element = null;
        if (!resultList.isEmpty() && resultList.size() != 0) {
            for (int i = 0; i < resultList.size(); i++) {
                element = (Element) resultList.get(i);
                
                list.add(element);
            }
            return list;
        } else {
            logger.info("执行xql语句" + xql + "时无记录返回");
            throw new InvalidXPathException("xmlPath 可能有误!");
        }

    }
    
    /**
     * 取得Element中指定属性的值
     * @param el
     * @param strAttribute 
     * @return String 取得的值
     * @throws InvalidXPathException
     * 
     * @author Bian Jiang
     * @since 2008.4.8
     */
    public static String getElementValue(Element el, String strAttribute) throws InvalidXPathException {
        String retStr = null;
        
        if (el == null || strAttribute == null || strAttribute.equals("")) {
            logger.info("元素或属性为空");
            throw new InvalidXPathException("元素或属性为空");
        }
        
        try {
            retStr = el.attribute(strAttribute).getValue();
        } catch (Exception ex) {
            logger.error("取得数据出错，该行数据为：" + el.asXML());
            throw new InvalidXPathException("取得数据出错，该行数据为 " + el.asXML());
        }
        return retStr;
    }

}
