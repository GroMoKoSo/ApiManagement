package de.thm.apimanagement.commands;

import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.repository.ApiRepository;

public class SaveApiToRepositoryCommand implements Command {
    private final ApiRepository apiRepository;
    private final Api api;

    public SaveApiToRepositoryCommand(ApiRepository apiRepository, Api api) {
        this.apiRepository = apiRepository;
        this.api = api;
    }

    @Override
    public void execute() {
        apiRepository.save(api);
    }

    @Override
    public void undo() {
        apiRepository.delete(api);
    }
}
