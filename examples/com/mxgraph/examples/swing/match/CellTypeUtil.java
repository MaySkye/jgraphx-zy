package com.mxgraph.examples.swing.match;

import com.mxgraph.examples.swing.decode.CellDecoder;
import com.mxgraph.examples.swing.decode.CellEle;

import java.util.*;


public class CellTypeUtil {

    private static List<CellEle> opticalDeviceCellList = CellDecoder.opticalDeviceCellList;
    private static List<CellEle> networkDeviceData = CellDecoder.networkDeviceCellList;
    private static Map<String, List<CellEle>> cellMap = null;
    private static Map<String, List<String>> typeMap = null;
    private static Set<String> typeSet = null;
    private static List<String> typeList = null;

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
        });
    }
}
