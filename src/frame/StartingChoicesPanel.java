package frame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class StartingChoicesPanel extends JPanel {
    private final JButton login = new JButton("Log in");
    public StartingChoicesPanel(){
        setLayout(new FlowLayout());
        initializeButtons();
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
