package com.sokolov.labs.parser;

import com.sokolov.labs.model.User;

import java.util.Collection;

public interface CollectionParser extends Parser {

    Collection<User> parseAll();
}
