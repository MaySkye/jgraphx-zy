package com.mxgraph.examples.swing.select;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Iterator;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:25 2020/5/15
 * @Modify By:
 */
public class ComboButtonSample extends Application {
    @Override
    public void start(Stage stage) {
        final ObservableList<CheckItem> items = fetchItems();
        ComboBox<CheckItem> combo = createComboBox(items);
        combo.setPromptText("enter searchstring here");
        combo.setEditable(true);


        // order the components vertically
        VBox vBox = new VBox();
        vBox.getChildren().add(combo);

        // Button to write out the text and the items of the combobox
        Button btn = new Button();
        btn.setText("combo text to console");
        btn.setOnAction((event) -> {
            System.out.println("Text is: "+combo.getEditor().getText());
            System.out.println("Content is: ");
            for (Iterator<CheckItem> iterator = combo.getItems().iterator(); iterator.hasNext();) {
                CheckItem ci = (CheckItem) iterator.next();
                System.out.println(String.format("[%s] %s -> %s", ci.selected ? "X" : " ",ci.getDisplayName(), ci.getInternalName()));

            }
        });

        vBox.getChildren().add(btn);

        // show you do not need any code to change the selection of the box.
        CheckBox checkBox = new CheckBox();
        checkBox.setText("test box");
        vBox.getChildren().add(checkBox);

        stage.setScene(new Scene(vBox));
        stage.show();
    }

    private ComboBox<CheckItem> createComboBox(ObservableList<CheckItem> data) {
        ComboBox<CheckItem> combo = new ComboBox<>();
        combo.getItems().addAll(data);
        combo.setCellFactory(listView -> new CheckItemListCell());
        return combo;
    }

    class CheckItemListCell extends ListCell<CheckItem> {
        private final CheckBox btn;

        CheckItemListCell() {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            btn = new CheckBox();
        }

        @Override
        protected void updateItem(CheckItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
            } else {
                btn.setText(item.getDisplayName());
                btn.selectedProperty().setValue(item.selected);
                setGraphic(btn);
            }
        }
    }

    private ObservableList<CheckItem> fetchItems() {
        final ObservableList<CheckItem> data = FXCollections
                .observableArrayList();
        for (int i = 1; i < 15; i++) {
            CheckItem chkItem = new CheckItem();
            chkItem.selected = i%3==0;
            chkItem.setDisplayName("DisplayName" + i);
            chkItem.setInternalName("InternalName" + i);
            data.add(chkItem);
        }
        return data;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class CheckItem {
        boolean selected;
        String displayName;
        String internalName;

        public boolean isChecked() {
            return selected;
        }

        public void setChecked(boolean checked) {
            this.selected = checked;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getInternalName() {
            return internalName;
        }

        public void setInternalName(String internalName) {
            this.internalName = internalName;
        }
    }
}
