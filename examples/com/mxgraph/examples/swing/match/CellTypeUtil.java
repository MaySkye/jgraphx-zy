package com.mxgraph.examples.swing.match;

import com.mxgraph.examples.swing.decode.CellDecoder;
import com.mxgraph.examples.swing.decode.CellEle;

import java.util.*;


public class CellTypeUtil {

    public static List<CellEle> opticalDeviceCellList = CellDecoder.opticalDeviceCellList;
    public static List<CellEle> networkDeviceData = CellDecoder.networkDeviceCellList;
    public static Map<String, List<CellEle>> cellMap = null;
    public static Map<String, List<String>> typeMap = null;
    public static Set<String> typeSet = null;
    public static List<String> typeList = null;

    static {
        loadData();
    }

    public static int getPortNum(String type) {
        if (cellMap.containsKey(type)) {
            return cellMap.get(type).get(0).getPorts().size();
        }
        return 0;
    }

    public static boolean isContain(String type) {
        return typeSet.contains(type);
    }

    public static List<String> getCells(String type) {
        return typeMap.get(type);
    }

    private static void loadData() {
        cellMap = new HashMap<>(opticalDeviceCellList.size());
        typeMap = new HashMap<>(opticalDeviceCellList.size());
        typeSet = new HashSet<>(opticalDeviceCellList.size());
        typeList = new ArrayList<>(opticalDeviceCellList.size());
        opticalDeviceCellList.forEach(cellEle -> {
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
