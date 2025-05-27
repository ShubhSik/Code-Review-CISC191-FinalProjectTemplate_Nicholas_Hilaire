
package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Author Nicholas Hilaire
 *
 * Java GUI Tutorial - Make a GUI in 13 Minutes #99 "https://www.youtube.com/watch?v=5o3fMLPY7qY
 * "How to Use BoxLayout"  https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
 */

public class BrokenArrowUnitComparisonTool extends Application
{

    // ComboBoxes for selecting units on the left and right panels
    private ComboBox<Unit> leftComboBox;
    private ComboBox<Unit> rightComboBox;

    // Panels that display unit details
    private VBox leftPanel;
    private VBox rightPanel;

    // List of units loaded from CSV
    private List<Unit> unitList;
    private Map<String, Image> unitImageMap;

    @Override
    public void start(Stage primaryStage)
    {
        //Loads units from CSV Change the unit arrow to List<Unit>
        List<Unit> unitList = UnitStatsLoader.loadUnits("C:\\Users\\Nicko\\IdeaProjects\\CISC191-FinalProjectTemplate\\Server\\src\\main\\resources\\Broken Arrow Unit Stats.csv");
        System.out.println("Units loaded: " + unitList.size());

        Map<String, List<Unit>> unitsByType = unitList.stream()
                .collect(Collectors.groupingBy(Unit::getUnitType));

        //Crates a UI to allow search by Unit type
        ChoiceBox<String> typeSearch = new ChoiceBox<>(
                FXCollections.observableArrayList(unitsByType.keySet()));

        typeSearch.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) ->
        {
            List<Unit> filtered = unitsByType.getOrDefault(newValue, unitList);
            leftComboBox.getItems().setAll(filtered);
            rightComboBox.getItems().setAll(filtered);
        });



        //Module 7 Used Hashmap for quick lookups between my Units and the images they are associated too.
        Map<String, String> imageFileName = Map.of(
                "Marine Raiders CQC",    "marine_raiders_cqc.png",
                "Chernye Berety",        "chernye_berety.png",
                "M1A2 SEP v2 Abrams",    "m1a2_sep_v2_abrams.png",
                "T-14 Armata",           "t14_armata.png",
                "F-35B",                 "f35b.png",
                "Su-57",                 "su57.png"
        );

        //Loads images into javaFX memory
        unitImageMap = new HashMap<>();
        for (Map.Entry<String, String> entry : imageFileName.entrySet()) {
            String displayName = entry.getKey();
            String fileName = entry.getValue();
            InputStream is = getClass().getResourceAsStream("/images/" + fileName);
            if (is == null) {
                System.err.println("Missing image resource: " + fileName);
                continue;
            }
            Image img = new Image(is);
            unitImageMap.put(displayName, img);
        }


        // Create ComboBoxes and populate them with units by Switching to List<Unit> now it can feed easier into the ComboBoxes
        leftComboBox = new ComboBox<>();
        rightComboBox = new ComboBox<>();
        leftComboBox.getItems().addAll(unitList);
        rightComboBox.getItems().addAll(unitList);


        // Use a StringConverter so that only the unit's name appears in the drop-down
        StringConverter<Unit> converter = new StringConverter<>() {
            @Override
            public String toString(Unit unit)
            {
                return unit == null ? "" : unit.getUnitName();
            }
            @Override
            public Unit fromString(String string)
            {
                return null;
            }
        };

        leftComboBox.setConverter(converter);
        rightComboBox.setConverter(converter);

        // Create detail panels for each side with images and labels
        leftPanel = createUnitDetailPanel();
        rightPanel = createUnitDetailPanel();

        // When a selection changes, update the corresponding panel
        leftComboBox.setOnAction(e -> showUnit(leftComboBox.getValue(), leftPanel));
        rightComboBox.setOnAction(e -> showUnit(rightComboBox.getValue(), rightPanel));


        // Top controls: display the ComboBoxes in an HBox added Type UI feature to use search tool in Module 9
        HBox topControls = new HBox(10,
                new Label("Type:"), typeSearch,
                new Label("Left Unit:"), leftComboBox,
                new Label("Right Unit:"), rightComboBox);
        topControls.setPadding(new Insets(10));

        // Container for the two detail panels side by side
        HBox panelsContainer = new HBox(20, leftPanel, rightPanel);
        panelsContainer.setPadding(new Insets(10));

        // Main UI using a BorderPane
        BorderPane root = new BorderPane();
        root.setTop(topControls);
        root.setCenter(panelsContainer);



        // Sets the UI to display unit as cards shapes in side by side panel.
        primaryStage.setTitle("Unit Comparison");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void showUnit(Unit unit, VBox panel)
    {
        if (unit == null) return;

        // 1) Update the images
        ImageView iv = (ImageView) panel.lookup("#unitImageView");
        iv.setImage(unitImageMap.get(unit.getUnitName()));

        // 2) Update borderpane labels
        updatePanel(panel, unit);
    }

    // Creates a panel (VBox) containing labels for unit details
    private VBox createUnitDetailPanel()
    {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-background-color: #f9f9f9;");

        ImageView iv = new ImageView();
        iv.setId("unitImageView");
        iv.setFitWidth(300);
        iv.setFitHeight(260);
        iv.setPreserveRatio(true);

        
        // Create labels for each stat
        Label nameLabel = new Label("Name: ");
        Label typeLabel = new Label("Type: ");
        Label specializationLabel = new Label("Specialization: ");
        Label statsLabel = new Label("Stats: ");
        Label abilitiesLabel = new Label("Abilities: ");

        // Use a helper object to store these components for easy updating
        PanelComponents comps = new PanelComponents(nameLabel, typeLabel, specializationLabel, statsLabel, abilitiesLabel);
        panel.setUserData(comps);

        panel.getChildren().addAll(iv, nameLabel, typeLabel, specializationLabel, statsLabel, abilitiesLabel);
        return panel;
    }

    // Updates the provided panel with data from the selected unit
    private void updatePanel(VBox panel, Unit unit)
    {
        if (unit == null) return;
        PanelComponents comps = (PanelComponents) panel.getUserData();
        comps.nameLabel.setText("Name: " + unit.getUnitName());
        comps.typeLabel.setText("Type: " + unit.getUnitType());
        comps.specializationLabel.setText("Specialization: " + unit.getSpecialization());
        comps.statsLabel.setText("Price: " + unit.getPrice() +
                " | Armor: " + unit.getArmor() +
                " | Health: " + unit.getHealth());
        comps.abilitiesLabel.setText("Abilities: " + unit.getAbilities());
    }


    // Helper class to hold UI components in each panel
    private static class PanelComponents
    {
        Label nameLabel;
        Label typeLabel;
        Label specializationLabel;
        Label statsLabel;
        Label abilitiesLabel;

        PanelComponents(Label nameLabel, Label typeLabel, Label specializationLabel, Label statsLabel, Label abilitiesLabel)
        {
            this.nameLabel = nameLabel;
            this.typeLabel = typeLabel;
            this.specializationLabel = specializationLabel;
            this.statsLabel = statsLabel;
            this.abilitiesLabel = abilitiesLabel;
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}