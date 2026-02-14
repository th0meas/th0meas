[[DDAA]]
```
public interface Queue {
	public int size( );
	// Returns the size of the queue
	public boolean isEmpty( );
	// Returns true if the queue is empty
	public boolean isFull();
	// Returns true if the queue is full
	public Object front( ) throws QueueEmptyException;
	// Returns the first element of the queue
	public void add(Object item) throws QueueFullException;
	// Adds an element at the end of the queue
	public Object remove( ) throws QueueEmptyException;
	// Removes the first element of the queue
	public void printQueue();
	// prints all elements
}
```

```
public class LinkedQueue implements Queue {
	private LinkedList l;
	public LinkedQueue(){
		l = new LinkedList();
	}
	public int size() {
		return l.size();
	}
	public boolean isEmpty() {
		return l.isEmpty();
	}
	public boolean isFull() {
		return false;
	}
	public Object front() throws QueueEmptyException {
		if(isEmpty()){
			throw new QueueEmptyException("Queue is Empty.");
		}
		Object frontObject = l.removeLast();
		l.insertLast(frontObject);
		return frontObject;
	}
	public void add(Object item) throws QueueFullException {
		l.insertLast(item);
	}
	public Object remove() throws QueueEmptyException {
		if(isEmpty()){
			throw new QueueEmptyException("Queue is Empty.");
		}
		Object ob = l.removeFirst();
		return ob;
	}
	public void printQueue(){
		l.printList();
	}
}	
```