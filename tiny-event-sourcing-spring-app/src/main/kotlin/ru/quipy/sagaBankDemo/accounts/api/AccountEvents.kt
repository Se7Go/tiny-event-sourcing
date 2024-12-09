package ru.quipy.sagaBankDemo.accounts.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.UUID

const val ACCOUNT_CREATED = "ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_CREATED = "BANK_ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_DEPOSIT = "BANK_ACCOUNT_DEPOSIT_EVENT"
const val BANK_ACCOUNT_WITHDRAWAL = "BANK_ACCOUNT_WITHDRAWAL_EVENT"
const val INTERNAL_ACCOUNT_TRANSFER = "INTERNAL_ACCOUNT_TRANSFER_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER = "EXTERNAL_ACCOUNT_TRANSFER_EVENT"

const val EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_SUCCESS = "EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_SUCCESS_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_WITHDRAW = "EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_WITHDRAW_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_FAILED = "EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_FAILED_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_SUCCESS = "EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_SUCCESS_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_DEPOSIT = "EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_DEPOSIT_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_FAILED = "EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_FAILED_EVENT"
const val EXTERNAL_ACCOUNT_TRANSFER_FAILED = "EXTERNAL_ACCOUNT_TRANSFER_FAILED_EVENT"

const val TRANSFER_TRANSACTION_STARTED = "TRANSFER_TRANSACTION_STARTED"
const val TRANSFER_TRANSACTION_PROCESSED = "TRANSFER_TRANSACTION_PROCESSED"
const val TRANSFER_TRANSACTION_ROLLBACKED = "TRANSFER_TRANSACTION_ROLLBACKED"

@DomainEvent(name = ACCOUNT_CREATED)
data class AccountCreatedEvent(
    val accountId: UUID,
    val userId: UUID,
) : Event<AccountAggregate>(
    name = ACCOUNT_CREATED,
)

@DomainEvent(name = BANK_ACCOUNT_CREATED)
data class BankAccountCreatedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_CREATED,
)

@DomainEvent(name = BANK_ACCOUNT_DEPOSIT)
data class BankAccountDepositEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_DEPOSIT,
)

@DomainEvent(name = BANK_ACCOUNT_WITHDRAWAL)
data class BankAccountWithdrawalEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_WITHDRAWAL,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER)
data class ExternalAccountTransferEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_SUCCESS)
data class ExternalAccountTransferWithdrawSuccessEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_SUCCESS,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_WITHDRAW)
data class ExternalAccountTransferRollbackWithdrawEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_WITHDRAW,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_DEPOSIT)
data class ExternalAccountTransferRollbackDepositEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_ROLLBACK_DEPOSIT,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_FAILED)
data class ExternalAccountTransferWithdrawFailedEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
    val message: String = "Limit"
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_WITHDRAW_FAILED,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_SUCCESS)
data class ExternalAccountTransferDepositSuccessEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_SUCCESS,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_FAILED)
data class ExternalAccountTransferDepositFailedEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
    val message: String = "Limit"
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_FAILED,
)

@DomainEvent(name = EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_FAILED)
data class ExternalAccountTransferFailedEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
    val message: String = "Limit"
) : Event<AccountAggregate>(
    name = EXTERNAL_ACCOUNT_TRANSFER_DEPOSIT_FAILED,
)

