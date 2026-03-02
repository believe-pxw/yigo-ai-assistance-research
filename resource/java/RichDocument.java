package com.bokesoft.yes.mid.cmd.richdocument.strut;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.bokesoft.distro.tech.yigosupport.extension.performance.impl.ActionType;
import com.bokesoft.distro.tech.yigosupport.extension.performance.impl.PerformanceAttributeData;
import com.bokesoft.yes.mid.dbcache.preload.PreLoadData;
import com.bokesoft.yigo.common.trace.TraceSystemManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bokesoft.erp.ERPSystemField;
import com.bokesoft.erp.mid.util.ArchiveDBUtil;
import com.bokesoft.erp.performance.trace.MetaObjectType;
import com.bokesoft.erp.performance.trace.Property;
import com.bokesoft.erp.performance.trace.TraceSetting;
import com.bokesoft.yes.bpm.meta.transform.BPMKeys;
import com.bokesoft.yes.common.json.JSONHelper;
import com.bokesoft.yes.common.struct.StringHashMap;
import com.bokesoft.yes.common.util.DBDataConvertor;
import com.bokesoft.yes.common.util.StringUtil;
import com.bokesoft.yes.erp.config.MetaFormNODBProcess;
import com.bokesoft.yes.erp.message.MessageFacade;
import com.bokesoft.yes.erp.scope.EffectScopeInDoc;
import com.bokesoft.yes.erp.scope.FormulaScope;
import com.bokesoft.yes.erp.scope.MetaFormAllFormulScopeCache;
import com.bokesoft.yes.erp.scope.MetaFormAllFormulaScope;
import com.bokesoft.yes.erp.scope.ScopeTree;
import com.bokesoft.yes.erp.scope.ScopeTreeBuilder;
import com.bokesoft.yes.log.LogSvr;
import com.bokesoft.yes.mid.authority.util.AuthorityCheckUtil;
import com.bokesoft.yes.mid.authority.util.AuthorityParaUtil;
import com.bokesoft.yes.mid.base.SvrInfo;
import com.bokesoft.yes.mid.cmd.calcdefaultformulavalue.calc.CalcTreeCache;
import com.bokesoft.yes.mid.cmd.richdocument.api.DynamicFieldCheckRule;
import com.bokesoft.yes.mid.cmd.richdocument.checkvalid.CheckValidExtensionPointManager;
import com.bokesoft.yes.mid.cmd.richdocument.delay.EffectScopeMap;
import com.bokesoft.yes.mid.cmd.richdocument.delay.FormulaItem;
import com.bokesoft.yes.mid.cmd.richdocument.delay.TableKeyAndBookmark;
import com.bokesoft.yes.mid.cmd.richdocument.delay.TableRelation;
import com.bokesoft.yes.mid.cmd.richdocument.dictfilter.checker.CachedDictFilterChecker;
import com.bokesoft.yes.mid.cmd.richdocument.dictfilter.checker.DefaultComboChecker;
import com.bokesoft.yes.mid.cmd.richdocument.dictfilter.checker.IComboChecker;
import com.bokesoft.yes.mid.cmd.richdocument.dictfilter.checker.IDictFilterChecker;
import com.bokesoft.yes.mid.cmd.richdocument.expand.ERPVirtualGrid;
import com.bokesoft.yes.mid.cmd.richdocument.expand.ExpandVirtualGridManager;
import com.bokesoft.yes.mid.cmd.richdocument.expand.model.ExpandDataModel;
import com.bokesoft.yes.mid.cmd.richdocument.expand.model.ExpandRowModel;
import com.bokesoft.yes.mid.cmd.richdocument.expand.model.multikey.TableMultiKeyInfo;
import com.bokesoft.yes.mid.cmd.richdocument.strut.uiprocess.CheckRuleTreeCache;
import com.bokesoft.yes.mid.cmd.richdocument.strut.uiprocess.EnableTreeCache;
import com.bokesoft.yes.mid.cmd.richdocument.strut.uiprocess.FieldUIExtensionPointManager;
import com.bokesoft.yes.mid.cmd.richdocument.strut.uiprocess.VisibleTreeCache;
import com.bokesoft.yes.mid.cmd.richdocument.strut.variant.VariantUtil;
import com.bokesoft.yes.mid.dbcache.datatable.DataTableExUtil;
import com.bokesoft.yes.mid.parameterizedsql.SqlString;
import com.bokesoft.yes.mid.parameterizedsql.SqlStringUtil;
import com.bokesoft.yes.mid.rights.IRightsProvider;
import com.bokesoft.yes.mid.rights.RightsProviderFactory;
import com.bokesoft.yes.parser.EvalScope;
import com.bokesoft.yes.struct.datatable.Row;
import com.bokesoft.yes.struct.document.DocumentJSONConstants;
import com.bokesoft.yes.struct.document.ExpandDataType;
import com.bokesoft.yes.tools.dic.filter.BaseItemFilter;
import com.bokesoft.yes.tools.dic.proxy.IDictCacheProxy;
import com.bokesoft.yes.util.DataConstant;
import com.bokesoft.yes.util.DictFilterSqlUtil;
import com.bokesoft.yes.util.ERPDateUtil;
import com.bokesoft.yes.util.ERPStringUtil;
import com.bokesoft.yes.util.JsonUtils;
import com.bokesoft.yes.util.RefParameter;
import com.bokesoft.yes.view.uistruct.IExprItemObject;
import com.bokesoft.yes.view.uistruct.calc.CalcAffectItemSet;
import com.bokesoft.yes.view.uistruct.calc.CalcItem;
import com.bokesoft.yes.view.uistruct.calc.CalcItemSet;
import com.bokesoft.yes.view.uistruct.calc.CalcTree;
import com.bokesoft.yes.view.uistruct.checkrule.CheckRuleAffectItemSet;
import com.bokesoft.yes.view.uistruct.checkrule.CheckRuleItem;
import com.bokesoft.yes.view.uistruct.checkrule.CheckRuleItemSet;
import com.bokesoft.yes.view.uistruct.checkrule.CheckRuleTree;
import com.bokesoft.yes.view.uistruct.enable.EnableAffectItemSet;
import com.bokesoft.yes.view.uistruct.enable.EnableItem;
import com.bokesoft.yes.view.uistruct.enable.EnableItemSet;
import com.bokesoft.yes.view.uistruct.enable.EnableTree;
import com.bokesoft.yes.view.uistruct.visible.VisibleAffectItemSet;
import com.bokesoft.yes.view.uistruct.visible.VisibleItem;
import com.bokesoft.yes.view.uistruct.visible.VisibleItemSet;
import com.bokesoft.yes.view.uistruct.visible.VisibleTree;
import com.bokesoft.yigo.common.def.AppRunType;
import com.bokesoft.yigo.common.def.ControlType;
import com.bokesoft.yigo.common.def.DataObjectSecondaryType;
import com.bokesoft.yigo.common.def.DataType;
import com.bokesoft.yigo.common.def.DictFilterType;
import com.bokesoft.yigo.common.def.DictStateMask;
import com.bokesoft.yigo.common.def.FilterValueType;
import com.bokesoft.yigo.common.def.FormType;
import com.bokesoft.yigo.common.def.OperationState;
import com.bokesoft.yigo.common.def.ScriptType;
import com.bokesoft.yigo.common.def.SystemField;
import com.bokesoft.yigo.common.def.TableMode;
import com.bokesoft.yigo.common.def.TableSourceType;
import com.bokesoft.yigo.common.json.JSONSerializable;
import com.bokesoft.yigo.common.util.TypeConvertor;
import com.bokesoft.yigo.meta.base.AbstractMetaObject;
import com.bokesoft.yigo.meta.base.KeyPairCompositeObject;
import com.bokesoft.yigo.meta.base.KeyPairMetaObject;
import com.bokesoft.yigo.meta.common.MetaBaseScript;
import com.bokesoft.yigo.meta.commondef.MetaOperation;
import com.bokesoft.yigo.meta.dataelement.MetaDataElement;
import com.bokesoft.yigo.meta.dataobject.MetaColumn;
import com.bokesoft.yigo.meta.dataobject.MetaDataObject;
import com.bokesoft.yigo.meta.dataobject.MetaDataSource;
import com.bokesoft.yigo.meta.dataobject.MetaTable;
import com.bokesoft.yigo.meta.dataobject.MetaTableCollection;
import com.bokesoft.yigo.meta.domain.MetaDomain;
import com.bokesoft.yigo.meta.domain.MetaItemKeyCollection;
import com.bokesoft.yigo.meta.factory.IMetaFactory;
import com.bokesoft.yigo.meta.factory.MetaFactory;
import com.bokesoft.yigo.meta.form.IPropertiesElement;
import com.bokesoft.yigo.meta.form.MetaForm;
import com.bokesoft.yigo.meta.form.MetaUICheckRule;
import com.bokesoft.yigo.meta.form.MetaUICheckRuleCollection;
import com.bokesoft.yigo.meta.form.component.MetaComponent;
import com.bokesoft.yigo.meta.form.component.control.MetaCheckListBox;
import com.bokesoft.yigo.meta.form.component.control.MetaComboBox;
import com.bokesoft.yigo.meta.form.component.control.MetaDataBinding;
import com.bokesoft.yigo.meta.form.component.control.MetaDefaultItem;
import com.bokesoft.yigo.meta.form.component.control.MetaDict;
import com.bokesoft.yigo.meta.form.component.control.MetaRadioButton;
import com.bokesoft.yigo.meta.form.component.control.MetaTextEditor;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaCheckListBoxProperties;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaComboBoxProperties;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaDictProperties;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaFilter;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaFilterValue;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaItemFilter;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaItemFilterCollection;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaNumberEditorProperties;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaRadioButtonProperties;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaTextEditorProperties;
import com.bokesoft.yigo.meta.form.component.control.properties.MetaUTCDatePickerProperties;
import com.bokesoft.yigo.meta.form.component.grid.MetaGrid;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridCell;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridColumn;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridColumnCollection;
import com.bokesoft.yigo.meta.form.component.grid.MetaGridRow;
import com.bokesoft.yigo.meta.util.MetaUtil;
import com.bokesoft.yigo.mid.base.DefaultContext;
import com.bokesoft.yigo.struct.datatable.DataTable;
import com.bokesoft.yigo.struct.datatable.DataTableMetaData;
import com.bokesoft.yigo.struct.dict.Item;
import com.bokesoft.yigo.struct.document.Document;
import com.bokesoft.yigo.struct.document.track.TrackDetail;
import com.bokesoft.yigo.struct.rights.DictRights;
import com.bokesoft.yigo.tools.document.DocumentUtil;

/**
 * 处理DefaultFormulaValue和ValueChanged的Document
 */
public class RichDocument extends DocumentWithCurrentOID {

    /**
     * 凭证编号
     */
    private static final String DOCUMENTNUMBER = "DocumentNumber";
    /**
     * 模拟Doc的Map
     */
    protected static final String SimulateDocumentMap = "SimulateDocumentMap";

    /**
	 * 模拟导入
	 * 同com.bokesoft.erp.SimulateConstant.SimulateImportDocumentMap
	 */
    private static final String SimulateImportDocumentMap = "ImportSimulateDocumentMap";

    /** 延后计算所基于的影响地图 */
    private final EffectScopeMap effectScopeMap = new EffectScopeMap(this);
    /** 正在计算的表达式 */
    Stack<FormulaItem> calcingFormulaItems = new Stack<FormulaItem>();
    /** 表达式计算的顺序号 */
    int formulaItemSequence = 0;
    /** 所有字段延后的UI全局计算 */
    protected LinkedHashSet<FieldLocation<?>> delayUIFormula = new LinkedHashSet<FieldLocation<?>>();
    protected Map<String, Set<Integer>> variantCalcBookmarks = new ConcurrentHashMap<>();

    /** 所有延后Grid检查公式的UI全局计算 */
	protected HashMap<String, HashMap<Integer, GridRow>> delayGridRowCheckUIFormula = new HashMap<String, HashMap<Integer, GridRow>>();
    /** 所有延后Grid列可见性的全局计算 */
    protected StringHashMap<List<String>> delayGridColumnVisibleUIFormula = new StringHashMap<>();
    /** 表格空白的值 ,字段存的是colKey */
    protected Map<String, Map<String, Object>> emptyGridRowValues = new ConcurrentHashMap<>();
	/** 当前计算的表达式的来源,用于去除赋值导致的对自身计算
	 * DebitMoney字段 DefaultFormulaValue="if(Direction==1,Money,0)" ValueChanged="Direction=if(DebitMoney<>0,1,-1);Money=if(DebitMoney<>0,DebitMoney,-CreditMoney)"
	 * 在上面的例子中，对DebitMoney赋值首先导致Direction的赋值，如果不做特殊处理，会计算DebitMoney的DefaultFormulaValue，这是错误的
	 * */
    private Stack<FieldLocation<?>> valueChangedList = new Stack<FieldLocation<?>>();
    /** 是否是完整的数据 ，完整的数据不做差异处理 */
    private boolean isFullData = false;

    /** 当前视图界面是否处于映射过程中 */
    private boolean bWFMapping = false;
    /**
	 * 1：存放界面位置在表头但是数据是保存到数据库明细表的字段值
	 * 2：SetValue时，存入headValues中，GetValue时从headValues中获取
	 * 3：新增编辑时，headValues中的值与所在数据库表对应列中的值一致。
	 * 3：LoadObject时，所在数据库表对应列中有值，但headValues中是没有值的。
	 */
	public HashMap<String, Object> headValues = new HashMap<String, Object>();

	/**
	 * 1：存放字段Key以_NODB4Other结尾的字段的值，其数据库表以及界面位置都是表头。
	 * 2：第一次加载界面时计算默认值，存入otherFieldValues中
	 * 3：切换字段值（valuechange）或给字段设置值(SetValue)时存入otherFieldValues中
	 * 4：GetValue时从otherFieldValues中获取
     * 5：只要界面未关闭，除非手动改变字段值，否则otherFieldValues中的值不变。
     */
    public HashMap<String, Object> otherFieldValues = new HashMap<String, Object>();

    private Map<Long, Integer> oidBookMarks = new HashMap<Long, Integer>();


    /** 是否需要解锁,Document在保存时将此参数设置为True，保存完后判断参数为True需要将单据锁解除。 */
    private boolean needUnLock = false;


    /** 是否在SaveObject中 */
    private boolean isInSaveObject = false;

    /**
     * Form的编辑状态,和Document中State状态区别是只有数据变化Document的状态才会改变<br>
     * 业务锁处理时需要判断Form是否可编辑,前端form_OperationState状态设置和Form状态设置保持一致
	 *
     */
    private int form_OperationState = OperationState.Default;
    private final static String RICHDOCUMENT_FORM_OPERATIONSTATE = "form_OperationState";
    /** 表单对应打开的菜单标识 */
    private String formEntryKey = "";

    /** 列扩展表格管理 */
    private ExpandVirtualGridManager expandManager = null;
    private boolean isIgnoreArithmeticException = true;
    /** 需要重置的字段*/
    protected Set<FieldLocation<?>> resetLockFields = new CopyOnWriteArraySet<FieldLocation<?>>();
	/** 字段缓存用*/
	protected Map<String, FieldLocation<?>> fieldLocationCache = new ConcurrentHashMap<>();
	/** 表格行对象缓存*/
	protected Map<String, GridRow> gridRowCache = new ConcurrentHashMap<>();
	
	protected boolean ignoreLockValue = false;
	
    public RichDocument(MetaForm metaForm) {
        super(metaForm);
    }

    /**
     * 是否需要解锁,Document在保存时将此参数设置为True，保存完后判断参数为True需要将单据锁解除
     * @return
     */
    public boolean isNeedUnLock() {
        return needUnLock;
    }

    /**
     * 是否需要解锁,Document在保存时将此参数设置为True，保存完后判断参数为True需要将单据锁解除
     * @param needUnLock
     */
    public void setNeedUnLock(boolean needUnLock) {
        this.needUnLock = needUnLock;
    }

    public boolean isInSaveObject() {
        return isInSaveObject;
    }

    public void setInSaveObject(boolean isInSaveObject) {
        this.isInSaveObject = isInSaveObject;
    }

    /**
	 * Form的编辑状态,和Document中State状态区别是只有数据变化Document的状态才会改变<br>
	 * 业务锁处理时需要判断Form是否可编辑
	 */
	public int getForm_OperationState() {
		return form_OperationState;
	}

	/**
	 * Form的编辑状态,和Document中State状态区别是只有数据变化Document的状态才会改变<br>
	 * 业务锁处理时需要判断Form是否可编辑
	 * @throws Throwable
     */
    public void setForm_OperationState(int form_OperationState) throws Throwable {
        //将所有依赖	form_OperationState的公式计算掉
        List<FormulaItem> formulaItems = effectScopeMap.beforeSetRichDocAttrCollectFormulaItems(false, true);
        while (formulaItems != null && formulaItems.size() > 0) {
            if (calcFormulaItems(formulaItems)) {
                formulaItems = effectScopeMap.beforeSetRichDocAttrCollectFormulaItems(false, true);
            } else {
                formulaItems = null;
            }
        }
        this.form_OperationState = form_OperationState;
    }

    /**
     * 设置文档为普通状态,将操作状态设置成default
     */
    @Override
    public void setNormal() {
        super.setNormal();
        try {
            this.setForm_OperationState(OperationState.Default);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException("操作状态设置失败", e);
            }
        }
    }

    /**
     * 设置文档为新增状态,将操作状态设置成新增
     */
    @Override
    public void setNew() {
        super.setNew();
        try {
            this.setForm_OperationState(OperationState.New);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException("操作状态设置失败", e);
            }
        }
    }

    /**
     * 设置文档为修改状态,将操作状态设置成编辑
     */
    @Override
    public void setModified() {
        super.setModified();
        try {
            this.setForm_OperationState(OperationState.Edit);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException("操作状态设置失败", e);
            }
        }
    }

    /**
     * 设置文档为修改状态,将操作状态设置成删除
     */
    @Override
    public void setDelete() {
        super.setDelete();
        try {
            this.setForm_OperationState(OperationState.Delete);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException("操作状态设置失败", e);
            }
        }
    }
	/**
	 * 获取菜单路径
	 * @return
	 */
	public String getFormEntryKey() {
		return formEntryKey;
	}

	/**
	 * 设置菜单路径
     * @param formEntryKey
     */
    public void setFormEntryKey(String formEntryKey) {
        this.formEntryKey = formEntryKey;
    }

    //	/**
    //	 * 由于有些表单是通过服务端创建新的Document后中间层处理时需要加锁，而加锁需要唯一的FormID，NewDocument时Form还不存在，加锁出错<br>
    //	 * 所以中间层添加documentID，只要NewDocument都生成一个新的documentID,确定加锁表单的唯一性.
    //	 *
    //	 */
    //	public String getDocumentID() {
    //		return documentID;
    //	}
    //
    //	/**
    //	 * 由于有些表单是通过服务端创建新的Document后中间层处理时需要加锁，而加锁需要唯一的FormID，NewDocument时Form还不存在，加锁出错<br>
    //	 * 所以中间层添加documentID，只要NewDocument都生成一个新的documentID,确定加锁表单的唯一性.
    //	 *
    //	 */
    //	public void setDocumentID(String documentID) {
    //		this.documentID = documentID;
    //	}

    private IDictFilterChecker dictFilterChecker = null;

    private IComboChecker comboChecker = null;
    public RichDocument(MetaForm metaForm, boolean isNewDocumentID) {
        super(metaForm);
        //		if (isNewDocumentID) {
        //			this.documentID = this.getNewDocumentID();
        //		}
        if (this.getMetaDataObject() == null) {
            MetaDataObject dataObject = new MetaDataObject();
            dataObject.setKey(metaForm.getKey());
            dataObject.setProject(metaForm.getProject());
            this.setMetaDataObject(dataObject);
        }
        this.expandManager = new ExpandVirtualGridManager(metaForm);
    }

    //	public RichDocument(MetaForm metaForm) {
    //		super(metaForm);
    //	}

    //	public String getNewDocumentID() {
    //		return GrantDocumentID.applyNewID();
    //	}
    /**
     * 设置是否是完整的数据 ，完整的数据不做差异处理
     */
    public void setFullData() {
        this.isFullData = true;
    }

    /**
     * 设置是否是完整的数据 ，完整的数据不做差异处理
     */
    public void setFullData(boolean isFullData) {
        this.isFullData = isFullData;
    }

    /**
     * 取是否是完整的数据 ，完整的数据不做差异处理
     * @return
     */
    public boolean isFullData() {
        return isFullData;
    }

    private boolean targetIsParentGrid(IDLookup idLookup, String sourceGridKey, String targetGridKey) {
		if (sourceGridKey == null || sourceGridKey.length() == 0 || targetGridKey == null
				|| targetGridKey.length() == 0) {
            return false;
        }
        List<String> childGridKeys = idLookup.getChildGridKeyByGridKey(targetGridKey);
        if (childGridKeys.indexOf(sourceGridKey) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 执行字段改变
     * <br>需要执行字段的ValueChanged都应该执行execDefaultFormulaValue
     * <br>先执行影响项的默认值,后执行本字段的ValueChanged
     * @param context
     * @param fieldKey
     * @param bookMark
     * @throws Throwable
     */
    public void fireValueChanged(RichDocumentContext context, String fieldKey, int bookMark) throws Throwable {
        fireValueChanged(context, fieldKey, bookMark, true);
	}
	/**
	 * 执行字段改变
	 * <br>需要执行字段的ValueChanged都应该执行execDefaultFormulaValue
	 * <br>先执行影响项的默认值,后执行本字段的ValueChanged
     * @param context
     * @param fieldKey
     * @param bookMark
     * @param isExecValueChanged
     * @throws Throwable
     */
    public void fireValueChanged(RichDocumentContext context, String fieldKey, int bookMark, boolean isExecValueChanged)
        throws Throwable {
        valueChangedList.push(FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark));
        execDefaultFormulaValue(context, fieldKey, bookMark);

        // 增加ui属性延后计算公式
        String tableKey = IDLookup.getIDLookup(metaForm).getTableKeyByFieldKey(fieldKey);
        if (!dirtyTables.contains(tableKey)) {
            execUIFormula(context, fieldKey, bookMark);
        }
        if (isExecValueChanged) {
            execValueChanged(context, fieldKey, bookMark);
        }
        if (valueChangedList.size() > 0) {
            valueChangedList.pop();
        }
    }

    private void execUIFormula(RichDocumentContext context, String fieldKey, int bookMark) throws Throwable {
        execUIFormula(context, fieldKey, bookMark, null);
    }

    /**
     * 值变化增加ui属性延后计算公式
     *
     * @param context
     * @param fieldKey
     * @param bookMark
     * @throws Throwable
     */
    private void execUIFormula(RichDocumentContext context, String fieldKey, int bookMark, Set<String> dirtyTableKeys) throws Throwable {
        execUIFormula(context, fieldKey, new int[]{bookMark}, dirtyTableKeys);
    }
    /**
     * 值变化增加ui属性延后计算公式
     *
     * @param context
     * @param fieldKey
     * @param bookMarks
     * @throws Throwable
     */
    private void execUIFormula(RichDocumentContext context, String fieldKey, int[] bookMarks, Set<String> dirtyTableKeys) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);

        boolean isOnlyGrifEmptyRow = bookMarks.length == 1 && bookMarks[0] == GridRow.GridEmptyBookMark;
        MetaComponent selfCom = metaForm.componentByKey(fieldKey);
        if (selfCom != null) {
            if (isNeedUICalc(idLookup, fieldKey)) {
                HeadFieldLocation fieldLocation = (HeadFieldLocation) FieldLocationUtil.getFieldLocation(this, fieldKey, 0);
                addDelayUIFormula(fieldLocation);
            }
        } else {
            String gridKey = idLookup.getGridKeyByFieldKey(fieldKey);
            String tableKey = idLookup.getTableKeyByGridKey(gridKey);
            MetaGridCell selfCell = metaForm.metaGridCellByKey(fieldKey);
            if (selfCell == null) {
                return;
            }
            if (isNeedUICalc(idLookup, fieldKey)) {
                for (int bookMark : bookMarks) {
                    if (bookMark == GridRow.GridEmptyBookMark) {
                        GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(selfCell, tableKey);
                        addDelayUIFormula(cellFieldLocation);
                    } else {
                        CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
                        addDelayUIFormula(cellFieldLocation);
                    }
                    addVarientDelayCalcBookmark(gridKey, bookMark);
                }
            }
        }

        EnableTree enableTree = EnableTreeCache.getEnableTree(context, metaForm);
        EnableAffectItemSet enableAffect = enableTree.getAffect(fieldKey);
        if (enableAffect != null) {
            for (IExprItemObject item : enableAffect.getArray()) { // 计算影响项
                switch (item.getObjectType()) {
                    case IExprItemObject.Item:
                        //空白行计算时不需要影响头控件
                        if (isOnlyGrifEmptyRow) {
                            continue;
                        }
                        EnableItem enableItem = (EnableItem) item;
                        MetaComponent metaComponent = metaForm.componentByKey(enableItem.getTarget());
                        if (metaComponent != null) { // 还有一种可能是字段在固定行上，暂时不处理
                            HeadFieldLocation fieldLocation = (HeadFieldLocation) FieldLocationUtil.getFieldLocation(this, enableItem.getTarget(), 0);
                            addDelayUIFormula(fieldLocation);
                        }
                        break;
                    case IExprItemObject.Set:
                        EnableItemSet itemSet = (EnableItemSet) item;
                        String tableKey = idLookup.getTableKeyByGridKey(itemSet.getSource());
                        if (dirtyTableKeys != null && dirtyTableKeys.contains(tableKey)) {
                            continue;
                        }
                        String sourceGridKey = idLookup.getGridKeyByFieldKey(fieldKey);
                        String targetGridKey = itemSet.getSource();// 被影响的字段的gridKey
                        MetaGrid metaGrid = (MetaGrid) metaForm.componentByKey(targetGridKey); // 被影响的表格
                        boolean isSameGrid = idLookup.isDetailCellInGrid(fieldKey, targetGridKey);// 被影响的字段与当前字段是同一个表格
                        boolean isChildGrid = targetIsParentGrid(idLookup, targetGridKey, sourceGridKey); // 被影响的字段是子表格字段
                        boolean isParentGrid = targetIsParentGrid(idLookup, sourceGridKey, targetGridKey);// 被影响的字段是父表格字段

                        for (Iterator<IExprItemObject> it = itemSet.iterator(); it.hasNext(); ) {
                            IExprItemObject expItemObject = it.next();
                            EnableItem cellEnableItem = (EnableItem) expItemObject;
                            String source = cellEnableItem.getTarget();
                            MetaGridCell metaGridCell = metaForm.metaGridCellByKey(source);
                            if (isSameGrid) {
                                for (int bookMark : bookMarks) {
                                    if (bookMark == GridRow.GridEmptyBookMark) {
                                        GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell, metaGrid.getTableKey());
                                        addDelayUIFormula(cellFieldLocation);
                                    } else {
                                        CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, bookMark);
                                        addDelayUIFormula(cellFieldLocation);
                                    }
                                }
                                //							GridRow gridRow = new GridRow(this, metaGrid.getTableKey(), bookMark);
                                //							CellFieldLocation cellFieldLocation = new CellFieldLocation(metaGridCell, gridRow);
                                //							addDelayUIFormula(cellFieldLocation);
                            } else if (isChildGrid) {
                                for (int bookMark : bookMarks) {
                                    DataTable childDataTable = get_impl(metaGrid.getTableKey());
                                    if (childDataTable != null) {
                                        DataTable pTable = get_impl(tableKey);
                                        int rowIndex = pTable.getRowIndexByBookmark(bookMark);
                                        int[] cIndexes;
                                        if (childDataTable.getMetaData().constains(SystemField.POID_SYS_KEY)) {
                                            Long poid = rowIndex == -1 ? 0L : pTable.getLong(rowIndex, SystemField.OID_SYS_KEY);
                                            cIndexes = childDataTable.fastFilter(SystemField.POID_SYS_KEY, poid);
                                        } else {
                                            // 兼容处理，如果Scope中没有包含POID字段，根据父表格的Bookmark来筛选子表格的行
                                            List<Integer> cIndexesList = new ArrayList<>();
                                            if (bookMark != -1) {
                                                for (int i = 0; i < childDataTable.size(); i++) {
                                                    if (childDataTable.getParentBookmark(i) == bookMark) {
                                                        cIndexesList.add(i);
                                                    }
                                                }
                                            }
                                            cIndexes = cIndexesList.stream().mapToInt(idx -> idx).toArray();
                                        }
                                        if (cIndexes.length>0) {
                                            for (int i = 0; i < cIndexes.length; i++) {
                                                int cBookmark = childDataTable.getBookmark(cIndexes[i]);
                                                CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, cBookmark);
                                                addDelayUIFormula(cellFieldLocation);
                                            }
                                        }
                                    }
                                    // 增加空白行的处理
                                    GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell,
                                            metaGrid.getTableKey());
                                    addDelayUIFormula(cellFieldLocation);
                                }
                            } else if (isParentGrid) {
                                for (int bookMark : bookMarks) {
                                    int childBookMark = bookMark;
                                    DataTable parentDataTable = get_impl(metaGrid.getTableKey());
                                    String childTableKey = idLookup.getTableKeyByFieldKey(fieldKey); // 当前字段为子表格
                                    DataTable childDataTable = get_impl(childTableKey);
                                    int parentBookmark = -1;
                                    int rowIndex = childDataTable.getRowIndexByBookmark(childBookMark);
                                    parentBookmark = childDataTable.getParentBookmark(rowIndex);
                                    CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, parentBookmark);
                                    addDelayUIFormula(cellFieldLocation);
                                }
                            } else {
                                // 表头影响表格的情况，不会出现2个没有关系的表格之间字段值的影响
                                DataTable targetDataTable = get_impl(metaGrid.getTableKey());
                                if (targetDataTable != null) {
                                    for (int rowIndex = 0; rowIndex < targetDataTable.size(); rowIndex++) {
                                        int targetBookMark = targetDataTable.getBookmark(rowIndex);
                                        CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, targetBookMark);
                                        addDelayUIFormula(cellFieldLocation);
                                    }
                                }
                                // 处理空白行
                                GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell,
                                        metaGrid.getTableKey());
                                addDelayUIFormula(cellFieldLocation);
                            }
                        }
                        break;
                }
            }
        }

        VisibleTree visibleTree = VisibleTreeCache.getVisibleTree(context, metaForm);
        VisibleAffectItemSet visibleAffect = visibleTree.getAffect(fieldKey);
        if (visibleAffect != null) {
            for (IExprItemObject item : visibleAffect.getArray()) { // 计算影响项
                switch (item.getObjectType()) {
                    case IExprItemObject.Item:
                        //空白行计算时不需要影响头控件
                        if (isOnlyGrifEmptyRow) {
                            continue;
                        }
                        VisibleItem visibleItem = (VisibleItem) item;
                        MetaComponent metaComponent = metaForm.componentByKey(visibleItem.getTarget());
                        if (metaComponent != null) { // 还有一种可能是字段在固定行上，暂时不处理
                            HeadFieldLocation fieldLocation = (HeadFieldLocation) FieldLocationUtil.getFieldLocation(this, visibleItem.getTarget(), 0);
                            addDelayUIFormula(fieldLocation);
                        }
                        break;
                    case IExprItemObject.Set:
                        VisibleItemSet itemSet = (VisibleItemSet) item;
                        String tableKey = idLookup.getTableKeyByGridKey(itemSet.getSource());
                        if (dirtyTableKeys != null && dirtyTableKeys.contains(tableKey)) {
                            continue;
                        }
                        String sourceGridKey = idLookup.getGridKeyByFieldKey(fieldKey);
                        String targetGridKey = itemSet.getSource();// 被影响的字段的gridKey
                        MetaGrid metaGrid = (MetaGrid) metaForm.componentByKey(targetGridKey); // 被影响的表格
                        boolean isSameGrid = idLookup.isDetailCellInGrid(fieldKey, targetGridKey);// 被影响的字段与当前字段是同一个表格
                        boolean isChildGrid = targetIsParentGrid(idLookup, targetGridKey, sourceGridKey); // 被影响的字段是子表格字段
                        boolean isParentGrid = targetIsParentGrid(idLookup, sourceGridKey, targetGridKey);// 被影响的字段是父表格字段

                        for (Iterator<IExprItemObject> it = itemSet.iterator(); it.hasNext(); ) {
                            IExprItemObject expItemObject = it.next();
                            VisibleItem cellVisibleItem = (VisibleItem) expItemObject;
                            String source = cellVisibleItem.getTarget();
                            MetaGridCell metaGridCell = metaForm.metaGridCellByKey(source);
                            if (isSameGrid) {
                                for (int bookMark : bookMarks) {
                                    if (bookMark == GridRow.GridEmptyBookMark) {
                                        GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell, metaGrid.getTableKey());
                                        addDelayUIFormula(cellFieldLocation);
                                    } else {
                                        CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, bookMark);
                                        addDelayUIFormula(cellFieldLocation);
                                    }
                                }
                            } else if (isChildGrid) {
                                if (cellVisibleItem.getType() == VisibleItem.Column) {
                                    MetaGridColumn gridColumn = idLookup.getGridMetaColumnByKey(source);
                                    GridColumnLocation gridColumnLocation = new GridColumnLocation(gridColumn, targetGridKey);
                                    addDelayUIFormula(gridColumnLocation);
                                } else {
                                    for (int bookMark : bookMarks) {
                                        DataTable childDataTable = get_impl(metaGrid.getTableKey());
                                        if (childDataTable != null) {
                                            DataTable pTable = get_impl(tableKey);
                                            int rowIndex = pTable.getRowIndexByBookmark(bookMark);
                                            int[] cIndexes;
                                            if (childDataTable.getMetaData().constains(SystemField.POID_SYS_KEY)) {
                                                Long poid = rowIndex == -1 ? 0L : pTable.getLong(rowIndex, SystemField.OID_SYS_KEY);
                                                cIndexes = childDataTable.fastFilter(SystemField.POID_SYS_KEY, poid);
                                            } else {
                                                List<Integer> cIndexesList = new ArrayList<>();
                                                if (bookMark != -1) {
                                                    for (int i = 0; i < childDataTable.size(); i++) {
                                                        if (childDataTable.getParentBookmark(i) == bookMark) {
                                                            cIndexesList.add(i);
                                                        }
                                                    }
                                                }
                                                cIndexes = cIndexesList.stream().mapToInt(idx -> idx).toArray();
                                            }
                                            if (cIndexes.length>0) {
                                                for (int i = 0; i < cIndexes.length; i++) {
                                                    int cBookmark = childDataTable.getBookmark(cIndexes[i]);
                                                    CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, cBookmark);
                                                    addDelayUIFormula(cellFieldLocation);
                                                }
                                            }
                                        }
                                    }
                                }
                                // 增加空白行的处理
                                GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell,
                                        metaGrid.getTableKey());
                                addDelayUIFormula(cellFieldLocation);
                            } else if (isParentGrid) {
                                for (int bookMark : bookMarks) {
                                    int childBookMark = bookMark;
                                    DataTable parentDataTable = get_impl(metaGrid.getTableKey());
                                    String childTableKey = idLookup.getTableKeyByFieldKey(fieldKey); // 当前字段为子表格
                                    DataTable childDataTable = get_impl(childTableKey);
                                    int parentBookmark = -1;
                                    for (int rowIndex = 0; rowIndex < childDataTable.size(); rowIndex++) {
                                        if (childDataTable.getBookmark(rowIndex) == childBookMark) {
                                            parentBookmark = childDataTable.getParentBookmark(rowIndex);
                                            break;
                                        }
                                    }
                                    CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, parentBookmark);
                                    addDelayUIFormula(cellFieldLocation);
                                }
                            } else {
                                // 表头影响表格的情况，不会出现2个没有关系的表格之间字段值的影响
                                DataTable targetDataTable = get_impl(metaGrid.getTableKey());
                                for (int rowIndex = 0; rowIndex < targetDataTable.size(); rowIndex++) {
                                    int targetBookMark = targetDataTable.getBookmark(rowIndex);

                                    CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, targetBookMark);
                                    addDelayUIFormula(cellFieldLocation);
                                }
                                // 处理空白行
                                GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell,
                                        targetDataTable.getKey());
                                addDelayUIFormula(cellFieldLocation);
                            }
                        }
                        break;
                }
            }
        }

        CheckRuleTree checkRuleTree = CheckRuleTreeCache.getCheckRuleTree(context, metaForm);
        CheckRuleAffectItemSet checkRuleAffect = checkRuleTree.getAffect(fieldKey);
        if (checkRuleAffect != null) {
            for (IExprItemObject item : checkRuleAffect.getArray()) { // 计算影响项
                switch (item.getObjectType()) {
                    case IExprItemObject.Item:
                        //空白行计算时不需要影响头控件
                        if (isOnlyGrifEmptyRow) {
                            continue;
                        }
                        CheckRuleItem checkRuleItem = (CheckRuleItem) item;
                        MetaComponent metaComponent = metaForm.componentByKey(checkRuleItem.getTarget());
                        if (metaComponent != null) { // 还有一种可能是字段在固定行上，暂时不处理
                            HeadFieldLocation fieldLocation = new HeadFieldLocation(metaComponent);
                            addDelayUIFormula(fieldLocation);
                        }
                        break;
                    case IExprItemObject.Set:
                        CheckRuleItemSet itemSet = (CheckRuleItemSet) item;
                        String tableKey = idLookup.getTableKeyByGridKey(itemSet.getSource());
                        if (dirtyTableKeys != null && dirtyTableKeys.contains(tableKey)) {
                            continue;
                        }
                        String sourceGridKey = idLookup.getGridKeyByFieldKey(fieldKey);
                        String targetGridKey = itemSet.getSource();// 被影响的字段的gridKey
                        MetaGrid metaGrid = (MetaGrid) metaForm.componentByKey(targetGridKey); // 被影响的表格
                        boolean isSameGrid = idLookup.isDetailCellInGrid(fieldKey, targetGridKey);// 被影响的字段与当前字段是同一个表格
                        boolean isChildGrid = targetIsParentGrid(idLookup, targetGridKey, sourceGridKey); // 被影响的字段是子表格字段
                        boolean isParentGrid = targetIsParentGrid(idLookup, sourceGridKey, targetGridKey);// 被影响的字段是父表格字段

                        for (Iterator<IExprItemObject> it = itemSet.iterator(); it.hasNext(); ) {
                            IExprItemObject expItemObject = it.next();
                            CheckRuleItem cellCheckRuleItem = (CheckRuleItem) expItemObject;
                            String source = cellCheckRuleItem.getTarget();
                            MetaGridCell metaGridCell = metaForm.metaGridCellByKey(source);
                            if (isSameGrid) {
                                for (int bookMark : bookMarks) {
                                    if (bookMark == GridRow.GridEmptyBookMark) {
                                        GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell, metaGrid.getTableKey());
                                        addDelayUIFormula(cellFieldLocation);
                                    } else {
                                        // GridRow gridRow = new GridRow(this, metaGrid.getTableKey(), bookMark);
                                        // CellFieldLocation cellFieldLocation = new CellFieldLocation(metaGridCell, gridRow);
                                        CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, bookMark);
                                        addDelayUIFormula(cellFieldLocation);
                                    }
                                }
                                //							GridRow gridRow = new GridRow(this, metaGrid.getTableKey(), bookMark);
                                //							CellFieldLocation cellFieldLocation = new CellFieldLocation(metaGridCell, gridRow);
                                //							addDelayUIFormula(cellFieldLocation);
                            } else if (isChildGrid) {
                                for (int bookMark : bookMarks) {
                                    DataTable childDataTable = get_impl(metaGrid.getTableKey());
                                    if (childDataTable != null) {// 可能相关表的CheckRule都没有包含MidFunction或含UIFunction，这样在Scope中就没有包含
                                        DataTable pTable = get_impl(tableKey);
                                        int rowIndex = pTable.getRowIndexByBookmark(bookMark);
                                        int[] cIndexes;
                                        if (childDataTable.getMetaData().constains(SystemField.POID_SYS_KEY)) {
                                            Long poid = rowIndex == -1 ? 0L : pTable.getLong(rowIndex, SystemField.OID_SYS_KEY);
                                            cIndexes = childDataTable.fastFilter(SystemField.POID_SYS_KEY, poid);
                                        } else {
                                            List<Integer> cIndexesList = new ArrayList<>();
                                            if (bookMark != -1) {
                                                for (int i = 0; i < childDataTable.size(); i++) {
                                                    if (childDataTable.getParentBookmark(i) == bookMark) {
                                                        cIndexesList.add(i);
                                                    }
                                                }
                                            }
                                            cIndexes = cIndexesList.stream().mapToInt(idx -> idx).toArray();
                                        }

                                        if (cIndexes.length>0) {
                                            for (int i = 0; i < cIndexes.length; i++) {
                                                int cBookmark = childDataTable.getBookmark(cIndexes[i]);
                                                CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, cBookmark);
                                                addDelayUIFormula(cellFieldLocation);
                                            }
                                        }
                                    }
                                    // 增加空白行的处理
                                    GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell,
                                            metaGrid.getTableKey());
                                    addDelayUIFormula(cellFieldLocation);
                                }
                            } else if (isParentGrid) {
                                for (int bookMark : bookMarks) {
                                    int childBookMark = bookMark;
                                    DataTable parentDataTable = get_impl(metaGrid.getTableKey());
                                    String childTableKey = idLookup.getTableKeyByFieldKey(fieldKey); // 当前字段为子表格
                                    DataTable childDataTable = get_impl(childTableKey);
                                    int parentBookmark = -1;
                                    for (int rowIndex = 0; rowIndex < childDataTable.size(); rowIndex++) {
                                        if (childDataTable.getBookmark(rowIndex) == childBookMark) {
                                            parentBookmark = childDataTable.getParentBookmark(rowIndex);
                                            break;
                                        }
                                    }
                                    // GridRow gridRow = new GridRow(this, parentDataTable.getKey(), parentBookmark);
                                    // CellFieldLocation cellFieldLocation = new CellFieldLocation(metaGridCell, gridRow);
                                    CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, parentBookmark);
                                    addDelayUIFormula(cellFieldLocation);
                                }
                            } else {
                                // 表头影响表格的情况，不会出现2个没有关系的表格之间字段值的影响
                                DataTable targetDataTable = get_impl(metaGrid.getTableKey());
                                for (int rowIndex = 0; rowIndex < targetDataTable.size(); rowIndex++) {
                                    int targetBookMark = targetDataTable.getBookmark(rowIndex);
                                    // GridRow gridRow = new GridRow(this, targetDataTable.getKey(), targetBookMark);
                                    // CellFieldLocation cellFieldLocation = new CellFieldLocation(metaGridCell, gridRow);
                                    CellFieldLocation cellFieldLocation = (CellFieldLocation) FieldLocationUtil.getFieldLocation(this, source, targetBookMark);
                                    addDelayUIFormula(cellFieldLocation);
                                }
                                // 处理空白行
                                GridEmptyRowFieldLocation cellFieldLocation = new GridEmptyRowFieldLocation(metaGridCell,
                                        targetDataTable.getKey());
                                addDelayUIFormula(cellFieldLocation);
                            }
                        }
                        break;
                }
            }
        }


    }

    /**
     * 执行字段的ValueChanged
     * @param context
     * @param fieldKey
     * @param bookMark
     * @throws Throwable
     */
    public void execValueChanged(RichDocumentContext context, String fieldKey, int bookMark) throws Throwable {
        //int actionID = Performance.startAction("execValueChanged:", fieldKey);
        processValueChangedItem(fieldKey, bookMark);
        //Performance.endActive(actionID);
    }

    /**
     * 执行受影响字段的默认值
     * <br>部分赋值仅需要执行execDefaultFormulaValue而不执行execValueChanged
     *
     * @param context
     * @param fieldKey
     * @param bookMark
     * @throws Throwable
     */
    public void execDefaultFormulaValue(RichDocumentContext context, String fieldKey, int bookMark) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);

        CalcTree calcTree = CalcTreeCache.getCalcTree(context, metaForm);
        CalcAffectItemSet affectItemSet = calcTree.getAffect(fieldKey);
        if (affectItemSet != null) {
            for (IExprItemObject item : affectItemSet.getArray()) { // 计算影响项
                switch (item.getObjectType()) {
                    case IExprItemObject.Item:
                        CalcItem calcItem = (CalcItem)item;
                        processHeadDefaultFormulaValueItem(calcItem.getTarget(), fieldKey, "execDefaultFormulaValue");
                        break;
                    case IExprItemObject.Set:
                        CalcItemSet itemSet = (CalcItemSet)item;
                        for (Iterator<IExprItemObject> it = itemSet.iterator(); it.hasNext(); ) {
                            IExprItemObject expItemObject = it.next();
                            CalcItem cellCalcItem = (CalcItem)expItemObject;
							TableKeyAndBookmark location = new TableKeyAndBookmark(idLookup.getTableKeyByFieldKey(fieldKey), bookMark);
							processDtlDefaultFormulaValueItem(cellCalcItem.getTarget(), location, fieldKey, "execDefaultFormulaValue");
                        }
                        break;
                }
            }
        }
    }

    /**
     * 行删除后的变化
     * @param context
     * @param deleteGridKey
     * @throws Throwable
     */
    public void rowCountChanged(RichDocumentContext context, String deleteGridKey, boolean onlyHead) throws Throwable {

        MetaComponent component = metaForm.componentByKey(deleteGridKey);
        if (!(component instanceof MetaGrid)) {
            return;
        }

        List<IExprItemObject> affectItems = this.getGridAffectItems(context, (MetaGrid)component);

        MetaComponent com = null;
        if (CollectionUtils.isEmpty(affectItems)) {
            return;
        }

        for (IExprItemObject item : affectItems) {
            com = metaForm.componentByKey(item.getSource());
            if (com == null) {
                continue;
            }
            switch (item.getObjectType()) {
                case IExprItemObject.Item:
                    CalcItem calcItem = (CalcItem)item;
                    processHeadDefaultFormulaValueItem(calcItem.getTarget(), deleteGridKey, ":deleteRowChanged");
                    break;
                case IExprItemObject.Set:
                    if (onlyHead) {
                        continue;
                    }
                    CalcItemSet itemSet = (CalcItemSet)item;
					for (Iterator<IExprItemObject> it = itemSet.iterator(); it.hasNext();)
					{

                        IExprItemObject expItemObject = it.next();
                        CalcItem cellCalcItem = (CalcItem)expItemObject;
                        String gridKey = itemSet.getSource();
                        MetaGrid deleteGrid = (MetaGrid)metaForm.componentByKey(deleteGridKey); //删除行的表格
                        boolean isChildGrid = isSubDetail(metaForm, deleteGrid, gridKey); // 当前删除的表格是否为子表格
                        if (isChildGrid) {
                            MetaGrid parentGrid = (MetaGrid)metaForm.componentByKey(gridKey); //受影响的祖先表格
                            DataTable parentDataTable = get(parentGrid.getTableKey());
                            if (parentDataTable.size() > 0 && parentDataTable.getPos() >= 0) {
                                int tmpBM = parentDataTable.getBookmark();
                                processDtlDefaultFormulaValueItem(cellCalcItem.getTarget(), tmpBM, deleteGridKey,
                                    ":deleteRowChanged");
                            }
                        }
                    }
            }
        }

    }

    public List<IExprItemObject> getGridAffectItems(RichDocumentContext context, MetaGrid metaGrid) throws Throwable {
        CalcTree calcTree = CalcTreeCache.getCalcTree(context, metaForm);

        List<IExprItemObject> items = calcTree.getGridAffect(metaGrid.getKey());
        if (items != null) {
            return items;
        }

        items = new ArrayList<IExprItemObject>();
        MetaGridRow detailRow = metaGrid.getDetailMetaRow();
        if (detailRow == null) {
            return items;
        }

        CalcAffectItemSet itemSet = null;
        List<IExprItemObject> _items = null;
        IExprItemObject item = null;
        for (int i = 0, size = detailRow.size(); i < size; i++) {
            itemSet = calcTree.getAffect(detailRow.get(i).getKey());
            if (itemSet != null) {
                _items = itemSet.getArray();
                for (int k = 0, length = _items.size(); k < length; k++) {
                    item = _items.get(k);

                    if (item.getSource().equals(metaGrid.getKey()) && item.getObjectType() == IExprItemObject.Set) {
                        continue;
                    }

                    items.add(item);
                }
            }
        }

        items.sort(new Comparator<IExprItemObject>() {

            @Override
            public int compare(IExprItemObject o1, IExprItemObject o2) {

                return o1.getOrder() - o2.getOrder();
            }

        });
        calcTree.addGridAffect(metaGrid.getKey(), items);
        return items;
    }

    /**
     * 判断给定组件是否是某个表格的直接子组件或者下游子组件
     *
     * @param metaForm  表单
     * @param component 给定的组件
     * @param gridKey   父表格
     * @return
     */
    public static boolean isSubDetail(MetaForm metaForm, MetaComponent component, String gridKey) {
        if (component.isSubDetail()) {
            String parentGridKey = component.getParentGridKey();
            if (!parentGridKey.equals(gridKey)) {
                //				MetaGrid metaGrid = (MetaGrid) metaForm.componentByKey(gridKey); //影响的表格
                //				return isSubDetail(metaForm,metaGrid, gridKey);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 点击事件
     * @param context
     * @param fieldKey
     * @param bookMark
     * @throws Throwable
     */
    public void fireOnClick(RichDocumentContext context, String fieldKey, int bookMark) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String onClick = idLookup.getOnClickByFieldKey(fieldKey);
        if (onClick != null && onClick.length() > 0) {
			GridRow oldGridRow = setGridRow(FieldLocationUtil.getGridRow(context.getRichDocument(), fieldKey, bookMark));
            context.getMidParser().eval(ScriptType.Formula, onClick);
            restoreGridRow(oldGridRow);
        }
    }

    public void addVarientDelayCalcBookmark(String gridKey, int bookmark) {
        if (!this.metaForm.isUseVariant()) {
            return;
        }
        variantCalcBookmarks.computeIfAbsent(gridKey, key -> new LinkedHashSet<>()).add(bookmark);
    }

    public void calcFieldUIItems(JSONObject result) throws Throwable {
        try {
            final JSONObject variantObject = FieldUIExtensionPointManager.calcItems(context, this, this.variantCalcBookmarks);
            if (variantObject.isEmpty()) {
                return;
            }
            JSONObject variantCache = result.optJSONObject("variantCache");
            if (variantCache == null) {
                variantCache = new JSONObject();
                result.put("variantCache", variantCache);
            }
            JsonUtils.deepMergeJSONObject(variantObject, variantCache);
        } finally {
            this.variantCalcBookmarks.clear();
        }
    }
    /**
     * 增加延后计算UI属性
     * @param fieldLocation
     */
    public void addDelayUIFormula(FieldLocation<?> fieldLocation) {
        if (delayUIFormula.contains(fieldLocation)) {
            delayUIFormula.remove(fieldLocation);
        }
        if (fieldLocation instanceof CellFieldLocation) {
            String tableKey = fieldLocation.getTableKey();
            HashMap<Integer, GridRow> gridRows = delayGridRowCheckUIFormula.get(tableKey);
            if (gridRows == null) {
                gridRows = new HashMap<Integer, GridRow>();
                delayGridRowCheckUIFormula.put(tableKey, gridRows);
            }
            int bookMark = fieldLocation.getBookMark();
            if (!gridRows.containsKey(bookMark)) {
                gridRows.put(bookMark, fieldLocation.getGridRow());
            }
        }

        if (fieldLocation instanceof GridColumnLocation) {
            String gridGolumnKey = fieldLocation.getKey();
            String gridKey = ((GridColumnLocation)fieldLocation).getGridKey();
            List<String> gridColumnVisibles = delayGridColumnVisibleUIFormula.get(gridKey);
            if (gridColumnVisibles == null) {
                gridColumnVisibles = new ArrayList<>();
                delayGridColumnVisibleUIFormula.put(gridKey, gridColumnVisibles);
            }

            if (!gridColumnVisibles.contains(gridGolumnKey)) {
                gridColumnVisibles.add(gridGolumnKey);
            }
            return;
        }

        delayUIFormula.add(fieldLocation);
    }

    /**
     * 根据表名取DataTable类型的数据，取数前会计算相关被延后的表达式
     * @param tableKey
     * @return
     */
    @Override
    public DataTable get(String tableKey) {
        if (!this.getDocumentTrack().contains(TrackDetail.MIGRATION, "")) {
            try {
                Stack<FormulaItem> calcingFormulaItems = this.calcingFormulaItems;
                int[] valueChangedFireSequence = null;
                for (int i = calcingFormulaItems.size() - 1; i >= 0; i--) {
                    FormulaItem formulaItem = calcingFormulaItems.get(i);
                    if (formulaItem.isValueChanged()) {
                        valueChangedFireSequence = formulaItem.getSequence();
                        break;
                    }
                }

				List<FormulaItem> formulaItems = effectScopeMap.beforeGetDataTableCollectFormulaItems(tableKey, valueChangedFireSequence);
                while (formulaItems != null && formulaItems.size() > 0) {
                    if (calcFormulaItems(formulaItems)) {
						formulaItems = effectScopeMap.beforeGetDataTableCollectFormulaItems(tableKey, valueChangedFireSequence);
                    } else {
                        formulaItems = null;
                    }
                }
                if (valueChangedFireSequence == null) {
                    effectScopeMap.clearNeedCollectRows(tableKey);
                }
            } catch (Throwable e) {
                String message = e.getMessage();
                if (ERPStringUtil.isBlankOrNull(message)) {
                    message = "getDataTable方法出错";
                }
                LogSvr.getInstance().error(message, e);
                throw new RuntimeException(message, e);
            }
        }
        return get_impl(tableKey);
    }

    @Override
    public DataTable get(int index) {
        DataTable tmp = super.get(index);
        if (tmp != null) {
            return get(tmp.getKey());
        }
        return super.get(index);
    }

    @Override
    public void put(String key, DataTable table) {
        super.put(key, table);
        this.effectScopeMap.checkDataTableIsSameObject(key, table);
    }

    /**
     * 根据bookMark取字段的值，可以会触发计算
     * @param context
     * @param fieldKey
     * @param bookMark
     * @return
     * @throws Throwable
     */
    public Object getValue(RichDocumentContext context, String fieldKey, int bookMark) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
        return getValue(context, fieldLocation, false);
    }

    /**
     * 根据bookMark取字段的值，不触发计算
     *
     * @param fieldKey
     * @param bookMark
     * @return
     * @throws Throwable
     */
    public Object getValue(String fieldKey, int bookMark) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
        return getValue(this.context, fieldLocation, false);
    }

    public Object getOldValue(String fieldKey, int bookMark) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
        return getValue(fieldLocation, true);
    }

    /**
     * 根据bookMark设置字段的值
     * @param context
     * @param fieldKey
     * @param bookMark
     * @param value
     * @throws Throwable
     */
    public void setValue(RichDocumentContext context, String fieldKey, int bookMark, Object value) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
        setValue(context, fieldLocation, value);
    }
    
    /**
     * 根据bookMark设置锁定字段的值
     * @param context
     * @param fieldKey
     * @param bookMark
     * @param value
     * @throws Throwable
     */
    public void setLockValue(RichDocumentContext context, String fieldKey, int bookMark, Object value) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark, true);
        if(!this.ignoreLockValue) {
        	fieldLocation.setNeedLockValue(true);
        }
        setValue(context, fieldLocation, value);
    }

    /**
     * 根据bookMark设置字段的值 不触发ValueChanged,根据execDefaultFormulaValue控制是否触发受影响字段的DefaultFormulaValue
     * <br>先执行默认值,再执行valueChanged
     *
     * @param context
     * @param fieldKey
     * @param bookMark
     * @param value
     * @param execDefaultFormulaValue
     * @throws Throwable
     */
    public void setValueNoChanged(RichDocumentContext context, String fieldKey, int bookMark, Object value,
        boolean execDefaultFormulaValue) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
        Stack<FormulaItem> calcingFormulaItems = this.calcingFormulaItems;
        int[] valueChangedFireSequence = null;
        for (int i = calcingFormulaItems.size() - 1; i >= 0; i--) {
            FormulaItem formulaItem = calcingFormulaItems.get(i);
            if (formulaItem.isValueChanged()) {
                valueChangedFireSequence = formulaItem.getSequence();
                break;
            }
        }
        String tableKey = fieldLocation.getTableKey();
        String columnKey = fieldLocation.getColumnKey();
        if (IDLookup.isOtherField(fieldLocation.getKey())) {
            tableKey = null;
            columnKey = fieldLocation.getKey();
        }

        if (fieldLocation instanceof HeadFieldLocation) {
            bookMark = this.getCurrentBookMark(tableKey);
        }
		List<FormulaItem> formulaItems = effectScopeMap.beforeSetValueCollectFormulaItems(tableKey, columnKey, bookMark, valueChangedFireSequence);
		while (formulaItems != null && formulaItems.size() > 0) {
			if (calcFormulaItems(formulaItems)) {
				formulaItems = effectScopeMap.beforeSetValueCollectFormulaItems(tableKey, columnKey, bookMark, valueChangedFireSequence);
            } else {
                formulaItems = null;
            }
        }
        // 只有值变化，才去计算相关的默认值，界面代码也是这样的逻辑
        if (setValueNoChanged(fieldLocation, value) && execDefaultFormulaValue) {
            execDefaultFormulaValue(context, fieldKey, bookMark);
        }
    }

    /**
     * 根据位置取值，如果表头字段row对象传null
     * @param context
     * @param fieldKey
     * @param gridRow
     * @return
     * @throws Throwable
     */
	protected Object getValue(RichDocumentContext context, String fieldKey, GridRow gridRow, boolean original) throws Throwable {
        if (!IDLookup.getIDLookup(metaForm).containFieldKey(fieldKey)) {
            throw new Exception("字段" + fieldKey + "在表单" + metaForm.getKey() + "中不存在，估计是配置错误。");
        }
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, gridRow);
        return getValue(context, fieldLocation, original);
    }

    /**
     * 取字段的值，取值前会计算相关延后的表达式
     * @param context
     * @param fieldLocation
     * @param original
     * @return
     * @throws Throwable
     */
	protected Object getValue(RichDocumentContext context, FieldLocation<?> fieldLocation, boolean original) throws Throwable {
        if (!this.getDocumentTrack().contains(TrackDetail.MIGRATION, "")) {
            Stack<FormulaItem> calcingFormulaItems = this.calcingFormulaItems;
            int[] valueChangedFireSequence = null;
            for (int i = calcingFormulaItems.size() - 1; i >= 0; i--) {
                FormulaItem formulaItem = calcingFormulaItems.get(i);
                if (formulaItem.isValueChanged()) {
                    valueChangedFireSequence = formulaItem.getSequence();
                    break;
                }
            }
            String tableKey = fieldLocation.getTableKey();
            String columnKey = fieldLocation.getColumnKey();
			if ((StringUtil.isBlankOrNull(tableKey)||tableKey.endsWith(MetaFormNODBProcess.STR_NODB4Other_Postfix))&&IDLookup.isOtherField(fieldLocation.getKey())) {
                tableKey = null;
                columnKey = fieldLocation.getKey();
            }

            int bookMark = fieldLocation.getBookMark();
            if (fieldLocation instanceof HeadFieldLocation) {
                bookMark = this.getCurrentBookMark(tableKey);
            }



			List<FormulaItem> formulaItems = effectScopeMap.beforeGetValueCollectFormulaItems(tableKey, columnKey, bookMark, valueChangedFireSequence);
			while (formulaItems != null && formulaItems.size() > 0) {
				if (calcFormulaItems(formulaItems)) {
					formulaItems = effectScopeMap.beforeGetValueCollectFormulaItems(tableKey, columnKey, bookMark, valueChangedFireSequence);
                } else {
                    formulaItems = null;
                }
            }
			
        }

        return getValue(fieldLocation, original);
    }

    protected Object getValue(FieldLocation<?> fieldLocation, boolean original) throws Throwable {
        String tableKey = fieldLocation.getTableKey();
        String columnKey = fieldLocation.getColumnKey();
        String fieldKey = fieldLocation.getKey();
        int componentType = fieldLocation.getComponentType();

        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        if (componentType == ControlType.RADIOBUTTON) {
            boolean isGroupHead = false;
            MetaRadioButton radioButton = (MetaRadioButton)idLookup.getComponentByKey(fieldKey);
            if (radioButton == null) {
                MetaGridCell gridCell = idLookup.getGridCellByKey(fieldKey);
                if (gridCell != null) {
                    MetaRadioButtonProperties properties = (MetaRadioButtonProperties)gridCell.getProperties();
                    isGroupHead = properties.getGroupHead();
                }
            } else {
                isGroupHead = radioButton.isGroupHead();
            }
            if (!isGroupHead) {
				throw new Exception(String.format("不应该直接表单对 %s 单选框组 %s 中非主控件 %s 进行取值", metaForm.getKey(), radioButton.getGroupKey(), fieldKey));
            }
        }
        Object result;
        if (fieldLocation instanceof GridEmptyRowFieldLocation) {
            if (StringUtil.isBlankOrNull(tableKey)) {
                throw new RuntimeException(String.format("FieldKey：%s 未绑定 ColumnKey！", fieldKey));
            }
            result = getGridEmptyRowValue(fieldKey);
        } else if (fieldLocation instanceof CellFieldLocation) {
            if (StringUtil.isBlankOrNull(tableKey)) {
                throw new RuntimeException(String.format("FieldKey：%s 未绑定 ColumnKey！", fieldKey));
            }
            GridRow gridRow = fieldLocation.getGridRow();
            if (gridRow == null) {
                throw new RuntimeException("不可能的错误");
            }
            if (gridRow.getBookMark() == GridRow.GridEmptyBookMark) {
                result = getGridEmptyRowValue(fieldKey);
            } else if (gridRow.isSameDataTable(tableKey)) {
                result = gridRow.getObject(this, columnKey, original);
            } else {
                DataTable table = get(tableKey);
				result = table.size() > 0
						? (original ? table.getOriginalObject(columnKey) : table.getObject(columnKey)) : null;
            }
            //		} else if (fieldLocation instanceof ColumnExpandLocation){
            //			GridRow gridRow = ((ColumnExpandLocation) fieldLocation).getGridRow();
            //			if (gridRow.getBookMark() == GridRow.GridEmptyBookMark) {
            //				result = getGridEmptyRowValue(tableKey, fieldKey);
            //			} else if (gridRow != null && gridRow.isSameDataTable(tableKey)) {
            //				result = gridRow.getObject(this, columnKey, original);
            //			} else {
            //				DataTable table = get(tableKey);
            //				result = table.size() > 0
            //						? (original ? table.getOriginalObject(columnKey) : table.getObject(columnKey)) : null;
            //			}
        } else {
            if (IDLookup.isOtherField(fieldKey)) {
                result = otherFieldValues.get(fieldKey);
            } else {
                if (StringUtils.isEmpty(tableKey)) {
                    result = headValues.get(fieldKey);
                } else {
                    DataTable table;
					if (metaForm.getDataSource().getDataObject().getMetaTable(tableKey).get(columnKey).isSupportI18n()) {
                        table = get_impl(tableKey + MetaTable._T);
                    } else {
                        table = get_impl(tableKey);
                    }
                    if (table == null || table.size() == 0) {
                        result = headValues.get(fieldKey);
                    } else {
						if (table.size() > 0) {
							result = original ? table.getOriginalObject(0, columnKey) : table.getObject(0, columnKey); // 表头字段所以取第一行数据
                            if (result == null) {
                                if (table.getMetaData().getColumnInfo(columnKey).getDataType() == DataType.LONG) {
                                    result = 0L;
                                }
                            }
                        } else {
                            result = null;
                        }
                    }
                }
            }
        }
        int targetDataType;
        if (IDLookup.isOtherField(fieldKey)) {
            targetDataType = idLookup.getDataTypeByComponentKey(fieldKey);
        } else {
            targetDataType = idLookup.getDataTypeByFieldKey(fieldKey);
        }
        result = TypeConvertor.toDataType(targetDataType, result);
        return result;
    }

    /**
     * 根据位置设值，如果表头字段row对象传null
     * @param context
     * @param fieldKey
     * @param gridRow
     * @param value
     * @throws Throwable
     */
    public void setValue(RichDocumentContext context, String fieldKey, GridRow gridRow, Object value) throws Throwable {
        FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, gridRow);
        setValue(context, fieldLocation, value);
    }

    /**
     * 对字段赋值，赋值前会计算相关的延后表达式
     * @param context
     * @param fieldLocation
     * @param value
     * @throws Throwable
     */
	protected void setValue(RichDocumentContext context, FieldLocation<?> fieldLocation, Object value) throws Throwable {
        int bookMark = fieldLocation.getBookMark();
        Stack<FormulaItem> calcingFormulaItems = this.calcingFormulaItems;
        int[] valueChangedFireSequence = null;
        for (int i = calcingFormulaItems.size() - 1; i >= 0; i--) {
            FormulaItem formulaItem = calcingFormulaItems.get(i);
            if (formulaItem.isValueChanged()) {
                valueChangedFireSequence = formulaItem.getSequence();
                break;
            }
        }
        String tableKey = fieldLocation.getTableKey();
        String columnKey = fieldLocation.getColumnKey();
		if ((StringUtil.isBlankOrNull(tableKey)||tableKey.endsWith(MetaFormNODBProcess.STR_NODB4Other_Postfix))&&IDLookup.isOtherField(fieldLocation.getKey())) {
            tableKey = null;
            columnKey = fieldLocation.getKey();
        }

        if (fieldLocation instanceof HeadFieldLocation) {
            bookMark = this.getCurrentBookMark(tableKey);
        }

        // beforeSetValueCollectFormulaItems 可能会对字段赋值， 这个赋值不应该记录
        boolean needLock = fieldLocation.getNeedLockValue();
		List<FormulaItem> formulaItems = effectScopeMap.beforeSetValueCollectFormulaItems(tableKey, columnKey, bookMark, valueChangedFireSequence);
        while (formulaItems != null && formulaItems.size() > 0) {
            if (calcFormulaItems(formulaItems)) {
				formulaItems = effectScopeMap.beforeSetValueCollectFormulaItems(tableKey, columnKey, bookMark, valueChangedFireSequence);
            } else {
                formulaItems = null;
            }
        }
        fieldLocation.setNeedLockValue(needLock);
        if (setValueNoChanged(fieldLocation, value)) {
            fireValueChanged(context, fieldLocation.getKey(), bookMark);
        }
    }

    /**
     * 设置数据，不触发DefaultFormulaValue和ValueChanged，只能内部使用
     * @param fieldLocation
     * @param value
     * @throws Throwable
     */
    protected boolean setValueNoChanged(FieldLocation<?> fieldLocation, Object value) throws Throwable {
        String tableKey = fieldLocation.getTableKey();
        String columnKey = fieldLocation.getColumnKey();
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String fieldKey = fieldLocation.getKey();
        boolean isValueNull = (value == null);
        
        int componentType = fieldLocation.getComponentType();
        if (componentType == ControlType.RADIOBUTTON) {
            boolean isGroupHead = false;
            MetaRadioButton radioButton = (MetaRadioButton)idLookup.getComponentByKey(fieldKey);
            if (radioButton == null) {
                MetaGridCell gridCell = idLookup.getGridCellByKey(fieldKey);
                if (gridCell != null) {
                    MetaRadioButtonProperties properties = (MetaRadioButtonProperties)gridCell.getProperties();
                    isGroupHead = properties.getGroupHead();
                }
            } else {
                isGroupHead = radioButton.isGroupHead();
            }
            if (!isGroupHead) {
				throw new Exception(String.format("不应该直接表单对 %s 单选框组 %s 中非主控件 %s 进行赋值", metaForm.getKey(), radioButton.getGroupKey(), fieldKey));
            }
        }
        if (isBlankOrNull(columnKey)) {
            columnKey = fieldLocation.getKey();
        }
        int dataType = DataType.STRING;
        Integer scale = null;
        Integer precision = null;
        DataTable dataTable = null;
        // 字典控件关联字段
        String dictCodeColumnKey = "";
		boolean isDict = componentType == ControlType.DICT || componentType == ControlType.COMPDICT
				|| componentType == ControlType.DYNAMICDICT;
        if (!isBlankOrNull(tableKey)) {
            dataTable = get_impl(tableKey);
            dataType = idLookup.getDataTypeByFieldKey(fieldKey);
            MetaColumn metaColumn = idLookup.getMetaColumnByFieldKey(fieldKey);
            scale = metaColumn.getScale();
            precision = metaColumn.getPrecision();
            if (isDict) {
                dictCodeColumnKey = metaColumn.getCodeColumnKey();
            }
        }
        AbstractMetaObject meta = idLookup.getMetaObjectByKey(fieldKey);
        AbstractMetaObject prop = null;
        if (meta != null) {
            if (meta instanceof MetaGridCell) {
                prop = ((MetaGridCell)meta).getProperties();
            } else if (meta instanceof MetaComponent) {
                prop = ((MetaComponent)meta).getProperties();
            }
        }
        Integer roundingMode = null;
        MetaUTCDatePickerProperties utcDatePickerProperties = null;
        MetaNumberEditorProperties numberEditorProperties = null;
        if (prop != null && componentType == ControlType.NUMBEREDITOR) {
            numberEditorProperties = MetaNumberEditorProperties.class.cast(prop);
            roundingMode = numberEditorProperties.getRoundingMode();
        } else if (prop != null && componentType == ControlType.UTCDATEPICKER) {
            utcDatePickerProperties = MetaUTCDatePickerProperties.class.cast(prop);
        }

        if (scale != null && scale > 0 && (dataType == DataType.DOUBLE || dataType == DataType.FLOAT || dataType == DataType.NUMERIC)) {
            if (roundingMode == null) {
                roundingMode = BigDecimal.ROUND_HALF_UP;
            }
            value = TypeConvertor.toBigDecimal(value, scale, roundingMode);
        } else {
            value = TypeConvertor.toDataType(dataType, value);
        }
        if (componentType == ControlType.TEXTEDITOR && idLookup.getComponentByKey(fieldLocation.getKey()) != null) {
            MetaTextEditorProperties properties =
                (MetaTextEditorProperties)idLookup.getComponentByKey(fieldLocation.getKey()).getProperties();
            if (properties.isTrim()) {
                value = StringUtils.trim((String)value);
            }
            if (properties.getCaseType() == MetaTextEditor.CASETYPE_LOWER) {
                value = StringUtils.lowerCase((String)value);
            } else if (properties.getCaseType() == MetaTextEditor.CASETYPE_UPPER) {
                value = StringUtils.upperCase((String)value);
            }
        }
        if (utcDatePickerProperties != null && value instanceof Long) {
            String stringValue = TypeConvertor.toString(value);
            Boolean onlyDate = utcDatePickerProperties.isOnlyDate();
            if (onlyDate && stringValue.length() > 8) {
                value = TypeConvertor.toLong(stringValue.substring(0, 8));
            }
        } else if (numberEditorProperties != null && value instanceof BigDecimal) {
            Integer numPrecision = numberEditorProperties.getPrecision();
            numPrecision = numPrecision == null ? (precision != null && precision > 0 ? precision : 16) : numPrecision;
            Integer numScale = numberEditorProperties.getScale();
            numScale = numScale == null ? (scale != null && scale > 0 ? scale : 2) : numScale;

            BigDecimal decimalValue = BigDecimal.class.cast(value);

            BigDecimal maxDecimalValue = getMaxBigDecimalValue(numPrecision, numScale);
            if (maxDecimalValue != null) {
                BigDecimal minDecimalValue = maxDecimalValue.negate();
                if (decimalValue.compareTo(maxDecimalValue) > 0) {
                    decimalValue = maxDecimalValue;
                } else if (decimalValue.compareTo(minDecimalValue) < 0) {
                    decimalValue = minDecimalValue;
                }
                value = decimalValue;
            }
        }
        GridRow gridRow = fieldLocation.getGridRow();
        boolean isChanged;
        if (fieldLocation instanceof GridEmptyRowFieldLocation) {
            Object oldValue = getGridEmptyRowValue(fieldKey, false);
            isChanged = !isEquals(oldValue, value, dataType);
            if (!isChanged && oldValue != null && oldValue.equals(0) && componentType == ControlType.COMBOBOX) {
                isChanged = true;
            }
            if (isChanged) {
                setGridEmptyRowValue(fieldKey, value);
            }
        } else if (gridRow != null) {
            Object oldValue = gridRow.getObject(this, columnKey, false);
            isChanged = !isEquals(oldValue, value, dataType);
            if (isChanged) {
                gridRow.setObject(this, columnKey, value);
                // 选择字段将SelectField同样赋值
                CellFieldLocation cellFieldLocation = (CellFieldLocation)fieldLocation;
                if (cellFieldLocation.isSelect()) {
                    gridRow.setObject(this, SystemField.SELECT_FIELD_KEY, value);
                }
                if (SystemField.POID_SYS_KEY.equals(columnKey) && dataTable != null) {
                    int rowIndex = getRowIndexByBookmark(dataTable, gridRow.getBookMark());
                    gridRow.setParentBookmark(this, dataTable.getParentBookmark(rowIndex));
                }
            }
        } else {
            if (IDLookup.isOtherField(fieldLocation.getKey())) {
                Object oldValue = otherFieldValues.get(fieldLocation.getKey());
                isChanged = !isEquals(oldValue, value, dataType);
                if (isChanged) {
                    otherFieldValues.put(fieldLocation.getKey(), value);
                }
                return isChanged;
            }
            if (dataTable == null) { // FIXME:这里为空原因是tableKey为空，但是这应该是不可能发生的问题，需要查找原因
                // 找到一种情况，比如button
                Object oldValue = headValues.get(fieldLocation.getKey());
                isChanged = !isEquals(oldValue, value, dataType);
                if (isChanged) {
                    headValues.put(fieldLocation.getKey(), value);
                }
                return isChanged;
            }
            MetaTable metaTable = this.getMetaDataObject().getMetaTable(tableKey);
            int dataTableSize = dataTable.size();
            // 如果这个字段属于表头,但是对应的数据表在明细
            if (metaTable.getTableMode() == TableMode.DETAIL && fieldLocation instanceof HeadFieldLocation) {
                Object oldValue = headValues.get(fieldLocation.getKey());
                isChanged = !isEquals(oldValue, value, dataType);
                if (isChanged) {
                    // 当字段变化时需要更新dataTable中的数据
                    headValues.put(fieldLocation.getKey(), value);
                    for (int rowIndex = 0; rowIndex < dataTableSize; rowIndex++) {
                        dataTable.setObject(rowIndex, columnKey, value);
                    }
                } else {
					// 对应新增行时，取headValues中的值作为字段的默认值
					int currentBookMark = getCurrentBookMark(tableKey);
					if (currentBookMark != FormulaItem.INT_EmptyRowBookmark
							&& currentBookMark != FormulaItem.INT_NotExistSingleTableBookmark) {
						int rowIndex = dataTable.getRowIndexByBookmark(currentBookMark);
						dataTable.setObject(rowIndex, columnKey, value);
					}
				}
			} else {
				if (dataTableSize == 0 && (metaTable.getTableMode() == TableMode.HEAD
						|| metaTable.getTableMode() == TableMode.UNKNOWN)) {
					DocumentUtil.newRow(metaTable, dataTable);
					dataTableSize = 1;
				}
				if (dataTableSize > 0) {
					Object oldValue = dataTable.getObject(0, columnKey);
					isChanged = !isEquals(oldValue, value, dataType);
					if (isChanged) {
						dataTable.setObject(0, columnKey, value);
						if (SystemField.POID_SYS_KEY.equals(columnKey)) {
							DataTable parentDataTable = get(
									idLookup.getTableByFieldKey(fieldKey).getBindingDBTableName());
                            dataTable.setParentBookmark(parentDataTable.getBookmark());
                        }
                    }
                } else {
                    isChanged = false;
                }
            }

        }

        if (isChanged && isDict) {
            setDictCodeValue(idLookup, fieldLocation, value, tableKey, dictCodeColumnKey);
        }
        
        // 需要记录的字段记录值
        if(fieldLocation.getNeedLockValue()) {
        	fieldLocation.setLockValue(isValueNull ? null : value);
        }
        // 存在主动赋值地方情况不做赋值处理
        if(isChanged && fieldLocation.getHasLockValue()) {
        	// 多次赋值是如果和锁定值一致， 则最后不需要补偿赋值
			if( isEquals(fieldLocation.getLockValue(), value, dataType)){
				resetLockFields.remove(fieldLocation);
			}else {
				if(fieldLocation.getNeedCheckLockValue()) {
					LogSvr.getInstance().info("字段"+fieldKey+"赋值与锁定值不一致，新值为:"+value + " 锁定值为:" + fieldLocation.getLockValue());
				}
            	resetLockFields.add(fieldLocation);
            }
        	return isChanged;
        } 

        return isChanged;
    }

    private int[] fastFilter(String tableKey, DataTable dataTable, String[] columnKeys, Object[] values) throws Throwable {
        if (!this.getDocumentTrack().contains(TrackDetail.MIGRATION, "")) {
            Stack<FormulaItem> calcingFormulaItems = this.calcingFormulaItems;
            int[] valueChangedFireSequence = null;
            for (int i = calcingFormulaItems.size() - 1; i >= 0; i--) {
                FormulaItem formulaItem = calcingFormulaItems.get(i);
                if (formulaItem.isValueChanged()) {
                    valueChangedFireSequence = formulaItem.getSequence();
                    break;
                }
            }
            assert(!tableKey.endsWith(MetaFormNODBProcess.STR_NODB4Other_Postfix));
            assert(Arrays.stream(columnKeys).noneMatch(IDLookup::isOtherField));

            List<FormulaItem> formulaItems = effectScopeMap.beforeGetValueCollectFormulaItems(tableKey, columnKeys, valueChangedFireSequence);
            while (formulaItems != null && !formulaItems.isEmpty()) {
                if (calcFormulaItems(formulaItems)) {
                    formulaItems = effectScopeMap.beforeGetValueCollectFormulaItems(tableKey, columnKeys, valueChangedFireSequence);
                } else {
                    formulaItems = null;
                }
            }

        }

        assert(dataTable != null);
        return dataTable.fastFilter(columnKeys, values);
    }

    /**
     * 根据多字段过滤，过滤出行号。过滤前计算必要有延后表达式
     * @param tableKey
     * @param columnKeys
     * @param values
     * @return
     * @throws Throwable
     */
    public int[] fastFilter(String tableKey, String[] columnKeys, Object[] values) throws Throwable {
        DataTable dataTable = get_impl(tableKey);
        if (dataTable == null) {
            return null;
        }
        return fastFilter(tableKey, dataTable, columnKeys, values);
    }

    /**
     * 根据多字段过滤，过滤出行引用（Bookmark）。过滤前计算必要有延后表达式
     * @param tableKey
     * @param columnKeys
     * @param values
     * @return
     * @throws Throwable
     */
    public int[] fastFilterBookmark(String tableKey, String[] columnKeys, Object[] values) throws Throwable {
        DataTable dataTable = get_impl(tableKey);
        if (dataTable == null) {
            return null;
        }
        int[] rowIndexes = fastFilter(tableKey, dataTable, columnKeys, values);
        assert(rowIndexes != null);
        return Arrays.stream(rowIndexes).map(dataTable::getBookmark).toArray();
    }

    private BigDecimal getMaxBigDecimalValue(Integer precision, Integer scale) {
        if (precision <= 0) {
            return null;
        }
        BigDecimal decimal1 = BigDecimal.TEN.pow(precision - scale, MathContext.DECIMAL128);
        BigDecimal zero_one = BigDecimal.ONE.divide(BigDecimal.TEN, 1, BigDecimal.ROUND_HALF_UP);
        BigDecimal decimal2 = zero_one.pow(scale, MathContext.DECIMAL128);

        BigDecimal max = decimal1.subtract(decimal2);
        return max;
    }

    /**
     * 设置字典的Code值
     * @param idLookup
     * @param fieldLocation
     * @param value
     * @param tableKey
     * @param dictCodeColumnKey
     * @throws Throwable
     */
    private void setDictCodeValue(IDLookup idLookup, FieldLocation<?> fieldLocation, Object value, String tableKey,
        String dictCodeColumnKey) throws Throwable {
        if (dictCodeColumnKey.length() == 0) {
            return;
        }

        DataTable dataTable = get_impl(tableKey);
        String itemKey = idLookup.getItemKeyByFieldKey(fieldLocation.getKey());
        // 动态字典itemKey处理
        if (fieldLocation.getComponentType() == ControlType.DYNAMICDICT) {
            String refDataElementKey = idLookup.getDataElementKeyByFieldKey(fieldLocation.getKey());
            if (!StringUtil.isBlankOrNull(refDataElementKey)) {
                String dataElementKey = "";
                if (fieldLocation instanceof HeadFieldLocation) {
                    dataElementKey = dataTable.getString(0, idLookup.getColumnKeyByFieldKey(refDataElementKey));
                } else if (fieldLocation instanceof CellFieldLocation) {
                    int bkmk = fieldLocation.getGridRow().getBookMark();
                    int rowIndex = dataTable.getRowIndexByBookmark(bkmk);
                    dataElementKey = dataTable.getString(rowIndex, idLookup.getColumnKeyByFieldKey(refDataElementKey));
                } else if (fieldLocation instanceof GridEmptyRowFieldLocation) {
                    dataElementKey = "";
                }
                itemKey = MetaUtil.getItemKeyByDataElementKey(context.getVE().getMetaFactory(),
                        dataElementKey);
            } else {
                if (fieldLocation instanceof HeadFieldLocation) {
                    itemKey = dataTable.getString(0, idLookup.getColumnKeyByFieldKey(itemKey));
                } else if (fieldLocation instanceof CellFieldLocation) {
                    int bkmk = fieldLocation.getGridRow().getBookMark();
                    int rowIndex = dataTable.getRowIndexByBookmark(bkmk);
                    itemKey = dataTable.getString(rowIndex, idLookup.getColumnKeyByFieldKey(itemKey));
                } else if (fieldLocation instanceof GridEmptyRowFieldLocation) {
                    itemKey = "";
                }
            }
        }
        if (StringUtil.isBlankOrNull(itemKey)) {
            return;
        }
        Long oid = TypeConvertor.toLong(value);// 多选字典是文本,也不需要转化了
        if (oid == null) {
            return;
        }
        Item dicItem = getContext().getDicItem(itemKey, oid);
        if (dicItem == null) {
            return;
        }

        // 处理显示列,UseCode作为显示列时取UseCode
        String codeColumnKey = SystemField.CODE_DICT_KEY;
        String displayColumnStr = MetaFactory.getGlobalInstance().getDataObject(itemKey).getDisplayColumnsStr();
        if (displayColumnStr != null && displayColumnStr.length() > 0) {
            String[] columns = displayColumnStr.split(";");
            for (String column : columns) {
                if (column.equalsIgnoreCase("UseCode")) {
                    codeColumnKey = column;
                    break;
                }
            }
        }


        String codeValue = TypeConvertor.toString(dicItem.getValue(codeColumnKey));

        GridRow gridRow = fieldLocation.getGridRow();
        if (fieldLocation instanceof GridEmptyRowFieldLocation) {
            List<String> dictCodeFieldKeys = idLookup.getFieldListKeyByTableColumnKey(tableKey, dictCodeColumnKey);
            if (!dictCodeFieldKeys.isEmpty()) {
                for (String dictCodeFieldKey : dictCodeFieldKeys) {
                    setGridEmptyRowValue(dictCodeFieldKey, codeValue);
                }
            }
        } else if (gridRow != null) {
            if (dictCodeColumnKey.length() != 0) {
                gridRow.setObject(this, dictCodeColumnKey, codeValue);
            }
        } else {
            MetaTable metaTable = this.getMetaDataObject().getMetaTable(tableKey);
            int dataTableSize = dataTable.size();
            if (metaTable.getTableMode() == TableMode.DETAIL && fieldLocation instanceof HeadFieldLocation) {
                // 当字段变化时需要更新dataTable中的数据
                List<String> dictCodeFieldKeys = idLookup.getFieldListKeyByTableColumnKey(tableKey, dictCodeColumnKey);
                if (!dictCodeFieldKeys.isEmpty()) {
                    for (String dictCodeFieldKey : dictCodeFieldKeys) {
                        headValues.put(dictCodeFieldKey, codeValue);
                    }
                }
            }
            for (int rowIndex = 0; rowIndex < dataTableSize; rowIndex++) {
                if (dictCodeColumnKey.length() != 0) {
                    dataTable.setObject(rowIndex, dictCodeColumnKey, codeValue);
                }
            }
        }
    }

	/**
	 * 设置空行值<br>
	 * 为了界面处理方面，使用FieldKey作为值的主键，而不是ColumnKey
     * @param fieldKey
     * @param value
     */
    private void setGridEmptyRowValue(String fieldKey, Object value) {
        IDLookup idLookup = IDLookup.getIDLookup(getMetaForm());
        final String gridKey = idLookup.getGridKeyByFieldKey(fieldKey);
        if (!emptyGridRowValues.containsKey(gridKey)) {
            synchronized (emptyGridRowValues) {
                if (!emptyGridRowValues.containsKey(gridKey)) {
                    emptyGridRowValues.put(gridKey, new HashMap<>());
                }
            }
        }
        Map<String, Object> rowValues = emptyGridRowValues.get(gridKey);
        rowValues.put(fieldKey, value);
    }

    public Object getGridEmptyRowValue(String fieldKey) {
        return getGridEmptyRowValue(fieldKey, true);
    }

    /**
	 * 取空白行的值<br>
	 * 为了界面处理方面，使用FieldKey作为值的主键，而不是ColumnKey
     * @param fieldKey
     * @return
     */
    public Object getGridEmptyRowValue(String fieldKey, boolean transNullValue) {
        IDLookup idLookup = IDLookup.getIDLookup(getMetaForm());
        final String gridKey = idLookup.getGridKeyByFieldKey(fieldKey);
        Map<String, Object> rowValues = emptyGridRowValues.get(gridKey);
        Object result = rowValues == null ? null : rowValues.get(fieldKey);
        if (!transNullValue && result == null) {
            return null;
        }
        return TypeConvertor.toDataType(idLookup.getDataTypeByFieldKey(fieldKey), result);
    }

    /**
     * 计算所有的延后表达式
     * @throws Throwable
     */
    public void calcDelayFormula() throws Throwable {
        calcDelayFormula(false);
    }

    /**
     * 计算所有的延后UI表达式
     * TODO 将此方法合并到CalcUtil中
     * @param context
     * @param dirtyTables
     * @param emptyGridRowDirtyValues
     * @param emptyGridRowDirtyValues
     * @throws Throwable
     */
	public JSONObject calcDelayUIFormula(RichDocumentContext context, Set<FieldLocation<?>>dirtyFieldLocations, Set<String> dirtyTables, Map<String, List<FieldLocation<?>>> emptyGridRowDirtyValues) throws Throwable {
        JSONObject result = new JSONObject();
        JSONObject headItems = new JSONObject();
        result.put("headItems", headItems);
        JSONObject headEnableItems = new JSONObject();
        JSONObject headVisibleItems = new JSONObject();
        JSONObject headCheckRuleItems = new JSONObject();
        headItems.put("headEnableItems", headEnableItems);
        headItems.put("headVisibleItems", headVisibleItems);
        headItems.put("headCheckRuleItems", headCheckRuleItems);

        JSONObject gridCellItems = new JSONObject();
        result.put("gridCellItems", gridCellItems);
        JSONObject gridRowCheckRuleItems = new JSONObject();
        result.put("gridRowCheckRuleItems", gridRowCheckRuleItems);
        JSONObject gridCellEnableItems = new JSONObject();
        JSONObject gridCellCheckRuleItems = new JSONObject();
        gridCellItems.put("gridCellEnableItems", gridCellEnableItems);
        gridCellItems.put("gridCellCheckRuleItems", gridCellCheckRuleItems);

        JSONObject gridColumnItems = new JSONObject();
        result.put("gridColumnItems", gridColumnItems);
        JSONObject gridColumnEnableItems = new JSONObject();
        JSONObject gridColumnVisbleItems = new JSONObject();
        gridColumnItems.put("gridColumnEnableItems", gridColumnEnableItems);
        gridColumnItems.put("gridColumnVisibleItems", gridColumnVisbleItems);

        JSONObject gridEmptyRowItems = new JSONObject();
        result.put("gridEmptyRowItems", gridEmptyRowItems);
        JSONObject gridEmptyRowCheckRuleItems = new JSONObject();
        JSONObject gridEmptyRowEnableItems = new JSONObject();
        gridEmptyRowItems.put("gridEmptyRowEnableItems", gridEmptyRowEnableItems);
        gridEmptyRowItems.put("gridEmptyRowCheckRuleItems", gridEmptyRowCheckRuleItems);

        // 将delayUIFormula中字段的影响字段也加入，否则在处理脏数据时还需再计算
        addDependencyFields(delayUIFormula, dirtyFieldLocations, dirtyTables);

		MetaFormAllFormulaScope allFormulaScopes = MetaFormAllFormulScopeCache.instance.processAllFormula(this.getContext().getMetaFactory(), metaForm);
        LinkedHashSet<FieldLocation<?>> remaining = new LinkedHashSet<FieldLocation<?>>();
        while (!delayUIFormula.isEmpty()) {
            FieldLocation<?> fieldLocation = delayUIFormula.iterator().next();
            delayUIFormula.remove(fieldLocation);

            if (fieldLocation.getGridRow() != null) {
                String tableKey = fieldLocation.getTableKey();
                int bookMark = fieldLocation.getGridRow().getBookMark();
                DataTable dataTable = this.get_impl(tableKey);
                if (dataTable == null || !dataTable.isBookmarkExist(bookMark)) {
                    continue;
                }
            }
            calcUIFieldLocation(context, allFormulaScopes, fieldLocation, headItems, gridCellItems, gridEmptyRowItems);
        }

        // 计算行检查公式
        calcGridRowCheckRuleItems(context, allFormulaScopes, gridRowCheckRuleItems);

        // 计算空白行影响字段
		calcEmptyGridRowDirtyValues(context, allFormulaScopes, headItems, gridCellItems, gridEmptyRowItems, emptyGridRowDirtyValues);

        // 计算列可见可用性
        calcGridColumnEnableVisibleItems(context, allFormulaScopes, gridColumnItems, dirtyTables);

        this.calcFieldUIItems(result);
        delayUIFormula.clear();
        delayUIFormula = remaining;
        return result;
    }

    /**
     * 计算表格列可见性可用性
     * @param context
     * @param allFormulaScopes
     * @param gridColumnItems
     * @param dirtyTables
     * @throws Throwable
     */
	private void calcGridColumnEnableVisibleItems(RichDocumentContext context, MetaFormAllFormulaScope allFormulaScopes, JSONObject gridColumnItems, Set<String> dirtyTables) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);

        JSONObject gridColumnVisibleItems = (JSONObject)gridColumnItems.get("gridColumnVisibleItems");
        JSONObject gridColumnEnableItems = (JSONObject)gridColumnItems.get("gridColumnEnableItems");

        for (String tableKey : dirtyTables) {
            String gridKey = idLookup.getGridKeyByTableKey(tableKey);
            MetaGrid metaGrid = idLookup.getMetaGridByGridKey(gridKey);
            if (metaGrid == null) {
                continue;
            }
            MetaGridColumnCollection columnCollection = metaGrid.getColumnCollection();

            JSONObject gridColumnVisibleItem = new JSONObject();
            gridColumnVisibleItems.put(gridKey, gridColumnVisibleItem);
            JSONObject gridColumnEnableItem = new JSONObject();
            gridColumnEnableItems.put(gridKey, gridColumnEnableItem);

            for (MetaGridColumn gridColumn : columnCollection) {
                String gridColumnVisible = gridColumn.getVisible();
                if (gridColumnVisible != null && gridColumnVisible.length() > 0) {
                    FormulaScope gridColumnVisibleScope = allFormulaScopes.getScope(gridColumnVisible);
                    if (gridColumnVisibleScope != null && gridColumnVisibleScope.depend.isHasERPMidFunction() && !gridColumnVisibleScope.depend.isHasOnlyUIFunction()) {
						boolean gridVisibleResult = TypeConvertor.toBoolean(evalUIAttrFormula(context, gridColumn.getKey(), "gridColumnVisible", gridColumnVisible));
                        gridColumnVisibleItem.put(gridColumn.getKey(), gridVisibleResult);
                    }
                }

                String gridColumnEnable = gridColumn.getEnable();
                if (gridColumnEnable != null && gridColumnEnable.length() > 0) {
                    FormulaScope gridColumnEnableScope = allFormulaScopes.getScope(gridColumnEnable);
                    if (gridColumnEnableScope != null && gridColumnEnableScope.depend.isHasERPMidFunction() && !gridColumnEnableScope.depend.isHasOnlyUIFunction()) {
						boolean gridEnableResult = TypeConvertor.toBoolean(evalUIAttrFormula(context, gridColumn.getKey(), "gridColumnEnable", gridColumnEnable));
                        gridColumnEnableItem.put(gridColumn.getKey(), gridEnableResult);
                    }
                }
            }
        }

        if (delayGridColumnVisibleUIFormula != null) {
            for (Map.Entry<String, List<String>> entry : delayGridColumnVisibleUIFormula.entrySet()) {
                String gridKey = entry.getKey();
                JSONObject gridColumnVisibleItem;
                if (!gridColumnVisibleItems.has(gridKey)) {
                    gridColumnVisibleItem = new JSONObject();
                    gridColumnVisibleItems.put(gridKey, gridColumnVisibleItem);
                } else {
                    gridColumnVisibleItem = (JSONObject)gridColumnVisibleItems.get(gridKey);
                }
                List<String> gridColumnKeys = entry.getValue();
                for (int index = 0; index < gridColumnKeys.size(); index++) {
                    String gridColumnKey = gridColumnKeys.get(index);
                    MetaGridColumn gridMetaColumn = idLookup.getGridMetaColumnByKey(gridColumnKey);
                    String gridColumnVisible = gridMetaColumn.getVisible();
                    if (gridColumnVisible != null && gridColumnVisible.length() > 0) {
                        FormulaScope gridColumnVisibleScope = allFormulaScopes.getScope(gridColumnVisible);
                        if (gridColumnVisibleScope != null && gridColumnVisibleScope.depend.isHasERPMidFunction() && !gridColumnVisibleScope.depend.isHasOnlyUIFunction()) {
							boolean gridVisibleResult = TypeConvertor.toBoolean(evalUIAttrFormula(context, gridMetaColumn.getKey(), "gridColumnVisible", gridColumnVisible));
                            gridColumnVisibleItem.put(gridMetaColumn.getKey(), gridVisibleResult);
                        }
                    }
                }
            }
        }
    }


    /**
     * 后端计算列是否可见
     * @param gridMetaColumn
     * @return 有表达式且不是UI公式 根据表达式计算，没有的默认true
     * @throws Throwable
     */
	public boolean evalGridColumnVisible(MetaGridColumn gridMetaColumn) throws Throwable {
		MetaFormAllFormulaScope allFormulaScopes = MetaFormAllFormulScopeCache.instance.processAllFormula(this.getContext().getMetaFactory(), metaForm);
        String gridColumnVisible = gridMetaColumn.getVisible();
        if (gridColumnVisible != null && !gridColumnVisible.isEmpty()) {
            FormulaScope gridColumnVisibleScope = allFormulaScopes.getScope(gridColumnVisible);
            if (gridColumnVisibleScope != null && !gridColumnVisibleScope.depend.isHasOnlyUIFunction()) {
                return TypeConvertor.toBoolean(evaluate(gridColumnVisible));
            }
        }
        return true;
    }

    /**
     * 后端计算头控件是否可编辑
	 * @param fieldKey
	 * @param isByFormInitState true:界面上打开单据时还没修改状态前，按理说这个时候不能计算Enable属性，有缺陷
	 * @return 有表达式：不是UI公式 根据表达式计算，没有的默认true
	 * 无表达式：按照单据状态
	 * @throws Throwable
	 */
	public boolean evalHeadFieldEnable(String fieldKey, boolean isByFormInitState) throws Throwable {
		MetaComponent comp = IDLookup.getIDLookup(metaForm).getComponentByKey(fieldKey);
		MetaFormAllFormulaScope allFormulaScopes = MetaFormAllFormulScopeCache.instance.processAllFormula(this.getContext().getMetaFactory(), metaForm);
		String enable = comp.getEnable();
		if (enable != null && !enable.isEmpty()) {
			FormulaScope gridColumnVisibleScope = allFormulaScopes.getScope(enable);
			if (gridColumnVisibleScope != null && !gridColumnVisibleScope.depend.isHasOnlyUIFunction()) {
				return TypeConvertor.toBoolean(evaluate(enable));
			}
			return true;
		} else {
			int state = isByFormInitState ? metaForm.getInitState() : form_OperationState;
			return state == OperationState.Edit || state == OperationState.New;
		}
	}

	/**
	 * 后端计算头控件是否可见
	 * @param fieldKey
	 * @return 有表达式：不是UI公式 根据表达式计算，没有的默认true
	 * 		   无表达式：true
     * @throws Throwable
     */
    public boolean evalHeadFieldVisible(String fieldKey) throws Throwable {
        MetaComponent comp = IDLookup.getIDLookup(metaForm).getComponentByKey(fieldKey);
		MetaFormAllFormulaScope allFormulaScopes = MetaFormAllFormulScopeCache.instance.processAllFormula(this.getContext().getMetaFactory(), metaForm);
        String visible = comp.getVisible();
        if (visible != null && !visible.isEmpty()) {
            FormulaScope gridColumnVisibleScope = allFormulaScopes.getScope(visible);
            if (gridColumnVisibleScope != null && !gridColumnVisibleScope.depend.isHasOnlyUIFunction()) {
                return TypeConvertor.toBoolean(evalUIAttrFormula(context, fieldKey, "visible", visible));
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * 计算掉空白行的影响字段
     * @param context
     * @param allFormulaScopes
     * @param headItems
     * @param gridCellItems
     * @param gridEmptyRowItems
     * @param emptyGridRowDirtyValues
     * @throws Throwable
     */
	private void calcEmptyGridRowDirtyValues(RichDocumentContext context, MetaFormAllFormulaScope allFormulaScopes,
											 JSONObject headItems, JSONObject gridCellItems, JSONObject gridEmptyRowItems, Map<String, List<FieldLocation<?>>> emptyGridRowDirtyValues) throws Throwable {
        // 收集空白行的影响字段
        for (Map.Entry<String, List<FieldLocation<?>>> entry : emptyGridRowDirtyValues.entrySet()) {
            String tableKey = entry.getKey();
            List<FieldLocation<?>> emptyRowDirty = emptyGridRowDirtyValues.get(tableKey);
            if (emptyRowDirty.size() == 0) {
                continue;
            }
            for (FieldLocation<?> fieldLocation : emptyRowDirty) {
                int bkmk = fieldLocation.getBookMark();
                String fieldKey = fieldLocation.getKey();
                execUIFormula(context, fieldKey, bkmk);
            }
        }

        while (!delayUIFormula.isEmpty()) {
            FieldLocation<?> fieldLocation = delayUIFormula.iterator().next();
            delayUIFormula.remove(fieldLocation);
            calcUIFieldLocation(context, allFormulaScopes, fieldLocation, headItems, gridCellItems, gridEmptyRowItems);

        }
    }

    /**
     * 计算行检查公式
	 * @param context
	 * @param gridRowCheckRuleItems
	 * @throws Throwable
	 */
	private void calcGridRowCheckRuleItems(RichDocumentContext context, MetaFormAllFormulaScope allFormulaScopes, JSONObject gridRowCheckRuleItems) throws Throwable {
		IDLookup idLookup = IDLookup.getIDLookup(metaForm);
		if (delayGridRowCheckUIFormula.size() > 0) {
			for (Map.Entry<String, HashMap<Integer, GridRow>> entry : delayGridRowCheckUIFormula.entrySet()) {
				String tableKey = entry.getKey();
				DataTable dataTable = this.get(tableKey);
				HashMap<Integer, GridRow> gridRows = entry.getValue();
				if (gridRows.size() > 0) {
					String gridKey = idLookup.getGridKeyByTableKey(tableKey);
					if(StringUtil.isBlankOrNull(gridKey)){ //只有固定行的表格
						continue;
					}
					MetaGrid metaGrid = idLookup.getMetaGridByGridKey(gridKey);

					MetaUICheckRuleCollection checkRuleCollection = metaGrid.getDetailMetaRow()
							.getCheckRuleCollection();
					if (checkRuleCollection == null || checkRuleCollection.size() == 0) {
						continue;
					}

					JSONObject gridRowCheckRuleItem = null;
					if (gridRowCheckRuleItems.has(gridKey)) {
						gridRowCheckRuleItem = (JSONObject) gridRowCheckRuleItems.get(gridKey);
					} else {
						gridRowCheckRuleItem = new JSONObject();
						gridRowCheckRuleItems.put(gridKey, gridRowCheckRuleItem);
					}

					for (Map.Entry<Integer, GridRow> dtl : gridRows.entrySet()) {
						int bkmk = dtl.getKey();
						if(!dataTable.isBookmarkExist(bkmk)) {
							continue;
						}
						GridRow oldGridRow = setGridRow(dtl.getValue());
						String gridCheckRuleResult = "";
						gridRowCheckRuleItem.put(dtl.getKey().toString(), true);
						for (MetaUICheckRule uicheckRule : checkRuleCollection) {
							String content = uicheckRule.getContent();
							FormulaScope scope = allFormulaScopes.getScope(content);
							if (scope != null && scope.depend.isHasERPMidFunction() && !scope.depend.isHasOnlyUIFunction()) {
								gridCheckRuleResult = TypeConvertor.toString(evalUIAttrFormula(context,
										metaGrid.getDetailMetaRow().getKey(), "GridRowCheckRule", content));
								if (gridCheckRuleResult.equals("") && !gridCheckRuleResult.equalsIgnoreCase("true")) {
									gridRowCheckRuleItem.put(dtl.getKey().toString(), gridCheckRuleResult);
									break;
								}
							}
						}
						restoreGridRow(oldGridRow);
					}
				}
			}
		}
	}

	/**
	 * 增加依赖字段
	 *
	 * @param delayUIFormula
	 * @param dirtyFieldLocations
	 * @param dirtyTables
	 * @throws Throwable
	 */
	private void addDependencyFields(LinkedHashSet<FieldLocation<?>> delayUIFormula, Set<FieldLocation<?>> dirtyFieldLocations, Set<String> dirtyTables) throws Throwable {
		// 增加延迟计算计算的依赖字段
		//		LinkedHashSet<FieldLocation<?>> delayDependFields = new  LinkedHashSet<FieldLocation<?>>();
		//		delayDependFields.addAll(delayUIFormula);
		//		while(!delayDependFields.isEmpty()) {
		//			FieldLocation<?> fieldLocation = delayDependFields.iterator().next();
		//			delayDependFields.remove(fieldLocation);
		//			if(fieldLocation instanceof GridEmptyRowFieldLocation) {
		//				continue;
		//			}
		//			int bookMark = -1;
		//			if (fieldLocation.getGridRow() != null) {
		//				String tableKey = fieldLocation.getTableKey();
		//				bookMark = fieldLocation.getGridRow().getBookMark();
		//				DataTable dataTable = this.getDataTable(tableKey);
		//				if (dataTable == null || !DataTableExUtil.containsBookMark(dataTable, bookMark)) {
		//					continue;
		//				}
		//			}else {
		//				bookMark = 0;
		//			}
		//			execUIFormula(context, fieldLocation.getKey(), bookMark);
		//		}

		// 增加脏数据字段的影响字段
		LinkedHashSet<FieldLocation<?>> dirtyFieldLocationsDependencyFields = new  LinkedHashSet<FieldLocation<?>>();
		dirtyFieldLocationsDependencyFields.addAll(dirtyFieldLocations);
		while (!dirtyFieldLocationsDependencyFields.isEmpty()) {
			FieldLocation<?> fieldLocation = dirtyFieldLocationsDependencyFields.iterator().next();
			dirtyFieldLocationsDependencyFields.remove(fieldLocation);
			if(fieldLocation instanceof GridEmptyRowFieldLocation) {
				continue;
			}
			int bookMark = -1;
			if (fieldLocation.getGridRow() != null) {
				String tableKey = fieldLocation.getTableKey();
                if (dirtyTables.contains(tableKey)) {
                    continue;
                }
				bookMark = fieldLocation.getGridRow().getBookMark();
				DataTable dataTable = this.get_impl(tableKey);
				if (!dataTable.isBookmarkExist(bookMark)) {
					continue;
				}
			}else {
				bookMark = 0;
			}
            execUIFormula(context, fieldLocation.getKey(), bookMark, dirtyTables);
		}
		IDLookup idLookup = IDLookup.getIDLookup(metaForm);
		for(String tableKey : dirtyTables) {
			List<String> fieldListByTableKey = idLookup.getFieldListByTableKey(tableKey);
			DataTable dataTable = get(tableKey);
			if (fieldListByTableKey == null || fieldListByTableKey.size() == 0 || dataTable == null ||dataTable.size() == 0) {
				continue;
			}
			// TODO 这里可以计算部分行
            int[] bkmks = new int[dataTable.size()];
			for(int rowIndex=0;rowIndex<dataTable.size();rowIndex++) {
                bkmks[rowIndex] = dataTable.getBookmark(rowIndex);
			}
            for (String fieldKey : fieldListByTableKey) {
                execUIFormula(context, fieldKey, bkmks, dirtyTables);
            }
		}
	}

    public boolean isNeedUICalc(IDLookup idLookup,String fieldKey) throws Throwable {
        MetaFormAllFormulaScope allFormulaScopes = MetaFormAllFormulScopeCache.instance.processAllFormula(this.getContext().getMetaFactory(), metaForm);
        boolean isNeedCalcEnable = false;
        String enable = idLookup.getEnable(fieldKey);
        if (enable != null && enable.length() > 0) {
            FormulaScope enableScope = allFormulaScopes.getScope(enable);
            if (enableScope != null && enableScope.depend.isHasERPMidFunction() && !enableScope.depend.isHasOnlyUIFunction()) {
                isNeedCalcEnable = true;
            }
        }
        String visible = idLookup.getVisible(fieldKey);
        boolean isNeedCalcVisible = false;
        if (visible != null && visible.length() > 0) {
            FormulaScope visibleScope = allFormulaScopes.getScope(visible);
            if (visibleScope != null && visibleScope.depend.isHasERPMidFunction() && !visibleScope.depend.isHasOnlyUIFunction()) {
                isNeedCalcVisible = true;
            }
        }
        boolean isNeedCalcCheckRule = false;
        List<String> checkRulesByFieldKey = IDLookup.getIDLookup(metaForm).getCheckRuleByFieldKey(fieldKey);
        if (this.form_OperationState != OperationState.Default) {
            if (checkRulesByFieldKey != null && checkRulesByFieldKey.size() > 0) {
                for (String checkRule : checkRulesByFieldKey) {
                    if (checkRule != null && checkRule.length() > 0) {
                        FormulaScope checkRuleScope = allFormulaScopes.getScope(checkRule);
                        if (checkRuleScope != null && checkRuleScope.depend.isHasERPMidFunction() && !checkRuleScope.depend.isHasOnlyUIFunction()) {
                            isNeedCalcCheckRule = true;
                            break;
                        }
                    }
                }
            }
        }
        if(isNeedCalcEnable || isNeedCalcVisible || isNeedCalcCheckRule) {
            return true;
        }
        return false;
    }

	/**
	 * 计算字段Enable等属性，只计算hasErpMidfunction的公式
	 * @param context
	 * @param allFormulaScopes
	 * @param fieldLocation
	 * @param headItems
	 * @param gridCellItems
	 * @param gridEmptyRowItems
	 * @return
	 * @throws Throwable
	 */
	private JSONObject calcUIFieldLocation(RichDocumentContext context, MetaFormAllFormulaScope allFormulaScopes, FieldLocation<?> fieldLocation, JSONObject headItems, JSONObject gridCellItems, JSONObject gridEmptyRowItems) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String fieldKey = fieldLocation.getKey();

        String enable = idLookup.getEnable(fieldKey);
        boolean isEnable = true;
        if (enable != null && enable.length() > 0) {
            FormulaScope enableScope = allFormulaScopes.getScope(enable);
            if (enableScope != null && enableScope.depend.isHasERPMidFunction() && !enableScope.depend.isHasOnlyUIFunction()) {
                isEnable = TypeConvertor.toBoolean(calcUIAttrFormula(context, fieldLocation, "enable", enable));
                if (fieldLocation instanceof HeadFieldLocation) {
                    JSONObject headEnableItems = (JSONObject)headItems.get("headEnableItems");
                    headEnableItems.put(fieldKey, isEnable);
                } else if (fieldLocation instanceof GridEmptyRowFieldLocation) {
                    String tableKey = fieldLocation.getTableKey();
                    String gridKey = idLookup.getGridKeyByTableKey(tableKey);

                    JSONObject gridEmptyRowEnableItems = (JSONObject)gridEmptyRowItems.get("gridEmptyRowEnableItems");
                    JSONObject gridEmptyRowEnableItem = null;
                    if (gridEmptyRowEnableItems.has(gridKey)) {
                        gridEmptyRowEnableItem = (JSONObject)gridEmptyRowEnableItems.get(gridKey);
                    } else {
                        gridEmptyRowEnableItem = new JSONObject();
                        gridEmptyRowEnableItems.put(gridKey, gridEmptyRowEnableItem);
                    }

                    gridEmptyRowEnableItem.put(fieldKey, isEnable);
                } else if (fieldLocation instanceof CellFieldLocation) {
                    String tableKey = fieldLocation.getTableKey();
                    String gridKey = idLookup.getGridKeyByTableKey(tableKey);
                    int bookMark = fieldLocation.getBookMark();
                    JSONObject gridCellEnableItems = (JSONObject)gridCellItems.get("gridCellEnableItems");

                    JSONObject gridCellEnableItem = null;
                    if (gridCellEnableItems.has(gridKey)) {
                        gridCellEnableItem = (JSONObject)gridCellEnableItems.get(gridKey);
                    } else {
                        gridCellEnableItem = new JSONObject();
                        gridCellEnableItems.put(gridKey, gridCellEnableItem);
                    }

                    JSONObject rowEnableItems = null;
                    if (gridCellEnableItem.has(String.valueOf(bookMark))) {
                        rowEnableItems = (JSONObject)gridCellEnableItem.get(String.valueOf(bookMark));
                    } else {
                        rowEnableItems = new JSONObject();
                        gridCellEnableItem.put(String.valueOf(bookMark), rowEnableItems);
                    }
                    rowEnableItems.put(fieldKey, isEnable);
                }
            }
        }
        String visible = idLookup.getVisible(fieldKey);
        if (visible != null && visible.length() > 0) {
            FormulaScope visibleScope = allFormulaScopes.getScope(visible);
            if (visibleScope != null && visibleScope.depend.isHasERPMidFunction() && !visibleScope.depend.isHasOnlyUIFunction()) {
                boolean temp = TypeConvertor.toBoolean(calcUIAttrFormula(context, fieldLocation, "visible", visible));
                if (fieldLocation instanceof HeadFieldLocation) {
                    JSONObject headVisibleItems = (JSONObject)headItems.get("headVisibleItems");
                    headVisibleItems.put(fieldKey, temp);
                }
            }
        }

        List<String> checkRulesByFieldKey = IDLookup.getIDLookup(metaForm).getCheckRuleByFieldKey(fieldKey);
        if (checkRulesByFieldKey != null && checkRulesByFieldKey.size() > 0) {
            for (String checkRule : checkRulesByFieldKey) {
                if (checkRule != null && checkRule.length() > 0 && (isEnable || this.form_OperationState != OperationState.Default)) {
                    FormulaScope checkRuleScope = allFormulaScopes.getScope(checkRule);
                    if (checkRuleScope != null && checkRuleScope.depend.isHasERPMidFunction() && !checkRuleScope.depend.isHasOnlyUIFunction()) {
                        Object temp = calcUIAttrFormula(context, fieldLocation, "checkRule", checkRule);
                        temp = temp == null ? "" : temp;
                        if (fieldLocation instanceof HeadFieldLocation) {
                            JSONObject headCheckRuleItems = (JSONObject)headItems.get("headCheckRuleItems");
                            headCheckRuleItems.put(fieldKey, temp);
                        } else if (fieldLocation instanceof GridEmptyRowFieldLocation) {
                            String tableKey = fieldLocation.getTableKey();
                            String gridKey = idLookup.getGridKeyByTableKey(tableKey);

							JSONObject gridEmptyRowCheckRuleItems = (JSONObject)gridEmptyRowItems.get("gridEmptyRowCheckRuleItems");
                            JSONObject gridEmptyRowCheckRuleItem = null;
                            if (gridEmptyRowCheckRuleItems.has(gridKey)) {
                                gridEmptyRowCheckRuleItem = (JSONObject)gridEmptyRowCheckRuleItems.get(gridKey);
                            } else {
                                gridEmptyRowCheckRuleItem = new JSONObject();
                                gridEmptyRowCheckRuleItems.put(gridKey, gridEmptyRowCheckRuleItem);
                            }

                            gridEmptyRowCheckRuleItem.put(fieldKey, temp);
                        } else if (fieldLocation instanceof CellFieldLocation) {
                            String tableKey = fieldLocation.getTableKey();
                            String gridKey = idLookup.getGridKeyByTableKey(tableKey);
                            int bookMark = fieldLocation.getBookMark();
                            JSONObject gridCellCheckRuleItems = (JSONObject)gridCellItems.get("gridCellCheckRuleItems");

                            JSONObject gridCellCheckRuleItem = null;
                            if (gridCellCheckRuleItems.has(gridKey)) {
                                gridCellCheckRuleItem = (JSONObject)gridCellCheckRuleItems.get(gridKey);
                            } else {
                                gridCellCheckRuleItem = new JSONObject();
                                gridCellCheckRuleItems.put(gridKey, gridCellCheckRuleItem);
                            }

                            JSONObject rowCheckRuleItems = null;
                            if (gridCellCheckRuleItem.has(String.valueOf(bookMark))) {
                                rowCheckRuleItems = (JSONObject)gridCellCheckRuleItem.get(String.valueOf(bookMark));
                            } else {
                                rowCheckRuleItems = new JSONObject();
                                gridCellCheckRuleItem.put(String.valueOf(bookMark), rowCheckRuleItems);
                            }

                            rowCheckRuleItems.put(fieldKey, temp);
                        }
                    }
                }
            }
        }
        return null;
    }

	private Object calcUIAttrFormula(RichDocumentContext context, FieldLocation<?> fieldLocation, String attrType, String formula) throws Throwable {
        Object result = null;
        String tableKey = fieldLocation.getTableKey();
        String fieldKey = fieldLocation.getKey();

        if (fieldLocation instanceof HeadFieldLocation) {
            result = evalUIAttrFormula(context, fieldKey, attrType, formula);
        } else if (fieldLocation instanceof GridEmptyRowFieldLocation) {
            GridRow oldGridRow = setGridRow(new GridRow(this, tableKey, GridRow.GridEmptyBookMark));
            result = evalUIAttrFormula(context, fieldKey, attrType, formula);
            restoreGridRow(oldGridRow);
        } else if (fieldLocation instanceof CellFieldLocation) {
            CellFieldLocation cellFieldLocation = (CellFieldLocation)fieldLocation;
            if (cellFieldLocation.getGridRow() == null) {
                throw new Exception("RichDocument calc UI计算错误，不应该运行到这里");
            }
            GridRow oldGridRow = setGridRow(cellFieldLocation.getGridRow());
            result = evalUIAttrFormula(context, fieldKey, attrType, formula);
            restoreGridRow(oldGridRow);
        }
        return result;
    }

	private Object evalUIAttrFormula(RichDocumentContext context, String comKey, String type, String formula) throws Throwable {
		Object result = null;
		try {
			result = context.getMidParser().eval(ScriptType.Formula, formula);
		} catch (Throwable e) {
			String error = "计算表单：" + context.getFormKey() + "中组件：" + comKey + "的" + type + "属性：" + formula + "时不正确";
			LogSvr.getInstance().error(error, null);
			if (TraceSetting.getOperatingEnvironment(context) == AppRunType.App){
				throw e;
			} else {
				context.getRichDocument().appendUICommand(new UICommand(UICommand.UI_CMD_ShowFormulaErrorInDesignMode, error));
			}
		}
		return result;
	}

    /**
     * 计算所有的延后表达式
     * @param onlyPersist 只处理存储到数据库中的字段
     * @throws Throwable
     */
    public void calcDelayFormula(boolean onlyPersist) throws Throwable {
        List<FormulaItem> formulaItems = effectScopeMap.collectFormulaItems();
        while (formulaItems != null && formulaItems.size() > 0) {
            // FIXME: 处理onlyPersist参数
            if (calcFormulaItems(formulaItems)) {
                formulaItems = effectScopeMap.collectFormulaItems();
            } else {
                formulaItems = null;
            }
        }
    }
    
    /**
     * 种植锁值
     * @throws Throwable
     */
    public void revertLockFields() throws Throwable{
    	doResetLocation(0);
    	this.fieldLocationCache.clear();
    }
    
    private void doResetLocation(int deep) throws Throwable{
        if(!resetLockFields.isEmpty()) {
        	if(deep > 2) {
        		throw new RuntimeException("当前赋值依赖可能存在循环。");
        	}
	        Iterator<FieldLocation<?>> it = resetLockFields.iterator();
	        while (it.hasNext()) {
	        	FieldLocation<?> fieldLocation = it.next();
	        	// 存在新增后删除行的情况， 需要先删除fieldLocation
	            resetLockFields.remove(fieldLocation);
	            if (fieldLocation.getGridRow() != null) {
	                String tableKey = fieldLocation.getTableKey();
	                int bookMark = fieldLocation.getGridRow().getBookMark();
	                DataTable dataTable = this.get_impl(tableKey);
	                if (dataTable == null || !dataTable.isBookmarkExist(bookMark)) {
	                    continue;
	                }
	            }
	            this.setValue(this.getContext(), fieldLocation, fieldLocation.getLockValue()); 
	        }
	        calcDelayFormula(true);
	    	doResetLocation(++deep);
        }
    }
    /**
     * 获取延迟计算表达式,用于进一步处理
     * @return 延迟计算表达式
     */
    public List<FormulaItem> collectDelayFormula() throws Throwable {
        return effectScopeMap.collectFormulaItems();
    }

    /**
     * 计算表达式列表，若有表达式计算过，返回true，否则返回false
     * @param formulaItems
     * @return
     * @throws Throwable
     */
    private boolean calcFormulaItems(List<FormulaItem> formulaItems) throws Throwable {
        boolean result = false;
        int includeDocumentCount = 0;
        List<FormulaItem> includeDocumentFormulaItems = new ArrayList<>();
        for (int i = 0, size = formulaItems.size(); i < size; i++) {
            FormulaItem formulaItem = formulaItems.get(i);
            if (!formulaItem.getScope().effect.isIncludeDocument()) {
                includeDocumentCount = i;
                break;
            }
            if (formulaItem.isEnable()) {
                includeDocumentFormulaItems.add(formulaItem);
                formulaItem.startCalcing();
            }
        }
        for (FormulaItem formulaItem : includeDocumentFormulaItems) {
            if (!this.calcingFormulaItems.contains(formulaItem)) {
                this.calcingFormulaItems.add(formulaItem);
                calcFormulaItem(formulaItem);
                if (this.calcingFormulaItems.peek() != formulaItem) {
                    throw new AssertionError();
                }
                formulaItem.endCalcing();
                this.calcingFormulaItems.pop();
                result = true;
            }
        }
        for (int i = includeDocumentCount, size = formulaItems.size(); i < size; i++) {
            FormulaItem formulaItem = formulaItems.get(i);
            if (formulaItem.isEnable()) { // 表达式因为依赖关系，可能被先计算掉了
                if (!this.calcingFormulaItems.contains(formulaItem)) {
                    this.calcingFormulaItems.add(formulaItem);
                    formulaItem.startCalcing();
                    calcFormulaItem(formulaItem);
                    if (this.calcingFormulaItems.peek() != formulaItem) {
                        throw new AssertionError();
                    }
                    formulaItem.endCalcing();
                    this.calcingFormulaItems.pop();
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 计算表达式项
     * @param formulaItem
     * @throws Throwable
     */
    private void calcFormulaItem(FormulaItem formulaItem) throws Throwable {
        TableKeyAndBookmark location = formulaItem.getTableBookmark();
        String tableKey = location.getTableKey();
        String columnKey = formulaItem.getTargetColumnKey();
        if (columnKey == null) {
            columnKey = formulaItem.getSourceColumnKey();
        }
        MetaTable metaTable = getMetaDataObject().getTable(tableKey);
        int bookMark = location.getBookMark();
        EffectScopeInDoc effect = formulaItem.getScope().effect;
        boolean isEffectDocumentOrTable = false;
        if (effect.isIncludeDocument() || (effect.getTableKeys() != null && effect.hasTableKey(tableKey))) {
            isEffectDocumentOrTable = true;
        }
		if (!(metaTable == null || metaTable.isHead()) && bookMark != FormulaItem.INT_EmptyRowBookmark
				&& bookMark != FormulaItem.INT_NotExistSingleTableBookmark
				&& !get_impl(tableKey).isBookmarkExist(bookMark) && !isEffectDocumentOrTable) {
            return;
        }
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String fieldKey = formulaItem.getFieldKey();
        // 这里的判断是为了和RichDocument.getValue中对无数据源字段的处理保持一致
        if (isBlankOrNull(fieldKey)) {
            if (tableKey == null) {
                fieldKey = columnKey;
            } else {
                final List<String> fieldKeys = idLookup.getFieldListKeyByTableColumnKey(tableKey, columnKey);
                fieldKey = fieldKeys.isEmpty() ? "" : fieldKeys.get(0);
            }
        }
        String formulaValue = formulaItem.getFormula();
        if (formulaItem.isDefaultFormulaValue()) {
            Object result = null;

            int dataType;
            if (tableKey == null || tableKey.length() == 0) {
                dataType = idLookup.getDataTypeByComponentKey(fieldKey);
            } else {
                dataType = metaTable.get(columnKey).getDataType();
            }

            if (formulaValue == null || formulaValue.isEmpty()) {
                throw new AssertionError();
            }

            if (formulaItem.isDefaultValue()) {
                result = convert(dataType, formulaValue);
            } else {
                GridRow oldGridRow = setGridRow(new GridRow(this, tableKey, bookMark));
                // 需要存一下原值，会有重复进入的情况，应以最外层的状态为准
                boolean oldIgnoreLockValue = this.ignoreLockValue;
                try {
                    this.ignoreLockValue = true;
                    result = context.evalWithTrace(MetaObjectType.Component, fieldKey, Property.DefaultFormulaValue,
                        formulaValue);
                } catch (ArithmeticException e) {
                    if (this.isIgnoreArithmeticException()) {
                        LogSvr.getInstance().error(e.getMessage(), e);
                        this.addArithmeticExceptions(fieldKey, bookMark);
                        result = null;
                    } else {
                        ExceptionUtils.rethrow(e);
                    }
                }finally {
                  	this.ignoreLockValue = oldIgnoreLockValue;
                }
                restoreGridRow(oldGridRow);
                result = convert(dataType, result);
            }

    		if (metaTable !=null && !metaTable.isHead()
    				&& !get_impl(tableKey).isBookmarkExist(bookMark) 
    				&& bookMark != FormulaItem.INT_NotExistSingleTableBookmark
    				&& bookMark != FormulaItem.INT_EmptyRowBookmark) {
                return;
            }
    		
            FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookMark);
            if(fieldLocation.getHasLockValue()) {
            	fieldLocation.setNeedCheckLockValue(true);
            }
            if (setValueNoChanged(fieldLocation, result) && calcingFormulaItems != null && calcingFormulaItems.peek().isHasGetValueInCalcing()) {
                execDefaultFormulaValue(context, fieldKey, bookMark);
            }
        } else if (formulaItem.isValueChanged()) {
            GridRow oldGridRow = setGridRow(new GridRow(this, tableKey, bookMark));
            boolean oldIgnoreLockValue = this.ignoreLockValue;
            try {
                this.ignoreLockValue = true;
            	context.evalWithTrace(MetaObjectType.Component, fieldKey, Property.ValueChanged, formulaValue);
            }finally {
            	this.ignoreLockValue = oldIgnoreLockValue;
            }
            restoreGridRow(oldGridRow);
        }
    }

    private void addArithmeticExceptions(String fieldKey, int bookmark) {
        String simpleName = ArithmeticException.class.getSimpleName();
        JSONObject expandData = (JSONObject)this.getExpandData(simpleName);
        if (expandData == null) {
            expandData = new JSONObject();
            this.putExpandData(simpleName, expandData);
        }
        JSONObject o;
        if (expandData.has(fieldKey)) {
            o = expandData.getJSONObject(fieldKey);
        } else {
            o = new JSONObject();
            expandData.put(fieldKey, o);
        }
        o.put(TypeConvertor.toString(bookmark), true);
    }

    /**
     * 表格空白行
     * @param metaGrid
     * @param tableKey
     * @throws Throwable
     */
    public void gridEmptyRow(MetaGrid metaGrid, String tableKey) throws Throwable {
        MetaGridRow metaGridRow = metaGrid.getDetailMetaRow();
        for (MetaGridCell metaGridCell : metaGridRow) {
            MetaDataBinding dataBinding = metaGridCell.getDataBinding();
            //对一些Template 表单会出现dataBinding.getColumnKey()=""情况。
            if (dataBinding == null || dataBinding.getColumnKey().length() == 0) {
                continue;
            }

            processDtlDefaultFormulaValueItem(metaGridCell.getKey(), FormulaItem.INT_EmptyRowBookmark,
                "com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocument.gridEmptyRow(MetaGrid metaGrid, String tableKey)");
        }
    }

    public void gridEmptyRow(String gridKey) throws Throwable {
        IDLookup idlookup = IDLookup.getIDLookup(metaForm);
        MetaGrid metaGrid = idlookup.getMetaGridByGridKey(gridKey);
        gridEmptyRow(metaGrid, metaGrid.getTableKey());
    }

	private void pCalRowDefaultRow(RichDocumentContext context, int bookMark, String tableKey, DataTable dataTable, int rowIndex) throws Throwable {
        IDLookup idlookup = IDLookup.getIDLookup(metaForm);
        String gridKey = idlookup.getGridKeyByTableKey(tableKey);
        String primaryTableKey = metaForm.getDataSource().getDataObject().getMainTableKey();
        if (StringUtil.isBlankOrNull(gridKey) && !tableKey.equalsIgnoreCase(primaryTableKey)) {
            //针对不是主表，但界面不是grid子表方式显示的情况，要算默认值。主要用于组织字典中的各个视图
            List<MetaComponent> components = metaForm.getAllComponents();
            // MetaTable metaTable = metaForm.getDataSource().getDataObject().getMetaTable(tableKey);
            for (MetaComponent component : components) {
                MetaDataBinding dataBinding = component.ensureDataBinding();
                String comptableKey = dataBinding.getTableKey();
                if (isBlankOrNull(comptableKey) || !comptableKey.equalsIgnoreCase(tableKey)) {
                    continue;
                }
                processHeadDefaultFormulaValueItem(component.getKey(),
                    "com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocument.pCalRowDefaultRow(RichDocumentContext context, int bookMark, String tableKey, DataTable dataTable, int rowIndex)");
            }
        } else {
            for (String fieldKey : idlookup.getFieldKeys()) {
                String fieldTableKey = idlookup.getTableKeyByFieldKey(fieldKey);
                if (!tableKey.equals(fieldTableKey)) {
                    continue;
                }
                // 对于表头上的一些字段默认值已经在新增表单时就已经处理.
                if (this.headValues.containsKey(fieldKey)) {
                    this.setValue(context, fieldKey, bookMark, headValues.get(fieldKey));
                }
            }

            if (!StringUtil.isBlankOrNull(gridKey)) {
                List<MetaGrid> metaGrids = idlookup.getMetaGrids();
                for (MetaGrid metaGrid : metaGrids) {
                    MetaGridRow metaGridRow = metaGrid.getDetailMetaRow();
                    if (metaGridRow == null) {
                        continue;
                    }
                    String rowTableKey = metaGridRow.getTableKey();
                    if (!tableKey.equals(rowTableKey)) {
                        continue;
                    }
                    for (MetaGridCell metaGridCell : metaGridRow) {
                        MetaDataBinding dataBinding = metaGridCell.getDataBinding();
                        if (dataBinding == null || isBlankOrNull(dataBinding.getColumnKey())) {
                            continue;
                        }
                        processDtlDefaultFormulaValueItem(metaGridCell.getKey(), bookMark,
                            "com.bokesoft.yes.mid.cmd.richdocument.strut.RichDocument.pCalRowDefaultRow(RichDocumentContext context, int bookMark, String tableKey, DataTable dataTable, int rowIndex)");
                    }
                }
            }
        }
    }


	/**
	 * 对新增行设置默认值
     * @param context
     * @param bookMark
     * @param tableKey
     * @param dataTable
     * @param rowIndex
     * @throws Throwable
     */
    public void setDefaultValue4NewRow(RichDocumentContext context, int bookMark, String tableKey, DataTable dataTable,
        int rowIndex) throws Throwable {
        IDLookup idlookup = IDLookup.getIDLookup(metaForm);

        for (String componentKey : idlookup.getFieldListByTableKey(tableKey)) {
            AbstractMetaObject object = idlookup.getMetaObjectByKey(componentKey);
            if (object == null) {
                continue;
            }
            if (object instanceof MetaComponent) {
                MetaComponent comp = (MetaComponent)object;
                String defaultValue = comp.getDefaultValue();
                if (!StringUtil.isBlankOrNull(defaultValue)) {
                    int dataType = idlookup.getDataTypeByFieldKey(componentKey);
                    Object result = RichDocument.convert(dataType, defaultValue);
                    dataTable.setObject(comp.getColumnKey(), result);
                }
            } else if (object instanceof MetaGridCell) {
                MetaGridCell gridCell = (MetaGridCell)object;
                String defaultValue = gridCell.getDefaultValue();
                if (!StringUtil.isBlankOrNull(defaultValue)) {
                    int dataType = idlookup.getDataTypeByFieldKey(componentKey);
                    Object result = RichDocument.convert(dataType, defaultValue);
                    dataTable.setObject(gridCell.getColumnKey(), result);
                }
            }
        }
    }

    /**
     * 子表格增加行
     * @param context
     * @param childTableKey
     * @param parentTableKey
     * @param parentOid
     * @return
     * @throws Throwable
     */
	public int appendChildDetail(RichDocumentContext context, String childTableKey, String parentTableKey, long parentOid) throws Throwable {
        addDirtyTableFlag(childTableKey);

        DataTable parentDataTable = get(parentTableKey);
        int parentBookMark = -1;
        int pos = parentDataTable.getPos();
        parentDataTable.beforeFirst();
        while (parentDataTable.next()) {
            if (parentDataTable.getLong(SystemField.OID_SYS_KEY) == parentOid) {
                parentBookMark = parentDataTable.getBookmark();
            }
        }
        parentDataTable.setPos(pos);
        // DataTable dataTable = get(childTableKey);
        // dataTable.append();
        DataTable dataTable = get(childTableKey);
        int newRowIndex = appendDetail(context, childTableKey);
        dataTable.setLong(newRowIndex, SystemField.POID_SYS_KEY, parentOid);
        dataTable.setParentBookmark(newRowIndex, parentBookMark);
        // 为子明细Sequence赋值后，重置DataTable的Filter
        String oldFilter = dataTable.getFilter();
        if (dataTable.getMetaData().findColumnIndexByKey(SystemField.SEQUENCE_SYS_KEY) > 0) {
            ArrayList<Integer> bkmarkArray = dataTable.filter(SystemField.POID_SYS_KEY + "==" + parentOid);
            dataTable.setInt(newRowIndex, SystemField.SEQUENCE_SYS_KEY, bkmarkArray.size());
        }
        dataTable.clearFilter();
        dataTable.filter(oldFilter);
        int bookMark = dataTable.getBookmark(newRowIndex);
        setCurrentBookMark(childTableKey, bookMark);
        pCalRowDefaultRow(context, dataTable.getBookmark(newRowIndex), childTableKey, dataTable, newRowIndex);
        setCurrentBookMark(childTableKey, bookMark);
        return newRowIndex;
    }

    /**
     * 删除明细
     * @param tableKey
     * @param bookMark
     * @throws Throwable
     */
    public void deleteDetail(String tableKey, int bookMark) throws Throwable {
        addDirtyTableFlag(tableKey);
        ignoreFormulaItems4DeleteDetail(tableKey, bookMark);
        DataTable dataTable = get_impl(tableKey);
        int length = dataTable.size();
        int count = 0;
        for (int rowIndex = 0; rowIndex < length; rowIndex++) {
            if (dataTable.getBookmark(rowIndex) == bookMark) {
                pDeleteDetail(dataTable, rowIndex);
                count++;
                break;
            }
        }
        if (count > 0) {//若count>0，则说明删除的是有值的明细行，调用的pDeleteDetail方法有排序，无须再次排序，直接返回即可
            return;
        }
        //若删除的是空行，则需要重新对数据排序
        if (dataTable.getMetaData().constains(SystemField.SEQUENCE_SYS_KEY)) {
            for (int rowIndex = 0; rowIndex < dataTable.size(); rowIndex++) {
                dataTable.setInt(rowIndex, SystemField.SEQUENCE_SYS_KEY, rowIndex + 1);
            }
        }
    }

    /**
     * 在删除表数据时候，对表数据行中的字段不需要触发默认值计算
     *
     * @param tableKey
     * @throws Throwable
     */
    private void ignoreFormulaItems4DeleteDetail(String tableKey, Integer bkmk) throws Throwable {
        if (bkmk < 0) {
            return;
        }
        Stack<FormulaItem> calcingFormulaItems = this.calcingFormulaItems;
        int[] valueChangedFireSequence = null;
        for (int i = calcingFormulaItems.size() - 1; i >= 0; i--) {
            FormulaItem formulaItem = calcingFormulaItems.get(i);
            if (formulaItem.isValueChanged()) {
                valueChangedFireSequence = formulaItem.getSequence();
                break;
            }
        }
		List<FormulaItem> formulaItems = effectScopeMap.beforeGetDataTableCollectFormulaItems(tableKey, valueChangedFireSequence);
        for (FormulaItem formulaItem : formulaItems) {
            TableKeyAndBookmark location = formulaItem.getTableBookmark();
            if (tableKey.equals(formulaItem.getTargetTableKey()) && location.getBookMark() == bkmk) {
                formulaItem.setEnable(false);
            }
        }
    }

    /**
     * 删除明细
     * @param tableKey
     * @param oid
     * @throws Throwable
     */
    public void deleteDetail(String tableKey, Long oid) throws Throwable {
        int bookMark = getBookMarkByOID4Table(tableKey, oid);
        deleteDetail(tableKey, bookMark);
    }

    /**
     * @param dataTable
     * @param rowIndex
     * @throws Throwable
     */
    private void pDeleteDetail(DataTable dataTable, int rowIndex) throws Throwable {
        Long OID = dataTable.getLong(rowIndex, SystemField.OID_SYS_KEY);
        Long POID = dataTable.getLong(rowIndex, SystemField.POID_SYS_KEY);

        // 根据dataObject中的表构建关系， 不从IDLookUp中获取关系，因为在IDLookUp中构建子明细中明细的关系会出现问题，参考采购订单中的“条件技术价格计算过程”，这个单据存在5层父子关系
        MetaTableCollection tableCollection = metaForm.getDataSource().getDataObject().getTableCollection();
        // Key:parentTableKey  Value:tableKeys  构建的关系
        HashMap<String, List<String>> relationTable = new HashMap<>();
        for (MetaTable metaTable : tableCollection) {
            String tableKey = metaTable.getKey();
            String parentTableKey = metaTable.getParentKey();
            if (StringUtil.isBlankOrNull(parentTableKey)) {
                continue;
            }
            List<String> childTables = relationTable.getOrDefault(parentTableKey, new ArrayList<>());
            childTables.add(tableKey);
            relationTable.put(parentTableKey, childTables);
        }

        String tableKey = dataTable.getKey();
        DataTable dataTable_T = getDataTable(tableKey + MetaTable._T);
        if (dataTable_T != null) {
            //删除明细行时也要同步删除T表数据
			List<Integer> deleteIndex = dataTable_T.filter(SystemField.Lang_SYS_KEY + "=='" + getContext().getEnv().getLocale() + "'&&" +
					SystemField.SrcLangOID_SYS_KEY + "==" + OID);
            if (CollectionUtils.isNotEmpty(deleteIndex)) {
                for (Integer index : deleteIndex) {
                    dataTable_T.delete(index);
                }
            }
        }
        List<String> childTableKeys = relationTable.getOrDefault(tableKey, new ArrayList<>());
        if (childTableKeys.size() > 0) {
            deleteChildDataTable(childTableKeys, OID, relationTable);
        }

        // 如果是树节点，删除子节点行
        // Key:父行OID;Value:子行OID数组。不用rowIndex的原因，父行在删除的时候，子行已经删除了，每次根据OID再到dataTable中取rowIndex，取不到说明已经删除了。
        boolean hasRowTree = false;// 不能仅仅靠metaTable是否与树相关的列来判断是否为树形表格，比如MM_PurchaseVoucherFlow，并不是树形表格。
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
		String gridKey = idLookup.getGridKeyByTableKey(tableKey);
		if (ERPStringUtil.isNotBlankOrNull(gridKey)) {// 存在没有表格绑定的数据表，如MM_ContractServiceConfirmation下的EMM_EntrySheetsDtl
			MetaGrid metaGrid = idLookup.getMetaGridByGridKey(gridKey);
			Iterator<MetaGridRow> gridRowIt = metaGrid.getRowCollection().iterator();
			while (gridRowIt.hasNext()) {
				MetaGridRow gridRow = gridRowIt.next();
				if (gridRow.getTableKey() != null && gridRow.getTableKey().equalsIgnoreCase(tableKey)
						&& gridRow.getRowTree() != null
						&& ERPStringUtil.isNotBlankOrNull(gridRow.getRowTree().getCellKey())) {
					hasRowTree = true;
					break;
				}
			}
		}
		DataTableMetaData metaData = dataTable.getMetaData();
		if (hasRowTree && metaData.constains(DataConstant.STR_FLD_TREEROWLEVEL)
				&& metaData.constains(DataConstant.STR_FLD_TREE_ROWINDEX)
				&& metaData.constains(DataConstant.STR_FLD_PARENT_TREE_ROWINDEX)) {
            HashMap<Long, List<Long>> treeRowMap = new HashMap<>();
            MetaTable metaTable = metaForm.getDataSource().getDataObject().getMetaTable(tableKey);
            MetaColumn metaColumn = metaTable.get(DataConstant.STR_FLD_TREEROWLEVEL);
            for (int i = 0; i < dataTable.size(); i++) {
                int level = dataTable.getInt(i, metaColumn.getBindingDBColumnName());
                if (level == DataConstant.SpecialTreeLevel) {
                    continue;
                }
                List<Long> childRows = treeRowMap.getOrDefault(i, new ArrayList<Long>());
                for (int j = i + 1; j < dataTable.size(); j++) {
                    int tmpLevel = dataTable.getInt(j, metaColumn.getBindingDBColumnName());
                    if (tmpLevel == DataConstant.SpecialTreeLevel) {
                        // 这种情况代表中途出现一个独立的行，比如1，special, 2, 2...一般不会出现
                        continue;
                    }
                    if (tmpLevel > level) {
                        childRows.add(dataTable.getLong(j, SystemField.OID_SYS_KEY));
                    } else {
                        break;
                    }
                }
                // 反转一下，使得子行删除的时候也能够从后往前删
                Collections.reverse(childRows);
                treeRowMap.put(dataTable.getLong(i, SystemField.OID_SYS_KEY), childRows);
            }
			List<Long> childRows = treeRowMap.getOrDefault(dataTable.getLong(rowIndex, SystemField.OID_SYS_KEY), new ArrayList<Long>());
            if (childRows.size() > 0) {
                deleteChildRows(dataTable, childRows, treeRowMap, relationTable);
            }
        }

        // 重新设置bkmk，删除的行是最后一行时，删除后指向上一行。否则删除后指向rowIndex对应的bkmk
        boolean isLastRow = rowIndex == (dataTable.size() - 1);
        dataTable.delete(rowIndex);

        int bookmark = -1;
        if (dataTable.size() > 0) {
            if (isLastRow) {
                bookmark = dataTable.getBookmark(rowIndex - 1);
            } else {
                bookmark = dataTable.getBookmark(rowIndex);
            }
        }
        this.setCurrentBookMark(tableKey, bookmark);

        oidBookMarks.remove(OID);
        resetSeqAfterDeleteDtl(dataTable, POID);
    }

    /**
	 * 树形表格的行删除
	 * 场景
	 * level   rowIndex
	 1	 	0
	 1    	1
	 2    	>2
	 3    	>>3
	 2    	>4
	 3    	>>5
	 4    	>>>6
	 3    	>>7
	 2    	>8
     * @param dataTable
     * @param childRows
     * @param treeRowMap
     * @param relationTable
     * @throws Throwable
     */
	private void deleteChildRows(DataTable dataTable, List<Long> childRows, HashMap<Long, List<Long>> treeRowMap, HashMap<String,List<String>> relationTable) throws Throwable {
        for (Long childRowOID : childRows) {
            // 先根据OID查找相应的rowIndex,可能不存在
            int rowIndex = -1;
            for (int i = 0; i < dataTable.size(); i++) {
                if (dataTable.getLong(i, SystemField.OID_SYS_KEY).equals(childRowOID)) {
                    rowIndex = i;
                    break;
                }
            }
            if (rowIndex < 0) {
                // 上面的场景中，当删除第5行的时候，发现第6行已经删掉了
                continue;
            }
            List<Long> tmpChildRows = treeRowMap.getOrDefault(childRowOID, new ArrayList<Long>());
            if (tmpChildRows.size() > 0) {
                deleteChildRows(dataTable, tmpChildRows, treeRowMap, relationTable);
            }
            String tableKey = dataTable.getKey();
            List<String> childTableKeys = relationTable.getOrDefault(tableKey, new ArrayList<>());
            if (childTableKeys.size() > 0) {
                deleteChildDataTable(childTableKeys, childRowOID, relationTable);
            }
            dataTable.delete(rowIndex);
            oidBookMarks.remove(childRowOID);
        }
    }

	private void deleteChildDataTable(List<String> childTableKeys, Long OID, HashMap<String, List<String>> relationTable) throws Throwable {
        for (String tableKey : childTableKeys) {
            DataTable childDataTable = this.get_impl(tableKey);
            String oldFilter = childDataTable.getFilter();
            childDataTable.setFilter(SystemField.POID_SYS_KEY + "==" + OID);
            childDataTable.filter();
            if (childDataTable.size() > 0) {
                DataTable dataTable_T = getDataTable(tableKey + MetaTable._T);
                List<String> tableKeys = relationTable.get(tableKey);
                for (int childRowIndex = childDataTable.size() - 1; childRowIndex >= 0; childRowIndex--) {
                    Long tmpOID = childDataTable.getLong(childRowIndex, SystemField.OID_SYS_KEY);
                    //继续删除子明细
                    if (tableKeys != null && tableKeys.size() > 0) {
                        deleteChildDataTable(tableKeys, tmpOID, relationTable);
                    }
                    //删除T表数据
                    if (dataTable_T != null) {
                        //删除明细行时也要同步删除T表数据
						List<Integer> deleteIndex = dataTable_T.filter(SystemField.Lang_SYS_KEY + "=='" + getContext().getEnv().getLocale() + "'&&" +
								SystemField.SrcLangOID_SYS_KEY + "==" + tmpOID);
                        if (CollectionUtils.isNotEmpty(deleteIndex)) {
                            for (Integer index : deleteIndex) {
                                dataTable_T.delete(index);
                            }
                        }
                    }
                    //删除自身
                    childDataTable.delete(childRowIndex);
                }
                this.addDirtyTableFlag(tableKey);
            }
            childDataTable.setFilter(oldFilter);
            childDataTable.filter();
        }
    }

    private void resetSeqAfterDeleteDtl(DataTable dataTable, Long POID) throws Throwable {
        if (!dataTable.getMetaData().constains(SystemField.SEQUENCE_SYS_KEY)) {
            //没有SEQUENCE字段则返回
            return;
        }
        //判断自己是否是儿子
        boolean isChildDataTable = (POID != null && POID != 0L);
        if (isChildDataTable) {
            //处理序号
            String oldFilter = dataTable.getFilter();
            ArrayList<Integer> bkmarkArray = dataTable.filter(SystemField.POID_SYS_KEY + "==" + POID);
            int seq = 1;
            int length = bkmarkArray.size();
            for (int index = 0; index < length; index++) {
                // 如果删除的表是子表，需要根据该行POID过滤，然后更新序号
                if (bkmarkArray.contains(dataTable.getBookmark(index))) {
                    dataTable.setInt(index, SystemField.SEQUENCE_SYS_KEY, seq++);
                }
            }
            dataTable.clearFilter();
            dataTable.filter(oldFilter);
        } else {
            for (int rowIndex = 0; rowIndex < dataTable.size(); rowIndex++) {
                dataTable.setInt(rowIndex, SystemField.SEQUENCE_SYS_KEY, rowIndex + 1);
            }
        }
    }

    /**
	 * 设置扩展数据，即基于Document的变量
	 * 数据类型只支持字符串，为什么只支持字符串？通过强制类型，减少开发过程中的错误
     * @param key
     * @param value
     */
    public void setExpandValue(String key, String value) {
        this.putExpandData(key, value);
    }

    /**
	 * 取扩展数据，即基于Document的变量
	 * 数据类型只支持字符串，为什么只支持字符串？通过强制类型，减少开发过程中的错误
     * @param key
     * @return
     */
    public String getExpandValue(String key) {
        return (String)this.getExpandData(key);
    }

    /**
     * 取一个表数据的所有OID
     * @param tableKey
     * @return
     */
    public Long[] getOIDs(String tableKey) {
        DataTable dataTable = get(tableKey);
        int length = dataTable.size();
        Long[] result = new Long[length];
        for (int rowIndex = 0; rowIndex < length; rowIndex++) {
            result[rowIndex] = dataTable.getLong(rowIndex, SystemField.OID_SYS_KEY);
        }
        return result;
    }

    /**
     * 获取表单指定数据源的数据
     *
     * @param tableKey
     * @return
     * @throws Throwable
     */
    public DataTable getDataTable(String tableKey) throws Throwable {
        return get(tableKey);
    }

    /**
     * 设置表单指定数据源的数据
     *
     * @param tablekey
     * @param dataTable
     */
    public void setDataTable(String tablekey, DataTable dataTable) {
        if (StringUtil.isBlankOrNull(tablekey)) {
            return;
        }
        //checkDataTableMetaData(tablekey, dataTable);
        this.addDirtyTableFlag(tablekey);
        dataTable.setKey(tablekey);
        put(tablekey, dataTable);
    }

    /**
     * 根据表名清除延迟表达式
	 * 注意：原来有个getValue(String,int)方法不进行计算默认值，调整过结构之后，所有的getValue都会计算默认值。只有com.bokesoft.erp.function.DocumentFunctionUtil#setDataTableByFilter(RichDocumentContext, RichDocument, SqlString)
	 * 这个方法比较特殊不需要计算默认值，仅为这个方法清除默认值公式。其他方法请勿调用，谢谢
     * @param tableKey
     * @throws Throwable
     */
    public void removeDelayDefaultFormulaValueByKey(String tableKey) throws Throwable {
        List<FormulaItem> formulaItems = this.effectScopeMap.beforeGetDataTableCollectFormulaItems(tableKey, null);
        for (FormulaItem tmp : formulaItems) {
            tmp.setEnable(false);
        }
    }

    //	private void checkDataTableMetaData(String tablekey, DataTable dataTable) {
    //		MetaDataObject dataObject = getMetaDataObject();
    //		if (StringUtil.isBlankOrNull(tablekey) || dataObject == null) {
    //			return;
    //		}
    //
    //		MetaTable metaTable = dataObject.getMetaTable(tablekey);
    //		if (metaTable == null) {
    //			return;
    //		}
    //		DataTableMetaData metaData = dataTable.getMetaData();
    //		for (MetaColumn metaColumn : metaTable) {
    //			String dbColumnName = metaColumn.getBindingDBColumnName();
    //			if (StringUtil.isBlankOrNull(dbColumnName) || !metaData.constains(dbColumnName)) {
    //				continue;
    //			}
    //			ColumnInfo columnInfo = metaData.getColumnInfo(dbColumnName);
    //			int metaTableDataType = metaColumn.getDataType();
    //			int dataTableDataType = columnInfo.getDataType();
    //
    //			if (metaTableDataType != dataTableDataType) {
    //				throw new RuntimeException("Datatable中字段" + dbColumnName + "的数据类型与期望的数据类型不一致,请检查!");
    //			}
    //		}
    //
    //		for (int index = 0, columnCount = metaData.getColumnCount(); index < columnCount; index++) {
    //			ColumnInfo columnInfo = metaData.getColumnInfo(index);
    //			String columnKey = columnInfo.getColumnKey();
    //			MetaColumn metaColumn = findMetaColumn(metaTable, columnKey);
    //			if (metaColumn == null) {
    //				continue;
    //			}
    //
    //		}
    //	}

    //	private MetaColumn findMetaColumn(MetaTable metaTable, String columnKey) {
    //		if (metaTable.containsKey(columnKey)) {
    //			return metaTable.get(columnKey);
    //		} else {
    //			// 数据库字段和DataObject中的Key不一致的情况
    //			for (MetaColumn column : metaTable) {
    //				String dbColumnName = column.getDBColumnName();
    //				if (StringUtil.isBlankOrNull(dbColumnName)) {
    //					continue;
    //				}
    //				if (dbColumnName.equalsIgnoreCase(columnKey)) {
    //					return column;
    //				}
    //			}
    //			return null;
    //		}
    //	}

    /** 拷贝自com.bokesoft.yes.view.process.convertor.convert() */
    public static Object convert(int dataType, Object value) throws Throwable {
        return convert(dataType, value, null);
    }
    /** 拷贝自com.bokesoft.yes.view.process.convertor.convert() */
    public static Object convert(int dataType, Object value, Integer scale) throws Throwable {
        Object result = null;

        switch (dataType) {
            case DataType.NUMERIC:
                result = TypeConvertor.toBigDecimal(value, scale);
                break;
            case DataType.DATE:
                result = TypeConvertor.toDate(value);
                break;
            // 普通字典是Long类型,多选字典是String
            case DataType.LONG:
                if (value == null) {
                    result = Long.valueOf(0);
                } else if (value instanceof Date) {
                    Date date = TypeConvertor.toDate(value);
                    String dateFormat = "yyyy-MM-dd HH:mm:ss";
                    String formatDate = ERPDateUtil.format(date, dateFormat);
                    String dateString = StringUtils.replace(formatDate, "-", "");
                    dateString = StringUtils.replace(dateString, ":", "");
                    dateString = StringUtils.replace(dateString, " ", "");
                    result = TypeConvertor.toLong(dateString);
                } else if (value instanceof String) {
                    String tmp = (String)value;
                    tmp = StringUtils.replace(tmp, "-", "");
                    tmp = StringUtils.replace(tmp, ":", "");
                    if (StringUtil.isNumeric(tmp)) {
                        result = TypeConvertor.toLong(tmp);
                    }
                } else if (StringUtil.isNumeric(value)) {
                    result = TypeConvertor.toLong(TypeConvertor.toBigDecimal(value, 0));
                }
                break;
            case DataType.DATETIME:
                result = TypeConvertor.toDataType(dataType, value);
                break;
            case DataType.STRING:
                if (value instanceof SqlString) {
                    value = SqlStringUtil.toJsonString(value);
                }
                result = TypeConvertor.toString(value);
                break;
			case DataType.INT:
                if (StringUtil.isNumeric(value)) {
                    result = TypeConvertor.toInteger(scale == null ? value : TypeConvertor.toBigDecimal(value, scale));
                } else {
                    result = TypeConvertor.toInteger(value);
                }
                break;
            case DataType.FIXED_STRING:
                result = TypeConvertor.toString(value);
                break;
            default:
                if (value instanceof SqlString) {
                    value = SqlStringUtil.toJsonString(value);
                }
                //			throw new Exception("没有实现");
                result = value;
                break;
        }
        return result;
    }

	/** 拷贝修改之com.bokesoft.yes.common.util.CompareUtil
	 * @throws Throwable */
    public static boolean isEquals(Object o1, Object o2, int dataType) throws Throwable {
        boolean result = false;
        if (o1 == o2) {
            result = true;
        } else if (o1 == null) {
            result = false;
        } else if (o2 == null) {
            result = false;
        } else {
            o1 = convert(dataType, o1);
            o2 = convert(dataType, o2);
            if (o1 != null && o2 != null) {
                switch (dataType) {
                    case DataType.NUMERIC:
                        result = ((BigDecimal)o1).compareTo((BigDecimal)o2) == 0;
                        break;
                    case DataType.DATE:
                    case DataType.DATETIME:
                        result = ((Date)o1).compareTo((Date)o2) == 0;
                        break;
                    // 普通字典是Long类型,多选字典是String
                    case DataType.LONG:
                        result = ((Long)o1).compareTo((Long)o2) == 0;
                        break;
                    default:
                        result = o1.equals(o2);
                        break;
                }
            }
        }
        return result;
    }

    public int getRowStatus(String tableKey, int bookMark) throws Throwable {
        if (bookMark == GridRow.GridEmptyBookMark) {
            throw new RuntimeException("空白行无法取行状态。");
        }
        DataTable dataTable = get(tableKey);
        int rowIndex = DataTableExUtil.getRowIndexByBookmark(dataTable, bookMark);
		@SuppressWarnings("deprecation")
		Row row = dataTable.getRowByIndex(rowIndex);
        return row.getState();
    }



    /**
     * 点击事件
     * @param context
     * @param operationKey
     * @throws Throwable
     */
    public void fireOperationAction(RichDocumentContext context, String operationKey) throws Throwable {
        KeyPairCompositeObject item = metaForm.getOperationCollection().get(operationKey);
        if (item.getObjectType() != MetaOperation.Operation) {
            return;
        }
        MetaOperation metaOperation = (MetaOperation)item;
        String contend = metaOperation.getAction().getContent();

        if (contend != null && contend.length() > 0) {
            EvalScope evalScope = new EvalScope(null);
            context.getMidParser().eval(ScriptType.Formula, contend, null, null, evalScope);
        }
    }

    public Object getComboBoxItems(RichDocumentContext context, String fieldKey) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String comboBoxItems = idLookup.getComboBoxFormulaItemsByFieldKey(fieldKey);
        if (comboBoxItems != null && comboBoxItems.length() > 0) {
            EvalScope evalScope = new EvalScope(null);
            return context.getMidParser().eval(ScriptType.Formula, comboBoxItems, null, null, evalScope);
        }
        return null;
    }

    @Override
    public JSONObject toJSON() throws Throwable {
        return TraceSystemManager.withTrace(()-> {
            this.setIgnoreArithmeticException();
            this.calcDelayFormula();
            setDictionaryCaption(context, metaForm);
            preCalcComboBoxNoCacheFormulaValue(context);

            JSONObject docJson = super.toJSON();
            appendExpandVirtualGridJSON(docJson);
            appendGridVariantJSON(docJson);

            //		JSONHelper.writeToJSON(docJson, DOCUMENT_DOCUMENTID, documentID, "");
            JSONHelper.writeToJSON(docJson, RICHDOCUMENT_FORM_OPERATIONSTATE, form_OperationState, Document.NORMAL);
            JSONObject jsonHeadDatas = new JSONObject();
            for (Entry<String, Object> e : headValues.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                value = toJSONValue(key, value);
                jsonHeadDatas.put(key, value);
            }
            docJson.put("headValues", jsonHeadDatas);

            JSONObject jsonOtherFieldDatas = new JSONObject();
            for (Entry<String, Object> e : otherFieldValues.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                value = toJSONValue(key, value);
                jsonOtherFieldDatas.put(key, value);
            }
            docJson.put("otherFieldValues", jsonOtherFieldDatas);

            JSONObject jsonEmptyGridRowValues = new JSONObject();
            for (Entry<String, Map<String, Object>> e : emptyGridRowValues.entrySet()) {
                String gridKey = e.getKey();
                JSONObject jsonEmptyRow = new JSONObject();
                Map<String, Object> emptyRow = e.getValue();
                for (Entry<String, Object> e1 : emptyRow.entrySet()) {
                    String fieldKey = e1.getKey();
                    Object value = e1.getValue();
                    value = toJSONValue(fieldKey, value);
                    jsonEmptyRow.put(fieldKey, value);
                }
                jsonEmptyGridRowValues.put(gridKey, jsonEmptyRow);
            }
            docJson.put("emptyGridRowValues", jsonEmptyGridRowValues);

            if (_tableFilterMap != null) {
                JSONObject jsonTableFilter = new JSONObject();
                for (Entry<String, SqlString> e : _tableFilterMap.entrySet()) {
                    String key = e.getKey();
                    SqlString value = e.getValue();
                    jsonTableFilter.put(key, SqlStringUtil.SqlStringToString(value));
                }
                docJson.put("tableFilter", jsonTableFilter);
            }

            return docJson;
        },this, PerformanceAttributeData.build(ActionType.Document.TO_JSON.getCode(),"RichDocument#toJSON"));
    }

    private final static String DICT_CAPTIONS = "DictCaptions";

    private final static String DICT_CODES = "DictCodes";

    private final static String DICT_DATA_ELEMENTS = "DictDataElements";
    
    /**
     * 字典没有启用的情况下将被删除
     * @param context
     * @param metaForm
     * @throws Throwable
     */
    public void removeDictValue(DefaultContext context, MetaForm metaForm) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        StringHashMap<String> dicFiledKey2ItemKey = idLookup.getAllDicFieldKey2ItemKey();
        HashSet<Long> loadOids = new HashSet<Long>(); // 所有已经加载的字典OID，这里利用了大猫中所有字典OID都不重复，省掉多余的ItemItem_OID的字符串拼装
        for (Map.Entry<String, String> entry : dicFiledKey2ItemKey.entrySet()) {
            String dicFieldKey = entry.getKey();
            String itemKey = entry.getValue();
            if (StringUtil.isBlankOrNull(itemKey)) {
                continue;
            }
            String tableKey = idLookup.getTableKeyByFieldKey(dicFieldKey);
            if (StringUtil.isBlankOrNull(tableKey)) {
                continue;
            }
            DataTable dt = this.get_impl(tableKey);
            if (dt == null || dt.size() == 0) {
                continue;
            }
            String colunmKey = idLookup.getColumnKeyByFieldKey(dicFieldKey);
            if (StringUtil.isBlankOrNull(colunmKey)) {
                continue;
            }
            int columnIndex = dt.getMetaData().findColumnIndexByKey(colunmKey);
            if (columnIndex < 0) {
                continue;
            }
            if (DictFilterSqlUtil.isEditValue(metaForm, idLookup, dicFieldKey)) {
                continue;
            }
            for (int i = 0; i < dt.size(); i++) {
                Object obj = dt.getObject(i, columnIndex);
                if (obj == null) {
                    continue;
                }
                if (obj instanceof String && (((String)obj).indexOf(",") > 0 || ((String)obj).indexOf("_") > 0)) {
                    // 多选字典的情况
                    long id = -1;
                    String[] array = ((String)obj).split(",");
                    for (int index = 0, size = array.length; index < size; index++) {
                        String dictOID = array[index].replaceAll("\\[|\\]", "");
                        if (dictOID.indexOf("_") >= 0) {
                            dictOID = dictOID.substring(dictOID.indexOf("_") + 1);
                        }
                        id = TypeConvertor.toLong(dictOID);
                        if (!loadOids.contains(id)) {
                            String caption = getDicStatusCaption(context, itemKey, id);
                            if ("".equals(caption)) {
                                dt.setObject(i, columnIndex, "");
                                loadOids.add(id);
                            }

                        } else {
                            dt.setObject(i, columnIndex, "");
                        }
                    }
                } else {
                    Long dicOID = TypeConvertor.toLong(obj);
                    if (!dicOID.equals(0L) && !loadOids.contains(dicOID)) {
                        String caption = getDicStatusCaption(context, itemKey, dicOID);
                        if ("".equals(caption)) {
                            dt.setObject(i, columnIndex, 0L);
                            loadOids.add(dicOID);
                        }

                    } else if (!dicOID.equals(0L)) {
                        dt.setObject(i, columnIndex, 0L);
                    }
                }
            }
        }
        loadOids.clear();
    }
    public void setDictionaryCaption(DefaultContext context, MetaForm metaForm) throws Throwable {
        Object captions = this.getExpandData(DICT_CAPTIONS);
        if (captions == null) {
            captions = new HashMap<String, String>();
            this.putExpandData(DICT_CAPTIONS, captions);
        }
        
        Object codes = this.getExpandData(DICT_CODES);
        if (codes == null) {
        	codes = new HashMap<String, String>();
            this.putExpandData(DICT_CODES, codes);
        }

        Object dataElements = this.getExpandData(DICT_DATA_ELEMENTS);
        if (dataElements == null) {
            dataElements = new HashMap<String, String>();
            this.putExpandData(DICT_DATA_ELEMENTS, dataElements);
        }


		@SuppressWarnings("unchecked")
		HashMap<String, String> tmpCaptions = (HashMap<String, String>) captions;
		@SuppressWarnings("unchecked")
		HashMap<String, String> tmpCodes = (HashMap<String, String>) codes;
        @SuppressWarnings("unchecked")
        HashMap<String, String> tmpDataElements = (HashMap<String, String>) dataElements;
		
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        HashMap<String, AbstractMetaObject> allUIComponents = metaForm.getAllUIComponents();
        StringHashMap<String> dicFiledKey2ItemKey = idLookup.getAllDicFieldKey2ItemKey();
        HashSet<Long> loadOids = new HashSet<Long>(); // 所有已经加载的字典OID，这里利用了大猫中所有字典OID都不重复，省掉多余的ItemItem_OID的字符串拼装
        // oid和字段Key对应关系
        HashMap<Long, String> oid2DicFieldKey = new HashMap<>();
        Map<String, String> codeFieldKey = new HashMap<>();
        
        // itemKey和对应的字典oid关系，批量查item
        HashMap<String, List<Long>> itemKey2DictOIDs = new HashMap<>();
        Map<String, List<String>> textFields = new HashMap<>();

        IMetaFactory metaFactory=context.getVE().getMetaFactory();
        for (Map.Entry<String, String> entry : dicFiledKey2ItemKey.entrySet()) {
            String dicFieldKey = entry.getKey();
            String tableKey = idLookup.getTableKeyByFieldKey(dicFieldKey);
            if (StringUtil.isBlankOrNull(tableKey)) {
                continue;
            }

            DataTable dt = this.get_impl(tableKey);
            if (dt == null) {
                continue;
            }

            // 处理动态字典的数据元素缓存
            if (idLookup.isDynamicDict(dicFieldKey)) {
                String refDataElementKey = idLookup.getDataElementKeyByFieldKey(dicFieldKey);
                String dataElementColumnKey = idLookup.getColumnKeyByFieldKey(refDataElementKey);
                int dataElementIndex = dt.getMetaData().findColumnIndexByKey(dataElementColumnKey);
                if (!StringUtil.isBlankOrNull(refDataElementKey) && dataElementIndex >= 0) {
                    for (int i = 0; i < dt.size(); i++) {
                        String dataElementKey = dt.getString(i, dataElementIndex);
                        if (!StringUtil.isBlankOrNull(dataElementKey)) {
                            String key = MetaUtil.getItemKeyByDataElementKey(metaFactory,
                                    dataElementKey);
                            tmpDataElements.put(dataElementKey, key);
                        }
                    }
                }
            }

            String itemKey = entry.getValue();
            if (StringUtil.isBlankOrNull(itemKey)) {
                continue;
            }

            if(!codeFieldKey.containsKey(itemKey)) {
	            MetaDataObject dictDataObject = context.getVE().getMetaFactory().getDataObject(itemKey);
	            if((";" + dictDataObject.getDisplayColumnsStr() + ";").indexOf(";"+ERPSystemField.USECODE_SYS_KEY+";") > -1) {
	            	codeFieldKey.put(itemKey, ERPSystemField.USECODE_SYS_KEY);
	            }else {
	            	codeFieldKey.put(itemKey, SystemField.CODE_DICT_KEY);
	            }
            }
            	
            List<Long> dictOIDs = itemKey2DictOIDs.get(itemKey);
            if (dictOIDs == null) {
                dictOIDs = new ArrayList<Long>();
                itemKey2DictOIDs.put(itemKey, dictOIDs);
            }

            String colunmKey = idLookup.getColumnKeyByFieldKey(dicFieldKey);
            if (StringUtil.isBlankOrNull(colunmKey)) {
                continue;
            }
            int columnIndex = dt.getMetaData().findColumnIndexByKey(colunmKey);
            if (columnIndex < 0) {
                continue;
            }
            if (DictFilterSqlUtil.isEditValue(metaForm, idLookup, dicFieldKey)) {
                continue;
            }

            // 处理DataTable数据
            for (int i = 0; i < dt.size(); i++) {
                Object obj = dt.getObject(i, columnIndex);
                if (obj == null) {
                    continue;
                }
                this.dealDictValue(obj,dicFieldKey,loadOids,dictOIDs,oid2DicFieldKey);
            }

            // 处理表格空白行
            String gridKey = idLookup.getGridKeyByFieldKey(dicFieldKey);
            if( gridKey!= null && !gridKey.isEmpty() ) {
                Map<String, Object> rowValues = emptyGridRowValues.get(gridKey);
                Object obj = rowValues == null ? null : rowValues.get(dicFieldKey);
                this.dealDictValue(obj,dicFieldKey,loadOids,dictOIDs,oid2DicFieldKey);
            }

            if (!textFields.containsKey(dicFieldKey)) {
                try {
                    AbstractMetaObject metaComponent = allUIComponents.get(dicFieldKey);
                    if (metaComponent != null) {
                        if (metaComponent instanceof IPropertiesElement) {
                            AbstractMetaObject metaObject = ((IPropertiesElement)metaComponent).getProperties();
                            if (metaObject instanceof MetaDictProperties) {
                                MetaDictProperties properties = (MetaDictProperties)metaObject;
                                String textField = properties.getTextField();
                                if (textField != null && !textField.isEmpty()) {
                                    List<String> fields = textFields.get(itemKey);
                                    if (fields == null) {
                                        fields = new ArrayList<>();
                                        fields.add(textField);
                                        textFields.put(itemKey, fields);
                                    } else if (!fields.contains(textField)) {
                                        fields.add(textField);
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    // 此处报错不用
                    LogSvr.getInstance().error(dicFieldKey + " is not a dict", e);
                }
            }
        }

        for (Map.Entry<String, List<Long>> entry : itemKey2DictOIDs.entrySet()) {
            String itemKey = entry.getKey();
            List<Long> dictOIDs = entry.getValue();
            if (dictOIDs != null && dictOIDs.size() > 0) {
                List<Item> items = ((IDictCacheProxy)context.getDictCache()).getItems(itemKey, dictOIDs);
                String codeField = codeFieldKey.get(itemKey);
                if(codeField == null || codeField.isEmpty()) {
                	codeField = SystemField.CODE_DICT_KEY;
                }
                
                if (items != null && items.size() > 0) {
                    for (Item item : items) {
                        String dictFieldKey = oid2DicFieldKey.get(item.getID());
                        String caption = "";
                        String key = new StringBuilder(itemKey).append("_").append(item.getID()).toString();
                        String code = "";
                     
                        if (hasRights(context, itemKey, item, dictFieldKey)) {
                            caption = item.getCaption();
                            code = TypeConvertor.toString(item.getValue(codeField));
                            boolean isArchivedData = false;
                            String archivedCaption = "";
							// caption为空（仅缓存了OID）且是归档数据时
							if((StringUtil.isBlankOrStrNull(caption) || caption.contentEquals(" ")) && ArchiveDBUtil.isArchivedData(context, itemKey, item.getID())) {
                                caption = getArchivedCaption();
                                archivedCaption = caption;
                                isArchivedData = true;
                            }

                            tmpCaptions.put(key, caption);
                            tmpCodes.put(key, code);
                            
                            List<String> fields = textFields.get(itemKey);
                            if (fields != null) {
                                for (String textField : fields) {
									key = new StringBuilder(itemKey).append("_").append(item.getID()).append("_").append(textField).toString();
                                    if (isArchivedData) {
                                        tmpCaptions.put(key, archivedCaption);
                                    } else {
                                        tmpCaptions.put(key, TypeConvertor.toString(item.getValue(textField)));
                                    }
                                }
                            }
                        } else {
                            tmpCaptions.put(key, caption);
                            tmpCodes.put(key, code);
                        }
                    }
                }
            }
        }
        loadOids.clear();
    }

    private void dealDictValue(Object obj,String dicFieldKey,HashSet<Long> loadOids,List<Long> dictOIDs,HashMap<Long, String> oid2DicFieldKey) {
        if( obj != null ) {
            if (obj instanceof String && (((String)obj).indexOf(",") > 0 || ((String)obj).indexOf("_") > 0)) {
                // 多选字典的情况
                long id = -1;
                String[] array = ((String)obj).split(",");
                for (String s : array) {
                    String dictOID = s.replaceAll("\\[|\\]", "");
                    if (dictOID.contains("_")) {
                        dictOID = dictOID.substring(dictOID.indexOf("_") + 1);
                    }
                    id = TypeConvertor.toLong(dictOID);
                    if (!loadOids.contains(id)) {
                        dictOIDs.add(id);
                        oid2DicFieldKey.put(id, dicFieldKey);
                        loadOids.add(id);
                    }
                }
            } else {
                Long dicOID = TypeConvertor.toLong(obj);
                if (!dicOID.equals(0L) && !loadOids.contains(dicOID)) {
                    dictOIDs.add(dicOID);
                    oid2DicFieldKey.put(dicOID, dicFieldKey);
                    loadOids.add(dicOID);
                }
            }
        }
    }

    private String archivedCaption = null;

    private String getArchivedCaption() throws Throwable {
        if (archivedCaption == null) {
            archivedCaption = MessageFacade.getMsgContent("ARCHIVENORMAL012", "");
        }
        return archivedCaption;
    }

    public void getExpandDataDirtyJSON(DefaultContext context, MetaForm metaForm, JSONObject result) throws Throwable {
        // 扩展数据
        JSONObject jsonExpandData = new JSONObject();
        result.put(DocumentJSONConstants.DOCUMENT_EXPAND_DATA, jsonExpandData);
        JSONObject jsonExpandDataType = new JSONObject();
        result.put(DocumentJSONConstants.DOCUMENT_EXPAND_DATATYPE, jsonExpandDataType);
        JSONObject jsonExpandDataClass = new JSONObject();
        result.put(DocumentJSONConstants.DOCUMENT_EXPAND_CLASS, jsonExpandDataClass);

        for (Entry<String, Object> e : this.getAllExpandData().entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof String) {
                jsonExpandDataType.put(key, ExpandDataType.STRING);
                jsonExpandData.put(key, value);
            } else if (value instanceof Integer) {
                jsonExpandDataType.put(key, ExpandDataType.INT);
                jsonExpandData.put(key, value);
            } else if (value instanceof Long) {
                jsonExpandDataType.put(key, ExpandDataType.LONG);
                jsonExpandData.put(key, value);
            } else if (value instanceof Map) {
                jsonExpandDataType.put(key, ExpandDataType.MAP);
                JSONObject o = new JSONObject((Map<?, ?>)value);
                jsonExpandData.put(key, o);
            } else if (value instanceof JSONObject) {
                jsonExpandDataType.put(key, ExpandDataType.JSONOBJ);
                jsonExpandData.put(key, value);
            } else if (value instanceof JSONSerializable) {
                jsonExpandDataType.put(key, ExpandDataType.JSON_OBJECT);
                JSONSerializable js = (JSONSerializable)value;
                jsonExpandData.put(key, js.toJSON());
                jsonExpandDataClass.put(key, value.getClass().getName());
            }
        }
    }
    private static String getDicStatusCaption(DefaultContext context, String itemKey, Long oid) throws Throwable {
        if (oid <= 0) {
            return "";
        }
        Item item = ((IDictCacheProxy)context.getDictCache()).getItem(itemKey, oid, DictStateMask.All);
        if (item != null) {
            int enable = item.getEnable();
            if (enable != 1) {
                return "";
            }

        } else {
            return null;
        }

        String result = item.getCaption();
        return result;
    }

	private static boolean hasRights(DefaultContext context, String itemKey, Item item, String fieldKey) throws Throwable {
        if (item == null || item.getID() <= 0) {
            return true;
        }

//        if (RichServiceFilterImpl.isCheckAdminRights()) {
//            IRightsProvider operatorRights = RightsProviderFactory.getInstance().newRightsProvider(context);
//            DictRights dictRights = operatorRights.getDictRights(itemKey);
//
//            if (!dictRights.hasRights(item.getID())) {
//                if (SvrInfo.getNoRightInfoType().equalsIgnoreCase(SvrInfo.NoRightInfoType_Complex)) {
//					String code = TypeConvertor.toString(item.getValue("Code"));
//					throw new Exception("当前用户缺失如下权限:【读权限:ItemKey=" + itemKey + ",Data="
//							+ (code.length() == 0 ? item.getCaption() : code) + "】,请联系管理员!");
//				}
//				LogSvr.getInstance().error("dict: " + itemKey + " oid: " + item.getID() + " has no rights.", null);
//				throw new Exception("表单中字典【" + context.getVE().getMetaFactory().getDataObject(itemKey).getCaption()
//						+ "】存在当前操作员不具有权限的数据，打开失败");
//            }
//        } else {
//            String activity = AuthorityParaUtil.getActivity(context);
//            String tcode = AuthorityParaUtil.getTCode(context);
//            if (!StringUtil.isBlankOrNull(activity) && !StringUtil.isBlankOrNull(tcode)) {
                AuthorityCheckUtil.hasDictRights(context, itemKey, item.getID(), context.getFormKey(), fieldKey, true);
//            }
//        }

        return true;
    }

    private final static String STR_ComboBoxNoCacheFormulaValues = "ComboBoxNoCacheFormulaValues";

    /**
     * 预计算不缓存的下拉框的值
     * @param context
     * @throws Throwable
     */
    protected void preCalcComboBoxNoCacheFormulaValue(RichDocumentContext context) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        List<KeyPairMetaObject> comboBoxesWithFormula = idLookup.getAllComboBoxWithFormula();
        if (comboBoxesWithFormula == null) {
            return;
        }
        for (KeyPairMetaObject metaObject : comboBoxesWithFormula) {
            String key = metaObject.getKey();

            MetaComboBoxProperties properties = null;
            if (metaObject instanceof MetaComboBox) {
                MetaComboBox metaComboBox = (MetaComboBox) metaObject;
                properties = metaComboBox.getProperties();
            } else if (metaObject instanceof MetaGridCell) {
                MetaGridCell metaGridCell = (MetaGridCell) metaObject;
                properties = (MetaComboBoxProperties) metaGridCell.getProperties();
            }
            if (properties == null) {
                continue;
            }
            MetaBaseScript formulaItems = properties.getFormulaItems();
            String formula = formulaItems == null ? null : formulaItems.getContent();

            //如果是依赖于表格字段的那么这里算出来的结果是不对的， 暂时按不算处理
            List<String> dependedFields = properties.getDependedFields();
            boolean isDependGridCell = false;
            if (dependedFields != null && dependedFields.size() > 0) {
                for (String dependedField : dependedFields) {
                    if (StringUtils.equals(dependedField, key)) {
                        continue;
                    }
                    boolean headField = idLookup.isHeadField(dependedField);
                    if (headField) {
                        continue;
                    }
                    isDependGridCell = true;
                    break;
                }
            }
            if (isDependGridCell) {
                continue;
            }

            ScopeTree scopeTree = ScopeTreeBuilder.getScopeTree(context, metaForm);
            FormulaScope scope = scopeTree.get(key);
            if (scope != null && scope.depend.isHasOnlyUIFunction()) {
				LogSvr.getInstance().warn("字段" + key + "的下拉框公式" + formula + "， 包含客户端公式，无法在服务端执行！");
                continue;
            }
            HashMap<String, String> formulaValues = new HashMap<String, String>();
            this.putExpandData(STR_ComboBoxNoCacheFormulaValues, formulaValues);
            if (StringUtils.isBlank(formula)) {
                formulaValues.put(key, StringUtils.EMPTY);
            } else {
                Object items = context.evalWithTrace(MetaObjectType.Component, key, Property.ComboBoxFormula, formula);
                if (items instanceof String) { // 目前只支持字符串
                    formulaValues.put(key, (String)items);
                }
            }
        }
    }

    /**
     * 某些类型数据值转JSON特殊处理
     *
     * @param fieldKey
     * @param value
     * @return
     * @throws Exception
     */
    protected Object toJSONValue(String fieldKey, Object value) throws Exception {
        if (value == null) {
            return null;
        }
        if (!(value instanceof Date)) {
            return value;
        }
        Date date = (Date)value;
        return date.getTime();
    }

    @Override
    public void fromJSON(JSONObject jsonObj) throws Throwable {
        //fixme 业务代码中 例如com.bokesoft.erp.co.formula.ActivityTypeFormula.runSplitting，会先newbillentity，然后通过当前方法反序列化json为当前document数据，而在newBillentity时已经存在一些延后计算的默认值表达式，在effectScopeMap中也没清除。建议业务代码不要调用这个方法，采用com.bokesoft.yes.mid.cmd.richdocument.strut.DocumentRecordDirty.getDocumentFromString来做反序列化动作
        TraceSystemManager.withTrace(()-> {
            super.fromJSON(jsonObj);
            IDLookup idLookup = metaForm == null ? null : IDLookup.getIDLookup(metaForm);
            //		documentID = JSONHelper.readFromJSON(jsonObj, DOCUMENT_DOCUMENTID,"");
            form_OperationState = JSONHelper.readFromJSON(jsonObj, RICHDOCUMENT_FORM_OPERATIONSTATE, Document.NORMAL);
            if (jsonObj.has("headValues")) {
                JSONObject jsonHeadData = jsonObj.getJSONObject("headValues");
                Iterator<?> it = jsonHeadData.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    Object value = null;
                    //需要考虑jsonobject为空的情况
                    if (jsonHeadData.isNull(key)) {
                        value = null;
                    } else if (metaForm != null) {
                        value = jsonHeadData.get(key);
                        MetaColumn metaColumn = idLookup.getMetaColumnByFieldKey(key);
                        if (metaColumn != null) {
                            int dataType = metaColumn.getDataType();
                            Integer scale = metaColumn.getScale();
                            value = RichDocument.convert(dataType, value, scale);
                        }
                    } else {
                        value = jsonHeadData.get(key);
                    }
                    //Object value = jsonHeadData.get(key);
                    headValues.put(key, value);
                }

            }
            if (jsonObj.has("otherFieldValues")) {
                JSONObject jsonHeadData = jsonObj.getJSONObject("otherFieldValues");
                Iterator<?> it = jsonHeadData.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    Object value = null;
                    // 需要考虑jsonobject为空的情况
                    if (jsonHeadData.isNull(key)) {
                        value = null;
                    } else if (metaForm != null) {
                        value = jsonHeadData.get(key);
                        MetaColumn metaColumn = idLookup.getMetaColumnByFieldKey(key);
                        if (metaColumn != null) {
                            int dataType = metaColumn.getDataType();
                            Integer scale = metaColumn.getScale();
                            value = RichDocument.convert(dataType, value, scale);
                        }
                    } else {
                        value = jsonHeadData.get(key);
                    }
                    otherFieldValues.put(key, value);
                }
            }

            if (jsonObj.has("emptyGridRowValues")) {
                JSONObject jsonEmptyGridRowValues = jsonObj.getJSONObject("emptyGridRowValues");
                Iterator<?> it = jsonEmptyGridRowValues.keys();
                while (it.hasNext()) {
                    String gridKey = it.next().toString();
                    Object tmp = jsonEmptyGridRowValues.get(gridKey);
                    if (tmp.toString().equalsIgnoreCase("null")) {
                        continue;
                    }
                    JSONObject jsonEmptyRow = (JSONObject) jsonEmptyGridRowValues.get(gridKey);
                    Iterator<?> valuesIt = jsonEmptyRow.keys();
                    Map<String, Object> rowValue = new HashMap<String, Object>();
                    while (valuesIt.hasNext()) {
                        String fieldKey = valuesIt.next().toString();
                        Object value = jsonEmptyRow.get(fieldKey);
                        if (idLookup != null) {
                            MetaColumn metaColumn = idLookup.getMetaColumnByFieldKey(fieldKey);
                            if (metaColumn != null) {
                                int dataType = metaColumn.getDataType();
                                Integer scale = metaColumn.getScale();
                                value = RichDocument.convert(dataType, value, scale);
                            }
                        }
                        rowValue.put(fieldKey, value);
                    }
                    emptyGridRowValues.put(gridKey, rowValue);
                }
            }
            if (jsonObj.has("tableFilter")) {
                JSONObject jsonTableFilter = jsonObj.getJSONObject("tableFilter");
                Iterator<?> it = jsonTableFilter.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    SqlString value = null;
                    if (jsonTableFilter.isNull(key)) {
                        value = null;
                    } else {
                        value = SqlStringUtil.StringToSqlString(jsonTableFilter.get(key).toString());
                    }
                    setTableFilter(key, value);
                }

            }
            syncClientExpandData(jsonObj);
        },this, PerformanceAttributeData.build(ActionType.Document.FROM_JSON.getCode(),"RichDocument#fromJSON"));
    }

    /**
     * 将客户端的expandtable同步到源表中，扩展报表中存在与服务端交互的情况
     *
     * @param jsonObj
     * @throws Throwable
     */
    private void syncClientExpandData(JSONObject jsonObj) throws Throwable {
        if (isReportType() && jsonObj.has(DocumentJSONConstants.DOCUMENT_TABLELIST)) {
            IDLookup idLookup = metaForm == null ? null : IDLookup.getIDLookup(metaForm);
            JSONArray tableArray = (JSONArray)jsonObj.get(DocumentJSONConstants.DOCUMENT_TABLELIST);
            for (int index = 0; index < tableArray.length(); index++) {
                JSONObject tableJSON = tableArray.getJSONObject(index);
				if (tableJSON.has(DocumentJSONConstants.DATATABLE_TABLEKEY)
						&& tableJSON.has(ERPVirtualGrid.EXPAND_DATATABLE)) {
                    String tableKey = tableJSON.getString(DocumentJSONConstants.DATATABLE_TABLEKEY);
                    JSONObject clientExpandDatatable = tableJSON.getJSONObject(ERPVirtualGrid.EXPAND_DATATABLE);
                    DataTable clientExpandDataTable = new DataTable();
                    clientExpandDataTable.fromJSON(clientExpandDatatable);

                    Object value = null;
                    String columnKey = null;
                    ExpandDataModel expandModel = getExpandModel(tableKey);
                    expandModel.loadExpandDataTable();

                    DataTableMetaData meta = clientExpandDataTable.getMetaData();
                    clientExpandDataTable.beforeFirst();
                    while (clientExpandDataTable.next()) {
						TableMultiKeyInfo primaryInfos = TableMultiKeyInfo.newInstance(tableKey, clientExpandDataTable, expandModel.getPrimaryKeys());
                        ExpandRowModel expandRow = expandModel.ensureExpandRow(primaryInfos, false);
                        expandRow.reloadSourceBkmk();
                        for (int col = 0; col < meta.getColumnCount(); col++) {
                            value = clientExpandDataTable.getObject(col);
                            if (value == null) {
                                continue;
                            }

                            columnKey = meta.getColumnInfo(col).getColumnKey();
                            if (columnKey.endsWith(MetaFormNODBProcess.STR_NODBTable_Profix)) {
                                List<String> fieldKeys = idLookup.getFieldListKeyByTableColumnKey(tableKey, columnKey);
                                if (!fieldKeys.isEmpty()) {
                                    for (String fieldKey : fieldKeys) {
                                        expandRow.setCellValueToTable(fieldKey, value);
                                    }
                                }
                            } else {
                                expandRow.setCellValueToTable(columnKey, value);
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean isBlankOrNull(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public boolean isWFMapping() {
        return bWFMapping;
    }

    public void setWFMapping(boolean b) throws Throwable {
        //将所有依赖	bWFMapping的公式计算掉
        List<FormulaItem> formulaItems = effectScopeMap.beforeSetRichDocAttrCollectFormulaItems(true, false);
        while (formulaItems != null && formulaItems.size() > 0) {
            if (calcFormulaItems(formulaItems)) {
                formulaItems = effectScopeMap.beforeSetRichDocAttrCollectFormulaItems(true, false);
            } else {
                formulaItems = null;
            }
        }
        bWFMapping = b;
    }
	/**
	 * 根据OID取BookMark
     * @param fieldKey
     * @param OID
     * @return
     * @throws Throwable
     */
    public int getBookMarkByOID(String fieldKey, Long OID) throws Throwable {
        if (IDLookup.isOtherField(fieldKey)) {
            return -1;
        }
        //表头字段BookMark直接返回-1;
        IDLookup idlookup = IDLookup.getIDLookup(metaForm);
        MetaComponent metaComponent = idlookup.getComponentByKey(fieldKey);
        if (metaComponent != null) {
            return -1;
        }
        int bkmk = -1;
        Integer tmp = oidBookMarks.get(OID);
        if (tmp != null) {
            bkmk = tmp;
        } else {
            String tableKey = idlookup.getTableKeyByFieldKey(fieldKey);
            bkmk = getBookMarkByOID4Table(tableKey, OID);
        }
        return bkmk;
    }
    /**
     * 根据行号取BookMark
     * @param tableKey
     * @param rowIndexes
     * @return
     */
    public int[] getBookMarksByRowIndexes(String tableKey, int[] rowIndexes) {
        DataTable dataTable = get_impl(tableKey);
        assert(dataTable != null);
        return Arrays.stream(rowIndexes).map(dataTable::getBookmark).toArray();
    }
    /**
     * 根据行号取BookMark
     * @param tableKey
     * @param rowIndex
     * @return
     */
    public int getBookMarkByRowIndex(String tableKey, int rowIndex) {
        DataTable dataTable = get_impl(tableKey);
        assert(dataTable != null);
        assert(rowIndex >= 0 && rowIndex < dataTable.size());
        return dataTable.getBookmark(rowIndex);
    }

    private int getBookMarkByOID4Table(String tableKey, Long OID) {
        int bkmk = -1;
        DataTable dt = get_impl(tableKey);
        int oidColumnIndex = dt.getMetaData().findColumnIndexByKey(SystemField.OID_SYS_KEY);
        for (int i = 0, len = dt.size(); i < len; i++) { // 一次性将所有的数据都取完，省得大数据量时性能指数级下降
            Long tmpOid = dt.getLong(i, oidColumnIndex);
            oidBookMarks.put(tmpOid, dt.getBookmark(i));
        }
        Integer tmp2 = oidBookMarks.get(OID);
        if (tmp2 != null) {
            bkmk = tmp2;
        }
        return bkmk;
    }

    public int appendDetail(RichDocumentContext context, String tableKey) throws Throwable {
        return appendDetail(context, tableKey, true);
    }

    /**
     * 增加行
     * @param context
     * @param tableKey
     * @return
     * @throws Throwable
     */
    public int appendDetail(RichDocumentContext context, String tableKey, boolean runValueChanged) throws Throwable {
        addDirtyTableFlag(tableKey);
        
        DataTable dataTable = get_impl(tableKey);
        MetaForm metaForm = getMetaForm();
        MetaTable metaTable = metaForm.getMetaTable(tableKey);
        int newRowIndex = DocumentUtil.newRow(metaTable, dataTable);
        final MetaDataObject dataObject = metaForm.getDataSource().getDataObject();
        final MetaTable mainTable = dataObject.getMainTable();
        final Integer secondaryType = dataObject.getSecondaryType();

        Long soid = this.getOID();
        if (soid <= 0 && mainTable != null && metaTable.getKey().equalsIgnoreCase(mainTable.getKey())) {
            soid = context.applyNewOID();
            setOID(soid);
        }
		boolean noMainTable = mainTable == null
				|| secondaryType == DataObjectSecondaryType.MIGRATION
				|| secondaryType == DataObjectSecondaryType.DATAOBJECTLIST;
        boolean isHeadTable = metaTable.getLevelID() <= 2 && !noMainTable && metaTable.isHead();
        Long oid = isHeadTable ? soid : context.applyNewOID();
        dataTable.setLong(newRowIndex, SystemField.OID_SYS_KEY, oid);
        dataTable.setLong(newRowIndex, SystemField.SOID_SYS_KEY, soid);

        Object POIDValue = dataTable.getObject(newRowIndex, SystemField.POID_SYS_KEY);
        if (POIDValue == null) {
            dataTable.setLong(newRowIndex, SystemField.POID_SYS_KEY, 0L);
        }
        int bookMark = dataTable.getBookmark(newRowIndex);
        setCurrentBookMark(tableKey, bookMark);
        String parentKey = metaTable.getParentKey();
        int parentBookMark = -1;
        if (parentKey != null && parentKey.length() > 0 && !parentKey.substring(0, 1).equalsIgnoreCase("$")) {
            parentBookMark = getCurrentBookMark(parentKey);
        }
        
        // 如果是子明细行，需要对子明细行设置父亲的parentBookmark.
		if (parentBookMark>=0 && parentKey != null && parentKey.length() > 0 && !parentKey.substring(0, 1).equalsIgnoreCase("$")) {
            DataTable parentDataTable = get_impl(parentKey);
            if (parentDataTable != null) {
                int parentRowIndex = getRowIndexByBookmark(parentDataTable, parentBookMark);
                dataTable.setParentBookmark(parentBookMark);
				dataTable.setLong(newRowIndex, SystemField.POID_SYS_KEY,parentDataTable.getLong(parentRowIndex, SystemField.OID_SYS_KEY));
            }
        }
		
        if (runValueChanged) {
            pCalRowDefaultRow(context, bookMark, tableKey, dataTable, newRowIndex);
        } else {
            setDefaultValue4NewRow(context, bookMark, tableKey, dataTable, newRowIndex);
        }

        setCurrentBookMark(tableKey, bookMark);
        return newRowIndex;
    }

    private int getRowIndexByBookmark(DataTable table, int bookmark) {
        if (table == null) {
            return -1;
        }
        for (int rowIndex = 0, size = table.size(); rowIndex < size; rowIndex++) {
            int bkmk = table.getBookmark(rowIndex);
            if (bookmark == bkmk) {
                return rowIndex;
            }
        }
        return -1;
    }

    public int appendDetailByRowIndex(RichDocumentContext context, String tableKey, int rowIndex) throws Throwable {
        return appendDetailByRowIndex(context, tableKey, rowIndex, true);
    }

    /**
     * 在rowIndex后增加行
     *
     * @param context
     * @param tableKey
     * @param rowIndex
     * @param runValueChanged
     * @return
     * @throws Throwable
     */
    public int appendDetailByRowIndex(RichDocumentContext context, String tableKey, int rowIndex,
        boolean runValueChanged) throws Throwable {
        addDirtyTableFlag(tableKey);

        DataTable dataTable = get(tableKey);
        MetaForm metaForm = getMetaForm();
        MetaTable metaTable = metaForm.getMetaTable(tableKey);
        int newRowIndex = dataTable.insert(rowIndex);

        Iterator<MetaColumn> itColumn = metaTable.iterator();
        Object value = null;
        while (itColumn.hasNext()) {
            MetaColumn metaColumn = itColumn.next();
            String defaultValue = metaColumn.getDefaultValue();
            String key = metaColumn.getKey();
            if (defaultValue != null) {
                if (defaultValue.isEmpty()) {
					if(metaColumn.getDataType() == DataType.STRING ||
							metaColumn.getDataType() == DataType.TEXT ||
							metaColumn.getDataType() == DataType.FIXED_STRING){
                        dataTable.setObject(key, StringUtil.EMPTY_STRING);
                    } else {
                        // 非系统字段数值类型的默认值设为 0
                        if (metaColumn.isNumeric()) {
                            value = TypeConvertor.toDataType(metaColumn.getDataType(), 0);
                            dataTable.setObject(key, value);
                        }
                    }
                } else {
                    value = DBDataConvertor.toConstValue(metaColumn.getDataType(), defaultValue);
                    dataTable.setObject(key, value);
                }
            } else {
                // 非系统字段数值类型的默认值设为 0
                if (metaColumn.isNumeric()) {
                    value = TypeConvertor.toDataType(metaColumn.getDataType(), 0);
                    dataTable.setObject(key, value);
                } else if (metaColumn.isString()) {
                    dataTable.setObject(key, StringUtil.EMPTY_STRING);
                }
            }
        }
        final MetaDataObject dataObject = metaForm.getDataSource().getDataObject();
        final MetaTable mainTable = dataObject.getMainTable();
        final Integer secondaryType = dataObject.getSecondaryType();

        Long soid = this.getOID();
        if (soid <= 0 && mainTable != null && metaTable.getKey().equalsIgnoreCase(mainTable.getKey())) {
            soid = context.applyNewOID();
            setOID(soid);
        }
		boolean noMainTable = mainTable == null
				|| secondaryType == DataObjectSecondaryType.MIGRATION
				|| secondaryType == DataObjectSecondaryType.DATAOBJECTLIST;
        boolean isHeadTable = metaTable.getLevelID() <= 2 && !noMainTable && metaTable.isHead();
        Long oid = isHeadTable ? soid : context.applyNewOID();
        dataTable.setLong(newRowIndex, SystemField.OID_SYS_KEY, oid);
        dataTable.setLong(newRowIndex, SystemField.SOID_SYS_KEY, soid);

        Object POIDValue = dataTable.getObject(newRowIndex, SystemField.POID_SYS_KEY);
        if (POIDValue == null) {
            dataTable.setLong(newRowIndex, SystemField.POID_SYS_KEY, 0L);
        }
        int bookMark = dataTable.getBookmark(newRowIndex);
        setCurrentBookMark(tableKey, bookMark);
        //		if (metaTable.getTableMode() == TableMode.DETAIL && metaTable.containsKey(SystemField.SEQUENCE_SYS_KEY)) {
        //			dataTable.setInt(newRowIndex, SystemField.SEQUENCE_SYS_KEY, newRowIndex + 1);
        //		}

        if (runValueChanged) {
            pCalRowDefaultRow(context, bookMark, tableKey, dataTable, newRowIndex);
        } else {
            setDefaultValue4NewRow(context, bookMark, tableKey, dataTable, newRowIndex);
        }
        //插入行默认值计算有可能会修改当前表格的行号，这边需要重新设置当前行
        setCurrentBookMark(tableKey, bookMark);
        return newRowIndex;
        // todo 如果是子明细行，需要对子明细行设置父亲的parentBookmark.
        // String parentKey = metaTable.getParentKey();
        // if (parentKey != null && parentKey.length() > 0)
        // table.setParentBookmark(bookMarkMap.get(parentKey));
    }

    /**
     * 通过表达式来设置的条件 不同于平台的filterMap，产品这里是一个sql的where片段，不是用?表示的
     */
    private HashMap<String, SqlString> _tableFilterMap;

    private void setTableFilter(String tableKey, SqlString filter) {
        if (_tableFilterMap == null) {
            _tableFilterMap = new HashMap<String, SqlString>();
        }
        _tableFilterMap.put(tableKey, filter);
    }

    public void setTableFilter(String tableKey, Object filter) {
        SqlString sqlString = SqlStringUtil.ToSqlString(filter);
        setTableFilter(tableKey, sqlString);
    }

    public SqlString getTableFilter(String tableKey) {
        if (_tableFilterMap == null) {
            _tableFilterMap = new HashMap<String, SqlString>();
        }
        return _tableFilterMap.get(tableKey);
    }

    public HashMap<String, SqlString> getTableFilterMap() {
        return _tableFilterMap;
    }

    private HashMap<String, List<Object>> _tableFilterParasMap;

    public HashMap<String, List<Object>> getTableFilterParasMap() {
        return _tableFilterParasMap;
    }

    public void setTableFilterParasMap(HashMap<String, List<Object>> _tableFilterParasMap) {
        this._tableFilterParasMap = _tableFilterParasMap;
    }

    public List<Object> getTableFilterParas(String tableKey) {
		return _tableFilterParasMap == null || !_tableFilterParasMap.containsKey(tableKey) ? null : _tableFilterParasMap.get(tableKey);
    }

    public void setTableFilterParas(String tableKey, List<Object> values) {
        if (_tableFilterParasMap == null) {
            _tableFilterParasMap = new HashMap<>();
        }
        _tableFilterParasMap.put(tableKey, values);
    }

    public void setTableFilterMap(HashMap<String, SqlString> tableFilterMap) {
        _tableFilterMap = tableFilterMap;
    }

    @Override
    public void clear() {
        super.clear();
        //平台在loadData之前会执行doc.clear(),此处把tableFilter清掉会导致表单load出来的数据不对,此处暂时先注释掉，等sunyh回来讨论修改方案
        //		if (_tableFilterMap != null) {
        //			_tableFilterMap.clear();
        //			_tableFilterMap = null;
        //		}

        oidBookMarks.clear();

        //		headValues.clear();
        //		headValues = null;

        valueChangedList.clear();

        delayUIFormula.clear();
        variantCalcBookmarks.clear();

        for (HashMap<Integer, GridRow> tmp : delayGridRowCheckUIFormula.values()) {
            tmp.clear();
            tmp = null;
        }
        delayGridRowCheckUIFormula.clear();

        for (Map<String, Object> tmp : emptyGridRowValues.values()) {
            tmp.clear();
            tmp = null;
        }
        emptyGridRowValues.clear();

        if (expandManager != null) {
            expandManager.resetExpandModel();
        }

        if (dictFilterChecker != null) {
            dictFilterChecker.clear();
        }
                
        this.resetLockFields.clear();
    }

    /**
     * document 数据有效性检查，目前检查document字段的checkRule ，字典类型字段根据字典的filter检查当前的值是否满足条件
     * @return
     * @throws Throwable
     */
    private List<String> checkValid(RichDocumentContext context, boolean onlyPersist) throws Throwable {
        if (dictFilterChecker == null) {
            this.dictFilterChecker = new CachedDictFilterChecker(this);
        }
        dictFilterChecker.init(context);

        MetaForm metaForm = getMetaForm();
        // 取所有component组件
        List<MetaComponent> allComponents = metaForm.getAllComponents();

        MetaDataObject dataObject = metaForm.getDataSource().getDataObject();
        IDLookup idLookup = IDLookup.getIDLookup(getMetaForm());
        Set<CheckErrorInfo> errInfoSet = new LinkedHashSet<CheckErrorInfo>() {
            @Override
            public boolean add(CheckErrorInfo e) {
                return super.add(e); // 把断点打在这里, 当产生错误信息, 就会停在这里, 便于确定产生错误信息的位置
            }
        };
        // 先对头表组件进行检查
        for (MetaComponent metaComponent : allComponents) {
            int controlType = metaComponent.getControlType();
            // 如果是grid表格,则跳过,下面单独进行表格检查
			if (ControlType.GRID != controlType && ControlType.LABEL != controlType && ControlType.BLOCK != controlType
					&& ControlType.TABPANEL != controlType && ControlType.GRIDLAYOUTPANEL != controlType
					&& ControlType.FLEXFLOWLAYOUTPANEL != controlType && ControlType.DICTVIEW != controlType && ControlType.SPLITPANEL != controlType
					&& ControlType.TOOLBAR != controlType) {
                String fieldKey = metaComponent.getKey();
                // 如果是模拟，凭证编号不做检查
				if (DOCUMENTNUMBER.equalsIgnoreCase(fieldKey) && (RichDocumentDefaultCmd.getThreadLocalData(SimulateDocumentMap) != null
						|| RichDocumentDefaultCmd.getThreadLocalData(SimulateImportDocumentMap)!=null)) {
                    continue;
                }
                checkOneDocument_headFields(context, metaForm.getKey(), fieldKey, idLookup, errInfoSet, onlyPersist);
            }
        }
        // 接下来检查明细表
        List<MetaGrid> metaGrids = idLookup.getMetaGrids();
        if (metaGrids == null) {
            List<String> errInfoList = new ArrayList<>();
            for (CheckErrorInfo checkErrorInfo : errInfoSet) {
                errInfoList.add(checkErrorInfo.toString());
            }
            return errInfoList;
        }
        for (MetaGrid metaGrid : metaGrids) {
            if (!StringUtils.isEmpty(metaGrid.getParentGridKey())) {
                continue;
            }
            MetaGridRow metaGridRow = metaGrid.getDetailMetaRow();
            if (metaGridRow == null) {
                continue;
            }
            String tableKey = metaGridRow.getTableKey();
            MetaTable metaTable = dataObject.getMetaTable(tableKey);
            if (metaTable == null) {
                continue;
            }
            DataTable dataTable = get_impl(tableKey);
            if (dataTable == null) {
                continue;
            }

            int oldCurrentBkmk = this.getCurrentBookMark(tableKey);
            for (int i = 0; i < dataTable.size(); i++) {
                int curBookMark = dataTable.getBookmark(i);
                setCurrentBookMark(tableKey, curBookMark);
                pCheckOneGridRow(context, metaForm.getKey(), idLookup, metaGrid, curBookMark, errInfoSet, onlyPersist);
            }
            // 计算完重置为之前的currentBkmk
            this.setCurrentBookMark(tableKey, oldCurrentBkmk);
        }
        List<String> errInfoList = new ArrayList<>();
        for (CheckErrorInfo error : errInfoSet) {
            errInfoList.add(error.toString());
        }
        return errInfoList;
    }

    /**
     * 与checkValid检查逻辑一致的方法
     * 不同的是对结果进行了包装，表caption+行号形式进行表格树的构建
     *
     * @param context
     * @param onlyPersist
     * @return 返回一个key是行号;表caption，value是错误描述的Map List
     * @throws Throwable
     */
    private List<HashMap<String,String>> checkValid2MessageList(RichDocumentContext context, boolean onlyPersist) throws Throwable {
        if (dictFilterChecker == null) {
            this.dictFilterChecker = new CachedDictFilterChecker(this);
        }
        dictFilterChecker.init(context);

        MetaForm metaForm = getMetaForm();
        // 取所有component组件
        List<MetaComponent> allComponents = metaForm.getAllComponents();

        MetaDataObject dataObject = metaForm.getDataSource().getDataObject();
        IDLookup idLookup = IDLookup.getIDLookup(getMetaForm());
        List<HashMap<String, String>> results = new ArrayList<>();
        // 先对头表组件进行检查
        for (MetaComponent metaComponent : allComponents) {
            Set<CheckErrorInfo> errInfoSet = new LinkedHashSet<>();
            int controlType = metaComponent.getControlType();
            // 如果是grid表格,则跳过,下面单独进行表格检查
			if (ControlType.GRID != controlType && ControlType.LABEL != controlType && ControlType.BLOCK != controlType
					&& ControlType.TABPANEL != controlType && ControlType.GRIDLAYOUTPANEL != controlType
					&& ControlType.FLEXFLOWLAYOUTPANEL != controlType && ControlType.DICTVIEW != controlType && ControlType.SPLITPANEL != controlType
					&& ControlType.TOOLBAR != controlType) {
                String fieldKey = metaComponent.getKey();
                // 如果是模拟，凭证编号不做检查
				if (DOCUMENTNUMBER.equalsIgnoreCase(fieldKey) && (RichDocumentDefaultCmd.getThreadLocalData(SimulateDocumentMap) != null
						|| RichDocumentDefaultCmd.getThreadLocalData(SimulateImportDocumentMap)!=null)) {
                    continue;
                }
                checkOneDocument_headFields(context, metaForm.getKey(), fieldKey, idLookup, errInfoSet, onlyPersist);
                for (CheckErrorInfo error : errInfoSet) {
                    HashMap<String, String> key2Error = new HashMap<>();
                    key2Error.put("0;"+metaComponent.getTableKey(),error.toString());
                    results.add(key2Error);
                }
            }
        }
        // 接下来检查明细表
        List<MetaGrid> metaGrids = idLookup.getMetaGrids();
        if (metaGrids == null) {
            return results;
        }
        for (MetaGrid metaGrid : metaGrids) {
            if (!StringUtils.isEmpty(metaGrid.getParentGridKey())) {
                continue;
            }
            MetaGridRow metaGridRow = metaGrid.getDetailMetaRow();
            if (metaGridRow == null) {
                continue;
            }
            String tableKey = metaGridRow.getTableKey();
            MetaTable metaTable = dataObject.getMetaTable(tableKey);
            if (metaTable == null) {
                continue;
            }
            DataTable dataTable = get_impl(tableKey);
            if (dataTable == null) {
                continue;
            }

            int oldCurrentBkmk = this.getCurrentBookMark(tableKey);
            for (int i = 0; i < dataTable.size(); i++) {
                Set<CheckErrorInfo> errInfoSet = new LinkedHashSet<>();
                int curBookMark = dataTable.getBookmark(i);
                setCurrentBookMark(tableKey, curBookMark);
                int index = i + 1;
                pCheckOneGridRow2MessageList(results,index,context, metaForm.getKey(), idLookup, metaGrid, curBookMark, errInfoSet, onlyPersist);
            }
            // 计算完重置为之前的currentBkmk
            this.setCurrentBookMark(tableKey, oldCurrentBkmk);
        }
        return results;
    }

    /**
     * 对头字段进行检查
     *
     * @param context
     * @param formKey
     * @param idLookup
     * @param errInfoSet
     * @param onlyPersist
     * @return
     * @throws Throwable
     */
	private void checkOneDocument_headFields(RichDocumentContext context,String formKey, String fieldKey, IDLookup idLookup,
											 Set<CheckErrorInfo> errInfoSet, boolean onlyPersist) throws Throwable {
        pCheckOneField_checkRule(context, idLookup, fieldKey, 0, errInfoSet, onlyPersist);
        pCheckOneField_dictFilter(context, formKey, idLookup, fieldKey, 0, errInfoSet, onlyPersist);
        pCheckComponentProperties(context, fieldKey, idLookup, errInfoSet, 0);
    }

    /**
     * 检查表格一行数据，同时检查对应的子表格。
     *
     * @param context
     * @param idLookup
     * @param metaGrid
     * @param errInfoSet
     * @param onlyPersist
     * @throws Throwable
     */
    private void pCheckOneGridRow(RichDocumentContext context, String formKey, IDLookup idLookup, MetaGrid metaGrid,
        int curBookMark, Set<CheckErrorInfo> errInfoSet, boolean onlyPersist) throws Throwable {
        MetaGridRow metaGridRow = metaGrid.getDetailMetaRow();
        String tableKey = metaGridRow.getTableKey();
        DataTable table = get_impl(tableKey);
        int rowIndex = table.getRowIndexByBookmark(curBookMark);
        Long pOID = table.getLong(rowIndex, SystemField.OID_SYS_KEY);
        for (MetaGridCell gridCell : metaGridRow) {
            pCheckOneField_checkRule(context, idLookup, gridCell.getKey(), curBookMark, errInfoSet, onlyPersist);
            pCheckOneField_dictFilter(context, formKey, idLookup, gridCell.getKey(), curBookMark, errInfoSet,
                onlyPersist);
            pCheckComponentProperties(context, gridCell.getKey(), idLookup, errInfoSet, curBookMark);
        }
        List<String> childGridKeys = idLookup.getChildGridKeyByGridKey(metaGrid.getKey());
        for (String childGridKey : childGridKeys) {
            String childTableKey = idLookup.getTableKeyByGridKey(childGridKey);
            DataTable childDataTable = get_impl(childTableKey);
            if (childDataTable == null) {
                continue;
            }
            int[] childRowIndexes = childDataTable.fastFilter(SystemField.POID_SYS_KEY, pOID);
            for (int childRowIndex : childRowIndexes) {
                int childBookMark = childDataTable.getBookmark(childRowIndex);
                setCurrentBookMark(childTableKey, childBookMark);
                pCheckOneGridRow(context, formKey, idLookup, idLookup.getMetaGridByGridKey(childGridKey), childBookMark,
                        errInfoSet, onlyPersist);
            }
        }
    }

    private void pCheckOneGridRow2MessageList(List<HashMap<String, String>> results, Integer index, RichDocumentContext context, String formKey, IDLookup idLookup, MetaGrid metaGrid,
                                              int curBookMark, Set<CheckErrorInfo> errInfoSet, boolean onlyPersist) throws Throwable {
        MetaGridRow metaGridRow = metaGrid.getDetailMetaRow();
        for (MetaGridCell gridCell : metaGridRow) {
            pCheckOneField_checkRule(context, idLookup, gridCell.getKey(), curBookMark, errInfoSet, onlyPersist);
            pCheckOneField_dictFilter(context, formKey, idLookup, gridCell.getKey(), curBookMark, errInfoSet,
                onlyPersist);
            pCheckComponentProperties(context, gridCell.getKey(), idLookup, errInfoSet, curBookMark);
        }
        for (CheckErrorInfo error : errInfoSet) {
            HashMap<String, String> key2Error = new HashMap<>();
            key2Error.put(index+";"+metaGrid.getTableKey(),error.toString());
            results.add(key2Error);
        }
        List<String> childGridKeys = idLookup.getChildGridKeyByGridKey(metaGrid.getKey());
        for (String childGridKey : childGridKeys) {
            String childTableKey = idLookup.getTableKeyByGridKey(childGridKey);
            DataTable childDataTable = get_impl(childTableKey);
            if (childDataTable == null) {
                continue;
            }
            MetaGrid gridByGridKey = idLookup.getMetaGridByGridKey(childGridKey);
            for (int i = 0; i < childDataTable.size(); i++) {
                Set<CheckErrorInfo> childErrInfoSet = new HashSet<>();
                int childBookMark = childDataTable.getBookmark(i);
                if (childDataTable.getParentBookmark(i) != curBookMark) {
                    continue;//如果不是父表格某行数据对应的子表格数据，则跳过【性能优化】
                }
                setCurrentBookMark(childTableKey, childDataTable.getBookmark(i));
                pCheckOneGridRow(context, formKey, idLookup, gridByGridKey, childBookMark,
                        childErrInfoSet, onlyPersist);
                int childIndex = i+1;
                for (CheckErrorInfo error : childErrInfoSet) {
                    HashMap<String, String> key2Error = new HashMap<>();
                    key2Error.put(childIndex+";"+gridByGridKey.getTableKey(),error.toString());
                    results.add(key2Error);
                }
            }
        }
    }

    private void pCheckOneField_checkRule(RichDocumentContext context, IDLookup idLookup, String fieldKey, int bookMark,
        Set<CheckErrorInfo> errInfoSet, boolean onlyPersist) throws Throwable {
        //没有数据源的字段不做检查
        if (onlyPersist) {
            MetaColumn metaColumn = idLookup.getMetaColumnByFieldKey(fieldKey);
            if (metaColumn == null || !metaColumn.isPersist()) {
                return;
            }
        }

        //获取当前条件下的动态检查规则
        DynamicFieldCheckRule dynRule =
            CheckValidExtensionPointManager.getDynamicCheckRule(context, idLookup, fieldKey, bookMark);

        boolean isRequired = idLookup.isRequired(fieldKey);
        if ((!isRequired) && dynRule.isRequired()) {    //如果最终扩展动态检查规则要求"必填", 则总是需要进行必填检查
            isRequired = true;
        }
        if (isRequired) {
            Object value = this.getValue(fieldKey, bookMark);
            boolean isAllowMultiSelection = false;
            boolean isEditValue = false;
            String controlType = "";
            MetaComponent component = idLookup.getComponentByKey(fieldKey);
            if (component != null) {
                controlType = ControlType.toString(component.getControlType());
                if (component instanceof MetaDict) {
                    isAllowMultiSelection = ((MetaDict)component).isAllowMultiSelection();
                    isEditValue = ((MetaDict)component).isEditValue();
                }
            } else {
                MetaGridCell gridCell = idLookup.getGridCellByKey(fieldKey);
                if (gridCell != null) {
                    controlType = ControlType.toString(gridCell.getCellType());
                    if (gridCell.getCellType() == ControlType.DICT) {
                        MetaDictProperties metaDictProperties = (MetaDictProperties)gridCell.getProperties();
                        isAllowMultiSelection = metaDictProperties.isAllowMultiSelection();
                        isEditValue = metaDictProperties.isEditValue();
                    }
                } else {
                    controlType = idLookup.getFieldControlType(fieldKey);
                }
            }
            boolean valid = true;
			if (controlType.equalsIgnoreCase(ControlType.STR_DICT)  && !isAllowMultiSelection && !isEditValue && TypeConvertor.toLong(value) == 0) {
                valid = false;
            } else if (value == null || value.toString().length() == 0) {
                valid = false;
            }
            if (!valid) {
                String caption = idLookup.getFieldCaption(fieldKey);
                String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 不能为空，检查不通过\n\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption));
                errInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, bookMark));
            }
        }
        List<String> checkRules = idLookup.getCheckRuleByFieldKey(fieldKey);
        for (int i = 0; i < checkRules.size(); i++) {
            String checkRule = checkRules.get(i);
            if (StringUtils.isNotEmpty(checkRule)) {
                Object result = context.getMidParser().eval(ScriptType.Formula, checkRule);
                if (result != null && result.toString().length() > 0 && !result.toString().equals("true")) {
                    String caption = idLookup.getFieldCaption(fieldKey);
					//fixme 这里提示出具体的值是不是更合理？
                    String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 检查不通过\n{3}\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), result);
                    errInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, bookMark));
                }
            }
        }
    }

    /**
     * 获取字典过滤条件
     *
     * @param formKey  当前表单FormKey
     * @param itemKey  表单过滤字典的itemKey
     * @param fieldKey 字典中的过滤字段FiledKey
     * @return
     */
    public BaseItemFilter getDictFilter(String formKey, String fieldKey, String itemKey) throws Throwable {
        RefParameter<Integer> filterIndex = new RefParameter<>(-1);
        MetaFilter metaFilter = getMetaFilter(formKey, fieldKey, itemKey, filterIndex);
        if (metaFilter == null) {
            return null;
        }
        BaseItemFilter itemFilter = new BaseItemFilter();
        itemFilter.setFieldKey(fieldKey);
        itemFilter.setItemKey(itemKey);
        itemFilter.setFormKey(formKey);
        itemFilter.setFilterIndex(filterIndex.getValue());
        Object obj = null;

        for (int index = 0; index < metaFilter.size(); index++) {
            MetaFilterValue metaFilterValue = metaFilter.get(index);
            int metaFilterType = metaFilter.getType();
            String formula = "";
            switch (metaFilterType) {
                case DictFilterType.FieldValue:
                    formula = metaFilterValue.getRefValueKey();
                    break;
                case DictFilterType.DataSet:
                case DictFilterType.Custom:
                    formula = metaFilterValue.getParaValue();
                    break;
            }
            int metaFilterValueType = metaFilterValue.getType();
            obj = metaFilterValueType == FilterValueType.CONST ? TypeConvertor.toString(formula)
                : context.getMidParser().eval(ScriptType.Formula, formula);
            if (obj instanceof SqlString) {
                itemFilter.addFilterValue(obj);
            } else {
                if (obj != null) {
                    itemFilter.addFilterValue(obj.toString());
                }
            }
        }
        return itemFilter;
    }

    /**
     * 参照前端的YIUI.DictHandler.getMetaFilter方法
     *
     * @param formKey
     * @param fieldKey
     * @param itemKey
     * @return
     * @throws Throwable
     */
    private MetaFilter getMetaFilter(String formKey, String fieldKey, String itemKey, RefParameter<Integer> filterIndex)
        throws Throwable {
        MetaForm form = this.context.getMetaFactory().getMetaForm(formKey);
        IDLookup idLookup = IDLookup.getIDLookup(form);
        MetaItemFilterCollection itemFilters = idLookup.getMetaItemFiltersByFieldKey(fieldKey);
        if (itemFilters == null || itemFilters.size() == 0) {
            return null;
        }

        MetaItemFilter itemFilter = itemFilters.get(itemKey);
        if (itemFilter == null) {
            itemFilter = itemFilters.get("");
        }
        if (itemFilter == null || itemFilter.size() == 0) {
            return null;
        }
        int index = -1;
        for (MetaFilter metaFilter : itemFilter) {
            index++;
            String condition = metaFilter.getCondition();
            // 这里的判断顺序会影响取值结果，所以先判断不为空的情况
            if (!StringUtil.isBlankOrNull(condition)) {
                Object ret = this.context.getMidParser().eval(ScriptType.Formula, condition);
                if (TypeConvertor.toBoolean(ret)) {
                    filterIndex.setValue(index);
                    return metaFilter;
                }
            } else {
                filterIndex.setValue(index);
                return metaFilter;
            }
        }
        return null;
    }

    private void pCheckOneField_dictFilter(RichDocumentContext context, String formKey, IDLookup idLookup,
										   String fieldKey, int bookMark,Set<CheckErrorInfo> errInfoSet,
										   boolean onlyPersist) throws Throwable {
		dictFilterChecker.check(context, formKey, idLookup, fieldKey, bookMark, errInfoSet, onlyPersist);
	}



	/**
	 * 批量计算界面中表格空白行默认值
	 *
	 * @param focusGridKey
	 * @throws Throwable
	 */
	public void calcEmptyRowIndependGrids(DefaultContext context,String focusGridKey,HashMap<String,Boolean> gridNewEmptyRows) throws Throwable {
        MetaForm metaForm = this.getMetaForm();
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);

        List<MetaGrid> metaGrids = idLookup.getMetaGrids();
        if (metaGrids == null) {
            return;
        }
        for (MetaGrid metaGrid : metaGrids) {
            String parentGridKey = metaGrid.getParentGridKey();
            // 算空白行默认值跟能不能插行没关系,无插行操作设置enable=true也需要算
            if (!metaGrid.hasDetailRow()) {
                continue;
            }
            // 只有需要新增空白行的表格才会计算
			boolean newEmptyRow = gridNewEmptyRows != null && gridNewEmptyRows.containsKey(metaGrid.getKey()) && gridNewEmptyRows.get(metaGrid.getKey());
			Boolean result = TypeConvertor.toBoolean(context.getMidParser().eval(ScriptType.Formula, metaGrid.getNewEmptyRow()));
            boolean isNewEmptyRow = newEmptyRow || result;
            if (!isNewEmptyRow) {
                continue;
            }
            // 子表格的默认值，当选中子表格行，点击编辑，如字典工艺路线的工序列表表格
            if (!StringUtil.isBlankOrNull(parentGridKey) && !metaGrid.getKey().equalsIgnoreCase(focusGridKey)) {
                continue;
            }
            this.gridEmptyRow(metaGrid, metaGrid.getTableKey());
        }
        if (StringUtil.isBlankOrNull(focusGridKey)) {
            return;
        }
        MetaGrid metaGrid = idLookup.getMetaGridByGridKey(focusGridKey);

        // 算空白行默认值跟能不能插行没关系,无插行操作设置enable=true也需要算
        if (metaGrid.hasDetailRow()) {
            CalcEmptyRowChildGridsCmd.calcEmptyRowChildGrid(context, this, focusGridKey, idLookup, gridNewEmptyRows);
            // 焦点在子表格的情况，父表格需要计算空白行，如字典工艺路线，焦点在表格【标准值】，点击修改的时候，父表格【工序列表】需要计算空白行默认值
            String parentGridKey = idLookup.getMetaGridByGridKey(focusGridKey).getParentGridKey();
            if (!StringUtil.isBlankOrNull(parentGridKey)) {
                this.gridEmptyRow(parentGridKey);
            }
        }
    }

    /** 每个RichDocument绑定一个context */
    private RichDocumentContext context;

    public RichDocumentContext getContext() {
        return context;
    }

    public void setContext(RichDocumentContext context) {
        if (this.context == context) {
            return;
        }
        // 下面的判断做不到，原因是一些新建事务后，需要将原事务中的Document对象传递给新事务中的Context对象
        //if (this.context != null && this.context.getParentContext() != null && this.context.getParentContext() != context) {
        //	throw new RuntimeException("Document只能属于一个Context对象，一个Context最多拥有一个Document，不支持跨上下文传递Document对象，唯一的例外是Document可以赋值给其父亲。请联系开发人员，谢谢。");
        //}
        this.context = context;
        RichDocument oldDocument = context.getRichDocument();
        if (oldDocument == null) {
            if (context.getRichDocument() != this) {
                context.setDocument(this);
            }
        } else if (oldDocument != this) {
            throw new RuntimeException("RichDocument没有一个独享的RichDocumentContext，请联系开发人员，谢谢。");
        }
        this.expandManager.initContext(context);
    }

    /**
	 * 拷贝数据，共享DataTable对象
	 * 这个方法只应用于RichDocument com.bokesoft.yes.erp.dev.MidContextTool.loadObject(RichDocumentContext context, RichDocument document, MetaForm metaForm, FilterMap filterMap, boolean updateCurDoc, boolean runDefaultValue) throws Throwable
	 * 开发者请勿调用，谢谢
     * @param doc
     */
    @Deprecated
    public void copyDataFromOtherDoc(RichDocument doc) {
        this.clear();
        for (MetaTable metaTable : doc.getMetaDataObject().getTableCollection()) {
            String tableKey = metaTable.getKey();
            DataTable dataTable = doc.get(tableKey);
			if (!(tableKey.endsWith(MetaFormNODBProcess.STR_NODBTable_Profix) && get(tableKey) != null  && dataTable.size() == 0) && dataTable != null) {
                dataTable.setKey(tableKey);
                put(tableKey, dataTable);
            }
        }
        setFilterMap(doc.getFilterMap());
        uiCommands = doc.uiCommands;
        setTableFilterMap(doc.getTableFilterMap());
        setTableFilterParasMap(doc.getTableFilterParasMap());
        headValues = doc.headValues;
        otherFieldValues = doc.otherFieldValues;
        for (Entry<String, Object> entry : doc.getAllExpandData().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            this.putExpandData(key, value);
        }

        if (!doc.getAllExpandData().containsKey(BPMKeys.STATE_MACHINE)) {
            this.getAllExpandData().remove(BPMKeys.STATE_MACHINE);
        }
        //		this.effectScopeMap = doc.effectScopeMap;
        //		this.effectScopeMap.setDocument(this);
        //		this.formulaItemSequence = doc.formulaItemSequence;
        //		this.calcingFormulaItems = doc.calcingFormulaItems;

        this._needRebuildComp = doc._needRebuildComp;
        this.emptyGridRowValues = doc.emptyGridRowValues;
        this.putAttr("state", String.valueOf(doc.getState()));
        this.isFullData = doc.isFullData;
        this.setDocumentType(doc.getDocumentType());
        this.setOID(doc.getOID());
        this.setVERID(doc.getVERID());
        this.setDVERID(doc.getDVERID());
        this.setMetaDataObject(doc.getMetaDataObject());
    }

    /**
	 * 根据原对象复制一个全新的对象，注意，没有修改OID等系统字段
	 * 这个方法纯粹为了兼容才加的，新写的代码不要使用这个方法
     * @return
     * @throws Throwable
     */
    @Deprecated
    public DocumentRecordDirty copyNewDocument() throws Throwable {
        DocumentRecordDirty result = new DocumentRecordDirty(this.metaForm);
        result.setNew();
        result.setFullData();

        for (MetaTable metaTable : this.getMetaDataObject().getTableCollection()) {
            String tableKey = metaTable.getKey();
            DataTable dataTable = this.get(tableKey);
			if (!(tableKey.endsWith(MetaFormNODBProcess.STR_NODBTable_Profix) && get(tableKey) != null && dataTable.size() == 0)) {
                dataTable = dataTable.deepClone();
                dataTable.setKey(tableKey);
                result.put(tableKey, dataTable);
            }
        }
        result.setFilterMap(this.getFilterMap());
        result.uiCommands = this.uiCommands;
        result.setTableFilterMap(this.getTableFilterMap());
        result.setTableFilterParasMap(this.getTableFilterParasMap());
        result.headValues = this.headValues;
        result.otherFieldValues = this.otherFieldValues;
        for (Entry<String, Object> entry : this.getAllExpandData().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.putExpandData(key, value);
        }
        result._needRebuildComp = this._needRebuildComp;
        result.emptyGridRowValues = this.emptyGridRowValues;
        result.setDocumentType(this.getDocumentType());

        result.setContext(new RichDocumentContext(this.getContext()));
        return result;
    }

    /**** 从以前的BillDocument中拷贝过来 ****/

    public Object getHeadFieldValue(String fieldKey) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String tabKey = idLookup.getTableKeyByFieldKey(fieldKey);
        int bookMark = getCurrentBookMark(tabKey);
        return getValue(context, fieldKey, bookMark);
    }

    public void setValue(String fieldKey, int bookMark, Object value) throws Throwable {
        setValue(context, fieldKey, bookMark, value);
    }

    public void setHeadFieldValue(String fieldKey, Object value) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String tabKey = idLookup.getTableKeyByFieldKey(fieldKey);
        int bookMark = getCurrentBookMark(tabKey);
        setValue(context, fieldKey, bookMark, value);
    }

    public void setLockHeadFieldValue(String fieldKey, Object value) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String tabKey = idLookup.getTableKeyByFieldKey(fieldKey);
        int bookMark = getCurrentBookMark(tabKey);
        setLockValue(context, fieldKey, bookMark, value);
    }
    
    public int appendDetail(String tableKey, boolean runValueChanged) throws Throwable {
        return appendDetail(context, tableKey, runValueChanged);
    }

    public int appendDetail(String tableKey) throws Throwable {
        return appendDetail(context, tableKey, true);
    }

    public int appendDetailByRowIndex(String tableKey, int rowIndex, boolean runValueChanged) throws Throwable {
        return appendDetailByRowIndex(context, tableKey, rowIndex, runValueChanged);
    }

    public int appendDetailByRowIndex(String tableKey, int rowIndex) throws Throwable {
        return appendDetailByRowIndex(context, tableKey, rowIndex, true);
    }

    public void setValueNoChanged(String fieldKey, int bookMark, Object value) throws Throwable {
        setValueNoChanged(context, fieldKey, bookMark, value, false);
    }

    public int appendChildDetail(String childTableKey, String parentTableKey, Long parentOid) throws Throwable {
        return appendChildDetail(context, childTableKey, parentTableKey, parentOid);
    }

    public void setCloseFlag(boolean flag) throws Throwable {
        setCloseFlag();
    }

    public Object evaluate(String formula) throws Throwable {
        return context.getMidParser().eval(ScriptType.Formula, formula);
    }

    public void fireValueChanged(String valueChangeKey, int bookMark) throws Throwable {
        fireValueChanged(context, valueChangeKey, bookMark);
    }

    public void setValue(String fieldKey, Long oid, Object value) throws Throwable {
        int bookMark = getBookMarkByOID(fieldKey, oid);
        setValue(context, fieldKey, bookMark, value);
    }

    public void setLockValue(String fieldKey, Long oid, Object value) throws Throwable {
        int bookMark = getBookMarkByOID(fieldKey, oid);
        setLockValue(context, fieldKey, bookMark, value);
    }
    
    public void setValueNoChanged(String fieldKey, Long oid, Object value) throws Throwable {
        int bookMark = getBookMarkByOID(fieldKey, oid);
        setValueNoChanged(context, fieldKey, bookMark, value, false);
    }

    public Object getValue(String fieldKey, Long oid) throws Throwable {
        int bookMark = getBookMarkByOID(fieldKey, oid);
        return getValue(context, fieldKey, bookMark);
    }

    public RichDocument deepCloneRichDocument() throws Throwable {
        return DocumentRecordDirty.getDocumentFromDoc(super.deepClone(), getMetaForm());
    }

    public void addNeedRebuildComp(String needRebuildComp) throws Throwable {
        String temp = getNeedRebuildComp();
        if (StringUtil.isBlankOrNull(temp)) {
            setNeedRebuildComp("," + needRebuildComp + ",");
        } else {
            setNeedRebuildComp(temp + needRebuildComp + ",");
        }
        setNeedRebuildComp(needRebuildComp);
    }

    /**
     * 检查所有字段数据有效性
     *
     * @return
     * @throws Throwable
     */
    public List<String> checkValid() throws Throwable {
        return CheckValidExtensionPointManager.wrapCheckValid(context, this, () -> {
            return checkValid(context, false);
        });
    }
    /**
     * 检查数据有效性
     *
     * @return
     * @throws Throwable
     */
    public List<String> checkValid(boolean onlyPersist) throws Throwable {
        return CheckValidExtensionPointManager.wrapCheckValid(context, this, () -> {
            return checkValid(context, onlyPersist);
        });
    }

    /**
     * 带有明细行号、表格父子结构的检查有效性结果
     *
     * @param onlyPersist
     * @return
     * @throws Throwable
     */
    public List<HashMap<String,String>> checkValid2MessageList(boolean onlyPersist) throws Throwable {
        return CheckValidExtensionPointManager.wrapCheckValid(context, this, () -> {
            return checkValid2MessageList(context, onlyPersist);
        });
    }

    /**** 以下两个属性和方法从DocumentRecordDirty中拷贝过来 ****/

    /** 所有需要刷新的表格数据 */
    protected Set<String> dirtyTables = new HashSet<String>();

    public void addDirtyTableFlag(String tableKey) {
        dirtyTables.add(tableKey);
    }

    protected boolean _closeFlag = false;

    public void setCloseFlag() {
        _closeFlag = true;
    }

    /** 所有的客户端指令 */
    protected List<UICommand> uiCommands = new ArrayList<UICommand>();

    /**
     * 增加界面命令
     * @param uiCommand
     */
    public void appendUICommand(UICommand uiCommand) {
        this.uiCommands.add(uiCommand);
    }

    // 返回的提示信息
    protected String _message = "";

    public void setMessage(String message) {
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    // 搜集的需要到客户端重新计算ItemFilter的字典Key,或者需要重新计算Items的下拉框Key
    protected String _needRebuildComp = "";

    public void setNeedRebuildComp(String needRebuildComp) {
        _needRebuildComp = needRebuildComp;
    }

    public String getNeedRebuildComp() {
        return _needRebuildComp;
    }

    /**
     * 获取列扩展后的数据模型
     *
     * @param tableKey
     * @return
     * @throws Throwable
     */
    public ExpandDataModel getExpandModel(String tableKey) throws Throwable {
        return expandManager.getExpandModel(tableKey);
    }

    /**
     * 判定表单是否属于报表类型，目前仅通过表单类型无法判定表单是否属于报表类型， 此判定应放在MetaForm对象中做
     *
     * @return
     */
    private boolean isReportType() {
		if (this.metaForm.getFormType() == FormType.Report ||
				this.metaForm.getFormType() == FormType.View) {
            return true;
        }

		if (this.metaForm.getOperationCollection() != null) {
			if (this.metaForm.getOperationCollection().contains("BillSave") ||
					this.metaForm.getOperationCollection().contains("BillEdit")) {
                return false;
            }
        }

        MetaDataSource metaDataSource = metaForm.getDataSource();
        MetaDataObject metaDataObject = metaDataSource == null ? null : metaDataSource.getDataObject();
        if (metaDataObject == null) {
            return false;
        }

        MetaTableCollection metaTableCollection = metaDataObject.getTableCollection();
        if (metaTableCollection == null) {
            return false;
        }

        for (MetaTable metaTable : metaTableCollection) {
			if (metaTable.getSourceType() != TableSourceType.QUERY
					&& metaTable.getSourceType() != TableSourceType.UNKNOWN) {
                return false;
            }
        }
        return this.getOID() <= 0L;
    }

    /**
     * 对报表类型的数据表生成扩展表格
     *
     * @param docJson
     * @throws Throwable
     */
    private void appendExpandVirtualGridJSON(JSONObject docJson) throws Throwable {
        if (isReportType() && docJson.has(DocumentJSONConstants.DOCUMENT_TABLELIST)) {
            JSONArray tableArray = (JSONArray)docJson.get(DocumentJSONConstants.DOCUMENT_TABLELIST);
            for (int index = 0; index < tableArray.length(); index++) {
                JSONObject tableJSON = tableArray.getJSONObject(index);
                if (tableJSON.has(DocumentJSONConstants.DATATABLE_TABLEKEY)) {
                    String tableKey = tableJSON.getString(DocumentJSONConstants.DATATABLE_TABLEKEY);
                    appendSingleVirtualGridJSON(tableKey, tableJSON);
                }
            }
        }
    }

    /**
     * 添加单个扩展表格
     * @param tableKey
     * @param tableJSON
     * @throws Throwable
     */
    protected void appendSingleVirtualGridJSON(String tableKey, JSONObject tableJSON) throws Throwable {
        DataTable dataTable = this.get(tableKey);
        // 如果表中扩展表中没有数据，不在服务端做扩展处理，报表扩展源可能来源于查询条件 ，如 分析点多栏明细账
        if (dataTable == null || dataTable.size() == 0) {
            return;
        }

        if (isReportType() && this.isExpandTable(tableKey)) {
            ERPVirtualGrid expandVirtualGrid = expandManager.getExpandVirtualGrid(tableKey);
            if (expandVirtualGrid != null) {
                JSONObject expandJSON = expandVirtualGrid.toJSON(this.context.getVE());
                tableJSON.put("expandvirtualgrid", expandJSON);
            }
        }
    }

    /**
     * 判断表是否为列扩展表
     * @param tableKey
     * @return
     */
    public boolean isExpandTable(String tableKey) {
        return expandManager.isExpandTable(tableKey);
    }

    /**
     * 处理表头字段的默认值表达式和默认值
     * @param fieldKey
     * @param debugInfos 调试信息
     * @throws Throwable
     */
    public void processHeadDefaultFormulaValueItem(String fieldKey, String... debugInfos) throws Throwable {
        FormulaItem parentFormulaItem = calcingFormulaItems.isEmpty() ? null : calcingFormulaItems.peek();
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        MetaTable table = idLookup.getTableByFieldKey(fieldKey);
        String tableKey = idLookup.getTableKeyByFieldKey(fieldKey);
        String columnKey = tableKey == null ? fieldKey : idLookup.getColumnKeyByFieldKey(fieldKey);
        DataTable dt = tableKey == null ? null : get_impl(tableKey);
        int bookmark = (dt == null || dt.size() == 0) ? FormulaItem.INT_NotExistSingleTableBookmark : dt.getBookmark(0);
        // FIXME by zhufw
        if (checkDefaultFormulaValueBySameValueChanged(tableKey, columnKey, bookmark)) {
            return;
        }
        String defaultFormulaValue = idLookup.getDefaultFormulaValueByFieldKey(fieldKey);
        boolean hasDefaultValue = false;
        boolean isDefaultValue = false;
        hasDefaultValue = !isBlankOrNull(defaultFormulaValue);
        if (!hasDefaultValue) {
            defaultFormulaValue = idLookup.getDefaultValueByFieldKey(fieldKey);
            hasDefaultValue = !isBlankOrNull(defaultFormulaValue);
            isDefaultValue = hasDefaultValue;
        }
        if (isDefaultValue) {//默认值（非公式） 不加入延迟计算，直接赋值，减少延迟计算里面list的size，加快循环速度
            // 设置DefaultValue时 理论上不应该执行默认值公式的执行。特别是全局表达式
            // 例如PS_WBSElement 表单  NewDocument 时 字段SettlementRuleVestKey 的 DefaultValue赋值 不会引起其他字段的影响。就算有影响 我们认为也应该是延后的。所以不能让SetValue收集到全局公式
            FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookmark);
            this.setValueNoChanged(fieldLocation, defaultFormulaValue);
        }
		else
		
		if (hasDefaultValue) {
			FormulaItem result = isDefaultValue
					? FormulaItem.DefaultValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos)
                    : FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
            result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
            if (table != null) {
                result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
            }
            result.setTableBookmark(new TableKeyAndBookmark(tableKey, bookmark));
            result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                : parentFormulaItem.getSubSequence());
            this.effectScopeMap.addFormulaItem(result);
        }
    }

    /**
     * 处理单一明细字段的默认值表达式和默认值
     * @param fieldKey
     * @param bookmark
     * @param debugInfos 调试信息
     * @throws Throwable
     */
	public void processDtlDefaultFormulaValueItem(String fieldKey, int bookmark, String... debugInfos) throws Throwable {
        FormulaItem parentFormulaItem = calcingFormulaItems.isEmpty() ? null : calcingFormulaItems.peek();
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        MetaTable table = idLookup.getTableByFieldKey(fieldKey);
        String tableKey = idLookup.getTableKeyByFieldKey(fieldKey);
        String columnKey = idLookup.getColumnKeyByFieldKey(fieldKey);
        if (checkDefaultFormulaValueBySameValueChanged(tableKey, columnKey, bookmark)) {
            return;
        }

        boolean isDefaultValue = false;
        String defaultFormulaValue = idLookup.getDefaultFormulaValueByFieldKey(fieldKey);
        boolean hasDefaultValue = false;
        hasDefaultValue = !isBlankOrNull(defaultFormulaValue);
        if (!hasDefaultValue) {
            defaultFormulaValue = idLookup.getDefaultValueByFieldKey(fieldKey);
            hasDefaultValue = !isBlankOrNull(defaultFormulaValue);
            isDefaultValue = hasDefaultValue;
        }
        if (isDefaultValue) {
            //默认值（非公式） 不加入延迟计算，直接赋值，减少延迟计算里面list的size，加快循环速度
//            this.setValueNoChanged(fieldKey, bookmark, defaultFormulaValue);
            FieldLocation<?> fieldLocation = FieldLocationUtil.getFieldLocation(this, fieldKey, bookmark);
            this.setValueNoChanged(fieldLocation, defaultFormulaValue);
		}
		else 
		
		if (hasDefaultValue) {
			FormulaItem result = isDefaultValue
					? FormulaItem.DefaultValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos)
                    : FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
            result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
            if (table != null) {
                result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
            }
            result.setTableBookmark(new TableKeyAndBookmark(tableKey, bookmark));
            result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                : parentFormulaItem.getSubSequence());
            this.effectScopeMap.addFormulaItem(result);
        }
    }

    /**
     * 根据触发位置处理明细字段的默认值表达式和默认值
     * @param fieldKey
     * @param location   触发位置
     * @param debugInfos 调试信息
     * @throws Throwable
     */
	public void processDtlDefaultFormulaValueItem(String fieldKey, TableKeyAndBookmark location, String... debugInfos) throws Throwable {
        FormulaItem parentFormulaItem = calcingFormulaItems.isEmpty() ? null : calcingFormulaItems.peek();
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        MetaTable table = idLookup.getTableByFieldKey(fieldKey);
        String tableKey = idLookup.getTableKeyByFieldKey(fieldKey);
        String columnKey = idLookup.getColumnKeyByFieldKey(fieldKey);
        String defaultFormulaValue = idLookup.getDefaultFormulaValueByFieldKey(fieldKey);
        if (defaultFormulaValue == null || defaultFormulaValue.length() == 0) {
            throw new AssertionError();
        }

        if (tableKey.equals(location.getTableKey())) {
            if (!checkDefaultFormulaValueBySameValueChanged(tableKey, columnKey, location.getBookMark())) {
				FormulaItem result = FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
                result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
                if (table != null) {
                    result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
                }
                result.setTableBookmark(location);
                result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                    : parentFormulaItem.getSubSequence());
                this.effectScopeMap.addFormulaItem(result);
            }
        } else {
            FormulaItem result;
            TableRelation tableRelation = TableRelation.getTableRelation(getMetaDataObject());
            // 影响项是赋值项的xx
            switch (tableRelation.relation(tableKey, location.getTableKey())) {
                case parent:
                    TableKeyAndBookmark parentTableBookmark = getParentBookmark(location, tableKey, tableRelation);
                    if (!checkDefaultFormulaValueBySameValueChanged(tableKey, columnKey,
                        parentTableBookmark.getBookMark())) {
						result = FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
                        result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
                        if (table != null) {
                            result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
                        }
                        result.setTableBookmark(parentTableBookmark);
                        result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                            : parentFormulaItem.getSubSequence());
                        this.effectScopeMap.addFormulaItem(result);
                    }
                    break;
                case son:
                    // 影响项是赋值项的儿子/孙子
                    // 比如采购订单明细与采购订单头表    要获取采购订单明细中的全部bookmarks
                    TableKeyAndBookmark[] sonTableBookmarks = getSonBookmarks(location, tableKey, tableRelation);
                    for (TableKeyAndBookmark sonTableBookmark : sonTableBookmarks) {
                        if (!checkDefaultFormulaValueBySameValueChanged(tableKey, columnKey,
                            sonTableBookmark.getBookMark())) {
							result = FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
                            result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
                            if (table != null) {
                                result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
                            }
                            result.setTableBookmark(sonTableBookmark);
                            result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                                : parentFormulaItem.getSubSequence());
                            this.effectScopeMap.addFormulaItem(result);
                        }
                    }
					result = FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
                    result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
                    result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
                    result.setTableBookmark(new TableKeyAndBookmark(tableKey, FormulaItem.INT_EmptyRowBookmark));
                    result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                        : parentFormulaItem.getSubSequence());
                    this.effectScopeMap.addFormulaItem(result);
                    break;
                case noRelation:
                    TableKeyAndBookmark[] tableBookmarks = getBookmarks(tableKey);
                    for (TableKeyAndBookmark tableBookmark : tableBookmarks) {
                        if (!checkDefaultFormulaValueBySameValueChanged(tableKey, columnKey,
                            tableBookmark.getBookMark())) {
							result = FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
                            result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
                            if (table != null) {
                                result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
                            }
                            result.setTableBookmark(tableBookmark);
                            result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                                : parentFormulaItem.getSubSequence());
                            this.effectScopeMap.addFormulaItem(result);
                        }
                    }
                    if (!getMetaDataObject().getMetaTable(tableKey).isHead()) {
						result = FormulaItem.DefaultFormulaValue(metaForm, defaultFormulaValue, parentFormulaItem, debugInfos);
                        result.setDefaultFormlaValueTarget(fieldKey, tableKey, columnKey);
                        if (table != null) {
                            result.setTargetColumnKeyPersist(table.get(columnKey).isPersist());
                        }
                        result.setTableBookmark(new TableKeyAndBookmark(tableKey, FormulaItem.INT_EmptyRowBookmark));
                        result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                            : parentFormulaItem.getSubSequence());
                        this.effectScopeMap.addFormulaItem(result);
                    }
                    break;
            }
        }
    }

    private boolean checkDefaultFormulaValueBySameValueChanged(String tableKey, String columnKey, int bookmark) {
        for (FormulaItem formulaItem : calcingFormulaItems) {
            if (formulaItem.isValueChangedSource(tableKey, columnKey, bookmark)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 处理值变化表达式
     * @param fieldKey
     * @param bookmark
     * @param debugInfos
     * @throws Throwable
     */
    private void processValueChangedItem(String fieldKey, int bookmark, String... debugInfos) throws Throwable {
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        String[] valueChangeds = idLookup.getValueChangedsByFieldKey(fieldKey);
        if (valueChangeds == null || valueChangeds.length == 0) {
            return;
        }
        FormulaItem parentFormulaItem = calcingFormulaItems.isEmpty() ? null : calcingFormulaItems.peek();
        boolean isNODB4Other = IDLookup.isOtherField(fieldKey);
        String tableKey = isNODB4Other ? null : idLookup.getTableKeyByFieldKey(fieldKey);
        String columnKey = isNODB4Other ? fieldKey : idLookup.getColumnKeyByFieldKey(fieldKey);
        TableKeyAndBookmark location = new TableKeyAndBookmark(tableKey, bookmark);
        for (String valueChanged : valueChangeds) {
            FormulaItem result = FormulaItem.ValueChanged(metaForm, valueChanged, parentFormulaItem, debugInfos);
            result.setValueChangedSource(tableKey, columnKey);
            result.setTableBookmark(location);
            result.setSequence(parentFormulaItem == null ? new int[] {this.formulaItemSequence++}
                : parentFormulaItem.getSubSequence());
            if (result.isImmediatelyCalc()) {
                this.calcFormulaItem(result);
            } else {
                this.effectScopeMap.addFormulaItem(result);
            }
        }
    }

	public TableKeyAndBookmark getParentBookmark(TableKeyAndBookmark tableBookmark, String parentTableKey, TableRelation tableRelation) {
        String tableKey = tableBookmark.getTableKey();
        String[] parentTableKeys = tableRelation.getParentTableKeys(tableKey);
        int parentIndex = ArrayUtils.indexOf(parentTableKeys, parentTableKey);
        int bookmark = tableBookmark.getBookMark();
        for (int i = parentTableKeys.length; i > parentIndex; i--) {
            DataTable dt = get_impl(tableKey);
            int rowIndex = dt.getRowIndexByBookmark(bookmark);
            assert(rowIndex >= 0);
            bookmark = dt.getParentBookmark(rowIndex);
            tableKey = parentTableKeys[i - 1];
        }
        return new TableKeyAndBookmark(tableKey, bookmark);
    }

    /**
     * 获取“子表”的bookmarks
     * @param tableBookmark 赋值项
     * @param sonTableKey   影响项的tablekey
     * @param tableRelation
     * @return
     */
	public TableKeyAndBookmark[] getSonBookmarks(TableKeyAndBookmark tableBookmark, String sonTableKey, TableRelation tableRelation) {
        String parentTableKey = tableBookmark.getTableKey();
        // 获取当前子表的所有父表
        String[] parentTableKeys = tableRelation.getParentTableKeys(sonTableKey);
        int parentIndex = ArrayUtils.indexOf(parentTableKeys, parentTableKey);
        boolean isDetailParent = false;
        if (this.metaForm != null) {
            MetaTable parentMetaTable = this.metaForm.getMetaTable(parentTableKey);
            if (parentMetaTable != null) {
                isDetailParent = parentMetaTable.getTableMode() == TableMode.DETAIL;
            }
        }
        List<Integer> sonBookmarks = new ArrayList<>();
        // "孙表"的情况
        if (parentIndex != parentTableKeys.length - 1 && parentTableKeys.length >= 3) {
            String directParentTableKey = parentTableKeys[parentTableKeys.length - 1];
			List<Integer> directParentBookmarks = getBookmarkByParentBookmark(get_impl(directParentTableKey), tableBookmark.getBookMark(), parentIndex == 0 && !isDetailParent);
            for (int directParentBookmark : directParentBookmarks) {
                List<Integer> tmp = getBookmarkByParentBookmark(get_impl(sonTableKey), directParentBookmark, false);
                sonBookmarks.addAll(tmp);
            }
        } else {
			sonBookmarks = getBookmarkByParentBookmark(get_impl(sonTableKey), tableBookmark.getBookMark(), parentIndex == 0 && !isDetailParent);
        }

        //		for (int i = parentIndex + 1, size = parentTableKeys.length; i < size; i++) {
        //			parentTableKey = parentTableKeys[i];
        //			DataTable dt = get_impl(parentTableKey);
        //			sonBookmarks = getBookmarkByParentBookmark(dt, sonBookmarks);
        //		}

        int resultSize = sonBookmarks.size();
        TableKeyAndBookmark[] result = new TableKeyAndBookmark[resultSize];
        for (int i = 0; i < resultSize; i++) {
            result[i] = new TableKeyAndBookmark(sonTableKey, sonBookmarks.get(i));
        }
        return result;
    }

    /**
	 * 根据父表bookmark获取子表的bookmark
	 * 回头将这个方法移到DataTable中，这样可使用IntList这个类
     * @param dt                       子表的datatable
     * @param parentBookmark           父表的bookmark
     * @param isFirstLevelAndNotDetail 是否第一个父类，且不是明细表
     * @return
     */
	private static List<Integer> getBookmarkByParentBookmark(DataTable dt, int parentBookmark, boolean isFirstLevelAndNotDetail) {
        int size = (dt == null) ? 0 : dt.size();
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            if (isFirstLevelAndNotDetail || dt.getParentBookmark(i) == parentBookmark) {
                result.add(dt.getBookmark(i));
            }
        }
        return result;
    }

    @Deprecated
    private static List<Integer> getBookmarkByParentBookmark(DataTable dt, List<Integer> parentBookmarks) {
        int size = (dt == null) ? 0 : dt.size();
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            int currentBookmark = dt.getBookmark(i);
            if (parentBookmarks.contains(currentBookmark)) {
                result.add(currentBookmark);
            }
        }
        return result;
    }

    public TableKeyAndBookmark[] getBookmarks(String tableKey) {
        DataTable dt = get_impl(tableKey);
        int size = dt.size();
        TableKeyAndBookmark[] result = new TableKeyAndBookmark[size];
        for (int i = 0; i < size; i++) {
            result[i] = new TableKeyAndBookmark(tableKey, dt.getBookmark(i));
        }
        return result;
    }

    /**
     * 设置表格变式内容
     *
     * @param docJson
     * @throws Throwable
     */
    private void appendGridVariantJSON(JSONObject docJson) throws Throwable {
        JSONObject gridSettingVariants = new JSONObject();
        docJson.put("gridSettingVariants", gridSettingVariants);
        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
        List<MetaGrid> metaGrids = idLookup.getMetaGrids();
        if (metaGrids == null || metaGrids.size() == 0) {
            return;
        }
        for (MetaGrid metaGrid : metaGrids) {
            String gridKey = metaGrid.getKey();
            JSONObject gridSettingVariantItem = VariantUtil.getUserVariant(context, metaForm.getKey(), gridKey);
            gridSettingVariants.put(gridKey, gridSettingVariantItem);
        }
    }

    /**
     * 检查组件的属性
     * TextEditor/TextArea：MaxLength,InvalidChars;
     * NumberEditor:Precision,Scale,IntegerValue;
     * DatePicker/UTCDatePicker:能否转换为日期；
     * CheckBox:只能是0，1
     * ComoboBox/CheckListBox: Editable=false时，不允许输入不在范围内的值
     *
     * @param context
     * @param fieldKey
     * @param idLookup
     * @param errorInfoSet
     * @throws Throwable
     */
    private void pCheckComponentProperties(RichDocumentContext context, String fieldKey,
                                           IDLookup idLookup, Set<CheckErrorInfo> errorInfoSet, int curBookMark) throws Throwable {
        String columnKey = idLookup.getColumnKeyByFieldKey(fieldKey);
        String tableKey = idLookup.getTableKeyByFieldKey(fieldKey);
        if (ERPStringUtil.isBlankOrNull(tableKey) || tableKey.endsWith(MetaFormNODBProcess.STR_NODBTable_Profix)
                || ERPStringUtil.isBlankOrNull(columnKey) || columnKey.endsWith(MetaFormNODBProcess.STR_NODBTable_Profix)
                || columnKey.endsWith(MetaFormNODBProcess.STR_NODB4Other_Postfix)) {
            // 无数据源的字段不检查
            // #100246 后台代码中SQL查询得到的DataTable直接放到document中，后面如果再取无数据源的字段，就无法取到了
            return;
        }
        MetaComponent component = idLookup.getComponentByKey(fieldKey);
        String caption = idLookup.getFieldCaption(fieldKey);
        if (component != null) {
            switch (component.getControlType()) {
                case ControlType.TEXTEDITOR:
                case ControlType.TEXTAREA:
                    MetaTextEditorProperties properties_text = (MetaTextEditorProperties) component.getProperties();
                    pCheckTextEditorPro(fieldKey, errorInfoSet, curBookMark, caption, properties_text);
                    return;
                case ControlType.NUMBEREDITOR:
                    MetaNumberEditorProperties properties_num = (MetaNumberEditorProperties) component.getProperties();
                    pCheckNumberEditorPro(fieldKey, errorInfoSet, curBookMark, caption, properties_num);
                    return;
                case ControlType.DATEPICKER:
                case ControlType.UTCDATEPICKER:
                    pCheckDateValid(fieldKey, errorInfoSet, curBookMark, caption);
                    return;
                case ControlType.CHECKBOX:
                    pCheckCheckBoxValue(fieldKey, errorInfoSet, curBookMark, caption);
                    return;
                case ControlType.COMBOBOX:
                    if (comboChecker == null) {
                        this.comboChecker = new DefaultComboChecker(this, this.metaForm);
                    }
                    MetaComboBoxProperties properties_combobox = ((MetaComboBox) component).getProperties();
                    comboChecker.check(context, fieldKey, curBookMark, errorInfoSet, properties_combobox);
                    return;
                case ControlType.CHECKLISTBOX:
                    if (comboChecker == null) {
                        this.comboChecker = new DefaultComboChecker(this, this.metaForm);
                    }
                    MetaCheckListBoxProperties properties_checkList = ((MetaCheckListBox) component).getProperties();
                    comboChecker.check(context, fieldKey, curBookMark, errorInfoSet, properties_checkList);
                    return;
                case ControlType.DYNAMICDICT:
                	MetaDictProperties properties_dict = (MetaDictProperties) component.getProperties();
                	MetaDataBinding dataBinding = component.getDataBinding();
                	
                	pCheckDynamicDictProp(idLookup, fieldKey, errorInfoSet, curBookMark, caption, properties_dict);
                	return;
                default:
                    return;
            }
        } else {
            MetaGridCell gridCell = idLookup.getGridCellByKey(fieldKey);
            if (gridCell != null) {
                int cellType = gridCell.getCellType();
                switch (cellType) {
                    case ControlType.TEXTEDITOR:
                    case ControlType.TEXTAREA:
                        MetaTextEditorProperties properties = (MetaTextEditorProperties) gridCell.getProperties();
                        pCheckTextEditorPro(fieldKey, errorInfoSet, curBookMark, caption, properties);
                        return;
                    case ControlType.NUMBEREDITOR:
                        MetaNumberEditorProperties properties_num =
                                (MetaNumberEditorProperties) gridCell.getProperties();
                        pCheckNumberEditorPro(fieldKey, errorInfoSet, curBookMark, caption, properties_num);
                        return;
                    case ControlType.DATEPICKER:
                    case ControlType.UTCDATEPICKER:
                        pCheckDateValid(fieldKey, errorInfoSet, curBookMark, caption);
                        return;
                    case ControlType.CHECKBOX:
                        if (gridCell.isSelect()) {
                            return;
                        }
                        pCheckCheckBoxValue(fieldKey, errorInfoSet, curBookMark, caption);
                        return;
                    case ControlType.COMBOBOX:
                        if (comboChecker == null) {
                            this.comboChecker = new DefaultComboChecker(this, this.metaForm);
                        }
                        MetaComboBoxProperties properties_combobox = (MetaComboBoxProperties) gridCell.getProperties();
                        comboChecker.check(context, fieldKey, curBookMark, errorInfoSet, properties_combobox);
                        return;
                    case ControlType.CHECKLISTBOX:
                        if (comboChecker == null) {
                            this.comboChecker = new DefaultComboChecker(this, this.metaForm);
                        }
                        MetaCheckListBoxProperties properties_checkList = (MetaCheckListBoxProperties) gridCell.getProperties();
                        comboChecker.check(context, fieldKey, curBookMark, errorInfoSet, properties_checkList);
                        return;
                    case ControlType.DYNAMICDICT:
                    	MetaDictProperties properties_dict = (MetaDictProperties) gridCell.getProperties();
                    	pCheckDynamicDictProp(idLookup, fieldKey, errorInfoSet, curBookMark, caption, properties_dict);
                    default:
                        return;
                }
            }
        }
    }

    /**
     * CheckBox，只可输入0，1
     * @param fieldKey
     * @param errorInfoSet
     * @param curBookMark
     * @param caption
     * @throws Throwable
     */
	private void pCheckCheckBoxValue(String fieldKey, Set<CheckErrorInfo> errorInfoSet, int curBookMark, String caption) throws Throwable {
		int checkValue = TypeConvertor.toInteger(this.getValue(fieldKey, curBookMark));
		if (checkValue != 0 && checkValue != 1) {
            String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 不合法\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), checkValue);
            errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
        }
    }

    /**
	 * DatePicker
	 * 如果不能转换为日期，则报错
	 * @param fieldKey
	 * @param errorInfoSet
	 * @param curBookMark
	 * @param caption
	 * @throws Throwable
	 */
	private void pCheckDateValid(String fieldKey, Set<CheckErrorInfo> errorInfoSet, int curBookMark, String caption) throws Throwable {
		Object date = this.getValue(fieldKey, curBookMark);
		if (ERPStringUtil.isBlankOrNull(date) || TypeConvertor.toString(date).equalsIgnoreCase("0")) {
			return;
		}
		if (date instanceof Date) {
			return;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			dateFormat.parse(TypeConvertor.toString(date));
		} catch (ParseException e) {
            String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 不是日期型\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), date);
            errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
		}
	}

	/**
	 * NumberEditor 检查是否为整型，精度和小数位数
     * @param fieldKey
     * @param errorInfoSet
     * @param curBookMark
     * @param caption
     * @param properties
     * @throws Throwable
     */
    private void pCheckNumberEditorPro(String fieldKey, Set<CheckErrorInfo> errorInfoSet, int curBookMark, String caption,
        MetaNumberEditorProperties properties) throws Throwable {
        Object number = this.getValue(fieldKey, curBookMark);

        if (number instanceof Integer) {
            //int类型满足下面的判断
            return;
        }
        if (properties.integerValue()) {
            try {
                new Integer(TypeConvertor.toString(number));
            } catch (NumberFormatException e) {
                String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 不是整型\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), number);
                errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
            }
		} else {
			BigDecimal bigDecimal = TypeConvertor.toBigDecimal(number);
			if (bigDecimal.precision() > properties.getPrecision()) {
                String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 超过了精度{4}\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), number, properties.getPrecision());
				errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
			}
			if (bigDecimal.scale() > properties.getScale()) {
                String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 小数位超过了{4}\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), number, properties.getScale());
				errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
			}
		}
	}

	/**
	 * TextEditor 检查是否含有非法字符和最大长度
	 * @param fieldKey
	 * @param errorInfoSet
	 * @param curBookMark
	 * @param caption
	 * @param properties
	 * @throws Throwable
	 */
	private void pCheckTextEditorPro(String fieldKey, Set<CheckErrorInfo> errorInfoSet, int curBookMark, String caption,
									 MetaTextEditorProperties properties) throws Throwable {
		int maxLength = properties.getMaxLength();
		String value = TypeConvertor.toString(this.getValue(fieldKey, curBookMark));
		// maxLength = -1 表示无限制
		if (maxLength > 0 && value.length() > maxLength) {
            String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 超过了设置的最大长度{4}\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), value, maxLength);
			errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
		}
		String invalidChars = properties.getInvalidChars();
		if (ERPStringUtil.isBlankOrNull(invalidChars)) {
			return;
		}
		for (int i = 0; i < value.length(); i++) {
			String c = value.substring(i, i + 1);
			if (invalidChars.indexOf(c) >= 0) {
                String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} 值:{3} 含有非法字符{4}\n\n", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), value, c);
				errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
			}
		}
	}

	/**
	 * 动态字典检查itemKey是否在domain定义范围内
	 * @param idLookup
	 * @param fieldKey
	 * @param errorInfoSet
	 * @param curBookMark
	 * @param caption
	 * @param properties
	 * @throws Throwable
	 */
	private void pCheckDynamicDictProp(IDLookup idLookup, String fieldKey, Set<CheckErrorInfo> errorInfoSet, int curBookMark, String caption,
									MetaDictProperties properties) throws Throwable {
      	MetaColumn metaColumn = idLookup.getMetaColumnByFieldKey(fieldKey);
      	if(metaColumn == null) {
      		return;
      	}
      	
      	MetaDataElement metaDataElement = metaColumn.getDataElement();
      	if(metaDataElement == null) {
      		return;
      	}
      	
      	MetaDomain metaDomain = metaDataElement.getDomain();
      	
      	if(metaDomain == null){
      		return;
      	}
      	
      	MetaItemKeyCollection itemKeyCollection = metaDomain.getItemKeys();
      	if(itemKeyCollection == null || itemKeyCollection.isEmpty()) {
      		return;
      	}

        String refDataElementKey = properties.getRefDataElementKey();
        String itemKey = "";
        if (!StringUtil.isBlankOrNull(refDataElementKey)) {
            String dataElementKey = TypeConvertor.toString(this.getValue(refDataElementKey, curBookMark));
            itemKey = MetaUtil.getItemKeyByDataElementKey(context.getMetaFactory(), dataElementKey);
        } else {
            String refKey = properties.getRefKey();
            itemKey = TypeConvertor.toString(this.getValue(refKey, curBookMark));
        }
		if(StringUtil.isBlankOrNull(itemKey)) {
			return;
		}
		
		boolean bExist = false;
      	for(MetaDefaultItem item : itemKeyCollection) {
      		String s = item.getValue();
      		if(s.equals(itemKey)) {
      			bExist = true;
      			break;
      		}
      	}
      	if(!bExist) {
      		String errInfo = ERPStringUtil.formatMessage(context.getEnv(), "key:{1} caption:{2} ItemKey:{3} 超过Domain设置字典标识范围", fieldKey, ERPStringUtil.formatMessage(context.getEnv(), caption), itemKey);
			errorInfoSet.add(new CheckErrorInfo(this, fieldKey, errInfo, curBookMark));
      	}

	}
	
	public void setIgnoreArithmeticException() {
		this.isIgnoreArithmeticException = true;
	}

	private boolean isIgnoreArithmeticException() {
		return isIgnoreArithmeticException;
	}

	public boolean isCreateInMid() {
		return isCreateInMid;
	}

	public void setCreateInMid(boolean createInMid) {
		isCreateInMid = createInMid;
	}

	/**
	 * 是否为中间层中创建的
	 * 对于从客户端传过来的是false
	 */
	private boolean isCreateInMid = true;

	/**
	 * 缓存字段信息
	 * @param fieldKey 字段标识
	 * @param bookMark datatable行号
	 * @param fieldLocation 字段信息
	 */
	public void addFieldLocation(String fieldKey, int bookMark, FieldLocation<?> fieldLocation) {
		if( bookMark < 0) {
	        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
	        if(idLookup.isHeadField(fieldKey)) {
	        	// 头组件 key的表示 bookmark 取0;
	        	bookMark = 0;
	        }
		}
		String key = fieldKey + "_" + bookMark;
		fieldLocationCache.put(key, fieldLocation);
	}
	
	/**
	 * 获取字段信息
	 * @param fieldKey 字典标识
	 * @param bookMark datatable行号
	 * @return 字段信息
	 */
	public FieldLocation<?> getFieldLocation(String fieldKey, int bookMark){
		
		if( bookMark < 0) {
	        IDLookup idLookup = IDLookup.getIDLookup(metaForm);
	        if(idLookup.isHeadField(fieldKey)) {
	        	// 头组件 key的表示 bookmark 取0;
	        	bookMark = 0;
	        }
		}
		String key = fieldKey + "_" + bookMark;
		return fieldLocationCache.get(key);
	}
	
	/**
	 * 缓存行信息
	 * @param tableKey 字段标识
	 * @param bookMark datatable行号
	 * @param gridRow 行信息
	 */
	public void addGridRow(String tableKey, int bookMark, GridRow gridRow) {
		String key = tableKey + "_" + bookMark;
		gridRowCache.put(key, gridRow);
	}
	
	/**
	 * 获取行信息
	 * @param tableKey 字段标识
	 * @param bookMark datatable行号
	 * @return 行信息
	 */
	public GridRow getGridRow(String tableKey, int bookMark){
		String key = tableKey + "_" + bookMark;
		return gridRowCache.get(key);
	}

    /**
     * 注册表单数据，用于数据预加载
     */
    public void registerPreloadData() {
        MetaDataObject dataObject = getMetaDataObject();
        if (dataObject != null) {
            MetaTableCollection metaTableCollection = dataObject.getTableCollection();
            if (metaTableCollection != null) {
                for (MetaTable metaTable : dataObject.getTableCollection()) {
                    String tableKey = metaTable.getKey();
                    DataTable dt = get_impl(tableKey);
                    if (dt != null) {
                        PreLoadData.registerData(tableKey, dt);
                    }
                }
            }
        }
    }
}
