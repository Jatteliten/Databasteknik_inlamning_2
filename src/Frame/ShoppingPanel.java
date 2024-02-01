package Frame;

import DataBase.Data;
import DataBase.Repository;
import DataBase.Shoe;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ShoppingPanel extends JPanel {
    private static ShoppingPanel shoppingPanel;
    private final int NULL_ORDER = -1;
    private final ArrayList<Shoe> SHOES_IN_CART = new ArrayList<>();
    private final ArrayList<JLabel> AMOUNT_LABELS = new ArrayList<>();
    int orderNumber = NULL_ORDER;

    private ShoppingPanel(){
        setLayout(new GridLayout(0,6));
    }

    public static ShoppingPanel getAddToCart() throws IOException {
        if(shoppingPanel == null){
            shoppingPanel = new ShoppingPanel();
            shoppingPanel.initializePanel();
        }
        return shoppingPanel;
    }

    private void initializePanel() throws IOException {
        add(createUnderScoredTextLabel("Brand:"));
        add(createUnderScoredTextLabel("Colour:"));
        add(createUnderScoredTextLabel("Size:"));
        add(createUnderScoredTextLabel("In cart:"));
        add(new JLabel());
        add(new JLabel());

        Data.getData().getSHOES().forEach(s -> {
            add(createCenteredTextLabel(s.getBRAND()));
            add(createCenteredTextLabel(s.getColour().getName()));
            add(createCenteredTextLabel(String.valueOf(s.getSIZE())));

            JLabel amountLabel = createCenteredTextLabel("0");
            AMOUNT_LABELS.add(amountLabel);

            JButton minusButton = new JButton("-");
            minusButton.addActionListener(e -> modifyShoeAmount(s, amountLabel, false));

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> modifyShoeAmount(s, amountLabel, true));

            add(amountLabel);
            add(minusButton);
            add(plusButton);
        });

        JButton emptyCartButton = new JButton("Empty cart");
        emptyCartButton.addActionListener(e -> clearShoesFromList());
        add(emptyCartButton);

        JButton placeOrderButton = new JButton("Place order");
        placeOrderButton.addActionListener(e -> orderShoes());
        add(placeOrderButton);
    }

    private JLabel createUnderScoredTextLabel(String text){
        JLabel tempLabel = new JLabel("<HTML><U><center>"+text+"</center></U></HTML>");
        tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return tempLabel;
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
                SHOES_IN_CART.add(s);
                currentAmount++;
            }else{
                if(currentAmount != 0) {
                    SHOES_IN_CART.remove(s);
                    currentAmount--;
                }
            }
            amountTextField.setText(String.valueOf(currentAmount));
        }
    }

    private void clearShoesFromList() {
        AMOUNT_LABELS.forEach(l -> l.setText("0"));
        SHOES_IN_CART.clear();
    }

    private void orderShoes() {
        SHOES_IN_CART.forEach(shoe -> {
            try {
                orderNumber = Repository.getRepository().placeOrder(Data.getData().getActiveCustomer(),
                        orderNumber, shoe);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        displayPurchase();
        clearShoesFromList();
        orderNumber = NULL_ORDER;
    }

    private void displayPurchase(){
        StringBuilder order = new StringBuilder("Order placed!\nItems in order:\n");
        int totalPrice = 0;
        for(Shoe s: SHOES_IN_CART){
            order.append(s.getBRAND()).append(" |Size: ").append(s.getSIZE()).append
                    (" |Colour: ").append(s.getColour().getName()).append(" |Price: ").append(s.getPRICE()).append(":-\n");
            totalPrice += s.getPRICE();
        }
        order.append("Total price: ").append(totalPrice).append(":-");
        JOptionPane.showMessageDialog(null, order);
    }
}