package edu.sdccd.cisc191.template;


/**
 * Author Nicholas Hilaire
 *
 *
 * References: "How to write a unit Test" https://stackoverflow.com/questions/8751553/how-to-write-a-unit-test
 * "Writing Tests with JUNIT 5 "https://blog.jetbrains.com/idea/2020/09/writing-tests-with-junit-5/"
 */

import org.junit.jupiter.api.Test;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.*;

import static java.util.Locale.filter;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;

public class ServerTest
{

    // Test if specific unit type is inherenting from the Unit superClass
    @Test
    public void testInheritance()
    {
        Unit tank = new Tank("Test Tank", "Test Tank", "Tank", 100, 100, 50, 200, 300, 18, 20, "TestAbilities",  80);
        Unit fighter = new Fighter("Test Fighter", "Fighter", "Air", 200, 30, 150, 400, 1.75, 300, 4000, "Test Abilities", 120);
        Unit infantry = new InfantryUnit("Test Infantry", "Infantry", "Ground", 50, 10, 100, 500, 1.0, 15, 1000, "Test Abilities", 30);

        assertEquals(true, tank instanceof Unit, "Tanks inherents from Unit Class");
        assertEquals(true, fighter instanceof Unit, "Fighter inherents from Unit Class");
        assertEquals(true, infantry instanceof Unit, "Infantry inherents from Unit Class");

    }

    // Test if the file is being read from the path and the contents of the file have data.
    @Test
    public void TestIOStream()
    {
        List<Unit> units = UnitStatsLoader.loadUnits("C:\\Users\\Nicko\\IdeaProjects\\CISC191-FinalProjectTemplate\\Server\\src\\main\\resources\\Broken Arrow Unit Stats.csv");
        assertFalse(units.isEmpty());
    }

    // Test if units are being created in the UnitGenerator class
    @Test
    public void TestUnitList()
        {
            List<Unit> units = new ArrayList<>();
            assertNotNull(units, "The units list should be created and not null");
            assertTrue(units.isEmpty(), "The units list should be empty upon initialization");
            Unit testUnit = new Unit("Test Unit", "Infantry", "Test Specialization", 100, 10, 100, 500, 1.5, 20, 2000, "Test Abilities");

        }

    // Test Hashmap to ensure the units correspond with the correct images.
    @Test
    void testUnitHashMap()
    {
        List<Unit> units = UnitStatsLoader.loadUnits("C:\\Users\\Nicko\\IdeaProjects\\CISC191-FinalProjectTemplate\\Server\\src\\main\\resources\\Broken Arrow Unit Stats.csv");
       Map<String, Unit> map = new HashMap<>();
       for (Unit unit : units)
       {
           String name = unit.getUnitName();
           if (name != null && !name.isBlank())
           {
               map.put(name, unit);
           }
       }

        assertFalse(map.isEmpty(), "The map should not be empty");
    }
    // Test search and sorting to ensure unit appears in the correct group type
    @Test
    void testSearchingbyType()
    {
        List<Unit> units = UnitStatsLoader.loadUnits("C:\\Users\\Nicko\\IdeaProjects\\CISC191-FinalProjectTemplate\\Server\\src\\main\\resources\\Broken Arrow Unit Stats.csv");
        Map<String, List<Unit>> byType = units.stream().collect(Collectors.groupingBy(Unit::getUnitType));

        for (Unit unit : units)
        {
            String type = unit.getUnitType();
            assertTrue(byType.containsKey(type), "The map should contain the type " + type);
        }
    }

    //Ensure Stream APL will filter by the unit's type
    @Test
    public void testFilterSorting() {
        List<Unit> units = UnitStatsLoader.loadUnits("C:\\Users\\Nicko\\IdeaProjects\\CISC191-FinalProjectTemplate\\Server\\src\\main\\resources\\Broken Arrow Unit Stats.csv");
        String type = units.get(0).getUnitType();
        List<Unit> result = units.stream()
                .filter(u -> type.equals(u.getUnitType()))
                .sorted(Comparator.comparing(Unit::getUnitName))
                .collect(Collectors.toList());
        assertFalse(result.isEmpty(), "Filtered & sorted list should not be empty");
    }

}
