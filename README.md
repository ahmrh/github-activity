# github-activity

A simple command-line tool that shows a GitHub user's recent public activity, built with **Spring Shell**, **Java** and **Gradle**. It calls the GitHub REST API and prints each event as a readable line.

This project is a solution to the roadmap.sh GitHub User Activity project: https://roadmap.sh/projects/github-user-activity

```bash
github-activity ahmrh
github-activity ahmrh --verbose
```

## Features

- Fetch a user's recent public events from the GitHub API
- Readable output for pushes, stars, forks, issues, pull requests, branches and more
- Optional `--verbose` flag with timestamps, branch names, commit messages and issue/PR titles
- Friendly messages for unknown users and network or API errors
- No API token and no local storage needed

## Requirements

- JDK 25
- Gradle (the included wrapper `./gradlew` is enough)
- Internet access to `api.github.com`

## Tech stack

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring Shell | 4.x |
| HTTP client | `RestClient` (`spring-boot-starter-restclient`) |
| JSON | Jackson 3 (`spring-boot-starter-jackson`) |
| Build tool | Gradle (Kotlin DSL) |

## Build

```bash
./gradlew bootJar
```

The jar is created at `build/libs/github-activity-0.0.1-SNAPSHOT.jar`.

## Run

Make sure you run the jar with Java 25. An older `java` on your `PATH` fails with `UnsupportedClassVersionError`.

**One-shot mode** (runs a command and exits):

```bash
java -jar build/libs/github-activity-0.0.1-SNAPSHOT.jar github-activity ahmrh
```

**Interactive mode** (no arguments, opens a `shell:>` prompt):

```bash
java -jar build/libs/github-activity-0.0.1-SNAPSHOT.jar
```

```
shell:> github-activity ahmrh
```

> `./gradlew bootRun` does not forward stdin, so interactive mode does not work through it. Use the jar. To run a single command through Gradle: `./gradlew bootRun --args='github-activity ahmrh'`.

### Use it as `github-activity`

Create a wrapper script at `~/.local/bin/github-activity`. The script adds the command name for you, so you only type the username:

```bash
#!/usr/bin/env bash
exec /path/to/jdk-25/bin/java -jar /path/to/github-activity/build/libs/github-activity-0.0.1-SNAPSHOT.jar github-activity "$@"
```

```bash
chmod +x ~/.local/bin/github-activity
```

Make sure `~/.local/bin` is on your `PATH`. Rebuild with `./gradlew bootJar` after code changes and the script picks up the new jar.

## Commands

| Command | Description | Example |
|---|---|---|
| `github-activity <username>` | Show the user's recent public activity | `github-activity ahmrh` |
| `github-activity <username> [-v, --verbose]` | Same, with details for each event | `github-activity ahmrh --verbose` |

### Example output

```
$ github-activity kamranahmedse
- Pushed 3 commits to kamranahmedse/developer-roadmap
- Opened a new issue in kamranahmedse/developer-roadmap
- Starred kamranahmedse/developer-roadmap
```

With `--verbose`:

```
$ github-activity kamranahmedse --verbose
- Pushed 3 commits to kamranahmedse/developer-roadmap (2026-09-30 08:12)
    branch: master
    * Fix typo in README
    * Update roadmap content
    * Add license file
- Opened a new issue in kamranahmedse/developer-roadmap (2026-09-29 17:40)
    #42 Button does not render on mobile
- Starred kamranahmedse/developer-roadmap (2026-09-29 10:03)
```

Timestamps are shown in your machine's timezone.

## Supported events

| Event | Output |
|---|---|
| `PushEvent` | Pushed N commits to `repo` |
| `WatchEvent` | Starred `repo` |
| `ForkEvent` | Forked `repo` |
| `CreateEvent` / `DeleteEvent` | Created or deleted a repository, branch or tag |
| `IssuesEvent` | Opened, closed or reopened an issue |
| `IssueCommentEvent` | Commented on an issue |
| `PullRequestEvent` | Opened, closed or merged a pull request |
| `PullRequestReviewEvent` / `PullRequestReviewCommentEvent` | Reviewed or commented on a pull request |
| `ReleaseEvent` | Published a release |
| Others | Shown as the event type and repository |

## Error handling

| Situation | Message |
|---|---|
| Unknown username | `User not found: <username>` |
| No recent public events | `No recent public activity for <username>` |
| Network or API error | `Could not fetch activity: <reason>` |

## API notes

The tool uses `GET https://api.github.com/users/{username}/events`.

- Only **public** events are returned, limited to the last 90 days or 300 events.
- Unauthenticated requests are limited to about 60 per hour per IP address. If you hit the limit, wait for it to reset.

## Project structure

```
src/main/java/com/example/githubcli/
├── GithubCliApplication.java
├── config/RestClientConfig.java        # RestClient bean (base URL, headers)
├── model/GithubEvent.java              # records matching the API JSON
├── repository/
│   ├── GithubRepository.java           # interface
│   └── GithubApiRepository.java        # RestClient implementation
└── command/
    ├── GithubCommands.java             # Spring Shell command
    └── EventFormatter.java             # event -> text
```
