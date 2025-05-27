import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Demo implements KeyListener, MouseListener, ActionListener, FocusListener, ItemListener{
    private JFrame frame;
    private JTextField commandLine;
    private JComboBox<String> listSelector;
    private HashMap<String, Pair<String, LinkedList<GraphicNode<Object>>>> lists; //HashMap< "listName" , {"type", llist}> 
    private HashMap<String, ArrayList<NodeLine>> listLines; //HashMap< "listName", all lines per list>
    private HashMap<String, ArrayList<Pointer>> listPointers; //HashMap < "listName" , all pointers per list> - excludes merged pointers and hidden pointers
    private HashMap<String, ArrayList<Pointer>> listMergedPointers; //HashMap < "listName" , all pointers that merge other pointers per list>
    private HashMap<String, ArrayList<Pointer>> listHiddenPointers; //HashMap < "listName" , all pointers that are hidden because they merged per list>

    private boolean mouseClick = false;
    private Point initialClickPos;
    private Runnable mouseDrag = new Runnable(){
        public void run(){
            while(mouseClick){
                Point current = MouseInfo.getPointerInfo().getLocation();
                int pixels = current.x-initialClickPos.x;
                initialClickPos = current;
                moveNodes(pixels);
                try{
                    Thread.sleep(1);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private String inputErrorMessage;
    private JPanel inputErrorMessagePanel;
    private Runnable showInputErrorMessage = new Runnable(){
        public void run(){
            // everything this thread is called, old messages will be wiped if still shown on screen or if failed to remove
            if(inputErrorMessagePanel.getComponentCount() > 0) 
                inputErrorMessagePanel.removeAll();
            JLabel message = new JLabel(inputErrorMessage);
            inputErrorMessagePanel.add(message);
            //these two are required because they refresh the panel after it's removed everything and added the new message
            inputErrorMessagePanel.revalidate();
            inputErrorMessagePanel.repaint();
            
            inputErrorMessagePanel.setVisible(true);
            try{
                Thread.sleep(5000);
            } catch(Exception e){
                e.printStackTrace();
            }
            /* if panel contains message 
             * (if no other message was added to the panel or 
             * if panel wasnt cleared during the sleep time) 
             * then hide panel and remove label*/
            //only refreshes if the message on this thread is the newest one
            for(Component c : inputErrorMessagePanel.getComponents()){ //technically dont need the for loop now but if something fails to get cleared out it wont affect it
                if( ((JLabel) c) == message) { //here .equals vs == shouldnt matter, but == represents what im trying to show in the expression more
                    inputErrorMessagePanel.setVisible(false);
                    inputErrorMessagePanel.remove(message);
                }
            }
        }
    };

    public void SpawnNewNode(){
        inputErrorMessage = "Unsafe coding - Not Specifying Type through <>";
        new Thread(showInputErrorMessage).start();
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        ArrayList<NodeLine> lines = listLines.get("objectList");
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(GraphicNode.defaultPos.x, GraphicNode.defaultPos.y);
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y);
            NodeLine newLine = new NodeLine(nodes.getLast().data.getX() + GraphicNode.width, nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y + GraphicNode.height/2);
            nodes.addTail(newNode);
            lines.add(newLine);
            frame.add(newNode);
            frame.add(newLine);
        }
        changeLists("objectList");
    }

    public void SpawnNewNode(Object data){
        String accListName = "objectList";
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        ArrayList<NodeLine> lines = listLines.get("objectList");
        if(data instanceof String) accListName = "stringList";
        else if(data instanceof Integer) accListName = "integerList";
        else if(data instanceof Double || data instanceof Float) accListName = "doubleList";
        nodes = lists.get(accListName).getValue();
        lines = listLines.get(accListName);
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(GraphicNode.defaultPos.x, GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
            NodeLine newLine = new NodeLine(nodes.getLast().data.getX() + GraphicNode.width, nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y + GraphicNode.height/2);
            nodes.addTail(newNode);
            lines.add(newLine);
            frame.add(newNode);
            frame.add(newLine);
        }
        changeLists(accListName);
    }

    public void SpawnNewNode(Object data, String listName){
        String accListName = "objectList";
        LinkedList<GraphicNode<Object>> nodes = lists.get("objectList").getValue();
        ArrayList<NodeLine> lines = listLines.get("objectList");
        if(lists.keySet().contains(listName)){
            accListName = listName;
            nodes = lists.get(listName).getValue();
            lines = listLines.get(listName);
        }
        else {
            inputErrorMessage = "Invalid List Name - added to standard lists";
            new Thread(showInputErrorMessage).start();
            if(data instanceof String) accListName = "stringList";
            else if(data instanceof Integer) accListName = "integerList";
            else if(data instanceof Double || data instanceof Float) accListName = "doubleList";
            nodes = lists.get(accListName).getValue();
            lines = listLines.get(accListName);
        }
        if(nodes.isEmpty()){
            GraphicNode<Object> newNode = new GraphicNode<Object>(GraphicNode.defaultPos.x,GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
            nodes.addHead(newNode);
            frame.add(newNode);
        }
        else{
            GraphicNode<Object> newNode = new GraphicNode<Object>(nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y, new LinkedList.Node<Object>(data));
            NodeLine newLine = new NodeLine(nodes.getLast().data.getX() + GraphicNode.width, nodes.getLast().data.getX() + GraphicNode.width + 50, GraphicNode.defaultPos.y + GraphicNode.height/2);
            nodes.addTail(newNode);
            lines.add(newLine);
            frame.add(newNode);
            frame.add(newLine);
        }
        changeLists(accListName);
    }
    
    public void SpawnNewPointer(String pointerName, GraphicNode<Object> node, String listName){
        if(node == null){
            inputErrorMessage = "NullPointerExecption - tried to initialize a pointer to null destination";
            new Thread(showInputErrorMessage).start();
            return;
        }
        String accListName = "objectList";
        Pointer pointer = new Pointer(pointerName, node);
        if(lists.keySet().contains(listName)){
            accListName = listName;
        }
        else{
            inputErrorMessage = "Invalid List Name - added to standard lists";
            new Thread(showInputErrorMessage).start();
            if(node.getNode().data instanceof String) accListName = "stringList";
            else if(node.getNode().data instanceof Integer) accListName = "integerList";
            else if(node.getNode().data instanceof Double || node.getNode().data instanceof Float) accListName = "doubleList";
        }
        for(Pointer p : listPointers.get(accListName)){
            if(p.name.equals(pointerName)){
                inputErrorMessage = "Invalid Pointer name - pointer name already exists";
                new Thread(showInputErrorMessage).start();
                return;
            }
        }
        boolean merged = false;
        for(Pointer mp : listMergedPointers.get(accListName)){ //checks to see if a merged pointer exists on that spot first
            if(mp.node.equals(node)){
                mp.setName(mp.name + ", " + pointerName);
                listHiddenPointers.get(accListName).add(pointer);
                merged = true;
            }
        }
        /* if merged pointer exists on that spot skip this loop because it will find nothing 
         * because it will not show the hidden pointers that are also on that node in the listPointers
         * but also because i only want to add it to the merged pointer if its there and not the regular pointer */
        // checks to see if a regular pointer exists on that spot if there is no merged pointer there
        Pointer otherPointer = pointer;
        if(!merged) {for(Pointer p : listPointers.get(accListName)){ // creates a merged pointer there if a regular pointer exists on that spot
            if(p.node.equals(node)){ //creating a new merged pointer to display when two pointers are on the same point
                Pointer mergedPointer = new Pointer(p.name + ", " + pointerName, node);
                listMergedPointers.get(accListName).add(mergedPointer);
                p.setVisible(false);
                pointer.setVisible(false);
                listHiddenPointers.get(accListName).add(pointer);
                listHiddenPointers.get(accListName).add(p);
                otherPointer = p;
                merged = true;
                frame.add(mergedPointer);
            }
        }}
        //doesnt add to the listPointers if it is merged because listPointers dont include hidden pointers
        if(!merged) listPointers.get(accListName).add(pointer);
        else {
            listPointers.get(accListName).remove(pointer);
            if(!otherPointer.equals(pointer))
                listPointers.get(accListName).remove(otherPointer);
        }
        frame.add(pointer);
        changeLists(accListName);
    }

    public void changeLists(String listName){
        if(lists.keySet().contains(listName)){
            //hides every graphic node component and shows all nodes of the new list
            for(String key : lists.keySet()){
                boolean visible = false; //default false, hiding everything
                if(key.equals(listName)) visible = true; //true if changing to it
                //iterates over components to set visibility
                LinkedList.Node<GraphicNode<Object>> it = lists.get(key).getValue().getFirst();
                while(it != null){
                    it.data.setVisible(visible);
                    it = it.next;
                }
                //iterates over lines to set visibility
                for(NodeLine line : listLines.get(key)){
                    line.setVisible(visible);
                }
                for(Pointer pointer : listPointers.get(key)){
                    pointer.setVisible(visible);
                }
                for(Pointer mergedPointer : listMergedPointers.get(key)){
                    mergedPointer.setVisible(visible);
                }
            }
            //changes listSelector to match
            listSelector.setSelectedItem(listName);
        }
    }

    public void addList(String listName){

    }
    
    public void RemoveNode(){

    }
    
    public void MovePointer(Pointer pointer, GraphicNode<Object> newNode){
        //pre-requisite - pointer is not moving to the same spot
        if(pointer.node.equals(newNode)) return;
        //moving out of merged pointer
        String listName = "";
        Pointer mergedPointer = pointer; //just so when it compiles it wont think it has a chance of not being assigned
        outerLoop: //labelling loops
        for(String key : lists.keySet()){ //loop to find if the pointer is currently part of a merged pointer to get listName and the mergedPointer
            for(Pointer p : listMergedPointers.get(key)){
                if(p.node.equals(pointer.node)){
                    listName = key;
                    mergedPointer = p;
                    break outerLoop; //breaking loop labeled outerloop - neat little thing i learned
                }
            }
        }
        if(!listName.equals("")){//if pointer is part of the merged pointer
            String newName = mergedPointer.name;
            String half1 = "", half2 = "";
            
            //removing pointer.name from mergedPointer.name
            int index = newName.indexOf(pointer.name);
            if(index != 0) half1 = newName.substring(0, index);
            if(index + pointer.name.length() < newName.length()) half2 = newName.substring(index+pointer.name.length(), newName.length());

            //removing ", " whether it's after the pointer name or before the pointer name
            if(half2.length() >= 2 && half2.charAt(0) == ',' && half2.charAt(1) == ' ') half2 = half2.substring(2);
            else half1 = half1.substring(0, half1.length()-2);
            newName = half1 + half2;

            //if mergedPointer is no longer merged - it only has one pointer in it because all commas and spaces are removed
            if(newName.indexOf(", ") == -1) {
                listMergedPointers.get(listName).remove(mergedPointer);
                Pointer hiddenPointer = pointer;//just so when it compiles it wont think it has a chance of not being assigned
                for(Pointer p : listHiddenPointers.get(listName)){
                    if(p.name.equals(newName)){
                        hiddenPointer = p;
                        break;
                    }
                }
                listHiddenPointers.get(listName).remove(hiddenPointer);
                listPointers.get(listName).add(hiddenPointer);
                hiddenPointer.setVisible(true);
                frame.remove(mergedPointer);
            }
            else mergedPointer.setName(newName);

            pointer.setVisible(true);
            listHiddenPointers.get(listName).remove(pointer);
            listPointers.get(listName).add(pointer);
        }
        //moving into another pointer - creating merged pointer
        listName = "";
        Pointer otherPointer = pointer;//just so when it compiles it wont think it has a chance of not being assigned
        outerLoop:
        for(String key : lists.keySet()){
            for(Pointer p : listMergedPointers.get(key)){
                if(p.node.equals(newNode)){
                    listName = key;
                    otherPointer = p;
                    break outerLoop;
                }
            }
        }
        if(!listName.equals("")){ // adding to merged pointer
            otherPointer.setName(otherPointer.name + ", " + pointer.name);
            listHiddenPointers.get(listName).add(pointer);
            listPointers.get(listName).remove(pointer);
            pointer.setVisible(false);
        }
        else{ // creating merged pointer with another regular pointer
            outerLoop:
            for(String key : lists.keySet()){
                for(Pointer p : listPointers.get(key)){
                    if(p.node.equals(newNode)){
                        listName = key;
                        otherPointer = p;
                        break outerLoop;
                    }
                }
            }
            if(!listName.equals("")){ //if a regular pointer exists on the newNode position - create the merged pointer
                mergedPointer = new Pointer(otherPointer.name + ", " + pointer.name, newNode);
                listMergedPointers.get(listName).add(mergedPointer);
                otherPointer.setVisible(false);
                pointer.setVisible(false);
                listHiddenPointers.get(listName).add(pointer);
                listHiddenPointers.get(listName).add(otherPointer);
                listPointers.get(listName).remove(otherPointer);
                listPointers.get(listName).remove(pointer);
                frame.add(mergedPointer);
            }
        }
        //regular stuff
        pointer.setNode(newNode);
    }

    public Demo(){
        initializeGraphics();
        lists = new HashMap<String, Pair<String, LinkedList<GraphicNode<Object>>>>();
        lists.put("objectList", new Pair<String, LinkedList<GraphicNode<Object>>>("Object", new LinkedList<GraphicNode<Object>>()));
        lists.put("stringList", new Pair<String, LinkedList<GraphicNode<Object>>>("String", new LinkedList<GraphicNode<Object>>()));
        lists.put("integerList", new Pair<String, LinkedList<GraphicNode<Object>>>("Integer", new LinkedList<GraphicNode<Object>>()));
        lists.put("doubleList", new Pair<String, LinkedList<GraphicNode<Object>>>("Double", new LinkedList<GraphicNode<Object>>()));
        
        listLines = new HashMap<String, ArrayList<NodeLine>>();
        listPointers = new HashMap<String, ArrayList<Pointer>>();
        listMergedPointers = new HashMap<String, ArrayList<Pointer>>();
        listHiddenPointers = new HashMap<String, ArrayList<Pointer>>();

        for(String key : lists.keySet()) {//initializes listLines, listPointers, merged and hidden pointers
            listLines.put(key, new ArrayList<NodeLine>());
            listPointers.put(key, new ArrayList<Pointer>());
            listMergedPointers.put(key, new ArrayList<Pointer>());
            listHiddenPointers.put(key, new ArrayList<Pointer>());
        }

        SpawnNewNode();
        SpawnNewNode("test string");
        SpawnNewNode("test string2");
        SpawnNewNode("test string3");
        SpawnNewNode(124);
        SpawnNewNode(125.0);

        SpawnNewPointer("head", lists.get("stringList").getValue().getFirst().data, "stringList");
        SpawnNewPointer("it", lists.get("stringList").getValue().getFirst().data, "stringList");
        SpawnNewPointer("a", lists.get("stringList").getValue().getFirst().next.data, "stringList");

        MovePointer(listHiddenPointers.get("stringList").get(1), lists.get("stringList").getValue().getFirst().next.data);
        MovePointer(listPointers.get("stringList").get(0), lists.get("stringList").getValue().getFirst().next.data);
        // MovePointer(listHiddenPointers.get("stringList").get(1), lists.get("stringList").getValue().getLast().data);
        MovePointer(listHiddenPointers.get("stringList").get(1), lists.get("stringList").getValue().getFirst().data);
        // MovePointer(listHiddenPointers.get("stringList").get(1), lists.get("stringList").getValue().getLast().data);
        MovePointer(listHiddenPointers.get("stringList").get(0), lists.get("stringList").getValue().getLast().data);

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
        String listName = (String) listSelector.getSelectedItem();
        LinkedList.Node<GraphicNode<Object>> it = lists.get(listName).getValue().getFirst();
        while(it != null){
            it.data.setLocation(it.data.getX() + pixels, it.data.getY());
            it = it.next;
        }
        for(NodeLine line : listLines.get(listName)){
            line.setLocation(line.getX() + pixels, line.getY());
        }
        for(Pointer pointer : listPointers.get(listName)){
            pointer.setLocation(pointer.getX() + pixels, pointer.getY());
        }
        for(Pointer mergedPointer : listMergedPointers.get(listName)){
            mergedPointer.setLocation(mergedPointer.getX() + pixels, mergedPointer.getY());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int displacement = 10;
        if(frame.hasFocus()){
            switch(e.getKeyCode()){
                case KeyEvent.VK_RIGHT: moveNodes(displacement); break;
                case KeyEvent.VK_LEFT: moveNodes(-displacement); break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getComponent().equals(frame)){
            focusLost(new FocusEvent(commandLine, FocusEvent.FOCUS_LOST, true));
            mouseClick = true;
            initialClickPos = MouseInfo.getPointerInfo().getLocation();
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
        //some notes abt how im gonna interpret the command line: 
        /* a class name ALWAYS comes before <>, in this case it can only be linked list and linked list node
         * inside <> must be a valid class type
         * look for = be sure to include type matching on both sides
         * 
         */
        /* Some Required things it needs to do:
         * make lists (LinkedList)
         * add to lists (.add(), .addHead(), .addTail(), .add, .remove(), .removeHead()) - TODO: add remove node method
         * create pointers (LinkedList.Node) - TODO: add multiple pointer detection - prob have to add a setName() to Pointer
         * iterate with pointers
         */
        // TODO: be sure to include checking to see if the pointer name exists across all lists
        String str = commandLine.getText();
        commandLine.setText("");
        focusLost(new FocusEvent(commandLine, 0, true));
        int last = 0;
        /* 0: searching for space
         * 1: found word
         * 2: searching for <>
         */
        boolean flag = false; //flag is used to alternate between detecting space(s) and detecting words
        for(int i = 0; i < str.length(); i++){//iterate through sentence(testcase)
            if(flag && str.charAt(i) != ' ') {last = i;flag = false;} //spaces
            if(!flag && (str.charAt(i) == ' ' || i + 1 == str.length())){ //words - if finds space or end of line - also not when it it looking to start the word(had a few weird bugs with it but fixed since i added the flag to the if)
                flag = true;
                if(i != last || str.length() == 1){
                    if(i + 1 == str.length()) i+=1;
                    String word = str.substring(last,i);
                    
                }
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        commandLine.setFocusable(true);
        commandLine.grabFocus();
        if(commandLine.getText().equals("Enter Command"))
            commandLine.setText("");
    }
    @Override
    public void focusLost(FocusEvent e) {
        commandLine.setFocusable(false);
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
