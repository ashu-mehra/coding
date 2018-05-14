import java.util.HashMap;
import java.util.Iterator;

public class LRUCache<K, V> implements Iterable<LRUCache.Node<K, V>> {
	static class Node<K, V> {
		K key;
		V value;
		Node<K, V> prev;
		Node<K, V> next;

		Node(K k, V v) {
			key = k;
			value = v;
			prev = null;
			next = null;
		}

		K getKey() {
			return key;
		}

		V getValue() {
			return value;
		}

		void setNext(Node<K, V> node) {
			next = node;
		}

		void setPrev(Node<K, V> node) {
			prev = node;
		}
	}

	HashMap<K, Node<K, V>> map;
	Node<K, V> head;
	Node<K, V> tail;
	int size;
	int currentSize;

	LRUCache(int size) {
		map = new HashMap<>();
		head = tail = null;
		this.size = size;
		currentSize = 0;
	}

	Node<K, V> getHead() {
		return head;
	}

	public V put(K k, V v) {
		Node<K, V> node = new Node<>(k, v);
		Node<K, V> nodeToRemove = null;
		V retVal = null;

		if (map.containsKey(k)) {
			nodeToRemove = map.get(k);
			retVal = nodeToRemove.getValue();
		} else if (currentSize == size) {
			nodeToRemove = tail;
		}
		if (nodeToRemove != null) {
			removeFromList(nodeToRemove);
		}
		addInFront(node);
		map.put(k, node);
		return retVal;
	}

	public V get(K k) {
		Node<K, V> node = map.get(k);
		if (node != null) {
			removeFromList(node);
			addInFront(node);
			return node.getValue();
		} else {
			return null;
		}
	}

	public void addInFront(Node<K, V> node) {
		if (head == null) {
			head = node;
			tail = node;
		} else {
			node.next = head;
			head.prev = node;
			head = node;
			node.prev = null;
		}
		currentSize += 1;
	}

	public void removeFromList(Node<K, V> node) {
		if (node.prev != null) {
			node.prev.next = node.next;
		}
		if (node.next != null) {
			node.next.prev = node.prev;
		}
	       	if (head == node) {	
			head = node.next;
		}
		currentSize -= 1;
		node.prev = null;
		node.next = null;
	}

	public void print() {
		System.out.println("\nCache contents:");
		for (Node<K, V> current: this) {
			System.out.println("(" + current.getKey() + ", " + current.getValue() + ")");
		}
		System.out.println();
	}

	public Iterator<Node<K, V>> iterator() {
		return new LRUCacheIterator<K, V>(this);
	}

	class LRUCacheIterator<K, V> implements Iterator<Node<K, V>> {
		LRUCache<K, V> cache;
		Node<K, V> currentNode;

		LRUCacheIterator(LRUCache<K, V> cache) {
			this.cache = cache;
			currentNode = cache.getHead();
		}

		public boolean hasNext() {
			if (currentNode == null) {
				return false;
			} else {
				return true;
			}
		}
		
		public Node<K, V> next() {
			if (hasNext()) {
				Node<K, V> node = currentNode;
				currentNode = currentNode.next;
				return node;
			}
			return null;
		}
	}

	public static void main(String args[]) {
		LRUCache<String, Integer> cache = new LRUCache<>(5);

		/* Add five elements to cache */
		System.out.println("Adding five elements");
		cache.put("Mumbai", 16368000);
		cache.put("Kolkata", 13217000);
		cache.put("Delhi", 12791000);
		cache.put("Chennai", 6425000);
		cache.put("Bangalore", 5687000);

		cache.print();

		/* Access an element */
		System.out.println("Population of Delhi is " + cache.get("Delhi"));

		cache.print();

		System.out.println("Population of Kolkata is " + cache.get("Kolkata"));

		cache.print();

		/* Update existing element */
		System.out.println("Updating population of Chennai");
		cache.put("Chennai", 7255000);

		cache.print();

		/* Add another element */
		System.out.println("Adding population of Hyderabad");
		cache.put("Hyderabad", 4565000);

		cache.print();
	}
}
