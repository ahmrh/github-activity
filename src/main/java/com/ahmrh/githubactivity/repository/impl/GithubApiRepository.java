package com.ahmrh.githubactivity.repository.impl;

import com.ahmrh.githubactivity.model.GithubEvent;
import com.ahmrh.githubactivity.repository.GithubRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Repository
public class GithubApiRepository implements GithubRepository {
    private final RestClient restClient;

    public GithubApiRepository(RestClient githubRestClient) {
        this.restClient = githubRestClient;
    }

    @Override
    public List<GithubEvent> getEvents(String username) {
        return restClient.get()
                .uri("/users/{username}/events?per_page=30", username)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubEvent>>() {});
    }
}
