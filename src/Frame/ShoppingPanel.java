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

    public ArrayList<Shoe> getShoesInCart() {
        return shoesInCart;
    }

    private void initializePanel() throws IOException {
        for(Shoe s: Data.getData().getShoes()){
            JButton shoeButton = new JButton(s.getBrand() + " " + s.getColour().getName() + " " + s.getSize() + " " +
                    s.getPrice() + ":-");
            add(shoeButton);

            JTextField amountTextField = new JTextField("0");
            amountTextField.setHorizontalAlignment(JTextField.CENTER);
            amountTextField.setEditable(false);
            amountTextFields.add(amountTextField);

            JButton minusButton = new JButton("-");
            minusButton.addActionListener(e -> {
                int currentAmount = Integer.parseInt(amountTextField.getText());
                if(currentAmount != 0) {
                    shoesInCart.remove(s);
                    currentAmount--;
                    amountTextField.setText(String.valueOf(currentAmount));
                }
            });

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> {
                int currentAmount = Integer.parseInt(amountTextField.getText());
                if(currentAmount <= s.getStock()) {
                    shoesInCart.add(s);
                    currentAmount++;
                    amountTextField.setText(String.valueOf(currentAmount));
                }
            });


            add(amountTextField);
            add(minusButton);
            add(plusButton);
        }
        JButton cancelButton = new JButton("Empty cart");
        cancelButton.addActionListener(e ->{
            for(JTextField text: amountTextFields){
                text.setText("0");
            }
            shoesInCart.clear();
        });
        add(cancelButton);
        JButton confirmButton = new JButton("Place order");
        confirmButton.addActionListener(e ->{
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
        });
        add(confirmButton);
    }

}