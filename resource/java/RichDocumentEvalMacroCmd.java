package com.bokesoft.yes.mid.cmd.richdocument.strut;

import java.util.Date;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bokesoft.yes.common.log.LogSvr;
import com.bokesoft.yes.common.struct.StringHashMap;
import com.bokesoft.yes.common.util.BaseTypeUtil;
import com.bokesoft.yes.common.util.StringUtil;
import com.bokesoft.yes.mid.cmd.IServiceCmd;
import com.bokesoft.yes.mid.parameterizedsql.SqlEncryptUtil;
import com.bokesoft.yes.mid.parameterizedsql.SqlString;
import com.bokesoft.yigo.common.def.JavaDataType;
import com.bokesoft.yigo.common.util.TypeConvertor;
import com.bokesoft.yigo.meta.common.MetaMacro;
import com.bokesoft.yigo.meta.common.MetaMacroCollection;
import com.bokesoft.yigo.meta.commondef.MetaCommonDef;
import com.bokesoft.yigo.meta.factory.IMetaFactory;
import com.bokesoft.yigo.meta.form.MetaForm;
import com.bokesoft.yigo.meta.solution.MetaProject;

/**
 * 
 * 宏公式执行命令
 *
 */
public class RichDocumentEvalMacroCmd extends RichDocumentDefaultCmd {
	
	private static final Logger logger = LoggerFactory.getLogger(RichDocumentEvalMacroCmd.class);

	public static final String CMD = "RichDocumentEvalMacro";
	/**
	 * 宏公式Key
	 */
	private String macroKey;
	
	/**
	 * 宏公式的参数
	 */
	private Object[] args;
	
//	/** 过滤条件映射 */
//	private FilterMap filterMap = null;
//	public static final String FilterMap_Key = "filterMap";
	
	@Override
	public void dealArguments(RichDocumentContext context, StringHashMap<Object> arguments) throws Throwable {
		super.dealArguments(context, arguments);
		this.macroKey = (String) arguments.get("macroKey");
		String argStr = (String) arguments.get("args");
		if (!StringUtil.isBlankOrNull(argStr)) {
			JSONArray array = new JSONArray(argStr);
			args = new Object[array.length()];
			for (int i = 0; i < array.length(); i++) {
				Object o = array.get(i);
				if (String.valueOf(o).contains("MARK(")) {
					//处理参数中存在加密的字段
					 o = SqlEncryptUtil.getDecryptByDESAddMD5(String.valueOf(o));
				}
				args[i] = o;
			}
		}
		logger.info(">>>RichDocumentEvalMacroCmd中macroKey="+ this.macroKey + " para的值" + context.getPara("DictOID"));
	}

	@Override
	public Object doCmd(RichDocumentContext context) throws Throwable {
		MetaForm metaForm = context.getDocumentRecordDirty().getMetaForm();
		MetaMacro metaMacro = (null == metaForm.getMacroCollection()) ? null
				: metaForm.getMacroCollection().get(macroKey);
		if (metaMacro == null) {
			IMetaFactory metaFactory = context.getVE().getMetaFactory();
	//		MetaForm metaForm = metaFactory.getMetaForm(this.formKey);
			MetaMacroCollection macroCollection = null;
	//		MetaMacro metaMacro = null;
			
			MetaProject metaProject = (MetaProject) metaForm.getProject();
			MetaCommonDef metaCommonDef = metaFactory.getCommonDef(metaProject.getKey());
			if (metaCommonDef != null) {
				macroCollection = metaCommonDef.getMacroCollection();
				if (macroCollection != null) {
					metaMacro = macroCollection.get(macroKey);
				}
			}
			if (metaMacro == null) {
				metaCommonDef = metaFactory.getCommonDef("");
				if (metaCommonDef != null) {
					macroCollection = metaCommonDef.getMacroCollection();
					if (macroCollection != null) {
						metaMacro = macroCollection.get(macroKey);
					}
				}
			}
		}
		
		Object result = null;
		if(metaMacro != null) {
			result = context.evalMacro(context, null, metaMacro.getKey(), metaMacro, args, null);
		}
		if (result != null && result instanceof SqlString &&!((SqlString) result).isEmpty()) {
			//设置当前SqlString对象为不可更改对象
			SqlString sqlStringResult = (SqlString) result;
			sqlStringResult.setFinalResult();

			// 对返回结果进行加密
			result = SqlEncryptUtil.getEncryptByDESAddMD5(sqlStringResult);
		}
		JSONObject json = super.getDirtyJSON();
		
		int type = BaseTypeUtil.getType(result);
		if (result != null) {
			switch (type) {
			case JavaDataType.USER_DATETIME:
				long date = ((Date) result).getTime();
				json.put("result", date);
				break;
			default:
				json.put("result", result);
				break;
			}
		}
		json.put("resultType", type);
		return json;
	}

	@Override
	public IServiceCmd<RichDocumentContext> newInstance() {
		return new RichDocumentEvalMacroCmd();
	}

	@Override
	public String getCmd() {
		return CMD;
	}

	@Override
	public String getCmdId(RichDocumentContext context, StringHashMap<Object> arguments) throws Throwable {
		try {
			String formKey = TypeConvertor.toString(arguments.get("metaFormKey"));
			IMetaFactory metaFactory = context.getVE().getMetaFactory();
			MetaForm metaForm = metaFactory.getMetaForm(formKey);
			String projectKey = metaForm.getProject().getKey();
			return new StringBuilder(128).append(projectKey).append("/").append(formKey).append("/")
					.append(arguments.get("macroKey")).toString();
		} catch (Exception e) {
			LogSvr.getInstance().error("getCmdId has error.", e);
			return "";
		}
	}
}
