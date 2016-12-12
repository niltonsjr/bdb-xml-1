package com.bdbxml;

import com.sleepycat.dbxml.*;

public class BDBXML {
    XmlManager xmlManager = null;
    XmlContainer xmlContainer = null;

    public void createContainer() {
        try {
            xmlManager = new XmlManager();
            xmlManager.setDefaultContainerType
                    (XmlContainer.NodeContainer);
            xmlContainer = xmlManager.createContainer
                    ("catalog.dbxml");
        } catch (XmlException e) {
            System.out.println("XmlException" +
                    e.getMessage());
        } catch (java.io.FileNotFoundException e) {
            System.out.println("FileNotFoundException"
                    + e.getMessage());
        }

    }

    public void addDocument() {
        try {
            String docString = "<catalog title='Oracle Magazine' publisher='Oracle Publishing'>" +
                    "<journal date='March-April 2006'>" +
                    "<article>" +
                    "<title>Using Bind Variables</title>" +
                    "<author> Steve Muench </author>" +
                    "</article>" +
                    "</journal>" + "</catalog>";

            String docName = "catalog1";

            XmlUpdateContext updateContext =
                    xmlManager.createUpdateContext();
            xmlContainer.putDocument(docName,
                    docString, updateContext, null);

            docString = "<catalog title='Oracle Magazine' publisher='Oracle Publishing'>" +
                    "<journal date='May-June 2006'>" +
                    "<article>" +
                    "<title>From Application Express to XE</title>" +
                    "<author>David A. Kelly </author>" +
                    "</article>" +
                    "</journal>" + "</catalog>";
            docName = "catalog2";
            xmlContainer.putDocument(docName, docString, updateContext, null);
            XmlResults results = xmlContainer.getAllDocuments(null);
            while (results.hasNext()) {
                XmlValue xmlValue = results.next();
                System.out.println(xmlValue.asString());
            }
        }
        catch (XmlException e) {
            System.out.println("XmlException" +
                    e.getMessage());
        }
    }

    public void queryDocument() {
        try {
            XmlQueryContext context =
                    xmlManager.createQueryContext();

            String query = "collection ('catalog.dbxml')/catalog/journal/article/title/text()";
            XmlQueryExpression qe = xmlManager.prepare(query, context);
            XmlResults results = qe.execute(context);

            while (results.hasNext()) {
                XmlValue xmlValue = results.next();
                System.out.println(xmlValue.asString());
            }
        } catch (XmlException e) {
            System.out.println("XmlException" +
                    e.getMessage());
        }
    }

    public void modifyDocument() {
        try {
            XmlQueryContext qc = xmlManager.createQueryContext();
            XmlUpdateContext uc = xmlManager.createUpdateContext();
            XmlModify mod = xmlManager.createModify();
            XmlQueryExpression select = xmlManager.prepare("/catalog/journal/article", qc);
            mod.addAppendStep(select, XmlModify.Attribute, "section", "Developer");

            String objectContent = "<article>" +
                    "<title>XML in Databases </title>" +
                    "<author>Ari Kaplan </author>" +
                    "</article>";

            select = xmlManager.prepare("/catalog/journal[article/title='Using Bind Variables']", qc);
            mod.addInsertAfterStep(select, XmlModify.Element, "journal",objectContent);

            XmlDocument xmlDocument = xmlContainer.getDocument("catalog1");
            XmlValue xmlValue = new XmlValue(xmlDocument);
            mod.execute(xmlValue, qc, uc);

            System.out.println("XML Documents after modification");
            XmlResults results = xmlContainer.getAllDocuments(null);

            while (results.hasNext()) {
                xmlValue = results.next();
                System.out.println(xmlValue.asString());
            }
        } catch (XmlException e) {
            System.out.println("XmlException" +
                    e.getMessage());
        }
    }

    public void updateDocument() {
        try {
            XmlQueryContext qc = xmlManager.createQueryContext();
            XmlUpdateContext uc = xmlManager.createUpdateContext();
            XmlModify mod = xmlManager.createModify();

            String updateContent = "Introduction to  Bind Variables";
            XmlQueryExpression select = xmlManager.prepare("/catalog/journal/article[title='Using Bind Variables']/title/text()" qc);
            mod.addUpdateStep(select, updateContent);

            select = xmlManager.prepare("/catalog/ journal", qc);
            mod.addRenameStep(select, "magazine");

            select = xmlManager.prepare("/catalog/magazine[2]", qc);
            mod.addRemoveStep(select);

            System.out.println("XML Documents after Update");

            XmlDocument xmlDocument = xmlContainer.getDocument("catalog1");
            XmlValue xmlValue = new XmlValue(xmlDocument);
            mod.execute(xmlValue, qc, uc);

            XmlResults results = xmlContainer.getAllDocuments(null);

            while (results.hasNext()) {
                xmlValue = results.next();
                System.out.println(xmlValue.asString());
            }
        } catch (XmlException e) {
            System.out.println("XmlException" +
                    e.getMessage());
        }
    }

    public static void main(String[] argv) {
        BDBXML bdbXML = new BDBXML();

        bdbXML.createContainer();
        bdbXML.addDocument();
        bdbXML.queryDocument();
        bdbXML.modifyDocument();
        bdbXML.updateDocument();
    }
}