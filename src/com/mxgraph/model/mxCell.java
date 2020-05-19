/**
 * Copyright (c) 2007, Gaudenz Alder
 */
package com.mxgraph.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.examples.swing.graph.EdgeLink;
import com.mxgraph.examples.swing.graph.VertexInterface;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Cells are the elements of the graph model. They represent the state
 * of the groups, vertices and edges in a graph.
 *
 * <h4>Edge Labels</h4>
 * 
 * Using the x- and y-coordinates of a cell's geometry it is
 * possible to position the label on edges on a specific location
 * on the actual edge shape as it appears on the screen. The
 * x-coordinate of an edge's geometry is used to describe the
 * distance from the center of the edge from -1 to 1 with 0
 * being the center of the edge and the default value. The
 * y-coordinate of an edge's geometry is used to describe
 * the absolute, orthogonal distance in pixels from that
 * point. In addition, the mxGeometry.offset is used
 * as a absolute offset vector from the resulting point.
 * 
 * The width and height of an edge geometry are ignored.
 * 
 * To add more than one edge label, add a child vertex with
 * a relative geometry. The x- and y-coordinates of that
 * geometry will have the same semantiv as the above for
 * edge labels.
 */
public class mxCell implements mxICell, Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 910211337632342672L;


	protected VertexInterface<String> v;

	public VertexInterface<String> getV() {
		return v;
	}

	public void setV(VertexInterface<String> v) {
		this.v = v;
	}

	protected String vertextInfo;

	public String getVertextInfo() {
		return vertextInfo;
	}

	public void setVertextInfo(String vertextInfo) {
		this.vertextInfo = vertextInfo;
	}

	protected EdgeLink edgeLink;

	public EdgeLink getEdgeLink() {
		return edgeLink;
	}

	public void setEdgeLink(EdgeLink edgeLink) {
		this.edgeLink = edgeLink;
	}

	protected String edgeLinkInfo;

	public String getEdgeLinkInfo() {
		return edgeLinkInfo;
	}

	public void setEdgeLinkInfo(String edgeLinkInfo) {
		this.edgeLinkInfo = edgeLinkInfo;
	}

	protected String name;

	protected String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected String deviceid;

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	//存储的has_property和connect信息
	protected String hasproperty;
	protected String hasconnect;

	public String getHasproperty() {
		return hasproperty;
	}

	public void setHasproperty(String hasproperty) {
		this.hasproperty = hasproperty;
	}

	public String getHasconnect() {
		return hasconnect;
	}

	public void setHasconnect(String hasconnect) {
		this.hasconnect = hasconnect;
	}

	//如果一个图元的类型为Port，则用到以下字段，存储的是port连接点的信息，否则为空
	protected String attr;
	protected String location;
	protected String direction;

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	//描述监测图元的，设备图元以下字段为空
	protected String monitor_device_name;
	protected String monitor_property_name;
	protected String monitor_property_type;
	protected String monitor_property_kind;//属于开关量、模拟量、累计量和设定量
	protected String monitor_unit;

	public String getMonitor_device_name() {
		return monitor_device_name;
	}

	public void setMonitor_device_name(String monitor_device_name) {
		this.monitor_device_name = monitor_device_name;
	}

	public String getMonitor_property_name() {
		return monitor_property_name;
	}

	public void setMonitor_property_name(String monitor_property_name) {
		this.monitor_property_name = monitor_property_name;
	}

	public String getMonitor_property_type() {
		return monitor_property_type;
	}

	public void setMonitor_property_type(String monitor_property_type) {
		this.monitor_property_type = monitor_property_type;
	}

	public String getMonitor_unit() {
		return monitor_unit;
	}

	public void setMonitor_unit(String monitor_unit) {
		this.monitor_unit = monitor_unit;
	}

	public String getMonitor_property_kind() {
		return monitor_property_kind;
	}

	public void setMonitor_property_kind(String monitor_property_kind) {
		this.monitor_property_kind = monitor_property_kind;
	}

	//原始资源文件，初始情况下两个字段内容相同
	//如果为组态图重新绑定资源文件，则bindResourceFile值改变
	protected String originalResourceFile;

	public String getOriginalResourceFile() {
		return originalResourceFile;
	}

	public void setOriginalResourceFile(String originalResourceFile) {
		this.originalResourceFile = originalResourceFile;
	}

	//绑定资源文件
	protected String bindResourceFile;

	public String getBindResourceFile() {
		return bindResourceFile;
	}

	public void setBindResourceFile(String bindResourceFile) {
		this.bindResourceFile = bindResourceFile;
	}

	//设备图元敏感点，存储跳转组态图的文件名称
	protected String sensitivePoint;

	public String getSensitivePoint() {
		return sensitivePoint;
	}

	public void setSensitivePoint(String sensitivePoint) {
		this.sensitivePoint = sensitivePoint;
	}

	//设备绑定的数据域，默认为 monitor_device_name + monitor_property_name
    protected  String bindDataField;

	public String getBindDataField() {
		return bindDataField;
	}

	public void setBindDataField(String bindDataField) {
		this.bindDataField = bindDataField;
	}

	/**
	 * Holds the Id. Default is null.
	 */
	protected String id;

	/**
	 * Holds the user object. Default is null.
	 */
	protected Object value;

	/**
	 * Holds the geometry. Default is null.
	 */
	protected mxGeometry geometry;

	/**
	 * Holds the style as a string of the form
	 * stylename[;key=value]. Default is null.
	 */
	protected String style;

	/**
	 * Specifies whether the cell is a vertex or edge and whether it is
	 * connectable, visible and collapsed. Default values are false, false,
	 * true, true and false respectively.
	 */
	protected boolean vertex = false, edge = false, connectable = true,
			visible = true, collapsed = false;

	/**
	 * Reference to the parent cell and source and target terminals for edges.
	 */
	protected mxICell parent, source, target;

	/**
	 * Holds the child cells and connected edges.
	 */
	protected List<Object> children, edges;

	/**
	 * Constructs a new cell with an empty user object.
	 */
	public mxCell()
	{

		this(null);
	}

	/**
	 * Constructs a new cell for the given user object.
	 * 
	 * @param value
	 *   Object that represents the value of the cell.
	 */
	public mxCell(Object value)
	{
		this(value, null, null);
	}

	/**
	 * Constructs a new cell for the given parameters.
	 * 
	 * @param value Object that represents the value of the cell.
	 * @param geometry Specifies the geometry of the cell.
	 * @param style Specifies the style as a formatted string.
	 */
	public mxCell(Object value, mxGeometry geometry, String style)
	{
		setValue(value);
		setGeometry(geometry);
		setStyle(style);
		setV(null);
	}

	/**
	 * Constructs a new cell for the given parameters.
	 *
	 * @param cellName String that represents the name of the cell.
	 * @param value    Object that represents the value of the cell.
	 * @param geometry Specifies the geometry of the cell.
	 * @param style    Specifies the style as a formatted string.
	 */
	public mxCell(String cellName, Object value, mxGeometry geometry, String style) {
		setName(cellName);
		setValue(value);
		setGeometry(geometry);
		setStyle(style);
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setId(String)
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getValue()
	 */
	public Object getValue()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setValue(Object)
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getGeometry()
	 */
	public mxGeometry getGeometry()
	{
		return geometry;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setGeometry(com.mxgraph.model.mxGeometry)
	 */
	public void setGeometry(mxGeometry geometry)
	{
		this.geometry = geometry;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getStyle()
	 */
	public String getStyle()
	{
		return style;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setStyle(String)
	 */
	public void setStyle(String style)
	{
		this.style = style;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#isVertex()
	 */
	public boolean isVertex()
	{
		return vertex;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setVertex(boolean)
	 */
	public void setVertex(boolean vertex)
	{
		this.vertex = vertex;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#isEdge()
	 */
	public boolean isEdge()
	{
		return edge;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setEdge(boolean)
	 */
	public void setEdge(boolean edge)
	{
		this.edge = edge;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#isConnectable()
	 */
	public boolean isConnectable()
	{
		return connectable;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setConnectable(boolean)
	 */
	public void setConnectable(boolean connectable)
	{
		this.connectable = connectable;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#isVisible()
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setVisible(boolean)
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#isCollapsed()
	 */
	public boolean isCollapsed()
	{
		return collapsed;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setCollapsed(boolean)
	 */
	public void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getParent()
	 */
	public mxICell getParent()
	{
		return parent;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setParent(com.mxgraph.model.mxICell)
	 */
	public void setParent(mxICell parent)
	{
		this.parent = parent;
	}

	/**
	 * Returns the source terminal.
	 */
	public mxICell getSource()
	{
		return source;
	}

	/**
	 * Sets the source terminal.
	 * 
	 * @param source Cell that represents the new source terminal.
	 */
	public void setSource(mxICell source)
	{
		this.source = source;
	}

	/**
	 * Returns the target terminal.
	 */
	public mxICell getTarget()
	{
		return target;
	}

	/**
	 * Sets the target terminal.
	 * 
	 * @param target Cell that represents the new target terminal.
	 */
	public void setTarget(mxICell target)
	{
		this.target = target;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getTerminal(boolean)
	 */
	public mxICell getTerminal(boolean source)
	{
		return (source) ? getSource() : getTarget();
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setTerminal(com.mxgraph.model.mxICell, boolean)
	 */
	public mxICell setTerminal(mxICell terminal, boolean isSource)
	{
		if (isSource)
		{
			setSource(terminal);
		}
		else
		{
			setTarget(terminal);
		}

		return terminal;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getChildCount()
	 */
	public int getChildCount()
	{
		return (children != null) ? children.size() : 0;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getIndex(com.mxgraph.model.mxICell)
	 */
	public int getIndex(mxICell child)
	{
		return (children != null) ? children.indexOf(child) : -1;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getChildAt(int)
	 */
	public mxICell getChildAt(int index)
	{
		return (children != null) ? (mxICell) children.get(index) : null;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#insert(com.mxgraph.model.mxICell)
	 */
	public mxICell insert(mxICell child)
	{
		int index = getChildCount();
		
		if (child.getParent() == this)
		{
			index--;
		}
		
		return insert(child, index);
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#insert(com.mxgraph.model.mxICell, int)
	 */
	public mxICell insert(mxICell child, int index)
	{
		if (child != null)
		{
			child.removeFromParent();
			child.setParent(this);

			if (children == null)
			{
				children = new ArrayList<Object>();
				children.add(child);
			}
			else
			{
				children.add(index, child);
			}
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#remove(int)
	 */
	public mxICell remove(int index)
	{
		mxICell child = null;

		if (children != null && index >= 0)
		{
			child = getChildAt(index);
			remove(child);
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#remove(com.mxgraph.model.mxICell)
	 */
	public mxICell remove(mxICell child)
	{
		if (child != null && children != null)
		{
			children.remove(child);
			child.setParent(null);
		}

		return child;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#removeFromParent()
	 */
	public void removeFromParent()
	{
		if (parent != null)
		{
			parent.remove(this);
		}
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getEdgeCount()
	 */
	public int getEdgeCount()
	{
		return (edges != null) ? edges.size() : 0;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getEdgeIndex(com.mxgraph.model.mxICell)
	 */
	public int getEdgeIndex(mxICell edge)
	{
		return (edges != null) ? edges.indexOf(edge) : -1;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getEdgeAt(int)
	 */
	public mxICell getEdgeAt(int index)
	{
		return (edges != null) ? (mxICell) edges.get(index) : null;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#insertEdge(com.mxgraph.model.mxICell, boolean)
	 */
	public mxICell insertEdge(mxICell edge, boolean isOutgoing)
	{
		if (edge != null)
		{
			edge.removeFromTerminal(isOutgoing);
			edge.setTerminal(this, isOutgoing);

			if (edges == null || edge.getTerminal(!isOutgoing) != this
					|| !edges.contains(edge))
			{
				if (edges == null)
				{
					edges = new ArrayList<Object>();
				}

				edges.add(edge);
			}
		}

		return edge;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#removeEdge(com.mxgraph.model.mxICell, boolean)
	 */
	public mxICell removeEdge(mxICell edge, boolean isOutgoing)
	{
		if (edge != null)
		{
			if (edge.getTerminal(!isOutgoing) != this && edges != null)
			{
				edges.remove(edge);
			}
			
			edge.setTerminal(null, isOutgoing);
		}

		return edge;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#removeFromTerminal(boolean)
	 */
	public void removeFromTerminal(boolean isSource)
	{
		mxICell terminal = getTerminal(isSource);

		if (terminal != null)
		{
			terminal.removeEdge(this, isSource);
		}
	}

	/**
	 * Returns the specified attribute from the user object if it is an XML
	 * node.
	 * 
	 * @param name Name of the attribute whose value should be returned.
	 * @return Returns the value of the given attribute or null.
	 */
	public String getAttribute(String name)
	{
		return getAttribute(name, null);
	}

	/**
	 * Returns the specified attribute from the user object if it is an XML
	 * node.
	 * 
	 * @param name Name of the attribute whose value should be returned.
	 * @param defaultValue Default value to use if the attribute has no value.
	 * @return Returns the value of the given attribute or defaultValue.
	 */
	public String getAttribute(String name, String defaultValue)
	{
		Object userObject = getValue();
		String val = null;

		if (userObject instanceof Element)
		{
			Element element = (Element) userObject;
			val = element.getAttribute(name);
		}

		if (val == null)
		{
			val = defaultValue;
		}

		return val;
	}

	/**
	 * Sets the specified attribute on the user object if it is an XML node.
	 * 
	 * @param name Name of the attribute whose value should be set.
	 * @param value New value of the attribute.
	 */
	public void setAttribute(String name, String value)
	{
		Object userObject = getValue();

		if (userObject instanceof Element)
		{
			Element element = (Element) userObject;
			element.setAttribute(name, value);
		}
	}

	/**
	 * Returns a clone of the cell.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		mxCell clone = (mxCell) super.clone();

		clone.setValue(cloneValue());
		clone.setStyle(getStyle());
		clone.setCollapsed(isCollapsed());
		clone.setConnectable(isConnectable());
		clone.setEdge(isEdge());
		clone.setVertex(isVertex());
		clone.setVisible(isVisible());
		clone.setParent(null);
		clone.setSource(null);
		clone.setTarget(null);
		clone.children = null;
		clone.edges = null;

		mxGeometry geometry = getGeometry();

		if (geometry != null)
		{
			clone.setGeometry((mxGeometry) geometry.clone());
		}

		return clone;
	}

	/**
	 * Returns a clone of the user object. This implementation clones any XML
	 * nodes or otherwise returns the same user object instance.
	 */
	protected Object cloneValue()
	{
		Object value = getValue();

		if (value instanceof Node)
		{
			value = ((Node) value).cloneNode(true);
		}

		return value;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(64);
		builder.append(getClass().getSimpleName());
		builder.append(" [");
		builder.append("id=");
		builder.append(id);
		builder.append(", value=");
		builder.append(value);
		builder.append(", geometry=");
		builder.append(geometry);
		builder.append("]");
		
		return builder.toString();
	}


	private String multiValue;
	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getMultiValue()
	 */
	public String getMultiValue() {
		return multiValue;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setMultiValue(String)
	 */
	public void setMultiValue(String multiValue) {
		this.multiValue = multiValue;
	}



	protected String originStyle;

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#getOriginStyle()
	 */
	public String getOriginStyle() {
		return originStyle;
	}

	/* (non-Javadoc)
	 * @see com.mxgraph.model.mxICell#setOriginStyle(String)
	 */
	public void setOriginStyle(String originStyle) {
		this.originStyle = originStyle;
	}

}
