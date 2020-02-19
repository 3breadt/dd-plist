package com.dd.plist.test.model;

import com.dd.plist.annotations.PlistAlias;
import com.dd.plist.annotations.PlistOptions;

import java.util.Objects;

@PlistOptions
public class TestAppleAbstract {

    @PlistAlias("PayloadType")
    private String type;
    @PlistAlias("PayloadVersion")
    private int version;
    @PlistAlias("PayloadIdentifier")
    private String identifier;
    @PlistAlias("PayloadUUID")
    private String uuid;
    @PlistAlias("PayloadDisplayName")
    private String displayName;
    @PlistAlias("PayloadDescription")
    private String description;
    @PlistAlias("PayloadOrganization")
    private String organization;

    public TestAppleAbstract(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        TestAppleAbstract that = (TestAppleAbstract) o;
        return this.version == that.version &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.identifier, that.identifier) &&
                Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.displayName, that.displayName) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.organization, that.organization);
    }

    @Override public int hashCode() {
        return Objects.hash(this.type, this.version, this.identifier, this.uuid, this.displayName, this.description, this.organization);
    }
}
