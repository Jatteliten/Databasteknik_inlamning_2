package Frame;

import javax.swing.*;
import java.io.IOException;

public class ShoppingFrame extends JFrame {
    private static ShoppingFrame shoppingFrame;
    private final ShopChoices shopChoicesPanel = ShopChoices.getShopChoices();
    private final UserNameAndPassWordEntry userNameAndPassWordEntry = UserNameAndPassWordEntry.getUserNameAndPassWordEntry();
    private final ShoppingPanel shoppingPanelPanel = ShoppingPanel.getAddToCart();

    private ShoppingFrame() throws IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(shopChoicesPanel);
        pack();
        setVisible(true);
    }

    public static ShoppingFrame getShoppingFrame() throws IOException {
        if(shoppingFrame == null){
            shoppingFrame = new ShoppingFrame();
        }
        return shoppingFrame;
    }

    public void switchPanel(Panels panelEnum){
        getContentPane().removeAll();
        switch (panelEnum){
            case SHOPPING_CHOICES -> add(shopChoicesPanel);
            case USER_NAME_AND_PASSWORD_ENTRY -> add(userNameAndPassWordEntry);
            case ADD_TO_CART -> add(shoppingPanelPanel);
        }
        pack();
        revalidate();
        repaint();
    }

}