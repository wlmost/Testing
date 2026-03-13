package poslog

import groovy.transform.ToString

/**
 * Represents a TenderSummary element used in TillSettle and SesSafeSettle.
 * Corresponds to the TenderSummary section in 4.3.7.3 and 4.3.7.10.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TenderSummary {
    /** Over-difference entry (created if count difference >= 0) */
    Over over
    /** Short-difference entry (created if count difference < 0) */
    ShortSummary shortSummary
    /** Closing/target balance of drawer or safe (optional) */
    SesNominal sesNominal
    /** Counted/actual balance of drawer or safe (optional) */
    SesEnding sesEnding

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents an Over element within TenderSummary (positive count difference).
 */
@ToString(includeNames = true, ignoreNulls = true)
class Over {
    /** Tender type */
    String tenderType
    /** Positive amount of the drawer/safe count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if count difference >= 0) */
    Integer count
}

/**
 * Represents a Short element within TenderSummary (negative count difference).
 * Named ShortSummary to avoid collision with java.lang.Short.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ShortSummary {
    /** Tender type */
    String tenderType
    /** Negative amount of the drawer/safe count difference in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if count difference < 0) */
    Integer count
}

/**
 * Represents SES:Nominal within TenderSummary (closing/target balance).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesNominal {
    /** Tender type attribute */
    String tenderType
    /**
     * TypeCode attribute to distinguish positive from negative amount.
     * Possible values: "Refund" (negative), "Sale" (positive)
     */
    String typeCode
    /** Closing/target amount of the drawer or safe in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}

/**
 * Represents SES:Ending within TenderSummary (counted/actual balance).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesEnding {
    /** Tender type attribute */
    String tenderType
    /**
     * TypeCode attribute to distinguish positive from negative amount.
     * Possible values: "Refund" (negative), "Sale" (positive)
     */
    String typeCode
    /** Counted/actual amount of the drawer or safe in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Count (optional; created if Count != 0) */
    Integer count
}
