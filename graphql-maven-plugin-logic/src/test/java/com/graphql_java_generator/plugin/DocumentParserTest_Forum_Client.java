package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Client_SpringConfiguration.class })
class DocumentParserTest_Forum_Client {

	@Autowired
	DocumentParser documentParser;

	@SuppressWarnings("unused")
	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

	@Test
	@DirtiesContext
	void test_initRelations() {

		// Verification
		assertEquals(19, documentParser.relations.size(), "nb relations found");

		// The Relation are found in the order of their declaratioon in the GraphQL schema definition.
		// Let's check that
		int i = 0;
		checkRelation(documentParser.relations.get(i++), "Board", "topics", "Topic", null, RelationType.OneToMany,
				true);
		checkRelation(documentParser.relations.get(i++), "Topic", "author", "Member", null, RelationType.ManyToOne,
				true);
		checkRelation(documentParser.relations.get(i++), "Topic", "posts", "Post", null, RelationType.OneToMany, true);
		checkRelation(documentParser.relations.get(i++), "Post", "author", "Member", null, RelationType.ManyToOne,
				true);
	}

	private void checkRelation(Relation relation, String objectSource, String fieldSource, String typeTarget,
			String targetField, RelationType manytoone, boolean b) {
		String msg = "Relation: " + objectSource + "." + fieldSource + "->" + typeTarget + " (" + manytoone + ")";
		assertNotNull(relation, msg + " [relation not null");
		assertEquals(objectSource, relation.getObjectType().getName(), msg + " [objectSource]");
		assertEquals(fieldSource, relation.getField().getName(), msg + " [fieldSource]");
		assertEquals(typeTarget, relation.getField().getType().getName(), msg + " [typeTarget]");
		if (targetField == null) {
			assertNull(relation.getMappedyBy(), msg + " [mapped by]");
		} else {
			assertEquals(targetField, relation.getMappedyBy().getName(), msg + " [mapped by]");
		}
	}

	/** Tests the annotation. We're in Server mode, thanks to the Spring Configuration used for this test */
	@Test
	@DirtiesContext
	void test_addAnnotations_client() {
		// Preparation
		Type topic = documentParser.objectTypes.stream().filter(o -> o.getName().equals("Topic")).findFirst().get();

		// Verification
		assertEquals("", topic.getAnnotation(), "Entity annotation");
		int i = 0;
		checkFieldAnnotation(topic.getFields().get(i++), "id",
				"@GraphQLScalar(graphQLTypeName = \"ID\", javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "date",
				"@JsonDeserialize(using = CustomScalarDeserializerDate.class)\n"
						+ "	@GraphQLScalar(graphQLTypeName = \"Date\", javaClass = Date.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "author",
				"@GraphQLNonScalar(graphQLTypeName = \"Member\", javaClass = Member.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "publiclyAvailable",
				"@GraphQLScalar(graphQLTypeName = \"Boolean\", javaClass = Boolean.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "nbPosts",
				"@GraphQLScalar(graphQLTypeName = \"Int\", javaClass = Integer.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "title",
				"@GraphQLScalar(graphQLTypeName = \"String\", javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "content",
				"@GraphQLScalar(graphQLTypeName = \"String\", javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "posts",
				"@JsonDeserialize(contentAs = Post.class)\n\t@GraphQLNonScalar(graphQLTypeName = \"Post\", javaClass = Post.class)");
	}

	private void checkFieldAnnotation(Field field, String name, String annotation) {
		String msg = "Check annotation for field " + field.getName() + " (client mode)";
		assertEquals(name, field.getName(), msg + " [name]");
		assertEquals(annotation, field.getAnnotation(), msg + " [annotation]");
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initDataFetchers() {
		assertEquals(0, documentParser.dataFetchers.size(), "no data fetcher in client mode");
	}
}
