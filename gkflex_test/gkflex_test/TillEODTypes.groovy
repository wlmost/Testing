package poslog

import groovy.transform.ToString

/**
 * Represents TillEOD within ControlTransaction.
 * Total/summary values (drawer related).
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillEOD {
    /** Session settle container with sum values per accounting period */
    TillEODSessionSettle sessionSettle
}

/**
 * Represents the SessionSettle container within TillEOD.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillEODSessionSettle {
    /** Sum values of tenders per accounting period (optional) */
    List<TillEODTenderSummary> tenderSummaries = []
    /** Sum values of sales per accounting period (optional) */
    List<SesSalesSummary> sesSalesSummaries = []
    /** Not generated at this time (optional) */
    // SesRebateSummary sesRebateSummary
    /** Sum values of taxes per accounting period (optional) */
    List<SesTaxSummary> sesTaxSummaries = []
}

/**
 * Represents TenderSummary within TillEOD's SessionSettle.
 * Contains opening/closing/pickup/over/short/ending balance per tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillEODTenderSummary {
    /** Opening balance (optional) */
    Beginning beginning
    /** Tender pickup (optional) */
    Pickup pickup
    /** Positive difference (created if over) */
    EODOver over
    /** Negative difference (created if short) */
    EODShort shortEntry
    /** Closing/target balance of drawer (optional) */
    SesNominal sesNominal
    /** Tender totals of all sales/return receipts (optional) */
    SesSalesTenderNominal sesSalesTenderNominal
    /** Counted/actual balance of drawer (optional) */
    SesEnding sesEnding
}

/**
 * Represents Beginning within TillEODTenderSummary (opening balance).
 */
@ToString(includeNames = true, ignoreNulls = true)
class Beginning {
    /** Tender type attribute */
    String tenderType
    /** Opening balance in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents Pickup within TillEODTenderSummary (manual pickup amount).
 */
@ToString(includeNames = true, ignoreNulls = true)
class Pickup {
    /** Tender type attribute */
    String tenderType
    /** Manual pickup amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents Over within TillEODTenderSummary (positive difference).
 */
@ToString(includeNames = true, ignoreNulls = true)
class EODOver {
    /** Tender type attribute */
    String tenderType
    /** Positive amount of the drawer count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents Short within TillEODTenderSummary (negative difference).
 * Named EODShort to avoid collision with java.lang.Short.
 */
@ToString(includeNames = true, ignoreNulls = true)
class EODShort {
    /** Tender type attribute */
    String tenderType
    /** Negative amount of the drawer count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents SES:SalesTenderNominal within TillEODTenderSummary.
 * Tender totals of all sales/return receipts.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSalesTenderNominal {
    /** Tender type attribute */
    String tenderType
    /**
     * TypeCode attribute.
     * Possible values: "Refund" (negative), "Sale" (positive)
     */
    String typeCode
    /** Tender totals of all sales/return receipts in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents SES:SalesSummary within TillEOD or SesStoreEODSummary SessionSettle.
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSalesSummary {
    /**
     * TypeCode attribute (optional).
     * Possible values: Refund, Sale
     */
    String typeCode
    /** Sales of sales receipts */
    BigDecimal amount
    /** Quantity of sales receipts */
    Integer count
    /**
     * Reason for the sales entry (attribute Name fix value "SalesID").
     * Examples: "2001" (Sales), "2011" (Subtotal rounding), "2020" (Sold gift certs),
     *           "3101" (Down payments), "3102" (Cleared down payments),
     *           "3103" (Invoices), "3104" (Pay-ins), "3201" (Pay-outs)
     */
    String reason
    /** Name attribute on Reason – fix value "SalesID" */
    String reasonName = 'SalesID'
}

/**
 * Represents SES:TaxSummary within TillEOD or SesStoreEODSummary SessionSettle.
 * Corresponds to section 4.3.8.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTaxSummary {
    /**
     * TenderType attribute (optional).
     * Possible values: Refund, Sale
     */
    String tenderType
    /** Sales per tax code */
    BigDecimal amount
    /** Quantity of taxed positions */
    Integer count
    /** ID of tax authority */
    String sesTaxAuthorityID
    /** ID of tax group */
    String sesTaxGroupID
}
