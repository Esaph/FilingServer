/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package config;

public class FilingConfig
{
    private int mMaxRequestLength;
    private int mRequestThreadSize;
    private int mRequestSubThreadSize;
    private String mServerName;
    private int mPort;
    private String mSSLKeystoreFilePath;
    private String mSSLTrustStoreFilePath;
    private String mSSLKeystoreFilePassword;
    private String mSSLTrustStoreFilePassword;

    public FilingConfig(int mMaxRequestLength,
                        int mRequestThreadSize,
                        int mRequestSubThreadSize,
                        String mServerName,
                        int mPort,
                        String mSSLKeystoreFilePath,
                        String mSSLTrustStoreFilePath,
                        String mSSLKeystoreFilePassword,
                        String mSSLTrustStoreFilePassword)
    {
        this.mMaxRequestLength = mMaxRequestLength;
        this.mRequestThreadSize = mRequestThreadSize;
        this.mRequestSubThreadSize = mRequestSubThreadSize;
        this.mServerName = mServerName;
        this.mPort = mPort;
        this.mSSLKeystoreFilePath = mSSLKeystoreFilePath;
        this.mSSLTrustStoreFilePath = mSSLTrustStoreFilePath;
        this.mSSLKeystoreFilePassword = mSSLKeystoreFilePassword;
        this.mSSLTrustStoreFilePassword = mSSLTrustStoreFilePassword;
    }

    public String getmSSLKeystoreFilePassword() {
        return mSSLKeystoreFilePassword;
    }

    public void setmSSLKeystoreFilePassword(String mSSLKeystoreFilePassword) {
        this.mSSLKeystoreFilePassword = mSSLKeystoreFilePassword;
    }

    public String getmSSLTrustStoreFilePassword() {
        return mSSLTrustStoreFilePassword;
    }

    public void setmSSLTrustStoreFilePassword(String mSSLTrustStoreFilePassword) {
        this.mSSLTrustStoreFilePassword = mSSLTrustStoreFilePassword;
    }

    public int getmMaxRequestLength() {
        return mMaxRequestLength;
    }

    public void setmMaxRequestLength(int mMaxRequestLength) {
        this.mMaxRequestLength = mMaxRequestLength;
    }

    public int getmRequestThreadSize() {
        return mRequestThreadSize;
    }

    public void setmRequestThreadSize(int mRequestThreadSize) {
        this.mRequestThreadSize = mRequestThreadSize;
    }

    public int getmRequestSubThreadSize() {
        return mRequestSubThreadSize;
    }

    public void setmRequestSubThreadSize(int mRequestSubThreadSize) {
        this.mRequestSubThreadSize = mRequestSubThreadSize;
    }

    public String getmServerName() {
        return mServerName;
    }

    public void setmServerName(String mServerName) {
        this.mServerName = mServerName;
    }

    public int getmPort() {
        return mPort;
    }

    public void setmPort(int mPort) {
        this.mPort = mPort;
    }

    public String getmSSLKeystoreFilePath() {
        return mSSLKeystoreFilePath;
    }

    public void setmSSLKeystoreFilePath(String mSSLKeystoreFilePath) {
        this.mSSLKeystoreFilePath = mSSLKeystoreFilePath;
    }

    public String getmSSLTrustStoreFilePath() {
        return mSSLTrustStoreFilePath;
    }

    public void setmSSLTrustStoreFilePath(String mSSLTrustStoreFilePath) {
        this.mSSLTrustStoreFilePath = mSSLTrustStoreFilePath;
    }
}
