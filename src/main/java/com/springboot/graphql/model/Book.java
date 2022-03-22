package com.springboot.graphql.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Book {

    @Id
    private int id;
    private String name;
    private String author;

}
