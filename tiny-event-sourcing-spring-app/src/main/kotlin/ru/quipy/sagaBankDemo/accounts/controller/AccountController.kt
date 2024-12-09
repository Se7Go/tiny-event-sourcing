package ru.quipy.sagaBankDemo.accounts.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.core.EventSourcingService
import ru.quipy.saga.SagaManager
import ru.quipy.sagaBankDemo.accounts.api.AccountAggregate
import ru.quipy.sagaBankDemo.accounts.api.AccountCreatedEvent
import ru.quipy.sagaBankDemo.accounts.api.BankAccountCreatedEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferEvent
import ru.quipy.sagaBankDemo.accounts.logic.Account
import ru.quipy.sagaBankDemo.accounts.logic.BankAccount
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    val accountEsService: EventSourcingService<UUID, AccountAggregate, Account>,
    val sagaManager: SagaManager
) {

    @PostMapping("/{holderId}")
    fun createAccount(@PathVariable holderId: UUID): AccountCreatedEvent {
        return accountEsService.create { it.createNewAccount(holderId = holderId) }
    }

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: UUID): Account? {
        return accountEsService.getState(accountId)
    }

    @PostMapping("/{accountId}/bankAccount")
    fun createBankAccount(@PathVariable accountId: UUID): BankAccountCreatedEvent {
        return accountEsService.update(accountId) { it.createNewBankAccount() }
    }

    @GetMapping("/{accountId}/bankAccount/{bankAccountId}")
    fun getBankAccount(@PathVariable accountId: UUID, @PathVariable bankAccountId: UUID): BankAccount? {
        return accountEsService.getState(accountId)?.bankAccounts?.get(bankAccountId)
    }

    @GetMapping("/{accountId}/bankAccount/{bankAccountId}/transfer/{toAccountId}/{toBankAccountId}")
    fun transfer(
        @PathVariable accountId: UUID,
        @PathVariable bankAccountId: UUID,
        @PathVariable toAccountId: UUID,
        @PathVariable toBankAccountId: UUID,
        @RequestBody amount: BigDecimal,
    ): ExternalAccountTransferEvent {
        val sagaContext = sagaManager
            .launchSaga("TRANSFER", "start transfer money")
            .sagaContext()
        return accountEsService.create(sagaContext) {
            it.startTransfer(
                accountId,
                bankAccountId,
                toAccountId,
                toBankAccountId,
                amount
            )
        }
    }
}