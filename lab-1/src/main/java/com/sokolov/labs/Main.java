package com.sokolov.labs;

import com.sokolov.labs.model.User;
import com.sokolov.labs.writer.UserWriter;
import com.sokolov.labs.service.UserCrudService;
import com.sokolov.labs.parser.impl.*;

import java.util.PriorityQueue;

/*
PriorityQueue — топ-3 по длине имени
Считать персонажей в PriorityQueue, где приоритет = длина имени.
Извлечь 3 самых длинных имени и записать их в файл.
*/


public class Main {

    public static void main(String[] args) {
        //Основное задание
        PriorityQueueCsvParser priorityQueueCsvParser = new PriorityQueueCsvParser("characters.csv");
        PriorityQueue<User> characters = priorityQueueCsvParser.parseAll();

        UserWriter writer = new UserWriter();
        String outputName = "result.csv";

        while (characters.size() > 3) {
            characters.poll();
        }
        for (User user : characters) {
            writer.writeUserToCsv(user, outputName);
        }
        //Доп. задание
        UserCrudService crud = new UserCrudService("characters.csv");
        crud.findAll().stream().limit(5).forEach(u -> System.out.println("   " + u.getId() + ": " + u.getName()));
        User user = crud.findById(1);
        if (user != null) {
            System.out.println("Найден: " + user.getName());
        }

        User newUser = new User(100, "George Floyd", "Alive", "Human", "Test Type", "Male", "Earth", "Earth", "1973-10-14");
        crud.create(newUser);

        newUser.setStatus("Dead");
        User newUser1 = new User(101, "George Droid", "Alive", "Cyborg", "Test Type", "Male", "Earth", "Earth", "2047-10-14");
        crud.create(newUser1);
        crud.update(newUser);

        crud.delete(100);
    }
}