package com.mxgraph.examples.swing.select;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.DefaultFileFilter;
import com.mxgraph.examples.swing.editor.EditorActions;
import com.mxgraph.examples.swing.graph.GraphInterface;
import com.mxgraph.examples.swing.graph.showGraph;
import com.mxgraph.examples.swing.match.ModifyTemplateCore;
import com.mxgraph.examples.swing.match.ResMatchCore;
import com.mxgraph.examples.swing.owl.*;
import com.mxgraph.examples.swing.resource_manage.ResourceManageFrame;
import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.FileUtil;
import com.mxgraph.examples.swing.util.editorUtil;
import com.mxgraph.examples.swing.util.ww.WWFiberManager;
import com.mxgraph.io.mxCodec;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;

import static com.mxgraph.examples.swing.editor.EditorActions.showTitle;
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
    private Map<String, OwlObject> main_objMap=new HashMap<>(); //<url,OwlObject> 存储所有的类型为FOI或ControlRoom的对象
    private Map<String, OwlObject> vertex_main_objMap=new HashMap<>(); //<id,OwlObject> 方便查找对象信息，存储当前显示的设备
    private Map<String, Pane> vertex_objMap=new HashMap<>(); //<id,Pane>  方便查找顶点对象，进行设备的增删改查
    private Map<String, Line> edge_objMap=new HashMap<>(); //<id,Line>  方便查找连线对象，进行连线的增删改查
    private String siteName;
    private String filePath;

    private List<String> devInfoList = new ArrayList<>();
    private List<CheckBox> propertyCkList = new ArrayList<>();
    private List<CheckBox> linkCkList = new ArrayList<>();
    private BorderPane devInfoBorderPane;
    private VBox propertyBox ;
    private VBox linkBox ;
    private Pane graphPane;
    private CheckBox checkBox;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.editor = editorUtil.getEditor();
        this.origin_owlResourceData = this.editor.getOrigin_owlResourceData();
        this.new_owlResourceData = this.editor.getNew_owlResourceData();
        this.origin_objMap = this.origin_owlResourceData.objMap;
        this.new_objMap = this.new_owlResourceData.objMap;
        this.filePath = editor.getResourceFile();

        initSiteName();
        //总布局
        BorderPane pane = new BorderPane();
        creatTabContent(pane);
        Scene scene = new Scene(pane, 1000, 500);
        primaryStage.setTitle("自定义监控资源模板界面");
        primaryStage.setScene(scene);
        primaryStage.show();
        this.primaryStage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

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
        openBtn.setOnMouseClicked(event -> openBtnAction());
        saveBtn.setOnMouseClicked(event -> saveBtnAction());
        backBtn.setOnMouseClicked(event -> backBtnAction());
        refreshBtn.setOnMouseClicked(event -> refreshBtnAction());
        diagramBtn.setOnMouseClicked(event -> diagramBtnAction());

        //拓扑图布局
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(700,400);
        //borderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        Pane p =new Pane();
        p.setPrefWidth(700);
        p.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        Label grpLabel = new Label("监控资源模板拓扑图");
        grpLabel.setFont(new Font(14));
        grpLabel.setAlignment(Pos.CENTER_LEFT);
        grpLabel.setPrefHeight(20);
        grpLabel.setPrefWidth(700);
        grpLabel.setPadding(new Insets(5, 5, 5, 10));
        //grpLabel.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        p.getChildren().add(grpLabel);
        borderPane.setTop(p);

        /*Canvas canvas = new Canvas(300, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2.0);
        gc.setFill(Color.RED);
        gc.strokeRoundRect(10, 10, 50, 50, 10, 10);
        gc.fillOval(70, 10, 50, 20);
        gc.strokeText("Hello Canvas", 150, 20);
        borderPane.setCenter(canvas);*/

        graphPane = paint(borderPane.getHeight()-grpLabel.getHeight()-10,borderPane.getWidth()-borderPane.getHeight()-10);
        graphPane.setPrefWidth(700);
        graphPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        borderPane.setCenter(graphPane);

        //信息框布局
        VBox vBox = new VBox();
        //vBox.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        Label infoLabel = new Label("资源信息预览");
        infoLabel.setFont(new Font(14));
        infoLabel.setAlignment(Pos.CENTER_LEFT);
        infoLabel.setPrefHeight(20);
        infoLabel.setPrefWidth(300);
        infoLabel.setPadding(new Insets(5, 5, 5, 10));
        infoLabel.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        vBox.getChildren().add(infoLabel);

        checkBox = new CheckBox("将该设备添加到监控图");
        checkBox.setSelected(true);
        checkBox.setPadding(new Insets(5, 5, 5, 10));
        vBox.getChildren().add(checkBox);

        Label lbDevInfo = new Label("基本信息:");
        Label lbProperty = new Label("监测属性:");
        Label lbLink = new Label("连接信息:");
        lbDevInfo.setPadding(new Insets(3, 3, 3, 5));
        lbProperty.setPadding(new Insets(3, 3, 3, 5));
        lbLink.setPadding(new Insets(3, 3, 3, 5));
        devInfoBorderPane = new BorderPane();
        propertyBox = new VBox();
        linkBox = new VBox();
        devInfoBorderPane.setPrefSize(300,400);
        propertyBox.setPrefSize(300,400);
        linkBox.setPrefSize(300,400);
        //devInfoBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        //propertyBox.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        //linkBox.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        //基本信息--字符串列表
        vBox.getChildren().add(lbDevInfo);
        vBox.getChildren().add(devInfoBorderPane);
        //监测属性--多选框列表
        vBox.getChildren().add(lbProperty);
        vBox.getChildren().addAll(propertyBox);
        //连接属性--多选框列表
        vBox.getChildren().add(lbLink);
        vBox.getChildren().addAll(linkBox);

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

    private void initList(OwlObject owlObject){
        devInfoList.clear();
        propertyCkList.clear();
        linkCkList.clear();

        checkBox.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            if (checkBox.isSelected()) {
                owlObject.visible = true;
            } else {
                owlObject.visible = false;
            }
        });

        String s1 = "Name: "+owlObject.id;
        String s2 = "Type: "+owlObject.type.id;
        devInfoList.add(s1);
        devInfoList.add(s2);
        for(Map.Entry<OwlDataAttribute, Set<Object>> entry : owlObject.dataAttrs.entrySet()){
            if(entry.getKey().id!=null){
                for (Object owlobj : entry.getValue()) {
                    System.out.println("entry.getKey().id: "+entry.getKey().id+"---:"+owlobj.toString());
                    String s = entry.getKey().id+": "+owlobj.toString();
                    devInfoList.add(s);
                }
            }
        }

        for (Map.Entry<OwlObjectAttribute, Set<OwlObject>> e : owlObject.objAttrs.entrySet()) {
            if (e.getKey().id.equals("has_property")) {
                for (OwlObject obj : e.getValue()) {
                    CheckBox ck = new CheckBox(obj.id);
                    ck.setSelected(obj.visible);
                    propertyCkList.add(ck);
                    ck.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
                        if (ck.isSelected()) {
                            obj.visible = true;
                        } else {
                            obj.visible = false;
                        }
                    });
                }
            }
            if (e.getKey().id.equals("connect") || e.getKey().id.equals("data_trans")) {
                for (OwlObject obj : e.getValue()) {
                    if (obj.visible) {
                        String s = owlObject.id + "-" + obj.id;
                        CheckBox ck = new CheckBox(s);
                        ck.setSelected(true);
                        linkCkList.add(ck);
                    }
                }
            }
        }
    }

    private void updateVBox(BorderPane devInfoBorderPane,VBox propertyBox,VBox linkBox){
        devInfoBorderPane.getChildren().clear();
        propertyBox.getChildren().clear();
        linkBox.getChildren().clear();

        ListView<String> infoListView = new ListView<>(FXCollections.observableList(devInfoList));
        infoListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());
        infoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        infoListView.setOnMouseClicked(event -> {
            String info = infoListView.getSelectionModel().getSelectedItem();//info
        });

        ListView<CheckBox> propertyListView = new ListView<>(FXCollections.observableList(propertyCkList));
        propertyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ListView<CheckBox> linkListView = new ListView<>(FXCollections.observableList(linkCkList));
        linkListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        devInfoBorderPane.setCenter(infoListView);
        propertyBox.getChildren().add(propertyListView);
        linkBox.getChildren().add(linkListView);
    }

    private Pane paint(double height,double width){
        System.out.println("height: "+height+" width:"+width);
        Pane graph = new Pane();
        main_objMap.clear();
        vertex_main_objMap.clear();
        vertex_objMap.clear();
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
                            edge_objMap.put(entry.getValue().id+"-"+obj2.id,line);
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
                            edge_objMap.put(entry.getValue().id+"-"+obj2.id,line);
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
            initList(vertex_main_objMap.get(name));
            updateVBox(devInfoBorderPane,propertyBox,linkBox);
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

    private void openBtnAction() {
        System.out.println("press button: open");

    }

    private void saveBtnAction() {
        System.out.println("press button: save");
        //保存为xmodel文件
        FileFilter selectedFilter;
        String filename;
        String filename_xmodel = "" ;
        String filename_xmi = "";
        String lastDir = FileUtil.getAppPath();
        System.out.println("FileUtil.getAppPath(): "+FileUtil.getAppPath());
        String wd;
        if (lastDir != null) {
            wd = lastDir;
        } else {
            wd = FileUtil.getAppPath();
        }
        JFileChooser fc = new JFileChooser(wd);
        // Adds the default file format
        DefaultFileFilter defaultFilter = new DefaultFileFilter(".xmodel",
                "xmodel file " + mxResources.get("file") + " (.xmodel)");
        // Adds special vector graphics formats and HTML
        fc.addChoosableFileFilter(defaultFilter);

        // Adds filter that accepts all supported image formats
        fc.setFileFilter(defaultFilter);
        int rc = fc.showDialog(null, mxResources.get("save"));

        if (rc != JFileChooser.APPROVE_OPTION) {
            return;
        } else {
            lastDir = fc.getSelectedFile().getParent();
        }

        filename = fc.getSelectedFile().getAbsolutePath();
        selectedFilter = fc.getFileFilter();
        if (selectedFilter == null || !selectedFilter.equals(defaultFilter)) {
            return;
        }

        String ext = ((DefaultFileFilter) selectedFilter).getExtension();

        if (!filename.toLowerCase().endsWith(ext)) {
            filename_xmodel =filename+ ext;
        }

       /* if (new File(filename_xmodel).exists() && JOptionPane.showConfirmDialog(graphComponent,
                mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
            return;
        }

        System.out.println("filename:"+filename);
        System.out.println("filename_xmodel:"+filename_xmodel);
        System.out.println("filename_xmi:"+filename_xmi);

        // save as xmodel file
        mxCodec codec = new mxCodec();
        graphPane.g
        String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

        try {
            mxUtils.writeFile(xml, filename_xmodel);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        SimplifyModelInfo ModelInfo =new SimplifyModelInfo(
                editor.getResourceFile(),
                editor.getNew_owlResourceData().classMap,
                editor.getNew_owlResourceData().objAttrMap,
                editor.getNew_owlResourceData().dataAttrMap,
                editor.getNew_owlResourceData().objMap,
                main_objMap,
                vertex_main_objMap,
                vertex_objMap,
                edge_objMap
        );

        FileOutputStream outStream = null;
        filename_xmi = filename+".xmi";
        System.out.println("filename_xmi:"+filename_xmi);
        try {
            outStream = new FileOutputStream(filename_xmi);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(ModelInfo);
            outStream.close();
            System.out.println("successful");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }*/
    }


    private void backBtnAction() {
        System.out.println("press button: back");

    }

    private void refreshBtnAction() {
        System.out.println("press button: refresh");
        graphPane.getChildren().clear();
        //graphPane = paint();


    }

    private void diagramBtnAction() {
        System.out.println("press button: diagram");
         //匹配模板图时使用如下方法
        //为graph匹配相似度最高的match_graph,得到对应模板图的文件名
        OwlResourceUtil.print(new_owlResourceData);

        String templatePath= ResMatchCore.getTemplatePath(new_owlResourceData);
        System.out.println("templatePath:"+templatePath);

        /*此段代码可以显示出模板mxe文件*/
        if (templatePath == null) {
            return;
        }
        Document document = null;
        try {
            document = mxXmlUtils.parseXml(FileUtil.readFile(this.getClass().getResourceAsStream(templatePath)));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        mxCodec codec = new mxCodec(document);
        codec.decode(document.getDocumentElement(), editor.getGraphComponent().getGraph().getModel());
        // 根据资源模型文件，对模板组态图进行调整,
        // 则可以显示出调整好的组态图，图元没有绑定资源信息
        GraphInterface<String> graph= showGraph.createGraph(new_owlResourceData);
        new ModifyTemplateCore(templatePath).postProcess(graph,editor,filePath);

        //添加标题
        showTitle(siteName,editor);
        editor.getGraphComponent().getGraph().refresh();
        editor.getOrigin_owlResourceData().title = siteName;
        editor.getNew_owlResourceData().title = siteName;

        // 王伟：todo
        WWFiberManager.doHandleForGraph(editor.getGraphComponent().getGraph());
        primaryStage.close();
        System.exit(0);
    }


    public void show() {
        ResourceSelectFrame.launch();
    }

    public static void main(String[] args) {
        //new ResourceSelectFrame().show();
        List<String> candidateList =new ArrayList<>();
        candidateList.add("111");
        candidateList.add("222");
        Object selectedVaule = JOptionPane.showInputDialog(null, AliasName.getAlias("select_template"),
                AliasName.getAlias("select"), JOptionPane.INFORMATION_MESSAGE, null,
                candidateList.toArray(), candidateList.get(0));
    }
}
