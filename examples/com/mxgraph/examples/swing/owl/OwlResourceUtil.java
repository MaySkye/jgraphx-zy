package com.mxgraph.examples.swing.owl;

import com.mxgraph.examples.swing.graph.Vertex;
import com.mxgraph.examples.swing.graph.VertexInterface;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.mxgraph.examples.swing.mysqldb.DBConnection.sqlConnect;


public class OwlResourceUtil {
    /**
     * @param model
     * @return uri -> cls
     */
    public static Map<String, OwlClass> parseOwlClass(Model model) {

        Map<String, OwlClass> classMap = new HashMap<>((int) model.size());
        Map<String, equivalent_class> all_equivalentClass= new HashMap<>((int) model.size());

        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String predicateStr=predicate.toString();
            String objectStr = object.toString();

            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();

            System.out.println("-----------------------------------");
            System.out.println("subjectStr:  "+subjectStr);
            System.out.println("predicateStr:  "+predicateStr);
            System.out.println("objectStr:  "+objectStr);
            System.out.println("subjectName:  "+subjectName);
            System.out.println("predicateName:  "+predicateName);

            //将每个类初始化
            if (!subject.isLiteral() && !object.isLiteral() && objectStr.endsWith("Class") && predicateName.equals("type") && !classMap.containsKey(subjectStr)) {
                OwlClass cls = new OwlClass();
                cls.uri=subjectStr;
                cls.id = subjectName;
                classMap.put(subjectStr, cls);
            }

            //存储类的关系
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("equivalentClass")) {
                equivalent_class ecls = new equivalent_class();
                if(!classMap.containsKey(subjectStr)){
                    OwlClass cls = new OwlClass();
                    cls.uri=subjectStr;
                    cls.id = subjectName;
                    cls.equivalentClass.put(objectStr,ecls);
                    classMap.put(subjectStr, cls);
                }else{
                    OwlClass cls = classMap.get(subjectStr);
                    cls.equivalentClass.put(objectStr,ecls);
                }
            }


            if (!subject.isLiteral() && !object.isLiteral() && !predicate.isLiteral()
                    &&subjectName==null
                    && predicateName.equals("type") && !all_equivalentClass.containsKey(subjectStr)) {
                equivalent_class ecls = new equivalent_class();
                ecls.euri=subjectStr;
                all_equivalentClass.put(subjectStr, ecls);
            }

            if (!subject.isLiteral() && !object.isLiteral() && !predicate.isLiteral()&&subjectName==null
                    && predicateName.equals("onProperty")) {
                if(!all_equivalentClass.containsKey(subjectStr)){
                    equivalent_class ecls = new equivalent_class();
                    ecls.euri=subjectStr;
                    ecls.onProperty=objectStr;
                    all_equivalentClass.put(subjectStr, ecls);
                }else {
                    equivalent_class ecls=all_equivalentClass.get(subjectStr);
                    ecls.onProperty=objectStr;
                }
            }

            if (!subject.isLiteral() && !object.isLiteral() && !predicate.isLiteral()&&subjectName==null
                    && predicateName.equals("allValuesFrom")) {
                if(!all_equivalentClass.containsKey(subjectStr)){
                    equivalent_class ecls = new equivalent_class();
                    ecls.euri=subjectStr;
                    ecls.ValuesFrom=objectStr;
                    all_equivalentClass.put(subjectStr, ecls);
                }else {
                    equivalent_class ecls=all_equivalentClass.get(subjectStr);
                    ecls.ValuesFrom=objectStr;
                }
            }

            if (!subject.isLiteral() && !object.isLiteral() && !predicate.isLiteral()&&subjectName==null
                    && predicateName.equals("someValuesFrom")) {
                if(!all_equivalentClass.containsKey(subjectStr)){
                    equivalent_class ecls = new equivalent_class();
                    ecls.euri=subjectStr;
                    ecls.ValuesFrom=objectStr;
                    all_equivalentClass.put(subjectStr, ecls);
                }else {
                    equivalent_class ecls=all_equivalentClass.get(subjectStr);
                    ecls.ValuesFrom=objectStr;
                }
            }
        }

        stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String predicateStr=predicate.toString();
            String objectStr = object.toString();

            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();


            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("label") && classMap.containsKey(subjectStr)) {

                OwlClass cls = classMap.get(subjectStr);
                cls.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("example") && classMap.containsKey(subjectStr)) {

                OwlClass cls = classMap.get(subjectStr);
                cls.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("definition") && classMap.containsKey(subjectStr)) {

                OwlClass cls = classMap.get(subjectStr);
                cls.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("isDefinedBy") && classMap.containsKey(subjectStr)) {

                OwlClass cls = classMap.get(subjectStr);
                cls.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("comment") && classMap.containsKey(subjectStr)) {

                OwlClass cls = classMap.get(subjectStr);
                cls.annotations.put(predicateName,objectStr);
            }
            //存储父类
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("subClassOf") && classMap.containsKey(subjectStr)) {
                OwlClass cls = classMap.get(subjectStr);
                cls.parentClass=classMap.get(objectStr);
            }
        }
        //存储equivalentClass

        Iterator<Map.Entry<String, OwlClass>> it = classMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, OwlClass> entry = it.next();
            OwlClass oclass=entry.getValue();
            Map<String, equivalent_class> eclass= oclass.equivalentClass;

            Iterator<Map.Entry<String, equivalent_class>> it2 = eclass.entrySet().iterator();
            while(it2.hasNext()){
                Map.Entry<String, equivalent_class> entry2 = it2.next();
                equivalent_class ec=entry2.getValue();
                ec.euri=all_equivalentClass.get(entry2.getKey()).euri;
                ec.onProperty=all_equivalentClass.get(entry2.getKey()).onProperty;
                ec.ValuesFrom=all_equivalentClass.get(entry2.getKey()).ValuesFrom;
            }
        }

        return classMap;
    }

    /**
     * @param model
     * @param classMap
     * @return uri -> objAttr
     * ObjectAttr.uri=subjectStr;
     * ObjectAttr.id = subjectName;
     */
    public static Map<String, OwlObjectAttribute> parseOwlObjectAttribute(Model model, Map<String, OwlClass> classMap) {

        Map<String, OwlObjectAttribute> attrMap = new HashMap<>((int) model.size());

        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();
            String objectStr = object.toString();

            if (!subject.isLiteral() && !object.isLiteral() && objectStr.endsWith("ObjectProperty")
                    && predicateName.equals("type") && !attrMap.containsKey(subjectStr)) {
                OwlObjectAttribute ObjectAttr = new OwlObjectAttribute();
                ObjectAttr.uri=subjectStr;
                ObjectAttr.id = subjectName;
                attrMap.put(subjectStr, ObjectAttr);
            }
        }

        stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();
            String objectStr = object.toString();

            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("label") && attrMap.containsKey(subjectStr)) {

                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("definition") && attrMap.containsKey(subjectStr)) {

                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("comment") && attrMap.containsKey(subjectStr)) {

                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("domainIncludes") && attrMap.containsKey(subjectStr)
                    && classMap.containsKey(objectStr)) {

                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.domain.add(classMap.get(objectStr));
            }

            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("rangeIncludes") && attrMap.containsKey(subjectStr)
                    && classMap.containsKey(objectStr)) {

                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.range.add(classMap.get(objectStr));
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("isDefinedBy") && attrMap.containsKey(subjectStr)) {
                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("inverseOf") && attrMap.containsKey(subjectStr)
                    &&attrMap.containsKey(objectStr)) {
                OwlObjectAttribute ObjectAttr = attrMap.get(subjectStr);
                ObjectAttr.inverseOfAttr=attrMap.get(objectStr);
                OwlObjectAttribute ObjectAttr2 = attrMap.get(objectStr);
                ObjectAttr2.inverseOfAttr=attrMap.get(subjectStr);
            }
        }
        return attrMap;
    }

    /**
     * @param model
     * @param classMap
     * @return uri -> dataAttr
     * dataAttr.uri= subjectStr;
     * dataAttr.id = subjectName;
     */
    public static Map<String, OwlDataAttribute> parseOwlDataAttribute(Model model, Map<String, OwlClass> classMap) {

        Map<String, OwlDataAttribute> attrMap = new HashMap<>((int) model.size());

        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();
            String objectStr = object.toString();

            if (!subject.isLiteral() && !object.isLiteral() && objectStr.endsWith("DatatypeProperty")
                    && predicateName.equals("type") && !attrMap.containsKey(subjectStr)) {

                OwlDataAttribute dataAttr = new OwlDataAttribute();
                dataAttr.uri=subjectStr;
                dataAttr.id = subjectName;
                attrMap.put(subjectStr, dataAttr);
            }
        }

        stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();
            String objectStr = object.toString();

            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("label") && attrMap.containsKey(subjectStr)) {

                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("definition") && attrMap.containsKey(subjectStr)) {

                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("comment") && attrMap.containsKey(subjectStr)) {

                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("isDefinedBy") && attrMap.containsKey(subjectStr)) {
                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.annotations.put(predicateName,objectStr);
            }

            //存储父类
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("subPropertyOf") && attrMap.containsKey(subjectStr)) {
                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.parentOda=attrMap.get(objectStr);
            }

            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("domainIncludes") && attrMap.containsKey(subjectStr)
                    && classMap.containsKey(objectStr)) {

                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.domainIncludes.add(classMap.get(objectStr));
            }

            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("rangeIncludes") && attrMap.containsKey(subjectStr)) {

                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.rangeIncludes.add(new String(objectStr));
            }

            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("range") && attrMap.containsKey(subjectStr)) {

                OwlDataAttribute dataAttr = attrMap.get(subjectStr);
                dataAttr.range=objectStr;
            }




        }
        return attrMap;
    }
    /**
     * @param model
     * @param classMap
     * @param objAttrMap
     * @param dataAttrMap
     * @return uri -> obj
     *
     */
    public static Map<String, OwlObject> parseOwlObject(Model model, Map<String, OwlClass> classMap,
                                                        Map<String, OwlObjectAttribute> objAttrMap,
                                                        Map<String, OwlDataAttribute> dataAttrMap) {
        Map<String, OwlObject> objMap = new HashMap<>((int) model.size());

        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject(); //主语
            Property predicate = statement.getPredicate();//谓语
            RDFNode object = statement.getObject();//宾语

            String subjectStr = subject.toString();
            String subjectName = subject.getLocalName();
            String predicateName = predicate.getLocalName();
            String objectStr = object.toString();

            if (!subject.isLiteral() && !object.isLiteral() && objectStr.endsWith("NamedIndividual")
                    && predicateName.equals("type") && !objMap.containsKey(subjectStr)) {
                OwlObject obj = new OwlObject();
                obj.uri=subjectStr;
                obj.id = subjectName;
                objMap.put(subjectStr, obj);
            }
        }

        stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            Resource subject = statement.getSubject();
            Property predicate = statement.getPredicate();
            RDFNode object = statement.getObject();

            String subjectStr = subject.toString();
            String subjectName = subject.getLocalName();
            String predicateStr = predicate.toString();
            String predicateName = predicate.getLocalName();
            String objectStr = object.toString();

            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("label") && objMap.containsKey(subjectStr)) {

                OwlObject obj = objMap.get(subjectStr);
                obj.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("definition") && objMap.containsKey(subjectStr)) {

                OwlObject obj = objMap.get(subjectStr);
                obj.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && object.isLiteral() && predicateName.equals("comment") && objMap.containsKey(subjectStr)) {

                OwlObject obj = objMap.get(subjectStr);
                obj.annotations.put(predicateName,objectStr);
            }
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("isDefinedBy") && objMap.containsKey(subjectStr)) {
                OwlObject obj = objMap.get(subjectStr);
                obj.annotations.put(predicateName,objectStr);
            }
           //实例的类型
            if (!subject.isLiteral() && !object.isLiteral() && predicateName.equals("type")&& !objectStr.endsWith("NamedIndividual")
                    &&objMap.containsKey(subjectStr)) {
                OwlObject obj = objMap.get(subjectStr);
                obj.type=classMap.get(objectStr);
            }
            //实例的对象属性
            if (!subject.isLiteral() && !object.isLiteral()&& objMap.containsKey(subjectStr)
                    && objAttrMap.containsKey(predicateStr) && objMap.containsKey(objectStr)) {

                OwlObject obj = objMap.get(subjectStr);
                if (!obj.objAttrs.containsKey(objAttrMap.get(predicateStr))) {
                    obj.objAttrs.put(objAttrMap.get(predicateStr), new HashSet<>());
                }
                Set<OwlObject> valueSet = obj.objAttrs.get(objAttrMap.get(predicateStr));
                valueSet.add(objMap.get(objectStr));
            }
            //实例的数据属性
            if (!subject.isLiteral() && object.isLiteral() && objMap.containsKey(subjectStr)
                    && dataAttrMap.containsKey(predicateStr)) {

                OwlObject obj = objMap.get(subjectStr);
                if (!obj.dataAttrs.containsKey(dataAttrMap.get(predicateStr))) {
                    obj.dataAttrs.put(dataAttrMap.get(predicateStr), new HashSet<>());
                }
                Set<Object> valueSet = obj.dataAttrs.get(dataAttrMap.get(predicateStr));
                valueSet.add(simplifyValue(objectStr));
            }
        }

        return objMap;
    }

    public static OwlResourceData parseResourceFile(String filePath) {
        System.out.println("filePath:"+filePath);
        // 调用Jena提供的API，获取Model数据
        Model model = getModel(filePath);
        // 解析出Model数据中的Classes资源
        Map<String, OwlClass> classMap = parseOwlClass(model);
        // 解析出Model数据中的Object Properties资源
        Map<String, OwlObjectAttribute> objAttrMap = parseOwlObjectAttribute(model, classMap);
        // 解析出Model数据中的Data properties资源
        Map<String, OwlDataAttribute> dataAttrMap = parseOwlDataAttribute(model, classMap);
        // 解析出Model数据中的Individuals资源
        Map<String, OwlObject> objMap = parseOwlObject(model, classMap, objAttrMap, dataAttrMap);
        // 打包所有资源信息，并返回
        OwlResourceData owlResourceData = new OwlResourceData();
        owlResourceData.model = model;
        owlResourceData.classMap = classMap;
        owlResourceData.objAttrMap = objAttrMap;
        owlResourceData.dataAttrMap = dataAttrMap;
        owlResourceData.objMap = objMap;

        return owlResourceData;
    }

    private static String simplifyLabel(String objStr) {
        int idx = objStr.lastIndexOf('@');
        if (idx > 0) {
            return objStr.substring(0, idx);
        }
        return objStr;
    }

    private static String simplifyValue(String objStr) {
        int idx = objStr.lastIndexOf("^^");
        if (idx > 0) {
            return objStr.substring(0, idx);
        }
        return objStr;
    }

    public static void print(OwlResourceData owlResourceData) {

        if (owlResourceData == null) {
            return;
        }
        /*
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> Triples >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        //打印model的三元组信息
        owlResourceData.model.write(System.out, "N-TRIPLES");
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        */
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> Class >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        owlResourceData.classMap.forEach((String uri, OwlClass cls) -> {
            System.out.println("--uri: " + uri);
            System.out.println("--id: " + cls.id);
            System.out.println("--annotations: ");
            Iterator<Map.Entry<String, String>> it = cls.annotations.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                System.out.println(entry.getKey()+": "+entry.getValue());
            }
           if(cls.parentClass!=null){
               System.out.println("--父类: "+cls.parentClass.id);
           }
            System.out.println("--equivalentClass: ");
            Iterator<Map.Entry<String, equivalent_class>> it1 = cls.equivalentClass.entrySet().iterator();
            while(it1.hasNext()){
                Map.Entry<String, equivalent_class> entry = it1.next();
                equivalent_class ec=entry.getValue();
                System.out.println("euri:"+ec.euri);
                System.out.println("onProperty:"+ec.onProperty);
                System.out.println("ValuesFrom:"+ec.ValuesFrom);
            }
            System.out.println("-----------------------------------------");
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");

        System.out.println(">>>>>>>>>>>>>>>>>>>> ObjectAttribute >>>>>>>>>>>>>>>>>>>>>>>");
        owlResourceData.objAttrMap.forEach((uri, objAttr) -> {
            System.out.println("--uri: " + uri);
            System.out.println("--id: " + objAttr.id);

            System.out.println("--annotations: ");
            Iterator<Map.Entry<String, String>> it = objAttr.annotations.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                System.out.println(entry.getKey()+": "+entry.getValue());
            }
            if(objAttr.inverseOfAttr!=null){
                System.out.println("--inverseOfAttr: " + objAttr.inverseOfAttr.id);
            }
            System.out.print("--domain: ");
            objAttr.domain.forEach(cls -> {
                System.out.print(cls.id + ";");
            });
            System.out.println();
            System.out.print("--range: ");
            objAttr.range.forEach(cls -> {
                System.out.print(cls.id + ";");
            });
            System.out.println();
            System.out.println("-----------------------------------------");
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");

        System.out.println(">>>>>>>>>>>>>>>>>>>> DataAttribute >>>>>>>>>>>>>>>>>>>>>>>");
        owlResourceData.dataAttrMap.forEach((uri, dataAttr) -> {
            System.out.println("--uri: " + uri);
            System.out.println("--id: " + dataAttr.id);
            if(dataAttr.parentOda!=null){
                System.out.println("--parentOda: "+dataAttr.parentOda.id);
            }
            System.out.println("--range: "+dataAttr.range);
            System.out.println("--domainIncludes: ");
            dataAttr.domainIncludes.forEach(cls -> {
                System.out.print(cls.id + " ");
            });
            System.out.println("--rangeIncludes: ");
            dataAttr.rangeIncludes.forEach(cls -> {
                System.out.print(cls.toString() + " ");
            });
            System.out.println("--annotations: ");
            Iterator<Map.Entry<String, String>> it = dataAttr.annotations.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                System.out.println(entry.getKey()+": "+entry.getValue());
            }
            System.out.println("-----------------------------------------");
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");

        System.out.println(">>>>>>>>>>>>>>>>>>>>> Individual >>>>>>>>>>>>>>>>>>>>>>>>");
        owlResourceData.objMap.forEach((uri, obj) -> {
            System.out.println("--uri: " + uri);
            System.out.println("--id: " + obj.id);
            System.out.println("--visible:"+obj.visible);
            System.out.println("--type: " + (obj.type == null ? "null" : obj.type.id));
            System.out.println("--种类: " + findKind(obj.type));
            System.out.println("--obj attr: ");
            obj.objAttrs.forEach((objAttr, objSet) -> {
                System.out.print("\t" + objAttr.id + ": ");
                String[] attrValue = {""};
                objSet.forEach(obj2 -> {
                    attrValue[0] = attrValue[0] + obj2.id + ", ";
                });
                if (attrValue[0].length() > 1) {
                    attrValue[0] = attrValue[0].substring(0, attrValue[0].length() - 2);
                }
                System.out.println(attrValue[0]);
            });
            System.out.println("--data attr: ");
            obj.dataAttrs.forEach((objAttr, objSet) -> {
                System.out.print("\t" + objAttr.id + ": ");
                String[] attrValue = {""};
                objSet.forEach(obj2 -> {
                    attrValue[0] = attrValue[0] + obj2.toString() + ", ";
                });
                if (attrValue[0].length() > 1) {
                    attrValue[0] = attrValue[0].substring(0, attrValue[0].length() - 2);
                }
                System.out.println(attrValue[0]);
            });
            System.out.println("-----------------------------------------");
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");


        System.out.println(">>>>>>>>>>>>>>>>>>>>> Relation >>>>>>>>>>>>>>>>>>>>>>>>");
        owlResourceData.objMap.forEach((uri, obj) -> {
            String id=obj.id;
            obj.objAttrs.forEach((objAttr, objSet) -> {
                objSet.forEach(obj2 -> {
                    System.out.println(id+"->" + objAttr.id + "->"+obj2.id);
                });
            });
            obj.dataAttrs.forEach((objAttr, objSet) -> {
                objSet.forEach(obj2 -> {
                    System.out.println(id+"->" + objAttr.id + "->"+obj2);
                });
            });
            System.out.println("-----------------------------------------------");
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
    }

    public static void printOwlobject(Map<String, OwlObject> objMap ){
        System.out.println(">>>>>>>>>>>>>>>>>>>>> Individual >>>>>>>>>>>>>>>>>>>>>>>>");
         objMap.forEach((uri, obj) -> {
            if(findKind(obj.type).equals("FeatureOfInterest")){
            System.out.println("--uri: " + uri);
            System.out.println("--id: " + obj.id);
            System.out.println("--visible:"+obj.visible);
            System.out.println("--type: " + (obj.type == null ? "null" : obj.type.id));
            System.out.println("--种类: " + findKind(obj.type));
            System.out.println("--obj attr: ");
            obj.objAttrs.forEach((objAttr, objSet) -> {
                System.out.print("\t" + objAttr.id + ": ");
                String[] attrValue = {""};
                objSet.forEach(obj2 -> {
                    attrValue[0] = attrValue[0] + obj2.id + ", ";
                });
                if (attrValue[0].length() > 1) {
                    attrValue[0] = attrValue[0].substring(0, attrValue[0].length() - 2);
                }
                System.out.println(attrValue[0]);
            });
            System.out.println("--data attr: ");
            obj.dataAttrs.forEach((objAttr, objSet) -> {
                System.out.print("\t" + objAttr.id + ": ");
                String[] attrValue = {""};
                objSet.forEach(obj2 -> {
                    attrValue[0] = attrValue[0] + obj2.toString() + ", ";
                });
                if (attrValue[0].length() > 1) {
                    attrValue[0] = attrValue[0].substring(0, attrValue[0].length() - 2);
                }
                System.out.println(attrValue[0]);
            });
            System.out.println("-----------------------------------------");
            }
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");


        System.out.println(">>>>>>>>>>>>>>>>>>>>> Relation >>>>>>>>>>>>>>>>>>>>>>>>");
        objMap.forEach((uri, obj) -> {
            String id=obj.id;
            obj.objAttrs.forEach((objAttr, objSet) -> {
                objSet.forEach(obj2 -> {
                    System.out.println(id+"->" + objAttr.id + "->"+obj2.id);
                });
            });
            obj.dataAttrs.forEach((objAttr, objSet) -> {
                objSet.forEach(obj2 -> {
                    System.out.println(id+"->" + objAttr.id + "->"+obj2);
                });
            });
            System.out.println("-----------------------------------------------");
        });
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
    }
    /**
     * 根据owl文件的路径获取model对象
     *
     * @param file 文件路径
     * @return model
     */
    public static Model getModel(String file) {

        if (file == null) {
            return null;
        }

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(file);
        if (in == null) {
            throw new IllegalArgumentException("File: " + file + " not found");
        }

        Model model = ModelFactory.createDefaultModel();
        // read the RDF/XML file
        model.read(in, null);
        return model;
    }

    public static String findKind(OwlClass cls){
        String kind="";
        if(cls!=null) {
            while (cls.parentClass != null) {
                cls = cls.parentClass;
            }
            kind=cls.id;
        }
        return kind;
    }

    public static OwlClass findKindClass(OwlClass cls){
        OwlClass kindClass=null;
        if(cls!=null) {
            while (cls.parentClass != null) {
                cls = cls.parentClass;
            }
            kindClass=cls;
        }
        return kindClass;
    }

    public static String findSubsystem(OwlClass cls){
        String kind="";
        if(cls!=null) {
            while (cls.parentClass.parentClass != null) {
                cls = cls.parentClass;
            }
            kind=cls.id;
        }
        return kind;
    }


    public static int getFOINum(Map<String, OwlObject> objMap){
        int num=0;
        for (Map.Entry<String, OwlObject> entry : objMap.entrySet()) {
            if(findKind(entry.getValue().type).equals("FeatureOfInterest")
                    ||findKind(entry.getValue().type).equals("ControlRoom")){
                num++;
            }
        }
       return num;
    }

    public static void main(String[] args) {
        OwlResourceData owlResourceData = parseResourceFile("C:/Users/ASUS/Desktop/fibre.owl");
        print(owlResourceData);

    }
}
