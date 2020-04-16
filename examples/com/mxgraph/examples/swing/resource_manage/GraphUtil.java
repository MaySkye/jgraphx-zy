package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.decode.GraphDecoder;
import com.mxgraph.examples.swing.decode.GraphEle;
import com.mxgraph.examples.swing.match.ResourceStaticData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphUtil {

    private static List<GraphEle> data = GraphDecoder.decodeDoc();
    private static Map<String, GraphEle> graphMap = null;
    private static List<String> graphList = null;


    private GraphUtil() {
        // static class
    }

    public static void quickRename(String oldName, String newName) {
        if (!isContain(oldName) || isContain(newName)) {
            return;
        }
        List<GraphEle> list = new ArrayList<>(data.size());
        data.forEach(graphEle -> {
            if (graphEle.equals(oldName)) {
                graphEle.setName(newName);
            }
            list.add(graphEle);
        });
        try {
            GraphDecoder.saveData(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reloadData();
    }

    public static void quickDel(String name) {
        if (name == null) {
            return;
        }
        List<GraphEle> list = new ArrayList<>(data.size());
        data.forEach(graphEle -> {
            if (!name.equals(graphEle.getName())) {
                list.add(graphEle);
            }
        });
        try {
            GraphDecoder.saveData(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        reloadData();
    }

    public static boolean isContain(String name) {
        if (graphMap == null) {
            initData();
        }
        return graphMap.containsKey(name);
    }

    public static List<String> listGraph() {
        if (graphList == null) {
            initData();
        }
        return graphList;
    }

    public static String getImgPath(String graphName) {
        if (graphMap == null) {
            initData();
        }
        return graphMap.get(graphName).getImagePath();
    }

    public static List<String> getLinkInfo(String graphName) {
        if (graphMap == null) {
            initData();
        }
        Map<ResourceStaticData.MTriple<String, String, String>, Integer> connMap = graphMap.get(graphName).getResourceStaticData().connMap;
        List<String> result=new ArrayList<>();
        for (ResourceStaticData.MTriple<String, String, String> key : connMap.keySet()) {
            System.out.println("key= "+ key + " and value= " + connMap.get(key));
            String s = key.getFirst()+" - "+key.getThird() +" : "+connMap.get(key);
            result.add(s);
        }
        return result;
    }

    public static List<String> getDeviceInfo(String graphName) {
        if (graphMap == null) {
            initData();
        }
        Map<String, Integer> cellCountMap = graphMap.get(graphName).getResourceStaticData().cellMap;
        List<String> result=new ArrayList<>();
        for (String key : cellCountMap.keySet()) {
            System.out.println("key= "+ key + " and value= " + cellCountMap.get(key));
            String s = key +" : "+cellCountMap.get(key);
            result.add(s);
        }
        return result;
    }

    private static void initData() {
        graphMap = new HashMap<>(data.size());
        graphList = new ArrayList<>(data.size());
        data.forEach(graphEle -> {
            System.out.println("graphEle getCellCountMap():"+graphEle.getCellCountMap());
            graphMap.put(graphEle.getName(), graphEle);
            graphList.add(graphEle.getName());
        });
    }

    private static void reloadData() {
        if (graphMap == null) {
            graphMap = new HashMap<>(data.size());
        } else {
            graphMap.clear();
        }
        if (graphList == null) {
            graphList = new ArrayList<>(data.size());
        } else {
            graphList.clear();
        }
        data.forEach(graphEle -> {
            graphMap.put(graphEle.getName(), graphEle);
            graphList.add(graphEle.getName());
        });
    }
}
