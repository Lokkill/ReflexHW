package our.company;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //Class math = MathClass.class;
        String math = "our.company.MathClass";
        try {
            start(math);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }


    static void start(Object name) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Class newClass;
        if (name instanceof String){
            newClass = Class.forName((String) name);
        } else {
            newClass = (Class) name;
        }
        MathClass mc = (MathClass) newClass.newInstance();

        Method[] methods = newClass.getDeclaredMethods();
        Map<Integer, ArrayList<Method>> map = checkAnnotations(methods);

        map.entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<Method>>comparingByKey().reversed()).forEach(x -> {
            ArrayList<Method> list = x.getValue();
            for (Method method : list){
                try {
                    System.out.println("Имя метода: " + method.getName() + ", приоритет: " + x.getKey() + ", результат: " + method.invoke(mc, 10, 5));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static Map<Integer, ArrayList<Method>> checkAnnotations(Method[] methods) {
        boolean isAfterSuit = false;
        boolean isBeforeSuit = false;
        Map<Integer, ArrayList<Method>> map = new HashMap<>();
        for (Method m : methods){
            Annotation[] arrAnnotations = m.getDeclaredAnnotations();
            boolean localAfter = false;
            boolean localBefore = false;
            for (Annotation an : arrAnnotations){
                if (an instanceof AfterSuite){
                    if (isAfterSuit) {
                        showError();
                    } else {
                        isAfterSuit = true;
                        localAfter = true;
                        addInMap(map,  0, m);
                    }
                }
                if (an instanceof BeforeSuite){
                    if (isBeforeSuit){
                        showError();
                    } else {
                        isBeforeSuit = true;
                        localBefore = true;
                        addInMap(map,  10, m);
                    }
                }
                if (localAfter && localBefore){
                    throw new RuntimeException("Один метод является и начальным и конечным");
                }

                if ((!localAfter && !localBefore) && an instanceof Test){
                    Test t = m.getAnnotation(Test.class);
                    addInMap(map, t.priority(), m);
                }
            }
        }
        return map;
    }

    static void addInMap(Map<Integer, ArrayList<Method>> map, int priority, Method method){
        ArrayList<Method> list = map.get(priority);
        if (list == null){
            ArrayList<Method> newList = new ArrayList<>();
            newList.add(method);
            map.put(priority, newList);
        } else {
            list.add(method);
            map.put(priority, list);
        }

    }

    public static void showError(){
        throw new RuntimeException("Некорректное количество методов с аннотацией AfterSuite и BeforeSuite");
    }
}
