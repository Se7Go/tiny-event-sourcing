package ru.quipy.sagaBankDemo.accounts.subscribers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.core.EventSourcingService
import ru.quipy.sagaBankDemo.accounts.api.AccountAggregate
import ru.quipy.sagaBankDemo.accounts.logic.Account
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferDepositEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferRollbackDepositEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferRollbackWithdrawEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferSuccessEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalTransferWithdrawEvent
import ru.quipy.sagaBankDemo.transfers.api.TransferAggregate
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Component
class TransfersSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val accountEsService: EventSourcingService<UUID, AccountAggregate, Account>
) {
    private val logger: Logger = LoggerFactory.getLogger(TransfersSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TransferAggregate::class, "accounts::transaction-processing-subscriber") {
            `when`(ExternalTransferWithdrawEvent::class) { event ->
                logger.info("Got transfer to process: $event")
                accountEsService.update(event.accountIdFrom) { 
                    it.transferFrom(
                        accountIdFrom = event.accountIdFrom,
                        bankAccountIdFrom = event.bankAccountIdFrom,
                        accountIdTo = event.accountIdTo,
                        bankAccountIdTo = event.bankAccountIdTo,
                        transactionId = event.transactionId,
                        transferAmount = event.transferAmount
                    )
                }
            }
            `when`(ExternalTransferRollbackWithdrawEvent::class) { event ->
                logger.info("Rollback withdraw: $event")
                accountEsService.update(event.accountIdFrom) { 
                    it.rollbackTransferFrom(
                        accountIdFrom = event.accountIdFrom,
                        bankAccountIdFrom = event.bankAccountIdFrom,
                        accountIdTo = event.accountIdTo,
                        bankAccountIdTo = event.bankAccountIdTo,
                        transactionId = event.transactionId,
                        transferAmount = event.transferAmount
                    )
                }
            }
            `when`(ExternalTransferDepositEvent::class) { event ->
                accountEsService.update(event.accountIdFrom) { 
                    it.transferTo(
                        accountIdFrom = event.accountIdFrom,
                        bankAccountIdFrom = event.bankAccountIdFrom,
                        accountIdTo = event.accountIdTo,
                        bankAccountIdTo = event.bankAccountIdTo,
                        transactionId = event.transactionId,
                        transferAmount = event.transferAmount
                    )
                }
            }

            `when`(ExternalTransferSuccessEvent::class) { event ->
                // done
                logger.info("Transfer with id = ${event.transactionId} success")
            }
        }
    }
}