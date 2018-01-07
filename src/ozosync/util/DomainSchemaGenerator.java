package ozosync.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import ozosync.Mock;

public class DomainSchemaGenerator {

    private static Package getRootPackage() {
        return Mock.class.getPackage();
    }

    /**
     * 最后应该有一个 "/" 号，比如："z:/com/company/project/"
     *
     * @return root package base src dir
     */
    private static String getRootPackageSrcDir() {
        return "c:/projects/ozosync/src/ozosync/";
    }

    private static String getDomainClassSuffix() {
        return "Info";
    }

    private static String getSchemaClassSuffix() {
        return "Schema";
    }

    public static void main(String[] args) {
        Package domainPackage = getRootPackage();

        List<Class<?>> domainClasses = getDomainClasses(domainPackage);

        String lineSeparator = System.getProperty("line.separator");

        for (Class domainClass : domainClasses) {

            String domainClassName = domainClass.getSimpleName();
            String schemaClassName = domainClassName.substring(0, domainClassName.indexOf(getDomainClassSuffix())) + getSchemaClassSuffix();

            String schemaFileDir = getRootPackageSrcDir();
            String schemaClassPackageName = domainClass.getPackage().getName();
            String domainPackageName = getRootPackage().getName();
            if (!schemaClassPackageName.equals(domainPackageName)) { //create schema class in sub directory?
                String subPackageName = schemaClassPackageName.substring(domainPackageName.length());
                subPackageName = subPackageName.substring(1); //strip the leading "."
                String subPackageDir = subPackageName.replace('.', '/');
                schemaFileDir = schemaFileDir + subPackageDir + "/" + "schema/";
            }

            String schemaFileFullName = schemaFileDir + schemaClassName + ".kt";

            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(schemaFileFullName);

                fileWriter.write("package " + domainClass.getPackage().getName() + ".schema");
                fileWriter.write(lineSeparator);
                fileWriter.write(lineSeparator);

                fileWriter.write("object " + schemaClassName + " {");
                fileWriter.write(lineSeparator);

                Method[] methods = domainClass.getDeclaredMethods();
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.startsWith("get")) {
                        String propertyName = methodName.substring("get".length());

                        fileWriter.write("    const val ");
                        propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

                        fileWriter.write(propertyName + " = \"" + propertyName + "\"");

                        fileWriter.write(lineSeparator);
                    }
                }

                fileWriter.write("}");
                fileWriter.write(lineSeparator);

                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Class<?>> getDomainClasses(Package pack) {
        List<Class<?>> domainClasses = new ArrayList<>();

        try {
            String packageName = pack.getName();
            String packageDirName = packageName.replace('.', '/');
            URL url = Thread.currentThread().getContextClassLoader().getResource(packageDirName);

            if (url == null) {
                return domainClasses;
            }

            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                findDomainClasses(packageName, filePath, domainClasses);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return domainClasses;
    }

    private static void findDomainClasses(String packageName, String packagePath, List<Class<?>> domainClasses) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        if (dirfiles == null) {
            return;
        }

        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findDomainClasses(packageName + "." + file.getName(), file.getAbsolutePath(), domainClasses);
            } else {
                String className = file.getName().substring(0, file.getName().length() - ".class".length());
                if (className.endsWith(getDomainClassSuffix())) {
                    try {
                        domainClasses.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
