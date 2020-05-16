package com.mxgraph.examples.swing.select;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 14:50 2020/5/15
 * @Modify By:
 */
public class DraggableDemo extends Application {
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
    }

    private Pane generateCircleNode(String data) {
        Pane node = new StackPane();

        Circle circle = new Circle(20);
        circle.setStyle("-fx-fill: rgb(51,184,223)");

        Text text = new Text(data);
        text.setStyle("-fx-fill: rgb(93,93,93);-fx-font-weight: bold;");

        node.getChildren().addAll(circle, text);

        return node;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        Pane node1 = generateCircleNode("1");
        Pane node2 = generateCircleNode("2");

        // 将结点重定位到任意位置
        node1.relocate(50, 50);
        node2.relocate(300, 150);

        // 使结点可拖拽
        draggable(node1);
        draggable(node2);

        // 创建直线
        Line line = new Line();

        // 将直线的起点坐标与 node1 的中心坐标进行绑定
        line.startXProperty().bind(node1.layoutXProperty().add(node1.widthProperty().divide(2)));
        line.startYProperty().bind(node1.layoutYProperty().add(node1.heightProperty().divide(2)));

        // 将直线的终点坐标与 node2 的中心坐标进行绑定
        line.endXProperty().bind(node2.layoutXProperty().add(node2.widthProperty().divide(2)));
        line.endYProperty().bind(node2.layoutYProperty().add(node2.heightProperty().divide(2)));

        root.getChildren().addAll(line, node1, node2);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
