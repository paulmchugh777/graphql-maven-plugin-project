/**
 * 
 */
package org.allGraphQLCases.introspection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.allGraphQLCases.Main;
import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client.__IntrospectionQuery;
import org.allGraphQLCases.client.__Schema;
import org.allGraphQLCases.client.__Type;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class contains the Integration tests, that will execute GraphQL introspection querie against the
 * allGraphQLCases-server GraphQL server.
 * 
 * @author etienne-sf
 */
public class IntrospectionIT {

	__IntrospectionQuery introspectionQuery = new __IntrospectionQuery(Main.GRAPHQL_ENDPOINT);

	@Test
	void testSchema() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Schema schema = introspectionQuery.__schema("{types {name fields {name type {name}}}}");

		// Verification
		assertEquals(40, schema.getTypes().size());
		assertEquals("AllFieldCases", schema.getTypes().get(0).getName());
		assertEquals("id", schema.getTypes().get(0).getFields().get(0).getName());
	}

	@Test
	void testType() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		__Type type = introspectionQuery.__type("{name fields {name type {name}}}", "AllFieldCases");

		// Verification
		assertEquals("AllFieldCases", type.getName());
		assertEquals("id", type.getFields().get(0).getName());
	}

	@Test
	void test__datatype_allFieldCases() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Verification
		MyQueryType queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);

		// Go, go, go
		// AllFieldCases ret = queryType.allFieldCases("{allFieldCases {id __typename}}", null);
		AllFieldCases ret = queryType.allFieldCases("{id __typename}", null);

		// Verification
		assertEquals("AllFieldCases", ret.get__typename());
	}

	@Disabled // Not ready yet for interfaces
	@Test
	void test__datatype_withoutParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Verification
		MyQueryType queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);

		// Go, go, go
		List<Character> ret = queryType.withoutParameters("{withoutParameters {id __typename}}");

		// Verification
		assertTrue(ret.size() >= 10);
		fail("not properly tested");
		// assertEquals("Droid", ret.get(0).get__typename());
	}

	@Disabled // Not ready yet
	@Test
	void test__datatype_allFieldCases_Error()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Verification
		MyQueryType queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);

		// Go, go, go
		AllFieldCases ret = queryType.allFieldCases("{allFieldCases {id __typename}}", null);

		// Verification
		fail("This should raise a proper error, as the correct query type should be '{id __typename}'");
	}
}