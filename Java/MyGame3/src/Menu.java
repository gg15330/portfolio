import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by george on 04/05/16.
 */

//contains code adapted from:
//http://stackoverflow.com/questions/13136701/java-button-width
public class Menu extends JPanel {

    private final int BUTTONWIDTH = 50;
    private final int BUTTONHEIGHT = 20;
    private GridBagConstraints constraints;

    //panel and constraints
    public Menu() {
        setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipadx = BUTTONWIDTH;
        constraints.ipady = BUTTONHEIGHT;
    }

    public void createButton(String text, String action, ActionListener listener) {
        JButton button = new JButton(text);
        button.setActionCommand(action);
        button.addActionListener(listener);
        add(button, constraints);
        constraints.gridy++;
    }

}
