package Frame;

import DataBase.Category;
import DataBase.Colour;
import DataBase.Data;
import DataBase.Repository;
import DataBase.Shoe;

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
            Data.getData().getCategories().stream().map(Category::getName).toList());
    private int orderNumber = NULL_ORDER;

    private ShoppingPanel() throws IOException {
        setLayout(new BorderLayout());
    }

    public static ShoppingPanel getAddToCart() throws IOException {
        if(shoppingPanel == null){
            shoppingPanel = new ShoppingPanel();
            shoppingPanel.initializePanel();
        }
        return shoppingPanel;
    }

    private void initializePanel() throws IOException {
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

        placeOrderButton.addActionListener(e -> orderShoes());
        buttonsPanel.add(placeOrderButton);

        fillShoesPanel();
    }

    private void fillShoesPanel() throws IOException {
        List<Shoe> shoesToDisplay = Data.getData().getShoes();

        shoesToDisplay = filterShoesDisplay(shoesToDisplay);

        shoesToDisplay.forEach(s -> {
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
        });
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
                    .anyMatch(cat -> cat.getName().equals(category))).toList();
        }
        return shoesToDisplay;
    }

    private JComboBox<String> createCategoryComboBox(String categoryName, List<?> categories) {
        JComboBox<String> tempBox = new JComboBox<>();
        tempBox.addItem(categoryName);
        if (checkIfItemIsNumber(categories.get(0).toString())) {
            List<Integer> integerList = new ArrayList<>();
            categories.forEach(e -> integerList.add((Integer) e));
            Collections.sort(integerList);
            integerList.forEach(size -> tempBox.addItem(size.toString()));
        } else {
            categories.forEach(category -> tempBox.addItem(category.toString()));
        }

        tempBox.addActionListener(e -> {
            try {
                shoesPanel.removeAll();
                fillShoesPanel();
                shoesInCart.clear();
                ShoppingFrame.getShoppingFrame().pack();
                shoesPanel.revalidate();
                shoesPanel.repaint();
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
        if(currentAmount <= s.getStock()) {
            if(additive) {
                shoesInCart.add(s);
                currentAmount++;
            }else{
                if(currentAmount != 0) {
                    shoesInCart.remove(s);
                    currentAmount--;
                }
            }
            amountTextField.setText(String.valueOf(currentAmount));
        }
    }

    private void clearShoesFromList() {
        amountLabels.forEach(l -> l.setText("0"));
        shoesInCart.clear();
    }

    private void orderShoes() {
        shoesInCart.forEach(shoe -> {
            try {
                orderNumber = Repository.getRepository().placeOrder(Data.getData().getActiveCustomer(),
                        orderNumber, shoe);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        displayPurchase();
        orderNumber = NULL_ORDER;
    }

    private void displayPurchase(){
        if(!shoesInCart.isEmpty()) {
            StringBuilder order = new StringBuilder("Order placed!\n\nItems in order:\n");
            int totalPrice = 0;
            for (Shoe s : shoesInCart) {
                order.append(s.getBrand()).append(" |Size: ").append(s.getSize()).append
                        (" |Colour: ").append(s.getColour().name()).append(" |Price: ").append(s.getPrice()).append(":-\n");
                totalPrice += s.getPrice();
            }
            order.append("Total price: ").append(totalPrice).append(":-");
            JOptionPane.showMessageDialog(null, order);
            clearShoesFromList();
        }else{
            JOptionPane.showMessageDialog(null, "Your cart is empty!");
        }
    }

}