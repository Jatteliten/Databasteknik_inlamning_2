package Frame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ShopChoices extends JPanel {
    private static ShopChoices shopChoices;
    private final JButton addToCart = new JButton("Add product to order");
    private ShopChoices(){
        setSize(800, 800);
        setLayout(new GridLayout(1,1));
    }

    public static ShopChoices getShopChoices(){
        if (shopChoices == null){
            shopChoices = new ShopChoices();
            shopChoices.initializeButtons();
        }
        return shopChoices;
    }

    private void initializeButtons(){
        addToCart.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.USER_NAME_AND_PASSWORD_ENTRY);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        shopChoices.add(addToCart);
    }
}
