package com.gitee.starblues.core.launcher.jar;

import java.security.CodeSigner;
import java.security.cert.Certificate;

/**
 * @author starBlues
 * @version 1.0
 */
public class JarEntryCertification {

    static final JarEntryCertification NONE = new JarEntryCertification(null, null);

    private final Certificate[] certificates;

    private final CodeSigner[] codeSigners;

    JarEntryCertification(Certificate[] certificates, CodeSigner[] codeSigners) {
        this.certificates = certificates;
        this.codeSigners = codeSigners;
    }

    Certificate[] getCertificates() {
        return (this.certificates != null) ? this.certificates.clone() : null;
    }

    CodeSigner[] getCodeSigners() {
        return (this.codeSigners != null) ? this.codeSigners.clone() : null;
    }

    static JarEntryCertification from(java.util.jar.JarEntry certifiedEntry) {
        Certificate[] certificates = (certifiedEntry != null) ? certifiedEntry.getCertificates() : null;
        CodeSigner[] codeSigners = (certifiedEntry != null) ? certifiedEntry.getCodeSigners() : null;
        if (certificates == null && codeSigners == null) {
            return NONE;
        }
        return new JarEntryCertification(certificates, codeSigners);
    }


}
