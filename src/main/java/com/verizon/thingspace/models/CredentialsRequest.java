package com.verizon.thingspace.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;

/**
 * This is a model class for CredentialsRequest type.
 *
 * Mirrors the official VerizonLib {@code CredentialsRequest} wire format:
 *   ECPD          Enterprise Customer Profile ID
 *   accountNumber Billing Account Number
 *   items         List of devices (1-50 items)
 */
public class CredentialsRequest {
    private String eCPD;
    private String accountNumber;
    private List<DeviceCredentialRequestItem> items;

    /**
     * Default constructor.
     */
    public CredentialsRequest() {
    }

    /**
     * Initialization constructor.
     */
    public CredentialsRequest(
            String eCPD,
            String accountNumber,
            List<DeviceCredentialRequestItem> items) {
        this.eCPD = eCPD;
        this.accountNumber = accountNumber;
        this.items = items;
    }

    /**
     * Getter for ECPD.
     * Enterprise Customer Profile ID
     * @return Returns the String
     */
    @JsonGetter("ECPD")
    public String getECPD() {
        return eCPD;
    }

    /**
     * Setter for ECPD.
     * Enterprise Customer Profile ID
     */
    @JsonSetter("ECPD")
    public void setECPD(String eCPD) {
        this.eCPD = eCPD;
    }

    /**
     * Getter for AccountNumber.
     * Billing Account Number
     * @return Returns the String
     */
    @JsonGetter("accountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Setter for AccountNumber.
     * Billing Account Number
     */
    @JsonSetter("accountNumber")
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Getter for Items.
     * List of devices (1-50 items)
     * @return Returns the List of DeviceCredentialRequestItem
     */
    @JsonGetter("items")
    public List<DeviceCredentialRequestItem> getItems() {
        return items;
    }

    /**
     * Setter for Items.
     * List of devices (1-50 items)
     */
    @JsonSetter("items")
    public void setItems(List<DeviceCredentialRequestItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CredentialsRequest [" + "eCPD=" + eCPD + ", accountNumber=" + accountNumber
                + ", items=" + items + "]";
    }

    /**
     * Builds a new {@link CredentialsRequest.Builder} object.
     */
    public Builder toBuilder() {
        return new Builder(eCPD, accountNumber, items);
    }

    /**
     * Class to build instances of {@link CredentialsRequest}.
     */
    public static class Builder {
        private String eCPD;
        private String accountNumber;
        private List<DeviceCredentialRequestItem> items;

        /**
         * Initialization constructor.
         */
        public Builder() {
        }

        /**
         * Initialization constructor.
         */
        public Builder(String eCPD, String accountNumber, List<DeviceCredentialRequestItem> items) {
            this.eCPD = eCPD;
            this.accountNumber = accountNumber;
            this.items = items;
        }

        public Builder eCPD(String eCPD) {
            this.eCPD = eCPD;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder items(List<DeviceCredentialRequestItem> items) {
            this.items = items;
            return this;
        }

        public CredentialsRequest build() {
            return new CredentialsRequest(eCPD, accountNumber, items);
        }
    }
}