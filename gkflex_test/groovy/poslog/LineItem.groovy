package poslog

import groovy.transform.ToString

/**
 * Represents a single LineItem within RetailTransaction.
 * Corresponds to section 4.3.6.1 of the GK POSLog Structure v3.
 *
 * The choice among sale, returnItem, rounding, sesRounding, voids, loyaltyReward,
 * tax, and tender determines the line item type.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LineItem {

    // ---- Attributes ----
    /**
     * "true" if this line item was voided (optional).
     */
    Boolean voidFlag
    /**
     * Entry method (optional; created only for sale and return).
     * Possible values: Scanned, SES:KeyedPerDuplicate, Keyed
     */
    String entryMethod

    // ---- XML elements ----
    /**
     * Sequence number of the receipt position.
     * For additional down payment line items for immediate pickups: starts at 100001.
     * Otherwise: sequence number of original receipt position (starts with '0').
     */
    String sequenceNumber
    /** Date and time of position creation (optional) */
    String beginDateTime

    // ---- Line item type choice (exactly one will be populated) ----
    /** Sale position (optional) */
    Sale sale
    /** Return position (optional) */
    Return returnItem
    /** Rounding total (optional) */
    Rounding rounding
    /** SES rounding line item (optional) */
    SesRounding sesRounding
    /** Void position (optional) */
    Voids voids
    /** Non-financial bonus (optional) */
    LoyaltyReward loyaltyReward
    /** Tax (one per used tax group, optional) */
    Tax tax
    /** Tender (one per used tender, optional) */
    Tender tender

    // ---- Common line item extensions ----
    /** Approval by another operator (optional) */
    SesOperatorBypassApproval sesOperatorBypassApproval
    /**
     * Direct void flag (optional); created in case of voided original receipt position.
     * true = direct void
     */
    Boolean sesDirectVoidFlag
    /**
     * Negative amount flag (optional).
     * For Rounding: true if rounding amount is negative.
     * For Tax: true if TaxableAmount < 0.
     */
    Boolean sesNegativeAmountFlag
    /**
     * Online/offline state when the line item was completed (optional).
     * Possible values: OnLineReferenceItem, OffLine, Both
     */
    String sesKeyedOfflineCode
    /** Position addon list (optional) */
    List<SesReceiptPositionAddon> sesReceiptPositionAddonList = []
    /** Binary data attached to the line item (optional) */
    List<BinaryData> sesLineItemBinaryDataList = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
