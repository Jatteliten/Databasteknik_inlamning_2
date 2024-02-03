package frame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ShopChoices extends JPanel {
    private static ShopChoices shopChoices;
    private final JButton addToCart = new JButton("Add product to order");
    private ShopChoices(){
        setLayout(new FlowLayout());
    }

    public static ShopChoices getShopChoices(){
        if (shopChoices == null){
            shopChoices = new ShopChoices();
            shopChoices.initializeButtons();
        }
        return shopChoices;
    }

    private void initializeButtons(){
        addToCart.setPreferredSize(new Dimension(200,50));
        addToCart.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.USER_NAME_AND_PASSWORD_ENTRY);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(addToCart);
    }
}
