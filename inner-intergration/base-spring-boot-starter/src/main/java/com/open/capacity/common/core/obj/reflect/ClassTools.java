package com.open.capacity.common.core.obj.reflect;

import java.util.LinkedList;
import java.util.List;

public class ClassTools {
    public static List<String> getAllPackagesByClassName(String className){
        String packageName = getPackageNameByClassName(className);

        return getAllPackagesByPackageName(packageName);
    }

    public static List<String> getAllPackagesByPackageName(String packageName){
        String[] shortPackageNames = packageName.split("\\.");
        LinkedList<String> packageNames = new LinkedList<>();

        StringBuilder sb = new StringBuilder();
        for (int i=0;i<shortPackageNames.length;i++){
            if (i != 0){
                sb.append(".");
            }
            packageNames.addFirst(sb.append(shortPackageNames[i]).toString());
        }
        return packageNames;
    }

    public static String getPackageNameByClassName(String className){
        return className.substring(0,className.lastIndexOf(".") -1);
    }
}
