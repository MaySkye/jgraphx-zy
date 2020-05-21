package com.mxgraph.examples.swing.match;

import com.mxgraph.examples.swing.decode.GraphDecoder;
import com.mxgraph.examples.swing.decode.GraphEle;
import com.mxgraph.examples.swing.graph.EdgeLink;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.VertexInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.owl.OwlResourceData;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.FileUtil;
import org.apache.commons.lang3.tuple.MutableTriple;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:12 2018/11/8
 * @Modify By:
 */
/*
* 此类用来得到了模板图的路径*/
public class ResMatchCore {

    public static String getTemplatePath(OwlResourceData owlResourceData) {

        GraphInterface<String> target_graph= showGraph.createGraph(owlResourceData);
        ResourceStaticData targetStatic = calcStaticMap(target_graph);

        List<String> candidateList = getCandidate(targetStatic);

        Object selectedVaule = JOptionPane.showInputDialog(null, AliasName.getAlias("select_template"),
                AliasName.getAlias("select"), JOptionPane.INFORMATION_MESSAGE, null,
                candidateList.toArray(), candidateList.get(0));

        if (selectedVaule == null) {
            return null;
        }

        //得到选中模板图的路径并返回
        String graphName = selectedVaule.toString();//graphName代表选中的模板的名字
        //String graphName = candidateList.get(0);

        StringBuilder graphPath = new StringBuilder();

        List<GraphEle> graphList = GraphDecoder.decodeDoc();
        graphList.forEach(graphEle -> {
            if (graphEle.getName().equals(graphName)) {
                graphPath.append(graphEle.getMxePath()); //得到模板图mxe文件的路径
            }
        });

        return graphPath.toString();
    }

    public static ResourceStaticData calcStaticMap(GraphInterface<String> graph) {
        /*
         * 对转换后的图结构信息做抽象信息处理，
         * 抽象出各对象的类型和相应数量信息，
         * 以及各对象间连接关系的类型和数量信息
         * */
        ResourceStaticData targetStaticData = new ResourceStaticData();
        targetStaticData.cellMap = new HashMap<>();
        targetStaticData.connMap = new HashMap<>();
        //得到图的顶点和边
        Map<String, VertexInterface<String>> vertexs=graph.getVertices();
        Map<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> edges=graph.getEdges();
        //为cellMap赋值
        Iterator<Map.Entry<String, VertexInterface<String>>> v_entry = vertexs.entrySet().iterator();
        while(v_entry.hasNext()){
            Map.Entry<String, VertexInterface<String>> entry = v_entry.next();
            String type = entry.getValue().getName();
            //type是key,数量是value
            if (!targetStaticData.cellMap.containsKey(type)) {
                targetStaticData.cellMap.put(type, 1);
            } else {
                targetStaticData.cellMap.put(type, targetStaticData.cellMap.get(type) + 1);
            }
        }

        //为connMap赋值
        Iterator<Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>>>
                e_entries1 = edges.entrySet().iterator();
        while(e_entries1.hasNext()){
            Map.Entry<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> entry = e_entries1.next();
            MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>> e=entry.getValue();
            System.out.println("entry.getValue():"+entry.getValue().getLeft().getName());
            ResourceStaticData.MTriple<String, String, String> tuple = new ResourceStaticData.MTriple<>
                    (e.getLeft().getName(),e.getMiddle().getName(), e.getRight().getName());

            if (!targetStaticData.connMap.containsKey(tuple)) {
                targetStaticData.connMap.put(tuple, 1);
            } else {
                targetStaticData.connMap.put(tuple, targetStaticData.connMap.get(tuple) + 1);
            }
        }

        System.out.println("==================== target owl resource info =====================");
        targetStaticData.cellMap.forEach((type, cnt) -> {
            System.out.println(type + ": " + cnt);
        });
        targetStaticData.connMap.forEach((tuple, cnt) -> {
            System.out.println(tuple.getFirst() + "--" + tuple.getSecond() + "-> " + tuple.getThird() + ": " + cnt);
        });
        System.out.println("===================================================================");

        return targetStaticData;
    }
    /*
    * 得到按照 相似度从高到低排序 的模板的list
    * */
    public static List<String> getCandidate(ResourceStaticData resourceStaticData) {

        Map<String, Double> similarMap = new HashMap<>(); //String模板名称，Double相似度的数值

        List<GraphEle> list = GraphDecoder.decodeDoc();
        String[] objs = new String[list.size()];


        for (GraphEle ele : list) {
            String graphName = ele.getName();
            printInfo(ele);

            //calcSimilar方法用来计算两个模板的相似度
            double similar = calcSimilar(resourceStaticData, ele.getResourceStaticData());
            objs[similarMap.size()] = graphName;
            similarMap.put(graphName, similar);
        }

        Arrays.sort(objs, (str1, str2) -> {
            return (int) (1000 * similarMap.get(str2)) - (int) (1000 * similarMap.get(str1));
        });

        System.out.println("----------------------- similar ----------------------");
        Stream.of(objs).forEach(str -> {
            System.out.println(str + ": " + String.format("%.2f%%", 100 * similarMap.get(str)));
        });
        System.out.println("--------------------- end similar --------------------");

        return Arrays.asList(objs);  //返回模板名称
    }
    /*
    * 打印出每个模板中，对象的个数和对象连接关系的个数
    * */
    private static void printInfo(GraphEle ele) {
        ResourceStaticData staticData = ele.getResourceStaticData();
        System.out.println("==================== " + ele.getName() + " owl resource info ====================");

        staticData.cellMap.forEach((type, cnt) -> {
            System.out.println(type + ": " + cnt);
        });
        staticData.connMap.forEach((tuple, cnt) -> {
            System.out.println(tuple.getFirst() + "--" + tuple.getSecond() + "-> " + tuple.getThird() + ": " + cnt);
        });
        System.out.println("=====================================================================");
    }

    //计算两个图元信息的相似度
    public static double calcSimilar(ResourceStaticData targetStaticData, ResourceStaticData candStaticData) {
        double precision = calcPrecision(targetStaticData, candStaticData);
        double recall = calcPrecision(candStaticData, targetStaticData);

        if (precision + recall < 0.000001) {
            return 0.0;
        }
        return 2.0 * precision * recall / (precision + recall);
    }

    //计算准确率
    public static double calcPrecision(ResourceStaticData testStaticData, ResourceStaticData standStaticData) {
        int[] total = {0}; // 整形数组，记录两集合元素数量之和
        int[] valid = {0}; // 整形数组，记录两集合交集的元素数量
        if (testStaticData == null || standStaticData == null) {
            return 0;
        }
        standStaticData.cellMap.forEach((type, cnt) -> {
            total[0] += cnt;
            if (testStaticData.cellMap.containsKey(type)) {
                valid[0] += Math.min(testStaticData.cellMap.get(type), cnt);
            }
        });
        standStaticData.connMap.forEach((tuple, cnt) -> {
            total[0] += cnt;
            if (testStaticData.connMap.containsKey(tuple)) {
                valid[0] += Math.min(testStaticData.connMap.get(tuple), cnt);
            }
        });
        testStaticData.cellMap.forEach((type, cnt) -> {
            total[0] += cnt;
        });
        testStaticData.connMap.forEach((tuple, cnt) -> {
            total[0] += cnt;
        });
        if (total[0] == valid[0]) {
            return 0.0;
        }
        return 1.0 * valid[0] / (total[0] - valid[0]);
    }
}
