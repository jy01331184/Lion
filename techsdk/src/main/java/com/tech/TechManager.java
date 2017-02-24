package com.tech;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by tianyang on 16/8/23.
 */
public abstract class TechManager {

    private static final String ANDROID_IMPL_CLASS_NAME = "com.tech.android.AsAndroidTechManager";

    public String threadName;
    protected Stack<StackObject> stacks = new Stack<StackObject>();
    public List<StackObject> results = Collections.synchronizedList(new ArrayList<StackObject>());

    static ThreadLocal<TechManager> threadLocal = new ThreadLocal<TechManager>(){
        @Override
        protected TechManager initialValue() {
            try
            {
                Class<?> cls = Class.forName(ANDROID_IMPL_CLASS_NAME);
                Constructor constructor = cls.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                return (TechManager) constructor.newInstance(new Object[]{});
            }
            catch (Exception e){
                e.printStackTrace();
            }
            throw new RuntimeException("NO Impl of TechManager");
        }
    };

    private static Set<TechManager> managers = Collections.synchronizedSet(new HashSet<TechManager>());

    public static TechManager getInstance()
    {
        TechManager techManager = threadLocal.get();
        managers.add(techManager);
        return techManager;
    }

    public abstract void init(Object context);

    public void handleMethodCutIn(AsJoinPoint joinPoint)
    {
        try {
            StackObject stackObject = new StackObject();

            stackObject.className = joinPoint.getClassName();

            stackObject.date = new Date();
            stackObject.methodName = joinPoint.getMethodName();

            StackObject top = stacks.isEmpty()?null:stacks.peek();
            if(top != null)
            {
                top.push(stackObject);
            }
            else
            {
                stacks.push(stackObject);
                results.add(stackObject);
            }
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    public void handleMethodCutOut()
    {
        try {
            StackObject top = stacks.isEmpty()?null:stacks.peek();
            if(top != null)
            {
                if(!top.pop()){
                    stacks.pop();
                }
            }
            else
            {
                stacks.pop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void handleConstructor(AsJoinPoint joinPoint) throws Throwable
    {
        StackObject top = stacks.isEmpty()?null:stacks.peek();
        if(top != null)
        {
            top.objCreate(joinPoint.getClassName());
        }
    }

    public static synchronized void showResult(final Order order)
    {
        for (TechManager manager : managers) {
            System.out.println("===="+manager.threadName+"====");
            Collections.sort(manager.results, new Comparator<StackObject>() {
                @Override
                public int compare(StackObject lhs, StackObject rhs) {
                    return lhs.date.compareTo(rhs.date) * (order.value);
                }
            });
            for (StackObject object  : manager.results) {
                System.out.println(object.fmtString(1));
            }
        }
    }

    public static synchronized void reset()
    {
        TechManager.getInstance().results.clear();
    }

    public static synchronized void resetAll()
    {
        for (TechManager manager : managers) {
            manager.results.clear();
        }
    }

    public static Set<TechManager> getResults(final Order order)
    {
        return managers;
    }


    public enum Order{
        ORDER_COUNT(1),ORDER_DATE(-1);

        Order(Integer value) {
            this.value = value;
        }

        Integer value;
    }
}
