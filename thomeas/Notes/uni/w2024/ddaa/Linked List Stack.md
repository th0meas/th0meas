[[DDAA]]
```
public interface Stack {
	public int size();
	public boolean isEmpty();
	public boolean isFull();
	public Object top() throws StackEmptyException;
	public void push(Object item) throws StackFullException;
	public Object pop() throws StackEmptyException;
}
```

```
public class LinkedStack implements Stack {
	private LinkedList l;
	LinkedStack() {
		l = new LinkedList();
	}
	public int size() {
		return l.size();
	}
	public boolean isEmpty() {
		return l.isEmpty();
	}
	public bollean isFull() {
		return l.isFull();	
	}
	public Object top() throws StackEmptyException {
		if (isEmpty())
			throws new StackEmptyException("Stack is Empty.");
		Object topObject = l.removeFirst();
		l.insertFirst(topObject);
		return topObject;
	}
	public void push(Object item) throws StackFullException {
		l.insertFirst(item);
	}
	public Object pop() throws StackEmptyException {
		if(isEmpty()) {
			throw new StackEmptyException("Stack is Empty.");
		}
		Object ob = l.removeFirst();
		return ob;
	}
```