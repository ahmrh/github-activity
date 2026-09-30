package com.ahmrh.githubactivity.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record GithubEvent(
        String id,
        String type,
        Actor actor,
        Repo repo,
        Payload payload,
        @JsonProperty("public") boolean isPublic,
        @JsonProperty("created_at") Instant createdAt) {

    public record Actor(String login) {}

    public record Repo(String name) {}

    public record Payload(
            String action,
            String ref,
            @JsonProperty("ref_type") String refType,
            Integer size,
            List<Commit> commits,
            Issue issue,
            @JsonProperty("pull_request") PullRequest pullRequest,
            Comment comment,
            Release release,
            Forkee forkee,
            Member member,
            List<Page> pages,
            Discussion discussion) {}

    // PushEvent
    public record Commit(String sha, String message) {}

    // IssuesEvent, IssueCommentEvent
    public record Issue(int number, String title, @JsonProperty("html_url") String url) {}

    // PullRequestEvent, PullRequestReview*Event
    public record PullRequest(
            int number,
            String title,
            Boolean merged,
            @JsonProperty("html_url") String url) {}

    // IssueCommentEvent, CommitCommentEvent, PullRequestReviewCommentEvent
    public record Comment(String body, @JsonProperty("html_url") String url) {}

    // ReleaseEvent
    public record Release(@JsonProperty("tag_name") String tagName, String name) {}

    // ForkEvent
    public record Forkee(@JsonProperty("full_name") String fullName) {}

    // MemberEvent
    public record Member(String login) {}

    // GollumEvent
    public record Page(@JsonProperty("page_name") String pageName, String title, String action) {}

    // DiscussionEvent
    public record Discussion(String title) {}
}