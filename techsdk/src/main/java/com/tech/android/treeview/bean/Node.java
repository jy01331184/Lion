/**
 * @Title: Node.java
 * @Package com.sloop.treeview.utils
 * @Description: Copyright: Copyright (c) 2015
 * @author sloop
 * @date 2015年2月21日 上午3:40:42
 * @version V1.0
 */

package com.tech.android.treeview.bean;

import com.tech.StackObject;
import com.tech.TechManager;
import com.tech.android.view.TechRootView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 树形结构的节点
 *
 * @ClassName: Node
 * @author sloop
 * @date 2015年2月21日 上午3:42:26
 */

public class Node implements Serializable{

    /**
     * 显示名称
     */
    private String name;

    /**
     * 是否展开
     */
    private boolean isExpend = false;

    /**
     * 父节点
     */
    private Node parent;
    /**
     * 子节点
     */
    private List<Node> children = new ArrayList<Node>();

    private int type;

    private String fullName;

    public Node(StackObject object, String name, String fullName, int type) {
        this.object = object;
        this.name = name;
        this.type = type;
        this.fullName = fullName;
    }

    public StackObject object;

    public List<StackObject> objects;

    public boolean lazyLoad;

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }


    /**
     * 得到当前节点的层级
     *
     * @Title: getLevel
     * @return int
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public boolean isExpend() {
        return isExpend;
    }

    /**
     * 改变展开状态
     *
     * @Title: setExpend
     * @param isExpend void
     */
    public void setExpend(boolean isExpend) {
        this.isExpend = isExpend;
        if (!isExpend) { // 收缩子节点
            for (Node node : children) {
                node.originExpend = node.isExpend;
                node.setExpend(false);
            }
        } else //打开原打开的所有节点
        {
            for (Node node : children) {
                node.setExpend(node.originExpend);
                node.originExpend = false;
            }
        }
    }

    boolean originExpend;

    public int getType() {
        return type;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }


    /**
     * 判断是否是根节点
     *
     * @Title: isRoot
     * @return boolean
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断父节点是否处于展开状态
     *
     * @Title: isParentExpend
     * @return boolean
     */
    public boolean isParentExpend() {
        if (parent == null)
            return false;
        return parent.isExpend();
    }

    /**
     * 判断是否是叶子节点
     *
     * @Title: isLeaf
     * @return boolean
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    public void loop() {
        if (objects != null) {
            Collections.sort(objects, new Comparator<StackObject>() {
                @Override
                public int compare(StackObject lhs, StackObject rhs) {
                    if (TechRootView.order == TechManager.Order.ORDER_DATE) {
                        return lhs.date.compareTo(rhs.date);
                    } else {
                        return -lhs.getCreateObjectCount() + rhs.getCreateObjectCount();
                    }
                }
            });

            for (StackObject object : objects) {
                loop(object);
            }
        }
    }

    public boolean isPlusOrMins = true;//plus true   mins false

    public void loop(StackObject object) {
        if (object == null || !object.hasContent())
            return;

        Node bean = new Node(object, object.toNodeString(), object.toDetailString(), TreeNodeBean.TYPE_CONTENT);
        getChildren().add(bean);
        bean.setParent(this);
        bean.objects = object.getResult();
    }
}
