/**
 * 
 */
package org.graphql.maven.plugin.samples.server;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.generated.Droid;
import org.graphql.maven.plugin.samples.server.generated.Human;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.io.Resources;

import graphql.GraphQL;
import graphql.TypeResolutionEnvironment;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

/**
 * Based on the https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/ tutorial
 * 
 * @author EtienneSF
 */
@Component
public class GraphQLProvider {

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Autowired
	GraphQLDataFetchers graphQLDataFetchers;

	private GraphQL graphQL;

	@Bean
	public GraphQL graphQL() {
		return graphQL;
	}

	@PostConstruct
	public void init() throws IOException {
		URL url = Resources.getResource("starWarsSchema.graphqls");
		String sdl = Resources.toString(url, Charsets.UTF_8);
		GraphQLSchema graphQLSchema = buildSchema(sdl);
		this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
	}

	private GraphQLSchema buildSchema(String sdl) {
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);

		// Add of the CharacterImpl type definition
		typeRegistry.add(getCharacterImplType(typeRegistry));

		RuntimeWiring runtimeWiring = buildWiring();
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
	}

	private RuntimeWiring buildWiring() {
		// Thanks to this thread :
		// https:// stackoverflow.com/questions/54251935/graphql-no-resolver-definied-for-interface-union-java
		//
		// Also see sample :
		// https://github.com/graphql-java/graphql-java-examples/tree/master/http-example
		return RuntimeWiring.newRuntimeWiring()
				// The Data Fetchers for queries must be defined. They'll trigger the JPA access to the database
				.type(newTypeWiring("QueryType").dataFetcher("hero", graphQLDataFetchers.hero()))
				.type(newTypeWiring("QueryType").dataFetcher("characters", graphQLDataFetchers.characters()))
				.type(newTypeWiring("QueryType").dataFetcher("human", graphQLDataFetchers.human()))
				.type(newTypeWiring("QueryType").dataFetcher("droid", graphQLDataFetchers.droid()))
				//
				// Defining the Data Fetchers for the fields are useless: these field will be populated by JPA
				// .type(newTypeWiring("Character").dataFetcher("friends", graphQLDataFetchers.friends()))
				// .type(newTypeWiring("Droid").dataFetcher("friends", graphQLDataFetchers.friends()))
				// .type(newTypeWiring("Human").dataFetcher("friends", graphQLDataFetchers.friends()))
				//
				// We still need to link the interface types to the concrete types
				.type("Character", typeWriting -> typeWriting.typeResolver(getCharacterResolver()))
				//
				.build();
	}

	private ObjectTypeDefinition getCharacterImplType(TypeDefinitionRegistry typeRegistry) {
		ObjectTypeDefinition humanDef = (ObjectTypeDefinition) typeRegistry.getType("Human").get();
		ObjectTypeDefinition.Builder characterImplDef = ObjectTypeDefinition.newObjectTypeDefinition();
		characterImplDef.name("CharacterImpl");
		for (FieldDefinition fieldDef : humanDef.getFieldDefinitions()) {
			// Let's add only the fields coming from the Character interface
			switch (fieldDef.getName()) {
			case "id":
			case "name":
			case "friends":
			case "appearsIn":
				characterImplDef.fieldDefinition(fieldDef);
			}
		}
		for (Type<?> typeName : humanDef.getImplements()) {
			// Let's add only the fields coming from the Character interface
			switch (((TypeName) typeName).getName()) {
			case "Character":
				characterImplDef.implementz(typeName);
			}

		}
		return characterImplDef.build();
	}

	private TypeResolver getCharacterResolver() {
		return new TypeResolver() {
			@Override
			public GraphQLObjectType getType(TypeResolutionEnvironment env) {
				Object javaObject = env.getObject();
				String ret = null;

				if (javaObject instanceof Human) {
					ret = "Human";
				} else if (javaObject instanceof Droid) {
					ret = "Droid";
				} else if (javaObject instanceof CharacterImpl) {
					ret = "CharacterImpl";
				} else {
					throw new RuntimeException("Can't resolve javaObject " + javaObject.getClass().getName());
				}
				logger.trace("Resolved type for javaObject {} is {}", javaObject.getClass().getName());
				return env.getSchema().getObjectType(ret);
			}
		};
	}
}