package com.bokesoft.yes.mid.cmd.richdocument.strut;

import com.bokesoft.distro.tech.yigosupport.extension.performance.impl.ActionType;
import com.bokesoft.distro.tech.yigosupport.extension.performance.impl.PerformanceAttributeData;
import com.bokesoft.erp.function.DecoratorType;
import com.bokesoft.erp.para.ParaDefine;
import com.bokesoft.erp.para.ParaDefines;
import com.bokesoft.erp.para.ParaScopeDefine;
import com.bokesoft.erp.performance.Performance;
import com.bokesoft.erp.performance.trace.MetaObjectType;
import com.bokesoft.erp.performance.trace.Property;
import com.bokesoft.erp.performance.trace.SessionUICommands;
import com.bokesoft.erp.performance.trace.TraceFormula;
import com.bokesoft.erp.performance.trace.TraceSetting;
import com.bokesoft.yes.bpm.meta.transform.BPMKeys;
import com.bokesoft.yes.common.struct.StringHashMap;
import com.bokesoft.yes.common.util.DBDataConvertor;
import com.bokesoft.yes.common.util.StringUtil;
import com.bokesoft.yes.erp.config.ERPMetaFactory;
import com.bokesoft.yes.log.LogSvr;
import com.bokesoft.yes.mid.base.ContextContainer;
import com.bokesoft.yes.mid.base.IMidGlobalEnv;
import com.bokesoft.yes.mid.base.MidVE;
import com.bokesoft.yes.mid.cmd.richdocument.strut.execute.ITransAction;
import com.bokesoft.yes.mid.cmd.richdocument.strut.execute.ITransExceptionHandler;
import com.bokesoft.yes.mid.cmd.richdocument.strut.execute.ITransFinalizer;
import com.bokesoft.yes.mid.dbcache.datatable.DataTableExUtil;
import com.bokesoft.yes.mid.parameterizedsql.SqlString;
import com.bokesoft.yes.mid.parser.MidFunctionImplMap;
import com.bokesoft.yes.mid.parser.MidParser;
import com.bokesoft.yes.parser.BaseFunImplMap;
import com.bokesoft.yes.parser.EvalScope;
import com.bokesoft.yes.parser.Heap;
import com.bokesoft.yes.parser.IFunImpl;
import com.bokesoft.yes.parser.IFuncImplMap;
import com.bokesoft.yes.parser.IHackEvalContext;
import com.bokesoft.yes.parser.IHeap;
import com.bokesoft.yes.parser.LexDef;
import com.bokesoft.yes.parser.SyntaxTree;
import com.bokesoft.yes.struct.datatable.Row;
import com.bokesoft.yes.tools.dic.proxy.IDictCacheProxy;
import com.bokesoft.yes.tools.scope.MacroUtils;
import com.bokesoft.yes.util.ERPStringUtil;
import com.bokesoft.yes.util.VarUtil;
import com.bokesoft.yigo.common.def.*;
import com.bokesoft.yigo.common.trace.TraceSystemManager;
import com.bokesoft.yigo.common.trace.intf.ITraceSupplier;
import com.bokesoft.yigo.common.util.TypeConvertor;
import com.bokesoft.yigo.meta.common.MetaMacro;
import com.bokesoft.yigo.meta.dataobject.MetaColumn;
import com.bokesoft.yigo.meta.dataobject.MetaDataObject;
import com.bokesoft.yigo.meta.dataobject.MetaDataSource;
import com.bokesoft.yigo.meta.dataobject.MetaTable;
import com.bokesoft.yigo.meta.dataobject.MetaTableCollection;
import com.bokesoft.yigo.meta.factory.IMetaFactory;
import com.bokesoft.yigo.meta.factory.MetaFactory;
import com.bokesoft.yigo.meta.form.MetaForm;
import com.bokesoft.yigo.meta.form.component.MetaComponent;
import com.bokesoft.yigo.meta.form.component.grid.MetaGrid;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridCell;
import com.bokesoft.yigo.meta.form.component.grid.MetaRowTree;
import com.bokesoft.yigo.meta.intf.IMetaProject;
import com.bokesoft.yigo.mid.base.BaseContext;
import com.bokesoft.yigo.mid.base.DefaultContext;
import com.bokesoft.yigo.parser.IEval;
import com.bokesoft.yigo.parser.IEvalContext;
import com.bokesoft.yigo.parser.IExecutor;
import com.bokesoft.yigo.struct.datatable.ColumnInfo;
import com.bokesoft.yigo.struct.datatable.DataTable;
import com.bokesoft.yigo.struct.datatable.DataTableMetaData;
import com.bokesoft.yigo.struct.dict.Item;
import com.bokesoft.yigo.struct.document.Document;
import com.bokesoft.yigo.struct.document.FilterMap;
import com.bokesoft.yigo.struct.usrpara.Para;
import com.bokesoft.yigo.struct.usrpara.Paras;
import com.bokesoft.yigo.tools.document.DataTableUtil;
import com.bokesoft.yigo.tools.document.DocumentUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;

public class RichDocumentContext extends DefaultContext {

	//用来区分当前上下文是联机还是脱机
	private Stack<String> states;
	private String old_state;

	/**
	 * 新建数据库连接的创建上下文对象，即开始新事务，使用后必须关闭数据库连接，默认使用脱机状态
	 * 
	 * @param ve
	 */
	protected RichDocumentContext(MidVE ve) {
	    super(ve);
        this.enter(OFF_LINE);
	}

	/**
	 * 共享数据库连接的创建上下文对象，即采用同个事务
	 *
	 * @param context
	 * @throws Throwable
	 */
	public RichDocumentContext(DefaultContext context) throws Throwable {
		this(context, true, true);
	}

	/**
	 * 共享数据库连接的创建上下文对象，即采用同个事务
	 * 
	 * @param context
	 * @throws Throwable
	 */
	public RichDocumentContext(DefaultContext context, boolean isCreate, boolean checkParentContextIsRichDocumentContext) throws Throwable {
		super(context, isCreate);
		//审批过程中父上下文是BPMContext的时候报错#zhenglu
		DefaultContext parentContext = context;
		if (checkParentContextIsRichDocumentContext && parentContext != null && !(parentContext instanceof RichDocumentContext)) {
			RichDocumentContext parentContextRichDocumentContext = new RichDocumentContext(parentContext, false, false);
			Document document = parentContext.getDocument();
			String formKey = parentContext.getFormKey();
			Document richDocument = document;
			if (!(document instanceof RichDocument) && ERPStringUtil.isNotBlankOrNull(formKey)) {
				if (document == null) {
					// 这段逻辑暂时不知道场景，先保留
					richDocument = parentContextRichDocumentContext.newDocument(formKey, document);
				} else {
					//当BPMContext中有Document时直接转换，而不是重新创建一个
					MetaForm metaForm = this.getMetaFactory().getMetaForm(formKey);
					richDocument = DocumentRecordDirty.getDocumentFromDoc(document, metaForm);
				}
			}
			parentContextRichDocumentContext.setDocument(richDocument);
			parentContextRichDocumentContext.setFormKey(formKey);
			parentContextRichDocumentContext.setDataObject(parentContext.getDataObject());
			parentContextRichDocumentContext.setParas(parentContext.getParas());
			parentContextRichDocumentContext.setConditionParas(parentContext.getConditionParas());
			this.setParentContext(parentContextRichDocumentContext);
		}
		if(context instanceof RichDocumentContext) {
			RichDocumentContext documentContext = (RichDocumentContext) context;
			this.setOperatorParas(documentContext.getOperatorParas());
			this.enter(documentContext.getNowState());
		}
		super.setFormKey(context.getFormKey());
		this.setParas(context.getParas());
		this.setConditionParas(context.getConditionParas());
		setHeadInfos(context);
	}

	private void setHeadInfos(DefaultContext context) {
		Map<String, Object> headInfos = context.getHeadInfos();
		if(headInfos != null) {
			Map<String, Object> newHeadInfos = new HashMap();
			for(Map.Entry<String, Object> entry: headInfos.entrySet()) {
				// Context之间TCode和Activity不传递
				if(entry.getKey().equals("TCode")|| entry.getKey().equals("Activity")) {
					continue;
				}
				newHeadInfos.put(entry.getKey(), entry.getValue());
			}
			this.setHeadInfos(newHeadInfos);
		}
	}

	/**
	 * 共享数据库连接的创建上下文对象，即采用同个事务
	 * 
	 * @param context
	 * @param bCreate 控制DefaultContext中transactionContainer对象是否创建
	 * @throws Throwable
	 */
	public RichDocumentContext(DefaultContext context, boolean bCreate) throws Throwable {
		super(context, bCreate);
//		DefaultContext parentContext = context.getParentContext();
//		// FIXME: 为了解决BokeDee接口(com.bokesoft.yes.dts.DataTransferService.multiDataTransfer)调用ERP公式的错误
//		if (parentContext == null || parentContext.getDocument() == null) {
//			this.setParentContext(null);
//		} else {
//			this.setParentContext(parentContext);
//		}
		super.setFormKey(context.getFormKey());
		this.setParas(context.getParas());
		this.setConditionParas(context.getConditionParas());
		if(context instanceof RichDocumentContext) {
			RichDocumentContext documentContext = (RichDocumentContext) context;
			this.enter(documentContext.getNowState());
		}
		setHeadInfos(context);
	}
	
	public void applyNewBizLockFormInfo() {
		Map<String, Object> headInfos = this.getHeadInfos();
		if (headInfos == null) {
			headInfos = new HashMap<>();
			this.setHeadInfos(headInfos);
		}
		headInfos.put("BizLockFormUUID", UUID.randomUUID());
		headInfos.put("BizLockFormKey", this.getFormKey());
	}
	
	public String getBizLockFormKey() {
		return this.getFormKey();
//		String formKey = TypeConvertor.toString(this.getHeadInfo("BizLockFormKey"));
//		if (StringUtil.isBlankOrNull(formKey) || formKey.endsWith("DictEdit")) {
//			formKey = this.getFormKey();
//		}
//		return formKey;
	}

	public String getBizLockFormUUID() throws Exception {
		String bizLockFormUUID = TypeConvertor.toString(this.getHeadInfo("BizLockFormUUID"));
		if (StringUtil.isBlankOrNull(bizLockFormUUID)) {
			throw new Exception("加锁的formUUID为空");
		}
		return bizLockFormUUID;
	}
	
	public DocumentRecordDirty getDocumentRecordDirty() {
		Document document = getDocument();
		if (document instanceof DocumentRecordDirty || document == null) {
			return (DocumentRecordDirty) document;
		} else {
			DocumentRecordDirty doc = null;
			try {
				doc = defaultDocument(this.formKey);
			} catch (Throwable e) {
				LogSvr.getInstance().debug("getRichDocument", e);
			}
			return doc;
		}
	}

	private DocumentRecordDirty getParentRichDocument() throws Throwable {
		RichDocumentContext parentContext = (RichDocumentContext) this.getParentContext();
		if (parentContext != null) {
			return parentContext.getDocumentRecordDirty();
		}
		return null;
	}

	@Override
	public void setValue(BaseContext evalContext, EvalScope scope, String object, String id, Object value)
			throws Throwable {
		DocumentRecordDirty richDocument = this.getDocumentRecordDirty();
		MetaForm metaForm = richDocument.getMetaForm();
		IDLookup idLookup = IDLookup.getIDLookup(metaForm);
		String tableKey = idLookup.getTableKeyByFieldKey(id);
		int bookMark = richDocument.getCurrentBookMark(tableKey);
		GridRow gridRow = new GridRow(richDocument, StringUtils.isBlank(object) ? tableKey : object, bookMark);
		richDocument.setValue(this, id, gridRow, value);
	}

	@Override
	public Object getValue(BaseContext evalContext, EvalScope scope, String object, String id) throws Throwable {
		Object result = null;
		Heap heap = scope.getHeap();
		Object var = null;
		boolean isDoc = true;
		if (object != null && object.isEmpty()) {
			if (heap.containVariable(object)) {
				var = heap.getVariable(object);
			}
		} else {
			// 从 ConditonParas中取值
			IHeap hostHeap = super.getHostHeap();
			if (hostHeap != null && hostHeap.containVariable(id)) {
				result = hostHeap.getVariable(id);
				isDoc = false;
			}
		}
		if (var != null) {
			if (var instanceof HashMap) {
				HashMap<?, ?> map = (HashMap<?, ?>) var;
				result = map.get(id);
			}
		} else if (isDoc && object != null) {
			if (object.equalsIgnoreCase("parent")) {
				RichDocumentContext parentContext = this.getParentContextEnsure();
				return parentContext.getValue(parentContext, scope, null, id);
			} else if (object.equalsIgnoreCase("parent.parent")) {
				RichDocumentContext parentContext = this.getParentContextEnsure().getParentContextEnsure();
				return parentContext.getValue(parentContext, scope, null, id);
			}
			Document document = getDocument();
			String tableKey = object;
			String columnKey = id;

			DataTable dataTable = document.get(tableKey);

			if (dataTable != null) {
				if (dataTable.size() > 0 && dataTable.getPos() < 0) {
					dataTable.setPos(0);
				}

				if (isOrignalValue()) {
					result = dataTable.getOriginalObject(columnKey);
				} else {
					result = dataTable.getObject(columnKey);
				}
				if (result == null) {
					result = getNullValue(dataTable, columnKey);
				}
			}
		} else if(result == null){
			String metaFormKey = getFormKey();
			if (metaFormKey != null && metaFormKey.length() > 0) {
				DocumentRecordDirty richDocument = this.getDocumentRecordDirty();
				if (richDocument == null) {
					return null;
				}
				MetaForm metaForm = richDocument.getMetaForm();
				IDLookup idLookup = IDLookup.getIDLookup(metaForm);
				String tableKey = idLookup.getTableKeyByFieldKey(id);

				int bookMark = richDocument.getCurrentBookMark(tableKey);
				GridRow gridRow = new GridRow(richDocument, tableKey, bookMark);

				result = richDocument.getValue(this, id, gridRow, false);
				if (result == null) {
					if (idLookup.containFieldKey(id)) {
						//上面的字段已经将依赖的表达式计算掉了,不需要使用get(这个会将表中所有延后表达式都计算掉),直接用get_impl获取表
						//DataTable dataTable = richDocument.get(idLookup.getTableKeyByFieldKey(id));
						DataTable dataTable = richDocument.get_impl(idLookup.getTableKeyByFieldKey(id));
						String columnKey = idLookup.getColumnKeyByFieldKey(id);
						result = getNullValue(dataTable, columnKey);
					}

				}
			}
		}
		return result;
	}

	private Object getNullValue(DataTable dataTable, String columnName) {
		if (dataTable == null || !dataTable.getMetaData().constains(columnName)) {
            return null;
        }
		int dataType = dataTable.getMetaData().getColumnInfo(columnName).getDataType();
		switch (dataType) {
		case DataType.LONG:
			return 0L;
		case DataType.INT:
			return 0;
		case DataType.STRING:
			return "";
		case DataType.NUMERIC:
			return BigDecimal.ZERO;
		}
		return null;
	}

	private static IFuncImplMap functionImplMap;
	private static int global_size1 = 0;
	private static int global_size2 = 0;
	@Override
	public IEval<BaseContext> getMidParser() {
		
		if (parser == null) {
			int size1 = 0, size2 = 0;
			BaseFunImplMap baseFunImplMap = MidFunctionImplMap.getMidInstance();
			try {
				Field field = BaseFunImplMap.class.getDeclaredField("functionImplMap");
				field.setAccessible(true);
				StringHashMap<IFunImpl> stringHashMap = (StringHashMap<IFunImpl>) field.get(baseFunImplMap);
				size1 = stringHashMap.size();

				IMidGlobalEnv midGlobalEnv = (IMidGlobalEnv) this.getVE().getGlobalEnv();
				BaseFunImplMap baseFunImplMap2 = midGlobalEnv.getExtMidFuncImplMap();
				if (baseFunImplMap2 != null) {
					StringHashMap<IFunImpl> stringHashMap2 = (StringHashMap<IFunImpl>)field.get(baseFunImplMap2);
					size2 = stringHashMap2.size();
				}
			} catch (NoSuchFieldException e) {
				LogSvr.getInstance().error(e.getMessage(), e);
			} catch (SecurityException e) {
				LogSvr.getInstance().error(e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				LogSvr.getInstance().error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LogSvr.getInstance().error(e.getMessage(), e);
			}
			
			if (functionImplMap == null || global_size1 != size1 || global_size2 != size2) {
				functionImplMap = getFunctionImplMap();
				global_size1 = size1;
				global_size2 = size2;
			}
			parser = new MidParser<BaseContext>(this, functionImplMap) {
				@Override
				public Object evalObject(BaseContext context, String object, String name, Object[] arguments)
						throws Throwable {
					if (object.startsWith("parent.com.")) {
						String fullName = object.substring(7) + "." + name;
						IFunImpl impl = functionImplMap.getFunctionImpl(fullName);
						if (impl != null) {
							RichDocumentContext oldContext = (RichDocumentContext) context;
							RichDocumentContext newContext = oldContext.getParentContextEnsure();
							String funName = impl.needFullName() ? fullName : name;
							Object result = impl.eval(funName, newContext, arguments, null);
							return result;
						} else {
							super.evalObject(context, object, name, arguments);
						}
					}
					return super.evalObject(context, object, name, arguments);
				}

				@Override
				public Object checkMacro(BaseContext evalContext, String object, String name) throws Throwable {
					MetaMacro metaMacro = null;
					String formKey = evalContext.getFormKey();
					if (formKey != null) {
						MetaForm metaForm = evalContext.getVE().getMetaFactory().getMetaForm(formKey);
						metaMacro = MacroUtils.findMacro(getVE().getMetaFactory(), metaForm, name);
					}
					if (metaMacro == null && dataObject != null) {
						metaMacro = MacroUtils.findMacro(getVE().getMetaFactory(), dataObject, name);
					}
					return metaMacro;
				}

				@Override
				public IEvalContext resolveObject(BaseContext self, EvalScope scope, String object) throws Throwable {
					if (!(self instanceof RichDocumentContext)) {
						throw new RuntimeException("不应该出现的错误");
					}
					if (object == null) {
						return self;
					} else if (LexDef.PARENT.equals(object)) {
						RichDocumentContext oldContext = (RichDocumentContext) self;
						RichDocumentContext newContext = oldContext.getParentContextEnsure();
						return newContext;
					} else if (object.startsWith("parent.com.")) {
						return null;
					} else {
						final Heap heap = scope.getHeap();
						if (heap != null) {
							final Object variable = heap.getVariable(object);
							if (variable != null) {
								self.setObject(variable);
								return self;
							}
						}
						throw new RuntimeException("不应该出现的错误，目前object只支持parent,可能对应的二次开发类没有注册或者方法没有实现:" + object);
					}
				}

				@Override
				public Object eval(int type, String script, BaseContext evalContext, 
						IHackEvalContext<BaseContext> hack, EvalScope scope) throws Throwable {
					//int action = Performance.startAction("Eval/", script);
					if (scope == null) {
						scope = new EvalScope();
					}
					EvalScope finalScope = scope;
					return TraceSystemManager.withTrace(()-> {
						SyntaxTree syntaxTree = super.parser(script);

						if (syntaxTree == null || syntaxTree.getRoot() == null) {
							return null;
						}
						Object result = super.eval(syntaxTree, evalContext, hack, finalScope);
	//					Object result = super.eval(type, script, evalContext, hack, scope);
						//Performance.endActive(action);
						return result;
					},this , PerformanceAttributeData.build(ActionType.EXPR.getCode(),"RichDocumentContext#MidParser#eval").assignFormula(script));

				}
			};
		}
		return parser;
	}

	/**
	 * 带跟踪信息地执行表达式
	 * @param metaObjectType
	 * @param key
	 * @param property
	 * @param formula
	 * @return
	 * @throws Throwable
	 */
	public Object evalWithTrace(MetaObjectType metaObjectType, String key, Property property, String formula) throws Throwable {
		int action = 0;
		TraceFormula trace = null;;
		if (TraceSetting.isTraceFormula(this)) {
			trace = new TraceFormula(metaObjectType, key, property, formula);
			action = Performance.startAction(trace);
		}
		Object result;
		if (TraceSetting.isJustRecordFormulaError(this)) {
			try {
				result = getMidParser().eval(ScriptType.Formula, formula);
			} catch (Throwable e) {
				String error = TraceSetting.getError(trace, e);
				SessionUICommands.addUICommand(this, new UICommand(UICommand.UI_CMD_ShowFormulaErrorInDesignMode, error));
				result = property.getDefaultValue();
			}
		} else {
			try {
				result = getMidParser().eval(ScriptType.Formula, formula);
			} catch (Throwable e) {
				String error = "计算表单：" + this.getFormKey() + "中组件：" + key + "的" + property.caption + "：" + formula + "时不正确    -》原因可能是类型为模板的表单单独打开了，请检查";
				LogSvr.getInstance().error(error, null);
				if (TraceSetting.getOperatingEnvironment(this) == AppRunType.App){
					return ExceptionUtils.rethrow(e);
				} else {
					this.getRichDocument().appendUICommand(new UICommand(UICommand.UI_CMD_ShowFormulaErrorInDesignMode, e.getMessage()));
					result = property.getDefaultValue();
				}
			}
		}
		if (action > 0) {//建议用 TraceSetting.isTraceFormula(this) 作为条件
			Performance.endActive(action, trace);
		}
		return result;
	}

	/**
	 * 确保取得新上下文对象
	 * 
	 * @return
	 * @throws Throwable
	 */
	public RichDocumentContext getParentContextEnsure() throws Throwable {
		DefaultContext ctx = this.getParentContext();
		if (ctx instanceof RichDocumentContext) {
			return (RichDocumentContext)ctx;
		}
		if (ctx == null) {
			RichDocumentContext parentContext = new RichDocumentContext(this);
			parentContext.setParas(null);
			parentContext.setConditionParas(null);
			this.setParentContext(parentContext);
			return parentContext;
			//			if (false)
			//				throw new RuntimeException("父对象不应该为空");
			//			DocumentRecordDirty parentDocument = this.getParentRichDocument();
			// parentContext = new RichDocumentContext(this);
			// parentContext.richDocument = parentDocument;
			// if (parentDocument != null) {
			// parentContext.setFormKey(parentDocument.getMetaForm().getKey());
			// }
			// this.setParentContext(parentContext);
		} else {
			return new RichDocumentContext(ctx, false, false);
		}
	}

	@Override
	public Object evalMacro(BaseContext evalContext, EvalScope scope, String name, Object macro, Object[] arguments,
			IExecutor executor) throws Throwable {
		return 	TraceSystemManager.withTrace(()->{
			MetaMacro metaMacro = (MetaMacro) macro;
			if (metaMacro != null) {
				evalDecoratorMacro(evalContext, scope, name, DecoratorType.PRE, arguments, executor);
				EvalScope oldScope = scope;
				EvalScope newScope = new EvalScope(oldScope);
				String[] args = metaMacro.getArgsList();
				if (args != null && args.length > 0) {
					Heap heap = newScope.getHeap();
					int length = args.length;
					for (int i = 0; i < length; ++i) {
						heap.addVariable(args[i], arguments[i]);
					}
				}

				String content = metaMacro.getContent();
				if (content != null && !content.isEmpty()) {
					Object returnValue = getMidParser().eval(ScriptType.Formula, content, evalContext, null, newScope);
					evalDecoratorMacro(evalContext, scope, name, DecoratorType.POST, arguments, executor);
					return returnValue;
				}
			}
			return true;
		},this,PerformanceAttributeData.build(ActionType.EXPR.getCode(),"RichDocumentContext#evalMacro"));
	}

	/**
	 * 装饰器的宏公式执行，不带返回值,如果带返回值可能会对产品的逻辑造成影响
	 * @param evalContext
	 * @param scope
	 * @param name
	 * @param decoratorType
	 * @param arguments
	 * @param executor
	 * @throws Throwable
	 */
	private void evalDecoratorMacro(BaseContext evalContext, EvalScope scope, String name, DecoratorType decoratorType, Object[] arguments,
									IExecutor executor) throws Throwable {
		MetaMacro metaMacro = null;
		String formKey = evalContext.getFormKey();
		if (formKey != null) {
			MetaForm metaForm = evalContext.getVE().getMetaFactory().getMetaForm(formKey);
			metaMacro = MacroUtils.findMacro(getVE().getMetaFactory(), metaForm, name+"@"+decoratorType.toString());
		}

		if (metaMacro == null) {
			return;
		}
		EvalScope newScope = new EvalScope(scope);
		String[] args = metaMacro.getArgsList();
		if (args != null && args.length > 0) {
			Heap heap = newScope.getHeap();
			int length = args.length;
			for (int i = 0; i < length; ++i) {
				heap.addVariable(args[i], arguments[i]);
			}
		}

		String content = metaMacro.getContent();
		if (content != null && !content.isEmpty()) {
			getMidParser().eval(ScriptType.Formula, content, evalContext, null, newScope);
		}
	}

//	public DocumentRecordDirty newOrgDataDocument(String metaFormKey) throws Throwable {
	// IMetaFactory metaFactory = getVE().getMetaFactory();
	// MetaForm metaForm = metaFactory.getMetaForm(metaFormKey);
	// MetaDataSource dataSource = metaForm.getDataSource();
	// MetaDataObject metadataObject = null;
	// if (dataSource != null) {
	// metadataObject = dataSource.getDataObject();
	// }
	// IDLookup idlookup = IDLookup.getIDLookup(metaForm);
	// OrgDataDictionaryListImpl orgDataDic=null;
	//
	// RichDocumentContext midContext = new RichDocumentContext(this);
	// if (metaFormKey.equalsIgnoreCase(V_Material.V_Material)){
	// orgDataDic = new MaterialDictionaryListImpl(midContext);
	// }else if(metaFormKey.equalsIgnoreCase(V_Customer.V_Customer)){
	// orgDataDic = new CustomerDictionaryListImpl(midContext);
	// }else if(metaFormKey.equalsIgnoreCase(V_Vendor.V_Vendor)){
	// orgDataDic = new VendorDictionaryListImpl(midContext);
	// }
	//
	// Document document = new Document(metadataObject, -1);
	//
//		MetaTableCollection metaTableCollection = metadataObject.getTableCollection();
	// Iterator<MetaTable> iter = metaTableCollection.iterator();
	// MetaTable mTable = null;
	// while (iter.hasNext()) {
	// mTable = iter.next();
	// DataTable table = DataTableUtil.newEmptyDataTable(mTable);
	// if (mTable.getKey().equalsIgnoreCase(orgDataDic.getMetaMainTableKey())) {
	// DocumentUtil.newRow(mTable, table);
	// }
	// document.add(mTable.getKey(), table);
	// }
	// document.setNew();
	// this.OID = this.applyNewOID();
	// document.setOID(OID);
	//
//		DocumentRecordDirty richDocument = new DocumentRecordDirty(this, document, metaForm);
	// richDocument.setNew(true);
	//
	// orgDataDic.NewDic(richDocument,metaForm);
	//
	// List<MetaComponent> components = metaForm.getAllComponents();
	// for (MetaComponent component : components) {
	// MetaDataBinding dataBinding = component.getDataBinding();
	// if (dataBinding == null) {
	// continue;
	// }
	// String formulaValue = dataBinding.getDefaultFormulaValue();
	// boolean hasDefaultValue = false;
	// hasDefaultValue = formulaValue != null && !formulaValue.isEmpty();
	// if (!hasDefaultValue) {
//				String defaultValue = idlookup.getDefaultValueByFieldKey(component.getKey());
	// hasDefaultValue = defaultValue != null && !defaultValue.isEmpty();
	// }
	// if (hasDefaultValue) {
	// HeadFieldLocation fieldLocation = new HeadFieldLocation(component);
	// richDocument.addDelayDefaultFormulaValue(fieldLocation);
	// }
	// }
	// this.setRichDocument(richDocument);
	// richDocument.calcDelayFormula();
	// return richDocument;
	// }
	//
//	public DocumentRecordDirty newAccDataDocument(String metaFormKey) throws Throwable {
	// IMetaFactory metaFactory = getVE().getMetaFactory();
	// MetaForm metaForm = metaFactory.getMetaForm(metaFormKey);
	// MetaDataSource dataSource = metaForm.getDataSource();
	// MetaDataObject metadataObject = null;
	// if (dataSource != null) {
	// metadataObject = dataSource.getDataObject();
	// }
	// IDLookup idlookup = IDLookup.getIDLookup(metaForm);
	// RichDocumentContext midContext = new RichDocumentContext(this);
//		OrgDataDictionaryTreeImpl orgDataDic=new AccountDictionaryTreeImpl(midContext);;
	//
	// Document document = new Document(metadataObject, -1);
	//
//		MetaTableCollection metaTableCollection = metadataObject.getTableCollection();
	// Iterator<MetaTable> iter = metaTableCollection.iterator();
	// MetaTable mTable = null;
	// while (iter.hasNext()) {
	// mTable = iter.next();
	// DataTable table = DataTableUtil.newEmptyDataTable(mTable);
	// if (mTable.getKey().equalsIgnoreCase(orgDataDic.getMetaMainTableKey())) {
	// DocumentUtil.newRow(mTable, table);
	// }
	// document.add(mTable.getKey(), table);
	// }
	// document.setNew();
	// this.OID = this.applyNewOID();
	// document.setOID(OID);
	//
//		DocumentRecordDirty richDocument = new DocumentRecordDirty(this, document, metaForm);
	// richDocument.setNew(true);
	//
	// orgDataDic.NewDic(richDocument,metaForm);
	//
	// List<MetaComponent> components = metaForm.getAllComponents();
	// for (MetaComponent component : components) {
	// MetaDataBinding dataBinding = component.getDataBinding();
	// if (dataBinding == null) {
	// continue;
	// }
	// String formulaValue = dataBinding.getDefaultFormulaValue();
	// boolean hasDefaultValue = false;
	// hasDefaultValue = formulaValue != null && !formulaValue.isEmpty();
	// if (!hasDefaultValue) {
//				String defaultValue = idlookup.getDefaultValueByFieldKey(component.getKey());
	// hasDefaultValue = defaultValue != null && !defaultValue.isEmpty();
	// }
	// if (hasDefaultValue) {
	// HeadFieldLocation fieldLocation = new HeadFieldLocation(component);
	// richDocument.addDelayDefaultFormulaValue(fieldLocation);
	// }
	// }
	// this.setRichDocument(richDocument);
	// richDocument.calcDelayFormula();
	// return richDocument;
	// }

	public DocumentRecordDirty newDocument(String metaFormKey, Document oldDocument) throws Throwable {
		return newDocument(metaFormKey, oldDocument, true);
	}

	/**
	 * 新增richDocument对象
	 * 
	 * @throws Throwable
	 */
	public DocumentRecordDirty newDocument(String metaFormKey, Document oldDocument, boolean runDefaultValue) throws Throwable {
		String oldMetaFormKey = this.getFormKey();

		// this.setFormKey(metaFormKey);
		super.setFormKey(metaFormKey);
		IMetaFactory metaFactory = getVE().getMetaFactory();
		MetaForm metaForm = metaFactory.getMetaForm(metaFormKey);
		MetaForm oldMetaForm = null;
		if (oldDocument != null) {
			oldMetaForm = metaFactory.getMetaForm(oldMetaFormKey);
		}
		MetaDataSource dataSource = metaForm.getDataSource();
		MetaDataObject metadataObject = null;
		if (dataSource != null) {
			metadataObject = dataSource.getDataObject();
		}
		// IDLookup idlookup = IDLookup.getIDLookup(metaForm);
		DocumentRecordDirty richDocument = new DocumentRecordDirty(metaForm);
		setDocument(richDocument);
		document.setNew();
		this.OID = this.applyNewOID();
		document.setOID(OID);
		if (dataSource != null && metadataObject != null) {
			// new出来的context，数据元素对象为null, 后面如果用getMidContext().getDataObject()时就取不到
			super.setDataObject(metadataObject);
			Iterator<MetaTable> it = metadataObject.getTableCollection().iterator();
			while (it.hasNext()) {
				MetaTable metaTable = it.next();
				DataTable table = DataTableUtil.newEmptyDataTable(metaTable);
				document.add(metaTable.getKey(), table);
				if (metaTable.isHead()) {
					richDocument.appendDetail(this, metaTable.getKey(), runDefaultValue);
					richDocument.addDirtyTableFlag(metaTable.getKey());
				}
			}
		}

		if (oldMetaFormKey != null && oldMetaFormKey.equals(metaFormKey) && oldDocument != null && oldDocument instanceof RichDocument) {
			richDocument.headValues.putAll(((RichDocument) oldDocument).headValues);
			richDocument.otherFieldValues.putAll(((RichDocument) oldDocument).otherFieldValues);
		}
		RichDocumentUtil.addNewRow4NoGridForm(richDocument);
		if (runDefaultValue) {
			RichDocumentUtil.dealDefaultValue(this, richDocument, oldMetaForm, oldDocument, true);
		}
		richDocument.setContext(this);
		return richDocument;
	}

    public DocumentRecordDirty defaultDocument(String metaFormKey) throws Throwable {
        return defaultDocument(metaFormKey, false);
    }
	/**
	 * 默认richDocument对象
	 * 
	 * @throws Throwable
	 */
	public DocumentRecordDirty defaultDocument(String metaFormKey, boolean needToJson) throws Throwable {
//		this.setFormKey(metaFormKey);
		super.setFormKey(metaFormKey);
		IMetaFactory metaFactory = getVE().getMetaFactory();
		MetaForm metaForm = metaFactory.getMetaForm(metaFormKey);
		MetaDataSource dataSource = metaForm.getDataSource();
		MetaDataObject metaDataObject = null;
		if (dataSource != null) {
			metaDataObject = dataSource.getDataObject();
		}
		IDLookup idlookup = IDLookup.getIDLookup(metaForm);
		DocumentRecordDirty richDocument = new DocumentRecordDirty(metaForm);
		if(document instanceof RichDocument) {
		    richDocument.setFormEntryKey(((RichDocument)this.document).getFormEntryKey());
        }
		setDocument(richDocument);
//		document.setNew();
		this.OID = this.applyNewOID();
		document.setOID(OID);
		if (dataSource != null && metaDataObject != null) {
			String mainTableKey = "";
			final MetaTable mainTable = metaDataObject.getMainTable();
			if (mainTable != null) {
				mainTableKey = mainTable.getKey();
			}
			if (metaDataObject.getTableCollection() != null) {
				Iterator<MetaTable> it = metaDataObject.getTableCollection().iterator();
				while (it.hasNext()) {
					MetaTable metaTable = it.next();
					DataTable table = DataTableUtil.newEmptyDataTable(metaTable);
					if (metaTable.isHead()) {
						DocumentUtil.newRow(metaTable, table);
						// richDocument.addDirtyTableFlag(metaTable.getKey());
					}
					document.add(metaTable.getKey(), table);
					final Integer secondaryType = metaDataObject.getSecondaryType();
					boolean noMainTable = mainTable == null
						|| secondaryType == DataObjectSecondaryType.MIGRATION
						|| secondaryType == DataObjectSecondaryType.DATAOBJECTLIST;
					if (metaTable.isHead()) {
						DataTable headDataTable = document.get(metaTable.getKey());
						if(!headDataTable.getMetaData().constains(SystemField.OID_SYS_KEY) || !headDataTable.getMetaData().constains(SystemField.SOID_SYS_KEY)) {
							continue;
						}
						if ( metaTable.getLevelID() <= 2 && !noMainTable ) {
							headDataTable.setObject(0, SystemField.OID_SYS_KEY, OID);
							headDataTable.setObject(0, SystemField.SOID_SYS_KEY, OID);
						} else {
							Long headOID = this.applyNewOID();
							headDataTable.setObject(0, SystemField.OID_SYS_KEY, headOID);
							headDataTable.setObject(0, SystemField.SOID_SYS_KEY, OID);
						}
					}
				}
			}
		}

		for (String fieldKey : idlookup.getFieldKeys()) {
			String gridKey = idlookup.getGridKeyByFieldKey(fieldKey);
			if (isBlankOrNull(gridKey) || IDLookup.isOtherField(fieldKey)) {
				String defaultFormulaValue = idlookup.getDefaultFormulaValueByFieldKey(fieldKey);
				String defaultValue = idlookup.getDefaultValueByFieldKey(fieldKey);
				if (!isBlankOrNull(defaultValue) || !isBlankOrNull(defaultFormulaValue)) {
					MetaComponent component ;
					if (metaForm.getFormType() == FormType.Template){
						 component = (MetaComponent) metaForm.getAllUIComponents().get(fieldKey);
					}else {
						 component = metaForm.componentByKey(fieldKey);
					}
					if (component == null) {
						component = idlookup.getComponentByKey(fieldKey);
					}
					if (component == null) {
						throw new Exception("组件不能为空");
					}
					richDocument.processHeadDefaultFormulaValueItem(fieldKey,
							"com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext.defaultDocument(String metaFormKey)");
				}
			}
		}
//		for (MetaGrid metaGrid : idlookup.getMetaGrids()) {
//			String gridKey = metaGrid.getKey();
//			if (isBlankOrNull(metaGrid.getParentGridKey())) {
//				richDocument.gridEmptyRow(gridKey);
//			}
//		}

		this.setDocument(richDocument);
		if (needToJson) {
			richDocument.setIgnoreArithmeticException();
		}
		richDocument.calcDelayFormula();
		richDocument.setFullData();
		return richDocument;
	}

	/**
	 * 复制新增<br/>
	 * 参照平台com.bokesoft.yes.mid.cmd.data.CopyNewCmd 逻辑处理
	 * @param metaFormKey
	 * @param richDocument
	 * @return
	 * @throws Throwable
	 */
	public RichDocument copyDocument(String metaFormKey, RichDocument richDocument) throws Throwable {
		IMetaFactory metaFactory = getVE().getMetaFactory();
		MetaForm metaForm = metaFactory.getMetaForm(metaFormKey);
		MetaDataObject metaDataObject = metaForm.getDataSource().getDataObject();
		MetaTableCollection metaTableCollection = metaDataObject.getTableCollection();
		setDocument(richDocument);
		Map<String, List<String>> ignoreKeysMap = StringHashMap.newInstance();
		Iterator<Entry<String, MetaTable>> it = metaTableCollection.entryIterator();
		
		Long OID = this.applyNewOID();
		document.setOID(OID);
		
		// FIXME:这里先写死
		IDLookup idLookup = IDLookup.getIDLookup(richDocument.getMetaForm());
		if (idLookup.containFieldKey("DocumentNumber") && idLookup.isHeadField("DocumentNumber")) {
			String documentNumber = TypeConvertor.toString(richDocument.getHeadFieldValue("DocumentNumber"));
			Map<String, Object> headInfos = this.getHeadInfos();
			if (headInfos != null) {
				headInfos.put("__CopyFromDocumentNumber", documentNumber);
			}
		}
		HashMap<String, HashMap<Long, Long>> OIDMap = new HashMap<String, HashMap<Long, Long>>();
		Entry<String, MetaTable> entry = null;
		while (it.hasNext()) {
			entry = it.next();
			MetaTable metaTable = entry.getValue();
			String tableKey = metaTable.getKey();
			DataTable table = document.get(tableKey);
			MetaComponent metaComponent = metaForm.findComponentByTable(tableKey);
			boolean copy = true;
			MetaRowTree rowTree = null;
			HashMap<Object, Object> rowTreeNewOID = new HashMap<>();
			String foreignKey = null;
			if( metaComponent != null && metaComponent.getControlType() == ControlType.GRID ) {
				MetaGrid metaGrid = (MetaGrid)metaComponent;
				rowTree = metaGrid.getDetailMetaRow().getRowTree();
				if (rowTree != null && rowTree.getTreeType() == GridTreeType.COMMON){
					foreignKey = rowTree.getForeign();
				}
				copy = metaGrid.isCopyNew();
			}
			// 对于需要存储的表，清除其系统字段，并置行状态为新增状态
			if (metaTable.isPersist() && copy) {
				if (table != null) {
					List<String> ignoreKeysKeys = new ArrayList<String>();
					ignoreKeysMap.put(tableKey, ignoreKeysKeys);

					DataTableMetaData metaData = table.getMetaData();

					Iterator<MetaColumn> metaTableIt = metaTable.iterator();
					while(metaTableIt.hasNext()) {
						MetaColumn column = metaTableIt.next();
						if (column.isSupportI18n()) {
							continue;
						}
						int dataType = column.getDataType();
						String columnKey = column.getKey();
						MetaComponent component = metaForm.getComponentByDataBinding(tableKey, columnKey);
						MetaGridCell metaGridCell = metaForm.getCellByDataBinding(tableKey, columnKey);
						
						if (SystemField.OID_SYS_KEY.equals(columnKey)) {
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								Long value = this.applyNewOID();
								Long oldValue = table.getLong(rowIndex, columnKey);
								if (!metaTable.containsKey(SystemField.SrcLangOID_SYS_KEY)) {
									HashMap<Long, Long> map = new HashMap<Long, Long>();
									if (tableKey.equals(document.getMetaDataObject().getMainTableKey())) {
										map.put(table.getLong(rowIndex, SystemField.OID_SYS_KEY), document.getOID());
									} else {
										map.put(table.getLong(rowIndex, SystemField.OID_SYS_KEY), value);
									}
									if (OIDMap.get(tableKey) == null) {
										OIDMap.put(tableKey, map);
									} else {
										OIDMap.get(tableKey).putAll(map);
									}
								}
								table.setObject(rowIndex, columnKey, value);

								// 处理树形表格，若绑定的表格主键字段是OID时，其他行的外键字段需要同步修改
								if (rowTree != null && rowTree.getTreeType() == GridTreeType.COMMON){
									if(rowTree.getParent().equalsIgnoreCase(columnKey)){
										rowTreeNewOID.put(oldValue, value);
									}
								}
							}
						} else if (SystemField.SOID_SYS_KEY.equals(columnKey)) {
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								table.setObject(rowIndex, columnKey, document.getOID());
							}
						} else if (SystemField.POID_SYS_KEY.equals(columnKey)&&!StringUtil.isBlankOrNull(metaTable.getParentKey())) {
							String parentKey = metaTable.getParentKey();
							DataTable parentTable = document.get(parentKey);
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								int parentBookmark = table.getParentBookmark(rowIndex);
								if (parentBookmark < 0) {
									table.setLong(rowIndex, columnKey, 0L);
									continue;
								}
								int parentRowIndex = -1;
								for (int parentRowIdx = 0; parentRowIdx < parentTable.size(); parentRowIdx++) {
									if (parentTable.getBookmark(parentRowIdx) == parentBookmark) {
										parentRowIndex = parentRowIdx;
										break;
									}
								}
								if (parentRowIndex < 0) {
									table.setLong(rowIndex, columnKey, 0L);
									continue;
								}
								Long poid = parentTable.getLong(parentRowIndex, SystemField.OID_SYS_KEY);
								table.setLong(rowIndex, columnKey, poid);
							}
						} else if (!SystemField.MAPCOUNT_SYS_KEY.equalsIgnoreCase(columnKey) && (component != null || metaGridCell != null)) {
							if (component != null) {
								if (!component.isCopyNew()) {
									int index = metaData.findColumnIndexByKey(component.getColumnKey());
									if (index != -1) {
										for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
											resetColumnValue(table, column, dataType, columnKey, rowIndex);
										}
									}
								} else {
									ignoreKeysKeys.add(columnKey);// 需要复制新增的字段忽略默认值
								}
							} else {
								if (!metaGridCell.isCopyNew()) {
									int index = metaData.findColumnIndexByKey(metaGridCell.getColumnKey());
									if (index != -1) {
										for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
											resetColumnValue(table, column, dataType, columnKey, rowIndex);
										}
									}
								} else {
									ignoreKeysKeys.add(columnKey);
								}
							}
						}else if (columnKey.equalsIgnoreCase(SystemField.INSTANCE_ID_SYS_KEY)) {
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								table.setObject(rowIndex, columnKey,  null);
							}
						} else if (SystemField.isSystemField(columnKey) 
								&& !SystemField.SEQUENCE_SYS_KEY.equals(columnKey)) { // SEQUENCE值会影响界面上的明细行排序，需要保留， 否则会导致复制新增后明细行排序不对
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								// 版本号重置为0
								if (columnKey.equalsIgnoreCase(SystemField.VERID_SYS_KEY) || columnKey.equalsIgnoreCase(SystemField.DVERID_SYS_KEY)) {
									 Row row = DataTableExUtil.getRowByIndex(table, rowIndex);
									 int verIDColumnIndex = table.getMetaData().findColumnIndexByKey(columnKey);
									 row.setObject(verIDColumnIndex, 0, false);
								} else if (metaTable.isT()) {
									table.setObject(rowIndex, columnKey, TypeConvertor.toDataType(dataType, table.getObject(rowIndex, columnKey)));
								} else {
									resetColumnValue(table, column, dataType, columnKey, rowIndex);
								}
							}
						} else if (metaTable.isT() && columnKey.equalsIgnoreCase(SystemField.SrcLangOID_SYS_KEY)) {
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								String srcTableKey = tableKey.substring(0, tableKey.length() - 2);
								Long oid = OIDMap.get(srcTableKey).get(table.getLong(rowIndex, SystemField.SrcLangOID_SYS_KEY));
								table.setObject(rowIndex, columnKey, oid);
							}
						}
					}

					// 为树形表格外键字段赋值
					if (foreignKey != null && !rowTreeNewOID.isEmpty()){
						for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
							Object value = table.getObject(rowIndex, foreignKey);
							Object newValue = rowTreeNewOID.get(value);
							if (newValue != null){
								table.setObject(rowIndex, foreignKey, newValue);
							}
						}
					}
				}
			} else {
				// 如果不需要存储的表，直接清除所有行
				if (table != null) {
					if (metaTable.isAutoGen()) {
						DataTableMetaData metaData = table.getMetaData();
						for (int i = 0, size = metaData.getColumnCount(); i < size; i++) {
							ColumnInfo info = metaData.getColumnInfo(i);
							int dataType = info.getDataType();
							for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
								table.setObject(rowIndex, i, TypeConvertor.toDataType(dataType, null));
							}
						}
					} else {
						table.clear();
					}
				}
			}
			if (table != null) {
				table.setNew();
			}
		}
		//复制新增出来的document不应该有原docment的tableFilterMap
		richDocument.setTableFilterMap(null);
		
		document.setNew();
		document.clearAllShadow();

		DataTable mainTable = document.get(metaDataObject.getMainTableKey());
		if (mainTable != null && mainTable.first()) {
			mainTable.setLong(SystemField.OID_SYS_KEY, OID);
			if (mainTable.getMetaData().constains(SystemField.ENABLE_DICT_KEY)) {
				mainTable.setInt(SystemField.ENABLE_DICT_KEY, 1);
			}
		}
		// 处理默认值
		dealDefaultValueforCopyNew(richDocument, metaForm,ignoreKeysMap);
		richDocument.setFullData();
		
		// 移除工作流相关数据
		dealBPMAndWorkItemInfo(richDocument);
		//移除字典不启用的值
		richDocument.removeDictValue(this,richDocument.getMetaForm());
		
		return richDocument;
	}

	/**
	 * 考虑MetaColumn是否有默认值，处理方式同com.bokesoft.yigo.tools.document.DocumentUtil.newRow
	 * 如果没有默认值，则设为空
	 * @param table
	 * @param column
	 * @param dataType
	 * @param columnKey
	 * @param rowIndex
	 * @throws Throwable
	 */
	private void resetColumnValue(DataTable table, MetaColumn column, int dataType, String columnKey, int rowIndex) throws Throwable {
		String defaultValue = column.getDefaultValue();
		if (ERPStringUtil.isBlankOrNull(defaultValue)) {
			table.setObject(rowIndex, columnKey, TypeConvertor.toDataType(dataType, null));
		} else {
			Object value = DBDataConvertor.toConstValue(column.getDataType(), defaultValue);
			table.setObject(rowIndex, columnKey, value);
		}
	}

	/**
	 * 移除工作流相关数据
	 * @param richDocument
	 */
	private void dealBPMAndWorkItemInfo(RichDocument richDocument){
		List<String> bpmWorkitemKeys = Arrays.asList(BPMKeys.STATE_MACHINE, BPMKeys.WORKITEM_INFO, BPMKeys.LoadBPM_KEY);
		for (String bpmWorkitemKey : bpmWorkitemKeys) {
			richDocument.removeExpandData(bpmWorkitemKey);
		}
	}

//	private void newEmptyRow_defaultValue(DocumentRecordDirty richDocument, IDLookup idlookup, String gridKey,
//			MetaTable metaTable) throws Throwable {
//		// 增加空白行的处理
//		MetaGrid metaGrid = idlookup.getMetaGridByGridKey(gridKey);
//		for (MetaColumn metaColumn : metaTable.items()) {
//			MetaGridCell metaCell = metaGrid.getMetaCellByColumnKey(metaColumn.getKey());
//			if (metaCell == null) {
//				continue;
//			}
//			String cellKey = metaCell.getKey();
//			String defaultFormulaValue = idlookup.getDefaultFormulaValueByFieldKey(cellKey);
//			String defaultValue = idlookup.getDefaultValueByFieldKey(cellKey);
//			if (!isBlankOrNull(defaultValue) || !isBlankOrNull(defaultFormulaValue)) {
//				GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaCell,
//						metaTable.getKey());
//				richDocument.addDelayDefaultFormulaValue(cellFieldLocation);
//			}
//		}
//	}

	/**
	 * 复制新增时对不需要复制新增的字段进行默认值计算
	 * 
	 * @throws Throwable
	 */
	public void dealDefaultValueforCopyNew(RichDocument richDocument, MetaForm metaForm,
			Map<String, List<String>> ignoreKeys) throws Throwable {
		if (metaForm == null) {
			return;
		}
		document = richDocument;
		IDLookup idlookup = IDLookup.getIDLookup(metaForm);
		Collection<String> fieldKeys = idlookup.getFieldKeys();
		// 处理表头字段的默认值
		for (String fieldKey : fieldKeys) {
			if (fieldKey.length() == 0) {
				continue;
			}
			if(idlookup.isGridTotalRowFields(fieldKey)) {
				continue;
			}
			String gridKey = idlookup.getGridKeyByFieldKey(fieldKey);
			if (gridKey == null || gridKey.length() == 0) {
				String tableKey = idlookup.getTableKeyByFieldKey(fieldKey);
				if (tableKey == null || tableKey.length() == 0) {
					continue;
				}
				String columnKey = idlookup.getColumnKeyByFieldKey(fieldKey);
				if (ignoreKeys != null && ignoreKeys.containsKey(tableKey)
						&& ignoreKeys.get(tableKey).indexOf(columnKey) != -1) {
					continue;
				}
				richDocument.processHeadDefaultFormulaValueItem(fieldKey,
						"com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext.dealDefaultValueforCopyNew()");
			}else {
				MetaGridCell metaGridCell = idlookup.getGridCellByKey(fieldKey);
				if (metaGridCell != null && metaGridCell.isSelect()) {
					continue;
				}
				// 处理明细表格的字段的默认值
				String tableKey = idlookup.getTableKeyByFieldKey(fieldKey);
				MetaTable metaTable = metaForm.getMetaTable(tableKey);
				if (metaTable == null) {
					continue;
				}
				if (metaTable.getSourceType() == TableSourceType.UNKNOWN) {
					DataTable dataTable = document.get(tableKey);
					if (dataTable != null && dataTable.size() == 0) {
						DocumentUtil.newRow(metaTable, dataTable);
					}
				} else {
					DataTable dataTable = document.get(tableKey);
					if (dataTable == null) {
						continue;
					}
					//遍历每个字段，并判断是否计算默认值
					for (int i = 0; i < dataTable.size(); i++) {
//						GridRow gridRow = new GridRow(richDocument, tableKey, dataTable.getBookmark(i));
						Map<String, List<String>> columnKeysAndFieldKeys =
								idlookup.getColumKeysAndFieldListKeys(tableKey);
						if (StringUtil.isBlankOrNull(columnKeysAndFieldKeys)) {
							continue;
						}
						String columnKey = idlookup.getColumnKeyByFieldKey(fieldKey);
						if (ignoreKeys != null && ignoreKeys.containsKey(tableKey)
								&& ignoreKeys.get(tableKey).indexOf(columnKey) != -1) {
							continue;
						}
						richDocument.processDtlDefaultFormulaValueItem(fieldKey, dataTable.getBookmark(i),
								"com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocumentContext.dealDefaultValueforCopyNew()");
					}
				}
			}
		}
		//richDocument.calcDelayFormula(this);
	}

	public JSONObject getDirtyJSON() throws Throwable {
		DocumentRecordDirty doc = this.getDocumentRecordDirty(); // TODO: 这一段收集form的paras,这个做法需要确认，这里只是临时先实现一下
//		doc.calcDelayFormula(this);
//		Paras paras = this.getParas();
//		if (paras != null) {
//			doc.appendUICommand(new UICommand(UICommand.UI_CMD_UpdateFormParas, paras.toJSON()));
//		}
//		DefaultContext parentContext = this.getParentContext();
//		if (parentContext != null) {
//			paras = parentContext.getParas();
//			if (paras != null) {
//				doc.appendUICommand(new UICommand(UICommand.UI_CMD_UpdateParentFormParas, paras.toJSON()));
//			}
//		}

		JSONObject result = new JSONObject();
		JSONArray formDirtyDatas = new JSONArray();
		JSONObject obj = new JSONObject();
		JSONObject docJson = doc.getDirtyJSON(this);
		String formKey = this.getFormKey();
		obj.put("formKey", formKey);
		obj.put("dirtyData", docJson);
		formDirtyDatas.put(obj);
		DocumentRecordDirty parentDocument = getParentRichDocument();
		if (parentDocument != null) {
			JSONObject parentDocJson = parentDocument.getDirtyJSON(getParentContextEnsure());
			String parentFormKey = parentDocument.getMetaForm().getKey();
			obj = new JSONObject();
			obj.put("formKey", parentFormKey);
			obj.put("dirtyData", parentDocJson);
			obj.put(parentFormKey, parentDocJson);
			obj.put("isParentForm", true);
			formDirtyDatas.put(obj);
		}
		result.put("formDirtyDatas", formDirtyDatas);

//		FilterMap filterMap = (FilterMap) this.getPara(RichDocumentEvalMacroCmd.FilterMap_Key);
		FilterMap filterMap = doc.getFilterMap();
		if (filterMap == null || (filterMap.getOID() <= 0 && filterMap.size() == 0)) {
		} else {
			result.put("filterMap", filterMap.toJSON());
		}
		Map<String, Object> tempSessionPara = this.getVE().getEnv().getTempSessionPara();
		if (tempSessionPara.size()>0){
			ArrayList<String> sessionList = new ArrayList<>();
			for (Map.Entry<String, Object> entry : tempSessionPara.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				sessionList.add(key+":"+value);
			}
			result.put("sessionPara",sessionList);
		}
		return result;
	}

	private boolean isBlankOrNull(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public Object checkMacro(BaseContext evalContext, String name) throws Throwable {
		MetaMacro metaMacro = null;
		String formKey = evalContext.getFormKey();
		if (formKey != null) {
			MetaForm metaForm = evalContext.getVE().getMetaFactory().getMetaForm(formKey);
			metaMacro = MacroUtils.findMacro(getVE().getMetaFactory(), metaForm, name);
		}
		if (metaMacro == null && this.dataObject != null) {
			metaMacro = MacroUtils.findMacro(getVE().getMetaFactory(), dataObject, name);
		}
		return metaMacro;
	}

	public void clear() throws Throwable {
		DefaultContext parentContext = this.getParentContext();
		this.setParentContext(null);
		if (parentContext != null && parentContext instanceof RichDocumentContext) {
			//RichDocumentContext parent = (RichDocumentContext) this.getParentContext();
			//parent.clear();
			((RichDocumentContext) parentContext).clear();
		}

		// 应该先clear，但是平台没有方法
		this.setParas(null);
		this.setConditionParas(null);
		this.parser = null;

		if (this.getContextContainer() != null) {
			// 没法处理
        }

		DocumentRecordDirty documentRecordDirty = this.getDocumentRecordDirty();
		if (documentRecordDirty != null) {
			documentRecordDirty.clear();
			this.setDocument(null);
		}
	}

    /**
     * 清除Context引用，防止不再使用的document无法被GC
     */
    public void clearContextContainer() {
        ContextContainer contextContainer = this.getContextContainer();
        if (contextContainer != null) {
            contextContainer.clear();
        }
    }

	public Long getClientID() {
		return VarUtil.toLong(getEnv().get("ClientID"));
	}
	
	@Override
	public void setDocument(Document document) {
		Document _document = this.document;
		if (_document == document) {
			return;
		}
		// 下面的判断做不到，主要原因是目前表达式计算无法传递Context或Document对象，比如RichDocumentEvalMacroCmd后，根据原先的context计算DirtyJson
		//if (_document != null && document != null) {
		//	throw new RuntimeException("Document只能属于一个Context对象，一个Context最多拥有一个Document，不支持跨上下文传递Document对象。请联系开发人员，谢谢。");
		//}
		super.setDocument(document);
		if (document instanceof RichDocument) {
			RichDocument richDocument = (RichDocument) document;
			formKey = richDocument.getMetaForm().getKey();
			richDocument.setContext(this);
		}
	}
	
	/**** 下面的方法从MidContext中拷贝过来 ****/
	
	/**
	 * 新建上下文对象，注意：这个方法连接数据库时，创建新的数据库连接
	 * 仅内部使用，外部请使用execInNewTrans方法或者RichDocumentContextUtil.execInNewTrans方法
	 * @return
	 * @throws Throwable
	 */
	protected RichDocumentContext newMidContext() throws Throwable {
		MidVE newVE = (MidVE) getVE().clone();
		RichDocumentContext context = new RichDocumentContext(newVE);
		context.setConditionParas(getConditionParas());
		context.setParentContext(getParentContext());
		context.setFormKey(getFormKey());
        Paras paras = getParas();
        if (paras != null) {
            Paras newParas = new Paras();
            Iterator<Entry<String, Para>> iterator = paras.iterator();
            while (iterator.hasNext()) {
                Entry<String, Para> entry = iterator.next();
                newParas.put(entry.getKey(), entry.getValue().getValue(), false);
            }
            context.setParas(newParas);
        }
		context.setOperatorParas(getOperatorParas());
		context.setHeadInfos(this);
		context.enter(this.getNowState());
		return context;
	}

    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该方法在try代码后默认会提交一次事务（如果不需要提交，请调用带isCommit参数的方法）,该事务始终会close,有异常时始终会rollback
     *
     * @param action 要执行的代码
     * @throws Throwable
     */
    public void execInNewTrans(ITransAction action) throws Throwable {
        execInNewTrans(true, action, null, null);
    }

    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该方法在try代码后默认会提交一次事务（如果不需要提交，请调用带isCommit参数的方法）,该事务始终会close,有异常时始终会rollback
     * @param action 要执行的代码
     * @param catchAction 错误处理，里面需自行抛出异常
     * @throws Throwable
     */
    public void execInNewTrans(ITransAction action, ITransExceptionHandler catchAction) throws Throwable {
        execInNewTrans(true, action, catchAction, null);
    }

    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该方法在try代码后默认会提交一次事务（如果不需要提交，请调用带isCommit参数的方法）,该事务始终会close,有异常时始终会rollback
     * @param action 要执行的代码
     * @param finallyAction 最终处理
     * @throws Throwable
     */
    public void execInNewTrans(ITransAction action, ITransFinalizer finallyAction) throws Throwable {
        execInNewTrans(true, action, null, finallyAction);
    }


    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该方法在try代码后默认会提交一次事务（如果不需要提交，请调用带isCommit参数的方法）,该事务始终会close,有异常时始终会rollback
     * @param action 要执行的代码
     * @param catchAction 错误处理，里面需自行抛出异常
     * @param finallyAction 最终处理
     * @throws Throwable
     */
    public void execInNewTrans(ITransAction action, ITransExceptionHandler catchAction, ITransFinalizer finallyAction) throws Throwable {
        execInNewTrans(true, action, catchAction, finallyAction);
    }


    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该事务始终会close,有异常时始终会rollback
     * @param isCommit 是否要在try代码最后提交事务
     * @param action 要执行的代码
     * @throws Throwable
     */
    public void execInNewTrans(boolean isCommit, ITransAction action) throws Throwable {
        execInNewTrans(isCommit, action, null, null);
    }

    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该事务始终会close,有异常时始终会rollback
     * @param isCommit 是否要在try代码最后提交事务
     * @param action 要执行的代码
     * @param catchAction 错误处理，里面需自行抛出异常
     * @throws Throwable
     */
    public void execInNewTrans(boolean isCommit, ITransAction action, ITransExceptionHandler catchAction) throws Throwable {
        execInNewTrans(isCommit, action, catchAction, null);
    }

    /**
     * 新增一个事务，并执行一个方法，参数为新建的RichDocumentContext。该事务始终会close,有异常时始终会rollback
     * @param isCommit 是否要在try代码最后提交事务
     * @param action 要执行的代码
     * @param catchAction 错误处理，里面需自行抛出异常
     * @param finallyAction 最终处理
     * @throws Throwable
     */
    public void execInNewTrans(boolean isCommit,ITransAction action, ITransExceptionHandler catchAction, ITransFinalizer finallyAction) throws Throwable {
        RichDocumentContext newContext = null;
        try {
            newContext = newMidContext();
            action.execute(newContext);
            if (isCommit) {
                newContext.commit();
            }
        } catch (Throwable e) {
            if (newContext != null) {
                newContext.rollback();
            }
            if (catchAction == null || newContext == null) {
                throw e;
            } else {
                catchAction.handle(e, newContext);
            }
        } finally {
            if (finallyAction != null && newContext != null) {
                finallyAction.run(newContext);
            }
            if (newContext != null) {
                newContext.close();
            }
        }
    }
	
	public RichDocument getRichDocument() {
		return (RichDocument) this.document;
	}

	public RichDocument getParentDocument() throws Throwable {
		if (this.getParentContext() instanceof RichDocumentContext) {
			RichDocumentContext parentContext = (RichDocumentContext) this.getParentContext();
			if (parentContext != null) {
				return parentContext.getRichDocument();
			}
		}
		return null;
	}
	
	public Object evalFormula(String formula, String desc) throws Throwable {
		return TraceSystemManager.withTrace(()-> {
			return this.getMidParser().eval(ScriptType.Formula, formula);
		}, this, PerformanceAttributeData.build(ActionType.EXPR.getCode(),"RichDocumentContext#evalFormula").assignFormula(formula));

	}

	/**
	 * 支持EvalScope参数，可利用heap传入参数变量
	 *
	 * @param formula
	 * @param desc
	 * @param evalScope
	 * @return
	 * @throws Throwable
	 */
	public Object evalFormula(String formula, String desc, EvalScope evalScope) throws Throwable {
		return this.getMidParser().eval(ScriptType.Formula, formula, null, null, evalScope);
	}

	/**
	 * 提交，请使用commit()方法
	 * @throws Throwable
	 */
	@Deprecated
	public void setComplete() throws Throwable {
		super.commit();
	}
	
	/**
	 * 回滚，请使用rollback()方法
	 * @throws Throwable
	 */
	@Deprecated
	public void setFail() throws Throwable {
		super.rollback();
	}
	
	public DataTable getResultSet(SqlString sql) throws Throwable {
		DataTable result = getDBManager().execPrepareQuery(sql.getSql(), sql.getParameters());
		if (!result.first()) {
			result.beforeFirst();
		}
		return result;
	}

	public DataTable getResultSet(String metaTableKey, SqlString sql) throws Throwable {
		DataTable dataTable = getDBManager().execPrepareQuery(sql.getSql(), sql.getParameters());
		dataTable.batchUpdate();
		return dataTable;
	}

	public DataTable getPrepareResultSet(String sql, Object[] arguments) throws Throwable {
		DataTable result = getDBManager().execPrepareQuery(sql, arguments);
		result.batchUpdate();
		return result;
	}
	
	public DataTable getPrepareResultSet(String metaTableKey, String sql, Object[] arguments) throws Throwable {
		DataTable dataTable = getDBManager().execPrepareQuery(sql, arguments);
		return dataTable;
	}

	public void executeUpdate(SqlString s) throws Throwable {
		getDBManager().execPrepareUpdate(s.getSql(),s.getParameters());
	}

	public int executePrepareUpdate(String s, Object[] aobj) throws Throwable {
		return getDBManager().execPrepareUpdate(s, aobj);
	}
	
	public IMetaFactory getMetaFactory() {
		return getVE().getMetaFactory();
	}
	
	public IMetaFactory getMetaFactory(boolean isProcessScope) throws Throwable {
		IMetaFactory metaFactory = getVE().getMetaFactory();
		if (metaFactory instanceof ERPMetaFactory) {
			ERPMetaFactory erpMetaFactory = (ERPMetaFactory) metaFactory;
			if (isProcessScope) {
				return erpMetaFactory;
			} else {
				return new ERPMetaFactory(erpMetaFactory.getInnerMetaFactory());
			}
		} else {
			return metaFactory;
		}
	}

    @Override
	public IDictCacheProxy getDictCache() {
		return super.getDictCache();
	}

	public DefaultContext getDefaultContext() {
		return this;
	}
	
	public Long getAutoID() throws Throwable {
		return applyNewOID();
	}

	public Item getDicItem(String itemKey, Long oid) throws Throwable {
		return this.getDictCache().getItem(itemKey, oid);
	}
	
	public int getDBType() throws Throwable {
		return getVE().getDSN().getDBType();
	}

	@Override
	public Object getPara(String paraKey) {
		return getPara(paraKey, true);
	}
	
	@Deprecated
	public Object getPara(String paraKey, boolean check) {
		if (check) {
			checkScope(paraKey);
		}

		Paras paras = super.getParas();
		Object result = (paras != null ? paras.get(paraKey, check) : null);
//		Object result = super.getPara(paraKey);

		//FIXME:这个地方怕改动太大， 先这样特殊处理吧。附件上传文件最大大小限制
		// 不知道这里为什么要加这个处理
		if (result == null && !"MaxSize".equals(paraKey)) {
			return "";
		}
		return result;
	}

	@Override
	public void setPara(String paraKey, Object paraValue) {
		setPara(paraKey, paraValue, true);
	}

	@Deprecated
	public void setPara(String paraKey, Object paraValue, boolean check) {
		
		if (check) {
			checkScope(paraKey);
		}
		
		if (paraValue instanceof SqlString) {
			// 标识参数类型为SqlString的参数值不允许改变
			((SqlString) paraValue).setFinalResult();
		}
		ensureParas().put(paraKey, paraValue, check);
	}

	public void removePara(String paraKey) {
		ensureParas().remove(paraKey);
	}
	
	private void checkScope(String paraKey) {
		if (!ParaDefines.getParaCheck()) {
			//以后!ParaDefines.instance.containsKey(paraKey) 这个判断去掉
			return;
		}
		
		Document document = this.getDocument();
		if (!(document instanceof RichDocument)) {
			return;
		}
		RichDocument doc = this.getRichDocument();
		if (doc == null) {
			return;
		}

		MetaForm metaForm = doc.getMetaForm();
		String metaFormKey = metaForm.getKey();
        String extend = metaForm.getExtend();
        if (!StringUtil.isBlankOrNull(extend)) {
            //取原单
            metaFormKey = extend;
            try {
                metaForm = MetaFactory.getGlobalInstance().getMetaForm(metaFormKey);
            } catch (Throwable e) {
            }
        }

		if (metaFormKey.equalsIgnoreCase("DataInterfaceTest")) {
			// 接口测试的不检查
			return;
		}

		IMetaProject project = metaForm.getProject();
		
        if (!ParaDefines.AllConfig.contains(project.getKey())) {
            // 没有注册para规则的模块也不检查
            return;
        }        

        if (!ParaDefines.instance.containsKey(paraKey)) {
            throw new RuntimeException("YigoERP产品中未定义" + paraKey + "的Para参数，请检查！");
        }

        Pair<ParaScopeDefine, ParaDefine> pair = ParaDefines.instance.get(paraKey);
        if (pair == null) {
            // 前面的代码已经检查，这里不会出现了
            throw new RuntimeException("定义Para参数" + paraKey + "的时候未定义对应的ParaDefine注解!");
        }

		String projectKey = pair.getLeft().scope().getProjectKey();
		
		if (projectKey == StringUtil.EMPTY_STRING) {
			// 全局的不检查
			return;
		}
		
		if(projectKey.equals(project.getKey())) {
			//模块检查通过			
			return ;
		}
		throw new RuntimeException(
				"YigoERP产品中定义" + paraKey + "的Para参数仅在" + projectKey + "模块表单使用，不能在" + project.getKey() + "模块表单使用，请检查！");
	}
	
	/** 当前上下文环境所对应的界面位置 */
	private LocationMap uiLocationMap = null;
	
	public void setUILocationMap(LocationMap locationMap) {
		this.uiLocationMap = locationMap;
	}
	
	public LocationMap getUILocationMap() {
		return this.uiLocationMap;
	}
	
	public RichDocumentContext getRichContext() {
		return this;
	}

	private StringHashMap<String> operatorParas = null;

	protected StringHashMap<String> getOperatorParas() {
		return operatorParas;
	}

	protected void setOperatorParas(StringHashMap<String> operatorParas) {
		this.operatorParas = operatorParas;
	}

	public String getOperatorPara(String paraID) throws Throwable {
        if (!getMetaFactory().getParameter().containsKey(paraID)) {
			throw new RuntimeException("YigoERP产品中未定义" + paraID + "的ParameterID参数，请检查！");
        }
		if (operatorParas == null) {
			return null;
		}
		return operatorParas.get(paraID);
	}


	public void setOperatorPara(String paraID, String value) throws Throwable {
		if (!getMetaFactory().getParameter().containsKey(paraID)) {
			throw new RuntimeException("YigoERP产品中未定义" + paraID + "的ParameterID参数，请检查！");
		}
		if (operatorParas == null) {
			operatorParas = new StringHashMap<>();
		}
		if (value == null || value.isEmpty()) {
			operatorParas.remove(paraID);
		} else {
			operatorParas.put(paraID, value);
		}
	}

	public static final String ON_LINE = "ON_LINE";
	public static final String OFF_LINE = "OFF_LINE";

	/**
	 * 进入联机状态
	 */
	public void enterOnLine(){
		if (states == null) {
			states = new Stack<>();
		}else {
			old_state = states.pop();
		}
		this.enter(ON_LINE);
	}

	/**
	 * 进入脱机状态
	 */
	public void enterOffLine(){
		if (states == null) {
			states = new Stack<>();
		}else {
			old_state = states.pop();
		}
		this.enter(OFF_LINE);
	}

	public boolean hasState(){
		return states==null||states.empty();
	}

	public String getNowState(){
		if(states==null){
			return "";
		}
		return states.peek();
	}

	public boolean isOnLine(){
		if(states==null){
			return false;
		}
		return states.peek().equals(ON_LINE);
	}

	public boolean isOffLine(){
		if(states==null){
			return false;
		}
		return states.peek().equals(OFF_LINE);
	}

	private String enter(String state){
		if(StringUtil.isBlankOrStrNull(state)){
			return state;
		}
		if (states == null) {
			states = new Stack<>();
		}else if(!states.empty()){
			old_state = states.pop();
		}
		states.push(state);
		return states.peek();
	}

	/**
	 * 退出当前状态，如果有旧状态则还原为旧状态
	 */
	public String exit(){
		if(states==null){
			return "";
		}
		states.pop();
		if(!StringUtil.isBlankOrStrNull(old_state)){
			return this.enter(old_state);
		}
		return "";
	}
}
