package frame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ShopChoicesPanel extends JPanel {
    private static ShopChoicesPanel shopChoices;
    private final JButton login = new JButton("Log in");
    private ShopChoicesPanel(){
        setLayout(new FlowLayout());
    }

    public static ShopChoicesPanel getShopChoices(){
        if (shopChoices == null){
            shopChoices = new ShopChoicesPanel();
            shopChoices.initializeButtons();
        }
        return shopChoices;
    }

    private void initializeButtons(){
        login.setPreferredSize(new Dimension(200,50));
        login.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.USER_NAME_AND_PASSWORD_ENTRY);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(login);
    }
}
