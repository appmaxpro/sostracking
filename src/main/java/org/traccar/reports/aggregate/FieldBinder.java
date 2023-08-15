package org.traccar.reports.aggregate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class FieldBinder {
	
	private String attributeName;
	private Expression<?> expression;	
	private FieldTypeBinder fieldType;
	private GroupByBinder groupBy;	
	
	private Field attribute;
	
	public FieldBinder(String attributeName, Expression<?> expression, FieldTypeBinder fieldType) {
		this.attributeName = attributeName;
		this.expression = expression;
		this.fieldType = fieldType; 
	}
	
	public FieldBinder(String attributeName, GroupByBinder collectionGroupBy, FieldTypeBinder fieldType) {
		this.attributeName = attributeName;
		this.groupBy = collectionGroupBy;
		this.fieldType = fieldType; 
	}

	public Object getTupleValue(Tuple tuple) {
		return tuple.get(expression);
	}
	
	public Class<?> getGroupByTypeClass() throws ClassNotFoundException {
		if (isCollection())
			return Class.forName(((ParameterizedType) getAttribute().getGenericType()).getActualTypeArguments()[0].getTypeName());
		else
			return getAttribute().getType();
	}

	public void setAttribute(Field field) {
		this.attribute = field;
	}
	
	public boolean isGroupBy() {
		return isCollection() || isSingle();
	}
	
	public boolean isCollection() {
		return fieldType.equals(FieldTypeBinder.COLLECTION);
	}
	
	public boolean isSingle() {
		return fieldType.equals(FieldTypeBinder.SINGLE);
	}

	public boolean isKey() {
		return fieldType.equals(FieldTypeBinder.KEY);
	}

	public String getAttributeName(){
		return attributeName;
	}

	public Expression<?> getExpression() {
		return expression;
	}

	public FieldTypeBinder getFieldType() {
		return fieldType;
	}

	public Field getAttribute() {
		return attribute;
	}

	public GroupByBinder getGroupBy() {
		return groupBy;
	}

}
