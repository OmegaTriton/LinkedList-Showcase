import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Demo implements KeyListener, MouseListener, ActionListener, FocusListener{
    private JFrame frame;
    private JTextField commandLine;
    private LinkedList<GraphicNode<Object>> nodes;

    private boolean textBoxFocus = false;

    private boolean mouseClick = false;
    private MouseEvent initialClickPos;
    private Runnable mouseDrag = new Runnable(){
        public void run(){
            while(mouseClick){
                //TODO: Implement this after implementing lines and node spawning
            }
        }
    };

    public void SpawnNewNode(){

    }
    
    public Demo(){
        initializeGraphics();
        nodes = new LinkedList<GraphicNode<Object>>();
        
        GraphicNode<Integer> box = new GraphicNode<Integer>(250, 150);
        frame.add(box);

        focusLost(new FocusEvent(commandLine, 0, true));
    }

    public void initializeGraphics(){
        frame = new JFrame();
        frame.setTitle("LinkedLists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,400);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setUndecorated(true);

        frame.addMouseListener(this);
        frame.addKeyListener(this);

        commandLine = new JTextField("Enter Command");
        commandLine.setBounds(400, 300, 100, 25);
        commandLine.addActionListener(this);
        commandLine.addFocusListener(this);
        commandLine.addMouseListener(this);
        frame.add(commandLine);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null); //centers frame to screen
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        // make sure to include textBoxFocus boolean
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        // make sure to include textBoxFocus boolean
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!e.getComponent().equals(commandLine)){
            focusLost(new FocusEvent(commandLine, FocusEvent.FOCUS_LOST, true));
            mouseClick = true;
            initialClickPos = e;
            new Thread(mouseDrag).start();
        }
        else{
            focusGained(new FocusEvent(commandLine, FocusEvent.FOCUS_GAINED, false));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseClick = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void focusGained(FocusEvent e) {
        textBoxFocus = true;
        if(commandLine.getText().equals("Enter Command"))
            commandLine.setText("");
    }
    @Override
    public void focusLost(FocusEvent e) {
        textBoxFocus = false;
        if(commandLine.getText().equals(""))
            commandLine.setText("Enter Command");
    }

    public void keyTyped(KeyEvent e) {} //unused
    public void mouseClicked(MouseEvent e) {} //unused
    public void mouseEntered(MouseEvent e) {} //unused
    public void mouseExited(MouseEvent e) {} //unused
}
