package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    static String dirName = "";
    private static final String OLD_FILES_PATH = "/ZIP/СТАРЫЕ ФАЙЛЫ";

    public static void main(String[] args) throws IOException {
        toOldFiles();
        List<File> files = getXMLFiles();
        System.out.println();
        System.out.println("Исправление");
        System.out.println();
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getPath().endsWith(".xml")) {
                File currentFile = files.get(i);
                System.out.println("File is XML " + currentFile);
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

                dirName = str3;
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

                saveChanges(currentFile, doc, dirName);
            }
        }
        System.out.println("Очищение старых файлов");
        deleteOldFiles();
        System.out.println("Очищение папки ZIP");
        deleteZIPFiles();
        System.out.println("Программа завершилась успешно");
    }

    //очищение папки ZIP
    private static void deleteZIPFiles() {
        List<File>files = getZIPFiles();
        deleteFiles(files);
    }

    //функция для удаления списка файлов
    private static void deleteFiles(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
    }

    //удаление папки СТАРЫЕ ФАЙЛЫ вместе с содержимым
    private static void deleteOldFiles() {
        String path = Paths.get("").toAbsolutePath() + OLD_FILES_PATH;
        File dir = new File(path);
        File[] arrFiles = dir.listFiles();
        List<File> files = Arrays.asList(arrFiles);

        deleteFiles(files);
        dir.delete();
    }

    private static List<File> getXMLFiles() {
        String path = Paths.get("").toAbsolutePath() + OLD_FILES_PATH;
        File dir = new File(path);
        File[] arrFiles = dir.listFiles();
        List<File> files = Arrays.asList(arrFiles);
        return files;
    }

    private static List<File> getZIPFiles() {
        String path = Paths.get("ZIP").toAbsolutePath().toString();
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

    private static void saveChanges(File currentFile, Document doc, String dirName) {
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
//            showMessageDialog(null, "Ошибка сохранения в файл");
        }
        File newDir = new File(Paths.get(dirName) + "/"); //папка в которую помещаются новые файлы
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

    public static void toOldFiles() throws IOException {
        //создание папки СТАРЫЕ ФАЙЛЫ
        File oldDir = new File(Paths.get("ZIP/СТАРЫЕ ФАЙЛЫ") + "/");
        if (!oldDir.exists()){
            oldDir.mkdirs();
        }
        File mainDir = new File(Paths.get("").toAbsolutePath().toString());
        System.out.println(mainDir);

        System.out.println("Распаковка");
        //распаковка файлов из архивов
        List<File> zipFiles = getZIPFiles();
        for (int i = 0; i < zipFiles.size(); i++) {
            if (zipFiles.get(i).getPath().endsWith(".zip")) {
                File currentFile = zipFiles.get(i);
                System.out.println("File is ZIP " + currentFile);
                ZipInputStream zis = new ZipInputStream(new FileInputStream(currentFile.getAbsolutePath()));

                ZipEntry entry;
                while ((entry=zis.getNextEntry()) != null) {
                    if(entry.getName().equals("meta.xml") || entry.getName().equals("1/card.xml")) {
                        continue;
                    }
                    System.out.println(entry.getName());
                    String name = checkName(entry.getName()); //entry.getName().substring(2);
                    FileOutputStream fout = new FileOutputStream(oldDir + "/" + name);
                    for (int c = zis.read(); c != -1; c = zis.read()) {
                        fout.write(c);
                    }
                    fout.flush();
                    fout.close();
                }
                zis.close();

            }
        }
    }

    public static String checkName (String str) {
        String newName = str;
        if (str.contains("/")) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) != '/') {
                    newName = newName.substring(1);
                } else {
                    newName = newName.substring(1);
                    break;
                }
            }
        }
        return newName;
    }
}
