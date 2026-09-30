package com.ahmrh.githubactivity.command;


import com.ahmrh.githubactivity.model.EventFormatter;
import com.ahmrh.githubactivity.model.GithubEvent;
import com.ahmrh.githubactivity.repository.GithubRepository;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GithubActivityCommands {

    private final GithubRepository repository;
    private final EventFormatter formatter;

    public GithubActivityCommands(GithubRepository repository, EventFormatter formatter) {
        this.repository = repository;
        this.formatter = formatter;
    }
    @Command(name = "github-activity", description = "Show recent GitHub activity of a user")
    public String activity(
            @Argument(index = 0, description = "GitHub username") String username,
            @Option(longName = "verbose", shortName = 'v',
                    description = "Show details for each event",
                    defaultValue = "false") boolean verbose) {
        try {
            List<GithubEvent> events = repository.getEvents(username);
            if (events.isEmpty()) {
                return "No recent public activity for " + username;
            }
            return events.stream()
                    .map(e -> formatter.format(e, verbose))
                    .collect(Collectors.joining("\n"));
        } catch (HttpClientErrorException.NotFound e) {
            return "User not found: " + username;
        } catch (RestClientException e) {
            return "Could not fetch activity: " + e.getMessage();
        }
    }

}
