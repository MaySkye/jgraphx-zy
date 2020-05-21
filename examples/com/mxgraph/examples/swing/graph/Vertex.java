package com.mxgraph.examples.swing.graph;

import com.alibaba.fastjson.JSON;
import com.mxgraph.examples.swing.owl.*;

import java.util.*;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 10:19 2018/8/7
 * @Modify By:
 */
public class Vertex<T> implements VertexInterface<T>, java.io.Serializable {

    private T label;//唯一，标识标点,可以用不同类型来标识顶点如String,Integer....

    /*以下为新添加的,与OwlObject一一对应*/
    private String name;
    private String id; //id和label一样，都是唯一的
    private Map<String,String> data_info=new HashMap<>();//一些简单的数据属性信息
    private Map<String,String> link_info=new HashMap<>();//一些简单的连接属性信息
    private String url;//资源url


    private List<Edge> edgeList;//到该顶点邻接点的边,实际以java.util.LinkedList存储
    private boolean visited;//标识顶点是否已访问
    private VertexInterface<T> previousVertex;//该顶点的前驱顶点
    private double cost;//顶点的权值,与边的权值要区别开来

    public Vertex() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getData_info() {
        return data_info;
    }

    public void setData_info(Map<String, String> data_info) {
        this.data_info = data_info;
    }

    public Map<String, String> getLink_info() {
        return link_info;
    }

    public void setLink_info(Map<String, String> link_info) {
        this.link_info = link_info;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public void setEdgeList(List<Edge> edgeList) {
        this.edgeList = edgeList;
    }

    public VertexInterface<T> getPreviousVertex() {
        return previousVertex;
    }

    public void setPreviousVertex(VertexInterface<T> previousVertex) {
        this.previousVertex = previousVertex;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getVisited(){
        return visited;
    }

    public Vertex(T vertexLabel){

        label = vertexLabel;
        edgeList = new LinkedList<Edge>();//是Vertex的属性,说明每个顶点都有一个edgeList用来存储所有与该顶点关系的边
        visited = false;
        previousVertex = null;
        cost = 0;
    }


    /**
     *Task: 这里用了一个单独的类来表示边,主要是考虑到带权值的边
     *可以看出,Edge类封装了一个顶点和一个EdgeLink类型变量
     *若不需要考虑权值,可以不需要单独创建一个Edge类来表示边,只需要一个保存顶点的列表即可
     * @author hapjin
     */
    protected class Edge implements java.io.Serializable {
        private VertexInterface<T> vertex;// 终点
        private EdgeLink edgelink;//权值

        //Vertex 类本身就代表顶点对象,因此在这里只需提供 endVertex，就可以表示一条边了
        protected Edge(VertexInterface<T> endVertex, EdgeLink edgelink){
            vertex = endVertex;
            this.edgelink = edgelink;
        }

        protected VertexInterface<T> getEndVertex(){
            return vertex;
        }
        protected EdgeLink getEdgeLink(){
            return edgelink;
        }
    }

    /**Task: 遍历该顶点邻接点的迭代器--为 getNeighborInterator()方法 提供迭代器
     * 由于顶点的邻接点以边的形式存储在java.util.List中,因此借助List的迭代器来实现
     */
    private class NeighborIterator implements Iterator<VertexInterface<T>>{

        Iterator<Edge> edgesIterator;
        private NeighborIterator() {
            edgesIterator = edgeList.iterator();//获得 LinkedList 的迭代器
        }
        @Override
        public boolean hasNext() {
            return edgesIterator.hasNext();
        }

        @Override
        public VertexInterface<T> next() {
            VertexInterface<T> nextNeighbor = null;
            if(edgesIterator.hasNext()){
                Edge edgeToNextNeighbor = edgesIterator.next();//LinkedList中存储的是Edge
                nextNeighbor = edgeToNextNeighbor.getEndVertex();//从Edge对象中取出顶点
            }
            else
                throw new NoSuchElementException();
            return nextNeighbor;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**Task: 生成一个遍历该顶点所有邻接边的权值的迭代器
     * 权值是Edge类的属性,因此先获得一个遍历Edge对象的迭代器,取得Edge对象,再获得权值
     * @author hapjin
     *
     */
    private class EdgeLinkIterator implements Iterator{//这里不知道为什么,用泛型报编译错误???

        private Iterator<Edge> edgesIterator;
        private EdgeLinkIterator(){
            edgesIterator = edgeList.iterator();
        }
        @Override
        public boolean hasNext() {
            return edgesIterator.hasNext();
        }
        @Override
        public Object next() {
            EdgeLink result;
            if(edgesIterator.hasNext()){
                Edge edge = edgesIterator.next();
                result = edge.getEdgeLink();
            }
            else throw new NoSuchElementException();
            return (Object)result;//从迭代器中取得结果时,需要强制转换成EdgeLink
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public T getLabel() {
        return label;
    }

    public void setLabel(T label) {
        this.label = label;
    }

    @Override
    public void visit() {
        this.visited = true;
    }

    @Override
    public void unVisit() {
        this.visited = false;
    }

    @Override
    public boolean isVisited() {
        return visited;
    }

    @Override
    public boolean connect(VertexInterface<T> endVertex, EdgeLink edgelink) {
        // 将"边"(边的实质是顶点)插入顶点的邻接表
        boolean result = false;
        if(!this.equals(endVertex)){//顶点互不相同
            Iterator<VertexInterface<T>> neighbors = this.getNeighborInterator();
            boolean duplicateEdge = false;
            while(!duplicateEdge && neighbors.hasNext()){//保证不添加重复的边
                VertexInterface<T> nextNeighbor = neighbors.next();
                if(endVertex.equals(nextNeighbor)){
                    duplicateEdge = true;
                    break;
                }
            }//end while
            if(!duplicateEdge){
                edgeList.add(new Edge(endVertex, edgelink));//添加一条新边
                result = true;
            }//end if
        }//end if
        return result;
    }

    @Override
    public boolean connect(VertexInterface<T> endVertex) {
        return connect(endVertex, null);
    }

    @Override
    public Iterator<VertexInterface<T>> getNeighborInterator() {
        return new NeighborIterator();
    }

    @Override
    public Iterator getEdgeLinkIterator() {
        return new EdgeLinkIterator();
    }

    @Override
    public boolean hasNeighbor() {
        return !(edgeList.isEmpty());//邻接点实质是存储是List中
    }

    @Override
    public VertexInterface<T> getUnvisitedNeighbor() {//最坏情况下复杂度为O(E)
        VertexInterface<T> result = null;
        Iterator<VertexInterface<T>> neighbors = getNeighborInterator();
        while(neighbors.hasNext() && result == null){//获得该顶点的第一个未被访问的邻接点
            VertexInterface<T> nextNeighbor = neighbors.next();
            if(!nextNeighbor.isVisited())
                result = nextNeighbor;
        }
        return result;
    }

    @Override
    public void setPredecessor(VertexInterface<T> predecessor) {
        this.previousVertex = predecessor;
    }

    @Override
    public VertexInterface<T> getPredecessor() {
        return this.previousVertex;
    }

    @Override
    public boolean hasPredecessor() {
        return this.previousVertex != null;
    }

    @Override
    public void setCost(double newCost) {
        cost = newCost;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public String getJsonStr(VertexInterface<T> v) {
        return JSON.toJSONString(v);
    }

    //判断两个顶点是否相同
    public boolean equals(Object other){
        boolean result;
        if((other == null) || (getClass() != other.getClass()))
            result = false;
        else
        {
            Vertex<T> otherVertex = (Vertex<T>)other;
            result = label.equals(otherVertex.label);//节点是否相同最终还是由标识 节点类型的类的equals() 决定
        }
        return result;
    }
}
