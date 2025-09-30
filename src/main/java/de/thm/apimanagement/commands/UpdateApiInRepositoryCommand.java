package de.thm.apimanagement.commands;

import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.repository.ApiRepository;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdateApiInRepositoryCommand implements Command {
    private final ApiRepository apiRepository;
    private final int apiId;
    private final Api newApi;
    private Api backupApi;

    public UpdateApiInRepositoryCommand(ApiRepository apiRepository, int apiId, Api api) {
        this.apiRepository = apiRepository;
        this.apiId = apiId;
        this.newApi  = api;
    }

    @Override
    public void execute() {
        apiRepository.findById(apiId).ifPresent(apiDb -> {
            // Backup current state for undo
            backupApi = new Api(apiDb);

            // Update fields only if the new data is not null/blank
            updateIfNotBlank(newApi::getName, apiDb::setName);
            updateIfNotBlank(newApi::getDescription, apiDb::setDescription);
            updateIfNotBlank(newApi::getVersion, apiDb::setVersion);
            updateIfNotBlank(newApi::getFormat, apiDb::setFormat);
            updateIfNotNull(newApi::getSpec, apiDb::setSpec);
            updateIfNotBlank(newApi::getToken, apiDb::setToken);

            apiRepository.save(apiDb);
        });
    }

    @Override
    public void undo() {
        if (backupApi != null) {
            apiRepository.save(backupApi);
        }
    }

    private void updateIfNotBlank(Supplier<String> getter, Consumer<String> setter) {
        String value = getter.get();
        if (value != null && !value.isBlank()) {
            setter.accept(value);
        }
    }

    private <T> void updateIfNotNull(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }
}
