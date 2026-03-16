package poslog

import groovy.transform.ToString

/**
 * Represents a TenderControlTransaction in the POSLog export.
 * Corresponds to section 4.3.7 of the GK POSLog Structure v3.
 *
 * The choice among the various sub-elements determines the type of tender control transaction.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TenderControlTransaction {

    // ---- Transaction type choice (exactly one will be populated) ----
    /** Change transaction – tender transfer from safe to drawer (optional) */
    SesTenderLoan sesTenderLoan
    /** Safe bag or bank deposit transaction – tender transfer from safe to bank (optional) */
    Deposit deposit
    /** Drawer settlement or cash check transaction (optional) */
    TillSettle tillSettle
    /** Safe drop transaction – cash transfer from bank to safe (optional) */
    SafeDrop safeDrop
    /** Tender pickup transaction – tender transfer from drawer to safe (optional) */
    SesTenderPickup sesTenderPickup
    /** Paid-in transaction or safe opening balance or safe correction pay-in (optional) */
    SesPaidIn sesPaidIn
    /** Paid-out transaction or safe correction pay-out (optional; will not be generated as TenderInterchange) */
    SesPaidOut sesPaidOut
    /** Tender loan carried forward – opening balance of next accounting period (optional) */
    SesTenderLoanCarriedForward sesTenderLoanCarriedForward
    /** Safe settlement transaction (optional) */
    SesSafeSettle sesSafeSettle

    /** Reference to another transaction (optional) */
    TransactionLink sesTransactionLink
    /** Receipt position addons (optional) */
    List<SesReceiptPositionAddon> sesReceiptPositionAddonList = []
    /** OperatorID of operator with one-to-one assignment to drawer (optional) */
    String sesAccountedOperatorID
    /** Name attribute on SES:AccountedOperatorID (optional) */
    String sesAccountedOperatorName

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:TenderLoan within TenderControlTransaction.
 * Tender transfer from safe to drawer.
 * Corresponds to section 4.3.7.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderLoan {
    /** Tender type attribute */
    String tenderType
    /** Tender description attribute (optional) */
    String tenderDescription
    /** Sum of tenders of tender group cash in main currency */
    SesTotals sesTotals

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:Totals within SesTenderLoan.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTotals {
    /** Amount (= 0 if no tender line item in transaction) */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Reason element created in case of change correction (optional) */
    String reason
    /** Description attribute on Reason (optional) */
    String reasonDescription
    /** Name attribute on Reason (optional) */
    String reasonName

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a Deposit element within TenderControlTransaction.
 * Safe bag or bank deposit – tender transfer from safe to bank.
 * Corresponds to section 4.3.7.2 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Deposit {
    /** Not used (empty string) */
    String bank = ''
    /** Not used (empty string) */
    String account = ''
    /** Safe bag number (safe bag) or empty string (bank deposit) */
    String bagID
    /** Amount in main currency */
    BigDecimal amount
    /** Currency attribute on Amount (optional; for foreign currency) */
    String amountCurrency
    /** ForeignAmount attribute on Amount (optional; for foreign currency) */
    BigDecimal foreignAmount
    /** Employee who transferred safe bag to bank (WorkerID attribute) */
    String depositorWorkerID
    /** Totals of tenders */
    List<DepositDetail> depositDetails = []
    /** Foreign currency tenders (optional) */
    List<SesDepositForeignCurrency> sesDepositForeignCurrencies = []
    /**
     * Safe bag status (optional).
     * 1 = created, 2 = deleted, 3 = picked up, 4 = created caused by change order
     */
    Integer sesSafebagStatus
    /** Safe bag document number (optional) */
    String sesSafebagDocumentNumber
    /** Pickup safe bag number (optional; created if sesSafebagStatus = 3) */
    String sesPickupSafebagNumber
    /**
     * Safe bag type code (optional).
     * 1 = common safe bag, 3 = safe bag caused by change order
     */
    Integer sesSafebagTypeCode
    /** Timestamp of safe bag status change (optional) */
    String sesStatusTimestamp

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents DepositDetail within Deposit.
 */
@ToString(includeNames = true, ignoreNulls = true)
class DepositDetail {
    /** Amount of tender of picked up safe bag in main currency */
    BigDecimal tenderTotal
    /** TenderType attribute on TenderTotal */
    String tenderType
    /** Fix value "0000" */
    String reason = '0000'
}

/**
 * Represents SES:DepositForeignCurrency within Deposit.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesDepositForeignCurrency {
    /** Amount of tender of picked up safe bag in main currency */
    BigDecimal tenderTotal
    /** TenderType attribute on TenderTotal */
    String tenderType
    /** Currency attribute on TenderTotal */
    String currency
    /** Amount in foreign currency (ForeignAmount attribute) */
    BigDecimal foreignAmount
}

/**
 * Represents a TillSettle element within TenderControlTransaction.
 * Drawer settlement or drawer cash check.
 * Corresponds to section 4.3.7.3 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class TillSettle {
    /** Tender summary entries (one per tender) */
    List<TenderSummary> tenderSummaries = []
    /**
     * Category of drawer counting transaction (optional).
     * "CHECK" = cash check transaction; "SETTLE" = settlement transaction
     */
    String sesTransactionCategoryCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SafeDrop within TenderControlTransaction.
 * Cash transfer from bank to safe.
 * Corresponds to section 4.3.7.4 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SafeDrop {
    /** Amount of cash receipt */
    BigDecimal dropAmount
    /** Empty string (mandatory field) */
    String envelopeID = ''
    /** Empty string (mandatory field) */
    String dropNumber = ''
}

/**
 * Represents SES:TenderPickup within TenderControlTransaction.
 * Tender transfer from drawer to safe.
 * Corresponds to section 4.3.7.5 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderPickup {
    /** Amount of tender in main currency (TenderType attribute, optional TypeCode, etc.) */
    List<SesTenderAmount> sesTenderAmounts = []
    /** Sum of all tenders (optional TypeCode = "Refund") */
    BigDecimal sesTotalAmount
    /** TypeCode attribute on SesTotalAmount (optional) – "Refund" */
    String sesTotalAmountTypeCode
    /** Identifier of the Envelope used for this Pickup (optional) */
    String sesEnvelopeID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single SES:TenderAmount within SesTenderPickup.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderAmount {
    /** Amount in main currency */
    BigDecimal amount
    /** TenderType attribute */
    String tenderType
    /** TypeCode attribute (optional; "Refund" only) */
    String typeCode
    /** Currency attribute (optional; foreign currency only) */
    String currency
    /** ForeignAmount attribute (optional; foreign currency only) */
    BigDecimal foreignAmount
    /** TenderDescription attribute (optional) */
    String tenderDescription

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:PaidIn within TenderControlTransaction.
 * Paid-in, safe opening balance, or safe correction pay-in transaction.
 * Corresponds to section 4.3.7.6 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesPaidIn {
    /** Amount of paid-in */
    BigDecimal sesAmount
    /** Entered reason for paid-in (default "0000") */
    String sesReason
    /** Tender positions of paid-in transaction (optional) */
    List<Tender> sesTenders = []
    /**
     * Category of PaidIn transaction (optional).
     * "PAYIN" = paid-in; "OPEN" = safe opening balance; "CORR" = safe correction
     */
    String sesTransactionCategoryCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:PaidOut within TenderControlTransaction.
 * Paid-out or safe correction pay-out transaction.
 * Corresponds to section 4.3.7.7 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesPaidOut {
    /** Amount of paid-out */
    BigDecimal amount
    /** Entered reason for paid-out (default "0000") */
    String reason
    /** Tender positions of paid-out transaction (optional) */
    List<Tender> tenders = []
    /**
     * Category of PaidOut transaction (optional).
     * "PAYOUT" = paid-out; "CORR" = safe correction
     */
    String sesTransactionCategoryCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:TenderLoanCarriedForward within TenderControlTransaction.
 * Tender amount in the drawer at the beginning of an accounting period.
 * Corresponds to section 4.3.7.9 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesTenderLoanCarriedForward {
    /** Carryforward of tender loan ending balance (opening balance of next period) */
    BigDecimal amount

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:SafeSettle within TenderControlTransaction.
 * Safe settlement / safe accounting transaction.
 * Corresponds to section 4.3.7.10 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSafeSettle {
    /** Tender summary entries for the safe accounting transaction */
    List<TenderSummary> tenderSummaries = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
