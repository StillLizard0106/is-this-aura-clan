package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "firebase.admin")
public class FirebaseAdminProperties {

	private boolean enabled;
	private String credentialsPath;
	/**
	 * Optional raw JSON credentials or base64-encoded JSON. If provided, this takes priority over path.
	 */
	private String credentialsJson;
	private String projectId;

	/** Optional: allowed email domain for student registrations (e.g. school.edu). If set, new student accounts require this domain. */
	private String allowedEmailDomain;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCredentialsPath() {
		return credentialsPath;
	}

	public void setCredentialsPath(String credentialsPath) {
		this.credentialsPath = credentialsPath;
	}

	public String getCredentialsJson() {
		return credentialsJson;
	}

	public void setCredentialsJson(String credentialsJson) {
		this.credentialsJson = credentialsJson;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getAllowedEmailDomain() {
		return allowedEmailDomain;
	}

	public void setAllowedEmailDomain(String allowedEmailDomain) {
		this.allowedEmailDomain = allowedEmailDomain;
	}
}
