import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Demo implements KeyListener, MouseListener, ActionListener, FocusListener, ItemListener{
    private JFrame frame;
    private JTextField commandLine;
    private JComboBox<String> listSelector;
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
    private JPanel inputErrorMessagePanel;
    private Runnable showInputErrorMessage = new Runnable(){
        public void run(){
            //TODO:display error message for a short time
            // FIX ISSUE OF HAVING THIS THREAD CALLED MULTIPLE TIMES
            JLabel message = new JLabel(inputErrorMessage);
            inputErrorMessagePanel.add(message);
            inputErrorMessagePanel.setVisible(true);
            try{
                Thread.sleep(10000);
            } catch(Exception e){
                e.printStackTrace();
            }
            inputErrorMessagePanel.setVisible(false);
            inputErrorMessagePanel.remove(message);
        }
    };

    public void SpawnNewNode(){
        inputErrorMessage = "Unsafe coding - Not Specifying Type through <>";
        new Thread(showInputErrorMessage).start();
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(GraphicNode.defaultPos.x, GraphicNode.defaultPos.y);
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y);
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
            GraphicNode<Object> newNode = new GraphicNode<Object>(GraphicNode.defaultPos.x, GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
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
            GraphicNode<Object> newNode = new GraphicNode<Object>(GraphicNode.defaultPos.x,GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
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
            //changes listSelector to match
            listSelector.setSelectedItem(listName);
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
        SpawnNewNode("test string2");
        SpawnNewNode(124);
        SpawnNewNode(125.0);

        focusLost(new FocusEvent(commandLine, 0, true));
        changeLists("stringList");
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
        commandLine.setBounds(400, 300, 200, 25);
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

        String[] defaultLists = {"objectList", "stringList", "integerList", "doubleList"};
        listSelector = new JComboBox<String>(defaultLists);
        listSelector.addItemListener(this);
        listSelector.setBounds(400,0,200,50);
        listSelector.setBackground(new Color(255, 222, 33)); //cozy yellow ish
        frame.add(listSelector);
        
        inputErrorMessagePanel = new JPanel();
        inputErrorMessagePanel.setBounds(350, 350, 300, 25);
        inputErrorMessagePanel.setBackground(frame.getBackground());
        inputErrorMessagePanel.setOpaque(false);
        frame.add(inputErrorMessagePanel);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null); //centers frame to screen
        inputErrorMessagePanel.setVisible(false);
    }

    public void moveNodes(int pixels){
        LinkedList.Node<GraphicNode<Object>> it = lists.get(listSelector.getSelectedItem()).getValue().getFirst();
        while(it != null){
            it.data.setLocation(it.data.getX() + pixels, it.data.getY());
            it = it.next;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(!textBoxFocus){
            switch(e.getKeyCode()){
                case KeyEvent.VK_RIGHT: moveNodes(5); break;
                case KeyEvent.VK_LEFT: moveNodes(-5); break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
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
        frame.requestFocus(); //required otherwise keylistener doesnt work
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            String selected = (String) listSelector.getSelectedItem();
            changeLists(selected);
            frame.requestFocus(); //required otherwise keylistener doesnt work
        }
    }

    public void keyTyped(KeyEvent e) {} //unused
    public void mouseClicked(MouseEvent e) {} //unused
    public void mouseEntered(MouseEvent e) {} //unused
    public void mouseExited(MouseEvent e) {} //unused
}
