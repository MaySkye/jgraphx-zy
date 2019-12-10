package com.mxgraph.examples.swing.util;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AliasName {

    private static List<NameAliasEle> list = null;
    private static Map<String, String> aliasMap = null;
    private static Map<String, String> reverseMap = null;


    private AliasName() {
        // static class
    }

    public static String getAlias(String name) {
        if (aliasMap == null) {
            loadData();
        }
        if(aliasMap.containsKey(name)){
            return aliasMap.get(name);
        }
        return name;
    }

    public static String getReverse(String alias) {
        if (reverseMap == null) {
            loadData();
        }
        if(reverseMap.containsKey(alias)){
            return reverseMap.get(alias);
        }
        return alias;
    }

    private static void loadData() {
        list = AliasNameDecoder.decodeDoc();
        aliasMap = new HashMap<>();
        reverseMap = new HashMap<>();
        list.forEach(nameAliasEle -> {
            aliasMap.put(nameAliasEle.getName(), nameAliasEle.getAlias());
            reverseMap.put(nameAliasEle.getAlias(), nameAliasEle.getName());
        });
    }

}
