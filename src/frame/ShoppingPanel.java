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

public class ShoppingPanel extends JPanel {
    private static ShoppingPanel shoppingPanel;
    private static final String BRAND = "Brand";
    private static final String COLOUR = "Colour";
    private static final String SIZE = "Size";
    private static final String CATEGORY = "Category";
    private static final int NULL_ORDER = -1;
    private final ArrayList<Shoe> shoesInCart = new ArrayList<>();
    private final ArrayList<JLabel> amountLabels = new ArrayList<>();
    private final JPanel boxPanel = new JPanel();
    private final JPanel shoesPanel = new JPanel();
    private final JPanel buttonsPanel = new JPanel();
    private final JButton placeOrderButton = new JButton("Place order");
    private final JButton emptyCartButton = new JButton("Empty cart");
    private final JComboBox<String> brandBox = createCategoryComboBox(BRAND,
            Data.getData().getShoes().stream().map(Shoe::getBrand).distinct().toList());
    private final JComboBox<String> colourBox = createCategoryComboBox(COLOUR,
            Data.getData().getColours().stream().map(Colour::name).distinct().toList());
    private final JComboBox<String> sizeBox = createCategoryComboBox(SIZE,
            Data.getData().getShoes().stream().map(Shoe::getSize).distinct().toList());
    private final JComboBox<String> categoryBox = createCategoryComboBox(CATEGORY,
            Data.getData().getCategories().stream().map(Category::name).toList());
    private int orderNumber = NULL_ORDER;

    private ShoppingPanel() {
        setLayout(new BorderLayout());
    }

    public static ShoppingPanel getShoppingPanel()  {
        if(shoppingPanel == null){
            shoppingPanel = new ShoppingPanel();
        }
        return shoppingPanel;
    }

    public void initializeShoppingPanel() throws IOException {
        add(boxPanel, BorderLayout.NORTH);
        boxPanel.add(brandBox);
        boxPanel.add(colourBox);
        boxPanel.add(sizeBox);
        boxPanel.add(categoryBox);

        shoesPanel.setLayout(new GridLayout(0, 6));
        add(shoesPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        emptyCartButton.addActionListener(e -> clearShoesFromList());
        buttonsPanel.add(emptyCartButton);

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
        amountLabels.clear();
        List<Shoe> shoesToDisplay = Data.getData().getShoes();

        shoesToDisplay = filterShoesDisplay(shoesToDisplay);

        shoesToDisplay.forEach(s -> {
            if(s.getStock() != 0) {
                shoesPanel.add(createCenteredTextLabel(s.getBrand()));
                shoesPanel.add(createCenteredTextLabel(s.getColour().name()));
                shoesPanel.add(createCenteredTextLabel(String.valueOf(s.getSize())));

                JLabel amountLabel = createCenteredTextLabel("0");
                amountLabels.add(amountLabel);

                JButton minusButton = new JButton("-");
                minusButton.addActionListener(e -> modifyShoeAmount(s, amountLabel, false));

                JButton plusButton = new JButton("+");
                plusButton.addActionListener(e -> modifyShoeAmount(s, amountLabel, true));

                shoesPanel.add(amountLabel);
                shoesPanel.add(minusButton);
                shoesPanel.add(plusButton);
            }
        });
        clearShoesFromList();
        ShoppingFrame.getShoppingFrame().refreshFrame();
    }

    private List<Shoe> filterShoesDisplay(List<Shoe> shoesToDisplay) {
        String brand = brandBox.getSelectedItem().toString();
        String colour = colourBox.getSelectedItem().toString();
        String size = sizeBox.getSelectedItem().toString();
        String category = categoryBox.getSelectedItem().toString();

        if(!brand.equals(BRAND)) {
            shoesToDisplay = shoesToDisplay.stream().filter(s -> s.getBrand().equals(brand)).toList();
        }
        if(!colour.equals(COLOUR)) {
            shoesToDisplay = shoesToDisplay.stream().filter(s -> s.getColour().name().equals(colour)).toList();
        }
        if(!size.equals(SIZE)) {
            shoesToDisplay = shoesToDisplay.stream().filter(s -> String.valueOf(s.getSize()).equals(size)).toList();
        }
        if(!category.equals(CATEGORY)){
            shoesToDisplay = shoesToDisplay.stream().filter(s -> s.getCategories().stream()
                    .anyMatch(c -> c.name().equals(category))).toList();
        }
        return shoesToDisplay;
    }

    private JComboBox<String> createCategoryComboBox(String categoryName, List<?> categories) {
        JComboBox<String> tempBox = new JComboBox<>();
        tempBox.addItem(categoryName);

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

    private void modifyShoeAmount(Shoe s, JLabel amountTextField, boolean additive) {
        int currentAmount = Integer.parseInt(amountTextField.getText());
            if(additive) {
                if(currentAmount < s.getStock()) {
                    shoesInCart.add(s);
                    currentAmount++;
                }
            }else{
                if(currentAmount != 0) {
                    shoesInCart.remove(s);
                    currentAmount--;
                }
            }
            amountTextField.setText(String.valueOf(currentAmount));
    }

    private void clearShoesFromList() {
        amountLabels.forEach(l -> l.setText("0"));
        shoesInCart.clear();
    }

    private void orderShoes() throws IOException {
        if(!shoesInCart.isEmpty()) {
            shoesInCart.forEach(shoe -> orderNumber = Repository.getRepository().placeOrder(Data.getData().getActiveCustomer(),
                    orderNumber, shoe));
            displayPurchase();
        }else{
            JOptionPane.showMessageDialog(null, "Your cart is empty!");
        }
    }

    private void displayPurchase() throws IOException {
        StringBuilder order = new StringBuilder("Order placed!\n\nItems in order:\n");
        int totalPrice = 0;
        for (Shoe s : shoesInCart) {
            order.append(s.getBrand()).append(" |Size: ").append(s.getSize()).append
                    (" |Colour: ").append(s.getColour().name()).append(" |Price: ").append(s.getPrice()).append(":-\n");
            totalPrice += s.getPrice();
        }
        order.append("Total price: ").append(totalPrice).append(":-");
        JOptionPane.showMessageDialog(null, order);

        Data.getData().reloadShoes();
        fillShoesPanel();
        orderNumber = NULL_ORDER;
    }

}