package poslog

import groovy.transform.ToString

/**
 * Represents Customer data within RetailTransaction.
 * Corresponds to section 4.3.6.3 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Customer {

    /** Customer ID (optional) */
    String customerID
    /** Customer name container (optional) */
    CustomerName customerName
    /** Customer address container (optional) */
    Address address
    /** Customer telephone container (optional) */
    Telephone telephone
    /** Customer email address container (optional) */
    Email email
    /** Customer birthdate (optional) */
    String birthdate
    /** Customer gender (optional) */
    String gender
    /**
     * Customer type.
     * Possible values: "EM" for employees, otherwise address type
     */
    String sesCustomerType
    /** Address type description (optional) */
    String sesCustomerTypeDescription
    /** Customer identifier, created only if address type exists (optional) */
    String sesCustomerIdentifier
    /**
     * Entry method (optional; not created for employee customers).
     * Possible values: Scanned, SES:Searched, Keyed
     */
    String sesEntryMethod
    /** Customer locking type code (optional) */
    String sesCustomerLockingTypeCode
    /** Customer tax ID (optional) */
    String sesCustomerTaxID
    /** Customer generic flag (optional) */
    String sesCustomerGenericFlag
    /** Customer business number (optional) */
    String sesCustomerBusinessNumber
    /** Customer location number (optional) */
    String sesCustomerLocationNumber
    /** Customer requisition required flag (optional, XML element: SES:CustomerRequisationRequired) */
    Boolean sesCustomerRequisitionRequired
    /** Customer buyer required flag (optional) */
    Boolean sesCustomerBuyerRequiredFlag
    /** Customer buyer name (optional) */
    String sesCustomerBuyerName
    /** Customer contact required flag (optional) */
    Boolean sesCustomerContactRequiredFlag
    /** Customer contact person name (optional) */
    String sesCustomerContactPersonName
    /** Customer group (optional) */
    String sesCustomerGroup
    /** Customer group default bonus points count (optional) */
    Integer sesCustomerGroupDefaultBonusPointsCount
    /** Parent customer ID (optional) */
    String sesParentCustomerID
    /** First name line of parent customer (optional) */
    String sesParentCustomerName1
    /** Second name line of parent customer (optional) */
    String sesParentCustomerName2

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents customer name data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class CustomerName {
    /** Salutation of customer (optional) */
    String salutation
    /** Customer first name (Location = "First", optional) */
    String firstName
    /** Customer last name (Location = "Last", optional) */
    String lastName
    /** Full name string – max. both customer name entries or organization name */
    String fullName
}

/**
 * Represents customer address data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Address {
    /**
     * Address line with TypeCode attribute (optional).
     * TypeCode possible values: Street, SES:District, SES:AdressLine1, SES:AdressLine2
     * (Note: 'AdressLine' is intentionally kept as-is to match the GK spec XML values.)
     */
    String addressLine
    /** TypeCode attribute on AddressLine (optional) */
    String addressLineTypeCode
    /** City (optional) */
    String city
    /** Postal code (optional) */
    String postalCode
    /** Country (optional) */
    String country
    /** Name of organisation (optional) */
    String name
}

/**
 * Represents customer telephone data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Telephone {
    /**
     * TypeCode attribute (optional).
     * Possible values: Work, Mobile, Home, WorkFax
     */
    String typeCode
    /** Full telephone number */
    String fullTelephoneNumber
}

/**
 * Represents customer email address data within Customer.
 */
@ToString(includeNames = true, ignoreNulls = true)
class Email {
    /** Email address */
    String emailAddress
}

/**
 * Represents SES:CouponSummary within RetailTransaction.
 * Corresponds to section 4.3.6.5 of the GK POSLog Structure v3.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCouponSummary {
    /** The coupon number */
    String sesCouponNumber
    /** Count of registered coupons */
    Integer sesInputCount
    /** Number of used coupons (optional) */
    Integer sesAppliedCount
    /** Identifier of the customer (optional) */
    String sesCustomerID
    /** The customer type code (optional) */
    String sesCustomerAddressTypeCode
    /** Coupon serial summaries (optional) */
    List<SesCouponSerialSummary> sesCouponSerialSummaries = []

    /** CST:XXCustom01 ... CST:XXCustom05 fields (optional) */
    Map<String, String> customFields = [:]
}

/**
 * Represents SES:CouponSerialSummary within SES:CouponSummary.
 */
@ToString(includeNames = true, ignoreNulls = true)
class SesCouponSerialSummary {
    /** The coupon serial number */
    String sesCouponSerialNumber
    /**
     * Whether the coupon redemption was successfully posted to the Couponing Service (optional).
     * Possible values: null (no service), "00" (ok), "01" (error)
     */
    String sesBookingSuccessfulTypeCode
    /** The couponing service transaction ID (optional) */
    String sesBookingTransactionUUID
    /** Defines if the coupon serial triggered a promotion price derivation rule (optional) */
    Boolean sesUsedFlag

    /** CST:XXCustom01 ... CST:XXCustom30 fields (optional) */
    Map<String, String> customFields = [:]
}
