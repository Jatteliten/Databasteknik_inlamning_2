package frame;

import database.Customer;
import database.Data;
import database.Repository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class UserNameAndPassWordEntry extends JPanel {
    private static UserNameAndPassWordEntry entry;
    private final JTextField userName = new JTextField("name");
    private final JTextField password = new JTextField("password");
    private final JButton cancel = new JButton("Cancel");
    private final JButton confirm = new JButton("Confirm");

    private UserNameAndPassWordEntry(){
        setLayout(new FlowLayout());
    }

    public static UserNameAndPassWordEntry getUserNameAndPassWordEntry(){
        if(entry == null){
            entry = new UserNameAndPassWordEntry();
            entry.initializePanel();
        }
        return entry;
    }

    private void initializePanel(){
        setTextFieldDesignAndListeners(userName.getText(), userName);
        add(userName);

        setTextFieldDesignAndListeners(password.getText(), password);
        add(password);

        cancel.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.SHOPPING_CHOICES);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(cancel);

        confirm.addActionListener(e -> checkCredentials());
        add(confirm);
    }

    private void setTextFieldDesignAndListeners(String textFieldName, JTextField textField){
        textField.setForeground(Color.GRAY);
        textField.setPreferredSize(new Dimension(200, 40));
        textField.addActionListener(e -> checkCredentials());
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(textField.getText().equals(textFieldName)){
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
        });
    }

    private void checkCredentials() {
        try {
            boolean customerFound = false;
            for(Customer c: Data.getData().getCustomers()){
                if(userName.getText().equalsIgnoreCase(c.getName()) &&
                        Repository.getRepository().findPassword(c, password.getText())){
                    Data.getData().setActiveCustomer(c);
                    ShoppingFrame.getShoppingFrame().switchPanel(Panels.ADD_TO_CART);
                    customerFound = true;
                    break;
                }
            }
            if(!customerFound){
                userName.setText("name");
                password.setText("password");
                userName.setForeground(Color.GRAY);
                password.setForeground(Color.GRAY);
                JOptionPane.showMessageDialog(null, "Customer and password does not match");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
