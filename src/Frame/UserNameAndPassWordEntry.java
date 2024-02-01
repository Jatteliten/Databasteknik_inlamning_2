package Frame;

import DataBase.Customer;
import DataBase.Data;
import DataBase.Repository;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class UserNameAndPassWordEntry extends JPanel {
    private static UserNameAndPassWordEntry entry;
    JTextField userName = new JTextField("name");
    JTextField password = new JTextField("password");
    JButton cancel = new JButton("Cancel");
    JButton confirm = new JButton("Confirm");

    private UserNameAndPassWordEntry(){
        setLayout(new GridLayout(2,2));
    }

    public static UserNameAndPassWordEntry getUserNameAndPassWordEntry(){
        if(entry == null){
            entry = new UserNameAndPassWordEntry();
            entry.initializePanel();
        }
        return entry;
    }

    private void initializePanel(){
        userName.setForeground(Color.GRAY);
        add(userName);
        password.setForeground(Color.GRAY);

        add(password);
        cancel.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.SHOPPING_CHOICES);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(cancel);

        confirm.addActionListener(e -> {
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
                    JOptionPane.showMessageDialog(null, "Customer and password does not match");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(confirm);
    }
}
