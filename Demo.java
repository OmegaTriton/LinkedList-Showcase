import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Demo implements KeyListener, MouseListener, ActionListener, FocusListener{
    private JFrame frame;
    private JTextField commandLine;
    private HashMap<String, Pair<String, LinkedList<GraphicNode<Object>>>> lists; //HashMap< "name" , {"type", llist}> 

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

    private String inputErrorMessage;
    private Runnable showInputErrorMessage = new Runnable(){
        public void run(){
            //TODO:display error message for a short time
        }
    };

    public void SpawnNewNode(){
        inputErrorMessage = "Unsafe coding - Not Specifying Type through <>";
        new Thread(showInputErrorMessage).start();
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(250,150);
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + 50, 150);
            nodes.addTail(newNode);
            frame.add(newNode);
        }
        changeLists("objectList");
    }

    public void SpawnNewNode(Object data){
        String listName = "objectList";
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        if(data instanceof String) {nodes = lists.get("stringList").getValue(); listName = "stringList";}
        else if(data instanceof Integer) {nodes = lists.get("integerList").getValue(); listName = "integerList";}
        else if(data instanceof Double || data instanceof Float) {nodes = lists.get("doubleList").getValue(); listName = "doubleList";}
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(250,150, new LinkedList.Node<Object>(data));
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + 50, 150, new LinkedList.Node<Object>(data));
            nodes.addTail(newNode);
            frame.add(newNode);
        }
        changeLists(listName);
    }

    public void SpawnNewNode(Object data, String listName){
        String accListName = "objectList";
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        if(lists.keySet().contains(listName)){
            accListName = listName;
            nodes = lists.get(listName).getValue();
        }
        else {
            inputErrorMessage = "Invalid List Name - added to standard lists";
            new Thread(showInputErrorMessage).start();
            if(data instanceof String) {nodes = lists.get("stringList").getValue(); accListName = "stringList";}
            else if(data instanceof Integer) {nodes = lists.get("integerList").getValue(); accListName = "integerList";}
            else if(data instanceof Double || data instanceof Float) {nodes = lists.get("doubleList").getValue(); accListName = "doubleList";}
        }
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(250,150, new LinkedList.Node<Object>(data));
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + 50, 150, new LinkedList.Node<Object>(data));
            nodes.addTail(newNode);
            frame.add(newNode);
        }
        changeLists(accListName);
    }
    
    public void changeLists(String listName){
        if(lists.keySet().contains(listName)){
            //hides every graphic node component and shows all nodes of the new list
            for(String key : lists.keySet()){
                boolean visible = false;
                if(key.equals(listName)) visible = true;
                LinkedList.Node<GraphicNode<Object>> it = lists.get(key).getValue().getFirst();
                while(it != null){
                    it.data.setVisible(visible);
                    it = it.next;
                }
            }
        }
    }

    public Demo(){
        initializeGraphics();
        lists = new HashMap<String, Pair<String, LinkedList<GraphicNode<Object>>>>();
        lists.put("objectList", new Pair<String, LinkedList<GraphicNode<Object>>>("Object", new LinkedList<GraphicNode<Object>>()));
        lists.put("stringList", new Pair<String, LinkedList<GraphicNode<Object>>>("String", new LinkedList<GraphicNode<Object>>()));
        lists.put("integerList", new Pair<String, LinkedList<GraphicNode<Object>>>("Integer", new LinkedList<GraphicNode<Object>>()));
        lists.put("doubleList", new Pair<String, LinkedList<GraphicNode<Object>>>("Double", new LinkedList<GraphicNode<Object>>()));
        
        SpawnNewNode();
        SpawnNewNode("test string");
        SpawnNewNode(124);
        SpawnNewNode(125.0);

        focusLost(new FocusEvent(commandLine, 0, true));
        changeLists("doubleList");
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

        /*this thing kept giving me issues when i tried
        to make shorter lines because it would have some weird behavior
        and not be precise so i just made the line go through the entire screen*/
        NodeLine nodeLine = new NodeLine(0,1000,200); 
        nodeLine.setOpaque(false); //required because the panel background covers the screen and doesnt make anything visible
        frame.add(nodeLine);

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
        commandLine.setFocusable(true);
        commandLine.grabFocus();
        textBoxFocus = true;
        if(commandLine.getText().equals("Enter Command"))
            commandLine.setText("");
    }
    @Override
    public void focusLost(FocusEvent e) {
        commandLine.setFocusable(false);
        textBoxFocus = false;
        if(commandLine.getText().equals(""))
            commandLine.setText("Enter Command");
    }

    public void keyTyped(KeyEvent e) {} //unused
    public void mouseClicked(MouseEvent e) {} //unused
    public void mouseEntered(MouseEvent e) {} //unused
    public void mouseExited(MouseEvent e) {} //unused
}
