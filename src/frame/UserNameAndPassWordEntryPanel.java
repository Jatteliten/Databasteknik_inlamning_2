package frame;

import database.Customer;
import database.Data;
import database.Repository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class UserNameAndPassWordEntryPanel extends JPanel {
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private final JTextField userName = setTextFieldDesignAndListeners(NAME);
    private final JTextField password = setTextFieldDesignAndListeners(PASSWORD);
    private final JButton cancel = new JButton("Cancel");
    private final JButton confirm = new JButton("Confirm");

    public UserNameAndPassWordEntryPanel(){
        setLayout(new FlowLayout());
        initializePanel();
    }

    private void initializePanel(){
        add(userName);
        add(password);

        cancel.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.STARTING_CHOICES);
                resetTextField(password, PASSWORD);
                resetTextField(userName, NAME);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(cancel);

        confirm.addActionListener(e -> checkCredentials());
        add(confirm);
    }

    private JTextField setTextFieldDesignAndListeners(String textFieldName){
        JTextField textField = new JTextField();
        resetTextField(textField, textFieldName);
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
        return textField;
    }

    private void resetTextField(JTextField textField, String text){
        textField.setText(text);
        textField.setForeground(Color.GRAY);
    }

    private void checkCredentials() {
        try {
            boolean customerFound = false;
            for(Customer c: Data.getData().getCustomers()){
                if(userName.getText().equalsIgnoreCase(c.getName()) &&
                        Repository.getRepository().findPassword(c, password.getText())){
                    Data.getData().setActiveCustomer(c);
                    ShoppingFrame.getShoppingFrame().switchPanel(Panels.SHOPPING_PANEL);
                    customerFound = true;
                    break;
                }
            }
            if(!customerFound){
                resetTextField(userName, NAME);
                resetTextField(password, PASSWORD);
                JOptionPane.showMessageDialog(null, "Customer name and password does not match");
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
