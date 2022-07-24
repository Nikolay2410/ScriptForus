package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<File> files = getFiles();
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getPath().endsWith(".xml")) {
                File currentFile = files.get(i);
                System.out.println("File is " + currentFile);
                Document doc = buildDoc(currentFile);
//                Node rootNode = doc.getFirstChild(); //Файл

                System.out.println("Было");
//                String str1 = rootNode.getAttributes().getNamedItem("ИдФайл").getTextContent();
//                System.out.println(str1); //ИдФайл

                NodeList secondNodeList = doc.getElementsByTagName("СвУчДокОбор");
                String str2 = secondNodeList.item(0).getAttributes().item(0).getTextContent();
                System.out.println(str2); //ИдОтпр
                String str3 = secondNodeList.item(0).getAttributes().item(1).getTextContent();
                System.out.println(str3); //ИдПол

                //Замена

//                str1 = myReplace(str1);
                str2 = myReplace(str2);
                str3 = myReplace(str3);
//                rootNode.getAttributes().getNamedItem("ИдФайл").setTextContent(str1);
                secondNodeList.item(0).getAttributes().item(0).setTextContent(str2);
                secondNodeList.item(0).getAttributes().item(1).setTextContent(str3);

                System.out.println("Стало");
//                str1 = rootNode.getAttributes().getNamedItem("ИдФайл").toString();
//                System.out.println(str1); //ИдФайл

                secondNodeList = doc.getElementsByTagName("СвУчДокОбор");
                str2 = secondNodeList.item(0).getAttributes().item(0).toString();
                System.out.println(str2); //ИдОтпр
                str3 = secondNodeList.item(0).getAttributes().item(1).toString();
                System.out.println(str3); //ИдПол
                System.out.println();

                saveChanges(currentFile, doc);
            }
        }
//        showMessageDialog(null, "Программа завершила выполнение успешно");
    }

    private static List<File> getFiles() {
        String path = Paths.get("").toAbsolutePath().toString();
        File dir = new File(path);
        File[] arrFiles = dir.listFiles();
        List<File> files = Arrays.asList(arrFiles);
        return files;
    }

    private static Document buildDoc(File currentFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            doc = dbf.newDocumentBuilder().parse(currentFile);
        } catch (Exception e) {
            System.out.println("Error is " + e.toString());
//            showMessageDialog(null, "Ошибка выполененися программы");
        }
        return doc;
    }

    private static void saveChanges(File currentFile, Document doc) {
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
//            showMessageDialog(null, "Ошибка сохранения в файл");
        }
        File newDir = new File(currentFile.getParent() + "/НОВЫЕ ФАЙЛЫ"); //папка в которую помещаются новые файлы
        if (!newDir.exists()){
            newDir.mkdirs();
        }
        File outFilePath = new File(newDir+ "/" + currentFile.getName());
        Result output = new StreamResult(outFilePath);
        Source input = new DOMSource(doc);

        try {
            transformer.transform(input, output);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static String myReplace(String str) {
        str = str.replace('_', '-');
        str = str.replace("\"", "");
        return str;
    }
}
