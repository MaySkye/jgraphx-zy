package com.mxgraph.examples.swing.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 14:24 2020/3/29
 * @Modify By:
 */
public class SiteInfoUtil {

    private static List<String> siteLevelList = new ArrayList<>();
    private static List<String> siteNameList = new ArrayList<>();
    private static List<String> fileNameList = new ArrayList<>();

    private SiteInfoUtil() {
        // static class
    }

    public static List<String> getSiteLevel() {
        siteLevelList.clear();
        siteLevelList.add("西安");
        siteLevelList.add("北京");
        siteLevelList.add("合肥");
        siteLevelList.add("武汉");
        return siteLevelList;
    }

    public static List<String> getSiteNames(String siteLevel) {
        if (siteLevel.equals("一级站")) {
            siteNameList.clear();
            siteNameList.add("西安");
            siteNameList.add("北京");
            siteNameList.add("合肥");
            siteNameList.add("武汉");
        }
        return siteNameList;
    }

    public static List<String> getFileNames(String siteName) {
        if (siteName.equals("西安")) {
            fileNameList.clear();
            fileNameList.add("西安1");
            fileNameList.add("西安2");
            fileNameList.add("西安3");
        }
        return fileNameList;
    }
}
