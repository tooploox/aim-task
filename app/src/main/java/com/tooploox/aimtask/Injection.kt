package com.tooploox.aimtask

import com.tooploox.aimtask.data.gateway.OkHttpDataGateway
import com.tooploox.aimtask.domain.Schedulers
import com.tooploox.aimtask.domain.UseCaseFactory
import com.tooploox.aimtask.platform.repository.ImMemoryDataRepository
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Normally dependency injection would be implemented using Dagger, not a service locator. And even if a service locator would be used,
 * e.g. before Dagger is set up, Kotlin allows for elegant lazy instantiation using `lazy` delegate.
 */
object Injection {

    private val dataGateway = OkHttpDataGateway()
    private val dataRepository = ImMemoryDataRepository()

    val schedulers = Schedulers(AndroidSchedulers.mainThread())

    val useCaseFactory = UseCaseFactory(dataGateway, dataRepository, schedulers)
}
