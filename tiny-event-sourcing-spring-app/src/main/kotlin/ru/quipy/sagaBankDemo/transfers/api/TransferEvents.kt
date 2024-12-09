package ru.quipy.sagaBankDemo.transfers.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.UUID

const val TRANSFER_TRANSACTION_CREATED = "TRANSFER_TRANSACTION_CREATED"

const val EXTERNAL_TRANSFER_FAILED = "EXTERNAL_TRANSFER_FAILED_EVENT"
const val EXTERNAL_TRANSFER_SUCCESS = "EXTERNAL_TRANSFER_SUCCESS_EVENT"
const val EXTERNAL_TRANSFER_WITHDRAW = "EXTERNAL_TRANSFER_WITHDRAW_EVENT"
const val EXTERNAL_TRANSFER_ROLLBACK_WITHDRAW = "EXTERNAL_TRANSFER_ROLLBACK_WITHDRAW_EVENT"
const val EXTERNAL_TRANSFER_DEPOSIT = "EXTERNAL_TRANSFER_DEPOSIT_EVENT"
const val EXTERNAL_TRANSFER_ROLLBACK_DEPOSIT = "EXTERNAL_TRANSFER_ROLLBACK_DEPOSIT_EVENT"

@DomainEvent(name = TRANSFER_TRANSACTION_CREATED)
data class TransferTransactionCreatedEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
    val transferAmount: BigDecimal,
) : Event<TransferAggregate>(
    name = TRANSFER_TRANSACTION_CREATED,
)


@DomainEvent(name = EXTERNAL_TRANSFER_WITHDRAW)
data class ExternalTransferWithdrawEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<TransferAggregate>(
    name = EXTERNAL_TRANSFER_WITHDRAW,
)
@DomainEvent(name = EXTERNAL_TRANSFER_ROLLBACK_WITHDRAW)
data class ExternalTransferRollbackWithdrawEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<TransferAggregate>(
    name = EXTERNAL_TRANSFER_ROLLBACK_WITHDRAW,
)


@DomainEvent(name = EXTERNAL_TRANSFER_DEPOSIT)
data class ExternalTransferDepositEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<TransferAggregate>(
    name = EXTERNAL_TRANSFER_DEPOSIT,
)

@DomainEvent(name = EXTERNAL_TRANSFER_ROLLBACK_DEPOSIT)
data class ExternalTransferRollbackDepositEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
) : Event<TransferAggregate>(
    name = EXTERNAL_TRANSFER_ROLLBACK_DEPOSIT,
)

@DomainEvent(name = EXTERNAL_TRANSFER_SUCCESS)
data class ExternalTransferSuccessEvent(
    val transactionId: UUID,
) : Event<TransferAggregate>(
    name = EXTERNAL_TRANSFER_SUCCESS,
)

@DomainEvent(name = EXTERNAL_TRANSFER_FAILED)
data class ExternalTransferFailedEvent(
    val accountIdFrom: UUID,
    val bankAccountIdFrom: UUID,
    val accountIdTo: UUID,
    val bankAccountIdTo: UUID,
    val transferAmount: BigDecimal,
    val transactionId: UUID,
    val message: String = "Limit"
) : Event<TransferAggregate>(
    name = EXTERNAL_TRANSFER_FAILED,
)

// @DomainEvent(name = EXTERNAL_TRANSFER_DEPOSIT_FAILED)
// data class ExternalTransferDepositFailedEvent(
//     val accountIdFrom: UUID,
//     val bankAccountIdFrom: UUID,
//     val accountIdTo: UUID,
//     val bankAccountIdTo: UUID,
//     val transferAmount: BigDecimal,
//     val transactionId: UUID,
//     val message: String = "Limit"
// ) : Event<TransferAggregate>(
//     name = EXTERNAL_TRANSFER_DEPOSIT_FAILED,
// )