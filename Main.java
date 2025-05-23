import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("LinkedLists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(1000,400);
        frame.setResizable(false);
        // frame.setUndecorated(true);
        
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(1000,400));
        panel.setBackground(Color.WHITE);
        frame.add(panel);
        
        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane);
        
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(15,15); //Border corners arcs {width,height}
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            //Draws the rounded panel with borders.
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint background
            graphics.setColor(getForeground());
            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint border
            }
        };
        p.setBounds(250,200,40,40);
        p.setPreferredSize(new Dimension(40,40));
        p.setSize(new Dimension(40,40));
        p.setBackground(Color.RED);
        scrollPane.setViewportView(p);
        scrollPane.setLayout(null);
        
        frame.setVisible(true);
    }
}