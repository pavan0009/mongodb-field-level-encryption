package com.micro.secureapp.config;

import java.util.Base64;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.micro.secureapp.crypt.CryptVault;
import com.micro.secureapp.secure.AbstractEncryptionEventListener;
import com.micro.secureapp.secure.CachedEncryptionEventListener;
import com.micro.secureapp.secure.ReflectionEncryptionEventListener;

@Configuration
@ConditionalOnProperty("mongodb.encrypt.keys[0].key")
public class EncryptAutoConfiguration {

    @Bean
    CryptVault cryptVault(EncryptConfigurationProperties properties) {
        CryptVault cryptVault = new CryptVault();
        if (properties.keys == null || properties.keys.isEmpty()) throw new IllegalArgumentException("mongodb.encrypt.keys is empty");

        for (Key key : properties.keys) {
            byte[] secretKeyBytes = Base64.getDecoder().decode(key.key);
            cryptVault.with256BitAesCbcPkcs5PaddingAnd128BitSaltKey(key.version, secretKeyBytes);
        }

        if (properties.defaultKey != null) {
            cryptVault.withDefaultKeyVersion(properties.defaultKey);
        }

        return cryptVault;
    }

    @Bean
    AbstractEncryptionEventListener encryptionEventListener(CryptVault cryptVault, EncryptConfigurationProperties properties) {
        AbstractEncryptionEventListener eventListener;
        if ("reflection".equalsIgnoreCase(properties.type)) {
            eventListener = new ReflectionEncryptionEventListener(cryptVault);
        } else {
            eventListener = new CachedEncryptionEventListener(cryptVault);
        }

        if (properties.silentDecryptionFailures == Boolean.TRUE) eventListener.withSilentDecryptionFailure(true);

        return eventListener;
    }

    @Component
    @ConfigurationProperties("mongodb.encrypt")
    public static class EncryptConfigurationProperties {
        List<Key> keys;
        Integer defaultKey;
        String type;
        Boolean silentDecryptionFailures;

        public void setKeys(List<Key> keys) {
            this.keys = keys;
        }

        public void setDefaultKey(Integer defaultKey) {
            this.defaultKey = defaultKey;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setSilentDecryptionFailures(Boolean silentDecryptionFailures) {
            this.silentDecryptionFailures = silentDecryptionFailures;
        }
    }

    public static class Key {
        int version;
        String key;

        public void setVersion(int version) {
            this.version = version;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
