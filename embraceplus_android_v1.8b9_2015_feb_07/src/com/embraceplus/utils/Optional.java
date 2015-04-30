package com.embraceplus.utils;

/**
 * Concept stolen from Java 8, it is a convenience method to warn that this object of type T can be null
 * @author Hugo
 *
 * @param <T>
 */
public class Optional<T> {
	
	/**
	 * The instance contained in this Optional, this can be null
	 */
	protected T mObject;

	/**
	 * Creates a new Optionnal, Warning, it's content is null !
	 */
	public Optional() {

	}
	
	/**
	 * Creates a new Optionnal with an object to initialise it
	 * @param object
	 */
	public Optional(T object){
		mObject = object;
	}
	
	/**
	 * Don't try to get this object before checking carefully if it is null or not !
	 * @return a non-null object
	 */
	public T get(){
		if(isEmpty()) throw new NullPointerException();
		return mObject;
	}
	
	/**
	 * Sets this Optional, the new value can be null
	 * @param object
	 */
	public void set(T object){
		mObject = object;
	}
	
	/**
	 * Sets this Optional, the new value can be null
	 * @param object
	 */
	public void set(Optional<T> object){
		if(object!=null && object.notEmpty()) {
			mObject = object.get();
		} else {
			mObject = null;
		}
	}
	
	/**
	 * @return true if this Optional is currently empty
	 */
	public boolean isEmpty(){
		return mObject == null;
	}
	
	/**
	 * @return true if this Optional is currently not empty
	 */
	public boolean notEmpty(){
		return !isEmpty();
	}
	
}
