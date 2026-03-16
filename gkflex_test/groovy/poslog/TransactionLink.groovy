package poslog

import groovy.transform.ToString

/**
 * Represents a shared link to another transaction.
 * Used within RetailTransaction, TenderControlTransaction, and ControlTransaction.
 * Corresponds to multiple TransactionLink sections in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TransactionLink {
    /**
     * Reason code attribute (optional).
     * Possible values: Voided, SES:Invoice, SES:OfflineRebooking, LayAway,
     *                  SES:RetrievedSuspend, SES:Replace, SES:Order,
     *                  SES:OrderQuantityChange, SES:InvoiceDuplicate, SES:TaxRefund
     */
    String reasonCode

    /** Retail store number of the original transaction */
    String retailStoreID
    /** Workstation/POS number of the original transaction */
    String workstationID
    /** Receipt number of the original transaction */
    String sequenceNumber
    /** Line item sequence number of the original line item (optional) */
    String lineItemSequenceNumber
    /** Date of business day of the original transaction */
    String businessDayDate
    /** Begin date and time of the original transaction */
    String beginDateTime
    /** End date and time of the original transaction (optional) */
    String endDateTime
    /** Receipt date and time of the original transaction (optional) */
    String sesReceiptDateTime
    /** Universally unique identifier (UUID) of the original transaction */
    String sesInternalTransactionID
}
