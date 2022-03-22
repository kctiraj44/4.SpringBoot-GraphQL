package com.springboot.graphql.dao;

import com.springboot.graphql.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Integer> {
    Book findByName(String name);
}
