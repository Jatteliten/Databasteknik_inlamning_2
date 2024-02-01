package Frame;

import DataBase.Customer;

import javax.swing.*;
import java.io.IOException;

public class ShoppingFrame extends JFrame {
    private static ShoppingFrame shoppingFrame;
    private final ShopChoices shopChoicesPanel = ShopChoices.getShopChoices();
    private final UserNameAndPassWordEntry userNameAndPassWordEntry = UserNameAndPassWordEntry.getUserNameAndPassWordEntry();
    private final ShoppingPanel shoppingPanelPanel = ShoppingPanel.getAddToCart();

    private ShoppingFrame() throws IOException {
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(shopChoicesPanel);
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
            case SHOPPING_CHOICES -> getContentPane().add(shopChoicesPanel);
            case USER_NAME_AND_PASSWORD_ENTRY -> getContentPane().add(userNameAndPassWordEntry);
            case ADD_TO_CART -> getContentPane().add(shoppingPanelPanel);
        }
        revalidate();
        repaint();
    }

}