package com.graphql_java_generator.client.domain.forum;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
public class MutationTypeCreatePosts {

	@JsonDeserialize(contentAs = Post.class)
	@JsonProperty("createPosts")
	List<Post> createPosts;

	public void setCreatePosts(List<Post> createPosts) {
		this.createPosts = createPosts;
	}

	public List<Post> getCreatePosts() {
		return createPosts;
	}
	
    public String toString() {
        return "MutationTypeCreatePosts {createPosts: " + createPosts + "}";
    }
}
