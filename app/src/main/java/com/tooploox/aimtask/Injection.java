package com.tooploox.aimtask;

import com.tooploox.aimtask.data.gateway.OkHttpDataGateway;
import com.tooploox.aimtask.domain.Schedulers;
import com.tooploox.aimtask.domain.UseCaseFactory;
import com.tooploox.aimtask.domain.interfaces.DataGateway;
import com.tooploox.aimtask.domain.interfaces.DataRepository;
import com.tooploox.aimtask.platform.repository.ImMemoryDataRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Normally dependency injection would be implemented using Dagger, not a service locator. And even if a service locator would be used,
 * e.g. before Dagger is set up, Kotlin allows for elegant lazy instantiation using `lazy` delegate.
 */
public enum Injection {
    INSTANCE;

    private final DataGateway dataGateway = new OkHttpDataGateway();
    private final DataRepository dataRepository = new ImMemoryDataRepository();
    private final Schedulers schedulers = new Schedulers(AndroidSchedulers.mainThread());

    private final UseCaseFactory useCaseFactory = new UseCaseFactory(dataGateway, dataRepository, schedulers);

    public UseCaseFactory getUseCaseFactory() {
        return useCaseFactory;
    }

    public Schedulers getSchedulers() {
        return schedulers;
    }
}
