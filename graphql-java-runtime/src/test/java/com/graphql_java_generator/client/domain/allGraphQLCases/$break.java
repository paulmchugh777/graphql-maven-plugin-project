package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 *      "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */

public class $break {

	@GraphQLInputParameters(names = { "test", "if" }, types = { "extends", "else" })
	@GraphQLScalar(graphQLTypeName = "extends", javaClass = $extends.class)
	$extends $case;

	public void setCase($extends _case) {
		this.$case = _case;
	}

	public $extends getCase() {
		return $case;
	}

	@Override
	public String toString() {
		return "$break {" + "$case: " + $case + "}";
	}
}