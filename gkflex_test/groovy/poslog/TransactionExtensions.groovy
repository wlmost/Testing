package poslog

import groovy.transform.ToString

/**
 * Represents approval by another operator (SES:OperatorBypassApproval).
 * Used in Transaction and LineItem.
 * Corresponds to section 4.3.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesOperatorBypassApproval {
    /**
     * Shows how authorization was triggered.
     * Fix value: "CODE" (operator number)
     */
    String approvalTypeCode

    /** Approval code (optional, not created) */
    String approvalCode
    /** ID of authorizing operator (optional) */
    String approverID
    /** WorkerID attribute on ApproverID (optional) */
    String workerID
    /** ApproverName attribute on ApproverID (optional) */
    String approverName

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single addon entry within SES:ReceiptHeaderAddonList.
 * Corresponds to section 4.3.2.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Addon {
    /** Addon sequence number per key (optional) */
    String addonPos
    /**
     * Addon key.
     * Examples: TransactionTypeCode, TransactionCategoryCode, FIRSTLOGIN, ONLINE, OFFLINE
     */
    String key
    /**
     * Addon value.
     * Examples: value of TransactionTypeCode, "true" (FIRSTLOGIN),
     *           timestamp of Online/Offline status change
     */
    String value

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single timer entry within SES:ReceiptTimerList.
 * Corresponds to section 4.3.3.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Timer {
    /**
     * Timer identifier.
     * "76" = first item registration to first tender registration.
     * "77" = first tender registration to end of receipt.
     */
    String timerID
    /**
     * Receipt begin timestamp (for "76") or tender begin timestamp (for "77").
     */
    String startTimestamp
    /**
     * Duration in seconds with 3 decimals.
     * For "76": duration from receipt begin to tender begin.
     * For "77": duration from tender begin to end of receipt.
     */
    BigDecimal duration
}

/**
 * Represents SES:LoyaltyAccount on transaction level.
 * Corresponds to section 4.3.4 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesLoyaltyAccount {
    /** Customer ID (optional) */
    String customerID
    /** Loyalty program details (optional) */
    List<LoyaltyProgram> loyaltyPrograms = []
}

/**
 * Represents a loyalty program within SES:LoyaltyAccount.
 * Corresponds to section 4.3.4.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LoyaltyProgram {
    /** Identifier of the loyalty account (optional) */
    String loyaltyAccountID
    /** Effective date (optional) */
    String effectiveDate
    /** Type of points (optional) */
    String points
    /** Type attribute on Points element (optional) */
    String pointsType
    /**
     * Determines the kind of customer account.
     * Examples: 00 (turnover of current year), 01 (rebate amount), 02 (bonus points),
     *           VP (Valuephone), GC (gift certificates)
     */
    String loyaltyAccountTypeCode
    /** External identifier of the accounting transaction (optional) */
    String loyaltyAccountTransactionID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
