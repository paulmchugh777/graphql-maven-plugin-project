package com.graphql_java_generator.client.domain.allGraphQLCases;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
public class MyQueryTypeAllFieldCases {

	@JsonProperty("allFieldCases")
	AllFieldCases allFieldCases;

	public void setAllFieldCases(AllFieldCases allFieldCases) {
		this.allFieldCases = allFieldCases;
	}

	public AllFieldCases getAllFieldCases() {
		return allFieldCases;
	}
	
    public String toString() {
        return "MyQueryTypeAllFieldCases {allFieldCases: " + allFieldCases + "}";
    }
}
