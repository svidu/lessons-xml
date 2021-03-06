package datamanager;

import com.company.People;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * Created by root on 10.02.17.
 */
public class DataManager {


    public static void serialize(Object obj) throws Exception {
        String value;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element element = doc.createElement("Object");
        element.setAttribute("type", obj.getClass().getSimpleName());
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Element element1 = doc.createElement("fields");
            element1.setAttribute("type", field.getType().getSimpleName());
            element1.setAttribute("id", field.getName());
            element1.setAttribute("value", value = (field.get(obj) !=null ? field.get(obj).toString() : null));
            element.appendChild(element1);
        }
        doc.appendChild(element);

        StreamResult result = new StreamResult(new File("test.xml"));
        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        StreamResult consoleResult = new StreamResult(System.out);
    }

    public static void serializeCollection(Collection<Object> collection) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element rootElement = doc.createElement("Collection");
        rootElement.setAttribute("type", Object.class.getSimpleName());
        for (Object obj : collection) {
            Element element = element = doc.createElement("Object");
            element.setAttribute("type", Object.class.getSimpleName());
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Element element1 = doc.createElement("fields");
                element1.setAttribute("type", field.getType().getSimpleName());
                element1.setAttribute("id", field.getName());
                element1.setAttribute("value", field.get(obj).toString());
                element.appendChild(element1);
            }
            rootElement.appendChild(element);
        }
        doc.appendChild(rootElement);

        StreamResult result = new StreamResult(new File("testCollection.xml"));
        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }


    public static People deserialize(String path) throws Exception { //передавать Class.class

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(path));

        Class cls = Class.forName("com.company.People");
        People people = (People) cls.newInstance();

        Field[] fields = people.getClass().getDeclaredFields();
        Map<String,Field> fieldsMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            fieldsMap.put(field.getName(),field);
        }

        NodeList nodeList = doc.getElementsByTagName("Object").item(0).getChildNodes();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() !=  Node.TEXT_NODE) {
                list.add(nodeList.item(i));
            }
        }

        for (int i = 0; i < list.size(); i++) {
            String id    = list.get(i).getAttributes().getNamedItem("id").getNodeValue();
            String type  = list.get(i).getAttributes().getNamedItem("type").getNodeValue();
            String value = list.get(i).getAttributes().getNamedItem("value").getNodeValue();
            switch (type) {
                case "String" :
                    fieldsMap.get(id).set(people,value);
                    break;
                case "int":
                    fieldsMap.get(id).set(people,Integer.parseInt(value));
                    break;
                case "double":
                    fieldsMap.get(id).set(people,Double.parseDouble(value));
                    break;
                default:
                    throw new Exception("Неизвестный тип");
            }
        }

        return people;
    }


}



