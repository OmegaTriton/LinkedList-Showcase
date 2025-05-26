import java.awt.*;

public class Pointer extends NodeLine{
    public static int YBuffer = 50;
    public String name;
    private int pointerX, pointerY;
    private GraphicNode<Object> node;

    public Pointer(String name, GraphicNode<Object> node){
        super(node.getX() + GraphicNode.width/2, node.getX() + GraphicNode.width/2, node.getY() - YBuffer, node.getY() - 10);
        this.name = name;
        this.node = node;
        /* six is just an arbitrary number based on testing 
         * because i couldn't be bothered to create a width function 
         * that takes each character and adds the widths of each*/
        pointerX = node.getX() + GraphicNode.width/2 - (int)(name.length()/2.0 * 6); 
        /* no need to account for text size
         * apparently when it draws it draws from bottom left corner
         * 10 is just to match the other 10, arbitrary */
        pointerY = node.getY() - YBuffer - 10; 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(name, pointerX, pointerY);
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        int dx = x - getX();
        int dy = y - getY();
        pointerX += dx;
        pointerY += dy;
        revalidate();
        repaint();
    }
}
