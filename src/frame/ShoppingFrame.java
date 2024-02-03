package frame;

import javax.swing.*;
import java.io.IOException;

public class ShoppingFrame extends JFrame {
    private static ShoppingFrame shoppingFrame;
    private final ShopChoicesPanel shopChoicesPanel = ShopChoicesPanel.getShopChoices();
    private final UserNameAndPassWordEntry userNameAndPassWordEntry = UserNameAndPassWordEntry.getUserNameAndPassWordEntry();
    private final ShoppingPanel shoppingPanel = ShoppingPanel.getShoppingPanel();

    private ShoppingFrame() throws IOException {
        setTitle("Shoe store");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        switchPanel(Panels.SHOPPING_CHOICES);
        setVisible(true);
    }

    public static ShoppingFrame getShoppingFrame() throws IOException {
        if(shoppingFrame == null){
            shoppingFrame = new ShoppingFrame();
        }
        return shoppingFrame;
    }

    public void switchPanel(Panels panelEnum) throws IOException {
        getContentPane().removeAll();
        switch (panelEnum){
            case SHOPPING_CHOICES -> add(shopChoicesPanel);
            case USER_NAME_AND_PASSWORD_ENTRY -> add(userNameAndPassWordEntry);
            case ADD_TO_CART -> { add(shoppingPanel); shoppingPanel.initializeShoppingPanel(); }
        }
        refreshFrame();
    }

    public void refreshFrame(){
        pack();
        revalidate();
        repaint();
    }

}