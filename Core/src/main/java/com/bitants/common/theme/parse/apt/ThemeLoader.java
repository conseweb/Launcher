package com.bitants.common.theme.parse.apt;

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

import com.bitants.common.framework.exception.ErrorCode;
import com.bitants.common.framework.exception.DesktopException;
import com.bitants.common.kitset.util.FileUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.kitset.util.TelephoneUtil;
import com.bitants.common.kitset.util.ThreadUtil;
import com.bitants.common.kitset.util.ZipUtil;
import com.bitants.common.kitset.xmlparser.Element;
import com.bitants.common.kitset.xmlparser.XmlParser;
import com.bitants.common.kitset.xmlparser.exception.XmlFormatErrorException;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.theme.ThemeManagerFactory;
import com.bitants.common.theme.data.BaseTheme;
import com.bitants.common.theme.data.ThemeGlobal;
import com.bitants.common.theme.module.ModuleConstant;
import com.bitants.common.theme.module.ModuleInfo;
import com.bitants.common.theme.data.BaseThemeData;
import com.bitants.common.theme.module.ThemeModuleHelper;
import com.bitants.common.theme.parse.EncodeTools;

/**
 * <br>Description: apt主题包解析装载
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
    private static final String THEME = "theme-config";

    /**
     * 主题xml内部配置workspace节点
     */
    private static final String WORKSPACE_THEME = "workspace";
    
    /**
     * 图标文字颜色节点
     */
    private static final String TEXTCOLOR = "textcolor";

    /**
     * 图标文字大小节点
     */
    private static final String TEXTSIZE = "textsize";
    
    /**
     * 主题xml内部配置keyconfig节点
     */
    private static final String ICON_CONFIG = "keyconfig";

    /**
     * 主题中文名称节点
     */
    private static final String THEME_NAME = "name";

    /**
     * 主题英文名称节点
     */
    private static final String THEME_NAME_EN = "en_name";

    /**
     * 主题intent节点
     */
    private static final String ICON_INTENT = "intent";

    /**
     * 主题text节点
     */
    private static final String ICON_TEXT = "text";

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
    private void loaderThemeXML(String themePath, BaseTheme baseThemeObj) throws DesktopException {
        try {
            root = XmlParser.buildXmlRootByString(StringUtil.renameRes(readXml(themePath)));
            createThemeObj(root, baseThemeObj);
        } catch (XmlFormatErrorException e) {
            Log.w(LOG_TAG, "Xml format error. " + e);
            throw new DesktopException(ErrorCode.XML_FORMAT_ERROR_CODE);
        } catch (DesktopException e) {
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
    private void loaderThemeXML(String themePath, ModuleInfo moduleInfo) throws DesktopException {
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
                if (THEME.equalsIgnoreCase(elementTag)) {
                    //读取主题名称
                    String name = attrMap.get(THEME_NAME);
                    String en_name = attrMap.get(THEME_NAME_EN);
                    if ((null == name) && (null == en_name)) {
                    	Log.w(LOG_TAG, "name attr is not found!");
                        throw new DesktopException(ErrorCode.XML_FORMAT_NAME_UNFOUND_ERROR);
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
            throw new DesktopException(ErrorCode.XML_FORMAT_ERROR_CODE);
        } catch (DesktopException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 解压ZIP文件到sdcard/mirrorhome/Themes并生成主题对象。
     * 
     * @param zipPath
     *            zip文件路径。
     * @return 主题对象。
     * @throws Exception
     *             SDcard是否存在异常。
     */
    public synchronized BaseTheme loaderThemeZipFromAssets(Context ctx, String zipPath) throws DesktopException {
    	BaseTheme baseTheme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatThemeObj();
        namePrefix = createTimeZone();
        String unZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
        if (!TelephoneUtil.isSdcardExist()) {
        	Log.w(LOG_TAG, "sdcard is not found!");
            throw new DesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
        }
        ZipUtil.ectract(ctx, zipPath, unZipPath, false);
        try {
        	//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new DesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
            loaderThemeXML(unZipPath + themeXmlName, baseTheme);
            //根据主题Id修改解压后的主题包路径
            namePrefix = baseTheme.getIDFlag().replace(" ", "_");
            baseTheme.setThemeId(baseTheme.getIDFlag());
            baseTheme.setIDFlag(baseTheme.getIDFlag());
            String newUnZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
            baseTheme.setAptPath(namePrefix + "/");
            
            // 把原来的文件夹重命名，便于删除
            final String delFilePath = BaseConfig.THEME_DIR + namePrefix + "_" + System.currentTimeMillis() + "/";
			File f = new File(newUnZipPath);
			if (f.exists()) {
				FileUtil.renameFile(newUnZipPath, delFilePath);
			}
			if (!FileUtil.renameFile(unZipPath, newUnZipPath)) {
				// 重全名失败，恢复原来的文件夹
				FileUtil.renameFile(delFilePath, newUnZipPath);
				throw new DesktopException(ErrorCode.FOLDER_RENAME_ERROR);
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
            //解压天气皮肤包
            loadThemeWeather(unZipPath, baseTheme.getThemeId());
            // 解压第三方小插件皮肤包
         	loadThemeWidget(unZipPath, baseTheme.getThemeId());
         	// 重命名旧主题目录文件
         	renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_DIR);
			renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_XHDPI_DIR);
        } catch (DesktopException pe) {
        	FileUtil.delFolder(unZipPath);
            throw pe;
        } catch (Exception e) {
            Log.w(LOG_TAG, e);
            throw new DesktopException(ErrorCode.OTHER_ERROR_CODE);
        }
        return baseTheme;
    }

    /**
     * 创建主题对象。
     * 
     * @param element
     *            xml元素。
     * @return 组装后的BasePandaTheme对象。
     * @throws DesktopException
     */
    private void createThemeObj(Element element, BaseTheme baseThemeObj) throws DesktopException {
        if (null != element) {
            assemblyAttr(element, baseThemeObj);
        }
    }

    /**
     * 组装元素属性到BasePandaTheme对象。
     * 
     * @param element
     *            xml元素。
     * @return 组装后的BasePandaTheme对象。
     */
    private void assemblyAttr(Element element, BaseTheme baseThemeObj) throws DesktopException {
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
            if (THEME.equalsIgnoreCase(elementTag)) {
                //读取主题名称
                String name = attrMap.get(THEME_NAME);
                String en_name = attrMap.get(THEME_NAME_EN);
                if ((null == name) && (null == en_name)) {
                	Log.w(LOG_TAG, "name attr is not found!");
                    throw new DesktopException(ErrorCode.XML_FORMAT_NAME_UNFOUND_ERROR);
                }
                if (null != name) {
                	baseThemeObj.setThemeName(name);
                }
                if (null != en_name) {
                	baseThemeObj.setThemeEnName(en_name);
                }
                //读取主题描述
                String desc = attrMap.get("desc");
                if (!TextUtils.isEmpty(desc)) {
                    baseThemeObj.setThemeDesc(desc);
                }
                //读取主题versionName
                String version = attrMap.get("ver");
                if (!TextUtils.isEmpty(version)) {
                    baseThemeObj.setVersion(version);
                }else{
                	baseThemeObj.setVersion("1");
                }
                //读取主题versionCode
                String versionCode = attrMap.get("version");
                if (!TextUtils.isEmpty(versionCode)) {
                	try {
                		baseThemeObj.setVersionCode(Integer.valueOf(versionCode));
                	} catch (Exception e) {
                		e.printStackTrace();
                		baseThemeObj.setVersionCode(1);
                	}
                }else{
                	baseThemeObj.setVersionCode(1);
                }
                //读取主题ID
                String idFlag = attrMap.get("id_flag");
                if (!TextUtils.isEmpty(idFlag)){
                	baseThemeObj.setIDFlag(idFlag);
                	baseThemeObj.setThemeId(idFlag);
                }else{
                	baseThemeObj.setIDFlag(en_name);
                	baseThemeObj.setThemeId(en_name);
                }
                String supportV6 = attrMap.get("support_v6");
                if(!TextUtils.isEmpty(supportV6)){
                	baseThemeObj.setSupportV6("true".equals(supportV6));
                }
                String guarded = attrMap.get("guarded");
                if(!TextUtils.isEmpty(guarded)){
                	baseThemeObj.setGuarded("true".equals(guarded));
                }
                String guardedVersion = attrMap.get("guarded_version");
                if(!TextUtils.isEmpty(guardedVersion)){
                	try {
                		baseThemeObj.setGuardedVersion(Integer.valueOf(guardedVersion));
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                String resType = attrMap.get("ResType");
                if(!TextUtils.isEmpty(resType)){
                	try {
                		// 转换部分resType有问题的V6主题
                		if(baseThemeObj.isSupportV6() && "2".equals(resType)) {
                			resType = "82301";
                		}
                		baseThemeObj.setResType(Integer.valueOf(resType));
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                String launcherMinVersion = attrMap.get("launcher_min_version");
                if(!TextUtils.isEmpty(launcherMinVersion)){
                	try {
                		baseThemeObj.setLauncherMinVersion(Integer.valueOf(launcherMinVersion));
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                }
                //读取theme-config节点其他属性
                baseThemeObj.loadOtherDataFromXml(attrMap);
            }
            //读取workspace节点
            if (!TextUtils.isEmpty(elementValue)) {
                if (WORKSPACE_THEME.equalsIgnoreCase(parentTag)) {
                	if (elementTag.equalsIgnoreCase(TEXTCOLOR)) {
                		baseThemeObj.getTextMap().put(BaseThemeData.TEXT_COLOR, elementValue);
                    } else if (elementTag.equalsIgnoreCase(TEXTSIZE)) {
                        baseThemeObj.getTextMap().put(BaseThemeData.TEXT_SIZE, elementValue);
                    } 
                }
            } else if (ICON_CONFIG.equalsIgnoreCase(parentTag)) {//读取keyconfig节点
                putIcon(element, baseThemeObj);
                return;
            }
            if (element.haveChildren()) {
                List<Element> children = element.getChildren();
                for (Element child : children) {
                    assemblyAttr(child, baseThemeObj);
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
    private void putIcon(Element appElement, BaseTheme baseThemeObj) {
        if ((null != appElement) && appElement.haveChildren()) {
            List<Element> children = appElement.getChildren();
            String attr = "";
            String text = "";
            String elementName;
            for (Element element : children) {
                if (null != element.getName()) {
                    elementName = cutPrefix(element.getName());

                    if (ICON_INTENT.equalsIgnoreCase(elementName)) {
                        attr = element.getValue();
                    } else if (ICON_TEXT.equalsIgnoreCase(elementName)) {
                        text = element.getValue();
                    } 
                }
            }
            if ((null != attr) && (!"".equalsIgnoreCase(attr))) {
                if ((null != text) && !"".equalsIgnoreCase(text)) {
                    baseThemeObj.getTextMap().put(attr, text);
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
     * @throws DesktopException
     */
    public String readXml(String themeXmlPath) throws DesktopException {
        StringBuffer sb = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(themeXmlPath)));
            while (in.ready()) {
                sb.append(in.readLine());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            Log.w(LOG_TAG, "Can not find file :" + themeXmlPath + fileNotFoundException);
            throw new DesktopException(ErrorCode.THEME_XML_FILE_UNFOUND_CODE);
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
     * 解压ZIP文件到sdcard/mirrorhome/Themes并生成主题对象。(适用主题安装与升级)
     * 
     * @param zipPath
     *            zip文件路径。
     * @return 主题对象。
     * @throws DesktopException
     *            安装主题发生异常，异常代码见ErrorCode.java
     */
	public synchronized BaseTheme loaderThemeZip(String zipPath) throws DesktopException {
		BaseTheme baseTheme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatThemeObj();
		namePrefix = createTimeZone();
		String unZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
		if (!TelephoneUtil.isSdcardExist()) {
			Log.w(LOG_TAG, "sdcard is not found!");
			throw new DesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
		}
		ZipUtil.ectract(zipPath, unZipPath, false);
		try {
			//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new DesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
			//解析主题配置文件
			loaderThemeXML(unZipPath + themeXmlName, baseTheme);
			baseTheme.setAptPath(namePrefix + "/");
			
			if (!StringUtil.isEmpty(baseTheme.getIDFlag())) {
				// 根据主题Id修改解压后的主题包路径
				String idFlag = baseTheme.getIDFlag();
				namePrefix = idFlag.replace(" ", "_");
				String newUnZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
				baseTheme.setAptPath(namePrefix + "/");
				
				// 验证加密主题包合法性
	            if(baseTheme.isGuarded()) {
	            	if(baseTheme.getGuardedVersion() > ThemeGlobal.SUPPORT_MAX_GUARDED_VERSION) {
	            		throw new DesktopException(ErrorCode.NOT_SUPPORT_GUARD_VERSION_ERROR);
	            	}
	            	if(!validateThemeGuardedRes(unZipPath)) {
	            		// 写入IMEI，暂时让主题成功安装，后期继续优化验证机制
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
					throw new DesktopException(ErrorCode.FOLDER_RENAME_ERROR);
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
			loadThemeLockBg(baseTheme.getThemeId());
			// 解压天气皮肤包
			loadThemeWeather(unZipPath, baseTheme.getThemeId());
			// 解压第三方小插件皮肤包
			loadThemeWidget(unZipPath, baseTheme.getThemeId());
			// 重命名旧主题目录文件
			renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_DIR);
			renameRes(unZipPath + ThemeGlobal.THEME_APT_DRAWABLE_XHDPI_DIR);
			
			//更新主题数据记录
			int ret = baseTheme.update();
			if (ret == ThemeGlobal.EXEC_FAIL) {
				throw new DesktopException(ErrorCode.THEME_DATA_SAVE_ERROR);
			}
		} catch (DesktopException pe) {
			FileUtil.delFolder(unZipPath);
			throw pe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
		return baseTheme;
	}
    
    /**
     * 加载文件夹形式主题
     * 
     * @param namePrefixTemp
     * @return
     * @throws DesktopException
     */
    public synchronized BaseTheme loaderThemeFolder(String namePrefixTemp) throws DesktopException {
    	BaseTheme baseTheme = ThemeManagerFactory.getInstance().getThemeManagerHelper().allocatThemeObj();
    	namePrefix = namePrefixTemp;
        String unZipPath = BaseConfig.THEME_DIR + namePrefix + "/";
        if (!TelephoneUtil.isSdcardExist()) {
        	Log.w(LOG_TAG, "sdcard is not found!");
            throw new DesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
        }
        try {
        	loaderThemeXML(unZipPath + themeXmlName, baseTheme);
        	//主题重新导入的时候，保存在数据库中的themeid和idflag，也是将下划线改为空格.
            baseTheme.setThemeId(baseTheme.getIDFlag());
            baseTheme.setIDFlag(baseTheme.getIDFlag());
            baseTheme.setAptPath(namePrefix + "/");
            //兼容安卓锁屏主题壁纸
            loadThemeLockBg(baseTheme.getThemeId());
            //解压天气皮肤包
            loadThemeWeather(unZipPath, baseTheme.getThemeId());
            //解压第三方小插件皮肤包
            loadThemeWidget(unZipPath, baseTheme.getThemeId());
        } catch (DesktopException pe) {
//        	FileUtil.delFolder(unZipPath);
            throw pe;
        }  catch (Exception e) {
			e.printStackTrace();
			throw new DesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
        return baseTheme;
    }
    
    /**
     * <br>Description: 安装模块包(适用模块包安装与升级)
     * @param zipPath 模块包路径
     * @return 模块包安装成功-模块包ID， 安装失败-null
     * @throws DesktopException
     */
    public synchronized String loaderThemeModuleZip(String zipPath, String moduleKey) throws DesktopException {
    	if(null == moduleKey)return null;
    	ModuleInfo moduleInfo = new ModuleInfo();
    	moduleInfo.setModuleKey(moduleKey);
    	moduleInfo.setModuleCategory(ModuleConstant.getModuleCategoryByKey(moduleKey));
    	moduleInfo.setInstallTime(System.currentTimeMillis());
    	namePrefix = createTimeZone();
		String unZipPath = BaseConfig.MODULE_DIR + "temp/" + namePrefix + "/";
		if (!TelephoneUtil.isSdcardExist()) {
			Log.w(LOG_TAG, "sdcard is not found!");
			throw new DesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
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
				throw new DesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
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
	            		throw new DesktopException(ErrorCode.NOT_SUPPORT_GUARD_VERSION_ERROR);
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
					throw new DesktopException(ErrorCode.FOLDER_RENAME_ERROR);
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
					throw new DesktopException(ErrorCode.THEME_DATA_SAVE_ERROR);
				}
			}
		} catch (DesktopException pe) {
			FileUtil.delFolder(unZipPath);
			throw pe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DesktopException(ErrorCode.OTHER_ERROR_CODE);
		}
    	return null;
    }
    
    /**
     * <br>Description: 从已有文件夹安装模块包
     * @param namePrefixTemp 模块文件夹路径
     * @param moduleKey 模块KEY
     * @return 模块包安装成功-模块包ID， 安装失败-null
     * @throws DesktopException
     */
    public synchronized String loaderThemeModuleFolder(String namePrefixTemp, String moduleKey) throws DesktopException {
    	if(null == moduleKey)return null;
    	ModuleInfo moduleInfo = new ModuleInfo();
    	moduleInfo.setModuleKey(moduleKey);
    	moduleInfo.setModuleCategory(ModuleConstant.getModuleCategoryByKey(moduleKey));
    	moduleInfo.setInstallTime(System.currentTimeMillis());
		String unZipPath = namePrefixTemp + "/";
		if (!TelephoneUtil.isSdcardExist()) {
			Log.w(LOG_TAG, "sdcard is not found!");
			throw new DesktopException(ErrorCode.SDCARD_UNFOUND_CODE);
		}
		try {
			//验证主题可用性
			if (!validateUnzipTheme(unZipPath)) {
				Log.w(LOG_TAG, "zip validate error!");
				throw new DesktopException(ErrorCode.ZIP_VALIDATE_ERROR_CODE);
			}
			//解析主题配置文件
			loaderThemeXML(unZipPath + themeXmlName, moduleInfo);
			String idFlag = moduleInfo.getModuleId();
			if (!StringUtil.isEmpty(idFlag)) {
				// 验证加密主题包合法性 caizp 2014-7-17
	            if(moduleInfo.isGuarded()) {
	            	if(moduleInfo.getGuardedVersion() > ThemeGlobal.SUPPORT_MAX_GUARDED_VERSION) {
	            		throw new DesktopException(ErrorCode.NOT_SUPPORT_GUARD_VERSION_ERROR);
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
		} catch (DesktopException pe) {
//			FileUtil.delFolder(unZipPath);
			throw pe;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DesktopException(ErrorCode.OTHER_ERROR_CODE);
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
     * @param themeId
     */
    public static void loadThemeLockBg(String themeId){
    	try {
    		String aptThemePath = BaseConfig.THEME_DIR + themeId.replace(" ", "_");
        	String lockSkinPath = aptThemePath + ThemeGlobal.THEME_LOCK_PATH;
    		String lockBgFilePath = aptThemePath + "/" + ThemeGlobal.THEME_APT_DRAWABLE_DIR + BaseThemeData.SCREEN_LOCK_MAIN_BACKGROUND + ThemeGlobal.CONVERTED_SUFFIX_JPG;
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
