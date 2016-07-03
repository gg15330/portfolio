/**
 * Created by george on 13/04/16.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//overall program control -
//contains Menu and Display objects
class Jugglr {

    private final String title = "Jugglr";
    private final int WWIDTH = 480;
    private final int WHEIGHT = 640;
    private final int EASY = 1;
    private final int MED = 2;
    private final int HARD = 3;

    private Display display;
    private Menu menu;
    private JFrame gamewindow, menuwindow;

    public static void main(String[] args) {
        Jugglr program = new Jugglr();
        SwingUtilities.invokeLater(program::createMenuWindow);
    }

    //create and display main menu
    private void createMenuWindow() {
        menuwindow = new JFrame("Jugglr");
        menuwindow.setDefaultCloseOperation(gamewindow.EXIT_ON_CLOSE);
        menu = new Menu();
        menu.createButton("Easy", "easy", this::listen);
        menu.createButton("Medium", "medium", this::listen);
        menu.createButton("Hard", "hard", this::listen);
        menu.createButton("Exit", "exit", this::listen);

        menuwindow.add(menu);
        menuwindow.setResizable(false);
        menuwindow.pack();
        menuwindow.setLocationByPlatform(true);
        menuwindow.setVisible(true);
    }

    //create and display game window
    private void createGameWindow(int num_balls) {
        gamewindow = new JFrame(title);
        gamewindow.setDefaultCloseOperation(gamewindow.EXIT_ON_CLOSE);

        try {
            display = new Display(WWIDTH, WHEIGHT, num_balls);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            shutDown();
        }

        gamewindow.add(display);
        gamewindow.setResizable(false);
        gamewindow.pack();
        gamewindow.setLocationByPlatform(true);
        gamewindow.setVisible(true);
    }

    //selects difficulty based on player choice, starts game or exits
    private void listen(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("easy")) {
            createGameWindow(EASY);
            menuwindow.dispose();
        } else if(action.equals("medium")) {
            createGameWindow(MED);
            menuwindow.dispose();
        } else if(action.equals("hard")) {
            createGameWindow(HARD);
            menuwindow.dispose();
        }
        else if (action.equals("exit")) {
            shutDown();
        }
    }

    //exit the program gracefully
    private void shutDown() {
        for(Frame f : Frame.getFrames()) {
            f.dispose();
            System.out.println("JFrame disposed.");
        }
        System.out.println("Exiting...");
    }

}
