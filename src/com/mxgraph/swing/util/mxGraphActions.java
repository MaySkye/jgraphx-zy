/**
 * Copyright (c) 2008, Gaudenz Alder
 */
package com.mxgraph.swing.util;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.frame.ResourceShowFrame;
import com.mxgraph.examples.swing.util.ww.WWFiberManager;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import static com.mxgraph.examples.swing.editor.EditorActions.getEditor;

/**
 *
 */
public class mxGraphActions
{

	/**
	 * 
	 */
	static final Action deleteAction = new DeleteAction("delete");

	/**
	 * 
	 */
	static final Action editAction = new EditAction("edit");

	/**
	 * 
	 */
	static final Action groupAction = new GroupAction("group");

	/**
	 * 
	 */
	static final Action ungroupAction = new UngroupAction("ungroup");

	/**
	 * 
	 */
	static final Action removeFromParentAction = new RemoveFromParentAction(
			"removeFromParent");

	/**
	 * 
	 */
	static final Action updateGroupBoundsAction = new UpdateGroupBoundsAction(
			"updateGroupBounds");

	/**
	 * 
	 */
	static final Action selectAllAction = new SelectAction("selectAll");

	/**
	 * 
	 */
	static final Action selectVerticesAction = new SelectAction("vertices");

	/**
	 * 
	 */
	static final Action selectEdgesAction = new SelectAction("edges");

	/**
	 * 
	 */
	static final Action selectNoneAction = new SelectAction("selectNone");

	/**
	 *
	 */
	static final Action selectNextAction = new SelectAction("selectNext");

	/**
	 * 
	 */
	static final Action selectPreviousAction = new SelectAction(
			"selectPrevious");

	/**
	 * 
	 */
	static final Action selectParentAction = new SelectAction("selectParent");

	/**
	 * 
	 */
	static final Action selectChildAction = new SelectAction("selectChild");

	/**
	 * 
	 */
	static final Action collapseAction = new FoldAction("collapse");

	/**
	 * 
	 */
	static final Action expandAction = new FoldAction("expand");

	/**
	 * 
	 */
	static final Action enterGroupAction = new DrillAction("enterGroup");

	/**
	 * 
	 */
	static final Action exitGroupAction = new DrillAction("exitGroup");

	/**
	 * 
	 */
	static final Action homeAction = new DrillAction("home");

	/**
	 * 
	 */
	static final Action zoomActualAction = new ZoomAction("actual");

	/**
	 * 
	 */
	static final Action zoomInAction = new ZoomAction("zoomIn");

	/**
	 * 
	 */
	static final Action zoomOutAction = new ZoomAction("zoomOut");

	/**
	 * 
	 */
	static final Action toBackAction = new LayerAction("toBack");

	/**
	 * 
	 */
	static final Action toFrontAction = new LayerAction("toFront");

	static final Action mxCellInfoAction = new mxCellInfoAction("mxCellInfo");
	static final Action setSensitivePointAction = new setSensitivePointAction("setSensitivePoint");
	static final Action bindDataFieldAction = new bindDataFieldAction("bindDataField");
	/**
	 * 
	 * @return the delete action
	 */
	public static Action getDeleteAction()
	{
		return deleteAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getEditAction()
	{
		return editAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getGroupAction()
	{
		return groupAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getUngroupAction()
	{
		return ungroupAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getRemoveFromParentAction()
	{
		return removeFromParentAction;
	}

	/**
	 * 
	 * @return the edit action
	 */
	public static Action getUpdateGroupBoundsAction()
	{
		return updateGroupBoundsAction;
	}

	/**
	 * 
	 * @return the select all action
	 */
	public static Action getSelectAllAction()
	{
		return selectAllAction;
	}

	/**
	 * 
	 * @return the select vertices action
	 */
	public static Action getSelectVerticesAction()
	{
		return selectVerticesAction;
	}

	/**
	 * 
	 * @return the select edges action
	 */
	public static Action getSelectEdgesAction()
	{
		return selectEdgesAction;
	}

	/**
	 * 
	 * @return the select none action
	 */
	public static Action getSelectNoneAction()
	{
		return selectNoneAction;
	}

	/**
	 * 
	 * @return the select next action
	 */
	public static Action getSelectNextAction()
	{
		return selectNextAction;
	}

	/**
	 * 
	 * @return the select previous action
	 */
	public static Action getSelectPreviousAction()
	{
		return selectPreviousAction;
	}

	/**
	 * 
	 * @return the select parent action
	 */
	public static Action getSelectParentAction()
	{
		return selectParentAction;
	}

	/**
	 * 
	 * @return the select child action
	 */
	public static Action getSelectChildAction()
	{
		return selectChildAction;
	}

	/**
	 * 
	 * @return the go into action
	 */
	public static Action getEnterGroupAction()
	{
		return enterGroupAction;
	}

	/**
	 * 
	 * @return the go up action
	 */
	public static Action getExitGroupAction()
	{
		return exitGroupAction;
	}

	/**
	 * 
	 * @return the home action
	 */
	public static Action getHomeAction()
	{
		return homeAction;
	}

	/**
	 * 
	 * @return the collapse action
	 */
	public static Action getCollapseAction()
	{
		return collapseAction;
	}

	/**
	 * 
	 * @return the expand action
	 */
	public static Action getExpandAction()
	{
		return expandAction;
	}

	/**
	 * 
	 * @return the zoom actual action
	 */
	public static Action getZoomActualAction()
	{
		return zoomActualAction;
	}

	/**
	 * 
	 * @return the zoom in action
	 */
	public static Action getZoomInAction()
	{
		return zoomInAction;
	}

	/**
	 * 
	 * @return the zoom out action
	 */
	public static Action getZoomOutAction()
	{
		return zoomOutAction;
	}

	/**
	 * 
	 * @return the action that moves cell(s) to the backmost layer
	 */
	public static Action getToBackAction()
	{
		return toBackAction;
	}

	/**
	 * 
	 * @return the action that moves cell(s) to the frontmost layer
	 */
	public static Action getToFrontAction()
	{
		return toFrontAction;
	}

	//图元信息
	public static Action getMxCellInfoAction()
	{
		return mxCellInfoAction;
	}
	//建立敏感点
	public static Action getSetSensitivePointAction()
	{
		return setSensitivePointAction;
	}
	//绑定数据域
	public static Action getBindDataFieldAction()
	{
		return bindDataFieldAction;
	}

	/**
	 * 
	 * @param e
	 * @return Returns the graph for the given action event.
	 */
	public static final mxGraph getGraph(ActionEvent e)
	{
		Object source = e.getSource();

		if (source instanceof mxGraphComponent)
		{
			return ((mxGraphComponent) source).getGraph();
		}

		return null;
	}

	/**
	 * 
	 */
	public static class EditAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4610112721356742702L;

		/**
		 * 
		 * @param name
		 */
		public EditAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof mxGraphComponent)
			{
				((mxGraphComponent) e.getSource()).startEditing();
			}
		}

	}

	/**
	 * 
	 */
	public static class DeleteAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -8212339796803275529L;

		/**
		 * 
		 * @param name
		 */
		public DeleteAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.removeCells();
			}
			WWFiberManager.doHandleForGraph(graph);
		}
	}

	/**
	 * 
	 */
	public static class GroupAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4718086600089409092L;

		/**
		 * 
		 * @param name
		 */
		public GroupAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		protected int getGroupBorder(mxGraph graph)
		{
			return 2 * graph.getGridSize();

		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.setSelectionCell(graph.groupCells(null,
						getGroupBorder(graph)));
			}
		}

	}

	/**
	 * 
	 */
	public static class UngroupAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2247770767961318251L;

		/**
		 * 
		 * @param name
		 */
		public UngroupAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.setSelectionCells(graph.ungroupCells());
			}
		}

	}

	/**
	 * 
	 */
	public static class RemoveFromParentAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7169443038859140811L;

		/**
		 * 
		 * @param name
		 */
		public RemoveFromParentAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.removeCellsFromParent();
			}
		}

	}

	/**
	 * 
	 */
	public static class UpdateGroupBoundsAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4718086600089409092L;

		/**
		 * 
		 * @param name
		 */
		public UpdateGroupBoundsAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		protected int getGroupBorder(mxGraph graph)
		{
			return 2 * graph.getGridSize();
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				graph.updateGroupBounds(null, getGroupBorder(graph));
			}
		}

	}

	/**
	 * 
	 */
	public static class LayerAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 562519299806253741L;

		/**
		 * 
		 * @param name
		 */
		public LayerAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				boolean toBack = getValue(Action.NAME).toString()
						.equalsIgnoreCase("toBack");
				graph.orderCells(toBack);
			}
		}

	}

	/**
	 * 
	 */
	public static class FoldAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4078517503905239901L;

		/**
		 * 
		 * @param name
		 */
		public FoldAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				boolean collapse = getValue(Action.NAME).toString()
						.equalsIgnoreCase("collapse");
				graph.foldCells(collapse);
			}
		}

	}

	/**
	 * 
	 */
	public static class DrillAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5464382323663870291L;

		/**
		 * 
		 * @param name
		 */
		public DrillAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				String name = getValue(Action.NAME).toString();

				if (name.equalsIgnoreCase("enterGroup"))
				{
					graph.enterGroup();
				}
				else if (name.equalsIgnoreCase("exitGroup"))
				{
					graph.exitGroup();
				}
				else
				{
					graph.home();
				}
			}
		}

	}

	/**
	 * 
	 */
	public static class ZoomAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7500195051313272384L;

		/**
		 * 
		 * @param name
		 */
		public ZoomAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			Object source = e.getSource();

			if (source instanceof mxGraphComponent)
			{
				String name = getValue(Action.NAME).toString();
				mxGraphComponent graphComponent = (mxGraphComponent) source;

				if (name.equalsIgnoreCase("zoomIn"))
				{
					graphComponent.zoomIn();
				}
				else if (name.equalsIgnoreCase("zoomOut"))
				{
					graphComponent.zoomOut();
				}
				else
				{
					graphComponent.zoomActual();
				}
			}
		}

	}

	/**
	 * 
	 */
	public static class SelectAction extends AbstractAction
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6501585024845668187L;

		/**
		 * 
		 * @param name
		 */
		public SelectAction(String name)
		{
			super(name);
		}

		/**
		 * 
		 */
		public void actionPerformed(ActionEvent e)
		{
			mxGraph graph = getGraph(e);

			if (graph != null)
			{
				String name = getValue(Action.NAME).toString();

				if (name.equalsIgnoreCase("selectAll"))
				{
					graph.selectAll();
				}
				else if (name.equalsIgnoreCase("selectNone"))
				{
					graph.clearSelection();
				}
				else if (name.equalsIgnoreCase("selectNext"))
				{
					graph.selectNextCell();
				}
				else if (name.equalsIgnoreCase("selectPrevious"))
				{
					graph.selectPreviousCell();
				}
				else if (name.equalsIgnoreCase("selectParent"))
				{
					graph.selectParentCell();
				}
				else if (name.equalsIgnoreCase("vertices"))
				{
					graph.selectVertices();
				}
				else if (name.equalsIgnoreCase("edges"))
				{
					graph.selectEdges();
				}
				else
				{
					graph.selectChildCell();
				}
			}
		}

	}

	public static class mxCellInfoAction extends AbstractAction
	{

		private static final long serialVersionUID = 6501585024845668188L;
		public mxCellInfoAction(String name)
		{
			super(name);
		}
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			mxGraph graph = getGraph(e);

			Object obj = graph.getSelectionCell();
			if (!(obj instanceof mxCell)) {
				return;
			}
			mxCell cell = (mxCell) obj;//得到选中的图元对象

			if(cell.getV()!=null){
				//输出数据属性信息
				Map<String,String> data_info=cell.getV().getData_info();
				Map<String,String> link_info=cell.getV().getLink_info();

				Iterator<Map.Entry<String, String>> data1 = data_info.entrySet().iterator();
				while (data1.hasNext()) {
					Map.Entry<String, String> entry = data1.next();
					String val=entry.getValue();
					System.out.println(entry.getKey()+":"+val);
				}

				Iterator<Map.Entry<String, String>> link1 = link_info.entrySet().iterator();
				while (link1.hasNext()) {
					Map.Entry<String, String> entry = link1.next();
					String val=entry.getValue();
					System.out.println(entry.getKey()+":"+val);
				}
			}

			new ResourceShowFrame(editor,graph,cell);

		}

	}

	public static class setSensitivePointAction extends AbstractAction
	{

		private static final long serialVersionUID = 6501585024845668188L;

		public setSensitivePointAction(String name)
		{
			super(name);

		}
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			mxGraph graph = getGraph(e);

			Object obj = graph.getSelectionCell();
			if (!(obj instanceof mxCell)) {
				return;
			}
			mxCell cell = (mxCell) obj;//得到选中的图元对象

            //先获取mxe文件信息

		}

	}

	public static class bindDataFieldAction extends AbstractAction
	{

		private static final long serialVersionUID = 6501585024845668188L;

		public bindDataFieldAction(String name)
		{
			super(name);
		}
		public void actionPerformed(ActionEvent e)
		{
			BasicGraphEditor editor = getEditor(e);
			mxGraph graph = getGraph(e);

			Object obj = graph.getSelectionCell();
			if (!(obj instanceof mxCell)) {
				return;
			}
			mxCell cell = (mxCell) obj;//得到选中的图元对象

			if(cell.getV()!=null){
				//输出数据属性信息
				Map<String,String> data_info=cell.getV().getData_info();
				Map<String,String> link_info=cell.getV().getLink_info();

				Iterator<Map.Entry<String, String>> data1 = data_info.entrySet().iterator();
				while (data1.hasNext()) {
					Map.Entry<String, String> entry = data1.next();
					String val=entry.getValue();
					System.out.println(entry.getKey()+":"+val);
				}

				Iterator<Map.Entry<String, String>> link1 = link_info.entrySet().iterator();
				while (link1.hasNext()) {
					Map.Entry<String, String> entry = link1.next();
					String val=entry.getValue();
					System.out.println(entry.getKey()+":"+val);
				}
			}
			new ResourceShowFrame(editor,graph,cell);

		}

	}

}
