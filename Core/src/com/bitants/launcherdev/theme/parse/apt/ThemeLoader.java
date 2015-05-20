package com.bitants.launcherdev.theme.parse.apt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bitants.launcherdev.framework.exception.ErrorCode;
import com.bitants.launcherdev.framework.exception.PandaDesktopException;
import com.bitants.launcherdev.kitset.util.FileUtil;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import com.bitants.launcherdev.kitset.util.ThreadUtil;
import com.bitants.launcherdev.kitset.util.ZipUtil;
import com.bitants.launcherdev.kitset.xmlparser.Element;
import com.bitants.launcherdev.kitset.xmlparser.XmlParser;
import com.bitants.launcherdev.kitset.xmlparser.exception.XmlFormatErrorException;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.theme.ThemeManagerFactory;
import com.bitants.launcherdev.theme.data.BasePandaTheme;
import com.bitants.launcherdev.theme.data.BaseThemeData;
import com.bitants.launcherdev.theme.data.ThemeGlobal;
import com.bitants.launcherdev.theme.module.ModuleConstant;
import com.bitants.launcherdev.theme.module.ModuleInfo;
import com.bitants.launcherdev.theme.module.ThemeModuleHelper;
import com.bitants.launcherdev.theme.parse.EncodeTools;

/**
 * <br>Description: apt主题包解析装载
 * <br>Author:caizp
 * <br>Date:2014-4-1下午4:59:51
 */
public class ThemeLoader {
	
	/** 日志标志。 */
    public static final String LOG_TAG = "BaseThemeLoader";

    /**
     * 主题XML名称 
     */
    public static final String themeXmlName = "panda_theme.xml";

    /**
     * namePrefix
     */
    private String namePrefix = "";

    /**
     * root 根元素
     */
    private Element root;

    /**
     * 主题xml内部配置头部节点
     */
    private static final String PANDA_THEME = "theme-config";

    /**
     * 主题xml内部配置workspace节点
     */
    private static final String PANDA_WORKSPACE_THEME = "workspace";
    
    /**
     * 图标文字颜色节点
     */
    private static final String PANDA_TEXTCOLOR = "textcolor";

    /**
     * 图标文字大小节点
     */
    private static final String PANDA_TEXTSIZE = "textsize";
    
    /**
     * 主题xml内部配置keyconfig节点
     */
    private static final String PANDA_ICON_CONFIG = "keyconfig";

    /**
     * 主题中文名称节点
     */
    private static final String PANDA_THEME_NAME = "name";

    /**
     * 主题英文名称节点
     */
    private static final String PANDA_THEME_NAME_EN = "en_name";

    /**
     * 主题intent节点
     */
    private static final String PANDA_ICON_INTENT = "intent";

    /**
     * 主题text节点
     */
    private static final String PANDA_ICON_TEXT = "text";

    /**
     * elementTag
     */
    private String elementTag = "";

    /**
     * parentTag
     */
    private String parentTag = "";

    /**
     * elementValue
     */
    private String elementValue = "";
    
    private static ThemeLoader loader;
    
    public static ThemeLoader getInstance() {
    	if(null == loader) {
    		loader = new ThemeLoader();
    	}
    	return loader;
    }

    /**
     * 载入主题包主配置文件组装主题对象
     * 
     * @param themePath
     *            主题配置文件路径。
     * @return 主题对象。
     */
    private void loaderThemeXML(String themePath, BasePandaTheme basePandaThemeObj) throws PandaDesktopException {
        try {
            root = XmlParser.buildXmlRootByString(StringUtil.renameRes(readXml(themePath)));
            createThemeObj(root, basePandaThemeObj);
        } catch (XmlFormatErrorException e) {
            Log.w(LOG_TAG, "Xml format error. " + e);
            throw new PandaDesktopException(ErrorCode.XML_FORMAT_ERROR_CODE);
        } catch (PandaDesktopException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 载入模块包主配置文件组装主题对象
     * 
     * @param themePath
     *            主题配置文件路径。
     * @return 主题对象。
     */
    private void loaderThemeXML(String themePath, ModuleInfo moduleInfo) throws PandaDesktopException {
        try {
        	if(null == moduleInfo)return;
            root = XmlParser.buildXmlRootByString(StringUtil.renameRes(readXml(themePath)));
            if (null != root) {
                Map<String, String> attrMap = root.getAttributes();
                elementTag = root.getName();
                elementValue = root.getValue();
                if (!TextUtils.isEmpty(elementTag)) {
                    elementTag = cutPrefix(elementTag);
                }

                if (null != root.getParent()) {
                    parentTag = root.getParent().getName();
                    if (!TextUtils.isEmpty(parentTag)) {
                        parentTag = cutPrefix(parentTag);
                    }
                }
                if (PANDA_THEME.equalsIgnoreCase(elementTag)) {
                    //读取主题名称
                    String name = attrMap.get(PANDA_THEME_NAME);
                    String en_name = attrMap.get(PANDA_THEME_NAME_EN);
                    if ((null == name) && (null == en_name)) {
                    	Log.w(LOG_TAG, "name attr is not found!");
                        throw new PandaDesktopException(ErrorCode.XML_FORMAT_NAME_UNFOUND_ERROR);
                    }
                    if (null != name) {
                    	moduleInfo.setName(name);
                    }
                    if (null != en_name) {
                    	moduleInfo.setEnName(en_name);
                    }
                    //读取主题versionName
                    String version = attrMap.get("ver");
                    if (!TextUtils.isEmpty(version)) {
                    	moduleInfo.setVersionName(version);
                    }else{
                    	moduleInfo.setVersionName("1");
                    }
                    //读取主题versionCode
                    String versionCode = attrMap.get("version");
                    if (!TextUtils.isEmpty(versionCode)) {
                    	try {
                    		moduleInfo.setVersionCode(Integer.valueOf(versionCode));
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    		moduleInfo.setVersionCode(1);
                    	}
                    }else{
                    	moduleInfo.setVersionCode(1);
                    }
                    //读取主题ID
                    String idFlag = attrMap.get("id_flag");
                    if (!TextUtils.isEmpty(idFlag)){
                    	moduleInfo.setModuleId(idFlag);
                    }else{
                    	moduleInfo.setModuleId(en_name);
                    }
                    String guarded = attrMap.get("guarded");
                    if(!TextUtils.isEmpty(guarded)){
                    	moduleInfo.setGuarded("true".equals(guarded));
                    }
                    String guardedVersion = attrMap.get("guarded_version");
                    if(!TextUtils.isEmpty(guardedVersion)){
                    	try {
                    		moduleInfo.setGuardedVersion(Integer.valueOf(guardedVersion));
                    	} catch(Exception e) {
                    		e.printStackTrace();
                    	}
                    }
                    String resType = attrMap.get("ResType");
                    if(!TextUtils.isEmpty(resType)){
                    	try {
                    		moduleInfo.setResType(Integer.valueOf(resType));
                    	} catch(Exception e) {
                    		e.printStackTrace();
                    	}
                    }
                    String launcherMinVersion = attrMap.get("launcher_min_version");
                    if(!TextUtils.isEmpty(launcherMinVersion)){
                    	try {
                    		moduleInfo.setLauncherMinVersion(Integer.valueOf(launcherMinVersion));
                    	} catch(Exception e) {
                    		e.printStackTrace();
                    	}
                    }
                }
            }
        } catch (XmlFormatErrorException e) {
            Log.w(LOG_TAG, "Xml format error. " + e);
            throw new PandaDesktopException(ErrorCode.XML_FORMAT_ERROR_CODE);
        } catch (PandaDesktopException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 解压ZIP文件到sdcard/PandaHome2/Themes并生成主题对象。
     * 
     * @param zipPath
     *            zip文件路径。
     * @return 主题对象。
     * @throws Exception
     *             SDcard是否存在异常。
     */
    public synchronized BasePandaTheme loaderThemeZipFromAssets(Context ctx, String zipPath) throws PandaDesktopException {
    	BasePandaTheme basePandaTheme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatPandaThemeObj();
        namePrefix = createTimeZone();
        String unZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
        if (!TelephoneUtil.isSdcardExist()) {
        	Log.w(LOG_TAG, "sdcard is not found!");
            throw new PandaDesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
        }
        ZipUtil.ectract( ctx, zipPath, unZipPath, false);
        try {
        	//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new PandaDesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
            loaderThemeXML(unZipPath + themeXmlName, basePandaTheme);
            //根据主题Id修改解压后的主题包路径
            namePrefix = basePandaTheme.getIDFlag().replace(" ", "_");
            basePandaTheme.setThemeId(basePandaTheme.getIDFlag());
            basePandaTheme.setIDFlag(basePandaTheme.getIDFlag());
            String newUnZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
            basePandaTheme.setAptPath(namePrefix + "/");
            
            // 把原来的文件夹重命名，便于删除
            final String delFilePath = BaseConfig.THEME_DIR + namePrefix + "_" + System.currentTimeMillis() + "/";
			File f = new File(newUnZipPath);
			if (f.exists()) {
				FileUtil.renameFile(newUnZipPath, delFilePath);
			}
			if (!FileUtil.renameFile(unZipPath, newUnZipPath)) {
				// 重全名失败，恢复原来的文件夹
				FileUtil.renameFile(delFilePath, newUnZipPath);
				throw new PandaDesktopException(ErrorCode.FOLDER_RENAME_ERROR);
			} else {
				// 重全名成功，删除原来的文件夹
				if(new File(delFilePath).exists()) {
					ThreadUtil.executeMore(new Runnable() {
						@Override
						public void run() {
							FileUtil.delFolder(delFilePath);
						}
					});
				}
			}
            unZipPath = newUnZipPath;
            //解压天气皮肤包  add by caizp 2011-09-02
            loadThemeWeather(unZipPath, basePandaTheme.getThemeId());
            // 解压第三方小插件皮肤包 add by caizp 2013-03-11
         	loadThemeWidget(unZipPath, basePandaTheme.getThemeId());
         	// 重命名旧主题目录文件 caizp 2014-6-27
         	renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_DIR);
			renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_XHDPI_DIR);
        } catch (PandaDesktopException pe) {
        	FileUtil.delFolder(unZipPath);
            throw pe;
        } catch (Exception e) {
            Log.w(LOG_TAG, e);
            throw new PandaDesktopException(ErrorCode.OTHER_ERROR_CODE);
        }
        return basePandaTheme;
    }

    /**
     * 创建主题对象。
     * 
     * @param element
     *            xml元素。
     * @return 组装后的BasePandaTheme对象。
     * @throws PandaDesktopException
     */
    private void createThemeObj(Element element, BasePandaTheme basePandaThemeObj) throws PandaDesktopException {
        if (null != element) {
            assemblyAttr(element, basePandaThemeObj);
        }
    }

    /**
     * 组装元素属性到BasePandaTheme对象。
     * 
     * @param element
     *            xml元素。
     * @return 组装后的BasePandaTheme对象。
     */
    private void assemblyAttr(Element element, BasePandaTheme basePandaThemeObj) throws PandaDesktopException {
        if (null != element) {
            Map<String, String> attrMap = element.getAttributes();
            elementTag = element.getName();

            elementValue = element.getValue();
            if (!TextUtils.isEmpty(elementTag)) {
                elementTag = cutPrefix(elementTag);
            }

            if (null != element.getParent()) {
                parentTag = element.getParent().getName();
                if (!TextUtils.isEmpty(parentTag)) {
                    parentTag = cutPrefix(parentTag);
                }
            }
            if (PANDA_THEME.equalsIgnoreCase(elementTag)) {
                //读取主题名称
                String name = attrMap.get(PANDA_THEME_NAME);
                String en_name = attrMap.get(PANDA_THEME_NAME_EN);
                if ((null == name) && (null == en_name)) {
                	Log.w(LOG_TAG, "name attr is not found!");
                    throw new PandaDesktopException(ErrorCode.XML_FORMAT_NAME_UNFOUND_ERROR);
                }
                if (null != name) {
                	basePandaThemeObj.setThemeName(name);
                }
                if (null != en_name) {
                	basePandaThemeObj.setThemeEnName(en_name);
                }
                //读取主题描述
                String desc = attrMap.get("desc");
                if (!TextUtils.isEmpty(desc)) {
                    basePandaThemeObj.setThemeDesc(desc);
                }
                //读取主题versionName
                String version = attrMap.get("ver");
                if (!TextUtils.isEmpty(version)) {
                    basePandaThemeObj.setVersion(version);
                }else{
                	basePandaThemeObj.setVersion("1");
                }
                //读取主题versionCode
                String versionCode = attrMap.get("version");
                if (!TextUtils.isEmpty(versionCode)) {
                	try {
                		basePandaThemeObj.setVersionCode(Integer.valueOf(versionCode));
                	} catch (Exception e) {
                		e.printStackTrace();
                		basePandaThemeObj.setVersionCode(1);
                	}
                }else{
                	basePandaThemeObj.setVersionCode(1);
                }
                //读取主题ID
                String idFlag = attrMap.get("id_flag");
                if (!TextUtils.isEmpty(idFlag)){
                	basePandaThemeObj.setIDFlag(idFlag);
                	basePandaThemeObj.setThemeId(idFlag);
                }else{
                	basePandaThemeObj.setIDFlag(en_name);
                	basePandaThemeObj.setThemeId(en_name);
                }
                String supportV6 = attrMap.get("support_v6");
                if(!TextUtils.isEmpty(supportV6)){
                	basePandaThemeObj.setSupportV6("true".equals(supportV6));
                }
                String guarded = attrMap.get("guarded");
                if(!TextUtils.isEmpty(guarded)){
                	basePandaThemeObj.setGuarded("true".equals(guarded));
                }
                String guardedVersion = attrMap.get("guarded_version");
                if(!TextUtils.isEmpty(guardedVersion)){
                	try {
                		basePandaThemeObj.setGuardedVersion(Integer.valueOf(guardedVersion));
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                String resType = attrMap.get("ResType");
                if(!TextUtils.isEmpty(resType)){
                	try {
                		// 转换部分resType有问题的V6主题
                		if(basePandaThemeObj.isSupportV6() && "2".equals(resType)) {
                			resType = "82301";
                		}
                		basePandaThemeObj.setResType(Integer.valueOf(resType));
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                String launcherMinVersion = attrMap.get("launcher_min_version");
                if(!TextUtils.isEmpty(launcherMinVersion)){
                	try {
                		basePandaThemeObj.setLauncherMinVersion(Integer.valueOf(launcherMinVersion));
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                //读取theme-config节点其他属性
                basePandaThemeObj.loadOtherDataFromXml(attrMap);
            }
            //读取workspace节点
            if (!TextUtils.isEmpty(elementValue)) {
                if (PANDA_WORKSPACE_THEME.equalsIgnoreCase(parentTag)) {
                	if (elementTag.equalsIgnoreCase(PANDA_TEXTCOLOR)) {
                		basePandaThemeObj.getTextMap().put(BaseThemeData.TEXT_COLOR, elementValue);
                    } else if (elementTag.equalsIgnoreCase(PANDA_TEXTSIZE)) {
                        basePandaThemeObj.getTextMap().put(BaseThemeData.TEXT_SIZE, elementValue);
                    } 
                }
            } else if (PANDA_ICON_CONFIG.equalsIgnoreCase(parentTag)) {//读取keyconfig节点
                putIcon(element, basePandaThemeObj);
                return;
            }
            if (element.haveChildren()) {
                List<Element> children = element.getChildren();
                for (Element child : children) {
                    assemblyAttr(child, basePandaThemeObj);
                }
            }
        }
    }

    /**
     * 组装对应文本、图标。
     * 
     * @param appElement
     *            元素对象。
     * @return 组装图标和文字后的BasePandaTheme对象。
     */
    private void putIcon(Element appElement, BasePandaTheme basePandaThemeObj) {
        if ((null != appElement) && appElement.haveChildren()) {
            List<Element> children = appElement.getChildren();
            String attr = "";
            String text = "";
            String elementName;
            for (Element element : children) {
                if (null != element.getName()) {
                    elementName = cutPrefix(element.getName());

                    if (PANDA_ICON_INTENT.equalsIgnoreCase(elementName)) {
                        attr = element.getValue();
                    } else if (PANDA_ICON_TEXT.equalsIgnoreCase(elementName)) {
                        text = element.getValue();
                    } 
                }
            }
            if ((null != attr) && (!"".equalsIgnoreCase(attr))) {
                if ((null != text) && !"".equalsIgnoreCase(text)) {
                    basePandaThemeObj.getTextMap().put(attr, text);
                }
            }
        }
    }

    /**
     * 截取元素后缀。
     * 
     * @param elementName
     *            元素完整名。
     * @return 截取元素的后缀。
     */
    private String cutPrefix(String elementName) {
        elementName = elementName.substring(elementName.indexOf(":") + 1, elementName.length()).trim();
        return elementName;
    }

    /**
     * 读取文件。
     * 
     * @param themeXmlPath
     *            文件路径
     * @return 返回文件内容。
     * @throws PandaDesktopException
     */
    public String readXml(String themeXmlPath) throws PandaDesktopException {
        StringBuffer sb = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(themeXmlPath)));
            while (in.ready()) {
                sb.append(in.readLine());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            Log.w(LOG_TAG, "Can not find file :" + themeXmlPath + fileNotFoundException);
            throw new PandaDesktopException(ErrorCode.THEME_XML_FILE_UNFOUND_CODE);
        } catch (IOException ioException) {
            Log.w(LOG_TAG, "Read xml IOException :" + ioException);
        } finally {
        	if(null != in) {
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return sb.toString();
    }
    
    public boolean validateUnzipTheme(String unZipPath) {
    	boolean validate = false;
    	File file = new File(unZipPath + themeXmlName);
    	if(file.exists())validate = true;
    	return validate;
    }

    /**
     * 验证主题zip压缩合法性
     * @param zipPath
     * @param unZipPath
     * @return
     */
    public boolean validateZipTheme(String zipPath, String unZipPath) {
        boolean validate = false;
        FileInputStream fins = null;
        ZipInputStream zins = null;
        try {
            fins = new FileInputStream(zipPath);
            zins = new ZipInputStream(fins);
            ZipEntry ze = null;
            while ((ze = zins.getNextEntry()) != null) {
                String destUnZipName = ze.getName();
                File zfile = new File(unZipPath + destUnZipName);
                if (ze.isDirectory()) {
                    continue;
                } else if (zfile.getAbsolutePath().equalsIgnoreCase(unZipPath + themeXmlName)) {
                    validate = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            validate = false;
            e.printStackTrace();
        } catch (IOException e) {
            validate = false;
            e.printStackTrace();
        } finally {
			try {
				if (null != fins) {
					fins.close();
				}
				if (null != zins) {
					zins.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return validate;
    }
    
    /**
     * 解压ZIP文件到sdcard/PandaHome2/Themes并生成主题对象。(适用主题安装与升级)
     * 
     * @param zipPath
     *            zip文件路径。
     * @return 主题对象。
     * @throws PandaDesktopException
     *            安装主题发生异常，异常代码见ErrorCode.java
     */
	public synchronized BasePandaTheme loaderThemeZip(String zipPath) throws PandaDesktopException {
		BasePandaTheme basePandaTheme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatPandaThemeObj();
		namePrefix = createTimeZone();
		String unZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
		if (!TelephoneUtil.isSdcardExist()) {
			Log.w(LOG_TAG, "sdcard is not found!");
			throw new PandaDesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
		}
		ZipUtil.ectract(zipPath, unZipPath, false);
		try {
			//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new PandaDesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
			//解析主题配置文件
			loaderThemeXML(unZipPath + themeXmlName, basePandaTheme);
			basePandaTheme.setAptPath(namePrefix + "/");
			
			if (!StringUtil.isEmpty(basePandaTheme.getIDFlag())) {
				// 根据主题Id修改解压后的主题包路径
				String idFlag = basePandaTheme.getIDFlag();
				namePrefix = idFlag.replace(" ", "_");
				String newUnZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
				basePandaTheme.setAptPath(namePrefix + "/");
				
				// 验证加密主题包合法性 caizp 2014-7-17
	            if(basePandaTheme.isGuarded()) {
	            	if(basePandaTheme.getGuardedVersion() > ThemeGlobal.SUPPORT_MAX_GUARDED_VERSION) {
	            		throw new PandaDesktopException(ErrorCode.NOT_SUPPORT_GUARD_VERSION_ERROR);
	            	}
	            	if(!validateThemeGuardedRes(unZipPath)) {
	            		// 写入IMEI，暂时让主题成功安装，后期继续优化验证机制 caizp 2014-8-8
//	            		throw new PandaDesktopException(ErrorCode.GUARD_VALIDATE_ERROR);
	            	}
	            }
				
	            // 把原来的文件夹重命名，便于删除
	            final String delFilePath = BaseConfig.THEME_DIR + namePrefix + "_" + System.currentTimeMillis() + "/";
				File f = new File(newUnZipPath);
				if (f.exists()) {
					FileUtil.renameFile(newUnZipPath, delFilePath);
				}
				if (!FileUtil.renameFile(unZipPath, newUnZipPath)) {
					// 重全名失败，恢复原来的文件夹
					FileUtil.renameFile(delFilePath, newUnZipPath);
					throw new PandaDesktopException(ErrorCode.FOLDER_RENAME_ERROR);
				} else {
					// 重全名成功，删除原来的文件夹
					if(new File(delFilePath).exists()) {
						ThreadUtil.executeMore(new Runnable() {
							@Override
							public void run() {
								FileUtil.delFolder(delFilePath);
							}
						});
					}
				}
				unZipPath = newUnZipPath;
			}
			
			// 兼容安卓锁屏主题壁纸
			loadThemeLockBg(basePandaTheme.getThemeId());
			// 解压天气皮肤包 add by caizp 2011-09-02
			loadThemeWeather(unZipPath, basePandaTheme.getThemeId());
			// 解压第三方小插件皮肤包 add by caizp 2013-03-11
			loadThemeWidget(unZipPath, basePandaTheme.getThemeId());
			// 重命名旧主题目录文件 caizp 2014-6-27
			renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_DIR);
			renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_XHDPI_DIR);
			
			//更新主题数据记录
			int ret = basePandaTheme.update();
			if (ret == ThemeGlobal.EXEC_FAIL) {
				throw new PandaDesktopException(ErrorCode.THEME_DATA_SAVE_ERROR);
			}
		} catch (PandaDesktopException pe) {
			FileUtil.delFolder(unZipPath);
			throw pe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PandaDesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
		return basePandaTheme;
	}
    
    /**
     * 加载文件夹形式主题
     * 
     * @param namePrefixTemp
     * @return
     * @throws PandaDesktopException
     */
    public synchronized BasePandaTheme loaderThemeFolder(String namePrefixTemp) throws PandaDesktopException {
    	BasePandaTheme basePandaTheme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatPandaThemeObj();
    	namePrefix = namePrefixTemp;
        String unZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
        if (!TelephoneUtil.isSdcardExist()) {
        	Log.w(LOG_TAG, "sdcard is not found!");
            throw new PandaDesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
        }
        try {
        	loaderThemeXML(unZipPath + themeXmlName, basePandaTheme);
        	//主题重新导入的时候，保存在数据库中的themeid和idflag，也是将下划线改为空格.modify by lx at 2012-09-10 14:14
            basePandaTheme.setThemeId(basePandaTheme.getIDFlag());
            basePandaTheme.setIDFlag(basePandaTheme.getIDFlag());
            basePandaTheme.setAptPath(namePrefix + "/");
            //兼容安卓锁屏主题壁纸
            loadThemeLockBg(basePandaTheme.getThemeId());
            //解压天气皮肤包  add by caizp 2011-09-02
            loadThemeWeather(unZipPath, basePandaTheme.getThemeId());
            //解压第三方小插件皮肤包  add by caizp 2013-03-11
            loadThemeWidget(unZipPath, basePandaTheme.getThemeId());
        } catch (PandaDesktopException pe) {
//        	FileUtil.delFolder(unZipPath);
            throw pe;
        }  catch (Exception e) {
			e.printStackTrace();
			throw new PandaDesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
        return basePandaTheme;
    }
    
    /**
     * <br>Description: 安装模块包(适用模块包安装与升级)
     * <br>Author:caizp
     * <br>Date:2014-6-19下午5:38:06
     * @param zipPath 模块包路径
     * @return 模块包安装成功-模块包ID， 安装失败-null
     * @throws PandaDesktopException
     */
    public synchronized String loaderThemeModuleZip(String zipPath, String moduleKey) throws PandaDesktopException {
    	if(null == moduleKey)return null;
    	ModuleInfo moduleInfo = new ModuleInfo();
    	moduleInfo.setModuleKey(moduleKey);
    	moduleInfo.setModuleCategory(ModuleConstant.getModuleCategoryByKey(moduleKey));
    	moduleInfo.setInstallTime(System.currentTimeMillis());
    	namePrefix = createTimeZone();
		String unZipPath = BaseConfig.MODULE_DIR + "temp/" + namePrefix + "/";
		if (!TelephoneUtil.isSdcardExist()) {
			Log.w(LOG_TAG, "sdcard is not found!");
			throw new PandaDesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
		}
		ZipUtil.ectract(zipPath, unZipPath, false);
		String moduleFolder = BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/";
		File folder = new File(moduleFolder);
		if (!folder.exists())
			folder.mkdirs();
		try {
			//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new PandaDesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
			//解析主题配置文件
			loaderThemeXML(unZipPath + themeXmlName, moduleInfo);
			String idFlag = moduleInfo.getModuleId();
			if (!StringUtil.isEmpty(idFlag)) {
				// 根据主题Id修改解压后的主题包路径
				namePrefix = idFlag.replace(" ", "_");
				String newUnZipPath = BaseConfig.MODULE_DIR + moduleKey.replace("@", "/") + "/" + namePrefix + "/";
				
				// 验证加密主题包合法性 caizp 2014-7-17
	            if(moduleInfo.isGuarded()) {
	            	if(moduleInfo.getGuardedVersion() > ThemeGlobal.SUPPORT_MAX_GUARDED_VERSION) {
	            		throw new PandaDesktopException(ErrorCode.NOT_SUPPORT_GUARD_VERSION_ERROR);
	            	}
	            	if(!validateThemeGuardedRes(unZipPath)) {
	            		// 写入IMEI，暂时让主题成功安装，后期继续优化验证机制 caizp 2014-8-8
//	            		throw new PandaDesktopException(ErrorCode.GUARD_VALIDATE_ERROR);
	            	}
	            }
				
	            // 把原来的文件夹重命名，便于删除
	            final String delFilePath = BaseConfig.THEME_DIR + namePrefix + "_" + System.currentTimeMillis() + "/";
				File f = new File(newUnZipPath);
				if (f.exists()) {
					FileUtil.renameFile(newUnZipPath, delFilePath);
				}
				if (!FileUtil.renameFile(unZipPath, newUnZipPath)) {
					// 重全名失败，恢复原来的文件夹
					FileUtil.renameFile(delFilePath, newUnZipPath);
					throw new PandaDesktopException(ErrorCode.FOLDER_RENAME_ERROR);
				} else {
					// 重全名成功，删除原来的文件夹
					if(new File(delFilePath).exists()) {
						ThreadUtil.executeMore(new Runnable() {
							@Override
							public void run() {
								FileUtil.delFolder(delFilePath);
							}
						});
					}
				}
				unZipPath = newUnZipPath;
				
	            // 更新模块数据记录
				if(ThemeModuleHelper.getInstance().updateModuleInfo(moduleInfo)) {
					return moduleInfo.getModuleId();
				} else {//保存失败
					throw new PandaDesktopException(ErrorCode.THEME_DATA_SAVE_ERROR);
				}
			}
		} catch (PandaDesktopException pe) {
			FileUtil.delFolder(unZipPath);
			throw pe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PandaDesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
    	return null;
    }
    
    /**
     * <br>Description: 从已有文件夹安装模块包
     * <br>Author:caizp
     * <br>Date:2014-8-16下午5:38:06
     * @param namePrefixTemp 模块文件夹路径
     * @param moduleKey 模块KEY
     * @return 模块包安装成功-模块包ID， 安装失败-null
     * @throws PandaDesktopException
     */
    public synchronized String loaderThemeModuleFolder(String namePrefixTemp, String moduleKey) throws PandaDesktopException {
    	if(null == moduleKey)return null;
    	ModuleInfo moduleInfo = new ModuleInfo();
    	moduleInfo.setModuleKey(moduleKey);
    	moduleInfo.setModuleCategory(ModuleConstant.getModuleCategoryByKey(moduleKey));
    	moduleInfo.setInstallTime(System.currentTimeMillis());
		String unZipPath = namePrefixTemp + "/";
		if (!TelephoneUtil.isSdcardExist()) {
			Log.w(LOG_TAG, "sdcard is not found!");
			throw new PandaDesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
		}
		try {
			//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new PandaDesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
			//解析主题配置文件
			loaderThemeXML(unZipPath + themeXmlName, moduleInfo);
			String idFlag = moduleInfo.getModuleId();
			if (!StringUtil.isEmpty(idFlag)) {
				// 验证加密主题包合法性 caizp 2014-7-17
	            if(moduleInfo.isGuarded()) {
	            	if(moduleInfo.getGuardedVersion() > ThemeGlobal.SUPPORT_MAX_GUARDED_VERSION) {
	            		throw new PandaDesktopException(ErrorCode.NOT_SUPPORT_GUARD_VERSION_ERROR);
	            	}
	            	if(!validateThemeGuardedRes(unZipPath)) {
	            		// 写入IMEI，暂时让主题成功安装，后期继续优化验证机制 caizp 2014-8-8
//	            		throw new PandaDesktopException(ErrorCode.GUARD_VALIDATE_ERROR);
	            	}
	            }
				if(ThemeModuleHelper.getInstance().saveModuleInfo(moduleInfo)) {
					return moduleInfo.getModuleId();
				}
			}
		} catch (PandaDesktopException pe) {
//			FileUtil.delFolder(unZipPath);
			throw pe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PandaDesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
    	return null;
    }

    /**
     * 创建时间
     * 
     * @return String
     */
    public synchronized String createTimeZone() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int mon = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        try {
            Thread.sleep(1050);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return "" + year + mon + day + hour + min + sec;
    }
    
    /**
     * <br>Description: 兼容安卓锁屏主题壁纸
     * <br>Author:caizp
     * <br>Date:2012-8-24下午04:15:07
     * @param themeId
     */
    public static void loadThemeLockBg(String themeId){
    	try {
    		String aptThemePath = BaseConfig.THEME_DIR + themeId.replace(" ", "_");
        	String lockSkinPath = aptThemePath + ThemeGlobal.THEME_91ZNS_PATH;
    		String lockBgFilePath = aptThemePath + "/" + ThemeGlobal.THEME_APT_DRAWABLE_DIR + BaseThemeData.PANDA_LOCK_MAIN_BACKGROUND + ThemeGlobal.CONVERTED_SUFFIX_JPG;
    		String desFilePath = lockSkinPath + BaseThemeData.ZNS_LOCK_BG + ".jpg";
    		if(new File(lockBgFilePath).exists() && !new File(desFilePath).exists()){
    			File dir = new File(lockSkinPath);
    			if (!dir.isDirectory()) {
    				dir.mkdirs();
    			}
    			FileUtil.copy(lockBgFilePath, desFilePath);
    		}
    	} catch (Exception e) {
    		Log.e("BaseThemeLoader", "loadThemeLockBg error!");
    	}
    }

    /**
     * <br>Description:加载主题天气皮肤
     * <br>Author:caizp
     * <br>Date:2011-5-3上午10:28:46
     * @param unZipPath
     * @param themeWeatherId
     */
    public static void loadThemeWeather(String unZipPath, String themeWeatherId){
    	String themeWeatherPackage = unZipPath + ThemeGlobal.THEME_CLOCKWEATHER_SKIN;
    	String themeWeatherFolder = unZipPath + ModuleConstant.MODULE_WEATHER + "/";
    	//如果中间生成文件夹存在，则都删除，避免数据冗余
    	String weatherShowSkinPath = BaseConfig.BASE_DIR_CLOCKWEATHER + themeWeatherId + "/";
		String weatherShowTempSkinPath = BaseConfig.BASE_DIR_CLOCKWEATHER + themeWeatherId + ".tmp/";
		//删除原文件夹
		FileUtil.delFolder(weatherShowSkinPath);
		FileUtil.delFolder(weatherShowTempSkinPath);
		//释放天气皮肤文件夹
		if (new File(themeWeatherPackage).exists()) {//weather.nwa文件存在，直接解压
			ZipUtil.ectract(themeWeatherPackage, weatherShowSkinPath, false);
		} else if (new File(themeWeatherFolder).exists()) {//weather文件夹存在，直接拷贝
			FileUtil.copyFolder(themeWeatherFolder, weatherShowSkinPath);
		}
    }
    
    /**
     * <br>Description:加载主题第三方小插件皮肤
     * <br>Author:caizp
     * <br>Date:2013-3-11上午10:28:46
     * @param unZipPath
     * @param themeId
     */
    public static void loadThemeWidget(String unZipPath, String themeId){
    	String themeWidgetPackage = unZipPath + ThemeGlobal.THEME_WIDGET_SKIN;
    	File widgetPackageFile = new File(themeWidgetPackage);
    	if(widgetPackageFile.exists()){
    		String skinPath = BaseConfig.THEME_DIR + themeId.replace(" ", "_") + ThemeGlobal.THEME_WIDGET_PATH;
    		if(!new File(skinPath).exists()){
	    		ZipUtil.ectract( themeWidgetPackage, skinPath, false );
    		}
    	}
    }
    
    /**
     * <br>Description: 重命名目录下的资源(.png->.a, .jpg->.b)
     * <br>Author:caizp
     * <br>Date:2014-6-27下午5:17:13
     * @param folderPath
     */
    public static void renameRes(String folderPath) {
    	File[] files = FileUtil.getFilesFromDir(folderPath, FileUtil.imagefileFilter);
    	if(null != files) {
    		for(int i=0; i<files.length; i++) {
    			if(files[i].isFile()) {
    				files[i].renameTo(new File(StringUtil.renameRes(files[i].getPath())));
    			}
    		}
    	}
    }
    
    /**
     * <br>Description: 验证主题资源加密资源是否合法
     * <br>Author:caizp
     * <br>Date:2014年7月18日下午2:03:23
     * @param folderPath
     * @return
     */
    private boolean validateThemeGuardedRes(String folderPath) {
    	if(TextUtils.isEmpty(folderPath))return false;
    	File file = new File(folderPath);
    	if(!file.exists())return false;
    	boolean result = true;
    	String imei = TelephoneUtil.getIMEI(BaseConfig.getApplicationContext());
    	List<String> guardedFiles = scanGuardRes(file, ThemeGlobal.GUARDED_RES);
    	try {
	    	if(null != guardedFiles) {
	    		for(int i=0; i<guardedFiles.size(); i++) {
	    			if(!EncodeTools.WriteImei(guardedFiles.get(i), imei)){
	    				Log.d("ThemeLoader", "writeIme:error..."+guardedFiles.get(i));
	    				result = false;
	    				break;
	    			}
	    			Log.d("ThemeLoader", "writeIme:success..."+guardedFiles.get(i));
	    		}
	    	}
    	} catch (Throwable t) {
    		t.printStackTrace();
    		result = false;
    	}
		
    	return result;
    }
    
    /**
     * <br>Description: 扫描主题加密资源
     * <br>Author:caizp
     * <br>Date:2014年7月18日下午3:50:00
     * @param rootPath
     * @param filterFileName
     * @return
     */
    private List<String> scanGuardRes(File rootPath, final String filterFileName) {
    	if(null == rootPath || TextUtils.isEmpty(filterFileName))return null;
    	final List<String> guardedFiles = new ArrayList<String>();
    	rootPath.listFiles(new FileFilter() {
    		public boolean accept(File pathname) {
    			if (ThemeGlobal.GUARDED_RES.equals(pathname.getName())) {
    				guardedFiles.add(pathname.getAbsolutePath());
    				return true;
    			}
    			if (pathname.isDirectory()) {//如果是目录  
    				guardedFiles.addAll(scanGuardRes(pathname, filterFileName));  
                    return true;
                } else { 
                    return false;  
                }  
    		}
    	});
    	return guardedFiles;
    }
    
}
