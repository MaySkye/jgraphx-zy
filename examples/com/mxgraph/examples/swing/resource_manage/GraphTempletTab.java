package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.frame.CellEditorFrame;
import com.mxgraph.examples.swing.util.AliasName;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.swing.*;
import java.util.List;
import java.util.Optional;


public class GraphTempletTab extends Tab {

    private ObservableList<String> graphData; // grpListView
    private String lastGraph = null;
    private ObservableList<String> deviceData; // deviceListView
    private ObservableList<String> linkData; // linkListView

    public GraphTempletTab(String title) {
        super(title);
        setClosable(false);
        initGrpData();
        setContent(creatTabContent());
    }

    private void initGrpData() {
        List<String> graphList = GraphUtil.listGraph();
        graphData = FXCollections.observableList(graphList);
    }

    private ObservableList<String> getGraphData() {
        return FXCollections.observableList(GraphUtil.listGraph());
    }

    private Node creatTabContent() {

        ListView<String> grpListView = new ListView<>(graphData);
        ListView<String> deviceListView = new ListView<>(deviceData);
        ListView<String> linkListView = new ListView<>(linkData);
        ImageView grpImageView = new ImageView();
        BorderPane wrapBorderPane = new BorderPane();
        wrapBorderPane.setCenter(grpImageView);
        wrapBorderPane.setPadding(new Insets(20, 20, 20, 20));

        grpListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());
        grpListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        deviceListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());
        deviceListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        linkListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());
        linkListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        grpListView.setOnMouseClicked(event -> {
            String graphName = grpListView.getSelectionModel().getSelectedItem();//graphName
            System.out.println("graphName:" + graphName);
            if (graphName == null || graphName.equals(lastGraph)) {
                return;
            }
            lastGraph = graphName;
            String imgPath = GraphUtil.getImgPath(graphName);
            grpImageView.setImage(new Image(imgPath, 350, 350, true, true));

            deviceData=getDeviceByGraphName(graphName);
            deviceListView.setItems(deviceData);
            deviceListView.refresh();

            linkData=getLinkByGraphName(graphName);
            linkListView.setItems(linkData);
            linkListView.refresh();

        });


        HBox grpHBox = new HBox();
        grpHBox.setPrefSize(250, 25);
        grpHBox.setPadding(new Insets(5, 10, 5, 10));
        grpHBox.setSpacing(5);
        Button addDel = new Button("创建");
        addDel.setOnMouseClicked(event -> makeGraphAction(grpListView));
        Button grpDel = new Button("删除");
        grpDel.setOnMouseClicked(event -> deleteGraphAction(grpListView));
        Button grpEdit = new Button("修改");
        grpEdit.setOnMouseClicked(event -> editGraphAction(grpListView));
        grpHBox.getChildren().addAll(addDel,grpDel, grpEdit);

        VBox vBox = new VBox(15);
        vBox.setPadding(new Insets(15, 5, 5, 5));
        Label lbDev = new Label("设备:");
        Label lbLink = new Label("连线:");
        vBox.getChildren().add(lbDev);
        vBox.getChildren().add(deviceListView);
        vBox.getChildren().add(lbLink);
        vBox.getChildren().add(linkListView);

        Label grpLabel = new Label("模板名称");
        Label imgLabel = new Label("模板预览");
        Label infoLabel = new Label("模板信息简介");

        grpLabel.setFont(new Font(14));
        grpLabel.setAlignment(Pos.CENTER_LEFT);
        grpLabel.setPrefHeight(20);
        grpLabel.setPadding(new Insets(5, 5, 5, 10));

        imgLabel.setFont(new Font(14));
        imgLabel.setAlignment(Pos.CENTER_LEFT);
        imgLabel.setPrefHeight(20);
        imgLabel.setPadding(new Insets(5, 5, 5, 10));

        infoLabel.setFont(new Font(14));
        infoLabel.setAlignment(Pos.CENTER_LEFT);
        infoLabel.setPrefHeight(20);
        infoLabel.setPrefWidth(300);
        infoLabel.setPadding(new Insets(5, 5, 5, 10));
        infoLabel.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        BorderPane grpBorderPane = new BorderPane();
        BorderPane imgBorderPane = new BorderPane();
        BorderPane infoBorderPane = new BorderPane();

        grpBorderPane.setPrefHeight(400);
        imgBorderPane.setPrefHeight(400);
        grpBorderPane.setPrefWidth(250);
        imgBorderPane.setPrefWidth(400);
        infoBorderPane.setPrefHeight(400);
        infoBorderPane.setPrefWidth(300);

        grpBorderPane.setTop(grpLabel);
        grpBorderPane.setCenter(grpListView);
        grpBorderPane.setBottom(grpHBox);
        imgBorderPane.setTop(imgLabel);
        imgBorderPane.setCenter(wrapBorderPane);
        infoBorderPane.setTop(infoLabel);
        infoBorderPane.setCenter(vBox);


        grpBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        imgBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        infoBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        wrapBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        HBox graphHBox = new HBox();
        graphHBox.getChildren().addAll(grpBorderPane, imgBorderPane,infoBorderPane);

        return graphHBox;
    }

    private ObservableList<String> getDeviceByGraphName(String name) {
        List<String> deviceList = GraphUtil.getDeviceInfo(name);
        return FXCollections.observableList(deviceList);
    }

    private ObservableList<String> getLinkByGraphName(String name) {
        List<String> linkList = GraphUtil.getLinkInfo(name);
        return FXCollections.observableList(linkList);
    }

    private void deleteGraphAction(ListView<String> grpListView) {
       /* System.out.println("press button: grp_remove");
        if (lastGraph == null) {
            JOptionPane.showMessageDialog(null, AliasName.getAlias("please_select_cfg_graph_name"),
                    AliasName.getAlias("remove_fail"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(null, AliasName.getAlias("confirm_remove_cfg_graph") + " :" + lastGraph,
                AliasName.getAlias("confirm"), JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        GraphUtil.quickDel(lastGraph);
        grpListView.setItems(getGraphData());
        grpListView.refresh();*/
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("提示");
        alert.setHeaderText("确定要删除 模板1 模板吗？");
        alert.setContentText("");

        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            //info.setText(“btn6 点击了确定”);
        } else {
            //info.setText(“btn6 点击了取消”);
        }

    }

    private void editGraphAction(ListView<String> grpListView) {

       System.out.println("press button: grp_edit");
        if (lastGraph == null) {
            JOptionPane.showMessageDialog(null, AliasName.getAlias("please_select_cfg_graph_name"),
                    AliasName.getAlias("rename_fail"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("提示");
        alert.setHeaderText("确定要修改 模板4 模板吗？");
        alert.setContentText("");

        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            CellEditorFrame.CustomGraphComponent c = new CellEditorFrame.CustomGraphComponent(new CellEditorFrame.CustomGraph());
            CellEditorFrame editor = new CellEditorFrame("模板图编辑器", c);
            JFrame cellMakeframe = editor.createCellFrame();
            c.setFrame(cellMakeframe);
            cellMakeframe.setVisible(true);
        } else {
            //info.setText(“btn6 点击了取消”);
        }

    }

    private void makeGraphAction(ListView<String> grpListView) {
        System.out.println("press button: grp_edit");
        if (lastGraph == null) {
            JOptionPane.showMessageDialog(null, AliasName.getAlias("please_select_cfg_graph_name"),
                    AliasName.getAlias("rename_fail"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CellEditorFrame.CustomGraphComponent c = new CellEditorFrame.CustomGraphComponent(new CellEditorFrame.CustomGraph());
        CellEditorFrame editor = new CellEditorFrame("模板图编辑器", c);
        JFrame cellMakeframe = editor.createCellFrame();
        c.setFrame(cellMakeframe);
        cellMakeframe.setVisible(true);
    }
}
