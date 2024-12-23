package ru.quipy.sagaBankDemo.accounts.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.sagaBankDemo.accounts.api.AccountAggregate
import ru.quipy.sagaBankDemo.accounts.logic.Account
import java.util.UUID

@Configuration
class AccountBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun accountEsService(): EventSourcingService<UUID, AccountAggregate, Account> =
        eventSourcingServiceFactory.create()
}