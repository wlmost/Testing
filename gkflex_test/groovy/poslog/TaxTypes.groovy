package poslog

import groovy.transform.ToString

/**
 * Represents a Tax element in a LineItem (line item level or transaction level).
 * Corresponds to the Tax sections in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Tax {

    // ---- Attributes ----
    /** TaxType attribute – fix value "Common" (optional) */
    String taxType
    /**
     * TaxSubType attribute (optional).
     * "ZeroRated" if rate of tax = 0; otherwise "Standard"
     */
    String taxSubType
    /**
     * TypeCode attribute (optional).
     * Possible values: Refund, Sale
     */
    String typeCode

    // ---- XML elements ----
    /** ID of the tax authority (optional) */
    String taxAuthority
    /** Taxable amount without sign (optional) */
    BigDecimal taxableAmount
    /** Tax amount (optional) */
    BigDecimal amount
    /** Tax percentage (optional) */
    BigDecimal percent
    /** Tax exemption data (optional) */
    TaxExemption taxExemption
    /** Tax override data (optional; not used for tax on transaction level) */
    TaxOverride taxOverride
    /** Tax group ID (optional) */
    String taxGroupID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a TaxExemption element within Tax.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TaxExemption {
    /** Tax certificate ID */
    String customerExemptionID
    /** Fix value = 0 (not supported) */
    BigDecimal exemptTaxAmount = BigDecimal.ZERO
    /** Reason code for the tax exemption (optional) */
    String reasonCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a TaxOverride element within Tax.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TaxOverride {
    /** Original tax percentage */
    BigDecimal originalPercent
    /** Original tax amount */
    BigDecimal originalTaxAmount
    /** New tax percentage */
    BigDecimal newTaxPercent
    /** New tax amount */
    BigDecimal newTaxAmount
    /** Reason code for the tax override (optional) */
    String reasonCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
