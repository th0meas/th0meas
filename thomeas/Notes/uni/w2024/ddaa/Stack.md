[[DDAA]]
Διασύνδεση Στοίβας
```
public interface Stack {
	public int size(); //μέγεθος στοίβας
	public boolean isEmpty(); //true αμα η στοίβα είναι κενή
	public Object top() throws StackEmptyException;
	//επιστρέφει το στοιχείο που βρίσκεται στην κορυφή της στοίβας
	public void push(Object item) throws StackFullException;
	//εισάγει νέο στοιχείο στην κορυφή της στοίβας
	public Object pop() throws StackEmptyException;
	//εξάγει και επιστρέφει το στοιχείο που βρίσκεται στην κορυφή της στοίβας
}
```
Υλοποίηση της κλάσης **ArrayStack**
```

```public class ArrayStack implements Stack {
	public static final int CAPACITY = 1000;
	private Object S[];
	private int top;
	public ArrayStack( ) {
		this(CAPACITY);
	}
	public ArrayStack(int cap) {
		top = -1; //-1 σημαίνει άδειος πίνακας
		S = new Object[cap];
	}
	public int size( ) {
		return (top+1);
	}
	public boolean isEmpty( ) {
		return (top < 0);
	}
	public void push(Object item) {
		if (size( )== S.length)
			throw new StackFullException("Stack overflow");
		top++;
		S[top] = item;
	}
	public Object top( ) throws StackEmptyException {
		if (isEmpty( ))
			throw new StackEmptyException("Stack is empty");
		return S[top];
	}
	public Object pop() throws StackEmptyException {
		Object element;
		if (isEmpty())
			throw new StackEmptyException("Stack is empty");
		element = S[top];
		S[top] = null;
		top--;
		return element;
	}
}
```
