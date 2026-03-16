package poslog

import groovy.transform.ToString

/**
 * Represents a ControlTransaction in the POSLog export.
 * Corresponds to section 4.3.8 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ControlTransaction {

    /** Reason code in case of an existing reason (optional) */
    String reasonCode

    // ---- Transaction type choice (exactly one will be populated) ----
    /** End-of-day (EOD) transaction (optional) */
    BusinessEOD businessEOD
    /** Total/summary values (drawer related) (optional) */
    TillEOD tillEOD
    /** NoSale transaction: timestamp for NoSale (optional) */
    String noSaleTimestamp
    /** Operator log-in (optional) */
    OperatorSignOn operatorSignOn
    /** Operator log-out (optional) */
    OperatorSignOff operatorSignOff
    /** Cashier statistics values (optional) */
    SesCashierStatistics sesCashierStatistics
    /** Total/summary values (store related) (optional) */
    SesStoreEODSummary sesStoreEODSummary
    /** Tax refund issue values (optional) */
    SesTaxRefund sesTaxRefund
    /** Created for all transaction types not exported in other tags (optional) */
    String sesOtherTransactionType

    /**
     * Flag created in case of OperatorSignOff (optional).
     * "true" = forced logout; "false" = normal logout
     */
    Boolean sesForcedSignOffFlag
    /** Identifier of workstation forced to log out (optional; only for forced logout) */
    String sesForcedSignOffWorkstationID
    /** OperatorID of operator with one-to-one assignment to drawer (optional) */
    String sesAccountedOperatorID
    /** Name attribute on SES:AccountedOperatorID (optional) */
    String sesAccountedOperatorName

    /** Reference to another transaction (optional) */
    List<TransactionLink> sesTransactionLinks = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents BusinessEOD within ControlTransaction.
 * Corresponds to section 4.3.8.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class BusinessEOD {
    /** Date and time of beginning of business day (accounting period) */
    String startDateTimestamp
}

/**
 * Represents OperatorSignOn within ControlTransaction.
 * Corresponds to section 4.3.8.3 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class OperatorSignOn {
    /** Date and time of sign-on receipt (optional) */
    String startDateTimestamp
}

/**
 * Represents OperatorSignOff within ControlTransaction.
 * Corresponds to section 4.3.8.4 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class OperatorSignOff {
    /** Date and time of sign-off receipt (optional) */
    String startDateTimestamp
}

/**
 * Represents SES:TaxRefund within ControlTransaction.
 * Corresponds to section 4.3.8.7 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTaxRefund {
    /**
     * TypeCode attribute (optional).
     * Possible values: Refund, Sale
     */
    String typeCode
    /** Unique identifier of the tax refund document from the tax refund service */
    String sesTaxRefundDocumentID
    /** UUID for the transaction in the external system (optional) */
    String sesExternalTransactionID
    /** Fiscal invoice number (optional; for countries where required) */
    String sesStoreInvoiceID
    /** Summarized gross amount for all sale transactions (absolute value) */
    BigDecimal sesTotalGrossAmount
    /** Summarized tax amount for all sale transactions (absolute value, optional) */
    BigDecimal sesTotalTaxAmount
    /** Resulting tax refund amount (absolute value) */
    BigDecimal sesTotalRefundAmount

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
