package com.sdkbox.live.gradle.manifest

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.w3c.dom.*

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory

class ModifyXML {

    private def manifestPath
    private def activities
    private def application
    private def appID
    private def appSec

    public ModifyXML(String path) {
        manifestPath = path
    }


    public void setAppID(String id) {
        appID = id
    }

    public void setAppSec(String sec) {
        appSec = sec
    }

    public void process() {
        def manifestOutFile = manifestPath
        def tagAppID = 'SDKBoxAppID'
        def tagAppSec = 'SDKBoxAppSec'

        def root = new XmlSlurper(false, false).parse(manifestOutFile)
                //.declareNamespace(android: "http://schemas.android.com/apk/res/android")
        [tagAppID, tagAppSec].each { String pattern ->
            def flavorName
            if (tagAppID == pattern) {
                flavorName = appID
            } else {
                flavorName = appSec
            }
            def metadata = root.application.'meta-data'
            def found = metadata.find { mt -> pattern == mt.'@android:name'.toString() }
            if (found.size() > 0) {
                found.replaceNode {
                    'meta-data'('android:name': found."@android:name", 'android:value': flavorName) {}
                }
            } else {
                root.application.appendNode {
                    'meta-data'('android:name': pattern, 'android:value': flavorName) {}
                }
            }
        }

        application = root.application."@android:name".toString()
        if (!application) {
            root.application."@android:name" = "com.sdkbox.live.app.SDKBoxApplication"
        }
        activities = root.application.activity*."@android:name"

        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield root },
                new FileWriter(manifestOutFile))
    }

    public void analysis() {
        def manifest = new File(manifestPath)
        if (!manifest.exists()) {
            return
        }

        def tagName = "android:name"
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestPath);
        Element root = document.getDocumentElement();

        Element appNode = (Element)selectSingleNode('/manifest/application', root)
        Attr attr = appNode.getAttributeNode(tagName)
        if (null == attr) {
            appNode.setAttribute(tagName, "com.sdkbox.live.app.SDKBoxApplication")
        } else {
            application = attr.getNodeValue()
        }

        NodeList activityNodes = selectNodes("/activity", appNode)
        for (Node n : activityNodes) {
            Element e = (Element)n
            attr = e.getAttributeNode(tagName)
            def actName = attr.getNodeValue()
            activities.add(actName)
        }

        saveXML(root, manifestPath)
    }

    private static void saveXML(Node node, String filename) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transFactory.newTransformer();
            // 设置各种输出属性
            transformer.setOutputProperty("encoding", "utf-8");
            transformer.setOutputProperty("indent", "yes");
            DOMSource source = new DOMSource();
            source.setNode(node);
            StreamResult result = new StreamResult();
            if (filename == null) {
                result.setOutputStream(System.out);
            } else {
                result.setOutputStream(new FileOutputStream(filename));
            }
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Node selectSingleNode(String expression, Object source) {
        try {
            return (Node) XPathFactory.newInstance().newXPath().evaluate(expression, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    private static NodeList selectNodes(String expression, Object source) {
        try {
            return (NodeList) XPathFactory.newInstance().newXPath().evaluate(expression, source, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    public String getAppName() {
        return application
    }

    public List<String> getActivityNames() {
        return activities
    }

}
