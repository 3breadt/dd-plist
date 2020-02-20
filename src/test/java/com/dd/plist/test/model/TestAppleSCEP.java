package com.dd.plist.test.model;

import com.dd.plist.annotations.PlistAlias;
import com.dd.plist.annotations.PlistIgnore;
import com.dd.plist.annotations.PlistInclude;
import com.dd.plist.annotations.PlistOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@PlistOptions(upperCamelCase = true)
public class TestAppleSCEP extends TestAppleAbstract {

    private static String ignoredStatic = "ignoredStatic";

    private transient String ignoredTransient = "ignoredTransient";

    @PlistIgnore
    private String ignored = "ignored";

    private String emptyTextIncluded = "";

    @PlistInclude(PlistInclude.Include.NON_EMPTY)
    private String emptyText = "";

    @PlistInclude(PlistInclude.Include.NON_EMPTY)
    private byte[] emptyArray = new byte[]{};

    private byte[] emptyArrayIncluded = new byte[]{};

    @PlistInclude(PlistInclude.Include.NON_NULL)
    private Integer nullInt = null;

    @PlistAlias("IsPresent")
    private Boolean isPresent = null;

    private boolean presentIs = false;

    private TestAppleSCEPContent payloadContent = new TestAppleSCEPContent();

    public TestAppleSCEP() {
        super("com.apple.security.scep");
    }

    public Boolean getPresent() {
        return this.isPresent;
    }

    public void setPresent(Boolean present) {
        this.isPresent = present;
    }

    public boolean isPresentIs() {
        return this.presentIs;
    }

    public void setPresentIs(boolean presentIs) {
        this.presentIs = presentIs;
    }

    public TestAppleSCEPContent getPayloadContent() {
        return this.payloadContent;
    }

    public void setPayloadContent(TestAppleSCEPContent payloadContent) {
        this.payloadContent = payloadContent;
    }

    @PlistOptions(upperCamelCase = true)
    public static class TestAppleSCEPContent {
        private boolean allowAllAppsAccess = false;
        @PlistAlias("CAFingerprint")
        private byte[] caFingerprint = new byte[]{};
        private String challenge = "";
        @PlistAlias("Key Type")
        private String keyType = "RSA";
        @PlistAlias("Key Usage")
        private int keyUsage = 0;
        @PlistAlias("Keysize")
        private int keySize = 0;
        private int retries = 3;
        private List<List<List<String>>> subject = new ArrayList<>();

        public boolean isAllowAllAppsAccess() {
            return this.allowAllAppsAccess;
        }

        public void setAllowAllAppsAccess(boolean allowAllAppsAccess) {
            this.allowAllAppsAccess = allowAllAppsAccess;
        }

        public byte[] getCaFingerprint() {
            return this.caFingerprint;
        }

        public void setCaFingerprint(byte[] caFingerprint) {
            this.caFingerprint = caFingerprint;
        }

        public String getChallenge() {
            return this.challenge;
        }

        public void setChallenge(String challenge) {
            this.challenge = challenge;
        }

        public String getKeyType() {
            return this.keyType;
        }

        public void setKeyType(String keyType) {
            this.keyType = keyType;
        }

        public int getKeyUsage() {
            return this.keyUsage;
        }

        public void setKeyUsage(int keyUsage) {
            this.keyUsage = keyUsage;
        }

        public int getKeySize() {
            return this.keySize;
        }

        public void setKeySize(int keySize) {
            this.keySize = keySize;
        }

        public int getRetries() {
            return this.retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }

        public List<List<List<String>>> getSubject() {
            return this.subject;
        }

        public void setSubject(List<List<List<String>>> subject) {
            this.subject = subject;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            TestAppleSCEPContent that = (TestAppleSCEPContent) o;
            return this.allowAllAppsAccess == that.allowAllAppsAccess &&
                    this.keyUsage == that.keyUsage &&
                    this.keySize == that.keySize &&
                    this.retries == that.retries &&
                    Arrays.equals(this.caFingerprint, that.caFingerprint) &&
                    Objects.equals(this.challenge, that.challenge) &&
                    Objects.equals(this.keyType, that.keyType) &&
                    Objects.equals(this.subject, that.subject);
        }

        @Override public int hashCode() {
            int result = Objects.hash(this.allowAllAppsAccess, this.challenge, this.keyType, this.keyUsage, this.keySize, this.retries, this.subject);
            result = 31 * result + Arrays.hashCode(this.caFingerprint);
            return result;
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TestAppleSCEP that = (TestAppleSCEP) o;
        return this.presentIs == that.presentIs &&
                Objects.equals(this.ignoredTransient, that.ignoredTransient) &&
                Objects.equals(this.ignored, that.ignored) &&
                Objects.equals(this.emptyTextIncluded, that.emptyTextIncluded) &&
                Objects.equals(this.emptyText, that.emptyText) &&
                Arrays.equals(this.emptyArray, that.emptyArray) &&
                Arrays.equals(this.emptyArrayIncluded, that.emptyArrayIncluded) &&
                Objects.equals(this.nullInt, that.nullInt) &&
                Objects.equals(this.isPresent, that.isPresent) &&
                Objects.equals(this.payloadContent, that.payloadContent);
    }

    @Override public int hashCode() {
        int result = Objects.hash(super.hashCode(), this.ignoredTransient, this.ignored, this.emptyTextIncluded, this.emptyText, this.nullInt, this.isPresent, this.presentIs, this.payloadContent);
        result = 31 * result + Arrays.hashCode(this.emptyArray);
        result = 31 * result + Arrays.hashCode(this.emptyArrayIncluded);
        return result;
    }
}
