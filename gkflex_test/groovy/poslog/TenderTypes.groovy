package poslog

import groovy.transform.ToString

/**
 * Represents a Tender element in a LineItem.
 * Created for each used tender.
 * Corresponds to the Tender section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Tender {

    // ---- Attributes ----
    /** Tender type */
    String tenderType
    /** TypeCode – possible values: Refund, Sale */
    String typeCode
    /** Tender description (optional) */
    String tenderDescription

    // ---- XML elements ----
    /** Tender ID for main or foreign currency (optional) */
    String tenderID
    /** Amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount for foreign currency (optional) */
    String amountCurrency
    /** ForeignAmount attribute on Amount for foreign currency (optional) */
    BigDecimal foreignAmount
    /** Tender change (optional; only within RetailTransaction) */
    TenderChange tenderChange
    /** Cashback amount in main currency (optional; only within RetailTransaction) */
    BigDecimal cashback
    /** Cashback currency attribute (optional) */
    String cashbackCurrency
    /** Cashback foreign amount attribute (optional) */
    BigDecimal cashbackForeignAmount
    /** Tip amount (optional; in main currency) */
    BigDecimal tip
    /** Tip currency attribute (optional) */
    String tipCurrency
    /** Tip foreign amount attribute (optional) */
    BigDecimal tipForeignAmount
    /** Authorization data (optional; created for tenders with external authorization) */
    Authorization authorization
    /** Foreign currency data (optional; created only for foreign currency) */
    ForeignCurrency foreignCurrency
    /** Check tender data (optional) */
    Check check
    /** Credit/debit tender data (optional) */
    CreditDebit creditDebit
    /** Coupon tender data (optional) */
    Coupon coupon
    /** Voucher tender data (optional) */
    Voucher voucher
    /** Loyalty redemption tender data (optional) */
    SesLoyaltyRedemption sesLoyaltyRedemption

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a TenderChange element within Tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TenderChange {
    /** Tender type attribute */
    String tenderType
    /** Tender ID for main or foreign currency (optional) */
    String tenderID
    /** Amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount for foreign currency (optional) */
    String amountCurrency
    /** ForeignAmount attribute on Amount for foreign currency (optional) */
    BigDecimal foreignAmount
}

/**
 * Represents an Authorization element within Tender.
 * Corresponds to the Authorization section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Authorization {

    // ---- Attributes ----
    /**
     * HostAuthorized: true (auth codes 3, 8, 10 or 11); false (all other codes) (optional)
     */
    Boolean hostAuthorized
    /**
     * ElectronicSignature: true (auth codes 3, 10 or 11); false (all other codes) (optional)
     */
    Boolean electronicSignature
    /**
     * ForceOnline: true (online); false (offline) (optional)
     */
    Boolean forceOnline

    // ---- XML elements ----
    /** Absolute value of transaction (optional) */
    BigDecimal requestedAmount
    /** Currency attribute on RequestedAmount (optional) */
    String requestedAmountCurrency
    /** ForeignAmount attribute on RequestedAmount (optional) */
    BigDecimal requestedForeignAmount
    /** Code of authorization code */
    String authorizationCode
    /** Number of transaction (optional) */
    String referenceNumber
    /** Timestamp of authorization (optional) */
    String authorizationDateTime
    /** ID of used terminal (optional) */
    String authorizingTermID
    /** Activation sequence number (optional) */
    String sesActivationSequenceNumber
    /** Transaction reference number for 'Purchase Reservation' and 'Reservation Adjustment' (optional) */
    String sesTransactionReferenceNumber
    /**
     * Transaction type (optional).
     * Possible values: credit, debit
     */
    String sesTransactionType
    /** Terminal tender description (optional) */
    String sesTerminalTenderDescription
    /** Application identifier (optional) */
    String applicationID
    /** Application PAN coded (optional) */
    String sesEncryptedPAN
    /** Transaction currency (optional) */
    String sesTransactionCurrencyCode
    /** Unique number assigned by payment processor when EFT transaction is processed (optional) */
    String sesTerminalTransactionToken
    /** Terminal authorization number (optional) */
    String sesApprovalCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a ForeignCurrency element within Tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ForeignCurrency {
    /** Code of foreign currency (optional) */
    String currencyCode
    /** Origin amount in foreign currency */
    BigDecimal originalFaceAmount
    /** Exchange rate, rounded to 5 decimals (optional) */
    BigDecimal exchangeRate
}

/**
 * Represents a Check element within Tender.
 * Corresponds to the Check section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Check {
    /** Bank ID (optional) */
    String bankID
    /** Account number (optional) */
    String accountNumber
    /** Bank card number (optional) */
    String checkCardNumber
    /** Full string of characters read from MICR strip (optional) */
    String fullMICR
    /** Bank identifier code (SWIFT code) (optional) */
    String sesBankIdentifierCode
    /** International bank account number (optional) */
    String sesInternationalBankAccountNumber
    /** Cheque number (optional; use SES:CheckNumber instead) */
    String sesCheckNumber

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a CreditDebit element within Tender.
 * Corresponds to the CreditDebit section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class CreditDebit {
    /** Bank ID (optional) */
    String issuerIdentificationNumber
    /** Account number / bank card number (default "0000") */
    String primaryAccountNumber
    /** Card suffix (optional) */
    String issueSequence
    /** Expiration date of card (optional) */
    String expirationDate
    /** Code of transaction result (optional) */
    String reconciliationCode
    /** Date of transaction start (optional) */
    String startDate
    /** Trace number (optional) */
    String sesTraceNumber
    /** IBAN for SEPA Direct Debit (ELV) (optional) */
    String sesInternationalBankAccountNumber
    /** BIC / SWIFT Code for SEPA Direct Debit (ELV) (optional) */
    String sesBankIdentifierCode
    /** Creditor ID for SEPA Direct Debit (ELV) (optional) */
    String sesCreditorID
    /** Mandate ID for SEPA Direct Debit (ELV) (optional) */
    String sesMandateID
    /** Pre-notification text for SEPA Direct Debit (ELV) (optional) */
    String sesPrenotificationText
}

/**
 * Represents a Coupon element within Tender.
 * Corresponds to the Coupon section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Coupon {
    /** Fix value: 1 */
    Integer quantity = 1
    /** Coupon number */
    String primaryLabel
    /** Manufacturer ID that funds the offer (optional; for manufacturer coupons) */
    String manufacturerID
    /** Offer number (promotion code) (optional; for manufacturer coupons) */
    String promotionCode
    /** Single amount of coupon */
    BigDecimal sesCouponSingleAmount
    /** Rate of taxation */
    BigDecimal sesTaxPercent
    /** Flag: internal or external coupon */
    String sesOrigin
    /** Reference item list (optional) */
    List<SesReferenceItem> sesReferenceItemList = []
}

/**
 * Represents a single entry in SES:ReferenceItemList within Coupon.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesReferenceItem {
    /** EAN of discounted item */
    String itemID
    /** Quantity of discounted item */
    BigDecimal itemQuantity
    /** Rebate share of item */
    BigDecimal itemRebateShare
    /** Link to position number of item in POSLog (LinkType fix value "Coupon") */
    String itemLink
    /** LinkType attribute on ItemLink – fix value "Coupon" */
    String itemLinkType = 'Coupon'
}

/**
 * Represents a Voucher element within Tender.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Voucher {
    /**
     * TypeCode attribute.
     * Possible values: SES:ElectronicGiftCertificate, SES:PaperGiftCertificate
     */
    String typeCode
    /** EAN of voucher */
    String serialNumber
}

/**
 * Represents SES:LoyaltyRedemption within Tender.
 * Corresponds to the SES:LoyaltyRedemption section in 4.3.6.1.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesLoyaltyRedemption {
    /** Redeemed bonus points */
    BigDecimal pointsRedeemed
    /** ID of bonus point redemption transaction */
    String transactionID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
