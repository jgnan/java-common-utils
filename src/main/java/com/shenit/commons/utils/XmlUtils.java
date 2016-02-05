/***********************************************************************************************************************
 * 
 * Copyright (C) 2013, 2014 by huanju (http://www.yy.com)
 * http://www.yy.com/
 * 
 *********************************************************************************************************************** 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 **********************************************************************************************************************/
package com.shenit.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML Utilities
 * 
 * @author jiangnan
 * 
 */
public final class XmlUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);
	private static final String XML_OPEN_LEFT = "<";
	private static final String XML_OPEN_RIGHT = ">";
	private static final String XML_CLOSE_LEFT = "</";

	/**
	 * POJO to xml string
	 * 
	 * @param obj
	 * @return
	 */
	public static String pojo2Xml(Object obj) {
		if (obj == null) return null;
		StringWriter writer = null;

		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marsh = context.createMarshaller();
			writer = new StringWriter();
			marsh.marshal(obj, writer);
			return writer.toString();
		} catch (JAXBException e) {
			if (LOG.isWarnEnabled()) LOG.warn("[toXmlStr] could not parse object[{}] to xml", obj, e);
		} finally {
			if (writer != null) try {
				writer.close();
			} catch (IOException e) {
				if (LOG.isWarnEnabled()) LOG.warn("[toXmlStr] could not close writer due to io exception", e);
			}
		}
		return null;
	}
	
	/**
	 * 简单的把Map对象的值转为XML
	 * @param root
	 * @param data
	 * @return
	 */
	public static String map2Xml(String root, Map<String,String> data){
    	StringBuilder builder = new StringBuilder();
    	builder.append(XML_OPEN_LEFT).append(root).append(XML_OPEN_RIGHT);
    	for(String key : data.keySet()){
    		builder.append(XML_OPEN_LEFT).append(key).append(XML_OPEN_RIGHT).append(data.get(key))
    		.append(XML_CLOSE_LEFT).append(key).append(XML_OPEN_RIGHT);
    	}
    	builder.append(XML_CLOSE_LEFT).append(root).append(XML_OPEN_RIGHT);
    	return builder.toString();
    }

	/**
	 * 把XML解释成Document对象
	 * @param xml
	 * @return
	 */
	public static Document parseXml(String xml) {
		ByteArrayInputStream is = null;
		try {
			is = new ByteArrayInputStream(xml.getBytes(HttpUtils.ENC_UTF8));
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			if (LOG.isWarnEnabled()) LOG.warn("[parseXml] Could not parse xml[{}] due to exception", new Object[] {xml,e});
        } finally {
			if (is != null) try {
	            is.close();
            } catch (IOException e) {
            	if (LOG.isWarnEnabled()) LOG.warn("[parseXml] Could not close input stream due to exception",e);
            }
		}
		return null;
	}
	
	/**
	 * 根据ID获取一个节点
	 * @param doc
	 * @param id
	 * @return
	 */
	public static Node byId(Document doc,String id){
		if(ValidationUtils.any(ValidationUtils.NULL_OR_EMPTY, doc,id)) return null;
		return doc.getElementById(id);
	}
	
	/**
	 * 根据标签名称获取节点
	 * @param doc
	 * @param name
	 * @return
	 */
	public static NodeList nodesByName(Document doc,String name){
		if(ValidationUtils.any(ValidationUtils.NULL_OR_EMPTY, doc,name)) return null;
		return doc.getElementsByTagName(name);
	}
	
	/**
	 * 根据标签名称获取节点
	 * @param doc
	 * @param name
	 * @return
	 */
	public static Node nodeByName(Document doc,String name){
		if(ValidationUtils.any(ValidationUtils.NULL_OR_EMPTY, doc,name)) return null;
		NodeList list = doc.getElementsByTagName(name);
		return list.getLength() > 0 ? list.item(0) : null;
	}
	
	/**
	 * 根据属性名获得属性节点
	 * @param node
	 * @param attr
	 * @return
	 */
	public static Node attr(Node node,String attr){
		if(ValidationUtils.any(ValidationUtils.NULL_OR_EMPTY, node,attr)) return null;
		return node.getAttributes().getNamedItem(attr);
	}
	
	/**
	 * 获取单个节点的值
	 * @param doc
	 * @param name
	 * @return
	 */
	public static String nodeVal(Document doc, String name){
		Node node = nodeByName(doc,name);
		return node != null ? node.getNodeValue() : null;
	}
	
	/**
	 * 获取多个节点的值，并且组合成一个数组
	 * @param doc
	 * @param name
	 * @return
	 */
	public static String[] nodeVals(Document doc, String name){
		NodeList nodes = nodesByName(doc,name);
		ArrayList<String> vals = new ArrayList<String>();
		int length = nodes.getLength();
		for(int i=0;i<length;i++){
			vals.add(nodes.item(i).getNodeValue());
		}
		return vals.toArray(new String[0]);
	}

	/**
	 * 把XML指定节点下的内容转换为一个Map
	 * @param xml
	 * @return
	 */
    public static Map<String, String> toMap(Node xml) {
    	if(xml == null) return null;
    	Map<String,String> map = new HashMap<String,String>();
    	NodeList children = xml.getChildNodes();
    	int length = children.getLength();
    	for(int i=0;i<length;i++){
    		Node node = children.item(i);
    		map.put(node.getNodeName(),node.getTextContent());
    	}
	    return map;
    }
	
}
