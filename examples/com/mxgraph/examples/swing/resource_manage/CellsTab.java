package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.match.CellTypeUtil;
import com.mxgraph.examples.swing.util.AliasName;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;


public class CellsTab extends Tab {

    private static final String ALL_TYPE = "所有类型";

    private ObservableList<String> typeData; // typeListView
    private ObservableList<String> infoData; // infoListView
    private String oldType = null;
    private String oldCell = null;

    public CellsTab(String title) {
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
        ListView<String> infoListView = new ListView<>(infoData);
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
            System.out.println("type:" + type);
            if (type == null || type.equals(oldType)) {
                return;
            }
            oldType = type;
            oldCell = null;
            type = AliasName.getReverse(type);
            infoData = getCellsByType(type);
            infoListView.setItems(infoData);
            infoListView.refresh();
            cllImageView.setImage(null);
        });
        infoListView.setOnMouseClicked(event -> {
            String cell = infoListView.getSelectionModel().getSelectedItem();
            System.out.println("cell:" + cell);
            if (cell == null || cell.equals(oldCell)) {
                return;
            }
            oldCell = cell;
            String imgPath = CellInfoUtil.getIcon(cell);
            cllImageView.setImage(new Image(imgPath, 200, 200, true, true));
        });

        Label typeLabel = new Label("图元类型");
        Label nameLabel = new Label("图元名称");
        Label imgLabel = new Label("图元展示");

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

        BorderPane typeBorderPane = new BorderPane();
        BorderPane nameBorderPane = new BorderPane();
        BorderPane imgBorderPane = new BorderPane();

        typeBorderPane.setPrefHeight(400);
        nameBorderPane.setPrefHeight(400);
        imgBorderPane.setPrefHeight(400);

        typeBorderPane.setTop(typeLabel);
        typeBorderPane.setCenter(typeListView);

        nameBorderPane.setTop(nameLabel);
        nameBorderPane.setCenter(infoListView);

        imgBorderPane.setTop(imgLabel);
        imgBorderPane.setCenter(wrapBorderPane);

        typeBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        nameBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        imgBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        wrapBorderPane.setBorder(new Border(new BorderStroke(Color.GREY, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        HBox cellsHBox = new HBox();
        cellsHBox.getChildren().addAll(typeBorderPane, nameBorderPane, imgBorderPane);

        return cellsHBox;
    }
}
