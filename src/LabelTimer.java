import javax.swing.*;

public class LabelTimer {
    private int counter = 0;
    private Timer timer;

    public void startTimer(JLabel label) {
        timer = new Timer(1000, e -> {
            counter++;
            label.setText("Time: " + String.format("%03d", counter) + " seconds");
        });
        timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
}
