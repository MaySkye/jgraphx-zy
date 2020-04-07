package com.mxgraph.examples.swing.match;

import com.mxgraph.examples.swing.decode.CellDecoder;
import com.mxgraph.examples.swing.decode.CellEle;

import java.util.*;


public class CellTypeUtil {

    private static List<CellEle> opticalDeviceData = CellDecoder.opticalDeviceCellList;
    private static List<CellEle> networkDeviceData = CellDecoder.networkDeviceCellList;
    private static Map<String, List<CellEle>> cellMap = null;
    private static Map<String, List<String>> typeMap = null;
    private static Set<String> typeSet = null;
    private static List<String> typeList = null;


    private CellTypeUtil() {
        // static class
    }

    public static int getPortNum(String type) {
        if (cellMap == null) {
            loadData();
        }
        if (cellMap.containsKey(type)) {
            return cellMap.get(type).get(0).getPorts().size();
        }
        return 0;
    }

    public static Set<String> getTypeSet() {
        if (typeSet == null) {
            loadData();
        }
        return typeSet;
    }

    public static List<String> listType() {
        if (typeList == null) {
            loadData();
        }
        return typeList;
    }

    public static boolean isContain(String type) {
        return getTypeSet().contains(type);
    }

    public static List<String> getCells(String type) {
        if (typeMap == null) {
            loadData();
        }
        return typeMap.get(type);
    }

    private static void loadData() {
        cellMap = new HashMap<>(opticalDeviceData.size());
        typeMap = new HashMap<>(opticalDeviceData.size());
        typeSet = new HashSet<>(opticalDeviceData.size());
        typeList = new ArrayList<>(opticalDeviceData.size());
        opticalDeviceData.forEach(cellEle -> {
            if (typeSet.add(cellEle.getType())) {
                typeList.add(cellEle.getType());
                typeMap.put(cellEle.getType(), new ArrayList<>());
                cellMap.put(cellEle.getType(), new ArrayList<>());
            }
            List<String> list1 = typeMap.get(cellEle.getType());
            list1.add(cellEle.getName());
            List<CellEle> list2 = cellMap.get(cellEle.getType());
            list2.add(cellEle);
        });




        cellMap = new HashMap<>(networkDeviceData.size());
        typeMap = new HashMap<>(networkDeviceData.size());
        typeSet = new HashSet<>(networkDeviceData.size());
        typeList = new ArrayList<>(networkDeviceData.size());
        networkDeviceData.forEach(cellEle -> {
            if (typeSet.add(cellEle.getType())) {
                typeList.add(cellEle.getType());
                typeMap.put(cellEle.getType(), new ArrayList<>());
                cellMap.put(cellEle.getType(), new ArrayList<>());
            }
            List<String> list1 = typeMap.get(cellEle.getType());
            list1.add(cellEle.getName());
            List<CellEle> list2 = cellMap.get(cellEle.getType());
            list2.add(cellEle);
        });
    }
}
