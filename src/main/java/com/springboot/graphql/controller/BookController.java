package com.springboot.graphql.controller;

import com.springboot.graphql.dao.BookRepository;
import com.springboot.graphql.model.Book;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookRepository repository;

    @Value("classpath:book.graphqls")
    private Resource schemaResource;

    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        File file = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(file);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema= new SchemaGenerator().makeExecutableSchema(registry,wiring);
        graphQL = GraphQL.newGraphQL(schema).build();

    }


    public RuntimeWiring buildWiring(){
        DataFetcher<List<Book>> fetcher1 = data->{
            return repository.findAll();
        };

        DataFetcher<Book> fetcher2= data->{
            return repository.findByName(data.getArgument("name"));
        };

        return RuntimeWiring.newRuntimeWiring().type("Query",typeWriting->
                typeWriting.dataFetcher("getAllBooks",fetcher1).dataFetcher("getBook",fetcher2)).build();

    }

    @PostMapping("/addPerson")
    public String add(@RequestBody List<Book> books){
        repository.save(books);
        return "Sved : "+ books.size();
    }

    @PostMapping("/getAll")
    public ResponseEntity<Object> getBooks(@RequestBody String  query){
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
