package com.labs.parser;

import com.labs.model.User;

import java.util.Collection;

public interface CollectionParser extends Parser {

    Collection<User> parseAll();
}
