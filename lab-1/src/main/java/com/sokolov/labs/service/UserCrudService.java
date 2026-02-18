package com.sokolov.labs.service;

import com.sokolov.labs.model.User;
import com.sokolov.labs.parser.impl.PriorityQueueCsvParser;

import java.io.*;
import java.util.*;

public class UserCrudService {

    private final String filePath;
    private List<User> users;

    public UserCrudService(String resourceFileName) {
        this.filePath = getSourceResourcesPath(resourceFileName);
        loadAll();
    }

    public void create(User user) {
        if (user == null) {
            System.out.println("Ошибка: пользователь null");
            return;
        }

        if (findById(user.getId()) != null) {
            System.out.println("Ошибка: пользователь с ID " + user.getId() + " уже существует");
            return;
        }

        users.add(user);
        saveAll();
        System.out.println("Создан новый персонаж: " + user.getName());
    }

    public User findById(Integer id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public void update(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                saveAll();
                System.out.println("Обновлен персонаж: " + updatedUser.getName());
                return;
            }
        }
        System.out.println("Персонаж с ID " + updatedUser.getId() + " не найден");
    }

    public void delete(Integer id) {
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            saveAll();
            System.out.println("Удален персонаж с ID: " + id);
        } else {
            System.out.println("Персонаж с ID " + id + " не найден");
        }
    }

    private void loadAll() {
        List<User> loadedUsers = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            this.users = new ArrayList<>();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // пропускаем заголовок
            while ((line = br.readLine()) != null) {
                User user = parseLine(line);
                if (user != null) {
                    loadedUsers.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        this.users = loadedUsers;
        System.out.println("Загружено персонажей: " + users.size());
    }

    private User parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = parseCsvLine(line);
        if (parts.length < 9) {
            return null;
        }

        try {
            return new User(
                    Integer.parseInt(parts[0].trim()),
                    unescapeCsv(parts[1]),
                    unescapeCsv(parts[2]),
                    unescapeCsv(parts[3]),
                    unescapeCsv(parts[4]),
                    unescapeCsv(parts[5]),
                    unescapeCsv(parts[6]),
                    unescapeCsv(parts[7]),
                    unescapeCsv(parts[8])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
    }

    private String unescapeCsv(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            return trimmed.substring(1, trimmed.length() - 1).replace("\"\"", "\"");
        }
        return trimmed;
    }

    private void saveAll() {
        users.sort(Comparator.comparing(User::getId));

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,name,status,species,type,gender,origin/name,location/name,created");
            for (User user : users) {
                writer.println(formatUserForCsv(user));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    private String formatUserForCsv(User user) {
        return String.join(",",
                String.valueOf(user.getId()),
                escapeCsv(user.getName()),
                escapeCsv(user.getStatus()),
                escapeCsv(user.getSpecies()),
                escapeCsv(user.getType()),
                escapeCsv(user.getGender()),
                escapeCsv(user.getOrigin()),
                escapeCsv(user.getLocation()),
                escapeCsv(user.getCreated())
        );
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
        System.out.println("Использую путь по умолчанию: " + defaultResources.getAbsolutePath());
        return new File(defaultResources, fileName).getAbsolutePath();
    }

    private File findResourcesDirectory(File startDir) {
        File resourcesDir = new File(startDir, "src/main/resources");
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            return resourcesDir;
        }

        File parentDir = startDir.getParentFile();
        if (parentDir != null) {
            return findResourcesDirectory(parentDir);
        }

        return null;
    }
}