package com.certn.mobile.network

import java.security.cert.X509Certificate
import java.util.regex.Pattern
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * workaround for Android9 - when X509 cert. subject CN equals hostname, verify OK
 */
internal class CustomHostnameVerifier(val defaultHostnameVerifier: HostnameVerifier) :
    HostnameVerifier {
    val pattern = Pattern.compile("^.*?,CN=(.*?),.*$")
    override fun verify(hostname: String, session: SSLSession): Boolean {
        try {
            val cert = session.peerCertificates[0] as X509Certificate
            val subject = cert.subjectX500Principal.name
            val matcher = pattern.matcher(subject)
            if (matcher.matches()) {
                val cn = matcher.group(1)
                if (cn == hostname) {
                    return true
                }
            }
        } catch (_: Exception) {
        }
        return defaultHostnameVerifier.verify(hostname, session)
    }
}
