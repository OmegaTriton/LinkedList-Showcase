import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class NodeLine extends JPanel{
    private int x1, x2, y1, y2;

    public NodeLine(int x1, int x2, int y){ //horizontal lines
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y;
        this.y2 = y;
        setSize(1000,400);
        setOpaque(false); //required because the panel background covers the screen and doesnt make anything visible
    }

    public NodeLine(int x1, int x2, int y1, int y2){ //lines in general
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        setSize(1000,400);
        setOpaque(false); //required because the panel background covers the screen and doesnt make anything visible
    }

    public void setLineBounds(int x1, int x2, int y){ //horizontal lines
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y;
        this.y2 = y;
    }
    
    public void setLineBounds(int x1, int x2, int y1, int y2){ //lines in general
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        Line2D line = new Line2D.Float(x1,y1,x2,y2);
        graphics.draw(line);
    }
}
