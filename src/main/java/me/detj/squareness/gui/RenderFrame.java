package me.detj.squareness.gui;

import me.detj.squareness.algorithm.Algorithm;
import me.detj.squareness.algorithm.Country;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.util.AffineTransformation;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RenderFrame extends JFrame {

    private final Map<String, Country> countries;
    private final String[] changeCountryChoices;
    private final RenderPanel panel;

    private String currentCountry;

    public RenderFrame(List<Country> cs) {
        countries = cs.stream()
                .collect(Collectors.toUnmodifiableMap(
                        country -> country.getNameWithCode(),
                        country -> country));
        changeCountryChoices = countries.values()
                .stream()
                .map(country -> country.getNameWithCode())
                .sorted()
                .collect(Collectors.toList())
                .toArray(new String[countries.values().size()]);

        panel = new RenderPanel();
        getContentPane().add(panel);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        JMenuItem changeCountryMenuItem = new JMenuItem("Change Country");
        changeCountryMenuItem.addActionListener(event -> showCountrySelectMenu());
        optionsMenu.add(changeCountryMenuItem);

        JMenu actionsMenu = new JMenu("Actions");
        menuBar.add(actionsMenu);

        JMenuItem rotateMenuItem = new JMenuItem("Rotate to most squareness");
        rotateMenuItem.addActionListener(event -> {
            if (currentCountry == null) {
                return;
            }
            Country country = countries.get(currentCountry);
            Geometry copy = country.getPolygon().copy();
            double angle = Algorithm.calculateAngleOfMostSquareness(country.getPolygon());
            copy.apply(AffineTransformation.rotationInstance(angle));
            panel.setGeometry(copy);
            panel.repaint();
        });
        actionsMenu.add(rotateMenuItem);


        setSize(700, 700);
        setVisible(true);
        panel.repaint();

    }

    private void showCountrySelectMenu() {
        String choice = (String) JOptionPane.showInputDialog(null, "Choose the Country",
                "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null, // Use
                // default
                // icon
                changeCountryChoices, // Array of choices
                currentCountry); // Initial choice

        if (choice != null) {
            currentCountry = choice;
            changeCountry(countries.get(choice));
        }
    }

    private void changeCountry(Country country) {
        setTitle(country.getNameWithCode());
        panel.setGeometry(country.getPolygon());
        panel.repaint();
    }

}
