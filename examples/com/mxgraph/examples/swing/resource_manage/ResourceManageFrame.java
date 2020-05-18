package com.mxgraph.examples.swing.resource_manage;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 16:51 2020/3/24
 * @Modify By:
 */
import com.mxgraph.examples.swing.util.AliasName;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class ResourceManageFrame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        TabPane mainPane = new TabPane();
        mainPane.getTabs().addAll(new CellManageTab("领域图元"),
                new GraphTempletTab("组态图模板"));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(mainPane);
       // borderPane.setBottom(new Label("status bar"));
        Scene scene = new Scene(borderPane, 1000, 500);
        primaryStage.setTitle(AliasName.getAlias("资源管理界面"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static class ListViewCell extends ListCell<String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            Label label = new Label();
            if (item != null) {
                label.setText(item);
            }
            label.setPrefHeight(25);
            setGraphic(label);
        }
    }



    public void show() {
        ResourceManageFrame.launch();
    }

    public static void main(String[] args) {
        new ResourceManageFrame().show();
    }

}
