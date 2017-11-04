/*
 * The Spring Framework is published under the terms
 * of the Apache Software License.
 */
 
package com.interface21.beans.factory.support;

import com.interface21.beans.MutablePropertyValues;
import com.interface21.beans.PropertyValues;

/** 
* Root bean definitions have a class and properties.
* @author Rod Johnson
* @version $Id: RootBeanDefinition.java,v 1.8 2003/07/28 17:19:37 jhoeller Exp $
*/
public class RootBeanDefinition extends AbstractBeanDefinition {

	/** Class of the wrapped object */
	private Class clazz;
	
	private String initMethodName;

	public RootBeanDefinition(Class clazz, PropertyValues pvs, boolean singleton, String initMethodName) {
		super(pvs, singleton);
		this.clazz = clazz;
		this.initMethodName = initMethodName;
	}
	
	public RootBeanDefinition(Class clazz, PropertyValues pvs, boolean singleton) {
		this(clazz, pvs, singleton, null); 
	}
	
	/**
	 * Deep copy constructor.
	 */
	public RootBeanDefinition(RootBeanDefinition other) {
		super(new MutablePropertyValues(other.getPropertyValues()), other.isSingleton());
		this.clazz = other.clazz;
		this.initMethodName = other.initMethodName;
	}
	
	/**
	 * Returns the name of the initializer method. The default is null
	 * in which case there is no initializer method.
	 */
	public String getInitMethodName() {
		return this.initMethodName;
	}

	/**
	 * Returns the class of the wrapped bean.
	 */
	public final Class getBeanClass() {
		return this.clazz;
	}

	public boolean equals(Object arg0) {
		if (!(arg0 instanceof RootBeanDefinition))
			return false;
		return super.equals(arg0) && ((RootBeanDefinition) arg0).getBeanClass().equals(this.getBeanClass());
	}

	public String toString() {
		return "RootBeanDefinition for class '" + getBeanClass().getName() + "'";
	}

}
