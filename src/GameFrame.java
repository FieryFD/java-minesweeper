import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame implements ActionListener {
    GamePanel gamePanel;
    GridBagLayout gbl;
    GridBagConstraints gbc;

    JButton newGameBtn;
    JLabel bombAmount;
    JLabel timer;

    public int rows = 16;
    public int columns = 30;
    public int difficulty = 20;

    public GameFrame(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(rows*35, columns*35);
        this.setResizable(false);
        this.setTitle("MINESWEEPER mini Project");

        gbl = new GridBagLayout();
        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        this.setLayout(gbl);

        bombAmount = new JLabel("BombCount", SwingConstants.CENTER);
        gbc.gridx = 0;
        this.add(bombAmount, gbc);

        newGameBtn = new JButton("New Game");
        newGameBtn.addActionListener(this);
        gbc.gridx = 1;
        this.add(newGameBtn, gbc);

        timer = new JLabel("Time: 000 seconds", SwingConstants.CENTER);
        gbc.gridx = 2;
        this.add(timer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gamePanel = new GamePanel(rows, columns, difficulty, timer,bombAmount);
        this.add(gamePanel, gbc);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource()== newGameBtn){
            this.remove(gamePanel);
            gamePanel = new GamePanel(rows, columns, difficulty, timer,bombAmount);
            this.add(gamePanel, gbc);
            timer.setText("Timer: 000 seconds");
            this.validate();
            this.repaint();
        }
    }
}
