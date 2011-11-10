package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

public class ObservableArrayList<T> extends Observable implements List<T>{

	
	
	// Methods of list interface
	private ArrayList<T> data = new ArrayList<T>();
	@Override
	public boolean add(T e) {
		this.setChanged();
		notifyObservers();
		return data.add(e);
		//return false;
	}

	@Override
	public void add(int index, T element) {
		data.add(index, element);
		
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return data.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return data.addAll(index, c);
	}

	@Override
	public void clear() {
		data.clear();
		
	}

	@Override
	public boolean contains(Object o) {

		return data.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return data.containsAll(c);
		
	}

	@Override
	public T get(int index) {
		return data.get(index);
		
	}

	@Override
	public int indexOf(Object o) {
		return data.indexOf(o);
		
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
		
	}

	@Override
	public Iterator<T> iterator() {
		
		return data.iterator();
		
	}

	@Override
	public int lastIndexOf(Object o) {
		
		return data.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		
		return data.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		
		return data.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return data.remove(o);
		
	}

	@Override
	public T remove(int index) {
		return data.remove(index);
		
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		
		return data.removeAll(c);
		
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		
		return data.retainAll(c);
	}

	@Override
	public T set(int index, T element) {
		
		return data.set(index, element);
	}

	@Override
	public int size() {

		return data.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		
		return data.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		
		return data.toArray(a);
	}
	

}
