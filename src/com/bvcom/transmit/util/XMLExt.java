package com.bvcom.transmit.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.InvalidXPathException;


/**
 * XML�ڵ���"��д�࣬ ��װ����"Attribute)���ı��ڵ��"Text��Node)"br>
 * ��д���룬�û�ֻ""�ṩDocument���ú�xql���"
 * 
 * @author liyh
 * @since 2007-3-26
 * 
 */
public class XMLExt {

    private static Logger logger = Logger.getLogger(XMLExt.class.getSimpleName());

    /**
     * ��ָ��Document��ִ��ָ��xql��䣬��ȡĳ��"ֵ"<br>
     * ��xql���Ҫ���ѯ������������"/Msg/@MsgID"
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
            logger.info("info: ִ��xql���" + xql + "ʱ�޼�¼����");
            throw new InvalidXPathException("info: ִ��xql���" + xql + "ʱ�޼�¼����");
        }
    }

    /**
     * ��ָ��Document��ִ��ָ��xql��䣬��ȡ�������""br>
     * ��xql���Ҫ���ѯ������������"/Msg/IDQueryReport/TsItem/@ICCardNo"
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
            logger.info("info: ִ��xql���" + xql + "ʱ�޼�¼����");
            throw new InvalidXPathException("info: ִ��xql���" + xql + "ʱ�޼�¼����");
        }

    }

    /**
     * ��ָ��Document��ִ��ָ��xql��䣬��ȡĳ��""br>
     * ��xql���Ҫ���ѯ������������"/Msg/@MsgID"
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
            logger.info("info: ִ��xql���" + xql + "ʱ�޼�¼����");
            throw new InvalidXPathException("info: ִ��xql���" + xql + "ʱ�޼�¼����");
        }

    }

    /**
     * ����xql��䣬��ѯ�����"��xql���Ҫ���ѯ������������"/Msg/IDQueryReport/TsItem/@ICCardNo"
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
            logger.info("info: ִ��xql���" + xql + "ʱ�޼�¼����");
            throw new InvalidXPathException("info: ִ��xql���" + xql + "ʱ�޼�¼����");
        }

    }

    /**
     * ��ָ��Document��ִ��ָ��xql��䣬��ȡĳԪ��"br>
     * ��xql���Ҫ���ѯ������������"/Msg/Return"
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
            logger.info("ִ��xql���" + xql + "ʱ�޼�¼����");
            throw new InvalidXPathException("ִ��xql���" + xql + "ʱ�޼�¼����");
        }
    }

    /**
     * ��ָ��Document��ִ��ָ��xql��䣬��ȡ���Ԫ��"<br>
     * ��xql���Ҫ���ѯ������������"/Msg/IDQueryReport/TsItem"
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
            logger.info("ִ��xql���" + xql + "ʱ�޼�¼����");
            throw new InvalidXPathException("xmlPath ��������!");
        }

    }
    
    /**
     * ȡ��Element��ָ�����Ե�ֵ
     * @param el
     * @param strAttribute 
     * @return String ȡ�õ�ֵ
     * @throws InvalidXPathException
     * 
     * @author Bian Jiang
     * @since 2008.4.8
     */
    public static String getElementValue(Element el, String strAttribute) throws InvalidXPathException {
        String retStr = null;
        
        if (el == null || strAttribute == null || strAttribute.equals("")) {
            logger.info("Ԫ�ػ�����Ϊ��");
            throw new InvalidXPathException("Ԫ�ػ�����Ϊ��");
        }
        
        try {
            retStr = el.attribute(strAttribute).getValue();
        } catch (Exception ex) {
            logger.error("ȡ�����ݳ�����������Ϊ��" + el.asXML());
            throw new InvalidXPathException("ȡ�����ݳ�����������Ϊ " + el.asXML());
        }
        return retStr;
    }

}
