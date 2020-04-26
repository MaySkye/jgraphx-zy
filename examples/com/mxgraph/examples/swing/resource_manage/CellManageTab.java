package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.frame.CellEditorFrame;
import com.mxgraph.examples.swing.match.CellTypeUtil;
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
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CellManageTab extends Tab {

    private static final String ALL_TYPE = "所有类型";

    private ObservableList<String> typeData; // typeListView
    private ObservableList<String> nameData; // infoListView
    private String oldType = null;
    private String oldCell = null;

    public CellManageTab(String title) {
        super(title);
        setClosable(false);
        initTypeData();
        setContent(creatTabContent());
    }

    private void initTypeData() {
        List<String> typeList = CellTypeUtil.typeList;
        List<String> adapterList = new ArrayList<>(typeList.size() + 1);
        adapterList.add(AliasName.getAlias(ALL_TYPE));
        typeList.forEach(str -> {
            adapterList.add(AliasName.getAlias(str));
        });
        typeData = FXCollections.observableList(adapterList);
    }

    private ObservableList<String> getCellsByType(String type) {
        List<String> infoList = null;
        if (ALL_TYPE.equals(type)) {
            infoList = new ArrayList<>();
            List<String> typeList = CellTypeUtil.typeList;
            for (int i = 0; i < typeList.size(); ++i) {
                String t = typeList.get(i);
                infoList.addAll(CellTypeUtil.getCells(t));
            }
        } else {
            infoList = CellTypeUtil.getCells(type);
        }
        return FXCollections.observableList(infoList);
    }

    private Node creatTabContent() {
        // TODO
        ListView<String> typeListView = new ListView<>(typeData);
        ListView<String> infoListView = new ListView<>(nameData);
        ImageView cllImageView = new ImageView();
        BorderPane wrapBorderPane = new BorderPane();
        wrapBorderPane.setCenter(cllImageView);
        wrapBorderPane.setPadding(new Insets(20, 20, 20, 20));

        typeListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());
        infoListView.setCellFactory(list -> new ResourceManageFrame.ListViewCell());

        typeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        infoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        typeListView.setOnMouseClicked(event -> {
            String type = typeListView.getSelectionModel().getSelectedItem();//type
            if (type == null || type.equals(oldType)) {
                return;
            }
            oldType = type;
            oldCell = null;
            type = AliasName.getReverse(type);
            nameData = getCellsByType(type);
            infoListView.setItems(nameData);
            infoListView.refresh();
            cllImageView.setImage(null);
        });
        infoListView.setOnMouseClicked(event -> {
            String cell = infoListView.getSelectionModel().getSelectedItem();
            if (cell == null || cell.equals(oldCell)) {
                return;
            }
            oldCell = cell;
            String imgPath = CellInfoUtil.getIcon(cell);
            cllImageView.setImage(new Image(imgPath, 200, 200, true, true));
        });

        Label typeLabel = new Label("图元类型");
        Label nameLabel = new Label("图元名称");
        Label imgLabel = new Label( "图元展示");
        Label infoLabel = new Label("图元信息");

        typeLabel.setFont(new Font(14));
        typeLabel.setAlignment(Pos.CENTER_LEFT);
        typeLabel.setPrefSize(250, 20);
        typeLabel.setPadding(new Insets(5, 5, 5, 10));

        nameLabel.setFont(new Font(14));
        nameLabel.setAlignment(Pos.CENTER_LEFT);
        nameLabel.setPrefSize(250, 20);
        nameLabel.setPadding(new Insets(5, 5, 5, 10));

        imgLabel.setFont(new Font(14));
        imgLabel.setAlignment(Pos.CENTER_LEFT);
        imgLabel.setPrefSize(300, 20);
        imgLabel.setPadding(new Insets(5, 5, 5, 10));

        infoLabel.setFont(new Font(14));
        infoLabel.setAlignment(Pos.CENTER_LEFT);
        infoLabel.setPrefSize(200, 20);
        infoLabel.setPadding(new Insets(5, 5, 5, 10));

        Button addCell = new Button("图元引入");
        addCell.setFont(new Font(12));
        addCell.setAlignment(Pos.CENTER_LEFT);
        addCell.setPadding(new Insets(5, 5, 5, 10));
        addCell.setOnMouseClicked(event -> addCellAction());
        Button makeCell = new Button("图元制作");
        makeCell.setFont(new Font(12));
        makeCell.setAlignment(Pos.CENTER_LEFT);
        makeCell.setPadding(new Insets(5, 5, 5, 10));
        makeCell.setOnMouseClicked(event -> makeCellAction());
        GridPane buttonPane = new GridPane();
        buttonPane.setPrefHeight(200);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        buttonPane.setHgap(40);
        buttonPane.setVgap(40);
        buttonPane.add(addCell,0,0);
        buttonPane.add(makeCell,0,1);

        Button deleteCell = new Button("删除");
        deleteCell.setFont(new Font(10));
        deleteCell.setAlignment(Pos.CENTER_LEFT);
        deleteCell.setPadding(new Insets(5, 5, 5, 10));
        deleteCell.setOnMouseClicked(event -> deleteCellAction(oldCell,typeListView));
        GridPane deleteCellPane = new GridPane();
        deleteCellPane.setPadding(new Insets(5, 12.5, 5, 14.5));
        deleteCellPane.setAlignment(Pos.CENTER_RIGHT);
        deleteCellPane.add(deleteCell,0,0);


        BorderPane typeBorderPane = new BorderPane();
        BorderPane nameBorderPane = new BorderPane();
        BorderPane imgBorderPane = new BorderPane();
        BorderPane infoBorderPane = new BorderPane();
        BorderPane btnPane = new BorderPane();

        typeBorderPane.setPrefHeight(400);
        nameBorderPane.setPrefHeight(400);
        imgBorderPane.setPrefHeight(400);
        infoBorderPane.setPrefHeight(400);
        btnPane.setPrefHeight(400);
        btnPane.setPrefWidth(300);

        typeBorderPane.setTop(typeLabel);
        typeBorderPane.setCenter(typeListView);

        nameBorderPane.setTop(nameLabel);
        nameBorderPane.setCenter(infoListView);

        imgBorderPane.setTop(imgLabel);
        imgBorderPane.setCenter(wrapBorderPane);
        imgBorderPane.setBottom(deleteCellPane);

        infoBorderPane.setTop(infoLabel);

        btnPane.setCenter(buttonPane);

        typeBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        nameBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        imgBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        wrapBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        btnPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        HBox cellsHBox = new HBox();
        cellsHBox.getChildren().addAll(typeBorderPane, nameBorderPane, imgBorderPane,btnPane);
        return cellsHBox;
    }

    private void addCellAction() {
        new AddCellFrame().show();
    }

    private void deleteCellAction(String cellName,ListView<String> typeListView) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("提示");
        alert.setHeaderText("确定要删除 "+cellName+" 图元吗？");
        alert.setContentText("");

        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            //根据cellName完成删除操作
           System.out.println("cellName: "+cellName);
           XmlReader.init(cellName);
           XmlReader.deleteCellImg();
           XmlReader.deleteCellXml();
            //刷新界面
          /* typeData = getCellsByType(type);
           typeListView.setItems(nameData);
           typeListView.refresh();*/

        } else {
           //取消，退出
            return;
        }
    }

    private void makeCellAction() {
        CellEditorFrame.CustomGraphComponent c = new CellEditorFrame.CustomGraphComponent(new CellEditorFrame.CustomGraph());
        CellEditorFrame editor = new CellEditorFrame("图元编辑器", c);
        JFrame cellMakeframe = editor.createCellFrame();
        c.setFrame(cellMakeframe);
        cellMakeframe.setVisible(true);
    }
}
