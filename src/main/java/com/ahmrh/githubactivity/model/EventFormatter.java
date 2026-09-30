package com.ahmrh.githubactivity.model;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class EventFormatter {
    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    public String format(GithubEvent e, boolean verbose) {
        String line = format(e);
        if (!verbose) {
            return line;
        }
        String time = e.createdAt() == null ? "" : " (" + TIME.format(e.createdAt()) + ")";
        return line + time + details(e);
    }

    public String format(GithubEvent e) {
        String repo = e.repo().name();
        GithubEvent.Payload p = e.payload();

        return switch (e.type()) {
            case "PushEvent" -> {
                int n = p.size() != null ? p.size()
                        : (p.commits() == null ? 0 : p.commits().size());
                yield "- Pushed %d %s to %s".formatted(n, n == 1 ? "commit" : "commits", repo);
            }
            case "WatchEvent" -> "- Starred " + repo;
            case "ForkEvent" -> "- Forked " + repo;
            case "CreateEvent" -> switch (p.refType()) {
                case "repository" -> "- Created repository " + repo;
                case "branch" -> "- Created branch %s in %s".formatted(p.ref(), repo);
                case "tag" -> "- Created tag %s in %s".formatted(p.ref(), repo);
                default -> "- Created something in " + repo;
            };
            case "DeleteEvent" -> "- Deleted %s %s in %s".formatted(p.refType(), p.ref(), repo);
            case "IssuesEvent" -> switch (p.action()) {
                case "opened" -> "- Opened a new issue in " + repo;
                case "closed" -> "- Closed an issue in " + repo;
                case "reopened" -> "- Reopened an issue in " + repo;
                default -> "- %s an issue in %s".formatted(capitalize(p.action()), repo);
            };
            case "IssueCommentEvent" -> "- Commented on an issue in " + repo;
            case "PullRequestEvent" -> {
                boolean merged = p.pullRequest() != null && Boolean.TRUE.equals(p.pullRequest().merged());
                if (merged) {
                    yield "- Merged a pull request in " + repo;
                }
                yield switch (p.action()) {
                    case "opened" -> "- Opened a new pull request in " + repo;
                    case "closed" -> "- Closed a pull request in " + repo;
                    default -> "- %s a pull request in %s".formatted(capitalize(p.action()), repo);
                };
            }
            case "PullRequestReviewEvent" -> "- Reviewed a pull request in " + repo;
            case "PullRequestReviewCommentEvent" -> "- Commented on a pull request in " + repo;
            case "CommitCommentEvent" -> "- Commented on a commit in " + repo;
            case "ReleaseEvent" -> "- Published a release in " + repo;
            case "PublicEvent" -> "- Made " + repo + " public";
            case "MemberEvent" -> "- Added a collaborator to " + repo;
            case "GollumEvent" -> "- Updated the wiki of " + repo;
            default -> "- %s in %s".formatted(e.type().replace("Event", ""), repo);
        };
    }


    private String details(GithubEvent e) {
        GithubEvent.Payload p = e.payload();
        StringBuilder sb = new StringBuilder();

        switch (e.type()) {
            case "PushEvent" -> {
                if (p.ref() != null) {
                    sb.append("\n    branch: ").append(p.ref().replace("refs/heads/", ""));
                }
                if (p.commits() != null) {
                    p.commits().stream().limit(5).forEach(c ->
                            sb.append("\n    * ").append(firstLine(c.message())));
                }
            }
            case "IssuesEvent", "IssueCommentEvent" -> {
                if (p.issue() != null) {
                    sb.append("\n    #%d %s".formatted(p.issue().number(), p.issue().title()));
                }
            }
            case "PullRequestEvent" -> {
                if (p.pullRequest() != null) {
                    sb.append("\n    #%d %s".formatted(p.pullRequest().number(), p.pullRequest().title()));
                }
            }
            case "ReleaseEvent" -> {
                if (p.release() != null) {
                    sb.append("\n    ").append(p.release().tagName());
                }
            }
            default -> { }
        }
        return sb.toString();
    }

    private String capitalize(String s) {
        return s == null || s.isEmpty() ? "Did something with" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String firstLine(String s) {
        if (s == null) return "";
        int i = s.indexOf('\n');
        return i < 0 ? s : s.substring(0, i);
    }
}
