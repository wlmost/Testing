package poslog

import groovy.transform.ToString

/**
 * Represents a Sale line item in a RetailTransaction.
 * Created in case of sales transaction (no returns, no empties, no voids).
 * Corresponds to the Sale section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Sale {

    // ---- Attributes ----
    /**
     * Type of line item.
     * Possible values: Stock, Deposit, ItemCollection, SES:GiftCertificate,
     *                  SES:Inpayment, SES:Outpayment, SES:PhoneCard,
     *                  SES:RebateGiftCertificate, SES:CustomerOrder, SES:Pickup,
     *                  SES:Downpayment, SES:DownpaymentClearing, SES:InvoicePayment,
     *                  SES:Donation, SES:Other, Fee, SES:ComboMeal
     */
    String itemType

    // ---- XML elements ----
    /** POS identity (optional) */
    POSIdentity posIdentity
    /** Item ID (optional) */
    String itemID
    /** Name attribute on ItemID (optional) */
    String itemIDName
    /** Special order number: invoice number or customer order number (optional) */
    String specialOrderNumber
    /** Merchandise hierarchy group (optional) */
    String merchandiseHierarchy
    /** EPC – used to identify individual items, e.g. from RFID scanning (optional) */
    String epc
    /** Item description (optional) */
    String description
    /** true if tax is included in price; otherwise false (optional) */
    Boolean taxIncludedInPriceFlag
    /** Regular sales unit price (or package price) */
    BigDecimal regularSalesUnitPrice
    /** Currency attribute on RegularSalesUnitPrice (not created) */
    String regularSalesUnitPriceCurrency
    /** Quantity attribute on RegularSalesUnitPrice for package prices (optional) */
    BigDecimal regularSalesUnitPriceQuantity
    /** Discounted price (ExtendedAmount) */
    BigDecimal extendedAmount
    /** Quantity of item (optional) */
    BigDecimal quantity
    /** Units attribute on Quantity (optional, e.g. for length, area, weight) */
    String quantityUnits
    /** UnitOfMeasureCode attribute on Quantity (optional) */
    String quantityUnitOfMeasureCode
    /** Seller information (optional) */
    Associate associate
    /** Price modifications (optional) */
    List<RetailPriceModifier> retailPriceModifiers = []
    /** Line item related tax (optional) */
    Tax tax
    /** Serial number or gift certificate number (optional) */
    String serialNumber
    /** Transaction link for food order or order quantity change (optional) */
    TransactionLink transactionLink
    /** Discounted price of position */
    BigDecimal sesExtendedPositionAmount
    /** true if generic article number (optional) */
    Boolean sesMerchandiseStructureItemFlag
    /** Link to empties item (optional) */
    SesItemLink sesItemLink
    /** Supplier ID of concessionaire (optional) */
    String sesConcessionSupplierID
    /** Special offer number (optional) */
    String sesSpecialOfferNumber
    /** false if blocking period was discontinued by operator (optional) */
    Boolean sesAuthorizedForSalesFlag
    /** "1" if price was revaluated (optional) */
    String sesPriceOrigin
    /** Position specific bonus points (optional) */
    SesLoyaltyReward sesLoyaltyReward
    /** External receipt reference list (optional) */
    List<SesExternalReceiptReference> sesExternalReceiptReferenceList = []
    /** Invoice number for bill payment on POS (optional) */
    String sesInvoiceNumber
    /** Reason code for paid in/out (optional) */
    String sesReasonCode
    /** ReasonType attribute on SES:ReasonCode – fix value "InOutpayment" (optional) */
    String sesReasonType
    /** Additional data for gift certificate line items (optional) */
    SesGiftCertificateData sesGiftCertificateData
    /** Additional data for sales order line items (optional) */
    SesSalesOrderData sesSalesOrderData
    /** Identifier of the price group for split pricing (optional) */
    String sesPriceGroupID

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a Return line item in a RetailTransaction.
 * Shares the same structure as Sale with minor deviations.
 * Corresponds to the Return section in 4.3.6.1 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Return extends Sale {
    /**
     * Reference to the original sales transaction and line item (optional).
     * Note: ReasonCode fix value = "Return"
     */
    TransactionLink returnTransactionLink
    /**
     * Return reason code (optional). Default: "0000".
     * ReasonType attribute fix value: "Return"
     */
    String sesReturnReasonCode
}

/**
 * Represents a POSIdentity element within Sale.
 */
@ToString(includeNames = true, ignoreNulls = true)
class POSIdentity {
    /**
     * POSIDType attribute.
     * Fix values: RegistrationNumber, MasterData
     */
    String posIDType
    /**
     * POSIDType="RegistrationNumber": on-POS entered registration number.
     * POSIDType="MasterData": main POS item ID.
     */
    String posItemID
    /**
     * Qualifier (optional); created only for magazine/newspaper line item.
     * Value: registration number from digit 14 to end.
     */
    String qualifier
}

/**
 * Represents an Associate (seller) element within Sale.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Associate {
    /** Seller ID */
    String associateID
    /** OperatorName attribute (optional) */
    String operatorName
    /** WorkerID attribute (optional, not created) */
    String workerID
}

/**
 * Represents a RetailPriceModifier element within Sale.
 */
@ToString(includeNames = true, ignoreNulls = true)
class RetailPriceModifier {
    /** Continuous number over all price modifications of one position, starting with "1" */
    String sequenceNumber
    /** Absolute amount of price modification */
    BigDecimal amount
    /**
     * Action attribute on Amount.
     * Possible values: Add, Substract
     */
    String amountAction
    /** Description of position condition (optional) */
    String promotionID
    /** Price derivation rule data (optional) */
    PriceDerivationRule priceDerivationRule
    /** Reason of corresponding price modification (default "0000") */
    String reasonCode
    /**
     * Type code for position condition, property value for price change.
     * Otherwise "0000"
     */
    String sesRebateMethod
    /**
     * External promotion ID for position condition.
     * Property value for price change. Otherwise "0"
     */
    String sesRebateID
    /** Discount percentage (optional; only for promotions with percentage rule) */
    BigDecimal sesPercent
    /** Identifier of the external offer for position condition (optional) */
    String sesExternalOfferID
    /** Price type code of the additional applied price for customer-specific prices (optional) */
    String sesAdditionalPriceTypeCode

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a PriceDerivationRule element within RetailPriceModifier or LoyaltyReward.
 */
@ToString(includeNames = true, ignoreNulls = true)
class PriceDerivationRule {
    /** First 40 digits of price derivation rule ID */
    String priceDerivationRuleID
    /** Eligibility (optional); created only if coupon number exists */
    Eligibility eligibility
    /** Amount */
    BigDecimal amount
    /**
     * Action attribute on Amount.
     * Possible values: Substract (negative amount), Add (positive amount)
     */
    String amountAction
    /** Origin (optional) – "ORIGIN" in case of loyalty trigger */
    String sesOrigin
    /** Applied quantity (optional) */
    BigDecimal sesAppliedQuantity
    /** Rule description (optional) */
    String sesRuleDescription

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents an Eligibility element within PriceDerivationRule.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Eligibility {
    /** Coupon reference number (optional) */
    String referenceID
    /** Type attribute on ReferenceID – fix value "StoreCoupon" (optional) */
    String referenceIDType

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:ItemLink within Sale (link to empties item).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesItemLink {
    /** LinkType attribute */
    String linkType
}

/**
 * Represents SES:LoyaltyReward within Sale (position-specific bonus points).
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesLoyaltyReward {
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
    /** External promotion ID or "0" */
    String sesRebateID
    /** Identifier of external offer for loyalty trigger (optional) */
    String sesExternalOfferID

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:GiftCertificateData within Sale (additional gift certificate data).
 * Corresponds to the SES:GiftCertificateData section in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesGiftCertificateData {
    /**
     * Gift certificate type (optional).
     * Possible values: SES:PaperGiftCertificate, SES:ElectronicGiftCertificate
     */
    String sesGiftCertificateType

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:SalesOrderData within Sale (additional sales order data).
 * Corresponds to the SES:SalesOrderData section in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesSalesOrderData {
    /**
     * Special order system (optional).
     * Possible values: ERP, Store
     */
    String sesSpecialOrderSystem
    /**
     * Special order type (optional).
     * Possible values: Reservation, SalesSelection, Pickup, Delivery,
     *                  ImmediatePickup, SES:Layaway
     */
    String sesSpecialOrderType
    /** External position numbers of customer order (optional) */
    String sesSpecialOrderPositionNumber

    /** CST:XXCustom01 ... CST:XXCustom15 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents a single entry in SES:ExternalReceiptReferenceList within Sale.
 * Corresponds to the SES:ExternalReceiptReferenceList section in the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesExternalReceiptReference {
    /**
     * Item origin (optional).
     * Possible values: 06 (offline rebooking of suspended position), 05 (empties),
     *                  04 (scale), otherwise type code of line item association
     *                  (e.g. SUSP, RETU, LIRE, DEPO)
     */
    String sesItemOrigin
    /** Reference to external document (invoice, customer order, etc.) */
    String sesReceiptReferenceNumber
    /** External transaction offline redemption flag (optional) */
    Boolean sesExternalTransactionOfflineRedemptionFlag

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}
