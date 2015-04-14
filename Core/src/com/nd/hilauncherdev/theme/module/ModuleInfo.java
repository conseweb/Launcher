package com.nd.hilauncherdev.theme.module;

/**
 * <br>
 * Description: 单独模块信息 <br>
 * Author:caizp <br>
 * Date:2014-6-12上午11:08:19
 */
public class ModuleInfo {

	/**
	 * 服务端拆分模块包时写入，由所属主题ID与标识组成。存放目录由此ID构成
	 */
	private String moduleId;
	/**
	 * 模块KEY
	 */
	private String moduleKey;
	/**
	 * 模块包中文名称
	 */
	private String name;
	/**
	 * 模块包英文名称
	 */
	private String enName;
	/**
	 * 模块版本名称
	 */
	private String versionName;
	/**
	 * 模块版本号
	 */
	private int versionCode;
	/**
	 * 资源是否已被加密
	 */
	private boolean guarded = false;
	/**
	 * 资源加密算法版本号
	 */
	private int guardedVersion = 1;
	/**
	 * 模块安装时间
	 */
	private long installTime;
	/**
	 * 服务端定义的资源类型(用于升级)
	 */
	private int resType = 0;
	/**
     * 主题支持的桌面最低版本(该版本号大于桌面版本号时，表示桌面版本过低，不支持该主题)
     */
    private int launcherMinVersion = 5998;
    /**
     * 是否有新版本
     */
    private boolean hasNewVersion = false;
    /**
	 * 模块分类
	 */
	private String moduleCategory;
    
	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleKey() {
		return moduleKey;
	}

	public void setModuleKey(String moduleKey) {
		this.moduleKey = moduleKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public boolean isGuarded() {
		return guarded;
	}

	public void setGuarded(boolean guarded) {
		this.guarded = guarded;
	}

	public int getGuardedVersion() {
		return guardedVersion;
	}

	public void setGuardedVersion(int guardedVersion) {
		this.guardedVersion = guardedVersion;
	}

	public long getInstallTime() {
		return installTime;
	}

	public void setInstallTime(long installTime) {
		this.installTime = installTime;
	}

	public int getResType() {
		return resType;
	}

	public void setResType(int resType) {
		this.resType = resType;
	}

	public int getLauncherMinVersion() {
		return launcherMinVersion;
	}

	public void setLauncherMinVersion(int launcherMinVersion) {
		this.launcherMinVersion = launcherMinVersion;
	}

	public boolean hasNewVersion() {
		return hasNewVersion;
	}

	public void setHasNewVersion(boolean hasNewVersion) {
		this.hasNewVersion = hasNewVersion;
	}

	public String getModuleCategory() {
		return moduleCategory;
	}

	public void setModuleCategory(String moduleCategory) {
		this.moduleCategory = moduleCategory;
	}

}
