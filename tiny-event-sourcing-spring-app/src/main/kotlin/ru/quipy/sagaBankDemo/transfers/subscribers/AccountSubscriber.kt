package ru.quipy.sagaBankDemo.transfers.subscribers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.core.EventSourcingService
import ru.quipy.sagaBankDemo.accounts.api.AccountAggregate
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferDepositFailedEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferDepositSuccessEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferWithdrawFailedEvent
import ru.quipy.sagaBankDemo.accounts.api.ExternalAccountTransferWithdrawSuccessEvent
import ru.quipy.sagaBankDemo.transfers.api.ExternalAccountTransferEvent
import ru.quipy.sagaBankDemo.transfers.api.TransferAggregate
import ru.quipy.sagaBankDemo.transfers.logic.Transfer
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.UUID
import javax.annotation.PostConstruct

@Component
class AccountSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val transactionEsService: EventSourcingService<UUID, TransferAggregate, Transfer>
) {
    private val logger: Logger = LoggerFactory.getLogger(AccountSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TransferAggregate::class,"transfers::accounts-subscriber-1") {
            `when`(ExternalAccountTransferEvent::class) { event ->
                transactionEsService.update(event.transactionId) {
                    it.withdrawMoneyFrom(
                        accountIdFrom = event.accountIdFrom,
                        bankAccountIdFrom = event.bankAccountIdFrom,
                        accountIdTo = event.accountIdTo,
                        bankAccountIdTo = event.bankAccountIdTo,
                        transactionId = event.transactionId,
                        transferAmount = event.transferAmount
                    )
                }
            }
        }
        subscriptionsManager.createSubscriber(AccountAggregate::class, "transfers::accounts-subscriber") {

            `when`(ExternalAccountTransferWithdrawSuccessEvent::class) { event ->
                transactionEsService.update(event.transactionId) {
                    it.depositMoneyTo(
                        accountIdFrom = event.accountIdFrom,
                        bankAccountIdFrom = event.bankAccountIdFrom,
                        accountIdTo = event.accountIdTo,
                        bankAccountIdTo = event.bankAccountIdTo,
                        transactionId = event.transactionId,
                        transferAmount = event.transferAmount
                    )
                }
            }
            `when`(ExternalAccountTransferDepositFailedEvent::class) { event ->
                transactionEsService.update(event.transactionId) {
                    it.rollbackWithdrawMoney(
                        accountIdFrom = event.accountIdFrom,
                        bankAccountIdFrom = event.bankAccountIdFrom,
                        accountIdTo = event.accountIdTo,
                        bankAccountIdTo = event.bankAccountIdTo,
                        transactionId = event.transactionId,
                        transferAmount = event.transferAmount
                    )
                }
            }
            `when`(ExternalAccountTransferDepositSuccessEvent::class) { event ->
                transactionEsService.update(event.transactionId) {
                    it.notifyTransferSuccess(event.transactionId)
                }
            }
            `when`(ExternalAccountTransferWithdrawFailedEvent::class) { event ->
                transactionEsService.update(event.transactionId) {
                    it.notifyTransferFailed(event.transactionId)
                }
            }

        }
    }
}