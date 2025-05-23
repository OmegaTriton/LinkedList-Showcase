import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class NodeLine extends JPanel{
    private int width;
    private int x1, x2, y;

    public NodeLine(int x1, int x2, int y){
        this.x1 = x1;
        this.x2 = x2;
        this.y = y;
        width = x2-x1;
        if(width < 0) width *= -1;
        if(width < 400) width = 400;
        setSize(width,400);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        Line2D line = new Line2D.Float(x1,y,x2,y);
        graphics.draw(line);
    }
}
