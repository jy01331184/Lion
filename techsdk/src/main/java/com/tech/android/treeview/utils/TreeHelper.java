/**
 * @Title: TreeHelper.java
 * @Package com.sloop.treeview.utils
 * @Description: TODO
 * Copyright: Copyright (c) 2015
 * 
 * @author sloop
 * @date 2015年2月21日 上午3:19:27
 * @version V1.0
 */

package com.tech.android.treeview.utils;

import android.util.Log;

import com.tech.android.treeview.bean.Node;

import java.util.ArrayList;
import java.util.List;



/**
 * 树形结构的帮助类 将元数据转换为节点
 * @ClassName: TreeHelper
 * @Description: 
 * @author sloop
 * @date 2015年2月21日 上午3:19:27
 *
 */

public class TreeHelper {

	/**
	 * 获取排序后的节点数据
	 * @Title: getSortedNodes
	 * @param datas
	 * @return List<Node>
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static List<Node> getSortedNodes(List<Node> datas, int defaultExpandLevel) throws IllegalAccessException, IllegalArgumentException {


		List<Node> result = new ArrayList<Node>();		//排序完成的节点
        //List<Node> nodes = convertDatas2Nodes(datas);	//转化后的所有节点


		List<Node> rootNodes = getRootNodes(datas);



		for (Node node : rootNodes) {
	//		Log.e("TAG", "根节点--"+node.getName());
			addNode(result, node, defaultExpandLevel, 1);
		}

		Log.e("TAG", "排序完成的节点个数"+result.size());

		return result;
	}
	
	/**
	 * 把一个节点的所有孩子节点都放入result(递归)
	 * @Title: addNode
	 * @param result					添加进哪个父节点
	 * @param node						需要添加进去的node
	 * @param defaultExpandLevel		默认展开层级
	 * @param currentLevel				当前层级
	 */
	private static void addNode(List<Node> result, Node node, int defaultExpandLevel, int currentLevel) {
		
		result.add(node);
		if (node.isLeaf()){							//如果是叶子节点说明该分支添加结束 返回
			return;
		}
		if (defaultExpandLevel >= currentLevel) {	//当前层级小于默认展开层级就展开当前
			node.setExpend(true);
		}

		for (int i = 0; i < node.getChildren().size(); i++) {
			addNode(result, node.getChildren().get(i), defaultExpandLevel, currentLevel+1);
		}
	}
	
	/**
	 * 过滤出需要显示的node集合
	 * @Title: fliterVisibleNodes
	 * @return List<Node>
	 */
	public static List<Node> fliterVisibleNodes(List<Node> nodes) {
		
		List<Node> result = new ArrayList<Node>();
		
		for (Node node : nodes) {
			if (node.isRoot() || node.isParentExpend()) {	//如果当前节点是根节点或者他的父节点处于展开状态就显示
				setNodeIcon(node);	//刷新图标
				result.add(node);
			}
		}

		return result;
	}

	/**
	 * 从所有节点中获取根节点集合
	 * @Title: getRootNodes
	 * @param nodes
	 * @return List<Node> 
	 */
	private static List<Node> getRootNodes(List<Node> nodes) {
		
		List<Node> root = new ArrayList<Node>();
		
		for (Node node : nodes) {
			if (node.isRoot()) {
				root.add(node);
			}
		}
		
		return root;
	}

	/**
	 * 给node设置图片
	 * @Title: setNodeIcon
	 * @param n void 
	 */
	private static void setNodeIcon(Node n) {
		if (n.getChildren().size()>0 && n.isExpend()) {			//有子节点并且是展开的

		}else if (n.getChildren().size()>0 && !n.isExpend()) {	//有子节点但是未展开

		}
	}
}
