package frame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class StartingChoicesPanel extends JPanel {

    public StartingChoicesPanel(){
        setLayout(new BorderLayout());
        initializeButtons();
    }

    private void initializeButtons(){
        JButton login = new JButton("Log in");
        login.setPreferredSize(new Dimension(200,50));
        login.addActionListener(e -> {
            try {
                ShoppingFrame.getShoppingFrame().switchPanel(Panels.USER_NAME_AND_PASSWORD_ENTRY);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        add(login, BorderLayout.NORTH);
    }
}
