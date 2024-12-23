package ru.quipy.sagaBankDemo.accounts.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.domain.Event
import ru.quipy.sagaBankDemo.accounts.api.AccountAggregate
import ru.quipy.sagaBankDemo.accounts.api.AccountCreatedEvent
import ru.quipy.sagaBankDemo.accounts.api.BankAccountCreatedEvent
import ru.quipy.sagaBankDemo.accounts.api.BankAccountDepositEvent
import ru.quipy.sagaBankDemo.accounts.api.BankAccountWithdrawalEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferDepositFailedEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferDepositSuccessEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferFailedEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferRollbackDepositEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferRollbackWithdrawEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferWithdrawFailedEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferWithdrawSuccessEvent
import java.math.BigDecimal
import java.util.UUID

class Account : AggregateState<UUID, AccountAggregate> {
    lateinit var accountId: UUID
    lateinit var holderId: UUID
    var bankAccounts: MutableMap<UUID, BankAccount> = mutableMapOf()

    override fun getId() = accountId

    fun createNewAccount(id: UUID = UUID.randomUUID(), holderId: UUID): AccountCreatedEvent {
        return AccountCreatedEvent(id, holderId)
    }

    fun createNewBankAccount(): BankAccountCreatedEvent {
        if (bankAccounts.size >= 5)
            throw IllegalStateException("Account $accountId already has ${bankAccounts.size} bank accounts")

        return BankAccountCreatedEvent(accountId = accountId, bankAccountId = UUID.randomUUID())
    }
    fun deposit1(
        accountIdTo: UUID,
        toBankAccountId: UUID,
        amount: BigDecimal,
    ): BankAccountDepositEvent {
        val bankAccount = (bankAccounts[toBankAccountId]
            ?: throw IllegalArgumentException("No such account to transfer to: $toBankAccountId"))

        if (bankAccount.balance + amount > BigDecimal(10_000_000))
            throw IllegalStateException("You can't store more than 10.000.000 on account ${bankAccount.id}")

        if (bankAccounts.values.sumOf { it.balance } + amount > BigDecimal(25_000_000))
            throw IllegalStateException("You can't store more than 25.000.000 in total")

        bankAccount.deposit(amount)
        return BankAccountDepositEvent(accountIdTo, toBankAccountId, amount)
    }

    fun deposit(
        toBankAccountId: UUID,
        amount: BigDecimal,
    ) {
        val bankAccount = (bankAccounts[toBankAccountId]
            ?: throw IllegalArgumentException("No such account to transfer to: $toBankAccountId"))

        if (bankAccount.balance + amount > BigDecimal(10_000_000))
            throw IllegalStateException("You can't store more than 10.000.000 on account ${bankAccount.id}")

        if (bankAccounts.values.sumOf { it.balance } + amount > BigDecimal(25_000_000))
            throw IllegalStateException("You can't store more than 25.000.000 in total")

        bankAccount.deposit(amount)
    }

    fun withdraw(
        fromBankAccountId: UUID,
        amount: BigDecimal,
    ) {
        val fromBankAccount = bankAccounts[fromBankAccountId]
            ?: throw IllegalArgumentException("No such account to withdraw from: $fromBankAccountId")

        if (amount > fromBankAccount.balance) {
            throw IllegalArgumentException("Cannot withdraw $amount. Not enough money: ${fromBankAccount.balance}")
        }

        fromBankAccount.withdraw(amount)
    }


    @StateTransitionFunc
    fun createNewBankAccount(event: AccountCreatedEvent) {
        accountId = event.accountId
        holderId = event.userId
    }

    @StateTransitionFunc
    fun createNewBankAccount(event: BankAccountCreatedEvent) {
        bankAccounts[event.bankAccountId] = BankAccount(event.bankAccountId)
    }
    @StateTransitionFunc
    fun withdrawsuccess(event: ExternalAccountTransferWithdrawSuccessEvent) {
        bankAccounts[event.bankAccountIdFrom]!!.withdraw(event.transferAmount)
    }
    @StateTransitionFunc
    fun depositsuccess(event: ExternalAccountTransferDepositSuccessEvent) {
        bankAccounts[event.bankAccountIdTo]!!.deposit(event.transferAmount)
    }
    @StateTransitionFunc
    fun rollback(event: ExternalAccountTransferRollbackDepositEvent) {
    }
    @StateTransitionFunc
    fun rollback(event: ExternalAccountTransferRollbackWithdrawEvent) {
    }
    @StateTransitionFunc
    fun depositfailed(event: ExternalAccountTransferDepositFailedEvent) {
    }
    @StateTransitionFunc
    fun withdrawfailed(event: ExternalAccountTransferWithdrawFailedEvent) {
    }
    @StateTransitionFunc
    fun withdrawfailed(event: ExternalAccountTransferFailedEvent) {
    }

    @StateTransitionFunc
    fun deposit(event: BankAccountDepositEvent) {
        bankAccounts[event.bankAccountId]!!.deposit(event.amount)
    }

    @StateTransitionFunc
    fun withdraw(event: BankAccountWithdrawalEvent) {
        bankAccounts[event.bankAccountId]!!.withdraw(event.amount)
    }


    fun transferFrom(
        bankAccountIdTo: UUID,
        transactionId: UUID,
        transferAmount: BigDecimal,
        accountIdFrom: UUID,
        bankAccountIdFrom: UUID,
        accountIdTo: UUID
    ): Event<AccountAggregate> {
        val bankAccount = bankAccounts[bankAccountIdFrom]
            ?: return ExternalAccountTransferWithdrawFailedEvent(
                accountIdFrom = accountIdFrom,
                bankAccountIdFrom = bankAccountIdFrom,
                accountIdTo = accountIdTo,
                bankAccountIdTo = bankAccountIdTo,
                transferAmount = transferAmount,
                transactionId = transactionId,
                "Bank account with id = $bankAccountIdFrom not found"
            )

        if (transferAmount > bankAccount.balance) {
            return ExternalAccountTransferWithdrawFailedEvent(
                accountIdFrom = accountIdFrom,
                bankAccountIdFrom = bankAccountIdFrom,
                accountIdTo = accountIdTo,
                bankAccountIdTo = bankAccountIdTo,
                transferAmount = transferAmount,
                transactionId = transactionId,
                "Cannot withdraw $transferAmount. Not enough money: ${bankAccount.balance}"
            )
        }
        return try {
            withdraw(bankAccountIdFrom, transferAmount)

            ExternalAccountTransferWithdrawSuccessEvent(
                accountIdFrom = accountIdFrom,
                bankAccountIdFrom = bankAccountIdFrom,
                accountIdTo = accountIdTo,
                bankAccountIdTo = bankAccountIdTo,
                transferAmount = transferAmount,
                transactionId = transactionId,
            )
        } catch (e: Exception) {
            ExternalAccountTransferWithdrawFailedEvent(
                accountIdFrom = accountIdFrom,
                bankAccountIdFrom = bankAccountIdFrom,
                accountIdTo = accountIdTo,
                bankAccountIdTo = bankAccountIdTo,
                transferAmount = transferAmount,
                transactionId = transactionId,
                message = e.message.orEmpty()
            )
        }
    }

    fun transferTo(
        bankAccountIdTo: UUID,
        transactionId: UUID,
        transferAmount: BigDecimal,
        accountIdFrom: UUID,
        bankAccountIdFrom: UUID,
        accountIdTo: UUID
    ): Event<AccountAggregate> {
        return try {
            deposit(bankAccountIdTo, transferAmount)
            ExternalAccountTransferDepositSuccessEvent(
                accountIdFrom = accountIdFrom,
                bankAccountIdFrom = bankAccountIdFrom,
                accountIdTo = accountIdTo,
                bankAccountIdTo = bankAccountIdTo,
                transferAmount = transferAmount,
                transactionId = transactionId,
            )
        } catch (e: Exception) {
            ExternalAccountTransferDepositFailedEvent(
                accountIdFrom = accountIdFrom,
                bankAccountIdFrom = bankAccountIdFrom,
                accountIdTo = accountIdTo,
                bankAccountIdTo = bankAccountIdTo,
                transferAmount = transferAmount,
                transactionId = transactionId,
                message = e.message.orEmpty()
            )
        }
    }

    fun rollbackTransferFrom(
        bankAccountIdTo: UUID,
        transactionId: UUID,
        transferAmount: BigDecimal,
        accountIdFrom: UUID,
        bankAccountIdFrom: UUID,
        accountIdTo: UUID
    ): ExternalAccountTransferFailedEvent{
        deposit(bankAccountIdTo, transferAmount)
        return ExternalAccountTransferFailedEvent(
            accountIdFrom = accountIdFrom,
            bankAccountIdFrom = bankAccountIdFrom,
            accountIdTo = accountIdTo,
            bankAccountIdTo = bankAccountIdTo,
            transferAmount = transferAmount,
            transactionId = transactionId,
            message = "rollbacked"
        )
    }
}

data class BankAccount(
    val id: UUID,
    internal var balance: BigDecimal = BigDecimal.ZERO,
) {
    fun deposit(amount: BigDecimal) {
        this.balance = this.balance.add(amount)
    }

    fun withdraw(amount: BigDecimal) {
        this.balance = this.balance.subtract(amount)
    }
}