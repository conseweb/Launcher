package com.nd.hilauncherdev.webconnect.downloadmanage.model ;

interface IPluginDownloadForwardService {

	/**
	 * 判断服务是否活动中
	*/
	boolean isServiceAlive();
	/**
	 * 获取下载的BaseDownloadInfo信息--返回json格式
	 */
	String getData(String identification);
	/**
	 * 获取所有的download tasks信息--返回json格式
	 */
	String getDownloadTasks();

	/**
     * 获取指定的download task信息--返回json格式
    */
    String getDownloadTaskById(String Id);
}