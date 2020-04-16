package com.mxgraph.examples.swing.resource_manage;

import com.mxgraph.examples.swing.util.AliasName;
import com.mxgraph.examples.swing.util.FileUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:26 2020/3/26
 * @Modify By:
 */
public class AddCellFrame extends Application {
    private static String appPath = FileUtil.getRootPath().substring(1);
    private static String noImagePic="file:"+appPath+"/examples/com/mxgraph/examples/swing/images/others/backpic.png";
    private static String testImagePic="file:"+appPath+"/examples/com/mxgraph/examples/swing/images/device/new.png";
    private static Image noImage=new Image(noImagePic);
    private static Image testImage=new Image(testImagePic);
    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println(noImagePic);
        VBox vBox = new VBox(15);
        vBox.setPadding(new Insets(15, 5, 5, 5));
        creatTabContent(vBox);

        Scene scene = new Scene(vBox, 500, 400);

        primaryStage.setTitle(AliasName.getAlias("图元引入"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void creatTabContent(VBox vBox){
        //图元路径
        Label lbPic = new Label("图元样式:");
        Label lbDir = new Label("/examples/com/mxgraph/examples/swing/images...");
        HBox hPic = new HBox(15);
        hPic.setPadding(new Insets(5, 15, 5, 15));
        hPic.getChildren().add(lbPic);
        hPic.getChildren().add(lbDir);

        //图片
        ImageView imageView = new ImageView();
        imageView.setImage(testImage);
        BorderPane picPane = new BorderPane();
        picPane.setCenter(imageView);
        //上传按钮
        Button btnChoice=new Button("选择");
        Button btnLoad=new Button("上传");
        GridPane btnPicPane = new GridPane();
        btnPicPane.setAlignment(Pos.CENTER);
        btnPicPane.setPadding(new Insets(5, 12.5, 5, 14.5));
        btnPicPane.setHgap(20);
        btnPicPane.setVgap(5.5);
        btnPicPane.add(btnChoice, 0, 0);
        btnPicPane.add(btnLoad, 1, 0);

        //类型
        Label lbType = new Label("设备类型:");
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "光纤光频传递分系统设备",
                        "光纤时间同步分系统设备",
                        "微波频率传递分系统设备","路由器设备","客户端设备"
                );
        ComboBox cbType = new ComboBox(options);
        cbType.getSelectionModel().select(0);
        Button btnType=new Button("+");
        HBox hType = new HBox(15);
        hType.setPadding(new Insets(5, 15, 5, 15));
        hType.getChildren().add(lbType);
        hType.getChildren().add(cbType);
        hType.getChildren().add(btnType);
        //名称
        Label lbName = new Label("设备名称:");
        TextField tfName = new TextField();
        HBox hName = new HBox(15);
        hName.setPadding(new Insets(5, 15, 5, 15));
        hName.getChildren().add(lbName);
        hName.getChildren().add(tfName);

        //高度、宽度
        Label lbWidth = new Label("宽度:");
        TextField tfWidth = new TextField();
        tfWidth.setPrefWidth(100);
        Label lbHeight = new Label("高度:");
        TextField tfHeight = new TextField();
        tfHeight.setPrefWidth(100);
        HBox hSize = new HBox(15);
        hSize.setPadding(new Insets(5, 15, 5, 15));
        hSize.getChildren().add(lbWidth);
        hSize.getChildren().add(tfWidth);
        hSize.getChildren().add(lbHeight);
        hSize.getChildren().add(tfHeight);
        //保存、取消按钮
        Button btnSave=new Button("保存");
        Button btnCancel=new Button("取消");
        btnSave.setOnMouseClicked(event -> saveAction());
        btnCancel.setOnMouseClicked(event -> cancelAction());
        GridPane btnPane = new GridPane();
        btnPane.setAlignment(Pos.CENTER);
        btnPane.setPadding(new Insets(5, 12.5, 5, 14.5));
        btnPane.setHgap(35);
        btnPane.setVgap(5.5);
        btnPane.add(btnSave, 0, 0);
        btnPane.add(btnCancel, 1, 0);

        vBox.getChildren().add(hPic);
        vBox.getChildren().add(picPane);
        vBox.getChildren().add(btnPicPane);
        vBox.getChildren().add(hType);
        vBox.getChildren().add(hName);
        vBox.getChildren().add(hSize);
        vBox.getChildren().add(btnPane);
    }

    private void saveAction() {
        new AddCellFrame().show();
    }
    private void cancelAction() {
        new AddCellFrame().show();
    }

    public void show() {
        AddCellFrame.launch();
    }

    public static void main(String[] args) {
        new AddCellFrame().show();
    }
}
