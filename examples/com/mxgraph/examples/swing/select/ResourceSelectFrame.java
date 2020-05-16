package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.owl.*;
import com.mxgraph.examples.swing.resource_manage.ResourceManageFrame;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.editorUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

import static com.mxgraph.examples.swing.owl.OwlResourceUtil.findKind;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 12:36 2020/5/15
 * @Modify By:
 */
public class ResourceSelectFrame extends Application {

    private BasicGraphEditor editor;
    private OwlResourceData origin_owlResourceData;
    private OwlResourceData new_owlResourceData;
    private Map<String, OwlObject> origin_objMap;
    private Map<String, OwlObject> new_objMap;
    private Map<String, OwlObject> main_objMap=new HashMap<>();
    private Map<String, Pane> vertex_objMap=new HashMap<>();
    private Map<String, OwlObject> vertex_main_objMap=new HashMap<>();
    private Map<Line, String> edge_objMap=new HashMap<>();
    private String siteName;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.editor = editorUtil.getEditor();
        this.origin_owlResourceData = this.editor.getOrigin_owlResourceData();
        this.new_owlResourceData = this.editor.getNew_owlResourceData();
        this.origin_objMap = this.origin_owlResourceData.objMap;
        this.new_objMap = this.new_owlResourceData.objMap;

        initSiteName();
        //总布局
        BorderPane pane = new BorderPane();
        creatTabContent(pane);
        Scene scene = new Scene(pane, 1000, 500);
        primaryStage.setTitle("自定义监控资源模板界面");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void creatTabContent(BorderPane pane){

        //按钮布局
        FlowPane flowPane = new FlowPane();
        flowPane.setPadding(new Insets(11, 12, 13, 14));
        flowPane.setHgap(5);//设置控件之间的垂直间隔距离
        flowPane.setVgap(5);//设置控件之间的水平间隔距离
        flowPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        Button openBtn = new Button("打开");
        Button saveBtn = new Button("保存");
        Button backBtn = new Button("还原");
        Button refreshBtn = new Button("刷新");
        Button diagramBtn = new Button("生成组态图");
        setButton(openBtn);
        setButton(saveBtn);
        setButton(backBtn);
        setButton(refreshBtn);
        setButton(diagramBtn);
        flowPane.getChildren().addAll(openBtn,saveBtn,backBtn,refreshBtn,diagramBtn);

        //拓扑图布局
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(700,400);
        borderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        Label grpLabel = new Label("监控资源模板拓扑图");
        grpLabel.setFont(new Font(14));
        grpLabel.setAlignment(Pos.CENTER_LEFT);
        grpLabel.setPrefHeight(20);
        grpLabel.setPrefWidth(700);
        grpLabel.setPadding(new Insets(5, 5, 5, 10));
        //grpLabel.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        borderPane.setTop(grpLabel);

        /*Canvas canvas = new Canvas(300, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2.0);
        gc.setFill(Color.RED);
        gc.strokeRoundRect(10, 10, 50, 50, 10, 10);
        gc.fillOval(70, 10, 50, 20);
        gc.strokeText("Hello Canvas", 150, 20);
        borderPane.setCenter(canvas);*/

        Pane graph = paint(borderPane.getHeight()-grpLabel.getHeight()-10,borderPane.getWidth()-borderPane.getHeight()-10);
        graph.setPrefWidth(700);
        graph.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        borderPane.setCenter(graph);

        //信息框布局
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 5, 5, 5));
        vBox.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        Label infoLabel = new Label("资源信息预览");
        infoLabel.setFont(new Font(14));
        infoLabel.setAlignment(Pos.CENTER_LEFT);
        infoLabel.setPrefHeight(20);
        infoLabel.setPrefWidth(300);
        infoLabel.setPadding(new Insets(5, 5, 5, 10));
        //infoLabel.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        vBox.getChildren().add(infoLabel);

        Label lbDevInfo = new Label("基本信息:");
        Label lbProperty = new Label("监测属性:");
        Label lbLink = new Label("连接信息:");
        BorderPane devInfoBorderPane = new BorderPane();
        VBox propertyBox = new VBox();
        VBox linkBox = new VBox();
        devInfoBorderPane.setPrefSize(300,400);
        propertyBox.setPrefSize(300,400);
        linkBox.setPrefSize(300,400);

        //包含三个信息框--基本信息、监测属性、连接属性
        //基本信息--列表
        List<String> infoList = new ArrayList<>();
        infoList.add("名称：设备");
        infoList.add("----------");
        infoList.add("----------");
        ListView<String> infoListView = new ListView<>(FXCollections.observableList(infoList));
        infoListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());
        infoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        infoListView.setOnMouseClicked(event -> {
            String info = infoListView.getSelectionModel().getSelectedItem();//info
        });
        devInfoBorderPane.setTop(lbDevInfo);
        devInfoBorderPane.setCenter(infoListView);

        //监测属性--多选框
        CheckBox ck1 = new CheckBox("麻婆豆腐"); // 创建一个复选框
        CheckBox ck2 = new CheckBox("清蒸桂花鱼"); // 创建一个复选框
        CheckBox ck3 = new CheckBox("香辣小龙虾"); // 创建一个复选框
        propertyBox.getChildren().add(lbProperty);
        propertyBox.getChildren().add(ck1);
        propertyBox.getChildren().add(ck2);
        propertyBox.getChildren().add(ck3);

        //连接属性--多选框
        linkBox.getChildren().add(lbLink);
        linkBox.getChildren().add(ck1);

        vBox.getChildren().add(devInfoBorderPane);
        vBox.getChildren().add(propertyBox);
        vBox.getChildren().add(linkBox);

        pane.setTop(flowPane);
        pane.setCenter(borderPane);
        pane.setRight(vBox);
    }

    private void setButton(Button btn){
        btn.setFont(new Font(12));
        btn.setAlignment(Pos.CENTER);
        btn.setPadding(new Insets(5, 5, 5, 10));
    }

    private void initSiteName(){

        if(new_objMap == null){
            System.out.println("new_objMap is null!");
            return;
        }
        for (Map.Entry<String, OwlObject> entry : new_objMap.entrySet()) {
            if((findKind(entry.getValue().type).equals("Site")) &&entry.getValue().visible){
                for (Map.Entry<OwlDataAttribute, Set<Object>> entrys : entry.getValue().dataAttrs.entrySet()) {
                    //得到站点名称
                    System.out.println("id: "+entrys.getKey().id+" size: "+entrys.getValue().size());
                    if (entrys.getKey().id.equals("站点名称")&&entrys.getValue().size()!=0) {
                        for (Object obj : entrys.getValue()) {
                            siteName = obj.toString();
                            System.out.println("siteName: "+siteName);
                            return;
                        }
                    }
                }
            }
        }
    }

    private Pane paint(double height,double width){
        System.out.println("height: "+height+" width:"+width);
        Pane graph = new Pane();
        main_objMap.clear(); //<url,OwlObject> 存储类型为FOI的对象
        vertex_main_objMap.clear(); //<id,OwlObject> 方便查找对象信息
        vertex_objMap.clear(); //<id,Object>  方便查找图形对象，进行图形的增删改查
        init_main_objMap(new_objMap,main_objMap);
        //先判断个数
        int num= OwlResourceUtil.getFOINum(main_objMap);
        //分网格
        int part_num=getPart_num(num);
        int part_height= (int) (height/part_num);
        int part_width= (int) (width/part_num);
        System.out.println("part_num: "+part_num+" part_height:"+part_height+" part_width:"+part_width);

        //绘制顶点
        int i=0;
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            //矩形左上角的坐标
            int x=part_width*(i%part_num)+part_width/3;
            int y=part_height*(i/part_num)+part_height/5;
            //显示的属性值
            String attr_str="";
            for (Map.Entry<OwlObjectAttribute, Set<OwlObject>> entry1 : entry.getValue().objAttrs.entrySet()) {
                if(entry1.getKey().id.equals("has_property")){
                    for (OwlObject obj : entry1.getValue()) {
                        if(obj.visible){
                            attr_str=attr_str+obj.id;
                        }
                    }
                }
            }
            Pane node = generateCircleNode(entry.getValue().id);
            // 将结点重定位到某一位置
            System.out.println("nodeX: "+x+" nodeY: "+y);
            node.relocate(x, y);
            // 使结点可拖拽
            draggable(node);
            graph.getChildren().add(node);
            vertex_objMap.put(entry.getValue().id,node);
            vertex_main_objMap.put(entry.getValue().id,entry.getValue());
            i++;
        }

        //绘制连线，根据坐标来找绘制的图形
        for (Map.Entry<String, OwlObject> entry : main_objMap.entrySet()) {
            entry.getValue().objAttrs.forEach((objAttr, objSet) -> {
                if(objAttr.id.equals("connect")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible) {
                            // 创建直线
                            Line line = new Line();
                            // 将直线的起点坐标与 node1 的中心坐标进行绑定
                            Pane node1 = vertex_objMap.get(entry.getValue().id);
                            Pane node2 = vertex_objMap.get(obj2.id);
                            line.startXProperty().bind(node1.layoutXProperty().add(node1.widthProperty().divide(2)));
                            line.startYProperty().bind(node1.layoutYProperty().add(node1.heightProperty().divide(2)));
                            // 将直线的终点坐标与 node2 的中心坐标进行绑定
                            line.endXProperty().bind(node2.layoutXProperty().add(node2.widthProperty().divide(2)));
                            line.endYProperty().bind(node2.layoutYProperty().add(node2.heightProperty().divide(2)));
                            graph.getChildren().add(line);
                            edge_objMap.put(line,entry.getValue().id+"-"+obj2.id);
                        }
                    });
                }
                if(objAttr.id.equals("data_trans")){
                    objSet.forEach(obj2 -> {
                        if(obj2.visible) {
                            // 创建直线
                            Line line = new Line();
                            // 将直线的起点坐标与 node1 的中心坐标进行绑定
                            Pane node1 = vertex_objMap.get(entry.getValue().id);
                            Pane node2 = vertex_objMap.get(obj2.id);
                            line.startXProperty().bind(node1.layoutXProperty().add(node1.widthProperty().divide(2)));
                            line.startYProperty().bind(node1.layoutYProperty().add(node1.heightProperty().divide(2)));
                            // 将直线的终点坐标与 node2 的中心坐标进行绑定
                            line.endXProperty().bind(node2.layoutXProperty().add(node2.widthProperty().divide(2)));
                            line.endYProperty().bind(node2.layoutYProperty().add(node2.heightProperty().divide(2)));
                            graph.getChildren().add(line);
                            edge_objMap.put(line,entry.getValue().id+"-"+obj2.id);
                        }
                    });
                }
            });
        }
        return graph;
    }

    private Map<String, OwlObject> init_main_objMap(Map<String, OwlObject> objMap,Map<String, OwlObject> main_objMap){
        for (Map.Entry<String, OwlObject> entry : objMap.entrySet()) {
            if((findKind(entry.getValue().type).equals("FeatureOfInterest") ||findKind(entry.getValue().type).equals("ControlRoom"))
                    &&entry.getValue().visible){
                main_objMap.put(entry.getKey(),entry.getValue());
            }
        }
        return main_objMap;
    }

    private int getPart_num(int num){
        int n;
        double m=Math.sqrt(num);
        System.out.println("m:"+m);
        n=(int)m;
        System.out.println("n:"+n);
        if(n==m){
            return n;
        }else{
            return n+1;
        }
    }

    private static class Position {
        double x;
        double y;
    }

    private void draggable(Node node) {
        final Position pos = new Position();

        // 提示用户该结点可点击
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> node.setCursor(Cursor.HAND));
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> node.setCursor(Cursor.DEFAULT));

        // 提示用户该结点可拖拽
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            node.setCursor(Cursor.MOVE);

            // 当按压事件发生时，缓存事件发生的位置坐标
            pos.x = event.getX();
            pos.y = event.getY();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> node.setCursor(Cursor.DEFAULT));

        // 实现拖拽功能
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            double distanceX = event.getX() - pos.x;
            double distanceY = event.getY() - pos.y;

            double x = node.getLayoutX() + distanceX;
            double y = node.getLayoutY() + distanceY;

            // 计算出 x、y 后将结点重定位到指定坐标点 (x, y)
            node.relocate(x, y);
        });

        //点击刷新信息
        node.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            String name = node.getId();
            System.out.println("nodeName: "+name);
        });
    }

    private Pane generateCircleNode(String data) {
        Pane node = new StackPane();

        Circle circle = new Circle(20);
        circle.setStyle("-fx-fill: rgb(51,184,223)");

        Text text = new Text(data);
        text.setStyle("-fx-fill: rgb(93,93,93);-fx-font-weight: bold;");

        node.setId(data);
        node.getChildren().addAll(circle, text);

        return node;
    }

    public void show() {
        ResourceSelectFrame.launch();
    }

    /*public static void main(String[] args) {
        new ResourceSelectFrame().show();
    }*/
}
