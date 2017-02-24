package com.tech;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by tianyang on 16/8/23.
 */
public class StackObject implements Serializable{

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:SS");
    private static final SimpleDateFormat FORMAT_SIMPLE = new SimpleDateFormat("hh:mm:SS");


    public String className;   //当次方法调用对象 的class
    public String methodName;  //当次方法调用的方法名
    public Date date;        //当次方法调用的时间

    private Stack<StackObject> invokeStack = new Stack<>();  //临时方法调用栈
    private Map<String,Integer> objectCreateRecord = new HashMap<>(); //创建的对象 key:className value:count

    private List<StackObject> result = new ArrayList<>();   //方法从方法栈 出栈后

    public void push(StackObject stackObject)
    {
        StackObject top = invokeStack.isEmpty()?null: invokeStack.peek();

        if(top == null)
        {
            invokeStack.push(stackObject);
        }
        else
        {
            top.push(stackObject);
        }
    }

    public boolean pop()
    {
        if(!invokeStack.isEmpty())
        {
            StackObject top = invokeStack.peek();

            if(top.invokeStack.isEmpty())
            {
                result.add(invokeStack.pop());
            }
            else
            {
                top.pop();
            }
            return true;
        }
        return false;
    }

    public void objCreate(String className)
    {
        StackObject top = invokeStack.isEmpty()?null: invokeStack.peek();

        if(top == null)
        {
            if(objectCreateRecord.containsKey(className))
            {
                objectCreateRecord.put(className, objectCreateRecord.get(className)+1);
            }
            else
            {
                objectCreateRecord.put(className,1);
            }
        }
        else
        {
            top.objCreate(className);
        }
    }

    @Override
    public String toString() {
        return  className+"-"+methodName+"\t\t\t"+getObjectCreateRecord() +"\t\t\t"+date.getTime();
    }

    private int childCreateObjectCountCache = -1;

    public int getCreateObjectCount(){
        if(childCreateObjectCountCache >= 0)
            return childCreateObjectCountCache;


        if(result.isEmpty()){
            childCreateObjectCountCache = getSelfCreateCount();
        }else{
            childCreateObjectCountCache = getSelfCreateCount();
            for (StackObject res : result) {
                childCreateObjectCountCache += res.getCreateObjectCount();
            }
        }
        return childCreateObjectCountCache;

    }

    public String toNodeString(){

        String tempClassName = className;

        if(tempClassName.length()>20 ){
            int firstIndex = tempClassName.indexOf(".");
            if(firstIndex <0)
                firstIndex = 5;
            int lastIndex = tempClassName.lastIndexOf(".")+1;
            if(lastIndex <0 )
                lastIndex = tempClassName.length()-20;


            tempClassName = tempClassName.substring(0,firstIndex).concat("...").concat(tempClassName.substring(lastIndex));
        }

        return  tempClassName+"-"+methodName+"\t\t\t[total:"+(getCreateObjectCount()) +" self:"+getSelfCreateCount()+"]\t\t\t"+FORMAT_SIMPLE.format(date);
    }

    public String toDetailString(){
        return  className+"-"+methodName+"\t\t\t"+getObjectCreateRecord() +"\t\t\t"+FORMAT.format(date);
    }

    public int getSelfCreateCount(){
        int selfTotalCount = 0;

        Collection<Integer> vals = getObjectCreateRecord().values();
        for (Integer i : vals) {
            selfTotalCount += i;
        }
        return selfTotalCount;
    }

    /**
     * 格式化对象输出
     * @param space
     * @return
     */
    public String fmtString(int space)
    {
        if(!hasContent())
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(toString());
        String spaceStr = "";
        for (int i = 0; i < space; i++) {
            spaceStr += "\t";
        }



        for (StackObject o : result) {
            String str = o.fmtString(space+1);
            if(str != null && str.length()>0)
                stringBuilder.append("\n"+spaceStr+str);
        }

        return stringBuilder.toString();
    }

    public List<StackObject> getResult() {
        return result;
    }

    public Map<String, Integer> getObjectCreateRecord() {
        return objectCreateRecord;
    }

    public List<Map.Entry<String,Integer>> getSortedDetail(){
        List<Map.Entry<String,Integer>> list = new ArrayList<>(objectCreateRecord.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                return -(lhs.getValue() - rhs.getValue());
            }
        });
        return list;
    }

    private int hasContentCache; //0 init 1 yes -1 no

    public boolean hasContent(){
        if(hasContentCache == 1)
            return true;
        if(hasContentCache == -1)
            return false;

        if(!objectCreateRecord.isEmpty() )
            return true;
        for (StackObject child : result) {
            if(child.hasContent()){
                hasContentCache = 1;
                return true;
            }
        }
        hasContentCache = -1;
        return false;
    }

}
