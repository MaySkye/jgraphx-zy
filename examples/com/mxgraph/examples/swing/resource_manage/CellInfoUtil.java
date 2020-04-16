package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.decode.CellDecoder;
import com.mxgraph.examples.swing.decode.CellEle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CellInfoUtil {


    private static List<CellEle> data = CellDecoder.opticalDeviceCellList;


    private static Map<String, CellEle> cellMap = null;
    private static List<String> cellList = null;


    private CellInfoUtil() {
        // static class
    }

    public static boolean isContain(String cellName) {
        if (cellMap == null) {
            initData();
        }
        return cellMap.containsKey(cellName);
    }

    public static List<String> getCells() {
        if (cellList == null) {
            initData();
        }
        return cellList;
    }

    public static String getType(String cellName) {
        if (cellMap == null) {
            initData();
        }
        return cellMap.get(cellName).getType();
    }

    public static String getIcon(String cellName) {
        if (cellMap == null) {
            initData();
        }
        return cellMap.get(cellName).getIcon();
    }

    private static void initData() {
        cellMap = new HashMap<>(data.size());
        cellList = new ArrayList<>(data.size());
        data.forEach(cellEle -> {
            cellMap.put(cellEle.getName(), cellEle);
            cellList.add(cellEle.getName());
        });
    }

}
