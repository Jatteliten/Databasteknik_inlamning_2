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
    private final ArrayList<Shoe> shoesInCart = new ArrayList<>();
    private final ArrayList<JTextField> amountTextFields = new ArrayList<>();

    private ShoppingPanel(){
        setSize(800, 800);
        setLayout(new GridLayout(0,4));
    }

    public static ShoppingPanel getAddToCart() throws IOException {
        if(shoppingPanel == null){
            shoppingPanel = new ShoppingPanel();
            shoppingPanel.initializePanel();
        }
        return shoppingPanel;
    }

    private void initializePanel() throws IOException {
        for(Shoe s: Data.getData().getShoes()){
            JTextField shoeInformation = new JTextField(s.getBrand() + " " + s.getColour().getName() + " " + s.getSize() + " " +
                    s.getPrice() + ":-");
            shoeInformation.setEditable(false);
            add(shoeInformation);

            JTextField amountTextField = new JTextField("0");
            amountTextField.setHorizontalAlignment(JTextField.CENTER);
            amountTextField.setEditable(false);
            amountTextFields.add(amountTextField);

            JButton minusButton = new JButton("-");
            minusButton.addActionListener(e -> subtractShoe(s, amountTextField));

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> addShoe(s, amountTextField));


            add(amountTextField);
            add(minusButton);
            add(plusButton);
        }
        JButton cancelButton = new JButton("Empty cart");
        cancelButton.addActionListener(e -> clearShoesFromList());
        add(cancelButton);
        JButton confirmButton = new JButton("Place order");
        confirmButton.addActionListener(e -> orderShoes());
        add(confirmButton);
    }

    private void addShoe(Shoe s, JTextField amountTextField) {
        int currentAmount = Integer.parseInt(amountTextField.getText());
        if(currentAmount <= s.getStock()) {
            shoesInCart.add(s);
            currentAmount++;
            amountTextField.setText(String.valueOf(currentAmount));
        }
    }

    private void subtractShoe(Shoe s, JTextField amountTextField) {
        int currentAmount = Integer.parseInt(amountTextField.getText());
        if(currentAmount != 0) {
            shoesInCart.remove(s);
            currentAmount--;
            amountTextField.setText(String.valueOf(currentAmount));
        }
    }

    private void clearShoesFromList() {
        for(JTextField text: amountTextFields){
            text.setText("0");
        }
        shoesInCart.clear();
    }

    private void orderShoes() {
        int orderNumber = 0;
        for(int i = 0; i < shoesInCart.size(); i++){
            try {
                if(i == 0){
                    orderNumber = Repository.getRepository().placeOrder(Data.getData().getActiveCustomer(),
                            -1, shoesInCart.get(i));
                }else{
                    Repository.getRepository().placeOrder(Data.getData().getActiveCustomer(), orderNumber, shoesInCart.get(i));
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        displayPurchase();
        clearShoesFromList();
    }

    private void displayPurchase(){
        StringBuilder order = new StringBuilder("Order placed!\nItems in order:\n");
        int totalPrice = 0;
        for(Shoe s: shoesInCart){
            order.append(s.getBrand()).append(" |Size: ").append(s.getSize()).append
                    (" |Colour: ").append(s.getColour().getName()).append(" |Price: ").append(s.getPrice()).append(":-\n");
            totalPrice += s.getPrice();
        }
        order.append("Total price: ").append(totalPrice).append(":-");
        JOptionPane.showMessageDialog(null, order);
    }
}