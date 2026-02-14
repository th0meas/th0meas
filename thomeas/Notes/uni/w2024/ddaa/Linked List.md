[[DDAA]]
```
public interface List {
	public boolean isEmpty();
	public int size;
	public void insertFirst(Object data);
	public void insertLast(Object data);
	public Object removeFirst() throws ListEmptyException;
	public Object removeLast() throws ListEmptyException;
	public void printList() throws ListEmptyException;
	public SLListNode getFirst() throws ListEmptyException;
}
```

```
public class SLListNode {
	private Object data;
	private SLListNode nextNode;
	
	public Node() {
		data = null;
		nextNode = null;
	}
	public SLListNode(Object data, SLListNode nextNode) {
		this.data = datal
		this.nextNode = nextNode;
	}
	
	public Object getNodeData(){ return data; }
	public SLListNode getNextNode( ){ return nextNode; }
	public void setNodeData(Object d){ this.data = d; }
	public void setNextNode(SLListNode n){ this.nextNode = n; }
	public String toString(){ return data.toString(); }
}
	
public class ListEmptyException extends RuntimeException{
	public ListEmptyException(String err){
		super(err);
	}
}

public class SimpleLinkedList implements List {
	private SLListNode first;
	private SLListNode last;
	public SimpleLinkedList(){
		first = null;
		last = null;
	}
	
	public boolean isEmpty() {
		return first == null; }
	
	public SLListNode getFirst() throws ListEmptyException{
		if(isEmpty()){
		throw new ListEmptyException("List is Empty.");
		}
		return first;
	}
	
	public int size() {
		int size = 0;
		for(SLListNode position = first; position != null;
		position = position.getNextNode()){ size++; }
		return size;
	}
	public void insert(Object data, pos)
		B = new Node(data, null);
		B.next = pos.next;
		B.setNext(pos.getNext);
		pos.setNext(B);
}
	public void insertFirst(Object data) {
		if(isEmpty()){
			first = new SLListNode(data, null);
			last = first;
		}
		else{
			SLListNode temp = new SLListNode(data, first);
			first = temp;
		}
	}
	
	public void insertLast(Object data) {
		if(isEmpty()){
			first = new SLListNode(data, null);
			last = first;
		}
		else{
			SLListNode temp = new SLListNode(data, null);
			last.setNextNode(temp);
			last = temp;
		}
	}
	
	public Object removeFirst() throws ListEmptyException {
		if(isEmpty()){
			throw new ListEmptyException("List is Empty.");
		}
		Object removedItem = first.getNodeData();
		if(first == last){
			first = null;
			last = null;
		}
		else{
			first = first.getNextNode();
		}
		return removedItem;
	}
	
	public Object removeLast() throws ListEmptyException {
		if(isEmpty()){
			throw new ListEmptyException("List is Empty.");
		}
		Object removedItem = last.getNodeData();
		if(first == last){
			first = null;
			last = null;
		}
		else{
			SLListNode position = first;
			while (position.getNextNode() != last){
			position = position.getNextNode();
			}
			last = position;
			position.setNextNode(null);
		}
		return removedItem;
	}
	
	public void printList() throws ListEmptyException{
		if(isEmpty()){
			throw new ListEmptyException("List is Empty.");
		}
		SLListNode position = first;
		while (position != null){
			System.out.println(position.getNodeData());
			position = position.getNextNode();
		}
	}
}
```