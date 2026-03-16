package poslog

import groovy.transform.ToString

/**
 * Represents a Rounding element in a LineItem.
 * Rounding total; created only when there is an existing rounding difference
 * for the sum of subtotal rounding and tender change rounding.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Rounding {
    // Currency and ForeignAmount attributes are not created
}

/**
 * Represents an SES:Rounding element in a LineItem.
 * Rounding line item; created only when there is an existing rounding difference
 * for subtotal rounding or tender change rounding.
 * Corresponds to the SES:Rounding section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesRounding {
    /**
     * Type of rounding.
     * Possible values: "00" (subtotal rounding), "01" (tender change rounding)
     */
    String sesRoundingTypeCode
    /** Rounding amount */
    BigDecimal sesAmount
    /**
     * Rounding direction.
     * Possible values: Up, Down
     */
    String sesRoundingDirection

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a Voids element in a LineItem.
 * Created only in case of a void position.
 * Corresponds to the Voids section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Voids {
    /** Item link referencing the voided position */
    ItemLink itemLink
}

/**
 * Represents an ItemLink element within Voids.
 */
@ToString(includeNames = true, ignoreNulls = true)
class ItemLink {
    /** ReasonCode attribute – fix value "Voided" */
    String reasonCode = 'Voided'
    /** Same content as Transaction.SequenceNumber */
    String sequenceNumber
    /** LineItem SequenceNumber of the voided original position */
    String lineItemSequenceNumber
    /** Void reason code (default "0000") */
    String sesVoidReasonCode
    /**
     * ReasonType attribute on SES:VoidReasonCode.
     * Possible values: Void, SES:BelatedVoid, SES:BelatedInternalVoid
     */
    String sesVoidReasonType
    /** Description attribute on SES:VoidReasonCode (optional) */
    String sesVoidReasonDescription

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a LoyaltyReward element in a LineItem.
 * Created only in case of non-financial bonus.
 * Corresponds to the LoyaltyReward section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class LoyaltyReward {
    /** Description of receipt condition (optional) */
    String promotionID
    /** Reason code according to db entry (default "0000") */
    String reasonCode
    /** Bonus points awarded (optional) */
    BigDecimal pointsAwarded
    /** Discount as gift certificate (optional) */
    GiftCertificate giftCertificate
    /** Discount as coupon (optional) */
    SesVoucher sesVoucher
    /**
     * Continuous number over all LoyaltyReward elements of one line item.
     * Starting with "1"; used only for position-specific bonus points.
     */
    String sequenceNumber
    /** Price derivation rule data (optional) */
    PriceDerivationRule sesPriceDerivationRule
    /** Type code of position condition or "0000" */
    String sesRebateMethod
    /**
     * Possible values: "0" (non-loyalty trigger), external promotion ID (first 40 digits)
     */
    String sesRebateID
    /** Identifier of external offer for loyalty trigger (optional) */
    String sesExternalOfferID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a GiftCertificate element within LoyaltyReward.
 */
@ToString(includeNames = true, ignoreNulls = true)
class GiftCertificate {
    /**
     * MediaType attribute.
     * Possible values: SES:ElectronicGiftCertificate, SES:PaperGiftCertificate
     */
    String mediaType
    /** Gift card number (optional) */
    String serialNumber
    /** Gift card value */
    BigDecimal faceValue
    /** Empty string */
    String giftCertificateID = ''

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents an SES:Voucher element within LoyaltyReward.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesVoucher {
    /** Coupon number */
    String sesVoucherID
    /** Coupon amount */
    BigDecimal sesAmount
    /** Coupon serial (optional); created only if coupon serial exists */
    SesCouponSerial sesCouponSerial
}

/**
 * Represents SES:CouponSerial within SES:Voucher.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCouponSerial {
    /** The coupon serial number */
    String sesCouponSerialNumber
    /**
     * Whether the coupon redemption was successfully posted to the Couponing Service (optional).
     * Possible values: null (no service), "00" (ok), "01" (error)
     */
    String sesBookingSuccessfulTypeCode
    /** The couponing service transaction ID (optional) */
    String sesBookingTransactionUUID

    /** CST:XXCustom01 ... CST:XXCustom30 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a receipt position addon entry within SES:ReceiptPositionAddonList.
 * Corresponds to the SES:ReceiptPositionAddonList section in 4.3.6.1.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesReceiptPositionAddon {
    /** Addon position (optional) */
    String addonPos
    /** Addon name (key) */
    String key
    /** Addon text (value) */
    String value

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
