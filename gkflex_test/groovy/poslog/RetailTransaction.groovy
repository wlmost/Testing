package poslog

import groovy.transform.ToString

/**
 * Represents a RetailTransaction in the POSLog export.
 * Corresponds to section 4.3.6 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class RetailTransaction {

    // ---- Attributes ----
    /**
     * Status of transaction.
     * Possible values: PostVoided, Suspended, Unknown, SES:ItemInfo, Replaced,
     *                  SES:Invoice, Finished, SES:LayawayPostVoided, SES:Ordered,
     *                  SES:Moved, SES:Scale, SES:ScalePostVoided
     */
    String transactionStatus

    // ---- XML elements ----
    /** Date and time of receipt (optional) */
    String receiptDateTime

    /** Line items and tender line items (optional) */
    List<LineItem> lineItems = []

    /** Total amount of the transaction (TotalType = TransactionGrandAmount, optional) */
    BigDecimal totalGrandAmount
    /** Total net amount of the transaction (TotalType = TransactionNetAmount, optional) */
    BigDecimal totalNetAmount
    /** Total tax amount of the transaction (TotalType = TransactionTaxAmount, optional) */
    BigDecimal totalTaxAmount
    /** CST:XXCustom attributes on Total elements (optional) */
    Map<String, String> totalCustomFields = [:]

    /** Customer data, created when the receipt includes customer data (optional) */
    Customer customer

    /** Transaction link (optional); created for voiding, invoice, offline rebooking, etc. */
    TransactionLink transactionLink

    /** "false" if total >= 0; "true" if total < 0 */
    Boolean sesNegativeTotalFlag
    /** "true" in case of amendment (optional) */
    Boolean sesAmendmentFlag
    /** "true" if sending receipt as e-mail is activated (optional) */
    Boolean sesEmailRequestedFlag
    /** E-mail address (optional) */
    String sesEmailAddress
    /** Invoice printout type code (optional) */
    String sesInvoicePrintoutTypeCode
    /** Number of the invoice, if invoice number was generated (optional) */
    String sesInvoiceNumber
    /** Coupon summary entries (optional) */
    List<SesCouponSummary> sesCouponSummaryList = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
