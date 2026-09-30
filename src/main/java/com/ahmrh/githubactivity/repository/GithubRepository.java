package com.ahmrh.githubactivity.repository;

import com.ahmrh.githubactivity.model.GithubEvent;

import java.util.List;

public interface GithubRepository {
    List<GithubEvent> getEvents(String username);
}
