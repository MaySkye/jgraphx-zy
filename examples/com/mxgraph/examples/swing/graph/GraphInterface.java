package com.mxgraph.examples.swing.graph;

import org.apache.commons.lang3.tuple.MutableTriple;


import java.util.Map;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:19 2018/8/7
 * @Modify By:
 */
public interface GraphInterface<T> extends BasicGraphInterface<T>, GraphAlgorithmsInterface<T>{

    public Map<T, VertexInterface<T>> getVertices();
    public Map<String, MutableTriple<VertexInterface<T>,EdgeLink,VertexInterface<T>>> getEdges();
    public void printVertices(Map<String, VertexInterface<String>> vertexs);
    public void printEdges(Map<String, MutableTriple<VertexInterface<String>,EdgeLink,VertexInterface<String>>> edges);

}
