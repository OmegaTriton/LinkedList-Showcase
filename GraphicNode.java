import javax.swing.*;
import java.awt.*;

public class GraphicNode<T> extends JPanel {
    public static int width = 150, height = 100;
    public static Point defaultPos = new Point(425,150);
    private LinkedList.Node<T> node;
    private JLabel text;
    private GridBagConstraints c = new GridBagConstraints();

    public GraphicNode(int x, int y){
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBounds(x, y, width, height);
        setOpaque(false);

        node = new LinkedList.Node<T>(null);
        c.gridy = 1;
        text = new JLabel("Null Node");
        add(text, c);
    }

    public GraphicNode(int x, int y, LinkedList.Node<T> n){
        super(new GridBagLayout());
        setBackground(Color.WHITE);
        setBounds(x, y, width, height);
        setOpaque(false);
        
        node = n;
        c.gridy = 1;
        text = new JLabel(n.data.toString());
        add(text, c);
    }

    public void setNode(LinkedList.Node<T> n){
        node = n;
        remove(text);
        text = new JLabel(n.data.toString());
        add(text, c);
    }

    public LinkedList.Node<T> getNode(){
        return node;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(25, 25);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draws the rounded panel with borders.
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint background
        graphics.setColor(getForeground());
        graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint border
    }
}
