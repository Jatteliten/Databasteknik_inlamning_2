package Frame;

import DataBase.Repository;
import DataBase.Shoe;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class AddToCart extends JPanel {
    private static AddToCart addToCart;
    private final ArrayList<JTextField> amountTextFields = new ArrayList<>();

    private AddToCart(){
        setSize(800, 800);
        setLayout(new GridLayout(0,4));
    }

    public static AddToCart getAddToCart() throws IOException {
        if(addToCart == null){
            addToCart = new AddToCart();
            addToCart.initializePanel();
        }
        return addToCart;
    }

    private void initializePanel() throws IOException {
        for(Shoe s: Repository.getRepository().getShoes()){
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
                    currentAmount--;
                    amountTextField.setText(String.valueOf(currentAmount));
                }
            });

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> {
                int currentAmount = Integer.parseInt(amountTextField.getText());
                if(currentAmount <= s.getStock()) {
                    currentAmount++;
                    amountTextField.setText(String.valueOf(currentAmount));
                }
            });


            add(amountTextField);
            add(minusButton);
            add(plusButton);
        }
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e ->{
            for(JTextField text: amountTextFields){
                text.setText("0");
            }
        });
        add(cancelButton);
        JButton confirmButton = new JButton("Add to cart");
        add(confirmButton);
    }

}