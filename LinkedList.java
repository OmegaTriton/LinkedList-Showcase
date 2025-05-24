public class LinkedList<T> {
    public static class Node<T>{
        public T data;
        public Node<T> next;
        public Node(T data, Node<T> next){
            this.data = data;
            this.next = next;
        }
        public Node(T data){
            this.data = data;
            this.next = null;
        }
    }
    protected Node<T> head;
    protected Node<T> tail;
    
    public LinkedList(){
        head = null;
        tail = null;
    }
    
    public void addHead(T data){
        head = new Node<T>(data, head);
        if(tail == null) tail = head;
    }
    public void addTail(T data){
        tail.next = new Node<T>(data);
        tail = tail.next;
        if(head == null) head = tail;
    }
    public void add(Node<T> before, T data){
        if(before == null){
            head = new Node<T>(data);
            tail = head;
            return;
        }
        before.next = new Node<T>(data, before.next);
    }
    public boolean remove(T data){
        if(head == null) return false;
        if(head.data == data) {head = head.next; return true;}
        Node<T> it = head;
        while(it != null && it.next != null && !it.next.data.equals(data)) it = it.next; //find()
        if(it == null) return false;
        it.next = it.next.next;
        return true;
    }

    public boolean removeHead(){
        if(head == tail) {
            head = null;
            tail = null;
            return true;
        }
        head = head.next;
        return true;
    }
    
    public void printList(){
        if(head == null) return;
        Node<T> it = head;
        while(it != null){
            System.out.println(it.data);
            it = it.next;
        }
    }
    public Node<T> find(T data){
        Node<T> it = head;
        while(it != null && !it.data.equals(data)) it = it.next;
        return it;
    }

    public Node<T> getFirst() {
        return head;
    }

    public Node<T> getLast() {
        return tail;
    }

    public boolean isEmpty() {
        return head == null;
    }
}