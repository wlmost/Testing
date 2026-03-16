package poslog

import groovy.transform.ToString

/**
 * Represents a single Transaction in the POSLog export.
 * Corresponds to section 4.3 of the GK POSLog Structure v3 specification.
 *
 * The choice between retailTransaction, tenderControlTransaction and controlTransaction
 * determines the type of transaction.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Transaction {

    // ---- Attributes ----
    /** Major version of POSLog export – fix value = 3 */
    Integer majorVersion = 3
    /** Minor version of POSLog export – fix value = 0 */
    Integer minorVersion = 0
    /** Fix version of POSLog export – fix value = 0 */
    Integer fixVersion = 0
    /** Internal major version of POSLog export interface – fix value = 2 */
    Integer sesInternalMajorVersion = 2
    /** Internal minor version of POSLog export interface – fix value = 5 */
    Integer sesInternalMinorVersion = 5
    /** Internal fix version of POSLog export interface – fix value = 9 */
    Integer sesInternalFixVersion = 9
    /** Flag for canceled transaction (optional) */
    Boolean cancelFlag
    /** Flag for transaction made in training mode (optional) */
    Boolean trainingModeFlag

    // ---- XML elements ----
    /** A unique retailer-assigned identifier for a RetailStore */
    String retailStoreID
    /** Identifier for the workstation/POS within the store */
    String workstationID
    /** Identifier for the tender repository (drawer) within the store (optional) */
    String tillID
    /** Receipt number / sequence counter (optional prefix added for offline rebooking/void) */
    String sequenceNumber
    /** Calendar date of the BusinessDay */
    String businessDayDate
    /** Time and date a transaction is initiated */
    String beginDateTime
    /** Time and date a transaction is completed */
    String endDateTime
    /** ID of the operator who created the transaction (optional) */
    String operatorID
    /** Operator name attribute of OperatorID (optional) */
    String operatorName
    /** WorkerID attribute of OperatorID (optional) */
    String workerID
    /** Unique identifier of the currency (3-digit ISO code, e.g. EUR, USD) */
    String currencyCode

    /** The unique identifier for the TENANT (optional) */
    String sesTenantID
    /** The time and date when the accounting period started (optional) */
    String sesBusinessBeginDateTime
    /** The time and date when the accounting period ended (optional) */
    String sesBusinessEndDateTime
    /** True in case of return transaction */
    Boolean sesReceiptReturnedFlag
    /** Reason code for canceled or voided transactions (optional) */
    String sesReasonCode
    /** ReasonType attribute on SES:ReasonCode (optional) */
    String sesReasonType
    /** Description attribute on SES:ReasonCode (optional) */
    String sesReasonDescription
    /** True when the entire original transaction has been post-voided */
    Boolean sesPostVoidedFlag
    /** Application version with which the transaction was created (optional) */
    String sesTransactionSoftwareVersion
    /** Composite version string (GK + release version) */
    String sesSoftwareVersion
    /**
     * Online/offline state when transaction was completed (optional).
     * Possible values: OnLineReferenceItem, OffLine, Both
     */
    String sesKeyedOfflineCode
    /** Universally unique identifier (UUID) for the Transaction */
    String sesInternalTransactionID
    /** Identifier created by POS, fiscal printer, or other fiscalization device (optional) */
    String sesFiscalSequenceNumber
    /** Flag denoting that this is a fiscal transaction */
    Boolean sesFiscalFlag
    /** Fiscal day counter (optional) */
    String sesFiscalDayNumber
    /** Identifier of the fiscal printer (optional) */
    String sesFiscalPrinterID
    /** Fiscal signature of the transaction (optional) */
    String sesFiscalSignature
    /** Flag denoting that this is a layaway transaction */
    Boolean sesLayawayFlag
    /**
     * Type of the layaway transaction (optional).
     * Possible values: FullyPay, Claiming, DownPayment, Modify, Void, Terminate,
     *                  Expire, Rebooking, Create, Extend
     */
    String sesLayawayTransactionType

    // ---- Sub-element containers ----
    /** XML-container: created in case of approval by another operator (optional) */
    SesOperatorBypassApproval sesOperatorBypassApproval
    /** XML-container: additional header information (optional) */
    List<Addon> sesReceiptHeaderAddonList = []
    /** XML-container: time information (optional) */
    List<Timer> sesReceiptTimerList = []
    /** XML-container: created in case of loyalty program (optional) */
    SesLoyaltyAccount sesLoyaltyAccount
    /** XML-container: binary data attached to the transaction (optional) */
    List<BinaryData> sesTransactionBinaryDataList = []

    // ---- Transaction type choice (exactly one will be populated) ----
    /** Created in case of retail transaction */
    RetailTransaction retailTransaction
    /** Created in case of tender control transaction */
    TenderControlTransaction tenderControlTransaction
    /** Created in case of control transaction */
    ControlTransaction controlTransaction

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}
