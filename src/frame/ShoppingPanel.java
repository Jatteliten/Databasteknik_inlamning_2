package frame;

import database.Category;
import database.Colour;
import database.Data;
import database.Repository;
import database.Shoe;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShoppingPanel extends JPanel {
    private static final String ALL = "All";
    private static final int NULL_ORDER = -1;
    private int orderNumber = NULL_ORDER;
    private final ArrayList<Shoe> shoesInCart = new ArrayList<>();
    private final ArrayList<JTextField> amountFields = new ArrayList<>();
    private final JPanel boxPanel = new JPanel();
    private final JPanel shoesPanel = new JPanel();
    private final JPanel incrementPanel = new JPanel();
    private final JPanel buttonsPanel = new JPanel();
    private final Dimension addToCartButtonsDimension = new Dimension(40, 25);
    private final JComboBox<String> brandBox = createFilterComboBox(
            Data.getData().getShoes().stream().map(Shoe::getBrand).distinct().toList());
    private final JComboBox<String> colourBox = createFilterComboBox(
            Data.getData().getColours().stream().map(Colour::name).distinct().toList());
    private final JComboBox<String> sizeBox = createFilterComboBox(
            Data.getData().getShoes().stream().map(Shoe::getSize).distinct().toList());
    private final JComboBox<String> categoryBox = createFilterComboBox(
            Data.getData().getCategories().stream().map(Category::name).toList());

    public ShoppingPanel() {
        setLayout(new BorderLayout());
    }

    public void initializeShoppingPanel() throws IOException {
        add(boxPanel, BorderLayout.NORTH);
        add(shoesPanel, BorderLayout.CENTER);
        add(incrementPanel, BorderLayout.EAST);
        add(buttonsPanel, BorderLayout.SOUTH);

        boxPanel.setLayout(new GridLayout(2, 4));
        shoesPanel.setLayout(new GridLayout(0, 4));
        incrementPanel.setLayout(new GridLayout(0, 3));

        boxPanel.add(createCenteredTextLabel("Brand:"));
        boxPanel.add(createCenteredTextLabel("Colour:"));
        boxPanel.add(createCenteredTextLabel("Size:"));
        boxPanel.add(createCenteredTextLabel("Category:"));
        boxPanel.add(brandBox);
        boxPanel.add(colourBox);
        boxPanel.add(sizeBox);
        boxPanel.add(categoryBox);

        JButton emptyCartButton = new JButton("Empty cart");
        emptyCartButton.addActionListener(e -> clearShoesFromList());
        buttonsPanel.add(emptyCartButton);

        JButton placeOrderButton = new JButton("Place order");
        placeOrderButton.addActionListener(e -> {
            try {
                orderShoes();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonsPanel.add(placeOrderButton);

        fillShoesPanel();
    }

    private void fillShoesPanel() throws IOException {
        shoesPanel.removeAll();
        incrementPanel.removeAll();
        amountFields.clear();
        List<Shoe> shoesToDisplay = filterShoesList(Data.getData().getShoes());

        shoesToDisplay.forEach(s -> {
            if(s.getStock() != 0) {
                shoesPanel.add(createCenteredTextLabel(s.getBrand()));
                shoesPanel.add(createCenteredTextLabel(s.getColour().name()));
                shoesPanel.add(createCenteredTextLabel(String.valueOf(s.getSize())));
                shoesPanel.add(createCenteredTextLabel(s.getPrice() + ":-"));

                JTextField amountField = createItemInCartCounterTextField();
                amountFields.add(amountField);

                JButton minusButton = new JButton("-");
                minusButton.setPreferredSize(addToCartButtonsDimension);
                minusButton.addActionListener(e -> subtractShoe(s, amountField));

                JButton plusButton = new JButton("+");
                plusButton.setPreferredSize(addToCartButtonsDimension);
                plusButton.addActionListener(e -> addShoe(s, amountField));

                incrementPanel.add(amountField);
                incrementPanel.add(minusButton);
                incrementPanel.add(plusButton);
            }
        });
        clearShoesFromList();
        ShoppingFrame.getShoppingFrame().refreshFrame();
    }

    private List<Shoe> filterShoesList(List<Shoe> shoesToDisplay) {
        String brand = Objects.requireNonNull(brandBox.getSelectedItem()).toString();
        String colour = Objects.requireNonNull(colourBox.getSelectedItem()).toString();
        String size = Objects.requireNonNull(sizeBox.getSelectedItem()).toString();
        String category = Objects.requireNonNull(categoryBox.getSelectedItem()).toString();

        return shoesToDisplay.stream().filter(s ->
              (brand.equals(ALL) || s.getBrand().equals(brand)) &&
              (colour.equals(ALL) || s.getColour().name().equals(colour)) &&
              (size.equals(ALL) || String.valueOf(s.getSize()).equals(size)) &&
              (category.equals(ALL) || s.getCategories().stream().anyMatch(c -> c.name().equals(category))))
                .toList();
    }

    private JComboBox<String> createFilterComboBox(List<?> categories) {
        JComboBox<String> tempBox = new JComboBox<>();
        tempBox.addItem(ShoppingPanel.ALL);

        if (checkIfItemIsNumber(categories.get(0).toString())) {
            List<Integer> integerList = new ArrayList<>();
            categories.forEach(size -> integerList.add((Integer) size));
            Collections.sort(integerList);
            integerList.forEach(size -> tempBox.addItem(size.toString()));
        } else {
            categories.forEach(category -> tempBox.addItem(category.toString()));
        }

        tempBox.addActionListener(e -> {
            try {
                fillShoesPanel();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return tempBox;
    }

    private boolean checkIfItemIsNumber(String check){
        try{
            Integer.parseInt(check);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    private JLabel createCenteredTextLabel(String text){
        JLabel tempLabel = new JLabel(text);
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return tempLabel;
    }

    private JTextField createItemInCartCounterTextField(){
        JTextField tempField = new JTextField("0");
        tempField.setPreferredSize(addToCartButtonsDimension);
        tempField.setEditable(false);
        tempField.setHorizontalAlignment(SwingConstants.CENTER);
        return tempField;
    }

    private void addShoe(Shoe s, JTextField amountTextField) {
        int currentAmount = Integer.parseInt(amountTextField.getText());
        if(currentAmount < s.getStock()) {
            shoesInCart.add(s);
            currentAmount++;
            amountTextField.setText(String.valueOf(currentAmount));
        }
    }

    private void subtractShoe(Shoe s, JTextField amountTextField){
        int currentAmount = Integer.parseInt(amountTextField.getText());
        if(currentAmount != 0) {
            shoesInCart.remove(s);
            currentAmount--;
            amountTextField.setText(String.valueOf(currentAmount));
        }
    }

    private void clearShoesFromList() {
        amountFields.forEach(l -> l.setText("0"));
        shoesInCart.clear();
    }

    private void orderShoes() throws IOException {
        if(!shoesInCart.isEmpty()) {
            shoesInCart.forEach(shoe -> orderNumber = Repository.getRepository()
                    .placeOrder(Data.getData().getActiveCustomer(), orderNumber, shoe));
            displayPurchase();
        }else{
            JOptionPane.showMessageDialog(null, "Your cart is empty!");
        }
    }

    private void displayPurchase() throws IOException {
        JOptionPane.showMessageDialog(null, createDisplayedMessage());

        Data.getData().reloadShoes();
        fillShoesPanel();
        orderNumber = NULL_ORDER;
    }

    private StringBuilder createDisplayedMessage() {
        StringBuilder text = new StringBuilder("Order placed!\n\nItems in order:\n");

        shoesInCart.forEach(s -> text.append(s.getBrand())
                .append(" |Size: ").append(s.getSize())
                .append(" |Colour: ").append(s.getColour().name())
                .append(" |Price: ").append(s.getPrice()).append(":-\n"));

        text.append("\nTotal price: ")
                .append(shoesInCart.stream().map(Shoe::getPrice).reduce(0, Integer::sum))
                .append(":-");
        return text;
    }

}