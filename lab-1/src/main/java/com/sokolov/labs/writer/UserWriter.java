package com.sokolov.labs.writer;

import com.sokolov.labs.model.User;

import java.io.*;

public class UserWriter {

    public void writeUserToCsv(User user, String outputFileName) {
        if (user == null) {
            System.out.println("Пользователь null");
            return;
        }

        String actualPath = getSourceResourcesPath(outputFileName);
        File file = new File(actualPath);
        boolean needHeader = !file.exists() || isFileEmpty(actualPath);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            if (needHeader) {
                writer.println("id,name,status,species,type,gender,origin/name,location/name,created");
            }
            writer.println(formatUserForCsv(user));
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл " + outputFileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatUserForCsv(User user) {
        return String.join(",", String.valueOf(user.getId()), escapeCsv(user.getName()), escapeCsv(user.getStatus()), escapeCsv(user.getSpecies()), escapeCsv(user.getType()), escapeCsv(user.getGender()), escapeCsv(user.getOrigin()), escapeCsv(user.getLocation()), escapeCsv(user.getCreated()));
    }

    private String escapeCsv(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.contains("\"") || trimmed.contains(",") || trimmed.contains("\n")) {
            return "\"" + trimmed.replace("\"", "\"\"") + "\"";
        }
        return trimmed;
    }

    public boolean isFileEmpty(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return true;
        }
        return file.length() == 0;
    }


    private String getSourceResourcesPath(String fileName) {
        // Получаем корень проекта (где находится .idea или pom.xml)
        String userDir = System.getProperty("user.dir");
        File projectRoot = new File(userDir);

        File potentialModuleDir = new File(projectRoot, "lab-1");
        if (potentialModuleDir.exists() && potentialModuleDir.isDirectory()) {
            File resourcesDir = new File(potentialModuleDir, "src/main/resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                resourcesDir.mkdirs();
                return new File(resourcesDir, fileName).getAbsolutePath();
            }
        }

        File srcDir = new File(projectRoot, "src");
        if (srcDir.exists() && srcDir.isDirectory()) {
            File resourcesDir = new File(projectRoot, "src/main/resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                resourcesDir.mkdirs();
                return new File(resourcesDir, fileName).getAbsolutePath();
            }
        }

        File parentDir = projectRoot.getParentFile();
        if (parentDir != null) {
            File moduleDir = new File(parentDir, "lab-1");
            if (moduleDir.exists() && moduleDir.isDirectory()) {
                File resourcesDir = new File(moduleDir, "src/main/resources");
                if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                    resourcesDir.mkdirs();
                    return new File(resourcesDir, fileName).getAbsolutePath();
                }
            }
        }

        File defaultResources = new File(projectRoot, "src/main/resources");
        defaultResources.mkdirs();
        return new File(defaultResources, fileName).getAbsolutePath();
    }
}