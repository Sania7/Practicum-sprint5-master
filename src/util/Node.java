package util;

// узел
public class Node<N> {

    private N item;
    private Node<N> next;
    private Node<N> prev;

    public Node(Node<N> prev, N element, Node<N> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }

    public N getItem() {
        return item;
    }

    public void setItem(N item) {
        this.item = item;
    }

    public Node<N> getNext() {
        return next;
    }

    public void setNext(Node<N> next) {
        this.next = next;
    }

    public Node<N> getPrev() {
        return prev;
    }

    public void setPrev(Node<N> prev) {
        this.prev = prev;
    }
}
