[[DDAA]]
Διασύνδεση Ουράς
```
public interface Queue {
	public int size();
	public boolean isEmpty();
	public Object front() throws QueueEmptyException;
	public void enqueue(Object item) throws QueueFullException;
	public Object dequeue() throws QueueEmptyException
}
```
Υλοποίηση κάσης ArrayQueue
```
public class ArrayQueue implements Queue {
	public static final int CAPACITY = 1000;
	private Object Q[];
	private int first;
	private int last;
	
	public ArrayQueue(){
		this(CAPACITY);
	}
	public int size(){
	return(last-first);
	}
	public boolean isEmpty(){
		return(first==last);
	}
	public void enqueue(Object item) throws QueueFullException {
		if(last == Q.length)
			throw new QueueFullException("Queue Overflow");
		Q[last] = item;
		last++;
	}
	public Object front() throws QueueEmptyException {
		if(isEmpty())
			throw new QueueEmptyException("Queue is empty");
		return Q[first];
	}
	public Object dequeue() throws QueueEmptyException {
		Object item;
		if (isEmpty())
			throw new QueueEmptyException("Queue is empty");
		item = Q[first];
		Q[first] = null; //!!!
		first++
		return item;
	}
}
```
