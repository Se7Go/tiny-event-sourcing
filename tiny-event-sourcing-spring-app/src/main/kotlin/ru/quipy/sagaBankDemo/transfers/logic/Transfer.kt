package ru.quipy.sagaBankDemo.transfers.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.domain.Event
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferDepositEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferRollbackDepositEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferRollbackWithdrawEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferSuccessEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferWithdrawEvent
import ru.quipy.sagaBankDemo.transfers.api.TransferAggregate
import java.math.BigDecimal
import java.util.UUID

class Transfer : AggregateState<UUID, TransferAggregate> {
    lateinit var transferId: UUID
    var status = TransferState.CREATED

    override fun getId(): UUID = transferId

    fun withdrawMoneyFrom(
        accountIdFrom: UUID,
        bankAccountIdFrom: UUID,
        transferAmount: BigDecimal,
        accountIdTo: UUID,
        bankAccountIdTo: UUID,
        transactionId: UUID
    ): Event<TransferAggregate> {
        return ExternalTransferWithdrawEvent(
            accountIdFrom = accountIdFrom,
            bankAccountIdFrom = bankAccountIdFrom,
            accountIdTo = accountIdTo,
            bankAccountIdTo = bankAccountIdTo,
            transferAmount = transferAmount,
            transactionId = transactionId,
        )
    }

    fun depositMoneyTo(
        accountIdTo: UUID,
        bankAccountIdTo: UUID,
        transferAmount: BigDecimal,
        bankAccountIdFrom: UUID,
        accountIdFrom: UUID,
        transactionId: UUID
    ): Event<TransferAggregate> {
        return ExternalTransferDepositEvent(
            accountIdFrom = accountIdFrom,
            bankAccountIdFrom = bankAccountIdFrom,
            accountIdTo = accountIdTo,
            bankAccountIdTo = bankAccountIdTo,
            transferAmount = transferAmount,
            transactionId = transactionId,
        )
    }

    fun rollbackWithdrawMoney(
        accountIdFrom: UUID,
        bankAccountIdFrom: UUID,
        transferAmount: BigDecimal,
        accountIdTo: UUID,
        bankAccountIdTo: UUID,
        transactionId: UUID
    ): Event<TransferAggregate> {
        return ExternalTransferRollbackWithdrawEvent(
            accountIdFrom = accountIdFrom,
            bankAccountIdFrom = bankAccountIdFrom,
            accountIdTo = accountIdTo,
            bankAccountIdTo = bankAccountIdTo,
            transferAmount = transferAmount,
            transactionId = transactionId,
        )
    }

    fun rollbackDepositMoney(
        accountIdFrom: UUID,
        bankAccountIdFrom: UUID,
        transferAmount: BigDecimal,
        accountIdTo: UUID,
        bankAccountIdTo: UUID,
        transactionId: UUID
    ): Event<TransferAggregate> {
        return ExternalTransferRollbackDepositEvent(
            accountIdFrom = accountIdFrom,
            bankAccountIdFrom = bankAccountIdFrom,
            accountIdTo = accountIdTo,
            bankAccountIdTo = bankAccountIdTo,
            transferAmount = transferAmount,
            transactionId = transactionId,
        )
    }

    fun notifyTransferSuccess(transactionId: UUID): ExternalTransferSuccessEvent {
        return ExternalTransferSuccessEvent(transactionId)
    }

    @StateTransitionFunc
    fun withdrawMoneyFrom(event: ExternalTransferWithdrawEvent) {
        status = TransferState.PROCESSING
    }

    @StateTransitionFunc
    fun depositMoneyTo(event: ExternalTransferDepositEvent) {
        status = TransferState.PROCESSING
    }

    @StateTransitionFunc
    fun rollbackWithdrawMoney(event: ExternalTransferRollbackWithdrawEvent) {
        status = TransferState.FAILED
    }

    @StateTransitionFunc
    fun rollbackDepositMoney(event: ExternalTransferRollbackDepositEvent) {
        status = TransferState.FAILED
    }

    @StateTransitionFunc
    fun notifyTransferSuccess(event: ExternalTransferSuccessEvent) {
        status = TransferState.SUCCEEDED
    }

    enum class TransferState {
        CREATED,
        PROCESSING,
        SUCCEEDED,
        FAILED
    }
}